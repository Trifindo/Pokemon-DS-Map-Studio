
package editor.state;

import formats.collisions.CollisionHandler;

/**
 * @author Trifindo
 */
public class CollisionLayerState extends State {

    private CollisionHandler collisionHandler;
    private int layerIndex;
    private byte[][] layer;

    public CollisionLayerState(String name, CollisionHandler collisionHandler) {
        super(name);
        this.collisionHandler = collisionHandler;

        layerIndex = collisionHandler.getIndexLayerSelected();
        layer = collisionHandler.cloneLayer(layerIndex);
    }

    @Override
    public void revertState() {
        collisionHandler.setLayer(layerIndex, layer);
    }

    public int getLayerIndex() {
        return layerIndex;
    }


}
