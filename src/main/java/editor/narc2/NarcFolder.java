/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.narc2;

import java.util.ArrayList;

public class NarcFolder {
    
    private String name = "";
    private NarcFolder parent;
    private ArrayList<NarcFolder> subfolders = new ArrayList<>();
    private ArrayList<NarcFile> files = new ArrayList<>();
    private int ID;
    
    public NarcFolder(){
        name = "";
    }
    
    public NarcFolder(NarcFolder parent){
        this.parent = parent;
    }
    
    public NarcFolder(String name, NarcFolder parent){
        this.name = name;
        this.parent = parent;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setParent(NarcFolder parent){
        this.parent = parent;
    }
    
    public String getName(){
        return name;
    }
    
    public NarcFolder getParent(){
        return parent;
    }
    
    public ArrayList<NarcFolder> getSubfolders(){
        return subfolders;
    }
    
    public ArrayList<NarcFile> getFiles(){
        return files;
    }
    
    public void setID(int ID){
        this.ID = ID;
    }
    
    public int getID(){
        return ID;
    }
    
    public void setFiles(ArrayList<NarcFile> files){
        this.files = files;
    }
    
}
