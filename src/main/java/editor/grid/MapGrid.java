
package editor.grid;

import editor.handler.MapEditorHandler;
import formats.obj.ObjWriter;

import java.io.*;
import java.util.HashSet;

import tileset.Tile;
import tileset.Tileset;
import utils.Utils;

/**
 * @author Trifindo
 */
public class MapGrid {

    private MapEditorHandler handler;

    //public String filePath = "";
    //public String tilesetFilePath = "";
    //public static final String fileExtension = "pdsmap";
//    private static final String mapMatrixTag = "mapmatrix";
//    private static final String gameIndexTag = "gameindex";
//    private static final String tileGridTag = "tilegrid";
//    private static final String heightGridTag = "heightgrid";
//    private static final String tilesetTag = "tileset";
//    private static final String bdhcTag = "bdhc";
    public static final int cols = 32;
    public static final int rows = 32;
    public static final int tileSize = 16;
    public static final int width = cols * tileSize;
    public static final int height = rows * tileSize;
    public static final float gridTileSize = 1.0f;

    public static final int numLayers = 9;
    public int[][][] tileLayers = new int[numLayers][cols][rows];
    public int[][][] heightLayers = new int[numLayers][cols][rows];

    //private int[][] tileLayerCopy = null;
    //private int[][] heightLayerCopy = null;
    public MapLayerGL[] mapLayersGL = new MapLayerGL[numLayers];

