
package geometry;

import java.util.ArrayList;
import tileset.Tile;

/**
 *
 * @author Trifindo
 */
public class TrisToQuads {

    private float[] vCoordsTri;
    private float[] nCoordsTri;
    private float[] tCoordsTri;

    private float[] vCoordsQuad;
    private float[] nCoordsQuad;
    private float[] tCoordsQuad;

    private static final int vPerVertex = 3;
    private static final int tPerVertex = 2;
    private static final int nPerVertex = 3;
    private static final int vertexPerFace = 3;
    private static final int edgesPerFace = 3;
    private int numFaces;
    private int numEdges;
    
    private ArrayList<Edge> edges;
    private ArrayList<ArrayList<Integer>> edgesConnected;
    private boolean[] usedFaces;

    public TrisToQuads(PolygonData pData) {

        this.vCoordsTri = pData.vCoordsTri;
        this.tCoordsTri = pData.tCoordsTri;
        this.nCoordsTri = pData.nCoordsTri;
        
        this.vCoordsQuad = pData.vCoordsQuad;
        this.tCoordsQuad = pData.tCoordsQuad;
        this.nCoordsQuad = pData.nCoordsQuad;
        
        this.numFaces = vCoordsTri.length / (vPerVertex * vertexPerFace);
        this.numEdges = numFaces * edgesPerFace;
    }
    
    public void calculateQuads(){
        edges = calculateEdges();
        edgesConnected = generateConnectedEdges(edges);
        usedFaces = new boolean[numFaces];
        
        for(int i = 0; i < edgesConnected.size(); i++){
            int faceIndex = edgesConnected.get(i).get(0) / edgesPerFace;
            if(edgesConnected.get(i) != null && !usedFaces[faceIndex] && !usedFaces[i / edgesPerFace]){
                
            }
        }
        
    }

    private ArrayList<Edge> calculateEdges() {
        ArrayList<Edge> edges = new ArrayList<>(numEdges);
        for (int i = 0; i < numFaces; i++) {
            int offset = edgesPerFace * i;
            for (int j = 0; j < edgesPerFace; j++) {
                edges.add(new Edge(offset + j, offset + (j + 1) % edgesPerFace));
            }
        }
        return edges;
    }
    
    private ArrayList<ArrayList<Integer>> generateConnectedEdges(ArrayList<Edge> edges) {
        ArrayList<ArrayList<Integer>> conectedEdges = new ArrayList<>(numEdges);
        for (int i = 0; i < numEdges; i++) {
            conectedEdges.add(new ArrayList<>());
        }
        for (int i = 0; i < numEdges; i++) {
            for (int j = i + 1; j < numEdges; j++) {
                if (edges.get(i).equals(edges.get(j))) {
                    conectedEdges.get(i).add(j);
                    conectedEdges.get(j).add(i);
                }
            }
        }
        return conectedEdges;
    }

    private class PolygonData {

        public float[] vCoordsTri;
        public float[] nCoordsTri;
        public float[] tCoordsTri;

        public float[] vCoordsQuad;
        public float[] nCoordsQuad;
        public float[] tCoordsQuad;

        public PolygonData() {

        }
    }

    private class Edge {

        public int vertexIndex1;
        public int vertexIndex2;

        public Edge(int vertexIndex1, int vertexIndex2) {
            this.vertexIndex1 = vertexIndex1;
            this.vertexIndex2 = vertexIndex2;
        }

        public Edge(int edgeIndex) {
            vertexIndex1 = edges.get(edgeIndex).vertexIndex1;
            vertexIndex2 = edges.get(edgeIndex).vertexIndex2;
        }

        public void flip() {
            int temp = vertexIndex1;
            vertexIndex1 = vertexIndex2;
            vertexIndex2 = temp;
        }

        private boolean sameVertexCoords(float[] coordData, int coordsPerVertex,
                int vertexIndex1, int vertexIndex2) {
            int offset1 = vertexIndex1 * coordsPerVertex;
            int offset2 = vertexIndex2 * coordsPerVertex;
            for (int i = 0; i < coordsPerVertex; i++) {
                if (Math.abs(coordData[offset1 + i] - coordData[offset2 + i]) > 0.0001f) {
                    return false;
                }
            }
            return true;
        }

        private boolean sameEdgeCoords(float[] coords, int coordsPerVertex,
                Edge other) {
            /*if (sameVertexCoords(coords, coordsPerVertex, this.vertexIndex1, other.vertexIndex1)) {
                if (sameVertexCoords(coords, coordsPerVertex, this.vertexIndex2, other.vertexIndex2)) {
                    return true;
                }
            } else */
            if (sameVertexCoords(coords, coordsPerVertex, this.vertexIndex1, other.vertexIndex2)) {
                if (sameVertexCoords(coords, coordsPerVertex, this.vertexIndex2, other.vertexIndex1)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            final Edge other = (Edge) obj;
            if (sameEdgeCoords(vCoordsTri, vPerVertex, other)
                    && sameEdgeCoords(tCoordsTri, tPerVertex, other)
                    && sameEdgeCoords(nCoordsTri, nPerVertex, other)) {
                return true;
            }
            return false;
        }

    }

}
