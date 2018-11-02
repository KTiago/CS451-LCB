import java.nio.ByteBuffer;
import java.util.Arrays;

public class Utils {
    //Return a String of a slice of an byte of array
    public static String bytesArraytoString(byte[] bytes, int offset,int length) {
        //ISO-8859-1 is the default charset encoding
        return new String(Arrays.copyOfRange(bytes, offset, offset + length));
    }

    //Return the integer corresponding to 4 bytes in an array at a given offset
    public static int bytesArraytoInt(byte[] bytes, int offset) {
        int ret = 0;
        for (int i=0; i<4 && i+offset<bytes.length; i++) {
            ret <<= 8;
            ret |= (int)bytes[i+offset] & 0xFF;
        }
        return ret;
    }

    public static String intToString(int a){
        return new String(Arrays.copyOfRange(ByteBuffer.allocate(4).putInt(a).array(), 0, 4));
    }

    public static byte[] intTo4BytesArray(int a){
        return ByteBuffer.allocate(4).putInt(a).array();
    }
}
