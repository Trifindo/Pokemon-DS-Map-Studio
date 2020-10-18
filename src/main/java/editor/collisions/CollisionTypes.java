
package editor.collisions;

import editor.MainFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Trifindo
 */
public class CollisionTypes {

    public static final int numCollisions = 256;
    private static final int tileSize = 16;
    private int numLayers;
    private Color[][] fillColors;
    private Color[][] fontColors;
    private BufferedImage[][] collisionImgs;
    private String[][] collisionNames;

    public static final String[] collisionTypesFilesPerGame = new String[]{
            "colors/CollisionsColorsDP.txt",
            "colors/CollisionsColorsDP.txt",
            "colors/CollisionsColorsDP.txt",
            "colors/CollisionsColorsHGSS.txt",
            "colors/CollisionsColorsHGSS.txt",
            "colors/CollisionsColorsBW.txt",
            "colors/CollisionsColorsBW.txt",
            "colors/CollisionsColorsBW.txt",
            "colors/CollisionsColorsBW.txt"
    };

    public static final int[] numLayersPerGame = new int[]{
            2, 2, 2, 2, 2, 8, 8, 8, 8
    };

    public CollisionTypes(int gameIndex) {
        this.numLayers = numLayersPerGame[gameIndex];
        fillColors = loadColorsAsResource(collisionTypesFilesPerGame[gameIndex]);
        fontColors = loadFontColors(fillColors);
        collisionImgs = drawImages(fillColors, fontColors);
    }

    public CollisionTypes(String path, int numLayers) {
        this.numLayers = numLayers;
        fillColors = loadColorsAsResource(path);//"colors/CollisionsColors.txt"
        fontColors = loadFontColors(fillColors);
        collisionImgs = drawImages(fillColors, fontColors);

    }

    public String getCollisionName(int layer, int index) {
        return collisionNames[layer][index];
    }

    private Color[][] loadColorsAsResource(String path) {
        Color[][] colors = new Color[numLayers][numCollisions];
        collisionNames = new String[numLayers][numCollisions];

        int collIndex = 0;
        int layerIndex = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    CollisionTypes.class.getClassLoader().getResourceAsStream(path)));

            String line;
            while ((line = br.readLine()) != null && collIndex < numCollisions && layerIndex < numLayers) {
                String[] words = line.split(" ");
                if (words != null && words.length > 2) {
                    colors[layerIndex][collIndex] = parseColor(words[1]);
                    String name = words[2];
                    for (int i = 3; i < words.length; i++) {
                        name += " " + words[i];
                    }
                    collisionNames[layerIndex][collIndex] = name;
                    collIndex++;
                    if (collIndex >= numCollisions) {
                        collIndex = 0;
                        layerIndex++;
                    }
                }
            }

            br.close();
            //input.close();
        } catch (IOException ex) {
            System.out.println("Error loading colors!");
        }

        return colors;

    }

    private BufferedImage[][] drawImages(Color[][] fillColors, Color[][] fontColors) {
        BufferedImage[][] imgs = new BufferedImage[numLayers][numCollisions];

        for (int i = 0; i < numLayers; i++) {
            for (int j = 0; j < numCollisions; j++) {
                BufferedImage img = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
                imgs[i][j] = img;
                Graphics g = img.getGraphics();

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                g.setColor(fillColors[i][j]);
                g.fillRect(0, 0, tileSize, tileSize);

                g.setColor(fontColors[i][j]);
                g.setFont(new Font("TAHOMA", Font.PLAIN, 11));
                String value = String.format("%02X", j);
                g2d.drawString(value, 3, tileSize - 3);
            }
        }

        return imgs;
    }

    private static Color parseColor(String colorString) {
        try {
            if (colorString.startsWith("#")) {
                colorString = colorString.substring(1, colorString.length());
            }
            byte[] rgb = hexStringToByteArray(colorString);

            Color color = new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF, 255);
            return color;
        } catch (IndexOutOfBoundsException ex) {
            return new Color(255, 255, 255, 100);
        }

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public Color[][] loadFontColors(Color[][] fillColors) {
        Color[][] colors = new Color[numLayers][numCollisions];

        for (int i = 0; i < numLayers; i++) {
            for (int j = 0; j < numCollisions; j++) {
                colors[i][j] = getContrastColor(fillColors[i][j]);
            }
        }
        return colors;
    }

    public static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    public BufferedImage getImage(int layerIndex, int imgIndex) {
        return collisionImgs[layerIndex][imgIndex];
    }

    public Color getFillColor(int layerIndex, int collIndex) {
        return this.fillColors[layerIndex][collIndex];
    }

    public Color getFontColor(int layerIndex, int collIndex) {
        return this.fontColors[layerIndex][collIndex];
    }
}
