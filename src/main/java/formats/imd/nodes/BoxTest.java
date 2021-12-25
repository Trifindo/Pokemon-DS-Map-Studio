
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;

import java.util.List;

/**
 * @author Trifindo
 */
public class BoxTest extends ImdNode {

    public BoxTest(int posScale, float[] xyz, float[] whd) {
        super("box_test");
        //System.out.println("Box test xyz: " + xyz[0] + " "+ xyz[1] + " "+ xyz[2]);
        //System.out.println("Box test whd: " + whd[0] + " "+ whd[1] + " "+ whd[2]);
        attributes = List.of(
                new ImdAttribute("pos_scale", posScale), 
                //new ImdAttribute("xyz", new float[]{-2.0f, -0.3125f, -3.0f}), 
                //new ImdAttribute("whd", new float[]{4.0f, 0.75f, 5.0f}), 
                //new ImdAttribute("xyz", new float[]{-3.0f, -0.3125f, -3.0f}), 
                //new ImdAttribute("whd", new float[]{6.0f, 0.75f, 6.0f}), 
                new ImdAttribute("xyz", xyz), 
                new ImdAttribute("whd", whd));
    }
}
