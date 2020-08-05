/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import editor.handler.MapEditorHandler;
import static com.jogamp.opengl.GL.GL_BACK;
import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_GREATER;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LESS;
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
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.GLBuffers;
import editor.bordermap.BorderMapsGrid;
import editor.handler.MapGrid;
import editor.state.MapLayerState;
import geometry.Generator;
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
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.swing.SwingUtilities;
import tileset.Tile;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class MapDisplay extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    //Editor Handler
    private MapEditorHandler handler;

    //Grid
    private final int cols = 32;
    private final int rows = 32;
    private final int tileSize = 16;
    private final int borderSize = 1;
    private final int width = (cols + borderSize * 2) * tileSize;
    private final int height = (rows + borderSize * 2) * tileSize;
    private final float gridTileSize = 1.0f;

    //OpenGL
    private GLU glu;
    private float[] grid;
    private float[] axis;
    private final float[] axisColors = {
        1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};

    private boolean[] renderVboTile;
    private boolean[] renderVboBorderMap;
    private final int vbosPerTile = 4;

    //Scene
    private float cameraX, cameraY, cameraZ;
    private float modelRotX, modelRotY, modelRotZ;
    private boolean orthoEnabled = true;
    private boolean drawGridEnabled = true;

    //Mouse events
    private int lastMouseX, lastMouseY;
    private int xMouse, yMouse;
    private Point dragStart = new Point();

    //Height map
    private boolean drawHeightMap = false;
    private float heightMapOpacity = 1.0f;

    //Update
    private boolean updateRequested = false;
    private boolean borderMapsUpdateRequested = false;

    //Smart grid
    private boolean smartGridEnabled = false;
    private Cursor smartGridCursor;
    private boolean smartGridInvertedEnabled = false;
    private Cursor smartGridInvertedCursor;

    //Clear tile
    private boolean clearTileEnabled = false;
    private Cursor clearTileCursor;

    //Screenshot
    private BufferedImage screenshot;
    private boolean screenshotRequested = false;

    //Background Image 
    private BufferedImage backImage = null;
    private boolean backImageEnabled = false;
    private float backImageAlpha = 0.5f;

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
        smartGridCursor = Utils.loadCursor("/cursors/smartGridCursor.png");
        smartGridInvertedCursor = Utils.loadCursor("/cursors/smartGridInvertedCursor.png");
        clearTileCursor = Utils.loadCursor("/cursors/clearTileCursor.png");

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        renderVboTile = new boolean[handler.getTileset().size() * vbosPerTile];
        renderVboBorderMap = new boolean[handler.getBorderMapsTileset().size() * vbosPerTile];

        //Create VAOs and VBOs
        grid = Generator.generateCenteredGrid(cols, rows, gridTileSize, 0.01f);
        axis = Generator.generateAxis(100.0f);
        setupVAOs(drawable.getGL().getGL2());

        //Load textures into OpenGL
        handler.getTileset().loadTexturesGL();
        handler.getBorderMapsTileset().loadTexturesGL();

        drawable.getGL().getGL2().glClearColor(0.0f, 0.5f, 0.5f, 1.0f);

        //Scene
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 32.0f;

        modelRotX = 0.0f;
        modelRotY = 0.0f;
        modelRotZ = 0.0f;

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose!! ");
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();//getGL().getGL4();//(GL4) GLContext.getCurrentGL();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (updateRequested || borderMapsUpdateRequested) {
            renderVboTile = new boolean[handler.getTileset().size() * vbosPerTile];
            renderVboBorderMap = new boolean[handler.getBorderMapsTileset().size() * vbosPerTile];

            //Create VAOs and VBOs
            setupVAOs(gl);

            //Load Textures into OpenGL
            handler.getTileset().loadTexturesGL();
            handler.getBorderMapsTileset().loadTexturesGL();

            updateRequested = false;
            borderMapsUpdateRequested = false;
        }

        try {
            //Draw grid
            if (drawGridEnabled) {
                drawGrid();
            }

            //Draw axis
            drawAxis();

            //Draw opaque tiles
            if (handler.getTileset().size() > 0) {
                drawOpaqueTiles(gl);
            }

            if (handler.getBorderMapsTileset().size() > 0) {
                drawOpaqueBorderMaps(gl);
            }

            //Draw transparent tiles
            if (handler.getTileset().size() > 0) {
                drawTransparentTiles(gl);
            }

            if (handler.getBorderMapsTileset().size() > 0) {
                drawTransparentBorderMaps(gl);
            }

            //Screenshot
            if (screenshotRequested) {
                drawScreenshot(gl);
                screenshotRequested = false;
            }

            gl.glFinish();
        } catch (GLException ex) {

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
        if (orthoEnabled) {
            if (handler.getTileset().size() > 0) {
                handler.setLayerChanged(false);
                if (drawHeightMap) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        handler.addMapState(new MapLayerState("Draw Height", handler));
                        setHeightInGrid(e, handler.getHeightSelected());
                        repaint();
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        handler.addMapState(new MapLayerState("Flood Fill Height", handler));
                        floodFillHeightInGrid(e);
                        repaint();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        setHeightIndexFromGrid(e);
                        repaint();
                        handler.getMainFrame().repaintHeightSelector();
                    }
                } else {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (smartGridEnabled) {
                            handler.addMapState(new MapLayerState("Smart Drawing Tile", handler));
                            smartFillTileInGrid(e);
                            disableSmartGrid();
                            repaint();
                        } else if (clearTileEnabled) {
                            handler.addMapState(new MapLayerState("Clear Tile", handler));
                            clearTileInGrid(e);
                            repaint();
                        } else {
                            dragStart = getMapCoords(e);
                            handler.addMapState(new MapLayerState("Draw Tile", handler));
                            setTileInGrid(e);
                            repaint();
                        }
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        if (clearTileEnabled) {
                            handler.addMapState(new MapLayerState("Flood Fill Clear Tile", handler));
                            floodFillClearTileInGrid(e);
                            repaint();
                        } else {
                            handler.addMapState(new MapLayerState("Flood Fill Tile", handler));
                            floodFillTileInGrid(e);
                            repaint();
                        }
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        setTileIndexFromGrid(e);
                        repaint();
                        handler.getMainFrame().repaintTileSelector();
                        handler.getMainFrame().updateTileSelectorScrollBar();
                        handler.getMainFrame().repaintTileDisplay();
                    }
                }
            }
        } else {
            if (SwingUtilities.isRightMouseButton(e)
                    | SwingUtilities.isMiddleMouseButton(e)) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (orthoEnabled) {
            handler.updateLayerThumbnail(handler.getActiveLayerIndex());
            handler.repaintThumbnailLayerSelector();
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (orthoEnabled) {
            if (drawHeightMap) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    setHeightInGrid(e, handler.getHeightSelected());
                    repaint();
                }
            } else {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (clearTileEnabled) {
                        clearTileInGrid(e);
                        repaint();
                    } else {
                        dragTileInGrid(e);
                        repaint();
                    }
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)
                | SwingUtilities.isMiddleMouseButton(e)) {
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
        if (orthoEnabled) {
            xMouse = (int) (e.getX() * (width / (float) getWidth()));
            yMouse = (int) (e.getY() * (height / (float) getHeight()));

            if (!drawHeightMap) {
                repaint();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (orthoEnabled) {
                set3DView();
                repaint();
            } else {
                setOrthoView();
                repaint();
            }
        }

        int delta = 5;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                modelRotZ -= delta;
                repaint();
                break;
            case KeyEvent.VK_LEFT:
                modelRotZ += delta;
                repaint();
                break;
            case KeyEvent.VK_DOWN:
                modelRotX -= delta;
                repaint();
                break;
            case KeyEvent.VK_UP:
                modelRotX += delta;
                repaint();
                break;
            default:
                break;
        }

        if (e.getKeyCode() == KeyEvent.VK_H) {
            toggleHeightView();
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_G) {
            toggleGridView();
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (!drawHeightMap) {
                toggleSmartGrid();
                repaint();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            enableSmartGridInverted();
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_C) {
            if (!drawHeightMap) {
                toggleClearTile();
                repaint();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_B) {
            toggleBackImageView();
            repaint();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_1:
                invertLayerState(0);
                System.out.println("Layer 1 activado");
                repaint();
                break;
            case KeyEvent.VK_2:
                invertLayerState(1);
                System.out.println("Layer 2 activado");
                repaint();
                break;
            case KeyEvent.VK_3:
                invertLayerState(2);
                repaint();
                break;
            default:
                break;
        }

        if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            handler.getMainFrame().undoMapState();
        }

        if ((e.getKeyCode() == KeyEvent.VK_Y) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            handler.getMainFrame().redoMapState();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            disableSmartGridInverted();
            repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!orthoEnabled) {
            int wheelRotation = e.getWheelRotation();
            if (wheelRotation > 0) {
                cameraZ *= 1.1;
            } else {
                cameraZ /= 1.1;
            }
            repaint();
        } else {
            if (drawHeightMap) {
                int delta = e.getWheelRotation() > 0 ? -1 : 1;
                handler.incrementHeightSelected(delta);
                handler.getMainFrame().repaintHeightSelector();
            } else {
                int delta = e.getWheelRotation() > 0 ? 1 : -1;
                handler.incrementTileSelected(delta);
                handler.getMainFrame().repaintTileSelector();
                handler.getMainFrame().repaintTileDisplay();
                repaint();
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handler != null) { 
            if (orthoEnabled) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHints(new RenderingHints(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR));
                g2d.scale((float) getWidth() / width, (float) getHeight() / height);

                if (backImageEnabled && backImage != null) {
                    drawBackImage(g);
                }
                if (drawHeightMap) {
                    drawHeightMap(g);
                } else if (smartGridEnabled) {
                    drawUnitTileBounds(g);
                } else if (clearTileEnabled) {
                    drawUnitTileBounds(g);
                } else {
                    drawTileThumbnail(g);
                }
                drawBorderBounds(g);

                g2d.scale((float) width / getWidth(), (float) height / getHeight());
            }
        }
    }

    private void drawTileThumbnail(Graphics g) {
        if (handler.getTileset().size() > 0) {
            Tile tile = handler.getTileset().get(handler.getTileIndexSelected());
            int x = (xMouse / tileSize) * tileSize;
            int y = ((yMouse / tileSize) - (tile.getHeight() - 1)) * tileSize;

            g.drawImage(tile.getThumbnail(), x, y, null);

            g.setColor(Color.red);
            g.drawRect(x, y, tile.getWidth() * tileSize, tile.getHeight() * tileSize);
        }
    }

    private void drawUnitTileBounds(Graphics g) {
        int x = (xMouse / tileSize) * tileSize;
        int y = ((yMouse / tileSize)) * tileSize;
        g.setColor(Color.red);
        g.drawRect(x, y, tileSize, tileSize);
    }

    private void drawHeightMap(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(heightMapOpacity));
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int x = (i + borderSize) * tileSize;
                int y = (rows - 1 - j + borderSize) * tileSize;
                int[][] heightGrid = handler.getActiveHeightLayer();
                g.drawImage(handler.getHeightImageByValue(heightGrid[i][j]), x, y, null);
            }
        }
    }

    private void drawBorderBounds(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(borderSize * tileSize, borderSize * tileSize,
                tileSize * cols, tileSize * rows);
    }

    private void setupVAOs(GL2 gl) {
        //Create tile VBOs
        if (handler.getTileset().size() > 0) {
            generateTileVBOs(gl);
        }

        //Create border maps VBOs
        if (handler.getBorderMapsTileset().size() > 0) {
            generateBorderMapsVBOs(gl);
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

    private void generateBorderMapsVBOs(GL2 gl) {
        for (int i = 0; i < handler.getBorderMapsTileset().size(); i++) {
            Tile tile = handler.getBorderMapsTileset().get(i);

            renderVboBorderMap[i * vbosPerTile] = tile.getVCoordsQuad().length > 0;
            renderVboBorderMap[i * vbosPerTile + 1] = tile.getTCoordsQuad().length > 0;
            renderVboBorderMap[i * vbosPerTile + 2] = tile.getVCoordsTri().length > 0;
            renderVboBorderMap[i * vbosPerTile + 3] = tile.getTCoordsTri().length > 0;
        }
    }

    private void drawGrid() {
        GL2 gl = (GL2) GLContext.getCurrentGL();

        gl.glLoadIdentity();

        if (orthoEnabled) {
            float v = 16.0f + borderSize;
            gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
        } else {
            float aspect = (float) this.getWidth() / (float) this.getHeight();
            glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        }

        gl.glTranslatef(-cameraX, -cameraY, -cameraZ); // translate into the screen
        gl.glRotatef(modelRotX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
        gl.glRotatef(modelRotY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
        gl.glRotatef(modelRotZ, 0.0f, 0.0f, 1.0f); // rotate about the z-axis

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
            float v = 16.0f + borderSize;
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

    private void drawTiles(GL2 gl) {
        for (int layerInd = 0; layerInd < MapGrid.numLayers; layerInd++) {
            if (handler.renderLayers[layerInd]) {
                int[][] tileGrid = handler.getTileLayer(layerInd);
                int[][] heightGrid = handler.getHeightLayer(layerInd);
                for (int i = 0; i < cols; i++) {
                    for (int j = 0; j < rows; j++) {
                        if ((tileGrid[i][j] != -1) && (tileGrid[i][j] < handler.getTileset().size())) {
                            drawTile(gl, i, j, heightGrid, tileGrid);
                        }
                    }
                }
            }
        }
    }

    private void drawBorderMaps(GL2 gl) {
        int[][] tileGrid = handler.getBorderMapsGrid().grid;
        int[][] heightGrid = handler.getBorderMapsGrid().heights;
        for (int i = 0; i < BorderMapsGrid.cols; i++) {
            for (int j = 0; j < BorderMapsGrid.rows; j++) {
                if ((tileGrid[i][j] != -1) && (tileGrid[i][j] < handler.getBorderMapsTileset().size())) {
                    drawBorderMap(gl, i, j, heightGrid, tileGrid);
                }
            }
        }

    }

    private void drawTile(GL2 gl, int i, int j, int[][] heightGrid, int[][] tileGrid) {
        gl.glLoadIdentity();
        if (orthoEnabled) {
            float v = 16.0f + borderSize;
            gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
        } else {
            float aspect = (float) this.getWidth() / (float) this.getHeight();
            glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        }

        gl.glTranslatef(-cameraX, -cameraY, -cameraZ); // translate into the screen

        gl.glRotatef(modelRotX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
        gl.glRotatef(modelRotY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
        gl.glRotatef(modelRotZ, 0.0f, 0.0f, 1.0f); // rotate about the z-axis

        gl.glTranslatef(
                (i - (cols) / 2) * gridTileSize,
                (j - (rows) / 2) * gridTileSize,
                heightGrid[i][j] * gridTileSize); // translate into the screen

        Tile tile = handler.getTileset().get(tileGrid[i][j]);

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        int vboID = tileGrid[i][j] * vbosPerTile;
        drawQuads(gl, tile, renderVboTile, vboID);
        drawTris(gl, tile, renderVboTile, vboID + 2);

    }

    private void drawBorderMap(GL2 gl, int i, int j, int[][] heightGrid, int[][] tileGrid) {
        gl.glLoadIdentity();
        if (orthoEnabled) {
            float v = 16.0f + borderSize;
            gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
        } else {
            float aspect = (float) this.getWidth() / (float) this.getHeight();
            glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
        }

        gl.glTranslatef(-cameraX, -cameraY, -cameraZ); // translate into the screen

        gl.glRotatef(modelRotX, 1.0f, 0.0f, 0.0f); // rotate about the x-axis
        gl.glRotatef(modelRotY, 0.0f, 1.0f, 0.0f); // rotate about the y-axis
        gl.glRotatef(modelRotZ, 0.0f, 0.0f, 1.0f); // rotate about the z-axis

        gl.glTranslatef(
                (i - 1) * gridTileSize * cols,
                (j - 1) * gridTileSize * rows,
                heightGrid[i][j] * gridTileSize); // translate into the screen

        Tile tile = handler.getBorderMapsTileset().get(tileGrid[i][j]);

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        int vboID = tileGrid[i][j] * vbosPerTile;
        drawQuads(gl, tile, renderVboBorderMap, vboID);
        drawTris(gl, tile, renderVboBorderMap, vboID + 2);
        //drawTris(gl, tile, vboBorderMaps, renderVboBorderMap, vboID + 2);
    }

    private void drawQuads(GL2 gl, Tile tile, boolean[] renderVbo, int vboID) {
        if (!(renderVbo[vboID] && renderVbo[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            // activate texture unit #0 and bind it to the brick texture object
            //gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, tile.getTexture(k).getTextureObject());

            gl.glEnable(GL_TEXTURE_2D);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

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
                }
            }
            gl.glEnd();

            gl.glDisable(GL_TEXTURE_2D);
        }
    }

    private void drawTris(GL2 gl, Tile tile, boolean[] renderVbo, int vboID) {
        if (!(renderVbo[vboID] && renderVbo[vboID + 1])) {
            return;
        }

        for (int k = 0; k < tile.getTextureIDs().size(); k++) {
            // activate texture unit #0 and bind it to the brick texture object
            //gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, tile.getTexture(k).getTextureObject());

            gl.glEnable(GL_TEXTURE_2D);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

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
                    gl.glTexCoord2fv(tile.getTCoordsTri(), (i * vPerPolygon + j) * 2);
                    gl.glColor3fv(tile.getColorsTri(), (i * vPerPolygon + j) * 3);
                    gl.glVertex3fv(tile.getVCoordsTri(), (i * vPerPolygon + j) * 3);
                }
            }
            gl.glEnd();

            gl.glDisable(GL_TEXTURE_2D);
        }

    }

    private void drawOpaqueTiles(GL2 gl) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        //gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0.9f);

        drawTiles(gl);
    }

    private void drawTransparentTiles(GL2 gl) {

        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        gl.glEnable(GL_BLEND);
        //gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);

        drawTiles(gl);
    }

    private void drawOpaqueBorderMaps(GL2 gl) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0.9f);

        drawBorderMaps(gl);
    }

    private void drawTransparentBorderMaps(GL2 gl) {
        gl.glEnable(GL_BLEND);
        //gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);

        drawBorderMaps(gl);
    }

    private boolean isPointInsideGrid(int col, int row) {
        return new Rectangle(cols, rows).contains(col, row);
    }

    private boolean canUseDragging(int col, int row, int tileWidth, int tileHeight) {
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

    private Point getMapCoords(MouseEvent e) {
        xMouse = (int) (e.getX() * (width / (float) getWidth()));
        yMouse = (int) (e.getY() * (height / (float) getHeight()));
        int x = xMouse / tileSize - borderSize;
        int y = rows - 1 - yMouse / tileSize + borderSize;
        return new Point(x, y);
    }

    private void floodFillClearTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            if (isPointInsideGrid(p.x, p.y)) {
                handler.getGrid().floodFillTileGrid(p.x, p.y, -1, 1, 1);
            }
        }
    }

    private void floodFillTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            if (isPointInsideGrid(p.x, p.y)) {
                Tile tile = handler.getTileSelected();
                handler.getGrid().floodFillTileGrid(p.x, p.y, handler.getTileIndexSelected(), tile.getWidth(), tile.getHeight());
            }
        }
    }

    private void floodFillHeightInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            if (isPointInsideGrid(p.x, p.y)) {
                handler.getGrid().floodFillHeightGrid(p.x, p.y, handler.getHeightSelected());
            }
        }
    }

    private void smartFillTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            if (isPointInsideGrid(p.x, p.y)) {
                handler.getSmartGridSelected().useSmartFill(handler, p.x, p.y, smartGridInvertedEnabled);
            }
        }
    }

    private void clearTileInGrid(MouseEvent e) {
        Point p = getMapCoords(e);
        int[][] tileGrid = handler.getActiveTileLayer();
        if (isPointInsideGrid(p.x, p.y)) {
            tileGrid[p.x][p.y] = -1;
        }
    }

    private void setTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            int[][] tileGrid = handler.getActiveTileLayer();
            Tile tile = handler.getTileSelected();
            if (isPointInsideGrid(p.x, p.y)) {
                clearAreaUnderTile(tileGrid, p.x, p.y, tile.getWidth(), tile.getHeight());
                tileGrid[p.x][p.y] = handler.getTileIndexSelected();
            }
        }
    }

    private void dragTileInGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            int[][] tileGrid = handler.getActiveTileLayer();
            Tile tile = handler.getTileSelected();
            p.x = ((p.x - dragStart.x % tile.getWidth()) / tile.getWidth()) * tile.getWidth() + dragStart.x % tile.getWidth();
            p.y = ((p.y - dragStart.y % tile.getHeight()) / tile.getHeight()) * tile.getHeight()+ dragStart.y % tile.getHeight();
            //p.y -= (p.y % tile.getHeight()- dragStart.y % tile.getHeight());
            if (isPointInsideGrid(p.x, p.y) /*&& canUseDragging(p.x, p.y, tile.getWidth(), tile.getHeight())*/) {
                clearAreaUnderTile(tileGrid, p.x, p.y, tile.getWidth(), tile.getHeight());
                tileGrid[p.x][p.y] = handler.getTileIndexSelected();
            }
        }
    }

    private void setTileIndexFromGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            if (isPointInsideGrid(p.x, p.y)) {
                int index = handler.getActiveTileLayer()[p.x][p.y];
                if (index != -1) {
                    handler.setIndexTileSelected(index);
                }
            }
        }
    }

    private void setHeightIndexFromGrid(MouseEvent e) {
        if (handler.getTileset().size() > 0) {
            Point p = getMapCoords(e);
            if (isPointInsideGrid(p.x, p.y)) {
                int index = handler.getActiveHeightLayer()[p.x][p.y];

                handler.setHeightSelected(index);
            }
        }
    }

    private void setHeightInGrid(MouseEvent e, int value) {
        Point p = getMapCoords(e);
        int[][] heightGrid = handler.getActiveHeightLayer();
        if (isPointInsideGrid(p.x, p.y)) {
            heightGrid[p.x][p.y] = value;
        }
    }

    public void setHandler(MapEditorHandler handler) {
        this.handler = handler;
    }

    public void requestUpdate() {
        updateRequested = true;
    }

    public void requestBorderMapsUpdate() {
        borderMapsUpdateRequested = true;
    }

    private void invertLayerState(int index) {
        if (!handler.renderLayers[index]) {
            handler.setActiveTileLayer(index);
        }
        handler.renderLayers[index] = !handler.renderLayers[index];
        handler.getMainFrame().repaintThumbnailLayerSelector();
    }

    public void setOrthoView() {
        orthoEnabled = true;

        modelRotX = 0.0f;
        modelRotY = 0.0f;
        modelRotZ = 0.0f;

        cameraZ = 32.0f;
    }

    public void set3DView() {
        orthoEnabled = false;
        drawHeightMap = false;

        modelRotX = -70.0f;
        modelRotY = 0.0f;
        modelRotZ = -20.0f;
    }

    public void toggleHeightView() {
        disableClearTile();
        disableSmartGrid();
        drawHeightMap = !drawHeightMap;
        if (drawHeightMap) {
            setOrthoView();
        }
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
        clearTileEnabled = false;
        if (smartGridEnabled) {
            setCursor(Cursor.getDefaultCursor());
            smartGridEnabled = false;
            smartGridInvertedEnabled = false;
        } else {
            setCursor(smartGridCursor);
            smartGridEnabled = true;
        }
    }

    public void enableSmartGridInverted() {
        if (smartGridEnabled) {
            smartGridInvertedEnabled = true;
            setCursor(smartGridInvertedCursor);
        }
    }

    public void disableSmartGridInverted() {
        if (smartGridEnabled) {
            smartGridInvertedEnabled = false;
            setCursor(smartGridCursor);
        }
    }

    public void enableSmartGrid() {
        clearTileEnabled = false;
        setCursor(smartGridCursor);
        smartGridEnabled = true;
    }

    private void disableSmartGrid() {
        setCursor(Cursor.getDefaultCursor());
        smartGridEnabled = false;
        smartGridInvertedEnabled = false;
    }

    public void toggleClearTile() {
        smartGridEnabled = false;
        smartGridInvertedEnabled = false;
        if (clearTileEnabled) {
            setCursor(Cursor.getDefaultCursor());
            clearTileEnabled = false;
        } else {
            setCursor(clearTileCursor);
            clearTileEnabled = true;
        }
    }

    private void disableClearTile() {
        setCursor(Cursor.getDefaultCursor());
        clearTileEnabled = false;
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

    private void drawScreenshot(GL2 gl) {
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

        screenshot = Utils.resize(upscaledImage, cols * tileSize, rows * tileSize, Image.SCALE_FAST);
    }

    public void drawBackImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(backImageAlpha));
        g2d.drawImage(backImage, borderSize * tileSize, borderSize * tileSize, null);
        g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f));
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
}
