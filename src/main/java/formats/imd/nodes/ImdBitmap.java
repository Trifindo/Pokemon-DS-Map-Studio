
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;
import formats.imd.ImdTextureIndexed;
import formats.nsbtx2.NsbtxTexture;

import java.util.List;

/**
 * @author Trifindo
 */
public class ImdBitmap extends ImdNode {

    public ImdBitmap(ImdTextureIndexed imdTexture) {
        super("bitmap");

        attributes = List.of(new ImdAttribute("size", imdTexture.getTextureDataSize()));

        content = imdTexture.getTexDataAsHexString();
    }

    public ImdBitmap(NsbtxTexture texture) {
        super("bitmap");

        attributes = List.of(new ImdAttribute("size", texture.getDataSizeImd()));

        content = texture.getDataAsHexStringImd();
    }

    public ImdBitmap(int size) {
        super("bitmap");

        attributes = List.of(new ImdAttribute("size", size));

        //TODO: change this
        StringBuilder pixels = new StringBuilder();
        for (int i = 0; i < size / 8; i++) {
            for (int j = 0; j < 7; j++) {
                pixels.append("0000 ");
            }
            pixels.append("0000 ");
        }
        content = pixels.toString();
    }
}
