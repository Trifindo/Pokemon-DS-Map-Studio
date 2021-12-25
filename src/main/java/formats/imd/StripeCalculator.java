
package formats.imd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Trifindo
 */
public class StripeCalculator {

    private PolygonData pData;
    private boolean isQuad;
    private float[] vCoords;
    private float[] tCoords;
    private float[] nCoords;
    private float[] colors;
    private static final int vPerVertex = 3;
    private static final int tPerVertex = 2;
    private static final int nPerVertex = 3;
    private static final int cPerVertex = 3;
    private int vertexPerFace;
    private int edgesPerFace;
    private int numFaces;
    private int numEdges;
    private static final int[] oppositeIndex = new int[]{2, 2, -2, -2};

    private List<Edge> edges;
    private List<List<Integer>> edgesConnected;
    private boolean[] usedFaces;

    private boolean useUniformNormalOrientation;
    private boolean useVertexColors;

    public StripeCalculator(PolygonData pData, boolean isQuad, boolean useUniformNormalOrientation, boolean useVertexColors) {
        this.pData = pData;
        this.isQuad = isQuad;
        this.useVertexColors = useVertexColors;
        this.useUniformNormalOrientation = useUniformNormalOrientation;

        if (isQuad) {
            this.vCoords = pData.vCoordsQuad;
            this.tCoords = pData.tCoordsQuad;
            this.nCoords = pData.nCoordsQuad;
            this.colors = pData.colorsQuad;
            this.vertexPerFace = 4;
        } else {
            this.vCoords = pData.vCoordsTri;
            this.tCoords = pData.tCoordsTri;
            this.nCoords = pData.nCoordsTri;
            this.colors = pData.colorsTri;
            this.vertexPerFace = 3;
        }
        this.edgesPerFace = vertexPerFace;
        this.numFaces = vCoords.length / (vPerVertex * vertexPerFace);
        this.numEdges = numFaces * edgesPerFace;
    }

    public List<PolygonData> calculateTriStrip() {
        //Calculate edges
        edges = calculateEdges();

        //Generate a list of indices of edges conected
        edgesConnected = generateConnectedEdges(edges);

        //Calculate connected faces
        List<List<Edge>> connectedEdgesLocal = new ArrayList<>();
        List<List<Integer>> connectedFaces = calculateAllConnectedQuadFaces(connectedEdgesLocal);

        return null;//TODO change this
    }

