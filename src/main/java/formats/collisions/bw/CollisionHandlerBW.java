package formats.collisions.bw;

import editor.handler.MapEditorHandler;
import formats.collisions.Collisions;

public class CollisionHandlerBW {

    private MapEditorHandler handler;
    private CollisionsBW3D[] collisionFiles = new CollisionsBW3D[2];
    private CollisionsEditorDialogBW dialog;
    private int selectedCollisionFile = 0;

    private float[] currentTile = new float[4];

    public CollisionHandlerBW(MapEditorHandler handler, CollisionsEditorDialogBW dialog){
        this.handler = handler;
        this.dialog = dialog;

        try {
            collisionFiles[0] = new CollisionsBW3D(handler.getCollisions().toByteArray(), handler.getGameIndex());
        }catch(Exception e){
            collisionFiles[0] = null; //TODO: Make a default constructor?
        }

        try {
            collisionFiles[1] = new CollisionsBW3D(handler.getCollisions2().toByteArray(), handler.getGameIndex());
        }catch(Exception e){
            collisionFiles[1] = null; //TODO: Make a default constructor?
        }
        /*
        //TEMP:
        try {
            //collisionsBW3D =  new CollisionsBW3D("D:\\Mis cosas\\RH\\SDSME181\\Permisos_ciudad_BW2.per");
            //collisionsBW3D =  new CollisionsBW3D("D:\\Mis cosas\\RH\\SDSME181\\26_BW2.per");
            collisionFiles[0] =  new CollisionsBW3D("D:\\Mis cosas\\RH\\SDSME181\\99_BW2.per");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void saveToCollision() throws Exception {
        handler.setCollisions(new Collisions(collisionFiles[0].toByteArray()));
        handler.setCollisions2(new Collisions(collisionFiles[1].toByteArray()));
    }

    public CollisionsEditorDialogBW getDialog() {
        return dialog;
    }

    public float[] getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(float[] currentTile) {
        this.currentTile = currentTile;
    }

    public void moveCurrentTile(float dz){
        for(int i = 0; i < currentTile.length; i++){
            currentTile[i] += dz;
        }
    }

    public void rotateCurrentTile(){
        float temp = currentTile[0];
        for(int i = 0; i < currentTile.length - 1; i++){
            currentTile[i] = currentTile[i + 1];
        }
        currentTile[currentTile.length - 1] = temp;
    }

    public void flipCurrentTile(){
        float temp = currentTile[0];
        currentTile[0] = currentTile[1];
        currentTile[1] = temp;

        temp = currentTile[2];
        currentTile[2] = currentTile[3];
        currentTile[3] = temp;
    }

    public CollisionsBW3D getCollisionsBW3D() {
        return collisionFiles[selectedCollisionFile];
    }

    public void setCollisionsBW3D(CollisionsBW3D collisionsBW3D) {
        this.collisionFiles[selectedCollisionFile] = collisionsBW3D;
    }

    public void setSelectedCollisionFile(int index){
        this.selectedCollisionFile = index;
    }
}


