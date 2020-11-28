package formats.nsbtx2;

import javax.swing.*;
import javax.swing.GroupLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Trifindo, JackHack96
 */
public class PaletteDisplay2 extends JPanel {

    private static final int tileSize = 10;
    private static final int maxNumColors = 256;
    private static final int cols = 16;
    private static final int rows = 16;
    private NsbtxHandler2 nsbtxHandler;

    private static final Color borderColor = new Color(102, 102, 102);
    private static final Color selectionFillColor = new Color(255, 0, 0, 120);
    private static final Color selectionBorderColor = Color.red;
    private BufferedImage backImg;
    public ArrayList<Color> palette;

    public PaletteDisplay2() {
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

        if (palette != null) {
            /*//TODO: Finish this
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            int index = nsbtxHandler.getColorIndexSelected();
            g.setColor(selectionBorderColor);
            g.drawRect(index * tileSize + 1, 1, tileSize - 2, tileSize - 2);
             */
        }

    }

    public void init(NsbtxHandler2 nsbtxHandler) {
        this.nsbtxHandler = nsbtxHandler;
    }

    public void updatePalette() {
        if (nsbtxHandler != null) {
            if (nsbtxHandler.getNsbtx() != null) {
                NsbtxPalette nsbtxPal = nsbtxHandler.getSelectedPalette();
                if (nsbtxPal != null) {
                    if (!nsbtxHandler.getNsbtx().hasTextures()) {
                        this.palette = nsbtxPal.getColors(nsbtxPal.getDataSize() / 2);
                    } else {
                        this.palette = nsbtxPal.getColors(
                                nsbtxHandler.getSelectedTexture().getNumColors());
                    }
                } else {
                    this.palette = null;
                }
            }
        }
    }

    /*
    public void updatePaletteColor(int index){
        if (nsbtxHandler != null) {
            if (nsbtxHandler.getNsbtx() != null) {
                palette.set(index, nsbtxHandler.getNsbtx().getPaletteColor(
                        nsbtxHandler.getPaletteIndexSelected(), index));
            }
        }
    }

    public void updateSelectedPaletteColor(){
        if (nsbtxHandler != null) {
            if (nsbtxHandler.getNsbtx() != null) {
                int index = nsbtxHandler.getColorIndexSelected();
                palette.set(index, nsbtxHandler.getNsbtx().getPaletteColor(
                        nsbtxHandler.getPaletteIndexSelected(), index));
            }
        }
    }
     */
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
