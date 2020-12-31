
package editor.handler;

import editor.mapmatrix.MapMatrix;
import editor.grid.MapGrid;
import editor.MainFrame;
import formats.backsound.Backsound;
import formats.bdhc.Bdhc;
import editor.bordermap.BorderMapsGrid;
import editor.buildingeditor2.buildfile.BuildFile;
import formats.bdhcam.Bdhcam;
import formats.collisions.Collisions;
import editor.game.Game;
import editor.smartdrawing.SmartGrid;
import editor.state.MapLayerState;
import editor.state.StateHandler;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import tileset.Tile;
import tileset.Tileset;
import utils.Utils;

/**
 * @author Trifindo
 */
public class MapEditorHandler {

    //Version name
    public static final String versionName = "Pokemon DS Map Studio 2.1.1 [AdAstra]";

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
    public static final int minHeight = -15;
    public static final int maxHeight = 15;
    public static final int numHeights = maxHeight - minHeight + 1;
    private int[] heights = new int[numHeights];
    private final Color[] heightColors = new Color[numHeights];
    private int heightIndexSelected = 15;
    private BufferedImage[] heightImages = new BufferedImage[numHeights];

    //Smart grid
    private int smartGridIndexSelected = 0;

    //Layers
    public boolean[] renderLayers = new boolean[MapGrid.numLayers];

    //Border Maps
    private Tileset borderMapTileset;
    private BorderMapsGrid borderMapsGrid = new BorderMapsGrid();

    //Map Data
    private MapMatrix mapMatrix;
    private Point mapSelected = new Point(0, 0);
    private int activeLayer = 0;
    private int[][] tileLayerCopy = null;
    private int[][] heightLayerCopy = null;

    //Map State Hanlder
    private StateHandler mapStateHandler = new StateHandler();
    private boolean layerChanged = false;

    private boolean realTimePostProcessing = true;

    public MapEditorHandler(MainFrame frame) {
        initHeights();

        this.mainFrame = frame;

        game = new Game(Game.DIAMOND);
        game.loadGameIcons();

        mapMatrix = new MapMatrix(this);

        //Active layer
        for (int i = 0; i < renderLayers.length; i++) {
            renderLayers[i] = true;
        }

    }

    private void initHeights() {
        heightImages = Utils.loadVerticalImageArrayAsResource("/imgs/heights_increased.png", 31);
        for (int i = 0; i < numHeights; i++) {
            heights[i] = minHeight + i;
        }

    }

