
package tileset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 *
 * @author Trifindo
 */
public class TilesetMaterial {

    //Textures
    private BufferedImage textureImg;
    private String imageName;
    private String materialName;
    private String paletteNameImd;
    private String textureNameImd;
    private boolean enableFog;
    private boolean renderBothFaces;
    private boolean uniformNormalOrientation;
    private boolean alwaysIncludeInImd;
    private boolean light0;
    private boolean light1;
    private boolean light2;
    private boolean light3;
    private int alpha;
    private int texGenMode;
    private int texTilingU;
    private int texTilingV;
    private int colorFormat;
    private boolean renderBorder;
    private boolean vertexColorsEnabled;

    public TilesetMaterial() {
        enableFog = true;
        alpha = 31;
        texGenMode = 0;
        texTilingU = 0;
        texTilingV = 0;
        colorFormat = 1;
        light0 = true;
        light1 = false;
        light2 = false;
        light3 = false;
        renderBorder = false;
        vertexColorsEnabled = false;
        uniformNormalOrientation = true;
    }

    @Override
    public TilesetMaterial clone() {
        TilesetMaterial material = new TilesetMaterial();
        material.imageName = imageName;
        material.materialName = materialName;
        material.paletteNameImd = paletteNameImd;
        material.textureNameImd = textureNameImd;
        material.textureImg = textureImg;
        material.enableFog = enableFog;
        material.renderBothFaces = renderBothFaces;
        material.uniformNormalOrientation = uniformNormalOrientation;
        material.alwaysIncludeInImd = alwaysIncludeInImd;
        material.light0 = light0;
        material.light1 = light1;
        material.light2 = light2;
        material.light3 = light3;
        material.alpha = alpha;
        material.texGenMode = texGenMode;
        material.texTilingU = texTilingU;
        material.texTilingV = texTilingV;
        material.colorFormat = colorFormat;
        material.renderBorder = renderBorder;
        material.vertexColorsEnabled = vertexColorsEnabled;

        return material;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.materialName);
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
        final TilesetMaterial other = (TilesetMaterial) obj;
        
        if (!Objects.equals(this.imageName, other.imageName)) {
            return false;
        }
        /*
        if (!Objects.equals(this.materialName, other.materialName)) {
            return false;
        }*/
        return true;
    }

    
    
    public boolean renderBorder(){
        return renderBorder;
    }
    
    public void setRenderBorder(boolean renderBorderEnabled){
        this.renderBorder = renderBorderEnabled;
    }
    
    public boolean light0() {
        return light0;
    }

    public void setLight0(boolean lightEnabled) {
        this.light0 = lightEnabled;
    }

    public boolean light1() {
        return light1;
    }

    public void setLight1(boolean lightEnabled) {
        this.light1 = lightEnabled;
    }

    public boolean light2() {
        return light2;
    }

    public void setLight2(boolean lightEnabled) {
        this.light2 = lightEnabled;
    }

    public boolean light3() {
        return light3;
    }

    public void setLight3(boolean lightEnabled) {
        this.light3 = lightEnabled;
    }

    public int getColorFormat() {
        return colorFormat;
    }

    public void setColorFormat(int colorFormat) {
        this.colorFormat = colorFormat;
    }

    public int getTexTilingU() {
        return texTilingU;
    }

    public void setTexTilingU(int texTiling) {
        this.texTilingU = texTiling;
    }

    public int getTexTilingV() {
        return texTilingV;
    }

    public void setTexTilingV(int texTiling) {
        this.texTilingV = texTiling;
    }

    public int getTexGenMode() {
        return texGenMode;
    }

    public void setTexGenMode(int texGenMode) {
        this.texGenMode = texGenMode;
    }

    public void loadTextureImgFromPath(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        this.textureImg = img;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String name) {
        this.imageName = name;
    }

    public String getPaletteNameImd() {
        return paletteNameImd;
    }

    public void setPaletteNameImd(String paletteNameImd) {
        this.paletteNameImd = paletteNameImd;
    }

    public String getTextureNameImd() {
        return textureNameImd;
    }

    public void setTextureNameImd(String textureNameImd) {
        this.textureNameImd = textureNameImd;
    }

    public BufferedImage getTextureImg() {
        return textureImg;
    }

    public void setTextureImg(BufferedImage textureImg) {
        this.textureImg = textureImg;
    }

    public boolean isFogEnabled() {
        return enableFog;
    }

    public void setFogEnabled(boolean enableFog) {
        this.enableFog = enableFog;
    }

    public boolean renderBothFaces() {
        return renderBothFaces;
    }

    public void setRenderBothFaces(boolean renderBothFaces) {
        this.renderBothFaces = renderBothFaces;
    }

    public boolean uniformNormalOrientation() {
        return uniformNormalOrientation;
    }

    public void setUniformNormalOrientation(boolean uniformNormalOrientation) {
        this.uniformNormalOrientation = uniformNormalOrientation;
    }

    public boolean alwaysIncludeInImd() {
        return alwaysIncludeInImd;
    }

    public void setAlwaysIncludeInImd(boolean include) {
        this.alwaysIncludeInImd = include;
    }
    
    public boolean vertexColorsEnabled(){
        return vertexColorsEnabled;
    }
    
    public void setVertexColorsEnabled(boolean enabled){
        this.vertexColorsEnabled = enabled;
    }

}
