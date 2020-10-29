
package editor.tileseteditor;

import static com.jogamp.opengl.GL.GL_BACK;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_CW;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FRONT;
import static com.jogamp.opengl.GL.GL_GREATER;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_NOTEQUAL;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_TRUE;
import static com.jogamp.opengl.GL2.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL2.GL_BLEND;
import static com.jogamp.opengl.GL2.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL2.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL2.GL_FLOAT;
import static com.jogamp.opengl.GL2.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL2.GL_LEQUAL;
import static com.jogamp.opengl.GL2.GL_LESS;
import static com.jogamp.opengl.GL2.GL_LINES;
import static com.jogamp.opengl.GL2.GL_NEAREST;
import static com.jogamp.opengl.GL2.GL_ONE_MINUS_DST_ALPHA;
import static com.jogamp.opengl.GL2.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL2.GL_REPEAT;
import static com.jogamp.opengl.GL2.GL_SRC_ALPHA;
import static com.jogamp.opengl.GL2.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL2.GL_TEXTURE0;
import static com.jogamp.opengl.GL2.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL2.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL2.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL2.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL2.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL2.GL_TRIANGLES;

import com.jogamp.opengl.GL2;

import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static com.jogamp.opengl.GL2ES1.GL_LIGHT_MODEL_AMBIENT;
import static com.jogamp.opengl.GL2ES1.GL_LIGHT_MODEL_TWO_SIDE;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHT2;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_POSITION;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import editor.handler.MapEditorHandler;
import geometry.Generator;
import graphicslib3D.Matrix3D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.SwingUtilities;

import tileset.Tile;
import tileset.Tileset;
import utils.GlUtils;
import utils.Utils;

/**
 * @author Trifindo
 */
