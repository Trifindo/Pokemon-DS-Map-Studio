/*
 * Created by JFormDesigner on Tue Nov 03 10:45:48 CET 2020
 */

package formats.bdhcam;

import java.awt.event.*;
import javax.swing.border.*;

import formats.bdhcam.camplate.Camplate;
import editor.handler.MapEditorHandler;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Truck
 */
public class BdhcamPlatesDisplay extends JPanel {

    private MapEditorHandler handler;
    private BdhcamHandler bdhcamHandler;
    private BufferedImage mapImage;

    private static final int TILE_SIZE = 16;
    private static final int cols = 32, rows = 32;
    private static final int width = cols * TILE_SIZE, height = rows * TILE_SIZE;
    private static final int plateMargin = 3;
    private static final int CORNER_SIZE = 5;
    private static final int xOffset = 0;
    private static final int yOffset = 0;

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
    private boolean dragging = false;
    private int lastPlateX, lastPlateY;
    private int lastPlateWidth, lastPlateHeight;
    private int lastX, lastY;

    private BufferedImage playerImg;
    private boolean movingPlayer = false;
    private boolean movingX = false;
    private int nextX, nextY;
    private float deltaX, deltaY;
    private final float delta = 0.08f;

    public BdhcamPlatesDisplay() {
        initComponents();
        setPreferredSize(new Dimension(width, height));

        try {
            playerImg = Utils.loadImageAsResource("/icons/playerIcon.png");
        } catch (IOException | IllegalArgumentException ex) {

        }
    }

    private void thisMouseDragged(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        if (bdhcamHandler.getBdhcam().getPlates().size() > 0) {
            if (dragging && isCursorInsiePanel(evt)) {
                int deltaX = (x - lastX) / TILE_SIZE;
                int deltaY = (y - lastY) / TILE_SIZE;
                Camplate p = bdhcamHandler.getSelectedPlate();
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
            }
            bdhcamHandler.setPlayerInPlate(bdhcamHandler.getSelectedPlate());
            repaint();

        }
    }

    private void thisMousePressed(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        if (indexPlateHovering != -1) {
            if (SwingUtilities.isLeftMouseButton(evt)) {
                if(bdhcamHandler.getSelectedPlate() != null) {
                    if (isHoveringPlate(bdhcamHandler.getSelectedPlate(), x, y)) {
                        partSelected = getPartSelected(bdhcamHandler.getSelectedPlate(), x, y);
                    } else {
                        bdhcamHandler.setSelectedPlate(indexPlateHovering);
                        bdhcamHandler.stopAnimation();
                        //bdhcamHandler.setPlayerInPlate(bdhcamHandler.getSelectedPlate());
                        partSelected = partHovering;
                    }
                }
                dragging = true;
                lastX = getFixedMouseX(evt);
                lastY = getFixedMouseY(evt);

                Camplate p = bdhcamHandler.getSelectedPlate();
                lastPlateX = p.x;
                lastPlateY = p.y;
                lastPlateWidth = p.width;
                lastPlateHeight = p.height;

                bdhcamHandler.getDialog().updateView();
            }
            repaint();
        }
    }

