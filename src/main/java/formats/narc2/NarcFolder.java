
package formats.narc2;

import java.util.ArrayList;
import java.util.List;

public class NarcFolder {

    private String name = "";
    private NarcFolder parent;
    private List<NarcFolder> subfolders = new ArrayList<>();
    private List<NarcFile> files = new ArrayList<>();
    private int ID;

    public NarcFolder() {
        name = "";
    }

    public NarcFolder(NarcFolder parent) {
        this.parent = parent;
    }

    public NarcFolder(String name, NarcFolder parent) {
        this.name = name;
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(NarcFolder parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public NarcFolder getParent() {
        return parent;
    }

    public List<NarcFolder> getSubfolders() {
        return subfolders;
    }

    public List<NarcFile> getFiles() {
        return files;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setFiles(List<NarcFile> files) {
        this.files = files;
    }

    public NarcFolder getFolderByName(String folderName){
        return subfolders.stream()
                .filter(folder -> folder.getName().equals(folderName))
                .findFirst().orElse(null);
    }

    public NarcFile getFileByName(String fileName){
        return files.stream()
                .filter(file -> file.getName().equals(fileName))
                .findFirst().orElse(null);
    }
}
