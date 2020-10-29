
package tileset;

import java.util.ArrayList;

import utils.Utils;

/**
 * @author Trifindo
 */
public class Face {

    public int[] vInd;
    public int[] tInd;
    public int[] nInd;
    public int[] cInd;
    public boolean isQuad;

    public Face(boolean isQuad) {
        int nElements;
        if (isQuad) {
            nElements = 4;
        } else {
            nElements = 3;
        }

        vInd = new int[nElements];
        tInd = new int[nElements];
        nInd = new int[nElements];
        cInd = new int[nElements];

        this.isQuad = isQuad;

    }

    public Face deepClone() {
        Face f = new Face(isQuad);
        f.vInd = Utils.cloneIntArray(vInd);
        f.tInd = Utils.cloneIntArray(tInd);
        f.nInd = Utils.cloneIntArray(nInd);
        f.cInd = Utils.cloneIntArray(cInd);
        return f;
    }


    public static ArrayList<Face> cloneArrayList(ArrayList<Face> array) {
        ArrayList<Face> newArray = new ArrayList<>(array.size());
        array.forEach((f) -> {
            newArray.add(f.deepClone());
        });
        return newArray;
    }

    public static void incrementAllIndices(ArrayList<Face> faces, int vIncrement,
                                           int tIncrement, int nIncrement, int cIncrement) {
        for (Face f : faces) {
            for (int i = 0; i < f.vInd.length; i++) {
                f.vInd[i] = f.vInd[i] + vIncrement;
            }
            for (int i = 0; i < f.tInd.length; i++) {
                f.tInd[i] = f.tInd[i] + tIncrement;
            }
            for (int i = 0; i < f.nInd.length; i++) {
                f.nInd[i] = f.nInd[i] + nIncrement;
            }
            for (int i = 0; i < f.cInd.length; i++) {
                f.cInd[i] = f.cInd[i] + cIncrement;
            }
        }
    }

}
