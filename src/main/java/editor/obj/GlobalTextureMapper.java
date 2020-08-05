/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.obj;

import java.util.ArrayList;
import tileset.Face;
import tileset.Tile;
import tileset.TileGeometryCompresser;
import tileset.TileGeometryDecompresser;

/**
 *
 * @author Trifindo
 */
public class GlobalTextureMapper {
    
    public static void applyGlobalTextureMapping(Tile tile, int col, int row){
        float[] tCoordsTri = TileGeometryDecompresser.decompressObjData(tile.getFIndTriObj(), tile.getTextureCoordsObj(), 2, 3);
        float[] tCoordsQuad = TileGeometryDecompresser.decompressObjData(tile.getFIndQuadObj(), tile.getTextureCoordsObj(), 2, 4);
       
        applyGlobalTextureMapping(tCoordsTri, tile.getFIndTriObj(), tile.getVertexCoordsObj(), tile.getGlobalTextureScale(), col, row);
        applyGlobalTextureMapping(tCoordsQuad, tile.getFIndQuadObj(), tile.getVertexCoordsObj(), tile.getGlobalTextureScale(), col, row);
        
        TileGeometryCompresser.CompressedObjData compressedData = TileGeometryCompresser.compressObjData(tCoordsTri, tCoordsQuad, 2);
        
        for(int i = 0; i < tile.getFIndTriObj().size(); i++){
            tile.getFIndTriObj().get(i).tInd = compressedData.triIndices[i];
        }
        for(int i = 0; i < tile.getFIndQuadObj().size(); i++){
            tile.getFIndQuadObj().get(i).tInd = compressedData.quadIndices[i];
        }
        
        tile.setTextureCoordsObj(compressedData.data);
    }
    
    private static void applyGlobalTextureMapping(float[] tCoords, ArrayList<Face> faces, 
            ArrayList<Float> vCoords, float scale, int col, int row){
        final int vCoordsPerVertex = 3;
        for(int i = 0, c = 0; i < faces.size(); i++){
            Face face = faces.get(i);
            
            for(int j = 0; j < face.vInd.length; j++){
                float x = vCoords.get((face.vInd[j] - 1) * vCoordsPerVertex);
                float y = vCoords.get((face.vInd[j] - 1) * vCoordsPerVertex + 1);
                
                tCoords[c++] = (x + col) * scale;
                tCoords[c++] = (y + row) * scale;
            }
        }
    }
    
    
    
    
}
