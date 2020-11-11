package GBN;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Header {
    private static final Map<String, Short> packetTypes;
    private int seqNum;
    private short checksum;
    private short packetType;
    private byte[] headerBytes = new byte[8];

    private static final int MASK16 = 0xFFFF;
    private static final int MASK8 = 0xFF;

    static
    {
        packetTypes = new HashMap<String, Short>();
        packetTypes.put("Data", (short)21845);
        packetTypes.put("Ack", (short)-21846);
    }
    public Header(int seqNum, byte[] segmentStream, String pType ) {
        this.seqNum = seqNum;
        this.checksum = this.calCheckSum(segmentStream);
        this.packetType = packetTypes.get(pType);
        this.headerBytes = setHeaderBytes();
    }
    public static short calCheckSum(byte[] segmentStream) {
        int length = segmentStream.length;
        int i = 0;
        long sum = 0;
        while (length > 0) {
            sum += (segmentStream[i++] & MASK8) << 8;
            if ((--length)==0) break;
            sum += (segmentStream[i++] & MASK8);
            --length;
        }
        return (short) ((~((sum & MASK16)+(sum >> 16)))& MASK16);
    }

    public byte[] setHeaderBytes() {
        byte[] hb = new byte[8];
        System.arraycopy(intToByteArray(this.seqNum), 0, hb, 0, 4);
        System.arraycopy(shortToByteArray(this.checksum), 0, hb, 4, 2);
        System.arraycopy(shortToByteArray(this.packetType), 0, hb, 6, 2);
        //System.out.println(this.checksum);
        byte[] arr = Arrays.copyOfRange(hb, 6, 8);
        return hb;
    }

    public byte[] getHeaderBytes() {
        return headerBytes;
    }

    public static byte[] intToByteArray(int val) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(val);
        return b.array();
    }

    public static byte[] shortToByteArray(short val) {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.putShort(val);
        return b.array();
    }

    public static int byteArrayToInt(byte[] arr) {
        ByteBuffer wrapped = ByteBuffer.wrap(arr);
        int num = wrapped.getInt();
        return num;
    }
    public static short byteArrayToShort(byte[] arr) {
        ByteBuffer wrapped = ByteBuffer.wrap(arr);
        short num = wrapped.getShort();
        return num;
    }
}