public class TileDisplay extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    //Map Editor Handler
    private MapEditorHandler handler;

    //OpenGL
    private GLU glu;
    private float[] grid;
    private float[] axis;
    private final float[] axisColors = {
            1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private ArrayList<Texture> textures = new ArrayList<>();

    //Scene
    private float cameraX, cameraY, cameraZ;
    private float modelRotX, modelRotY, modelRotZ;
    private boolean orthoEnabled = false;
    private boolean drawGridEnabled = true;

    //Mouse Events
    private boolean dragging = false;
    private int lastMouseX, lastMouseY;

    // Update Display
    private boolean updateRequested = false;

    //Display Mode
    private boolean wireframeEnabled = true;
    private boolean backfaceCullingEnabled = true;
    private boolean texturesEnabled = true;
    private boolean lightingEnabled = false;

    public TileDisplay() {
        //Add listeners
        addGLEventListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);

        //Set focusable for keyListener
        setFocusable(true);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        //Create and compile programs
        //renderVboTile = new boolean[handler.getTileset().size() * vbosPerTile];
        //Create VAOs and VBOs
        grid = Generator.generateCenteredGrid(Tile.maxTileSize, Tile.maxTileSize, 1.0f, -0.01f);
        axis = Generator.generateAxis(100.0f);

        //Load Textures into OpenGL
        //handler.getTileset().loadTexturesGL();
        loadTexturesGL();

        drawable.getGL().getGL2().glClearColor(0.0f, 0.5f, 0.5f, 1.0f);

        //Scene
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 8.0f;

        modelRotX = -30.0f;
        modelRotY = 0.0f;
        modelRotZ = 0.0f;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getContext().getGL().getGL2();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (updateRequested) {
            //renderVboTile = new boolean[handler.getTileset().size() * vbosPerTile];
            System.out.println("Number of tiles: " + handler.getTileset().size());

            System.out.println("UPDATING WINDOW...");

            //handler.getTileset().updateTextures(gl, "res/tileset");
            //handler.getTileset().loadTexturesGL();
            loadTexturesGL();

            //Load Textures into OpenGL
            //handler.getTileset().loadTextures("res/tileset");
            updateRequested = false;

        }

        //Draw grid
        drawGrid();

        //Draw axis
        drawAxis();

        //Draw tiles
        if (handler.getTileset().size() > 0) {
            if (lightingEnabled) {
                gl.glLoadIdentity();

                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT0);

                //float[] ambientLight = {1f, 1f, 1f, 0f};
                //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);

                //float[] specularLight = {1f, 1f, 1f, 1f};
                //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularLight, 0);

                //float[] diffuseLight = {1f, 1f, 1f, 0f};
                //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);

                //float[] emissionLight = {1f, 1f, 1f, 0f};
                //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_EMISSION, emissionLight, 0);

                gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f, 0.0f}, 0);
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{-1.0f, 1.0f, -0.05f, 0.0f}, 0);
            }

            drawOpaque();
            drawTransparent();

            if (lightingEnabled) {
                gl.glDisable(GL2.GL_LIGHTING);
                gl.glDisable(GL2.GL_LIGHT0);
            }

            if (wireframeEnabled) {
                drawWireframe();
            }
        }

        gl.glFinish();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
        }
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
        if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
            float delta = 100.0f;
            modelRotZ += (((float) ((e.getX() - lastMouseX))) / getWidth()) * delta;
            lastMouseX = e.getX();
            modelRotX += (((float) ((e.getY() - lastMouseY))) / getHeight()) * delta;
            lastMouseY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (orthoEnabled) {
                orthoEnabled = false;
            } else {
                orthoEnabled = true;
            }
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_G) {
            drawGridEnabled = !drawGridEnabled;
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int wheelRotation = e.getWheelRotation();
        if (wheelRotation > 0) {
            cameraZ *= 1.1;
        } else {
            cameraZ /= 1.1;
        }
        repaint();
    }

    public void requestUpdate() {
        updateRequested = true;
    }

    private void drawGrid() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glLoadIdentity();

        if (orthoEnabled) {
            float v = 6.0f;
            gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
        } else {
            float aspect = (float) this.getWidth() / (float) this.getHeight();
            glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        }

        gl.glTranslatef(-cameraX, -cameraY, -cameraZ); // translate into the screen
        gl.glRotatef(modelRotX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
        gl.glRotatef(modelRotY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
        gl.glRotatef(modelRotZ, 0.0f, 0.0f, 1.0f); // rotate about the z-axis

        final int offset = Tile.maxTileSize / 2;
        gl.glTranslatef(offset, offset, 0.0f); // translate into the screen

        //Adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glLineWidth(1);

        gl.glDisable(GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);

        final int coordsPerVertex = 3;
        final int vertexPerLine = 2;
        final int coordsPerLine = coordsPerVertex * vertexPerLine;
        gl.glBegin(GL_LINES);
        for (int i = 0; i < grid.length; i += coordsPerLine) {
            gl.glVertex3fv(grid, i);
            gl.glVertex3fv(grid, i + coordsPerVertex);
        }
        gl.glEnd();
    }

    private void drawAxis() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glLoadIdentity();

        if (orthoEnabled) {
            float v = 6.0f;
            gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
        } else {
            float aspect = (float) this.getWidth() / (float) this.getHeight();
            glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        }

        gl.glTranslatef(-cameraX, -cameraY, -cameraZ); // translate into the screen
        gl.glRotatef(modelRotX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
        gl.glRotatef(modelRotY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
        gl.glRotatef(modelRotZ, 0.0f, 0.0f, 1.0f); // rotate about the z-axis

        gl.glDisable(GL_TEXTURE_2D);

        //Adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glBegin(GL_LINES);
        for (int i = 0; i < axis.length; i += 3) {
            gl.glColor3fv(axisColors, i);
            gl.glVertex3fv(axis, i);
        }

        gl.glEnd();
    }

    public void drawTile(boolean useWireframe) {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glLoadIdentity();
        if (orthoEnabled) {
            float v = 6.0f;
            gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
        } else {
            float aspect = (float) this.getWidth() / (float) this.getHeight();
            glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        }

        gl.glTranslatef(-cameraX, -cameraY, -cameraZ); // translate into the screen

        gl.glRotatef(modelRotX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
        gl.glRotatef(modelRotY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
        gl.glRotatef(modelRotZ, 0.0f, 0.0f, 1.0f); // rotate about the z-axis

        Tile tile = handler.getTileset().get(handler.getTileIndexSelected());

        drawQuads(gl, tile, useWireframe);
        drawTris(gl, tile, useWireframe);

    }

    private void drawQuads(GL2 gl, Tile tile, boolean useWireframe) {
        if (!(tile.getVCoordsQuad().length > 0 && tile.getTCoordsQuad().length > 0 && tile.getColorsQuad().length > 0)) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            // activate texture unit #0 and bind it to the brick texture object
            //gl.glActiveTexture(GL_TEXTURE0);
            if (texturesEnabled && !useWireframe) {
                //gl.glBindTexture(GL_TEXTURE_2D, tile.getTexture(k).getTextureObject());
                gl.glBindTexture(GL_TEXTURE_2D, textures.get(tile.getTextureIDs().get(k)).getTextureObject());

                gl.glEnable(GL_TEXTURE_2D);

                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            }

            //Draw polygons
            int start, end;
            final int vPerPolygon = 4;
            start = tile.getTexOffsetsQuad().get(k);
            if (k + 1 < tile.getTextureIDs().size()) {
                end = (tile.getTexOffsetsQuad().get(k + 1));
            } else {
                end = tile.getVCoordsQuad().length / (3 * vPerPolygon);
            }

            if (!useWireframe) {
                gl.glBegin(GL_QUADS);
                for (int i = start; i < end; i++) {
                    for (int j = 0; j < vPerPolygon; j++) {
                        gl.glTexCoord2fv(tile.getTCoordsQuad(), (i * vPerPolygon + j) * 2);
                        gl.glNormal3fv(tile.getNCoordsQuad(), (i * vPerPolygon + j) * 3);
                        gl.glColor3fv(tile.getColorsQuad(), (i * vPerPolygon + j) * 3);
                        gl.glVertex3fv(tile.getVCoordsQuad(), (i * vPerPolygon + j) * 3);
                    }
                }
                gl.glEnd();
            } else {
                gl.glBegin(GL_QUADS);
                for (int i = start; i < end; i++) {
                    for (int j = 0; j < vPerPolygon; j++) {
                        gl.glTexCoord2fv(tile.getTCoordsQuad(), (i * vPerPolygon + j) * 2);
                        gl.glVertex3fv(tile.getVCoordsQuad(), (i * vPerPolygon + j) * 3);
                    }
                }
                gl.glEnd();
            }
        }
    }

    private void drawTris(GL2 gl, Tile tile, boolean useWireframe) {
        if (!(tile.getVCoordsTri().length > 0 && tile.getTCoordsTri().length > 0 && tile.getColorsTri().length > 0)) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            // activate texture unit #0 and bind it to the brick texture object
            //gl.glActiveTexture(GL_TEXTURE0);
            if (texturesEnabled && !useWireframe) {
                //gl.glBindTexture(GL_TEXTURE_2D, tile.getTexture(k).getTextureObject());
                gl.glBindTexture(GL_TEXTURE_2D, textures.get(tile.getTextureIDs().get(k)).getTextureObject());

                gl.glEnable(GL_TEXTURE_2D);

                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            }

            //Draw polygons
            int start, end;
            final int vPerPolygon = 3;
            start = tile.getTexOffsetsTri().get(k);
            if (k + 1 < tile.getTextureIDs().size()) {
                end = (tile.getTexOffsetsTri().get(k + 1));
            } else {
                end = tile.getVCoordsTri().length / (3 * vPerPolygon);
            }

            if (!useWireframe) {
                gl.glBegin(GL_TRIANGLES);
                for (int i = start; i < end; i++) {
                    for (int j = 0; j < vPerPolygon; j++) {
                        gl.glTexCoord2fv(tile.getTCoordsTri(), (i * vPerPolygon + j) * 2);
                        gl.glNormal3fv(tile.getNCoordsTri(), (i * vPerPolygon + j) * 3);
                        gl.glColor3fv(tile.getColorsTri(), (i * vPerPolygon + j) * 3);
                        gl.glVertex3fv(tile.getVCoordsTri(), (i * vPerPolygon + j) * 3);
                    }
                }
                gl.glEnd();
            } else {
                gl.glBegin(GL_TRIANGLES);
                for (int i = start; i < end; i++) {
                    for (int j = 0; j < vPerPolygon; j++) {
                        gl.glTexCoord2fv(tile.getTCoordsTri(), (i * vPerPolygon + j) * 2);
                        gl.glVertex3fv(tile.getVCoordsTri(), (i * vPerPolygon + j) * 3);
                    }
                }
                gl.glEnd();
            }

        }

    }

    public void drawOpaque() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0.9f);

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        if (backfaceCullingEnabled) {
            gl.glEnable(GL_CULL_FACE);
        }

        drawTile(false);

        if (backfaceCullingEnabled) {
            gl.glDisable(GL_CULL_FACE);
        }

    }

    public void drawTransparent() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        if (backfaceCullingEnabled) {
            gl.glEnable(GL_CULL_FACE);
        }

        drawTile(false);

        if (backfaceCullingEnabled) {
            gl.glDisable(GL_CULL_FACE);
        }

    }

    public void drawWireframe() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glColor3f(0.0f, 0.0f, 0.0f);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        gl.glDisable(GL_TEXTURE_2D);

        gl.glLineWidth(1.5f);

        drawTile(true);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

    }

    private void loadTexturesGL() {
        textures = new ArrayList<>();
        for (int i = 0; i < handler.getTileset().getMaterials().size(); i++) {
            textures.add(loadTextureGL(i));
        }
    }

    private Texture loadTextureGL(int index) {
        Texture tex = null;
        try {
            BufferedImage img = Utils.cloneImg(handler.getTileset().getMaterials().get(index).getTextureImg());
            ImageUtil.flipImageVertically(img);
            tex = AWTTextureIO.newTexture(GLProfile.getDefault(), img, false);
        } catch (Exception e) {
            tex = AWTTextureIO.newTexture(GLProfile.getDefault(), Tileset.defaultTexture, false);
        }
        return tex;
    }

    public void swapTextures(int index1, int index2) {
        Collections.swap(textures, index1, index2);
    }

    public void setHandler(MapEditorHandler handler) {
        this.handler = handler;
    }

    public void setWireframe(boolean enabled) {
        this.wireframeEnabled = enabled;
    }

    public void setBackfaceCulling(boolean enabled) {
        this.backfaceCullingEnabled = enabled;
    }

    public void setLightingEnabled(boolean lightingEnabled) {
        this.lightingEnabled = lightingEnabled;
    }

    public boolean isLightingEnabled() {
        return lightingEnabled;
    }

    public void setTexturesEnabled(boolean texturesEnabled) {
        this.texturesEnabled = texturesEnabled;
    }

    public boolean isTexturesEnabled() {
        return texturesEnabled;
    }

}
