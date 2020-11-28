package formats.bdhcam.camplate;

public class CamplatePosDep extends Camplate{

    public CamplatePosDep(int x, int y, int z, int width, int height, int type, int numParams, boolean useZ) {
        super(x, y, z, width, height, type, numParams, useZ);
    }

    public CamplatePosDep(){
        super(16, 16, 0, 2, 2, Type.POS_DEPENDENT_X.ID, 0, false);
    }

    public CamplatePosDep(Camplate other, int type, int numPlates){
        super(other, type, numPlates);
    }

    @Override
    public void addParameter() {
        parameters.add(new CamParameterPosDep(CamParameter.Type.CAMERA_X,0,0));//TODO: Improve this

    }
}