    public List<PolygonData> calculateQuadStrip() {
        //Calculate edges
        edges = calculateEdges();

        //Generate a list of indices of edges conected
        edgesConnected = generateConnectedEdges(edges);

        //Calculate connected faces
        List<List<Edge>> connectedEdgesLocal = new ArrayList<>();
        List<List<Integer>> connectedFaces = calculateAllConnectedQuadFaces(connectedEdgesLocal);

        List<List<Integer>> stripFaces = new ArrayList<>();
        List<List<Edge>> stripEdges = new ArrayList<>();
        List<Integer> looseFaces = new ArrayList<>();
        for (int i = 0; i < connectedFaces.size(); i++) {
            if (connectedFaces.get(i).size() > 1) {
                stripFaces.add(connectedFaces.get(i));
                stripEdges.add(connectedEdgesLocal.get(i));
            } else {
                looseFaces.add(connectedFaces.get(i).get(0));
            }
        }

        List<PolygonData> pDataStrips = new ArrayList<>(stripFaces.size() + looseFaces.size());
        if (isQuad) {
            if (looseFaces.size() > 0) {
                PolygonData pDataLoose = new PolygonData();
                pDataLoose.initQuads(looseFaces.size());
                final int vCoordsPerFace = vPerVertex * vertexPerFace;
                final int tCoordsPerFace = tPerVertex * vertexPerFace;
                final int nCoordsPerFace = nPerVertex * vertexPerFace;
                final int colorsPerFace = cPerVertex * vertexPerFace;
                for (int i = 0; i < looseFaces.size(); i++) {
                    int faceIndex = looseFaces.get(i);
                    System.arraycopy(pData.vCoordsQuad, faceIndex * vCoordsPerFace, pDataLoose.vCoordsQuad, vCoordsPerFace * i, vCoordsPerFace);
                    System.arraycopy(pData.tCoordsQuad, faceIndex * tCoordsPerFace, pDataLoose.tCoordsQuad, tCoordsPerFace * i, tCoordsPerFace);
                    System.arraycopy(pData.nCoordsQuad, faceIndex * nCoordsPerFace, pDataLoose.nCoordsQuad, nCoordsPerFace * i, nCoordsPerFace);
                    if (useVertexColors) {
                        System.arraycopy(pData.colorsQuad, faceIndex * colorsPerFace, pDataLoose.colorsQuad, colorsPerFace * i, colorsPerFace);
                    }
                }
                pDataStrips.add(pDataLoose);
            } else {
                pDataStrips.add(null);
            }
        } else {

        }

        //pDataStrips = new ArrayList<>(stripFaces.size());
        //PolygonData pDataLoose;
        if (isQuad) {
            for (List<Edge> stripEdge : stripEdges) {
                PolygonData newPdata = new PolygonData();
                newPdata.initQuadStrip(stripEdge.size());
                for (int j = 0; j < stripEdge.size(); j++) {
                    copyEdgeData(newPdata, stripEdge.get(j), j);
                }
                pDataStrips.add(newPdata);
            }
        } else {
            for (List<Integer> stripFace : stripFaces) {
                PolygonData newPdata = new PolygonData();
                newPdata.initTris(stripFace.size());
                for (int j = 0; j < stripFace.size(); j++) {
                    for (int k = 0; k < 3; k++) {

                    }
                }
            }
        }

        return pDataStrips;
    }

    private void copyEdgeData(PolygonData newPData, Edge edge, int dstEdgeIndex) {
        copyEdgeCoords(vCoords, newPData.vCoordsQuad, edge, vPerVertex, dstEdgeIndex);
        copyEdgeCoords(tCoords, newPData.tCoordsQuad, edge, tPerVertex, dstEdgeIndex);
        copyEdgeCoords(nCoords, newPData.nCoordsQuad, edge, nPerVertex, dstEdgeIndex);
        if (useVertexColors) {
            copyEdgeCoords(colors, newPData.colorsQuad, edge, cPerVertex, dstEdgeIndex);
        }
    }

    private void copyEdgeCoords(float[] coordsData, float[] newCoordsData, Edge edge, int coordsPerVertex, int dstEdgeIndex) {
        System.arraycopy(
                coordsData, edge.vertexIndex1 * coordsPerVertex,
                newCoordsData, dstEdgeIndex * coordsPerVertex * 2, coordsPerVertex);
        System.arraycopy(
                coordsData, edge.vertexIndex2 * coordsPerVertex,
                newCoordsData, dstEdgeIndex * coordsPerVertex * 2 + coordsPerVertex, coordsPerVertex);
    }

    private int getOppositeEdgeIndex(int edgeIndex) {
        return edgeIndex + oppositeIndex[edgeIndex % vertexPerFace];
    }

    private int getEdgeIndex(int faceIndex, int localEdgeIndex) {
        return faceIndex * edgesPerFace + localEdgeIndex;
    }

    private int getFaceIndex(int edgeIndex) {
        return edgeIndex / vertexPerFace;
    }

    private boolean isEdgeConectedToNewFace(int edgeIndex, boolean[] localUsedFaces) {
        if (edgesConnected.get(edgeIndex).size() > 0) {
            int faceIndex = getFaceIndex(edgesConnected.get(edgeIndex).get(0));
            return !localUsedFaces[faceIndex] && !usedFaces[faceIndex];
        }
        return false;
    }

