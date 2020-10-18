
package editor.game;

import java.io.File;

/**
 * @author Trifindo
 */
public class GameFileSystemDPPt extends GameFileSystem {

    private final String areaDataPath;
    private final String areaBuildModelPath;
    private final String areaBuildTilesetPath;
    private final String buildModelPath;
    private final String buildModelMatshpPath;
    private final String buildModelAnimePath;
    private final String buildModelAnimeListPath;

    public GameFileSystemDPPt() {
        areaDataPath = getPath(new String[]{"data", "fielddata", "areadata", "area_data.narc"});
        areaBuildModelPath = getPath(new String[]{"data", "fielddata", "areadata", "area_build_model", "area_build.narc"});
        areaBuildTilesetPath = getPath(new String[]{"data", "fielddata", "areadata", "area_build_model", "areabm_texset.narc"});
        buildModelPath = getPath(new String[]{"data", "fielddata", "build_model", "build_model.narc"});
        buildModelMatshpPath = getPath(new String[]{"data", "fielddata", "build_model", "build_model_matshp.dat"});
        buildModelAnimePath = getPath(new String[]{"data", "arc", "bm_anime.narc"});
        buildModelAnimeListPath = getPath(new String[]{"data", "arc", "bm_anime_list.narc"});
    }

    public String getAreaDataPath() {
        return areaDataPath;
    }

    public String getAreaBuildModelPath() {
        return areaBuildModelPath;
    }

    public String getAreaBuildTilesetPath() {
        return areaBuildTilesetPath;
    }

    public String getBuildModelPath() {
        return buildModelPath;
    }

    public String getBuildModelMatshpPath() {
        return buildModelMatshpPath;
    }

    public String getBuildModelAnimePath() {
        return buildModelAnimePath;
    }

    public String getBuildModelAnimeListPath() {
        return buildModelAnimeListPath;
    }

}
