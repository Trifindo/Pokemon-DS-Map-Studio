package formats.bdhcam;

import formats.bdhcam.camplate.*;

import java.util.ArrayList;
import java.util.List;

public class Bdhcam {

    public static final String fileExtension = "bdhcam";
    private final List<Camplate> plates;

    public Bdhcam(){
        this(0);
    }

    public Bdhcam(int numPlates) {
        plates = new ArrayList<>(numPlates);
    }

    public List<Camplate> getPlates() {
        return plates;
    }

    public Camplate getPlate(int index) {
        return plates.get(index);
    }

    public void changePlateType(int index, int type) {
        if (plates.size() <= 0 || index < 0 || index >= plates.size()) {
            return;
        }

        Camplate p = plates.get(index);
        if (p.type.ID == type) {
            return;
        }
        if (p.type.ID == Camplate.Type.POS_INDEPENDENT.ID) {
            plates.set(index, new CamplatePosDep(p, type, 0));
            for(CamParameter param : p.parameters){
                plates.get(index).parameters.add(new CamParameterPosDep(param.type, 0, 0));
            }
        } else {
            if (type == Camplate.Type.POS_INDEPENDENT.ID) {
                plates.set(index, new CamplatePosIndep(p, type, 0));
                for(CamParameter param : p.parameters){
                    plates.get(index).parameters.add(new CamParameterPosIndep(param.type, 1, 0));
                }
            } else {
                CamplatePosDep newPlate = new CamplatePosDep(p, type, 0);
                newPlate.parameters = p.parameters;
                plates.set(index, newPlate);
            }
        }
    }

    public int getNumValidPlates(){
        return (int) plates.stream()
                .filter(plate -> plate.parameters.size() > 0)
                .count();
    }
}
