
package editor.imd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Trifindo
 */
public class PolygonData {

    public float[] vCoordsQuad;
    public float[] tCoordsQuad;
    public float[] nCoordsQuad;
    public float[] colorsQuad;

    public float[] vCoordsTri;
    public float[] tCoordsTri;
    public float[] nCoordsTri;
    public float[] colorsTri;

    public PolygonData() {

    }

    public void groupByNormals(boolean isQuad) {
        int nPerPoly;
        float[] nCoords;
        if (isQuad) {
            nPerPoly = 4;
            nCoords = nCoordsQuad;
        } else {
            nPerPoly = 3;
            nCoords = nCoordsTri;
        }
        int numPolys = nCoords.length / (3 * nPerPoly);
        ArrayList<Poly> polys = new ArrayList<>(numPolys);
        for (int i = 0; i < numPolys; i++) {
            polys.add(new Poly(i, isQuad));
        }

        /*
        System.out.println("Polygon");
        for (int i = 0; i < polys.size(); i++) {
            polys.get(i).printData();
        }*/
        Collections.sort(polys);

        /*
        System.out.println("Sorted Polygon");
        for (int i = 0; i < polys.size(); i++) {
            polys.get(i).printData();
        }*/
        if (isQuad) {
            vCoordsQuad = sortByIndices(vCoordsQuad, 12, polys);
            tCoordsQuad = sortByIndices(tCoordsQuad, 8, polys);
            nCoordsQuad = sortByIndices(nCoordsQuad, 12, polys);
            colorsQuad = sortByIndices(colorsQuad, 12, polys);
        } else {
            vCoordsTri = sortByIndices(vCoordsTri, 9, polys);
            tCoordsTri = sortByIndices(tCoordsTri, 6, polys);
            nCoordsTri = sortByIndices(nCoordsTri, 9, polys);
            colorsTri = sortByIndices(colorsTri, 9, polys);
        }

    }

    private float[] sortByIndices(float[] array, int unitSize, ArrayList<Poly> polys) {
        float[] copy = new float[array.length];
        for (int i = 0; i < polys.size(); i++) {
            System.arraycopy(array, i * unitSize, copy, polys.get(i).index * unitSize, unitSize);
        }
        return copy;
    }

    public void initTrisVertices(int numVertices) {
        vCoordsTri = new float[numVertices * 3];
        tCoordsTri = new float[numVertices * 2];
        nCoordsTri = new float[numVertices * 3];
        colorsTri = new float[numVertices * 3];
    }

    public void initQuads(int numQuads) {
        vCoordsQuad = new float[numQuads * 4 * 3];
        tCoordsQuad = new float[numQuads * 4 * 2];
        nCoordsQuad = new float[numQuads * 4 * 3];
        colorsQuad = new float[numQuads * 4 * 3];
    }

    public void initTris(int numTris) {
        vCoordsTri = new float[numTris * 3 * 3];
        tCoordsTri = new float[numTris * 3 * 2];
        nCoordsTri = new float[numTris * 3 * 3];
        colorsTri = new float[numTris * 3 * 3];
    }

    public void initQuadStrip(int numEdges) {
        vCoordsQuad = new float[numEdges * 2 * 3];
        tCoordsQuad = new float[numEdges * 2 * 2];
        nCoordsQuad = new float[numEdges * 2 * 3];
        colorsQuad = new float[numEdges * 2 * 3];
    }

    public void scale(float scale) {
        scaleArray(vCoordsQuad, scale);
        scaleArray(vCoordsTri, scale);
    }

    public void scaleTextures(int width, int height) {
        for (int i = 0; i < tCoordsQuad.length; i += 2) {
            tCoordsQuad[i] *= width;
            tCoordsQuad[i + 1] *= height;
        }
        for (int i = 0; i < tCoordsTri.length; i += 2) {
            tCoordsTri[i] *= width;
            tCoordsTri[i + 1] *= height;
        }
    }

    public void fixTextureCoords(int imgWidth, int imgHeight) {
        fixTextureCoords(tCoordsQuad, 4, imgWidth, imgHeight);
        fixTextureCoords(tCoordsTri, 3, imgWidth, imgHeight);
    }

