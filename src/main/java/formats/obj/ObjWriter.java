
package formats.obj;

import editor.game.Game;
import editor.grid.MapGrid;

import static editor.grid.MapGrid.cols;
import static editor.grid.MapGrid.gridTileSize;
import static editor.grid.MapGrid.rows;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;

import tileset.Face;
import tileset.Tile;
import tileset.Tileset;
import tileset.TilesetMaterial;
import utils.Utils;

/**
 * @author Trifindo
 */
public class ObjWriter {

    private Tileset tset;
    //private MapGrid grid;
    private HashMap<Point, MapGrid> maps;
    private String folderPath;
    private String savePathObj;
    private String matFilename;
    private boolean saveTextures;
    private boolean saveVertexColors = true;
    private float tileUpscale;

    private ArrayList<Tile> outTiles = new ArrayList<>();
    private ArrayList<Integer> textureUsage;

    private static final int maxTileableSizeBW = 16;
    private static final int maxTileableSizeDPHGSS = 8;
    private int maxTileableSize = 8; //TODO: Use 16 instead??

    public ObjWriter(Tileset tset, HashMap<Point, MapGrid> maps, String savePath, int game,
                     boolean saveTextures, boolean saveVertexColors, float tileUpscale) {
        this.tset = tset;
        this.maps = maps;
        this.savePathObj = savePath;
        this.saveTextures = saveTextures;
        this.saveVertexColors = saveVertexColors;
        this.tileUpscale = tileUpscale;

        if (game == Game.BLACK || game == Game.WHITE || game == Game.BLACK2 || game == Game.WHITE2) {
            maxTileableSize = maxTileableSizeBW;
        } else {
            maxTileableSize = maxTileableSizeDPHGSS;
        }
    }

    public ObjWriter(Tileset tset, MapGrid grid, String savePath, int game,
                     boolean saveTextures, boolean saveVertexColors, float tileUpscale) {
        this.tset = tset;
        this.maps = new HashMap<Point, MapGrid>(1) {
            {
                put(new Point(0, 0), grid);
            }
        };
        this.savePathObj = savePath;
        this.saveTextures = saveTextures;
        this.saveVertexColors = saveVertexColors;
        this.tileUpscale = tileUpscale;

        if (game == Game.BLACK || game == Game.WHITE || game == Game.BLACK2 || game == Game.WHITE2) {
            maxTileableSize = maxTileableSizeBW;
        } else {
            maxTileableSize = maxTileableSizeDPHGSS;
        }
    }

    public void writeMapObj() throws FileNotFoundException {
        if (!savePathObj.endsWith(".obj")) {
            savePathObj = savePathObj.concat(".obj");
        }

        String savePathMtl = savePathObj.substring(0, savePathObj.length() - (".obj").length());
        savePathMtl = savePathMtl.concat(".mtl");

        folderPath = new File(savePathObj).getParent();
        matFilename = new File(savePathMtl).getName();

        PrintWriter outObj = new PrintWriter(savePathObj);
        PrintWriter outMtl = new PrintWriter(savePathMtl);

        long time = System.currentTimeMillis();

        for (HashMap.Entry<Point, MapGrid> mapEntry : maps.entrySet()) {
            for (int k = 0; k < mapEntry.getValue().numLayers; k++) {
                boolean[][] writtenGrid = new boolean[cols][rows];
                for (int i = 0; i < cols; i++) {
                    for (int j = 0; j < rows; j++) {
                        evaluateTile(mapEntry.getValue(), mapEntry.getKey(), k, i, j, writtenGrid, tileUpscale);
                    }
                }
            }
        }

        System.out.println("Elapsed time: " + (System.currentTimeMillis() - time) + " ms");
        writeTiles(outObj, outMtl);

        outObj.close();
        outMtl.close();

        if (saveTextures) {
            writeTextures();
        }
    }

