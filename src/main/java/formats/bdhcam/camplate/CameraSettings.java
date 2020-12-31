package formats.bdhcam.camplate;

public class CameraSettings {

    public static final float[] defaultValues = {
            0.0f, 13.0f, 14.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f
    };
    public float[] values = new float[defaultValues.length];

    public CameraSettings(){
        System.arraycopy(defaultValues, 0, values, 0, defaultValues.length);
    }

    public CameraSettings(float x, float y, float z,
                          float targetX, float targetY, float targetZ,
                          float upX, float upY, float upZ) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = targetX;
        values[4] = targetY;
        values[5] = targetZ;
        values[6] = upX;
        values[7] = upY;
        values[8] = upZ;

    }

    public CameraSettings(CamplatePosIndep plate, int frame){
        moveCameraToPlate(plate);

        //Apply camera parameters
        for (CamParameter param : plate.parameters) {
            if(param.type.camParamIndex != -1) {
                CamParameterPosIndep paramPosIndep = (CamParameterPosIndep) param;
                float weight = Math.min((float) frame / paramPosIndep.duration, 1.0f);
                values[param.type.camParamIndex] += param.getWeightedValue(weight)*1.0; 
            }
        }
    }

    public CameraSettings(CamplatePosDep plate, float weight, float[] playerPos) {
        moveCameraToPlayer(plate, playerPos);

        //Apply camera parameters
        for (CamParameter param : plate.parameters) {
            if(param.type.camParamIndex !=-1){
                values[param.type.camParamIndex] += param.getWeightedValue(weight)*1.0;
            }
        }

    }

    public CameraSettings(Camplate plate, float weight) {
        moveCameraToPlate(plate);

        //Apply camera parameters
        for (CamParameter param : plate.parameters) {
            if(param.type.camParamIndex != -1) {
                values[param.type.camParamIndex] += param.getWeightedValue(weight)*1.0;
            }
        }

    }

    private void moveCameraToPlayer(Camplate plate, float[] playerPos){
        //Copy default values
        System.arraycopy(defaultValues, 0, values, 0, defaultValues.length);

        //Move camera to player position
        for (int i = 0; i < 6; i++) {
            values[i] += playerPos[i % 3];
        }

        if(plate.useZ){
            values[2] += plate.z;
            values[5] += plate.z;
        }
    }

    private void moveCameraToPlate(Camplate plate){
        //Copy default values
        System.arraycopy(defaultValues, 0, values, 0, defaultValues.length);

        //Move camera to center of plate
        float[] plateCenter = plate.getCenterInWorld();
        for (int i = 0; i < 6; i++) {
            values[i] += plateCenter[i % 3];
        }

        if(plate.useZ){
            values[2] += plate.z;
            values[5] += plate.z;
        }
    }

}


