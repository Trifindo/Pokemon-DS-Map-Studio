/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tileset;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.ImageIO;

import graphicslib3D.Point3D;
import utils.Utils;
import utils.Utils.IntTuple;
import static utils.Utils.floatListToArray;

/**
 *
 * @author Trifindo
 */
public class Tile {

    private Tileset tileset;

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

    private String folderPath;
    private String objFilename;

    //Model OBJ
    private ArrayList<Float> vCoordsObj = new ArrayList<>();
    private ArrayList<Float> tCoordsObj = new ArrayList<>();
    private ArrayList<Float> nCoordsObj = new ArrayList<>();
    private ArrayList<Float> colorsObj = new ArrayList<>();

    private ArrayList<Face> fIndsQuad = new ArrayList<>();
    private ArrayList<Face> fIndsTri = new ArrayList<>();

    //Model OpenGL
    private float[] vCoordsTri;
    private float[] tCoordsTri;
    private float[] nCoordsTri;
    private float[] colorsTri;

    private float[] vCoordsQuad;
    private float[] tCoordsQuad;
    private float[] nCoordsQuad;
    private float[] colorsQuad;

    private ArrayList<Integer> textureIDs = new ArrayList<>();
    private ArrayList<Integer> texOffsetsTri = new ArrayList<>();
    private ArrayList<Integer> texOffsetsQuad = new ArrayList<>();

    private BufferedImage thumbnail;
    private BufferedImage smallThumbnail;

    public Tile(Tileset tileset, String folderPath, String objFilename,
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

        loadFromObj(folderPath, objFilename);

    }

    public Tile(Tileset tileset, String filePath) throws IOException,
            TextureNotFoundException, NormalsNotFoundException {
        this(tileset, new File(filePath).getParent(),
                new File(filePath).getName(), 1, 1, false,
                false, false, false, false, 1.0f, 0.0f, 0.0f);
    }

    public Tile(Tileset tileset, String filePath, Tile tile) throws IOException,
            TextureNotFoundException, NormalsNotFoundException {
        this(tileset, new File(filePath).getParent(),
                new File(filePath).getName(), tile.getWidth(), tile.getHeight(),
                tile.isXtileable(), tile.isYtileable(),
                tile.isUtileable(), tile.isVtileable(),
                tile.useGlobalTextureMapping(),
                tile.getGlobalTextureScale(),
                tile.getXOffset(), tile.getYOffset());
    }

    public Tile() {
        width = 1;
        height = 1;

        xTileable = false;
        yTileable = false;
        uTileable = false;
        vTileable = false;

        globalTexMapping = false;
        globalTexScale = 1.0f;
        xOffset = 0.0f;
        yOffset = 0.0f;
    }

    @Override
    public Tile clone() {
        Tile tile = new Tile();

        tile.tileset = tileset;

        tile.width = width;
        tile.height = height;
        tile.xTileable = xTileable;
        tile.yTileable = yTileable;
        tile.uTileable = uTileable;
        tile.vTileable = vTileable;
        tile.globalTexMapping = globalTexMapping;
        tile.globalTexScale = globalTexScale;
        tile.xOffset = xOffset;
        tile.yOffset = yOffset;

        tile.folderPath = folderPath;
        tile.objFilename = objFilename;

        tile.vCoordsObj = Utils.cloneArrayListFloat(vCoordsObj);
        tile.tCoordsObj = Utils.cloneArrayListFloat(tCoordsObj);
        tile.nCoordsObj = Utils.cloneArrayListFloat(nCoordsObj);
        tile.colorsObj = Utils.cloneArrayListFloat(colorsObj);

        tile.fIndsQuad = Face.cloneArrayList(fIndsQuad);
        tile.fIndsTri = Face.cloneArrayList(fIndsTri);

        tile.textureIDs = Utils.cloneArrayListInt(textureIDs);
        tile.texOffsetsTri = Utils.cloneArrayListInt(texOffsetsTri);
        tile.texOffsetsQuad = Utils.cloneArrayListInt(texOffsetsQuad);

        tile.vCoordsTri = Utils.cloneArray(vCoordsTri);
        tile.tCoordsTri = Utils.cloneArray(tCoordsTri);
        tile.nCoordsTri = Utils.cloneArray(nCoordsTri);
        tile.colorsTri = Utils.cloneArray(colorsTri);
        tile.vCoordsQuad = Utils.cloneArray(vCoordsQuad);
        tile.tCoordsQuad = Utils.cloneArray(tCoordsQuad);
        tile.nCoordsQuad = Utils.cloneArray(nCoordsQuad);
        tile.colorsQuad = Utils.cloneArray(colorsQuad);

        tile.thumbnail = thumbnail;
        tile.smallThumbnail = smallThumbnail;

        return tile;
    }

