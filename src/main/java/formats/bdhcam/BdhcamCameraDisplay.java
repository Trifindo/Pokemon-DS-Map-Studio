package formats.bdhcam;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import formats.bdhcam.camplate.CameraSettings;
import editor.grid.GeometryGL;
import editor.grid.MapGrid;
import editor.grid.MapLayerGL;
import editor.handler.MapData;
import editor.handler.MapEditorHandler;
import utils.Utils;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_NOTEQUAL;
import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

public class BdhcamCameraDisplay extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {
	
	//Cam Choice
	private static final boolean wideCamMode = false;

    //Editor Handler
    protected MapEditorHandler handler;
    protected BdhcamHandler bdhcamHandler;

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

    //Camera
    protected CameraSettings camera = new CameraSettings(wideCamMode);

    //Update
    protected boolean updateRequested = false;

    //Player image
    BufferedImage playerImg;
    protected float[] playerVCoords = {
            -0.5f, 0.0f, 0.1f,
            -0.5f, 0.7f, 1.5f,
            0.5f, 0.7f, 1.5f,
            0.5f, 0.0f, 0.1f
    };
    protected float[] playerTCoords ={
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };
    protected Texture playerTexture;

    public BdhcamCameraDisplay(){
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

        try {
            playerImg = Utils.loadImageAsResource("/icons/playerIcon.png");
        } catch (IOException | IllegalArgumentException ex) {

        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        //grid = Generator.generateCenteredGrid(cols, rows, gridTileSize, 0.02f);
        //gridBuffer = Buffers.newDirectFloatBuffer(grid);
        //axis = Generator.generateAxis(100.0f);

        //Load textures into OpenGL
        handler.getTileset().loadTexturesGL();
        handler.getBorderMapsTileset().loadTexturesGL();
        loadPlayerTextureGL();

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
            loadPlayerTextureGL();

            updateMapLayersGL();

            updateRequested = false;
        }

        try {
            /*
            //Draw grid
            if (drawGridEnabled) {
                drawGridMaps(gl);
            }

            //Draw axis
            drawAxis();
            */
            //Draw opaque tiles
            if (handler.getTileset().size() > 0) {
                drawOpaqueMaps(gl);
            }

            //Draw semitransparent tiles
            if (handler.getTileset().size() > 0) {
                drawTransparentMaps(gl);
            }

            drawPlayer(gl);

            gl.glFinish();
        } catch (GLException ex) {
            ex.printStackTrace();
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

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    protected void applyCameraTransform(GL2 gl) {
        gl.glLoadIdentity();

        float aspect = (float) getWidth() / (float) getHeight();
        float jogampFOV;

		if (wideCamMode) {
		    jogampFOV = 38.5f;
		} else {
            jogampFOV = 15.0f;
		}

        if (cameraZ < 40.0f) {
            glu.gluPerspective(jogampFOV, aspect, 1.0f, 1000.0f);
        } else {
            glu.gluPerspective(jogampFOV, aspect, 1.0f + (cameraZ - 40.0f) / 4, 1000.0f + (cameraZ - 40.0f));
        }

        if (camera == null) {
            glu.gluLookAt(
                    0.0f, 0.0f, cameraZ,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f);

            gl.glRotatef(-cameraRotX, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(-cameraRotY, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(-cameraRotZ, 0.0f, 0.0f, 1.0f);

            gl.glTranslatef(-cameraX, -cameraY, 0.0f);
        } else {
            glu.gluLookAt(
                    camera.values[0], -camera.values[1], camera.values[2],
                    camera.values[3], -camera.values[4], camera.values[5],
                    camera.values[6], -camera.values[7], camera.values[8]
            );
        }

        //Point mapCoords = handler.getMapSelected();
        //gl.glTranslatef(-mapCoords.x * MapGrid.cols, mapCoords.y * MapGrid.rows, -0.0f);


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

        drawAllMaps(gl, (gl2, geometryGL, textures) -> {
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

        drawAllMaps(gl, (gl2, geometryGL, textures) -> {
            drawGeometryGL(gl2, geometryGL, textures);
        });
    }

    protected void drawAllMaps(GL2 gl, DrawGeometryGLFunction drawFunction) {
        for (HashMap.Entry<Point, MapData> map : handler.getMapMatrix().getMatrix().entrySet()) {
            drawAllMapLayersGL(gl, drawFunction, map.getValue().getGrid().mapLayersGL,
                    map.getKey().x * cols, -map.getKey().y * rows, 0);
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
        applyCameraTransform(gl);

        Point mapCoords = handler.getMapSelected();
        gl.glTranslatef(-mapCoords.x * MapGrid.cols, mapCoords.y * MapGrid.rows, -0.0f);

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

    public void drawPlayer(GL2 gl){
        if(playerTexture != null) {

            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);

            gl.glEnable(GL_DEPTH_TEST);
            gl.glDepthFunc(GL_LESS);

            gl.glEnable(GL_ALPHA_TEST);
            gl.glAlphaFunc(GL_GREATER, 0.9f);

            gl.glLoadIdentity();
            applyCameraTransform(gl);
            gl.glTranslatef(
                    bdhcamHandler.getPlayerX() - 16.0f + 0.5f,
                    -(bdhcamHandler.getPlayerY() + 1 - 16.0f),
                    bdhcamHandler.getSelectedPlateZ()
                    );

            gl.glBindTexture(GL_TEXTURE_2D, playerTexture.getTextureObject());
            gl.glEnable(GL_TEXTURE_2D);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            gl.glBegin(GL_QUADS);
            for (int i = 0; i < 4; i++) {
                gl.glTexCoord2fv(playerTCoords, i * 2);
                gl.glVertex3fv(playerVCoords, i * 3);
            }
            gl.glEnd();
        }
    }

    public void loadPlayerTextureGL(){
        if(playerImg != null){
            Texture tex = null;
            try {
                BufferedImage img = playerImg;
                ImageUtil.flipImageVertically(img);
                tex = AWTTextureIO.newTexture(GLProfile.getDefault(), img, false);
                playerTexture = tex;
            }catch(Exception ex){

            }
        }
    }

    public void setHandler(MapEditorHandler handler, BdhcamHandler bdhcamHandler) {
        this.handler = handler;
        this.bdhcamHandler = bdhcamHandler;
    }

    public void requestUpdate(){
        updateRequested = true;
    }

    public void setCamera(CameraSettings camera){
        this.camera = camera;
    }

    protected static interface DrawGeometryGLFunction {
        public void draw(GL2 gl, GeometryGL geometryGL, ArrayList<Texture> textures);
    }



}

