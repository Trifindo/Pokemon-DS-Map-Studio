/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2;

import editor.buildingeditor2.buildmodel.BuildModelMatshp;
import editor.buildingeditor2.buildmodel.BuildModelList;
import editor.buildingeditor2.animations.BuildAnimations;
import editor.buildingeditor2.animations.BuildAnimeListDPPt;
import editor.buildingeditor2.areabuild.AreaBuild;
import editor.buildingeditor2.areabuild.AreaBuildList;
import editor.buildingeditor2.areadata.AreaDataListDPPt;
import editor.buildingeditor2.tileset.BuildTileset;
import editor.buildingeditor2.tileset.BuildTilesetList;
import editor.game.GameFileSystemDPPt;
import editor.handler.MapEditorHandler;
import editor.narc2.Narc;
import editor.narc2.NarcIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nitroreader.nsbmd.NSBMD;
import nitroreader.nsbmd.sbccommands.MAT;
import nitroreader.nsbmd.sbccommands.SBCCommand;
import nitroreader.nsbmd.sbccommands.SHP;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class BuildHandlerDPPt {

    //private MapEditorHandler handler;
    private String gameFolderPath = "";
    private GameFileSystemDPPt gameFileSystem;

    private BuildModelList buildModelList;
    private BuildModelMatshp buildModelMatshp;
    private BuildAnimeListDPPt buildModelAnimeList;
    private BuildAnimations buildModelAnims;
    private AreaDataListDPPt areaDataList;
    private BuildTilesetList buildTilesetList;
    private AreaBuildList areaBuildList;

    public BuildHandlerDPPt(String gameFolderPath) {
        this.gameFolderPath = gameFolderPath;
        this.gameFileSystem = new GameFileSystemDPPt();
    }

    public boolean areAllFilesLoaded() {
        return buildModelList != null
                && buildModelMatshp != null
                && buildModelAnimeList != null
                && buildModelAnims != null
                && areaDataList != null
                && buildTilesetList != null
                && areaBuildList != null;
    }

    public boolean areAllFilesAvailable() {
        return (isGameFileAvailable(gameFileSystem.getBuildModelPath())
                && isGameFileAvailable(gameFileSystem.getBuildModelMatshpPath())
                && isGameFileAvailable(gameFileSystem.getBuildModelAnimeListPath())
                && isGameFileAvailable(gameFileSystem.getBuildModelAnimePath())
                && isGameFileAvailable(gameFileSystem.getAreaDataPath())
                && isGameFileAvailable(gameFileSystem.getAreaBuildTilesetPath())
                && isGameFileAvailable(gameFileSystem.getAreaBuildModelPath()));
    }

    public void loadAllFiles() throws Exception {
        try {
            Narc buildModelNarc = NarcIO.loadNarc(getGameFilePath(gameFileSystem.getBuildModelPath()));
            buildModelList = new BuildModelList(buildModelNarc);
            System.out.println("buildModelList LOADED!");

            buildModelMatshp = new BuildModelMatshp(getGameFilePath(gameFileSystem.getBuildModelMatshpPath()));
            System.out.println("buildModelMatshp LOADED!");

            Narc buildModelAnimeListNarc = NarcIO.loadNarc(getGameFilePath(gameFileSystem.getBuildModelAnimeListPath()));
            buildModelAnimeList = new BuildAnimeListDPPt(buildModelAnimeListNarc);
            System.out.println("buildModelAnimeList LOADED!");

            Narc buildModelAnimsNarc = NarcIO.loadNarc(getGameFilePath(gameFileSystem.getBuildModelAnimePath()));
            buildModelAnims = new BuildAnimations(buildModelAnimsNarc);
            System.out.println("buildModelAnims LOADED!");

            Narc areaDataListNarc = NarcIO.loadNarc(getGameFilePath(gameFileSystem.getAreaDataPath()));
            areaDataList = new AreaDataListDPPt(areaDataListNarc);

            Narc buildTilesetListNarc = NarcIO.loadNarc(getGameFilePath(gameFileSystem.getAreaBuildTilesetPath()));
            buildTilesetList = new BuildTilesetList(buildTilesetListNarc);

            Narc areaBuildListNarc = NarcIO.loadNarc(getGameFilePath(gameFileSystem.getAreaBuildModelPath()));
            areaBuildList = new AreaBuildList(areaBuildListNarc);

        } catch (Exception ex) {
            buildModelList = null;
            buildModelMatshp = null;
            buildModelAnimeList = null;
            buildModelAnims = null;
            areaDataList = null;
            buildTilesetList = null;
            areaBuildList = null;
            throw ex;
        }
    }

    public void saveAllFiles() throws Exception {
        try {
            NarcIO.writeNarc(buildModelList.toNarc(), getGameFilePath(gameFileSystem.getBuildModelPath()));
            buildModelMatshp.saveToFile(getGameFilePath(gameFileSystem.getBuildModelMatshpPath()));
            NarcIO.writeNarc(buildModelAnimeList.toNarc(), getGameFilePath(gameFileSystem.getBuildModelAnimeListPath()));
            NarcIO.writeNarc(buildModelAnims.toNarc(), getGameFilePath(gameFileSystem.getBuildModelAnimePath()));
            NarcIO.writeNarc(areaDataList.toNarc(), getGameFilePath(gameFileSystem.getAreaDataPath()));
            NarcIO.writeNarc(areaBuildList.toNarc(), getGameFilePath(gameFileSystem.getAreaBuildModelPath()));
            NarcIO.writeNarc(buildTilesetList.toNarc(), getGameFilePath(gameFileSystem.getAreaBuildTilesetPath()));
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void saveBuildModelList() throws IOException {
        NarcIO.writeNarc(buildModelList.toNarc(), getGameFilePath(gameFileSystem.getBuildModelPath()));
    }

    private void generateMissingFiles() {
        //TODO: Finish this
    }

    public void addBuilding(String path) throws IOException {
        try {
            buildModelList.addBuildingModel(path);

            try {
                byte[] data = buildModelList.getModelsData().get(buildModelList.getSize() - 1);
                ArrayList<Integer> materials = BuildHandlerDPPt.getMaterialOrder(data);
                buildModelMatshp.addBuildingMaterials(materials);
            } catch (Exception ex) {
                buildModelMatshp.addBuildingMaterials(new ArrayList<>());
            }

            buildModelAnimeList.addBuildingAnimation(new ArrayList<>(), (byte) -1);

        } catch (Exception ex) {
            throw new IOException();
        }
    }

    public void replaceBuilding(int index, String path) throws IOException {
        try {
            buildModelList.replaceBuildingModel(index, path);
            buildModelMatshp.replaceBuildingMaterials(index, new ArrayList<>());
            buildModelAnimeList.replaceBuildingAnimation(index, new ArrayList<>(), (byte) -1);
        } catch (Exception ex) {
            throw new IOException();
        }
    }

    public void saveBuilding(int index, String path) throws IOException {
        buildModelList.saveBuildingModel(index, path);
    }

    public void saveBuildingTileset(int index, String path) throws IOException {
        buildTilesetList.saveTileset(index, path);
    }

    public void removeBuilding(int index) {
        buildModelList.removeBuildingModel(index);
        buildModelMatshp.removeBuildingMaterials(index);
        buildModelAnimeList.removeBuildingAnimation(index);
        areaBuildList.shiftBuildingIDsFrom(index);
    }

    public void addBuildingTileset(byte[] data) {
        buildTilesetList.getTilesets().add(new BuildTileset(data));
        areaBuildList.getAreaBuilds().add(new AreaBuild(new ArrayList<>()));
    }

    public void replaceBuildingTileset(int index, byte[] data) {
        buildTilesetList.getTilesets().set(index, new BuildTileset(data));
    }

    public void addBuildingMaterial(int buildIndex) {
        buildModelMatshp.addBuildingMaterial(buildIndex);
    }

    public void removeBuildingMaterial(int buildIndex, int materialIndex) {
        buildModelMatshp.removeBuildingMaterial(buildIndex, materialIndex);
    }

    public void removeAllBuildingMaterials(int buildIndex) {
        buildModelMatshp.replaceBuildingMaterials(buildIndex, new ArrayList<>());
    }

    public void moveBuildingMaterialUp(int buildIndex, int materialIndex) {
        buildModelMatshp.moveMaterialUp(buildIndex, materialIndex);
    }

    public void moveBuildingMaterialDown(int buildIndex, int materialIndex) {
        buildModelMatshp.moveMaterialDown(buildIndex, materialIndex);
    }

    public void addBuildingAnimation(int buildIndex, int animationIndex) {
        buildModelAnimeList.addBuildingAnimation(buildIndex, animationIndex);
    }

    public void removeBuildingAnimation(int buildIndex, int animationIndex) {
        buildModelAnimeList.removeBuildingAnimation(buildIndex, animationIndex);
    }

    public void replaceBuildingAnimation(int buildIndex, int animationIndex, int oldAnimationIndex) {
        buildModelAnimeList.replaceBuildingAnimation(buildIndex, animationIndex, oldAnimationIndex);
    }

    public void addBuildToAreaBuild(int areaBuildIndex, int buildIndex) {
        areaBuildList.addBuilding(areaBuildIndex, buildIndex);
    }

    public void replaceBuildToAreaBuild(int areaBuildIndex, int buildIndex, int oldAnimationIndex) {
        areaBuildList.replaceBuilding(areaBuildIndex, buildIndex, oldAnimationIndex);
    }

    public void removeBuildToAreaBuild(int areaBuildIndex, int buildIndex) {
        areaBuildList.removeBuilding(areaBuildIndex, buildIndex);
    }

    public void addAnimationFile(String path) throws IOException {
        buildModelAnims.addBuildAnimation(path);
    }

    public void replaceAnimationFile(int index, String path) throws IOException {
        buildModelAnims.replaceAnimation(index, path);
    }

    public void saveAnimationFile(int index, String path) throws IOException {
        buildModelAnims.saveAnimation(index, path);
    }

    private String getGameFilePath(String relativePath) {
        return gameFolderPath + File.separator + relativePath;
    }

    private boolean isGameFileAvailable(String path) {
        return isFileAvailable(gameFolderPath + File.separator + path);
    }

    private boolean isFileAvailable(String path) {
        return new File(path).exists();
    }

    public void setGameFolderPath(String path) {
        this.gameFolderPath = path;
    }

    public String getGameFolderPath() {
        return gameFolderPath;
    }

    public BuildModelList getBuildModelList() {
        return buildModelList;
    }

    public BuildModelMatshp getBuildModelMatshp() {
        return buildModelMatshp;
    }

    public BuildAnimeListDPPt getBuildModelAnimeList() {
        return buildModelAnimeList;
    }

    public BuildAnimations getBuildModelAnims() {
        return buildModelAnims;
    }

    public AreaDataListDPPt getAreaDataList() {
        return areaDataList;
    }

    public BuildTilesetList getBuildTilesetList() {
        return buildTilesetList;
    }

    public AreaBuildList getAreaBuildList() {
        return areaBuildList;
    }

    public static ArrayList<Integer> getMaterialOrder(byte[] nsbmdData) {
        NSBMD nsbmd = new NSBMD(nsbmdData);

        List<SBCCommand> sbc = nsbmd.getMLD0().getModels().get(0).getSBC();
        List<Integer> materialIDs = new ArrayList<>();
        List<Integer> shapeIDs = new ArrayList<>();

        for (SBCCommand cmd : sbc) {
            if (cmd instanceof MAT) {
                MAT mat = (MAT) cmd;
                materialIDs.add(mat.getMatID());
            } else if (cmd instanceof SHP) {
                SHP shp = (SHP) cmd;
                shapeIDs.add(shp.getShpID());
            }
        }

        int[] polyLookup = new int[materialIDs.size()];
        for (int i = 0; i < materialIDs.size(); i++) {
            polyLookup[materialIDs.get(i)] = shapeIDs.get(i);
        }

        ArrayList<Integer> materials = new ArrayList<>(polyLookup.length);
        for (int i = 0; i < polyLookup.length; i++) {
            materials.add(polyLookup[i]);
        }

        if (Utils.hasDuplicates(materials)) {
            return new ArrayList<>();
        }

        return materials;
    }

}
