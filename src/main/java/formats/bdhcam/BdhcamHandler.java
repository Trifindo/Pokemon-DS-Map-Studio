package formats.bdhcam;

import formats.bdhcam.animation.CamAnimPosDep;
import formats.bdhcam.animation.CamAnimPosIndep;
import formats.bdhcam.animation.CamAnimator;
import formats.bdhcam.camplate.CamParameter;
import formats.bdhcam.camplate.Camplate;
import formats.bdhcam.camplate.CamplatePosDep;
import formats.bdhcam.camplate.CamplatePosIndep;
import editor.handler.MapEditorHandler;

public class BdhcamHandler {

    private MapEditorHandler handler;
    //private Bdhcam bdhcam;//Move this to Map Editor Handler
    private BdhcamEditorDialog dialog;
    private int indexSelected = 0;
    private int indexParamSelected = 0;

    private CamAnimator animator;

    private float playerX = 16.0f, playerY = 16.0f;

    public BdhcamHandler(MapEditorHandler handler, BdhcamEditorDialog dialog){
        this.handler = handler;
        this.dialog = dialog;
    }

    public Bdhcam getBdhcam() {
        return handler.getBdhcam();
    }

    public Camplate getSelectedPlate(){
        Bdhcam bdhcam = handler.getBdhcam();
        if(indexSelected < bdhcam.getPlates().size()){
            return bdhcam.getPlates().get(indexSelected);
        }
        return null;
    }

    public CamParameter getSelectedParameter(){
        Camplate plate = getSelectedPlate();
        if (plate != null && indexParamSelected < plate.parameters.size()) {
            return plate.parameters.get(indexParamSelected);
        }
        return null;
    }

    public void setSelectedPlate(int index){
        this.indexSelected = index;
        if(getSelectedPlate() != null){
            setPlayerInPlate(getSelectedPlate());
        }
    }

    public void setIndexParamSelected(int indexParamSelected) {
        this.indexParamSelected = indexParamSelected;
    }

    public int getIndexSelected(){
        return indexSelected;
    }

    public int getIndexParamSelected() {
        return indexParamSelected;
    }

    public BdhcamEditorDialog getDialog() {
        return dialog;
    }

    public float getPlayerX() {
        return playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public float getSelectedPlateZ(){
        Camplate plate = getSelectedPlate();
        return plate != null && plate.useZ ? plate.z : 0.0f;
    }

    public void setPlayerX(float playerX) {
        this.playerX = playerX;
    }

    public void setPlayerY(float playerY) {
        this.playerY = playerY;
    }

    public void setPlayerInPlate(Camplate plate){
        float[] center = plate.getCenter();
        playerX = (int)center[0];
        playerY = (int)center[1];
    }

    public CamAnimator getAnimator() {
        return animator;
    }

    public void startAnimation(){
        Bdhcam bdhcam = handler.getBdhcam();
        if(bdhcam.getPlates().size() > 0) {
            if (animator != null) {
                animator.finish();
            }

            if (getSelectedPlate().type.ID == Camplate.Type.POS_INDEPENDENT.ID) {
                animator = new CamAnimPosIndep(handler, this, (CamplatePosIndep) getSelectedPlate());
            }else{
                animator = new CamAnimPosDep(handler, this, (CamplatePosDep) getSelectedPlate());
            }
            animator.start();
        }
    }

    public void stopAnimation(){
        if (animator != null) {
            animator.finish();
        }
    }

    public void setBdhcam(Bdhcam bdhcam) {
        handler.setBdhcam(bdhcam);
    }
}
