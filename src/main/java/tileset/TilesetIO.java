
package tileset;

import editor.MainFrame;
import editor.smartdrawing.SmartGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

import utils.Utils;

/**
 * @author Trifindo
 */
public class TilesetIO {

    private static final byte TAG_TILE = 0;
    //private static final int TAG_NTEXTURES = 2;
    private static final byte TAG_MATERIAL_START = 9;
    private static final byte TAG_MATERIAL_END = 10;
    private static final byte TAG_IMG_NAME = 1;
    private static final byte TAG_MAT_NAME = 4;
    private static final byte TAG_PNAME_IMD = 2;
    private static final byte TAG_TNAME_IMD = 3;
    private static final byte TAG_FOG = 30;
    private static final byte TAG_BOTHFACE = 31;
    private static final byte TAG_NORMALORIENT = 32;
    private static final byte TAG_ALPHA = 33;
    private static final byte TAG_TEXGENMODE = 34;
    private static final byte TAG_TEX_TILING_U = 40;
    private static final byte TAG_TEX_TILING_V = 41;
    private static final byte TAG_COLOR_FORMAT = 42;
    private static final byte TAG_LIGHT0 = 43;
    private static final byte TAG_LIGHT1 = 44;
    private static final byte TAG_LIGHT2 = 45;
    private static final byte TAG_LIGHT3 = 46;
    private static final byte TAG_RENDER_BORDER = 47;
    private static final byte TAG_VERTEX_COLORS = 48;
    private static final byte TAG_INCLUDE_IN_IMD = 35;
    private static final byte TAG_WIDTH = 11;
    private static final byte TAG_HEIGHT = 12;
    private static final byte TAG_XTILEABLE = 13;
    private static final byte TAG_YTILEABLE = 14;
    private static final byte TAG_UTILEABLE = 36;
    private static final byte TAG_VTILEABLE = 37;
    private static final byte TAG_GLOBALMAPPING = 27;
    private static final byte TAG_GLOBALTEXSCALE = 28;
    private static final byte TAG_XOFFSET = 38;
    private static final byte TAG_YOFFSET = 39;
    private static final byte TAG_VCOORDS = 15;
    private static final byte TAG_TCOORDS = 16;
    private static final byte TAG_NCOORDS = 22;
    private static final byte TAG_COLORS = 49;
    private static final byte TAG_TIDS = 19;
    private static final byte TAG_TOFFSETSQUAD = 24;
    private static final byte TAG_TOFFSETSTRI = 25;
    //private static final byte TAG_TOFFSETS = 20;
    private static final byte TAG_OBJNAME = 21;

    private static final byte TAG_FINDSQUADS = 17;
    private static final byte TAG_FINDSTRIS = 18;
    private static final byte TAG_FINDSQUADS_EXTENDED = 50;
    private static final byte TAG_FINDSTRIS_EXTENDED = 51;

    private static final byte TAG_SMARTGRID = 26;

