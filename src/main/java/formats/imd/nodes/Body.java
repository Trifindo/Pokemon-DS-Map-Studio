
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;

import java.util.List;

/**
 * @author Trifindo
 */
public class Body extends ImdNode {

    public Body() {
        super("body");

        ImdNode snOriginalCreate = new ImdNode("original_create");
        snOriginalCreate.attributes = List.of(
                new ImdAttribute("user", "unknown"), 
                new ImdAttribute("host", "unknown"), 
                new ImdAttribute("date", "2019-01-11T13:52:52"), 
                new ImdAttribute("source", "untitled"));
        subnodes.add(snOriginalCreate);

        ImdNode snOriginalGenerator = new ImdNode("original_generator");
        snOriginalGenerator.attributes = List.of(
                new ImdAttribute("name", "Pokemon DS Map Studio"), 
                new ImdAttribute("version", "1.0"));
        subnodes.add(snOriginalGenerator);
    }
}
