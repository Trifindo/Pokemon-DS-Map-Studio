
package editor.buildingeditor2.tileset;

import editor.narc2.Narc;
import editor.narc2.NarcFile;
import editor.narc2.NarcFolder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class BuildTilesetList {
    
    private ArrayList<BuildTileset> tilesets;
    
    public BuildTilesetList(Narc narc){
        final int numTilesets = narc.getRoot().getFiles().size();
        tilesets = new ArrayList<>(numTilesets);
        for(NarcFile file : narc.getRoot().getFiles()){
            tilesets.add(new BuildTileset(file.getData()));
        }
    }
    
    public Narc toNarc() throws Exception{
        NarcFolder root = new NarcFolder();
        ArrayList<NarcFile> files = new ArrayList<>(tilesets.size());
        for(BuildTileset tileset : tilesets){
            files.add(new NarcFile("", root, tileset.getData()));
        }
        root.setFiles(files);
        return new Narc(root);
    }

    public ArrayList<BuildTileset> getTilesets() {
        return tilesets;
    }
    
    public void saveTileset(int index, String path) throws IOException{
        tilesets.get(index).save(path);
    }
    
    
}
