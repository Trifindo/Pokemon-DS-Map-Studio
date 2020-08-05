/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.imd;

import utils.image.FastColor;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import tileset.TilesetMaterial;
import utils.Utils;
import utils.image.Clusterer;

/**
 *
 * @author Trifindo
 */
public class ImdPaletteArray {

    private static final int[] numColorsTable = new int[]{4, 16, 256, 32, 8};

    public ArrayList<ArrayList<Color>> palettes = new ArrayList<>();
    public ArrayList<String> paletteNames = new ArrayList<>();
    public ArrayList<Integer> paletteSizes = new ArrayList<>();

    public ImdPaletteArray(ArrayList<TilesetMaterial> materials) {
        ArrayList<TreeSet<FastColor>> paletteSets = new ArrayList<>();
        for (int i = 0; i < materials.size(); i++) {
            TilesetMaterial m = materials.get(i);
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
            ArrayList<Color> palette = new ArrayList<>();
            palette.addAll(paletteSets.get(i));
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
        ArrayList<String> textureNames = getTextureNames(materials);
        for (int i = 0; i < textureNames.size(); i++) {
            ArrayList<Integer> indices = getMaterialIndicesUsingTexture(materials, textureNames.get(i));
            if (indices.size() > 1) {
                String refPalName = materials.get(indices.get(0)).getPaletteNameImd();
                System.out.println("Reference palette: " + refPalName);
                int refPalIndex = paletteNames.indexOf(refPalName);
                if (refPalIndex != -1) {
                    ArrayList<Color> refPal = palettes.get(refPalIndex);
                    for(int j = 1; j < indices.size(); j++){
                        String fixPalName = materials.get(indices.get(j)).getPaletteNameImd();
                        int fixPalIndex = paletteNames.indexOf(fixPalName);
                        if (fixPalIndex != -1) {
                            ArrayList<Color> fixPal = palettes.get(fixPalIndex);
                            TilesetMaterial refMat = materials.get(indices.get(0));
                            TilesetMaterial fixMat = materials.get(indices.get(j));
                            ArrayList<Color> newPalette = fixColorOrder(refMat, fixMat, refPal, fixPal);
                            palettes.set(fixPalIndex, newPalette);
                        }
                    }
                }
            }
        }
        

    }

    private ArrayList<Color> fixColorOrder(TilesetMaterial refMat,
            TilesetMaterial fixMat, ArrayList<Color> refPal, ArrayList<Color> fixPal) {
        int numColors = Math.max(refPal.size(), fixPal.size());
        ArrayList<Integer> colorLookup = new ArrayList<>(numColors);
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
        
        ArrayList<Color> newPalette = new ArrayList<>(fixPal.size());
        for(int i = 0; i < fixPal.size(); i++){
            System.out.print(colorLookup.get(i) + " ");
            newPalette.add(fixPal.get(colorLookup.get(i)));
        }
        System.out.println();
        return newPalette;
    }

    private ArrayList<String> getTextureNames(ArrayList<TilesetMaterial> materials) {
        ArrayList<String> textureNames = new ArrayList<>();
        for (int i = 0; i < materials.size(); i++) {
            if (!textureNames.contains(materials.get(i).getTextureNameImd())) {
                textureNames.add(materials.get(i).getTextureNameImd());
            }
        }
        return textureNames;
    }

    private ArrayList<Integer> getMaterialIndicesUsingTexture(
            ArrayList<TilesetMaterial> materials, String textureName) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).getTextureNameImd().equals(textureName)) {
                indices.add(i);
            }
        }
        return indices;
    }

    public ArrayList<Color> getPalette(String palName) {
        int index = paletteNames.indexOf(palName);
        if (index == -1) {
            index = 0;
        }
        return palettes.get(index);
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
                FastColor color = getDsColor(new Color(img.getRGB(i, j), true));
                colors.add(color);
            }
        }
        return colors;
    }

    private FastColor getDsColor(Color c) {
        int r = (c.getRed() / 8) * 8;
        int g = (c.getGreen() / 8) * 8;
        int b = (c.getBlue() / 8) * 8;
        int a = (c.getAlpha() / 8) * 8;
        if(a < 20){//New code
            r = 0;
            g = 0;
            b = 0;
        }
        return new FastColor(r, g, b, a);
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

}
