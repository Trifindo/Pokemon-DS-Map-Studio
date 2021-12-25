package formats.collisions.bw;

public class CParam2D implements Comparable<CParam2D> {
    public final int ID;
    public final Float slopeX;
    public final Float slopeY;

    public CParam2D(int ID, Float slopeX, Float slopeY) {
        this.ID = ID;
        this.slopeX = slopeX;
        this.slopeY = slopeY;
    }

    @Override
    public int compareTo(CParam2D o) {
        return slopeX.compareTo(o.slopeX);
    }
}
