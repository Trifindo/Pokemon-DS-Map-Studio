
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
