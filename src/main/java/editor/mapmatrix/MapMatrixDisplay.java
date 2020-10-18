package editor.mapmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import editor.grid.MapGrid;
import editor.handler.MapData;
import editor.handler.MapEditorHandler;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Trifindo, JackHack96
 */
public class MapMatrixDisplay extends JPanel {

    private MapEditorHandler handler;

    private BufferedImage mapsImg;

    private Dimension matrixSize;
    private Point matrixMin = new Point();

    private float scale = 0.5f;

    public MapMatrixDisplay() {
        initComponents();
    }

    private void formMousePressed(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        //int mapX = Math.floorDiv((int) Math.floor(x * scale), MapData.mapThumbnailSize);
        //int mapY = Math.floorDiv((int) Math.floor(y * scale), MapData.mapThumbnailSize);
        int mapX = Math.floorDiv(x, (int) (MapData.mapThumbnailSize * scale));
        int mapY = Math.floorDiv(y, (int) (MapData.mapThumbnailSize * scale));
        Point mapCoords = new Point(mapX + matrixMin.x, mapY + matrixMin.y);
        //System.out.println("Point pressed: " + mapCoords.x + " " + mapCoords.y);

        if (handler != null) {
            Set<Point> maps = handler.getMapMatrix().getMatrix().keySet();
            if (maps.contains(mapCoords)) {
                //System.out.println("Map selected: " + mapCoords.x + " " + mapCoords.y);

                if (!mapCoords.equals(handler.getMapSelected())) {
                    handler.setMapSelected(mapCoords, false);
                    handler.getMainFrame().getMapDisplay().setCameraAtMap(mapCoords);

                    handler.getMainFrame().getMapDisplay().repaint();
                } else {
                    handler.getMainFrame().getMapDisplay().setCameraAtMap(mapCoords);
                    handler.getMainFrame().getMapDisplay().repaint();
                }

                repaint();
            }
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //g.setColor(Color.darkGray);
        //g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transform = g2d.getTransform();

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));//RenderingHints.VALUE_INTERPOLATION_BILINEAR));

        g2d.scale(scale, scale);

        if (handler != null) {
            if (mapsImg != null) {
                g.drawImage(mapsImg, 0, 0, null);
            }

            Point selectedMap = handler.getMapSelected();
            g2d.setColor(new Color(1.0f, 0.0f, 0.0f, 0.2f));
            g2d.fillRect(
                    (selectedMap.x - matrixMin.x) * MapData.mapThumbnailSize,
                    (selectedMap.y - matrixMin.y) * MapData.mapThumbnailSize,
                    MapData.mapThumbnailSize - 1, MapData.mapThumbnailSize - 1);

            g2d.setColor(Color.white);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(
                    (selectedMap.x - matrixMin.x) * MapData.mapThumbnailSize,
                    (selectedMap.y - matrixMin.y) * MapData.mapThumbnailSize,
                    MapData.mapThumbnailSize - 1, MapData.mapThumbnailSize - 1);

            g2d.setColor(Color.red);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(
                    (selectedMap.x - matrixMin.x) * MapData.mapThumbnailSize - 3,
                    (selectedMap.y - matrixMin.y) * MapData.mapThumbnailSize - 3,
                    MapData.mapThumbnailSize + 6, MapData.mapThumbnailSize + 6);
            g2d.setStroke(new BasicStroke(1));

        }

        g2d.setTransform(transform);
    }

    public void updateMapsImage() {
        this.mapsImg = handler.getMapMatrix().getMapMatrixImage();
        repaint();
    }

    public void updateSize() {
        this.matrixMin = handler.getMapMatrix().getMinCoords();
        this.matrixSize = handler.getMapMatrix().getMatrixSize();

        this.setPreferredSize(new Dimension(
                (int) (matrixSize.width * MapData.mapThumbnailSize * scale),
                (int) (matrixSize.height * MapData.mapThumbnailSize * scale)));
        //this.revalidate();
    }

    public float getScale() {
        return scale;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                formMousePressed(e);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGap(0, 530, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGap(0, 400, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
