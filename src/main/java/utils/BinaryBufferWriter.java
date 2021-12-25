package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class BinaryBufferWriter {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    protected byte[] buf;
    protected int pos;
    protected int size;
    protected int mark;

    public BinaryBufferWriter(int size){
        buf = new byte[size];
    }

    public BinaryBufferWriter(){
        this(32);
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.length > 0)
            grow(minCapacity);
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        buf = Arrays.copyOf(buf, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public synchronized byte[] toByteArray() {
        return Arrays.copyOf(buf, size);
    }

    public synchronized void write(int b) {
        ensureCapacity(pos + 1);
        buf[pos] = (byte) b;
        pos++;
        size = Math.max(pos, size);
    }

    public synchronized void write(byte[] b) {
        write(b, 0, b.length);
    }

    public synchronized void write(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || (off + len - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(pos + len);
        System.arraycopy(b, off, buf, pos, len);
        pos += len;
        size = Math.max(pos, size);
    }

    public void mark(){
        mark = pos;
    }

    public void reset(){
        pos = mark;
    }

    public void skip(int n){
        pos += n;
    }

    public void jumpRel(int relOffset){
        pos = relOffset;
    }

    public void writeUInt8(int value) {
        write(value);
    }

    public void writeUInt16(int value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort((short) (value & 0xffffL));
        byte[] array = byteBuffer.array();
        write(array, 0, array.length);
    }

    public void writeUInt32(long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt((int) (value & 0xffffffffL));
        byte[] array = byteBuffer.array();
        write(array, 0, array.length);
    }

    public void writeInt32(long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt((int)value);
        byte[] array = byteBuffer.array();
        write(array, 0, array.length);
    }

    public void writeString(String string){
        byte[] array = string.getBytes();
        write(array, 0, array.length);
    }

    public int getPos(){
        return pos;
    }
}
