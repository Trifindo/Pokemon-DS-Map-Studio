
package editor.buildingeditor2.buildfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import utils.BinaryReader;
import utils.BinaryWriter;
import utils.Utils;

/**
 * @author Trifindo
 */
public class BuildFile {

    public static final String fileExtension = "bld";
    private List<Build> builds;

    public BuildFile() {
        builds = new ArrayList<>();
    }

    public BuildFile(String path) throws IOException {
        try {
            byte[] data = Files.readAllBytes(new File(path).toPath());

            int numBuilds = data.length / Build.dataSize;
            builds = new ArrayList<Build>(numBuilds);

            for (int i = 0, offset = 0; i < numBuilds; i++) {
                offset = Build.dataSize * i;
                int buildID = (int) BinaryReader.readUInt16(data, offset);
                offset += 4;
                float x = BinaryReader.readFI32(data, offset);
                offset += 4;
                float y = BinaryReader.readFI32(data, offset);
                offset += 4;
                float z = BinaryReader.readFI32(data, offset);
                offset += 4 + 12;
                float xScale = BinaryReader.readUInt16(data, offset) / 4096f;
                offset += 4;
                float yScale = (int) BinaryReader.readUInt16(data, offset) / 4096f;
                offset += 4;
                float zScale = (int) BinaryReader.readUInt16(data, offset) / 4096f;

                builds.add(new Build(buildID, x, y, z, xScale, yScale, zScale));
            }

        } catch (Exception ex) {
            throw new IOException();
        }
    }

    public byte[] toByteArray() throws Exception {
        byte[] data = new byte[builds.size() * Build.dataSize];

        for (int i = 0, offset = 0; i < builds.size(); i++) {
            offset = Build.dataSize * i;

            Build build = builds.get(i);

            BinaryWriter.writeUInt16(data, offset, build.getModeID());
            offset += 4;
            BinaryWriter.writeFI32(data, offset, build.getX());
            offset += 4;
            BinaryWriter.writeFI32(data, offset, build.getY());
            offset += 4;
            BinaryWriter.writeFI32(data, offset, build.getZ());
            offset += 4 + 12;
            BinaryWriter.writeUInt16(data, offset, (int) (build.getScaleX() * 4096));
            offset += 4;
            BinaryWriter.writeUInt16(data, offset, (int) (build.getScaleY() * 4096));
            offset += 4;
            BinaryWriter.writeUInt16(data, offset, (int) (build.getScaleZ() * 4096));
        }

        return data;
    }

    public void saveToFile(String path) throws IOException {
        if (!path.endsWith("." + fileExtension)) {
            path = Utils.removeExtensionFromPath(path);
            path += "." + fileExtension;
        }
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(toByteArray());
        } catch (Exception ex) {
            throw new IOException();
        }
    }

    public List<Build> getBuilds() {
        return builds;
    }

}
