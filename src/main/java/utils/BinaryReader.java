
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Trifindo
 */
public class BinaryReader {

    private FileInputStream fis;

    public BinaryReader(String path) throws FileNotFoundException {
        fis = new FileInputStream(new File(path));
    }

    public BinaryReader(File file) throws FileNotFoundException {
        fis = new FileInputStream(file);
    }

    public void close() throws IOException {
        fis.close();
    }

    public String readString(int size) throws IOException {
        byte[] data = new byte[size];
        fis.read(data);
        return new String(data);
    }

    public int readUInt8() throws IOException {
        return fis.read() & 0xFF;
    }

    public int readUInt16() throws IOException {
        byte[] data = new byte[2];
        fis.read(data);
        return ((data[1] & 0xff) << 8) | (data[0] & 0xff);
    }

    public long readUInt32() throws IOException {
        byte[] data = new byte[4];
        fis.read(data);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFF;
    }

    public byte[] readBytes(int size) throws IOException {
        byte[] data = new byte[size];
        int result = fis.read(data);
        if (result == -1) {
            throw new IOException();
        }
        return data;
    }

    public static int readInt8(byte[] fullData, int offset) throws Exception {
        return fullData[offset];
    }

    public static long readInt16(byte[] fullData, int offset) throws Exception {
        return (fullData[offset + 1] << 8) | fullData[offset];
    }

    public static long readInt32(byte[] fullData, int offset) throws Exception {
        return (fullData[offset + 3] << 8) | (fullData[offset + 2] << 8) | (fullData[offset + 1] << 8) | fullData[offset];
    }

    public static int readUInt8(byte[] fullData, int offset) throws Exception {
        return fullData[offset] & 0xFF;
    }

    public static long readUInt16(byte[] fullData, int offset) throws Exception {
        return ((fullData[offset + 1] & 0xff) << 8) | (fullData[offset] & 0xff);
    }

    public static long readUInt32(byte[] fullData, int offset) throws Exception {
        byte[] data = new byte[4];
        System.arraycopy(fullData, offset, data, 0, 4);
        return (long) (ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL);
    }

    public static float readFI32(byte[] fullData, int offset) throws Exception {
        return (float)readInt16(fullData, offset + 2) + readUInt16(fullData, offset) / 65535f;
    }

    public static String readString(byte[] fullData, int offset, int size) throws Exception {
        byte[] data = new byte[size];
        System.arraycopy(fullData, offset, data, 0, size);
        return new String(data);
    }

    public static byte[] readBytes(byte[] fullData, int offset, int size) throws Exception {
        byte[] data = new byte[size];
        System.arraycopy(fullData, offset, data, 0, size);
        return data;
    }

}