    public MapGrid(MapEditorHandler handler) {
        this.handler = handler;

        //filePath = "";
        //tilesetFilePath = "";
        for (int k = 0; k < numLayers; k++) {
            //int[][] tileGrid = new int[cols][rows];
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    tileLayers[k][i][j] = -1;
                }
            }
        }

        //updateAllMapLayers(handler.useRealTimePostProcessing());
    }

    /*
    public void saveToFile(String path) throws FileNotFoundException {
        if (!path.endsWith("." + fileExtension)) {
            path = path.concat("." + fileExtension);
        }
        PrintWriter out = new PrintWriter(path);

        out.println(gameIndexTag);
        out.println(handler.getGameIndex());

        out.println(tilesetTag);
        String filename = Utils.removeExtensionFromPath(new File(path).getName());
        out.println(filename + "." + Tileset.fileExtension);

        for (int[][] tLayer : tileLayers) {
            out.println(tileGridTag);
            printMatrixInFile(out, tLayer); //Todo change this
        }

        for (int[][] hLayer : heightLayers) {
            out.println(heightGridTag);
            printMatrixInFile(out, hLayer); //Todo change this
        }

        out.close();
    }

    public void loadFromFile(String path) throws FileNotFoundException, IOException {
        InputStream input = new FileInputStream(new File(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        int numTileLayersRead = 0;
        int numHeightLayersRead = 0;

        if (br.readLine().startsWith(mapMatrixTag)) {

        } else {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(gameIndexTag)) {
                    handler.setGameIndex(Integer.valueOf(br.readLine()));
                } else if (line.startsWith(tilesetTag)) {
                    String folderPath = new File(path).getParent();
                    tilesetFilePath = folderPath + File.separator + br.readLine();
                    System.out.println("Tileset path: " + tilesetFilePath);
                } else if (line.startsWith(tileGridTag)) {
                    loadMatrixFromFile(br, tileLayers[numTileLayersRead]);
                    numTileLayersRead++;
                } else if (line.startsWith(heightGridTag)) {
                    loadMatrixFromFile(br, heightLayers[numHeightLayersRead]);
                    numHeightLayersRead++;
                }
            }
        }
        br.close();
        input.close();
    }*/
    public void saveMapToOBJ(Tileset tset, String path, boolean saveTextures,
                             boolean includeVertexColors, boolean useExportgroups, float tileUpscale) throws FileNotFoundException {
        MapGrid grid;

        if (useExportgroups) {
            //TO COMPLETE! EXPORT ONE MAP BUT WITH ITS GROUPS AS WELL
            grid = this;
        }
        else
            //TO COMPLETE
            grid = this;

        ObjWriter writer = new ObjWriter(tset, grid, path, handler.getGameIndex(),
                saveTextures, includeVertexColors, tileUpscale);
        writer.writeMapObj();
    }

    public static void loadMatrixFromFile(BufferedReader br, int[][] matrix) throws IOException {
        for (int i = 0; i < rows; i++) {
            String line = br.readLine();
            String[] lineSplitted = line.split(" ");
            for (int j = 0; j < cols; j++) {
                matrix[j][i] = Integer.parseInt(lineSplitted[j]);
            }
        }
    }

    private Integer[][] loadLayerFromFile(BufferedReader br) throws IOException {
        Integer[][] layer = new Integer[cols][rows];
        for (int i = 0; i < rows; i++) {
            String line = br.readLine();
            String[] lineSplitted = line.split(" ");
            for (int j = 0; j < cols; j++) {
                layer[j][i] = Integer.parseInt(lineSplitted[j]);
            }
        }
        return layer;
    }

    public void replaceTilesUsingIndices(int[] indices) {
        int[][][] oldTileLayers = cloneTileLayers();
        for (int i = 0; i < numLayers; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < rows; k++) {
                    int index = oldTileLayers[i][j][k];
                    try {
                        if (index == -1) {
                            tileLayers[i][j][k] = -1;
                        } else {
                            tileLayers[i][j][k] = indices[index];
                        }
                    } catch (Exception ex) {
                        tileLayers[i][j][k] = -1;
                    }
                }
            }
        }
    }

    public void replaceTileWithOldLayer(int[][][] oldTileLayers, int oldIndex, int newIndex) {
        for (int i = 0; i < numLayers; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < rows; k++) {
                    if (oldTileLayers[i][j][k] == oldIndex) {
                        tileLayers[i][j][k] = newIndex;
                    }
                }
            }
        }
    }

    public int[][][] cloneTileLayers() {
        int[][][] newTileLayers = new int[numLayers][cols][rows];
        for (int i = 0; i < numLayers; i++) {
            for (int j = 0; j < cols; j++) {
                System.arraycopy(tileLayers[i][j], 0, newTileLayers[i][j], 0, rows);
            }
        }
        return newTileLayers;
    }

    public void removeTileFromMap(int tileIndex) {
        for (int i = 0; i < numLayers; i++) {
            removeTileFromLayer(i, tileIndex);
        }
    }

    public void removeTileFromLayer(int layerIndex, int tileIndex) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (tileLayers[layerIndex][i][j] == tileIndex) {
                    tileLayers[layerIndex][i][j] = -1;
                }
            }
        }
    }

    public void clearLayer(int layerIndex) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tileLayers[layerIndex][i][j] = -1;
                heightLayers[layerIndex][i][j] = 0;
            }
        }
    }

    public void clearAllLayers() {
        for (int i = 0; i < numLayers; i++) {
            clearLayer(i);
        }
    }

    public void moveTilesUp(int layerIndex) {
        for (int i = 0; i < (cols - 0); i++) {
            for (int j = (rows - 1); j > 0; j--) {
                tileLayers[layerIndex][i][j] = tileLayers[layerIndex][i][j - 1];
                heightLayers[layerIndex][i][j] = heightLayers[layerIndex][i][j - 1];
            }
            tileLayers[layerIndex][i][0] = -1;
            heightLayers[layerIndex][i][0] = 0;
        }
    }

    public void moveTilesDown(int layerIndex) {
        for (int i = 0; i < (cols - 0); i++) {
            for (int j = 0; j < (rows - 1); j++) {
                tileLayers[layerIndex][i][j] = tileLayers[layerIndex][i][j + 1];
                heightLayers[layerIndex][i][j] = heightLayers[layerIndex][i][j + 1];
            }
            tileLayers[layerIndex][i][rows - 1] = -1;
            heightLayers[layerIndex][i][rows - 1] = 0;
        }
    }

    public void moveTilesRight(int layerIndex) {
        for (int i = 0; i < (rows - 0); i++) {
            for (int j = (cols - 1); j > 0; j--) {
                tileLayers[layerIndex][j][i] = tileLayers[layerIndex][j - 1][i];
                heightLayers[layerIndex][j][i] = heightLayers[layerIndex][j - 1][i];
            }
            tileLayers[layerIndex][0][i] = -1;
            heightLayers[layerIndex][0][i] = 0;
        }
    }

    public void moveTilesLeft(int layerIndex) {
        for (int i = 0; i < (rows - 0); i++) {
            for (int j = 0; j < (cols - 1); j++) {
                tileLayers[layerIndex][j][i] = tileLayers[layerIndex][j + 1][i];
                heightLayers[layerIndex][j][i] = heightLayers[layerIndex][j + 1][i];
            }
            tileLayers[layerIndex][rows - 1][i] = -1;
            heightLayers[layerIndex][rows - 1][i] = 0;
        }
    }

    public void moveTilesUpZ(int layerIndex) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (heightLayers[layerIndex][i][j] < MapEditorHandler.maxHeight) {
                    heightLayers[layerIndex][i][j]++;
                }
            }
        }
    }

    public void moveTilesDownZ(int layerIndex) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (heightLayers[layerIndex][i][j] > MapEditorHandler.minHeight) {
                    heightLayers[layerIndex][i][j]--;
                }
            }
        }
    }

    public static void printMatrixInFile(PrintWriter out, int[][] matrix) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                out.print(matrix[j][i] + " ");
            }
            out.println();
        }
    }

    public void floodFillTileGrid(int x, int y, int value, int tileWidth, int tileHeight) {
        final int prevC = handler.getActiveTileLayer()[x][y];

        //Generate mask
        boolean[][] mask = new boolean[MapGrid.cols][MapGrid.rows];
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                mask[i][j] = true;
            }
        }

        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                final int tileIndex = handler.getActiveTileLayer()[i][j];
                if (tileIndex != -1 && tileIndex != prevC) {
                    try {
                        Tile tile = handler.getTileset().get(handler.getActiveTileLayer()[i][j]);
                        int xSize = tile.getWidth() - Math.max(0, i + tile.getWidth() - MapGrid.cols);
                        int ySize = tile.getHeight() - Math.max(0, j + tile.getHeight() - MapGrid.rows);
                        for (int m = 0; m < xSize; m++) {
                            for (int n = 0; n < ySize; n++) {
                                mask[i + m][j + n] = false;
                            }
                        }
                    } catch (Exception ex) {
                        mask[i][j] = false;
                    }
                }
            }
        }

        Utils.floodFillMatrix(tileLayers[handler.getActiveLayerIndex()], mask, x, y, value, tileWidth, tileHeight);
    }

    public void floodFillHeightGrid(int x, int y, int value) {
        Utils.floodFillMatrix(heightLayers[handler.getActiveLayerIndex()], x, y, value);
    }

    /*
    public void clearCopyLayer() {
        tileLayerCopy = null;
        heightLayerCopy = null;
    }*/

    /*
       public void copySelectedLayer() {
           copyLayer(handler.getActiveLayerIndex());
       }

       public void pasteTileLayer() {
           pasteTileLayer(handler.getActiveLayerIndex());
       }

       public void pasteHeightLayer() {
           pasteHeightLayer(handler.getActiveLayerIndex());
       }
       public void copyLayer(int index) {
           tileLayerCopy = cloneTileLayer(index);
           heightLayerCopy = cloneHeightLayer(index);
       }

       public void pasteTileLayer(int index) {
           if (tileLayerCopy != null) {
               tileLayers[index] = cloneLayer(tileLayerCopy);
           }
       }

       public void pasteHeightLayer(int index) {
           if (heightLayerCopy != null) {
               heightLayers[index] = cloneLayer(heightLayerCopy);
           }
       }

       public int[][] getTileLayerCopy() {
           return tileLayerCopy;
       }

       public int[][] getHeightLayerCopy() {
           return heightLayerCopy;
       }*/
    public int[][] cloneLayer(int[][] layer) {
        int[][] copy = new int[cols][rows];
        for (int i = 0; i < layer.length; i++) {
            System.arraycopy(layer[i], 0, copy[i], 0, layer[i].length);
        }
        return copy;
    }

    public int[][] cloneTileLayer(int index) {
        return cloneLayer(tileLayers[index]);
    }

    public int[][] cloneHeightLayer(int index) {
        return cloneLayer(heightLayers[index]);
    }

    public void setTileLayer(int index, int[][] tileLayer) {
        this.tileLayers[index] = tileLayer;
    }

    public void setHeightLayer(int index, int[][] heightLayer) {
        this.heightLayers[index] = heightLayer;
    }

    public boolean isEmpty() {
        for (int i = 0; i < tileLayers.length; i++) {
            for (int j = 0; j < tileLayers[i].length; j++) {
                for (int k = 0; k < tileLayers[i][j].length; k++) {
                    if (tileLayers[i][j][k] != -1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void updateAllMapLayers(boolean realTimePostProcessing) {
        for (int i = 0; i < mapLayersGL.length; i++) {
            updateMapLayerGL(i, realTimePostProcessing);
        }
    }

    public void updateMapLayerGL(int layerIndex, boolean realTimePostProcessing) {
        this.mapLayersGL[layerIndex] = new MapLayerGL(
                tileLayers[layerIndex], heightLayers[layerIndex],
                handler.getTileset(),
                realTimePostProcessing, handler.getGame().getMaxTileableSize());
    }

    public void addTileIndicesUsed(HashSet<Integer> indices) {
        for (int i = 0; i < tileLayers.length; i++) {
            for (int j = 0; j < tileLayers[i].length; j++) {
                for (int k = 0; k < tileLayers[i][j].length; k++) {
                    if (tileLayers[i][j][k] != -1) {
                        indices.add(tileLayers[i][j][k]);
                    }
                }
            }
        }
    }

    public HashSet<Integer> getTileIndicesUsed() {
        HashSet<Integer> indices = new HashSet<>();
        addTileIndicesUsed(indices);
        return indices;
    }

    public int getNumTriangles() {
        int numTriangles = 0;
        for (MapLayerGL mapLayerGL : mapLayersGL) {
            if (mapLayerGL != null) {
                numTriangles += mapLayerGL.getNumTriangles();
            }
        }
        return numTriangles;
    }

    public int getNumQuads() {
        int numQuads = 0;
        for (MapLayerGL mapLayerGL : mapLayersGL) {
            if (mapLayerGL != null) {
                numQuads += mapLayerGL.getNumQuads();
            }
        }
        return numQuads;
    }

    public int getNumPolygons() {
        return getNumTriangles() + getNumQuads();
    }

    public int getNumMaterials() {
        int numMaterials = 0;
        for (MapLayerGL mapLayerGL : mapLayersGL) {
            if (mapLayerGL != null) {
                numMaterials += mapLayerGL.getNumGeometryGL();
            }
        }
        return numMaterials;
    }

    public void applyLookupTable(int[] tileIndices) {
        for (int i = 0; i < tileLayers.length; i++) {
            for (int j = 0; j < tileLayers[i].length; j++) {
                for (int k = 0; k < tileLayers[i][j].length; k++) {
                    try {
                        tileLayers[i][j][k] = tileIndices[tileLayers[i][j][k]];
                    } catch (Exception ex) {
                        tileLayers[i][j][k] = -1;
                    }
                }
            }
        }
    }

}
