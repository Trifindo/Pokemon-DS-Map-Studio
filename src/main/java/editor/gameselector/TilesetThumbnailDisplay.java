package editor.gameselector;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo, JackHack96
 */
public class TilesetThumbnailDisplay extends JPanel {

    private BufferedImage img;
    public TilesetThumbnailDisplay() {
        initComponents();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (img != null) {
            g.drawImage(img, 0, 0, null);
        }

    }

    public void setImage(BufferedImage img) {
        this.img = img;
        if (img != null) {
            setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
            revalidate();
        }else{
            setPreferredSize(new Dimension(128, 10));
            revalidate();
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========

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
