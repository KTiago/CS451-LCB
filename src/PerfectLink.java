import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class PerfectLink {

    private InetAddress sourceIP;
    private int sourcePort;
    private DatagramSocket socket;
    private HashMap<Integer, Link> links;

    public PerfectLink(String sourceIP, int sourcePort) throws Exception {
        this.sourceIP = InetAddress.getByName(sourceIP);
        this.sourcePort = sourcePort;
        this.socket = new DatagramSocket(this.sourcePort, this.sourceIP);
        this.links = new HashMap<>();
    }

    public void addLink(int destinationID, String destinationIP, int destinationPort)throws Exception{
        links.put(destinationID, new Link(destinationIP, destinationPort));
    }


    /*
    public void send(String message) throws Exception{
        byte buffer[] = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer,message.length(),destinationIP,destinationPort);
        socket.send(packet);
    }
    */

    /*
    public String receive(int length) throws Exception{
        DatagramPacket packet = new DatagramPacket(new byte[length],length);
        socket.receive(packet);
        return new String(packet.getData());
    }
    */

    class Link {
        private InetAddress destinationIP;
        private int destinationPort;
        public Link(String destinationIP, int destinationPort) throws Exception{
            this.destinationIP = InetAddress.getByName(destinationIP);;
            this.destinationPort = destinationPort;
        }
    }
}
