package editor.smartdrawing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import editor.grid.MapGrid;
import editor.handler.MapEditorHandler;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tileset.Tile;
import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class SmartGridEditableDisplay extends JPanel {

    private static final BufferedImage gridImage = Utils.loadTexImageAsResource("/imgs/smartGrid.png");

    private MapEditorHandler handler;

    private ArrayList<SmartGridEditable> smartGridArray;

    public SmartGridEditableDisplay() {
        initComponents();

        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize));
    }

    private void formMouseMoved(MouseEvent evt) {
        if (handler.getTileset().size() > 0) {
            float scale = getScale();
            int x = (int)(evt.getX() / (MapGrid.tileSize * scale));
            int y = (int)(evt.getY() / (MapGrid.tileSize * scale));
            int gridIndex = y / SmartGrid.height;
            y %= SmartGrid.height;
            System.out.println(x + "  " + y);
            if (gridIndex < smartGridArray.size() && gridIndex >= 0) {
                if (!(y == 2 && (x == 4 || x == 3))) {
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

    private void formMousePressed(MouseEvent evt) {
        if (handler.getTileset().size() > 0) {
            float scale = getScale();
            int x = (int)(evt.getX() / (MapGrid.tileSize * scale));
            int y = (int)(evt.getY() / (MapGrid.tileSize * scale));
            int gridIndex = y / SmartGrid.height;
            y %= SmartGrid.height;
            //System.out.println(x + "  " + y);
            if (gridIndex < smartGridArray.size() && gridIndex >= 0) {
                if (!(y == 2 && (x == 4 || x == 3))) {
                    Tile[][] grid = smartGridArray.get(gridIndex).sgrid;
                    if (new Rectangle(SmartGrid.width, SmartGrid.height).contains(x, y)) {
                        if (SwingUtilities.isLeftMouseButton(evt)) {
                            if (handler.getTileSelected().isSizeOne()) {
                                grid[x][y] = handler.getTileSelected();
                            }
                        }
                        repaint();
                    }
                } else {
                    handler.setSmartGridIndexSelected(gridIndex);
                    repaint();
                }
            }

            if (SwingUtilities.isRightMouseButton(evt) && handler != null) {
                if (gridIndex >= 0 && gridIndex < smartGridArray.size()) {
                    handler.setSmartGridIndexSelected(gridIndex);
                }

                JPopupMenu menu = new JPopupMenu();
                JMenuItem item1 = new JMenuItem("Add Smart Painter");
                JMenuItem item2 = new JMenuItem("Remove Smart Painter");
                item1.addActionListener(e -> {
                    smartGridArray.add(new SmartGridEditable());
                    updateSize();
                    repaint();
                });
                item2.addActionListener(e -> {
                    if (smartGridArray.size() > 1) {
                        if (gridIndex >= 0 && gridIndex < smartGridArray.size()) {
                            smartGridArray.remove(gridIndex);
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
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        float scale = getScale();
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(scale, scale);

        if (gridImage != null && handler != null) {
            for (int k = 0; k < smartGridArray.size(); k++) {
                g.drawImage(gridImage, 0, SmartGrid.height * k * MapGrid.tileSize, null);
            }
        }

        if (handler != null) {
            for (int k = 0; k < smartGridArray.size(); k++) {
                SmartGridEditable sg = smartGridArray.get(k);
                Tile[][] grid = sg.sgrid;
                for (int i = 0; i < SmartGrid.width; i++) {
                    for (int j = 0; j < SmartGrid.height; j++) {
                        Tile tile = grid[i][j];
                        if (tile != null) {
                            try {
                                BufferedImage img = tile.getThumbnail();
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
            g.drawRect(0,
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
        //int numSmartGrids = handler.getSmartGridArray().size();
        int numSmartGrids = smartGridArray.size();
        System.out.println("Smart grid size: " + numSmartGrids);
        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize * numSmartGrids));
        this.setSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize * numSmartGrids));
    }

    public void updateTiles() {
        for (SmartGridEditable sgrid : smartGridArray) {
            Tile[][] data = sgrid.sgrid;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    if (!handler.getTileset().getTiles().contains(data[i][j])) {
                        data[i][j] = null;
                    }
                }
            }
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;

        smartGridArray = new ArrayList<>(handler.getSmartGridArray().size());
        for (SmartGrid sgrid : handler.getSmartGridArray()) {
            smartGridArray.add(new SmartGridEditable(sgrid.sgrid, handler.getTileset()));
        }

        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.tileSize,
                SmartGrid.height * MapGrid.tileSize * handler.getSmartGridArray().size()));
    }

    public List<SmartGridEditable> getSmartGridArray() {
        return smartGridArray;
    }

    public void moveSelectedSmartGridUp() {
        int index = handler.getSmartGridIndexSelected();
        if (index > 0) {
            Collections.swap(smartGridArray, index, index - 1);
            handler.setSmartGridIndexSelected(index - 1);
        }
    }

    public void moveSelectedSmartGridDown() {
        int index = handler.getSmartGridIndexSelected();
        if (index < smartGridArray.size() - 1) {
            Collections.swap(smartGridArray, index, index + 1);
            handler.setSmartGridIndexSelected(index + 1);
        }
    }

    private float getScale(){
        return getWidth() / (float)(SmartGrid.width * MapGrid.tileSize);
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
