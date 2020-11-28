package formats.bdhc;

import editor.handler.MapEditorHandler;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Trifindo, JackHack96
 */
public class BdhcDisplay extends JPanel {
    private MapEditorHandler handler;
    private BufferedImage mapImage;
    private BdhcHandler bdhcHandler;

    private static final int tileSize = 16;
    private static final int cols = 32, rows = 32;
    private static final int width = cols * tileSize, height = rows * tileSize;
    private static final int plateMargin = 3;
    private static final int cornerSize = 5;

    private static final int CORNER_NW = 6;
    private static final int CORNER_NE = 7;
    private static final int CORNER_SW = 4;
    private static final int CORNER_SE = 5;
    private static final int BORDER_N = 8;
    private static final int BORDER_S = 9;
    private static final int BORDER_W = 10;
    private static final int BORDER_E = 11;
    private static final int CENTER = 13;

    private int partHovering = -1;
    private int indexPlateHovering = -1;
    private int partSelected = -1;
    //private int indexPlateSelected = 0;
    //private int lastIndexPlateSelected = -1;
    private boolean dragging = false;
    private int lastPlateX, lastPlateY;
    private int lastPlateWidth, lastPlateHeight;
    private int lastX, lastY;

    public BdhcDisplay() {
        initComponents();
        setPreferredSize(new Dimension(width, height));
    }

    private void formMouseDragged(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        if (dragging && isCursorInsiePanel(evt)) {
            Plate p = bdhcHandler.getSelectedPlate();
            int deltaX = (x - lastX) / tileSize;
            int deltaY = (y - lastY) / tileSize;
            switch (partSelected) {
                case CENTER:
                    updatePlate(p, lastPlateX + deltaX, lastPlateY + deltaY, p.width, p.height);
                    break;
                case CORNER_SE:
                    updatePlate(p, p.x, p.y, lastPlateWidth + deltaX, lastPlateHeight + deltaY);
                    break;
                case CORNER_NW:
                    updatePlate(p, lastPlateX + deltaX, lastPlateY + deltaY, lastPlateWidth - deltaX, lastPlateHeight - deltaY);
                    break;
                case CORNER_NE:
                    updatePlate(p, p.x, lastPlateY + deltaY, lastPlateWidth + deltaX, lastPlateHeight - deltaY);
                    break;
                case CORNER_SW:
                    updatePlate(p, lastPlateX + deltaX, p.y, lastPlateWidth - deltaX, lastPlateHeight + deltaY);
                    break;
                case BORDER_N:
                    updatePlate(p, p.x, lastPlateY + deltaY, p.width, lastPlateHeight - deltaY);
                    break;
                case BORDER_S:
                    updatePlate(p, p.x, p.y, p.width, lastPlateHeight + deltaY);
                    break;
                case BORDER_W:
                    updatePlate(p, lastPlateX + deltaX, p.y, lastPlateWidth - deltaX, p.height);
                    break;
                case BORDER_E:
                    updatePlate(p, p.x, p.y, lastPlateWidth + deltaX, p.height);
                    break;
            }
            bdhcHandler.getDialog().updateViewBdhcDisplay3D();
            repaint();
        }
    }

    private void formMouseMoved(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        if (isHoveringPlate(bdhcHandler.getSelectedPlate(), x, y)) {
            partHovering = getPartSelected(bdhcHandler.getSelectedPlate(), x, y);
            indexPlateHovering = bdhcHandler.getSelectedPlateIndex();
            setCursor(new Cursor(partHovering));
        } else {
            for (int i = 0; i < bdhcHandler.getBdhc().getPlates().size(); i++) {
                Plate p = bdhcHandler.getBdhc().getPlate(i);
                if (isHoveringPlate(p, x, y)) {
                    partHovering = getPartSelected(p, x, y);
                    indexPlateHovering = i;
                    setCursor(new Cursor(partHovering));
                    return;
                }
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            partHovering = -1;
            indexPlateHovering = -1;
        }
    }

    private void formMouseWheelMoved(MouseWheelEvent evt) {
        int wheelRotation = evt.getWheelRotation();
        if (wheelRotation > 0) {
            bdhcHandler.getSelectedPlate().z--;
        } else if (wheelRotation < 0) {
            bdhcHandler.getSelectedPlate().z++;
        }
        bdhcHandler.getDialog().updateView();
    }

    private void formMousePressed(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        if (indexPlateHovering != -1) {
            if (SwingUtilities.isLeftMouseButton(evt)) {
                if (isHoveringPlate(bdhcHandler.getSelectedPlate(), x, y)) {
                    partSelected = getPartSelected(bdhcHandler.getSelectedPlate(), x, y);
                } else {
                    bdhcHandler.setSelectedPlate(indexPlateHovering);
                    partSelected = partHovering;
                }

                dragging = true;
                lastX = getFixedMouseX(evt);
                lastY = getFixedMouseY(evt);

                Plate p = bdhcHandler.getSelectedPlate();
                lastPlateX = p.x;
                lastPlateY = p.y;
                lastPlateWidth = p.width;
                lastPlateHeight = p.height;

                bdhcHandler.getDialog().updateView();
            }
            repaint();
        }
    }

    private void formMouseReleased(MouseEvent e) {
        dragging = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null && handler != null && bdhcHandler != null) {
            Bdhc bdhc = bdhcHandler.getBdhc();

            Graphics2D g2d = (Graphics2D) g;

            g2d.scale((float) getWidth() / width, (float) getHeight() / height);

            g.drawImage(mapImage, 0, 0, null);

            g.setColor(new Color(255, 255, 255, 100));
            drawGrid(g);

            g2d.setStroke(new BasicStroke(4));
            g.setColor(Color.red);
            g.drawLine(width / 2, height / 2, width, height / 2);
            g.setColor(Color.green);
            g.drawLine(width / 2, height / 2, width / 2, height);
            g.setColor(Color.blue);
            g.drawLine(width / 2, height / 2, width / 2, width / 2);

            g.translate(width / 2, height / 2);
            drawPlates(g, bdhc);

            g.translate(-width / 2, -height / 2);

            g2d.scale((float) width / getWidth(), (float) height / getHeight());
        }
    }

