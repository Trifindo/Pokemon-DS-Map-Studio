package formats.bdhcam;

import editor.game.Game;
import formats.bdhc.Bdhc;
import formats.bdhc.BdhcWriterDP;
import formats.bdhc.BdhcWriterHGSS;
import formats.bdhcam.camplate.CamParameter;
import formats.bdhcam.camplate.CamParameterPosDep;
import formats.bdhcam.camplate.CamParameterPosIndep;
import formats.bdhcam.camplate.Camplate;
import utils.BinaryArrayWriter;
import utils.BinaryBufferWriter;
import utils.BinaryWriter;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;


public class BdhcamWriter {

    public static void writeBdhcamToFile(String path, Bdhcam bdhcam, Bdhc bdhc, int game) throws IOException {

        byte[] bdhcData;
        if (game == Game.DIAMOND || game == Game.PEARL) {
            bdhcData = BdhcWriterDP.bdhcToByteArray(bdhc);
        } else {
            bdhcData = BdhcWriterHGSS.bdhcToByteArray(bdhc);
        }
        writeBdhcamToFile(path, bdhcam, bdhcData);

    }

    public static void writeBdhcamToFile(String path, Bdhcam bdhcam, byte[] bdhcData) throws IOException {
        try {
            byte[] bdhcamData = bdhcamToByteArray(bdhcam, bdhcData);
            path = Utils.addExtensionToPath(path, Bdhcam.fileExtension);
            Files.write(new File(path).toPath(), bdhcamData);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public static byte[] bdhcamToByteArray(Bdhcam bdhcam, byte[] bdhcData) throws Exception {
        byte[] bdhcamData = bdhcamToByteArray(bdhcam);
        int padding = (-bdhcData.length) & 3;
        byte[] mergedData = new byte[bdhcData.length + padding + bdhcamData.length];

        System.arraycopy(bdhcData, 0, mergedData, 0, bdhcData.length);
        for (int i = 0; i < padding; i++) {
            mergedData[bdhcData.length + i] = (byte) 0xFF;
        }
        System.arraycopy(bdhcamData, 0, mergedData, bdhcData.length + padding, bdhcamData.length);

        BinaryWriter.writeUInt16(mergedData, 0, bdhcamData.length + padding);
        BinaryWriter.writeUInt16(mergedData, 2, bdhcData.length - 0x10 + padding);

        return mergedData;
    }

    public static void writeBdhcamToFile(String path, Bdhcam bdhcam) throws Exception {
        byte[] bdhcamData = bdhcamToByteArray(bdhcam);
        path = Utils.addExtensionToPath(path, Bdhcam.fileExtension);
        Files.write(new File(path).toPath(), bdhcamData);
    }

    public static byte[] bdhcamToByteArray(Bdhcam bdhcam) throws Exception {
        BinaryBufferWriter writer = new BinaryBufferWriter();

        writer.writeString("DCAM");

        final int numPlates = bdhcam.getNumValidPlates();
        writer.writeUInt32(numPlates);

        int paramBytesWritten = 0;
        for (Camplate plate : bdhcam.getPlates()) {
            if (plate.parameters.size() > 0) {
                int offset = writer.getPos();

                writer.writeUInt8(plate.x);
                writer.writeUInt8(plate.y);
                writer.writeUInt8(plate.x + plate.width);
                writer.writeUInt8(plate.y + plate.height);

                if (plate.useZ) {
                    writer.writeUInt8(plate.z);
                } else {
                    writer.writeUInt8(0x80);
                }

                writer.writeUInt8(plate.type.ID);

                int paramOffset = 8 + numPlates * 8 + paramBytesWritten - offset;
                writer.writeUInt16(paramOffset);

                writer.mark();
                writer.skip(paramOffset - 8);

                writer.writeUInt32(plate.parameters.size());
                paramBytesWritten += 4;

                for (CamParameter param : plate.parameters) {
                    writer.writeUInt32(param.type.ID);
                    paramBytesWritten += 4;

                    if (plate.type.ID == Camplate.Type.POS_INDEPENDENT.ID) {
                        CamParameterPosIndep paramPI = (CamParameterPosIndep) param;
                        writer.writeUInt32(paramPI.duration);
                        writer.writeUInt32((int) (paramPI.finalValue * 0x10000));
                        paramBytesWritten += 8;
                    } else {
                        CamParameterPosDep paramPD = (CamParameterPosDep) param;
                        writer.writeUInt32((int) (paramPD.firstValue * 0x10000));
                        writer.writeUInt32((int) (paramPD.secondValue * 0x10000));
                        paramBytesWritten += 8;
                    }
                }
                writer.reset();
            }
        }

        return writer.toByteArray();

    }

}
