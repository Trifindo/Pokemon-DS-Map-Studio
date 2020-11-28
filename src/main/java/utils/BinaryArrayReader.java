package utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;



public class BinaryArrayReader {

    private byte[] buf;
    private int pos = 0;
    private int mark = 0;
    private int offset;

    public BinaryArrayReader(String path) throws IOException {
        buf = Files.readAllBytes(new File(path).toPath());
    }

    public BinaryArrayReader(byte[] buf, int offset) {
        this.buf = buf;
        this.offset = offset;
        this.pos = offset;
        this.mark = offset;
    }

    private int read() { return (pos < buf.length) ? (buf[pos++] & 0xff) : -1;}

    private int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos + len > buf.length) {
            return -1;
        }

        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    private int read(byte[] b){
        return read(b, 0,b.length);
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

    public void jumpAbs(int offset){
        pos = offset;
    }

    public void skip(int n){
        pos += n;
    }

    public String readString(int size) throws Exception {
        byte[] data = new byte[size];
        read(data);
        return new String(data);
    }

    public int readUInt8() throws Exception {
        return read() & 0xFF;
    }

    public int readUInt16() throws IOException {
        byte[] data = new byte[2];
        read(data);
        return ((data[1] & 0xff) << 8) | (data[0] & 0xff);
    }

    public long readUInt32() throws IOException {
        byte[] data = new byte[4];
        read(data);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFF;
    }


    public long readInt32() {
        byte[] data = new byte[4];
        read(data);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public float readFX32(){
        return (float) readInt32() / 0x1000;
    }

    public byte[] readBytes(int size) throws IOException {
        byte[] data = new byte[size];
        int result = read(data);
        if (result == -1) {
            throw new IOException();
        }
        return data;
    }

}
