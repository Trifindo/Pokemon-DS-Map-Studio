
package formats.obj;

import java.util.List;

import tileset.Face;
import tileset.Tile;
import tileset.TileGeometryCompressor;
import tileset.TileGeometryDecompressor;

/**
 * @author Trifindo
 */
public class GlobalTextureMapper {

    public static void applyGlobalTextureMapping(Tile tile, int col, int row) {
        float[] tCoordsTri = TileGeometryDecompressor.decompressObjData(tile.getFIndTriObj(), tile.getTextureCoordsObj(), 2, 3);
        float[] tCoordsQuad = TileGeometryDecompressor.decompressObjData(tile.getFIndQuadObj(), tile.getTextureCoordsObj(), 2, 4);

        applyGlobalTextureMapping(tCoordsTri, tile.getFIndTriObj(), tile.getVertexCoordsObj(), tile.getGlobalTextureScale(), col, row);
        applyGlobalTextureMapping(tCoordsQuad, tile.getFIndQuadObj(), tile.getVertexCoordsObj(), tile.getGlobalTextureScale(), col, row);

        TileGeometryCompressor.CompressedObjData compressedData = TileGeometryCompressor.compressObjData(tCoordsTri, tCoordsQuad, 2);

        for (int i = 0; i < tile.getFIndTriObj().size(); i++) {
            tile.getFIndTriObj().get(i).tInd = compressedData.triIndices[i];
        }
        for (int i = 0; i < tile.getFIndQuadObj().size(); i++) {
            tile.getFIndQuadObj().get(i).tInd = compressedData.quadIndices[i];
        }

        tile.setTextureCoordsObj(compressedData.data);
    }

    private static void applyGlobalTextureMapping(float[] tCoords, List<Face> faces, List<Float> vCoords, float scale, int col, int row) {
        final int vCoordsPerVertex = 3;
        for (int i = 0, c = 0; i < faces.size(); i++) {
            Face face = faces.get(i);

            for (int j = 0; j < face.vInd.length; j++) {
                float x = vCoords.get((face.vInd[j] - 1) * vCoordsPerVertex);
                float y = vCoords.get((face.vInd[j] - 1) * vCoordsPerVertex + 1);

                tCoords[c++] = (x + col) * scale;
                tCoords[c++] = (y + row) * scale;
            }
        }
    }
}
