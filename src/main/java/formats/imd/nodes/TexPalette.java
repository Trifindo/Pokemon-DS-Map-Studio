
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;
import formats.nsbtx2.NsbtxPalette;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class TexPalette extends ImdNode {

    public TexPalette(int index, String name, int colorSize, String content) {
        super("tex_palette");

        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("index", index));
                add(new ImdAttribute("name", name));
                add(new ImdAttribute("color_size", colorSize));
            }
        };

        this.content = content;
    }

    public TexPalette(int index, NsbtxPalette pal) {
        this(index, pal.getName(), pal.getDataSizeImd(), pal.getDataAsHexStringImd());
    }


}