    public static void saveTilesetToFile(String path, Tileset tset)
            throws FileNotFoundException, IOException {

        FileOutputStream out = new FileOutputStream(path);

        for (int i = 0; i < tset.getMaterials().size(); i++) {
            TilesetMaterial m = tset.getMaterials().get(i);
            writeIntElement(out, TAG_MATERIAL_START, i);
            writeStringElement(out, TAG_IMG_NAME, m.getImageName());
            writeStringElement(out, TAG_MAT_NAME, m.getMaterialName());
            writeStringElement(out, TAG_PNAME_IMD, m.getPaletteNameImd());
            writeStringElement(out, TAG_TNAME_IMD, m.getTextureNameImd());
            writeBoolElement(out, TAG_FOG, m.isFogEnabled());
            writeBoolElement(out, TAG_BOTHFACE, m.renderBothFaces());
            writeBoolElement(out, TAG_NORMALORIENT, m.uniformNormalOrientation());
            writeBoolElement(out, TAG_INCLUDE_IN_IMD, m.alwaysIncludeInImd());
            writeBoolElement(out, TAG_LIGHT0, m.light0());
            writeBoolElement(out, TAG_LIGHT1, m.light1());
            writeBoolElement(out, TAG_LIGHT2, m.light2());
            writeBoolElement(out, TAG_LIGHT3, m.light3());
            writeBoolElement(out, TAG_RENDER_BORDER, m.renderBorder());
            writeBoolElement(out, TAG_VERTEX_COLORS, m.vertexColorsEnabled());
            writeIntElement(out, TAG_ALPHA, m.getAlpha());
            writeIntElement(out, TAG_TEXGENMODE, m.getTexGenMode());
            writeIntElement(out, TAG_TEX_TILING_U, m.getTexTilingU());
            writeIntElement(out, TAG_TEX_TILING_V, m.getTexTilingV());
            writeIntElement(out, TAG_COLOR_FORMAT, m.getColorFormat());
            writeIntElement(out, TAG_MATERIAL_END, i);
        }

        for (SmartGrid sg : tset.getSmartGridArray()) {
            writeIntElement(out, TAG_SMARTGRID, sg.sgrid);
        }

        for (int i = 0; i < tset.getTiles().size(); i++) {
            Tile tile = tset.getTiles().get(i);
            writeIntElement(out, TAG_TILE, i);
            writeIntElement(out, TAG_WIDTH, tile.getWidth());
            writeIntElement(out, TAG_HEIGHT, tile.getHeight());
            writeBoolElement(out, TAG_XTILEABLE, tile.isXtileable());
            writeBoolElement(out, TAG_YTILEABLE, tile.isYtileable());
            writeBoolElement(out, TAG_UTILEABLE, tile.isUtileable());
            writeBoolElement(out, TAG_VTILEABLE, tile.isVtileable());
            writeBoolElement(out, TAG_GLOBALMAPPING, tile.useGlobalTextureMapping());
            writeFloatElement(out, TAG_GLOBALTEXSCALE, tile.getGlobalTextureScale());
            writeFloatElement(out, TAG_XOFFSET, tile.getXOffset());
            writeFloatElement(out, TAG_YOFFSET, tile.getYOffset());
            writeFloatElement(out, TAG_VCOORDS, tile.getVertexCoordsObj());
            writeFloatElement(out, TAG_TCOORDS, tile.getTextureCoordsObj());
            writeFloatElement(out, TAG_NCOORDS, tile.getNormalCoordsObj());
            writeFloatElement(out, TAG_COLORS, tile.getColorsObj());
            writeFaceExtendedElement(out, TAG_FINDSQUADS_EXTENDED, tile.getFIndQuadObj());
            writeFaceExtendedElement(out, TAG_FINDSTRIS_EXTENDED, tile.getFIndTriObj());
            writeIntElement(out, TAG_TIDS, tile.getTextureIDs());
            writeIntElement(out, TAG_TOFFSETSQUAD, tile.getTexOffsetsQuad());
            writeIntElement(out, TAG_TOFFSETSTRI, tile.getTexOffsetsTri());
            writeStringElement(out, TAG_OBJNAME, tile.getObjFilename());
        }

        out.close();
    }

    public static Tileset readTilesetFromFileAsResourceURI(String path)
            throws FileNotFoundException, IOException, NullPointerException,
            TextureNotFoundException {
        try {
            File file = new File(MainFrame.class.getResource(path).toURI());
            return readTilesetFromFile(MainFrame.class.getResourceAsStream(file.getPath()), path, true);
        } catch (URISyntaxException ex) {
            throw new FileNotFoundException();
        }
    }

    public static Tileset readTilesetFromFileAsResource(String path)
            throws FileNotFoundException, IOException, NullPointerException,
            TextureNotFoundException {
        return readTilesetFromFile(TilesetIO.class.getResourceAsStream(path), path, true);
    }

    public static Tileset readTilesetFromFile(String path)
            throws FileNotFoundException, IOException, NullPointerException,
            TextureNotFoundException {
        return readTilesetFromFile(new FileInputStream(path), path, false);
    }

