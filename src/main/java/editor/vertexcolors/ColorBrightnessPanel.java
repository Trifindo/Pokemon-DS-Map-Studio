package editor.vertexcolors;

import java.awt.event.*;
import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class ColorBrightnessPanel extends JPanel {

    private static final int width = 13;
    private static final int height = 101;
    private static final int circleSize = 6;
    private static final int margin = 6;
    private final BufferedImage colorGradientImg;

    private final int currentX = width / 2;
    private int currentY = 0;

    public ColorBrightnessPanel() {
        initComponents();

        this.setPreferredSize(new Dimension(width + 2 * margin, height + 2 * margin));

        colorGradientImg = generateColorGradient(width, height);
    }

    private void formMouseDragged(MouseEvent evt) {
        int y = evt.getY() - margin;

        if (y < 0) {
            currentY = 0;
        } else if (y > height) {
            y = height;
        } else {
            currentY = y;
        }
        repaint();
    }

    private void formMousePressed(MouseEvent evt) {
        int y = evt.getY() - margin;

        if (y < 0) {
            currentY = 0;
        } else if (y > height) {
            y = height;
        } else {
            currentY = y;
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (colorGradientImg != null) {
            g.drawImage(colorGradientImg, margin, margin, null);
        }

        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        g.setColor(Color.white);
        g.fillOval(currentX - circleSize / 2 + margin, currentY - circleSize / 2 + margin, circleSize, circleSize);

        g.setColor(Color.black);
        g.drawOval(currentX - circleSize / 2 + margin, currentY - circleSize / 2 + margin, circleSize, circleSize);
    }

    private BufferedImage generateColorGradient(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                float value = 1.0f - (float) j / img.getHeight();
                Color c = new Color(value, value, value);
                img.setRGB(i, j, c.getRGB());
            }
        }

        BufferedImage imgComp = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = imgComp.getGraphics();

        g.fillRoundRect(0, 0, width - 1, height - 1, 8, 8);

        Graphics2D g2d = (Graphics2D) g;
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_IN);
        g2d.setComposite(ac);

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        g2d.drawImage(img, 0, 0, null);

        ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g2d.setComposite(ac);

        g.setColor(Color.black);
        g.drawRoundRect(0, 0, width - 1, height - 1, 8, 8);

        return imgComp;
    }

    public float getBrightness() {
        return 1.0f - (float) currentY / height;
    }

    public void setBrightness(float brightness) {
        this.currentY = (int) ((1.0f - brightness) * height);
    }

    public void setColor(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        this.currentY = (int) ((1.0f - hsb[2]) * height);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                formMouseDragged(e);
            }
        });
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
