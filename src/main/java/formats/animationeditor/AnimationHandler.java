
package formats.animationeditor;

import formats.nsbtx2.Nsbtx2;
import formats.nsbtx2.NsbtxLoader2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Trifindo
 */
public class AnimationHandler {

    private Nsbtx2 nsbtx;
    private AnimationFile animationFile;
    private AnimationEditorDialog dialog;

    private List<BufferedImage> nsbtxImages;
    private int currentFrameIndex = 0;
    private AnimationThread animationThread;

    public AnimationHandler(AnimationEditorDialog dialog) {
        this.dialog = dialog;
    }

    public void readAnimationFile(String path) throws IOException {
        this.animationFile = new AnimationFile(path);
    }

    public void saveAnimationFile(String path) throws IOException {
        this.animationFile.saveAnimationFile(path);
    }

    public void readNsbtx(String path) throws IOException {
        nsbtx = NsbtxLoader2.loadNsbtx(path);
        readNsbtxImages();
    }

    public void readNsbtxImages() {
        if (nsbtx != null && nsbtx.hasTextures() && nsbtx.hasPalettes()) {
            nsbtxImages = new ArrayList<>(nsbtx.getTextures().size());
            for (int i = 0; i < nsbtx.getTextures().size(); i++) {
                nsbtxImages.add(nsbtx.getImage(i, 0));
            }
        }
    }

    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    public void setCurrentFrameIndex(int index) {
        this.currentFrameIndex = index;
    }

    public String getTextureName(int index) {
        if (nsbtx != null && animationFile != null) {
            Animation anim = getAnimationSelected();
            if (anim != null && index >= 0 && index < anim.size()) {
                int frameIndex = anim.getFrame(index);
                if (frameIndex >= 0 && frameIndex < nsbtx.getTextures().size()) {
                    return nsbtx.getTexture(frameIndex).getName();
                }
            }
        }
        return "";
    }

    public void incrementFrameIndex() {
        currentFrameIndex++;
        if (animationFile != null && getAnimationSelected() != null) {
            Animation anim = getAnimationSelected();
            if (currentFrameIndex >= anim.size()) {
                currentFrameIndex = 0;
            }
        }
    }

    public int getCurrentDelay() {
        if (getAnimationSelected() != null) {
            Animation anim = getAnimationSelected();
            return anim.getDelay(currentFrameIndex);
        } else {
            return 1;
        }
    }

    public BufferedImage getCurrentFrameImage() {
        if (nsbtxImages != null && animationFile != null) {
            Animation anim = getAnimationSelected();
            if (currentFrameIndex >= 0 && currentFrameIndex < anim.size()) {
                int frameIndex = anim.getFrame(currentFrameIndex);
                if (frameIndex >= 0 && frameIndex < nsbtxImages.size()) {
                    return nsbtxImages.get(frameIndex);
                }
            }
        }
        return null;
    }

    public int getCurrentNsbtxTextureIndex() {
        if (nsbtxImages != null && animationFile != null) {
            Animation anim = getAnimationSelected();
            if (currentFrameIndex >= 0 && currentFrameIndex < anim.size()) {
                int frameIndex = anim.getFrame(currentFrameIndex);
                if (frameIndex >= 0 && frameIndex < nsbtxImages.size()) {
                    return frameIndex;
                }
            }
        }
        return 0;
    }

    public BufferedImage getFrameImage(int index) {
        if (nsbtxImages != null && animationFile != null) {
            Animation anim = getAnimationSelected();
            if (index >= 0 && index < anim.size()) {
                int frameIndex = anim.getFrame(index);
                if (frameIndex >= 0 && frameIndex < nsbtxImages.size()) {
                    return nsbtxImages.get(frameIndex);
                }
            }
        }
        return null;
    }

    public void setCurrentDelay(int value) {
        if (getAnimationSelected() != null) {
            Animation anim = getAnimationSelected();
            if (currentFrameIndex >= 0 && currentFrameIndex < anim.size()) {
                anim.setDelay(currentFrameIndex, value);
            }
        }
    }

    public void setCurrentTexture(int value) {
        if (getAnimationSelected() != null) {
            Animation anim = getAnimationSelected();
            if (currentFrameIndex >= 0 && currentFrameIndex < anim.size()) {
                anim.setFrame(currentFrameIndex, value);
            }
        }
    }

    public Animation getAnimationSelected() {
        if (animationFile != null) {
            return animationFile.getAnimation(dialog.getAnimationSelectedIndex());
        } else {
            return null;
        }
    }

    public void addAnimation(String name) {
        animationFile.addAnimation(name);
    }

    public void removeAnimation(int index) {
        animationFile.removeAnimation(index);
    }

    public AnimationFile getAnimationFile() {
        return animationFile;
    }

    public Nsbtx2 getNsbtx() {
        return nsbtx;
    }

    public void repaintDialog() {
        dialog.repaintFrames();
    }

    public void playAnimation() {
        animationThread = new AnimationThread(this);
        animationThread.start();
        System.out.println("Started!");
    }

    public void pauseAnimation() {
        if (animationThread != null) {
            animationThread.terminate();
            try {
                animationThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(AnimationHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isAnimationRunning() {
        if (animationThread != null) {
            return animationThread.isRunnning();
        } else {
            return false;
        }
    }
}
