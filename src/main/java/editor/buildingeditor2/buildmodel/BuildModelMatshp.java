
package editor.buildingeditor2.buildmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import utils.BinaryReader;
import utils.BinaryWriter;

/**
 * @author Trifindo
 */
public class BuildModelMatshp {

    private final List<List<Integer>> materials;

    public BuildModelMatshp(String path) throws IOException {
        BinaryReader br = new BinaryReader(path);

        //Read header
        int numBuildings = br.readUInt16();
        int numMaterials = br.readUInt16();

        //Read block 1
        List<List<Integer>> newMaterials = new ArrayList<>(numBuildings);
        List<Integer> offsets = new ArrayList<>(numBuildings);
        List<Integer> matsPerBuild = new ArrayList<>(numBuildings);
        for (int i = 0; i < numBuildings; i++) {
            int nMatsPerBuild = br.readUInt16();
            int offset = br.readUInt16();
            if (nMatsPerBuild > 0 && offset != 65535) {
                offsets.add(offset);
                matsPerBuild.add(nMatsPerBuild);
            } else {
                offsets.add(null);
                matsPerBuild.add(null);
            }
        }

        //Read block 2
        for (int i = 0; i < numBuildings; i++) {
            if (offsets.get(i) != null) {
                int nMats = matsPerBuild.get(i);
                List<Integer> matInBuild = IntStream.range(0, nMats)
                        .mapToObj(j -> 0)
                        .collect(Collectors.toCollection(() -> new ArrayList<>(nMats)));

                for (int j = 0; j < nMats; j++) {
                    matInBuild.set(br.readUInt16(), br.readUInt16());
                }
                newMaterials.add(matInBuild);
            } else {
                newMaterials.add(new ArrayList<>());
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
        for (List<Integer> integers : materials) {
            if (!integers.isEmpty()) {
                bw.writeUInt16(integers.size());
                bw.writeUInt16(offset);
                offset += integers.size();
            } else {
                bw.writeUInt16(0);
                bw.writeUInt16(65535);
            }
        }

        for (List<Integer> material : materials) {
            if (!material.isEmpty()) {
                for (int j = 0; j < material.size(); j++) {
                    bw.writeUInt16(j);
                    bw.writeUInt16(material.get(j));
                }
            }
        }

        bw.close();
    }

    public void addBuildingMaterials(List<Integer> newMaterials) {
        materials.add(newMaterials);
    }

    public void replaceBuildingMaterials(int index, List<Integer> newMaterials) {
        if (index >= 0 && index < materials.size()) {
            materials.set(index, newMaterials);
        }
    }

    public void removeBuildingMaterials(int index) {
        if (index >= 0 && index < materials.size()) {
            materials.remove(index);
        }
    }

    public void addBuildingMaterial(int buildIndex) {
        if (buildIndex >= 0 && buildIndex < materials.size()) {
            if (materials.get(buildIndex).isEmpty()) {
                materials.set(buildIndex, new ArrayList<>());
            }
            materials.get(buildIndex).add(materials.get(buildIndex).size());
        }
    }

    public void removeBuildingMaterial(int buildIndex, int materialIndex) {
        if (buildIndex >= 0 && buildIndex < materials.size()) {
            List<Integer> buildMaterials = materials.get(buildIndex);
            if (!buildMaterials.isEmpty()) {
                if (materialIndex >= 0 && materialIndex < buildMaterials.size()) {
                    int materialValue = buildMaterials.get(materialIndex);
                    buildMaterials.remove(materialIndex);
                    if (buildMaterials.isEmpty()) {
                        materials.set(buildIndex, new ArrayList<>());
                    } else {
                        for (int i = 0; i < buildMaterials.size(); i++) {
                            if (buildMaterials.get(i) > materialValue) {
                                buildMaterials.set(i, buildMaterials.get(i) - 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void moveMaterialUp(int buildIndex, int materialIndex) {
        if (buildIndex >= 0 && buildIndex < materials.size()) {
            List<Integer> buildMaterials = materials.get(buildIndex);
            if (!buildMaterials.isEmpty()) {
                if (materialIndex >= 1 && materialIndex < buildMaterials.size()) {
                    Collections.swap(buildMaterials, materialIndex, materialIndex - 1);
                }
            }
        }
    }

    public void moveMaterialDown(int buildIndex, int materialIndex) {
        if (buildIndex >= 0 && buildIndex < materials.size()) {
            List<Integer> buildMaterials = materials.get(buildIndex);
            if (!buildMaterials.isEmpty()) {
                if (materialIndex >= 0 && materialIndex < buildMaterials.size() - 1) {
                    Collections.swap(buildMaterials, materialIndex, materialIndex + 1);
                }
            }
        }
    }

    public int countNumberOfMaterials() {
        return materials.stream()
                .filter(material -> !material.isEmpty())
                .mapToInt(List::size)
                .sum();
    }

    public List<List<Integer>> getAllMaterials() {
        return materials;
    }

    public List<Integer> getMaterials(int buildingID) {
        return materials.get(buildingID);
    }

    public void setMaterials(int buildingID, List<Integer> materials) {
        this.materials.set(buildingID, materials);
    }

    public int getMaterial(int buildingID, int materialIndex) {
        return materials.get(buildingID).get(materialIndex);
    }
}
