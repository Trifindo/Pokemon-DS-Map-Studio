package formats.bdhcam.camplate;

public class CamParameterPosDep extends CamParameter{

    public float firstValue;
    public float secondValue;


    public CamParameterPosDep(CamParameter.Type type, float firstValue, float secondValue){
        super(type);
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }


    @Override
    public float getWeightedValue(float weight) {
        return (secondValue - firstValue) * weight + firstValue;
    }
}
