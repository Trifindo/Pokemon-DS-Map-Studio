
package editor.buildingeditor2.animations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import utils.BinaryReader;
import utils.BinaryWriter;

/**
 * @author Trifindo
 */
public class BuildAnimInfoHGSS {

    public static final int MAX_ANIMS_PER_BUILDING = 4;

    // Animations
    public static final int TYPE1_NO_ANIMATION = 255;
    public static final int TYPE1_LOOP = 0;
    public static final int TYPE1_TRIGGER_UNK = 2;
    public static final int TYPE1_TRIGGER = 3;
    public static final int TYPE1_DAYNIGHT = 8;

    public static final int TYPE2_NO_ANIMATION = 0;
    public static final int TYPE2_LOOP = 1;
    public static final int TYPE2_TRIGGER = 2;

    public static final int LOOPTYPE_PERMANENT = 0;
    public static final int LOOPTYPE_ONESHOT = 1;

    public static final int DOORSOUND_NONE = 0;


    public static final Map<Integer, String> namesAnimType1 = new HashMap<Integer, String>() {
        {
            put((Integer) TYPE1_NO_ANIMATION, "No Animation");
            put((Integer) TYPE1_LOOP, "Loop");
            put((Integer) TYPE1_TRIGGER_UNK, "Trigger (?)");
            put((Integer) TYPE1_TRIGGER, "Trigger");
            put((Integer) TYPE1_DAYNIGHT, "Day/Night Cycle");
        }
    };

    public static final Map<Integer, String> namesLoopType = new HashMap<Integer, String>() {
        {
            put((Integer) LOOPTYPE_PERMANENT, "Permanent");
            put((Integer) LOOPTYPE_ONESHOT, "Oneshot");
        }
    };

    public static final Map<Integer, String> namesDoorSound = new HashMap<Integer, String>() {
        {
            put((Integer) DOORSOUND_NONE, "No sound");
            put((Integer) 1, "Wooden Door");
            put((Integer) 2, "Automatic Door");
            put((Integer) 3, "Old door (?)");
            put((Integer) 4, "Sliding Door");
        }
    };

    public static final Map<Integer, String> namesNumAnims = new HashMap<Integer, String>() {
        {
            put((Integer) 0, "0");
            put((Integer) 1, "1");
            put((Integer) 2, "2");
            put((Integer) 3, "3");
            put((Integer) 4, "4");
        }
    };

    public static final Map<Integer, String> namesAnimType2 = new HashMap<Integer, String>() {
        {
            put((Integer) TYPE2_NO_ANIMATION, "No Animation");
            put((Integer) TYPE2_LOOP, "Loop");
            put((Integer) TYPE2_TRIGGER, "Trigger");
        }
    };

