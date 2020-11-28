
package editor.buildingeditor2.animations;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcFolder;

import java.util.ArrayList;

import utils.BinaryReader;
import utils.BinaryWriter;

/**
 * @author Trifindo
 */
public class BuildAnimeListDPPt {

    public static final int MAX_ANIMS_PER_BUILDING = 4;
    private ArrayList<ArrayList<Integer>> animations;
    private ArrayList<Byte> secondBytes;
    private ArrayList<Boolean> slopeAnimations;

    public BuildAnimeListDPPt(Narc narc) {
        NarcFolder root = narc.getRoot();
        animations = new ArrayList<>(root.getFiles().size());
        secondBytes = new ArrayList<>(root.getFiles().size());
        slopeAnimations = new ArrayList<>(root.getFiles().size());
        for (NarcFile file : root.getFiles()) {
            animations.add(loadAnimation(file.getData()));
            secondBytes.add(file.getData()[1]);
            slopeAnimations.add(file.getData()[2] == 1);
        }
    }

    public Narc toNarc() throws Exception {
        NarcFolder root = new NarcFolder();

        ArrayList<NarcFile> files = new ArrayList<>(animations.size());
        for (int i = 0; i < animations.size(); i++) {
            files.add(new NarcFile("", root, animationToByteArray(i)));
        }
        root.setFiles(files);
        return new Narc(root);
    }

    private byte[] animationToByteArray(int animationIndex) throws Exception {
        byte[] data = new byte[4 + MAX_ANIMS_PER_BUILDING * 4];
        if (animations.get(animationIndex).isEmpty()) {
            BinaryWriter.writeUInt16(data, 0, 0xFFFF);
        } else {
            BinaryWriter.writeUInt8(data, 0, 1);
            BinaryWriter.writeUInt8(data, 1, secondBytes.get(animationIndex));
            if(slopeAnimations.get(animationIndex)){
                BinaryWriter.writeUInt8(data, 2, 1);
            }
        }
        //BinaryWriter.writeUInt16(data, 2, 0);//Padding?
        int i;
        for (i = 0; i < animations.get(animationIndex).size(); i++) {
            BinaryWriter.writeUInt32(data, 4 + i * 4, animations.get(animationIndex).get(i));
        }
        for (; i < MAX_ANIMS_PER_BUILDING; i++) {
            BinaryWriter.writeUInt32(data, 4 + i * 4, 0xFFFFFFFF);
        }
        return data;
    }

    private static ArrayList<Integer> loadAnimation(byte[] data) {
        ArrayList<Integer> animationIndices;
        try {
            if (BinaryReader.readUInt8(data, 0) != 255) { //Has animation
                animationIndices = new ArrayList<>();
                for (int i = 0; i < MAX_ANIMS_PER_BUILDING; i++) {
                    int anim = (int) BinaryReader.readUInt32(data, (i + 1) * MAX_ANIMS_PER_BUILDING);
                    if (anim != 0xFFFFFFFF) {
                        animationIndices.add(anim);
                    } else {
                        if (animationIndices.isEmpty()) {
                            return new ArrayList<>();
                        }
                        break;
                    }
                }
                return animationIndices;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void addBuildingAnimation(ArrayList<Integer> newAnimations, byte secondByte, boolean slopeAnimation) {
        animations.add(newAnimations);
        secondBytes.add(secondByte);
        slopeAnimations.add(slopeAnimation);
    }

    public void addBuildingAnimation(int buildIndex, int animationIndex) {
        if (buildIndex >= 0 && buildIndex < animations.size()) {
            ArrayList<Integer> buildAnimations = animations.get(buildIndex);
            if (buildAnimations.size() < MAX_ANIMS_PER_BUILDING) {
                buildAnimations.add(animationIndex);
            }
        }
    }

    public void removeBuildingAnimation(int buildIndex, int animationIndex) {
        if (buildIndex >= 0 && buildIndex < animations.size()) {
            ArrayList<Integer> buildAnimations = animations.get(buildIndex);
            if (!buildAnimations.isEmpty()) {
                buildAnimations.remove(animationIndex);
                if (buildAnimations.isEmpty()) {
                    animations.set(buildIndex, new ArrayList<>());
                }
            }
        }
    }

    public void replaceBuildingAnimation(int buildIndex, int animationIndex, int oldAnimationIndex) {
        if (buildIndex >= 0 && buildIndex < animations.size()) {
            ArrayList<Integer> buildAnimations = animations.get(buildIndex);
            if (oldAnimationIndex >= 0 && oldAnimationIndex < buildAnimations.size()) {
                buildAnimations.set(oldAnimationIndex, animationIndex);
            }
        }
    }

    public void replaceBuildingAnimation(int index, ArrayList<Integer> newAnimations, byte secondByte, boolean slopeAnimation) {
        if (index >= 0 && index < animations.size()) {
            animations.set(index, newAnimations);
            secondBytes.set(index, secondByte);
            slopeAnimations.set(index, slopeAnimation);
        }
    }

    public void removeBuildingAnimation(int index) {
        if (index >= 0 && index < animations.size()) {
            animations.remove(index);
            secondBytes.remove(index);
            slopeAnimations.remove(index);
        }
    }

    public ArrayList<ArrayList<Integer>> getAnimations() {
        return animations;
    }

    public ArrayList<Byte> getSecondBytes() {
        return secondBytes;
    }

    public ArrayList<Boolean> getSlopeAnimations(){return slopeAnimations; }

}
