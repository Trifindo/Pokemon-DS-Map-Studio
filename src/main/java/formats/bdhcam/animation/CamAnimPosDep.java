package formats.bdhcam.animation;

import formats.bdhcam.BdhcamHandler;
import formats.bdhcam.camplate.CameraSettings;
import formats.bdhcam.camplate.Camplate;
import formats.bdhcam.camplate.CamplatePosDep;
import editor.handler.MapEditorHandler;

public class CamAnimPosDep extends CamAnimator{

    private final CamplatePosDep plate;
    private int distanceGoal = 1;
    private float distMoved = 0.0f;
    private final float increment = 0.05f;

    public CamAnimPosDep(MapEditorHandler handler, BdhcamHandler bdhcamHandler, CamplatePosDep plate) {
        super(handler, bdhcamHandler);
        this.plate = plate;
        if(plate.type.ID == Camplate.Type.POS_DEPENDENT_X.ID){
            distanceGoal = plate.width;
            bdhcamHandler.setPlayerX(plate.x);
            bdhcamHandler.setPlayerY((int)((2 * plate.y + plate.height)/2));
        }else{
            distanceGoal = plate.height;
            bdhcamHandler.setPlayerX((int)((2 * plate.x + plate.width)/2));
            bdhcamHandler.setPlayerY(plate.y);
        }
    }

    @Override
    protected void updateLogic() {
        float[] playerPos = new float[]{
                bdhcamHandler.getPlayerX() - 16.0f,
                bdhcamHandler.getPlayerY() - 16.0f,
                0.0f};
        bdhcamHandler.getDialog().getBdhcamDisplay().setCamera(new CameraSettings(plate, distMoved / distanceGoal, playerPos));
        if(plate.type.ID == Camplate.Type.POS_DEPENDENT_X.ID){
            bdhcamHandler.setPlayerX(bdhcamHandler.getPlayerX() + increment);
        }else{
            bdhcamHandler.setPlayerY(bdhcamHandler.getPlayerY() + increment);
        }
        distMoved += increment;
        if(distMoved >= distanceGoal){
            running = false;
        }
    }

    @Override
    protected void repaint() {
        bdhcamHandler.getDialog().getBdhcamDisplay().repaint();
        bdhcamHandler.getDialog().getPlatesDisplay().repaint();
    }
}
