
package tileset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
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

    private int[] diffuseRGB;
    private int[] ambientRGB;
    private int[] specularRGB;
    private int[] emissionRGB;

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

        diffuseRGB = new int[]{ 25, 25, 25 };
        ambientRGB = new int[]{ 31, 31, 31 };
        specularRGB = new int[]{ 0, 0, 0 };
        emissionRGB = new int[]{ 0, 0, 0 };

        renderBorder = false;
        vertexColorsEnabled = false;
        uniformNormalOrientation = true;
    }

    @Override
    public TilesetMaterial clone() {
        TilesetMaterial duplicate = new TilesetMaterial();
        duplicate.imageName = imageName;
        duplicate.materialName = materialName;
        duplicate.paletteNameImd = paletteNameImd;
        duplicate.textureNameImd = textureNameImd;
        duplicate.textureImg = textureImg;
        duplicate.enableFog = enableFog;
        duplicate.renderBothFaces = renderBothFaces;
        duplicate.uniformNormalOrientation = uniformNormalOrientation;
        duplicate.alwaysIncludeInImd = alwaysIncludeInImd;

        duplicate.light0 = light0;
        duplicate.light1 = light1;
        duplicate.light2 = light2;
        duplicate.light3 = light3;

        System.arraycopy(diffuseRGB,0, duplicate.diffuseRGB,0,3);
        System.arraycopy(ambientRGB, 0, duplicate.ambientRGB,0,3);
        System.arraycopy(specularRGB,0, duplicate.specularRGB,0,3);
        System.arraycopy(emissionRGB,0, duplicate.emissionRGB,0,3);

        duplicate.alpha = alpha;
        duplicate.texGenMode = texGenMode;
        duplicate.texTilingU = texTilingU;
        duplicate.texTilingV = texTilingV;
        duplicate.colorFormat = colorFormat;
        duplicate.renderBorder = renderBorder;
        duplicate.vertexColorsEnabled = vertexColorsEnabled;

        return duplicate;
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


    public boolean hasRenderBorder() {
        return renderBorder;
    }

    public void setRenderBorder(boolean renderBorderEnabled) {
        this.renderBorder = renderBorderEnabled;
    }

    public boolean getLight0() {
        return light0;
    }

    public void setLight0(boolean status) {
        this.light0 = status;
    }

    public int[] getDiffuse() {
        return this.diffuseRGB;
    }
    public void setDiffuse(int[] rgb) {
        this.diffuseRGB = rgb;
    }
    public int getDiffuseR() {
        return diffuseRGB[0];
    }
    public int getDiffuseG() {
        return diffuseRGB[1];
    }
    public int getDiffuseB() {
        return diffuseRGB[2];
    }
    public void setDiffuseR(int R) {
        this.diffuseRGB[0] = R;
    }
    public void setDiffuseG(int G) {
        this.diffuseRGB[1] = G;
    }
    public void setDiffuseB(int B) {
        this.diffuseRGB[2] = B;
    }

    public boolean getLight1() {
        return light1;
    }
    public void setLight1(boolean status) {
        this.light1 = status;
    }

    public int[] getAmbient() {
        return this.ambientRGB;
    }
    public void setAmbient(int[] rgb) {
        this.ambientRGB = rgb;
    }
    public int getAmbientR() {
        return ambientRGB[0];
    }
    public int getAmbientG() {
        return ambientRGB[1];
    }
    public int getAmbientB() {
        return ambientRGB[2];
    }
    public void setAmbientR(int R) {
        this.ambientRGB[0] = R;
    }
    public void setAmbientG(int G) {
        this.ambientRGB[1] = G;
    }
    public void setAmbientB(int B) {
        this.ambientRGB[2] = B;
    }

    public boolean getLight2() {
        return light2;
    }

    public void setLight2(boolean status) {
        this.light2 = status;
    }

    public int[] getSpecular() {
        return this.specularRGB;
    }
    public void setSpecular(int[] rgb) {
        this.specularRGB = rgb;
    }
    public int getSpecularR() {
        return specularRGB[0];
    }
    public int getSpecularG() {
        return specularRGB[1];
    }
    public int getSpecularB() {
        return specularRGB[2];
    }
    public void setSpecularR(int R) {
        this.specularRGB[0] = R;
    }
    public void setSpecularG(int G) {
        this.specularRGB[1] = G;
    }
    public void setSpecularB(int B) {
        this.specularRGB[2] = B;
    }

    public boolean getLight3() {
        return light3;
    }

    public void setLight3(boolean status) {
        this.light3 = status;
    }

    public int[] getEmission() {
        return this.emissionRGB;
    }
    public void setEmission(int[] rgb) {
        this.emissionRGB = rgb;
    }
    public int getEmissionR() {
        return emissionRGB[0];
    }
    public int getEmissionG() {
        return emissionRGB[1];
    }
    public int getEmissionB() {
        return emissionRGB[2];
    }
    public void setEmissionR(int R) {
        this.emissionRGB[0] = R;
    }
    public void setEmissionG(int G) {
        this.emissionRGB[1] = G;
    }
    public void setEmissionB(int B) {
        this.emissionRGB[2] = B;
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

    public boolean areVertexColorsEnabled() {
        return vertexColorsEnabled;
    }

    public void setVertexColorsEnabled(boolean enabled) {
        this.vertexColorsEnabled = enabled;
    }

}
