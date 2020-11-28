
package formats.nsbtx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class Nsbtx {

    public static final String fileExtension = "nsbtx";
    public static final int maxNumColors = 16;
    public ArrayList<Byte[]> textureData;
    public ArrayList<Byte[]> paletteData;
    public ArrayList<TextureInfo> textureInfos;
    public ArrayList<PaletteInfo> paletteInfos;
    public ArrayList<String> textureNames;
    public ArrayList<String> paletteNames;
    public ArrayList<Integer> textureDataOffsets;
    public ArrayList<Integer> paletteDataOffsets;
    public byte[] rawData;

    public BufferedImage getImage(int textureIndex, int paletteIndex) {

        BufferedImage img = new BufferedImage(
                textureInfos.get(textureIndex).width,
                textureInfos.get(textureIndex).height,
                BufferedImage.TYPE_INT_ARGB);

        ArrayList<Color> colors = getPaletteColorsExtended(paletteIndex);
        if (textureInfos.get(textureIndex).transparent) {
            colors.set(0, new Color(
                    colors.get(0).getRed(),
                    colors.get(0).getGreen(),
                    colors.get(0).getBlue(),
                    0));
        }

        short[] colorIndices = getColorIndices(textureIndex);
        drawColors(img, textureInfos.get(textureIndex).format, colorIndices, colors);

        return img;
    }

    public void importTextureOnly(BufferedImage img, int textureIndex, int paletteIndex) {
        ArrayList<Color> colors = getPaletteColors(paletteIndex);
        short[] colorIndices;
        TextureInfo texInfo = textureInfos.get(textureIndex);
        int format = texInfo.format;
        switch (format) {
            case 1:
                //colorIndices = getColorIndices(img, colors, textureIndex, paletteIndex); //TODO: Change this
                break;
            default:
                if (texInfo.transparent) {
                    colorIndices = getColorIndicesWithTransparency(img, colors);
                } else {
                    colorIndices = getColorIndices(img, colors, textureIndex, paletteIndex);
                }
                setColorIndices(colorIndices, textureIndex);
                break;
        }
    }

    public void importTextureAndPalette(BufferedImage img, int textureIndex, int paletteIndex) {
        TextureInfo texInfo = textureInfos.get(textureIndex);
        int format = texInfo.format;
        switch (format) {
            case 1:
                break;
            default:
                if (texInfo.transparent) {
                    setTextureAndPaletteWithTransparency(img, textureIndex, paletteIndex);
                } else {
                    setTextureAndPalette(img, textureIndex, paletteIndex);
                }
                break;
        }

    }

    public void setTextureAndPalette(BufferedImage img, int textureIndex, int paletteIndex) {
        ArrayList<Color> colors = new ArrayList<>();
        TextureInfo texInfo = textureInfos.get(textureIndex);
        PaletteInfo palInfo = paletteInfos.get(paletteIndex);
        short[] colorIndices = new short[texInfo.width * texInfo.height];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = new Color(img.getRGB(i, j));
                int index = colors.indexOf(color);
                if (index == -1) {
                    if (colors.size() < palInfo.getNumColors()) {
                        index = colors.size();
                        colors.add(color);
                    } else {
                        index = getCloserColorIndex(color, colors);
                    }
                }
                colorIndices[c] = (short) index;
            }
        }
        setColorIndices(colorIndices, textureIndex);
        setPaletteColorsExtended(colors, palInfo, paletteIndex);
    }

    public void setTextureAndPaletteWithTransparency(BufferedImage img, int textureIndex, int paletteIndex) {
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(255, 0, 255, 0));
        TextureInfo texInfo = textureInfos.get(textureIndex);
        PaletteInfo palInfo = paletteInfos.get(paletteIndex);
        short[] colorIndices = new short[texInfo.width * texInfo.height];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = new Color(img.getRGB(i, j), true);
                int index;
                if (color.getAlpha() < 250) { //This could be < 255
                    index = 0;

                } else {
                    index = colors.indexOf(color);
                    if (index == -1) {
                        if (colors.size() < palInfo.getNumColors()) {
                            index = colors.size();
                            colors.add(color);
                        } else {
                            index = getCloserColorIndex(color, colors);
                        }
                    }
                }
                colorIndices[c] = (short) index;
            }
        }
        setColorIndices(colorIndices, textureIndex);
        setPaletteColorsExtended(colors, palInfo, paletteIndex);
    }

    public void setColor(int paletteIndex, int colorIndex, Color color) {
        byte b1 = (byte) (((color.getBlue() >> 3) << 2) | ((color.getGreen() >> 6) & 0x03));
        byte b2 = (byte) ((((color.getGreen() >> 3) << 5) & 0xE0) | (color.getRed() >> 3));

        paletteData.get(paletteIndex)[colorIndex * 2] = b2;
        paletteData.get(paletteIndex)[colorIndex * 2 + 1] = b1;
    }

    public void swapColors(int palleteIndex, int index1, int index2) {
        int bytesPerColor = 2;
        index1 *= bytesPerColor;
        index2 *= bytesPerColor;

        Byte[] palette = paletteData.get(palleteIndex);
        for (int i = 0; i < 2; i++) {
            byte temp = palette[index1 + i];
            palette[index1 + i] = palette[index2 + i];
            palette[index2 + i] = temp;
        }
    }

    public void swapColors(int textureIndex, int palleteIndex, int index1, int index2) {
        short[] colorIndices = getColorIndices(textureIndex);
        short[] colorIndicesCopy = new short[colorIndices.length];
        System.arraycopy(colorIndices, 0, colorIndicesCopy, 0, colorIndices.length);
        for (int i = 0; i < colorIndices.length; i++) {
            if (colorIndicesCopy[i] == index1) {
                colorIndices[i] = (short) index2;
            } else if (colorIndicesCopy[i] == index2) {
                colorIndices[i] = (short) index1;
            }
        }
        setColorIndices(colorIndices, textureIndex);

        int bytesPerColor = 2;
        index1 *= bytesPerColor;
        index2 *= bytesPerColor;

        Byte[] palette = paletteData.get(palleteIndex);
        for (int i = 0; i < 2; i++) {
            byte temp = palette[index1 + i];
            palette[index1 + i] = palette[index2 + i];
            palette[index2 + i] = temp;
        }

    }

    private void setColorIndices(short[] colorIndices, int textureIndex) {
        int bitDepth = textureInfos.get(textureIndex).getBitDepth();
        int pixelsPerByte = 8 / bitDepth;
        int mask = 0xFF >> (8 - bitDepth);
        int pixelIndex = 0;
        for (int i = 0; i < textureData.get(textureIndex).length; i++) {
            byte data = 0x00;
            for (int j = 0; j < pixelsPerByte; j++) {
                data |= (colorIndices[pixelIndex] & mask) << bitDepth * j;
                pixelIndex++;
            }
            textureData.get(textureIndex)[i] = data;
        }
    }

    private void setPaletteColorsExtended(ArrayList<Color> colors, PaletteInfo palInfo, int paletteIndex) {
        for (int i = colors.size(); i < palInfo.getNumColors(); i++) {
            colors.add(Color.black);
        }

        for (int i = 0; i < colors.size(); i++) {
            setColor(paletteIndex, i, colors.get(i));
        }
    }

    private short[] getColorIndices(BufferedImage img,
                                    ArrayList<Color> colors, int textureIndex, int paletteIndex) {
        short[] colorIndices = new short[img.getWidth() * img.getHeight()];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = new Color(img.getRGB(i, j));
                short index = (short) colors.indexOf(color);
                if (index != -1) {
                    colorIndices[c] = index;
                } else {
                    colorIndices[c] = (short) getCloserColorIndex(color, colors);
                }
            }
        }
        return colorIndices;
    }

    private short[] getColorIndicesWithTransparency(BufferedImage img,
                                                    ArrayList<Color> colors) {
        short[] colorIndices = new short[img.getWidth() * img.getHeight()];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = new Color(img.getRGB(i, j));
                short index = (short) colors.indexOf(color);
                if (index != -1) {
                    colorIndices[c] = index;
                } else if (color.getAlpha() < 250) {//TODO: this conditional could just be == 0
                    colorIndices[c] = 0;
                } else {
                    colorIndices[c] = (short) getCloserColorIndex(color, colors);
                }
            }
        }
        return colorIndices;
    }

    public void drawColors(BufferedImage img, int format, short[] colorIndices, ArrayList<Color> colors) {
        switch (format) {
            case 1:
                drawColorsWithTransparency(img, (byte) 0x0F, colorIndices, colors);
                break;
            case 6:
                drawColorsWithTransparency(img, (byte) 0x07, colorIndices, colors);
                break;
            default:
                for (int j = 0, c = 0; j < img.getHeight(); j++) {
                    for (int i = 0; i < img.getWidth(); i++, c++) {
                        try {
                            img.setRGB(i, j, colors.get(colorIndices[c]/* & 0x0F*/).getRGB());
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("aweonao");
                        }
                    }
                }
                break;
        }

    }

    private void drawColorsWithTransparency(BufferedImage img, byte mask,
                                            short[] colorIndices, ArrayList<Color> colors) {
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                try {
                    Color color = new Color(colors.get(colorIndices[c] & mask).getRGB()); //0x0F
                    int transparency = (colorIndices[c] & (~mask & 0xFF)); //Short form for (colorIndices[c] >> 4)*4  //0xF0
                    Color transpColor = new Color(
                            color.getRed(),
                            color.getGreen(),
                            color.getBlue(),
                            transparency);
                    img.setRGB(i, j, transpColor.getRGB());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("aweonao transp");
                }
            }
        }
    }

    public ArrayList<Color> getPaletteColorsExtended(int paletteIndex) {
        int numColors = paletteInfos.get(paletteIndex).getNumColors();
        ArrayList<Color> colors = new ArrayList<>(numColors);
        for (int i = 0; i < maxNumColors; i++) {
            colors.add(new Color(0, 0, 0));
        }
        for (int i = 0; i < numColors; i++) {
            byte b2 = paletteData.get(paletteIndex)[i * 2];
            byte b1 = paletteData.get(paletteIndex)[i * 2 + 1];
            int b = ((b1 & 0x7C) >> 2) << 3;
            int g = (((b1 & 0x03) << 3) | ((b2 & 0xE0) >> 5)) << 3;
            int r = (b2 & 0x1F) << 3;

            //System.out.println(r + " " + g + " " + b);
            colors.set(i, new Color(r, g, b));
        }
        return colors;
    }

    public Color getPaletteColor(int paletteIndex, int colorIndex) {
        byte b2 = paletteData.get(paletteIndex)[colorIndex * 2];
        byte b1 = paletteData.get(paletteIndex)[colorIndex * 2 + 1];
        int b = ((b1 & 0x7C) >> 2) << 3;
        int g = (((b1 & 0x03) << 3) | ((b2 & 0xE0) >> 5)) << 3;
        int r = (b2 & 0x1F) << 3;

        return new Color(r, g, b);
    }

    public ArrayList<Color> getPaletteColors(int paletteIndex) {
        int numColors = paletteInfos.get(paletteIndex).getNumColors();
        ArrayList<Color> colors = new ArrayList<>(numColors);
        for (int i = 0; i < numColors; i++) {
            byte b2 = paletteData.get(paletteIndex)[i * 2];
            byte b1 = paletteData.get(paletteIndex)[i * 2 + 1];
            int b = ((b1 & 0x7C) >> 2) << 3;
            int g = (((b1 & 0x03) << 3) | ((b2 & 0xE0) >> 5)) << 3;
            int r = (b2 & 0x1F) << 3;

            //System.out.println(r + " " + g + " " + b);
            colors.add(new Color(r, g, b));
        }
        return colors;
    }

    public short[] getColorIndices(int textureIndex) {
        int bitDepth = textureInfos.get(textureIndex).getBitDepth();
        int pixelsPerByte = 8 / bitDepth;
        int mask = 0xFF >> (8 - bitDepth);
        int pixelIndex = 0;
        short[] colorIndices = new short[textureInfos.get(textureIndex).width
                * textureInfos.get(textureIndex).height];
        for (int i = 0; i < textureData.get(textureIndex).length; i++) {
            byte data = textureData.get(textureIndex)[i];
            for (int j = 0; j < pixelsPerByte; j++) {
                int colorIndex = (data >> bitDepth * j) & mask;
                colorIndices[pixelIndex] = (short) colorIndex;
                pixelIndex++;
            }
        }
        return colorIndices;
    }

    private int getCloserColorIndex(Color c, ArrayList<Color> colors) {
        int index = 0;
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < colors.size(); i++) {
            int dist = getDistanceToColor(c, colors.get(i));
            if (dist < minDist) {
                index = i;
                minDist = dist;
            }
        }
        return index;
    }

    private static int getDistanceToColor(Color c1, Color c2) {
        int rd = c1.getRed() - c2.getRed();
        int gd = c1.getGreen() - c2.getGreen();
        int bd = c1.getBlue() - c2.getBlue();

        return rd * rd + gd * gd + bd * bd;
    }

}
