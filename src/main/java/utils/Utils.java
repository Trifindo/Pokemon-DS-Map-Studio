
package utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tileset.Face;

/**
 * @author Trifindo
 */
public class Utils {

    public static String[] readShaderAsResource(String filename) {
        Vector lines = new Vector();
        Scanner sc;
        sc = new Scanner(Utils.class.getClassLoader().getResourceAsStream(filename));

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
            img = ImageIO.read(Utils.class.getResource(path));
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
        BufferedImage img = ImageIO.read(Utils.class.getResource(path));
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

    public static float imageDifferenceNorm(BufferedImage img1, BufferedImage img2) {
        float totalDiff = 0.0f;
        try {
            for (int i = 0; i < img1.getWidth(); i++) {
                for (int j = 0; j < img1.getHeight(); j++) {
                    int c1 = img1.getRGB(i, j);
                    int c2 = img2.getRGB(i, j);

                    totalDiff += Math.abs((((c1 & 0x00FF0000) >> 16) - ((c2 & 0x00FF0000) >> 16)));
                    totalDiff += Math.abs((((c1 & 0x0000FF00) >> 8) - ((c2 & 0x0000FF00) >> 8)));
                    totalDiff += Math.abs(((c1 & 0x000000FF) - (c2 & 0x000000FF)));
                }
            }
            return totalDiff / (img1.getWidth() * img1.getHeight() * 255 * 3);
        } catch (Exception ex) {
            return 1.0f;
        }
    }

    public static BufferedImage addBackgroundColor(Color color, BufferedImage img) {
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        Graphics g = newImg.getGraphics();

        g.setColor(color);
        g.fillRect(0, 0, newImg.getWidth(), newImg.getHeight());
        g.drawImage(img, 0, 0, null);

        return newImg;
    }

    public static BufferedImage[] imageToImageArray(BufferedImage img, int cols, int rows) {
        int width = img.getWidth() / cols;
        int height = img.getHeight() / rows;
        BufferedImage[] array = new BufferedImage[cols * rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                array[i * rows + (cols - j - 1)] = img.getSubimage(i * width, j * height, width, height);
            }
        }
        return array;
    }

    public static BufferedImage getImageFromClipboard() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                Image img = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                return toBufferedImage(img);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
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

    public static String removeMapCoordsFromName(String name) {
        try {
            if (nameHasMapCoords(name)) {
                String[] splitString = name.split("_");
                String newName = "";
                for (int i = 0; i < splitString.length - 3; i++) {
                    newName += splitString[i] + "_";
                }
                newName += splitString[splitString.length - 3];
                return newName;
            } else {
                return name;
            }
        } catch (Exception ex) {
            return name;
        }
    }

    private static boolean nameHasMapCoords(String fileName) {
        String name = Utils.removeExtensionFromPath(fileName);
        try {
            String[] splitName = name.split("_");
            return canParseInteger(splitName[splitName.length - 1])
                    && canParseInteger(splitName[splitName.length - 2]);
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean canParseInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
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

    public static boolean containsArray(byte[] bigArray, byte[] smallArray, int offset) {
        for (int i = 0; i < smallArray.length; i++) {
            if (bigArray[offset + i] != smallArray[i]) {
                return false;
            }
        }
        return true;
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

    public static byte[] toArray(List<Byte> list){
        byte[] data = new byte[list.size()];
        for(int i = 0; i < list.size(); i++){
            data[i] = list.get(i);
        }
        return data;
    }

    public static Cursor loadCursor(String path) {
        return loadCursor(path, new Point(0, 0));
    }

    public static Cursor loadCursor(String path, Point hotSpot) {
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

    public static void addListenerToJTextFieldColor(JTextField jtf, MutableBoolean enabled, Color backgroundColor, Color foregroundColor) {
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
                    jtf.setBackground(backgroundColor);
                    jtf.setForeground(foregroundColor);
                }
            }
        });
    }

    public static class MutableBoolean {

        public boolean value;

        public MutableBoolean(boolean value) {
            this.value = value;
        }
    }

    ;

    public static class MutableInt {

        public int value;

        public MutableInt(int value) {
            this.value = value;
        }
    }

    ;

    public static class MutableLong {

        public long value;

        public MutableLong(long value) {
            this.value = value;
        }
    }

    ;

}
