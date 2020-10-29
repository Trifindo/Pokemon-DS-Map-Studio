package editor.buildingeditor2.tileset;

import editor.nsbtx2.NsbtxPalette;
import editor.nsbtx2.NsbtxTexture;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Trifindo, JackHack96
 */
public class PaletteDisplay extends JPanel {
    private static final int tileSize = 10;
    private static final int maxNumColors = 256;
    private static final int cols = 16;
    private static final int rows = 16;

    private static final Color borderColor = new Color(102, 102, 102);
    private static final Color selectionFillColor = new Color(255, 0, 0, 120);
    private static final Color selectionBorderColor = Color.red;
    private BufferedImage backImg;
    public ArrayList<Color> palette;

    public PaletteDisplay() {
        initComponents();

        setPreferredSize(new Dimension(tileSize * cols, tileSize * rows));

        backImg = createBackImg(createNoPaletteImg());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backImg != null) {
            g.drawImage(backImg, 0, 0, null);
        }

        if (palette != null) {
            for (int i = 0; i < palette.size(); i++) {
                g.setColor(palette.get(i));
                int x = i % cols;
                int y = i / cols;
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);

                g.setColor(borderColor);
                g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        g.setColor(borderColor);
        g.drawRect(0, 0, cols * tileSize - 1, rows * tileSize - 1);

    }

    public void updatePalette(NsbtxTexture nsbtxTex, NsbtxPalette nsbtxPal) {
        if (nsbtxPal != null) {
            if (nsbtxTex == null) {
                this.palette = nsbtxPal.getColors(nsbtxPal.getDataSize() / 2);
            } else {
                this.palette = nsbtxPal.getColors(nsbtxTex.getNumColors());
            }
        } else {
            this.palette = null;
        }
    }

    private BufferedImage createNoPaletteImg() {
        BufferedImage img = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

        g2d.setColor(new Color(255, 120, 120));
        g2d.drawLine(0, 0, img.getWidth(), img.getHeight());
        g2d.drawLine(img.getWidth(), 0, 0, img.getHeight());

        g2d.setColor(borderColor);
        g2d.drawRect(0, 0, img.getWidth(), img.getHeight());

        return img;
    }

    private BufferedImage createBackImg(BufferedImage noPalImg) {
        BufferedImage img = new BufferedImage(tileSize * cols, tileSize * rows,
                BufferedImage.TYPE_INT_RGB);

        Graphics g = img.getGraphics();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                g.drawImage(noPalImg, i * tileSize, j * tileSize, null);
            }
        }
        return img;
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

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
