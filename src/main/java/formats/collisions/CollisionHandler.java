
package formats.collisions;

import editor.game.Game;
import editor.handler.MapEditorHandler;
import editor.state.CollisionLayerState;
import editor.state.StateHandler;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo
 */
public class CollisionHandler {

    private MapEditorHandler handler;
    private CollisionsEditorDialog dialog;
    private int indexLayerSelected = 0;
    private int indexCollisionSelected = 0;
    private final CollisionTypes collisionTypes;

    private int indexCollisionFileSelected = 0;
    private boolean lockTerrainLayers = true;

    private boolean isLayerChanged = false;
    private StateHandler collisionStateHandler = new StateHandler();
    //private int numLayers = 2;

    public CollisionHandler(MapEditorHandler handler, CollisionsEditorDialog dialog) {
        this.handler = handler;
        this.dialog = dialog;
        collisionTypes = new CollisionTypes(handler.getGameIndex());
        /*
        collisionTypes = new CollisionTypes("colors/CollisionsColors.txt", handler.getCollisions().getNumLayers());*/
    }

    public String getCollisionNameSelected() {
        return collisionTypes.getCollisionName(indexLayerSelected, indexCollisionSelected);
    }

    public void incrementCollisionSelected(int delta) {
        int newCollIndex = indexCollisionSelected + delta;
        if (newCollIndex >= 0 && newCollIndex < Collisions.cols * Collisions.rows) {
            indexCollisionSelected = newCollIndex;
        }
    }

    public CollisionTypes getCollisionTypes() {
        return collisionTypes;
    }

    public BufferedImage getImage(int layer, int index) {
        return collisionTypes.getImage(layer, index);
    }

    public byte[][] getLayer(int index) {
        return getCollisions().getLayer(index);
    }

    public byte[][] getLayerSelected() {
        return getCollisions().getLayer(indexLayerSelected);
    }

    public int getIndexCollisionSelected() {
        return indexCollisionSelected;
    }

    public void setIndexCollisionSelected(int index) {
        indexCollisionSelected = index;
    }

    public BufferedImage getImage(int index) {
        return collisionTypes.getImage(indexLayerSelected, index);
    }

    public int getNumLayers() {
        return getCollisions().getNumLayers();
    }

    public int getIndexLayerSelected() {
        return indexLayerSelected;
    }

    public void setIndexLayerSelected(int index) {
        this.indexLayerSelected = index;
    }

    public void setValue(int value, int layer, int x, int y) {
        getCollisions().setValue(value, layer, x, y);
    }

    public void setValue(int value, int x, int y) {
        getCollisions().setValue(value, indexLayerSelected, x, y);
    }

    public int getValue(int layer, int x, int y) {
        return getCollisions().getValue(layer, x, y);
    }

    public int getValue(int x, int y) {
        return getCollisions().getValue(indexLayerSelected, x, y);
    }

    public Color getFillColor(int layerIndex, int collIndex) {
        return collisionTypes.getFillColor(layerIndex, collIndex);
    }

    public int getGame() {
        return handler.getGameIndex();
    }

    public CollisionsEditorDialog getDialog() {
        return dialog;
    }

    public byte[][] cloneLayer(int index) {
        return getCollisions().cloneLayer(index);
    }

    public void setLayer(int index, byte[][] layer) {
        this.getCollisions().setLayer(index, layer);
    }

    public void addLayerState(CollisionLayerState state) {
        collisionStateHandler.addState(state);
        dialog.getUndoButton().setEnabled(true);
        dialog.getRedoButton().setEnabled(false);
    }

    public void resetMapStateHandler() {
        collisionStateHandler = new StateHandler();
    }

    public StateHandler getCollisionStateHandler() {
        return collisionStateHandler;
    }

    public boolean isLayerChanged() {
        return isLayerChanged;
    }

    public void setLayerChanged(boolean isLayerChanged) {
        this.isLayerChanged = isLayerChanged;
    }

    public void setCollisions(Collisions collisions){
        if(indexCollisionFileSelected == 0){
            handler.setCollisions(collisions);
        }else{
            handler.setCollisions2(collisions);
        }
    }

    public Collisions getCollisions(){
        if(indexCollisionFileSelected == 0){
            return handler.getCollisions();
        }else{
            return handler.getCollisions2();
        }
    }

    public void setIndexCollisionFileSelected(int index){
        this.indexCollisionFileSelected = index;
    }

    public boolean lockTerrainLayers() {
        return lockTerrainLayers;
    }

    public void setLockTerrainLayersEnabled(boolean enabled){
        this.lockTerrainLayers = enabled;
    }

    public boolean canEditLayer(int index){
        return !Game.isGenV(getGame()) || !lockTerrainLayers || indexLayerSelected > 3;
    }
}
