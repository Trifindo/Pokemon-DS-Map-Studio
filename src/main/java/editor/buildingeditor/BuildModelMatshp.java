/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class BuildModelMatshp {
    
    private ArrayList<ArrayList<Integer>> materials;
    
    public BuildModelMatshp(String path) throws FileNotFoundException, 
            IOException{
        BinaryReader br = new BinaryReader(path);
        
        int numBuildings = br.readUInt16();
        int numMaterials = br.readUInt16();
        
        ArrayList<ArrayList<Integer>> newMaterials = new ArrayList<>(numBuildings);
        ArrayList<Integer> offsets = new ArrayList<>(numBuildings);
        ArrayList<Integer> matsPerBuild = new ArrayList<>(numBuildings);
        for(int i = 0; i < numBuildings; i++){
            int nMatsPerBuild = br.readUInt16();
            int offset = br.readUInt16();
            if(nMatsPerBuild > 0 && offset != 255){
                offsets.add(offset);
                matsPerBuild.add(nMatsPerBuild);
            }else{
                offsets.add(null);
                matsPerBuild.add(null);
            }
        }
        
        for(int i = 0; i < numBuildings; i++){
            if(offsets.get(i) != null){
                int nMats = matsPerBuild.get(i);
                ArrayList<Integer> matInBuild = new ArrayList<>(nMats);
                for(int j = 0; j < nMats; j++){
                    matInBuild.add(0);
                }
                for(int j = 0; j < nMats; j++){
                    matInBuild.set(br.readUInt16(), br.readUInt16());
                }
                newMaterials.add(matInBuild);
            }else{
                newMaterials.add(null);
            }
        }
        
        br.close();
        
        materials = newMaterials;
        
    }
    
    public void saveToFile(String path) throws FileNotFoundException, IOException{
        BinaryWriter bw = new BinaryWriter(path);
        
        bw.writeUInt16(materials.size());
        bw.writeUInt16(countNumberOfMaterials());
        
        int offset = 0;
        for(int i = 0; i < materials.size(); i++){
            if(materials.get(i) != null){
                bw.writeUInt16(materials.get(i).size());
                bw.writeUInt16(offset);
                offset += materials.get(i).size();
            }else{
                bw.writeUInt16(0);
                bw.writeUInt16(65535);
            }
        }
        
        for(int i = 0; i < materials.size(); i++){
            if(materials.get(i) != null){
                for(int j = 0; j < materials.get(i).size(); j++){
                    bw.writeUInt16(j);
                    bw.writeUInt16(materials.get(i).get(j));
                }
            }
        }
        
        bw.close();
    }
    
    public int countNumberOfMaterials(){
        int count = 0;
        for(int i = 0; i < materials.size(); i++){
            if(materials.get(i) != null){
                count += materials.get(i).size();
            }
        }
        return count;
    }
    
    public ArrayList<ArrayList<Integer>> getAllMaterials(){
        return materials;
    }
    
    public ArrayList<Integer> getMaterials(int buildingID){
        return materials.get(buildingID);
    }
    
    public int getMaterial(int buildingID, int materialIndex){
        return materials.get(buildingID).get(materialIndex);
    }
    
    
}
