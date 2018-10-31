import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UniformReliableBroadcast {

    private HashMap<Integer, Pair<String,Integer>> peers;
    private int numberPeers;
    //Map the message to the number of ack received for that message
    private HashMap<Pair<Integer,Integer>,Integer> ack = new HashMap<>();
    private BlockingQueue<Pair<Pair<Integer,Integer>,String>> receiveQueue = new LinkedBlockingQueue<>();

    //List to store the messages that have been sent
    private int selfId;


    public UniformReliableBroadcast(HashMap<Integer, Pair<String,Integer>> peers, int selfId) {
        this.peers = peers;
        numberPeers = peers.size();
        this.selfId = selfId;
    }

    //Start Broadcasting a message
    public void broadcast(String message){
        for (Integer id : peers.keySet()){
            if (id != selfId){
                //TODO send through perfect link
            }
        }
    }

    private void broadcast(String message, Pair<Integer,Integer> messageId, int source) {

    }

    //Callback method for perfect link
    public void deliver(int pi, String message){

    }

    private Boolean canDeliver(m){
        //TODO message can be delivered when at least half of the ACKs are received
        return
    }

}
