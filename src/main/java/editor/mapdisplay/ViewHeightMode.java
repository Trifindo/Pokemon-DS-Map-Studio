
package editor.mapdisplay;

import com.jogamp.opengl.GL2;
import editor.state.MapLayerState;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import javax.swing.SwingUtilities;

/**
 *
 * @author Trifindo
 */
public class ViewHeightMode extends ViewMode {

    @Override
    public void mousePressed(MapDisplay d, MouseEvent e) {
        if (d.SHIFT_PRESSED) {
            if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                d.lastMouseX = e.getX();
                d.lastMouseY = e.getY();
            }
        } else {
            switch (d.editMode) {
                case MODE_EDIT:
                    d.setMapSelectedIfExists(e);
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        d.handler.addMapState(new MapLayerState("Draw Height", d.handler));
                        d.setHeightInGrid(e, d.handler.getHeightSelected());
                        d.updateActiveMapLayerGL();
                        d.repaint();
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        d.handler.addMapState(new MapLayerState("Flood Fill Height", d.handler));
                        d.floodFillHeightInGrid(e);
                        d.updateActiveMapLayerGL();
                        d.repaint();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        d.setHeightIndexFromGrid(e);
                        d.updateActiveMapLayerGL();
                        d.repaint();
                        d.handler.getMainFrame().repaintHeightSelector();
                    }
                case MODE_MOVE:
                    if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        d.lastMouseX = e.getX();
                        d.lastMouseY = e.getY();
                    }
                    break;
                case MODE_ZOOM:
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        d.orthoScale *= 1.5;
                        d.repaint();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        d.orthoScale /= 1.5;
                        d.repaint();
                    }
                    break;
            }
        }
    }

    @Override
    public void mouseReleased(MapDisplay d, MouseEvent e) {
        switch (d.editMode) {
            case MODE_CLEAR:
                d.handler.getMapMatrix().removeUnusedMaps();
                break;
        }
        d.handler.updateLayerThumbnail(d.handler.getActiveLayerIndex());
        d.handler.repaintThumbnailLayerSelector();

        d.editedMapCoords.add(d.handler.getMapSelected());
        d.handler.updateMapThumbnails(d.editedMapCoords);
        d.editedMapCoords = new HashSet<>();

        d.handler.getMainFrame().updateMapMatrixDisplay();
    }

    @Override
    public void mouseDragged(MapDisplay d, MouseEvent e) {
        d.updateMousePostion(e);
        if (d.SHIFT_PRESSED) {
            if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                d.moveCamera(e);
                d.repaint();
            }
        } else {
            switch (d.editMode) {
                case MODE_EDIT:
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        d.setMapSelectedIfExists(e);
                        d.editedMapCoords.add(d.getMapCoords(e));
                        d.setHeightInGrid(e, d.handler.getHeightSelected());
                        d.updateActiveMapLayerGL();
                        d.repaint();
                    }
                    break;
                case MODE_MOVE:
                    if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        d.cameraX -= (((float) ((e.getX() - d.lastMouseX))) / d.getWidth()) / (d.orthoScale / (d.cols + 2 * d.borderSize));
                        d.cameraY += (((float) ((e.getY() - d.lastMouseY))) / d.getHeight()) / (d.orthoScale / (d.rows + 2 * d.borderSize));
                        d.targetX = d.cameraX;
                        d.targetY = d.cameraY;
                        d.lastMouseX = e.getX();
                        d.lastMouseY = e.getY();
                        d.repaint();
                    }
                    break;
            }
        }
    }

    @Override
    public void mouseMoved(MapDisplay d, MouseEvent e) {
        d.updateMousePostion(e);
        d.repaint();
    }

    @Override
    public void keyPressed(MapDisplay d, KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                d.set3DView();
                d.repaint();
                break;
            case KeyEvent.VK_H:
                d.setOrthoView();
                d.repaint();
                break;
            case KeyEvent.VK_RIGHT:
                d.setCameraAtNextMapAndSelect(new Point(1, 0));
                d.repaint();
                break;
            case KeyEvent.VK_LEFT:
                d.setCameraAtNextMapAndSelect(new Point(-1, 0));
                d.repaint();
                break;
            case KeyEvent.VK_UP:
                d.setCameraAtNextMapAndSelect(new Point(0, -1));
                d.repaint();
                break;
            case KeyEvent.VK_DOWN:
                d.setCameraAtNextMapAndSelect(new Point(0, 1));
                d.repaint();
                break;
        }
    }

    @Override
    public void keyReleased(MapDisplay d, KeyEvent e) {

    }

    @Override
    public void mouseWheelMoved(MapDisplay d, MouseWheelEvent e) {
        if (d.SHIFT_PRESSED) {
            d.zoomCameraOrtho(e);
            d.repaint();
        } else {
            switch (d.editMode) {
                case MODE_EDIT:
                    int delta = e.getWheelRotation() < 0 ? 1 : -1;
                    d.handler.incrementHeightSelected(delta);
                    d.handler.getMainFrame().repaintHeightSelector();
                    d.repaint();
                    break;
                case MODE_MOVE:
                    d.zoomCameraOrtho(e);
                    d.repaint();
                    break;
                case MODE_ZOOM:
                    d.zoomCameraOrtho(e);
                    d.repaint();
                    break;
            }
        }
    }

    @Override
    public void paintComponent(MapDisplay d, Graphics g) {
        if (d.handler != null) {
            Graphics2D g2d = (Graphics2D) g;

            AffineTransform transform = g2d.getTransform();
            d.applyGraphicsTransform(g2d);

            if (d.backImageEnabled && d.backImage != null) {
                d.drawBackImage(g);
            }

            d.drawActiveHeightMap(g);

            g.setColor(Color.white);
            d.drawAllMapBounds(g);

            if(d.drawAreasEnabled){
                d.drawAllMapContours(g);
            }
            
            g.setColor(Color.red);
            d.drawBorderBounds(g,
                    d.handler.getMapSelected().x * d.cols * d.tileSize,
                    d.handler.getMapSelected().y * d.rows * d.tileSize, 2);

            g2d.setTransform(transform);
        }
    }

    @Override
    public void applyCameraTransform(MapDisplay d, GL2 gl) {
        float v = (16.0f + d.borderSize) / d.orthoScale;
        gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
    }

    @Override
    public void setCameraAtMap(MapDisplay d) {
        d.orthoScale = 1.0f;
    }

    @Override
    public ViewID getViewID() {
        return ViewID.VIEW_HEIGHT;
    }

}
