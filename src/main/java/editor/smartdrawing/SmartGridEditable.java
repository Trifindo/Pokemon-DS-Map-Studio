/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.smartdrawing;

import static editor.smartdrawing.SmartGrid.height;
import static editor.smartdrawing.SmartGrid.width;
import tileset.Tile;
import tileset.Tileset;

/**
 *
 * @author Trifindo
 */
public class SmartGridEditable {

    public Tile[][] sgrid = new Tile[width][height];

    public SmartGridEditable(int[][] data, Tileset tset) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                try{
                    sgrid[i][j] = tset.get(data[i][j]);
                }catch(Exception ex){
                    sgrid[i][j] = null;
                }
            }
        }
    }
    
    public SmartGridEditable() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sgrid[i][j] = null;
            }
        }
    }
    
    public int[][] toTileIndices(Tileset tset){
        int[][] indices = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                indices[i][j] = tset.getTiles().indexOf(sgrid[i][j]);
            }
        }
        return indices;
    }
}
