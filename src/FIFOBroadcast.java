import java.util.*;

public class FIFOBroadcast {

    private final UniformReliableBroadcast urb;
    private final Da_proc proc;
    private List<List<String>> messagesToDeliver;
    private int[] nextSequenceToDeliver;

    public FIFOBroadcast(HashMap<Integer, Pair<String, Integer>> peers, int selfId, Da_proc proc) throws Exception {
        this.urb = new UniformReliableBroadcast(peers, selfId, this);
        this.proc = proc;
        this.messagesToDeliver = new ArrayList<>();
        for (int i = 0; i < peers.size() + 1; i++) {
            this.messagesToDeliver.add(new ArrayList<>());
        }
        this.nextSequenceToDeliver = new int[peers.size() + 1];
        Arrays.fill(nextSequenceToDeliver, 1);
    }

    public void start(){
        urb.start();
    }

    public void stop(){
        urb.stop();
    }

    public void broadcast(String message) {
        urb.broadcast(message);
    }

    public void deliver(int id, int sequenceNumber, String message){
        synchronized (messagesToDeliver) {
            List<String> messages = messagesToDeliver.get(id);
            int size = messages.size();
            int difference = sequenceNumber - size + 1;
            if (difference > 0) {
                messages.addAll(Collections.nCopies(difference, null));
            }
            messages.add(sequenceNumber, message);
            for(int i = nextSequenceToDeliver[id]; i < size + difference; i ++){
                if(messages.get(i) == null){
                    break;
                }
                proc.deliver(id, i, messages.get(i));
                nextSequenceToDeliver[id] += 1;
            }
        }
    }
}
