import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class PacketWrapper {

    private final int SIZEHEADER = 5;
    private Boolean isACK;
    private int sequenceNumber;
    private InetAddress IP;
    private int port;
    private DatagramPacket packet;
    private String data;

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

    //Return the content of a packet
    //It is null if it is an ACK packet
    public String getData() {
        return data;
    }

    //Read packet content in order to know if it is an ACK and to get sequence number and content
    private void readPacketContent(){
        byte[] content = packet.getData();
        //The ACK byte if the first byte
        int ACKbyte = content[0];
        //If the ACkbyte is 6 then the packet is an ACK packet
        if(ACKbyte == 6){
            this.isACK = true;
            this.data = null;
        } else {
            this.isACK = false;
            this.data = toString(content,SIZEHEADER,packet.getLength()-SIZEHEADER);
        }
        this.sequenceNumber = toInt(content,1);

    }

    //Return a String of a slice of an byte of array
    private String toString(byte[] bytes, int offset,int length) {
        //ISO-8859-1 is the default charset encoding
            return new String(Arrays.copyOfRange(bytes, offset, offset + length));
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
