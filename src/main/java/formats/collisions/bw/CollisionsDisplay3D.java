package formats.collisions.bw;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import editor.grid.GeometryGL;
import editor.grid.MapLayerGL;
import editor.handler.MapData;
import editor.handler.MapEditorHandler;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;

import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static com.jogamp.opengl.GL2GL3.*;

public class CollisionsDisplay3D extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    protected MapEditorHandler handler;
    protected CollisionHandlerBW cHandler;

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
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};

    //Scene
    protected float cameraX, cameraY, cameraZ;
    protected float targetX, targetY, targetZ;
    protected float cameraRotX, cameraRotY, cameraRotZ;
    protected static final float defaultCamRotX = 40.0f, defaultCamRotY = 0.0f, defaultCamRotZ = 0.0f;
    protected float lastMouseX, lastMouseY;

    //Update
    protected boolean updateRequested = false;
    protected final float minZ = -8.0f, maxZ = 8.0f;

    protected boolean mapEnabled = true;
    protected boolean wireframeEnabled = true;
    protected boolean transparentEnabled = true;
    protected boolean xRayEnabled = true;
    protected float platesAlpha = 0.85f;

    protected BufferedImage[][] arrows = new BufferedImage[][]{
            {loadImage("/icons/arrowLU.png"), loadImage("/icons/arrowU.png"), loadImage("/icons/arrowRU.png")},
            {loadImage("/icons/arrowL.png"), null, loadImage("/icons/arrowR.png")},
            {loadImage("/icons/arrowLD.png"), loadImage("/icons/arrowD.png"), loadImage("/icons/arrowRD.png")}
    };

    public enum Mode {
        View, Edit
    }

    private Mode mode = Mode.View;

    public CollisionsDisplay3D() {
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
        cameraZ = 32.0f;

        cameraRotX = defaultCamRotX;
        cameraRotY = defaultCamRotY;
        cameraRotZ = defaultCamRotZ;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        drawable.getGL().getGL2().glClearColor(0.0f, 0.5f, 0.5f, 1.0f);

        handler.getTileset().loadTexturesGL();
        handler.getBorderMapsTileset().loadTexturesGL();
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (updateRequested) {
            handler.getTileset().loadTexturesGL();
            handler.getBorderMapsTileset().loadTexturesGL();

            updateMapLayersGL();

            updateRequested = false;
        }

        if (cHandler.getCollisionsBW3D() != null) {
            if (mapEnabled) {
                if (handler.getTileset().size() > 0) {
                    drawOpaqueMaps(gl);
                }

                //Draw semitransparent tiles
                if (handler.getTileset().size() > 0) {
                    drawTransparentMaps(gl);
                }
            }

            switch (mode) {
                case View:
                    drawCollisions(gl);

                    if (wireframeEnabled) {
                        drawWireframe(gl);
                    }
                    break;
                case Edit:
                    drawCollisions(gl);
                    break;
            }
        }
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
        switch (mode) {
            case View:
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                break;
            case Edit:
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point p = getCoordsInMap(e);
                    cHandler.setCurrentTile(cHandler.getCollisionsBW3D().getZCoords(p.x, p.y));
                    cHandler.getDialog().updateViewCTile();

                    //logCTile(p);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    Point p = getCoordsInMap(e);
                    cHandler.getCollisionsBW3D().setZCoords(p.x, p.y, cHandler.getCurrentTile());
                    cHandler.getDialog().updateViewCollisionDisplays();

                } else if(SwingUtilities.isMiddleMouseButton(e)){
                    Point p = getCoordsInMap(e);
                    cHandler.getCollisionsBW3D().floodFill(p.x, p.y, cHandler.getCurrentTile());
                    cHandler.getDialog().updateViewCollisionDisplays();
                }
                break;
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
        switch (mode) {
            case View:
                if (SwingUtilities.isLeftMouseButton(e)) {
                    float dist = cameraZ;
                    float deltaX = ((e.getX() - lastMouseX) / getWidth()) * dist;
                    float deltaZ = ((e.getY() - lastMouseY) / getHeight()) * dist;

                    Vector3D v = new Vector3D(deltaX, 0.0f, deltaZ);
                    Matrix3D m2 = new Matrix3D(cameraRotZ, new Vector3D(0.0f, 1.0f, 0.0f));
                    v = v.mult(m2);

                    cameraX -= (float) v.getX();
                    cameraY += (float) v.getZ();

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();

                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e) | SwingUtilities.isMiddleMouseButton(e)) {
                    float delta = 100.0f;
                    cameraRotZ -= ((e.getX() - lastMouseX) / getWidth()) * delta;
                    lastMouseX = e.getX();
                    cameraRotX -= ((e.getY() - lastMouseY) / getHeight()) * delta;
                    lastMouseY = e.getY();
                    repaint();
                }
                break;
            case Edit:
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point p = getCoordsInMap(e);
                    cHandler.setCurrentTile(cHandler.getCollisionsBW3D().getZCoords(p.x, p.y));
                    cHandler.getDialog().updateViewCTile();

                    //logCTile(p);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    Point p = getCoordsInMap(e);
                    cHandler.getCollisionsBW3D().setZCoords(p.x, p.y, cHandler.getCurrentTile());
                    cHandler.getDialog().updateViewCollisionDisplays();
                }
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        switch (mode) {
            case View:
                if (e.getWheelRotation() > 0) {
                    cameraZ *= 1.1;
                } else {
                    cameraZ /= 1.1;
                }
                repaint();
                break;
            case Edit:
                cHandler.moveCurrentTile(e.getWheelRotation() > 0 ? -1.0f : 1.0f);
                cHandler.getDialog().updateViewCTile();
                break;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mode == Mode.Edit) {
            Graphics2D g2d = (Graphics2D) g;

            AffineTransform transf = g2d.getTransform();

            g2d.scale((float) getWidth() / width, (float) getHeight() / height);

            g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
            drawGrid(g);

            g2d.setComposite(AlphaComposite.SrcOver.derive(0.35f));
            drawArrows(g);

            g2d.setTransform(transf);
        }
    }


    protected void applyCameraTransform(GL2 gl) {
        gl.glLoadIdentity();

        switch (mode) {
            case Edit:
                float v = (16.0f) / orthoScale;
                gl.glOrtho(-v, v, -v, v, -100.0f, 100.0f);
                break;
            case View:
                float aspect = (float) getWidth() / (float) getHeight();
                if (cameraZ < 40.0f) {
                    glu.gluPerspective(60.0f, aspect, 1.0f, 1000.0f);
                } else {
                    glu.gluPerspective(60.0f, aspect, 1.0f + (cameraZ - 40.0f) / 4, 1000.0f + (cameraZ - 40.0f));
                }
                break;
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

    private void logCTile(Point p) {
        if (cHandler.getCollisionsBW3D().isCorner(cHandler.getCurrentTile())) {
            int[] slopeIndices = cHandler.getCollisionsBW3D().getSlopeIndicesCorner(p.x, p.y, cHandler.getCurrentTile());
            int[] distIndices = cHandler.getCollisionsBW3D().getDistanceIndicesCorner(p.x, p.y, cHandler.getCurrentTile());

            cHandler.getDialog().getJlInfo().setText(
                    "CORNER " +
                            String.format("0x%04x", slopeIndices[0] + 1) + "  " + String.format("0x%04x", slopeIndices[1] / 4) + "  " +
                            String.format("0x%04x", distIndices[0]) + "  " + String.format("0x%04x", distIndices[1])
            );
        } else {
            float distance = cHandler.getCollisionsBW3D().getDistancePlane(cHandler.getCurrentTile(), p.x, p.y);
            int distIndex = cHandler.getCollisionsBW3D().getDistanceIndexPlane(p.x, p.y);
            int slopeIndex = cHandler.getCollisionsBW3D().getSlopeIndex(p.x, p.y);

            cHandler.getDialog().getJlInfo().setText("PLANE " + String.format("0x%04x", slopeIndex) + "  " + String.format("0x%04x", distIndex));
        }
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < width; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, height);
        }

        for (int i = 0; i < height; i++) {
            g.drawLine(0, i * tileSize, width, i * tileSize);
        }
    }

    private void drawArrows(Graphics g) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                BufferedImage img = getArrowPoint(i, j);
                if (img != null) {
                    g.drawImage(img, i * tileSize, j * tileSize, null);
                }
            }
        }
    }

    public void updateMapLayersGL() {
        for (int i = 0; i < handler.getGrid().mapLayersGL.length; i++) {
            updateMapLayerGL(i);
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

    protected void drawOpaqueMaps(GL2 gl) {
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0.9f);

        /*
        drawAllMaps(gl, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });*/
        drawCurrentMap(gl, this::drawGeometryGL);
    }

    protected void drawTransparentMaps(GL2 gl) {
        gl.glEnable(GL_BLEND);

        gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);

        drawCurrentMap(gl, this::drawGeometryGL);

        /*
        drawAllMaps(gl, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });*/
    }

    protected void drawAllMaps(GL2 gl, CollisionsDisplay3D.DrawGeometryGLFunction drawFunction) {
        for (HashMap.Entry<Point, MapData> map : handler.getMapMatrix().getMatrix().entrySet()) {
            drawAllMapLayersGL(gl, drawFunction, map.getValue().getGrid().mapLayersGL,
                    map.getKey().x * cols, -map.getKey().y * rows, 0);
        }
    }

    protected void drawCurrentMap(GL2 gl, CollisionsDisplay3D.DrawGeometryGLFunction drawFunction) {
        MapData map = handler.getCurrentMap();
        drawAllMapLayersGL(gl, drawFunction, map.getGrid().mapLayersGL, 0, 0, 0);
    }

    protected void drawAllMapLayersGL(GL2 gl, CollisionsDisplay3D.DrawGeometryGLFunction drawFunction, MapLayerGL[] mapLayersGL, float x, float y, float z) {
        for (int i = 0; i < mapLayersGL.length; i++) {
            if (handler.renderLayers[i] && mapLayersGL[i] != null) {
                drawMapLayerGL(gl, drawFunction, mapLayersGL[i], x, y, z);
            }
        }
    }

    protected void drawMapLayerGL(GL2 gl, CollisionsDisplay3D.DrawGeometryGLFunction drawFunction, MapLayerGL mapLayerGL, float x, float y, float z) {
        applyCameraTransform(gl);

        gl.glTranslatef(x, y, z);

        for (GeometryGL geometryGL : mapLayerGL.getGeometryGL().values()) {
            drawFunction.draw(gl, geometryGL, handler.getTileset().getTextures());
        }
    }

    protected void drawGeometryGL(GL2 gl, GeometryGL geometryGL, List<Texture> textures) {
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

                    gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, geometryGL.tCoordsTriBuffer);
                    gl.glColorPointer(3, GL2.GL_FLOAT, 0, geometryGL.colorsTriBuffer);
                    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, geometryGL.vCoordsTriBuffer);

                    gl.glDrawArrays(GL2.GL_TRIANGLES, 0, geometryGL.vCoordsTri.length / 3);

                    gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
                    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);

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

                    gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, geometryGL.tCoordsQuadBuffer);
                    gl.glColorPointer(3, GL2.GL_FLOAT, 0, geometryGL.colorsQuadBuffer);
                    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, geometryGL.vCoordsQuadBuffer);

                    gl.glDrawArrays(GL2.GL_QUADS, 0, geometryGL.vCoordsQuad.length / 3);

                    gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
                    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);

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

    public void drawCollisions(GL2 gl) {
        float[] coords = cHandler.getCollisionsBW3D().getVCoords();

        gl.glEnable(GL_LINE_SMOOTH);
        gl.glEnable(GL_POLYGON_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        gl.glEnable(GL_BLEND);
        //gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        if (xRayEnabled) {
            //gl.glDepthFunc(GL_ALWAYS);
            gl.glClear(GL_DEPTH_BUFFER_BIT);
        }
        gl.glDepthFunc(GL_LEQUAL);
        //gl.glDepthFunc(GL_LEQUAL);

        //gl.glEnable(GL_ALPHA_TEST);
        //gl.glAlphaFunc(GL_GREATER, 0.9f);

        applyCameraTransform(gl);

        final int coordsPerVertex = 3;
        final int vertexPerTile = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerTile;

        gl.glBegin(GL_QUADS);
        for (int i = 0; i < coords.length / coordsPerPlate; i++) {
            for (int j = 0; j < vertexPerTile; j++) {
                float value = (coords[i * coordsPerPlate + j * coordsPerVertex + 2] - minZ) / (maxZ - minZ);
                value = Math.max(Math.min(value, 1.0f), 0.0f);
                Color c = Color.getHSBColor(0.66f, 1.0f - value, 1.0f);
                //Color c = Color.getHSBColor(1.0f - value, 1.0f, 1.0f);
                gl.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, platesAlpha);
                gl.glVertex3fv(coords, i * coordsPerPlate + j * coordsPerVertex);
            }
        }
        gl.glEnd();

    }

    public void drawWireframe(GL2 gl) {
        float[] coords = cHandler.getCollisionsBW3D().getVCoords();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // adjust OpenGL settings and draw model
        gl.glEnable(GL_DEPTH_TEST);
        if (xRayEnabled) {
            //gl.glDepthFunc(GL_ALWAYS);
            //gl.glClear(GL_DEPTH_BUFFER_BIT);
            gl.glDepthFunc(GL_LEQUAL);
        } else {
            gl.glDepthFunc(GL_LEQUAL);
        }

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        gl.glDisable(GL_TEXTURE_2D);

        gl.glLineWidth(1.5f);

        applyCameraTransform(gl);

        final int coordsPerVertex = 3;
        final int vertexPerTile = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerTile;
        gl.glBegin(GL_QUADS);
        for (int i = 0; i < coords.length / coordsPerPlate; i++) {
            for (int j = 0; j < vertexPerTile; j++) {
                gl.glColor3f(0, 0, 0);
                gl.glVertex3fv(coords, i * coordsPerPlate + j * coordsPerVertex);
            }
        }
        gl.glEnd();

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case Edit:
                cameraX = 0.0f;
                cameraY = 0.0f;
                cameraZ = 32.0f;

                cameraRotX = 0.0f;
                cameraRotY = 0.0f;
                cameraRotZ = 0.0f;
                break;
            case View:
                cameraX = 0.0f;
                cameraY = 0.0f;
                cameraZ = 32.0f;

                cameraRotX = defaultCamRotX;
                cameraRotY = defaultCamRotY;
                cameraRotZ = defaultCamRotZ;
                break;
        }
    }

    private Point getCoordsInMap(MouseEvent evt) {
        return new Point(
                Math.max(Math.min(getFixedMouseX(evt) / tileSize, cols - 1), 0),
                Math.max(Math.min(getFixedMouseY(evt) / tileSize, rows - 1), 0));
    }

    private int getFixedMouseX(MouseEvent evt) {
        return (int) (evt.getX() * ((float) width / getWidth()));
    }

    private int getFixedMouseY(MouseEvent evt) {
        return (int) (evt.getY() * ((float) height / getHeight()));
    }

    private BufferedImage getArrowPoint(int x, int y) {
        float slopeX = cHandler.getCollisionsBW3D().getSlopeMeanX(x, y);
        float slopeY = cHandler.getCollisionsBW3D().getSlopeMeanY(x, y);

        final float min = 0.001f;
        int i = 1, j = 1;
        if (Math.abs(slopeX) > min) {
            if (slopeX < 0) {
                i--;
            } else {
                i++;
            }
        }
        if (Math.abs(slopeY) > min) {
            if (slopeY < 0) {
                j--;
            } else {
                j++;
            }
        }
        return arrows[j][i];
    }

    public void initHandler(MapEditorHandler handler, CollisionHandlerBW cHandler) {
        this.handler = handler;
        this.cHandler = cHandler;
    }

    public void setxRayEnabled(boolean xRayEnabled) {
        this.xRayEnabled = xRayEnabled;
    }

    public void setMapEnabled(boolean mapEnabled) {
        this.mapEnabled = mapEnabled;
    }

    public void setTransparentEnabled(boolean transparentEnabled) {
        this.transparentEnabled = transparentEnabled;
    }

    public void setWireframeEnabled(boolean wireframeEnabled) {
        this.wireframeEnabled = wireframeEnabled;
    }

    public void setPlatesAlpha(float platesAlpha) {
        this.platesAlpha = platesAlpha;
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(CollisionsDisplay3D.class.getResource(path));
        } catch (IOException e) {
            return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        }
    }

    protected interface DrawGeometryGLFunction {
        void draw(GL2 gl, GeometryGL geometryGL, List<Texture> textures);
    }
}
