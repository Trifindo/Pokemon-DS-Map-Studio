package editor.tileselector;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import editor.handler.MapEditorHandler;
import editor.mapdisplay.MapDisplay;
import editor.mapdisplay.ViewMode;
import editor.tileseteditor.TilesetEditorDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

import tileset.Tile;

/**
 * @author Trifindo, JackHack96
 */
public class TileSelector extends JPanel {

    private MapEditorHandler handler;
    private TilesetEditorDialog dialog;
    private final int tilePixelSize = 16;
    private final int maxCols = 8;
    private int rows;
    private ArrayList<Rectangle> boundingBoxes = new ArrayList<>();
    private BufferedImage display;

    private boolean multiSelectionEnabled;
    private boolean multiselecting = false;
    private boolean dragging = false;
    private boolean canDrag = false;
    private int indexTileHovering = -1;
    private int indexSecondTileSelected = -1;
    private int mouseX, mouseY;
    private BufferedImage multiSelectImg;

    public TileSelector() {
        initComponents();
    }

    private void formMouseDragged(MouseEvent evt) {
        if (multiSelectionEnabled) {
            if (SwingUtilities.isLeftMouseButton(evt) && canDrag) {
                dragging = true;
                mouseX = evt.getX();
                mouseY = evt.getY();
                indexTileHovering = getIndexSelected(evt);
                repaint();
            }
        }
    }