    private static Tileset readTilesetFromFile(InputStream in, String path, boolean asResource)
            throws FileNotFoundException, IOException, NullPointerException, TextureNotFoundException {
        Tileset tset = new Tileset();
        tset.tilesetFolderPath = new File(path).getParent();
        tset.tilesetFolderPath = tset.tilesetFolderPath.replace('\\', '/');

        int tag;
        TilesetMaterial material = null;
        Tile tile = null;
        while ((tag = in.read()) != -1) {
            switch ((byte) (tag & 0xFF)) {
                case TAG_MATERIAL_START:
                    readIntElement(in); //Discard
                    material = new TilesetMaterial();
                    break;
                case TAG_IMG_NAME:
                    material.setImageName(readStringElement(in));
                    break;
                case TAG_MAT_NAME:
                    material.setMaterialName(readStringElement(in));
                    break;
                case TAG_PNAME_IMD:
                    material.setPaletteNameImd(readStringElement(in));
                    break;
                case TAG_TNAME_IMD:
                    material.setTextureNameImd(readStringElement(in));
                    break;
                case TAG_FOG:
                    material.setFogEnabled(readBoolElement(in));
                    break;
                case TAG_BOTHFACE:
                    material.setRenderBothFaces(readBoolElement(in));
                    break;
                case TAG_NORMALORIENT:
                    material.setUniformNormalOrientation(readBoolElement(in));
                    break;
                case TAG_INCLUDE_IN_IMD:
                    material.setAlwaysIncludeInImd(readBoolElement(in));
                    break;
                case TAG_LIGHT0:
                    material.setLight0(readBoolElement(in));
                    break;
                case TAG_LIGHT1:
                    material.setLight1(readBoolElement(in));
                    break;
                case TAG_LIGHT2:
                    material.setLight2(readBoolElement(in));
                    break;
                case TAG_LIGHT3:
                    material.setLight3(readBoolElement(in));
                    break;
                case TAG_RENDER_BORDER:
                    material.setRenderBorder(readBoolElement(in));
                    break;
                case TAG_VERTEX_COLORS:
                    material.setVertexColorsEnabled(readBoolElement(in));
                    break;
                case TAG_ALPHA:
                    material.setAlpha(readIntElement(in));
                    break;
                case TAG_TEXGENMODE:
                    material.setTexGenMode(readIntElement(in));
                    break;
                case TAG_TEX_TILING_U:
                    material.setTexTilingU(readIntElement(in));
                    break;
                case TAG_TEX_TILING_V:
                    material.setTexTilingV(readIntElement(in));
                    break;
                case TAG_COLOR_FORMAT:
                    material.setColorFormat(readIntElement(in));
                    break;
                case TAG_MATERIAL_END:
                    readIntElement(in); //Discard
                    tset.getMaterials().add(material);
                    break;
                case TAG_SMARTGRID:
                    tset.getSmartGridArray().add(new SmartGrid(readIntMatrixElement(in)));
                    break;
                case TAG_TILE:
                    readIntElement(in); //Discard
                    if (tile != null) {
                        // Check if all textures can be read
                        findAndFixMissedTextures(tset, tile, asResource);

                        if (tile.getColorsObj().size() == 0) {
                            ArrayList<Float> colors = new ArrayList<>();
                            colors.add(1.0f);
                            colors.add(1.0f);
                            colors.add(1.0f);
                            tile.setColorsObj(colors);
                        }

                        tile.objDataToGlData();
                        tset.addTile(tile);
                        /*
                        int id = idOfMissedTexture(tset, tile);
                        if (id == -1) {
                            tile.objDataToGlData();
                            tset.addTile(tile);
                        } else {
                            System.out.println("Missed texture at ID: " + id);
                        }*/
 /*else {
                            throw new TextureNotFoundException(
                                    "Can't load texture named "
                                    + tset.getTextureName(id));
                        }*/
                    }
                    tile = new Tile();
                    break;
                case TAG_WIDTH:
                    tile.setWidth(readIntElement(in));
                    break;
                case TAG_HEIGHT:
                    tile.setHeight(readIntElement(in));
                    break;
                case TAG_XTILEABLE:
                    tile.setXtileable(readBoolElement(in));
                    break;
                case TAG_YTILEABLE:
                    tile.setYtileable(readBoolElement(in));
                    break;
                case TAG_UTILEABLE:
                    tile.setUtileable(readBoolElement(in));
                    break;
                case TAG_VTILEABLE:
                    tile.setVtileable(readBoolElement(in));
                    break;
                case TAG_GLOBALMAPPING:
                    tile.setGlobalTextureMapping(readBoolElement(in));
                    break;
                case TAG_GLOBALTEXSCALE:
                    tile.setGlobalTextureScale(readFloatElement(in));
                    break;
                case TAG_XOFFSET:
                    tile.setXOffset(readFloatElement(in));
                    break;
                case TAG_YOFFSET:
                    tile.setYOffset(readFloatElement(in));
                    break;
                case TAG_VCOORDS:
                    tile.setVertexCoordsObj(readFloatArray(in));
                    break;
                case TAG_TCOORDS:
                    tile.setTextureCoordsObj(readFloatArray(in));
                    break;
                case TAG_NCOORDS:
                    tile.setNormalCoordsObj(readFloatArray(in));
                    break;
                case TAG_COLORS:
                    tile.setColorsObj(readFloatArray(in));
                    break;
                case TAG_FINDSQUADS:
                    tile.setFaceIndsQuads(readFaceArray(in, true));
                    break;
                case TAG_FINDSTRIS:
                    tile.setFaceIndsTris(readFaceArray(in, false));
                    break;
                case TAG_FINDSQUADS_EXTENDED:
                    tile.setFaceIndsQuads(readFaceExtendedArray(in, true));
                    break;
                case TAG_FINDSTRIS_EXTENDED:
                    tile.setFaceIndsTris(readFaceExtendedArray(in, false));
                    break;
                case TAG_TIDS:
                    tile.setTextureIDs(readIntArrayElement(in));
                    break;
                case TAG_TOFFSETSQUAD:
                    tile.setTexOffsetsQuad(readIntArrayElement(in));
                    break;
                case TAG_TOFFSETSTRI:
                    tile.setTexOffsetsTri(readIntArrayElement(in));
                    break;
                case TAG_OBJNAME:
                    tile.setObjFilename(readStringElement(in));
                    break;
            }
        }

        findAndFixMissedTextures(tset, tile, asResource);

        if (tile != null) {
            if (tile.getColorsObj().size() == 0) {
                ArrayList<Float> colors = new ArrayList<>();
                colors.add(1.0f);
                colors.add(1.0f);
                colors.add(1.0f);
                tile.setColorsObj(colors);
            }

            tile.objDataToGlData();
            tset.addTile(tile);
        }
        /*
        int id = idOfMissedTexture(tset, tile);
        if (id == -1) {
            tile.objDataToGlData();
            tset.addTile(tile);
        }*/

        if (tset.getSmartGridArray().size() < 1) {
            tset.getSmartGridArray().add(new SmartGrid());
        }

        in.close();

        if (asResource) {
            tset.loadTextureImgsAsResource();
        } else {
            tset.loadTextureImgs();
        }

        System.out.println("Numero de tiles en el tileset: " + tset.getTiles().size());

        return tset;
    }

