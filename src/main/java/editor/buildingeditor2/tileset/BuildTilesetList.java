
package editor.buildingeditor2.tileset;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trifindo
 */
public class BuildTilesetList {

    private final List<BuildTileset> tilesets;

    public BuildTilesetList(Narc narc) {
        final int numTilesets = narc.getRoot().getFiles().size();
        tilesets = new ArrayList<>(numTilesets);
        for (NarcFile file : narc.getRoot().getFiles()) {
            tilesets.add(new BuildTileset(file.getData()));
        }
    }

    public Narc toNarc() {
        NarcFolder root = new NarcFolder();
        List<NarcFile> files = tilesets.stream()
                .map(tileset -> new NarcFile("", root, tileset.getData()))
                .collect(Collectors.toCollection(() -> new ArrayList<>(tilesets.size())));
        root.setFiles(files);
        return new Narc(root);
    }

    public List<BuildTileset> getTilesets() {
        return tilesets;
    }

    public void saveTileset(int index, String path) throws IOException {
        tilesets.get(index).save(path);
    }
}
