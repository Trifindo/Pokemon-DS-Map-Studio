package formats.mapbin;

import utils.BinaryReader;
import utils.BinaryWriter;

public class NsbmdUtils {

    public static boolean hasNsbtx(byte[] nsbmd){
        return nsbmd[0xE] == 0x02;
    }

    public static byte[] nsbmdTexToNsbmdOnly(byte[] nsbmdTex) throws Exception {
        if(hasNsbtx(nsbmdTex)){
            int offset1 = (int) BinaryReader.readUInt32(nsbmdTex, 0x10);
            int offset2 = (int) BinaryReader.readUInt32(nsbmdTex, 0x14);
            String type1 = BinaryReader.readString(nsbmdTex, (int) offset1, 4);
            String type2 = BinaryReader.readString(nsbmdTex, (int) offset2, 4);

            int offset;
            if(type1.equals("MDL0")){
                offset = offset1;
            }else if(type2.equals("MDL0")){
                offset = offset2;
            }else {
                throw new Exception();
            }

            //Create new NSBMD without NSBTX data
            int mdl0Size = (int) BinaryReader.readUInt32(nsbmdTex, offset + 0x4);
            int nsbmdSize = 0x14 + mdl0Size;
            byte[] nsbmdOnly = new byte[nsbmdSize];

            //Write header
            BinaryWriter.writeString(nsbmdOnly, 0x0,"BMD0");
            BinaryWriter.writeBytes(nsbmdOnly, 0x4, new byte[]{(byte)0xFF, (byte)0xFE, (byte)0x02, (byte)0x00});
            BinaryWriter.writeUInt32(nsbmdOnly, 0x8, nsbmdSize);
            BinaryWriter.writeUInt16(nsbmdOnly, 0xC, 16);
            BinaryWriter.writeUInt16(nsbmdOnly, 0xe, 1);
            BinaryWriter.writeUInt32(nsbmdOnly, 0x10, 0x14);

            //Write MDL0
            System.arraycopy(nsbmdTex, offset, nsbmdOnly, 0x14, mdl0Size);

            return nsbmdOnly;
        }else{
            return nsbmdTex.clone();
        }
    }
}
