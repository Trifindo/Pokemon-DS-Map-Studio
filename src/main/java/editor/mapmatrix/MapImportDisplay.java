package editor.mapmatrix;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import editor.handler.MapData;
import editor.handler.MapEditorHandler;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Trifindo, JackHack96
 */
public class MapImportDisplay extends JPanel {

    private MapEditorHandler handler;

    private BufferedImage mapsImg;

    private Point mapMin = new Point();
    private Point mapMax = new Point();
    private Dimension mapSize;
    private Point newMapMin = new Point();
    private Point newMapPos = new Point();
    private Dimension newMapSize = new Dimension();
    private HashMap<Point, MapData> maps;

    private Point globalMin = new Point();

    private float scale = 0.5f;

    private Point lastMouse = new Point();

    public MapImportDisplay() {
        initComponents();
    }

    public void init(MapEditorHandler handler, HashMap<Point, MapData> maps) {
        this.handler = handler;
        this.maps = maps;


        mapMin = handler.getMapMatrix().getMinCoords();
        mapMax = handler.getMapMatrix().getMaxCoords();
        mapSize = handler.getMapMatrix().getMatrixSize();

        newMapMin = MapMatrix.getMinCoords(maps);
        newMapSize = MapMatrix.getMatrixSize(maps);

        updateSize();

        //this.importMatrixMin = getMinCoords(maps);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transform = g2d.getTransform();

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));//RenderingHints.VALUE_INTERPOLATION_BILINEAR));

        g2d.scale(scale, scale);

        if (handler != null) {
            if (mapsImg != null) {
                g.drawImage(mapsImg,
                        - globalMin.x * MapData.mapThumbnailSize,
                        - globalMin.y * MapData.mapThumbnailSize,
                        null);
            }

            if (maps != null) {
                for (Point p : maps.keySet()) {
                    int x = p.x - newMapMin.x + newMapPos.x - globalMin.x - mapMin.x;
                    int y = p.y - newMapMin.y + newMapPos.y - globalMin.y - mapMin.y;

                    g2d.setColor(new Color(1.0f, 0.0f, 0.0f, 0.3f));
                    g2d.fillRect(
                            x * MapData.mapThumbnailSize,
                            y * MapData.mapThumbnailSize,
                            MapData.mapThumbnailSize - 1, MapData.mapThumbnailSize - 1);

                    g2d.setColor(Color.red);
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRect(
                            x * MapData.mapThumbnailSize - 3,
                            y * MapData.mapThumbnailSize - 3,
                            MapData.mapThumbnailSize + 6, MapData.mapThumbnailSize + 6);
                    g2d.setStroke(new BasicStroke(1));

                    g2d.setColor(Color.white);
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRect(
                            x * MapData.mapThumbnailSize,
                            y * MapData.mapThumbnailSize,
                            MapData.mapThumbnailSize - 1, MapData.mapThumbnailSize - 1);
                }
            }
        }

        g2d.setTransform(transform);
    }

    public void updateMapsImage() {
        this.mapsImg = handler.getMapMatrix().getMapMatrixImage();
        repaint();
    }

    public void updateSize() {
        globalMin = new Point(Math.min(newMapPos.x - mapMin.x, 0), Math.min(newMapPos.y - mapMin.y, 0));
        Point globalMax = new Point(Math.max(mapSize.width, newMapPos.x + newMapSize.width), Math.max(mapSize.height, newMapPos.y + newMapSize.height));

        this.setPreferredSize(new Dimension(
                (int) ((globalMax.x - globalMin.x) * MapData.mapThumbnailSize * scale),
                (int) ((globalMax.y - globalMin.y) * MapData.mapThumbnailSize * scale)));

        revalidate();
    }

    public void moveMap(Point dp) {
        Point newPos = new Point(newMapPos.x + dp.x, newMapPos.y + dp.y);
        this.newMapPos = newPos;

        updateSize();
    }

    public Point getNewMapPos() {
        return newMapPos;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
