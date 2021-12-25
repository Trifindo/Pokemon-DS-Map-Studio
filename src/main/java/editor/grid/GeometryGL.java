
package editor.grid;

import com.jogamp.common.nio.Buffers;

import java.nio.FloatBuffer;

/**
 * @author Trifindo
 */
public class GeometryGL {

    public static final int vertexPerTri = 3;
    public static final int vertexPerQuad = 4;

    public static final int vPerVertex = 3;
    public static final int tPerVertex = 2;
    public static final int nPerVertex = 3;
    public static final int cPerVertex = 3;

    public static final int vPerTri = vertexPerTri * vPerVertex;
    public static final int tPerTri = vertexPerTri * tPerVertex;
    public static final int nPerTri = vertexPerTri * nPerVertex;
    public static final int cPerTri = vertexPerTri * cPerVertex;

    public static final int vPerQuad = vertexPerQuad * vPerVertex;
    public static final int tPerQuad = vertexPerQuad * tPerVertex;
    public static final int nPerQuad = vertexPerQuad * nPerVertex;
    public static final int cPerQuad = vertexPerQuad * cPerVertex;

    public float[] vCoordsTri;
    public float[] tCoordsTri;
    public float[] nCoordsTri;
    public float[] colorsTri;

    public float[] vCoordsQuad;
    public float[] tCoordsQuad;
    public float[] nCoordsQuad;
    public float[] colorsQuad;

    public FloatBuffer vCoordsTriBuffer;
    public FloatBuffer tCoordsTriBuffer;
    public FloatBuffer nCoordsTriBuffer;
    public FloatBuffer colorsTriBuffer;

    public FloatBuffer vCoordsQuadBuffer;
    public FloatBuffer tCoordsQuadBuffer;
    public FloatBuffer nCoordsQuadBuffer;
    public FloatBuffer colorsQuadBuffer;

    public int textureID;

    public GeometryGL(int textureID, int numTris, int numQuads) {
        this.textureID = textureID;

        final int verticesPerTri = 3;
        final int verticesPerQuad = 4;
        final int coordsPerV = 3;
        final int coordsPerT = 2;
        final int coordsPerN = 3;
        final int coordsPerColor = 3;

        this.vCoordsTri = new float[numTris * verticesPerTri * coordsPerV];
        this.tCoordsTri = new float[numTris * verticesPerTri * coordsPerT];
        this.nCoordsTri = new float[numTris * verticesPerTri * coordsPerN];
        this.colorsTri = new float[numTris * verticesPerTri * coordsPerColor];

        this.vCoordsQuad = new float[numQuads * verticesPerQuad * coordsPerV];
        this.tCoordsQuad = new float[numQuads * verticesPerQuad * coordsPerT];
        this.nCoordsQuad = new float[numQuads * verticesPerQuad * coordsPerN];
        this.colorsQuad = new float[numQuads * verticesPerQuad * coordsPerColor];
    }

    public void updateBuffers() {
        this.vCoordsTriBuffer = Buffers.newDirectFloatBuffer(vCoordsTri);
        this.tCoordsTriBuffer = Buffers.newDirectFloatBuffer(tCoordsTri);
        this.nCoordsTriBuffer = Buffers.newDirectFloatBuffer(nCoordsTri);
        this.colorsTriBuffer = Buffers.newDirectFloatBuffer(colorsTri);

        this.vCoordsQuadBuffer = Buffers.newDirectFloatBuffer(vCoordsQuad);
        this.tCoordsQuadBuffer = Buffers.newDirectFloatBuffer(tCoordsQuad);
        this.nCoordsQuadBuffer = Buffers.newDirectFloatBuffer(nCoordsQuad);
        this.colorsQuadBuffer = Buffers.newDirectFloatBuffer(colorsQuad);
    }

    public boolean hasTriData() {
        return vCoordsTri != null && vCoordsTri.length > 0
                && tCoordsTri != null && tCoordsTri.length > 0
                && nCoordsTri != null && nCoordsTri.length > 0
                && colorsTri != null && colorsTri.length > 0;
    }

    public boolean hasQuadData() {
        return vCoordsQuad != null && vCoordsQuad.length > 0
                && tCoordsQuad != null && tCoordsQuad.length > 0
                && nCoordsQuad != null && nCoordsQuad.length > 0
                && colorsQuad != null && colorsQuad.length > 0;
    }

    public boolean hasTriBufferData() {
        return vCoordsTriBuffer != null && vCoordsTriBuffer.hasRemaining()
                && tCoordsTriBuffer != null && tCoordsTriBuffer.hasRemaining()
                && nCoordsTriBuffer != null && nCoordsTriBuffer.hasRemaining()
                && colorsTriBuffer != null && colorsTriBuffer.hasRemaining();
    }

    public boolean hasQuadBufferData() {
        return vCoordsQuadBuffer != null && vCoordsQuadBuffer.hasRemaining()
                && tCoordsQuadBuffer != null && tCoordsQuadBuffer.hasRemaining()
                && nCoordsQuadBuffer != null && nCoordsQuadBuffer.hasRemaining()
                && colorsQuadBuffer != null && colorsQuadBuffer.hasRemaining();
    }
}
