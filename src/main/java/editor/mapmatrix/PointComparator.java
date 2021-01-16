package editor.mapmatrix;

import java.awt.*;
import java.util.Comparator;

public class PointComparator implements Comparator<Point> {

    @Override
    public int compare(Point o1, Point o2) {
        int cmpX = Double.compare(o1.getX(), o2.getX());
        int cmpY = Double.compare(o1.getY(), o2.getY());
        return (cmpX == 0 ? cmpY : cmpX);
    }
}