    private static void findAndFixMissedTextures(Tileset tset, Tile tile, boolean asResource)
            throws IOException {
        if (!asResource) {
            if (tile != null) {
                for (int i = 0; i < tile.getTextureIDs().size(); i++) {
                    String textName = tset.getImageName(tile.getTextureIDs().get(i));
                    String path = tset.tilesetFolderPath + "/" + textName;
                    File file = new File(path);
                    if (!file.exists()) {
                        try {
                            textName = file.getName();
                            path = tset.tilesetFolderPath + "/" + textName;
                            file = new File(path);
                            ImageIO.write(Tileset.defaultTexture, "png", file);
                            tset.getMaterial(tile.getTextureIDs().get(i)).setImageName(textName);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static int idOfMissedTexture(Tileset tset, Tile tile) {
        for (int i = 0; i < tile.getTextureIDs().size(); i++) {
            String textName = tset.getImageName(tile.getTextureIDs().get(i));
            if (!new File(tset.tilesetFolderPath + "/" + textName).exists()) {
                return tile.getTextureIDs().get(i);
            }
        }
        return -1;
    }

    private static ArrayList<Face> readFaceArray(InputStream in, boolean isQuadFace)
            throws IOException {
        int size = readInt(in);
        ArrayList<Face> array = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            array.add(readFace(in, isQuadFace));
        }
        return array;
    }

    private static ArrayList<Face> readFaceExtendedArray(InputStream in, boolean isQuadFace)
            throws IOException {
        int size = readInt(in);
        ArrayList<Face> array = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            array.add(readFaceExtended(in, isQuadFace));
        }
        return array;
    }

    private static ArrayList<Float> readFloatArray(InputStream in)
            throws IOException {
        int size = readInt(in);
        ArrayList<Float> array = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            array.add(readFloat(in));
        }
        return array;

    }

    private static float readFloatElement(InputStream in)
            throws IOException {
        int size = readInt(in); //Discard Size 1
        return readFloat(in);
    }

    private static Boolean readBoolElement(InputStream in)
            throws IOException {
        int size = readInt(in); //Discard size 1
        return readInt(in) == 1;
    }

    private static String readStringElement(InputStream in)
            throws IOException {
        int size = readInt(in);
        byte[] data = new byte[size];
        in.read(data);
        return new String(data);
    }

    private static int[][] readIntMatrixElement(InputStream in) throws IOException {
        int size = readInt(in);
        int[][] matrix = new int[size][];
        for (int i = 0; i < size; i++) {
            int colSize = readInt(in);
            matrix[i] = new int[colSize];
            for (int j = 0; j < colSize; j++) {
                matrix[i][j] = readInt(in);
            }
        }
        return matrix;
    }

    private static ArrayList<Integer> readIntArrayElement(InputStream in) throws IOException {
        int size = readInt(in);
        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            array.add(readInt(in));
        }
        return array;
    }

    private static int readIntElement(InputStream in) throws IOException {
        int size = readInt(in); //Discard Size 1
        return readInt(in);
    }

    private static int readInt(InputStream in) throws IOException {
        byte[] data = new byte[Integer.BYTES];
        in.read(data);
        return ByteBuffer.wrap(data).getInt();
    }

    private static float readFloat(InputStream in) throws IOException {
        byte[] data = new byte[Float.BYTES];
        in.read(data);
        return ByteBuffer.wrap(data).getFloat();
    }

    private static Face readFace(InputStream in, boolean isQuadFace) throws IOException {
        Face f = new Face(isQuadFace);
        for (int i = 0; i < f.vInd.length; i++) {
            f.vInd[i] = readInt(in);
        }
        for (int i = 0; i < f.tInd.length; i++) {
            f.tInd[i] = readInt(in);
        }
        for (int i = 0; i < f.nInd.length; i++) {
            f.nInd[i] = readInt(in);
        }
        for (int i = 0; i < f.cInd.length; i++) {
            f.cInd[i] = 1;
        }
        return f;
    }

    private static Face readFaceExtended(InputStream in, boolean isQuadFace) throws IOException {
        Face f = new Face(isQuadFace);
        for (int i = 0; i < f.vInd.length; i++) {
            f.vInd[i] = readInt(in);
        }
        for (int i = 0; i < f.tInd.length; i++) {
            f.tInd[i] = readInt(in);
        }
        for (int i = 0; i < f.nInd.length; i++) {
            f.nInd[i] = readInt(in);
        }
        for (int i = 0; i < f.cInd.length; i++) {
            f.cInd[i] = readInt(in);
        }
        return f;
    }

    private static void writeStringElement(FileOutputStream out, byte tag, String data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, data.length());
        writeString(out, data);
    }

    private static void writeIntElement(FileOutputStream out, byte tag, ArrayList<Integer> data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, data.size());
        writeIntArray(out, Utils.intListToArray(data));
    }