    private List<List<Integer>> calculateAllConnectedTriFaces(List<List<Edge>> edgesLocalConnected) {
        List<List<Integer>> connectedFaces = new ArrayList<>();
        usedFaces = new boolean[numFaces];
        for (int i = 0, c = 0; i < numFaces; i++) {
            if (!usedFaces[i]) {
                List<Edge> edgesLocal1 = new ArrayList<>();
                List<Edge> edgesLocal2 = new ArrayList<>();
                List<Edge> edgesLocal3 = new ArrayList<>();

                //ArrayList<Integer> connected1 = connectedTriFaces(i, 0, 2, edgesLocalX);
                c++;
            }
        }
        return connectedFaces;
    }

    private List<List<Integer>> calculateAllConnectedQuadFaces(List<List<Edge>> edgesLocalConnected) {
        List<List<Integer>> connectedFaces = new ArrayList<>();
        usedFaces = new boolean[numFaces];
        for (int i = 0, c = 0; i < numFaces; i++) {
            if (!usedFaces[i]) {
                List<Edge> edgesLocalX = new ArrayList<>();
                List<Edge> edgesLocalY = new ArrayList<>();
                List<Integer> connectedX = connectedQuadFaces(i, 0, 2, edgesLocalX);
                List<Integer> connectedY = connectedQuadFaces(i, 1, 3, edgesLocalY);
                if (connectedX.size() > connectedY.size()) {
                    connectedFaces.add(connectedX);
                    edgesLocalConnected.add(edgesLocalX);
                } else {
                    connectedFaces.add(connectedY);
                    edgesLocalConnected.add(edgesLocalY);
                }
                for (int j = 0; j < connectedFaces.get(c).size(); j++) {
                    usedFaces[connectedFaces.get(c).get(j)] = true;
                }
                c++;
            }
        }
        return connectedFaces;
    }

    private List<Integer> connectedTriFaces(int startFaceIndex) {
        List<Integer> connectedFaces = new ArrayList<>();
        boolean[] localUsedFaces = new boolean[usedFaces.length];
        localUsedFaces[startFaceIndex] = true;

        //(...)
        return null;
    }

    private List<Integer> connectedQuadFaces(int startFaceIndex, int forwardEdgeLocalIndex,
                                                  int backwardEdgeLocalIndex, List<Edge> edgesLocalConnected) {
        boolean[] localUsedFaces = new boolean[usedFaces.length];
        localUsedFaces[startFaceIndex] = true;
        List<Edge> edgesForward = new ArrayList<>();
        List<Edge> edgesBackward = new ArrayList<>();
        List<Integer> forward = connectedFacesOneDirection(startFaceIndex, forwardEdgeLocalIndex, localUsedFaces, edgesForward);
        List<Integer> backward = connectedFacesOneDirection(startFaceIndex, backwardEdgeLocalIndex, localUsedFaces, edgesBackward);

        Collections.reverse(forward);
        List<Integer> connectedFaces = new ArrayList<>(forward);
        connectedFaces.add(startFaceIndex);
        connectedFaces.addAll(backward);

        Collections.reverse(edgesForward);
        edgesLocalConnected.addAll(edgesForward);
        for (Edge edge : edgesBackward) {
            edge.flip();
        }
        edgesLocalConnected.addAll(edgesBackward);

        return connectedFaces;
    }

    private List<Integer> connectedFacesOneDirection(int startFaceIndex, int startEdgeIndex, boolean[] localUsedFaces, List<Edge> edgesLocalConnected) {
        List<Integer> connectedFaces = new ArrayList<>();
        int secondEdgeIndex;
        int firstEdgeIndex;
        secondEdgeIndex = getEdgeIndex(startFaceIndex, startEdgeIndex);
        edgesLocalConnected.add(new Edge(secondEdgeIndex));
        while (isEdgeConectedToNewFace(secondEdgeIndex, localUsedFaces)) {
            firstEdgeIndex = edgesConnected.get(secondEdgeIndex).get(0);
            int faceIndex = getFaceIndex(firstEdgeIndex);
            localUsedFaces[faceIndex] = true;
            connectedFaces.add(faceIndex);
            secondEdgeIndex = getOppositeEdgeIndex(firstEdgeIndex);
            edgesLocalConnected.add(new Edge(secondEdgeIndex));
        }
        return connectedFaces;
    }

