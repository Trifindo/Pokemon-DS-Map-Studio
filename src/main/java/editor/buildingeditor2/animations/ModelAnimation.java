/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2.animations;

import editor.buildingeditor2.NamedFile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import utils.BinaryReader;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class ModelAnimation implements NamedFile {

    public static final int TYPE_NSBCA = 0;
    public static final int TYPE_NSBTA = 1;
    public static final int TYPE_NSBTP = 2;
    public static final int TYPE_NSBMA = 3;
    public static final int TYPE_NSBVA = 4;

    private static final ArrayList<String> typeFileNames = new ArrayList<String>() {
        {
            add("BCA0");
            add("BTA0");
            add("BTP0");
            add("BMA0");
        }
    };

    private static final ArrayList<String> typeFileExtensions = new ArrayList<String>() {
        {
            add("nsbca");
            add("nsbta");
            add("nsbtp");
            add("nsbma");
        }
    };

    private String name;
    private int type;
    private byte[] data;

    public ModelAnimation(byte[] data, int index) {
        this.data = data;
        calculateAnimationName(index);
        calculateAnimationType();
    }

    @Override
    public String getName() {
        return name;
    }

    public ModelAnimation(String path, int index) throws IOException, Exception {
        byte[] data = Files.readAllBytes(Paths.get(path));
        String signature = BinaryReader.readString(data, 0, 4);
        if (typeFileNames.contains(signature)) {
            this.data = data;
            calculateAnimationName(index);
            calculateAnimationType();
        } else {
            throw new Exception();
        }
    }

    public void saveToFile(String path) throws IOException {
        path = Utils.addExtensionToPath(path, typeFileExtensions.get(type));
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(data);
        fos.close();
    }

    public void calculateAnimationName(int index) {
        try {
            name = getAnimationName(data);
        } catch (Exception ex) {
            name = "Unknown animation " + String.valueOf(index);
        }
    }

    public void calculateAnimationType() {
        try {
            type = getAnimationType(data);
        } catch (Exception ex) {
            type = -1;
        }
    }

    public String getAnimationTypeName() {
        if (type >= 0 && type < typeFileNames.size()) {
            return typeFileNames.get(type);
        } else {
            return "????";
        }
    }

    public int getAnimationType() {
        return type;
    }

    public String getExtensionName() {
        return typeFileExtensions.get(type);
    }

    public byte[] getData(){
        return data;
    }
    
    private static String getAnimationName(byte[] data) throws Exception {
        long nameOffset = BinaryReader.readUInt32(data, 16);
        String name = BinaryReader.readString(data, (int) (32 + nameOffset), 16);
        return Utils.removeLastOcurrences(name, '\u0000');
    }

    private static int getAnimationType(byte[] data) throws Exception {
        String type = BinaryReader.readString(data, 0, 4);
        return typeFileNames.indexOf(type);
    }

}
