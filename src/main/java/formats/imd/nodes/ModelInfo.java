
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;

import java.util.List;

/**
 * @author Trifindo
 */
public class ModelInfo extends ImdNode {

    public ModelInfo(int posScale, int materialSize) {
        super("model_info");
        attributes = List.of(
                new ImdAttribute("pos_scale", posScale), 
                new ImdAttribute("scaling_rule", "standard"), 
                new ImdAttribute("vertex_style", "direct"), 
                new ImdAttribute("magnify", 1), 
                new ImdAttribute("tool_start_frame", 1), 
                new ImdAttribute("tex_matrix_mode", "maya"), 
                new ImdAttribute("compress_node", "merge"), 
                new ImdAttribute("node_size", new int[]{1, 1}),  //TODO: Check this
                new ImdAttribute("compress_material", true), 
                new ImdAttribute("material_size", new int[]{materialSize, materialSize}), 
                new ImdAttribute("output_texture", "used"), 
                new ImdAttribute("force_full_weight", false), 
                new ImdAttribute("use_primitive_strip", true));
    }
}
