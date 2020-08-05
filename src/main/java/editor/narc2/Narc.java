/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.narc2;

import java.util.ArrayList;
import utils.Utils.MutableInt;

public class Narc {

    private NarcFolder root;

    public Narc(NarcFolder root) {
        this.root = root;
    }

    public boolean hasNamedFiles(){
        return hasNamedFiles(root);
    }
    
    private boolean hasNamedFiles(NarcFolder folder){
        for(NarcFile file : folder.getFiles()){
            if(file.getName() != null){
                if(!file.getName().equals("")){
                    return true;
                }
            }
        }
        for(NarcFolder subfolder : folder.getSubfolders()){
            if(hasNamedFiles(subfolder)){
                return true;
            }
        }
        return false;
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

    private static void getNumDirectories(ArrayList<NarcFolder> subfolders, MutableInt sum) {
        sum.value += subfolders.size();
        for (NarcFolder subfolder : subfolders) {
            getNumDirectories(subfolder.getSubfolders(), sum);
        }
    }

    public ArrayList<NarcFile> getAllFiles() {
        ArrayList<NarcFile> files = new ArrayList<>();
        addFolderFiles(files, root);
        return files;
    }

    private static void addFolderFiles(ArrayList<NarcFile> files, NarcFolder folder) {
        for (NarcFile file : folder.getFiles()) {
            files.add(file);
        }
        for (NarcFolder subfolder : folder.getSubfolders()) {
            addFolderFiles(files, subfolder);
        }
    }

    public ArrayList<NarcFolder> getAllFolders() {
        ArrayList<NarcFolder> folders = new ArrayList<>();
        folders.add(root);
        addFolderSubfolders(folders, root);
        return folders;
    }

    private static void addFolderSubfolders(ArrayList<NarcFolder> folders, NarcFolder folder) {
        for (NarcFolder subfolder : folder.getSubfolders()) {
            folders.add(subfolder);
            addFolderSubfolders(folders, subfolder);
        }
    }

}
