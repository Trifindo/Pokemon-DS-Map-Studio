
package formats.imd;

import utils.image.FastColor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import tileset.TilesetMaterial;
import utils.image.Clusterer;

/**
 * @author Trifindo
 */
public class ImdPaletteArray {

    private static final int[] numColorsTable = new int[]{4, 16, 256, 32, 8};

    public List<List<Color>> palettes = new ArrayList<>();
    public List<String> paletteNames = new ArrayList<>();
    public List<Integer> paletteSizes = new ArrayList<>();

    public List<Integer> textureIndices = new ArrayList<>();

    public ImdPaletteArray(List<TilesetMaterial> materials) {
        List<TreeSet<FastColor>> paletteSets = new ArrayList<>();
        for (TilesetMaterial m : materials) {
            int index = paletteNames.indexOf(m.getPaletteNameImd());
            if (index != -1) {
                paletteSets.set(index, addColorsFromTexture(paletteSets.get(index), m.getTextureImg()));
                int paletteSize = numColorsTable[m.getColorFormat()];
                if (paletteSize > paletteSizes.get(index)) {
                    paletteSizes.set(index, paletteSize);
                }
            } else {
                paletteNames.add(m.getPaletteNameImd());
                paletteSets.add(addColorsFromTexture(new TreeSet<>(), m.getTextureImg()));
                paletteSizes.add(numColorsTable[m.getColorFormat()]);
            }
        }

        //Reduce colors
        for (int i = 0; i < paletteSets.size(); i++) {
            List<Color> palette = new ArrayList<>(paletteSets.get(i));
            if (paletteSets.get(i).size() > paletteSizes.get(i)) {
                palette = Clusterer.clusterColors(palette, paletteSizes.get(i), 100, 0.00001f);
            } else if (paletteSets.get(i).size() < paletteSizes.get(i)) {
                int nColorsToAdd = paletteSizes.get(i) - paletteSets.get(i).size();
                for (int j = 0; j < nColorsToAdd; j++) {
                    palette.add(new Color(0, 0, 0, 255));
                }
            }
            palettes.add(palette);//fastColorArrayToColorArray(palette));

            //Move most transparent color to the front
            int lessAlphaColorIndex = getLessAlphaColorIndex(palette);
            if (palettes.get(i).get(lessAlphaColorIndex).getAlpha() < 20) {
                Collections.swap(palettes.get(i), 0, lessAlphaColorIndex);
            }
        }

        //Fix palettes that use the same texture
        List<String> textureNames = getTextureNames(materials);
        for (String textureName : textureNames) {
            List<Integer> indices = getMaterialIndicesUsingTexture(materials, textureName);
            if (indices.size() <= 1) {
                continue;
            }

            String refPalName = materials.get(indices.get(0)).getPaletteNameImd();
            System.out.println("Reference palette: " + refPalName);
            int refPalIndex = paletteNames.indexOf(refPalName);
            if (refPalIndex == -1) {
                continue;
            }

            List<Color> refPal = palettes.get(refPalIndex);
            for (int j = 1; j < indices.size(); j++) {
                String fixPalName = materials.get(indices.get(j)).getPaletteNameImd();
                int fixPalIndex = paletteNames.indexOf(fixPalName);
                if (fixPalIndex != -1) {
                    List<Color> fixPal = palettes.get(fixPalIndex);
                    TilesetMaterial refMat = materials.get(indices.get(0));
                    TilesetMaterial fixMat = materials.get(indices.get(j));
                    List<Color> newPalette = fixColorOrder(refMat, fixMat, refPal, fixPal);
                    palettes.set(fixPalIndex, newPalette);
                }
            }
        }
    }

    private int getBestTextureIndex(List<Integer> matIndices){
        for (int matIndex : matIndices) {
            //materials.
        }
        //materials.
        return 0;
    }

    private List<Color> fixColorOrder(TilesetMaterial refMat, TilesetMaterial fixMat, List<Color> refPal, List<Color> fixPal) {
        int numColors = Math.max(refPal.size(), fixPal.size());
        List<Integer> colorLookup = new ArrayList<>(numColors);
        for (int i = 0; i < numColors; i++) {
            colorLookup.add(i);
        }

        BufferedImage refImg = refMat.getTextureImg();
        BufferedImage fixImg = fixMat.getTextureImg();
        if (refImg.getWidth() == fixImg.getWidth() && refImg.getHeight() == fixImg.getHeight()) {
            System.out.println("Fixing colors: " + fixMat.getPaletteNameImd());
            for (int j = 0; j < refImg.getHeight(); j++) {
                for (int i = 0; i < refImg.getWidth(); i++) {
                    int refColorIndex = getCloserColorIndex(getDsColor(new Color(refImg.getRGB(i, j), true)), refPal);
                    int fixColorIndex = getCloserColorIndex(getDsColor(new Color(fixImg.getRGB(i, j), true)), fixPal);
                    colorLookup.set(refColorIndex, fixColorIndex);
                }
            }
        }

        return IntStream.range(0, fixPal.size())
                .mapToObj(i -> fixPal.get(colorLookup.get(i)))
                .collect(Collectors.toList());
    }

    private List<String> getTextureNames(List<TilesetMaterial> materials) {
        return materials.stream()
                .map(TilesetMaterial::getTextureNameImd)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Integer> getMaterialIndicesUsingTexture(List<TilesetMaterial> materials, String textureName) {
        return IntStream.range(0, materials.size())
                .filter(i -> materials.get(i).getTextureNameImd().equals(textureName))
                .boxed()
                .collect(Collectors.toList());
    }

    public List<Color> getPalette(String palName) {
        int index = paletteNames.indexOf(palName);
        if (index == -1) {
            index = 0;
        }
        return palettes.get(index);
    }

    private int getLessAlphaColorIndex(List<Color> colors) {
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
    /*
    private ArrayList<Color> fastColorArrayToColorArray(ArrayList<Color> colors) {
        ArrayList<Color> newColors = new ArrayList<>(colors.size());
        for (Color c : colors) {
            //newColors.add(c.getColor());
            newColors.add(new Color(c.getRGB(), true));
        }
        return newColors;
    }*/

    private TreeSet<FastColor> addColorsFromTexture(TreeSet<FastColor> colors, BufferedImage img) {
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                colors.add(getDsColor(new Color(img.getRGB(i, j), true)));
            }
        }
        return colors;
    }

    private FastColor getDsColor(Color c) {
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

    private int getCloserColorIndex(Color c, List<Color> colors) {
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
}