    public static final Map<String, Integer> namesAnimType1Swap = namesAnimType1.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    public static final Map<String, Integer> namesLoopTypeSwap = namesLoopType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    public static final Map<String, Integer> namesDoorSoundSwap = namesDoorSound.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    public static final Map<String, Integer> namesNumAnimsSwap = namesNumAnims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    public static final Map<String, Integer> namesAnimType2Swap = namesAnimType2.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));


    private ArrayList<Integer> animIDs = new ArrayList<>();
    private int animType1;//second byte: FF=no animation, 00=loop, 02=trigger(?), 03=trigger, 08=day/night cycle
    private int loopType; //fourth byte: 00=Loop, 01=Trigger

    private int doorSound; //first byte; 00, 01, 02, 03
    private int numAnims; //third byte; Num animations (?)
    private int animType2; //fourth byte: 00=no animation, 01=loop animation, 02=trigger animation

    public BuildAnimInfoHGSS() {
        animType1 = 0xFF;
        loopType = 0;

        doorSound = 0;
        numAnims = 0;
        animType2 = 0;
    }

    public BuildAnimInfoHGSS(byte[] data) throws Exception {
        animType1 = BinaryReader.readUInt8(data, 1);
        loopType = BinaryReader.readUInt8(data, 3);

        doorSound = BinaryReader.readUInt8(data, 4);
        numAnims = BinaryReader.readUInt8(data, 6);
        animType2 = BinaryReader.readUInt8(data, 7);

        for (int i = 0; i < MAX_ANIMS_PER_BUILDING; i++) {
            int anim = (int) BinaryReader.readUInt32(data, i * MAX_ANIMS_PER_BUILDING + 8);
            if (anim != 0xFFFFFFFF) {
                animIDs.add(anim);
            }
        }
    }

    public byte[] toByteArray() throws Exception {
        byte[] data = new byte[24];

        if (animIDs.size() > 0) {//Has animation
            BinaryWriter.writeUInt8(data, 0, 0x01);
        } else {
            BinaryWriter.writeUInt8(data, 0, 0xFF);
        }
        BinaryWriter.writeUInt8(data, 1, animType1);
        BinaryWriter.writeUInt8(data, 3, loopType);
        BinaryWriter.writeUInt8(data, 4, doorSound);
        BinaryWriter.writeUInt8(data, 6, numAnims);
        BinaryWriter.writeUInt8(data, 7, animType2);

        int i;
        for (i = 0; i < animIDs.size(); i++) {
            BinaryWriter.writeUInt32(data, 8 + i * MAX_ANIMS_PER_BUILDING, animIDs.get(i));
        }
        for (; i < MAX_ANIMS_PER_BUILDING; i++) {
            BinaryWriter.writeUInt32(data, 8 + i * MAX_ANIMS_PER_BUILDING, 0xFFFFFFFF);
        }
        return data;
    }

    public ArrayList<Integer> getAnimIDs() {
        return animIDs;
    }

    public void setAnimIDs(ArrayList<Integer> animIDs) {
        this.animIDs = animIDs;
    }

    public int getAnimType1() {
        return animType1;
    }

    public int getDoorSound() {
        return doorSound;
    }

    public int getAnimType2() {
        return animType2;
    }

    public int getNumAnims() {
        return numAnims;
    }

    public int getLoopType() {
        return loopType;
    }

    
    
    /*
    public BuildAnimInfoHGSS() {
        animType1 = -128; //0xFF
        loopType = 0;

        unknown1 = 0;
        numAnimations = 0;
        animType2 = 0;
    }

    public BuildAnimInfoHGSS(byte[] data) throws Exception {
        animType1 = data[1];
        loopType = data[3];

        unknown1 = data[4];
        numAnimations = data[6];
        numAnimations = data[7];

        for (int i = 0; i < MAX_ANIMS_PER_BUILDING; i++) {
            int anim = (int) BinaryReader.readUInt32(data, i * MAX_ANIMS_PER_BUILDING + 8);
            if (anim != 0xFFFFFFFF) {
                animIDs.add(anim);
            }
        }
    }

    public byte[] toByteArray() throws Exception {
        byte[] data = new byte[24];

        if (animIDs.size() > 0) {//Has animation
            BinaryWriter.writeUInt8(data, 0, 0x01);
        } else {
            BinaryWriter.writeUInt8(data, 0, 0xFF);
        }
        data[1] = animType1;
        data[3] = loopType;
        data[4] = unknown1;
        data[6] = numAnimations;
        data[7] = animType2;

        int i;
        for (i = 0; i < animIDs.size(); i++) {
            BinaryWriter.writeUInt32(data, 8 + i * MAX_ANIMS_PER_BUILDING, animIDs.get(i));
        }
        for (; i < MAX_ANIMS_PER_BUILDING; i++) {
            BinaryWriter.writeUInt32(data, 8 + i * MAX_ANIMS_PER_BUILDING, 0xFFFFFFFF);
        }
        return data;
    }

    public ArrayList<Integer> getAnimIDs() {
        return animIDs;
    }

    public void setAnimIDs(ArrayList<Integer> animIDs) {
        this.animIDs = animIDs;
    }

    public byte getAnimType1() {
        return animType1;
    }

    public byte getUnknown1() {
        return unknown1;
    }

    public byte getAnimType2() {
        return animType2;
    }

    public byte getNumAnimations() {
        return numAnimations;
    }

    public byte getLoopType() {
        return loopType;
    }*/

    public void setAnimType1(int animType1) {
        this.animType1 = animType1;
    }

    public void setLoopType(int loopType) {
        this.loopType = loopType;
    }

    public void setDoorSound(int doorSound) {
        this.doorSound = doorSound;
    }

    public void setNumAnims(int numAnimations) {
        this.numAnims = numAnimations;
    }

    public void setAnimType2(int animType2) {
        this.animType2 = animType2;
    }
}
