package formats.bdhc;

import javax.swing.*;
import java.awt.*;

/**
 * @author Trifindo, JackHack96
 */
public class AngleDisplay extends JPanel {
    private static final int size = 64;
    private static final int radius = 24;

    private int indexSlope1 = 0;
    private int indexSlope2 = 1;
    private boolean frontView = true;

    private BdhcHandler bdhcHandler;

    public AngleDisplay() {
        initComponents();
        setPreferredSize(new Dimension(size, size));
    }

    public void init(BdhcHandler bdhcHandler, int index1, int index2, boolean frontView) {
        this.bdhcHandler = bdhcHandler;
        this.indexSlope1 = index1;
        this.indexSlope2 = index2;
        this.frontView = frontView;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bdhcHandler != null) {
            int[] slopes = bdhcHandler.getSelectedPlate().getSlope();
            float angle = (float) Math.atan2(slopes[indexSlope1], slopes[indexSlope2]);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(Color.MAGENTA);
            final int angleRadius = (radius * 2) / 3;
            final int angleOffset = size / 2 - angleRadius;
            g2.setStroke(new BasicStroke(2));
            g.drawArc(angleOffset, angleOffset, 2 * angleRadius, 2 * angleRadius, 0, (int) (-(angle * 180) / Math.PI));

            g2.setStroke(new BasicStroke(2));
            //g.drawLine(size / 2, size / 2, size, size / 2);
            g.setColor(frontView ? Color.RED : Color.GREEN);
            g.drawLine(size / 2, size / 2, size, size / 2);
            g.setColor(Color.BLUE);
            g.drawLine(size / 2, size / 2, size / 2, 0);

            int circleOffset = size / 2 - radius;
            g.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g.drawOval(circleOffset, circleOffset, radius * 2, radius * 2);

            int x = (int) (Math.cos(angle) * radius);
            int y = (int) (Math.sin(angle) * radius);
            int lineOffset = size / 2;
            g.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(3));
            g.drawLine(lineOffset - x, lineOffset - y, lineOffset + x, lineOffset + y);
        }
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
