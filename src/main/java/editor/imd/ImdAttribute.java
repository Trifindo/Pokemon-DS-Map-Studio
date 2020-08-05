/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.imd;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 *
 * @author Trifindo
 */
public class ImdAttribute {
    private static final float floatRound = 100000.0f;
    
    public String tag;
    public String value;
    
    public ImdAttribute(String tag){
        this.tag = tag;
    }
    
    public ImdAttribute(String tag, int value){
        this.tag = tag;
        setValue(value);
    }
    
    public ImdAttribute(String tag, boolean value){
        this.tag = tag;
        setValue(value);
    }
    
    public ImdAttribute(String tag, int[] values){
        this.tag = tag;
        setValue(values);
    }
    
    public ImdAttribute(String tag, float value){
        this.tag = tag;
        setValue(value);
    }
    
     public ImdAttribute(String tag, float[] values){
        this.tag = tag;
        setValue(values);
    }
    
      public ImdAttribute(String tag, String value){
        this.tag = tag;
        setValue(value);
    }
     
    public void setValue(int value){
        this.value = String.valueOf(value);
    }
    
    public void setValue(int[] values){
        this.value = "";
        for(int i = 0; i < values.length - 1; i++){
            this.value += String.valueOf(values[i]) + " ";
        }
        this.value += String.valueOf(values[values.length - 1]);
    }
    
    public void setValue(float value){
        this.value = String.format(Locale.US, "%.6f", round(value));
    }
    
    public void setValue(float[] values){
        this.value = "";
        for(int i = 0; i < values.length - 1; i++){
            this.value += String.format(Locale.US, "%.6f", round(values[i])) + " ";
        }
        this.value += String.format(Locale.US, "%.6f", round(values[values.length - 1]));
    }
    
    public float round(float f){
        return Math.round(f * floatRound) / floatRound;
    }
    
    public void setValue(boolean value){
        this.value = value ? "on" : "off";
    }
    
    public void setValue(String value){
        this.value = value;
    }
    
}
