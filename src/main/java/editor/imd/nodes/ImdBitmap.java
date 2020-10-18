
package editor.imd.nodes;

import editor.imd.ImdAttribute;
import editor.imd.ImdNode;
import editor.imd.ImdTexture;
import editor.imd.ImdTextureIndexed;
import editor.nsbtx2.NsbtxTexture;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class ImdBitmap extends ImdNode{
    
    public ImdBitmap(ImdTextureIndexed imdTexture){
        super("bitmap");
        
        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("size", imdTexture.getTextureDataSize()));
            }
        };
        
        content = imdTexture.getTexDataAsHexString();
    }
    
    public ImdBitmap(NsbtxTexture texture){
        super("bitmap");
        
        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("size", texture.getDataSizeImd()));
            }
        };
        
        content = texture.getDataAsHexStringImd();
    }
    
    public ImdBitmap(int size) {
        super("bitmap");
        
        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("size", size));
            }
        };
        
        //TODO: change this
        String pixels = "";
        for(int i = 0; i < size / 8; i++){
            for(int j = 0; j < 7; j++){
                pixels += "0000 ";
            }
            pixels += "0000 ";
        }
        content = pixels;
    }
    
    
    
}