    public MapData getCurrentMap() {
        return mapMatrix.getMapAndCreate(mapSelected);
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

    public BufferedImage getHeightImage(int index) {
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

    public void setHeightSelected(int value) {
        setHeightIndexSelected(value - minHeight);
    }

    public void setHeightIndexSelected(int index) {
        if (index >= 0 && index < heights.length) {
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
        return getCurrentMap().getGrid();
    }

    public void setGrid(MapGrid grid) {
        getCurrentMap().setGrid(grid);
    }

    public ArrayList<SmartGrid> getSmartGridArray() {
        return tset.getSmartGridArray();
    }


    public SmartGrid getSmartGrid(int index) {
        return tset.getSmartGridArray().get(index);
    }

    public String getLastTilesetDirectoryUsed() {
        return lastTilesetDirectoryUsed;
    }

    public String getLastMapDirectoryUsed() {
        return lastMapDirectoryUsed;
    }

    public String getLastBdhcDirectoryUsed() {
        return lastBdhcDirectoryUsed;
    }

    public String getLastCollisionsDirectoryUsed() {
        return lastCollisionsDirectoryUsed;
    }

    public String getLastNsbtxDirectoryUsed() {
        return lastNsbtxDirectoryUsed;
    }

    public String getLastTileObjDirectoryUsed() {
        return lastTileObjDirectoryUsed;
    }

    public String getLastBuildDirectoryUsed() {
        return lastBuildEditorDirectoryUsed;
    }

    public void setLastTilesetDirectoryUsed(String lastDirectoryUsed) {
        this.lastTilesetDirectoryUsed = lastDirectoryUsed;
    }

    public void setLastMapDirectoryUsed(String lastDirectoryUsed) {
        this.lastMapDirectoryUsed = lastDirectoryUsed;
    }

    public void setLastBdhcDirectoryUsed(String lastDirectoryUsed) {
        this.lastBdhcDirectoryUsed = lastDirectoryUsed;
    }

    public void setLastCollisionsDirectoryUsed(String lastDirectoryUsed) {
        this.lastCollisionsDirectoryUsed = lastDirectoryUsed;
    }

    public void setLastNsbtxDirectoryUsed(String lastDirectoryUsed) {
        this.lastNsbtxDirectoryUsed = lastDirectoryUsed;
    }

    public void setLastTileObjDirectoryUsed(String lastDirectoryUsed) {
        this.lastTileObjDirectoryUsed = lastDirectoryUsed;
    }

    public void setLastBuildDirectoryUsed(String lastDirectoryUsed) {
        this.lastBuildEditorDirectoryUsed = lastDirectoryUsed;
    }

    public int[][] getActiveTileLayer() {
        return getCurrentMap().getGrid().tileLayers[activeLayer];
    }

    public int[][] getActiveHeightLayer() {
        return getCurrentMap().getGrid().heightLayers[activeLayer];
    }

    public void setActiveTileLayer(int index) {
        MapGrid grid = getCurrentMap().getGrid();
        if (index >= 0 && index < grid.numLayers) {
            activeLayer = index;
        }
    }

    public void setOnlyActiveTileLayer(int index) {
        MapGrid grid = getCurrentMap().getGrid();
        if (index >= 0 && index < grid.numLayers) {
            for (int i = 0; i < grid.numLayers; i++) {
                renderLayers[i] = false;
            }
            renderLayers[index] = true;
            activeLayer = index;
            mainFrame.repaintMapDisplay();
        }
    }
    
    public void invertEveryLayer(int index) {
        MapGrid grid = getCurrentMap().getGrid();
        if (index >= 0 && index < grid.numLayers) {
            for (int i = 0; i < grid.numLayers; i++) {
                renderLayers[i] = !renderLayers[i];
            }
            activeLayer = index;
            mainFrame.repaintMapDisplay();
        }
    }

    public boolean isLayerTheOnlyActive(int index) {
        MapGrid grid = getCurrentMap().getGrid();
        for(int i = 0; i < grid.numLayers; i++){
            if(renderLayers[i] && i != index){
                return false;
            }
        }
        return true;
    }
    
    public void setAllLayersState(boolean enabled) {
        MapGrid grid = getCurrentMap().getGrid();
        for(int i = 0; i < grid.numLayers; i++){
            renderLayers[i] = enabled;
        }
    }

    public int[][] getTileLayer(int index) {
        return getCurrentMap().getGrid().tileLayers[index];
    }

    public int[][] getHeightLayer(int index) {
        return getCurrentMap().getGrid().heightLayers[index];
    }

    public int getActiveLayerIndex() {
        return activeLayer;
    }

    public SmartGrid getSmartGridSelected() {
        return tset.getSmartGridArray().get(smartGridIndexSelected);
    }

    public void setSmartGridIndexSelected(int index) {
        smartGridIndexSelected = index;
    }

    public int getSmartGridIndexSelected() {
        return smartGridIndexSelected;
    }

    public void invertLayerState(int index) {
        renderLayers[index] = !renderLayers[index];
        //mainFrame.repaintMapDisplay();
    }

    public void setLayerState(int index, boolean enabled) {
        renderLayers[index] = enabled;
    }

    public void updateLayerThumbnail(int index) {
        mainFrame.getThumbnailLayerSelector().drawLayerThumbnail(index);
    }

    public void repaintThumbnailLayerSelector() {
        mainFrame.getThumbnailLayerSelector().repaint();
    }

    public Bdhc getBdhc() {
        return getCurrentMap().getBdhc();
    }

    public Bdhcam getBdhcam(){
        return getCurrentMap().getBdhcam();
    }

    public void setBdhcam(Bdhcam bdhcam){
        getCurrentMap().setBdhcam(bdhcam);
    }

    public void setBdhc(Bdhc bdhc) {
        getCurrentMap().setBdhc(bdhc);
    }

    public Backsound getBacksound() {
        return getCurrentMap().getBacksound();
    }

    public void setBacksound(Backsound backsound) {
        getCurrentMap().setBacksound(backsound);
    }

    public Collisions getCollisions() {
        return getCurrentMap().getCollisions();
    }

    public void setCollisions(Collisions collisions) {
        getCurrentMap().setCollisions(collisions);
    }

    public Collisions getCollisions2() {
        return getCurrentMap().getCollisions2();
    }

    public void setCollisions2(Collisions collisions) {
        getCurrentMap().setCollisions2(collisions);
    }

    public void setGameIndex(int game) {
        this.game.gameSelected = game;
    }

    public int getGameIndex() {
        return game.gameSelected;
    }

    public Game getGame() {
        return game;
    }

    public StateHandler getMapStateHandler() {
        return mapStateHandler;
    }

    public void addMapState(MapLayerState state) {
        mapStateHandler.addState(state);
        mainFrame.getUndoButton().setEnabled(true);
        mainFrame.getRedoButton().setEnabled(false);
    }

    public void resetMapStateHandler() {
        mapStateHandler = new StateHandler();
    }

    public void setMapStateHandler(StateHandler mapStateHandler) {
        this.mapStateHandler = mapStateHandler;
    }

    public boolean isLayerChanged() {
        return layerChanged;
    }

    public void setLayerChanged(boolean layerChanged) {
        this.layerChanged = layerChanged;
    }

    public void moveSelectedSmartGridUp() {
        if (smartGridIndexSelected > 0) {
            Collections.swap(tset.getSmartGridArray(), smartGridIndexSelected, smartGridIndexSelected - 1);
            smartGridIndexSelected--;
        }
    }

    public void moveSelectedSmartGridDown() {
        if (smartGridIndexSelected < tset.getSmartGridArray().size() - 1) {
            Collections.swap(tset.getSmartGridArray(), smartGridIndexSelected, smartGridIndexSelected + 1);
            smartGridIndexSelected++;
        }
    }

    public boolean mapHasVertexColors() {
        //Get indices of tiles used in map
        MapGrid grid = getGrid();
        TreeSet<Integer> tileIndicesInGrid = new TreeSet();
        for (int i = 0; i < grid.tileLayers.length; i++) {
            for (int j = 0; j < grid.tileLayers[i].length; j++) {
                for (int k = 0; k < grid.tileLayers[i][j].length; k++) {
                    int tileIndex = grid.tileLayers[i][j][k];
                    if (tileIndex != -1) {
                        tileIndicesInGrid.add(tileIndex);
                    }
                }
            }
        }

        //Get indices of materials used in tiles
        TreeSet<Integer> materialIndicesInGrid = new TreeSet();
        for (Integer tileIndex : tileIndicesInGrid) {
            Tile tile = tset.get(tileIndex);
            for (Integer materialIndex : tile.getTextureIDs()) {
                materialIndicesInGrid.add(materialIndex);
            }
        }

        //Check if materials in map use vertex colors
        for (Integer materialIndex : materialIndicesInGrid) {
            if (tset.getMaterial(materialIndex).vertexColorsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public void updateAllMapThumbnails() {
        for (HashMap.Entry<Point, MapData> map : mapMatrix.getMatrix().entrySet()) {
            map.getValue().updateMapThumbnail();
        }
    }

    public void updateMapThumbnails(Set<Point> mapsCoords) {
        for (Point mapCoords : mapsCoords) {
            MapData data = mapMatrix.getMap(mapCoords);
            if (data != null) {
                data.updateMapThumbnail();
            }
        }
    }

    public void setBorderMapsTileset(Tileset borderMapTileset) {
        this.borderMapTileset = borderMapTileset;
    }

    public Tileset getBorderMapsTileset() {
        return borderMapTileset;
    }

    public BorderMapsGrid getBorderMapsGrid() {
        return borderMapsGrid;
    }

    public void setBorderMapsGrid(BorderMapsGrid borderMapsGrid) {
        this.borderMapsGrid = borderMapsGrid;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getMapName() {
        return Utils.removeExtensionFromPath(new File(getMapMatrix().filePath).getName());
    }

    public BuildFile getBuildings() {
        return getCurrentMap().getBuildings();
    }

    public void setBuildings(BuildFile buildings) {
        this.getCurrentMap().setBuildings(buildings);
    }

    public MapMatrix getMapMatrix() {
        return mapMatrix;
    }

    public void setMapSelected(Point mapCoords, boolean updateScrollbars) {
        this.mapSelected = mapCoords;

        mainFrame.getThumbnailLayerSelector().drawAllLayerThumbnails();
        mainFrame.getThumbnailLayerSelector().repaint();

        mainFrame.getMapMatrixDisplay().repaint();
        if (updateScrollbars) {
            mainFrame.updateMapMatrixDisplayScrollBars();
        }

        mainFrame.updateViewMapInfo();
    }

    public void setMapSelected(Point mapCoords) {
        setMapSelected(mapCoords, true);
    }


    public void setDefaultMapSelected() {
        if (mapMatrix.getMatrix().isEmpty()) { //|| mapMatrix.getMatrix().get(new Point(0, 0)) == null) {
            mapSelected = new Point(0, 0);
        } else {
            mapSelected = mapMatrix.getMatrix().keySet().iterator().next();
        }
    }

    public boolean mapExists(Point mapCoords) {
        return mapMatrix.getMatrix().keySet().contains(mapCoords);
    }

    public boolean mapSelectedExists() {
        return mapExists(mapSelected);
    }

    public Point getMapSelected() {
        return mapSelected;
    }

    public MapData getMapData() {
        return mapMatrix.getMapAndCreate(mapSelected);
    }

    public void setMapMatrix(MapMatrix mapMatrix) {
        this.mapMatrix = mapMatrix;
        mapSelected = new Point(0, 0);
    }

    public boolean useRealTimePostProcessing() {
        return realTimePostProcessing;
    }

    public void setRealTimePostProcessing(boolean enabled) {
        this.realTimePostProcessing = enabled;
    }

    public void clearLayer(int index) {
        addMapState(new MapLayerState("Clear Layer", this));
        getGrid().clearLayer(index);
        mainFrame.getThumbnailLayerSelector().drawLayerThumbnail(index);
        mainFrame.getThumbnailLayerSelector().repaint();
        mainFrame.getMapDisplay().updateMapLayerGL(index);
        mainFrame.getMapDisplay().repaint();
    }

    public void pasteLayer(int index) {
        if (getTileset().size() > 0) {
            if (tileLayerCopy != null && heightLayerCopy != null) {
                addMapState(new MapLayerState("Paste Tile and Height Layer", this));
                pasteTileLayer(index);
                pasteHeightLayer(index);
                mainFrame.getMapDisplay().updateMapLayerGL(index);
                mainFrame.getMapDisplay().repaint();
                updateLayerThumbnail(index);
                mainFrame.getThumbnailLayerSelector().repaint();
            }
        }
    }

    public void pasteLayerTiles(int index) {
        if (getTileset().size() > 0) {
            if (tileLayerCopy != null) {
                addMapState(new MapLayerState("Paste Tile Layer", this));
                pasteTileLayer(index);
                mainFrame.getMapDisplay().updateMapLayerGL(index);
                mainFrame.getMapDisplay().repaint();
                updateLayerThumbnail(index);
                mainFrame.getThumbnailLayerSelector().repaint();
            }
        }
    }

    public void pasteLayerHeights(int index) {
        if (getTileset().size() > 0) {
            if (heightLayerCopy != null) {
                addMapState(new MapLayerState("Paste Height Layer", this));
                pasteHeightLayer(index);
                mainFrame.getMapDisplay().updateMapLayerGL(index);
                mainFrame.getMapDisplay().repaint();
                updateLayerThumbnail(index);
                mainFrame.getThumbnailLayerSelector().repaint();
            }
        }
    }


    public void copySelectedLayer() {
        copyLayer(getActiveLayerIndex());
    }

    public void pasteTileLayer() {
        pasteTileLayer(getActiveLayerIndex());
    }

    public void pasteHeightLayer() {
        pasteHeightLayer(getActiveLayerIndex());
    }

    public void copyLayer(int index) {
        tileLayerCopy = getGrid().cloneTileLayer(index);
        heightLayerCopy = getGrid().cloneHeightLayer(index);
    }

    public void pasteTileLayer(int index) {
        if (tileLayerCopy != null) {
            getGrid().tileLayers[index] = getGrid().cloneLayer(tileLayerCopy);
        }
    }

    public void pasteHeightLayer(int index) {
        if (heightLayerCopy != null) {
            getGrid().heightLayers[index] = getGrid().cloneLayer(heightLayerCopy);
        }
    }

    public int[][] getTileLayerCopy() {
        return tileLayerCopy;
    }

    public int[][] getHeightLayerCopy() {
        return heightLayerCopy;
    }

    public void clearCopyLayer() {
        tileLayerCopy = null;
        heightLayerCopy = null;
    }

    public void indexOfTileVisualData() {

    }
}
