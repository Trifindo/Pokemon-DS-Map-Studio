package editor.mapgroups;

import java.awt.*;
import java.util.TreeSet;

public class MapGroup implements Comparable<MapGroup>{
    private final int index;
    private final TreeSet<Point> coordList = new TreeSet<>((o1, o2) -> {
        int cmpX = Double.compare(o1.getX(), o2.getX());
        int cmpY = Double.compare(o1.getY(), o2.getY());
        return (cmpX == 0 ? cmpY : cmpX);
    });

    //Add the first element
    public MapGroup(int index, Point p) {
        this.index = index;
        coordList.add(p);
    }

    public int getIndex() {
        return index;
    }

    public TreeSet<Point> getCoordList() {
        return coordList;
    }

    @Override
    public String toString() {
        String msg = "#" + index + ":   ";
        for (Point p : coordList) {
            msg += (( "(" + (int) p.getX()) + ", " + (int) p.getY() + "); ");
        }
        System.out.println(msg);
        return msg;
    }


    @Override
    public int compareTo(MapGroup o) {
        return Integer.compare(this.index, o.index);
    }
}
