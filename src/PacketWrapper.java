import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.lang.ArrayUtils;

public class PacketWrapper {

    private Boolean isACK;
    private int sequenceNumber;
    private InetAddress IP;
    private int port;
    private DatagramPacket packet;

    public PacketWrapper(DatagramPacket packet) {
        this.packet = packet;
        this.IP = packet.getAddress();
        this.port = packet.getPort();
    }

    //Return true if the packet is an ACK
    public Boolean isACK() {
        return isACK;
    }

    //Return the sequence number
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    //Return the IP Address
    public InetAddress getIP() {
        return IP;
    }

    //Return the Port number
    public int getPort() {
        return port;
    }

    //Read packet content in order to know if it is an ACK and to get sequence number and content
    private void readPacketContent(){
        byte[] content = packet.getData();
        //The ACK byte if the first byte
        int ACKbyte = content[0];
        //If the ACkbyte is 6 then the packet is an ACK packet
        if(ACKbyte == 6){
            this.isACK = true;
        } else {
            this.isACK = false;
        }
        this.sequenceNumber = toInt(content,1);

    }

    //Return a
    private String toString(byte[] bytes,int length ,int offset){
        //ISO-8859-1 is the default charset encoding
        return new String(ArrayUtils.subarray(bytes, offset, offset+length), "ISO-8859-1");
    }

    //Return the integer corresponding to 4 bytes in an array at a given offset
    private int toInt(byte[] bytes, int offset) {
        int ret = 0;
        for (int i=0; i<4 && i+offset<bytes.length; i++) {
            ret <<= 8;
            ret |= (int)bytes[i+offset] & 0xFF;
        }
        return ret;
    }

    //Create a DatagramPacket corresponding to an ACK
    public static DatagramPacket createACK(int sequenceNumber, InetAddress destinationIP,int destinationPort){
        return null;
        //TODO
    }

    //Create a DatagramPacket corresponding to a Simple Message
    public static DatagramPacket createSimpleMessage(String message, int sequenceNumber, InetAddress destinationIP, int destinationPort){
        return null;
        //TODO
    }
}
