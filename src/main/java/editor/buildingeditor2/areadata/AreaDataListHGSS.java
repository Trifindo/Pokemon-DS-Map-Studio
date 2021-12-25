
package editor.buildingeditor2.areadata;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trifindo
 */
public class AreaDataListHGSS {

    private final List<AreaDataHGSS> areaDatas;

    public AreaDataListHGSS(Narc narc) {
        final int numFiles = narc.getRoot().getFiles().size();
        areaDatas = new ArrayList<>(numFiles);
        for (int i = 0; i < numFiles; i++) {
            areaDatas.add(new AreaDataHGSS(narc.getRoot().getFiles().get(i).getData()));
        }
    }

    public Narc toNarc() {
        NarcFolder root = new NarcFolder();
        List<NarcFile> files = new ArrayList<>(areaDatas.size());
        for (AreaDataHGSS areaData : areaDatas) {
            files.add(new NarcFile("", root, areaData.toByteArray()));
        }
        root.setFiles(files);
        return new Narc(root);
    }

    public List<AreaDataHGSS> getAreaDatas() {
        return areaDatas;
    }
}
