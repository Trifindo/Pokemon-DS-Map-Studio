
package editor.imd;

import editor.imd.nodes.Body;
import editor.imd.nodes.Head;

/**
 *
 * @author Trifindo
 */
public class ImdTileset extends ImdNode{
   
    public ImdTileset() {
        super("imd");
        
        subnodes.add(new Head());

        Body body = new Body();
        
        
        
        
    }
   
    
    
}
