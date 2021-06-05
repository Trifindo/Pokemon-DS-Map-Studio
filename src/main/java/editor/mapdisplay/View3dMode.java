
package editor.mapdisplay;

import com.jogamp.opengl.GL2;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;

/**
 * @author Trifindo
 */
public class View3dMode extends ViewMode {

    @Override
    public void mousePressed(MapDisplay d, MouseEvent e) {
        d.lastMouseX = e.getX();
        d.lastMouseY = e.getY();
        switch (d.editMode) {
            case MODE_ZOOM:
                if (SwingUtilities.isLeftMouseButton(e)) {
                    d.cameraZ /= 1.5;
                    d.repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    d.cameraZ *= 1.5;
                    d.repaint();
                }
                break;
        }
    }

    @Override
    public void mouseReleased(MapDisplay d, MouseEvent e) {

    }

    @Override
    public void mouseDragged(MapDisplay d, MouseEvent e) {
        if (d.editMode != MapDisplay.EditMode.MODE_ZOOM) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                float dist = d.cameraZ;
                float deltaX = (((float) ((e.getX() - d.lastMouseX))) / d.getWidth()) * dist;
                float deltaZ = (((float) ((e.getY() - d.lastMouseY))) / d.getHeight()) * dist;

                Vector3D v = new Vector3D(deltaX, 0.0f, deltaZ);
                Matrix3D m2 = new Matrix3D(d.cameraRotZ, new Vector3D(0.0f, 1.0f, 0.0f));
                v = v.mult(m2);

                d.cameraX -= (float) v.getX();
                d.cameraY += (float) v.getZ();

                d.lastMouseX = e.getX();
                d.lastMouseY = e.getY();

                d.repaint();
            } else if (SwingUtilities.isRightMouseButton(e)
                    | SwingUtilities.isMiddleMouseButton(e)) {
                float delta = 100.0f;
                d.cameraRotZ -= (((float) ((e.getX() - d.lastMouseX))) / d.getWidth()) * delta;
                d.lastMouseX = e.getX();
                d.cameraRotX -= (((float) ((e.getY() - d.lastMouseY))) / d.getHeight()) * delta;
                d.lastMouseY = e.getY();
                d.repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MapDisplay d, MouseEvent e) {

    }

    @Override
    public void keyPressed(MapDisplay d, KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                d.setOrthoView();
                d.repaint();
                break;
            case KeyEvent.VK_H:
                d.setHeightView();
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
        if (e.getWheelRotation() > 0) {
            d.cameraZ *= 1.1;
        } else {
            d.cameraZ /= 1.1;
        }
        d.repaint();
    }

    @Override
    public void paintComponent(MapDisplay d, Graphics g) {

    }

    @Override
    public void applyCameraTransform(MapDisplay d, GL2 gl) {
        float aspect = (float) d.getWidth() / (float) d.getHeight();
        if (d.cameraZ < 40.0f) {
            d.glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        } else {
            d.glu.gluPerspective(60.0f, aspect, 1.0f + (d.cameraZ - 40.0f) / 4, 1000.0f + (d.cameraZ - 40.0f));
        }

    }

    @Override
    public void setCameraAtMap(MapDisplay d) {
        d.cameraRotX = d.defaultCamRotX;
        d.cameraRotY = d.defaultCamRotY;
        d.cameraRotZ = d.defaultCamRotZ;

        d.cameraZ = 40.0f;
    }

    @Override
    public ViewID getViewID() {
        return ViewID.VIEW_3D;
    }
}
