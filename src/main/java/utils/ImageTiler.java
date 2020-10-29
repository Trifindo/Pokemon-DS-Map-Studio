
package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.imageio.ImageIO;

import tileset.Tile;
import tileset.Tileset;
import tileset.TilesetMaterial;

/**
 * @author Trifindo
 */
public class ImageTiler {

    public static int[][] imageToTileLayer(BufferedImage img, Tileset tset, int cols, int rows, int tileSize) {
        img = Utils.resize(img, cols * tileSize, rows * tileSize, Image.SCALE_FAST);
        List<BufferedImage> imgTiles = Arrays.asList(Utils.imageToImageArray(img, cols, rows));
        ArrayList<Integer> indices = new ArrayList<>(cols * rows);

        for (BufferedImage tileImg : imgTiles) {
            ArrayList<Float> diffs = new ArrayList<>(tset.size());
            for (Tile tile : tset.getTiles()) {
                diffs.add(Utils.imageDifferenceNorm(tile.getThumbnail(), tileImg));
            }
            indices.add(diffs.indexOf(Collections.min(diffs)));
        }

        int[][] tileGrid = new int[cols][rows];
        for (int i = 0, c = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++, c++) {
                tileGrid[i][j] = indices.get(c);
            }
        }
        return tileGrid;
    }

}
