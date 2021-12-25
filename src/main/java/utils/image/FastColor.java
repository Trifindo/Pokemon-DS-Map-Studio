
package utils.image;

import java.awt.Color;

/**
 * @author Trifindo
 */
public class FastColor extends Color implements Comparable<FastColor> {

    private final int hashCode;

    public FastColor(int r, int g, int b, int a) {
        super(r, g, b, a);
        hashCode = super.hashCode();
    }

    public FastColor(float r, float g, float b, float a) {
        super(r, g, b, a);
        hashCode = super.hashCode();
    }

    public FastColor(int rgb, boolean hasAlpha) {
        super(rgb, hasAlpha);
        hashCode = super.hashCode();
    }

    public Color getColor() {
        return new Color(getRGB(), true);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        final FastColor other = (FastColor) obj;
        return this.hashCode == other.hashCode;
    }

    @Override
    public int compareTo(FastColor o) {
        return this.hashCode - o.hashCode;
    }
}
