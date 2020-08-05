/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.imd.nodes;

import editor.imd.ImdAttribute;
import editor.imd.ImdNode;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class Polygon extends ImdNode{
    
    public Polygon(int index, String name, int vertexSize, int polygonSize,
            int triangleSize, int quadSize, float[] volumeMin, float volume_r,
            int mtxPrimSize, boolean useVertexColors) {
        super("polygon");
        
        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("index", index));
                add(new ImdAttribute("name", name));
                add(new ImdAttribute("vertex_size", vertexSize));
                add(new ImdAttribute("polygon_size", polygonSize));
                add(new ImdAttribute("triangle_size", triangleSize));
                add(new ImdAttribute("quad_size", quadSize));
                add(new ImdAttribute("volume_min", volumeMin));
                add(new ImdAttribute("volume_max", volumeMin));
                add(new ImdAttribute("volume_r", volume_r));
                add(new ImdAttribute("mtx_prim_size", mtxPrimSize));
                add(new ImdAttribute("nrm_flag", true));
                add(new ImdAttribute("clr_flag", useVertexColors));
                add(new ImdAttribute("tex_flag", true));
            }
        };
        
        
        
        
    }
    
}
