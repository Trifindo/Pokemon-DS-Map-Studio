/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tileset;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class Tile2 {

    //Tileset
    private Tileset tileset;

    //Tile properties
    public static int maxTileSize = 6;
    private int width;
    private int height;
    private boolean xTileable;
    private boolean yTileable;
    private boolean uTileable;
    private boolean vTileable;
    private boolean globalTexMapping;
    private float globalTexScale;
    private float xOffset;
    private float yOffset;

    //Path and OBJ file name
    private String folderPath;
    private String objFilename;

    //Thumbnail image
    private BufferedImage thumbnail;
    private BufferedImage smallThumbnail;

    //Geometry
    private ArrayList<TileGeometry> tris;
    private ArrayList<TileGeometry> quads;
    
    
    public Tile2(Tileset tileset, String folderPath, String objFilename,
            int width, int height, boolean xTileable, boolean yTileable,
            boolean uTileable, boolean vTileable,
            boolean globalTexMapping, float globalTexScale,
            float xOffset, float yOffset)
            throws IOException, TextureNotFoundException,
            NormalsNotFoundException {

        this.tileset = tileset;

        this.folderPath = folderPath;
        this.objFilename = objFilename;

        this.width = width;
        this.height = height;

        this.xTileable = xTileable;
        this.yTileable = yTileable;
        this.uTileable = uTileable;
        this.vTileable = vTileable;

        this.globalTexMapping = globalTexMapping;
        this.globalTexScale = globalTexScale;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        //loadFromObj(folderPath, objFilename);
    }


}