    private void formMousePressed(MouseEvent evt) {
        int index = getIndexSelected(evt);
        if (multiSelectionEnabled) {
            if (index != -1) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    if (index >= handler.getTileIndexSelected() && index <= indexSecondTileSelected && multiselecting) {
                        canDrag = true;
                        multiSelectImg = getSubTilesetImage(handler.getTileIndexSelected(), indexSecondTileSelected);
                    } else if (index == handler.getTileIndexSelected()) {
                        canDrag = true;
                        indexSecondTileSelected = handler.getTileIndexSelected();
                        multiSelectImg = handler.getTileSelected().getThumbnail();
                    } else {
                        handler.setIndexTileSelected(index);
                    }
                    multiselecting = false;
                } else if (SwingUtilities.isRightMouseButton(evt) && multiSelectionEnabled) {
                    multiselecting = true;
                    indexSecondTileSelected = index;
                    if (indexSecondTileSelected < handler.getTileIndexSelected()) {
                        indexSecondTileSelected = handler.getTileIndexSelected();
                        handler.setIndexTileSelected(index);
                    }
                }
            } else {
                multiselecting = false;
            }
        } else {
            if (index != -1) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    handler.setIndexTileSelected(index);

                    if (handler.getMainFrame().getMapDisplay().getViewMode().getViewID() == ViewMode.ViewID.VIEW_ORTHO) {
                        handler.getMainFrame().getMapDisplay().setEditMode(MapDisplay.EditMode.MODE_EDIT);
                        handler.getMainFrame().getJtbModeEdit().setSelected(true);
                    }
                }
            }
        }
        System.out.println("fitst index: " + handler.getTileIndexSelected() + " second index: " + indexSecondTileSelected);
        repaint();
    }

    private void formMouseReleased(MouseEvent evt) {
        if (multiSelectionEnabled) {
            canDrag = false;
            if (dragging) {
                dragging = false;

                int index = getIndexSelected(evt);
                if (index != -1) {
                    /*if (indexSecondTileSelected == handler.getTileIndexSelected()) {
                        handler.getTileset().swapTiles(index, handler.getTileIndexSelected());
                        dialog.getTileDisplay().swapVBOs(index, handler.getTileIndexSelected());

                        handler.setIndexTileSelected(index);
                        updateLayout();
                        dialog.updateViewTileIndex();
                    } else */
                    if (index < handler.getTileIndexSelected() || index > indexSecondTileSelected) {
                        ArrayList<Integer> indices = new ArrayList<>(handler.getTileset().size());
                        for (int i = 0; i < handler.getTileset().size(); i++) {
                            indices.add(i);
                        }
                        ArrayList<Integer> selectionIndices = new ArrayList(
                                indices.subList(handler.getTileIndexSelected(),
                                        indexSecondTileSelected + 1));
                        for (int i = 0; i < selectionIndices.size(); i++) {
                            indices.remove(handler.getTileIndexSelected());
                        }

                        if (index > handler.getTileIndexSelected()) {
                            index -= selectionIndices.size();
                        }

                        indices.addAll(index, selectionIndices);
                        //indices.addAll(Math.min(index, indices.size()), selectionIndices);

                        handler.getTileset().moveTiles(indices);

                        handler.setIndexTileSelected(index);
                        updateLayout();
                        dialog.updateViewTileIndex();
                    }
                }
                repaint();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (display != null) {
            g.drawImage(display, 0, 0, null);
            if (boundingBoxes.size() > 0) {
                g.setColor(Color.red);
                drawTileBounds(g, handler.getTileIndexSelected());
            }
        }

        if (handler != null) {
            if (handler.getTileset().size() > 0) {
                if (multiSelectionEnabled && (multiselecting || dragging || canDrag) && boundingBoxes.size() > 0) {
                    int limit = Math.min(indexSecondTileSelected, handler.getTileset().size() - 1);
                    for (int i = handler.getTileIndexSelected() + 1; i <= limit; i++) {
                        g.setColor(Color.red);
                        drawTileBounds(g, i);
                    }
                }
                if (dragging) {
                    if (indexTileHovering != -1 && boundingBoxes.size() > 0) {
                        g.setColor(Color.blue);
                        drawTileBounds(g, indexTileHovering);
                    }

                    g.drawImage(multiSelectImg,
                            mouseX, mouseY, null);
                    /*
                    g.drawImage(handler.getTileSelected().getThumbnail(),
                            mouseX, mouseY, null);*/
                }
            }
        }
    }

    public void init(MapEditorHandler handler, TilesetEditorDialog dialog) {
        this.handler = handler;
        this.multiSelectionEnabled = true;
        this.dialog = dialog;

        rows = countRows();

        if (rows > 0) {
            display = new BufferedImage(maxCols * tilePixelSize, rows * tilePixelSize, BufferedImage.TYPE_4BYTE_ABGR);

            paintTiles();
        }

    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        this.multiSelectionEnabled = false;

        rows = countRows();

        if (rows > 0) {
            display = new BufferedImage(maxCols * tilePixelSize, rows * tilePixelSize, BufferedImage.TYPE_4BYTE_ABGR);

            paintTiles();
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

        Tile tile = handler.getTileset().get(index);
        g.drawImage(
                tile.getThumbnail(),
                boundingBoxes.get(index).x,
                boundingBoxes.get(index).y,
                null);
    }

    public void updateTiles(ArrayList<Integer> indices) {
        Graphics g = display.getGraphics();

        for (int i = 0; i < indices.size(); i++) {
            int index = indices.get(i);
            Tile tile = handler.getTileset().get(index);
            g.drawImage(
                    tile.getThumbnail(),
                    boundingBoxes.get(index).x,
                    boundingBoxes.get(index).y,
                    null);
        }
    }

    private void paintTiles() {
        Graphics g = display.getGraphics();

        int rowIndex = 0;
        int colIndex = 0;
        int rowWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < handler.getTileset().size(); i++) {
            Tile tile = handler.getTileset().get(i);
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
        for (int i = 0; i < handler.getTileset().size(); i++) {
            Tile tile = handler.getTileset().get(i);
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

    public ArrayList<Integer> getIndicesSelected() {
        ArrayList<Integer> indices;
        if (multiselecting) {
            int indexStart = handler.getTileIndexSelected();
            int indexEnd = indexSecondTileSelected;
            if (indexEnd < indexStart) {
                indexStart = indexSecondTileSelected;
                indexEnd = handler.getTileIndexSelected();
            }
            indices = new ArrayList<>(indexEnd - indexStart + 1);
            for (int i = 0; i < indexEnd - indexStart + 1; i++) {
                indices.add(indexStart + i);
            }
        } else {
            indices = new ArrayList<>(1);
            indices.add(handler.getTileIndexSelected());
        }
        return indices;
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

    private BufferedImage getSubTilesetImage(int indexFrom, int indexTo) {
        int yMin = boundingBoxes.get(indexFrom).y;
        int yMax = yMin;
        for (int i = indexFrom; i <= indexTo; i++) {
            Rectangle bounds = boundingBoxes.get(i);
            int newY = bounds.y + bounds.height;
            if (newY > yMax) {
                yMax = newY;
            }
        }

        BufferedImage img = new BufferedImage(maxCols * tilePixelSize, yMax - yMin, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        for (int i = indexFrom; i <= indexTo; i++) {
            BufferedImage tileImg = handler.getTileset().getTiles().get(i).getThumbnail();
            Rectangle bounds = boundingBoxes.get(i);
            g.drawImage(tileImg, bounds.x, bounds.y - yMin, null);
        }

        return img;
    }

    public BufferedImage getTilesetImage() {
        return display;
    }

    public int getTileSelectedY() {
        if (handler.getTileIndexSelected() < boundingBoxes.size()) {
            return boundingBoxes.get(handler.getTileIndexSelected()).y;
        }
        return 0;
    }

    public void setIndexSecondTileSelected(int index) {
        this.indexSecondTileSelected = index;
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
