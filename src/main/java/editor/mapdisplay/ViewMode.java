
package editor.mapdisplay;

import com.jogamp.opengl.GL2;
import math.vec.Vec3f;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Trifindo
 */
public abstract class ViewMode {

    public enum ViewID {
        VIEW_3D, VIEW_ORTHO, VIEW_HEIGHT
    }

    public static View3dMode VIEW_3D_MODE = new View3dMode();
    public static ViewOrthoMode VIEW_ORTHO_MODE = new ViewOrthoMode();
    public static ViewHeightMode VIEW_HEIGHT_MODE = new ViewHeightMode();

    public abstract void mousePressed(MapDisplay d, MouseEvent e);

    public abstract void mouseReleased(MapDisplay d, MouseEvent e);

    public abstract void mouseDragged(MapDisplay d, MouseEvent e);

    public abstract void mouseMoved(MapDisplay d, MouseEvent e);

    public abstract void keyPressed(MapDisplay d, KeyEvent e);

    public abstract void keyReleased(MapDisplay d, KeyEvent e);

    public abstract void mouseWheelMoved(MapDisplay d, MouseWheelEvent e);

    public abstract void paintComponent(MapDisplay d, Graphics g);

    public abstract void applyCameraTransform(MapDisplay d, GL2 gl);

    public abstract void setCameraAtMap(MapDisplay d);

    public abstract ViewID getViewID();

    public abstract Vec3f[][] getFrustumPlanes(MapDisplay d);

    public abstract float getZNear(MapDisplay d);

    public abstract float getZFar(MapDisplay d);

}
