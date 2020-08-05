/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.dae;

/**
 *
 * @author Trifindo
 */
public class DaeImage extends DaeNode {

    private String fileName;

    public DaeImage(String id, String name) {
        super(id, name);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
    
    
}
