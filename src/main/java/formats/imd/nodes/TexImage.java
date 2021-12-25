
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;

import java.util.List;

import formats.imd.ImdTextureIndexed;
import formats.nsbtx2.Nsbtx2;
import formats.nsbtx2.NsbtxTexture;

/**
 * @author Trifindo
 */
public class TexImage extends ImdNode {

    private static final String[] colorFormatTable = new String[]{
            "palette4",
            "palette16",
            "palette256",
            "a3i5",
            "a5i3"
    };

    public TexImage(int index, String name, String paletteName, ImdTextureIndexed imdTexture, int colorFormat, String path) {
        super("tex_image");

        String color0mode = imdTexture.isTransparent() ? "transparency" : "color";

        attributes = List.of(
                new ImdAttribute("index", index), 
                new ImdAttribute("name", name), 
                new ImdAttribute("width", imdTexture.width), 
                new ImdAttribute("height", imdTexture.height), 
                new ImdAttribute("original_width", imdTexture.width), 
                new ImdAttribute("original_height", imdTexture.height), 
                new ImdAttribute("format", colorFormatTable[colorFormat]), 
                new ImdAttribute("color0_mode", color0mode), 
                new ImdAttribute("palette_name", paletteName), 
                new ImdAttribute("path", path));

        subnodes.add(new ImdBitmap(imdTexture));
        //subnodes.add(new ImdBitmap((width * height) / 4));
    }

    public TexImage(int index, NsbtxTexture texture, String path) {
        super("tex_image");

        String color0mode = texture.isTransparent() ? "transparency" : "color";

        attributes = List.of(
                new ImdAttribute("index", index), 
                new ImdAttribute("name", texture.getName()), 
                new ImdAttribute("width", texture.getWidth()), 
                new ImdAttribute("height", texture.getHeight()), 
                new ImdAttribute("original_width", texture.getWidth()), 
                new ImdAttribute("original_height", texture.getHeight()), 
                new ImdAttribute("format", Nsbtx2.formatNames[texture.getColorFormat()]), 
                new ImdAttribute("color0_mode", color0mode), 
                new ImdAttribute("palette_name", ""), 
                new ImdAttribute("path", path));

        subnodes.add(new ImdBitmap(texture));
        //subnodes.add(new ImdBitmap((width * height) / 4));
    }
}
