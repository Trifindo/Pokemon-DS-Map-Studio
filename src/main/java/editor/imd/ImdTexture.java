
package editor.imd;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import tileset.TilesetMaterial;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class ImdTexture {

    private static final int COLOR4 = 0;
    private static final int COLOR16 = 1;
    private static final int COLOR256 = 2;
    private static final int A3I5 = 3;
    private static final int A5I3 = 4;

    private static final int[] numColorsTable = new int[]{4, 16, 256, 32, 8};
    private static final int[] bitDepthTable = new int[]{2, 4, 8, 8, 8};
    private static final int minImgSize = 8;
    private static final int maxImgSize = 1024;

    private byte[] textureData;
    private byte[] paletteData;

    public int width;
    public int height;

    private boolean isTransparent = false;

    public ImdTexture(TilesetMaterial material) {
        //System.out.println("Texture name: " + material.getImageName());
        BufferedImage img = fixImageSize(material.getTextureImg());
        width = img.getWidth();
        height = img.getHeight();
        isTransparent = Utils.hasTransparentColor(img);
        if (material.getColorFormat() == A3I5) {
            setTextureAndPaletteSemitransp(img, material, 5);
        } else if (material.getColorFormat() == A5I3) {
            setTextureAndPaletteSemitransp(img, material, 3);
        } else {
            if (isTransparent) {
                setTextureAndPaletteWithTransparency(img, material);
            } else {
                setTextureAndPalette(img, material);
            }
        }

    }

    public void setTextureAndPaletteWithTransparency(BufferedImage img, TilesetMaterial material) {
        int numColors = numColorsTable[material.getColorFormat()];
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(255, 0, 255, 0));
        short[] colorIndices = new short[img.getWidth() * img.getHeight()];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = getDsColor(new Color(img.getRGB(i, j), true));
                int index;
                if (color.getAlpha() < 20) { //This could be < 255
                    index = 0;
                } else {
                    index = colors.indexOf(color);
                    if (index == -1) {
                        if (colors.size() < numColors) {
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
        for (int i = colors.size(); i < numColors; i++) {
            colors.add(Color.black);
        }
        textureData = colorIndicesToTexData(colorIndices, bitDepthTable[material.getColorFormat()]);
        paletteData = paletteColorsToPalData(colors);
    }

    public void setTextureAndPalette(BufferedImage img, TilesetMaterial material) {
        int numColors = numColorsTable[material.getColorFormat()];
        ArrayList<Color> colors = new ArrayList<>();
        short[] colorIndices = new short[img.getWidth() * img.getHeight()];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = getDsColor(new Color(img.getRGB(i, j)));
                int index = colors.indexOf(color);
                if (index == -1) {
                    if (colors.size() < numColors) {
                        index = colors.size();
                        colors.add(color);
                    } else {
                        index = getCloserColorIndex(color, colors);
                    }
                }
                colorIndices[c] = (short) index;
            }
        }
        for (int i = colors.size(); i < numColors; i++) {
            colors.add(Color.black);
        }
        textureData = colorIndicesToTexData(colorIndices, bitDepthTable[material.getColorFormat()]);
        paletteData = paletteColorsToPalData(colors);
    }

    public void setTextureAndPaletteSemitransp(BufferedImage img, TilesetMaterial material, int nBitsColor) {
        int numColors = numColorsTable[material.getColorFormat()];
        ArrayList<Color> colors = new ArrayList<>();
        short[] colorIndices = new short[img.getWidth() * img.getHeight()];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = getDsColor(new Color(img.getRGB(i, j), true));
                int index = colors.indexOf(color);
                if (index == -1) {
                    if (colors.size() < numColors) {
                        index = colors.size();
                        colors.add(color);
                    } else {
                        index = getCloserColorIndex(color, colors);
                    }
                }
                colorIndices[c] = (short) index;
            }
        }
        for (int i = colors.size(); i < numColors; i++) {
            colors.add(Color.black);
        }
        textureData = colorIndicesToTexDataSemitransp(colorIndices, colors, nBitsColor);
        paletteData = paletteColorsToPalData(colors);
    }

    private static byte[] colorIndicesToTexDataSemitransp(short[] colorIndices, 
            ArrayList<Color> colors, int nBitsColor) {
        final byte[] texData = new byte[colorIndices.length];
        for (int i = 0; i < texData.length; i++) {
            byte data = 0x00;
            data |= (colorIndices[i]) | (((colors.get(colorIndices[i]).getAlpha() & 0xFF) >> nBitsColor) << nBitsColor);
            texData[i + 1 - (i % 2) * 2] = data;
        }
        return texData;
    }

    private static byte[] colorIndicesToTexData(short[] colorIndices, int bitDepth) {
        int dataSize = (colorIndices.length * bitDepth) / 8;
        int pixelsPerByte = 8 / bitDepth;
        int mask = 0xFF >> (8 - bitDepth);
        int pixelIndex = 0;
        byte[] texData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            byte data = 0x00;
            for (int j = 0; j < pixelsPerByte; j++) {
                data |= (colorIndices[pixelIndex] & mask) << bitDepth * j;
                pixelIndex++;
            }
            texData[i + 1 - (i % 2) * 2] = data;
        }
        return texData;
    }

    private static byte[] paletteColorsToPalData(ArrayList<Color> colors) {
        byte[] palData = new byte[colors.size() * 2];
        for (int i = 0; i < colors.size(); i++) {
            Color c = colors.get(i);
            byte b1 = (byte) (((c.getBlue() >> 3) << 2) | ((c.getGreen() >> 6) & 0x03));
            byte b2 = (byte) ((((c.getGreen() >> 3) << 5) & 0xE0) | (c.getRed() >> 3));

            palData[i * 2] = b1;
            palData[i * 2 + 1] = b2;
        }
        return palData;
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
        int ad = c1.getAlpha() - c2.getAlpha();

        return rd * rd + gd * gd + bd * bd + ad * ad;
    }

    private static BufferedImage fixImageSize(BufferedImage img) {
        if (img.getWidth() % minImgSize != 0 || img.getHeight() % minImgSize != 0) {
            int newWidth = (img.getWidth() / minImgSize) * minImgSize;
            int newHeight = (img.getHeight() / minImgSize) * minImgSize;

            int newSize = Math.max(Math.min(Math.min(newWidth, newHeight), maxImgSize), minImgSize);
            img = Utils.resize(img, newSize, newSize, Image.SCALE_FAST);
            System.out.println("Texture resized");
        }
        return img;
    }

    public String getTexDataAsHexString() {
        return byteArrayToHexString(textureData);
    }

    public String getPalDataAsHexString() {
        return byteArrayToHexString(paletteData);
    }

    private static String byteArrayToHexString(byte[] data) {
        String hexString = "";
        for (int i = 0; i < data.length; i++) {
            if (i % 2 == 0) {
                hexString += " ";
            }
            hexString += String.format("%02x", data[i]);
        }
        return hexString;
    }

    public int getTextureDataSize() {
        return textureData.length / 2;
    }

    public int getPaletteDataSize() {
        return paletteData.length / 2;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    private Color getDsColor(Color c) {
        int r = (c.getRed() / 8) * 8;
        int g = (c.getGreen() / 8) * 8;
        int b = (c.getBlue() / 8) * 8;
        int a = (c.getAlpha() / 8) * 8;
        return new Color(r, g, b, a);
    }
}
