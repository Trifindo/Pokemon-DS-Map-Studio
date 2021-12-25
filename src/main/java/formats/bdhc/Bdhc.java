
package formats.bdhc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trifindo
 */
public class Bdhc {

    public static final String fileExtension = "bdhc";

    public static final int sizePointDP = 12;
    public static final int sizeSlopeDP = 12;
    public static final int sizePlateDP = 12;
    public static final int sizeStripeDP = 10;

    private List<Plate> plates = new ArrayList<>();

    public Bdhc(List<Plate> plates) {
        this.plates = plates;
    }

    public Bdhc() {
        plates.add(new Plate());
        plates.add(new Plate());
    }

    public List<Plate> getPlates() {
        return plates;
    }

    public Plate getPlate(int index) {
        return plates.get(index);
    }

    public void addPlate() {
        plates.add(new Plate());
    }
}
