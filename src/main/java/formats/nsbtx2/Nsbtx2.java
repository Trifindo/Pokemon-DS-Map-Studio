
package formats.nsbtx2;

import formats.nsbtx2.exceptions.NsbtxTextureSizeException;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import utils.Utils;
import utils.image.Clusterer;
import utils.image.FastColor;

/**
 * @author Trifindo
 */
public class Nsbtx2 {

    public static final int FORMAT_NO_TEXTURE = 0;
    public static final int FORMAT_A3I5 = 1;
    public static final int FORMAT_COLOR_4 = 2;
    public static final int FORMAT_COLOR_16 = 3;
    public static final int FORMAT_COLOR_256 = 4;
    public static final int FORMAT_4X4_TEXEL = 5;
    public static final int FORMAT_A5I3 = 6;
    public static final int FORMAT_DIRECT_TEXTURE = 7;

    public static final int[] bitDepth = {0, 8, 2, 4, 8, 2, 8, 16};
    public static final int[] numColors = {0, 32, 4, 16, 256, 0, 8, 0};
    public static final int[] indicesMask = {0xFF, 0x1F, 0xFF, 0xFF, 0xFF, 0xFF, 0x07, 0xFF};
    //public static final int[] numColors = {0, 32, 4, 16, 256, 0, 8, 0};
    public static final String[] formatNames = {
            "NO TEXTURE",
            "a3i5",
            "palette4",
            "palette16",
            "palette256",
            "4X4 TEXEL",
            "a5i3",
            "DIRECT TEXTURE"
    };

    public static final int[] jcbToFormatLookup = {2, 3, 4, 1, 6};
    public static final int[] formatToJcbLookup = {0, 3, 0, 1, 2, 0, 4, 0};

    public static final int maxNameSize = 16;

    private static final int minImgSize = 8;
    private static final int maxImgSize = 128;

    private String path = null;

    private ArrayList<NsbtxTexture> textures;
    private ArrayList<NsbtxPalette> palettes;

    public Nsbtx2(ArrayList<NsbtxTexture> textures, ArrayList<NsbtxPalette> palettes) {
        this.textures = textures;
        this.palettes = palettes;

        this.path = null;
    }

    public Nsbtx2() {
        this.textures = new ArrayList<>();
        this.palettes = new ArrayList<>();

        this.path = null;
    }

    public void addTextures(ArrayList<NsbtxTexture> newTextures) {
        textures.addAll(newTextures);
    }

    public void addPalettes(ArrayList<NsbtxPalette> newPalettes) {
        palettes.addAll(newPalettes);
    }

    public void addNsbtx(Nsbtx2 newNsbtx) {
        addTextures(newNsbtx.getTextures());
        addPalettes(newNsbtx.getPalettes());
    }

    public void addTexture(NsbtxTexture texture) {
        this.textures.add(texture);
    }

    public void addTextureIfNameIsNotFound(NsbtxTexture texture) {
        if (!isTextureNameUsed(texture.getName())) {
            textures.add(texture);
        }
    }

