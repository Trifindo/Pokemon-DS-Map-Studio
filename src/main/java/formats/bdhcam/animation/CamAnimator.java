package formats.bdhcam.animation;

import formats.bdhcam.BdhcamHandler;
import editor.handler.MapEditorHandler;

public abstract class CamAnimator extends Thread{

    protected MapEditorHandler handler;
    protected BdhcamHandler bdhcamHandler;

    private final int TICKS_PER_SECOND = 30;
    private final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    private final int MAX_FRAMESKIP = 5;
    protected volatile boolean running;

    public CamAnimator(MapEditorHandler handler, BdhcamHandler bdhcamHandler){
        this.handler = handler;
        this.bdhcamHandler = bdhcamHandler;
    }

    @Override
    public void run() {
        double next_game_tick = System.currentTimeMillis();
        int loops;

        running = true;
        while (running) {
            loops = 0;
            while (System.currentTimeMillis() > next_game_tick && loops < MAX_FRAMESKIP) {
                updateLogic();
                repaint();

                next_game_tick += SKIP_TICKS;
                loops++;
            }
        }
    }

    public void finish(){
        this.running = false;
    }

    protected abstract void updateLogic();

    protected abstract void repaint();
}
