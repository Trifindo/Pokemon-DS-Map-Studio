package editor.collisions;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import editor.state.CollisionLayerState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class CollisionLayerSelector extends JPanel {

    private CollisionHandler collHandler;

    private static final int cols = 32, rows = 32;
    private static final int tileSize = 2;
    private int textGapHeight = 20;
    private int thumbnailWidth = cols * tileSize, thumbnailHeight = rows * tileSize;
    private int maxNumLayers = 8;

    private BufferedImage[] layerImgs;
    private static final Color backColor = new Color(255, 255, 255);
    private static final Color borderColor = Color.lightGray;

    private static final String[][] layerNames = new String[][]{
            {"TYPE", "COLLISION"},
            {"TYPE", "COLLISION"},
            {"TYPE", "COLLISION"},
            {"TYPE", "COLLISION"},
            {"TYPE", "COLLISION"},
            {"TERRAIN", "LAYER 2 (?)", "HEIGHT", "LAYER 4 (?)", "FLAGS", "LAYER 6 (?)", "COLLISION", "SHADOW"},
            {"TERRAIN", "LAYER 2 (?)", "HEIGHT", "LAYER 4 (?)", "FLAGS", "LAYER 6 (?)", "COLLISION", "SHADOW"},
            {"TERRAIN", "LAYER 2 (?)", "HEIGHT", "LAYER 4 (?)", "FLAGS", "LAYER 6 (?)", "COLLISION", "SHADOW"},
            {"TERRAIN", "LAYER 2 (?)", "HEIGHT", "LAYER 4 (?)", "FLAGS", "LAYER 6 (?)", "COLLISION", "SHADOW"}
    };

    public CollisionLayerSelector() {
        initComponents();

        this.setPreferredSize(new Dimension(
                thumbnailWidth * maxNumLayers,
                thumbnailHeight + textGapHeight));
    }

    private void formMousePressed(MouseEvent evt) {
        int x = evt.getX() / thumbnailWidth;
        int y = (evt.getY() - textGapHeight) / thumbnailHeight;

        if (y == 0 && x >= 0 && x < collHandler.getNumLayers()) {

            if (!collHandler.isLayerChanged()) {
                collHandler.setLayerChanged(true);
                collHandler.addLayerState(new CollisionLayerState("Layer change", collHandler));
            }

            collHandler.setIndexLayerSelected(x);

            collHandler.getDialog().repaintTypesDisplay();
            collHandler.getDialog().repaintDisplay();
            collHandler.getDialog().updateView();
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (collHandler != null && layerImgs != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            for (int i = 0; i < collHandler.getNumLayers(); i++) {
                g.drawImage(layerImgs[i],
                        i * thumbnailWidth, textGapHeight, null);

                String string = layerNames[collHandler.getGame()][i];
                int width = g.getFontMetrics().stringWidth(string);
                g.drawString(string,
                        i * thumbnailWidth + (thumbnailWidth - width) / 2,
                        (textGapHeight * 2) / 3);
            }

            g.setColor(Color.red);
            g.drawRect(collHandler.getIndexLayerSelected() * thumbnailWidth,
                    textGapHeight, thumbnailWidth - 1, thumbnailHeight - 1);
            g.setColor(new Color(255, 100, 100, 100));
            g.fillRect(collHandler.getIndexLayerSelected() * thumbnailWidth,
                    textGapHeight, thumbnailWidth - 1, thumbnailHeight - 1);
        }

    }

    public void init(CollisionHandler collHandler) {
        this.collHandler = collHandler;

        this.setPreferredSize(new Dimension(
                thumbnailWidth * collHandler.getNumLayers(),
                thumbnailHeight + textGapHeight));

        layerImgs = new BufferedImage[collHandler.getNumLayers()];

        for (int i = 0; i < layerImgs.length; i++) {
            BufferedImage img = new BufferedImage(thumbnailWidth,
                    thumbnailHeight, BufferedImage.TYPE_INT_RGB);
            layerImgs[i] = img;
            Graphics g = img.getGraphics();
            g.setColor(backColor);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
        }

        drawAllLayers();
    }

    public void drawAllLayers() {
        for (int i = 0; i < collHandler.getNumLayers(); i++) {
            drawLayer(i);
        }
    }

    public void drawLayer(int layerIndex) {
        BufferedImage img = layerImgs[layerIndex];
        Graphics g = img.getGraphics();
        g.setColor(backColor);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int value = collHandler.getValue(layerIndex, i, j);
                g.setColor(collHandler.getFillColor(layerIndex, value));
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }

        g.setColor(borderColor);
        g.drawRect(0, 0, thumbnailWidth - 1, thumbnailHeight - 1);

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
