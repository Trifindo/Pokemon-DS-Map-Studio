package formats.mapbin;

import editor.buildingeditor2.buildfile.BuildFile;
import formats.bdhc.Bdhc;
import formats.bdhcam.Bdhcam;
import formats.collisions.Collisions;
import utils.BinaryWriter;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MapBinPt extends MapBin {

    private byte[] per;
    private byte[] bld;
    private byte[] nsbmd;
    private byte[] bdhc;

    public MapBinPt(String folderPath, String mapName) throws MissingMapBinFileException, NsbmdConversionException {
        mapName = Utils.removeExtensionFromPath(mapName);
        try {
            per = Files.readAllBytes(Paths.get(folderPath + File.separator + mapName + "." + Collisions.fileExtension));
        } catch (Exception ex) {
            throw new MissingMapBinFileException(MissingMapBinFileException.MISSING_PER);
        }

        try {
            bld = Files.readAllBytes(Paths.get(folderPath + File.separator + mapName + "." + BuildFile.fileExtension));
        } catch (Exception ex) {
            throw new MissingMapBinFileException(MissingMapBinFileException.MISSING_BLD);
        }

        try {
            nsbmd = Files.readAllBytes(Paths.get(folderPath + File.separator + mapName + "." + "nsbmd"));
        } catch (Exception ex) {
            throw new MissingMapBinFileException(MissingMapBinFileException.MISSING_NSBMD);
        }

        try {
            nsbmd = NsbmdUtils.nsbmdTexToNsbmdOnly(nsbmd);
        } catch (Exception ex) {
            throw new NsbmdConversionException();
        }

        try {
            bdhc = Files.readAllBytes(Paths.get(folderPath + File.separator + mapName + "." + Bdhc.fileExtension));
            try {
                byte[] bdhcam = Files.readAllBytes(Paths.get(folderPath + File.separator + mapName + "." + Bdhcam.fileExtension));
                if(bdhcam.length > bdhc.length + 10){//BDHCAM has data
                    bdhc = bdhcam;
                }
            }catch(Exception ex2){

            }
        } catch (Exception ex) {
            throw new MissingMapBinFileException(MissingMapBinFileException.MISSING_BDHC);
        }
    }

    public static byte[] toByteArray(MapBinPt map) {
        byte[] header = new byte[16];
        BinaryWriter.writeUInt32(header, 0, map.per.length);
        BinaryWriter.writeUInt32(header, 4, map.bld.length);
        BinaryWriter.writeUInt32(header, 8, map.nsbmd.length);
        BinaryWriter.writeUInt32(header, 12, map.bdhc.length);

        byte[] data = new byte[header.length + map.per.length + map.bld.length + map.nsbmd.length + map.bdhc.length];
        int offset = 0;
        System.arraycopy(header, 0, data, offset, header.length);
        offset += header.length;
        System.arraycopy(map.per, 0, data, offset, map.per.length);
        offset += map.per.length;
        System.arraycopy(map.bld, 0, data, offset, map.bld.length);
        offset += map.bld.length;
        System.arraycopy(map.nsbmd, 0, data, offset, map.nsbmd.length);
        offset += map.nsbmd.length;
        System.arraycopy(map.bdhc, 0, data, offset, map.bdhc.length);

        return data;
    }

    @Override
    public void saveToFile(String path) throws IOException {
        try {
            byte[] byteData = toByteArray(this);
            Files.write(new File(path).toPath(), byteData);
        } catch (Exception ex) {
            throw new IOException();
        }
    }
}