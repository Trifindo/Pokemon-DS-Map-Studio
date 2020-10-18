
package editor.dae;

/**
 * @author Trifindo
 */
public class DaeNode {

    protected String id;
    protected String name;

    public DaeNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


}
