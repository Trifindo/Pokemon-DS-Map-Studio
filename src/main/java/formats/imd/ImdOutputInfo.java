
package formats.imd;

/**
 * @author Trifindo
 */
public class ImdOutputInfo {

    public String objName;

    public int numMaterials;
    public int numVertices;
    public int numPolygons;
    public int numTris;
    public int numQuads;

    public Exception ex = null;

    public ImdOutputInfo(String objName, ImdModel model) {
        this.objName = objName;
        numMaterials = model.getNumMaterials();
        numVertices = model.getNumVertices();
        numPolygons = model.getNumPolygons();
        numTris = model.getNumTris();
        numQuads = model.getNumQuads();
    }

    public ImdOutputInfo(String objName, Exception ex) {
        this.ex = ex;
    }
}
