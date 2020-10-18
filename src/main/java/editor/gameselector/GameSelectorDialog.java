package editor.gameselector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

import editor.TilesetRenderer;
import editor.game.Game;
import editor.handler.MapEditorHandler;
import editor.smartdrawing.SmartGrid;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import tileset.TextureNotFoundException;
import tileset.Tileset;
import tileset.TilesetIO;
import utils.Utils;


/**
 * @author Trifindo, JackHack96
 */
public class GameSelectorDialog extends JDialog {

    private MapEditorHandler handler;
    public static final int ACEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private int newGame = Game.DIAMOND;

    private static final String[] defaultTsetsFolderPaths = new String[]{
            "/tilesets/dp",
            "/tilesets/pt",
            "/tilesets/hgss",
            "/tilesets/bw"
    };

    private static final String[] defaultTsetsNames = new String[]{
            "DiamondPearlTileset.pdsts",
            "PlatinumTileset.pdsts",
            "HeartGoldSoulSilverTileset.pdsts",
            "BlackWhiteTileset.pdsts"
    };

    public GameSelectorDialog(Window owner) {
        super(owner);
        initComponents();
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void jcbGameActionPerformed(ActionEvent e) {
        newGame = jcbGame.getSelectedIndex();
        updateViewGameIcon();
    }

    private void jcbTileListActionPerformed(ActionEvent e) {
        int tilesetIndex = jcbTileList.getSelectedIndex();

        loadTilesetThumbnail(tilesetIndex);
    }

    private void jbFinishActionPerformed(ActionEvent e) {
        handler.setGameIndex(newGame);

        Tileset tileset;
        int tilesetIndex = jcbTileList.getSelectedIndex();
        if (tilesetIndex < 4) {
            try {
                //MainFrame.class.getClassLoader().getResourceAsStream(filename)
                tileset = TilesetIO.readTilesetFromFileAsResource(
                        defaultTsetsFolderPaths[tilesetIndex] + "/"
                                + defaultTsetsNames[tilesetIndex]);

                TilesetRenderer tr = new TilesetRenderer(tileset);
                try {
                    tr.renderTiles();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                tr.destroy();
            } catch (NullPointerException | TextureNotFoundException | IOException ex) {
                System.out.println("Tileset not found");
                tileset = new Tileset();
                tileset.getSmartGridArray().add(new SmartGrid());
            }
        } else {
            tileset = new Tileset();
            tileset.getSmartGridArray().add(new SmartGrid());
        }
        handler.setTileset(tileset);

        returnValue = ACEPTED;
        dispose();
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCELED;
        dispose();
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        updateViewGameIcon();
        loadTilesetThumbnail(0);
    }

    public void updateViewGameIcon() {
        jlGameIcon.setIcon(new ImageIcon(handler.getGame().gameIcons[newGame]));
    }

    public int getReturnValue() {
        return returnValue;
    }

    private void loadTilesetThumbnail(int tilesetIndex) {
        BufferedImage img;
        try {
            img = Utils.loadImageAsResource(defaultTsetsFolderPaths[tilesetIndex] + "/" + "TilesetThumbnail.png");
            tilesetThumbnailDisplay1.setImage(img);
            tilesetThumbnailDisplay1.repaint();
        } catch (IOException | IndexOutOfBoundsException ex) {
            tilesetThumbnailDisplay1.setImage(null);
            tilesetThumbnailDisplay1.repaint();
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jcbGame = new JComboBox<>();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jcbTileList = new JComboBox<>();
        jpanelIcon = new JPanel();
        jlGameIcon = new JLabel();
        jbFinish = new JButton();
        jbCancel = new JButton();
        jLabel3 = new JLabel();
        jScrollPane1 = new JScrollPane();
        tilesetThumbnailDisplay1 = new TilesetThumbnailDisplay();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Map - Select Game");
        setResizable(false);
        var contentPane = getContentPane();

        //---- jcbGame ----
        jcbGame.setModel(new DefaultComboBoxModel<>(new String[]{
                "Pokemon Diamond",
                "Pokemon Pearl",
                "Pokemon Platinum",
                "Pokemon Heart Gold",
                "Pokemon Soul Silver",
                "Pokemon Black",
                "Pokemon White",
                "Pokemon Black 2",
                "Pokemon White 2"
        }));
        jcbGame.addActionListener(e -> jcbGameActionPerformed(e));

        //---- jLabel1 ----
        jLabel1.setText("Select Game for this Map: ");

        //---- jLabel2 ----
        jLabel2.setText("Use Predesigned Tileset: ");
        jLabel2.setToolTipText("");

        //---- jcbTileList ----
        jcbTileList.setModel(new DefaultComboBoxModel<>(new String[]{
                "Pokemon Diamond / Pearl",
                "Pokemon Platinum",
                "Pokemon Heart Gold / Soul Silver",
                "Pokemon Black / White",
                "None"
        }));
        jcbTileList.addActionListener(e -> jcbTileListActionPerformed(e));

        //======== jpanelIcon ========
        {
            jpanelIcon.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

            //---- jlGameIcon ----
            jlGameIcon.setMaximumSize(new Dimension(32, 32));
            jlGameIcon.setMinimumSize(new Dimension(32, 32));
            jlGameIcon.setPreferredSize(new Dimension(32, 32));

            GroupLayout jpanelIconLayout = new GroupLayout(jpanelIcon);
            jpanelIcon.setLayout(jpanelIconLayout);
            jpanelIconLayout.setHorizontalGroup(
                    jpanelIconLayout.createParallelGroup()
                            .addGroup(jpanelIconLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jlGameIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jpanelIconLayout.setVerticalGroup(
                    jpanelIconLayout.createParallelGroup()
                            .addGroup(jpanelIconLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jlGameIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //---- jbFinish ----
        jbFinish.setText("Finish");
        jbFinish.setToolTipText("");
        jbFinish.addActionListener(e -> jbFinishActionPerformed(e));

        //---- jbCancel ----
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(e -> jbCancelActionPerformed(e));

        //---- jLabel3 ----
        jLabel3.setText("Tileset Preview: ");

        //======== jScrollPane1 ========
        {
            jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            //======== tilesetThumbnailDisplay1 ========
            {
                tilesetThumbnailDisplay1.setMaximumSize(new Dimension(128, 32767));
                tilesetThumbnailDisplay1.setMinimumSize(new Dimension(128, 100));
                tilesetThumbnailDisplay1.setPreferredSize(new Dimension(128, 137));

                GroupLayout tilesetThumbnailDisplay1Layout = new GroupLayout(tilesetThumbnailDisplay1);
                tilesetThumbnailDisplay1.setLayout(tilesetThumbnailDisplay1Layout);
                tilesetThumbnailDisplay1Layout.setHorizontalGroup(
                        tilesetThumbnailDisplay1Layout.createParallelGroup()
                                .addGap(0, 128, Short.MAX_VALUE)
                );
                tilesetThumbnailDisplay1Layout.setVerticalGroup(
                        tilesetThumbnailDisplay1Layout.createParallelGroup()
                                .addGap(0, 141, Short.MAX_VALUE)
                );
            }
            jScrollPane1.setViewportView(tilesetThumbnailDisplay1);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(jbFinish, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jbCancel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                                                        .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(jcbTileList)
                                                                        .addComponent(jcbGame))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jpanelIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jcbGame, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jcbTileList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(jpanelIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jbFinish)
                                        .addComponent(jbCancel))
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox<String> jcbGame;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JComboBox<String> jcbTileList;
    private JPanel jpanelIcon;
    private JLabel jlGameIcon;
    private JButton jbFinish;
    private JButton jbCancel;
    private JLabel jLabel3;
    private JScrollPane jScrollPane1;
    private TilesetThumbnailDisplay tilesetThumbnailDisplay1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
