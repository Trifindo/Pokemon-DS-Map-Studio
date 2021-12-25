
package formats.bdhc;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class IndexedTriangle {

    public int pointInd1, pointInd2, pointInd3;
    public int slopeInd;
    public float coordZ;
    public int type;

    public IndexedTriangle(int pointInd1, int pointInd2, int pointInd3, int slopeInd, float coordZ, int type) {
        this.pointInd1 = pointInd1;
        this.pointInd2 = pointInd2;
        this.pointInd3 = pointInd3;
        this.slopeInd = slopeInd;
        this.coordZ = coordZ;
        this.type = type;
    }

    public int getMinX(ArrayList<BdhcPoint> points) {
        return Math.min(Math.min(points.get(pointInd1).x, points.get(pointInd2).x), points.get(pointInd3).x);
    }

    public int getMinY(ArrayList<BdhcPoint> points) {
        return Math.min(Math.min(points.get(pointInd1).y, points.get(pointInd2).y), points.get(pointInd3).y);
    }

    public int getMaxX(ArrayList<BdhcPoint> points) {
        return Math.max(Math.max(points.get(pointInd1).x, points.get(pointInd2).x), points.get(pointInd3).x);
    }

    public int getMaxY(ArrayList<BdhcPoint> points) {
        return Math.max(Math.max(points.get(pointInd1).y, points.get(pointInd2).y), points.get(pointInd3).y);
    }
}
