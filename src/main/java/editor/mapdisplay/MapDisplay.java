
package editor.mapdisplay;

import com.jogamp.common.nio.Buffers;
import editor.handler.MapEditorHandler;

import static com.jogamp.opengl.GL.GL_BACK;
import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_GREATER;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LESS;
import static com.jogamp.opengl.GL.GL_LINEAR_MIPMAP_LINEAR;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_NEAREST;
import static com.jogamp.opengl.GL.GL_NOTEQUAL;
import static com.jogamp.opengl.GL.GL_NO_ERROR;
import static com.jogamp.opengl.GL.GL_ONE;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_DST_ALPHA;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_RGBA;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_UNSIGNED_BYTE;

import com.jogamp.opengl.GL2;

import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static com.jogamp.opengl.GL2ES3.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.Texture;
import editor.bordermap.BorderMapsGrid;
import editor.grid.GeometryGL;
import editor.grid.MapGrid;
import editor.grid.MapLayerGL;
import editor.handler.MapData;
import editor.state.MapLayerState;
import geometry.Generator;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;

import math.mat.Mat4f;
import math.transf.TransfMat;
import math.vec.Vec3f;
import math.vec.Vec4f;
import tileset.Tile;
import utils.ImageTiler;
import utils.Utils;

/**
 * @author Trifindo
 */
