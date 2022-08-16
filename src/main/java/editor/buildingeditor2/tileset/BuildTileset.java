
package editor.buildingeditor2.tileset;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.Utils;

/**
 * @author Trifindo
 */
public class BuildTileset {
    public static final String format = "nsbtx";
    public static final String ext = '.' + format;

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

    public void save(String path) throws IOException {
        path = Utils.addExtensionToPath(path, format);
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(data);
        fos.close();
    }


}
