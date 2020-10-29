
package editor.nsbtx;

/**
 * @author Trifindo
 */
public class TextureInfo {

    int textureOffset;
    boolean transparent;
    int format;
    int width;
    int height;

    public TextureInfo(byte[] data, int offset) {
        textureOffset = ((data[offset + 0x01] & 0xFF) << 8 | (data[offset] & 0xFF)) << 3;

        byte b1 = data[offset + 0x03];
        byte b2 = data[offset + 0x02];

        transparent = ((b1 & 0x20) >> 5) == 1;
        format = (b1 & 0x1C) >> 2;
        height = 8 << (((b1 & 0x03) << 1) | ((b2 & 0x80) >> 7));
        width = 8 << ((b2 & 0x70) >> 4);
    }

    public int getBitDepth() {
        int[] bitDepth = new int[]{0, 8, 2, 4, 8, 2, 8, 16};
        return bitDepth[format];
    }

    public int getSize() {
        int[] bitDepth = new int[]{0, 8, 2, 4, 8, 2, 8, 16};
        return (width * height * bitDepth[format]) / 8;
    }

}