    public Tile cloneObjData() {
        Tile tile = new Tile();

        tile.tileset = tileset;

        tile.width = width;
        tile.height = height;
        tile.xTileable = xTileable;
        tile.yTileable = yTileable;
        tile.uTileable = uTileable;
        tile.vTileable = vTileable;
        tile.globalTexMapping = globalTexMapping;
        tile.globalTexScale = globalTexScale;
        tile.xOffset = xOffset;
        tile.yOffset = yOffset;

        tile.folderPath = folderPath;
        tile.objFilename = objFilename;

        tile.vCoordsObj = Utils.cloneArrayListFloat(vCoordsObj);
        tile.tCoordsObj = Utils.cloneArrayListFloat(tCoordsObj);
        tile.nCoordsObj = Utils.cloneArrayListFloat(nCoordsObj);
        tile.colorsObj = Utils.cloneArrayListFloat(colorsObj);

        tile.fIndsQuad = Face.cloneArrayList(fIndsQuad);
        tile.fIndsTri = Face.cloneArrayList(fIndsTri);

        tile.textureIDs = textureIDs;
        tile.texOffsetsTri = texOffsetsTri;
        tile.texOffsetsQuad = texOffsetsQuad;

        return tile;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.width;
        hash = 11 * hash + this.height;
        hash = 11 * hash + (this.xTileable ? 1 : 0);
        hash = 11 * hash + (this.yTileable ? 1 : 0);
        hash = 11 * hash + (this.uTileable ? 1 : 0);
        hash = 11 * hash + (this.vTileable ? 1 : 0);
        hash = 11 * hash + (this.globalTexMapping ? 1 : 0);
        hash = 11 * hash + Float.floatToIntBits(this.globalTexScale);
        hash = 11 * hash + Float.floatToIntBits(this.xOffset);
        hash = 11 * hash + Float.floatToIntBits(this.yOffset);
        hash = 11 * hash + Objects.hashCode(this.objFilename);
        hash = 11 * hash + Objects.hashCode(this.vCoordsObj);
        hash = 11 * hash + Objects.hashCode(this.tCoordsObj);
        hash = 11 * hash + Objects.hashCode(this.nCoordsObj);
        hash = 11 * hash + Objects.hashCode(this.colorsObj);
        hash = 11 * hash + Arrays.hashCode(this.colorsTri);
        hash = 11 * hash + Arrays.hashCode(this.colorsQuad);
        hash = 11 * hash + Objects.hashCode(this.textureIDs);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tile other = (Tile) obj;
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (this.xTileable != other.xTileable) {
            return false;
        }
        if (this.yTileable != other.yTileable) {
            return false;
        }
        if (this.uTileable != other.uTileable) {
            return false;
        }
        if (this.vTileable != other.vTileable) {
            return false;
        }
        if (this.globalTexMapping != other.globalTexMapping) {
            return false;
        }
        if (Float.floatToIntBits(this.globalTexScale) != Float.floatToIntBits(other.globalTexScale)) {
            return false;
        }
        if (Float.floatToIntBits(this.xOffset) != Float.floatToIntBits(other.xOffset)) {
            return false;
        }
        if (Float.floatToIntBits(this.yOffset) != Float.floatToIntBits(other.yOffset)) {
            return false;
        }
        if (!Objects.equals(this.objFilename, other.objFilename)) {
            return false;
        }
        if (!Objects.equals(this.vCoordsObj, other.vCoordsObj)) {
            return false;
        }
        if (!Objects.equals(this.tCoordsObj, other.tCoordsObj)) {
            return false;
        }
        if (!Objects.equals(this.nCoordsObj, other.nCoordsObj)) {
            return false;
        }
        if (!Objects.equals(this.colorsObj, other.colorsObj)) {
            return false;
        }
        if (!Arrays.equals(this.colorsTri, other.colorsTri)) {
            return false;
        }
        if (!Arrays.equals(this.colorsQuad, other.colorsQuad)) {
            return false;
        }
        if (!Objects.equals(this.textureIDs, other.textureIDs)) {
            return false;
        }
        return true;
    }

    public boolean equalsVisualData(Object obj) {
        final Tile other = (Tile) obj;
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (this.xTileable != other.xTileable) {
            return false;
        }
        if (this.yTileable != other.yTileable) {
            return false;
        }
        if (this.uTileable != other.uTileable) {
            return false;
        }
        if (this.vTileable != other.vTileable) {
            return false;
        }
        if (this.globalTexMapping != other.globalTexMapping) {
            return false;
        }
        if (Float.floatToIntBits(this.globalTexScale) != Float.floatToIntBits(other.globalTexScale)) {
            return false;
        }
        if (Float.floatToIntBits(this.xOffset) != Float.floatToIntBits(other.xOffset)) {
            return false;
        }
        if (Float.floatToIntBits(this.yOffset) != Float.floatToIntBits(other.yOffset)) {
            return false;
        }
        if (!Objects.equals(this.objFilename, other.objFilename)) {
            return false;
        }
        if (!Objects.equals(this.vCoordsObj, other.vCoordsObj)) {
            return false;
        }
        if (!Objects.equals(this.tCoordsObj, other.tCoordsObj)) {
            return false;
        }
        if (!Objects.equals(this.nCoordsObj, other.nCoordsObj)) {
            return false;
        }
        if (!Objects.equals(this.colorsObj, other.colorsObj)) {
            return false;
        }
        if (!Arrays.equals(this.colorsTri, other.colorsTri)) {
            return false;
        }
        if (!Arrays.equals(this.colorsQuad, other.colorsQuad)) {
            return false;
        }

        for (int i = 0; i < textureIDs.size(); i++) {
            try {
                TilesetMaterial mat = tileset.getMaterial(textureIDs.get(i));
                TilesetMaterial matOther = other.getTileset().getMaterial(other.getTextureIDs().get(i));
                if (!mat.getImageName().equals(matOther.getImageName())) {
                    return false;
                }
            } catch (Exception ex) {
                return false;
            }

        }

        return true;
    }

