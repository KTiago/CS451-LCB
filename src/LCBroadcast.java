import java.util.*;

public class LCBroadcast {

    private final UniformReliableBroadcast urb;
    private final Da_proc proc;
    private HashMap<Pair<Integer, Integer>, Pair<int[], String>> pending;
    private int[] vectorClock;
    private List<Integer> dependencies;
    private int lsn;
    private int selfId;
    private int nbrPeers;

    /*
        FIFO broadcast that uses a Uniform Reliable Broadcast and orders messages to ensure FIFO properties.
    */
    public LCBroadcast(HashMap<Integer, Pair<String, Integer>> peers, List<Integer> dependencies, int selfId, Da_proc proc) throws Exception {
        this.urb = new UniformReliableBroadcast(peers, selfId, this);
        this.dependencies = dependencies;
        this.selfId = selfId;
        this.lsn = 1;
        this.proc = proc;
        this.nbrPeers = peers.size();
        this.pending = new HashMap<>();
        this.vectorClock = new int[nbrPeers + 1];
        Arrays.fill(vectorClock, 1);
    }

    public void start() {
        urb.start();
    }

    public void stop() {
        urb.stop();
    }

    // broadcast method used by the upper layer to broadcast messages
    public void broadcast(String message) {
        synchronized (this) {
            int[] vectorClockCopy = vectorClock.clone();
            vectorClockCopy[selfId] = lsn;
            for (int i : dependencies) {
                vectorClockCopy[i] = 1;
            }
            lsn += 1;
            urb.broadcast(Utils.VCToString(vectorClockCopy) + message);
        }
    }

    private boolean isSmaller(int[] vc1, int[] vc2){
        for(int i = 0; i < vc1.length; i++){
            if(vc1[i] > vc2[i]){
                return false;
            }
        }
        return true;
    }

    // deliver method used by the lower layer (uniform reliable broadcast) to deliver messages
    // the algorithm store messages that are out of sequence to deliver them when possible.
    public void deliver(int id, int sequenceNumber, String payload) {
        synchronized (this) {
            int[] receivedVectorClock = Utils.stringToVC(payload, nbrPeers);
            String message = Utils.getMessageVC(payload, nbrPeers);
            pending.putIfAbsent(Pair.of(id, sequenceNumber), Pair.of(receivedVectorClock, message));
            Iterator<Pair<Integer, Integer>> iterator = pending.keySet().iterator();
            while (iterator.hasNext()) {
                Pair<Integer, Integer> pair = iterator.next();
                if (isSmaller(pending.get(pair).first, vectorClock)) {
                    vectorClock[pair.first] += 1;
                    proc.deliver(pair.first, pair.second, pending.get(pair).second);
                    iterator.remove();
                }
            }
        }
    }
}
