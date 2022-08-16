
package editor.buildingeditor2.animations;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcFolder;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class BuildAnimeListHGSS {


    //private ArrayList<ArrayList<Integer>> animations;
    //private ArrayList<Byte> secondBytes;

    private ArrayList<BuildAnimInfoHGSS> animationConfigFiles;

    public BuildAnimeListHGSS(Narc narc) {
        NarcFolder root = narc.getRoot();
        animationConfigFiles = new ArrayList<>(root.getFiles().size());

        System.out.println("File " + root.getName() + " has been read.");

        for (NarcFile file : root.getFiles()) {
            try {
                animationConfigFiles.add(new BuildAnimInfoHGSS(file.getData()));
            } catch (Exception ex) {
                animationConfigFiles.add(new BuildAnimInfoHGSS());
            }
        }
    }

    public Narc toNarc() throws Exception {
        NarcFolder root = new NarcFolder();

        ArrayList<NarcFile> files = new ArrayList<>(animationConfigFiles.size());
        for (BuildAnimInfoHGSS anim : animationConfigFiles) {
            files.add(new NarcFile("", root, anim.toByteArray()));
        }
        root.setFiles(files);
        return new Narc(root);
    }

    /*
    private byte[] animationToByteArray(int animationIndex) throws Exception {
        byte[] data = new byte[4 + MAX_ANIMS_PER_BUILDING * 4];
        if (animations.get(animationIndex).isEmpty()) {
            BinaryWriter.writeUInt16(data, 0, 0xFFFF);
        } else {
            BinaryWriter.writeUInt8(data, 0, 1);
            BinaryWriter.writeUInt8(data, 1, secondBytes.get(animationIndex));
        }
        BinaryWriter.writeUInt16(data, 2, 0);//Padding?
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
                    int anim = (int) BinaryReader.readUInt32(data, (i + 2) * MAX_ANIMS_PER_BUILDING);
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
    }*/

    public void addBuildingAnimation(ArrayList<Integer> newAnimations, byte secondByte) {
        BuildAnimInfoHGSS anim = new BuildAnimInfoHGSS();
        anim.setAnimIDs(newAnimations);
        animationConfigFiles.add(anim);
    }

    public void addBuildingAnimation(int buildIndex, int animationIndex) {
        if (buildIndex >= 0 && buildIndex < animationConfigFiles.size()) {
            ArrayList<Integer> buildAnimations = animationConfigFiles.get(buildIndex).getAnimIDs();
            if (buildAnimations.size() < BuildAnimInfoHGSS.MAX_ANIMS_PER_BUILDING) {
                buildAnimations.add(animationIndex);
            }
        }
    }

    public void removeBuildingAnimation(int buildIndex, int animationIndex) {
        if (buildIndex >= 0 && buildIndex < animationConfigFiles.size()) {
            ArrayList<Integer> buildAnimations = animationConfigFiles.get(buildIndex).getAnimIDs();
            if (!buildAnimations.isEmpty()) {
                buildAnimations.remove(animationIndex);
                if (buildAnimations.isEmpty()) {
                    animationConfigFiles.get(buildIndex).setAnimIDs(new ArrayList<>());
                }
            }
        }
    }

    public void replaceBuildingAnimation(int buildIndex, int animationIndex, int oldAnimationIndex) {
        if (buildIndex >= 0 && buildIndex < animationConfigFiles.size()) {
            ArrayList<Integer> buildAnimations = animationConfigFiles.get(buildIndex).getAnimIDs();
            if (oldAnimationIndex >= 0 && oldAnimationIndex < buildAnimations.size()) {
                buildAnimations.set(oldAnimationIndex, animationIndex);
            }
        }
    }

    public void replaceBuildingAnimationIDs(int index, ArrayList<Integer> newAnimations) {
        if (index >= 0 && index < animationConfigFiles.size()) {
            animationConfigFiles.get(index).setAnimIDs(newAnimations);
        }
    }

    public void removeBuildingAnimation(int index) {
        if (index >= 0 && index < animationConfigFiles.size()) {
            animationConfigFiles.remove(index);
        }
    }

    public ArrayList<BuildAnimInfoHGSS> getAnimationConfigFiles() {
        return animationConfigFiles;
    }

}
