
package editor.bdhc;

/**
 * @author Trifindo
 */
public class Slope {
    public int x, y, z;

    public Slope(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Slope(int[] slopes) {
        this.x = slopes[0];
        this.y = slopes[1];
        this.z = slopes[2];
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        hash = 97 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Slope other = (Slope) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }


}
