package editor.heightselector;

import editor.handler.MapEditorHandler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Trifindo, JackHack96
 */
public class HeightSelector extends JPanel {

    private MapEditorHandler handler;

    private int tileSize = 16;

    public HeightSelector() {
        initComponents();
    }

    private void formMousePressed(MouseEvent e) {
        int y = e.getY() / tileSize;
        int numHeights = handler.getNumHeights();
        if ((y >= 0) && (y < numHeights)) {
            handler.setHeightIndexSelected(numHeights - 1 - y);
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handler != null) {
            int numHeights = handler.getNumHeights();
            for (int i = 0; i < numHeights; i++) {
                g.drawImage(handler.getHeightImage(i), 0, i * tileSize, null);
                /*
                //Draw height color
                g.setColor(handler.getHeightColorByIndex(i));
                g.fillRect(0, (numHeights - 1 - i) * tileSize, tileSize, tileSize);

                //Draw grid
                g.setColor(new Color(255, 255, 255, 255));
                g.drawRect(0, (numHeights - 1 - i) * tileSize, tileSize, tileSize);

                //Draw height value
                int xOffset = Math.abs(handler.getHeight(i)) > 9 ? 1 : 5;
                g.setColor(new Color(0, 0, 0, 255));
                g.drawString(String.valueOf(handler.getHeight(i)), xOffset, (numHeights - i) * tileSize - 2);
                */
            }

            //g.setColor(new Color(50, 50, 50));
            //g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g.setColor(Color.red);
            g.drawRect(
                    0,
                    (numHeights - 1 - handler.getHeightIndexSelected()) * tileSize,
                    tileSize - 1,
                    tileSize - 1);

            g.setColor(new Color(255, 0, 0, 96));
            g.fillRect(
                    0,
                    (numHeights - 1 - handler.getHeightIndexSelected()) * tileSize,
                    tileSize - 1,
                    tileSize - 1);
        }

    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;

        setPreferredSize(new Dimension(tileSize, handler.getNumHeights() * tileSize));
        setSize(new Dimension(tileSize, handler.getNumHeights() * tileSize));
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