    //TODO This should be done in a separate file
    public void writeTileObj(int tileIndex, float scale, boolean flipYZ) throws FileNotFoundException {
        if (!savePathObj.endsWith(".obj")) {
            savePathObj = savePathObj.concat(".obj");
        }

        String savePathMtl = savePathObj.substring(0, savePathObj.length() - (".obj").length());
        savePathMtl = savePathMtl.concat(".mtl");

        folderPath = new File(savePathObj).getParent();
        matFilename = new File(savePathMtl).getName();

        PrintWriter outObj = new PrintWriter(savePathObj);
        PrintWriter outMtl = new PrintWriter(savePathMtl);

        Tile tile = tset.get(tileIndex).cloneObjData();
        if (scale != 1.0f) {
            tile.scaleObjModel(scale);
        }

        if (flipYZ) {
            tile.flipObjModelInvertedYZ();
        }

        outTiles = new ArrayList();
        outTiles.add(tile);

        writeTiles(outObj, outMtl);

        outObj.close();
        outMtl.close();

        if (saveTextures) {
            writeTextures();
        }
    }

    //TODO This should be done in a separate file
    public void writeAllTilesObj(float scale, boolean flipYZ) throws FileNotFoundException {
        File file = new File(savePathObj);
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        folderPath = file.getPath();

        ArrayList<String> tileObjNames = new ArrayList();
        for (int i = 0; i < tset.size(); i++) {
            String objFilename = tset.get(i).getObjFilename();
            objFilename = Utils.addExtensionToPath(objFilename, "obj");
            int counter = 1;
            String nameNoExtension = Utils.removeExtensionFromPath(objFilename);
            while (tileObjNames.contains(objFilename)) {
                objFilename = nameNoExtension + "_" + String.valueOf(counter) + ".obj";
                counter++;
            }
            tileObjNames.add(objFilename);

            savePathObj = folderPath + "/" + objFilename;
            matFilename = Utils.removeExtensionFromPath(objFilename) + ".mtl";
            String savePathMtl = folderPath + "/" + matFilename;

            PrintWriter outObj = new PrintWriter(savePathObj);
            PrintWriter outMtl = new PrintWriter(savePathMtl);

            Tile tile = tset.get(i).cloneObjData();
            if (scale != 1.0f) {
                tile.scaleObjModel(scale);
            }

            if (flipYZ) {
                tile.flipObjModelInvertedYZ();
            }

            outTiles = new ArrayList();
            outTiles.add(tile);
            writeTiles(outObj, outMtl);

            outObj.close();
            outMtl.close();
        }

        if (saveTextures) {
            writeAllTextures();
        }
    }

