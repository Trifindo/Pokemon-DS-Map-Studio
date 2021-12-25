package editor.bordermap;

import editor.handler.MapEditorHandler;
import tileset.NormalsNotFoundException;
import tileset.TextureNotFoundException;
import tileset.Tile;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Trifindo, JackHack96
 */
public class BorderMapsDisplay extends JPanel {
    private static final int mapSize = 32;
    private static final Color backgroundColor = new Color(0.0f, 0.5f, 0.5f, 1.0f);
    private static final Color borderColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    private MapEditorHandler handler;

    public BorderMapsDisplay() {
        initComponents();
        setPreferredSize(new Dimension(BorderMapsGrid.cols * mapSize, BorderMapsGrid.rows * mapSize));
    }

    private void formMouseMoved(MouseEvent evt) {
        int x = evt.getX() / mapSize;
        int y = BorderMapsGrid.cols - 1 - evt.getY() / mapSize;
        if (new Rectangle(BorderMapsGrid.cols, BorderMapsGrid.rows).contains(x, y) && !(x == 1 && y == 1)) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.setToolTipText("Open map");
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.setToolTipText(null);
        }
    }

    private void formMousePressed(MouseEvent evt) {
        int x = evt.getX() / mapSize;
        int y = BorderMapsGrid.cols - 1 - evt.getY() / mapSize;

        if (new Rectangle(BorderMapsGrid.cols, BorderMapsGrid.rows).contains(x, y) && !(x == 1 && y == 1)) {
            if (SwingUtilities.isLeftMouseButton(evt)) {
                final JFileChooser fc = new JFileChooser();
                if (handler.getLastTilesetDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
                }
                fc.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
                fc.setApproveButtonText("Open");
                fc.setDialogTitle("Open");
                int returnVal = fc.showOpenDialog(handler.getMainFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        handler.setLastTilesetDirectoryUsed(fc.getSelectedFile().getParent());
                        File file = fc.getSelectedFile();

                        handler.getBorderMapsGrid().grid[x][y] = handler.getBorderMapsTileset().getTiles().size();
                        for (int i = 0; i < handler.getBorderMapsTileset().size(); i++) {
                            if (!handler.getBorderMapsGrid().isTileInGrid(i)) {
                                handler.getBorderMapsTileset().removeTile(i);
                                handler.getBorderMapsGrid().decreaseFromIndex(i);
                                i--;
                            }
                        }
                        handler.getBorderMapsGrid().grid[x][y] = handler.getBorderMapsTileset().getTiles().size();
                        Tile tile = new Tile(handler.getBorderMapsTileset(), file.getAbsolutePath());
                        handler.getBorderMapsTileset().getTiles().add(tile);

                        System.out.println("Size border tileset: " + handler.getBorderMapsTileset().size());

                        //New code
                        handler.getBorderMapsTileset().removeUnusedTextures();
                        //handler.getMainFrame().getMapDisplay().requestBorderMapsUpdate();//REMOVED
                        handler.getMainFrame().getMapDisplay().repaint();
                        handler.getMainFrame().getTileDisplay().requestUpdate();
                        handler.getMainFrame().getTileDisplay().repaint();

                        repaint();

                        System.out.println("border map loaded");

                    } catch (TextureNotFoundException | NormalsNotFoundException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) {
                handler.getBorderMapsGrid().grid[x][y] = -1;

                //handler.getMainFrame().getMapDisplay().requestBorderMapsUpdate();
                handler.getMainFrame().getMapDisplay().repaint();

                repaint();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < BorderMapsGrid.cols; i++) {
            for (int j = 0; j < BorderMapsGrid.rows; j++) {
                g.setColor(backgroundColor);
                g.fillRect(i * mapSize, j * mapSize, mapSize, mapSize);

                g.setColor(borderColor);
                g.drawRect(i * mapSize, j * mapSize, mapSize, mapSize);
            }
        }

        g.setColor(Color.white);
        g.fillRect(mapSize, mapSize, mapSize, mapSize);

        if (handler != null) {
            for (int i = 0; i < BorderMapsGrid.cols; i++) {
                for (int j = 0; j < BorderMapsGrid.rows; j++) {
                    g2.setStroke(new BasicStroke(1));
                    if (handler.getBorderMapsGrid().grid[i][j] != -1) {
                        g2.setColor(Color.green);
                        g.fillRect(i * mapSize, (BorderMapsGrid.cols - 1 - j) * mapSize, mapSize, mapSize);
                    }

                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(borderColor);
                    g.drawRect(i * mapSize, (BorderMapsGrid.cols - 1 - j) * mapSize, mapSize, mapSize);
                }
            }
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
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
