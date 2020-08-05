/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import editor.MainFrame;
import editor.handler.MapGrid;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tileset.Face;
import tileset.Tile;

/**
 *
 * @author Trifindo
 */
public class Utils {

    public static String[] readShaderAsResource(String filename) {
        Vector lines = new Vector();
        Scanner sc;
        sc = new Scanner(MainFrame.class.getClassLoader().getResourceAsStream(filename));

        while (sc.hasNext()) {
            lines.addElement(sc.nextLine());
        }

        String[] program = new String[lines.size()];

        for (int i = 0; i < lines.size(); ++i) {
            program[i] = (String) lines.elementAt(i) + "\n";
        }

        sc.close();
        return program;
    }

    public static BufferedImage loadTexImageAsResource(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(MainFrame.class.getResource(path));
        } catch (IOException | IllegalArgumentException ex) {
            int size = 64;
            img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, size, size);
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, size / 2, size / 2);
            g.setColor(Color.CYAN);
            g.fillRect(size / 2, size / 2, size, size);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, size - 1, size - 1);
            System.out.println("IO Exception leyendo imagen como resource");
        }
        return img;
    }

    public static BufferedImage loadImageAsResource(String path) throws IOException, IllegalArgumentException {
        BufferedImage img = ImageIO.read(MainFrame.class.getResource(path));
        return img;
    }

    public static BufferedImage[] loadHorizontalImageArrayAsResource(String path,
            int numTiles) {
        BufferedImage img = loadTexImageAsResource(path);

        BufferedImage[] array = new BufferedImage[numTiles];
        int width = img.getWidth() / numTiles;
        int height = img.getHeight();
        for (int i = 0; i < numTiles; i++) {
            array[i] = img.getSubimage(i * width, 0, width, height);
        }

        return array;
    }

    public static BufferedImage[] loadVerticalImageArrayAsResource(String path,
            int numTiles) {
        BufferedImage img = loadTexImageAsResource(path);

        BufferedImage[] array = new BufferedImage[numTiles];
        int width = img.getWidth();
        int height = img.getHeight() / numTiles;
        for (int i = 0; i < numTiles; i++) {
            array[i] = img.getSubimage(0, i * height, width, height);
        }

        return array;
    }

    public static float[] floatListToArray(ArrayList<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i).floatValue();
        }
        return array;
    }

    public static int[] intListToArray(ArrayList<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i).intValue();
        }
        return array;
    }

    public static Face[] faceListToArray(ArrayList<Face> list) {
        Face[] array = new Face[list.size()];
        for (int i = 0; i < array.length; i++) {
            Face f = list.get(i);
            array[i] = f;
        }
        return array;
    }

    public static ArrayList<Integer> cloneArrayListInt(ArrayList<Integer> list) {
        ArrayList<Integer> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            newList.add(new Integer(list.get(i).intValue()));
        }
        return newList;
    }

    public static ArrayList<Float> cloneArrayListFloat(ArrayList<Float> list) {
        ArrayList<Float> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            newList.add(new Float(list.get(i).floatValue()));
        }
        return newList;
    }

    public static float[] cloneArray(float[] src) {
        float[] dst = new float[src.length];
        System.arraycopy(src, 0, dst, 0, dst.length);
        return dst;
    }

    public static void incrementAllElements(ArrayList<Integer> list, int increment) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i) + increment);
        }
    }

    public static String removeExtensionFromPath(String path) {
        return path.replaceFirst("[.][^.]+$", "");
    }

    public static String addExtensionToPath(String path, String extension) {
        if (!path.endsWith("." + extension)) {
            path = path.concat("." + extension);
        }
        return path;
    }

    public static String removeLastOcurrences(String string, char c) {
        int nCharsToRemove = 0;
        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.charAt(i) == c) {
                nCharsToRemove++;
            } else {
                break;
            }
        }
        return string.substring(0, string.length() - nCharsToRemove);
    }

    public static BufferedImage cloneImg(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static boolean hasTransparentColor(BufferedImage img) {
        if (img.getColorModel().hasAlpha()) {
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    if (new Color(img.getRGB(i, j), true).getAlpha() < 20) { //This value could be changed
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int[] cloneIntArray(int[] array) {
        int[] newArray = new int[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    public static void printArrayInFile(PrintWriter writer, float[] array, int cols) {
        for (int i = 0; i < array.length; i++) {
            if (i % cols == 0) {
                writer.println();
            }
            writer.print(array[i] + " ");
        }
        writer.println();
    }

    public static void printArrayInFile(PrintWriter writer, int[] array, int cols) {
        for (int i = 0; i < array.length; i++) {
            if (i % cols == 0) {
                writer.println();
            }
            writer.print(array[i] + " ");
        }
        writer.println();
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        return resize(img, newW, newH, Image.SCALE_SMOOTH);

    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH, int scalingType) {
        Image tmp = img.getScaledInstance(newW, newH, scalingType);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage generateTransparentBackground(int size, int tileSize) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        int numCells = size / tileSize;
        Color[] colors = new Color[]{Color.white, Color.lightGray};
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                g.setColor(colors[(i + j) % 2]);
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
        return img;

    }

    public static void floodFillMatrix(int screen[][], int x, int y, int newC) {
        int prevC = screen[x][y];
        if (newC == prevC) {
            return;
        }
        int M = screen.length;
        int N = screen[0].length;
        Utils.floodFillUtil(screen, x, y, prevC, newC, M, N);
    }

    private static void floodFillUtil(int screen[][], int x, int y, int prevC, int newC, int M, int N) {
        // Base cases 
        if (x < 0 || x >= M || y < 0 || y >= N) {
            return;
        }
        if (screen[x][y] != prevC) {
            return;
        }

        // Replace the color at (x, y) 
        screen[x][y] = newC;

        // Recur for north, east, south and west 
        floodFillUtil(screen, x + 1, y, prevC, newC, M, N);
        floodFillUtil(screen, x - 1, y, prevC, newC, M, N);
        floodFillUtil(screen, x, y + 1, prevC, newC, M, N);
        floodFillUtil(screen, x, y - 1, prevC, newC, M, N);
    }

    public static void floodFillMatrix(int screen[][], boolean[][] mask, int x, int y, int newC) {
        int prevC = screen[x][y];
        if (newC == prevC) {
            return;
        }
        int M = screen.length;
        int N = screen[0].length;
        Utils.floodFillUtil(screen, mask, x, y, prevC, newC, M, N);
    }

    private static void floodFillUtil(int screen[][], boolean[][] mask, int x, int y, int prevC, int newC, int M, int N) {
        // Base cases 
        if (x < 0 || x >= M || y < 0 || y >= N) {
            return;
        }
        if (screen[x][y] != prevC) {
            return;
        }

        if (!mask[x][y]) {
            return;
        }

        // Replace the tile at (x, y) 
        screen[x][y] = newC;

        // Recur for north, east, south and west 
        floodFillUtil(screen, mask, x + 1, y, prevC, newC, M, N);
        floodFillUtil(screen, mask, x - 1, y, prevC, newC, M, N);
        floodFillUtil(screen, mask, x, y + 1, prevC, newC, M, N);
        floodFillUtil(screen, mask, x, y - 1, prevC, newC, M, N);
    }

    public static void floodFillMatrix(int screen[][], boolean[][] mask, int x, int y, int newC, int tileWidth, int tileHeight) {
        int prevC = screen[x][y];
        if (newC == prevC) {
            return;
        }
        int M = screen.length;
        int N = screen[0].length;
        Utils.floodFillUtil(screen, mask, x, y, prevC, newC, M, N, tileWidth, tileHeight);
    }

    private static void floodFillUtil(int screen[][], boolean[][] mask, int x, int y, int prevC, int newC, int M, int N, int tileWidth, int tileHeight) {
        if (!isTileAreaAvailable(screen, mask, x, y, prevC, newC, M, N, tileWidth, tileHeight)) {
            return;
        }
        // Base cases 
        if (x < 0 || x >= M || y < 0 || y >= N) {
            return;
        }
        if (screen[x][y] != prevC) {
            return;
        }

        if (!mask[x][y]) {
            return;
        }

        //Clear tiles under current tile
        for (int i = 0; i < tileWidth; i++) {
            for (int j = 0; j < tileHeight; j++) {
                screen[x + i][y + j] = -1;
            }
        }

        // Replace the tile at (x, y) 
        screen[x][y] = newC;

        // Recur for north, east, south and west 
        floodFillUtil(screen, mask, x + tileWidth, y, prevC, newC, M, N, tileWidth, tileHeight);
        floodFillUtil(screen, mask, x - tileWidth, y, prevC, newC, M, N, tileWidth, tileHeight);
        floodFillUtil(screen, mask, x, y + tileHeight, prevC, newC, M, N, tileWidth, tileHeight);
        floodFillUtil(screen, mask, x, y - tileHeight, prevC, newC, M, N, tileWidth, tileHeight);
    }

    private static boolean isTileAreaAvailable(int screen[][], boolean[][] mask, int x, int y, int prevC, int newC, int M, int N, int tileWidth, int tileHeight) {
        try {
            for (int i = 0; i < tileWidth; i++) {
                for (int j = 0; j < tileHeight; j++) {
                    if (screen[x + i][y + j] != prevC || !mask[x + i][y + j]) {
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static void floodFillMatrix(byte screen[][], int x, int y, byte newC) {
        byte prevC = screen[x][y];
        if (newC == prevC) {
            return;
        }
        int M = screen.length;
        int N = screen[0].length;
        Utils.floodFillUtil(screen, x, y, prevC, newC, M, N);
    }

    private static void floodFillUtil(byte screen[][], int x, int y, byte prevC, byte newC, int M, int N) {
        // Base cases 
        if (x < 0 || x >= M || y < 0 || y >= N) {
            return;
        }
        if (screen[x][y] != prevC) {
            return;
        }

        // Replace the color at (x, y) 
        screen[x][y] = newC;

        // Recur for north, east, south and west 
        floodFillUtil(screen, x + 1, y, prevC, newC, M, N);
        floodFillUtil(screen, x - 1, y, prevC, newC, M, N);
        floodFillUtil(screen, x, y + 1, prevC, newC, M, N);
        floodFillUtil(screen, x, y - 1, prevC, newC, M, N);
    }

    public static Cursor loadCursor(String path) {
        Image img = Utils.loadTexImageAsResource(path);
        if (img != null) {
            return Toolkit.getDefaultToolkit().createCustomCursor(
                    img,
                    new Point(0, 0), "custom cursor");
        } else {
            return Cursor.getDefaultCursor();
        }
    }

    public static boolean hasDuplicates(ArrayList<Integer> array) {
        Set<Integer> set = new HashSet<Integer>();
        for (int i : array) {
            if (set.contains(i)) {
                return true;
            }
            set.add(i);
        }
        return false;
    }

    public static class IntTuple {

        public int e1, e2;

        public IntTuple(int e1, int e2) {
            this.e1 = e1;
            this.e2 = e2;
        }
    }

    public static void addListenerToJTextFieldColor(JTextField jtf,
            MutableBoolean enabled, Color updateColor) {
        jtf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeBackground();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeBackground();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeBackground();
            }

            public void changeBackground() {
                if (enabled.value) {
                    jtf.setBackground(updateColor);
                }
            }
        });
    }

    public static class MutableBoolean {

        public boolean value;

        public MutableBoolean(boolean value) {
            this.value = value;
        }
    };

    public static class MutableInt {

        public int value;

        public MutableInt(int value) {
            this.value = value;
        }
    };

    public static class MutableLong {

        public long value;

        public MutableLong(long value) {
            this.value = value;
        }
    };

}
