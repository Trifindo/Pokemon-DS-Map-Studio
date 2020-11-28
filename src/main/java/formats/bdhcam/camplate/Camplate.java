package formats.bdhcam.camplate;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Trifindo
 */

public abstract class Camplate {

    public static enum Type {

        POS_INDEPENDENT(0, "Position independent", Color.orange),
        POS_DEPENDENT_X(1, "Position dependent X", Color.red),
        POS_DEPENDENT_Y(2, "Position dependent Y", Color.green);

        Type(int ID, String name, Color color) {
            this.ID = ID;
            this.name = name;
            this.color = color;
        }

        public static Type get(int ID) {
            for (Type type : values()) {
                if (type.ID == ID) {
                    return type;
                }
            }
            return POS_INDEPENDENT;
        }

        public final int ID;
        public final String name;
        public final Color color;
    }

    public int x, y;
    public int width, height;
    public Type type;

    public int z;
    public boolean useZ;
    public static final int useZCode = 0x80;

    public ArrayList<CamParameter> parameters;

    public Camplate(int x, int y, int z, int width, int height, int type, int numParameters, boolean useZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.useZ = useZ;
        this.width = width;
        this.height = height;
        this.type = Type.get(type);

        parameters = new ArrayList<>(numParameters);
    }

    public Camplate(Camplate other, int type, int numParams){
        this(other.x, other.y, other.z,  other.width, other.height, type, numParams, other.useZ);
    }

    public Color getFillColor() {
        Color c = type.color;
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 100);
    }

    public Color getBorderColor() {
        return type.color;
    }

    public float[] getCenterInWorld() {
        return new float[]{
                (2 * x + width) / 2.0f - 16.0f,
                (2 * y + height) / 2.0f - 16.0f,
                0.0f};
    }

    public float[] getCenter(){
        return new float[]{
                (2 * x + width) / 2.0f,
                (2 * y + height) / 2.0f,
                0.0f};
    }

    public abstract void addParameter();

    public void setType(int type){
        this.type = Type.values()[type];
        parameters = new ArrayList<>();
    }

}
