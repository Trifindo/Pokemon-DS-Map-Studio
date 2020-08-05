/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2.areadata;

import utils.BinaryReader;
import utils.BinaryWriter;

/**
 *
 * @author Trifindo
 */
public class AreaDataDPPt {

    private int buildingTilesetID;
    private int mapTilesetID;
    private int unknown1;
    private int areaType;

    public AreaDataDPPt(byte[] data) throws Exception {
        buildingTilesetID = (int) BinaryReader.readUInt16(data, 0);
        mapTilesetID = (int) BinaryReader.readUInt16(data, 2);
        unknown1 = (int) BinaryReader.readUInt16(data, 4);
        areaType = (int) BinaryReader.readUInt16(data, 6);
    }
    
    public AreaDataDPPt(){
        buildingTilesetID = 0;
        mapTilesetID = 0;
        unknown1 = 0;
        areaType = 0;
    }

    public byte[] toByteArray() throws Exception{
        byte[] data = new byte[8];
        BinaryWriter.writeUInt16(data, 0, buildingTilesetID);
        BinaryWriter.writeUInt16(data, 2, mapTilesetID);
        BinaryWriter.writeUInt16(data, 4, unknown1);
        BinaryWriter.writeUInt16(data, 6, areaType);
        return data;
    }
    
    public int getBuildingTilesetID() {
        return buildingTilesetID;
    }

    public void setBuildingTilesetID(int buildingTilesetID) {
        this.buildingTilesetID = buildingTilesetID;
    }

    public int getMapTilesetID() {
        return mapTilesetID;
    }

    public void setMapTilesetID(int mapTilesetID) {
        this.mapTilesetID = mapTilesetID;
    }

    public int getUnknown1() {
        return unknown1;
    }

    public void setUnknown1(int unknown1) {
        this.unknown1 = unknown1;
    }

    public int getUnknown2() {
        return areaType;
    }

    public void setAreaType(int unknown2) {
        this.areaType = unknown2;
    }

    
    
}
