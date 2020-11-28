
package formats.imd;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class TriangleStripCalculator {

    private float[] vCoords;
    private float[] tCoords;
    private float[] nCoords;
    private float[] colors;
    private static final int vPerVertex = 3;
    private static final int tPerVertex = 2;
    private static final int nPerVertex = 3;
    private static final int cPerVertex = 3;
    private static final int vertexPerFace = 3;
    private static final int edgesPerFace = 3;
    private int numFaces;
    private int numEdges;

    private ArrayList<Edge> edges;
    private ArrayList<ArrayList<Integer>> edgesConnected;
    private boolean[] usedFaces;

    private boolean useUniformNormalOrientation;
    private boolean useVertexColors;

    public TriangleStripCalculator(PolygonData pData, boolean useUniformNormalOrientation, boolean useVertexColors) {
        this.vCoords = pData.vCoordsTri;
        this.tCoords = pData.tCoordsTri;
        this.nCoords = pData.nCoordsTri;
        this.colors = pData.colorsTri;

        this.useVertexColors = useVertexColors;
        this.useUniformNormalOrientation = useUniformNormalOrientation;

        this.numFaces = vCoords.length / (vPerVertex * vertexPerFace);
        this.numEdges = numFaces * edgesPerFace;
    }

    public ArrayList<PolygonData> calculateTriStrip() {
        edges = calculateEdges();
        edgesConnected = generateConnectedEdges(edges);
        usedFaces = new boolean[numFaces];
        ArrayList<ArrayList<Integer>> strips = new ArrayList<>();
        ArrayList<Integer> looseTris = new ArrayList<>();
        for (int i = 0; i < numFaces; i++) {
            if (!usedFaces[i]) {
                ArrayList<ArrayList<Integer>> stripCandidates = new ArrayList<>();
                for (int j = 0; j < edgesPerFace; j++) {
                    ArrayList<Integer> stripForward = getStripVertexIndices(i, j, true);
                    if (stripContainsFace(stripForward, i)) {
                        stripCandidates.add(stripForward);
                    }
                    ArrayList<Integer> stripBackward = getStripVertexIndices(i, j, false);
                    if (stripContainsFace(stripBackward, i)) {
                        stripCandidates.add(stripBackward);
                    }
                }

                ArrayList<Integer> longestStrip;
                if (stripCandidates.size() > 0) {
                    longestStrip = getLongestStrip(stripCandidates);
                } else {
                    longestStrip = new ArrayList<>();
                    for (int j = 0; j < edgesPerFace; j++) {
                        longestStrip.add(i + j);
                    }
                }
                disableFacesUsedByStrip(longestStrip);

                if (longestStrip.size() > vertexPerFace) {
                    strips.add(longestStrip);
                } else {
                    looseTris.addAll(longestStrip);
                }
            }
        }

        ArrayList<PolygonData> pDataStrips = new ArrayList<>();
        if (looseTris.size() > 0) {
            PolygonData pDataTriangles = new PolygonData();
            pDataTriangles.initTrisVertices(looseTris.size());
            for (int i = 0; i < looseTris.size(); i++) {
                copyVertexData(vCoords, looseTris.get(i) * vPerVertex, pDataTriangles.vCoordsTri, i * vPerVertex, vPerVertex);
                copyVertexData(tCoords, looseTris.get(i) * tPerVertex, pDataTriangles.tCoordsTri, i * tPerVertex, tPerVertex);
                copyVertexData(nCoords, looseTris.get(i) * nPerVertex, pDataTriangles.nCoordsTri, i * nPerVertex, nPerVertex);
                if (useVertexColors) {
                    copyVertexData(colors, looseTris.get(i) * cPerVertex, pDataTriangles.colorsTri, i * cPerVertex, cPerVertex);
                }
            }
            pDataStrips.add(pDataTriangles);
        } else {
            pDataStrips.add(null);
        }
        for (int i = 0; i < strips.size(); i++) {
            PolygonData pDataStrip = new PolygonData();
            pDataStrip.initTrisVertices(strips.get(i).size());
            for (int j = 0; j < strips.get(i).size(); j++) {
                copyVertexData(vCoords, strips.get(i).get(j) * vPerVertex, pDataStrip.vCoordsTri, j * vPerVertex, vPerVertex);
                copyVertexData(tCoords, strips.get(i).get(j) * tPerVertex, pDataStrip.tCoordsTri, j * tPerVertex, tPerVertex);
                copyVertexData(nCoords, strips.get(i).get(j) * nPerVertex, pDataStrip.nCoordsTri, j * nPerVertex, nPerVertex);
                if (useVertexColors) {
                    copyVertexData(colors, strips.get(i).get(j) * cPerVertex, pDataStrip.colorsTri, j * cPerVertex, cPerVertex);
                }
            }
            pDataStrips.add(pDataStrip);
        }

        return pDataStrips;
    }

    private boolean stripContainsFace(ArrayList<Integer> strip, int faceIndex) {
        for (int i = 0; i < strip.size(); i++) {
            if (strip.get(i) / edgesPerFace == faceIndex) {
                return true;
            }
        }
        return false;
    }

    private void copyVertexData(float[] src, int srcOffset, float[] dst, int dstOffset, int elePerVertex) {
        for (int i = 0; i < elePerVertex; i++) {
            dst[dstOffset + i] = src[srcOffset + i];
        }
    }

    private ArrayList<Integer> getStripVertexIndices(int faceIndex, int edgeIndexInFace, boolean forward) {
        boolean[] usedFacesInStrip = new boolean[numFaces];

        //Edges backward
        int edgeIndex = getEdgeIndex(faceIndex, edgeIndexInFace);
        int previousEdgeIndex = edgeIndex;
        usedFacesInStrip[edgeIndex / edgesPerFace] = true;

        boolean firstEdge = forward;
        while (isEdgeConnectedToNewFace(edgeIndex, usedFacesInStrip)) {
            int connectedEdgeIndex = edgesConnected.get(edgeIndex).get(0);
            previousEdgeIndex = connectedEdgeIndex;
            if (firstEdge) {
                edgeIndex = getNextEdgeIndex(connectedEdgeIndex);
            } else {
                edgeIndex = getPrevEdgeIndex(connectedEdgeIndex);
            }
            usedFacesInStrip[edgeIndex / edgesPerFace] = true;

            firstEdge = !firstEdge;
        }
        int firstVertexIndex = getOppositeVertexIndex(previousEdgeIndex);

        //Get full strip starting from first vertex
        ArrayList<Integer> vertexIndices = new ArrayList<>();
        vertexIndices.add(firstVertexIndex);
        //vertexIndices.add(getPrevEdgeIndex(vertexIndices.get(0)));
        //vertexIndices.add(getPrevEdgeIndex(vertexIndices.get(1)));
        vertexIndices.add(getNextEdgeIndex(vertexIndices.get(0)));
        vertexIndices.add(getNextEdgeIndex(vertexIndices.get(1)));

        usedFacesInStrip = new boolean[numFaces];
        edgeIndex = getOppositeEdgeIndex(firstVertexIndex);
        firstEdge = false;
        while (isEdgeConnectedToNewFace(edgeIndex, usedFacesInStrip)) {
            int connectedEdgeIndex = edgesConnected.get(edgeIndex).get(0);
            if (firstEdge) {
                edgeIndex = getNextEdgeIndex(connectedEdgeIndex);
            } else {
                edgeIndex = getPrevEdgeIndex(connectedEdgeIndex);
            }
            usedFacesInStrip[edgeIndex / edgesPerFace] = true;

            vertexIndices.add(getOppositeVertexIndex(connectedEdgeIndex));

            firstEdge = !firstEdge;
        }

        return vertexIndices;
    }

    private ArrayList<Integer> getLongestStrip(ArrayList<ArrayList<Integer>> strips) {
        int maxSize = -1;
        int maxIndex = 0;
        for (int i = 0; i < strips.size(); i++) {
            if (strips.get(i).size() > maxSize) {
                maxSize = strips.get(i).size();
                maxIndex = i;
            }
        }
        return strips.get(maxIndex);
    }

    private Edge getConnectedEdge(int edgeIndex) {
        return edges.get(edgesConnected.get(edgeIndex).get(0));
    }

    private boolean isEdgeConnected(int edgeIndex) {
        return edgesConnected.get(edgeIndex).size() > 0;
    }

    private boolean isEdgeConnectedToNewFace(int edgeIndex, boolean[] localUsedFaces) {
        if (edgesConnected.get(edgeIndex).size() > 0) {
            int faceIndex = edgesConnected.get(edgeIndex).get(0) / edgesPerFace;
            if (!usedFaces[faceIndex] && !localUsedFaces[faceIndex]) {
                return true;
            }
        }
        return false;
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

    private void disableFacesUsedByStrip(ArrayList<Integer> strip) {
        for (int i = 0; i < strip.size(); i++) {
            usedFaces[strip.get(i) / edgesPerFace] = true;
        }
    }

    private int getEdgeIndex(int faceIndex, int edgeIndexInFace) {
        return faceIndex * edgesPerFace + edgeIndexInFace;
    }

    private Edge getEdge(int faceIndex, int edgeIndexInFace) {
        return edges.get(getEdgeIndex(faceIndex, edgeIndexInFace));
    }

    private int getNextEdgeIndex(int edgeIndex) {
        int newIndex = edgeIndex + 1;
        return newIndex - (newIndex % edgesPerFace == 0 ? edgesPerFace : 0);
    }

    private int getPrevEdgeIndex(int edgeIndex) {
        int newIndex = edgeIndex - 1;
        return newIndex + (edgeIndex % edgesPerFace == 0 ? edgesPerFace : 0);
    }

    private int getOppositeVertexIndex(int edgeIndex) {
        int sum = edges.get(edgeIndex).vertexIndex1 + edges.get(edgeIndex).vertexIndex2;
        int mod = edges.get(edgeIndex).vertexIndex1 / edgesPerFace;
        return (mod * 2 + mod + 1) * edgesPerFace - sum;
    }

    private int getOppositeEdgeIndex(int vertexIndex) {
        int newIndex = vertexIndex + 1;
        return newIndex - (newIndex % edgesPerFace == 0 ? edgesPerFace : 0);
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
            if (useUniformNormalOrientation) {
                if (useVertexColors) {
                    if (sameEdgeCoords(vCoords, vPerVertex, other)
                            && sameEdgeCoords(tCoords, tPerVertex, other)
                            && sameEdgeCoords(colors, cPerVertex, other)) {
                        return true;
                    }
                } else {
                    if (sameEdgeCoords(vCoords, vPerVertex, other)
                            && sameEdgeCoords(tCoords, tPerVertex, other)) {
                        return true;
                    }
                }
            } else {
                if (useVertexColors) {
                    if (sameEdgeCoords(vCoords, vPerVertex, other)
                            && sameEdgeCoords(tCoords, tPerVertex, other)
                            && sameEdgeCoords(nCoords, nPerVertex, other)
                            && sameEdgeCoords(colors, cPerVertex, other)) {
                        return true;
                    }
                } else {
                    if (sameEdgeCoords(vCoords, vPerVertex, other)
                            && sameEdgeCoords(tCoords, tPerVertex, other)
                            && sameEdgeCoords(nCoords, nPerVertex, other)) {
                        return true;
                    }
                }
            }

            return false;
        }

    }

}
