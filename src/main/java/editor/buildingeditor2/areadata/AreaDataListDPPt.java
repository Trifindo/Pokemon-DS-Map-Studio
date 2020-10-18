
package editor.buildingeditor2.areadata;

import editor.narc2.Narc;
import editor.narc2.NarcFile;
import editor.narc2.NarcFolder;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class AreaDataListDPPt {
    
    private ArrayList<AreaDataDPPt> areaDatas;
    
    public AreaDataListDPPt(Narc narc) throws Exception{
        final int numFiles = narc.getRoot().getFiles().size();
        areaDatas = new ArrayList<>(numFiles);
        for(int i = 0; i < numFiles; i++){
            areaDatas.add(new AreaDataDPPt(narc.getRoot().getFiles().get(i).getData()));
        }
    }
    
    public Narc toNarc() throws Exception{
        NarcFolder root = new NarcFolder();
        ArrayList<NarcFile> files = new ArrayList<>(areaDatas.size());
        for(AreaDataDPPt areaData : areaDatas){
            files.add(new NarcFile("", root, areaData.toByteArray()));
        }
        root.setFiles(files);
        return new Narc(root);
    }
    
    public  ArrayList<AreaDataDPPt> getAreaDatas(){
        return areaDatas;
    }
    
}