public class MapDisplay extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    //Editor Handler
    protected MapEditorHandler handler;

    //Grid
    protected final int cols = 32;
    protected final int rows = 32;
    protected final int tileSize = 16;
    protected final int borderSize = 1;
    protected final int width = (cols + borderSize * 2) * tileSize;
    protected final int height = (rows + borderSize * 2) * tileSize;
    protected final float gridTileSize = 1.0f;
    protected float orthoScale = 1.0f;

    //OpenGL
    protected GLU glu;
    protected float[] grid;
    protected FloatBuffer gridBuffer;
    protected float[] axis;
    protected final float[] axisColors = {
            1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};

    //Scene
    protected float cameraX, cameraY, cameraZ;
    protected float targetX, targetY, targetZ;
    protected float cameraRotX, cameraRotY, cameraRotZ;
    protected static final float defaultCamRotX = 40.0f, defaultCamRotY = 0.0f, defaultCamRotZ = 0.0f;
    protected Mat4f camModelView = new Mat4f();
    protected Mat4f camProj = new Mat4f();
    protected Mat4f camMVP = new Mat4f();
    protected final Vec4f[] cornerDeltas = new Vec4f[]{
            new Vec4f(-MapGrid.cols / 2, -MapGrid.rows / 2, 0.0f, 0.0f),
            new Vec4f(+MapGrid.cols / 2, -MapGrid.rows / 2, 0.0f, 0.0f),
            new Vec4f(+MapGrid.cols / 2, +MapGrid.rows / 2, 0.0f, 0.0f),
            new Vec4f(-MapGrid.cols / 2, +MapGrid.rows / 2, 0.0f, 0.0f)
    };
    protected final float fovDeg = 60.0f;
    //protected HashMap<Point, MapData> filteredMaps;
    //protected Vec3f[][] frustum;
    //protected final float zNear = 1.0f;
    //protected final float zFar = 1000.0f;

    //Scene displays
    protected boolean drawGridEnabled = true;
    protected boolean drawWireframeEnabled = false;
    protected boolean drawAreasEnabled = true;
    protected boolean drawGridBorderMaps = true;

    //Mouse events
    protected int lastMouseX, lastMouseY;
    protected int xMouse, yMouse;
    protected Point dragStart = new Point();
    protected Set<Point> editedMapCoords = new HashSet<>();

    //Keyboard events
    protected boolean CONTROL_PRESSED = false;
    protected boolean SHIFT_PRESSED = false;

    //Height map
    protected float heightMapOpacity = 1.0f;

    //Update
    protected boolean updateRequested = false;

    //Screenshot
    protected BufferedImage screenshot;
    protected boolean screenshotRequested = false;

    //Background Image 
    protected BufferedImage backImage = null;
    protected boolean backImageEnabled = false;
    protected float backImageAlpha = 0.5f;

    //View Modes
    protected ViewMode viewMode = ViewMode.VIEW_ORTHO_MODE;

    //Edit Modes
    public static enum EditMode {

        MODE_EDIT(new Cursor(Cursor.DEFAULT_CURSOR)),
        MODE_MOVE(new Cursor(Cursor.MOVE_CURSOR)),
        MODE_ZOOM(Utils.loadCursor("/cursors/zoomCursor.png")),
        MODE_CLEAR(Utils.loadCursor("/cursors/clearTileCursor.png")),
        MODE_SMART_PAINT(Utils.loadCursor("/cursors/smartGridCursor.png")),
        MODE_INV_SMART_PAINT(Utils.loadCursor("/cursors/smartGridInvertedCursor.png"));

        public final Cursor cursor;

        private EditMode(Cursor cursor) {
            this.cursor = cursor;
        }

    }

    ;
    protected EditMode editMode = EditMode.MODE_EDIT;

    public MapDisplay() {
        //Set default display size
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


        //Create custom cursors
        //smartGridCursor = Utils.loadCursor("/cursors/smartGridCursor.png");
        //smartGridInvertedCursor = Utils.loadCursor("/cursors/smartGridInvertedCursor.png");
        //clearTileCursor = Utils.loadCursor("/cursors/clearTileCursor.png");
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        grid = Generator.generateCenteredGrid(cols, rows, gridTileSize, 0.02f);
        gridBuffer = Buffers.newDirectFloatBuffer(grid);
        axis = Generator.generateAxis(100.0f);

        //Load textures into OpenGL
        handler.getTileset().loadTexturesGL();
        handler.getBorderMapsTileset().loadTexturesGL();

        drawable.getGL().getGL2().glClearColor(0.0f, 0.5f, 0.5f, 1.0f);

        //Scene
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 32.0f;

        cameraRotX = 0.0f;
        cameraRotY = 0.0f;
        cameraRotZ = 0.0f;

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose!! ");
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //gl.glLoadIdentity();
        //lighting(gl);

        applyCameraTransform(gl);

        /*
        gl.glPushMatrix();
        gl.glLoadIdentity();

        Vec3f point = new Vec4f(8.0f, 8.0f, 0.0f, 1.0f).mul(camMVP).toVec3f();
        gl.glPointSize(5.0f);

        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex2f(point.x, point.y);
        gl.glEnd();

        gl.glPopMatrix();
        //point.print();*/

        if (updateRequested) {

            //Load Textures into OpenGL
            handler.getTileset().loadTexturesGL();
            handler.getBorderMapsTileset().loadTexturesGL();

            updateMapLayersGL();

            updateRequested = false;
        }

        try {

            Vec3f[][] frustum = viewMode.getFrustumPlanes(this);
            HashMap<Point, MapData> filteredMaps = getMapsInsideFrustum(frustum);

            //gl.glEnable(GL2.GL_LIGHTING);

            //Draw opaque tiles
            if (handler.getTileset().size() > 0) {
                drawOpaqueMaps(gl, filteredMaps);
            }

            //Draw semitransparent tiles
            if (handler.getTileset().size() > 0) {
                drawTransparentMaps(gl, filteredMaps);
            }

            //gl.glDisable(GL2.GL_LIGHTING);

            //Draw grid
            if (drawGridEnabled) {
                drawGridMaps(gl);
            }

            //Draw axis
            drawAxis();

            if (drawWireframeEnabled) {
                if (handler.getTileset().size() > 0) {
                    drawWireframeMaps(gl, filteredMaps);
                }
            }

            if (drawGridBorderMaps) {
                drawGridBorderMaps(gl);
            }

            if (drawAreasEnabled) {
                drawAllContourLines(gl);
            }

            //Screenshot
            if (screenshotRequested) {
                drawScreenshot(gl);
                screenshotRequested = false;
            }

            gl.glFinish();
        } catch (GLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        viewMode.mousePressed(this, e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        viewMode.mouseReleased(this, e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        viewMode.mouseDragged(this, e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        viewMode.mouseMoved(this, e);

        if (!hasFocus()) {
            requestFocusInWindow();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        viewMode.keyPressed(this, e);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                setEditMode(EditMode.MODE_EDIT);
                handler.getMainFrame().getJtbModeEdit().setSelected(true);
                repaint();
                break;
            case KeyEvent.VK_SHIFT:
                SHIFT_PRESSED = true;
                break;
            case KeyEvent.VK_E:
                setEditMode(EditMode.MODE_EDIT);
                break;
            case KeyEvent.VK_G:
                toggleGridView();
                handler.getMainFrame().getJtbViewGrid().setSelected(drawGridEnabled);
                repaint();
                break;
            case KeyEvent.VK_W:
                drawWireframeEnabled = !drawWireframeEnabled;
                handler.getMainFrame().getJtbViewWireframe().setSelected(drawWireframeEnabled);
                repaint();
                break;
            case KeyEvent.VK_A:
                drawAreasEnabled = !drawAreasEnabled;
                handler.getMainFrame().getJcbViewAreas().setSelected(drawAreasEnabled);
                repaint();
                break;
            case KeyEvent.VK_F:
                setCameraAtSelectedMap();
                repaint();
                break;
            case KeyEvent.VK_Q://TODO: DELETE THIS
                BufferedImage img = Utils.getImageFromClipboard();
                if (img != null) {
                    handler.addMapState(new MapLayerState("Import map as image", handler));
                    handler.getGrid().setTileLayer(handler.getActiveLayerIndex(),
                            ImageTiler.imageToTileLayer(img, handler.getTileset(), cols, rows, tileSize)
                    );
                    updateActiveMapLayerGL();
                    repaint();
                    //setBackImage(img);
                    //backImageEnabled = true;
                    //repaint();
                }
                break;
            case KeyEvent.VK_Z:
                if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    handler.getMainFrame().undoMapState();
                }
                break;
            case KeyEvent.VK_Y:
                if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    handler.getMainFrame().redoMapState();
                }
                break;
            case KeyEvent.VK_B:
                drawGridBorderMaps = !drawGridBorderMaps;
                repaint();
                break;
            case KeyEvent.VK_1:
                changeLayerWithNumKey(e, 0);
                repaint();
                break;
            case KeyEvent.VK_2:
                changeLayerWithNumKey(e, 1);
                repaint();
                break;
            case KeyEvent.VK_3:
                changeLayerWithNumKey(e, 2);
                repaint();
                break;
            case KeyEvent.VK_4:
                changeLayerWithNumKey(e, 3);
                repaint();
                break;
            case KeyEvent.VK_5:
                changeLayerWithNumKey(e, 4);
                repaint();
                break;
            case KeyEvent.VK_6:
                changeLayerWithNumKey(e, 5);
                repaint();
                break;
            case KeyEvent.VK_7:
                changeLayerWithNumKey(e, 6);
                repaint();
                break;
            case KeyEvent.VK_8:
                changeLayerWithNumKey(e, 7);
                repaint();
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            SHIFT_PRESSED = false;
            //disableCameraMove();
            repaint();
        }

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        viewMode.mouseWheelMoved(this, e);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handler != null) {
            viewMode.paintComponent(this, g);

            if (backImageEnabled) {
                drawBackImage(g);
            }
        }

    }

    protected void applyGraphicsTransform(Graphics2D g2d) {
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
        /*
                g2d.setRenderingHints(new RenderingHints(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR));*/


        float xScaleWindows = (float) getWidth() / width;
        float yScaleWindows = (float) getHeight() / height;
        g2d.scale(xScaleWindows, yScaleWindows);

        //TODO: Use this code for keeping the aspect ratio
        //g2d.scale(yScaleWindows, yScaleWindows);
        //float aspect = getAspectRatio();
        //g2d.translate((aspect - 1.0f) * width / 2, 1.0f);


        float xScaleFactor = orthoScale;
        float yScaleFactor = orthoScale;

        float xTranslation = (getWidth() * (1.0f - xScaleFactor) / 2f);
        float yTranslation = (getHeight() * (1.0f - yScaleFactor) / 2f);

        g2d.translate(xTranslation / xScaleWindows, yTranslation / yScaleWindows);
        //g2d.translate(xTranslation / yScaleWindows, yTranslation / yScaleWindows);
        g2d.scale(xScaleFactor, yScaleFactor);

        g2d.translate(-cameraX * tileSize, cameraY * tileSize);
    }

    protected void drawTileThumbnail(Graphics g) {
        if (handler.getTileset().size() > 0) {
            Tile tile = handler.getTileset().get(handler.getTileIndexSelected());
            int x = Math.floorDiv(xMouse, tileSize) * tileSize;
            int y = (Math.floorDiv(yMouse, tileSize) - (tile.getHeight() - 1)) * tileSize;

            g.drawImage(tile.getThumbnail(), x, y, null);

            g.setColor(Color.red);
            g.drawRect(x, y, tile.getWidth() * tileSize, tile.getHeight() * tileSize);
        }
    }

    protected void drawUnitTileBounds(Graphics g) {
        int x = Math.floorDiv(xMouse, tileSize) * tileSize;
        int y = Math.floorDiv(yMouse, tileSize) * tileSize;
        g.setColor(Color.red);
        g.drawRect(x, y, tileSize, tileSize);
    }

    protected void drawAllHeightMaps(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(heightMapOpacity));

        for (HashMap.Entry<Point, MapData> map : handler.getMapMatrix().getMatrix().entrySet()) {
            drawHeightMap(g, map.getValue().getGrid().heightLayers[handler.getActiveLayerIndex()],
                    map.getKey().x * cols * tileSize, map.getKey().y * rows * tileSize);
        }
    }

    protected void drawHeightMap(Graphics g, int[][] heightGrid, int xOffset, int yOffset) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int x = (i + borderSize) * tileSize + xOffset;
                int y = (rows - 1 - j + borderSize) * tileSize + yOffset;
                g.drawImage(handler.getHeightImageByValue(heightGrid[i][j]), x, y, null);
            }
        }
    }

    protected void drawActiveHeightMap(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(heightMapOpacity));

        Point map = handler.getMapSelected();
        drawHeightMap(g, handler.getActiveHeightLayer(), map.x * cols * tileSize, map.y * rows * tileSize);
    }

    protected void drawHeightMapsBorder(Graphics g, int borderSize) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(heightMapOpacity));

        Point centerMap = handler.getMapSelected();
        for (int i = -borderSize; i <= borderSize; i++) {
            for (int j = -borderSize; j <= borderSize; j++) {
                Point map = new Point(centerMap.x + i, centerMap.y + j);
                MapData mapData = handler.getMapMatrix().getMatrix().get(map);
                if (mapData != null) {
                    drawHeightMap(g, mapData.getGrid().heightLayers[handler.getActiveLayerIndex()],
                            map.x * cols * tileSize, map.y * rows * tileSize);
                }
            }
        }
        //drawHeightMap(g, handler.getActiveHeightLayer(), centerMap.x * cols * tileSize, centerMap.y * rows * tileSize);
    }

    protected void drawAllMapBounds(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        Set<Point> maps = handler.getMapMatrix().getMatrix().keySet();
        for (Point map : maps) {
            drawBorderBounds(g2, map.x * cols * tileSize, map.y * rows * tileSize, 0);
        }
    }

    protected void drawAllMapContours(Graphics g) {
        HashMap<Integer, ArrayList<Point>> allContourPoints = handler.getMapMatrix().getContourPoints();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(4));

        AffineTransform transf = g2d.getTransform();

        g2d.translate(tileSize, tileSize);

        for (HashMap.Entry<Integer, ArrayList<Point>> entry : allContourPoints.entrySet()) {
            ArrayList<Point> contourPoints = entry.getValue();
            for (int i = 0; i < contourPoints.size(); i += 2) {
                try {
                    g.setColor(handler.getMapMatrix().getAreaColors().get(entry.getKey()));
                } catch (Exception ex) {
                    g.setColor(Color.blue);
                }
                Point p1 = contourPoints.get(i);
                Point p2 = contourPoints.get(i + 1);
                g.drawLine(
                        p1.x * cols * tileSize,
                        p1.y * rows * tileSize,
                        p2.x * cols * tileSize,
                        p2.y * rows * tileSize);
            }
        }

        g2d.setTransform(transf);
    }

    protected void drawBorderBounds(Graphics g, int xOffset, int yOffset, int outOffset) {
        g.drawRect(borderSize * tileSize + xOffset - outOffset, borderSize * tileSize + yOffset - outOffset,
                tileSize * cols + 2 * outOffset, tileSize * rows + 2 * outOffset);
    }

    protected void drawGrid(GL2 gl, float x, float y, float z, float r, float g, float b, float a) {
        //applyCameraTransform(gl);

        gl.glPushMatrix();

        gl.glTranslatef(x, y, z);

        //Adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glLineWidth(1);

        //gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glDisable(GL_TEXTURE_2D);
        gl.glColor4f(r, g, b, a);

        drawLines(gl, gridBuffer);

        gl.glColor4f(1, 1, 1, 1);

        gl.glPopMatrix();
    }

    protected void drawGridBorderMaps(GL2 gl) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        for (Point borderMap : handler.getMapMatrix().getBorderMaps()) {
            drawGrid(gl, borderMap.x * cols, -borderMap.y * rows, 0, 1.0f, 1.0f, 1.0f, 0.2f);
        }
    }

    protected void drawGridMaps(GL2 gl) {
        Set<Point> maps = handler.getMapMatrix().getMatrix().keySet();
        Point mapSelected = handler.getMapSelected();
        for (Point map : maps) {
            if (!map.equals(mapSelected)) {
                drawGrid(gl, map.x * cols, -map.y * rows, 0, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        drawGrid(gl, mapSelected.x * cols, -mapSelected.y * rows, 0, 1.0f, 0.9f, 0.9f, 1.0f);
    }

    protected void drawAxis() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        //applyCameraTransform(gl);

        gl.glDisable(GL_TEXTURE_2D);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glLineWidth(1f);

        gl.glBegin(GL_LINES);
        for (int i = 0; i < axis.length; i += 3) {
            gl.glColor3fv(axisColors, i);
            gl.glVertex3fv(axis, i);
        }

        gl.glEnd();
    }

    protected void drawOpaqueMaps(GL2 gl, HashMap<Point, MapData> maps) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0.9f);

        //long before = System.nanoTime();
        drawAllMaps(gl, maps, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });
        //System.out.println("Elapsed: " + (System.nanoTime() - before));
    }

    protected void drawTransparentMaps(GL2 gl, HashMap<Point, MapData> maps) {
        gl.glEnable(GL_BLEND);

        gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);

        drawAllMaps(gl, maps, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });
    }

    protected void drawWireframeMaps(GL2 gl, HashMap<Point, MapData> maps) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glColor3f(0.0f, 0.0f, 0.0f);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        gl.glDisable(GL_TEXTURE_2D);
        gl.glLineWidth(1.5f);

        drawAllMaps(gl, maps, (gl2, geometryGL, textures) -> {
            drawWireframeGeometryGL(gl2, geometryGL, textures);
        });

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    protected void drawAllMaps(GL2 gl, HashMap<Point, MapData> maps, DrawGeometryGLFunction drawFunction) {

        for (HashMap.Entry<Point, MapData> map : maps.entrySet()) {

            drawAllMapLayersGL(gl, drawFunction, map.getValue().getGrid().mapLayersGL,
                    map.getKey().x * cols, -map.getKey().y * rows, 0);


            /*
            //Simple optimization culling map corners
            for(Vec4f cornerDelta : cornerDeltas){
                Vec3f mapCenter = new Vec4f(
                        map.getKey().x * MapGrid.cols,
                        -map.getKey().y * MapGrid.rows,
                        0.0f,
                        1.0f).add(cornerDelta).mul(camMVP).toVec3f();

                gl.glPushMatrix();
                gl.glLoadIdentity();

                gl.glPointSize(5.0f);

                gl.glBegin(GL2.GL_POINTS);
                gl.glVertex2f(mapCenter.x, mapCenter.y);
                gl.glEnd();

                gl.glPopMatrix();

                if (mapCenter.x > -1.0f && mapCenter.x < 1.0f && mapCenter.y > -1.0f && mapCenter.y < 1.0f && mapCenter.z > 0.0f) {
                    drawAllMapLayersGL(gl, drawFunction, map.getValue().getGrid().mapLayersGL,
                            map.getKey().x * cols, -map.getKey().y * rows, 0);
                    break;
                }
            }
            */


            /*
            //Simple optimization culling map centers
            Vec3f mapCenter = new Vec4f(
                    map.getKey().x * MapGrid.cols,
                    -map.getKey().y * MapGrid.rows,
                    0.0f,
                    1.0f).mul(camMVP).toVec3f();



            gl.glPushMatrix();
            gl.glLoadIdentity();

            gl.glPointSize(5.0f);

            gl.glBegin(GL2.GL_POINTS);
            gl.glVertex2f(mapCenter.x, mapCenter.y);
            gl.glEnd();

            gl.glPopMatrix();


            if (mapCenter.x > -1.0f && mapCenter.x < 1.0f && mapCenter.y > -1.0f && mapCenter.y < 1.0f && mapCenter.z > 0.0f) {
                drawAllMapLayersGL(gl, drawFunction, map.getValue().getGrid().mapLayersGL,
                        map.getKey().x * cols, -map.getKey().y * rows, 0);
            }
            */

            /*
            drawAllMapLayersGL(gl, drawFunction, map.getValue().getGrid().mapLayersGL,
                    map.getKey().x * cols, -map.getKey().y * rows, 0);
            */
        }
    }

    protected void drawAllMapLayersGL(GL2 gl, DrawGeometryGLFunction drawFunction, MapLayerGL[] mapLayersGL, float x, float y, float z) {
        for (int i = 0; i < mapLayersGL.length; i++) {
            if (handler.renderLayers[i]) {
                if (mapLayersGL[i] != null) {
                    drawMapLayerGL(gl, drawFunction, mapLayersGL[i], x, y, z);
                }
            }
        }
    }

    protected void drawMapLayerGL(GL2 gl, DrawGeometryGLFunction drawFunction, MapLayerGL mapLayerGL, float x, float y, float z) {
        //applyCameraTransform(gl);
        gl.glPushMatrix();

        gl.glTranslatef(x, y, z);

        for (GeometryGL geometryGL : mapLayerGL.getGeometryGL().values()) {
            drawFunction.draw(gl, geometryGL, handler.getTileset().getTextures());
            //drawGeometryGL(gl, geometryGL, handler.getTileset().getTextures());
        }

        gl.glPopMatrix();
    }

    protected void drawGeometryGL(GL2 gl, GeometryGL geometryGL, ArrayList<Texture> textures) {
        try {
            gl.glBindTexture(GL_TEXTURE_2D, textures.get(geometryGL.textureID).getTextureObject());
            gl.glEnable(GL_TEXTURE_2D);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            //gl.glGenerateMipmap(GL_TEXTURE_2D);
            //Draw Tris
            if (geometryGL.hasTriBufferData()) {
                try {
                    gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                    gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
                    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
                    //gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

                    gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, geometryGL.tCoordsTriBuffer);
                    gl.glColorPointer(3, GL2.GL_FLOAT, 0, geometryGL.colorsTriBuffer);
                    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, geometryGL.vCoordsTriBuffer);
                    //gl.glNormalPointer(GL2.GL_FLOAT, 0, geometryGL.nCoordsTriBuffer);

                    gl.glDrawArrays(GL2.GL_TRIANGLES, 0, geometryGL.vCoordsTri.length / 3);

                    gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
                    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
                    //gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);

                } catch (Exception ex) {
                    gl.glBegin(GL_TRIANGLES);
                    for (int i = 0, numVertices = geometryGL.vCoordsTri.length / 3; i < numVertices; i++) {
                        gl.glTexCoord2fv(geometryGL.tCoordsTri, i * 2);
                        gl.glColor3fv(geometryGL.colorsTri, i * 3);
                        gl.glVertex3fv(geometryGL.vCoordsTri, i * 3);
                    }
                    gl.glEnd();
                }
            }

            //Draw Quads
            if (geometryGL.hasQuadBufferData()) {
                try {
                    gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                    gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
                    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
                    //gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

                    gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, geometryGL.tCoordsQuadBuffer);
                    gl.glColorPointer(3, GL2.GL_FLOAT, 0, geometryGL.colorsQuadBuffer);
                    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, geometryGL.vCoordsQuadBuffer);
                    //gl.glNormalPointer(GL2.GL_FLOAT, 0, geometryGL.nCoordsQuadBuffer);

                    gl.glDrawArrays(GL2.GL_QUADS, 0, geometryGL.vCoordsQuad.length / 3);

                    gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
                    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
                    //gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);

                } catch (Exception ex) {
                    gl.glBegin(GL_QUADS);
                    for (int i = 0, numVertices = geometryGL.vCoordsQuad.length / 3; i < numVertices; i++) {
                        gl.glTexCoord2fv(geometryGL.tCoordsQuad, i * 2);
                        gl.glColor3fv(geometryGL.colorsQuad, i * 3);
                        gl.glVertex3fv(geometryGL.vCoordsQuad, i * 3);
                    }
                    gl.glEnd();
                }
            }

            gl.glDisable(GL_TEXTURE_2D);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void drawWireframeGeometryGL(GL2 gl, GeometryGL geometryGL, ArrayList<Texture> textures) {
        try {
            //Draw Tris
            if (geometryGL.hasTriBufferData()) {
                try {
                    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

                    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, geometryGL.vCoordsTriBuffer);

                    gl.glDrawArrays(GL2.GL_TRIANGLES, 0, geometryGL.vCoordsTri.length / 3);

                    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

                } catch (Exception ex) {
                    gl.glBegin(GL_TRIANGLES);
                    for (int i = 0, numVertices = geometryGL.vCoordsTri.length / 3; i < numVertices; i++) {
                        gl.glVertex3fv(geometryGL.vCoordsTri, i * 3);
                    }
                    gl.glEnd();
                }
            }

            //Draw Quads
            if (geometryGL.hasQuadBufferData()) {
                try {
                    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

                    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, geometryGL.vCoordsQuadBuffer);

                    gl.glDrawArrays(GL2.GL_QUADS, 0, geometryGL.vCoordsQuad.length / 3);

                    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

                } catch (Exception ex) {
                    gl.glBegin(GL_QUADS);
                    for (int i = 0, numVertices = geometryGL.vCoordsQuad.length / 3; i < numVertices; i++) {
                        gl.glVertex3fv(geometryGL.vCoordsQuad, i * 3);
                    }
                    gl.glEnd();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void drawAllContourLines(GL2 gl) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDisable(GL_TEXTURE_2D);
        gl.glLineWidth(3f);

        //applyCameraTransform(gl);
        gl.glPushMatrix();

        gl.glTranslatef(-cols / 2, rows / 2, 0.025f);
        gl.glScalef(cols, -rows, 1.0f);

        for (HashMap.Entry<Integer, FloatBuffer> entry : handler.getMapMatrix().getContourPointsGL().entrySet()) {
            try {
                Color c = handler.getMapMatrix().getAreaColors().get(entry.getKey());
                gl.glColor3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
            } catch (Exception ex) {
                gl.glColor3f(0.0f, 0.0f, 1.0f);
            }

            FloatBuffer contourPoints = entry.getValue();
            drawLines(gl, contourPoints);
        }

        gl.glLineWidth(1f);

        gl.glPopMatrix();
    }

    protected void drawLines(GL2 gl, FloatBuffer vCoordsPoints) {
        try {
            if (vCoordsPoints != null) {
                gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
                gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vCoordsPoints);
                gl.glDrawArrays(GL2.GL_LINES, 0, vCoordsPoints.limit() / 3);
                gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void rotToDir(Vec3f rot, Vec3f dir) {
        dir.mul(TransfMat.eulerDegToMat_(rot));
    }

    public static Vec3f rotToDir_(Vec3f angles) {
        Vec3f dir = new Vec3f(0.0f, 0.0f, -1.0f);
        rotToDir(angles, dir);
        return dir;
    }

    public static void rotToUp(Vec3f rot, Vec3f dst){
        dst.set(0.0f, 1.0f, 0.0f);
        dst.mul(TransfMat.eulerDegToMat_(rot));
    }

    public static Vec3f rotToUp_(Vec3f rot){
        Vec3f dst = new Vec3f();
        rotToUp(rot, dst);
        return dst;
    }

    public static float distPointPlaneSigned(Vec3f point, Vec3f[] plane){
        Vec3f normal = plane[1].sub_(plane[0]).cross(plane[2].sub_(plane[0])).normalize();
        //Vec3f normal = plane[2].sub_(plane[1]).cross(plane[0].sub_(plane[1])).normalize();
        return normal.dot(point) -normal.dot(plane[0]);
    }

    protected void applyCameraTransform(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        viewMode.applyCameraTransform(this, gl);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(
                0.0f, 0.0f, cameraZ,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

        gl.glRotatef(-cameraRotX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(-cameraRotY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(-cameraRotZ, 0.0f, 0.0f, 1.0f);

        gl.glTranslatef(-cameraX, -cameraY, 0.0f);


        Mat4f rx = TransfMat.rotationDeg_(-cameraRotX, new Vec3f(1.0f, 0.0f, 0.0f));
        Mat4f ry = TransfMat.rotationDeg_(-cameraRotY, new Vec3f(0.0f, 1.0f, 0.0f));
        Mat4f rz = TransfMat.rotationDeg_(-cameraRotZ, new Vec3f(0.0f, 0.0f, 1.0f));
        Vec3f tarPos = new Vec3f(cameraX, cameraY, 0.0f);
        Vec3f camDir = rotToDir_(new Vec3f(cameraRotX, cameraRotY, cameraRotZ));
        Vec3f camPos = tarPos.add_(camDir.negate_().scale_(cameraZ));
        Mat4f t = TransfMat.translation_(camPos.negate_());
        camModelView = rx.mul_(ry).mul(rz).mul(t);
        camProj = TransfMat.perspective_(60.0f, (float) getWidth() / getHeight(), 1.0f, 1000.0f);
        camMVP = camProj.mul_(camModelView);

        /*
        System.out.println("MODELVIEW: ");
        camModelView.print();
        System.out.println("PROJECTION: ");
        camProj.print();*/

        //new Vec3f(cameraX, cameraY, cameraZ).print();
        //rotToDir_(new Vec3f(cameraRotX, cameraRotY, cameraRotZ)).print();

    }

    protected boolean isPointInsideGrid(int col, int row) {
        return new Rectangle(cols, rows).contains(col, row);
    }

    protected boolean canUseDragging(int col, int row, int tileWidth, int tileHeight) {
        return (col % tileWidth) == (dragStart.getX() % tileWidth) && (row % tileHeight) == (dragStart.getY() % tileHeight);
    }

    private void clearAreaUnderTile(int[][] tileGrid, int col, int row, int tileWidth, int tileHeight) {
        for (int i = 0; i < tileWidth; i++) {
            for (int j = 0; j < tileHeight; j++) {
                if (isPointInsideGrid(col + i, row + j)) {
                    tileGrid[col + i][row + j] = -1;
                }
            }
        }
    }

    public void updateMapLayerGL(int layerIndex) {
        MapLayerGL newLayer = new MapLayerGL(
                handler.getTileLayer(layerIndex),
                handler.getHeightLayer(layerIndex),
                handler.getTileset(),
                handler.useRealTimePostProcessing(),
                handler.getGame().getMaxTileableSize());
        handler.getGrid().mapLayersGL[layerIndex] = newLayer;
    }

    public void updateActiveMapLayerGL() {
        updateMapLayerGL(handler.getActiveLayerIndex());
    }

    public void updateMapLayersGL() {
        for (int i = 0; i < handler.getGrid().mapLayersGL.length; i++) {
            updateMapLayerGL(i);
        }
    }

    protected void updateMousePostion(MouseEvent e) {
        xMouse = (int) ((((float) e.getX() / getWidth() - (1.0f - orthoScale) / 2) * width) / orthoScale + cameraX * tileSize);
        yMouse = (int) ((((float) e.getY() / getHeight() - (1.0f - orthoScale) / 2) * height) / orthoScale - cameraY * tileSize);
    }

    protected void zoomCameraOrtho(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            orthoScale /= 1.1;
        } else {
            orthoScale *= 1.1;
        }
    }

    protected Point getCoordsInMap(MouseEvent e) {
        float x = (float) e.getX() / getWidth(); //Normalize
        x -= (1.0f - orthoScale) / 2; //Move ortho scale offset
        x *= (cols + 2 * borderSize) / orthoScale; //Apply grid size and ortho scale
        x -= borderSize; //Move border size
        x += cameraX; //Move camera

        float y = (float) e.getY() / getHeight();
        y -= (1.0f - orthoScale) / 2;
        y *= (rows + 2 * borderSize) / orthoScale;
        y = rows - y + borderSize;
        y += cameraY;

        return new Point((int) x, (int) y);
    }

    protected Point getCoordsInSelectedMap(MouseEvent e) {
        float x = (float) e.getX() / getWidth(); //Normalize
        x -= (1.0f - orthoScale) / 2; //Move ortho scale offset
        x *= (cols + 2 * borderSize) / orthoScale; //Apply grid size and ortho scale
        x -= borderSize; //Move border size
        x += cameraX; //Move camera

        float y = (float) e.getY() / getHeight();
        y -= (1.0f - orthoScale) / 2;
        y *= (rows + 2 * borderSize) / orthoScale;
        y -= borderSize;
        y -= cameraY;

        Point mapCoords = getMapCoords(e);

        int xInt = (int) x;
        int yInt = (int) y;

        if (x < 0) {
            xInt--;
        }

        if (y < 0) {
            yInt--;
        }

        xInt -= mapCoords.x * cols;
        yInt -= mapCoords.y * rows;

        xInt = Math.max(0, Math.min(cols - 1, xInt));
        yInt = rows - 1 - Math.max(0, Math.min(rows - 1, yInt));

        return new Point(xInt, yInt);
    }

    public Point getMapCoords(MouseEvent e) {
        float x = (float) e.getX() / getWidth(); //Normalize
        x -= (1.0f - orthoScale) / 2; //Move ortho scale offset
        x *= (cols + 2 * borderSize) / orthoScale; //Apply grid size and ortho scale
        x -= borderSize; //Move border size
        x += cameraX; //Move camera

        float y = (float) e.getY() / getHeight();
        y -= (1.0f - orthoScale) / 2;
        y *= (rows + 2 * borderSize) / orthoScale;
        y -= borderSize;
        y -= cameraY;

        return new Point(Math.floorDiv((int) Math.floor(x), cols), Math.floorDiv((int) Math.floor(y), rows));
    }

    public void setMapSelected(MouseEvent e) {
        Point selectedMap = getMapCoords(e);
        if (!selectedMap.equals(handler.getMapSelected())) {
            handler.setMapSelected(selectedMap);
        }
    }

    public void setMapSelectedIfExists(MouseEvent e) {
        Point selectedMap = getMapCoords(e);
        if (!selectedMap.equals(handler.getMapSelected()) && handler.mapExists(selectedMap)) {
            handler.setMapSelected(selectedMap);
        }
    }

    protected void floodFillClearTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            if (isPointInsideGrid(p.x, p.y) && handler.getActiveTileLayer()[p.x][p.y] != -1) {
                handler.getGrid().floodFillTileGrid(p.x, p.y, -1, 1, 1);
                //updateMapThumbnail(e);
            }
        }
    }

    protected void floodFillTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            if (isPointInsideGrid(p.x, p.y) && handler.getActiveTileLayer()[p.x][p.y] != handler.getTileIndexSelected()) {
                Tile tile = handler.getTileSelected();
                handler.getGrid().floodFillTileGrid(p.x, p.y, handler.getTileIndexSelected(), tile.getWidth(), tile.getHeight());
                //updateMapThumbnail(e);
            }
        }
    }

    protected void floodFillHeightInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            if (isPointInsideGrid(p.x, p.y) && handler.getActiveHeightLayer()[p.x][p.y] != handler.getHeightSelected()) {
                handler.getGrid().floodFillHeightGrid(p.x, p.y, handler.getHeightSelected());
                //updateMapThumbnail(e);
            }
        }
    }

    protected void smartFillTileInGrid(MouseEvent e, boolean invert) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            if (isPointInsideGrid(p.x, p.y)) {
                handler.getSmartGridSelected().useSmartFill(handler, p.x, p.y, invert);
                //updateMapThumbnail(e);
            }
        }
    }

    protected void clearTileInGrid(MouseEvent e) {
        Point p = getCoordsInSelectedMap(e);
        int[][] tileGrid = handler.getActiveTileLayer();
        if (isPointInsideGrid(p.x, p.y) && tileGrid[p.x][p.y] != -1) {
            tileGrid[p.x][p.y] = -1;
            //updateMapThumbnail(e);
        }
    }

    protected void setTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            int[][] tileGrid = handler.getActiveTileLayer();
            Tile tile = handler.getTileSelected();
            if (isPointInsideGrid(p.x, p.y) && tileGrid[p.x][p.y] != handler.getTileIndexSelected()) {
                System.out.println("Xg: " + p.x + " Yg: " + p.y);
                clearAreaUnderTile(tileGrid, p.x, p.y, tile.getWidth(), tile.getHeight());
                tileGrid[p.x][p.y] = handler.getTileIndexSelected();
                //updateMapThumbnail(e);
            }
        }
    }

    protected void dragTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            int[][] tileGrid = handler.getActiveTileLayer();
            Tile tile = handler.getTileSelected();
            p.x = ((p.x - dragStart.x % tile.getWidth()) / tile.getWidth()) * tile.getWidth() + dragStart.x % tile.getWidth();
            p.y = ((p.y - dragStart.y % tile.getHeight()) / tile.getHeight()) * tile.getHeight() + dragStart.y % tile.getHeight();
            //p.y -= (p.y % tile.getHeight()- dragStart.y % tile.getHeight());
            if (isPointInsideGrid(p.x, p.y) /*&& canUseDragging(p.x, p.y, tile.getWidth(), tile.getHeight())*/) {
                clearAreaUnderTile(tileGrid, p.x, p.y, tile.getWidth(), tile.getHeight());
                tileGrid[p.x][p.y] = handler.getTileIndexSelected();
            }
        }
    }

    protected void setTileIndexFromGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            if (isPointInsideGrid(p.x, p.y)) {
                int index = handler.getActiveTileLayer()[p.x][p.y];
                if (index != -1) {
                    handler.setIndexTileSelected(index);
                }
            }
        }
    }

    protected void setHeightIndexFromGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getCoordsInSelectedMap(e);
            if (isPointInsideGrid(p.x, p.y)) {
                int index = handler.getActiveHeightLayer()[p.x][p.y];

                handler.setHeightSelected(index);
            }
        }
    }

    protected void setHeightInGrid(MouseEvent e, int value) {
        Point p = getCoordsInSelectedMap(e);
        int[][] heightGrid = handler.getActiveHeightLayer();
        if (isPointInsideGrid(p.x, p.y)) {
            if (heightGrid[p.x][p.y] != value) {
                heightGrid[p.x][p.y] = value;
                //updateMapThumbnail(e);
            }
        }
    }

    public void setCameraAtMap(Point mapCoods) {
        cameraX = mapCoods.x * cols;
        cameraY = -mapCoods.y * cols;

        viewMode.setCameraAtMap(this);

        /*
        if (orthoEnabled) {
            setOrthoView();
        } else {
            set3DView();
        }*/
    }

    public void setCameraAtSelectedMap() {
        setCameraAtMap(handler.getMapSelected());
    }

    public void setCameraAtMapIfExists(Point mapCoords) {
        if (handler.mapExists(mapCoords)) {
            setCameraAtMap(mapCoords);
        }
    }

    public void setCameraAtNextMapAndSelect(Point displacement) {
        Point mapSelected = handler.getMapSelected();
        Point nextMap = new Point(mapSelected.x + displacement.x, mapSelected.y + displacement.y);
        if (handler.mapExists(nextMap)) {
            setCameraAtMap(nextMap);
            handler.setMapSelected(nextMap);
        }
    }

    public void moveCamera(MouseEvent e) {
        cameraX -= (((float) ((e.getX() - lastMouseX))) / getWidth()) / (orthoScale / (cols + 2 * borderSize));
        cameraY += (((float) ((e.getY() - lastMouseY))) / getHeight()) / (orthoScale / (rows + 2 * borderSize));
        targetX = cameraX;
        targetY = cameraY;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    public void setHandler(MapEditorHandler handler) {
        this.handler = handler;
    }

    public void requestUpdate() {
        updateRequested = true;
    }

    protected void invertLayerState(int index) {
        if (!handler.renderLayers[index]) {
            handler.setActiveTileLayer(index);
        }
        handler.renderLayers[index] = !handler.renderLayers[index];
        handler.getMainFrame().repaintThumbnailLayerSelector();
    }

    public void setOrthoView() {
        viewMode = ViewMode.VIEW_ORTHO_MODE;
        handler.getMainFrame().getJtbViewOrtho().setSelected(true);

        //orthoScale = 1.0f;
        cameraRotX = 0.0f;
        cameraRotY = 0.0f;
        cameraRotZ = 0.0f;

        cameraZ = 32.0f;

        handler.getMainFrame().getJtbModeClear().setEnabled(true);
        handler.getMainFrame().getJtbModeSmartPaint().setEnabled(true);
        handler.getMainFrame().getJtbModeInvSmartPaint().setEnabled(true);

        handler.getMainFrame().updateMapDisplaySize();
    }

    public void set3DView() {
        viewMode = ViewMode.VIEW_3D_MODE;
        handler.getMainFrame().getJtbView3D().setSelected(true);

        cameraRotX = defaultCamRotX;
        cameraRotY = defaultCamRotY;
        cameraRotZ = defaultCamRotZ;

        cameraZ = 40.0f;

        handler.getMainFrame().getJtbModeClear().setEnabled(false);
        handler.getMainFrame().getJtbModeSmartPaint().setEnabled(false);
        handler.getMainFrame().getJtbModeInvSmartPaint().setEnabled(false);

        if (editMode == EditMode.MODE_CLEAR || editMode == EditMode.MODE_SMART_PAINT || editMode == EditMode.MODE_INV_SMART_PAINT) {
            handler.getMainFrame().getJtbModeEdit().setSelected(true);
            setEditMode(EditMode.MODE_EDIT);
        }

        handler.getMainFrame().updateMapDisplaySize();
    }

    public void setHeightView() {
        viewMode = ViewMode.VIEW_HEIGHT_MODE;
        handler.getMainFrame().getJtbViewHeight().setSelected(true);

        //orthoScale = 1.0f;
        cameraRotX = 0.0f;
        cameraRotY = 0.0f;
        cameraRotZ = 0.0f;

        cameraZ = 32.0f;

        handler.getMainFrame().getJtbModeClear().setEnabled(false);
        handler.getMainFrame().getJtbModeSmartPaint().setEnabled(false);
        handler.getMainFrame().getJtbModeInvSmartPaint().setEnabled(false);

        if (editMode == EditMode.MODE_CLEAR || editMode == EditMode.MODE_SMART_PAINT || editMode == EditMode.MODE_INV_SMART_PAINT) {
            handler.getMainFrame().getJtbModeEdit().setSelected(true);
            setEditMode(EditMode.MODE_EDIT);
        }

        handler.getMainFrame().updateMapDisplaySize();
    }

    public void toggleGridView() {
        drawGridEnabled = !drawGridEnabled;
    }

    public void disableGridView() {
        drawGridEnabled = false;
    }

    public void setGridEnabled(boolean enable) {
        this.drawGridEnabled = enable;
    }

    public boolean isGridEnabled() {
        return drawGridEnabled;
    }

    public void toggleSmartGrid() {
        if (editMode == EditMode.MODE_SMART_PAINT) {
            setEditMode(EditMode.MODE_EDIT);
            handler.getMainFrame().getJtbModeEdit().setSelected(true);
        } else {
            setEditMode(EditMode.MODE_SMART_PAINT);
            handler.getMainFrame().getJtbModeSmartPaint().setSelected(true);
        }
    }

    protected void disableSmartGrid() {
        if (editMode == EditMode.MODE_SMART_PAINT) {
            setEditMode(EditMode.MODE_EDIT);
            handler.getMainFrame().getJtbModeEdit().setSelected(true);
        } else {
            setEditMode(EditMode.MODE_SMART_PAINT);
            handler.getMainFrame().getJtbModeSmartPaint().setSelected(true);
        }
    }

    public void toggleClearTile() {
        if (editMode == EditMode.MODE_CLEAR) {
            setEditMode(EditMode.MODE_EDIT);
            handler.getMainFrame().getJtbModeEdit().setSelected(true);
        } else {
            setEditMode(EditMode.MODE_CLEAR);
            handler.getMainFrame().getJtbModeClear().setSelected(true);
        }
    }

    public float getAspectRatio() {
        return (float) getWidth() / getHeight();
    }

    public boolean isSphereInsideFrustum(Vec3f spherePos, float radius, Vec3f[][] frustum){
        for(Vec3f[] plane : frustum){
            float distance = distPointPlaneSigned(spherePos, plane);
            if(distance < -radius){
                return false;
            }else if(distance < radius){
                //return false;
            }
        }
        return true;
    }

    public HashMap<Point, MapData> getMapsInsideFrustum(Vec3f[][] frustum){
        //System.out.println("---------------");
        float radius = (float) Math.sqrt((MapGrid.cols * MapGrid.cols) / 2.0f);
        HashMap<Point, MapData> maps = new HashMap<Point, MapData>();
        for (HashMap.Entry<Point, MapData> map : handler.getMapMatrix().getMatrix().entrySet()) {
            Point p = map.getKey();
            Vec3f center = new Vec3f(p.x * MapGrid.cols, -p.y * MapGrid.rows, 0.0f);
            if(isSphereInsideFrustum(center, radius, frustum)){
                maps.put(map.getKey(), map.getValue());
                //System.out.println("INSIDE: " + p.x + " " + p.y);
            }
        }
        return maps;
    }

    boolean checkOpenGLError() {
        GL2 gl = (GL2) GLContext.getCurrentGL();
        boolean foundError = false;
        GLU glu = new GLU();
        int glErr = gl.glGetError();
        while (glErr != GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl.glGetError();
        }
        return foundError;
    }

    public GLContext getGLContext() {
        return GLContext.getCurrent();
    }

    protected void drawScreenshot(GL2 gl) {

        float xScale = (float) getWidth() / width;
        float yScale = (float) getHeight() / height;
        final int mapWidth = (int) (cols * tileSize * xScale);
        final int mapHeight = (int) (rows * tileSize * yScale);
        BufferedImage upscaledImage = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = upscaledImage.getGraphics();

        ByteBuffer buffer = GLBuffers.newDirectByteBuffer(mapWidth * mapHeight * 4);

        gl.glReadBuffer(GL_BACK);
        gl.glReadPixels((int) (borderSize * tileSize * xScale), (int) (borderSize * tileSize * yScale),
                mapWidth, mapHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        for (int h = 0; h < mapHeight; h++) {
            for (int w = 0; w < mapWidth; w++) {
                graphics.setColor(new Color((buffer.get() & 0xff), (buffer.get() & 0xff),
                        (buffer.get() & 0xff)));
                buffer.get();
                graphics.drawRect(w, mapHeight - 2 - h, 1, 1);
            }
        }

        /*
        BufferedImage upscaledImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = upscaledImage.createGraphics();
        paint(g);
        g.dispose();*/

        screenshot = Utils.resize(upscaledImage, cols * tileSize, rows * tileSize, Image.SCALE_FAST);
    }

    public void drawBackImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(backImageAlpha));
        g2d.drawImage(backImage, borderSize * tileSize, borderSize * tileSize, null);
        g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f));
    }

    public void updateLastMapState() {
        try {
            MapLayerState state = (MapLayerState) handler.getMapStateHandler().getLastState();
            state.updateState();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void changeLayerWithNumKey(KeyEvent e, int layerIndex) {
        if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
            handler.renderLayers[layerIndex] = !handler.renderLayers[layerIndex];
        } else {
            handler.setActiveTileLayer(layerIndex);
        }
        handler.getMainFrame().getThumbnailLayerSelector().repaint();
    }

    private void lighting(GL2 gl) {
        //gl.glEnable(GL2.GL_LIGHTING);
        //gl.glEnable (GL2.GL_COLOR_MATERIAL ) ;
        //gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f, 0.0f}, 0);
        //gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, new float[]{1.0f, 0.0f, 1.0f, 0.0f}, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, new float[]{1.0f, 0.0f, 1.0f, 0.0f}, 0);

        //gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{0.2f, 0.2f, 0.2f, 0.0f}, 0);
        //gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 55.0f);

        gl.glEnable(GL2.GL_LIGHT0);
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f, 0.0f}, 0);
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{-1.0f, 1.0f, 1.0f, 0.0f}, 0);
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, new float[]{1.0f, 1.0f, 1.0f, 0.0f}, 0);

        gl.glEnable(GL2.GL_LIGHT1);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[]{0.8f, 0.8f, 0.8f, 0.0f}, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[]{1.0f, -1.0f, -1.0f, 1.0f}, 0);

    }

    public void requestScreenshot() {
        screenshotRequested = true;
    }

    public BufferedImage getScreenshot() {
        return screenshot;
    }

    public void setBackImage(BufferedImage backImage) {
        this.backImage = backImage;
    }

    public void toggleBackImageView() {
        backImageEnabled = !backImageEnabled;
    }

    public void setBackImageEnabled(boolean enabled) {
        this.backImageEnabled = enabled;
    }

    public void setHeightMapAlpha(float alpha) {
        this.heightMapOpacity = alpha;
    }

    public void setBackImageAlpha(float alpha) {
        this.backImageAlpha = alpha;
    }

    public void setEditMode(EditMode mode) {
        editMode = mode;
        setCursor(editMode.cursor);
    }

    public EditMode getEditMode() {
        return editMode;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setDrawAreasEnabled(boolean drawAreas) {
        this.drawAreasEnabled = drawAreas;
    }

    public void setDrawWireframeEnabled(boolean drawWireframeEnabled) {
        this.drawWireframeEnabled = drawWireframeEnabled;
    }

    public void setDrawGridBorderMaps(boolean drawGridBorderMaps) {
        this.drawGridBorderMaps = drawGridBorderMaps;
    }

    protected static interface DrawGeometryGLFunction {

        public void draw(GL2 gl, GeometryGL geometryGL, ArrayList<Texture> textures);
    }

}
