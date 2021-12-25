
package formats.imd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trifindo
 */
public class ImdNode {
    public String nodeName;
    public List<ImdAttribute> attributes;
    public String content;
    public List<ImdNode> subnodes;

    public ImdNode(String nodeName) {
        this.nodeName = nodeName;
        subnodes = new ArrayList<>();
        attributes = new ArrayList<>();
        content = "";
    }

    public ImdAttribute getAttribute(String tag) {
        return attributes.stream()
                .filter(p -> p.tag.equals(tag))
                .findFirst().orElse(null);
    }
}
