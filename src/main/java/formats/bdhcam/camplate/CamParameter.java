package formats.bdhcam.camplate;

import java.awt.*;

public abstract class CamParameter{

    public static enum Type{
        UNKNOWN_1(0x00,"Unknown 1", Color.black, 0, -1),
        UNKNOWN_2(0x04,"Unknown 2", Color.black, 1,-1),
        UNKNOWN_3(0x08,"Unknown 3", Color.black, 2,-1),
        UNKNOWN_4(0x0C,"Unknown 4", Color.black, 3,-1),
        UNKNOWN_5(0x10,"Unknown 5", Color.black, 4,-1),
        CAMERA_X(0x14,"Camera X", Color.red, 5, 0),
        CAMERA_Y(0x1C,"Camera Y", Color.green, 6, 1),
        CAMERA_Z(0x18,"Camera Z", Color.blue, 7, 2),
        TARGET_X(0x20,"Target X", Color.red, 8, 3),
        TARGET_Y(0x28,"Target Y", Color.green, 9, 4),
        TARGET_Z(0x24,"Target Z", Color.blue, 10, 5),
        CAMERA_UP_X(0X2C, "Camera Up X",Color.red, 11, 6),
        CAMERA_UP_Y(0X34, "Camera Up Y",Color.green, 12, 7),
        CAMERA_UP_Z(0X30, "Camera Up Z",Color.blue, 13, 8),
        UNKNOWN_6(0x38,"Unknown 6", Color.black, 14,-1),
        UNKNOWN_7(0x3C,"Unknown 7", Color.black, 15,-1),
        UNKNOWN_8(0x40,"Unknown 8", Color.black, 16,-1),
        UNKNOWN_9(0x44,"Unknown 9", Color.black, 17,-1),
        TARGET_PREV_X(0x48, "Target Prev X",Color.black, 18,-1),
        TARGET_PREV_Y(0x50, "Target Prev Y",Color.black, 19,-1),
        TARGET_PREV_Z(0x4C, "Target Prev Z",Color.black, 20,-1);

        public final String name;
        public final Color color;
        public final int ID;
        public final int index;
        public final int camParamIndex;

        Type(int ID, String name, Color color, int index, int camParamIndex){
            this.name = name;
            this.color = color;
            this.ID = ID;
            this.index = index;
            this.camParamIndex = camParamIndex;
        }

        public static Type get(int ID){
            for(Type type : Type.values()){
                if(type.ID == ID){
                    return type;
                }
            }
            return UNKNOWN_1;
        }
    }

    public Type type;

    public CamParameter(Type type){
        this.type = type;
    }

    public abstract float getWeightedValue(float weight);

}
