/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.imd;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
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

    private ArrayList<Edge> edges;
    private ArrayList<ArrayList<Integer>> edgesConnected;
    private boolean[] usedFaces;

    private boolean useUniformNormalOrientation;
    private boolean useVertexColors;

    public StripeCalculator(PolygonData pData, boolean isQuad,
            boolean useUniformNormalOrientation, boolean useVertexColors) {
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

    public ArrayList<PolygonData> calculateTriStrip() {
        //Calculate edges
        edges = calculateEdges();

        //Generate a list of indices of edges conected
        edgesConnected = generateConnectedEdges(edges);

        //Calculate connected faces
        ArrayList<ArrayList<Edge>> connectedEdgesLocal = new ArrayList<>();
        ArrayList<ArrayList<Integer>> connectedFaces = calculateAllConnectedQuadFaces(connectedEdgesLocal);

        return null;//TODO change this
    }

    public ArrayList<PolygonData> calculateQuadStrip() {
        //Calculate edges
        edges = calculateEdges();

        //Generate a list of indices of edges conected
        edgesConnected = generateConnectedEdges(edges);

        //Calculate connected faces
        ArrayList<ArrayList<Edge>> connectedEdgesLocal = new ArrayList<>();
        ArrayList<ArrayList<Integer>> connectedFaces = calculateAllConnectedQuadFaces(connectedEdgesLocal);

        ArrayList<ArrayList<Integer>> stripFaces = new ArrayList<>();
        ArrayList<ArrayList<Edge>> stripEdges = new ArrayList<>();
        ArrayList<Integer> looseFaces = new ArrayList<>();
        for (int i = 0; i < connectedFaces.size(); i++) {
            if (connectedFaces.get(i).size() > 1) {
                stripFaces.add(connectedFaces.get(i));
                stripEdges.add(connectedEdgesLocal.get(i));
            } else {
                looseFaces.add(connectedFaces.get(i).get(0));
            }
        }

        ArrayList<PolygonData> pDataStrips = new ArrayList<>(stripFaces.size() + looseFaces.size());
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
            for (int i = 0; i < stripEdges.size(); i++) {
                PolygonData newPdata = new PolygonData();
                newPdata.initQuadStrip(stripEdges.get(i).size());
                for (int j = 0; j < stripEdges.get(i).size(); j++) {
                    copyEdgeData(newPdata, stripEdges.get(i).get(j), j);
                }
                pDataStrips.add(newPdata);
            }
        } else {
            for (int i = 0; i < stripFaces.size(); i++) {
                PolygonData newPdata = new PolygonData();
                newPdata.initTris(stripFaces.get(i).size());
                for (int j = 0; j < stripFaces.get(i).size(); j++) {
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

    private void copyEdgeCoords(float[] coordsData, float[] newCoordsData,
            Edge edge, int coordsPerVertex, int dstEdgeIndex) {
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
            if (!localUsedFaces[faceIndex] && !usedFaces[faceIndex]) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ArrayList<Integer>> calculateAllConnectedTriFaces(
            ArrayList<ArrayList<Edge>> edgesLocalConnected) {
        ArrayList<ArrayList<Integer>> connectedFaces = new ArrayList<>();
        usedFaces = new boolean[numFaces];
        for (int i = 0, c = 0; i < numFaces; i++) {
            if (!usedFaces[i]) {
                ArrayList<Edge> edgesLocal1 = new ArrayList<>();
                ArrayList<Edge> edgesLocal2 = new ArrayList<>();
                ArrayList<Edge> edgesLocal3 = new ArrayList<>();

                //ArrayList<Integer> connected1 = connectedTriFaces(i, 0, 2, edgesLocalX);
                c++;
            }
        }
        return connectedFaces;
    }

    private ArrayList<ArrayList<Integer>> calculateAllConnectedQuadFaces(
            ArrayList<ArrayList<Edge>> edgesLocalConnected) {
        ArrayList<ArrayList<Integer>> connectedFaces = new ArrayList<>();
        usedFaces = new boolean[numFaces];
        for (int i = 0, c = 0; i < numFaces; i++) {
            if (!usedFaces[i]) {
                ArrayList<Edge> edgesLocalX = new ArrayList<>();
                ArrayList<Edge> edgesLocalY = new ArrayList<>();
                ArrayList<Integer> connectedX = connectedQuadFaces(i, 0, 2, edgesLocalX);
                ArrayList<Integer> connectedY = connectedQuadFaces(i, 1, 3, edgesLocalY);
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

    private ArrayList<Integer> connectedTriFaces(int startFaceIndex) {
        ArrayList<Integer> connectedFaces = new ArrayList<>();
        boolean[] localUsedFaces = new boolean[usedFaces.length];
        localUsedFaces[startFaceIndex] = true;

        //(...)
        return null;
    }

    private ArrayList<Integer> connectedQuadFaces(int startFaceIndex, int forwardEdgeLocalIndex,
            int backwardEdgeLocalIndex, ArrayList<Edge> edgesLocalConnected) {
        ArrayList<Integer> connectedFaces = new ArrayList<>();
        boolean[] localUsedFaces = new boolean[usedFaces.length];
        localUsedFaces[startFaceIndex] = true;
        ArrayList<Edge> edgesForward = new ArrayList<>();
        ArrayList<Edge> edgesBackward = new ArrayList<>();
        ArrayList<Integer> forward = connectedFacesOneDirection(startFaceIndex, forwardEdgeLocalIndex, localUsedFaces, edgesForward);
        ArrayList<Integer> backward = connectedFacesOneDirection(startFaceIndex, backwardEdgeLocalIndex, localUsedFaces, edgesBackward);

        Collections.reverse(forward);
        connectedFaces.addAll(forward);
        connectedFaces.add(startFaceIndex);
        connectedFaces.addAll(backward);

        Collections.reverse(edgesForward);
        edgesLocalConnected.addAll(edgesForward);
        for (int i = 0; i < edgesBackward.size(); i++) {
            edgesBackward.get(i).flip();
        }
        edgesLocalConnected.addAll(edgesBackward);

        return connectedFaces;
    }

    private ArrayList<Integer> connectedFacesOneDirection(int startFaceIndex,
            int startEdgeIndex, boolean[] localUsedFaces, ArrayList<Edge> edgesLocalConnected) {
        ArrayList<Integer> connectedFaces = new ArrayList<>();
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

    private void printCoords(int vertexIndex) {
        for (int i = 0; i < vPerVertex; i++) {
            System.out.print(vCoords[vertexIndex * vPerVertex + i] + " ");
        }
        System.out.println();
    }

    private void printConectedEdgesCoords(ArrayList<Edge> edges,
            ArrayList<ArrayList<Integer>> edgesConnected) {
        for (int i = 0; i < edgesConnected.size(); i++) {
            if (edgesConnected.get(i).size() > 0) {
                Edge edge = edges.get(edgesConnected.get(i).get(0));
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
            }else{
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