    private void loadFromObj(String folderPath, String objName) throws IOException,
            TextureNotFoundException, NormalsNotFoundException {
        vCoordsObj = new ArrayList<>();
        tCoordsObj = new ArrayList<>();
        nCoordsObj = new ArrayList<>();
        colorsObj = new ArrayList<>();

        ArrayList<ArrayList<Face>> fIndsQuadArray = new ArrayList<>();
        ArrayList<ArrayList<Face>> fIndsTriArray = new ArrayList<>();

        ArrayList<String> materialNames = new ArrayList();

        InputStream inputObj = new FileInputStream(new File(folderPath + "/" + objName));
        BufferedReader brObj = new BufferedReader(new InputStreamReader(inputObj));

        boolean firstColorAdded = false;
        int firstColorIndex = 0;
        String mtlName = "";
        int materialIndex = 0;
        String lineObj;
        while ((lineObj = brObj.readLine()) != null) {
            if (lineObj.startsWith("mtllib")) {
                mtlName = lineObj.substring(lineObj.indexOf(" ") + 1, lineObj.length());
            } else if (lineObj.startsWith("o")) {

            } else if (lineObj.startsWith("v ")) {
                lineObj = lineObj.substring(2);
                for (String s : lineObj.split(" ")) {
                    try {
                        vCoordsObj.add(Float.valueOf(s));
                    } catch (NumberFormatException ex) {
                        vCoordsObj.add(0.0f); //TODO: Improve this?
                    }
                }
            } else if (lineObj.startsWith("vt")) {
                for (String s : (lineObj.substring(3)).split(" ")) {
                    try {
                        tCoordsObj.add(Float.valueOf(s));
                    } catch (NumberFormatException ex) {
                        tCoordsObj.add(0.0f);
                    }
                }
            } else if (lineObj.startsWith("vn")) {
                for (String s : (lineObj.substring(3)).split(" ")) {
                    try {
                        nCoordsObj.add(Float.valueOf(s));
                    } catch (NumberFormatException ex) {
                        nCoordsObj.add(0.0f);
                    }
                }
            } else if (lineObj.startsWith("c")) {
                for (String s : (lineObj.substring(2)).split(" ")) {
                    try {
                        colorsObj.add(Float.valueOf(s));
                    } catch (NumberFormatException ex) {
                        colorsObj.add(1.0f);
                    }
                }
            } else if (lineObj.startsWith("usemtl")) {
                //String name = lineObj.split(" ")[1];
                String name = lineObj.substring(lineObj.indexOf(" ") + 1, lineObj.length());
                materialIndex = materialNames.indexOf(name);
                if (materialIndex == -1) {
                    fIndsQuadArray.add(new ArrayList<>());
                    fIndsTriArray.add(new ArrayList<>());
                    materialIndex = materialNames.size();
                    materialNames.add(name);
                }
            } else if (lineObj.startsWith("f")) {
                String[] splittedLine = (lineObj.substring(2)).split(" ");
                int numVertex = 0;
                for (int i = 0; i < splittedLine.length; i++) {
                    if (splittedLine[i].contains("/")) {
                        numVertex++;
                    }
                }
                if (numVertex > 3) {
                    numVertex = 4;
                } else {
                    numVertex = 3;
                }
                Face f = new Face(numVertex > 3);
                for (int i = 0; i < numVertex; i++) {
                    String[] sArray = splittedLine[i].split("/");
                    f.vInd[i] = Integer.valueOf(sArray[0]);
                    f.tInd[i] = Integer.valueOf(sArray[1]);
                    if (sArray.length > 2) {
                        f.nInd[i] = Integer.valueOf(sArray[2]);
                    } else {
                        throw new NormalsNotFoundException("The OBJ file \""
                                + objName + "\" does not contain calculated normals.");
                    }
                    if (sArray.length > 3) {
                        f.cInd[i] = Integer.valueOf(sArray[3]);
                    } else {
                        if (!firstColorAdded) {
                            firstColorIndex = colorsObj.size() / 3 + 1;
                            f.cInd[i] = firstColorIndex;
                            colorsObj.add(1.0f);
                            colorsObj.add(1.0f);
                            colorsObj.add(1.0f);
                            firstColorAdded = true;
                        } else {
                            f.cInd[i] = firstColorIndex;
                        }
                    }
                }
                if (f.isQuad) {
                    fIndsQuadArray.get(materialIndex).add(f);
                } else {
                    fIndsTriArray.get(materialIndex).add(f);
                }
            }
        }
        inputObj.close();

        //Load Mtl file
        int numMaterials = countNumberOfStarts(new File(folderPath + "/" + mtlName), "newmtl");
        for (int i = 0; i < numMaterials; i++) {
            textureIDs.add(0);
        }
        InputStream inputMtl = new FileInputStream(new File(folderPath + "/" + mtlName));
        BufferedReader brMtl = new BufferedReader(new InputStreamReader(inputMtl));
        int matIndex = 0;
        String lineMtl;
        while ((lineMtl = brMtl.readLine()) != null) {
            if (lineMtl.startsWith("newmtl")) {
                //String matName = lineMtl.split(" ")[1];
                String matName = lineMtl.substring(lineMtl.indexOf(" ") + 1, lineMtl.length());
                matIndex = materialNames.indexOf(matName);
            } else if (lineMtl.startsWith("map_Kd")) {
                //String textName = lineMtl.split(" ")[1];
                String textName = lineMtl.substring(lineMtl.indexOf(" ") + 1, lineMtl.length());
                int textIndex = tileset.getIndexOfMaterialByImgName(textName);
                if (textIndex == -1) { //Not found
                    if (new File(folderPath + "/" + textName).exists() || new File(textName).exists()) {
                        textureIDs.set(matIndex, tileset.getMaterials().size());
                        try {
                            TilesetMaterial material = new TilesetMaterial();
                            try {
                                material.loadTextureImgFromPath(folderPath + "/" + textName);//Relative path
                            } catch (IOException ex) {
                                try {
                                    material.loadTextureImgFromPath(textName);//Absolute path
                                    textName = new File(textName).getName();
                                } catch (Exception ex2) {
                                    throw new IOException();
                                }
                            }
                            material.setImageName(textName);
                            String textNameImd = Utils.removeExtensionFromPath(textName);
                            material.setMaterialName(textNameImd);
                            material.setTextureNameImd(textNameImd);
                            material.setPaletteNameImd(textNameImd + "_pl");
                            tileset.getMaterials().add(material);
                        } catch (IOException ex) {
                            throw new TextureNotFoundException(
                                    "Can't open texture named: \"" + textName + "\"" + "\n"
                                    + "Make sure that the texture is in the same folder as the OBJ file");
                        }
                    } else {
                        throw new TextureNotFoundException(
                                "Texture named: \"" + textName
                                + "\" does not exist in the folder of the OBJ file");
                    }
                    //textures.add(loadTexture(folderPath + "/" + textName));
                } else {
                    textureIDs.set(matIndex, textIndex);
                }
            }
        }
        inputMtl.close();

        //Fix material names for avoiding sub folder issues
        for (int i = 0; i < textureIDs.size(); i++) {
            TilesetMaterial material = tileset.getMaterial(textureIDs.get(i));
            String[] splittedName = material.getImageName().split("/");
            if (splittedName.length > 1) {
                String newName = splittedName[splittedName.length - 1];
                int index = tileset.getIndexOfMaterialByImgName(newName);
                if (index == -1) {
                    material.setImageName(newName);
                    newName = Utils.removeExtensionFromPath(newName);
                    material.setMaterialName(newName);
                    material.setTextureNameImd(newName);
                    material.setPaletteNameImd(newName + "_pl");
                } else {
                    textureIDs.set(i, index);
                }
            }
        }

        //Fix duplicated Texture IDs
        ArrayList<ArrayList<Face>> fIndsQuadArrayFixed = new ArrayList<>();
        ArrayList<ArrayList<Face>> fIndsTriArrayFixed = new ArrayList<>();
        ArrayList<Integer> textureIDsFixed = new ArrayList<>();
        for (int i = 0; i < textureIDs.size(); i++) {
            int id = textureIDs.get(i);
            int index = textureIDsFixed.indexOf(id);
            if (index == -1) {
                ArrayList<Face> fIndsQuad = new ArrayList<>();
                ArrayList<Face> fIndsTri = new ArrayList<>();
                for (int j = 0; j < fIndsQuadArray.get(i).size(); j++) {
                    fIndsQuad.add(fIndsQuadArray.get(i).get(j));
                }
                for (int j = 0; j < fIndsTriArray.get(i).size(); j++) {
                    fIndsTri.add(fIndsTriArray.get(i).get(j));
                }
                fIndsQuadArrayFixed.add(fIndsQuad);
                fIndsTriArrayFixed.add(fIndsTri);
                textureIDsFixed.add(id);
            } else {
                ArrayList<Face> fIndsQuad = fIndsQuadArrayFixed.get(index);
                ArrayList<Face> fIndsTri = fIndsTriArrayFixed.get(index);
                for (int j = 0; j < fIndsQuadArray.get(i).size(); j++) {
                    fIndsQuad.add(fIndsQuadArray.get(i).get(j));
                }
                for (int j = 0; j < fIndsTriArray.get(i).size(); j++) {
                    fIndsTri.add(fIndsTriArray.get(i).get(j));
                }
            }
        }
        textureIDs = textureIDsFixed;
        fIndsQuadArray = fIndsQuadArrayFixed;
        fIndsTriArray = fIndsTriArrayFixed;

        //Group all indices by material into a single array
        fIndsQuad = new ArrayList<>();
        fIndsTri = new ArrayList<>();
        for (int i = 0, c = 0; i < fIndsQuadArray.size(); i++) {
            texOffsetsQuad.add(c);
            for (int j = 0; j < fIndsQuadArray.get(i).size(); j++, c++) {
                fIndsQuad.add(fIndsQuadArray.get(i).get(j));
            }
        }
        for (int i = 0, c = 0; i < fIndsTriArray.size(); i++) {
            texOffsetsTri.add(c);
            for (int j = 0; j < fIndsTriArray.get(i).size(); j++, c++) {
                fIndsTri.add(fIndsTriArray.get(i).get(j));
            }
        }

        /*
        //Add default color
        colorsObj.add(1.0f);
        colorsObj.add(1.0f);
        colorsObj.add(1.0f);*/
        //Tranform obj vertices and textures into opengl vertices and textures
        objDataToGlData();
    }

