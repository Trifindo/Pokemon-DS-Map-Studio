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
public class Head extends ImdNode {

    public Head() {
        super("head");
        
        ImdNode snCreate = new ImdNode("create");
        snCreate.attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("user", "unknown"));
                add(new ImdAttribute("host", "unknown"));
                add(new ImdAttribute("date", "2019-01-11T13:52:52"));
                add(new ImdAttribute("source", "untitled"));
            }
        };
        subnodes.add(snCreate);
        
        ImdNode snTitle = new ImdNode("title");
        snTitle.content = "Model Data for NINTENDO NITRO-System";
        subnodes.add(snTitle);
        
        ImdNode snGenerator = new ImdNode("generator");
        snGenerator.attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("name", "Pokemon DS Map Studio"));
                add(new ImdAttribute("version", "1.0"));
            }
        };
        subnodes.add(snGenerator);

    }

}
