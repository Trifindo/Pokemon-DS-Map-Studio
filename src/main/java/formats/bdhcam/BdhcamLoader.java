package formats.bdhcam;

import formats.bdhcam.camplate.*;
import utils.BinaryArrayReader;
import utils.BinaryReader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class BdhcamLoader {

    public static Bdhcam loadBdhcam(String path) throws Exception{
        return loadBdhcam(Files.readAllBytes(new File(path).toPath()));
    }

    public static Bdhcam loadBdhcam(byte[] data) throws Exception {
        int bdhcamOffset = (int)BinaryReader.readUInt16(data, 2) + 0x10;
        return loadBdhcam(data, bdhcamOffset);
    }

    public static Bdhcam loadBdhcam(String path, int offset) throws Exception{
        return loadBdhcam(Files.readAllBytes(new File(path).toPath()), offset);
    }

    public static Bdhcam loadBdhcam(byte[] data, int offset) throws Exception {

        BinaryArrayReader reader = new BinaryArrayReader(data, offset);

        String signature = reader.readString(4);

        int numPlates = (int) reader.readUInt32();
        Bdhcam bdhcam = new Bdhcam(numPlates);
        ArrayList<Camplate> plates = bdhcam.getPlates();
        for (int i = 0; i < numPlates; i++) {
            Camplate plate;

            reader.mark();
            int x1 = reader.readUInt8();
            int y1 = reader.readUInt8();
            int x2 = reader.readUInt8();
            int y2 = reader.readUInt8();

            int z = reader.readUInt8();
            boolean useZ = true;
            if(z == 0x80){
                z = 0;
                useZ = false;
            }
            int plateType = reader.readUInt8();

            int paramOffset = reader.readUInt16();
            reader.reset();

            reader.skip(paramOffset);
            int numParams = (int) reader.readUInt32();
            if (plateType == Camplate.Type.POS_INDEPENDENT.ID) {
                plate = new CamplatePosIndep(x1, y1, z, x2 - x1, y2 - y1, plateType, numPlates, useZ);
                for (int j = 0; j < numParams; j++) {
                    int paramID = (int) reader.readUInt32();
                    int duration = (int) reader.readUInt32();
                    float finalValue = (float)reader.readInt32() / 0x10000;

                    CamParameter.Type type = CamParameter.Type.get(paramID);
                    CamParameterPosIndep param = new CamParameterPosIndep(type, duration, finalValue);
                    plate.parameters.add(param);
                }
            } else {
                plate = new CamplatePosDep(x1, y1, z, x2 - x1, y2 - y1, plateType, numPlates, useZ);
                for (int j = 0; j < numParams; j++) {
                    int paramID = (int) reader.readUInt32();
                    float firstValue = (float)reader.readInt32() / 0x10000;
                    float secondValue = (float)reader.readInt32() / 0x10000;

                    CamParameter.Type type = CamParameter.Type.get(paramID);
                    CamParameterPosDep param = new CamParameterPosDep(type, firstValue, secondValue);
                    plate.parameters.add(param);
                }
            }
            bdhcam.getPlates().add(plate);

            reader.reset();
            reader.skip(8);
        }

        return bdhcam;
    }
}
