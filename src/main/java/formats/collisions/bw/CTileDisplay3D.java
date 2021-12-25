package formats.collisions.bw;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static com.jogamp.opengl.GL2GL3.*;

public class CTileDisplay3D extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    private CollisionHandlerBW cHandler;
    //public float[] tileCoords;

    //Grid
    protected final int cols = 32;
    protected final int rows = 32;
    protected final int tileSize = 16;
    protected final int width = cols * tileSize;
    protected final int height = rows * tileSize;
    protected final float gridTileSize = 1.0f;
    protected float orthoScale = 1.0f;

    //OpenGL
    protected GLU glu;
    protected float[] grid;
    protected FloatBuffer gridBuffer;
    protected float[] axis = {
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
    };
    protected final float[] axisColors = {
            1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };

    private static final float[] cubeCoords = new float[]{
            1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };

    final float[] xDelta = {0.0f, 1.0f, 1.0f, 0.0f};
    final float[] yDelta = {0.0f, 0.0f, -1.0f, -1.0f};

    //Scene
    protected float cameraX, cameraY, cameraZ;
    protected float targetX, targetY, targetZ;
    protected float cameraRotX, cameraRotY, cameraRotZ;
    protected static final float defaultCamRotX = 40.0f, defaultCamRotY = 0.0f, defaultCamRotZ = 0.0f;
    protected float lastMouseX, lastMouseY;

    //Update
    protected boolean updateRequested = false;
    protected final float minZ = -8.0f, maxZ = 8.0f;

    public CTileDisplay3D() {
        setPreferredSize(new Dimension(width, height));
        setSize(new Dimension(width, height));

        //Add listeners
        addGLEventListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);

        //Set focusable for keyListener
        setFocusable(true);

        //Scene
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 8.0f;

        cameraRotX = defaultCamRotX;
        cameraRotY = defaultCamRotY;
        cameraRotZ = defaultCamRotZ;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        drawable.getGL().getGL2().glClearColor(0.0f, 0.5f, 0.5f, 1.0f);

        //axis = Generator.generateAxis(2.0f);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL_LINE_SMOOTH);
        gl.glEnable(GL_POLYGON_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        if (updateRequested) {
            updateRequested = false;
        }

        if (cHandler != null) {
            drawCollisions(gl);
            drawWireframe(gl);
            drawTileGrid(gl);
        }

        drawAxis(gl);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            /*
            float dist = cameraZ;
            float deltaX = (((float) ((e.getX() - lastMouseX))) / getWidth()) * dist;
            float deltaZ = (((float) ((e.getY() - lastMouseY))) / getHeight()) * dist;

            Vector3D v = new Vector3D(deltaX, 0.0f, deltaZ);
            Matrix3D m2 = new Matrix3D(cameraRotZ, new Vector3D(0.0f, 1.0f, 0.0f));
            v = v.mult(m2);

            cameraX -= (float) v.getX();
            cameraY += (float) v.getZ();

            lastMouseX = e.getX();
            lastMouseY = e.getY();

            repaint();
            */
        } else if (SwingUtilities.isRightMouseButton(e) | SwingUtilities.isMiddleMouseButton(e)) {
            float delta = 100.0f;
            cameraRotZ -= (((float) ((e.getX() - lastMouseX))) / getWidth()) * delta;
            lastMouseX = e.getX();
            cameraRotX -= (((float) ((e.getY() - lastMouseY))) / getHeight()) * delta;
            lastMouseY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            cameraZ *= 1.1;
        } else {
            cameraZ /= 1.1;
        }
        repaint();
    }

    protected void applyCameraTransform(GL2 gl) {
        gl.glLoadIdentity();

        float aspect = (float) getWidth() / (float) getHeight();
        if (cameraZ < 40.0f) {
            glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        } else {
            glu.gluPerspective(60.0f, aspect, 1.0f + (cameraZ - 40.0f) / 4, 1000.0f + (cameraZ - 40.0f));
        }

        glu.gluLookAt(
                0.0f, 0.0f, cameraZ,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

        gl.glRotatef(-cameraRotX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(-cameraRotY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(-cameraRotZ, 0.0f, 0.0f, 1.0f);

        gl.glTranslatef(-cameraX, -cameraY, 0.0f);
    }

    public void drawCollisions(GL2 gl) {
        float[] zCoords = cHandler.getCurrentTile();

        gl.glEnable(GL_BLEND);
        //gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        //gl.glEnable(GL_ALPHA_TEST);
        //gl.glAlphaFunc(GL_GREATER, 0.9f);

        applyCameraTransform(gl);

        final int coordsPerVertex = 3;
        final int vertexPerTile = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerTile;

        gl.glBegin(GL_QUADS);
        for (int j = 0; j < vertexPerTile; j++) {
            float value = (zCoords[j] - minZ) / (maxZ - minZ);
            value = Math.max(Math.min(value, 1.0f), 0.0f);
            Color c = Color.getHSBColor(0.66f, 1.0f - value, 1.0f);
            //Color c = Color.getHSBColor(value, 1.0f, 1.0f);
            gl.glColor3f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
            gl.glVertex3f(xDelta[j], yDelta[j], zCoords[j]);
        }
        gl.glEnd();
    }

    public void drawWireframe(GL2 gl) {
        float[] zCoords = cHandler.getCurrentTile();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        gl.glDisable(GL_TEXTURE_2D);
        gl.glLineWidth(1.5f);

        applyCameraTransform(gl);

        final int coordsPerVertex = 3;
        final int vertexPerTile = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerTile;

        gl.glBegin(GL_QUADS);
        for (int j = 0; j < vertexPerTile; j++) {
            gl.glColor3f(0, 0, 0);
            gl.glVertex3f(xDelta[j], yDelta[j], zCoords[j]);
        }
        gl.glEnd();

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    protected void drawAxis(GL2 gl) {
        applyCameraTransform(gl);

        gl.glDisable(GL_TEXTURE_2D);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);
        gl.glLineWidth(3f);

        gl.glBegin(GL_LINES);
        for (int i = 0; i < axis.length; i += 3) {
            gl.glColor3fv(axisColors, i);
            gl.glVertex3fv(axis, i);
        }
        gl.glEnd();
    }

    public void drawTileGrid(GL2 gl) {
        applyCameraTransform(gl);

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        gl.glDisable(GL_TEXTURE_2D);
        gl.glLineWidth(1);

        final int vertexPerTile = 4;

        Float[] zCoords = toObjectArray(cHandler.getCurrentTile());
        float min = Math.min(Collections.min(Arrays.asList(zCoords)), 0);
        float max = Math.max(Collections.max(Arrays.asList(zCoords)), 0);
        int numCubes = (int) Math.ceil(max - min);

        //System.out.println("NUM CUBES: " + numCubes);

        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.2f);
        //gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef(0, -1.0f, (float) Math.round(min));

        for (int k = 0; k < numCubes; k++) {
            gl.glBegin(GL_QUADS);
            for (int i = 0, c = 0; i < 6; i++) {
                for (int j = 0; j < 4; j++, c += 3) {
                    gl.glVertex3fv(cubeCoords, c);
                }
            }
            gl.glEnd();
            gl.glTranslatef(0.0f, 0.0f, 1.0f);
        }

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void init(CollisionHandlerBW cHandler) {
        this.cHandler = cHandler;
    }

    private static Float[] toObjectArray(float[] array) {
        return IntStream.range(0, array.length)
                .mapToObj(i -> array[i])
                .toArray(Float[]::new);
    }
}
