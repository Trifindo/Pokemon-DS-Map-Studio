
package formats.nsbtx2;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import utils.Utils;

/**
 * @author Trifindo
 */
public class NsbtxPalette {

    private String name;
    private byte[] data;

    public NsbtxPalette(byte[] nsbtxData, int offsetPaletteInfo, List<Integer> paletteOffsets, int paletteDataOffset, int offsetPaletteName) {
        //Load palette data
        int paletteOffset = ((nsbtxData[offsetPaletteInfo + 0x01] & 0xFF) << 8 | nsbtxData[offsetPaletteInfo] & 0xFF) << 3;
        int offsetIndex = paletteOffsets.indexOf(paletteOffset);
        boolean useColor4 = (nsbtxData[offsetPaletteInfo + 0x02] & 0xFF) == 0x01;
        if (useColor4) {
            data = new byte[8];
        } else {
            data = new byte[paletteOffsets.get(offsetIndex + 1) - paletteOffset];
        }
        System.arraycopy(nsbtxData, paletteDataOffset + paletteOffset, data, 0, Math.min(data.length, nsbtxData.length - paletteOffset));

        //Load palette name
        name = Utils.removeLastOccurrences(new String(nsbtxData, offsetPaletteName, 16), '\u0000');
    }

    public NsbtxPalette(String name, int numColors) {
        this.name = name;
        this.data = new byte[numColors * 2];
    }

    public List<Color> getColors(int numColorsRequested) {
        List<Color> colors = IntStream.range(0, numColorsRequested)
                .mapToObj(i -> new Color(0, 0, 0))
                .collect(Collectors.toList());

        int numColorsData = Math.min(data.length / 2, numColorsRequested);
        for (int i = 0; i < numColorsData; i++) {
            byte b2 = data[i * 2];
            byte b1 = data[i * 2 + 1];
            int b = ((b1 & 0x7C) >> 2) << 3;
            int g = (((b1 & 0x03) << 3) | ((b2 & 0xE0) >> 5)) << 3;
            int r = (b2 & 0x1F) << 3;

            colors.set(i, new Color(r, g, b));
        }
        return colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDataSize() {
        return data != null ? this.data.length : 0;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getDataSizeImd() {
        return this.data.length / 2;
    }

    public String getDataAsHexStringImd() {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < data.length; i += 2) {
            hexString.append(" ").append(String.format("%02x", data[i + 1])).append(String.format("%02x", data[i]));
        }
        return hexString.toString();
    }
}
