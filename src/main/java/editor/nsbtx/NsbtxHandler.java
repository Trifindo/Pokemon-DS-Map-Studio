
package editor.nsbtx;

import editor.handler.MapEditorHandler;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author Trifindo
 */
public class NsbtxHandler {

    private MapEditorHandler handler;
    private NsbtxEditorDialog dialog;
    private Nsbtx nsbtx = null;
    private String nsbtxPath = null;

    private int textureIndexSelected = 0;
    private int paletteIndexSelected = 0;
    private int colorIndexSelected = 0;

    public NsbtxHandler(MapEditorHandler handler, NsbtxEditorDialog dialog) {
        this.handler = handler;
        this.dialog = dialog;
    }

    public Nsbtx getNsbtx() {
        return nsbtx;
    }

    public void loadNsbtx(String path) throws IOException {
        this.nsbtx = NsbtxLoader.loadNsbtx(path);
        this.nsbtxPath = path;
    }

    public BufferedImage getSelectedImage() {
        if (nsbtx != null) {
            return nsbtx.getImage(textureIndexSelected, paletteIndexSelected);
        }
        return null;
    }

    public int getTextureIndexSelected() {
        return textureIndexSelected;
    }

    public int getPaletteIndexSelected() {
        return paletteIndexSelected;
    }

    public void setTextureIndexSelected(int index) {
        this.textureIndexSelected = index;
    }

    public void setPaletteIndexSelected(int index) {
        this.paletteIndexSelected = index;
    }

    public int getSelectedTextureWidth() {
        return nsbtx.textureInfos.get(textureIndexSelected).width;
    }

    public int getSelectedTextureHeight() {
        return nsbtx.textureInfos.get(textureIndexSelected).height;
    }

    public int getColorIndexSelected() {
        return colorIndexSelected;
    }

    public void setColorIndexSelected(int index) {
        this.colorIndexSelected = index;
    }

    public NsbtxEditorDialog getDialog() {
        return dialog;
    }

    public int getNumColorsInSelectedPalette() {
        return nsbtx.paletteInfos.get(paletteIndexSelected).getNumColors();
    }

    public void incrementColorIndexSelected(int increment) {
        this.colorIndexSelected += increment;
    }

    public void decrementColorIndexSelected() {
        this.colorIndexSelected--;
    }

    public String getNsbtxPath(){
        return nsbtxPath;
    }
    
    public void setNsbtxPath(String path){
        this.nsbtxPath = path;
    }
    
}
