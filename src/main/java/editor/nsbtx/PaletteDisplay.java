package editor.nsbtx;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.BasicStroke;
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
public class PaletteDisplay extends JPanel {

    private static final int tileSize = 16;
    private static final int maxNumColors = 16;
    private NsbtxHandler nsbtxHandler;

    private static final Color borderColor = Color.black;
    private static final Color selectionFillColor = new Color(255, 0, 0, 120);
    private static final Color selectionBorderColor = Color.red;
    private BufferedImage noPaletteImg;
    public ArrayList<Color> palette;

    public PaletteDisplay() {
        initComponents();

        setPreferredSize(new Dimension(tileSize * maxNumColors, tileSize));

        noPaletteImg = createNoPaletteImg();
    }

    private void formMousePressed(MouseEvent evt) {
        if (palette != null) {
            int index = evt.getX() / tileSize;
            if (index >= 0 && index < palette.size()) {
                nsbtxHandler.setColorIndexSelected(index);
                repaint();
                nsbtxHandler.getDialog().updateViewColorValues();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (palette != null) {
            for (int i = 0; i < palette.size(); i++) {
                g.setColor(palette.get(i));
                g.fillRect(i * tileSize, 0, tileSize, tileSize);

                g.setColor(borderColor);
                g.drawRect(i * tileSize, 0, tileSize, tileSize);
            }

            for (int i = palette.size(); i < maxNumColors; i++) {
                g.drawImage(noPaletteImg, i * tileSize, 0, null);
            }
        }

        g.setColor(borderColor);
        g.drawRect(0, 0, maxNumColors * tileSize - 1, tileSize - 1);

        if (palette != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            int index = nsbtxHandler.getColorIndexSelected();
            g.setColor(selectionBorderColor);
            g.drawRect(index * tileSize + 1, 1, tileSize - 2, tileSize - 2);
            //g.setColor(selectionFillColor);
            //g.fillRect(index * tileSize + 1, 1, tileSize - 2, tileSize - 2);
            /*
            g.setColor(Color.white);
            g.drawRect(index * tileSize + 1, 1, tileSize - 2, tileSize - 3);
            g.setColor(selectionBorderColor);
            g.drawRect(index * tileSize, 0, tileSize, tileSize - 1);*/

        }

    }

    public void init(NsbtxHandler nsbtxHandler) {
        this.nsbtxHandler = nsbtxHandler;
    }

    public void updatePalette() {
        if (nsbtxHandler != null) {
            if (nsbtxHandler.getNsbtx() != null) {
                this.palette = nsbtxHandler.getNsbtx().getPaletteColors(nsbtxHandler.getPaletteIndexSelected());
            }
        }
    }

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

    private BufferedImage createNoPaletteImg() {
        BufferedImage img = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

        g2d.setColor(Color.red);
        g2d.drawLine(0, 0, img.getWidth(), img.getHeight());
        g2d.drawLine(img.getWidth(), 0, 0, img.getHeight());

        g2d.setColor(borderColor);
        g2d.drawRect(0, 0, img.getWidth(), img.getHeight());

        return img;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                formMousePressed(e);
            }
        });

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
