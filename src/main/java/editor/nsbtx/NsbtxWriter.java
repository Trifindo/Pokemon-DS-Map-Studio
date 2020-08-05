/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.nsbtx;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
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
