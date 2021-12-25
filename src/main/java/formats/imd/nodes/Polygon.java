
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;

import java.util.List;

/**
 * @author Trifindo
 */
public class Polygon extends ImdNode {

    public Polygon(int index, String name, int vertexSize, int polygonSize,
                   int triangleSize, int quadSize, float[] volumeMin, float volume_r,
                   int mtxPrimSize, boolean useVertexColors) {
        super("polygon");

        attributes = List.of(
                new ImdAttribute("index", index), 
                new ImdAttribute("name", name), 
                new ImdAttribute("vertex_size", vertexSize), 
                new ImdAttribute("polygon_size", polygonSize), 
                new ImdAttribute("triangle_size", triangleSize), 
                new ImdAttribute("quad_size", quadSize), 
                new ImdAttribute("volume_min", volumeMin), 
                new ImdAttribute("volume_max", volumeMin), 
                new ImdAttribute("volume_r", volume_r), 
                new ImdAttribute("mtx_prim_size", mtxPrimSize), 
                new ImdAttribute("nrm_flag", true), 
                new ImdAttribute("clr_flag", useVertexColors), 
                new ImdAttribute("tex_flag", true));
    }
}
