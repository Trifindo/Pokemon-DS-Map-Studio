
package editor.vertexcolors;

import static com.jogamp.opengl.GL.GL_ALWAYS;
import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_GREATER;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LESS;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_NOTEQUAL;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_DST_ALPHA;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import com.jogamp.opengl.GL2;
import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import editor.handler.MapEditorHandler;
import editor.tileseteditor.TilesetEditorHandler;
import geometry.Generator;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import tileset.Tile;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class VColorEditorDisplay extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    //Map Editor Handler
    private MapEditorHandler handler;
    private TilesetEditorHandler tsetHandler;
    private VColorEditorDialog dialog;

    //OpenGL
    private GLU glu;
    private float[] grid;
    private float[] axis;
    private final float[] axisColors = {
        1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private boolean[] renderVboTile;
    private final int vbosPerTile = 4;

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
    private boolean backfaceCullingEnabled = false;
    private boolean drawTextures = true;

    //Points
    private ArrayList<Point3D> points;

    //Brush
    private int brushRadius = 20;
    private int mouseX;
    private int mouseY;

    //Render mode
    private final int textureMode = 0;
    private final int wireframeMode = 1;
    private final int pointMode = 2;

    //Polygon Selection
    public static final int BRUSH_MODE = 0;
    public static final int POLYGON_SELECTION_MODE = 1;
    public static final int COLOR_GRAB_MODE = 2;
    private int selectionMode = BRUSH_MODE;
    private final int faceSelectionRadius = 7;
    private final int colorGrabRadius = 7;

    private FaceSelection faceSelected;

    private float[] fCoordsTri;
    private float[] fCoordsQuad;

    //Color grab
    private Cursor colorGrabCursor;

    public VColorEditorDisplay() {
        //Add listeners
        addGLEventListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);

        //Set focusable for keyListener
        setFocusable(true);

        colorGrabCursor = Utils.loadCursor("/cursors/grabColorCursor.png");
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        //Create and compile programs
        renderVboTile = new boolean[handler.getTileset().size() * vbosPerTile];

        //Create VAOs and VBOs
        grid = Generator.generateCenteredGrid(Tile.maxTileSize, Tile.maxTileSize, 1.0f, -0.01f);
        axis = Generator.generateAxis(100.0f);
        setupVAOs(drawable.getGL().getGL2());

        fCoordsTri = calculateFaceCoords(handler.getTileSelected().getVCoordsTri(), 3);
        fCoordsQuad = calculateFaceCoords(handler.getTileSelected().getVCoordsQuad(), 4);

        //Load Textures into OpenGL
        handler.getTileset().loadTexturesGL();

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
            renderVboTile = new boolean[handler.getTileset().size() * vbosPerTile];

            //Create VAOs and VBOs
            setupVAOs(gl);

            handler.getTileset().loadTexturesGL();

            updateRequested = false;

        }

        //Draw grid
        drawGrid();

        //Draw axis
        drawAxis();

        //Draw tiles
        if (handler.getTileset().size() > 0) {
            drawOpaque();
            drawTransparent();

            if (wireframeEnabled) {
                drawWireframe();
            }

            if (selectionMode == BRUSH_MODE) {
                drawVertexPoints();
            } else if (selectionMode == POLYGON_SELECTION_MODE) {
                drawFaceCenterPoints();
                drawFaceSelected();
            } else if (selectionMode == COLOR_GRAB_MODE) {
                drawVertexPoints();
            }

        }

        //points = getVerticesOnScreen();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (handler != null) {
                if (handler.getTileset().size() > 0) {
                    if (selectionMode == POLYGON_SELECTION_MODE) {
                        ArrayList<Point3D> fPointsQuads = getVerticesOnScreen(fCoordsQuad);
                        ArrayList<Point3D> fPointsTris = getVerticesOnScreen(fCoordsTri);
                        int closerQuadIndex = getCloserVertexIndex(e.getX(), e.getY(), faceSelectionRadius, fPointsQuads);
                        int closerTriIndex = getCloserVertexIndex(e.getX(), e.getY(), faceSelectionRadius, fPointsTris);
                        if (closerQuadIndex != -1 && closerTriIndex != -1) {
                            float quadZ = (float) fPointsQuads.get(closerQuadIndex).getZ();
                            float triZ = (float) fPointsTris.get(closerTriIndex).getZ();
                            if (quadZ < triZ) {
                                faceSelected = new FaceSelection(closerQuadIndex, true);
                            } else {
                                faceSelected = new FaceSelection(closerTriIndex, false);
                            }
                        } else if (closerQuadIndex != -1) {
                            faceSelected = new FaceSelection(closerQuadIndex, true);
                        } else if (closerTriIndex != -1) {
                            faceSelected = new FaceSelection(closerTriIndex, false);
                        } else {
                            faceSelected = null;
                        }
                        repaint();
                    }
                }
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (handler != null) {
                if (handler.getTileset().size() > 0) {
                    Tile tile = handler.getTileSelected();
                    if (selectionMode == BRUSH_MODE) {
                        paintVertexColors(e.getX(), e.getY(), tile.getVCoordsQuad(), tile.getColorsQuad());
                        paintVertexColors(e.getX(), e.getY(), tile.getVCoordsTri(), tile.getColorsTri());
                    } else if (selectionMode == POLYGON_SELECTION_MODE) {
                        if (faceSelected != null) {
                            if (faceSelected.isQuad) {
                                paintVertexColorsInFace(e.getX(), e.getY(), tile.getVCoordsQuad(), tile.getColorsQuad(), faceSelected.faceIndex, 4);
                            } else {
                                paintVertexColorsInFace(e.getX(), e.getY(), tile.getVCoordsTri(), tile.getColorsTri(), faceSelected.faceIndex, 3);
                            }
                        }
                    } else if (selectionMode == COLOR_GRAB_MODE) {
                        Color color = grabColor(e.getX(), e.getY());
                        if (color != null) {
                            dialog.setSelectedColor(color);
                        }
                    }
                }
            }
            repaint();
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
        this.mouseX = e.getX();
        this.mouseY = e.getY();

        if (SwingUtilities.isMiddleMouseButton(e)) {
            float delta = 100.0f;
            modelRotZ += (((float) ((e.getX() - lastMouseX))) / getWidth()) * delta;
            lastMouseX = e.getX();
            modelRotX += (((float) ((e.getY() - lastMouseY))) / getHeight()) * delta;
            lastMouseY = e.getY();

        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (handler != null) {
                if (handler.getTileset().size() > 0) {
                    Tile tile = handler.getTileSelected();
                    if (selectionMode == BRUSH_MODE) {
                        paintVertexColors(e.getX(), e.getY(), tile.getVCoordsQuad(), tile.getColorsQuad());
                        paintVertexColors(e.getX(), e.getY(), tile.getVCoordsTri(), tile.getColorsTri());
                    } else if (selectionMode == POLYGON_SELECTION_MODE) {
                        if (faceSelected != null) {
                            if (faceSelected.isQuad) {
                                paintVertexColorsInFace(e.getX(), e.getY(), tile.getVCoordsQuad(), tile.getColorsQuad(), faceSelected.faceIndex, 4);
                            } else {
                                paintVertexColorsInFace(e.getX(), e.getY(), tile.getVCoordsTri(), tile.getColorsTri(), faceSelected.faceIndex, 3);
                            }
                        }
                    } else if (selectionMode == COLOR_GRAB_MODE) {
                        Color color = grabColor(e.getX(), e.getY());
                        if (color != null) {
                            dialog.setSelectedColor(color);
                        }
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();

        repaint();
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*
        if (points != null) {
            for (Point3D point : points) {
                g.setColor(Color.blue);
                g.drawRect((int) point.getX(), (int) point.getY(), 3, 3);
            }
        }*/
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        g2.setStroke(new BasicStroke(2));

        if (dialog != null) {
            g.setColor(dialog.getSelectedColor());
        }

        if (selectionMode == BRUSH_MODE) {
            g.drawOval(mouseX - brushRadius, mouseY - brushRadius, brushRadius * 2, brushRadius * 2);
        } else if (selectionMode == POLYGON_SELECTION_MODE) {
            g.drawOval(mouseX - brushRadius, mouseY - brushRadius, brushRadius * 2, brushRadius * 2);
        } else if (selectionMode == COLOR_GRAB_MODE) {
            //g.drawOval(mouseX - colorGrabRadius, mouseY - colorGrabRadius, colorGrabRadius * 2, colorGrabRadius * 2);
        }

    }

    public void requestUpdate() {
        updateRequested = true;
    }

    private void setupVAOs(GL2 gl) {
        //Create tile VBOs
        if (handler.getTileset().size() > 0) {
            generateTileVBOs(gl);
        }

    }

    private void generateTileVBOs(GL2 gl) {

        for (int i = 0; i < handler.getTileset().size(); i++) {
            Tile tile = handler.getTileset().get(i);

            renderVboTile[i * vbosPerTile] = tile.getVCoordsQuad().length > 0;
            renderVboTile[i * vbosPerTile + 1] = tile.getTCoordsQuad().length > 0;
            renderVboTile[i * vbosPerTile + 2] = tile.getVCoordsTri().length > 0;
            renderVboTile[i * vbosPerTile + 3] = tile.getTCoordsTri().length > 0;
        }
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

    public void drawTile(int mode) {
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

        int vboID = handler.getTileIndexSelected() * vbosPerTile;
        if (mode == textureMode) {
            drawQuads(gl, tile, vboID);
            drawTris(gl, tile, vboID + 2);
        } else if (mode == wireframeMode) {
            drawQuadsWireframe(gl, tile, vboID);
            drawTrisWireframe(gl, tile, vboID + 2);
        } else if (mode == pointMode) {
            drawQuadsPoints(gl, tile, vboID);
            drawTrisPoints(gl, tile, vboID + 2);
        }

    }

    private void drawQuads(GL2 gl, Tile tile, int vboID) {
        if (!(renderVboTile[vboID] && renderVboTile[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            if (drawTextures) {
                gl.glBindTexture(GL_TEXTURE_2D, tile.getTexture(k).getTextureObject());

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

            gl.glBegin(GL_QUADS);
            for (int i = start; i < end; i++) {
                for (int j = 0; j < vPerPolygon; j++) {
                    gl.glTexCoord2fv(tile.getTCoordsQuad(), (i * vPerPolygon + j) * 2);
                    gl.glColor3fv(tile.getColorsQuad(), (i * vPerPolygon + j) * 3);
                    gl.glVertex3fv(tile.getVCoordsQuad(), (i * vPerPolygon + j) * 3);
                    //gl.glNormal3fv(tile.getNCoordsQuad(), (i * vPerPolygon + j) * 3);
                }
            }
            gl.glEnd();

        }
    }

    private void drawTris(GL2 gl, Tile tile, int vboID) {
        if (!(renderVboTile[vboID] && renderVboTile[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            if (drawTextures) {
                gl.glBindTexture(GL_TEXTURE_2D, tile.getTexture(k).getTextureObject());

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

            /*
            int mode;
            if (usePoints) {
                gl.glEnable(GL2.GL_PROGRAM_POINT_SIZE_EXT);
                gl.glPointSize(4);
                mode = GL2.GL_POINTS;

            } else {
                mode = GL2.GL_TRIANGLES;
            }*/
            gl.glBegin(GL_TRIANGLES);
            for (int i = start; i < end; i++) {
                for (int j = 0; j < vPerPolygon; j++) {
                    gl.glTexCoord2fv(tile.getTCoordsTri(), (i * vPerPolygon + j) * 2);
                    gl.glColor3fv(tile.getColorsTri(), (i * vPerPolygon + j) * 3);
                    gl.glVertex3fv(tile.getVCoordsTri(), (i * vPerPolygon + j) * 3);
                    //gl.glNormal3fv(tile.getNCoordsTri(), (i * vPerPolygon + j) * 3);
                }
            }
            gl.glEnd();
        }

    }

    private void drawQuadsWireframe(GL2 gl, Tile tile, int vboID) {
        if (!(renderVboTile[vboID] && renderVboTile[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            //Draw polygons
            int start, end;
            final int vPerPolygon = 4;
            start = tile.getTexOffsetsQuad().get(k);
            if (k + 1 < tile.getTextureIDs().size()) {
                end = (tile.getTexOffsetsQuad().get(k + 1));
            } else {
                end = tile.getVCoordsQuad().length / (3 * vPerPolygon);
            }

            gl.glBegin(GL_QUADS);
            for (int i = start; i < end; i++) {
                for (int j = 0; j < vPerPolygon; j++) {
                    //gl.glTexCoord2fv(tile.getTCoordsQuad(), (i * vPerPolygon + j) * 2);
                    gl.glVertex3fv(tile.getVCoordsQuad(), (i * vPerPolygon + j) * 3);
                }
            }
            gl.glEnd();
        }
    }

    private void drawTrisWireframe(GL2 gl, Tile tile, int vboID) {
        if (!(renderVboTile[vboID] && renderVboTile[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            //Draw polygons
            int start, end;
            final int vPerPolygon = 3;
            start = tile.getTexOffsetsTri().get(k);
            if (k + 1 < tile.getTextureIDs().size()) {
                end = (tile.getTexOffsetsTri().get(k + 1));
            } else {
                end = tile.getVCoordsTri().length / (3 * vPerPolygon);
            }

            gl.glBegin(GL_TRIANGLES);
            for (int i = start; i < end; i++) {
                for (int j = 0; j < vPerPolygon; j++) {
                    //gl.glTexCoord2fv(tile.getTCoordsTri(), (i * vPerPolygon + j) * 2);
                    gl.glVertex3fv(tile.getVCoordsTri(), (i * vPerPolygon + j) * 3);
                }
            }
            gl.glEnd();
        }
    }

    private void drawQuadsPoints(GL2 gl, Tile tile, int vboID) {
        if (!(renderVboTile[vboID] && renderVboTile[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            //Draw polygons
            int start, end;
            final int vPerPolygon = 4;
            start = tile.getTexOffsetsQuad().get(k);
            if (k + 1 < tile.getTextureIDs().size()) {
                end = (tile.getTexOffsetsQuad().get(k + 1));
            } else {
                end = tile.getVCoordsQuad().length / (3 * vPerPolygon);
            }

            gl.glEnable(GL2.GL_PROGRAM_POINT_SIZE_EXT);
            gl.glPointSize(4);

            gl.glBegin(GL_POINTS);
            for (int i = start; i < end; i++) {
                for (int j = 0; j < vPerPolygon; j++) {
                    //gl.glTexCoord2fv(tile.getTCoordsQuad(), (i * vPerPolygon + j) * 2);
                    gl.glColor3fv(tile.getColorsQuad(), (i * vPerPolygon + j) * 3);
                    gl.glVertex3fv(tile.getVCoordsQuad(), (i * vPerPolygon + j) * 3);
                }
            }
            gl.glEnd();
        }
    }

    private void drawTrisPoints(GL2 gl, Tile tile, int vboID) {
        if (!(renderVboTile[vboID] && renderVboTile[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            //Draw polygons
            int start, end;
            final int vPerPolygon = 3;
            start = tile.getTexOffsetsTri().get(k);
            if (k + 1 < tile.getTextureIDs().size()) {
                end = (tile.getTexOffsetsTri().get(k + 1));
            } else {
                end = tile.getVCoordsTri().length / (3 * vPerPolygon);
            }

            gl.glEnable(GL2.GL_PROGRAM_POINT_SIZE_EXT);
            gl.glPointSize(4);

            gl.glBegin(GL_POINTS);
            for (int i = start; i < end; i++) {
                for (int j = 0; j < vPerPolygon; j++) {
                    //gl.glTexCoord2fv(tile.getTCoordsTri(), (i * vPerPolygon + j) * 2);
                    gl.glColor3fv(tile.getColorsTri(), (i * vPerPolygon + j) * 3);
                    gl.glVertex3fv(tile.getVCoordsTri(), (i * vPerPolygon + j) * 3);
                }
            }
            gl.glEnd();
        }
    }

    private void drawPoints(GL2 gl, float[] pointCoords, int pointSize) {
        final int coordsPerPoint = 3;
        if (pointCoords.length > 0) {
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

            final int nPoints = pointCoords.length / coordsPerPoint;

            gl.glEnable(GL2.GL_PROGRAM_POINT_SIZE_EXT);
            gl.glPointSize(pointSize);

            gl.glBegin(GL_POINTS);
            for (int k = 0; k < nPoints; k++) {
                gl.glVertex3fv(pointCoords, k * coordsPerPoint);
            }
            gl.glEnd();
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

        drawTile(textureMode);

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

        drawTile(textureMode);

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

        drawTile(wireframeMode);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

    }

    public void drawVertexPoints() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_ALWAYS);

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0.9f);

        gl.glDisable(GL_TEXTURE_2D);

        //gl.glColor3f(1.0f, 1.0f, 1.0f);
        drawTile(pointMode);

    }

    public void drawFaceCenterPoints() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        /*
        gl.glDisable(GL_DEPTH_TEST);
       
        gl.glColor3f(0.7f, 0.7f, 0.7f);
        
        drawPoints(gl, fCoordsTri, 3);
        drawPoints(gl, fCoordsQuad, 3);
         */
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_ALWAYS);

        gl.glColor3f(0.0f, 0.0f, 0.0f);

        drawPoints(gl, fCoordsTri, 4);
        drawPoints(gl, fCoordsQuad, 4);
    }

    public void drawFaceSelected() {
        if (faceSelected != null) {
            GL2 gl = (GL2) GLContext.getCurrentGL();

            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

            gl.glEnable(GL_DEPTH_TEST);
            gl.glDepthFunc(GL_ALWAYS);

            gl.glColor3f(1.0f, 0.0f, 0.0f);

            gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

            gl.glDisable(GL_TEXTURE_2D);

            gl.glLineWidth(3.0f);

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

            int numVertices;
            float[] vArray;
            int mode;
            if (faceSelected.isQuad) {
                numVertices = 4;
                vArray = tile.getVCoordsQuad();
                mode = GL_QUADS;
            } else {
                numVertices = 3;
                vArray = tile.getVCoordsTri();
                mode = GL_TRIANGLES;
            }

            gl.glBegin(mode);
            for (int i = 0; i < numVertices; i++) {
                gl.glVertex3fv(vArray, faceSelected.faceIndex * numVertices * 3 + i * 3);
            }
            gl.glEnd();

            gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
    }

    public void swapVBOs(int i1, int i2) {
        boolean tempDoRender;
        for (int i = 0; i < vbosPerTile; i++) {
            tempDoRender = renderVboTile[i1 * vbosPerTile + i];
            renderVboTile[i1 * vbosPerTile + i] = renderVboTile[i2 * vbosPerTile + i];
            renderVboTile[i2 * vbosPerTile + i] = tempDoRender;
        }
    }

    public void moveVBOs(ArrayList<Integer> indices) {
        boolean[] renderVboTileCopy = new boolean[renderVboTile.length];
        for (int i = 0; i < indices.size(); i++) {
            int index = indices.get(i);
            System.arraycopy(renderVboTile, index * vbosPerTile, renderVboTileCopy, i * vbosPerTile, vbosPerTile);
        }
        renderVboTile = renderVboTileCopy;
    }

    public void setHandler(MapEditorHandler handler, TilesetEditorHandler tsetHandler, VColorEditorDialog dialog) {
        this.handler = handler;
        this.tsetHandler = tsetHandler;
        this.dialog = dialog;
    }

    public void setWireframe(boolean enabled) {
        this.wireframeEnabled = enabled;
    }

    public void setBackfaceCulling(boolean enabled) {
        this.backfaceCullingEnabled = enabled;
    }

    public ArrayList<Point3D> getVerticesOnScreen(float[] vertexArray) {
        ArrayList<Point3D> points = new ArrayList<>();

        if (handler != null) {
            if (handler.getTileset().size() > 0) {
                Matrix3D transformationMatrix = getTransformationMatrix();
                float[] vCoords = applyTransformation(vertexArray, transformationMatrix, getWidth(), getHeight());

                final int numPoints = vertexArray.length / 3;
                points = new ArrayList<>(numPoints);
                for (int i = 0; i < vCoords.length / 3; i++) {
                    points.add(new Point3D(vCoords[i * 3], vCoords[i * 3 + 1], vCoords[i * 3 + 2]));
                }
            }
        }
        return points;
    }

    private float[] applyTransformation(float[] vertexArray, Matrix3D transformationMatrix, int screenWidth, int screenHeight) {
        float[] newVertexArray = new float[vertexArray.length];
        for (int i = 0; i < vertexArray.length / 3; i++) {
            Point3D vertex = new Point3D(vertexArray[i * 3], vertexArray[i * 3 + 1], vertexArray[i * 3 + 2]);
            vertex = vertex.mult(transformationMatrix);
            newVertexArray[i * 3] = (float) (((float) vertex.getX() / vertex.getW()) * screenWidth / 2 + screenWidth / 2);
            newVertexArray[i * 3 + 1] = (float) (-(float) vertex.getY() / vertex.getW() * screenHeight / 2 + screenHeight / 2);
            newVertexArray[i * 3 + 2] = (float) ((float) vertex.getZ() / vertex.getW());
        }
        return newVertexArray;
    }

    private Matrix3D getTransformationMatrix() {
        float aspect = (float) this.getWidth() / (float) this.getHeight();
        Matrix3D perspMatrix = Generator.perspective(60.0f, aspect, 1.0f, 1000.0f);

        Matrix3D transformationMatrix = new Matrix3D();
        transformationMatrix.concatenate(perspMatrix);

        Matrix3D translationMatrix = new Matrix3D();
        translationMatrix.translate(-cameraX, -cameraY, -cameraZ);
        transformationMatrix.concatenate(translationMatrix);

        Matrix3D rotationMatrix = new Matrix3D();
        rotationMatrix.rotate(modelRotX, modelRotY, modelRotZ);
        transformationMatrix.concatenate(rotationMatrix);

        return transformationMatrix;
    }

    private void paintVertexColors(int mouseX, int mouseY, float[] vertexArray, float[] colorArray) {
        if (colorArray != null && vertexArray != null) {
            if (colorArray.length == vertexArray.length) {
                Color c = dialog.getSelectedColor();
                ArrayList<Point3D> points = getVerticesOnScreen(vertexArray);

                for (int i = 0; i < points.size(); i++) {
                    int x = (int) points.get(i).getX();
                    int y = (int) points.get(i).getY();

                    int dist = (int) Math.sqrt((x - mouseX) * (x - mouseX) + (y - mouseY) * (y - mouseY));
                    if (dist < brushRadius) {
                        colorArray[i * 3] = c.getRed() / 255f;
                        colorArray[i * 3 + 1] = c.getGreen() / 255f;
                        colorArray[i * 3 + 2] = c.getBlue() / 255f;
                    }
                }
            }
        }
    }

    private void paintVertexColorsInFace(int mouseX, int mouseY, float[] vertexArray, float[] colorArray, int faceIndex, int vertexPerFace) {
        if (colorArray != null && vertexArray != null) {
            if (colorArray.length == vertexArray.length) {
                Color c = dialog.getSelectedColor();
                ArrayList<Point3D> points = getVerticesOnScreen(vertexArray);

                for (int i = 0; i < points.size(); i++) {
                    int x = (int) points.get(i).getX();
                    int y = (int) points.get(i).getY();

                    int fIndex = i / vertexPerFace;
                    if (fIndex == faceIndex) {
                        int dist = (int) Math.sqrt((x - mouseX) * (x - mouseX) + (y - mouseY) * (y - mouseY));
                        if (dist < brushRadius) {
                            colorArray[i * 3] = c.getRed() / 255f;
                            colorArray[i * 3 + 1] = c.getGreen() / 255f;
                            colorArray[i * 3 + 2] = c.getBlue() / 255f;
                        }
                    }
                }
            }
        }
    }

    private Color grabColor(int mouseX, int mouseY) {
        Color c = null;

        Tile tile = handler.getTileSelected();
        ArrayList<Point3D> fPointsQuads = getVerticesOnScreen(tile.getVCoordsQuad());
        ArrayList<Point3D> fPointsTris = getVerticesOnScreen(tile.getVCoordsTri());
        int closerQuadIndex = getCloserVertexIndex(mouseX, mouseY, colorGrabRadius, fPointsQuads);
        int closerTriIndex = getCloserVertexIndex(mouseX, mouseY, colorGrabRadius, fPointsTris);
        if (closerQuadIndex != -1 && closerTriIndex != -1) {
            float quadZ = (float) fPointsQuads.get(closerQuadIndex).getZ();
            float triZ = (float) fPointsTris.get(closerTriIndex).getZ();
            if (quadZ < triZ) {
                c = getColorFromArray(tile.getColorsQuad(), closerQuadIndex);
            } else {
                c = getColorFromArray(tile.getColorsTri(), closerTriIndex);
            }
        } else if (closerQuadIndex != -1) {
            c = getColorFromArray(tile.getColorsQuad(), closerQuadIndex);
        } else if (closerTriIndex != -1) {
            c = getColorFromArray(tile.getColorsTri(), closerTriIndex);
        }
        return c;
    }

    private Color getColorFromArray(float[] array, int vertexIndex) {
        return new Color(array[vertexIndex * 3],
                array[vertexIndex * 3 + 1],
                array[vertexIndex * 3 + 2]);
    }

    private int getCloserVertexIndex(int mouseX, int mouseY, int radius, ArrayList<Point3D> pointsOnScreen) {
        float minZ = 2.0f;
        int index = -1;

        for (int i = 0; i < pointsOnScreen.size(); i++) {
            int x = (int) pointsOnScreen.get(i).getX();
            int y = (int) pointsOnScreen.get(i).getY();
            float z = (int) pointsOnScreen.get(i).getZ();

            int dist = (int) Math.sqrt((x - mouseX) * (x - mouseX) + (y - mouseY) * (y - mouseY));
            if (dist < radius) {
                if (z < minZ) {
                    index = i;
                    minZ = z;
                }
            }
        }
        return index;
    }

    private float[] calculateFaceCoords(float[] vCoords, int vPerFace) {
        final int coordsPerVertex = 3;
        float[] fCoords = new float[vCoords.length / vPerFace];
        int numFaces = vCoords.length / (vPerFace * coordsPerVertex);
        for (int i = 0, c = 0; i < numFaces; i++) {
            float[] coordsMean = new float[coordsPerVertex];
            for (int j = 0; j < vPerFace; j++) {
                for (int k = 0; k < coordsPerVertex; k++, c++) {
                    coordsMean[k] += vCoords[c] / vPerFace;
                }
            }
            for (int j = 0; j < coordsPerVertex; j++) {
                fCoords[i * coordsPerVertex + j] = coordsMean[j];
            }
        }
        return fCoords;
    }

    public void setSelectionMode(int mode) {
        this.selectionMode = mode;
        if (mode == COLOR_GRAB_MODE) {
            this.setCursor(colorGrabCursor);
        } else {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void setDrawTextures(boolean drawTextures) {
        this.drawTextures = drawTextures;
    }

    private class FaceSelection {

        private int faceIndex;
        private boolean isQuad;

        public FaceSelection(int faceIndex, boolean isQuad) {
            this.faceIndex = faceIndex;
            this.isQuad = isQuad;
        }

        public int getFaceIndex() {
            return faceIndex;
        }

        public boolean isQuad() {
            return isQuad;
        }
    }
    
    public void setBrushSize(int size){
        this.brushRadius = size;
    }

}
