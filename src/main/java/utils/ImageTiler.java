
package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import tileset.Tileset;

/**
 * @author Trifindo
 */
public class ImageTiler {

    public static int[][] imageToTileLayer(BufferedImage img, Tileset tset, int cols, int rows, int tileSize) {
        img = Utils.resize(img, cols * tileSize, rows * tileSize, Image.SCALE_FAST);
        BufferedImage[] imgTiles = Utils.imageToImageArray(img, cols, rows);
        List<Integer> indices = new ArrayList<>(cols * rows);

        for (BufferedImage tileImg : imgTiles) {
            List<Float> diffs = tset.getTiles().stream()
                    .map(tile -> Utils.imageDifferenceNorm(tile.getThumbnail(), tileImg))
                    .collect(Collectors.toList());
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
