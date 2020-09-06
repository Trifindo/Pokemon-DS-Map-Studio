/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import tileset.Tile;
import tileset.Tileset;

/**
 *
 * @author Trifindo
 */
public class MapLayerGL {

    private HashMap<Integer, GeometryGL> geometryGL;

    public MapLayerGL(int[][] tileGrid, int[][] heightGrid, Tileset tset, boolean realTimePostProcessing, int maxTileableSize) {
        if (realTimePostProcessing) {
            this.geometryGL = generateGeometryMapPostProcessed(tileGrid, heightGrid, tset, maxTileableSize);
        } else {
            this.geometryGL = generateGeometryMap(tileGrid, heightGrid, tset);
        }
    }

    private static HashMap<Integer, GeometryGL> generateGeometryMap(
            int[][] tileGrid, int[][] heightGrid, Tileset tset) {

        //Calculate geometry sizes
        HashMap<Integer, GeometrySize> infoMap = calculateGeometrySizes(tileGrid, tset);

        //Calculate texture IDs and info array
        ArrayList<GeometrySize> sizeArray = new ArrayList<>(infoMap.values());

        //Generate geometry
        HashMap<Integer, GeometryGL> geometryMap = new HashMap<>(sizeArray.size());
        for (GeometrySize sizes : sizeArray) {
            GeometryGL geometry = new GeometryGL(sizes.getID(), sizes.getNumTris(), sizes.getNumQuads());
            geometryMap.put(sizes.getID(), geometry);
        }

        //Generate map model
        HashMap<Integer, Integer> trisAdded = new HashMap<>(geometryMap.size());
        HashMap<Integer, Integer> quadsAdded = new HashMap<>(geometryMap.size());
        for (Integer ID : geometryMap.keySet()) {
            trisAdded.put(ID, 0);
            quadsAdded.put(ID, 0);
        }
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                if (tileGrid[i][j] != -1) {
                    try {
                        Tile tile = tset.get(tileGrid[i][j]);

                        float[] offset = new float[]{
                            (i - (MapGrid.cols) / 2),
                            (j - (MapGrid.rows) / 2),
                            heightGrid[i][j]
                        };

                        addTileToMap(tile, geometryMap, offset, trisAdded, quadsAdded);
                    } catch (Exception ex) {

                    }
                }
            }
        }

        for (GeometryGL geometry : geometryMap.values()) {
            geometry.updateBuffers();
        }

        return geometryMap;
    }

    private static HashMap<Integer, GeometryGL> generateGeometryMapPostProcessed(
            int[][] tileGrid, int[][] heightGrid, Tileset tset, int maxTileableSize) {

        int[][] scaleMatrixX = new int[MapGrid.cols][MapGrid.rows];
        int[][] scaleMatrixY = new int[MapGrid.cols][MapGrid.rows];

        boolean[][] writtenGrid = new boolean[MapGrid.cols][MapGrid.rows];

        HashMap<Integer, GeometrySize> infoMap = new HashMap<>(tset.getMaterials().size());
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                if (!writtenGrid[i][j] && tileGrid[i][j] != -1) {
                    try {
                        Tile tile = tset.get(tileGrid[i][j]);
                        evaluateTile(tile, i, j, tileGrid, heightGrid, writtenGrid, scaleMatrixX, scaleMatrixY, MapGrid.cols, MapGrid.rows, maxTileableSize);

                        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
                            int ID = tile.getTextureIDs().get(k);
                            int numTris = getNumPolygons(k, tile.getTexOffsetsTri(), tile.getVCoordsTri(), 3, tile.getTextureIDs().size());
                            int numQuads = getNumPolygons(k, tile.getTexOffsetsQuad(), tile.getVCoordsQuad(), 4, tile.getTextureIDs().size());

                            GeometrySize info = infoMap.get(ID);
                            if (info != null) {
                                info.addTris(numTris);
                                info.addQuads(numQuads);
                            } else {
                                info = new GeometrySize(ID, numTris, numQuads);
                                infoMap.put(ID, info);
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }

        //Calculate texture IDs and info array
        ArrayList<GeometrySize> sizeArray = new ArrayList<>(infoMap.values());

        //Generate geometry
        HashMap<Integer, GeometryGL> geometryMap = new HashMap<>(sizeArray.size());
        for (GeometrySize sizes : sizeArray) {
            GeometryGL geometry = new GeometryGL(sizes.getID(), sizes.getNumTris(), sizes.getNumQuads());
            geometryMap.put(sizes.getID(), geometry);
        }

        //Generate map model
        HashMap<Integer, Integer> trisAdded = new HashMap<>(geometryMap.size());
        HashMap<Integer, Integer> quadsAdded = new HashMap<>(geometryMap.size());
        for (Integer ID : geometryMap.keySet()) {
            trisAdded.put(ID, 0);
            quadsAdded.put(ID, 0);
        }
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                if (scaleMatrixX[i][j] != 0 && scaleMatrixY[i][j] != 0) {
                    try {
                        Tile tile = tset.get(tileGrid[i][j]);

                        float[] offset = new float[]{
                            (i - (MapGrid.cols) / 2),
                            (j - (MapGrid.rows) / 2),
                            heightGrid[i][j]
                        };

                        float[] scale = new float[]{
                            scaleMatrixX[i][j],
                            scaleMatrixY[i][j],
                            1.0f
                        };

                        float[] texScale;
                        if (tile.useGlobalTextureMapping()) {
                            texScale = new float[]{1.0f, 1.0f};
                        } else {
                            texScale = getTexScale(tile, scaleMatrixX[i][j], scaleMatrixY[i][j]);
                        }

                        addTileToMap(tile, geometryMap, offset, scale, texScale, trisAdded, quadsAdded);
                    } catch (Exception ex) {

                    }
                }
            }
        }

        for (GeometryGL geometry : geometryMap.values()) {
            geometry.updateBuffers();
        }

        return geometryMap;
    }

    private static void evaluateTile(Tile tile, int c, int r,
            int[][] tileGrid, int[][] heightGrid, boolean[][] writtenGrid,
            int[][] scaleMatrixX, int[][] scaleMatrixY, int cols, int rows, int maxTileableSize) {
        if ((!tile.isXtileable()) && (!tile.isYtileable())) {
            scaleMatrixX[c][r] = 1;
            scaleMatrixY[c][r] = 1;
            updateWrittenGrid(writtenGrid, c, r, 1, 1, cols, rows);
        } else if (tile.isXtileable() && tile.isYtileable()) {
            int xSize = getNumEqualTilesX(tileGrid, heightGrid, c, r, writtenGrid, tile.getWidth(), cols, maxTileableSize);
            int ySize = getNumEqualTilesY(tileGrid, heightGrid, c, r, writtenGrid, tile.getHeight(), rows, maxTileableSize);
            if (xSize == 1 && ySize == 1) {
                scaleMatrixX[c][r] = 1;
                scaleMatrixY[c][r] = 1;
                updateWrittenGrid(writtenGrid, c, r, tile.getWidth(), tile.getHeight(), cols, rows);
            } else if (xSize > ySize) {
                int yExp = getExpansionY(tileGrid, heightGrid, c, r, writtenGrid, tile.getWidth(), tile.getHeight(), xSize, rows, maxTileableSize);
                scaleMatrixX[c][r] = xSize;
                scaleMatrixY[c][r] = yExp;
                updateWrittenGrid(writtenGrid, c, r, xSize * tile.getWidth(), yExp * tile.getHeight(), cols, rows);
            } else {
                int xExp = getExpansionX(tileGrid, heightGrid, c, r, writtenGrid, tile.getWidth(), tile.getHeight(), ySize, cols, maxTileableSize);
                scaleMatrixX[c][r] = xExp;
                scaleMatrixY[c][r] = ySize;
                updateWrittenGrid(writtenGrid, c, r, xExp * tile.getWidth(), ySize * tile.getHeight(), cols, rows);
            }
        } else if (tile.isXtileable()) {
            int xSize = getNumEqualTilesX(tileGrid, heightGrid, c, r, writtenGrid, tile.getWidth(), cols, maxTileableSize);
            scaleMatrixX[c][r] = xSize;
            scaleMatrixY[c][r] = 1;
            updateWrittenGrid(writtenGrid, c, r, xSize * tile.getWidth(), tile.getHeight(), cols, rows);
        } else {
            int ySize = getNumEqualTilesY(tileGrid, heightGrid, c, r, writtenGrid, tile.getHeight(), rows, maxTileableSize);
            scaleMatrixX[c][r] = 1;
            scaleMatrixY[c][r] = ySize;
            updateWrittenGrid(writtenGrid, c, r, tile.getWidth(), ySize * tile.getHeight(), cols, rows);
        }
    }

    private static int getNumEqualTilesX(int[][] tileGrid, int[][] heightGrid,
            int c, int r, boolean[][] writtenGrid, int width, int cols, int maxTileableSize) {
        int n = 1;
        for (int i = width, limit = cols - c; i < limit && n < maxTileableSize; i += width) {
            int nextC = c + i;
            int nextR = r;
            if (sameHeightAndType(tileGrid, heightGrid, c, r, nextC, nextR) && !writtenGrid[nextC][nextR]) {
                n++;
            } else {
                return n;
            }
        }
        return n;
    }

    private static int getNumEqualTilesY(int[][] tileGrid, int[][] heightGrid,
            int c, int r, boolean[][] writtenGrid, int height, int rows, int maxTileableSize) {
        int n = 1;
        for (int i = height, limit = rows - r; i < limit && n < maxTileableSize; i += height) {
            int nextC = c;
            int nextR = r + i;
            if (sameHeightAndType(tileGrid, heightGrid, c, r, nextC, nextR) && !writtenGrid[nextC][nextR]) {
                n++;
            } else {
                return n;
            }
        }
        return n;
    }

    private static int getExpansionY(int[][] tileGrid, int[][] heightGrid,
            int c, int r, boolean[][] writtenGrid, int width, int height,
            int xSize, int rows, int maxTileableSize) {
        int n = 1;
        for (int i = height, limit = rows - r; i < limit && n < maxTileableSize; i += height) {
            for (int j = 0; j < xSize * width; j += width) {
                int nextC = c + j;
                int nextR = r + i;
                if (!(sameHeightAndType(tileGrid, heightGrid, c, r, nextC, nextR) && !writtenGrid[nextC][nextR])) {
                    return n;
                }
            }
            n++;
        }

        return n;
    }

    private static int getExpansionX(int[][] tileGrid, int[][] heightGrid,
            int c, int r, boolean[][] writtenGrid, int width, int height,
            int ySize, int cols, int maxTileableSize) {
        int n = 1;
        for (int i = width, limit = cols - c; i < limit && n < maxTileableSize; i += width) {
            for (int j = 0; j < ySize * height; j += height) {
                int nextC = c + i;
                int nextR = r + j;
                if (!(sameHeightAndType(tileGrid, heightGrid, c, r, nextC, nextR) && !writtenGrid[nextC][nextR])) {
                    return n;
                }
            }
            n++;
        }
        return n;
    }

    private static boolean sameHeightAndType(int[][] tileGrid, int heightGrid[][], int c1, int r1, int c2, int r2) {
        return (tileGrid[c1][r1] == tileGrid[c2][r2] && heightGrid[c1][r1] == heightGrid[c2][r2]);
    }

    private static float[] getTexScale(Tile tile, int scaleX, int scaleY) {
        if (tile.isXtileable() && tile.isYtileable()) {
            //New code
        } else if (tile.isXtileable()) {
            if (tile.isVtileable()) {
                scaleY = scaleX;
                scaleX = 1;
            }
        } else if (tile.isYtileable()) {
            if (tile.isUtileable()) {
                scaleX = scaleY;
                scaleY = 1;
            }
        } else if (tile.isYtileable() && !tile.isXtileable()) {
            scaleX = scaleY;
            scaleY = 1;
        }

        if (!tile.isUtileable() && !tile.isVtileable()) {//NEW CODE
            scaleX = 1;
            scaleY = 1;
        }

        return new float[]{scaleX, scaleY};
    }

    private static void updateWrittenGrid(boolean[][] writtenGrid, int c, int r,
            int xSize, int ySize, int cols, int rows) {
        int xLimit = Math.min(xSize, cols - c);
        int yLimit = Math.min(ySize, rows - r);
        for (int i = 0; i < xLimit; i++) {
            for (int j = 0; j < yLimit; j++) {
                writtenGrid[c + i][r + j] = true;
            }
        }
    }

    private static void addTileToMap(Tile tile, HashMap<Integer, GeometryGL> geometryMap, float[] offsets,
            HashMap<Integer, Integer> trisAdded,
            HashMap<Integer, Integer> quadsAdded) {

        for (int i = 0; i < tile.getTextureIDs().size(); i++) {
            int ID = tile.getTextureIDs().get(i);
            int numTris = getNumPolygons(i, tile.getTexOffsetsTri(), tile.getVCoordsTri(), GeometryGL.vertexPerTri, tile.getTextureIDs().size());
            int numQuads = getNumPolygons(i, tile.getTexOffsetsQuad(), tile.getVCoordsQuad(), GeometryGL.vertexPerQuad, tile.getTextureIDs().size());

            int numTrisAdded = trisAdded.get(ID);
            int numQuadsAdded = quadsAdded.get(ID);

            GeometryGL geometry = geometryMap.get(ID);
            System.arraycopy(tile.getVCoordsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.vPerTri, geometry.vCoordsTri, numTrisAdded * GeometryGL.vPerTri, numTris * GeometryGL.vPerTri);
            System.arraycopy(tile.getTCoordsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.tPerTri, geometry.tCoordsTri, numTrisAdded * GeometryGL.tPerTri, numTris * GeometryGL.tPerTri);
            System.arraycopy(tile.getNCoordsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.nPerTri, geometry.nCoordsTri, numTrisAdded * GeometryGL.nPerTri, numTris * GeometryGL.nPerTri);
            System.arraycopy(tile.getColorsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.cPerTri, geometry.colorsTri, numTrisAdded * GeometryGL.cPerTri, numTris * GeometryGL.cPerTri);
            applyOffsetsToArray(geometry.vCoordsTri, offsets, numTrisAdded * GeometryGL.vPerTri, numTris * GeometryGL.vPerTri);

            System.arraycopy(tile.getVCoordsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.vPerQuad, geometry.vCoordsQuad, numQuadsAdded * GeometryGL.vPerQuad, numQuads * GeometryGL.vPerQuad);
            System.arraycopy(tile.getTCoordsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.tPerQuad, geometry.tCoordsQuad, numQuadsAdded * GeometryGL.tPerQuad, numQuads * GeometryGL.tPerQuad);
            System.arraycopy(tile.getNCoordsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.nPerQuad, geometry.nCoordsQuad, numQuadsAdded * GeometryGL.nPerQuad, numQuads * GeometryGL.nPerQuad);
            System.arraycopy(tile.getColorsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.cPerQuad, geometry.colorsQuad, numQuadsAdded * GeometryGL.cPerQuad, numQuads * GeometryGL.cPerQuad);
            applyOffsetsToArray(geometry.vCoordsQuad, offsets, numQuadsAdded * GeometryGL.vPerQuad, numQuads * GeometryGL.vPerQuad);

            trisAdded.put(ID, numTrisAdded + numTris);
            quadsAdded.put(ID, numQuadsAdded + numQuads);
        }
    }

    private static void addTileToMap(Tile tile, HashMap<Integer, GeometryGL> geometryMap,
            float[] offsets, float[] scales, float[] scalesTex,
            HashMap<Integer, Integer> trisAdded,
            HashMap<Integer, Integer> quadsAdded) {

        for (int i = 0; i < tile.getTextureIDs().size(); i++) {
            int ID = tile.getTextureIDs().get(i);
            int numTris = getNumPolygons(i, tile.getTexOffsetsTri(), tile.getVCoordsTri(), GeometryGL.vertexPerTri, tile.getTextureIDs().size());
            int numQuads = getNumPolygons(i, tile.getTexOffsetsQuad(), tile.getVCoordsQuad(), GeometryGL.vertexPerQuad, tile.getTextureIDs().size());

            int numTrisAdded = trisAdded.get(ID);
            int numQuadsAdded = quadsAdded.get(ID);

            GeometryGL geometry = geometryMap.get(ID);
            System.arraycopy(tile.getVCoordsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.vPerTri, geometry.vCoordsTri, numTrisAdded * GeometryGL.vPerTri, numTris * GeometryGL.vPerTri);
            System.arraycopy(tile.getNCoordsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.nPerTri, geometry.nCoordsTri, numTrisAdded * GeometryGL.nPerTri, numTris * GeometryGL.nPerTri);
            System.arraycopy(tile.getColorsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.cPerTri, geometry.colorsTri, numTrisAdded * GeometryGL.cPerTri, numTris * GeometryGL.cPerTri);
            applyScalesToArray(geometry.vCoordsTri, scales, numTrisAdded * GeometryGL.vPerTri, numTris * GeometryGL.vPerTri);
            applyOffsetsToArray(geometry.vCoordsTri, offsets, numTrisAdded * GeometryGL.vPerTri, numTris * GeometryGL.vPerTri);
            if (tile.useGlobalTextureMapping()) {
                applyGlobalTexMapping(geometry.tCoordsTri, geometry.vCoordsTri, tile.getGlobalTextureScale(), numTrisAdded * GeometryGL.tPerTri, numTrisAdded * GeometryGL.vPerTri, numTris * GeometryGL.tPerTri);
            } else {
                System.arraycopy(tile.getTCoordsTri(), tile.getTexOffsetsTri().get(i) * GeometryGL.tPerTri, geometry.tCoordsTri, numTrisAdded * GeometryGL.tPerTri, numTris * GeometryGL.tPerTri);
                applyScalesToArray(geometry.tCoordsTri, scalesTex, numTrisAdded * GeometryGL.tPerTri, numTris * GeometryGL.tPerTri);
            }

            System.arraycopy(tile.getVCoordsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.vPerQuad, geometry.vCoordsQuad, numQuadsAdded * GeometryGL.vPerQuad, numQuads * GeometryGL.vPerQuad);
            System.arraycopy(tile.getNCoordsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.nPerQuad, geometry.nCoordsQuad, numQuadsAdded * GeometryGL.nPerQuad, numQuads * GeometryGL.nPerQuad);
            System.arraycopy(tile.getColorsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.cPerQuad, geometry.colorsQuad, numQuadsAdded * GeometryGL.cPerQuad, numQuads * GeometryGL.cPerQuad);
            applyScalesToArray(geometry.vCoordsQuad, scales, numQuadsAdded * GeometryGL.vPerQuad, numQuads * GeometryGL.vPerQuad);
            applyOffsetsToArray(geometry.vCoordsQuad, offsets, numQuadsAdded * GeometryGL.vPerQuad, numQuads * GeometryGL.vPerQuad);
            if (tile.useGlobalTextureMapping()) {
                applyGlobalTexMapping(geometry.tCoordsQuad, geometry.vCoordsQuad, tile.getGlobalTextureScale(), numQuadsAdded * GeometryGL.tPerQuad, numQuadsAdded * GeometryGL.vPerQuad, numQuads * GeometryGL.tPerQuad);
            } else {
                System.arraycopy(tile.getTCoordsQuad(), tile.getTexOffsetsQuad().get(i) * GeometryGL.tPerQuad, geometry.tCoordsQuad, numQuadsAdded * GeometryGL.tPerQuad, numQuads * GeometryGL.tPerQuad);
                applyScalesToArray(geometry.tCoordsQuad, scalesTex, numQuadsAdded * GeometryGL.tPerQuad, numQuads * GeometryGL.tPerQuad);
            }

            trisAdded.put(ID, numTrisAdded + numTris);
            quadsAdded.put(ID, numQuadsAdded + numQuads);
        }
    }

    private static void applyOffsetsToArray(float[] dst, float[] offsets, int startPos, int size) {
        for (int i = startPos, endPos = startPos + size; i < endPos; i++) {
            dst[i] += offsets[i % offsets.length];
        }
    }

    private static void applyScalesToArray(float[] dst, float[] scales, int startPos, int size) {
        for (int i = startPos, endPos = startPos + size; i < endPos; i++) {
            dst[i] *= scales[i % scales.length];
        }
    }

    private static void applyGlobalTexMapping(float[] tCoords, float[] vCoords, float scale, int startPosT, int startPosV, int size) {
        for (int i = startPosT, j = startPosV, endPos = startPosT + size; i < endPos; i += 2, j += 3) {
            tCoords[i] = vCoords[j] * scale;
            tCoords[i + 1] = vCoords[j + 1] * scale;
        }
    }

    private static HashMap<Integer, GeometrySize> calculateGeometrySizes(int[][] tileGrid, Tileset tset) {
        HashMap<Integer, GeometrySize> infoMap = new HashMap<>(tset.getMaterials().size());
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                if (tileGrid[i][j] != -1) {
                    try {
                        Tile tile = tset.get(tileGrid[i][j]);
                        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
                            int ID = tile.getTextureIDs().get(k);
                            int numTris = getNumPolygons(k, tile.getTexOffsetsTri(), tile.getVCoordsTri(), 3, tile.getTextureIDs().size());
                            int numQuads = getNumPolygons(k, tile.getTexOffsetsQuad(), tile.getVCoordsQuad(), 4, tile.getTextureIDs().size());

                            GeometrySize info = infoMap.get(ID);
                            if (info != null) {
                                info.addTris(numTris);
                                info.addQuads(numQuads);
                            } else {
                                info = new GeometrySize(ID, numTris, numQuads);
                                infoMap.put(ID, info);
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }
        return infoMap;
    }

    private static int getNumPolygons(int index, ArrayList<Integer> offsets, float[] vCoords,
            int vPerPolygon, int numTexIDs) {
        int start, end;
        start = offsets.get(index);
        if (index + 1 < numTexIDs) {
            end = (offsets.get(index + 1));
        } else {
            end = vCoords.length / (3 * vPerPolygon);
        }
        return end - start;

    }

    private static class GeometrySize {

        private int ID;
        private int numTris = 0;
        private int numQuads = 0;

        public GeometrySize(int ID, int numTris, int numQuads) {
            this.ID = ID;
            this.numTris = numTris;
            this.numQuads = numQuads;
        }

        public void addQuads(int numQuads) {
            this.numQuads += numQuads;
        }

        public void addTris(int numTris) {
            this.numTris += numTris;
        }

        public int getNumQuads() {
            return numQuads;
        }

        public int getNumTris() {
            return numTris;
        }

        public int getID() {
            return ID;
        }
    }

    public HashMap<Integer, GeometryGL> getGeometryGL() {
        return geometryGL;
    }

    public int getNumGeometryGL() {
        return geometryGL.size();
    }

    public int getNumTriangles() {
        int numVCoords = 0;
        for (GeometryGL geom : geometryGL.values()) {
            numVCoords += geom.vCoordsTri.length;
        }
        return numVCoords / GeometryGL.vPerTri;
    }

    public int getNumQuads() {
        int numVCoords = 0;
        for (GeometryGL geom : geometryGL.values()) {
            numVCoords += geom.vCoordsQuad.length;
        }
        return numVCoords / GeometryGL.vPerQuad;
    }

    public int getNumPolygons() {
        return getNumTriangles() + getNumQuads();
    }

}
