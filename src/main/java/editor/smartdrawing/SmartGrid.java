
package editor.smartdrawing;

import editor.handler.MapEditorHandler;
import editor.grid.MapGrid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import tileset.Tile;

/**
 * @author Trifindo
 */
public class SmartGrid {

    public static final int width = 5;
    public static final int height = 3;
    public int[][] sgrid = new int[width][height];

    private static List<SmartUnit> smartUnits = List.of(
            new SmartUnit(true, false, false, true, false, false, false, false),
            new SmartUnit(true, false, true, true, false, false, false, false),
            new SmartUnit(true, false, true, false, false, false, false, false),
            new SmartUnit(true, true, true, true, true, false, true, true),
            new SmartUnit(true, true, true, true, false, true, true, true),

            new SmartUnit(true, true, false, true, false, false, false, false),
            new SmartUnit(true, true, true, true, true, true, true, true),
            new SmartUnit(true, true, true, false, false, false, false, false),
            new SmartUnit(true, true, true, true, true, true, true, false),
            new SmartUnit(true, true, true, true, true, true, false, true),

            new SmartUnit(false, true, false, true, false, false, false, false),
            new SmartUnit(false, true, true, true, false, false, false, false),
            new SmartUnit(false, true, true, false, false, false, false, false)
        );

    private static List<SmartUnit> invertedSmartUnits = List.of(
            new SmartUnit(true, true, true, true, true, false, true, true), //3
            new SmartUnit(false, true, true, true, false, false, false, false), //12
            new SmartUnit(true, true, true, true, false, true, true, true), //4
            new SmartUnit(true, false, false, true, false, false, false, false), //0
            new SmartUnit(true, false, true, false, false, false, false, false), //2

            new SmartUnit(true, true, true, false, false, false, false, false), //7
            new SmartUnit(true, true, true, true, true, true, true, true), //6
            new SmartUnit(true, true, false, true, false, false, false, false), //5
            new SmartUnit(false, true, false, true, false, false, false, false), //11
            new SmartUnit(false, true, true, false, false, false, false, false), //13

            new SmartUnit(true, true, true, true, true, true, true, false), //8
            new SmartUnit(true, false, true, true, false, false, false, false), //1
            new SmartUnit(true, true, true, true, true, true, false, true) //9
        );

    public SmartGrid(int[][] data) {
        this.sgrid = data;
    }

    public SmartGrid() {
        for (int i = 0; i < width; i++) {
            Arrays.fill(sgrid[i], -1);
        }

        /*
        smartUnits.add(new SmartUnit(true, false, false, true, false, false, false, false));
        smartUnits.add(new SmartUnit(true, false, true, true, false, false, false, false));
        smartUnits.add(new SmartUnit(true, false, true, false, false, false, false, false));
        smartUnits.add(new SmartUnit(true, true, true, true, true, false, true, true));
        smartUnits.add(new SmartUnit(true, true, true, true, false, true, true, true));

        smartUnits.add(new SmartUnit(true, true, false, true, false, false, false, false));
        smartUnits.add(new SmartUnit(true, true, true, true, true, true, true, true));
        smartUnits.add(new SmartUnit(true, true, true, false, false, false, false, false));
        smartUnits.add(new SmartUnit(true, true, true, true, true, true, true, false));
        smartUnits.add(new SmartUnit(true, true, true, true, true, true, false, true));

        smartUnits.add(new SmartUnit(false, true, false, true, false, false, false, false));
        smartUnits.add(new SmartUnit(false, true, true, true, false, false, false, false));
        smartUnits.add(new SmartUnit(false, true, true, false, false, false, false, false));
         */
    }

