
package formats.bdhc;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Trifindo
 */
public class Stripe {

    public ArrayList<Integer> plateIndices;
    public int y;

    public Stripe(int y) {
        this.y = y;
        this.plateIndices = new ArrayList<>();
    }

    public void sortPlateIndices(ArrayList<Plate> plates) {
        ArrayList<IndexAndPlate> indicesAndPlates = new ArrayList<>();

        for (int i = 0; i < plateIndices.size(); i++) {
            indicesAndPlates.add(new IndexAndPlate(plateIndices.get(i), plates.get(i)));
        }

        Collections.sort(indicesAndPlates);

        for (int i = 0; i < plateIndices.size(); i++) {
            plateIndices.set(i, indicesAndPlates.get(i).index);
        }

    }

    private class IndexAndPlate implements Comparable {
        public Plate plate;
        public int index;

        public IndexAndPlate(int index, Plate plate) {
            this.index = index;
            this.plate = plate;
        }

        @Override
        public int compareTo(Object o) {
            return Integer.compare(plate.x, ((IndexAndPlate) o).plate.x);
        }
    }

}
