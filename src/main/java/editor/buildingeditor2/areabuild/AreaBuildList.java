
package editor.buildingeditor2.areabuild;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trifindo
 */
public class AreaBuildList {

    private final List<AreaBuild> areaBuilds;

    public AreaBuildList(Narc narc) {
        areaBuilds = new ArrayList<>(narc.getRoot().getFiles().size());
        for (NarcFile file : narc.getRoot().getFiles()) {
            areaBuilds.add(new AreaBuild(file.getData()));
        }
        System.out.println("Donete");
    }

    public Narc toNarc() {
        NarcFolder root = new NarcFolder();
        List<NarcFile> files = new ArrayList<>(areaBuilds.size());
        for (AreaBuild areaBuild : areaBuilds) {
            files.add(new NarcFile("", root, areaBuild.toByteArray()));
        }
        root.setFiles(files);
        return new Narc(root);
    }

    public List<Integer> getBuildingOccurrences(int buildingIndex) {
        List<Integer> ocurrences = new ArrayList<>();
        for (int i = 0; i < areaBuilds.size(); i++) {
            AreaBuild areaBuild = areaBuilds.get(i);
            for (Integer buildID : areaBuild.getBuildingIDs()) {
                if (buildID == buildingIndex) {
                    ocurrences.add(i);
                    break;
                }
            }
        }
        return ocurrences;
    }

    public void removeBuildingOccurrences(int buildingIndex) {
        for (AreaBuild areaBuild : areaBuilds) {
            for (int j = 0; j < areaBuild.getBuildingIDs().size(); j++) {
                if (areaBuild.getBuildingIDs().get(j) == buildingIndex) {
                    areaBuild.getBuildingIDs().remove(j);
                    j--;
                }
            }
        }
    }

    public void shiftBuildingIDsFrom(int buildingIndex) {
        for (AreaBuild areaBuild : areaBuilds) {
            for (int j = 0; j < areaBuild.getBuildingIDs().size(); j++) {
                if (areaBuild.getBuildingIDs().get(j) > buildingIndex) {
                    areaBuild.getBuildingIDs().set(j, areaBuild.getBuildingIDs().get(j) - 1);
                }
            }
        }
    }

    public List<AreaBuild> getAreaBuilds() {
        return areaBuilds;
    }

    public void addBuilding(int areaBuildIndex, int buildingIndex) {
        if (areaBuildIndex >= 0 && areaBuildIndex < areaBuilds.size()) {
            List<Integer> indices = areaBuilds.get(areaBuildIndex).getBuildingIDs();
            if (!indices.contains(buildingIndex)) {
                indices.add(buildingIndex);
            }
        }
    }

    public void replaceBuilding(int areaBuildIndex, int buildingIndex, int oldBuildingIndex) {
        if (areaBuildIndex >= 0 && areaBuildIndex < areaBuilds.size()) {
            List<Integer> indices = areaBuilds.get(areaBuildIndex).getBuildingIDs();
            if (!indices.contains(buildingIndex)) {
                if (oldBuildingIndex >= 0 && oldBuildingIndex < indices.size()) {
                    indices.set(oldBuildingIndex, buildingIndex);
                }
            }
        }
    }

    public void removeBuilding(int areaBuildIndex, int buildingIndex) {
        if (areaBuildIndex >= 0 && areaBuildIndex < areaBuilds.size()) {
            List<Integer> indices = areaBuilds.get(areaBuildIndex).getBuildingIDs();
            if (buildingIndex >= 0 && buildingIndex < indices.size()) {
                indices.remove(buildingIndex);
            }
        }
    }
}