    public void replaceTilesUsingIndices(int[] indices) {
        int[][] oldGrid = cloneGrid();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int index = oldGrid[i][j];
                sgrid[i][j] = index == -1 ? -1 : indices[index];
            }
        }
    }

    public int[][] cloneGrid() {
        int[][] copy = new int[width][height];
        for (int i = 0; i < width; i++) {
            System.arraycopy(sgrid[i], 0, copy[i], 0, height);
        }
        return copy;
    }

    public void useSmartFill(MapEditorHandler handler, int x, int y, boolean invert) {
        int prevC = handler.getActiveTileLayer()[x][y];
        boolean[][] gridToEdit = new boolean[MapGrid.cols][MapGrid.rows];
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                gridToEdit[i][j] = handler.getActiveTileLayer()[i][j] == prevC;
            }
        }

        boolean[][] remainingGrid = new boolean[MapGrid.cols][MapGrid.rows];
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                remainingGrid[i][j] = handler.getActiveTileLayer()[i][j] == prevC;
            }
        }

        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                int tileIndex = handler.getActiveTileLayer()[i][j];
                if (tileIndex != -1 && tileIndex != prevC) {
                    Tile tile = handler.getTileset().get(handler.getActiveTileLayer()[i][j]);
                    int xSize = tile.getWidth() - Math.max(0, i + tile.getWidth() - MapGrid.cols);
                    int ySize = tile.getHeight() - Math.max(0, j + tile.getHeight() - MapGrid.rows);
                    for (int m = 0; m < xSize; m++) {
                        for (int n = 0; n < ySize; n++) {
                            remainingGrid[i + m][j + n] = false;
                            gridToEdit[i + m][j + n] = false;
                        }
                    }
                }
            }
        }

        int W = MapGrid.cols;
        int H = MapGrid.rows;
        if (invert) {
            floodFillUtil(handler.getActiveTileLayer(), gridToEdit, remainingGrid, x, y, prevC, W, H, invertedSmartUnits);
        } else {
            floodFillUtil(handler.getActiveTileLayer(), gridToEdit, remainingGrid, x, y, prevC, W, H, smartUnits);
        }
    }

    private void floodFillUtil(int[][] screen, boolean[][] gridToEdit, boolean[][] remainingGrid,
            int x, int y, int prevC, int W, int H, List<SmartUnit> units) {
        // Base cases 
        if (x < 0 || x >= W || y < 0 || y >= H) {
            return;
        }

        if (!gridToEdit[x][y] || !remainingGrid[x][y]) {
            return;
        }

        SmartUnit unit = generateSmartUnit(gridToEdit, x, y, W, H);
        int index;
        if (unit.fullCrossNeighbours()) {
            index = indexOfSameCorner(units, unit);
        } else {
            index = indexOfSameCross(units, unit);
        }
        if (index == -1) {
            index = 6;
        }

        screen[x][y] = sgrid[index % width][index / width];
        remainingGrid[x][y] = false;

        // Recur for north, east, south and west 
        floodFillUtil(screen, gridToEdit, remainingGrid, x + 1, y, prevC, W, H, units);
        floodFillUtil(screen, gridToEdit, remainingGrid, x - 1, y, prevC, W, H, units);
        floodFillUtil(screen, gridToEdit, remainingGrid, x, y + 1, prevC, W, H, units);
        floodFillUtil(screen, gridToEdit, remainingGrid, x, y - 1, prevC, W, H, units);
    }

    private SmartUnit generateSmartUnit(boolean[][] screen, int x, int y, int M, int N) {
        boolean[] corners = new boolean[4];
        boolean[] cross = new boolean[4];
        cross[0] = hasSameNeighbor(screen, x, y - 1, M, N);
        cross[1] = hasSameNeighbor(screen, x, y + 1, M, N);
        cross[2] = hasSameNeighbor(screen, x - 1, y, M, N);
        cross[3] = hasSameNeighbor(screen, x + 1, y, M, N);

        corners[0] = hasSameNeighbor(screen, x - 1, y - 1, M, N);
        corners[1] = hasSameNeighbor(screen, x + 1, y - 1, M, N);
        corners[2] = hasSameNeighbor(screen, x - 1, y + 1, M, N);
        corners[3] = hasSameNeighbor(screen, x + 1, y + 1, M, N);

        return new SmartUnit(cross, corners);
    }

    private boolean hasSameNeighbor(boolean[][] screen, int x, int y, int M, int N) {
        if (x < 0 || x >= M || y < 0 || y >= N) {
            return true; //false
        } else {
            return screen[x][y];
        }
    }

    private int indexOfSameCross(List<SmartUnit> units, SmartUnit unit) {
        return IntStream.range(0, units.size())
                .filter(i -> units.get(i).sameCross(unit))
                .findFirst().orElse(-1);
    }

    private int indexOfSameCorner(List<SmartUnit> units, SmartUnit unit) {
        return IntStream.range(0, units.size())
                .filter(i -> units.get(i).sameCorners(unit))
                .findFirst().orElse(-1);
    }

    private static class SmartUnit {
        public boolean[] cross;
        public boolean[] corners;

        public SmartUnit(boolean t, boolean b, boolean l, boolean r, boolean tl, boolean tr, boolean bl, boolean br) {
            cross = new boolean[4];
            corners = new boolean[4];
            cross[0] = t;
            cross[1] = b;
            cross[2] = l;
            cross[3] = r;
            corners[0] = tl;
            corners[1] = tr;
            corners[2] = bl;
            corners[3] = br;
        }

        public SmartUnit(boolean[] cross, boolean[] corners) {
            this.cross = cross;
            this.corners = corners;
        }

        public boolean fullCrossNeighbours() {
            return cross[0] && cross[1] && cross[2] && cross[3];
        }

        public boolean sameCross(SmartUnit unit) {
            return IntStream.range(0, cross.length).noneMatch(i -> this.cross[i] != unit.cross[i]);
        }

        public boolean sameCorners(SmartUnit unit) {
            return IntStream.range(0, corners.length).noneMatch(i -> this.corners[i] != unit.corners[i]);
        }

        public void printUnit() {
            System.out.println(corners[0] + " " + cross[0] + " " + corners[1]);
            System.out.println(cross[2] + " " + "    " + " " + cross[3]);
            System.out.println(corners[2] + " " + cross[1] + " " + corners[3]);
        }
    }
}
