/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2.buildmodel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import utils.BinaryReader;
import utils.BinaryWriter;

/**
 *
 * @author Trifindo
 */
public class BuildModelMatshp {

    private ArrayList<ArrayList<Integer>> materials;

    public BuildModelMatshp(String path) throws FileNotFoundException,
            IOException {
        BinaryReader br = new BinaryReader(path);

        //Read header
        int numBuildings = br.readUInt16();
        int numMaterials = br.readUInt16();

        //Read block 1
        ArrayList<ArrayList<Integer>> newMaterials = new ArrayList<>(numBuildings);
        ArrayList<Integer> offsets = new ArrayList<>(numBuildings);
        ArrayList<Integer> matsPerBuild = new ArrayList<>(numBuildings);
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
                ArrayList<Integer> matInBuild = new ArrayList<>(nMats);
                for (int j = 0; j < nMats; j++) {
                    matInBuild.add(0);
                }

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

    public void saveToFile(String path) throws FileNotFoundException, IOException {
        BinaryWriter bw = new BinaryWriter(path);

        bw.writeUInt16(materials.size());
        bw.writeUInt16(countNumberOfMaterials());

        int offset = 0;
        for (int i = 0; i < materials.size(); i++) {
            if (!materials.get(i).isEmpty()) {
                bw.writeUInt16(materials.get(i).size());
                bw.writeUInt16(offset);
                offset += materials.get(i).size();
            } else {
                bw.writeUInt16(0);
                bw.writeUInt16(65535);
            }
        }

        for (int i = 0; i < materials.size(); i++) {
            if (!materials.get(i).isEmpty()) {
                for (int j = 0; j < materials.get(i).size(); j++) {
                    bw.writeUInt16(j);
                    bw.writeUInt16(materials.get(i).get(j));
                }
            }
        }

        bw.close();
    }

    public void addBuildingMaterials(ArrayList<Integer> newMaterials) {
        materials.add(newMaterials);
    }

    public void replaceBuildingMaterials(int index, ArrayList<Integer> newMaterials) {
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
            ArrayList<Integer> buildMaterials = materials.get(buildIndex);
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
            ArrayList<Integer> buildMaterials = materials.get(buildIndex);
            if (!buildMaterials.isEmpty()) {
                if (materialIndex >= 1 && materialIndex < buildMaterials.size()) {
                    Collections.swap(buildMaterials, materialIndex, materialIndex - 1);
                }
            }
        }
    }

    public void moveMaterialDown(int buildIndex, int materialIndex) {
        if (buildIndex >= 0 && buildIndex < materials.size()) {
            ArrayList<Integer> buildMaterials = materials.get(buildIndex);
            if (!buildMaterials.isEmpty()) {
                if (materialIndex >= 0 && materialIndex < buildMaterials.size() - 1) {
                    Collections.swap(buildMaterials, materialIndex, materialIndex + 1);
                }
            }
        }
    }

    public int countNumberOfMaterials() {
        int count = 0;
        for (int i = 0; i < materials.size(); i++) {
            if (!materials.get(i).isEmpty()) {
                count += materials.get(i).size();
            }
        }
        return count;
    }

    public ArrayList<ArrayList<Integer>> getAllMaterials() {
        return materials;
    }

    public ArrayList<Integer> getMaterials(int buildingID) {
        return materials.get(buildingID);
    }

    public void setMaterials(int buildingID, ArrayList<Integer> materials) {
        this.materials.set(buildingID, materials);
    }

    public int getMaterial(int buildingID, int materialIndex) {
        return materials.get(buildingID).get(materialIndex);
    }

}
