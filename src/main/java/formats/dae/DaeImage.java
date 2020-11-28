
package formats.dae;

/**
 * @author Trifindo
 */
public class DaeImage extends DaeNode {

    private String fileName;

    public DaeImage(String id, String name) {
        super(id, name);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
