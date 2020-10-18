
package editor.imd.nodes;

import editor.imd.ImdAttribute;
import editor.imd.ImdNode;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class Body extends ImdNode{
    
    public Body() {
        super("body");
        
        ImdNode snOriginalCreate = new ImdNode("original_create");
        snOriginalCreate.attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("user", "unknown"));
                add(new ImdAttribute("host", "unknown"));
                add(new ImdAttribute("date", "2019-01-11T13:52:52"));
                add(new ImdAttribute("source", "untitled"));
            }
        };
        subnodes.add(snOriginalCreate);
        
        ImdNode snOriginalGenerator = new ImdNode("original_generator");
        snOriginalGenerator.attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("name", "Pokemon DS Map Studio"));
                add(new ImdAttribute("version", "1.0"));
            }
        };
        subnodes.add(snOriginalGenerator);
    }
    
}
