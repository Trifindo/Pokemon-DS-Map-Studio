/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.nsbtx;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Trifindo
 */
public class NsbtxLoader {

    private static final int headerOffset = 0x04;
    private static final int headerTexOffset = 0x14;
    private static final int sectionTextInfoOffset = 0x54;
    private static final int textureInfoSize = 0x08;
    private static final int textureNameSize = 0x10;
    private static final int paletteInfoSize = 0x04;
    private static final int paletteNameSize = 0x10;

    public static Nsbtx loadNsbtx(String path) throws IOException {
        Nsbtx nsbtx = new Nsbtx();

        File file = new File(path);
        byte[] data = Files.readAllBytes(file.toPath());

        int texSectionSize = readUnsignedInt(data, 0x18);
        int textureDataSize = readUnsignedShort(data, 0x20) << 3;
        int textureInfoOffset = readUnsignedShort(data, 0x22) + headerTexOffset;
        int textureDataOffset = readUnsignedInt(data, 0x28) + headerTexOffset;

        int paletteDataSize = readUnsignedInt(data, 0x44) << 3;
        int paletteInfoOffset = readUnsignedInt(data, 0x48) + headerTexOffset;
        int paletteDataOffset = readUnsignedInt(data, 0x4C) + headerTexOffset;

        int numTextures = readUnsignedByte(data, textureInfoOffset + 0x01);
        int numPalettes = readUnsignedByte(data, paletteInfoOffset + 0x01);

        //Load texture info
        int textureInfoArrayOffset = readUnsignedShort(data, 0x02 + sectionTextInfoOffset) + sectionTextInfoOffset;
        ArrayList<TextureInfo> textureInfos = new ArrayList<>();
        for (int i = 0; i < numTextures; i++) {
            int offset = textureInfoArrayOffset + i * textureInfoSize;
            textureInfos.add(new TextureInfo(data, offset));
        }

        //Load texture names
        int textureNamesOffset = textureInfoArrayOffset + textureInfoSize * numTextures;
        ArrayList<String> textureNames = new ArrayList<>();
        for (int i = 0; i < numTextures; i++) {
            int offset = textureNamesOffset + i * textureNameSize;
            textureNames.add(new String(data, offset, textureNameSize));
        }

        //Load texture data
        ArrayList<Byte[]> textureData = new ArrayList<>(numTextures);
        ArrayList<Integer> textureDataOffsets = new ArrayList<>(numTextures);
        for (int i = 0; i < numTextures; i++) {
            int textOffset = textureInfos.get(i).textureOffset;
            textureDataOffsets.add(textureDataOffset + textOffset);
            Byte[] textData = getSubArray(data,
                    textureDataOffset + textOffset,
                    textureInfos.get(i).getSize());
            textureData.add(textData);
        }

        //Load palette info
        int sectionPalInfoOffset = paletteInfoOffset + 0x04;
        int paletteInfoArrayOffset = readUnsignedShort(data, 0x02 + sectionPalInfoOffset) + sectionPalInfoOffset;
        ArrayList<PaletteInfo> paletteInfos = new ArrayList<>();
        for (int i = 0; i < numPalettes; i++) {
            int offset = paletteInfoArrayOffset + i * paletteInfoSize;
            paletteInfos.add(new PaletteInfo(data, offset));
        }

        //Load palette names
        int paletteNamesOffset = paletteInfoArrayOffset + paletteInfoSize * numPalettes;
        ArrayList<String> paletteNames = new ArrayList<>();
        for (int i = 0; i < numPalettes; i++) {
            int offset = paletteNamesOffset + i * paletteNameSize;
            paletteNames.add(new String(data, offset, paletteNameSize));
        }

        //Load palette data
        ArrayList<Byte[]> paletteData = new ArrayList<>(numPalettes);
        ArrayList<Integer> paletteDataOffsets = new ArrayList<>(numPalettes);
        for (int i = 0; i < numPalettes; i++) {
            int palOffset = paletteInfos.get(i).paletteOffset;
            int palSize = paletteInfos.get(i).getSize();
            paletteDataOffsets.add(paletteDataOffset + palOffset);
            Byte[] palData = getSubArray(data,
                    paletteDataOffset + palOffset,
                    palSize);
            paletteData.add(palData);
        }

        nsbtx.textureInfos = textureInfos;
        nsbtx.paletteInfos = paletteInfos;
        nsbtx.textureData = textureData;
        nsbtx.paletteData = paletteData;
        nsbtx.textureDataOffsets = textureDataOffsets;
        nsbtx.paletteDataOffsets = paletteDataOffsets;
        nsbtx.textureNames = textureNames;
        nsbtx.paletteNames = paletteNames;
        nsbtx.rawData = data; //TODO: this is inefficient but simplifies the writing process

        return nsbtx;
    }

    private static int readUnsignedInt(byte[] data, int offset) {
        byte[] bytes = new byte[]{
            data[offset],
            data[offset + 1],
            data[offset + 2],
            data[offset + 3]
        };
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /*
    public static short readUnsignedShort(byte[] data, int offset) {
        return (short) (((data[offset + 1]) << 8) | (data[offset] ));
    }
     */
    private static int readUnsignedShort(byte[] data, int offset) {
        byte[] bytes = new byte[]{
            data[offset],
            data[offset + 1]
        };
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static int readUnsignedByte(byte[] data, int offset) {
        return data[offset] & 0xFF;
    }

    private static Byte[] getSubArray(byte[] data, int offset, int size) {
        Byte[] subArray = new Byte[size];
        for (int i = 0; i < size; i++) {
            try {
                subArray[i] = data[offset + i];
            } catch (ArrayIndexOutOfBoundsException ex) {
                return subArray;
            }
        }
        return subArray;
    }

}
