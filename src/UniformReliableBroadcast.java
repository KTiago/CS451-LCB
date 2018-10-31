import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UniformReliableBroadcast {

    private Set<Integer> peers;
    private int majority;
    private PerfectLink perfectLink;
    //Map the message to the number of ack received for that message
    private HashMap<Pair<Integer,Integer>,Integer> nbrAcks = new HashMap<>();
    private HashMap<Pair<Integer,Integer>,String> messages = new HashMap<>();
    private Set<Pair<Integer, Integer>> delivered;

    private BlockingQueue<String> receiveQueue = new LinkedBlockingQueue<>();

    //List to store the messages that have been sent
    private int selfId;
    private int sequenceNumber;
    private Thread t1;


    public UniformReliableBroadcast(HashMap<Integer, Pair<String,Integer>> peers, int selfId) throws Exception{
        this.peers = peers.keySet();
        this.perfectLink = new PerfectLink(peers.get(selfId).first, peers.get(selfId).second, peers);
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

    public void start(){
        t1.start();
        perfectLink.start();
    }
    public void stop(){
        t1.start();
        perfectLink.stop();
    }

    //Start Broadcasting a message
    public void broadcast(String message){
        broadcast(message, Pair.of(sequenceNumber++, selfId), selfId);
    }

    private void broadcast(String message, Pair<Integer,Integer> messageId, int source) {
        for (Integer id : peers){
            if (id != selfId && id != source){
                // FIXME encode message first !
                perfectLink.send(message, id);
            }
        }
    }

    private void handler() throws Exception{
        while(true){
            String payload = receiveQueue.take();
            Integer id = 0;
            Integer sequence = 0;
            String message = "";
            Pair<Integer, Integer> messageIdentifier = Pair.of(id, sequence);
            if(!delivered.contains(messageIdentifier)) {
                messages.putIfAbsent(messageIdentifier, message);
                Integer nbrAck = nbrAcks.getOrDefault(messageIdentifier, 0) + 1;
                nbrAcks.put(messageIdentifier, nbrAck);
                if (nbrAck == majority) {
                    delivered.add(messageIdentifier);
                    deliver(id, sequence, message);
                }
            }
        }
    }

    //Callback method for perfect link
    public void deliver(int id, int sequenceNumber, String message){
        System.out.println("Delivered "+id+" "+sequenceNumber+" "+message);
    }
}
