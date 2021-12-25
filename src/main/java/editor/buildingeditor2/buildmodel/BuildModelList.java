
package editor.buildingeditor2.buildmodel;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcFolder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import utils.BinaryReader;
import utils.Utils;

/**
 * @author Trifindo
 */
public class BuildModelList {

    private final List<byte[]> buildModelsData;
    private List<String> buildModelsName;

    public BuildModelList(Narc narc) {
        final int numModels = narc.getRoot().getFiles().size();
        buildModelsData = new ArrayList<>(numModels);
        for (NarcFile file : narc.getRoot().getFiles()) {
            buildModelsData.add(file.getData());
        }
        calculateModelsName();
    }

    public Narc toNarc() {
        NarcFolder root = new NarcFolder();
        for (byte[] buildModelsDatum : buildModelsData) {
            root.getFiles().add(new NarcFile("", root, buildModelsDatum));
        }
        return new Narc(root);
    }

    public void addBuildingModel(String path) throws Exception {
        buildModelsData.add(readBuildingModel(path));
        calculateModelsName();
    }

    public void replaceBuildingModel(int index, String path) throws Exception {
        buildModelsData.set(index, readBuildingModel(path));
        calculateModelsName();
    }

    public void saveBuildingModel(int index, String path) throws IOException {
        byte[] data = buildModelsData.get(index);
        path = Utils.addExtensionToPath(path, "nsbmd");
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(data);
        fos.close();
    }

    public void removeBuildingModel(int index) {
        if (index >= 0 && index < buildModelsData.size()) {
            buildModelsData.remove(index);
            calculateModelsName();
        }
    }

    private static byte[] readBuildingModel(String path) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(path));
        if (BinaryReader.readString(data, 0, 4).equals("BMD0")) {
            return data;
        } else {
            throw new Exception();
        }
    }

    public void addModel(byte[] data) {
        buildModelsData.add(data);
        calculateModelsName();
    }

    public void addModel(int index, byte[] data) {
        buildModelsData.add(index, data);
        calculateModelsName();
    }

    public void removeModel(int index) {
        buildModelsData.remove(index);
        calculateModelsName();
    }

    public List<byte[]> getModelsData() {
        return buildModelsData;
    }

    public List<String> getModelsName() {
        return buildModelsName;
    }

    private void calculateModelsName() {
        buildModelsName = new ArrayList<>(buildModelsData.size());
        for (int i = 0; i < buildModelsData.size(); i++) {
            try {
                buildModelsName.add(getModelName(buildModelsData.get(i)));
            } catch (Exception ex) {
                buildModelsName.add("Unknown model " + i);
            }
        }
    }

    public int getSize() {
        return buildModelsData.size();
    }

    private static String getModelName(byte[] data) {
        long nameOffset = BinaryReader.readUInt32(data, 16);
        return Utils.removeLastOccurrences(BinaryReader.readString(data, (int) (32 + nameOffset), 16), '\u0000');
    }
}
