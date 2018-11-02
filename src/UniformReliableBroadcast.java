import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UniformReliableBroadcast {

    private Set<Integer> peers;
    private int majority;
    private PerfectLink perfectLink;
    //Map the message to the number of ack received for that message
    private HashMap<Pair<Integer, Integer>, Set<Integer>> nbrAcks = new HashMap<>();
    private HashMap<Pair<Integer, Integer>, String> messages = new HashMap<>();
    private Set<Pair<Integer, Integer>> delivered = new HashSet<>();

    private BlockingQueue<Pair<String, Integer>> receiveQueue = new LinkedBlockingQueue<>();

    //List to store the messages that have been sent
    private int selfId;
    private int sequenceNumber;
    private Thread t1;

    private boolean debug = false;

    public UniformReliableBroadcast(HashMap<Integer, Pair<String, Integer>> peers, int selfId) throws Exception {
        this.peers = peers.keySet();
        this.perfectLink = new PerfectLink(this, peers.get(selfId).first, peers.get(selfId).second, peers);
        this.majority = peers.size() / 2 + 1;
        this.selfId = selfId;
        this.t1 = new Thread() {
            public void run() {
                try {
                    handler();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        };
    }

    public void start() {
        t1.start();
        perfectLink.start();
    }

    public void stop() {
        t1.stop();
        perfectLink.stop();
    }

    public void plDeliver(String payload, Integer senderID) {
        if (debug) System.out.println("Pf received "+payload.substring(8, payload.length())+" from "+senderID);
        this.receiveQueue.add(Pair.of(payload, senderID));
    }

    //Start Broadcasting a message
    public void broadcast(String message) {
        if (debug) System.out.println("Broadcasting :"+message);
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
                if (debug) System.out.println("Pf sent (bcast) "+message+" to "+id);
            }
        }
    }

    private void handler() throws Exception {
        while (true) {
            Pair<String, Integer> payloadAndId = receiveQueue.take();
            String payload = payloadAndId.first;
            Integer senderId = payloadAndId.second;

            // Unpacking
            byte[] bytes = payload.getBytes();
            Integer id = Utils.bytesArraytoInt(bytes, 0);
            Integer sequence = Utils.bytesArraytoInt(bytes, 4);
            String message = Utils.bytesArraytoString(bytes, 8, payload.length() - 8);

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

            // We ack any message that is not itself an ACK
            if(message.length() > 0){
                perfectLink.send(payload.substring(0, 8), senderId);
                if (debug) System.out.println("Pf sent "+message+" to "+senderId);
            }
            if (debug) System.out.println(messageIdentifier + " acked set = "+ackedSet);
        }
    }

    //Callback method for perfect link
    public void deliver(int id, int sequenceNumber, String message) {
        if (debug) System.out.println("Delivered " + id + " " + sequenceNumber + " " + message);
        System.out.println("Delivered : " + message);
    }
}
