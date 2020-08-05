/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2.areadata;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import utils.BinaryReader;
import utils.BinaryWriter;

/**
 *
 * @author Trifindo
 */
public class AreaDataHGSS {

    public static final Map<Integer, String> namesDynamicTexType = new HashMap<Integer, String>() {
        {
            put((Integer) 65535, "No Dynamic Textures");
            put((Integer) 0, "Outdoor Dynamic Textures");
            put((Integer) 1, "Other Dynamic Textures");
        }
    };
    public static final Map<String, Integer> namesDynamicTexTypeSwap = namesDynamicTexType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private int buildingTilesetID;
    private int mapTilesetID;
    private int dynamicTexType;
    private int areaType;
    private int lightType;

    public AreaDataHGSS(byte[] data) throws Exception {
        buildingTilesetID = (int) BinaryReader.readUInt16(data, 0);
        mapTilesetID = (int) BinaryReader.readUInt16(data, 2);
        dynamicTexType = (int) BinaryReader.readUInt16(data, 4);
        areaType = (int) BinaryReader.readUInt8(data, 6);
        lightType = (int) BinaryReader.readUInt8(data, 7);

    }

    public AreaDataHGSS() {
        buildingTilesetID = 0;
        mapTilesetID = 0;
        dynamicTexType = 65535;
        areaType = 1;
        lightType = 1;
    }

    public byte[] toByteArray() throws Exception {
        byte[] data = new byte[8];
        BinaryWriter.writeUInt16(data, 0, buildingTilesetID);
        BinaryWriter.writeUInt16(data, 2, mapTilesetID);
        BinaryWriter.writeUInt16(data, 4, dynamicTexType);
        BinaryWriter.writeUInt8(data, 6, areaType);
        BinaryWriter.writeUInt8(data, 7, lightType);
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
        return dynamicTexType;
    }

    public void setUnknown1(int unknown1) {
        this.dynamicTexType = unknown1;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int unknown2) {
        this.areaType = unknown2;
    }

    public int getDynamicTexType() {
        return dynamicTexType;
    }

    public int getLightType() {
        return lightType;
    }

    public void setDynamicTexType(int dynamicTexType) {
        this.dynamicTexType = dynamicTexType;
    }

    public void setLightType(int lightType) {
        this.lightType = lightType;
    }

}
