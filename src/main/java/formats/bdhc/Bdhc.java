
package formats.bdhc;

import java.util.ArrayList;

/**
 * @author Trifindo
 */
public class Bdhc {

    public static final String fileExtension = "bdhc";

    public static final int sizePointDP = 12;
    public static final int sizeSlopeDP = 12;
    public static final int sizePlateDP = 12;
    public static final int sizeStripeDP = 10;

    private ArrayList<Plate> plates = new ArrayList<>();

    public Bdhc(ArrayList<Plate> plates) {
        this.plates = plates;
    }

    public Bdhc() {
        plates.add(new Plate());
        plates.add(new Plate());

    }

    public ArrayList<Plate> getPlates() {
        return plates;
    }

    public Plate getPlate(int index) {
        return plates.get(index);
    }

    public void addPlate() {
        plates.add(new Plate());
    }

    public void addPlate(Plate toDuplicate) {
        plates.add(new Plate(toDuplicate));
    }
}
