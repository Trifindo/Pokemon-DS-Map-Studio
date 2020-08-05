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
public class BoxTest extends ImdNode{
    
    public BoxTest(int posScale, float[] xyz, float[] whd) {
        super("box_test");
        System.out.println("Box test xyz: " + xyz[0] + " "+ xyz[1] + " "+ xyz[2]);
        System.out.println("Box test whd: " + whd[0] + " "+ whd[1] + " "+ whd[2]);
        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("pos_scale", posScale));
                //add(new ImdAttribute("xyz", new float[]{-2.0f, -0.3125f, -3.0f}));
                //add(new ImdAttribute("whd", new float[]{4.0f, 0.75f, 5.0f}));
                //add(new ImdAttribute("xyz", new float[]{-3.0f, -0.3125f, -3.0f}));
                //add(new ImdAttribute("whd", new float[]{6.0f, 0.75f, 6.0f}));
                add(new ImdAttribute("xyz", xyz));
                add(new ImdAttribute("whd", whd));
            }
        };
    }
    
}
