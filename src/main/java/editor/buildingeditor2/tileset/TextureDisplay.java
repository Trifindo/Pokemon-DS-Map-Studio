package editor.buildingeditor2.tileset;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class TextureDisplay extends JPanel {

    private static final int size = 160;
    private BufferedImage img;
    private BufferedImage backImg;

    public TextureDisplay() {
        initComponents();

        setPreferredSize(new Dimension(size, size));

        backImg = createBackImg();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========

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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.backImg != null) {
            g.drawImage(backImg, 0, 0, null);
        }

        if (this.img != null) {
            int x = getWidth() / 2 - img.getWidth() / 2;
            int y = getHeight() / 2 - img.getHeight() / 2;
            g.drawImage(img, x, y, null);
        }

    }

    public void updateImage(BufferedImage updatedImage) {
        if (updatedImage != null) {
            this.img = updatedImage;
        } else {
            this.img = null;
        }
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

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
