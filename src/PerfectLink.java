import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class PerfectLink {

    private InetAddress sourceIP;
    private int sourcePort;
    private InetAddress destinationIP;
    private int destinationPort;
    private DatagramSocket socket;

    public PerfectLink(String sourceIP, int sourcePort, String destinationIP, int destinationPort) throws Exception {
        this.sourceIP = InetAddress.getByName(sourceIP);
        this.sourcePort = sourcePort;
        this.destinationIP = InetAddress.getByName(destinationIP);
        this.destinationPort =  destinationPort;
        this.socket = new DatagramSocket(this.sourcePort, this.sourceIP);
    }

    public void send(String message) throws Exception{
        byte buffer[] = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer,message.length(),destinationIP,destinationPort);
        socket.send(packet);
    }

    public String receive(int length) throws Exception{
        DatagramPacket packet = new DatagramPacket(new byte[length],length);
        socket.receive(packet);
        return new String(packet.getData());
    }
}
