
package editor.collisions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class Collisions {

    public static final String fileExtension = "per";
    public static final int cols = 32, rows = 32;
    public int numLayers; //TODO: make this relative to the game used
    private byte[][][] layers;
    private ArrayList<Byte> lastBytes;

    public Collisions(int gameIndex) {
        this.numLayers = CollisionTypes.numLayersPerGame[gameIndex];
        layers = new byte[numLayers][cols][rows];

        lastBytes = new ArrayList<>();
    }
    /*
    public Collisions() {
        layers = new byte[numLayers][cols][rows];
    }*/

    public Collisions(String path) throws IOException {
        File file = new File(path);
        byte[] data = Files.readAllBytes(file.toPath());

        numLayers = data.length / (cols * rows);
        int offset = 0;
        if (numLayers > 2) {
            offset = 4;
        }
        layers = new byte[numLayers][cols][rows];
        for (int i = 0; i < numLayers; i++) {
            for (int j = 0, c = 0; j < rows; j++) {
                for (int k = 0; k < cols; k++, c += numLayers) {
                    layers[i][k][j] = data[c + i + offset];
                }
            }
        }

        lastBytes = new ArrayList<>();
        for (int i = numLayers * (cols * rows) + offset; i < data.length; i++) {
            lastBytes.add(data[i]);
        }
    }

    public void saveToFile(String path) throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        byte[] data = new byte[cols * rows * numLayers];

        if (numLayers > 2) {
            out.write(new byte[]{0x20, 0x00, 0x20, 0x00});
        }

        for (int i = 0; i < numLayers; i++) {
            for (int j = 0, c = 0; j < rows; j++) {
                for (int k = 0; k < cols; k++, c += numLayers) {
                    data[c + i] = layers[i][k][j];
                }
            }
        }
        out.write(data);

        for (int i = 0; i < lastBytes.size(); i++) {
            out.write(lastBytes.get(i));
        }
        out.close();
    }

    public int getValue(int layer, int x, int y) {
        return layers[layer][x][y] & 0xFF;
    }

    public void setValue(int value, int layer, int x, int y) {
        layers[layer][x][y] = (byte) value;
    }

    public byte[][] getLayer(int index) {
        return layers[index];
    }

    public int getNumLayers() {
        return numLayers;
    }

    public byte[][] cloneLayer(int index) {
        return cloneLayer(layers[index]);
    }

    private byte[][] cloneLayer(byte[][] layer) {
        byte[][] copy = new byte[cols][rows];
        for (int i = 0; i < layer.length; i++) {
            System.arraycopy(layer[i], 0, copy[i], 0, layer[i].length);
        }
        return copy;
    }

    public void setLayer(int layerIndex, byte[][] layers) {
        this.layers[layerIndex] = layers;
    }


}
