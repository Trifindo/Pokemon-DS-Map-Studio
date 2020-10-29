
package editor.imd.nodes;

import editor.imd.ImdAttribute;
import editor.imd.ImdNode;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class ModelInfo extends ImdNode {

    public ModelInfo(int posScale, int materialSize) {
        super("model_info");
        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("pos_scale", posScale));
                add(new ImdAttribute("scaling_rule", "standard"));
                add(new ImdAttribute("vertex_style", "direct"));
                add(new ImdAttribute("magnify", 1));
                add(new ImdAttribute("tool_start_frame", 1));
                add(new ImdAttribute("tex_matrix_mode", "maya"));
                add(new ImdAttribute("compress_node", "merge"));
                add(new ImdAttribute("node_size", new int[]{1, 1})); //TODO: Check this
                add(new ImdAttribute("compress_material", true));
                add(new ImdAttribute("material_size", new int[]{materialSize, materialSize}));
                add(new ImdAttribute("output_texture", "used"));
                add(new ImdAttribute("force_full_weight", false));
                add(new ImdAttribute("use_primitive_strip", true));
            }
        };
    }

}
