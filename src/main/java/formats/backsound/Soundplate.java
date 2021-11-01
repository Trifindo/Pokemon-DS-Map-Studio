
package formats.backsound;

import editor.grid.MapGrid;

import java.awt.Rectangle;

/**
 * @author Trifindo
 */
public class Soundplate {
    public static final byte MAX_VOLUME = 2;
    public static final byte SOUNDS_COUNT = 15;
    public int soundCode;
    public int volume;
    public int byte3;
    public int byte4;
    public int x, y;
    public int width, height;

    public Soundplate(int soundCode, int volume, int byte3, int byte4, int x, int y, int width, int height) {
        this.soundCode = fitInBounds(soundCode, 0, SOUNDS_COUNT);
        this.volume = fitInBounds(volume, 0, MAX_VOLUME);
        this.byte3 = byte3;
        this.byte4 = byte4;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Soundplate() {
        this(0, 0, 0, 0, MapGrid.cols / 2, MapGrid.rows / 2, 2, 2);
    }

    public Soundplate(Soundplate toCopy) {
        this.soundCode = toCopy.soundCode;
        this.volume = toCopy.volume;
        this.byte3 = toCopy.byte3;
        this.byte4 = toCopy.byte4;
        this.x = toCopy.x;
        this.y = toCopy.y;
        this.width = toCopy.width;
        this.height = toCopy.height;
    }

    private static int fitInBounds(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getSoundCode() {
        return soundCode;
    }

    public int getVolume() {
        return volume;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSoundCode(int soundCode) {
        this.soundCode = soundCode;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }


}
