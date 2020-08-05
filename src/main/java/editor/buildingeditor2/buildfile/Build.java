/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2.buildfile;

/**
 *
 * @author Trifindo
 */
public class Build {
    
    public static final int dataSize = 48;
     
    private int modelID;
    private float x, y, z;
    private float scaleX, scaleY, scaleZ;
    
    public Build(){
        this.modelID = 0;
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleZ = 1.0f;
    }
    
    public Build(int modelID, float x, float y, float z, float scaleX, float scaleY, float scaleZ){
        this.modelID = modelID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public int getModeID() {
        return modelID;
    }

    public void setModelID(int modelIndex) {
        this.modelID = modelIndex;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public void setScaleZ(float scaleZ) {
        this.scaleZ = scaleZ;
    }
    
    
    
}
