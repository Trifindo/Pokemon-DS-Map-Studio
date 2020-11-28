package formats.animationeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class AnimationDisplay extends JPanel {
    private static final int size = 160;
    private AnimationHandler animHandler;
    private final BufferedImage backImg;

    private final float scale = 2.0f;

    public AnimationDisplay() {
        initComponents();
        this.setPreferredSize(new Dimension(size, size));

        backImg = createBackImg();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backImg != null) {
            g.drawImage(backImg, 0, 0, null);
        }

        if (animHandler != null) {
            BufferedImage img = animHandler.getCurrentFrameImage();
            if (img != null) {
                int x = getWidth() / 2 - img.getWidth() / 2;
                int y = getHeight() / 2 - img.getHeight() / 2;
                g.drawImage(img, x, y, null);
            }
        }
    }

    public void init(AnimationHandler animHandler) {
        this.animHandler = animHandler;
    }

    public BufferedImage createBackImg() {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        int tileSize = 8;
        int numCells = size / tileSize;
        Color[] colors = new Color[]{Color.white, Color.lightGray};
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                g.setColor(colors[(i + j) % 2]);
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
        return img;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setForeground(new Color(102, 102, 102));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGap(0, 300, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
