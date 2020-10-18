
package editor.buildingeditor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import utils.BinaryReader;
import utils.BinaryWriter;

/**
 *
 * @author Trifindo
 */
public class BuildTilesetList {
    
    private ArrayList<Integer> buildingIDs;
    
    public BuildTilesetList(String path) throws FileNotFoundException, 
            IOException{
        BinaryReader br = new BinaryReader(path);
        int numberOfBuildings = br.readUInt16();
        buildingIDs = new ArrayList<>(numberOfBuildings);
        for(int i = 0; i < numberOfBuildings; i++){
            buildingIDs.add(br.readUInt16());
        }
        br.close();
    }
    
    public void saveToFile(String path)throws FileNotFoundException, IOException{
        BinaryWriter bw = new BinaryWriter(path);
        
        bw.writeUInt16(buildingIDs.size());
        for(int i = 0; i < buildingIDs.size(); i++){
            bw.writeUInt16(buildingIDs.get(i));
        }
        bw.close();
        
    }
    
    public ArrayList<Integer> getBuildingIDs(){
        return buildingIDs;
    }
    
    
}
