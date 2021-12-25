
package formats.animationeditor;

import java.util.Arrays;

/**
 * @author Trifindo
 */
public class Animation {

    public static final int maxNameSize = 16;
    public static final int maxNumFrames = 18;

    private String name;
    private int[] frames;
    private int[] delays;

    public Animation(String name, int[] frames, int[] delays) {
        this.name = name;
        this.frames = frames;
        this.delays = delays;
    }

    public Animation(String name) {
        this.name = name;
        this.frames = new int[maxNumFrames];
        this.delays = new int[maxNumFrames];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = 255;
            delays[i] = 255;
        }
        frames[0] = 0;
        delays[0] = 1;
    }

    public int size() {
        return Arrays.stream(frames)
                .filter(f -> f == 255)
                .findFirst().orElse(frames.length);
    }

    public int getFrame(int index) {
        return frames[index];
    }

    public int getDelay(int index) {
        return delays[index];
    }

    public void setDelay(int index, int value) {
        this.delays[index] = Math.min(Math.max(value, 0), 254);
    }

    public void setFrame(int index, int value) {
        this.frames[index] = Math.min(Math.max(value, 0), 254);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addFrame(int frameIndex, int delay) {
        int size = size();
        if (size < frames.length) {
            frames[size] = frameIndex;
            delays[size] = delay;
            return true;
        } else {
            return false;
        }
    }

    public boolean removeFrame(int frameIndex) {
        int size = size();
        if (size() > 1 && frameIndex >= 0 && frameIndex < size) {
            for (int i = frameIndex; i < size - 1; i++) {
                frames[i] = frames[i + 1];
                delays[i] = delays[i + 1];
            }
            frames[size - 1] = 255;
            delays[size - 1] = 255;
            return true;
        }
        return false;
    }
}
