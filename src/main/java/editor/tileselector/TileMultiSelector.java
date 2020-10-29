package editor.tileselector;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import editor.handler.MapEditorHandler;
import editor.tileseteditor.TilesetEditorDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import tileset.Tile;
import tileset.Tileset;

/**
 * @author Trifindo, JackHack96
 */
public class TileMultiSelector extends JPanel {

    private Tileset tileset;
    private final int tilePixelSize = 16;
    private final int maxCols = 8;
    private int rows;
    private ArrayList<Rectangle> boundingBoxes = new ArrayList<>();
    private BufferedImage display;
    private boolean[] selection;
    private boolean multiselecting = false;
    private int lastIndex = 0;

    public TileMultiSelector() {
        initComponents();
    }

    private void formMousePressed(MouseEvent evt) {
        int index = getIndexSelected(evt);
        if (index != -1) {
            if (multiselecting) {
                setSelected(true, Math.min(index, lastIndex), Math.max(index, lastIndex));
                repaint();
            } else {
                selection[index] = !selection[index];
                repaint();
            }
            lastIndex = index;
        }
    }

    private void formKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
            multiselecting = true;
        }
    }

    private void formKeyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
            multiselecting = false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (display != null) {
            g.drawImage(display, 0, 0, null);
            if (boundingBoxes.size() > 0) {
                g.setColor(Color.red);

                for (int i = 0; i < selection.length; i++) {
                    if (selection[i]) {
                        drawTileBounds(g, i);
                    }
                }
            }

            //g.setColor(Color.blue);
            //drawTileBounds(g, lastIndex);

            //System.out.println("Number of bounds: " + boundingBoxes.size());
        }
    }

    public void init(Tileset tileset) {
        this.tileset = tileset;

        rows = countRows();

        selection = new boolean[tileset.size()];

        if (rows > 0) {
            display = new BufferedImage(maxCols * tilePixelSize, rows * tilePixelSize, BufferedImage.TYPE_4BYTE_ABGR);

            paintTiles();

            updateSize();
        }
    }

    public void updateLayout() {
        boundingBoxes = new ArrayList<>();

        rows = countRows();
        if (rows > 0) {
            System.out.println("Num rows: " + rows);
            display = new BufferedImage(maxCols * tilePixelSize, rows * tilePixelSize, BufferedImage.TYPE_4BYTE_ABGR);
        } else {
            display = new BufferedImage(maxCols * tilePixelSize, 1 * tilePixelSize, BufferedImage.TYPE_4BYTE_ABGR);
        }
        paintTiles();

        updateSize();
    }

    private void updateSize() {
        this.setPreferredSize(new Dimension(
                display.getWidth(),
                display.getHeight()));
        this.setSize(new Dimension(
                display.getWidth(),
                display.getHeight()));
    }

    public void updateTile(int index) {
        Graphics g = display.getGraphics();

        Tile tile = tileset.get(index);
        g.drawImage(
                tile.getThumbnail(),
                boundingBoxes.get(index).x,
                boundingBoxes.get(index).y,
                null);
    }

    private void paintTiles() {
        Graphics g = display.getGraphics();

        int rowIndex = 0;
        int colIndex = 0;
        int rowWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < tileset.size(); i++) {
            Tile tile = tileset.get(i);
            rowWidth += tile.getWidth();

            if (rowWidth > maxCols) {
                rowWidth = 0;
                colIndex = 0;
                rowIndex += maxHeight;
                maxHeight = 0;
                i--;
            } else {
                Rectangle bounds = new Rectangle(
                        colIndex * tilePixelSize,
                        rowIndex * tilePixelSize,
                        tile.getWidth() * tilePixelSize,
                        tile.getHeight() * tilePixelSize
                );
                boundingBoxes.add(bounds);
                g.drawImage(
                        tile.getThumbnail(),
                        colIndex * tilePixelSize,
                        rowIndex * tilePixelSize,
                        null);
                colIndex += tile.getWidth();
                if (tile.getHeight() > maxHeight) {
                    maxHeight = tile.getHeight();
                }
                if (rowWidth == maxCols) {
                    rowWidth = 0;
                    colIndex = 0;
                    rowIndex += maxHeight;
                    maxHeight = 0;
                }
            }
        }

    }

    private int countRows() {
        int rowIndex = 0;
        int rowWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < tileset.size(); i++) {
            Tile tile = tileset.get(i);
            rowWidth += tile.getWidth();

            if (rowWidth > maxCols) {
                rowWidth = 0;
                rowIndex += maxHeight;
                maxHeight = 0;
                i--;
            } else {
                if (tile.getHeight() > maxHeight) {
                    maxHeight = tile.getHeight();
                }
                if (rowWidth == maxCols) {
                    rowWidth = 0;
                    rowIndex += maxHeight;
                    maxHeight = 0;
                }
            }
        }
        return rowIndex + maxHeight;
    }

    private int getIndexSelected(java.awt.event.MouseEvent evt) {
        int index = -1;
        for (int i = 0; i < boundingBoxes.size(); i++) {
            Rectangle bounds = boundingBoxes.get(i);
            if (bounds.contains(evt.getX(), evt.getY())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void drawTileBounds(Graphics g, int tileIndex) {
        Color borderColor = g.getColor();
        Color fillColor = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 100);
        Rectangle bounds = boundingBoxes.get(tileIndex);
        g.setColor(fillColor);
        g.fillRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
        g.setColor(borderColor);
        g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
    }

    public void selectAll() {
        setSelected(true, 0, selection.length);
    }

    public void deselectAll() {
        setSelected(false, 0, selection.length);
    }

    private void setSelected(boolean selected, int start, int end) {
        end = Math.min(selection.length, end + 1);
        for (int i = start; i < end; i++) {
            selection[i] = selected;
        }
    }

    public ArrayList<Tile> getTilesSelected() {
        ArrayList<Tile> tiles = new ArrayList();
        for (int i = 0; i < selection.length; i++) {
            if (selection[i]) {
                tiles.add(tileset.get(i));
            }
        }
        return tiles;
    }

    public int getNumTilesSelected() {
        int count = 0;
        for (int i = 0; i < selection.length; i++) {
            if (selection[i]) {
                count++;
            }
        }
        return count;
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
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                formKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                formKeyReleased(e);
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
