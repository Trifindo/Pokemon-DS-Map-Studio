
package editor.imd.nodes;

import editor.imd.ImdAttribute;
import editor.imd.ImdNode;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class Node extends ImdNode {

    public Node(int index, int displaySize, int vertexSize, int polygonSize,
                int triangleSize, int quadSize, ArrayList<Integer> textureIDs) {
        super("node");

        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("index", index));
                add(new ImdAttribute("name", "world_root"));
                add(new ImdAttribute("kind", "mesh"));
                add(new ImdAttribute("parent", -1));
                add(new ImdAttribute("child", -1));
                add(new ImdAttribute("brother_next", -1));
                add(new ImdAttribute("brother_prev", -1));
                add(new ImdAttribute("draw_mtx", true));
                add(new ImdAttribute("billboard", false));
                add(new ImdAttribute("scale", new float[]{1.0f, 1.0f, 1.0f}));
                add(new ImdAttribute("rotate", new float[]{0.0f, 0.0f, 0.0f}));
                add(new ImdAttribute("translate", new float[]{0.0f, 0.0f, 0.0f}));
                add(new ImdAttribute("visibility", true));
                add(new ImdAttribute("display_size", displaySize));
                add(new ImdAttribute("vertex_size", vertexSize));
                add(new ImdAttribute("polygon_size", polygonSize));
                add(new ImdAttribute("triangle_size", triangleSize));
                add(new ImdAttribute("quad_size", quadSize));
                add(new ImdAttribute("volume_min", new float[]{0.0f, 0.0f, 0.0f}));
                add(new ImdAttribute("volume_max", new float[]{0.0f, 0.0f, 0.0f}));
                add(new ImdAttribute("volume_r", 0.0f));
            }
        };

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
