
package formats.nsbtx;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Trifindo
 */
public class NsbtxWriter {

    public static void saveNsbtx(Nsbtx nsbtx, String path) throws IOException {

        for (int i = 0; i < nsbtx.textureData.size(); i++) {
            for (int j = 0; j < nsbtx.textureData.get(i).length; j++) {
                nsbtx.rawData[nsbtx.textureDataOffsets.get(i) + j] = nsbtx.textureData.get(i)[j];
            }
        }

        for (int i = 0; i < nsbtx.paletteData.size(); i++) {
            for (int j = 0; j < nsbtx.paletteData.get(i).length; j++) {
                nsbtx.rawData[nsbtx.paletteDataOffsets.get(i) + j] = nsbtx.paletteData.get(i)[j];
            }
        }

        FileOutputStream out = new FileOutputStream(path);
        out.write(nsbtx.rawData);
        out.close();

    }

}
