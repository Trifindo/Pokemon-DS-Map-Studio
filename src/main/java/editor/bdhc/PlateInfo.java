/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bdhc;

/**
 *
 * @author Trifindo
 */
public class PlateInfo implements Comparable{
    
    public int plateIndex;
    public int y;
    
    public PlateInfo(int plateIndex, int y){
        this.plateIndex = plateIndex;
        this.y = y;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.y, ((PlateInfo)o).y);
    }

    
}
