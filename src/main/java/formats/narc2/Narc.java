
package formats.narc2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.Utils.MutableInt;

public class Narc {

    private final NarcFolder root;

    public Narc(NarcFolder root) {
        this.root = root;
    }

    public boolean hasNamedFiles() {
        return hasNamedFiles(root);
    }

    private boolean hasNamedFiles(NarcFolder folder) {
        return folder.getFiles().stream().anyMatch(file -> file.getName() != null && !file.getName().equals(""))
                || folder.getSubfolders().stream().anyMatch(this::hasNamedFiles);
    }

    public NarcFolder getRoot() {
        return root;
    }

    public void calculateIndices() {
        root.setID(0);
        calculateIndices(root, new MutableInt(0));
    }

    private static void calculateIndices(NarcFolder folder, MutableInt lastID) {
        for (NarcFolder subfolder : folder.getSubfolders()) {
            subfolder.setID(++lastID.value);
            calculateIndices(subfolder, lastID);
        }
    }

    public int getNumDirectories() {
        MutableInt sum = new MutableInt(1);
        getNumDirectories(root.getSubfolders(), sum);
        return sum.value;
    }

    private static void getNumDirectories(List<NarcFolder> subfolders, MutableInt sum) {
        sum.value += subfolders.size();
        for (NarcFolder subfolder : subfolders) {
            getNumDirectories(subfolder.getSubfolders(), sum);
        }
    }

    public List<NarcFile> getAllFiles() {
        List<NarcFile> files = new ArrayList<>();
        addFolderFiles(files, root);
        return files;
    }

    private static void addFolderFiles(List<NarcFile> files, NarcFolder folder) {
        files.addAll(folder.getFiles());
        for (NarcFolder subfolder : folder.getSubfolders()) {
            addFolderFiles(files, subfolder);
        }
    }

    public List<NarcFolder> getAllFolders() {
        ArrayList<NarcFolder> folders = new ArrayList<>();
        folders.add(root);
        addFolderSubfolders(folders, root);
        return folders;
    }

    private static void addFolderSubfolders(List<NarcFolder> folders, NarcFolder folder) {
        for (NarcFolder subfolder : folder.getSubfolders()) {
            folders.add(subfolder);
            addFolderSubfolders(folders, subfolder);
        }
    }

    public NarcFile getFileByPath(String path){
        return getFileByPath(path.split(File.separator));
    }

    public NarcFile getFileByPath(String[] splitPath){
        NarcFolder nextFolder = root;
        for(int i = 0; i < splitPath.length - 1; i++){
            nextFolder = nextFolder.getFolderByName(splitPath[i]);
            if(nextFolder == null){
                return null;
            }
        }
        return nextFolder.getFileByName(splitPath[splitPath.length - 1]);
    }
}
