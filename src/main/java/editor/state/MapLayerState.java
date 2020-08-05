/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.state;

import editor.handler.MapEditorHandler;

/**
 *
 * @author Trifindo
 */
public class MapLayerState extends State{
    
    private MapEditorHandler handler;
    private int layerIndex;
    
    private int[][] tileLayer;
    private int[][] heightLayer;

    public MapLayerState(String name, MapEditorHandler handler) {
        super(name);
        this.handler = handler;
        this.layerIndex = handler.getActiveLayerIndex();
        tileLayer = handler.getGrid().cloneTileLayer(layerIndex);
        heightLayer = handler.getGrid().cloneHeightLayer(layerIndex); 
    }

    @Override
    public void revertState() {
        handler.getGrid().setTileLayer(layerIndex, tileLayer);
        handler.getGrid().setHeightLayer(layerIndex, heightLayer);
    }
    
    public int getLayerIndex(){
        return layerIndex;
    }
    
    
}
