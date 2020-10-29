package editor.backsound;

import editor.handler.MapEditorHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class BacksoundDisplay extends JPanel {
    private MapEditorHandler handler;
    private BacksoundHandler backsoundHandler;
    private BufferedImage mapImage;

    private static final int TILE_SIZE = 16;
    private static final int COLS = 32, ROWS = 32;
    private static final int WIDTH = COLS * TILE_SIZE, HEIGHT = ROWS * TILE_SIZE;
    private static final int PLATE_MARGIN = 3;
    private static final int CORNER_SIZE = 5;

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
    private final int xOffset = 0;
    private final int yOffset = 0;

    private final Color defaultPlateColor = new Color(200, 200, 200);
    private final Color[] colorLookupTable = new Color[]{
            new Color(50, 0, 255),
            new Color(185, 195, 255),
            new Color(0, 0, 255),
            new Color(64, 64, 64),
            new Color(0, 150, 255),
            new Color(0, 0, 160),
            new Color(64, 64, 64),
            new Color(230, 230, 0),
            new Color(100, 180, 150),
            new Color(64, 64, 64),
            new Color(0, 0, 255),
            new Color(255, 170, 0),
            new Color(150, 255, 230),
            new Color(64, 64, 64),
            new Color(255, 0, 255),
            new Color(100, 255, 100)
    };

    public BacksoundDisplay() {
        initComponents();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private void formMouseDragged(MouseEvent e) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            if (dragging && isCursorInsiePanel(e)) {
                Soundplate p = backsoundHandler.getSelectedSoundplate();
                int deltaX = (e.getX() - lastX) / TILE_SIZE;
                int deltaY = (e.getY() - lastY) / TILE_SIZE;
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
                repaint();
            }
        }
    }

    private void formMouseMoved(MouseEvent e) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            int x = e.getX();
            int y = e.getY();
            if (isHoveringPlate(backsoundHandler.getSelectedSoundplate(), x, y)) {
                partHovering = getPartSelected(backsoundHandler.getSelectedSoundplate(), x, y);
                indexPlateHovering = backsoundHandler.getIndexSelected();
                setCursor(new Cursor(partHovering));
            } else {
                for (int i = 0; i < backsoundHandler.getBacksound().getSoundplates().size(); i++) {
                    Soundplate p = backsoundHandler.getBacksound().getSoundplate(i);
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

    private void formMousePressed(MouseEvent e) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            int x = e.getX();
            int y = e.getY();
            if (indexPlateHovering != -1) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isHoveringPlate(backsoundHandler.getSelectedSoundplate(), x, y)) {
                        partSelected = getPartSelected(backsoundHandler.getSelectedSoundplate(), x, y);
                    } else {
                        backsoundHandler.setIndexSelected(indexPlateHovering);
                        partSelected = partHovering;
                    }

                    dragging = true;
                    lastX = e.getX();
                    lastY = e.getY();

                    Soundplate p = backsoundHandler.getSelectedSoundplate();
                    lastPlateX = p.x;
                    lastPlateY = p.y;
                    lastPlateWidth = p.width;
                    lastPlateHeight = p.height;

                    backsoundHandler.getDialog().updateView();
                }
                repaint();
            }
        }
    }

    private void formMouseReleased(MouseEvent e) {
        dragging = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null && handler != null && backsoundHandler != null) {
            Backsound backsound = backsoundHandler.getBacksound();
            g.drawImage(mapImage, 0, 0, null);

            g.setColor(new Color(255, 255, 255, 100));
            drawGrid(g);
            drawPlates(g, backsound);
        }
    }

    public void init(MapEditorHandler handler, BacksoundHandler backsoundHandler, BufferedImage mapImage) {
        this.handler = handler;
        this.backsoundHandler = backsoundHandler;
        this.mapImage = mapImage;
    }

    private void drawPlates(Graphics g, Backsound backsound) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            for (int i = 0; i < backsound.getSoundplates().size(); i++) {

                g.setColor(getColorWithAlpha(getPlateColor(backsound, i), 120));
                fillPlate(g, backsound.getSoundplate(i));
                drawPlateIndex(g, backsound.getSoundplate(i), i);
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            for (int i = 0; i < backsound.getSoundplates().size(); i++) {
                //g.setColor(bdhc.getPlate(i).getBorderColor());//g.setColor(Color.blue);
                g.setColor(getColorWithAlpha(getPlateColor(backsound, i), 200));
                drawPlateBorder(g, backsound.getSoundplate(i));
            }

            g.setColor(Color.red);
            drawPlateBorder(g, backsoundHandler.getSelectedSoundplate());
        }
    }

    private Color getPlateColor(Backsound backsound, int plateIndex) {
        int soundCode = backsound.getSoundplate(plateIndex).soundCode;
        if (soundCode < colorLookupTable.length) {
            return colorLookupTable[Math.max(0, soundCode)];
        } else {
            return defaultPlateColor;
        }
    }

    private static Color getColorWithAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public void updatePlate(Soundplate p, int c, int r, int width, int height) {
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

    public int getPartSelected(Soundplate p, int x, int y) {
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

    public boolean isCursorInsiePanel(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return false;
        }
        return true;
    }

    public boolean isHoveringPlate(Soundplate p, int x, int y) {
        return new Rectangle(
                p.x * TILE_SIZE + xOffset,
                p.y * TILE_SIZE + yOffset,
                p.width * TILE_SIZE,
                p.height * TILE_SIZE
        ).contains(x, y);
    }

    private void drawPlateBorder(Graphics g, Soundplate p) {
        g.drawRect(p.x * TILE_SIZE, p.y * TILE_SIZE,
                p.width * TILE_SIZE - 1, p.height * TILE_SIZE - 1);
    }

    private void fillPlate(Graphics g, Soundplate p) {
        g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE,
                p.width * TILE_SIZE, p.height * TILE_SIZE);

    }

    private void drawPlateIndex(Graphics g, Soundplate p, int index) {
        g.setColor(Color.white);
        g.drawString(
                String.valueOf(index), p.x * TILE_SIZE + 4, p.y * TILE_SIZE + 12);
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < WIDTH; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, HEIGHT);
        }

        for (int i = 0; i < HEIGHT; i++) {
            g.drawLine(0, i * TILE_SIZE, WIDTH, i * TILE_SIZE);
        }
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
