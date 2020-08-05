/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.imd.nodes;

import editor.imd.ImdAttribute;
import editor.imd.ImdNode;
import editor.imd.PolygonData;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class Primitive extends ImdNode {

    private static float[] lastNormalUsed = new float[]{0.0f, 0.0f, 0.0f};

    public Primitive(int index, String type, int vertexSize) {
        super("primitive");

        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("index", index));
                add(new ImdAttribute("type", type));
                add(new ImdAttribute("vertex_size", vertexSize));
            }
        };
    }

    public void calculateElementsWithStrips(PolygonData pData, boolean firstPrimitive,
            boolean isQuad, boolean useUniformNormals, boolean useVertexColors) {
        if (firstPrimitive) {
            ImdNode mtx = new ImdNode("mtx");
            mtx.attributes.add(new ImdAttribute("idx", 0));
            subnodes.add(mtx);
        }
        int numVertices;
        float[] vs;
        float[] ts;
        float[] ns;
        float[] cs;
        if (isQuad) {
            numVertices = pData.vCoordsQuad.length / 3;
            vs = pData.vCoordsQuad;
            ts = pData.tCoordsQuad;
            ns = pData.nCoordsQuad;
            cs = pData.colorsQuad;
        } else {
            numVertices = pData.vCoordsTri.length / 3;
            vs = pData.vCoordsTri;
            ts = pData.tCoordsTri;
            ns = pData.nCoordsTri;
            cs = pData.colorsTri;
        }

        addTexture(getSubArrayCell(ts, 0, 2));

        final float[] uniformNormals = new float[]{0.0f, 0.997f, 0.0f};
        float[] nCoords;
        if (useUniformNormals) {
            nCoords = uniformNormals;
        } else {
            nCoords = getSubArrayCell(ns, 0, 3);
        }
        addNormal(nCoords);

        float[] colors = null;
        if (useVertexColors) {
            colors = getSubArrayCell(cs, 0, 3);
            addColor(colors);
        }

        float[] vCoords = getSubArrayCell(vs, 0, 3);
        addVertexXYZ(vCoords);
        for (int v = 1; v < numVertices; v++) {//Shape (quad or tri)
            addTexture(getSubArrayCell(ts, v, 2));

            float[] newNCoords;
            if (useUniformNormals) {
                newNCoords = uniformNormals;
            } else {
                newNCoords = getSubArrayCell(ns, v, 3);
            }
            if (!sameArraysNormals(nCoords, newNCoords)) {
                addNormal(newNCoords);
            }
            nCoords = newNCoords;//New code

            float[] newColors;
            if (useVertexColors) {
                newColors = getSubArrayCell(cs, v, 3);
                //if(!sameArraysColors(colors, newColors)){
                addColor(newColors);
                //}
                colors = newColors;
            }

            float[] newVCoords = getSubArrayCell(vs, v, 3);
            if (sameCoordinate(vCoords, newVCoords, 0)) {
                addVertexYZ(newVCoords);
            } else if (sameCoordinate(vCoords, newVCoords, 1)) {
                addVertexXZ(newVCoords);
            } else if (sameCoordinate(vCoords, newVCoords, 2)) {
                addVertexXY(newVCoords);
            } else if (canUseDiff(vCoords, newVCoords)) {
                addVertexDiff(vCoords, newVCoords);
            } else {
                addVertexXYZ(newVCoords);
            }
            vCoords = newVCoords;
        }
    }

    public void calculateElements(PolygonData pData, boolean firstPrimitive,
            boolean isQuad, boolean useUniformNormals, boolean useVertexColors) {
        if (firstPrimitive) {
            ImdNode mtx = new ImdNode("mtx");
            mtx.attributes.add(new ImdAttribute("idx", 0));
            subnodes.add(mtx);
        }

        int vertexPerShape;
        int numShapes;
        float[] vs;
        float[] ts;
        float[] ns;
        float[] cs;
        if (isQuad) {
            vertexPerShape = 4;
            numShapes = pData.vCoordsQuad.length / (3 * vertexPerShape);
            vs = pData.vCoordsQuad;
            ts = pData.tCoordsQuad;
            ns = pData.nCoordsQuad;
            cs = pData.colorsQuad;
        } else {
            vertexPerShape = 3;
            numShapes = pData.vCoordsTri.length / (3 * vertexPerShape);
            vs = pData.vCoordsTri;
            ts = pData.tCoordsTri;
            ns = pData.nCoordsTri;
            cs = pData.colorsTri;
        }

        //boolean firstColor = true;
        float[] newColors;
        float[] colors = new float[]{1.0f, 1.0f, 1.0f}; //TODO Revise this!!

        //float[] uniformNormals = new float[]{0.8944f, 0.456f, 0.0f};
        float[] uniformNormals = new float[]{0.0f, 0.997f, 0.0f};
        boolean firstNormal = true;
        float[] newNCoords;
        float[] nCoords = uniformNormals;
        for (int i = 0, v = 0; i < numShapes; i++) {//Shape (quad or tri)
            addTexture(getSubArrayCell(ts, v, 2));

            if (useUniformNormals) {
                newNCoords = uniformNormals; //BW NORMAL
            } else {
                newNCoords = getSubArrayCell(ns, v, 3);
            }
            if (firstNormal) {
                addNormal(newNCoords);
                firstNormal = false;
            } else {
                if (!sameArraysNormals(nCoords, newNCoords)) {
                    addNormal(newNCoords); //BW NORMAL
                }
            }
            nCoords = newNCoords;//getSubArrayCell(ns, v, 3);

            if (useVertexColors) {
                newColors = getSubArrayCell(cs, v, 3);
                //if(firstColor){
                addColor(newColors);
                //firstColor = false;
                //}else{
                //    if(!sameArraysColors(colors, newColors)){
                //        addColor(newColors);
                //    }
                //}
                colors = newColors;
            }

            float[] vCoords = getSubArrayCell(vs, v, 3);
            addVertexXYZ(vCoords);
            v++;
            for (int j = 1; j < vertexPerShape; j++, v++) {//Vertex
                addTexture(getSubArrayCell(ts, v, 2));

                if (useUniformNormals) {
                    newNCoords = uniformNormals; //BW NORMAL
                } else {
                    newNCoords = getSubArrayCell(ns, v, 3);
                }
                if (!sameArraysNormals(nCoords, newNCoords)) {
                    addNormal(newNCoords);
                }
                nCoords = newNCoords;//New code

                if (useVertexColors) {
                    newColors = getSubArrayCell(cs, v, 3);
                    if (!isQuad) {//Triangles are problematic
                        addColor(newColors);
                    } else if (!sameArraysColors(colors, newColors)) {
                        addColor(newColors);
                    }
                    colors = newColors;
                }

                float[] newVCoords = getSubArrayCell(vs, v, 3);
                if (sameCoordinate(vCoords, newVCoords, 0)) {
                    addVertexYZ(newVCoords);
                } else if (sameCoordinate(vCoords, newVCoords, 1)) {
                    addVertexXZ(newVCoords);
                } else if (sameCoordinate(vCoords, newVCoords, 2)) {
                    addVertexXY(newVCoords);
                } else if (canUseDiff(vCoords, newVCoords)) {
                    addVertexDiff(vCoords, newVCoords);
                } else {
                    addVertexXYZ(newVCoords);
                }
                vCoords = newVCoords;
            }

            /*
            if(firstColor){
                firstColor = false;
            }*/
        }

    }

    private boolean canUseDiff(float[] arr1, float[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (Math.abs(arr1[i] - arr2[i]) >= 0.124f) {//TODO: 0.125
                return false;
            }
        }
        return true;
    }

    private boolean sameCoordinate(float[] arr1, float[] arr2, int coordInd) {
        //System.out.println("coord " + coordInd + ": " + arr1[coordInd] + " " + arr2[coordInd]);
        return Math.abs(arr1[coordInd] - arr2[coordInd]) < 0.0001f;
    }

    private boolean sameArraysNormals(float[] arr1, float[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (Math.abs(arr1[i] - arr2[i]) > 0.001f) {
                return false;
            }
        }
        return true;
    }

    private boolean sameArraysColors(float[] arr1, float[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (colorToDsColor(arr1[i]) != colorToDsColor(arr2[i])) {
                return false;
            }
        }
        return true;
    }

    private void addNormal(float[] normalCoords) {
        if (!sameArraysNormals(normalCoords, lastNormalUsed)) {
            ImdNode node = new ImdNode("nrm");
            node.attributes.add(new ImdAttribute("xyz", normalCoords));
            subnodes.add(node);
        }
        lastNormalUsed = normalCoords;
    }

    private void addTexture(float[] textureCoords) {
        ImdNode node = new ImdNode("tex");
        node.attributes.add(new ImdAttribute("st", textureCoords));
        subnodes.add(node);
    }

    private void addColor(float[] colors) {
        ImdNode node = new ImdNode("clr");
        int[] colorsRGB = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colorsRGB[i] = colorToDsColor(colors[i]);
        }
        node.attributes.add(new ImdAttribute("rgb", colorsRGB));
        subnodes.add(node);
    }

    private int colorToDsColor(float color) {
        return (int) Math.min(Math.max(color * 31.0f, 0.0f), 31.0f);
    }

    private void addVertexXYZ(float[] vertexCoords) {
        ImdNode node = new ImdNode("pos_xyz");
        node.attributes.add(new ImdAttribute("xyz", vertexCoords));
        subnodes.add(node);
    }

    private void addVertexYZ(float[] vertexCoords) {
        float[] yz = new float[]{vertexCoords[1], vertexCoords[2]};
        ImdNode node = new ImdNode("pos_yz");
        node.attributes.add(new ImdAttribute("yz", yz));
        subnodes.add(node);
    }

    private void addVertexXZ(float[] vertexCoords) {
        float[] xz = new float[]{vertexCoords[0], vertexCoords[2]};
        ImdNode node = new ImdNode("pos_xz");
        node.attributes.add(new ImdAttribute("xz", xz));
        subnodes.add(node);
    }

    private void addVertexXY(float[] vertexCoords) {
        float[] xy = new float[]{vertexCoords[0], vertexCoords[1]};
        ImdNode node = new ImdNode("pos_xy");
        node.attributes.add(new ImdAttribute("xy", xy));
        subnodes.add(node);
    }

    private void addVertexDiff(float[] vertexCoords, float[] newVertexCoords) {
        //Current - previous
        float[] diff = new float[vertexCoords.length];
        for (int i = 0; i < diff.length; i++) {
            diff[i] = newVertexCoords[i] - vertexCoords[i];
        }
        ImdNode node = new ImdNode("pos_diff");
        node.attributes.add(new ImdAttribute("xyz", diff));
        subnodes.add(node);
    }

    private static int getIndexSameCoord(float[] array1, float[] array2) {
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] == array2[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean lowerBitsFixedPointAreZero(float f, int numZeroBits, int numBitsDecPart) {
        int fix = (int) (f * (1 << numBitsDecPart));

        for (int i = 0; i < numZeroBits; i++) {
            if ((fix & (1 << i)) == 1 << i) {
                return false;
            }
        }
        return true;
    }

    private static float[] getSubArrayCell(float[] array, int cellIndex, int cellSize) {
        float[] subArray = new float[cellSize];
        for (int i = 0; i < cellSize; i++) {
            subArray[i] = array[cellIndex * cellSize + i];
        }
        return subArray;
    }

    private static float[] getCoordsAtVertex(float[] array, int vertexIndex) {
        float[] coords = new float[3];
        coords[0] = array[vertexIndex * 3];
        coords[1] = array[vertexIndex * 3 + 1];
        coords[2] = array[vertexIndex * 3 + 2];
        return coords;
    }

    public static void init(){
        lastNormalUsed = new float[]{0.0f, 0.0f, 0.0f};
    }
    
}
