
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Collections;
import java.util.List;

public class PerfectLink {

    private InetAddress sourceIP;
    private int sourcePort;
    private DatagramSocket socket;
    private HashMap<Integer, Pair<InetAddress, Integer>> peers;
    private Thread t1;
    private Thread t2;
    private Thread t3;
    private BlockingQueue<String> receiveQueue;
    private BlockingQueue<DatagramPacket> sendQueue;
    private int[] localAck;
    private int[] remoteAck;
    private List<List<String>> messagesToSend;
    private List<List<String>> messagesToDeliver;

    public PerfectLink(String sourceIP, int sourcePort, HashMap<Integer, Pair<String, Integer>> peers) throws Exception {
        this.sourceIP = InetAddress.getByName(sourceIP);
        this.sourcePort = sourcePort;
        this.socket = new DatagramSocket(this.sourcePort, this.sourceIP);

        this.peers = resolveAddresses(peers);

        this.receiveQueue = new LinkedBlockingQueue<>();
        this.sendQueue = new LinkedBlockingQueue<>();

        this.localAck = new int[peers.size()];
        this.remoteAck  = new int[peers.size()];

        this.messagesToSend = Collections.synchronizedList(new ArrayList<>());
        this.messagesToDeliver = Collections.synchronizedList(new ArrayList<>());

        t1 = new Thread() {
            public void run() {
                receiveLoop();
            }
        };
        t2 = new Thread() {
            public void run() {
                sendLoop();
            }
        };
        t3 = new Thread() {
            public void run(){
                try {
                    handler();
                }catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        };

    }

    private void start() {
        t1.start();
        t2.start();
    }

    private void stop() {
        t1.stop();
        t2.stop();
    }

    private void deliver(String message){

    }

    public void send(String message, int destinationID){
        synchronized (messagesToSend) {
            messagesToSend.get(destinationID).add(message);
            // add datagram packet to send queue
        }
    }

    private void handler() throws Exception{
        while(true){
            String packet = receiveQueue.take();
            if(packet == "ACK"){

            }else{
                /*
                    if(packet.seq )
                */
            }
        }
    }

    private void receiveLoop() {
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                receiveQueue.add(new String(packet.getData()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void sendLoop() {
        try {
            while (true) {
                DatagramPacket packet = sendQueue.take();
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private HashMap<Integer, Pair<InetAddress, Integer>> resolveAddresses (HashMap<Integer, Pair<String, Integer>> map) throws Exception {
        HashMap<Integer, Pair<InetAddress, Integer>> peers = new HashMap<>();
        for (Map.Entry<Integer, Pair<String, Integer>> entry : map.entrySet()) {
            Integer id = entry.getKey();
            Pair<String, Integer> pair = entry.getValue();
            peers.put(id, Pair.of(InetAddress.getByName(pair.first), pair.second));
        }
    }
}
