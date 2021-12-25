
package formats.dae;

import java.util.List;

/**
 * @author Trifindo
 */
public class DaeGeometry extends DaeNode {

    private List<DaeMesh> meshes;

    public DaeGeometry(String id, String name) {
        super(id, name);
    }
}
