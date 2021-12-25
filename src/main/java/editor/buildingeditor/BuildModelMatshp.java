
package editor.buildingeditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import utils.BinaryReader;
import utils.BinaryWriter;

/**
 * @author Trifindo
 */
public class BuildModelMatshp {

    private final List<List<Integer>> materials;

    public BuildModelMatshp(String path) throws IOException {
        BinaryReader br = new BinaryReader(path);

        int numBuildings = br.readUInt16();
        int numMaterials = br.readUInt16();

        List<List<Integer>> newMaterials = new ArrayList<>(numBuildings);
        List<Integer> offsets = new ArrayList<>(numBuildings);
        List<Integer> matsPerBuild = new ArrayList<>(numBuildings);
        for (int i = 0; i < numBuildings; i++) {
            int nMatsPerBuild = br.readUInt16();
            int offset = br.readUInt16();
            if (nMatsPerBuild > 0 && offset != 255) {
                offsets.add(offset);
                matsPerBuild.add(nMatsPerBuild);
            } else {
                offsets.add(null);
                matsPerBuild.add(null);
            }
        }

        for (int i = 0; i < numBuildings; i++) {
            if (offsets.get(i) != null) {
                int nMats = matsPerBuild.get(i);
                List<Integer> matInBuild = new ArrayList<>(nMats);
                for (int j = 0; j < nMats; j++) {
                    matInBuild.add(0);
                }
                for (int j = 0; j < nMats; j++) {
                    matInBuild.set(br.readUInt16(), br.readUInt16());
                }
                newMaterials.add(matInBuild);
            } else {
                newMaterials.add(null);
            }
        }

        br.close();

        materials = newMaterials;
    }

    public void saveToFile(String path) throws IOException {
        BinaryWriter bw = new BinaryWriter(path);

        bw.writeUInt16(materials.size());
        bw.writeUInt16(countNumberOfMaterials());

        int offset = 0;
        for (List<Integer> material : materials) {
            if (material != null) {
                bw.writeUInt16(material.size());
                bw.writeUInt16(offset);
                offset += material.size();
            } else {
                bw.writeUInt16(0);
                bw.writeUInt16(65535);
            }
        }

        for (List<Integer> material : materials) {
            if (material != null) {
                for (int j = 0; j < material.size(); j++) {
                    bw.writeUInt16(j);
                    bw.writeUInt16(material.get(j));
                }
            }
        }

        bw.close();
    }

    public int countNumberOfMaterials() {
        return materials.stream()
                .filter(Objects::nonNull)
                .mapToInt(List::size)
                .sum();
    }

    public List<List<Integer>> getAllMaterials() {
        return materials;
    }

    public List<Integer> getMaterials(int buildingID) {
        return materials.get(buildingID);
    }

    public int getMaterial(int buildingID, int materialIndex) {
        return materials.get(buildingID).get(materialIndex);
    }
}
