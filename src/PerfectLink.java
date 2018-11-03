


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PerfectLink {
    UniformReliableBroadcast urb;

    private final int DATAGRAM_LENGTH = 1024;


    private HashMap<Pair<Integer,Integer>, Pair<DatagramPacket, Long>> timerPackets = new HashMap<>();
    private InetAddress sourceIP;
    private int sourcePort;
    private DatagramSocket socket;
    private Map<Integer, Pair<InetAddress, Integer>> peers;
    private Map<Pair<InetAddress, Integer>, Integer> peersInverse;
    private Thread t1;
    private Thread t2;
    private Thread t3;
    private Thread t4;
    private BlockingQueue<PacketWrapper> receiveQueue;
    private BlockingQueue<DatagramPacket> sendQueue;
    // the array of sequence numbers for messages to be sent per peer
    private int[] sequenceNumbers;
    // the array of ACKS the local machine wants to receive messages for per peer
    private int[] localAcks;
    // the array of the last (highest value) ACK received per peer
    private int[] remoteAcks;
    private List<List<String>> messagesToSend;
    private List<List<String>> messagesToDeliver;

    public PerfectLink(UniformReliableBroadcast urb, String sourceIP, int sourcePort, HashMap<Integer, Pair<String, Integer>> peers) throws Exception {
        this.urb = urb;
        this.sourceIP = InetAddress.getByName(sourceIP);
        this.sourcePort = sourcePort;
        this.socket = new DatagramSocket(this.sourcePort, this.sourceIP);

        this.peers = resolveAddresses(peers);
        Map<Pair<InetAddress, Integer>, Integer> peersInverse = new HashMap<>();
        for (Map.Entry<Integer, Pair<InetAddress, Integer>> entry : this.peers.entrySet()) {
            peersInverse.put(entry.getValue(), entry.getKey());
        }
        this.peersInverse = peersInverse;

        this.receiveQueue = new LinkedBlockingQueue<>();
        this.sendQueue = new LinkedBlockingQueue<>();

        this.localAcks = new int[peers.size() + 1];
        this.remoteAcks = new int[peers.size() + 1];
        this.sequenceNumbers = new int[peers.size() + 1];
        Arrays.fill(sequenceNumbers, -1);

        this.messagesToSend = new ArrayList<>();
        for (int i = 0; i < peers.size() + 1; i++) {
            this.messagesToSend.add(new ArrayList<>());
        }
        this.messagesToDeliver = new ArrayList<>();
        for (int i = 0; i < peers.size() + 1; i++) {
            this.messagesToDeliver.add(new ArrayList<>());
        }

        t1 = new Thread() {
            public void run() {
                try {
                    receiveLoop();
                } catch (Exception e) {
                    this.interrupt();
                }
            }
        };
        t2 = new Thread() {
            public void run() {
                try {
                    sendLoop();
                } catch (Exception e) {
                    this.interrupt();
                }
            }
        };
        t3 = new Thread() {
            public void run() {
                try {
                    handler();
                } catch (Exception e) {
                    this.interrupt();
                }
            }
        };
        t4 = new Thread() {
            public void run() {
                    handleTimer();
            }
        };

    }

    public void start() {
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    public void stop() {
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
        t4.interrupt();
        socket.close();
    }

    private void deliver(String message, Integer senderID) {
        urb.plDeliver(message, senderID);
    }

    public void send(String message, int destinationID) {
        //System.out.println("Sending to "+destinationID+" - "+message);
        synchronized (messagesToSend) {
            messagesToSend.get(destinationID).add(message);
        }
        InetAddress destinationIP = peers.get(destinationID).first;
        int destinationPort = peers.get(destinationID).second;
        int sequenceNumber = ++sequenceNumbers[destinationID];
        DatagramPacket packet = PacketWrapper.createSimpleMessage(message, sequenceNumber, destinationIP, destinationPort);
        sendQueue.add(packet);

        synchronized (timerPackets) {
            timerPackets.put(Pair.of(destinationID,sequenceNumber),Pair.of(packet,System.currentTimeMillis()));
        }
    }


    private void handleTimer() {
    try{
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (timerPackets) {
                long now = System.currentTimeMillis();
                List<Pair<Integer,Integer>> toBeRemoved = new ArrayList<>();
                List<Pair<Integer,Integer>> toBeRetransmitted= new ArrayList<>();
                for (Pair<Integer, Integer> id_m : timerPackets.keySet()) {
                    if (remoteAcks[id_m.first] > id_m.second) {
                        toBeRemoved.add(id_m);
                    } else {
                        if (now - timerPackets.get(id_m).second >= 100) {
                            toBeRetransmitted.add(id_m);
                        }
                    }
                }
                for (Pair<Integer, Integer> id_m:toBeRemoved){
                    timerPackets.remove(id_m);
                }
                for (Pair<Integer,Integer> id_m:toBeRetransmitted){
                    DatagramPacket packet = timerPackets.get(id_m).first;
                    timerPackets.put(id_m, Pair.of(packet, now));
                    sendQueue.add(packet);
                }


            }
            Thread.sleep(10);
        }
    } catch (Exception e){
        Thread.currentThread().interrupt();
    }

    }

    private void handler(){
        try {
            while (!Thread.currentThread().isInterrupted()) {
                PacketWrapper packet = receiveQueue.take();
                int sequenceNumber = packet.getSequenceNumber();
                int id = peersInverse.get(Pair.of(packet.getIP(), packet.getPort()));
                // ***** CASE 1 - THE PACKET IS A AN ACK *****
                if (packet.isACK()) {
                    int ack = packet.getSequenceNumber();

                    // This ack is not outdated
                    if (ack > remoteAcks[id]) {
                        remoteAcks[id] = ack;
                        // The ack corresponds to a message in memory
                        if (ack <= sequenceNumbers[id]) {
                            String message;
                            synchronized (messagesToSend) {
                                message = messagesToSend.get(id).get(ack);
                            }
                            DatagramPacket messagePacket = PacketWrapper.createSimpleMessage(message, ack, packet.getIP(), packet.getPort());
                            sendQueue.add(messagePacket);
                        }
                        // This ack is outdated, nothing needs to be done
                    } else {
                        // FIXME remove if indeed not needed
                    }
                    // ***** CASE 2 - THE PACKET IS A MESSAGE *****
                } else {
                    //System.out.println("Message received, seq = "+sequenceNumber);
                    // this synchronized blocks adds enough elements to the list to fit the received message
                    // at its position corresponding to the sequence numbers.
                    synchronized (messagesToDeliver) {
                        int size = messagesToDeliver.get(id).size();
                        int difference = sequenceNumber - size + 1;
                        if (difference > 0) {
                            messagesToDeliver.get(id).addAll(Collections.nCopies(difference, null));
                        }
                    }
                    // The message was the expected message in sequence.
                    if (localAcks[id] == sequenceNumber) {
                        synchronized (messagesToDeliver) {
                            messagesToDeliver.get(id).set(sequenceNumber, packet.getData());
                            for (int i = sequenceNumber; i < messagesToDeliver.get(id).size(); i++) {
                                String message = messagesToDeliver.get(id).get(i);
                                if (message == null) {
                                    break;
                                }
                                deliver(message, id);
                                localAcks[id]++;
                            }
                        }
                    }
                    // The message was not expected, either with higher or lower sequence number.
                    else {
                        //FIXME remove if indeed not needed
                        //nothing special is to be done
                    }
                    // For any received message (in sequence or not) an ACK is sent with the next expected message sequence
                    DatagramPacket ackPacket = PacketWrapper.createACK(localAcks[id], packet.getIP(), packet.getPort());
                    sendQueue.add(ackPacket);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void receiveLoop() throws Exception {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(new byte[DATAGRAM_LENGTH], DATAGRAM_LENGTH);
                socket.receive(packet);
                receiveQueue.add(new PacketWrapper(packet));
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendLoop() throws Exception {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket packet = sendQueue.take();
                socket.send(packet);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private HashMap<Integer, Pair<InetAddress, Integer>> resolveAddresses(HashMap<Integer, Pair<String, Integer>> map) throws Exception {
        HashMap<Integer, Pair<InetAddress, Integer>> peers = new HashMap<>();
        for (Map.Entry<Integer, Pair<String, Integer>> entry : map.entrySet()) {
            Integer id = entry.getKey();
            Pair<String, Integer> pair = entry.getValue();
            peers.put(id, Pair.of(InetAddress.getByName(pair.first), pair.second));
        }
        return peers;
    }
}
