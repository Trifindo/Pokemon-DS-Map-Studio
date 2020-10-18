
package editor.bdhc;

import editor.handler.MapEditorHandler;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class BdhcHandler {
    
    private MapEditorHandler handler;
    //private Bdhc bdhc;
    private int indexSelected = 0;
    private BdhcEditorDialog dialog;
    
    public BdhcHandler(BdhcEditorDialog dialog){
        this.dialog = dialog;
        
        indexSelected = 0;
    }
    
    public void init(MapEditorHandler handler){
        this.handler = handler;
        //this.bdhc = handler.getBdhc();
    }
    
    public Bdhc getBdhc(){
        return handler.getBdhc();
    }
    
    public void setBdhc(Bdhc bdhc){
        this.handler.setBdhc(bdhc);
    }
    
    public void addPlate(){
        handler.getBdhc().addPlate();
    }
    
    public void setSelectedPlate(int index){
        this.indexSelected = index;
    }
    
    public Plate getSelectedPlate(){
        return handler.getBdhc().getPlate(indexSelected);
    }
    
    public int getSelectedPlateIndex(){
        return indexSelected;
    }
    
    public void removeSelectedPlate(){
        handler.getBdhc().getPlates().remove(indexSelected);
        if(indexSelected > 0){
            indexSelected--;
        }else{
            indexSelected = 0;
        }
        
    }
    
    public ArrayList<Plate> getPlates(){
        return handler.getBdhc().getPlates();
    }
    
    public BdhcEditorDialog getDialog(){
        return dialog;
    }
}
