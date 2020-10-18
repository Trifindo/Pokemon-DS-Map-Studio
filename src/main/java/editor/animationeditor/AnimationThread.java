
package editor.animationeditor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Trifindo
 */
public class AnimationThread extends Thread {

    private AnimationHandler animHandler;
    private volatile boolean running = true;

    public AnimationThread(AnimationHandler animHandler) {
        this.animHandler = animHandler;
    }

    @Override
    public void run() {
        while (running) {
            animHandler.incrementFrameIndex();
            animHandler.repaintDialog();

            try {
                Thread.sleep((long) ((animHandler.getCurrentDelay() / 30.0f) * 1000));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void terminate() {
        this.running = false;
    }

    public boolean isRunnning() {
        return running;
    }

}
