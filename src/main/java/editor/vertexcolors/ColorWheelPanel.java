package editor.vertexcolors;

import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class ColorWheelPanel extends JPanel {

    private static final int size = 101;
    private static final int circleSize = 6;
    private static final int margin = 6;
    private BufferedImage colorWheelImg;

    private int currentX = size / 2;
    private int currentY = size / 2;

    private float brightness = 1.0f;

    public ColorWheelPanel() {
        initComponents();

        this.setPreferredSize(new Dimension(size + margin * 2, size + margin * 2));

        colorWheelImg = generateColorWheel(size, brightness);
    }

    private void formMouseDragged(MouseEvent evt) {
        int x = evt.getX() - margin;
        int y = evt.getY() - margin;

        int distToCenter = (int) Math.sqrt((x - size / 2) * (x - size / 2) + (y - size / 2) * (y - size / 2));
        if (distToCenter < size / 2) {
            currentX = x;
            currentY = y;
        } else {
            float angle = (float) Math.atan2(x - size / 2, y - size / 2);
            currentX = (int) ((size / 2) * Math.sin(angle)) + size / 2;
            currentY = (int) ((size / 2) * Math.cos(angle)) + size / 2;
        }

        repaint();
    }

    private void formMousePressed(MouseEvent evt) {
        int x = evt.getX() - margin;
        int y = evt.getY() - margin;

        int distToCenter = (int) Math.sqrt((x - size / 2) * (x - size / 2) + (y - size / 2) * (y - size / 2));
        if (distToCenter < size / 2) {
            currentX = x;
            currentY = y;
        } else {
            float angle = (float) Math.atan2(x - size / 2, y - size / 2);
            currentX = (int) ((size / 2) * Math.cos(angle)) + size / 2;
            currentY = (int) ((size / 2) * Math.sin(angle)) + size / 2;
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (colorWheelImg != null) {
            g.drawImage(colorWheelImg, margin, margin, null);
        }

        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        g.setColor(Color.white);
        g.fillOval(currentX - circleSize / 2 + margin, currentY - circleSize / 2 + margin, circleSize, circleSize);

        g.setColor(Color.black);
        g.drawOval(currentX - circleSize / 2 + margin, currentY - circleSize / 2 + margin, circleSize, circleSize);

    }

    private BufferedImage generateColorWheel(int diameter, float brightness) {
        BufferedImage img = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

        final int radius = diameter / 2;
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color c;
                int distToCenter = (int) Math.sqrt((i - img.getWidth() / 2) * (i - img.getWidth() / 2) + (j - img.getHeight() / 2) * (j - img.getHeight() / 2));
                if (distToCenter < radius) {
                    float h = (float) (Math.atan2(i - img.getWidth() / 2, j - img.getHeight() / 2) / (2 * Math.PI));
                    float s = (float) distToCenter / radius;
                    c = Color.getHSBColor(h, s, brightness);
                } else {
                    c = new Color(1.0f, 1.0f, 1.0f, 0.0f);
                }
                img.setRGB(i, j, c.getRGB());
            }
        }

        Graphics g = img.getGraphics();

        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        g.setColor(Color.black);

        g.drawOval(0, 0, img.getWidth() - 1, img.getHeight() - 1);

        return img;
    }

    public Color getSelectedColor() {
        int distToCenter = (int) Math.sqrt((currentX - size / 2) * (currentX - size / 2) + (currentY - size / 2) * (currentY - size / 2));
        float h = (float) (Math.atan2(currentX - size / 2, currentY - size / 2) / (2 * Math.PI));
        float s = (float) distToCenter / (size / 2);

        return Color.getHSBColor(h, s, brightness);
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void updateImage() {
        this.colorWheelImg = generateColorWheel(size, brightness);
    }

    public void setHueSaturarion(float hue, float saturation) {
        float angle = hue * 2 * (float) Math.PI;
        float distToCenter = saturation * (size / 2);

        currentX = (int) (distToCenter * Math.sin(angle)) + size / 2;
        currentY = (int) (distToCenter * Math.cos(angle)) + size / 2;
    }

    public void setColor(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

        float angle = hsb[0] * 2 * (float) Math.PI;
        float distToCenter = hsb[1] * (size / 2);

        currentX = (int) (distToCenter * Math.sin(angle)) + size / 2;
        currentY = (int) (distToCenter * Math.cos(angle)) + size / 2;
        brightness = hsb[2];
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