    private void fixTextureCoords(float[] tCoords, int vertexPerPolygon, int imgWidth, int imgHeight) {
        final int tPerVertex = 2;
        final int tPerPolygon = tPerVertex * vertexPerPolygon;
        for (int i = 0; i < tCoords.length; i += tPerPolygon) {
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;

            for (int j = 0; j < tPerPolygon; j += tPerVertex) {
                if (tCoords[i + j] < minX) {
                    minX = tCoords[i + j];
                }
                if (tCoords[i + j] > maxX) {
                    maxX = tCoords[i + j];
                }
                if (tCoords[i + j + 1] < minY) {
                    minY = tCoords[i + j + 1];
                }
                if (tCoords[i + j + 1] > maxY) {
                    maxY = tCoords[i + j + 1];
                }
            }

            float deltaX = Math.round(-((maxX + minX) / 2) / imgWidth) * imgWidth;
            float deltaY = Math.round(-((maxY + minY) / 2) / imgHeight) * imgHeight;

            for (int j = 0; j < tPerPolygon; j += tPerVertex) {
                tCoords[i + j] += deltaX;
                tCoords[i + j + 1] += deltaY;
            }

            final float minCoordAvailable = -2030.0f;
            final float maxCoordAvailable = 2030.0f;
            for (int j = 0; j < tPerPolygon; j += tPerVertex) {
                if (tCoords[i + j] * imgWidth < minCoordAvailable) {
                    tCoords[i + j] = minCoordAvailable / imgWidth;
                } else if (tCoords[i + j] * imgWidth > maxCoordAvailable) {
                    tCoords[i + j] = maxCoordAvailable / imgWidth;
                }
            }
            
            for (int j = 0; j < tPerPolygon; j += tPerVertex) {
                if (tCoords[i + j + 1] * imgHeight < minCoordAvailable) {
                    tCoords[i + j + 1] = minCoordAvailable / imgHeight;
                } else if (tCoords[i + j + 1] * imgHeight > maxCoordAvailable) {
                    tCoords[i + j + 1] = maxCoordAvailable / imgHeight;
                }
            }
        }
    }


    private void scaleArray(float[] array, float scale) {
        for (int i = 0; i < array.length; i++) {
            array[i] *= scale;
        }
    }

    public void fixNormals(float value) {
        scaleArray(nCoordsQuad, value);
        scaleArray(nCoordsTri, value);
    }

    public void rotateData() {
        rotateArrayX(vCoordsQuad);
        rotateArrayX(vCoordsTri);
        rotateArrayX(nCoordsQuad);
        rotateArrayX(nCoordsTri);
    }

    private void rotateArrayX(float[] array) {
        for (int i = 0; i < array.length; i += 3) {
            float temp = array[i + 1];
            array[i + 1] = array[i + 2];
            array[i + 2] = -temp;
        }
    }

    private class Poly implements Comparable<Poly> {

        private static final int nPerVertex = 3;
        private float[] nCoords;
        private int nPerPoly;
        private int index;
        private int vertexPerPoly;

        public Poly(int index, boolean isQuad) {
            this.index = index;
            if (isQuad) {
                this.vertexPerPoly = 4;
                nCoords = nCoordsQuad;
            } else {
                this.vertexPerPoly = 3;
                nCoords = nCoordsTri;
            }
            this.nPerPoly = nPerVertex * vertexPerPoly;

        }

        public void printData() {
            System.out.println(index + ": " + hashCode() + " / " + nCoords[index * nPerPoly + 0] + " " + nCoords[index * nPerPoly + 1] + " " + nCoords[index * nPerPoly + 2]);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Poly other = (Poly) obj;
            if (hashCode() != other.hashCode()) {
                return false;
            }
            /*
            for(int i = 0; i < nPerVertex; i++){
                if(Math.abs(nCoords[index * nPerPoly + i] - other.nCoords[index * nPerPoly + i]) > 0.0001f){
                    return false;
                }
            }*/
            return true;
        }

        @Override
        public int hashCode() {
            /*
            float[] nCoordsSubArray = new float[nPerVertex];
            System.arraycopy(nCoords, index * nPerPoly, nCoordsSubArray, 0, nPerVertex);
            for(int i = 0; i < nCoordsSubArray.length; i++){
                nCoordsSubArray[i] = round(nCoordsSubArray[i], 5);
            }*/

            int hash = 3;
            hash = 13 * hash + Float.floatToIntBits(round(nCoords[index * nPerPoly + 0], 3));
            hash = 13 * hash + Float.floatToIntBits(round(nCoords[index * nPerPoly + 1], 3));
            hash = 13 * hash + Float.floatToIntBits(round(nCoords[index * nPerPoly + 2], 3));

            return hash;

            //return Arrays.hashCode(nCoordsSubArray);
        }

        @Override
        public int compareTo(Poly o) {
            return (this.hashCode() / 100) - (o.hashCode() / 100);
        }

    }

    public static float round(float d, int decimalPlace) {
        //System.out.println(d);
        try {
            BigDecimal bd = new BigDecimal(Float.toString(d));
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bd.floatValue();
        } catch (NumberFormatException ex) {
            //System.out.println("ERROR: " + d);
            return 0.0f;
        }

    }

}
