package formats.collisions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import editor.game.Game;
import editor.handler.MapEditorHandler;
import editor.state.CollisionLayerState;

import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class CollisionsDisplay extends JPanel {

    private MapEditorHandler handler;
    private CollisionHandler collHandler;

    private BufferedImage mapImage;

    private static final int cols = 32, rows = 32;
    private static final int tileSize = 16;
    private static final int width = cols * tileSize, height = rows * tileSize;

    public float transparency = 0.5f;

    public CollisionsDisplay() {
        initComponents();

        setPreferredSize(new Dimension(width, height));
    }

    private void formMouseDragged(MouseEvent evt) {
        if(collHandler.canEditLayer(collHandler.getIndexLayerSelected())) {
            if (SwingUtilities.isLeftMouseButton(evt)) {
                //collHandler.addMapState(new CollisionLayerState("Draw collision", collHandler));
                setCollision(evt);
            }
        }
    }

    private void formMouseWheelMoved(MouseWheelEvent evt) {
        int delta = evt.getWheelRotation() > 0 ? 1 : -1;
        collHandler.incrementCollisionSelected(delta);
        collHandler.getDialog().repaintTypesDisplay();
        collHandler.getDialog().updateViewCollisionTypeName();
    }

    private void formMousePressed(MouseEvent evt) {
        if(collHandler.canEditLayer(collHandler.getIndexLayerSelected())) {
            handler.setLayerChanged(false);
            if (SwingUtilities.isLeftMouseButton(evt)) {
                collHandler.addLayerState(new CollisionLayerState("Draw collision", collHandler));
                setCollision(evt);
            } else if (SwingUtilities.isMiddleMouseButton(evt)) {
                collHandler.addLayerState(new CollisionLayerState("Flood fill collision", collHandler));
                floodFillCollision(evt);
            } else if (SwingUtilities.isRightMouseButton(evt)) {
                setIndexSelected(evt);
            }
        }
    }

    private void formMouseReleased(MouseEvent e) {
        if(collHandler.canEditLayer(collHandler.getIndexLayerSelected())) {
            collHandler.getDialog().redrawSelectedLayerInSelector();
            collHandler.getDialog().repaintLayerSelector();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, null);
        }

        if (handler != null && collHandler.getCollisions() != null && collHandler != null) {
            drawCollisions(g, collHandler.getCollisions());

            if(!collHandler.canEditLayer(collHandler.getIndexLayerSelected())){
                g.setColor(new Color(0.5f,0.5f,0.5f,0.5f));
                g.fillRect(0,0,width, height);
            }
        }

        //g.setColor(Color.white);
        //drawGrid(g);
    }

    public void init(MapEditorHandler handler, BufferedImage mapImage,
                     CollisionHandler collisionHandler) {
        this.handler = handler;
        this.mapImage = mapImage;
        this.collHandler = collisionHandler;
    }


    private void setIndexSelected(java.awt.event.MouseEvent evt) {
        int c = evt.getX() / tileSize;
        int r = evt.getY() / tileSize;

        if (new Rectangle(0, 0, cols, rows).contains(c, r)) {
            collHandler.setIndexCollisionSelected(collHandler.getValue(c, r));
            collHandler.getDialog().repaintTypesDisplay();
            collHandler.getDialog().updateViewCollisionTypeName();
            repaint();
        }
    }


    private void setCollision(java.awt.event.MouseEvent evt) {
        int c = evt.getX() / tileSize;
        int r = evt.getY() / tileSize;

        if (new Rectangle(0, 0, cols, rows).contains(c, r)) {
            collHandler.setValue(collHandler.getIndexCollisionSelected(), c, r);
            repaint();
        }
    }

    private void floodFillCollision(java.awt.event.MouseEvent evt) {
        int c = evt.getX() / tileSize;
        int r = evt.getY() / tileSize;
        if (new Rectangle(0, 0, cols, rows).contains(c, r)) {
            Utils.floodFillMatrix(collHandler.getLayerSelected(), c, r,
                    (byte) collHandler.getIndexCollisionSelected());
            collHandler.setValue(collHandler.getIndexCollisionSelected(), c, r);
            repaint();
        }
    }

    private void drawCollisions(Graphics g, Collisions collisions) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(transparency));
        for (int i = 0; i < Collisions.cols; i++) {
            for (int j = 0; j < Collisions.rows; j++) {
                int value = collisions.getValue(collHandler.getIndexLayerSelected(), i, j);
                g.drawImage(collHandler.getImage(value),
                        i * tileSize, j * tileSize, null);
            }
        }
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
        addMouseWheelListener(e -> formMouseWheelMoved(e));
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
