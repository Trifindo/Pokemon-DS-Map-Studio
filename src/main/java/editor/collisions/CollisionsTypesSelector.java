package editor.collisions;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class CollisionsTypesSelector extends JPanel {

    private static final int rows = 32, cols = CollisionTypes.numCollisions / rows;
    private static final int tileSize = 16;
    private static final int width = cols * tileSize, height = rows * tileSize;

    private BufferedImage[] collTypesImgs;

    private CollisionHandler collHandler;

    public CollisionsTypesSelector() {
        initComponents();
        setPreferredSize(new Dimension(width, height));
    }

    private void formMouseDragged(MouseEvent evt) {
        selectCollisionType(evt);
    }

    private void formMousePressed(MouseEvent evt) {
        selectCollisionType(evt);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (collHandler != null && collTypesImgs != null) {
            g.drawImage(collTypesImgs[collHandler.getIndexLayerSelected()], 0, 0, null);

            int indexSelected = collHandler.getIndexCollisionSelected();
            int x = (indexSelected % cols) * tileSize;
            int y = (indexSelected / cols) * tileSize;

            g.setColor(Color.red);
            g.drawRect(x, y, tileSize - 1, tileSize - 1);

            g.setColor(new Color(255, 100, 100, 100));
            g.fillRect(x, y, tileSize - 1, tileSize - 1);
        }

    }

    public void init(CollisionHandler collisionHandler) {
        this.collHandler = collisionHandler;

        collTypesImgs = drawColors();
    }

    private void selectCollisionType(java.awt.event.MouseEvent evt) {
        int c = evt.getX() / tileSize;
        int r = evt.getY() / tileSize;
        int value = r * cols + c;
        if (new Rectangle(0, 0, cols, rows).contains(c, r)
                && value < CollisionTypes.numCollisions) {
            collHandler.setIndexCollisionSelected(value);
            collHandler.getDialog().updateView();
            repaint();

        }
    }

    private BufferedImage[] drawColors() {
        BufferedImage[] images = new BufferedImage[collHandler.getNumLayers()];

        for (int l = 0; l < collHandler.getNumLayers(); l++) {
            BufferedImage image = new BufferedImage(cols * tileSize, rows * tileSize, BufferedImage.TYPE_INT_RGB);
            images[l] = image;
            Graphics g = image.getGraphics();
            for (int j = 0, c = 0; j < rows; j++) {
                for (int i = 0; i < cols; i++, c++) {
                    g.drawImage(collHandler.getImage(l, c), i * tileSize, j * tileSize, null);
                }
            }
            g.setColor(Color.lightGray);
            drawGrid(g);
        }

        return images;
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < cols; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, height);
        }

        for (int i = 0; i < rows; i++) {
            g.drawLine(0, i * tileSize, width, i * tileSize);
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