    private static int countNumberOfStarts(File file, String content) throws IOException {
        InputStream input = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        ArrayList<String> matLines = new ArrayList<>();
        //int count = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(content)) {
                if (!matLines.contains(line)) {
                    matLines.add(line);
                }
                //count++;
            }
        }
        return matLines.size();
        //return count;
    }

    public void objDataToGlData() {
        ArrayList<Float> vCoordsTri = new ArrayList<>();
        ArrayList<Float> tCoordsTri = new ArrayList<>();
        ArrayList<Float> nCoordsTri = new ArrayList<>();
        ArrayList<Float> colorsTri = new ArrayList<>();

        ArrayList<Float> vCoordsQuad = new ArrayList<>();
        ArrayList<Float> tCoordsQuad = new ArrayList<>();
        ArrayList<Float> nCoordsQuad = new ArrayList<>();
        ArrayList<Float> colorsQuad = new ArrayList<>();

        for (int i = 0; i < fIndsQuad.size(); i++) {//face
            Face f = fIndsQuad.get(i);
            for (int j = 0; j < 4; j++) {//vertex
                for (int k = 0; k < 3; k++) {//coord
                    vCoordsQuad.add(vCoordsObj.get((f.vInd[j] - 1) * 3 + k));
                }
                for (int k = 0; k < 2; k++) {
                    tCoordsQuad.add(tCoordsObj.get((f.tInd[j] - 1) * 2 + k));
                }
                for (int k = 0; k < 3; k++) {
                    nCoordsQuad.add(nCoordsObj.get((f.nInd[j] - 1) * 3 + k));
                }
                for (int k = 0; k < 3; k++) {
                    colorsQuad.add(colorsObj.get((f.cInd[j] - 1) * 3 + k));
                }
            }
        }

        for (int i = 0; i < fIndsTri.size(); i++) {//face
            Face f = fIndsTri.get(i);
            for (int j = 0; j < 3; j++) {//vertex
                for (int k = 0; k < 3; k++) {//coord
                    vCoordsTri.add(vCoordsObj.get((f.vInd[j] - 1) * 3 + k));
                }
                for (int k = 0; k < 2; k++) {
                    tCoordsTri.add(tCoordsObj.get((f.tInd[j] - 1) * 2 + k));
                }
                for (int k = 0; k < 3; k++) {
                    nCoordsTri.add(nCoordsObj.get((f.nInd[j] - 1) * 3 + k));
                }
                for (int k = 0; k < 3; k++) {
                    colorsTri.add(colorsObj.get((f.cInd[j] - 1) * 3 + k));
                }
            }
        }

        this.vCoordsQuad = floatListToArray(vCoordsQuad);
        this.tCoordsQuad = floatListToArray(tCoordsQuad);
        this.nCoordsQuad = floatListToArray(nCoordsQuad);
        this.colorsQuad = floatListToArray(colorsQuad);

        this.vCoordsTri = floatListToArray(vCoordsTri);
        this.tCoordsTri = floatListToArray(tCoordsTri);
        this.nCoordsTri = floatListToArray(nCoordsTri);
        this.colorsTri = floatListToArray(colorsTri);

    }

    public static BufferedImage loadTextureImgWithDefault(String path) throws IOException {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            img = Tileset.defaultTexture;
        }
        return img;
    }

    public static BufferedImage loadTextureImg(String path) throws IOException {
        BufferedImage img = null;
        img = ImageIO.read(new File(path));
        return img;
    }

    public static Texture loadTexture(String path) {
        Texture tex = null;
        try {
            tex = TextureIO.newTexture(new File(path), false);
        } catch (Exception e) {
            tex = AWTTextureIO.newTexture(GLProfile.getDefault(), Tileset.defaultTexture, false);
            //e.printStackTrace();
        }
        return tex;
    }

    /*
    public static Texture loadTextureResource(String textureFileName) {
        Texture tex = null;

        try {
            tex = TextureIO.newTexture(new File(Tile.class
                    .getClassLoader().getResource(textureFileName).getFile()), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tex;
    }*/
    public boolean isTextureUsed(int index) {
        for (int i = 0; i < textureIDs.size(); i++) {
            if (textureIDs.get(i) == index) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Face> getFaceIndQuadOfTex(int index) {
        int start = texOffsetsQuad.get(index);
        int end;
        if (index < textureIDs.size() - 1) {
            end = texOffsetsQuad.get(index + 1);
        } else {
            end = fIndsQuad.size();
        }

        return new ArrayList(this.fIndsQuad.subList(start, end));
    }

    public ArrayList<Face> getFaceIndTriOfTex(int index) {
        int start = texOffsetsTri.get(index);
        int end;
        if (index < textureIDs.size() - 1) {
            end = texOffsetsTri.get(index + 1);
        } else {
            end = fIndsTri.size();
        }

        return new ArrayList(fIndsTri.subList(start, end));
    }

    public void swapMaterials(int indexMat1, int indexMat2) {
        for (int i = 0; i < textureIDs.size(); i++) {
            int matIndex = textureIDs.get(i);
            if (matIndex == indexMat1) {
                textureIDs.set(i, indexMat2);
            } else if (matIndex == indexMat2) {
                textureIDs.set(i, indexMat1);
            }
        }
    }

    public void moveModel(float x, float y, float z) {
        moveModel(new float[]{x, y, z});
    }

    public void moveModel(float[] dpos) {
        moveCoords(vCoordsObj, dpos);
        moveCoords(vCoordsQuad, dpos);
        moveCoords(vCoordsTri, dpos);
    }

    public void scaleModel(float scale) {
        float[] scaleXYZ = new float[]{scale, scale, scale};
        scaleCoords(vCoordsObj, scaleXYZ);
        scaleCoords(vCoordsQuad, scaleXYZ);
        scaleCoords(vCoordsTri, scaleXYZ);
    }

    private void scaleCoords(float[] array, float[] scale) {
        for (int i = 0; i < array.length; i += 3) {
            for (int j = 0; j < 3; j++) {
                array[i + j] *= scale[j];
            }
        }
    }

    private void scaleCoords(ArrayList<Float> array, float[] scale) {
        for (int i = 0; i < array.size(); i += 3) {
            for (int j = 0; j < 3; j++) {
                int index = i + j;
                array.set(index, array.get(index) * scale[j]);
            }
        }
    }

    public void flipModelYZ() {
        flipCoordsYZ(vCoordsObj);
        flipCoordsYZ(nCoordsObj);
        flipCoordsYZ(vCoordsQuad);
        flipCoordsYZ(vCoordsTri);
        flipCoordsYZ(nCoordsQuad);
        flipCoordsYZ(nCoordsTri);
    }

    public void flipObjModelYZ() {
        flipCoordsYZ(vCoordsObj);
        flipCoordsYZ(nCoordsObj);
    }

    public void flipObjModelInvertedYZ() {
        flipCoordsInvertedYZ(vCoordsObj);
        flipCoordsInvertedYZ(nCoordsObj);
    }

    public void scaleObjModel(float scale) {
        float[] scaleXYZ = new float[]{scale, scale, scale};
        scaleCoords(vCoordsObj, scaleXYZ);
    }

    private void flipCoordsYZ(float[] array) {
        for (int i = 0; i < array.length; i += 3) {
            float temp = array[i + 1];
            array[i + 1] = -array[i + 2];
            array[i + 2] = temp;
        }
    }

    private void flipCoordsYZ(ArrayList<Float> array) {
        for (int i = 0; i < array.size(); i += 3) {
            float temp = array.get(i + 1);
            array.set(i + 1, -array.get(i + 2));
            array.set(i + 2, temp);
        }
    }

    private void flipCoordsInvertedYZ(ArrayList<Float> array) {
        for (int i = 0; i < array.size(); i += 3) {
            float temp = -array.get(i + 1);
            array.set(i + 1, array.get(i + 2));
            array.set(i + 2, temp);
        }
    }

    public void flipModelX() {
        flipCoordsX(vCoordsObj);
        flipCoordsX(nCoordsObj);
        flipCoordsX(vCoordsQuad);
        flipCoordsX(vCoordsTri);
        flipCoordsX(nCoordsQuad);
        flipCoordsX(nCoordsTri);
    }

    /*
    public void flipFaces(){
        int numQuads = vCoordsQuad.length / (4 * 3);
        int numTris = vCoordsQuad.length / (3 * 3);
        for(int i = 0; i < numQuads; i++){
            flipFace(vCoordsQuad, i, 4, 3);
            flipFace(tCoordsQuad, i, 4, 2);
            flipFace(nCoordsQuad, i, 4, 3);
        }
        //NOT FINISHED!!
    }
    
    public void flipFace(float[] coords, int faceIndex, int vertexPerFace, int coordsPerVertex){
        int coordsPerFace = vertexPerFace * coordsPerVertex;
        float[] copy = new float[coordsPerFace];
        System.arraycopy(coords, faceIndex * coordsPerFace, copy, 0, copy.length);
        int[] indices = new int[vertexPerFace];
        for(int i = 0; i < indices.length; i++){
            indices[i] = vertexPerFace - 1 - i;
        }
        for(int i = 0, c = 0; i < vertexPerFace; i++){
            for(int j = 0; j < coordsPerVertex; j++, c++){
                coords[coordsPerFace * (indices[i] + faceIndex) + c] = copy[c];
            }
        }
    }*/
    public void flipCoordsX(float[] array) {
        for (int i = 0; i < array.length; i += 3) {
            array[i] = -array[i];
        }
    }

    public void flipCoordsX(ArrayList<Float> array) {
        for (int i = 0; i < array.size(); i += 3) {
            array.set(i, -array.get(i));
        }
    }

    /*
    public void flipModelX() {
        float[][] matrix = new float[][]{
            {-1.0f, 0.0f, 0.0f},
            {0.0f, 1.0f, 0.0f},
            {0.0f, 0.0f, 1.0f}};

        applyTransform(vCoordsObj, matrix);
        applyTransform(nCoordsObj, matrix);
        applyTransform(vCoordsQuad, matrix);
        applyTransform(vCoordsTri, matrix);
        applyTransform(nCoordsQuad, matrix);
        applyTransform(nCoordsTri, matrix);
    }
     */
    public void rotateModelZ() {
        rotateCoordsZ(vCoordsObj);
        rotateCoordsZ(nCoordsObj);
        rotateCoordsZ(vCoordsQuad);
        rotateCoordsZ(vCoordsTri);
        rotateCoordsZ(nCoordsQuad);
        rotateCoordsZ(nCoordsTri);

        float[] dpos = new float[]{width, 0.0f, 0.0f};
        moveCoords(vCoordsObj, dpos);
        moveCoords(vCoordsQuad, dpos);
        moveCoords(vCoordsTri, dpos);

    }

    private void applyTransform(ArrayList<Float> array, float[][] trMatrix) {
        for (int i = 0; i < array.size(); i += 3) {
            float[] newVector = new float[3];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    newVector[j] += trMatrix[j][k] * array.get(i + k);
                }
            }
            for (int j = 0; j < 3; j++) {
                array.set(i + j, newVector[j]);
            }
        }
    }

    private void applyTransform(float[] array, float[][] trMatrix) {
        for (int i = 0; i < array.length; i += 3) {
            float[] newVector = new float[3];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    newVector[j] += trMatrix[j][k] * array[i + k];
                }
            }
            System.arraycopy(newVector, 0, array, i, newVector.length);
        }
    }

    private void moveCoords(float[] array, float[] dpos) {
        for (int i = 0; i < array.length; i += 3) {
            for (int j = 0; j < 3; j++) {
                array[i + j] += dpos[j];
            }
        }
    }

    private void moveCoords(ArrayList<Float> array, float[] dpos) {
        for (int i = 0; i < array.size(); i += 3) {
            for (int j = 0; j < 3; j++) {
                int index = i + j;
                array.set(index, array.get(index) + dpos[j]);
            }
        }
    }

    private void rotateCoordsZ(float[] array) {
        for (int i = 0; i < array.length; i += 3) {
            float temp = array[i];
            array[i] = -array[i + 1];
            array[i + 1] = temp;
        }
    }

    private void rotateCoordsZ(ArrayList<Float> array) {
        for (int i = 0; i < array.size(); i += 3) {
            float temp = array.get(i);
            array.set(i, -array.get(i + 1));
            array.set(i + 1, temp);
        }
    }

    private void groupTrianglesIntoQuads() {

        for (int i = 0; i < fIndsTri.size(); i++) {

        }

    }

    private Point3D getNormal(Face f) {
        Point3D v1 = new Point3D(
                vCoordsObj.get(f.vInd[0]),
                vCoordsObj.get(f.vInd[1]),
                vCoordsObj.get(f.vInd[2])
        );
        return null; //UNFINISHED
    }

    private IntTuple indicesOfSameVCoords(Face f1, Face f2) {//UNFINISHED
        IntTuple vertexIndices = null;
        for (int i = 0; i < f1.vInd.length; i++) {
            for (int j = 0; j < f2.vInd.length; j++) {
                if (areSameEdgeCoordInds(f1.vInd, f2.vInd, i, j)) {
                    vertexIndices = new IntTuple(i, j);
                    break;
                }
            }
        }
        if (vertexIndices == null) {
            return null;
        }

        if (!areSameEdgeCoordInds(f1.tInd, f2.tInd, vertexIndices.e1, vertexIndices.e2)) {
            return null;
        }
        return vertexIndices;
    }

    private boolean areSameEdgeCoordInds(int[] coords1, int[] coords2, int ind1, int ind2) {//UNFINISHED
        return coords1[ind1] == coords2[(ind2 + 1) % coords2.length] && coords1[ind1 + 1] % coords1.length == coords2[ind2];
    }

    public void updateObjData() {
        TileGeometryCompresser.compressTile(this);
    }

    public float[] getVCoordsQuad() {
        return vCoordsQuad;
    }

    public float[] getTCoordsQuad() {
        return tCoordsQuad;
    }

    public float[] getVCoordsTri() {
        return vCoordsTri;
    }

    public float[] getTCoordsTri() {
        return tCoordsTri;
    }

    public float[] getNCoordsTri() {
        return nCoordsTri;
    }

    public float[] getNCoordsQuad() {
        return nCoordsQuad;
    }

    public float[] getColorsTri() {
        return colorsTri;
    }

    public float[] getColorsQuad() {
        return colorsQuad;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(BufferedImage img) {
        this.thumbnail = img;
    }

    public BufferedImage getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(BufferedImage img) {
        this.smallThumbnail = img;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public ArrayList<Integer> getTextureIDs() {
        return textureIDs;
    }

    public ArrayList<Integer> getTexOffsetsQuad() {
        return texOffsetsQuad;
    }

    public ArrayList<Integer> getTexOffsetsTri() {
        return texOffsetsTri;
    }

    public Texture getTexture(int index) {
        return tileset.getTexture(textureIDs.get(index));
    }

    public Tileset getTileset() {
        return tileset;
    }

    public boolean isXtileable() {
        return xTileable;
    }

    public boolean isYtileable() {
        return yTileable;
    }

    public boolean isUtileable() {
        return uTileable;
    }

    public boolean isVtileable() {
        return vTileable;
    }

    public boolean useGlobalTextureMapping() {
        return globalTexMapping;
    }

    public void setXtileable(boolean isTileable) {
        xTileable = isTileable;
    }

    public void setYtileable(boolean isTileable) {
        yTileable = isTileable;
    }

    public void setUtileable(boolean isTileable) {
        uTileable = isTileable;
    }

    public void setVtileable(boolean isTileable) {
        vTileable = isTileable;
    }

    public void setGlobalTextureMapping(boolean useGlobalTexMapping) {
        this.globalTexMapping = useGlobalTexMapping;
    }

    public ArrayList<Float> getVertexCoordsObj() {
        return vCoordsObj;
    }

    public ArrayList<Float> getTextureCoordsObj() {
        return tCoordsObj;
    }

    public ArrayList<Float> getNormalCoordsObj() {
        return nCoordsObj;
    }

    public ArrayList<Float> getColorsObj() {
        return colorsObj;
    }

    public ArrayList<Face> getFIndQuadObj() {
        return fIndsQuad;
    }

    public ArrayList<Face> getFIndTriObj() {
        return fIndsTri;
    }

    public void setVertexCoordsObj(ArrayList<Float> vertexCoordsObj) {
        this.vCoordsObj = vertexCoordsObj;
    }

    public void setTextureCoordsObj(ArrayList<Float> textureCoordsObj) {
        this.tCoordsObj = textureCoordsObj;
    }

    public void setNormalCoordsObj(ArrayList<Float> normalCoordsObj) {
        this.nCoordsObj = normalCoordsObj;
    }

    public void setColorsObj(ArrayList<Float> colorsObj) {
        this.colorsObj = colorsObj;
    }

    /*
    public void setFIndicesObj(ArrayList<Face> fIndObj) {
        this.fInds = fIndObj;
    }*/
    public String getObjFilename() {
        return objFilename;
    }

    public void setObjFilename(String objFilename) {
        this.objFilename = objFilename;
    }

    public void setTexOffsetsQuad(ArrayList<Integer> textureOffsets) {
        this.texOffsetsQuad = textureOffsets;
    }

    public void setTexOffsetsTri(ArrayList<Integer> textureOffsets) {
        this.texOffsetsTri = textureOffsets;
    }

    public void setTextureIDs(ArrayList<Integer> textureIDs) {
        this.textureIDs = textureIDs;
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;
    }

    public boolean isSizeOne() {
        return (width == 1) && (height == 1);
    }

    public void setFaceIndsQuads(ArrayList<Face> fInds) {
        this.fIndsQuad = fInds;
    }

    public void setFaceIndsTris(ArrayList<Face> fInds) {
        this.fIndsTri = fInds;
    }

    public float getGlobalTextureScale() {
        return globalTexScale;
    }

    public void setGlobalTextureScale(float globalTexScale) {
        this.globalTexScale = globalTexScale;
    }

    public float getXOffset() {
        return xOffset;
    }

    public void setXOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getYOffset() {
        return yOffset;
    }

    public void setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public void printTileData(PrintWriter writer) {

        writer.println("----------------------------------");
        writer.println(objFilename);

        writer.println("vCoordsQuad");
        Utils.printArrayInFile(writer, vCoordsQuad, 12);

        writer.println("tCoordsQuad");
        Utils.printArrayInFile(writer, tCoordsQuad, 8);

        writer.println("nCoordsQuad");
        Utils.printArrayInFile(writer, nCoordsQuad, 12);

        writer.println("vCoordsTri");
        Utils.printArrayInFile(writer, vCoordsTri, 9);

        writer.println("tCoordsTri");
        Utils.printArrayInFile(writer, tCoordsTri, 6);

        writer.println("nCoordsTri");
        Utils.printArrayInFile(writer, nCoordsTri, 9);

    }

}
