
package editor.mapdisplay;

import com.jogamp.opengl.GL2;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import math.vec.Vec3f;

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
        if (d.editMode == MapDisplay.EditMode.MODE_ZOOM) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                d.cameraZ /= 1.5;
                d.repaint();
            } else if (SwingUtilities.isRightMouseButton(e)) {
                d.cameraZ *= 1.5;
                d.repaint();
            }
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
        d.glu.gluPerspective(d.fovDeg, d.getAspectRatio(), getZNear(d), getZFar(d));
    }

    @Override
    public void setCameraAtMap(MapDisplay d) {
        d.cameraRotX = MapDisplay.defaultCamRotX;
        d.cameraRotY = MapDisplay.defaultCamRotY;
        d.cameraRotZ = MapDisplay.defaultCamRotZ;

        d.cameraZ = 40.0f;
    }

    @Override
    public ViewID getViewID() {
        return ViewID.VIEW_3D;
    }

    @Override
    public float getZNear(MapDisplay d) {
        if (d.cameraZ < 40.0f) {
            return 1.0f;
        } else {
            return 1.0f + (d.cameraZ - 40.0f) / 4;
        }
    }

    @Override
    public float getZFar(MapDisplay d) {
        if (d.cameraZ < 40.0f) {
            return 1000.0f;
        } else {
            return 1000.0f + (d.cameraZ - 40.0f);
        }
    }

    @Override
    public Vec3f[][] getFrustumPlanes(MapDisplay d) {
        Vec3f camAngles = new Vec3f(d.cameraRotX, d.cameraRotY, d.cameraRotZ);
        Vec3f tarPos = new Vec3f(d.cameraX, d.cameraY, 0.0f);
        Vec3f camDir = MapDisplay.rotToDir_(camAngles);
        Vec3f camUp = MapDisplay.rotToUp_(camAngles);
        Vec3f camRight = camDir.cross_(camUp);
        Vec3f camPos = tarPos.add_(camDir.negate_().scale_(d.cameraZ));

        //camDir.print("DIR");
        //camUp.print("UP");
        //camRight.print("RIGHT");

        float zNear = getZNear(d);
        float zFar = getZFar(d);
        float fov = (float) (d.fovDeg * Math.PI / 180);

        float hNear = 2.0f * (float) Math.tan(fov / 2.0f) * zNear;
        float wNear = hNear * d.getAspectRatio();

        float hFar = 2.0f * (float) Math.tan(fov / 2.0f) * zFar;
        float wFar = hFar * d.getAspectRatio();

        //Far plane points
        Vec3f fc = camDir.scale_(zFar).add(camPos);
        Vec3f ftl = fc.add_(camUp.scale_(hFar / 2.0f)).sub(camRight.scale_(wFar / 2.0f));
        Vec3f ftr = fc.add_(camUp.scale_(hFar / 2.0f)).add(camRight.scale_(wFar / 2.0f));
        Vec3f fbl = fc.sub_(camUp.scale_(hFar / 2.0f)).sub(camRight.scale_(wFar / 2.0f));
        Vec3f fbr = fc.sub_(camUp.scale_(hFar / 2.0f)).add(camRight.scale_(wFar / 2.0f));

        //Near plane points
        Vec3f nc = camDir.scale_(zNear).add(camPos);
        Vec3f ntl = nc.add_(camUp.scale_(hNear / 2.0f)).sub(camRight.scale_(wNear / 2.0f));
        Vec3f ntr = nc.add_(camUp.scale_(hNear / 2.0f)).add(camRight.scale_(wNear / 2.0f));
        Vec3f nbl = nc.sub_(camUp.scale_(hNear / 2.0f)).sub(camRight.scale_(wNear / 2.0f));
        Vec3f nbr = nc.sub_(camUp.scale_(hNear / 2.0f)).add(camRight.scale_(wNear / 2.0f));

        //Return frustum planes defined by 3 points
        return new Vec3f[][]{
                {ntr, ntl, ftl},
                {nbl, nbr, fbr},
                {ntl, nbl, fbl},
                {nbr, ntr, fbr},
                {ntl, ntr, nbr},
                {ftr, ftl, fbl}
        };
    }
}
