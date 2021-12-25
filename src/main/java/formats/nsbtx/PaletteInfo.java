
package formats.nsbtx;

/**
 * @author Trifindo
 */
public class PaletteInfo {

    public int paletteOffset;
    public int numColorsCode;
    //public boolean unknown;

    public PaletteInfo(byte[] data, int offset) {
        //unknown = data[offset + 0x02] == 0x01;
        numColorsCode = data[offset + 0x02];
        paletteOffset = ((data[offset + 0x01] & 0xFF) << 8 | data[offset] & 0xFF) << 3;
    }

    public int getNumColors() {
        int[] sizes = new int[]{16, 8};
        return sizes[numColorsCode];
    }

    public int getSize() {
        return getNumColors() * 2;
    }
}
