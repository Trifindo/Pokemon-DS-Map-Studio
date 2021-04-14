package formats.bdhc;

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_GREATER;
import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

public class BdhcDisplay3D extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    //Editor Handler
    protected MapEditorHandler handler;
    protected BdhcHandler bdhcHandler;

    //Grid
    protected final int cols = 32;
    protected final int rows = 32;
    protected final int tileSize = 16;
    protected final int borderSize = 1;
    protected final int width = (cols + borderSize * 2) * tileSize;
    protected final int height = (rows + borderSize * 2) * tileSize;

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
    protected float lastMouseX, lastMouseY;

    //Update
    protected boolean updateRequested = false;

    //Plates
    protected float[] plateCoords;
    protected final float minZ = -10.0f, maxZ = 10.0f;

    protected boolean mapEnabled = true;
    protected boolean wireframeEnabled = true;
    protected boolean transparentEnabled = true;
    protected boolean xRayEnabled = true;

    public BdhcDisplay3D() {
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
    }


    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        //Scene
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 32.0f;

        cameraRotX = defaultCamRotX;
        cameraRotY = defaultCamRotY;
        cameraRotZ = defaultCamRotZ;


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

            //Load Textures into OpenGL
            handler.getTileset().loadTexturesGL();
            handler.getBorderMapsTileset().loadTexturesGL();

            updateMapLayersGL();

            updateRequested = false;
        }

        if(mapEnabled){
            if (handler.getTileset().size() > 0) {
                drawOpaqueMaps(gl);
            }

            //Draw semitransparent tiles
            if (handler.getTileset().size() > 0) {
                drawTransparentMaps(gl);
            }
        }


        if(bdhcHandler != null) {
            if(transparentEnabled){
                drawTransparent(gl);
            }else{
                drawOpaque(gl);
            }

            if(wireframeEnabled){
                drawWireframe(gl);
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
        } else if (SwingUtilities.isRightMouseButton(e)
                | SwingUtilities.isMiddleMouseButton(e)) {
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

    public void setHandler(MapEditorHandler handler, BdhcHandler bdhcHandler) {
        this.handler = handler;
        this.bdhcHandler = bdhcHandler;
    }

    public void drawPlates(GL2 gl){
        applyCameraTransform(gl);

        final int coordsPerVertex = 3;
        final int vertexPerPlate = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerPlate;
        for(int i = 0; i < plateCoords.length / coordsPerPlate; i++){
            gl.glBegin(GL_QUADS);
            for (int j = 0; j < vertexPerPlate; j++) {
                float color = (plateCoords[i * coordsPerPlate + j * coordsPerVertex + 2] - minZ) / (maxZ - minZ);
                //float scale = 0.75f;
                //color *= (1.0f - scale) + scale;
                //color = Math.max(Math.min(color, 1.0f), 0.0f);
                color = Math.max(Math.min(color, 1.0f), 0.0f);
                gl.glColor3f(color, color, color);
                gl.glVertex3fv(plateCoords, i * coordsPerPlate + j * coordsPerVertex);
            }
            gl.glEnd();
        }
    }

    public void drawOpaque(GL2 gl){
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        if(xRayEnabled){
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            gl.glDepthFunc(GL_LEQUAL);
        }else{
            gl.glDepthFunc(GL_LESS);
        }

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0.9f);

        applyCameraTransform(gl);

        final int coordsPerVertex = 3;
        final int vertexPerPlate = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerPlate;
        for(int i = 0; i < plateCoords.length / coordsPerPlate; i++){
            gl.glBegin(GL_QUADS);
            for (int j = 0; j < vertexPerPlate; j++) {
                float value = (plateCoords[i * coordsPerPlate + j * coordsPerVertex + 2] - minZ) / (maxZ - minZ);
                value = Math.max(Math.min(value, 1.0f), 0.0f);
                Color c = Color.getHSBColor(0.66f, 1.0f - value, 1.0f);
                gl.glColor3f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
                gl.glVertex3fv(plateCoords, i * coordsPerPlate + j * coordsPerVertex);
            }
            gl.glEnd();
        }
    }

    public void drawTransparent(GL2 gl){
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        if(xRayEnabled){
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            gl.glDepthFunc(GL_LEQUAL);
        }else{
            gl.glDepthFunc(GL_LEQUAL);
        }

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);

        applyCameraTransform(gl);

        final int coordsPerVertex = 3;
        final int vertexPerPlate = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerPlate;
        for(int i = 0; i < plateCoords.length / coordsPerPlate; i++){
            gl.glBegin(GL_QUADS);
            for (int j = 0; j < vertexPerPlate; j++) {
                float value = (plateCoords[i * coordsPerPlate + j * coordsPerVertex + 2] - minZ) / (maxZ - minZ);
                value = Math.max(Math.min(value, 1.0f), 0.0f);
                Color c = Color.getHSBColor(0.66f, 1.0f - value, 1.0f);
                gl.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 0.5f);
                gl.glVertex3fv(plateCoords, i * coordsPerPlate + j * coordsPerVertex);
            }
            gl.glEnd();
        }
    }

    public void drawWireframe(GL2 gl){
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
        final int vertexPerPlate = 4;
        final int coordsPerPlate = coordsPerVertex * vertexPerPlate;
        for(int i = 0; i < plateCoords.length / coordsPerPlate; i++){
            gl.glBegin(GL_QUADS);

            if(i == bdhcHandler.getSelectedPlateIndex()){
                gl.glColor3f(1.0f, 0.0f, 0.0f);
            }else{
                gl.glColor3f(0.0f, 0.0f, 0.0f);
            }

            for (int j = 0; j < vertexPerPlate; j++) {
                gl.glVertex3fv(plateCoords, i * coordsPerPlate + j * coordsPerVertex);
            }
            gl.glEnd();
        }

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void updatePlateCoords(){
        final int coordsPerPlate = 3 * 4; //3 dimensions, 4 sizes
        float[] plateCoords = new float[bdhcHandler.getPlates().size() * coordsPerPlate];
        for(int i = 0; i < bdhcHandler.getPlates().size(); i++){
            Plate plate = bdhcHandler.getPlates().get(i);
            float[] coords = plate.getVertexCoords();
            System.arraycopy(coords, 0,plateCoords, i * coordsPerPlate, coords.length);
        }
        this.plateCoords = plateCoords;

        for (int i = 2; i < coordsPerPlate; i+=3) { //push each plate up a little, to avoid Z-fighting
            plateCoords[i] += 0.075f;
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
        drawCurrentMap(gl, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });
    }

    protected void drawTransparentMaps(GL2 gl) {
        gl.glEnable(GL_BLEND);

        gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS); //Less instead of equal for drawing the grid

        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_NOTEQUAL, 0.0f);

        drawCurrentMap(gl, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });

        /*
        drawAllMaps(gl, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });*/
    }

    protected void drawAllMaps(GL2 gl, BdhcDisplay3D.DrawGeometryGLFunction drawFunction) {
        for (HashMap.Entry<Point, MapData> map : handler.getMapMatrix().getMatrix().entrySet()) {
            drawAllMapLayersGL(gl, drawFunction, map.getValue().getGrid().mapLayersGL,
                    map.getKey().x * cols, -map.getKey().y * rows, 0);
        }
    }

    protected void drawCurrentMap(GL2 gl, BdhcDisplay3D.DrawGeometryGLFunction drawFunction) {
        MapData map = handler.getCurrentMap();
        drawAllMapLayersGL(gl, drawFunction, map.getGrid().mapLayersGL,
                0, 0, 0);
    }

    protected void drawAllMapLayersGL(GL2 gl, BdhcDisplay3D.DrawGeometryGLFunction drawFunction, MapLayerGL[] mapLayersGL, float x, float y, float z) {
        for (int i = 0; i < mapLayersGL.length; i++) {
            if (handler.renderLayers[i]) {
                if (mapLayersGL[i] != null) {
                    drawMapLayerGL(gl, drawFunction, mapLayersGL[i], x, y, z);
                }
            }
        }
    }

    protected void drawMapLayerGL(GL2 gl, BdhcDisplay3D.DrawGeometryGLFunction drawFunction, MapLayerGL mapLayerGL, float x, float y, float z) {
        applyCameraTransform(gl);

        gl.glTranslatef(x, y, z);

        for (GeometryGL geometryGL : mapLayerGL.getGeometryGL().values()) {
            drawFunction.draw(gl, geometryGL, handler.getTileset().getTextures());
        }
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

    protected static interface DrawGeometryGLFunction {
        public void draw(GL2 gl, GeometryGL geometryGL, ArrayList<Texture> textures);
    }
}
