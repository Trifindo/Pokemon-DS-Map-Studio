package editor.smartdrawing;

import java.awt.event.*;
import javax.swing.*;

import editor.handler.MapEditorHandler;
import editor.grid.MapGrid;
import editor.mapdisplay.MapDisplay;
import editor.mapdisplay.ViewMode;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class SmartGridDisplay extends JPanel {

    private static final BufferedImage gridImage = Utils.loadTexImageAsResource("/imgs/smartGrid.png");

    private MapEditorHandler handler;
    private boolean editable = true;

    public SmartGridDisplay() {
        initComponents();

        //gridImage = Utils.loadImageAsResource("/imgs/smartGrid.png");
        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize));
    }

    private void formMouseMoved(MouseEvent evt) {
        if (editable) {
            if (handler.getTileset().size() > 0) {
                int x = evt.getX() / MapGrid.tileSize;
                int y = evt.getY() / MapGrid.tileSize;
                int gridIndex = y / SmartGrid.height;
                y %= SmartGrid.height;
                System.out.println(x + "  " + y);
                if (gridIndex < handler.getSmartGridArray().size() && gridIndex >= 0) {
                    if (!((y == 2) && (x == 4 || x == 3))) {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        setToolTipText(null);
                    } else {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        setToolTipText("Select Smart Drawing");
                    }
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    setToolTipText("Right click for adding or removing Smart Drawing");
                }
            }
        }
    }

    private void formMousePressed(MouseEvent evt) {
        if (editable) {
            if (handler.getTileset().size() > 0) {
                int x = evt.getX() / MapGrid.tileSize;
                int y = evt.getY() / MapGrid.tileSize;
                int gridIndex = y / SmartGrid.height;
                y %= SmartGrid.height;
                //System.out.println(x + "  " + y);
                if (gridIndex < handler.getSmartGridArray().size() && gridIndex >= 0) {
                    if (!(y == 2 && (x == 4 || x == 3))) {
                        int[][] grid = handler.getSmartGrid(gridIndex).sgrid;
                        if (new Rectangle(SmartGrid.width, SmartGrid.height).contains(x, y)) {
                            if (SwingUtilities.isLeftMouseButton(evt)) {
                                if (handler.getTileSelected().isSizeOne()) {
                                    grid[x][y] = handler.getTileIndexSelected();
                                }
                            }
                            /*else if (SwingUtilities.isRightMouseButton(evt)) {
                        grid[x][y] = -1;
                    }*/
                            repaint();
                        }
                    } else {
                        handler.setSmartGridIndexSelected(gridIndex);
                        repaint();
                    }
                }

                if (SwingUtilities.isRightMouseButton(evt) && handler != null) {
                    if (gridIndex >= 0 && gridIndex < handler.getSmartGridArray().size()) {
                        handler.setSmartGridIndexSelected(gridIndex);
                    }

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item1 = new JMenuItem("Add Smart Painter");
                    JMenuItem item2 = new JMenuItem("Remove Smart Painter");
                    item1.addActionListener(e -> {
                        handler.getSmartGridArray().add(new SmartGrid());
                        updateSize();
                        repaint();
                    });
                    item2.addActionListener(e -> {
                        if (handler.getSmartGridArray().size() > 1) {
                            if (gridIndex >= 0 && gridIndex < handler.getSmartGridArray().size()) {
                                handler.getSmartGridArray().remove(gridIndex);
                                handler.setSmartGridIndexSelected(Math.max(0, gridIndex - 1));
                                updateSize();
                                repaint();
                            }
                        } else {
                            System.out.println("No se puede");
                            JOptionPane.showMessageDialog(menu,
                                    "There must me at least one Smart Painter",
                                    "Can't delete Smart Painter",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    menu.add(item1);
                    menu.add(item2);

                    menu.show(this, evt.getX(), evt.getY());
                }
            }
        } else {
            if (handler.getTileset().size() > 0) {
                int y = evt.getY() / MapGrid.tileSize;
                int gridIndex = y / SmartGrid.height;
                if (gridIndex < handler.getSmartGridArray().size() && gridIndex >= 0) {
                    handler.setSmartGridIndexSelected(gridIndex);
                    repaint();

                    if (handler.getMainFrame().getMapDisplay().getViewMode().getViewID() == ViewMode.ViewID.VIEW_ORTHO) {
                        if (handler.getMainFrame().getMapDisplay().getEditMode() != MapDisplay.EditMode.MODE_INV_SMART_PAINT) {
                            handler.getMainFrame().getMapDisplay().setEditMode(MapDisplay.EditMode.MODE_SMART_PAINT);
                            handler.getMainFrame().getJtbModeSmartPaint().setSelected(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gridImage != null && handler != null) {
            for (int k = 0; k < handler.getSmartGridArray().size(); k++) {
                g.drawImage(gridImage, 0, SmartGrid.height * k * MapGrid.tileSize, null);
            }
        }

        if (handler != null) {
            for (int k = 0; k < handler.getSmartGridArray().size(); k++) {
                SmartGrid sg = handler.getSmartGrid(k);
                int[][] grid = sg.sgrid;
                for (int i = 0; i < SmartGrid.width; i++) {
                    for (int j = 0; j < SmartGrid.height; j++) {
                        int indexTile = grid[i][j];
                        if (indexTile != -1) {
                            try {
                                BufferedImage img = handler.getTileset().get(indexTile).getThumbnail();
                                g.drawImage(
                                        img,
                                        i * MapGrid.tileSize,
                                        (j + SmartGrid.height * k) * MapGrid.tileSize,
                                        null);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            int index = handler.getSmartGridIndexSelected();
            g.setColor(Color.red);
            g.drawRect(
                    0,
                    index * SmartGrid.height * MapGrid.tileSize,
                    SmartGrid.width * MapGrid.tileSize - 1,
                    SmartGrid.height * MapGrid.tileSize - 1);
            g.setColor(new Color(255, 100, 100, 50));
            g.fillRect(0,
                    index * SmartGrid.height * MapGrid.tileSize,
                    SmartGrid.width * MapGrid.tileSize - 1,
                    SmartGrid.height * MapGrid.tileSize - 1);
        }

    }

    public void updateSize() {
        int numSmartGrids = handler.getSmartGridArray().size();
        System.out.println("Smart grid size: " + numSmartGrids);
        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize * numSmartGrids));
        this.setSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize * numSmartGrids));
    }

    public void init(MapEditorHandler handler, boolean editable) {
        this.handler = handler;
        this.editable = editable;

        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize * handler.getSmartGridArray().size()));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        addMouseMotionListener(new MouseMotionAdapter() {
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
