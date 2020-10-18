package editor.tileseteditor;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

import tileset.Tile;
import tileset.Tileset;
import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class TextureDisplayMaterial extends JPanel {

    private TilesetEditorHandler tileHandler;
    private TilesetEditorDialog dialog;

    private static final int size = 128;
    private BufferedImage backImg;

    private static final int reloadButtonSize = 32;
    private BufferedImage reloadIcon;

    public TextureDisplayMaterial() {
        initComponents();

        try {
            reloadIcon = Utils.loadImageAsResource("/icons/reloadIcon.png");
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    private void formMouseMoved(MouseEvent evt) {
        if (new Rectangle(size - reloadButtonSize,
                size - reloadButtonSize, reloadButtonSize,
                reloadButtonSize).contains(evt.getX(), evt.getY())) {
            setToolTipText("Reload texture");
        } else {
            setToolTipText("Edit Texture in Image Editor");
        }
    }

    private void formMousePressed(MouseEvent evt) {
        if (tileHandler != null) {
            Tileset tset = tileHandler.getMapEditorHandler().getTileset();
            if (tset.size() > 0) {
                File file = new File(tset.tilesetFolderPath + "/" + tileHandler.getMaterialSelectedTextureName());
                if (file.exists()) {
                    if (new Rectangle(size - reloadButtonSize,
                            size - reloadButtonSize, reloadButtonSize,
                            reloadButtonSize).contains(evt.getX(), evt.getY())) {
                        dialog.replaceTexture(tileHandler.getMaterialIndexSelected(), file.getPath());
                    } else {
                        try {
                            Desktop.getDesktop().edit(file);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "There is not default program for editing images.",
                                    "Can't edit texture image", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "There was a problem opening the image. \n"
                                    + "Save the map, open it and try again.",
                            "Can't open texture image", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backImg != null) {
            g.drawImage(backImg, 0, 0, null);
        }

        if (tileHandler != null) {
            if (tileHandler.getMapEditorHandler().getTileset().size() > 0) {
                Tile tile = tileHandler.getMapEditorHandler().getTileSelected();
                BufferedImage img = tile.getTileset().getTextureImg(tileHandler.getMaterialIndexSelected());
                int x = getWidth() / 2 - img.getWidth() / 2;
                int y = getHeight() / 2 - img.getHeight() / 2;

                g.drawImage(img, x, y, null);
            }
        }

        if (reloadIcon != null) {
            g.drawImage(reloadIcon, size - reloadButtonSize, size - reloadButtonSize, null);
        }
    }

    public void init(TilesetEditorHandler tileHandler, TilesetEditorDialog dialog) {
        this.tileHandler = tileHandler;
        this.backImg = createBackImg();
        this.dialog = dialog;
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
        setToolTipText("Edit Texture in Image Editor");
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
