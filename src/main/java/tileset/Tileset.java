
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

import utils.Utils;

/**
 * @author Trifindo
 */
public class Tileset {

    //File extension
    public static final String fileExtension = "pdsts";

    //Tileset path
    public String tilesetFolderPath = "";

    //Tiles
    private List<Tile> tiles;

    //Textures
    private List<Texture> textures;
    private List<TilesetMaterial> materials;

    public static final BufferedImage defaultTexture = Utils.loadTexImageAsResource("/imgs/defaultTexture.png");

    //Smart grid
    private List<SmartGrid> sgridArray;

    public Tileset() {
        tiles = new ArrayList<>();
        textures = new ArrayList<>();
        materials = new ArrayList<>();

        sgridArray = new ArrayList<>();
        //sgridArray.add(new SmartGrid());
    }

    @Override
    public Tileset clone() {
        Tileset tileset = new Tileset();

        tileset.tiles = new ArrayList<>();
        tileset.tiles.addAll(tiles);

        tileset.textures = new ArrayList<>();
        tileset.textures.addAll(textures);

        tileset.materials = new ArrayList<>();
        for (TilesetMaterial material : materials) {
            tileset.materials.add(material.clone()); //TODO: need clone here?
        }

        tileset.sgridArray = new ArrayList<>();
        tileset.sgridArray.addAll(sgridArray);

        return tileset;
    }

    public void saveImagesToFile(String path) {
        for (TilesetMaterial material : materials) {
            String outPath = path + File.separator + material.getImageName();
            try {
                ImageIO.write(material.getTextureImg(), "png", new File(outPath));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<Integer> replaceMaterial(int oldIndex, int newIndex) {
        List<Integer> indicesTilesReplaced;
        indicesTilesReplaced = replaceTextureIDs(oldIndex, newIndex);
        removeUnusedTextures();
        return indicesTilesReplaced;
    }

    public List<Integer> replaceTextureIDs(int oldIndex, int newIndex) {
        return IntStream.range(0, tiles.size())
                .filter(i -> Collections.replaceAll(tiles.get(i).getTextureIDs(), oldIndex, newIndex))
                .boxed()
                .collect(Collectors.toList());
    }

    public void removeUnusedTextures() {
        List<Integer> textureUsage = countTextureUsage();

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

    public void removeTiles(List<Integer> indices) {
        for (int i = 0; i < indices.size(); i++) {

        }
    }

    private List<Integer> countTextureUsage() {
        List<Integer> count = new ArrayList<>();
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

    public boolean addTexture(String path) {
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
        } else {
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
        for (Texture texture : textures) {
            texture.destroy(gl);
        }
        loadTexturesGL();
    }

    public void swapTiles(int e1, int e2) {
        Collections.swap(tiles, e1, e2);
    }

    public void moveTiles(List<Integer> indices) {
        tiles = indices.stream()
                .map(index -> tiles.get(index))
                .collect(Collectors.toList());
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

    public void loadTextureImgs() {
        List<Integer> textureUsage = countTextureUsage();
        for (int i = 0; i < textureUsage.size(); i++) {
            if (textureUsage.get(i) == 0) {
                materials.remove(i);
                textureUsage.remove(i);
                shiftTextureIDsFrom(i);
                i--;
            }
        }
        for (TilesetMaterial material : materials) {
            String textureName = material.getImageName();
            material.setTextureImg(Tile.loadTextureImgWithDefault(tilesetFolderPath + "/" + textureName));
        }
    }

    public void loadTextureImgsAsResource() {
        List<Integer> textureUsage = countTextureUsage();
        for (int i = 0; i < textureUsage.size(); i++) {
            if (textureUsage.get(i) == 0) {
                materials.remove(i);
                textureUsage.remove(i);
                shiftTextureIDsFrom(i);
                i--;
            }
        }
        for (TilesetMaterial material : materials) {
            String textureName = material.getImageName();
            //System.out.println(tilesetFolderPath);
            BufferedImage img = Utils.loadTexImageAsResource(tilesetFolderPath + "/" + textureName);
            material.setTextureImg(img);
        }
    }

    public void loadTextureImgFromPath(int index, String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        materials.get(index).setTextureImg(img);
    }

    public List<Texture> getTextures() {
        return textures;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public void addTile(Tile tile) {
        tile.setTileset(this);
        this.tiles.add(tile);
    }

    public void importTile(Tile tile) {
        List<Integer> texIDs = tile.getTextureIDs();
        for (int i = 0; i < texIDs.size(); i++) {
            TilesetMaterial material = tile.getTileset().getMaterial(texIDs.get(i));
            int index = materials.indexOf(material);
            if (index == -1) {
                texIDs.set(i, materials.size());
                materials.add(material);
            } else {
                texIDs.set(i, index);
            }
        }
        tile.setTileset(this);
        tiles.add(tile);
    }

    public void importTiles(List<Tile> tiles) {
        for (Tile tile : tiles) {
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

    public List<Tile> getTiles() {
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

    public void duplicateTiles(List<Integer> indices) {
        int startIndex = indices.get(indices.size() - 1) + 1;
        for (int i = 0; i < indices.size(); i++) {
            tiles.add(startIndex + i, tiles.get(indices.get(i)).clone());
        }
    }

    public int getIndexOfTileByObjFilename(String filename) {
        return IntStream.range(0, tiles.size())
                .filter(i -> tiles.get(i).getObjFilename().equals(filename))
                .findFirst()
                .orElse(-1);
    }

    public int getIndexOfMaterialByImgName(String textureName) {
        return IntStream.range(0, materials.size())
                .filter(i -> materials.get(i).getImageName().equals(textureName))
                .findFirst()
                .orElse(-1);
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

    public List<SmartGrid> getSmartGridArray() {
        return sgridArray;
    }

    public void setSgridArray(List<SmartGrid> sgridArray) {
        this.sgridArray = sgridArray;
    }

    public List<TilesetMaterial> getMaterials() {
        return materials;
    }

    public TilesetMaterial getMaterial(int index) {
        return materials.get(index);
    }

    public int indexOfTileVisualData(Tile tile) {
        if (tile == null) {
            return IntStream.range(0, tiles.size())
                    .filter(i -> tiles.get(i) == null)
                    .findFirst().orElse(-1);
        } else {
            return IntStream.range(0, tiles.size())
                    .filter(i -> tile.equalsVisualData(tiles.get(i)))
                    .findFirst().orElse(-1);
        }
    }
}
