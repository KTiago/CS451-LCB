import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//FIXME SEQUENCE SHOULD START AT 1 NOT AT 0
public class UniformReliableBroadcast {

    private Set<Integer> peers;
    private int majority;
    private PerfectLink perfectLink;
    private Da_proc proc;
    //Map the message to the number of ack received for that message
    private HashMap<Pair<Integer, Integer>, Set<Integer>> nbrAcks = new HashMap<>();
    private HashMap<Pair<Integer, Integer>, String> messages = new HashMap<>();
    private Set<Pair<Integer, Integer>> delivered = new HashSet<>();

    private BlockingQueue<Pair<String, Integer>> receiveQueue = new LinkedBlockingQueue<>();

    //List to store the messages that have been sent
    private int selfId;
    private int sequenceNumber = 1;
    private Thread t1;

    private boolean debug = false;

    public UniformReliableBroadcast(HashMap<Integer, Pair<String, Integer>> peers, int selfId, Da_proc proc) throws Exception {
        this.peers = peers.keySet();
        this.perfectLink = new PerfectLink(this, peers.get(selfId).first, peers.get(selfId).second, peers);
        this.majority = peers.size() / 2 + 1;
        this.selfId = selfId;
        this.proc = proc;
        this.t1 = new Thread() {
            public void run() {
                    handler();
            }
        };
    }

    public void start() {
        t1.start();
        perfectLink.start();
    }

    public void stop() {
        perfectLink.stop();
        t1.interrupt();
    }

    public void plDeliver(String payload, Integer senderID) {
        this.receiveQueue.add(Pair.of(payload, senderID));
    }

    //Start Broadcasting a message
    public void broadcast(String message) {
        Pair<Integer, Integer> messageIdentifier = Pair.of(selfId, sequenceNumber++);
        Set<Integer> ackedSet = new HashSet<>();
        ackedSet.add(selfId);
        nbrAcks.put(messageIdentifier, ackedSet);
        messages.put(messageIdentifier, message);
        broadcast(message, messageIdentifier);
    }

    private void broadcast(String message, Pair<Integer, Integer> messageIdentifier) {
        for (Integer id : peers) {
            if (!nbrAcks.get(messageIdentifier).contains(id)) {
                String senderId = Utils.intToString(messageIdentifier.first);
                String sequence = Utils.intToString(messageIdentifier.second);
                perfectLink.send(senderId + sequence + message, id);
                if (debug)
                    System.out.println("Sending (" + messageIdentifier.first + "," + messageIdentifier.second + ") to " + id);
            }
        }
    }

    private void handler(){
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Pair<String, Integer> payloadAndId = receiveQueue.take();
                String payload = payloadAndId.first;
                Integer senderId = payloadAndId.second;

                // Unpacking
                byte[] bytes = payload.getBytes();
                Integer id = Utils.bytesArraytoInt(bytes, 0);
                Integer sequence = Utils.bytesArraytoInt(bytes, 4);
                String message = Utils.bytesArraytoString(bytes, 8, payload.length() - 8);
                if (debug) System.out.println("Received (" + id + "," + sequence + ") from " + senderId);
                Pair<Integer, Integer> messageIdentifier = Pair.of(id, sequence);

                Set<Integer> ackedSet;

                // When it's the first time we see a message
                if (!messages.containsKey(messageIdentifier)) {
                    // Add message to all messages seen so far
                    messages.put(messageIdentifier, message);

                    ackedSet = new HashSet<>();
                    ackedSet.add(selfId);
                    // Add sender of message to peers who acked it
                    ackedSet.add(senderId);
                    // Add origin of message to peers who acked it
                    ackedSet.add(id);
                    nbrAcks.put(messageIdentifier, ackedSet);
                    broadcast(message, messageIdentifier);

                } else { //When it's not the first time we see a message
                    ackedSet = nbrAcks.get(messageIdentifier);
                    ackedSet.add(senderId);
                }

                // If we have enough ACKS for the message, we can deliver it
                if (ackedSet.size() >= majority && !delivered.contains(messageIdentifier)) {
                    //System.out.println("Majority = "+majority);
                    delivered.add(messageIdentifier);
                    deliver(id, sequence, messages.get(messageIdentifier));
                }

                // We ack any message we have not yet acked
                if (message.length() > 0) {
                    perfectLink.send(payload.substring(0, 8), senderId);
                    if (debug) System.out.println("Sending (" + id + "," + sequence + ") to " + senderId);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    //Callback method for perfect link
    public void deliver(int id, int sequenceNumber, String message) {
        proc.deliver(id, sequenceNumber, message);
        if (debug) System.out.println("deliver " + id + " " + sequenceNumber);
    }
}
