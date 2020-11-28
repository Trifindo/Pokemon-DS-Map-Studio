
package formats.backsound;

import utils.exceptions.WrongFormatException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import utils.BinaryReader;
import utils.BinaryWriter;

/**
 * @author Trifindo
 */
public class Backsound {

    public static final String fileExtension = "bgs";
    private static final int SIGNATURE = 4660;
    private static final int BYTES_PER_SOUNDPLATE = 8;

    private ArrayList<Soundplate> soundplates = new ArrayList<>();

    public Backsound() {
        soundplates = new ArrayList<>();
        //soundplates.add(new Soundplate());
    }

    public Backsound(String path) throws FileNotFoundException, IOException, WrongFormatException {
        BinaryReader reader = new BinaryReader(path);

        if (reader.readUInt16() != SIGNATURE) {
            throw new WrongFormatException("The signature of the file doesn't correspond to a background sound file");
        }

        final int sectionSize = reader.readUInt16();
        final int numPlates = sectionSize / BYTES_PER_SOUNDPLATE;

        soundplates = new ArrayList<>(numPlates);
        for (int i = 0; i < numPlates; i++) {
            int soundCode = reader.readUInt8();
            int volume = reader.readUInt8();
            int byte3 = reader.readUInt8(); //Unknown
            int byte4 = reader.readUInt8(); //Unknown
            int x = reader.readUInt8();
            int y = reader.readUInt8();
            int width = reader.readUInt8() - x + 1;
            int height = reader.readUInt8() - y + 1;

            soundplates.add(new Soundplate(soundCode, volume, byte3, byte4, x, y, width, height));
        }

        reader.close();
    }

    public void writeToFile(String path) throws FileNotFoundException, IOException {
        BinaryWriter writer = new BinaryWriter(path);

        writer.writeUInt16(SIGNATURE);
        writer.writeUInt16(soundplates.size() * BYTES_PER_SOUNDPLATE);

        for (int i = 0; i < soundplates.size(); i++) {
            Soundplate soundplate = soundplates.get(i);
            writer.writeUInt8(soundplate.getSoundCode());
            writer.writeUInt8(soundplate.getVolume());
            writer.writeUInt8(soundplate.byte3);//Unknown
            writer.writeUInt8(soundplate.byte4);//Unknown
            writer.writeUInt8(soundplate.getX());
            writer.writeUInt8(soundplate.getY());
            writer.writeUInt8(soundplate.getWidth() + soundplate.getX() - 1);
            writer.writeUInt8(soundplate.getHeight() + soundplate.getY() - 1);
        }

        writer.close();
    }

    public ArrayList<Soundplate> getSoundplates() {
        return soundplates;
    }

    public Soundplate getSoundplate(int index) {
        return soundplates.get(index);
    }

}
