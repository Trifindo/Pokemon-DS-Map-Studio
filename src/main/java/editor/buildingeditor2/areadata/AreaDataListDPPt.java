
package editor.buildingeditor2.areadata;

import formats.narc2.Narc;
import formats.narc2.NarcFile;
import formats.narc2.NarcFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trifindo
 */
public class AreaDataListDPPt {

    private final List<AreaDataDPPt> areaDatas;

    public AreaDataListDPPt(Narc narc) {
        final int numFiles = narc.getRoot().getFiles().size();
        areaDatas = new ArrayList<>(numFiles);
        for (int i = 0; i < numFiles; i++) {
            areaDatas.add(new AreaDataDPPt(narc.getRoot().getFiles().get(i).getData()));
        }
    }

    public Narc toNarc() {
        NarcFolder root = new NarcFolder();
        List<NarcFile> files = new ArrayList<>(areaDatas.size());
        for (AreaDataDPPt areaData : areaDatas) {
            files.add(new NarcFile("", root, areaData.toByteArray()));
        }
        root.setFiles(files);
        return new Narc(root);
    }

    public List<AreaDataDPPt> getAreaDatas() {
        return areaDatas;
    }
}
