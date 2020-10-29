package editor.tileseteditor;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import tileset.Tile;
import tileset.TilesetMaterial;

/**
 * @author Trifindo, JackHack96
 */
public class TextureDisplayConfigurable extends JPanel {

    private TilesetEditorHandler tileHandler;
    private int index = 0;
    private static final int size = 128;
    private BufferedImage backImg;

    public TextureDisplayConfigurable() {
        initComponents();
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backImg != null) {
            g.drawImage(backImg, 0, 0, null);
        }

        if (tileHandler != null) {
            if (tileHandler.getMapEditorHandler().getTileset().size() > 0) {
                TilesetMaterial material = tileHandler.getMapEditorHandler().getTileset().getMaterial(index);
                BufferedImage img = material.getTextureImg();
                int x = getWidth() / 2 - img.getWidth() / 2;
                int y = getHeight() / 2 - img.getHeight() / 2;

                g.drawImage(img, x, y, null);
            }
        }
    }

    public void init(TilesetEditorHandler tileHandler) {
        this.tileHandler = tileHandler;
        this.backImg = createBackImg();
    }

    public void setImageIndex(int index) {
        this.index = index;
    }

    public BufferedImage createBackImg() {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        int tileSize = 8;
        int numCells = size / tileSize;
        Color[] colors = new Color[]{Color.white, Color.lightGray};
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                g.setColor(colors[(i + j) % 2]);
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
        return img;
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
