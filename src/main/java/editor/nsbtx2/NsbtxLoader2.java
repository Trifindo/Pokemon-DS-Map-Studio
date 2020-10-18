
package editor.nsbtx2;

import editor.nsbtx2.exceptions.NsbtxTextureFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Trifindo
 */
public class NsbtxLoader2 {

    private static final int headerOffset = 0x04;
    private static final int headerTexOffset = 0x14;
    private static final int sectionTextInfoOffset = 0x54;
    private static final int textureInfoSize = 0x08;
    private static final int textureNameSize = 0x10;
    private static final int paletteInfoSize = 0x04;
    private static final int paletteNameSize = 0x10;

    public static Nsbtx2 loadNsbtx(byte[] data){
        int texSectionSize = readUnsignedInt(data, 0x18);
        int textureDataSize = readUnsignedShort(data, 0x20) << 3;
        int textureInfoOffset = readUnsignedShort(data, 0x22) + headerTexOffset;
        int textureDataOffset = readUnsignedInt(data, 0x28) + headerTexOffset;

        int paletteDataSize = readUnsignedInt(data, 0x44) << 3;
        int paletteInfoOffset = readUnsignedInt(data, 0x48) + headerTexOffset;
        int paletteDataOffset = readUnsignedInt(data, 0x4C) + headerTexOffset;

        int numTextures = readUnsignedByte(data, textureInfoOffset + 0x01);
        int numPalettes = readUnsignedByte(data, paletteInfoOffset + 0x01);

        //Load textures
        int textureInfoArrayOffset = readUnsignedShort(data, 0x02 + sectionTextInfoOffset) + sectionTextInfoOffset;
        int textureNamesOffset = textureInfoArrayOffset + textureInfoSize * numTextures;
        ArrayList<NsbtxTexture> textures = new ArrayList<>(numTextures);
        for (int i = 0; i < numTextures; i++) {
            int texInfoOffset = textureInfoArrayOffset + i * textureInfoSize;
            int texNameOffset = textureNamesOffset + i * textureNameSize;
            try {
                textures.add(new NsbtxTexture(data, texInfoOffset, textureDataOffset, texNameOffset));
            } catch (IndexOutOfBoundsException ex) {
                System.out.println("ERROR LOADING TEXTURE " + i);
            } catch (NsbtxTextureFormatException ex) {
                System.out.println("ERROR LOADING TEXTURE " + i);
            }
        }

        //Calculate palette offsets for dividing the palette data
        int sectionPalInfoOffset = paletteInfoOffset + 0x04;
        int paletteInfoArrayOffset = readUnsignedShort(data, 0x02 + sectionPalInfoOffset) + sectionPalInfoOffset;
        ArrayList<Integer> paletteOffsets = new ArrayList<>();
        for (int i = 0; i < numPalettes; i++) {
            int palInfoOffset = paletteInfoArrayOffset + i * paletteInfoSize;
            int paletteOffset = ((data[palInfoOffset + 0x01] & 0xFF) << 8 | data[palInfoOffset] & 0xFF) << 3;
            if (!paletteOffsets.contains(paletteOffset)) {
                paletteOffsets.add(paletteOffset);
            }
        }
        paletteOffsets.add(data.length - paletteDataOffset);
        Collections.sort(paletteOffsets);

        //Load palettes 
        int paletteNamesOffset = paletteInfoArrayOffset + paletteInfoSize * numPalettes;
        ArrayList<NsbtxPalette> palettes = new ArrayList<>(numPalettes);
        for (int i = 0; i < numPalettes; i++) {
            int palInfoOffset = paletteInfoArrayOffset + i * paletteInfoSize;
            int palNameOffset = paletteNamesOffset + i * paletteNameSize;
            try {
                palettes.add(new NsbtxPalette(data, palInfoOffset, paletteOffsets, paletteDataOffset, palNameOffset));
            } catch (IndexOutOfBoundsException ex) {
                System.out.println("ERROR LOADING PALETTE " + i);
            }
        }
        
        //Generate NSBTX
        Nsbtx2 nsbtx = new Nsbtx2(textures, palettes);
        //nsbtx.setPath(path);
        
        return nsbtx;
    }
    
    public static Nsbtx2 loadNsbtx(String path) throws IOException {
        File file = new File(path);
        byte[] data = Files.readAllBytes(file.toPath());

        Nsbtx2 nsbtx = loadNsbtx(data);
        nsbtx.setPath(path);
        
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

}
