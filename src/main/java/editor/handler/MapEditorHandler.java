/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.handler;

import editor.MainFrame;
import editor.backsound.Backsound;
import editor.bdhc.Bdhc;
import editor.bordermap.BorderMapsGrid;
import editor.buildingeditor2.buildfile.BuildFile;
import editor.collisions.Collisions;
import editor.game.Game;
import editor.smartdrawing.SmartGrid;
import editor.state.MapLayerState;
import editor.state.StateHandler;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import tileset.Tile;
import tileset.Tileset;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class MapEditorHandler {

    //Version name
    public static final String versionName = "Pokemon DS Map Studio 1.19";
    
    //Main frame
    private MainFrame mainFrame;

    //Game
    private Game game;
    
    //Working directory
    private String lastTilesetDirectoryUsed = null;
    private String lastMapDirectoryUsed = null;
    private String lastBdhcDirectoryUsed = null;
    private String lastCollisionsDirectoryUsed = null;
    private String lastNsbtxDirectoryUsed = null;
    private String lastTileObjDirectoryUsed = null;
    private String lastBuildEditorDirectoryUsed = null;
    
    //Tileset
    private Tileset tset;

    //Tile selector
    private int indexTileSelected = 0;

    //Height map
    private final int minHeight = -15;
    private final int maxHeight = 15;
    private final int numHeights = maxHeight - minHeight + 1;
    private int[] heights = new int[numHeights];
    private final Color[] heightColors = new Color[numHeights];
    private int heightIndexSelected = 0;
    private BufferedImage[] heightImages = new BufferedImage[numHeights];

    //Map grid
    private MapGrid grid = new MapGrid(this);
    private int[][] tileLayerCopy = null;
    private int[][] heightLayerCopy = null;
    
    //Layers
    public boolean[] renderLayers = new boolean[MapGrid.numLayers];

    //Smart grid
    private int smartGridIndexSelected = 0;
    
    //Bdhc
    private Bdhc bdhc;
    
    //Backsound
    private Backsound backsound;
    
    //Collisions
    private Collisions collisions;
    
    //Building file
    private BuildFile buildings;
    
    //Map State Hanlder
    private StateHandler mapStateHandler = new StateHandler();
    private boolean layerChanged = false;
    
    //Border Maps
    private Tileset borderMapTileset;
    private BorderMapsGrid borderMapsGrid = new BorderMapsGrid();
    
    
    public MapEditorHandler(MainFrame frame) {
        initHeights();

        game = new Game(Game.DIAMOND);
        game.loadGameIcons();
        
        this.mainFrame = frame;
        
        //Active layer
        for(int i = 0; i < renderLayers.length; i++){
            renderLayers[i] = true;
        }
        
        //sgridArray.add(new SmartGrid(this));
        //sgridArray.add(new SmartGrid(this));
        //sgridArray.add(new SmartGrid(this));
    }

    private void initHeights() {
        heightImages = Utils.loadVerticalImageArrayAsResource("/imgs/heights_increased.png", 31);
        for (int i = 0; i < numHeights; i++) {
            heights[i] = minHeight + i;
            /*
            int r = ((maxColor.getRed() - minColor.getRed()) * i) / numHeights + minColor.getRed();
            int g = ((maxColor.getGreen() - minColor.getGreen()) * i) / numHeights + minColor.getGreen();
            int b = ((maxColor.getBlue() - minColor.getBlue()) * i) / numHeights + minColor.getBlue();
            int a = ((maxColor.getAlpha() - minColor.getAlpha()) * i) / numHeights + minColor.getAlpha();
            heightColors[i] = new Color(r, g, b, a);

            heightImages[i] = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics gr = heightImages[i].getGraphics();

            //Draw height color
            gr.setColor(heightColors[i]);
            gr.fillRect(0, 0, tileSize, tileSize);

            //Draw height value
            int xOffset = Math.abs(heights[i]) > 9 ? 1 : 5;
            gr.setColor(new Color(0, 0, 0, 255));
            gr.drawString(String.valueOf(heights[i]), xOffset, tileSize - 2);
            */
        }
        
    }

    

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void incrementTileSelected(int delta) {
        int newTileIndex = indexTileSelected + delta;
        if ((newTileIndex >= 0) && (newTileIndex < tset.size())) {
            indexTileSelected = newTileIndex;
        }
    }

    public void incrementHeightSelected(int delta) {
        int newHeightIndex = heightIndexSelected + delta;
        if ((newHeightIndex >= 0) && (newHeightIndex < numHeights)) {
            heightIndexSelected = newHeightIndex;
        }
    }

    public BufferedImage getHeightImage(int index){
        return heightImages[index];
    }
    
    public BufferedImage getHeightImageByValue(int value) {
        return heightImages[maxHeight - value];
    }

    public int[] getHeights() {
        return heights;
    }

    public int getNumHeights() {
        return numHeights;
    }

    public int getHeight(int index) {
        return heights[index];
    }

    public int getHeightSelected() {
        return heights[heightIndexSelected];
    }

    public int getHeightIndexSelected() {
        return heightIndexSelected;
    }

    public void setHeightSelected(int value){
        setHeightIndexSelected(value - minHeight);
    }
    
    public void setHeightIndexSelected(int index) {
        if(index >= 0 && index < heights.length){
            this.heightIndexSelected = index;
        }
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public Color getHeightColor(int heightValue) {
        return heightColors[heightValue - minHeight];
    }

    public Color getHeightColorByIndex(int index) {
        return heightColors[index];
    }

    public void setIndexTileSelected(int index) {
        this.indexTileSelected = index;
    }

    public int getTileIndexSelected() {
        return indexTileSelected;
    }

    public void setTileset(Tileset tileset) {
        this.tset = tileset;
    }

    public Tile getTileSelected() {
        return tset.get(indexTileSelected);
    }

    public Tileset getTileset() {
        return tset;
    }

    public MapGrid getGrid() {
        return grid;
    }

    public void setGrid(MapGrid grid) {
        this.grid = grid;
    }

    public ArrayList<SmartGrid> getSmartGridArray(){
        return tset.getSmartGridArray();
    }
    
    public SmartGrid getSmartGrid(int index){
        return tset.getSmartGridArray().get(index);
    }
    
    public String getLastTilesetDirectoryUsed(){
        return lastTilesetDirectoryUsed;
    }
    
    public String getLastMapDirectoryUsed(){
        return lastMapDirectoryUsed;
    }
    
    public String getLastBdhcDirectoryUsed(){
        return lastBdhcDirectoryUsed;
    }
    public String getLastCollisionsDirectoryUsed(){
        return lastCollisionsDirectoryUsed;
    }
    
    public String getLastNsbtxDirectoryUsed(){
        return lastNsbtxDirectoryUsed;
    }
    
    public String getLastTileObjDirectoryUsed(){
        return lastTileObjDirectoryUsed;
    }
    
    public String getLastBuildDirectoryUsed(){
        return lastBuildEditorDirectoryUsed;
    }
    
    public void setLastTilesetDirectoryUsed(String lastDirectoryUsed){
        this.lastTilesetDirectoryUsed = lastDirectoryUsed; 
    }
    
    public void setLastMapDirectoryUsed(String lastDirectoryUsed){
        this.lastMapDirectoryUsed = lastDirectoryUsed; 
    }
    
    public void setLastBdhcDirectoryUsed(String lastDirectoryUsed){
        this.lastBdhcDirectoryUsed = lastDirectoryUsed; 
    }
    
    public void setLastCollisionsDirectoryUsed(String lastDirectoryUsed){
        this.lastCollisionsDirectoryUsed = lastDirectoryUsed; 
    }
    
    public void setLastNsbtxDirectoryUsed(String lastDirectoryUsed){
        this.lastNsbtxDirectoryUsed = lastDirectoryUsed; 
    }
    
    public void setLastTileObjDirectoryUsed(String lastDirectoryUsed){
        this.lastTileObjDirectoryUsed = lastDirectoryUsed;
    }
    
    public void setLastBuildDirectoryUsed(String lastDirectoryUsed){
        this.lastBuildEditorDirectoryUsed = lastDirectoryUsed;
    }
    
    public int[][] getActiveTileLayer(){
        return grid.tileLayers[grid.activeLayer];
    }
    
    public int[][] getActiveHeightLayer(){
        return grid.heightLayers[grid.activeLayer];
    }
    
    public void setActiveTileLayer(int index){
        if(index >= 0 && index < grid.numLayers){
            grid.activeLayer = index;
        }
    }
    
    public void setOnlyActiveTileLayer(int index){
        if(index >= 0 && index < grid.numLayers){
            for(int i = 0; i < grid.numLayers; i++){
                renderLayers[i] = false;
            }
            renderLayers[index] = true;
            grid.activeLayer = index;
            mainFrame.repaintMapDisplay();
        }
    }
    
    public int[][] getTileLayer(int index){
        return grid.tileLayers[index];
    }
    
    public int[][] getHeightLayer(int index){
        return grid.heightLayers[index];
    }
    
    public int getActiveLayerIndex(){
        return grid.activeLayer;
    }
    
    public SmartGrid getSmartGridSelected(){
        return tset.getSmartGridArray().get(smartGridIndexSelected);
    }
    
    public void setSmartGridIndexSelected(int index){
        smartGridIndexSelected = index;
    }
    
    public int getSmartGridIndexSelected(){
        return smartGridIndexSelected;
    }
    
    public void invertLayerState(int index) {
        renderLayers[index] = !renderLayers[index];
        //mainFrame.repaintMapDisplay();
    }
    
    public void updateLayerThumbnail(int index){
        mainFrame.getThumbnailLayerSelector().drawLayerThumbnail(index);
    }
    
    public void repaintThumbnailLayerSelector(){
        mainFrame.getThumbnailLayerSelector().repaint();
    }
   
    public Bdhc getBdhc(){
        return bdhc;
    }
    
    public void setBdhc(Bdhc bdhc){
        this.bdhc = bdhc;
    }
    
    public Backsound getBacksound(){
        return backsound;
    }
    
    public void setBacksound(Backsound backsound){
        this.backsound = backsound;
    }
    
    public Collisions getCollisions(){
        return collisions;
    }
    
    public void setCollisions(Collisions collisions){
        this.collisions = collisions;
    }
    
    public void setGameIndex(int game){
        this.game.gameSelected = game;
    }
    
    public int getGameIndex(){
        return game.gameSelected;
    }
    
    public Game getGame(){
        return game;
    }
    
    public StateHandler getMapStateHandler(){
        return mapStateHandler;
    }
    
    public void addMapState(MapLayerState state){
        mapStateHandler.addState(state);
        mainFrame.getUndoButton().setEnabled(true);
        mainFrame.getRedoButton().setEnabled(false);
    }
    
    public void resetMapStateHandler(){
        mapStateHandler = new StateHandler();
    }
    
    public void moveSelectedSmartGridUp(){
        if(smartGridIndexSelected > 0){
            Collections.swap(tset.getSmartGridArray(), smartGridIndexSelected, smartGridIndexSelected - 1);
            smartGridIndexSelected--;
        }
    }
    
    public void moveSelectedSmartGridDown(){
        if(smartGridIndexSelected < tset.getSmartGridArray().size() - 1){
            Collections.swap(tset.getSmartGridArray(), smartGridIndexSelected, smartGridIndexSelected + 1);
            smartGridIndexSelected++;
        }
    }
    
    public boolean mapHasVertexColors(){
        //Get indices of tiles used in map
        TreeSet<Integer> tileIndicesInGrid = new TreeSet();
        for(int i = 0; i < grid.tileLayers.length; i++){
            for(int j = 0; j < grid.tileLayers[i].length; j++){
                for(int k = 0; k < grid.tileLayers[i][j].length; k++){
                    int tileIndex = grid.tileLayers[i][j][k];
                    if(tileIndex != -1){
                        tileIndicesInGrid.add(tileIndex);
                    }
                }
            }
        }
        
        //Get indices of materials used in tiles
        TreeSet<Integer> materialIndicesInGrid = new TreeSet();
        for(Integer tileIndex : tileIndicesInGrid){
            Tile tile = tset.get(tileIndex);
            for(Integer materialIndex : tile.getTextureIDs()){
                materialIndicesInGrid.add(materialIndex);
            }
        }
        
        //Check if materials in map use vertex colors
        for(Integer materialIndex : materialIndicesInGrid){
            if(tset.getMaterial(materialIndex).vertexColorsEnabled()){
                return true;
            }
        }
        return false;
    }
    
    public boolean isLayerChanged(){
        return layerChanged;
    }
    
    public void setLayerChanged(boolean layerChanged){
        this.layerChanged = layerChanged;
    }
    
    public void setBorderMapsTileset(Tileset borderMapTileset){
        this.borderMapTileset = borderMapTileset;
    }
    
    public Tileset getBorderMapsTileset(){
        return borderMapTileset;
    }
    
    public BorderMapsGrid getBorderMapsGrid(){
        return borderMapsGrid;
    }
    
    public void setBorderMapsGrid(BorderMapsGrid borderMapsGrid){
        this.borderMapsGrid = borderMapsGrid;
    }
    
    public String getVersionName(){
        return versionName;
    }
    
    public String getMapName(){
        return Utils.removeExtensionFromPath(new File(grid.filePath).getName());
    }

    public BuildFile getBuildings() {
        return buildings;
    }

    public void setBuildings(BuildFile buildings) {
        this.buildings = buildings;
    }
    
    
    
}
