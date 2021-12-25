package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryArrayWriter {

    private byte[] buf;
    private int pos;
    private int mark;
    private int offset;

    public BinaryArrayWriter(byte[] buf, int offset) {
        this.buf = buf;
        this.offset = offset;
        this.pos = offset;
        this.mark = offset;
    }

    public void writeUInt8(int value) {
        buf[pos] = (byte) value;
        pos++;
    }

    public void writeUInt16(int value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort((short) (value & 0xffffL));
        byte[] array = byteBuffer.array();
        System.arraycopy(array, 0, buf, pos, 2);
        pos += 2;
    }

    public void writeUInt32(long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt((int) (value & 0xffffffffL));
        byte[] array = byteBuffer.array();
        System.arraycopy(array, 0, buf, pos, 4);
        pos += 4;
    }

    public void mark(){
        mark = pos;
    }

    public void reset(){
        pos = mark;
    }

    public void jumpRel(int relOffset){
        pos = offset + relOffset;
    }
}