    private void thisMouseMoved(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        if (bdhcamHandler.getSelectedPlate() != null) {
            if (isHoveringPlate(bdhcamHandler.getSelectedPlate(), x, y)) {
                partHovering = getPartSelected(bdhcamHandler.getSelectedPlate(), x, y);
                indexPlateHovering = bdhcamHandler.getIndexSelected();
                setCursor(new Cursor(partHovering));
            } else {
                for (int i = 0; i < bdhcamHandler.getBdhcam().getPlates().size(); i++) {
                    Camplate p = bdhcamHandler.getBdhcam().getPlate(i);
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
    }

    private void thisKeyPressed(KeyEvent e) {
        if (!movingPlayer) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    movingPlayer = true;
                    movingX = false;
                    nextX = (int) (bdhcamHandler.getPlayerX());
                    nextY = (int) (bdhcamHandler.getPlayerY() + 1);
                    deltaX = 0.0f;
                    deltaY = delta;
                    break;
                case KeyEvent.VK_DOWN:
                    movingPlayer = true;
                    movingX = false;
                    nextX = (int) (bdhcamHandler.getPlayerX());
                    nextY = (int) (bdhcamHandler.getPlayerY() - 1);
                    deltaX = 0.0f;
                    deltaY = -delta;
                    break;
                case KeyEvent.VK_LEFT:
                    movingPlayer = true;
                    movingX = true;
                    nextX = (int) bdhcamHandler.getPlayerX() - 1;
                    nextY = (int) (bdhcamHandler.getPlayerY());
                    deltaX = -delta;
                    deltaY = 0.0f;
                    break;
                case KeyEvent.VK_RIGHT:
                    movingPlayer = true;
                    movingX = true;
                    nextX = (int) bdhcamHandler.getPlayerX() + 1;
                    nextY = (int) (bdhcamHandler.getPlayerY());
                    deltaX = delta;
                    deltaY = 0.0f;
                    break;
            }
        }
    }

    public void init(MapEditorHandler handler, BdhcamHandler bdhcamHandler, BufferedImage mapImage) {
        this.handler = handler;
        this.bdhcamHandler = bdhcamHandler;
        this.mapImage = mapImage;

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null && handler != null && bdhcamHandler != null) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform transform = g2d.getTransform();

            g2d.scale((float) getWidth() / width, (float) getHeight() / height);
            g.drawImage(mapImage, 0, 0, null);

            g.setColor(new Color(255, 255, 255, 100));
            drawGrid(g);
            drawPlates(g, bdhcamHandler.getBdhcam());

            if (playerImg != null) {
                g.drawImage(playerImg,
                        (int) (bdhcamHandler.getPlayerX() * TILE_SIZE),
                        (int) (bdhcamHandler.getPlayerY() * TILE_SIZE) - (playerImg.getHeight() - TILE_SIZE),
                        null);
            }

            g2d.setTransform(transform);
        }
    }

    private void drawPlates(Graphics g, Bdhcam bdhcam) {
        for (int i = 0; i < bdhcam.getPlates().size(); i++) {
            g.setColor(bdhcam.getPlate(i).getFillColor());
            fillPlate(g, bdhcam.getPlate(i));

            g.setColor(bdhcam.getPlate(i).getBorderColor());
            if (bdhcam.getPlate(i).type.ID == Camplate.Type.POS_DEPENDENT_X.ID) {
                drawBArrowX(g, bdhcam.getPlate(i), 10);
            }else if(bdhcam.getPlate(i).type.ID == Camplate.Type.POS_DEPENDENT_Y.ID){
                drawBArrowY(g, bdhcam.getPlate(i), 10);
            }

            drawPlateIndex(g, bdhcam.getPlate(i), i);
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        for (int i = 0; i < bdhcam.getPlates().size(); i++) {
            g.setColor(bdhcam.getPlate(i).getBorderColor());
            drawPlateBorder(g, bdhcam.getPlates().get(i));
        }


        if (bdhcamHandler.getSelectedPlate() != null) {
            g.setColor(Color.red);
            drawPlateBorder(g, bdhcamHandler.getSelectedPlate());
            g.setColor(Color.white);
            drawPlateBorder(g, bdhcamHandler.getSelectedPlate(), 2);


        }
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < width; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, height);
        }

        for (int i = 0; i < height; i++) {
            g.drawLine(0, i * TILE_SIZE, width, i * TILE_SIZE);
        }
    }

    private void drawPlateBorder(Graphics g, Camplate p) {
        g.drawRect(p.x * TILE_SIZE, p.y * TILE_SIZE,
                p.width * TILE_SIZE - 1, p.height * TILE_SIZE - 1);
    }

    private void drawPlateBorder(Graphics g, Camplate p, int margin) {
        g.drawRect(p.x * TILE_SIZE - margin, p.y * TILE_SIZE - margin,
                p.width * TILE_SIZE - 1 + margin * 2, p.height * TILE_SIZE - 1 + margin * 2);
    }

    private void fillPlate(Graphics g, Camplate p) {
        g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE,
                p.width * TILE_SIZE, p.height * TILE_SIZE);

    }

    private void drawPlateIndex(Graphics g, Camplate p, int index) {
        g.setColor(Color.white);
        g.drawString(
                String.valueOf(index), p.x * TILE_SIZE + 4, p.y * TILE_SIZE + 12);
    }

    private void drawBArrowX(Graphics g, int x1, int y1, int x2, int y2, int scale) {
        g.drawLine(x1, y1, x2, y2);

        int[] xValues1 = {x1, x1 + scale, x1 + scale};
        int[] yValues1 = {y1, y1 - scale / 2, y1 + scale / 2};

        int[] xValues2 = {x2, x2 - scale, x2 - scale};
        int[] yValues2 = {y2, y2 - scale / 2, y2 + scale / 2};

        g.fillPolygon(xValues1, yValues1, 3);
        g.fillPolygon(xValues2, yValues2, 3);
    }

    private void drawBArrowX(Graphics g, Camplate p, int scale) {
        drawBArrowX(g,
                (p.x) * TILE_SIZE + xOffset,
                ((p.y + p.y + p.height) * TILE_SIZE / 2 + yOffset),
                (p.x + p.width) * TILE_SIZE + xOffset,
                (p.y + p.y + p.height) * TILE_SIZE / 2 + yOffset,
                scale);
    }

    private void drawBArrowY(Graphics g, int x1, int y1, int x2, int y2, int scale) {
        g.drawLine(x1, y1, x2, y2);

        int[] yValues1 = {y1, y1 + scale, y1 + scale};
        int[] xValues1 = {x1, x1 - scale / 2, x1 + scale / 2};

        int[] yValues2 = {y2, y2 - scale, y2 - scale};
        int[] xValues2 = {x2, x2 - scale / 2, x2 + scale / 2};

        g.fillPolygon(xValues1, yValues1, 3);
        g.fillPolygon(xValues2, yValues2, 3);
    }

    private void drawBArrowY(Graphics g, Camplate p, int scale) {
        drawBArrowY(g,
                ((p.x + p.x + p.width) * TILE_SIZE / 2 + xOffset),
                (p.y) * TILE_SIZE + yOffset,
                ((p.x + p.x + p.width) * TILE_SIZE / 2 + xOffset),
                (p.y + p.height) * TILE_SIZE + yOffset,
                scale);
    }


    public int getPartSelected(Camplate p, int x, int y) {
        if (new Rectangle(
                p.x * TILE_SIZE + xOffset,
                p.y * TILE_SIZE + yOffset,
                CORNER_SIZE,
                CORNER_SIZE
        ).contains(x, y)) {
            return CORNER_NW;
        }

        if (new Rectangle(
                (p.x + p.width) * TILE_SIZE + xOffset - CORNER_SIZE,
                p.y * TILE_SIZE + yOffset,
                CORNER_SIZE,
                CORNER_SIZE
        ).contains(x, y)) {
            return CORNER_NE;
        }

        if (new Rectangle(
                p.x * TILE_SIZE + xOffset,
                (p.y + p.height) * TILE_SIZE + yOffset - CORNER_SIZE,
                CORNER_SIZE,
                CORNER_SIZE
        ).contains(x, y)) {
            return CORNER_SW;
        }

        if (new Rectangle(
                (p.x + p.width) * TILE_SIZE + xOffset - CORNER_SIZE,
                (p.y + p.height) * TILE_SIZE + yOffset - CORNER_SIZE,
                CORNER_SIZE,
                CORNER_SIZE
        ).contains(x, y)) {
            return CORNER_SE;
        }

        if (y < p.y * TILE_SIZE + yOffset + CORNER_SIZE) {
            return BORDER_N;
        }
        if (y > (p.y + p.height) * TILE_SIZE + yOffset - CORNER_SIZE) {
            return BORDER_S;
        }
        if (x < p.x * TILE_SIZE + xOffset + CORNER_SIZE) {
            return BORDER_W;
        }
        if (x > (p.x + p.width) * TILE_SIZE + xOffset - CORNER_SIZE) {
            return BORDER_E;
        }

        return CENTER;
    }

    public boolean isHoveringPlate(Camplate p, int x, int y) {
        return new Rectangle(
                p.x * TILE_SIZE + xOffset,
                p.y * TILE_SIZE + yOffset,
                p.width * TILE_SIZE,
                p.height * TILE_SIZE
        ).contains(x, y);
    }

    public boolean isHoveringPlayer(int x, int y) {
        return new Rectangle(
                (int) (bdhcamHandler.getPlayerX() * TILE_SIZE + xOffset),
                (int) (bdhcamHandler.getPlayerY() * TILE_SIZE + yOffset),
                TILE_SIZE,
                TILE_SIZE
        ).contains(x, y);
    }

    public void updatePlate(Camplate p, int c, int r, int width, int height) {
        int xMin = 0;
        int xMax = 32;
        int yMin = 0;
        int yMax = 32;

        int newX = Math.min(Math.max(c, xMin), xMax - width);
        int newY = Math.min(Math.max(r, yMin), yMax - height);

        if (width > 0 && height > 0) {
            p.x = newX;
            p.y = newY;
            p.width = width;
            p.height = height;
        }
    }

    private void updatePlayer(int x, int y) {
        int xMin = 0;
        int xMax = 32;
        int yMin = 0;
        int yMax = 32;

        int newX = Math.max(xMin, Math.min(x, xMax - 1));
        int newY = Math.max(yMin, Math.min(y, yMax - 1));

        bdhcamHandler.setPlayerX(newX);
        bdhcamHandler.setPlayerY(newY);
    }

    private int getFixedMouseX(MouseEvent evt) {
        return (int) (evt.getX() * ((float) width / getWidth()));
    }

    private int getFixedMouseY(MouseEvent evt) {
        return (int) (evt.getY() * ((float) height / getHeight()));
    }

    public boolean isCursorInsiePanel(MouseEvent evt) {
        int x = getFixedMouseX(evt);
        int y = getFixedMouseY(evt);
        return x >= 0 && x < width && y >= 0 && y < height;
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setBorder(LineBorder.createGrayLineBorder());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                thisMousePressed(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                thisMouseDragged(e);
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                thisMouseMoved(e);
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                thisKeyPressed(e);
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
