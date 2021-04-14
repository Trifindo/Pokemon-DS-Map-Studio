package formats.bdhcam;

import formats.bdhcam.camplate.*;

import java.util.ArrayList;


public class Bdhcam {

    public static final String fileExtension = "bdhcam";
    private ArrayList<Camplate> plates;

    public Bdhcam(){
        this(0);
    }

    public Bdhcam(int numPlates) {
        plates = new ArrayList<>(numPlates);
    }

    public ArrayList<Camplate> getPlates() {
        return plates;
    }

    public Camplate getPlate(int index) {
        return plates.get(index);
    }
    
   

    public void changePlateType(int index, int type) {
        if (plates.size() > 0) {
            if (index >= 0 && index < plates.size()) {
                Camplate p = plates.get(index);
                if (p.type.ID != type) {
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
            }
        }
    }

    public int getNumValidPlates(){
        int count = 0;
        for(Camplate plate : plates){
            if(plate.parameters.size() > 0){
                count ++;
            }
        }
        return count;
    }

    public void duplicatePlate(Camplate toCopy) {
        Camplate newPlate;

        if (toCopy.type.ID == Camplate.Type.POS_INDEPENDENT.ID) {
            newPlate = new CamplatePosIndep(toCopy, toCopy.type.ID, 0);

            for (int i = 0; i < toCopy.parameters.size(); i++) {
                CamParameterPosIndep sourceparam = (CamParameterPosIndep) toCopy.parameters.get(i);
                newPlate.parameters.add(new CamParameterPosIndep(sourceparam.type, sourceparam.duration, sourceparam.finalValue));
            }
        } else {
            newPlate = new CamplatePosDep(toCopy, toCopy.type.ID, 0);

            for (int i = 0; i < toCopy.parameters.size(); i++) {
                CamParameterPosDep sourceparam = (CamParameterPosDep) toCopy.parameters.get(i);
                newPlate.parameters.add(new CamParameterPosDep(sourceparam.type, sourceparam.firstValue, sourceparam.secondValue));
            }

        }
        plates.add(newPlate);
    }
}
