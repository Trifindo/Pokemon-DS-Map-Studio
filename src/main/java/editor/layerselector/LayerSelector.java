package editor.layerselector;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import editor.handler.MapEditorHandler;
import editor.grid.MapGrid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class LayerSelector extends JPanel {

    private MapEditorHandler handler;

    private BufferedImage[] layersOnImgs = new BufferedImage[MapGrid.numLayers];
    private BufferedImage[] layersOffImgs = new BufferedImage[MapGrid.numLayers];

    public LayerSelector() {
        initComponents();
        setPreferredSize(new Dimension(MapGrid.tileSize,
                MapGrid.tileSize * MapGrid.numLayers));
    }

    private void formMousePressed(MouseEvent evt) {
        if (handler != null) {
            int index = evt.getY() / MapGrid.tileSize;
            if (index >= 0 && index < MapGrid.numLayers) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    handler.setActiveTileLayer(index);
                } else if (SwingUtilities.isRightMouseButton(evt)) {
                    handler.invertLayerState(index);
                } else if (SwingUtilities.isMiddleMouseButton(evt)) {
                    handler.setOnlyActiveTileLayer(index);
                }
                repaint();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handler != null && layersOffImgs != null && layersOnImgs != null) {
            for (int i = 0; i < MapGrid.numLayers; i++) {
                BufferedImage img;
                if (handler.renderLayers[i]) {
                    img = layersOnImgs[i];
                } else {
                    img = layersOffImgs[i];
                }
                g.drawImage(img, 0, i * MapGrid.tileSize, null);
            }
            g.setColor(new Color(255, 100, 100, 100));
            g.fillRect(0, handler.getActiveLayerIndex() * MapGrid.tileSize,
                    MapGrid.tileSize - 1, MapGrid.tileSize - 1);
            g.setColor(Color.red);
            g.drawRect(0, handler.getActiveLayerIndex() * MapGrid.tileSize,
                    MapGrid.tileSize - 1, MapGrid.tileSize - 1);

        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;

        layersOnImgs = Utils.loadVerticalImageArrayAsResource(
                "/imgs/layersSelected.png",
                MapGrid.numLayers);

        layersOffImgs = Utils.loadVerticalImageArrayAsResource(
                "/imgs/layersNoSelected.png",
                MapGrid.numLayers);
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