    private List<List<Integer>> generateConnectedEdges(List<Edge> edges) {
        List<List<Integer>> conectedEdges = new ArrayList<>(numEdges);
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

    private List<Edge> calculateEdges() {
        List<Edge> edges = new ArrayList<>(numEdges);
        for (int i = 0; i < numFaces; i++) {
            int offset = edgesPerFace * i;
            for (int j = 0; j < edgesPerFace; j++) {
                edges.add(new Edge(offset + j, offset + (j + 1) % edgesPerFace));
            }
        }
        return edges;
    }

    private void printCoords(int vertexIndex) {
        for (int i = 0; i < vPerVertex; i++) {
            System.out.print(vCoords[vertexIndex * vPerVertex + i] + " ");
        }
        System.out.println();
    }

    private void printConectedEdgesCoords(List<Edge> edges, List<List<Integer>> edgesConnected) {
        for (List<Integer> integers : edgesConnected) {
            if (integers.size() > 0) {
                Edge edge = edges.get(integers.get(0));
                edge.printEdgeCoords();
            }
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

        public void printEdgeCoords() {
            printCoords(vertexIndex1);
            printCoords(vertexIndex2);
            System.out.println();
        }

        public void printIndices() {
            System.out.println(vertexIndex1 + " " + vertexIndex2);
        }

        public void flip() {
            int temp = vertexIndex1;
            vertexIndex1 = vertexIndex2;
            vertexIndex2 = temp;
        }

        private boolean sameVertexCoords(float[] coordData, int coordsPerVertex, int vertexIndex1, int vertexIndex2) {
            int offset1 = vertexIndex1 * coordsPerVertex;
            int offset2 = vertexIndex2 * coordsPerVertex;
            return IntStream.range(0, coordsPerVertex)
                    .noneMatch(i -> Math.abs(coordData[offset1 + i] - coordData[offset2 + i]) > 0.0001f);
        }

        private boolean sameEdgeCoords(float[] coords, int coordsPerVertex, Edge other) {
            /*if (sameVertexCoords(coords, coordsPerVertex, this.vertexIndex1, other.vertexIndex1)) {
                if (sameVertexCoords(coords, coordsPerVertex, this.vertexIndex2, other.vertexIndex2)) {
                    return true;
                }
            } else */
            if (sameVertexCoords(coords, coordsPerVertex, this.vertexIndex1, other.vertexIndex2)) {
                return sameVertexCoords(coords, coordsPerVertex, this.vertexIndex2, other.vertexIndex1);
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            final Edge other = (Edge) obj;
            if (useUniformNormalOrientation) {
                if (useVertexColors) {
                    return sameEdgeCoords(vCoords, vPerVertex, other) && sameEdgeCoords(tCoords, tPerVertex, other) && sameEdgeCoords(colors, cPerVertex, other);
                } else {
                    return sameEdgeCoords(vCoords, vPerVertex, other) && sameEdgeCoords(tCoords, tPerVertex, other);
                }
            } else {
                if (useVertexColors) {
                    return sameEdgeCoords(vCoords, vPerVertex, other) && sameEdgeCoords(tCoords, tPerVertex, other) && sameEdgeCoords(nCoords, nPerVertex, other) && sameEdgeCoords(colors, cPerVertex, other);
                } else {
                    return sameEdgeCoords(vCoords, vPerVertex, other) && sameEdgeCoords(tCoords, tPerVertex, other) && sameEdgeCoords(nCoords, nPerVertex, other);
                }
            }
        }
    }
}
