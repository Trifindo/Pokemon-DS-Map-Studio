package formats.bdhc;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.*;

/**
 * @author Trifindo, JackHack96
 */
public class AngleDisplay3D extends JPanel {
    private BdhcHandler bdhcHandler;

    private static final int size = 64;
    private static final int radius = 24;

    private final float[] planeCoords = {
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f

    };

    private final float[] cameraPos = {0.0f, -1.0f, 0.0f};
    private final float cameraY = 2.0f;
    private final float fov = 2.0f;

    public AngleDisplay3D() {
        initComponents();
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bdhcHandler != null) {
            int[] slopes = bdhcHandler.getSelectedPlate().getSlope();
            float angle1 = (float) Math.atan2(slopes[0], slopes[1]);
            float angle2 = (float) Math.atan2(slopes[2], slopes[1]);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            float[] plane = new float[planeCoords.length];
            System.arraycopy(planeCoords, 0, plane, 0, planeCoords.length);

            float cos = (float) Math.cos(angle1);
            float sin = (float) Math.sin(angle1);
            for (int i = 0; i < plane.length; i += 3) {
                float x = plane[i];
                float y = plane[i + 2];
                plane[i] = (x * cos - y * sin);
                plane[i + 2] = (x * sin - y * cos);
            }

            cos = (float) Math.cos(angle2);
            sin = (float) Math.sin(angle2);
            for (int i = 0; i < plane.length; i += 3) {
                float x = plane[i + 1];
                float y = plane[i + 2];
                plane[i + 1] = (x * cos - y * sin);
                plane[i + 2] = (x * sin - y * cos);
            }

            Point[] points = new Point[4];
            for (int i = 0, c = 0; i < plane.length; i += 3, c++) {
                float depthFactor = (plane[i + 1] - cameraY) / fov;
                int x = (int) (plane[i] * depthFactor * radius);
                int z = (int) (plane[i + 2] * depthFactor * radius);
                points[c] = new Point(x, z);
            }

            int lineOffset = size / 2;
            g.setColor(Color.blue);
            for (int i = 0; i < points.length; i++) {
                int x1 = points[i].x;
                int y1 = points[i].y;
                int x2 = points[(i + 1) % points.length].x;
                int y2 = points[(i + 1) % points.length].y;
                g.drawLine(lineOffset + x1, lineOffset + y1, lineOffset + x2, lineOffset + y2);
            }


            /*
            int x = (int) (Math.cos(angle) * radius);
            int y = (int) (Math.sin(angle) * radius);
            int lineOffset = size / 2;
            g.setColor(Color.blue);
            g.drawLine(lineOffset - x, lineOffset - y, lineOffset + x, lineOffset + y);*/

            int circleOffset = size / 2 - radius;
            g.setColor(Color.black);
            g.drawOval(circleOffset, circleOffset, radius * 2, radius * 2);

        }

    }

    public void init(BdhcHandler bdhcHandler) {
        this.bdhcHandler = bdhcHandler;
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
