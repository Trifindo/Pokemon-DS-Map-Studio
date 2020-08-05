/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.dae;

import java.util.List;

/**
 *
 * @author Trifindo
 */
public class DaeGeometry extends DaeNode {
    
    private List<DaeMesh> meshes;
    
    public DaeGeometry(String id, String name) {
        super(id, name);
    }
    
}
