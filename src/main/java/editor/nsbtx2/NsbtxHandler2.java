
package editor.nsbtx2;

import editor.handler.MapEditorHandler;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Trifindo
 */
public class NsbtxHandler2 {

    private MapEditorHandler handler;
    private NsbtxEditorDialog2 dialog;

    private Nsbtx2 nsbtx = null;


    public NsbtxHandler2(MapEditorHandler handler, NsbtxEditorDialog2 dialog) {
        this.handler = handler;
        this.dialog = dialog;

        this.nsbtx = new Nsbtx2();
    }

    public void newNsbtx() {
        this.nsbtx = new Nsbtx2();
    }

    public BufferedImage getSelectedImage() {
        if (nsbtx != null) {
            int textureIndex = dialog.getTextureIndexSelected();
            int paletteIndex = dialog.getPaletteIndexSelected();
            if (textureIndex != -1 && paletteIndex != -1) {
                return nsbtx.getImage(dialog.getTextureIndexSelected(),
                        dialog.getPaletteIndexSelected());
            }
        }
        return null;
    }

    public NsbtxPalette getSelectedPalette() {
        int index = dialog.getPaletteIndexSelected();
        if (index != -1) {
            return getNsbtx().getPalette(index);
        }
        return null;
    }

    public NsbtxTexture getSelectedTexture() {
        int index = dialog.getTextureIndexSelected();
        if (index != -1) {
            return getNsbtx().getTexture(index);
        }
        return null;
    }


    public int indexOfTextureName(String name) {
        return nsbtx.indexOfTextureName(name);
    }

    public int indexOfPaletteName(String name) {
        return nsbtx.indexOfPaletteName(name);
    }

    public void loadNsbtx(String path) throws IOException {
        this.nsbtx = NsbtxLoader2.loadNsbtx(path);
    }

    public Nsbtx2 getNsbtx() {
        return nsbtx;
    }

}
