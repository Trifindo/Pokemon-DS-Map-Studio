/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor;

import java.io.IOException;

/**
 *
 * @author Trifindo
 */
public class BuildHandler {
    
    private BuildModelMatshp bModelMatshp;
    private BuildTilesetList bTilesetList;
    
    public void BuildEditorHandler(){
        
    }
    
    public void loadBuildModelMashup(String path) throws IOException{
        this.bModelMatshp = new BuildModelMatshp(path);
    }
    
    public void loadBuildTilesetList(String path) throws IOException{
        this.bTilesetList = new BuildTilesetList(path);
    }
    
    public BuildModelMatshp getBuildModelMatshp(){
        return bModelMatshp;
    }
    
    public BuildTilesetList getBuildTilesetList(){
        return bTilesetList;
    }
}