    private static void writeIntElement(FileOutputStream out, byte tag, int[][] data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, data.length);
        for (int i = 0; i < data.length; i++) {
            writeIntValue(out, data[i].length);
            writeIntArray(out, data[i]);
        }
    }

    private static void writeFaceElement(FileOutputStream out, byte tag, ArrayList<Face> data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, data.size());
        writeFaceArray(out, data);
    }

    private static void writeFaceExtendedElement(FileOutputStream out, byte tag, ArrayList<Face> data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, data.size());
        writeFaceExtendedArray(out, data);
    }

    private static void writeFloatElement(FileOutputStream out, byte tag, ArrayList<Float> data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, data.size());
        writeFloatArray(out, Utils.floatListToArray(data));
    }

    private static void writeIntElement(FileOutputStream out, byte tag, int data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, 1);
        writeIntValue(out, data);
    }

    private static void writeFloatElement(FileOutputStream out, byte tag, float data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, 1);
        writeFloatValue(out, data);
    }

    private static void writeBoolElement(FileOutputStream out, byte tag, boolean data)
            throws IOException {
        writeTag(out, tag);
        writeIntValue(out, 1);
        writeIntValue(out, data ? 1 : 0);
    }

    private static void writeString(FileOutputStream out, String s)
            throws IOException {
        out.write(s.getBytes());
    }

    private static void writeIntArray(FileOutputStream out, int[] data)
            throws IOException {
        for (int i : data) {
            writeIntValue(out, i);
        }
    }

    private static void writeFloatArray(FileOutputStream out, float[] data)
            throws IOException {
        for (float f : data) {
            writeFloatValue(out, f);
        }
    }

    private static void writeFaceArray(FileOutputStream out, ArrayList<Face> data)
            throws IOException {
        for (Face f : data) {
            writeIntArray(out, f.vInd);
            writeIntArray(out, f.tInd);
            writeIntArray(out, f.nInd);
        }
    }

    private static void writeFaceExtendedArray(FileOutputStream out, ArrayList<Face> data)
            throws IOException {
        for (Face f : data) {
            writeIntArray(out, f.vInd);
            writeIntArray(out, f.tInd);
            writeIntArray(out, f.nInd);
            writeIntArray(out, f.cInd);
        }
    }

    private static void writeIntValue(FileOutputStream out, int value)
            throws IOException {
        out.write(ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
    }

    private static void writeFloatValue(FileOutputStream out, float value)
            throws IOException {
        out.write(ByteBuffer.allocate(Float.BYTES).putFloat(value).array());
    }

    private static void writeTag(FileOutputStream out, byte tag)
            throws IOException {
        out.write(tag);
    }

}
