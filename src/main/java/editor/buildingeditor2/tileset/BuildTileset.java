/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2.tileset;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class BuildTileset {

    private byte[] data;

    public BuildTileset(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void save(String path) throws IOException{
        path = Utils.addExtensionToPath(path, "nsbtx");
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(data);
        fos.close();
    }
    
    
    
}
