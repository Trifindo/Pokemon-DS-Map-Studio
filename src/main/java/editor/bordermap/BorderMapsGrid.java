
package editor.bordermap;

import java.util.Arrays;

/**
 * @author Trifindo
 */
public class BorderMapsGrid {
    public static final int cols = 3;
    public static final int rows = 3;

    public int[][] grid;
    public int[][] heights;

    public BorderMapsGrid() {
        grid = new int[cols][rows];
        heights = new int[cols][rows];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = -1;
                heights[i][j] = 0;
            }
        }
        grid[1][1] = -1;
    }

    public boolean isTileInGrid(int tileIndex) {
        for (int[] ints : grid) {
            if (Arrays.stream(ints).anyMatch(i -> i == tileIndex)) {
                return true;
            }
        }
        return false;
    }

    public void decreaseFromIndex(int tileIndex) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > tileIndex) {
                    grid[i][j]--;
                }
            }
        }
    }
}