    public boolean isTextureNameUsed(String name) {
        for (NsbtxTexture tex : textures) {
            if (tex.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addPalette(NsbtxPalette palette) {
        this.palettes.add(palette);
    }

    public void addPaletteIfNameIsNotFound(NsbtxPalette palette) {
        if (!isPaletteNameUsed(palette.getName())) {
            palettes.add(palette);
        }
    }

    public boolean isPaletteNameUsed(String name) {
        for (NsbtxPalette tex : palettes) {
            if (tex.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public BufferedImage getImage(int textureIndex, int paletteIndex) {
        return getImage(textures.get(textureIndex), palettes.get(paletteIndex));
    }

    public BufferedImage getImage(NsbtxTexture tex, NsbtxPalette pal) {
        //Generate image
        BufferedImage img = new BufferedImage(
                tex.getWidth(), tex.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        //Get palette colors
        ArrayList<Color> colors = pal.getColors(tex.getNumColors());

        //Check if first color is transparent
        if (tex.isTransparent()) {
            colors.set(0, new Color(0, 0, 0, 0));
        }

        //Get color indices
        byte[] colorIndices = tex.getColorIndices();

        //Draw pixels
        switch (tex.getColorFormat()) {
            case FORMAT_COLOR_4:
                drawColorsOpaque(img, colors, colorIndices);
                break;
            case FORMAT_COLOR_16:
                drawColorsOpaque(img, colors, colorIndices);
                break;
            case FORMAT_COLOR_256:
                drawColorsOpaque(img, colors, colorIndices);
                break;
            case FORMAT_A3I5:
                drawColorsSemitransparent(img, colors, colorIndices, (byte) 0x1F, tex.isTransparent());
                break;
            case FORMAT_A5I3:
                drawColorsSemitransparent(img, colors, colorIndices, (byte) 0x07, tex.isTransparent());
                break;
        }
        return img;
    }

    public void drawColorsOpaque(BufferedImage img, ArrayList<Color> colors,
                                 byte[] colorIndices) {
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                try {
                    img.setRGB(i, j, colors.get(colorIndices[c] & 0xFF).getRGB());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawColorsSemitransparent(BufferedImage img,
                                           ArrayList<Color> colors, byte[] colorIndices, byte mask, boolean isTransparent) {
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                try {
                    int index = (colorIndices[c] & 0xFF) & mask;
                    Color color = new Color(colors.get(index).getRGB()); //0x0F

                    int transparency;
                    if (index == 0 && isTransparent) {
                        transparency = 0;
                    } else {
                        transparency = ((colorIndices[c] & 0xFF) & (~mask & 0xFF));//Short form for (colorIndices[c] >> 4)*4  //0xF0
                    }
                    Color transpColor = new Color(
                            color.getRed(),
                            color.getGreen(),
                            color.getBlue(),
                            transparency);
                    img.setRGB(i, j, transpColor.getRGB());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void replaceTexture(int textureIndex, int paletteIndex,
                               BufferedImage newImg, int colorFormat, boolean isTransparent) {
        this.textures.set(textureIndex, generateTexture(
                textures.get(textureIndex).getName(),
                palettes.get(paletteIndex).getColors(numColors[colorFormat]),
                newImg, colorFormat, isTransparent));
    }

    public void addTexture(int textureIndex, int paletteIndex,
                           BufferedImage newImg, int colorFormat, boolean isTransparent,
                           String textureName) {
        NsbtxTexture newTexture = generateTexture(textureName,
                palettes.get(paletteIndex).getColors(numColors[colorFormat]),
                newImg, colorFormat, isTransparent);
        try {
            textures.add(textureIndex + 1, newTexture);
        } catch (IndexOutOfBoundsException ex) {
            textures.add(newTexture);
        }
    }

    public void replaceTextureAndPalette(int textureIndex, int paletteIndex,
                                         BufferedImage newImg, int colorFormat, boolean isTransparent) {

        ArrayList<Color> colors = generatePaletteColors(newImg, numColors[colorFormat]);
        NsbtxPalette palette = generatePalette(colors, palettes.get(paletteIndex).getName());
        this.palettes.set(paletteIndex, palette);

        this.textures.set(textureIndex, generateTexture(
                textures.get(textureIndex).getName(), colors,
                newImg, colorFormat, isTransparent));
    }

    public void addTextureAndPalette(int textureIndex, int paletteIndex,
                                     BufferedImage newImg, int colorFormat, boolean isTransparent,
                                     String textureName, String paletteName) {

        ArrayList<Color> colors = generatePaletteColors(newImg, numColors[colorFormat]);

        NsbtxPalette newPalette = generatePalette(colors, paletteName);
        try {
            palettes.add(paletteIndex + 1, newPalette);
        } catch (IndexOutOfBoundsException ex) {
            palettes.add(newPalette);
        }

        NsbtxTexture newTexture = generateTexture(
                textureName, colors,
                newImg, colorFormat, isTransparent);
        try {
            textures.add(textureIndex + 1, newTexture);
        } catch (IndexOutOfBoundsException ex) {
            textures.add(newTexture);
        }
    }

    private ArrayList<Color> generatePaletteColors(BufferedImage newImg, int numColors) {
        //Fix new image size
        newImg = fixImageSize(newImg);

        //Get colors from texture
        TreeSet<FastColor> colorSet = new TreeSet<>();
        colorSet = addColorsFromTexture(colorSet, newImg);

        //Cluster colors if the image has too many colors or add colors
        ArrayList<Color> colors = new ArrayList<>();
        colors.addAll(colorSet);
        if (colorSet.size() > numColors) {
            colors = Clusterer.clusterColors(colors, numColors, 100, 0.00001f);
        } else {
            int nColorsToAdd = numColors - colorSet.size();
            for (int j = 0; j < nColorsToAdd; j++) {
                colors.add(new Color(0, 0, 0, 255));
            }
        }

        //Move most transparent color to the front
        int lessAlphaColorIndex = getLessAlphaColorIndex(colors);
        if (colors.get(lessAlphaColorIndex).getAlpha() < 20) {
            Collections.swap(colors, 0, lessAlphaColorIndex);
        }

        return colors;
    }

    private NsbtxPalette generatePalette(ArrayList<Color> colors,
                                         String paletteName) {

        //Generate NSBTX palette data
        NsbtxPalette newPalette = new NsbtxPalette(paletteName, colors.size());
        newPalette.setData(paletteColorsToPalData(colors));

        return newPalette;
    }

    private NsbtxPalette generateFixedPalette(BufferedImage oldImg, int numColorsOldImg,
                                              NsbtxPalette oldPalette, String paletteName,
                                              BufferedImage newImg, byte[] colorIndices, int indexMask)
            throws NsbtxTextureSizeException {

        //Fix new image size
        newImg = fixImageSize(newImg);

        //Check if the source image and the destination image have the same size
        //BufferedImage oldImg = getImage(oldTexture, oldPalette);
        if (oldImg.getWidth() != newImg.getWidth() || oldImg.getHeight() != newImg.getHeight()) {
            throw new NsbtxTextureSizeException();
        }

        //Get colors from texture
        TreeSet<FastColor> colorSet = new TreeSet<>();
        colorSet = addColorsFromTexture(colorSet, newImg);

        //Cluster colors if the image has too many colors or add colors
        int numColors = numColorsOldImg;
        ArrayList<Color> colors = new ArrayList<>();
        colors.addAll(colorSet);
        if (colorSet.size() > numColors) {
            colors = Clusterer.clusterColors(colors, numColors, 100, 0.00001f);
        } else {
            int nColorsToAdd = numColors - colorSet.size();
            for (int j = 0; j < nColorsToAdd; j++) {
                colors.add(new Color(0, 0, 0, 255));
            }
        }

        //Move most transparent color to the front
        int lessAlphaColorIndex = getLessAlphaColorIndex(colors);
        if (colors.get(lessAlphaColorIndex).getAlpha() < 20) {
            Collections.swap(colors, 0, lessAlphaColorIndex);
        }

        //Fix color order to match the original texture pixels
        ArrayList<Color> refPal = oldPalette.getColors(numColors);
        colors = fixColorOrder(oldImg, newImg, refPal, colors, colorIndices, indexMask);

        //Generate NSBTX palette data
        NsbtxPalette newPalette = new NsbtxPalette(paletteName, colors.size());
        newPalette.setData(paletteColorsToPalData(colors));

        return newPalette;
    }

    public void replacePalette(int textureIndex, int paletteIndex,
                               BufferedImage newImg) throws NsbtxTextureSizeException {

        byte[] indices = textures.get(textureIndex).getColorIndices();

        palettes.set(paletteIndex, generateFixedPalette(
                getImage(textures.get(textureIndex), palettes.get(paletteIndex)),
                textures.get(textureIndex).getNumColors(), palettes.get(paletteIndex),
                palettes.get(paletteIndex).getName(), newImg,
                textures.get(textureIndex).getColorIndices(),
                indicesMask[textures.get(textureIndex).getColorFormat()]
        ));
    }

    public void addPalette(int textureIndex, int paletteIndex, String paletteName,
                           BufferedImage newImg) throws NsbtxTextureSizeException {
        NsbtxPalette newPalette = generateFixedPalette(
                getImage(textures.get(textureIndex), palettes.get(paletteIndex)),
                textures.get(textureIndex).getNumColors(), palettes.get(paletteIndex),
                paletteName, newImg,
                textures.get(textureIndex).getColorIndices(),
                indicesMask[textures.get(textureIndex).getColorFormat()]
        );
        try {
            palettes.add(paletteIndex + 1, newPalette);
        } catch (IndexOutOfBoundsException ex) {
            palettes.add(newPalette);
        }
    }


    private ArrayList<Color> fixColorOrder(BufferedImage refImg,
                                           BufferedImage fixImg, ArrayList<Color> refPal, ArrayList<Color> fixPal,
                                           byte[] colorIndices, int indexMask) {

        int[][] colorCount = new int[refPal.size()][fixPal.size()];
        if (refImg.getWidth() == fixImg.getWidth() && refImg.getHeight() == fixImg.getHeight()) {
            for (int j = 0, c = 0; j < refImg.getHeight(); j++) {
                for (int i = 0; i < refImg.getWidth(); i++, c++) {
                    int fixColorIndex = getCloserColorIndex(getDsColor(new Color(fixImg.getRGB(i, j), true)), fixPal);
                    colorCount[(colorIndices[c] & 0xFF) & indexMask][fixColorIndex]++;
                }
            }
        }


        ArrayList<Color> newPalette = new ArrayList<>(fixPal.size());
        for (int i = 0; i < colorCount.length; i++) {
            int maxIndex = 0;
            int maxValue = -1;
            for (int j = 0; j < colorCount[i].length; j++) {
                if (colorCount[i][j] > maxValue) {
                    maxIndex = j;
                    maxValue = colorCount[i][j];
                }
            }
            newPalette.add(fixPal.get(maxIndex));
        }
        return newPalette;
    }

    /*
    private ArrayList<Color> fixColorOrder(BufferedImage refImg,
            BufferedImage fixImg, ArrayList<Color> refPal, ArrayList<Color> fixPal) {
        int numColors = Math.max(refPal.size(), fixPal.size());
        ArrayList<Integer> colorLookup = new ArrayList<>(numColors);
        for (int i = 0; i < numColors; i++) {
            colorLookup.add(i);
        }

        if (refImg.getWidth() == fixImg.getWidth() && refImg.getHeight() == fixImg.getHeight()) {
            for (int j = 0; j < refImg.getHeight(); j++) {
                for (int i = 0; i < refImg.getWidth(); i++) {
                    int refColorIndex = getCloserColorIndex(getDsColor(new Color(refImg.getRGB(i, j), true)), refPal);
                    int fixColorIndex = getCloserColorIndex(getDsColor(new Color(fixImg.getRGB(i, j), true)), fixPal);
                    colorLookup.set(refColorIndex, fixColorIndex);
                }
            }
        }

        ArrayList<Color> newPalette = new ArrayList<>(fixPal.size());
        for (int i = 0; i < fixPal.size(); i++) {
            newPalette.add(fixPal.get(colorLookup.get(i)));
        }
        return newPalette;
    }
    */

    private TreeSet<FastColor> addColorsFromTexture(TreeSet<FastColor> colors, BufferedImage img) {
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                FastColor color = getDsFastColor(new Color(img.getRGB(i, j), true));
                colors.add(color);
            }
        }
        return colors;
    }

    private int getLessAlphaColorIndex(ArrayList<Color> colors) {
        int minAlpha = 255;
        int minIndex = 0;
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).getAlpha() < minAlpha) {
                minAlpha = colors.get(i).getAlpha();
                minIndex = i;
            }
        }
        return minIndex;
    }

    private NsbtxTexture generateTexture(String textureName, ArrayList<Color> colors,
                                         BufferedImage newImg, int colorFormat, boolean isTransparent) {

        newImg = fixImageSize(newImg);
        NsbtxTexture newTex = new NsbtxTexture(textureName, colorFormat,
                isTransparent,//Utils.hasTransparentColor(newImg),
                newImg.getWidth(), newImg.getHeight());

        //ArrayList<Color> colors = pal.getColors(newTex.getNumColors());
        byte[] colorIndices;
        if (newTex.isTransparent()) {
            colorIndices = getCloserColorIndicesTransparency(newImg, colors);
        } else {
            colorIndices = getCloserColorIndices(newImg, colors);
        }

        switch (newTex.getColorFormat()) {
            case FORMAT_COLOR_4:
                newTex.setData(colorIndicesToTexData(colorIndices, newTex.getBitDepth()));
                break;
            case FORMAT_COLOR_16:
                newTex.setData(colorIndicesToTexData(colorIndices, newTex.getBitDepth()));
                break;
            case FORMAT_COLOR_256:
                newTex.setData(colorIndicesToTexData(colorIndices, newTex.getBitDepth()));
                break;
            case FORMAT_A3I5:
                newTex.setData(colorIndicesToTexDataSemitransp(colorIndices, colors, 5));
                break;
            case FORMAT_A5I3:
                newTex.setData(colorIndicesToTexDataSemitransp(colorIndices, colors, 3));
                break;
        }
        return newTex;
    }

    private static byte[] colorIndicesToTexData(byte[] colorIndices, int bitDepth) {
        int dataSize = (colorIndices.length * bitDepth) / 8;
        int pixelsPerByte = 8 / bitDepth;
        int mask = 0xFF >> (8 - bitDepth);
        int pixelIndex = 0;
        byte[] texData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            byte data = 0x00;
            for (int j = 0; j < pixelsPerByte; j++) {
                data |= ((colorIndices[pixelIndex] & 0xFF) & mask) << bitDepth * j;
                pixelIndex++;
            }
            //texData[i + 1 - (i % 2) * 2] = data;
            texData[i] = data;
        }
        return texData;
    }

    private static byte[] colorIndicesToTexDataSemitransp(byte[] colorIndices,
                                                          ArrayList<Color> colors, int nBitsColor) {
        final byte[] texData = new byte[colorIndices.length];
        for (int i = 0; i < texData.length; i++) {
            byte data = 0x00;
            data |= (colorIndices[i] & 0xFF) | (((colors.get(colorIndices[i] & 0xFF).getAlpha() & 0xFF) >> nBitsColor) << nBitsColor);
            //texData[i + 1 - (i % 2) * 2] = data;
            texData[i] = data;
        }
        return texData;
    }

    private static byte[] paletteColorsToPalData(ArrayList<Color> colors) {
        byte[] palData = new byte[colors.size() * 2];
        for (int i = 0; i < colors.size(); i++) {
            Color c = colors.get(i);
            byte b1 = (byte) (((c.getBlue() >> 3) << 2) | ((c.getGreen() >> 6) & 0x03));
            byte b2 = (byte) ((((c.getGreen() >> 3) << 5) & 0xE0) | (c.getRed() >> 3));

            palData[i * 2] = b2;
            palData[i * 2 + 1] = b1;
        }
        return palData;
    }

    private byte[] getCloserColorIndices(BufferedImage img, ArrayList<Color> colors) {
        byte[] colorIndices = new byte[img.getWidth() * img.getHeight()];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = getDsColor(new Color(img.getRGB(i, j), true));
                colorIndices[c] = (byte) getCloserColorIndex(color, colors);
            }
        }
        return colorIndices;
    }

    private byte[] getCloserColorIndicesTransparency(BufferedImage img, ArrayList<Color> colors) {
        byte[] colorIndices = new byte[img.getWidth() * img.getHeight()];
        for (int j = 0, c = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++, c++) {
                Color color = getDsColor(new Color(img.getRGB(i, j), true));
                if (color.getAlpha() < 20) {
                    colorIndices[c] = 0;
                } else {
                    colorIndices[c] = (byte) getCloserColorIndex(color, colors, 1);
                }
            }
        }
        return colorIndices;
    }

    private static BufferedImage fixImageSize(BufferedImage img) {
        if (img.getWidth() % minImgSize != 0 || img.getHeight() % minImgSize != 0
                || img.getWidth() > maxImgSize || img.getWidth() < minImgSize
                || img.getHeight() > maxImgSize || img.getHeight() < minImgSize) {
            int newWidth = (img.getWidth() / minImgSize) * minImgSize;
            int newHeight = (img.getHeight() / minImgSize) * minImgSize;

            newWidth = Math.max(Math.min(newWidth, maxImgSize), minImgSize);
            newHeight = Math.max(Math.min(newHeight, maxImgSize), minImgSize);

            img = Utils.resize(img, newWidth, newHeight, Image.SCALE_FAST);
        }
        return img;
    }

    private int getCloserColorIndex(Color c, ArrayList<Color> colors) {
        return getCloserColorIndex(c, colors, 0);
    }

    private int getCloserColorIndex(Color c, ArrayList<Color> colors, int startColorIndex) {
        int index = 0;
        int minDist = Integer.MAX_VALUE;
        for (int i = startColorIndex; i < colors.size(); i++) {
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

    private Color getDsColor(Color c) {
        int r = (c.getRed() / 8) * 8;
        int g = (c.getGreen() / 8) * 8;
        int b = (c.getBlue() / 8) * 8;
        int a = (c.getAlpha() / 8) * 8;
        if (a < 20) {//New code
            r = 0;
            g = 0;
            b = 0;
        }
        return new Color(r, g, b, a);
    }

    private FastColor getDsFastColor(Color c) {
        int r = (c.getRed() / 8) * 8;
        int g = (c.getGreen() / 8) * 8;
        int b = (c.getBlue() / 8) * 8;
        int a = (c.getAlpha() / 8) * 8;
        if (a < 20) {//New code
            r = 0;
            g = 0;
            b = 0;
        }
        return new FastColor(r, g, b, a);
    }

    public int indexOfTextureName(String name) {
        for (int i = 0; i < textures.size(); i++) {
            if (textures.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfPaletteName(String name) {
        for (int i = 0; i < palettes.size(); i++) {
            if (palettes.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public boolean moveTextureUp(int index) {
        if (index > 0) {
            Collections.swap(textures, index, index - 1);
            return true;
        } else {
            return false;
        }
    }

    public boolean moveTextureDown(int index) {
        if (index < textures.size() - 1) {
            Collections.swap(textures, index, index + 1);
            return true;
        } else {
            return false;
        }
    }

    public boolean movePaletteUp(int index) {
        if (index > 0 && palettes.size() > 1) {
            Collections.swap(palettes, index, index - 1);
            return true;
        } else {
            return false;
        }
    }

    public boolean movePaletteDown(int index) {
        if (index < palettes.size() - 1 && palettes.size() > 1) {
            Collections.swap(palettes, index, index + 1);
            return true;
        } else {
            return false;
        }
    }

    public String getRepeatedTextureName() {
        for (int i = 0; i < textures.size() - 1; i++) {
            for (int j = i + 1; j < textures.size(); j++) {
                if (textures.get(i).getName().equals(textures.get(j).getName())) {
                    return textures.get(i).getName();
                }
            }
        }
        return null;
    }

    public String getRepeatedPaletteName() {
        for (int i = 0; i < palettes.size() - 1; i++) {
            for (int j = i + 1; j < palettes.size(); j++) {
                if (palettes.get(i).getName().equals(palettes.get(j).getName())) {
                    return palettes.get(i).getName();
                }
            }
        }
        return null;
    }

    public NsbtxTexture getTexture(int index) {
        return textures.get(index);
    }

    public NsbtxPalette getPalette(int index) {
        return palettes.get(index);
    }

    public ArrayList<NsbtxTexture> getTextures() {
        return textures;
    }

    public ArrayList<NsbtxPalette> getPalettes() {
        return palettes;
    }

    public void removeAllTextures() {
        this.textures = new ArrayList<>();
    }

    public void removeAllPalettes() {
        this.palettes = new ArrayList<>();
    }

    public boolean hasTextures() {
        return textures.size() > 0;
    }

    public boolean hasPalettes() {
        return palettes.size() > 0;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<String> getTextureNames() {
        ArrayList<String> texNames = new ArrayList<>(textures.size());
        for (NsbtxTexture tex : textures) {
            texNames.add(tex.getName());
        }
        return texNames;
    }

    public ArrayList<String> getPaletteNames() {
        ArrayList<String> palNames = new ArrayList<>(palettes.size());
        for (NsbtxPalette pal : palettes) {
            palNames.add(pal.getName());
        }
        return palNames;
    }

    public int removeTexture(String texName) {
        for (NsbtxTexture tex : textures) {
            if (tex.getName().equals(texName)) {
                if (textures.remove(tex)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public int removePalette(String palName) {
        for (NsbtxPalette pal : palettes) {
            if (pal.getName().equals(palName)) {
                if (palettes.remove(pal)) {
                    return 1;
                }
            }
        }
        return 0;
    }

}
