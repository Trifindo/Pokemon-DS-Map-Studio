/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.tileseteditor;

import editor.TilesetRenderer;
import editor.handler.MapEditorHandler;
import editor.smartdrawing.SmartGrid;
import java.awt.Color;
import java.util.ArrayList;
import tileset.Tile;
import tileset.Tileset;
import tileset.TilesetMaterial;

/**
 *
 * @author Trifindo
 */
public class TilesetEditorHandler {

    private MapEditorHandler handler;

    private Tileset oldTset;

    private int textureIdIndexSelected = 0;
    private int materialIndexSelected = 0;

    //private TilesetRenderer tr;
    
    private Color lastColorUsed = Color.white;

    public TilesetEditorHandler(MapEditorHandler handler) {
        this.handler = handler;

        oldTset = handler.getTileset().clone();

        //tr = new TilesetRenderer(handler.getTileset());
    }

    public int[] getChangeIndices(){
        Tileset tset = handler.getTileset();
        int[] indices = new int[oldTset.size()];
        for (int i = 0; i < oldTset.size(); i++) {
            indices[i] = tset.getIndexOfTile(oldTset.get(i));
        }
        return indices;
    }
    
    public void fixMapGridIndices(int[] indices) {
        handler.getGrid().replaceTilesUsingIndices(indices);
    }
    
    public void fixTilesetGridIndices(int[] indices){
        for (SmartGrid sgrid : handler.getSmartGridArray()) {
            sgrid.replaceTilesUsingIndices(indices);
        }
    }

    public void setTextureIdIndexSelected(int index) {
        this.textureIdIndexSelected = index;
    }

    public int getTextureIdIndexSelected() {
        return textureIdIndexSelected;
    }
    
    public int getTextureIndexSelected(){
        return handler.getTileSelected().getTextureIDs().get(textureIdIndexSelected);
    }

    public String getTextureSelectedName() {
        return handler.getTileset().getImageName(handler.getTileSelected().getTextureIDs().get(textureIdIndexSelected));
    }
    
    public String getMaterialSelectedTextureName(){
        return handler.getTileset().getImageName(getMaterialIndexSelected());
    }

    public MapEditorHandler getMapEditorHandler() {
        return handler;
    }

    /*
    public void updateTilesetRenderer() {
        tr.setTileset(handler.getTileset());
    }

    public void updateTileThumbnail(int index) {
        tr.init();
        tr.renderTileThumbnail(index);
        //tr.renderTiles();
    }
    
    public void updateTileThumnails(ArrayList<Integer> indices){
        tr.init();
        for(int i = 0; i < indices.size(); i++){
            tr.renderTileThumbnail(indices.get(i));
        }
    }
    */
    public void setMaterialIndexSelected(int index) {
        this.materialIndexSelected = index;
    }
    
    public int getMaterialIndexSelected(){
        return this.materialIndexSelected;
    }

    public TilesetMaterial getMaterialSelected() {
        return handler.getTileset().getMaterial(materialIndexSelected);
    }

    public Color getLastColorUsed(){
        return lastColorUsed;
    }
    
    public void setLastColorUsed(Color color){
        this.lastColorUsed = color;
    }
    
}
