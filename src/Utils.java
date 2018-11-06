import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Utils {
    //Return a String of a slice of an byte of array
    public static String bytesArraytoString(byte[] bytes, int offset,int length) {
        //ISO-8859-1 is the default charset encoding
        return new String(Arrays.copyOfRange(bytes, offset, offset + length),Charset.forName("ISO-8859-1"));
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

    //Return a string of 4 characters corresponding to a 4 bytes int
    public static String intToString(int a){
        return new String(Arrays.copyOfRange(intTo4BytesArray(a), 0, 4),Charset.forName("ISO-8859-1"));
    }

    //Return a 4 bytes Array corresponding to an int
    public static byte[] intTo4BytesArray(int a){
        return ByteBuffer.allocate(4).putInt(a).array();
    }
}