    private void evaluateTile(MapGrid grid, Point mapCoords, int layer, int c, int r, boolean[][] writtenGrid, float scale) {
        try {
            if ((!writtenGrid[c][r]) && (grid.tileLayers[layer][c][r] != -1)) {
                Tile tile = tset.get(grid.tileLayers[layer][c][r]).cloneObjData();
                if ((!tile.isXtileable()) && (!tile.isYtileable())) {
                    stretchTile(tile, 1, 1, c, r);
                    writeTile(grid, mapCoords, tile, layer, c, r, scale);
                    updateWGridNoTileable(writtenGrid, tile, c, r);
                } else if (tile.isXtileable() && tile.isYtileable()) {
                    int xSize = getNumEqualTilesX(grid, layer, c, r, writtenGrid, tile.getWidth());
                    int ySize = getNumEqualTilesY(grid, layer, c, r, writtenGrid, tile.getHeight());
                    if (xSize == 1 && ySize == 1) {
                        stretchTile(tile, 1, 1, c, r); // TODO: Make specific function?
                        writeTile(grid, mapCoords, tile, layer, c, r, scale);
                        updateGridTileable(writtenGrid, c, r, tile.getWidth(), tile.getHeight());
                    } else if (xSize > ySize) {
                        int yExp = getExpansionY(grid, layer, c, r, writtenGrid, tile.getWidth(), tile.getHeight(), xSize);
                        stretchTile(tile, xSize, yExp, c, r);
                        writeTile(grid, mapCoords, tile, layer, c, r, scale);
                        updateGridTileable(writtenGrid, c, r, xSize * tile.getWidth(), yExp * tile.getHeight());
                    } else {
                        int xExp = getExpansionX(grid, layer, c, r, writtenGrid, tile.getWidth(), tile.getHeight(), ySize);
                        stretchTile(tile, xExp, ySize, c, r);
                        writeTile(grid, mapCoords, tile, layer, c, r, scale);
                        updateGridTileable(writtenGrid, c, r, xExp * tile.getWidth(), ySize * tile.getHeight());
                    }
                } else if (tile.isXtileable()) {
                    int xSize = getNumEqualTilesX(grid, layer, c, r, writtenGrid, tile.getWidth());
                    if (xSize == 1) {
                        stretchTile(tile, 1, 1, c, r); // TODO: Make specific function?
                        writeTile(grid, mapCoords, tile, layer, c, r, scale);
                    } else {
                        stretchTile(tile, xSize, 1, c, r);
                        writeTile(grid, mapCoords, tile, layer, c, r, scale);
                    }
                    updateGridTileable(writtenGrid, c, r, xSize * tile.getWidth(), tile.getHeight());
                } else {
                    int ySize = getNumEqualTilesY(grid, layer, c, r, writtenGrid, tile.getHeight());
                    if (ySize == 1) {
                        stretchTile(tile, 1, 1, c, r); // TODO: Make specific function?
                        writeTile(grid, mapCoords, tile, layer, c, r, scale);
                    } else {
                        stretchTile(tile, 1, ySize, c, r);
                        writeTile(grid, mapCoords, tile, layer, c, r, scale);
                    }
                    updateGridTileable(writtenGrid, c, r, tile.getWidth(), ySize * tile.getHeight());
                }
                moveTile(tile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateGridTileable(boolean[][] writtenGrid, int c, int r, int xSize, int ySize) {
        int xLimit = Math.min(xSize, cols - c);
        int yLimit = Math.min(ySize, rows - r);
        for (int i = 0; i < xLimit; i++) {
            for (int j = 0; j < yLimit; j++) {
                writtenGrid[c + i][r + j] = true;
            }
        }
    }

    private void updateWGridNoTileable(boolean[][] writtenGrid, Tile tile, int c, int r) {
        int xLimit = Math.min(tile.getWidth(), cols - c);
        int yLimit = Math.min(tile.getHeight(), rows - r);
        for (int i = 0; i < xLimit; i++) {
            for (int j = 0; j < yLimit; j++) {
                writtenGrid[c + i][r + j] = true;
            }
        }
    }

    private void stretchTile(Tile tile, int xMult, int yMult, int c, int r) {
        ArrayList<Float> vertexCoords = tile.getVertexCoordsObj();
        int numVertex = vertexCoords.size() / 3;
        if (!(xMult == 1 && yMult == 1)) {
            for (int i = 0; i < numVertex; i++) {
                float xValue = vertexCoords.get(i * 3) * xMult;
                float yValue = vertexCoords.get(i * 3 + 1) * yMult;
                vertexCoords.set(i * 3, xValue);
                vertexCoords.set(i * 3 + 1, yValue);
            }
        }

        if (!tile.useGlobalTextureMapping()) {
            //TODO: Remove this and make extra configurable for Y tileable?
            if (tile.isXtileable() && tile.isYtileable()) {
                //New code
            } else if (tile.isXtileable()) {
                if (tile.isVtileable()) {
                    yMult = xMult;
                    xMult = 1;
                }
            } else if (tile.isYtileable()) {
                if (tile.isUtileable()) {
                    xMult = yMult;
                    yMult = 1;
                }
            } else if (tile.isYtileable() && !tile.isXtileable()) {
                xMult = yMult;
                yMult = 1;
            }

            if (!tile.isUtileable() && !tile.isVtileable()) {//NEW CODE
                xMult = 1;
                yMult = 1;
            }
            /*
            if (tile.isYtileable() && !tile.isXtileable()) {
                    xMult = yMult;
                    yMult = 1;
                }
             */
            ArrayList<Float> textureCoords = tile.getTextureCoordsObj();
            int numTextCoords = textureCoords.size() / 2;
            for (int i = 0; i < numTextCoords; i++) {
                float xValue = textureCoords.get(i * 2) * xMult;
                float yValue = textureCoords.get(i * 2 + 1) * yMult;
                textureCoords.set(i * 2, xValue);
                textureCoords.set(i * 2 + 1, yValue);
            }
        } else {
            GlobalTextureMapper.applyGlobalTextureMapping(tile, c, r);

            /*
            ArrayList<Float> textureCoords = tile.getTextureCoordsObj();
            int numTextCoords = textureCoords.size() / 2;
            BufferedImage texture = tset.getTextureImg(tile.getTextureIDs().get(0));
            float minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            float maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (int i = 0; i < tile.getTextureCoordsObj().size(); i += 2) {
                float xValue = tile.getTextureCoordsObj().get(i);
                if (xValue < minX) {
                    minX = xValue;
                } else if (xValue > maxX) {
                    maxX = xValue;
                }

                float yValue = tile.getTextureCoordsObj().get(i + 1);
                if (yValue < minY) {
                    minY = yValue;
                } else if (yValue > maxY) {
                    maxY = yValue;
                }
            }

            float detltaX = ((maxX - minX) / tile.getWidth()) * tile.getGlobalTextureScale();
            float detltaY = ((maxY - minY) / tile.getHeight()) * tile.getGlobalTextureScale();
            for (int i = 0; i < numTextCoords; i++) {
                float xValue = textureCoords.get(i * 2) * xMult + c * detltaX;
                float yValue = textureCoords.get(i * 2 + 1) * yMult + r * detltaY;
                textureCoords.set(i * 2, xValue);
                textureCoords.set(i * 2 + 1, yValue);
            }
             */
        }

    }

    private void moveTile(Tile tile) {
        ArrayList<Float> vertexCoords = tile.getVertexCoordsObj();
        int numVertex = vertexCoords.size() / 3;
        if (tile.getXOffset() != 0.0f || tile.getYOffset() != 0.0f) {
            for (int i = 0; i < numVertex; i++) {
                float xValue = vertexCoords.get(i * 3) + tile.getXOffset();
                float yValue = vertexCoords.get(i * 3 + 1) + tile.getYOffset();
                vertexCoords.set(i * 3, xValue);
                vertexCoords.set(i * 3 + 1, yValue);
            }
        }
    }

    private void displaceTile(Tile tile, int c, int r, int height) {
        ArrayList<Float> vertexCoords = tile.getVertexCoordsObj();
        int numVertex = vertexCoords.size() / 3;
        for (int i = 0; i < numVertex; i++) {
            float xValue = vertexCoords.get(i * 3) + c * gridTileSize;
            float yValue = vertexCoords.get(i * 3 + 1) + r * gridTileSize;
            float zValue = vertexCoords.get(i * 3 + 2) + height * gridTileSize;
            vertexCoords.set(i * 3, xValue);
            vertexCoords.set(i * 3 + 1, yValue);
            vertexCoords.set(i * 3 + 2, zValue);
        }
    }

    private float[] getTileCenter(Tile tile){
        ArrayList<Float> vertexCoords = tile.getVertexCoordsObj();
        float[] mean = new float[3];
        final int coordsPerVertex = 3;
        final int numVertex = vertexCoords.size() / coordsPerVertex;
        for (int i = 0; i < numVertex; i++) {
            for(int j = 0; j < coordsPerVertex; j++){
                mean[j] += vertexCoords.get(i * 3 + j);
            }
        }
        for(int i = 0; i < coordsPerVertex; i++){
            mean[i] /= numVertex;
        }
        return mean;
    }

    private void scaleTile(Tile tile, float scale){
        float[] center = getTileCenter(tile);
        ArrayList<Float> vertexCoords = tile.getVertexCoordsObj();
        final int coordsPerVertex = 3;
        final int numVertex = vertexCoords.size() / coordsPerVertex;
        for (int i = 0; i < numVertex; i++) {
            for(int j = 0; j < coordsPerVertex; j++){
                int index = i * 3 + j;
                vertexCoords.set(index, (vertexCoords.get(index) - center[j]) * scale + center[j]);
            }
        }
    }

    private int getExpansionY(MapGrid grid, int layer, int c, int r,
                              boolean[][] writtenGrid, int width, int height, int xSize) {
        int n = 1;
        for (int i = height, limit = rows - r; i < limit && n < maxTileableSize; i += height) {
            for (int j = 0; j < xSize * width; j += width) {
                int nextC = c + j;
                int nextR = r + i;
                if (!(sameHeightAndType(grid, layer, c, r, nextC, nextR) && !writtenGrid[nextC][nextR])) {
                    return n;
                }
            }
            n++;
        }

        return n;
    }

    private int getExpansionX(MapGrid grid, int layer, int c, int r,
                              boolean[][] writtenGrid, int width, int height, int ySize) {
        int n = 1;
        for (int i = width, limit = cols - c; i < limit && n < maxTileableSize; i += width) {
            for (int j = 0; j < ySize * height; j += height) {
                int nextC = c + i;
                int nextR = r + j;
                if (!(sameHeightAndType(grid, layer, c, r, nextC, nextR) && !writtenGrid[nextC][nextR])) {
                    return n;
                }
            }
            n++;
        }
        return n;
    }

    private int getNumEqualTilesX(MapGrid grid, int layer, int c, int r, boolean[][] writtenGrid, int width) {
        int n = 1;
        for (int i = width, limit = cols - c; i < limit && n < maxTileableSize; i += width) {
            int nextC = c + i;
            int nextR = r;
            if (sameHeightAndType(grid, layer, c, r, nextC, nextR) && !writtenGrid[nextC][nextR]) {
                n++;
            } else {
                return n;
            }
        }
        return n;
    }

    private int getNumEqualTilesY(MapGrid grid, int layer, int c, int r, boolean[][] writtenGrid, int height) {
        int n = 1;
        for (int i = height, limit = rows - r; i < limit && n < maxTileableSize; i += height) {
            int nextC = c;
            int nextR = r + i;
            if (sameHeightAndType(grid, layer, c, r, nextC, nextR) && !writtenGrid[nextC][nextR]) {
                n++;
            } else {
                return n;
            }
        }
        return n;
    }

    private boolean sameHeightAndType(MapGrid grid, int layer, int c1, int r1, int c2, int r2) {
        return (grid.tileLayers[layer][c1][r1] == grid.tileLayers[layer][c2][r2]
                && grid.heightLayers[layer][c1][r1] == grid.heightLayers[layer][c2][r2]);
    }

    private void writeTile(MapGrid grid, Point mapCoords, Tile tile, int layer, int c, int r, float scale) {
        if(true){//tile.isXtileable() && tile.isYtileable()){
            scaleTile(tile, scale);
        }

        displaceTile(tile,
                c - cols / 2 + mapCoords.x * cols,
                r - rows / 2 - mapCoords.y * cols,
                grid.heightLayers[layer][c][r]);
        outTiles.add(tile);
    }

    public void writeTiles(PrintWriter outObj, PrintWriter outMtl) {
        ArrayList<Float> vertexCoords = new ArrayList<>();
        ArrayList<Float> textureCoords = new ArrayList<>();
        ArrayList<Float> normalCoords = new ArrayList<>();
        ArrayList<Float> colors = new ArrayList<>();

        ArrayList<Integer> vertexCoordsOffsets = new ArrayList<>();
        ArrayList<Integer> textureCoordsOffsets = new ArrayList<>();
        ArrayList<Integer> normalCoordsOffsets = new ArrayList<>();
        ArrayList<Integer> colorsOffsets = new ArrayList<>();

        // Create and fill an array with all the v, t and n coordinates
        // Create and fill an array storing the offsets for the indices
        for (int i = 0; i < outTiles.size(); i++) {
            Tile tile = outTiles.get(i);

            vertexCoordsOffsets.add(vertexCoords.size() / 3);
            textureCoordsOffsets.add(textureCoords.size() / 2);
            normalCoordsOffsets.add(normalCoords.size() / 3);
            colorsOffsets.add(colors.size() / 3);

            vertexCoords.addAll(tile.getVertexCoordsObj());
            textureCoords.addAll(tile.getTextureCoordsObj());
            normalCoords.addAll(tile.getNormalCoordsObj());
            colors.addAll(tile.getColorsObj());
        }

        // Increment all indices from individual tiles with the offsets
        for (int i = 0; i < outTiles.size(); i++) {
            Tile tile = outTiles.get(i);

            Face.incrementAllIndices(tile.getFIndQuadObj(),
                    vertexCoordsOffsets.get(i),
                    textureCoordsOffsets.get(i),
                    normalCoordsOffsets.get(i),
                    colorsOffsets.get(i));

            Face.incrementAllIndices(tile.getFIndTriObj(),
                    vertexCoordsOffsets.get(i),
                    textureCoordsOffsets.get(i),
                    normalCoordsOffsets.get(i),
                    colorsOffsets.get(i));
        }

        // Initialize array of arrays with quads and tris indices for each tex
        int numTextures = tset.getMaterials().size();
        ArrayList<ArrayList<Face>> fIndsQuad = new ArrayList<>(numTextures);
        ArrayList<ArrayList<Face>> fIndsTri = new ArrayList<>(numTextures);
        for (int i = 0; i < numTextures; i++) {
            fIndsQuad.add(new ArrayList<>());
            fIndsTri.add(new ArrayList<>());
        }

        for (int tex = 0; tex < numTextures; tex++) {
            for (int t = 0; t < outTiles.size(); t++) {
                Tile tile = outTiles.get(t);
                int index = tile.getTextureIDs().indexOf(tex);
                if (index != -1) {
                    fIndsQuad.get(tex).addAll(tile.getFaceIndQuadOfTex(index));
                    fIndsTri.get(tex).addAll(tile.getFaceIndTriOfTex(index));
                }
            }
        }

        // Write material file
        String mtlName = matFilename;//.concat(".mtl");
        outObj.println("mtllib " + mtlName);
        String mapName = "map"; //TODO: make this configurable
        outObj.println("o " + mapName);
        int numVertexCoords = vertexCoords.size() / 3;
        for (int i = 0; i < numVertexCoords; i++) {
            writeVertexLine(outObj, vertexCoords, i);
        }
        int numTextureCoords = textureCoords.size() / 2;
        for (int i = 0; i < numTextureCoords; i++) {
            writeTextureCoordLine(outObj, textureCoords, i);
        }
        int numNormalCoords = normalCoords.size() / 3;
        for (int i = 0; i < numNormalCoords; i++) {
            writeNormalCoordLine(outObj, normalCoords, i);
        }
        if (saveVertexColors) {
            int numColors = colors.size() / 3;
            for (int i = 0; i < numColors; i++) {
                writeColorLine(outObj, colors, i);
            }
        }

        // Calculate texture usage and write all faces
        textureUsage = countTextureUsage();
        for (int i = 0; i < numTextures; i++) {
            if (textureUsage.get(i) > 0) {

                String matName = (tset.getImageName(i).split("\\."))[0];//TODO: Mabye dont use this?

                outMtl.println("newmtl " + matName);
                outMtl.println("map_Kd " + tset.getImageName(i));
                outMtl.println("map_d " + tset.getImageName(i));
                outMtl.println();

                outObj.println("usemtl " + matName);
                outObj.println("s off");

                for (Face f : fIndsQuad.get(i)) {
                    writeFaceLine(outObj, f, 4);
                }
                for (Face f : fIndsTri.get(i)) {
                    writeFaceLine(outObj, f, 3);
                }
            }
        }
    }

    private void writeVertexLine(PrintWriter out,
                                 ArrayList<Float> vertexCoords, int vertexIndex) {
        out.print("v ");
        out.print(vertexCoords.get(vertexIndex * 3) + " ");
        out.print(vertexCoords.get(vertexIndex * 3 + 1) + " ");
        out.print(vertexCoords.get(vertexIndex * 3 + 2));
        out.println();
    }

    private void writeTextureCoordLine(PrintWriter out,
                                       ArrayList<Float> textureCoords, int textureCoordIndex) {
        out.print("vt ");
        out.print(textureCoords.get(textureCoordIndex * 2) + " ");
        out.print(textureCoords.get(textureCoordIndex * 2 + 1));
        out.println();
    }

    private void writeNormalCoordLine(PrintWriter out,
                                      ArrayList<Float> normalCoords, int normalCoordIndex) {
        out.print("vn ");
        out.print(normalCoords.get(normalCoordIndex * 3) + " ");
        out.print(normalCoords.get(normalCoordIndex * 3 + 1) + " ");
        out.print(normalCoords.get(normalCoordIndex * 3 + 2));
        out.println();
    }

    private void writeColorLine(PrintWriter out,
                                ArrayList<Float> colors, int colorIndex) {
        out.print("c ");
        out.print(colors.get(colorIndex * 3) + " ");
        out.print(colors.get(colorIndex * 3 + 1) + " ");
        out.print(colors.get(colorIndex * 3 + 2));
        out.println();
    }

    private void writeFaceLine(PrintWriter out, Face face, int numVertices) {
        out.print("f");
        if (saveVertexColors) {
            for (int i = 0; i < numVertices; i++) {
                out.print(" " + face.vInd[i] + "/" + face.tInd[i] + "/" + face.nInd[i] + "/" + face.cInd[i]);
            }
        } else {
            for (int i = 0; i < numVertices; i++) {
                out.print(" " + face.vInd[i] + "/" + face.tInd[i] + "/" + face.nInd[i]);
            }
        }
        out.println();
    }

    private void writeFaceLine(PrintWriter out, ArrayList<Integer> vInd,
                               ArrayList<Integer> tInd, ArrayList<Integer> nInd, int index) {
        out.print("f ");
        out.print(vInd.get(index * 3) + "/" + tInd.get(index * 3) + "/" + nInd.get(index * 3) + " ");
        out.print(vInd.get(index * 3 + 1) + "/" + tInd.get(index * 3 + 1) + "/" + nInd.get(index * 3 + 1) + " ");
        out.print(vInd.get(index * 3 + 2) + "/" + tInd.get(index * 3 + 2) + "/" + nInd.get(index * 3 + 2));
        out.println();
    }

    private ArrayList<Integer> countTextureUsage() {
        ArrayList<Integer> count = new ArrayList<>();
        for (TilesetMaterial material : tset.getMaterials()) {
            count.add(0);
        }

        for (Tile tile : outTiles) {
            for (Integer i : tile.getTextureIDs()) {
                count.set(i, count.get(i) + 1);
            }
        }
        return count;
    }

    public void writeTextures() {
        for (int i = 0; i < tset.getMaterials().size(); i++) {
            if (textureUsage.get(i) > 0) {
                String path = folderPath + File.separator + tset.getImageName(i);
                //System.out.println(path);
                File outputfile = new File(path);
                try {
                    ImageIO.write(tset.getTextureImg(i), "png", outputfile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void writeAllTextures() {
        for (int i = 0; i < tset.getMaterials().size(); i++) {
            String path = folderPath + File.separator + tset.getImageName(i);
            //System.out.println(path);
            File outputfile = new File(path);
            try {
                ImageIO.write(tset.getTextureImg(i), "png", outputfile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
