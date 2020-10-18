
package editor.buildingeditor2.areabuild;

import java.util.ArrayList;

import utils.BinaryReader;
import utils.BinaryWriter;

/**
 * @author Trifindo
 */
public class AreaBuild {

    private ArrayList<Integer> buildingIDs;

    public AreaBuild(ArrayList<Integer> buildingIDs) {
        this.buildingIDs = buildingIDs;
    }

    public AreaBuild(byte[] data) {
        try {
            final int numBuildings = (int) BinaryReader.readUInt16(data, 0);
            buildingIDs = new ArrayList<>(numBuildings);
            for (int i = 0; i < numBuildings; i++) {
                buildingIDs.add((int) BinaryReader.readUInt16(data, 2 + i * 2));
            }
        } catch (Exception ex) {
            buildingIDs = new ArrayList<>();
        }
    }

    public byte[] toByteArray() throws Exception {
        byte[] data = new byte[2 + buildingIDs.size() * 2];
        BinaryWriter.writeUInt16(data, 0, buildingIDs.size());
        for (int i = 0; i < buildingIDs.size(); i++) {
            BinaryWriter.writeUInt16(data, 2 + i * 2, buildingIDs.get(i));
        }
        return data;
    }

    public ArrayList<Integer> getBuildingIDs() {
        return buildingIDs;
    }


}