    private void drawPlates(Graphics g, Bdhc bdhc) {

        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            g.setColor(bdhc.getPlate(i).getColor());
            fillPlate(g, bdhc.getPlate(i));
            drawPlateIndex(g, bdhc.getPlate(i), i);
        }
        /*
        for (int i = 0; i < bdhcHandler.getSelectedPlateIndex(); i++) {
            g.setColor(bdhc.getPlate(i).getColor());
            fillPlate(g, bdhc.getPlate(i));
            drawPlateIndex(g, bdhc.getPlate(i), i);
        }
        g.setColor(Plate.selectedColor);
        fillPlate(g, bdhcHandler.getSelectedPlate());
        drawPlateIndex(g, bdhcHandler.getSelectedPlate(), bdhcHandler.getSelectedPlateIndex());
        for (int i = bdhcHandler.getSelectedPlateIndex() + 1; i < bdhc.getPlates().size(); i++) {
            g.setColor(bdhc.getPlate(i).getColor());
            fillPlate(g, bdhc.getPlate(i));
            drawPlateIndex(g, bdhc.getPlate(i), i);
        }
         */
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            g.setColor(bdhc.getPlate(i).getBorderColor());//g.setColor(Color.blue);
            drawPlateBorder(g, bdhc.getPlates().get(i));
        }

        g.setColor(Color.red);
        drawPlateBorder(g, bdhcHandler.getSelectedPlate());
    }

    public void updatePlate(Plate p, int c, int r, int width, int height) {
        int xMin = -16;
        int xMax = 16;
        int yMin = -16;
        int yMax = 16;

        int newX = Math.min(Math.max(c, xMin), xMax - width);
        int newY = Math.min(Math.max(r, yMin), yMax - height);

        if (width > 0 && height > 0) {
            p.x = newX;
            p.y = newY;
            p.width = width;
            p.height = height;
        }
    }

    public int getPartSelected(Plate p, int x, int y) {
        if (new Rectangle(
                p.x * tileSize + width / 2,
                p.y * tileSize + height / 2,
                cornerSize,
                cornerSize
        ).contains(x, y)) {
            return CORNER_NW;
        }

        if (new Rectangle(
                (p.x + p.width) * tileSize + width / 2 - cornerSize,
                p.y * tileSize + height / 2,
                cornerSize,
                cornerSize
        ).contains(x, y)) {
            return CORNER_NE;
        }

        if (new Rectangle(
                p.x * tileSize + width / 2,
                (p.y + p.height) * tileSize + height / 2 - cornerSize,
                cornerSize,
                cornerSize
        ).contains(x, y)) {
            return CORNER_SW;
        }

        if (new Rectangle(
                (p.x + p.width) * tileSize + width / 2 - cornerSize,
                (p.y + p.height) * tileSize + height / 2 - cornerSize,
                cornerSize,
                cornerSize
        ).contains(x, y)) {
            return CORNER_SE;
        }

        if (y < p.y * tileSize + height / 2 + cornerSize) {
            return BORDER_N;
        }
        if (y > (p.y + p.height) * tileSize + height / 2 - cornerSize) {
            return BORDER_S;
        }
        if (x < p.x * tileSize + width / 2 + cornerSize) {
            return BORDER_W;
        }
        if (x > (p.x + p.width) * tileSize + width / 2 - cornerSize) {
            return BORDER_E;
        }

        return CENTER;
    }

    public boolean isCursorInsiePanel(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isHoveringPlate(Plate p, int x, int y) {
        return new Rectangle(
                p.x * tileSize + width / 2,
                p.y * tileSize + height / 2,
                p.width * tileSize,
                p.height * tileSize
        ).contains(x, y);
    }

    private void drawPlateBorder(Graphics g, Plate p) {
        g.drawRect(p.x * tileSize, p.y * tileSize,
                p.width * tileSize - 1, p.height * tileSize - 1);
    }

    private void fillPlate(Graphics g, Plate p) {
        g.fillRect(p.x * tileSize, p.y * tileSize,
                p.width * tileSize, p.height * tileSize);

    }

    private void drawPlateIndex(Graphics g, Plate p, int index) {
        g.setColor(Color.white);
        g.drawString(
                String.valueOf(index), p.x * tileSize + 4, p.y * tileSize + 12);
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < width; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, height);
        }

        for (int i = 0; i < height; i++) {
            g.drawLine(0, i * tileSize, width, i * tileSize);
        }
    }

    public void init(MapEditorHandler handler, BufferedImage mapImage, BdhcHandler bdhcHandler) {
        this.handler = handler;
        this.mapImage = mapImage;
        this.bdhcHandler = bdhcHandler;
    }

    private int getFixedMouseX(MouseEvent evt) {
        return (int) (evt.getX() * ((float) width / getWidth()));
    }

    private int getFixedMouseY(MouseEvent evt) {
        return (int) (evt.getY() * ((float) height / getHeight()));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                formMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                formMouseMoved(e);
            }
        });
        addMouseWheelListener(e -> formMouseWheelMoved(e));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                formMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                formMouseReleased(e);
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
