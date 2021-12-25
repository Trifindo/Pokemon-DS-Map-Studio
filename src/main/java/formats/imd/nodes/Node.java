
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;

import java.util.List;

/**
 * @author Trifindo
 */
public class Node extends ImdNode {

    public Node(int index, int displaySize, int vertexSize, int polygonSize,
                int triangleSize, int quadSize, List<Integer> textureIDs) {
        super("node");

        attributes = List.of(
                new ImdAttribute("index", index), 
                new ImdAttribute("name", "world_root"), 
                new ImdAttribute("kind", "mesh"), 
                new ImdAttribute("parent", -1), 
                new ImdAttribute("child", -1), 
                new ImdAttribute("brother_next", -1), 
                new ImdAttribute("brother_prev", -1), 
                new ImdAttribute("draw_mtx", true), 
                new ImdAttribute("billboard", false), 
                new ImdAttribute("scale", new float[]{1.0f, 1.0f, 1.0f}), 
                new ImdAttribute("rotate", new float[]{0.0f, 0.0f, 0.0f}), 
                new ImdAttribute("translate", new float[]{0.0f, 0.0f, 0.0f}), 
                new ImdAttribute("visibility", true), 
                new ImdAttribute("display_size", displaySize), 
                new ImdAttribute("vertex_size", vertexSize), 
                new ImdAttribute("polygon_size", polygonSize), 
                new ImdAttribute("triangle_size", triangleSize), 
                new ImdAttribute("quad_size", quadSize), 
                new ImdAttribute("volume_min", new float[]{0.0f, 0.0f, 0.0f}), 
                new ImdAttribute("volume_max", new float[]{0.0f, 0.0f, 0.0f}), 
                new ImdAttribute("volume_r", 0.0f));

        for (int i = 0; i < displaySize; i++) {
            ImdNode display = new ImdNode("display");
            display.attributes.add(new ImdAttribute("index", i));
            display.attributes.add(new ImdAttribute("material", textureIDs.get(i)));
            display.attributes.add(new ImdAttribute("polygon", i));
            display.attributes.add(new ImdAttribute("priority", 0));
            subnodes.add(display);
        }
    }
}
