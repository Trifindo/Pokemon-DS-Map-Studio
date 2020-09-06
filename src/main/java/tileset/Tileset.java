/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tileset;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import editor.smartdrawing.SmartGrid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class Tileset {

    //File extension
    public static final String fileExtension = "pdsts";

    //Tileset path
    public String tilesetFolderPath = "";

    //Tiles
    private ArrayList<Tile> tiles = new ArrayList();

    //Textures
    private ArrayList<Texture> textures = new ArrayList<>();
    private ArrayList<TilesetMaterial> materials = new ArrayList();

    public static final BufferedImage defaultTexture = Utils.loadTexImageAsResource("/imgs/defaultTexture.png");

    //Smart grid
    private ArrayList<SmartGrid> sgridArray = new ArrayList<>();

    public Tileset() {
        tiles = new ArrayList();
        textures = new ArrayList<>();
        materials = new ArrayList<>();

        sgridArray = new ArrayList<>();
        //sgridArray.add(new SmartGrid());
    }

    @Override
    public Tileset clone() {
        Tileset tileset = new Tileset();

        tileset.tiles = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++) {
            tileset.tiles.add(tiles.get(i));
        }

        tileset.textures = new ArrayList<>();
        for (int i = 0; i < textures.size(); i++) {
            tileset.textures.add(textures.get(i));
        }

        tileset.materials = new ArrayList<>();
        for (TilesetMaterial material : materials) {
            tileset.materials.add(material.clone()); //TODO: need clone here?
        }

        tileset.sgridArray = new ArrayList<>();
        for (int i = 0; i < sgridArray.size(); i++) {
            tileset.sgridArray.add(sgridArray.get(i));
        }

        return tileset;
    }

    public void saveImagesToFile(String path) {
        for (int i = 0; i < materials.size(); i++) {
            TilesetMaterial material = materials.get(i);
            String outPath = path + File.separator + material.getImageName();
            File outputfile = new File(outPath);
            try {
                ImageIO.write(material.getTextureImg(), "png", outputfile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<Integer> replaceMaterial(int oldIndex, int newIndex) {
        ArrayList<Integer> indicesTilesReplaced;
        indicesTilesReplaced = replaceTextureIDs(oldIndex, newIndex);
        removeUnusedTextures();
        return indicesTilesReplaced;
    }

    public ArrayList<Integer> replaceTextureIDs(int oldIndex, int newIndex) {
        ArrayList<Integer> indicesTilesReplaced = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++) {
            if (Collections.replaceAll(tiles.get(i).getTextureIDs(), oldIndex, newIndex)) {
                indicesTilesReplaced.add(i);
            }
        }
        return indicesTilesReplaced;
    }

    public void removeUnusedTextures() {
        ArrayList<Integer> textureUsage = countTextureUsage();

        // TODO: could this be improved by using pointers instead of IDs?
        for (int i = 0; i < textureUsage.size(); i++) {
            if (textureUsage.get(i) == 0) {
                materials.remove(i);
                textureUsage.remove(i);
                shiftTextureIDsFrom(i);
                i--;
            }
        }
    }

    public void swapMaterials(int indexMat1, int indexMat2) {
        Collections.swap(textures, indexMat1, indexMat2);
        Collections.swap(materials, indexMat1, indexMat2);
        for (Tile tile : tiles) {
            tile.swapMaterials(indexMat1, indexMat2);
        }

    }

    public void removeTile(int index) {
        tiles.remove(index);

        removeUnusedTextures();
    }
    
    public void removeTiles(ArrayList<Integer> indices){
        for(int i = 0; i < indices.size(); i++){
            
        }
    }

    private ArrayList<Integer> countTextureUsage() {
        ArrayList<Integer> count = new ArrayList<>();
        /*
        for (Texture texture : textures) {
            count.add(0);
        }*/
        for (int i = 0; i < materials.size(); i++) {
            count.add(0);
        }

        for (Tile tile : tiles) {
            for (Integer i : tile.getTextureIDs()) {
                count.set(i, count.get(i) + 1);
            }
        }
        return count;
    }

    public boolean addTexture(String path) throws IOException {
        String filename = new File(path).getName();

        int textIndex = getIndexOfMaterialByImgName(filename);
        if (textIndex == -1) {
            TilesetMaterial material = new TilesetMaterial();
            material.setTextureImg(Tile.loadTextureImgWithDefault(path));
            material.setImageName(filename);
            String textureNameImd = Utils.removeExtensionFromPath(filename);
            material.setMaterialName(textureNameImd);
            material.setTextureNameImd(textureNameImd);
            material.setPaletteNameImd(textureNameImd + "_pl");
            materials.add(material);
            return true;
        } else {
            return false;
        }
    }

    public boolean replaceTexture(int index, String path) throws IOException {
        String filename = new File(path).getName();

        int textIndex = getIndexOfMaterialByImgName(filename);
        if (textIndex == -1 || textIndex == index) {
            TilesetMaterial material = materials.get(index);
            BufferedImage img = Tile.loadTextureImg(path);
            material.setTextureImg(img);
            material.setImageName(filename);
            /*
            String textureNameImd = Utils.removeExtensionFromPath(filename);
            material.setMaterialName(textureNameImd);
            material.setTextureNameImd(textureNameImd);
            material.setPaletteNameImd(textureNameImd + "_pl");*/
            return true;
        }else{
            return false;
        }
    }

    private void shiftTextureIDsFrom(int index) {
        for (Tile tile : tiles) {
            for (int i = 0; i < tile.getTextureIDs().size(); i++) {
                int id = tile.getTextureIDs().get(i);
                if (id > index) {
                    tile.getTextureIDs().set(i, id - 1);
                }
            }
        }
    }

    public void updateTextures(GL2 gl) {
        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).destroy(gl);
        }
        loadTexturesGL();
    }

    public void swapTiles(int e1, int e2) {
        Collections.swap(tiles, e1, e2);
    }

    public void moveTiles(ArrayList<Integer> indices) {
        ArrayList<Tile> newTiles = new ArrayList<>();
        for (int i = 0; i < indices.size(); i++) {
            newTiles.add(tiles.get(indices.get(i)));
        }
        tiles = newTiles;
    }

    public void loadTexturesGL() {
        textures = new ArrayList<>();
        for (int i = 0; i < materials.size(); i++) {
            textures.add(loadTextureGL(i));
        }
    }

    private Texture loadTextureGL(int index) {
        Texture tex = null;
        try {
            BufferedImage img = Utils.cloneImg(materials.get(index).getTextureImg());
            ImageUtil.flipImageVertically(img);
            tex = AWTTextureIO.newTexture(GLProfile.getDefault(), img, false);
        } catch (Exception e) {
            tex = AWTTextureIO.newTexture(GLProfile.getDefault(), Tileset.defaultTexture, false);
            //e.printStackTrace();
        }
        return tex;
    }

    public void loadTextureImgs() throws IOException {
        ArrayList<Integer> textureUsage = countTextureUsage();
        for (int i = 0; i < textureUsage.size(); i++) {
            if (textureUsage.get(i) == 0) {
                materials.remove(i);
                textureUsage.remove(i);
                shiftTextureIDsFrom(i);
                i--;
            }
        }
        for (int i = 0; i < materials.size(); i++) {
            String textureName = materials.get(i).getImageName();
            materials.get(i).setTextureImg(Tile.loadTextureImgWithDefault(tilesetFolderPath + "/" + textureName));
        }
    }

    public void loadTextureImgsAsResource() throws IOException {
        ArrayList<Integer> textureUsage = countTextureUsage();
        for (int i = 0; i < textureUsage.size(); i++) {
            if (textureUsage.get(i) == 0) {
                materials.remove(i);
                textureUsage.remove(i);
                shiftTextureIDsFrom(i);
                i--;
            }
        }
        for (int i = 0; i < materials.size(); i++) {
            String textureName = materials.get(i).getImageName();
            //System.out.println(tilesetFolderPath);
            BufferedImage img = Utils.loadTexImageAsResource(tilesetFolderPath + "/" + textureName);
            materials.get(i).setTextureImg(img);
        }
    }

    public void loadTextureImgFromPath(int index, String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        materials.get(index).setTextureImg(img);
    }

    public ArrayList<Texture> getTextures() {
        return textures;
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }

    public void addTile(Tile tile) {
        tile.setTileset(this);
        this.tiles.add(tile);
    }
    
    public void importTile(Tile tile){
        ArrayList<Integer> texIDs = tile.getTextureIDs();
        for(int i = 0; i < texIDs.size(); i++){
            TilesetMaterial material = tile.getTileset().getMaterial(texIDs.get(i));
            int index = materials.indexOf(material);
            if(index == -1){
                texIDs.set(i, materials.size());
                materials.add(material);
            }else{
                texIDs.set(i, index);
            }
        }
        tile.setTileset(this);
        tiles.add(tile);
    }
    
    public void importTiles(ArrayList<Tile> tiles){
        for(Tile tile : tiles){
            importTile(tile);
        }
        
        System.out.println("Tiles imported");
    }
    

    public Tile get(int index) {
        return tiles.get(index);
    }

    public int size() {
        return tiles.size();
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public Texture getTexture(int index) {
        return textures.get(index);
    }

    public BufferedImage getTextureImg(int index) {
        return materials.get(index).getTextureImg();
    }

    public int getIndexOfTile(Tile tile) {
        return tiles.indexOf(tile);
    }

    public void duplicateTile(int index) {
        tiles.add(index, tiles.get(index).clone());
    }
    
    public void duplicateTiles(ArrayList<Integer> indices){
        int startIndex = indices.get(indices.size() - 1) + 1;
        for(int i = 0; i < indices.size(); i++){
            tiles.add(startIndex + i, tiles.get(indices.get(i)).clone());
        }
    }

    public int getIndexOfTileByObjFilename(String filename) {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).getObjFilename().equals(filename)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOfMaterialByImgName(String textureName) {
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).getImageName().equals(textureName)) {
                return i;
            }
        }
        return -1;
    }

    public String getMaterialName(int index) {
        return materials.get(index).getMaterialName();
    }

    public String getImageName(int index) {
        return materials.get(index).getImageName();
    }

    public String getPaletteNameImd(int index) {
        return materials.get(index).getPaletteNameImd();
    }

    public void setMaterialName(int index, String name) {
        materials.get(index).setMaterialName(name);
    }

    public void setPaletteNameImd(int index, String name) {
        materials.get(index).setPaletteNameImd(name);
    }

    public String getTextureNameImd(int index) {
        return materials.get(index).getTextureNameImd();
    }

    public void setTextureNameImd(int index, String name) {
        materials.get(index).setTextureNameImd(name);
    }

    public ArrayList<SmartGrid> getSmartGridArray() {
        return sgridArray;
    }

    public void setSgridArray(ArrayList<SmartGrid> sgridArray) {
        this.sgridArray = sgridArray;
    }
    
    

    public ArrayList<TilesetMaterial> getMaterials() {
        return materials;
    }

    public TilesetMaterial getMaterial(int index) {
        return materials.get(index);
    }

    public int indexOfTileVisualData(Tile tile){
        if (tile == null) {
            for (int i = 0; i < tiles.size(); i++)
                if (tiles.get(i)==null)
                    return i;
        } else {
            for (int i = 0; i < tiles.size(); i++)
                if (tile.equalsVisualData(tiles.get(i)))
                    return i;
        }
        return -1;
    }
    
}
