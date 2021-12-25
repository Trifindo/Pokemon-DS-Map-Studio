package editor.layerselector;

import java.awt.event.*;
import javax.swing.*;

import editor.handler.MapEditorHandler;
import editor.grid.MapGrid;
import editor.state.MapLayerState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author Trifindo, JackHack96
 */
public class ThumbnailLayerSelector extends JPanel {

    private MapEditorHandler handler;

    private final BufferedImage[] layerThumbnails = new BufferedImage[MapGrid.numLayers];
    private static final int layerWidth = 64, layerHeight = 64;
    private static final int smallTileSize = 2;
    private static final Color backColor = new Color(0, 127, 127, 255);

    private boolean hovering = false;
    private int hoverIndex = 0;

    public ThumbnailLayerSelector() {
        initComponents();

        for (int i = 0; i < layerThumbnails.length; i++) {
            BufferedImage img = new BufferedImage(layerWidth, layerHeight, BufferedImage.TYPE_INT_RGB);
            layerThumbnails[i] = img;
            Graphics g = img.getGraphics();
            g.setColor(backColor);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
        }

        setPreferredSize(new Dimension(layerWidth, layerHeight * MapGrid.numLayers));
    }

    private void formMouseMoved(MouseEvent evt) {
        int index = evt.getY() / layerHeight;
        if (index >= 0 && index < MapGrid.numLayers) {
            hovering = true;
            hoverIndex = index;
        }
        repaint();
    }

    private void formMouseExited(MouseEvent evt) {
        hovering = false;
        repaint();
    }

    private void formMousePressed(MouseEvent evt) {
        if (handler != null) {
            int index = evt.getY() / layerHeight;
            if (index >= 0 && index < MapGrid.numLayers) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    if (!handler.isLayerChanged()) {
                        handler.setLayerChanged(true);
                        handler.addMapState(new MapLayerState("Layer change", handler));

                    }
                    handler.setActiveTileLayer(index);
                } else if (SwingUtilities.isRightMouseButton(evt)) {
                    //LayerPopupMenu popupMenu = new LayerPopupMenu(index);
                    //popupMenu.show(this, evt.getX(), evt.getY());

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem itemShowLayer = new JMenuItem("Show Layer");
                    JMenuItem itemHideLayer = new JMenuItem("Hide Layer");
                    JSeparator separator1 = new JSeparator();
                    JMenuItem itemClearLayer = new JMenuItem("Clear Layer");
                    JSeparator separator2 = new JSeparator();
                    JMenuItem itemCopyLayer = new JMenuItem("Copy Layer");
                    JMenuItem itemPasteLayer = new JMenuItem("Paste Layer");
                    JMenuItem itemPasteTiles = new JMenuItem("Paste Tiles");
                    JMenuItem itemPasteHeights = new JMenuItem("Paste Heights");

                    itemShowLayer.addActionListener(e -> {
                        handler.setLayerState(index, true);
                        repaint();
                        handler.getMainFrame().repaintMapDisplay();
                    });
                    itemHideLayer.addActionListener(e -> {
                        handler.setLayerState(index, false);
                        repaint();
                        handler.getMainFrame().repaintMapDisplay();
                    });
                    itemClearLayer.addActionListener(e -> {
                        if (handler.getTileset().size() > 0) {
                            handler.clearLayer(index);
                            //handler.getMainFrame().repaintMapDisplay();
                        }
                    });
                    itemCopyLayer.addActionListener(e -> {
                        if (handler.getTileset().size() > 0) {
                            handler.copyLayer(index);
                            //handler.getMainFrame().repaintMapDisplay();
                        }
                    });
                    itemPasteLayer.addActionListener(e -> {
                        if (handler.getTileset().size() > 0) {
                            handler.pasteLayer(index);
                            //handler.getGrid().pasteTileLayer(index);
                            //handler.getGrid().pasteHeightLayer(index);
                            //handler.getMainFrame().repaintMapDisplay();
                        }
                    });
                    itemPasteTiles.addActionListener(e -> {
                        if (handler.getTileset().size() > 0) {
                            handler.pasteLayerTiles(index);
                            //handler.getGrid().pasteTileLayer(index);
                            //handler.getMainFrame().repaintMapDisplay();
                        }
                    });
                    itemPasteHeights.addActionListener(e -> {
                        if (handler.getTileset().size() > 0) {
                            handler.pasteLayerHeights(index);
                            //handler.getGrid().pasteHeightLayer(index);
                            //handler.getMainFrame().repaintMapDisplay();
                        }
                    });

                    itemShowLayer.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/turnOnIcon.png"))));
                    itemHideLayer.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/turnOffIcon.png"))));
                    itemClearLayer.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/RemoveIcon.png"))));
                    itemCopyLayer.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/copyIcon.png"))));
                    itemPasteLayer.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/pasteIcon.png"))));
                    itemPasteTiles.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/pasteTileIcon.png"))));
                    itemPasteHeights.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/pasteHeightIcon.png"))));

                    menu.add(itemShowLayer);
                    menu.add(itemHideLayer);
                    menu.add(separator1);
                    menu.add(itemClearLayer);
                    menu.add(separator2);
                    menu.add(itemCopyLayer);
                    menu.add(itemPasteLayer);
                    menu.add(itemPasteTiles);
                    menu.add(itemPasteHeights);

                    menu.show(this, evt.getX(), evt.getY());

                    //handler.invertLayerState(index);
                } else if (SwingUtilities.isMiddleMouseButton(evt)) {
                    if (handler.isLayerTheOnlyActive(index))
                        handler.setLayersEnabled(true);
                    else {
                        handler.setOnlyActiveTileLayer(index);
                    }
                }
                repaint();
                handler.getMainFrame().repaintMapDisplay();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handler != null) {
            for (int i = 0; i < layerThumbnails.length; i++) {
                if (layerThumbnails[i] != null) {
                    g.drawImage(layerThumbnails[i], 0, i * layerHeight, null);
                }

                if (!handler.renderLayers[i]) {
                    g.setColor(new Color(0, 0, 0, 100));
                } else {
                    g.setColor(new Color(100, 100, 100, 0));
                }
                if (handler.getActiveLayerIndex() == i) {
                    //g.setColor(new Color(255, 100, 100, 100));
                }
                g.fillRect(0, i * layerHeight, layerWidth - 1, layerHeight - 1);

                g.setColor(Color.white);
                g.drawRect(0, i * layerHeight, layerWidth - 1, layerHeight - 1);
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g.setColor(Color.red);
            g.drawRect(1, handler.getActiveLayerIndex() * layerHeight + 1, layerWidth - 2, layerHeight - 2);

        }

        if (hovering) {
            g.setColor(new Color(0.7f, 0.7f, 1.0f, 0.2f));
            g.fillRect(0, hoverIndex * layerHeight, layerWidth - 1, layerHeight - 1);
        }
    }

