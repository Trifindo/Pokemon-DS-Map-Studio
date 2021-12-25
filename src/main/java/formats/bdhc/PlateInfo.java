
package formats.bdhc;

/**
 * @author Trifindo
 */
public class PlateInfo implements Comparable<PlateInfo> {

    public int plateIndex;
    public int y;

    public PlateInfo(int plateIndex, int y) {
        this.plateIndex = plateIndex;
        this.y = y;
    }

    @Override
    public int compareTo(PlateInfo o) {
        return Integer.compare(this.y, o.y);
    }
}