    public void drawAllLayerThumbnails() {
        for (int i = 0; i < MapGrid.numLayers; i++) {
            drawLayerThumbnail(i);
        }
    }

    public void drawLayerThumbnail(int index) {
        BufferedImage imgLayer = layerThumbnails[index];
        Graphics g = imgLayer.getGraphics();
        g.setColor(backColor);
        g.fillRect(0, 0, layerWidth, layerHeight);
        int[][] grid = handler.getGrid().tileLayers[index];
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                int tileIndex = grid[i][j];
                if (tileIndex != -1) {
                    if (tileIndex < handler.getTileset().size()) {
                        BufferedImage tileThumbnail = handler.getTileset().get(tileIndex).getSmallThumbnail();

                        g.drawImage(tileThumbnail,
                                i * smallTileSize,
                                (MapGrid.cols - j - 1) * smallTileSize - (tileThumbnail.getHeight() - smallTileSize), //+ tileThumbnail.getHeight(),
                                null);
                    }
                }
            }
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
    }

    class LayerPopupMenu extends JPopupMenu {

        public LayerPopupMenu(int layerIndex) {
            JMenuItem showLayer = new JMenuItem("Show Layer");
            showLayer.addActionListener(e -> {
                handler.setLayerState(layerIndex, true);
                repaint();
            });
            showLayer.setEnabled(!handler.renderLayers[layerIndex]);
            add(showLayer);

            JMenuItem hideLayer = new JMenuItem("Hide Layer");
            hideLayer.addActionListener(e -> {
                handler.setLayerState(layerIndex, false);
                repaint();
            });
            hideLayer.setEnabled(handler.renderLayers[layerIndex]);
            add(hideLayer);
        }
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
            public void mouseExited(MouseEvent e) {
                formMouseExited(e);
            }

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
