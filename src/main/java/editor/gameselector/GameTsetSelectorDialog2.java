package editor.gameselector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.TilesetRenderer;
import editor.game.Game;
import editor.handler.MapEditorHandler;
import editor.smartdrawing.SmartGrid;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;

import tileset.TextureNotFoundException;
import tileset.Tileset;
import tileset.TilesetIO;
import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class GameTsetSelectorDialog2 extends JDialog {

    private MapEditorHandler handler;
    public static final int ACCEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private int newGame = Game.DIAMOND;

    //Separator
    private static final String s = "/";

    private static final String rootFolderPath = "/" + "tilesets";
    private static final String none = "None";

    private String[] folderPaths = {
            "Diamond_-_Pearl",
            "Platinum",
            "Heart_Gold_-_Soul_Silver",
            "Black_-_White",
            "Black_-_White_2",
            "Various",
            "None"
    };
    private String[][] subfolderPaths = {
            {"Overworld"},
            {
                    "Tileset_6_-_Overworld_(by_Jiboule)",
                    "Tileset_6_-_Overworld_(by_Driox)",
                    "Tileset_7_-_Overworld_(by_Jiboule)",
                    "Tileset_8_-_Overworld_(by_Jiboule)",
                    "Tileset_9_-_Overworld_(by_Jiboule)",
                    "Tileset_13_-_Overworld_(by_Jiboule)",
                    "Tileset_14_-_Snow_(by_Driox)",
                    "Tileset_17_-_Stark_Mountain_(by_Jiboule)",
                    "Tileset_19_-_Resort_Island_(by_Jiboule)",
                    "Tileset_20_-_House_Indoor",
                    "Tileset_52_-_Cave_(by_Jiboule)",
                    "Tileset_53_-_Forest_(by_Jiboule)"
            },
            {
                    "Tileset_2_-_Overworld",
                    "Tileset_25_-_New_Bark_Town_Houses_(by_Monkeyboy0)",
                    "Tileset_70_-_Tohjo_Falls",
                    "Tileset_73_-_Cerulean_Cave",
                    "Tileset_75_-_Safari_Zone_(by_Mikelan98)",
                    "Tileset_76_-_Ice_Path"
            },
            {
                    "Overworld",
                    "N_Castle"
            },
            {
                    "Tileset_2_-_Overworld_(by_Brom)",
                    "Tileset_2_-_Overworld_(by_AdAstra)",
                    "Cave",
                    "Chargestone_Cave"},
            {"Low_Poly_Tileset", "Sylvan_Town_(by_Anarlaurendil)"}};

    private String[][] tsetNames = {
            {"DiamondPearlTileset.pdsts"},
            {
                    "Tileset_6_PT_Jiboule.pdsts",
                    "tset6.pdsts",
                    "Tileset_7_PT.pdsts",
                    "Tileset_8_PT.pdsts",
                    "Tileset_9_PT.pdsts",
                    "Tileset_13_PT.pdsts",
                    "tset14.pdsts",
                    "Tileset_17_PT.pdsts",
                    "Tileset_19_PT.pdsts",
                    "Tileset_20_PT_House_Indoor.pdsts",
                    "Tileset_52_PT_cave.pdsts",
                    "Tileset_53_PT_Forest.pdsts"
            },
            {
                    "Tileset_2_HGSS_Overworld.pdsts",
                    "Tileset_25_HGSS_by_Monkeyboy0.pdsts",
                    "Tileset_70_HGSS_Tohjo_Falls.pdsts",
                    "Tileset_73_HGSS_Cerulean_cave.pdsts",
                    "Tileset_75_HGSS_safari_zone.pdsts",
                    "Tileset_76_Ice_Path.pdsts"
            },
            {
                    "BlackWhiteTileset.pdsts",
                    "N_Castle_BW.pdsts"
            },
            {
                    "Tileset_2_Overworld_BW2.pdsts",
                    "Tileset_2_BW2_AdAstra.pdsts",
                    "CaveTilesetBW2.pdsts",
                    "ElectricCave.pdsts"
            },
            {"LowPolyTileset.pdsts", "Sylvan_Town.pdsts"}};

    public GameTsetSelectorDialog2(Window owner) {
        super(owner);
        initComponents();

        addItemsToJList(jlTsetFolder, folderPaths);
        jlTsetFolder.setSelectedIndex(0);
        addItemsToJList(jlTsetName, subfolderPaths[0]);
        jlTsetName.setSelectedIndex(0);

        jScrollPane4.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void jbFinishActionPerformed(ActionEvent e) {
        handler.setGameIndex(newGame);

        Tileset tileset;
        int folderIndex = jlTsetFolder.getSelectedIndex();
        int tilesetIndex = jlTsetName.getSelectedIndex();
        if (folderIndex < jlTsetFolder.getModel().getSize() - 1) {
            try {
                String path = rootFolderPath + s + folderPaths[folderIndex]
                        + s + subfolderPaths[folderIndex][tilesetIndex]
                        + s + tsetNames[folderIndex][tilesetIndex];
                System.out.println("Resulting path: " + path);
                tileset = TilesetIO.readTilesetFromFileAsResource(path);
                TilesetRenderer tr = new TilesetRenderer(tileset);
                try {
                    tr.renderTiles();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                tr.destroy();//NEW CODE
            } catch (NullPointerException | TextureNotFoundException | IOException | IndexOutOfBoundsException ex) {
                System.out.println("Tileset not found");
                tileset = new Tileset();
                tileset.getSmartGridArray().add(new SmartGrid());
            }
        } else {
            tileset = new Tileset();
            tileset.getSmartGridArray().add(new SmartGrid());
        }
        handler.setTileset(tileset);

        returnValue = ACCEPTED;
        dispose();
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCELED;
        dispose();
    }

    private void jlGameValueChanged(ListSelectionEvent e) {
        newGame = jlGame.getSelectedIndex();
        updateViewGameIcon();
    }

    private void jlTsetFolderValueChanged(ListSelectionEvent e) {
        if (subfolderPaths != null) {
            if (jlTsetFolder.getSelectedIndex() < jlTsetFolder.getModel().getSize() - 1) {
                addItemsToJList(jlTsetName, subfolderPaths[jlTsetFolder.getSelectedIndex()]);
                jlTsetName.setSelectedIndex(0);
            } else {
                addItemsToJList(jlTsetName, null);
                jlTsetName.setSelectedIndex(0);
            }
        }
    }

    private void jlTsetNameValueChanged(ListSelectionEvent e) {
        if (subfolderPaths != null && tsetNames != null) {
            loadTilesetThumbnail(jlTsetFolder.getSelectedIndex(), jlTsetName.getSelectedIndex());
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        updateViewGameIcon();
        loadTilesetThumbnail(0, 0);

    }

    public void updateViewGameIcon() {
        jlGameIcon.setIcon(new ImageIcon(handler.getGame().gameIcons[newGame]));
    }

    public int getReturnValue() {
        return returnValue;
    }

    private void loadTilesetThumbnail(int folderIndex, int tilesetIndex) {
        BufferedImage img;
        try {
            String thumbnailName = "TilesetThumbnail.png";
            System.out.println("Thumbnail name: " + thumbnailName);
            System.out.println("Path name: " + rootFolderPath + s + folderPaths[folderIndex] + s + subfolderPaths[folderIndex][tilesetIndex] + s + thumbnailName);
            img = Utils.loadImageAsResource(rootFolderPath + s + folderPaths[folderIndex] + s + subfolderPaths[folderIndex][tilesetIndex] + s + thumbnailName);
            tilesetThumbnailDisplay.setImage(img);
            tilesetThumbnailDisplay.repaint();
        } catch (IOException | IndexOutOfBoundsException | IllegalArgumentException ex) {
            tilesetThumbnailDisplay.setImage(null);
            tilesetThumbnailDisplay.repaint();
        }

    }

    private void addItemsToJList(JList list, String[] items) {
        DefaultListModel<String> model = new DefaultListModel<>();
        if (items != null) {
            for (String item : items) {
                model.addElement(item.replace("_", " "));
            }
        }
        list.setModel(model);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jpanelIcon = new JPanel();
        jlGameIcon = new JLabel();
        jbFinish = new JButton();
        jbCancel = new JButton();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jScrollPane1 = new JScrollPane();
        jlGame = new JList<>();
        jScrollPane2 = new JScrollPane();
        jlTsetFolder = new JList<>();
        jScrollPane3 = new JScrollPane();
        jlTsetName = new JList<>();
        jScrollPane4 = new JScrollPane();
        tilesetThumbnailDisplay = new TilesetThumbnailDisplay();
        jLabel4 = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Map - Select Game and Tileset");
        setResizable(false);
        setModal(true);
        var contentPane = getContentPane();

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

        //---- jLabel1 ----
        jLabel1.setText("Game:");

        //---- jLabel2 ----
        jLabel2.setText("Tileset folders:");

        //---- jLabel3 ----
        jLabel3.setText("Tileset name:");

        //======== jScrollPane1 ========
        {

            //---- jlGame ----
            jlGame.setModel(new AbstractListModel<String>() {
                String[] values = {
                    "Pokemon Diamond",
                    "Pokemon Pearl",
                    "Pokemon Platinum",
                    "Pokemon Heart Gold",
                    "Pokemon Soul Silver",
                    "Pokemon Black",
                    "Pokemon White",
                    "Pokemon Black 2",
                    "Pokemon White 2"
                };
                @Override
                public int getSize() { return values.length; }
                @Override
                public String getElementAt(int i) { return values[i]; }
            });
            jlGame.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jlGame.setSelectedIndex(0);
            jlGame.addListSelectionListener(e -> jlGameValueChanged(e));
            jScrollPane1.setViewportView(jlGame);
        }

        //======== jScrollPane2 ========
        {

            //---- jlTsetFolder ----
            jlTsetFolder.setModel(new AbstractListModel<String>() {
                String[] values = {

                };
                @Override
                public int getSize() { return values.length; }
                @Override
                public String getElementAt(int i) { return values[i]; }
            });
            jlTsetFolder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jlTsetFolder.setSelectedIndex(0);
            jlTsetFolder.addListSelectionListener(e -> jlTsetFolderValueChanged(e));
            jScrollPane2.setViewportView(jlTsetFolder);
        }

        //======== jScrollPane3 ========
        {
            jScrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            //---- jlTsetName ----
            jlTsetName.setModel(new AbstractListModel<String>() {
                String[] values = {

                };
                @Override
                public int getSize() { return values.length; }
                @Override
                public String getElementAt(int i) { return values[i]; }
            });
            jlTsetName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jlTsetName.setSelectedIndex(0);
            jlTsetName.addListSelectionListener(e -> jlTsetNameValueChanged(e));
            jScrollPane3.setViewportView(jlTsetName);
        }

        //======== jScrollPane4 ========
        {
            jScrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            jScrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            //======== tilesetThumbnailDisplay ========
            {
                tilesetThumbnailDisplay.setMaximumSize(new Dimension(128, 32767));
                tilesetThumbnailDisplay.setMinimumSize(new Dimension(128, 100));
                tilesetThumbnailDisplay.setPreferredSize(new Dimension(128, 137));

                GroupLayout tilesetThumbnailDisplayLayout = new GroupLayout(tilesetThumbnailDisplay);
                tilesetThumbnailDisplay.setLayout(tilesetThumbnailDisplayLayout);
                tilesetThumbnailDisplayLayout.setHorizontalGroup(
                    tilesetThumbnailDisplayLayout.createParallelGroup()
                        .addGap(0, 128, Short.MAX_VALUE)
                );
                tilesetThumbnailDisplayLayout.setVerticalGroup(
                    tilesetThumbnailDisplayLayout.createParallelGroup()
                        .addGap(0, 347, Short.MAX_VALUE)
                );
            }
            jScrollPane4.setViewportView(tilesetThumbnailDisplay);
        }

        //---- jLabel4 ----
        jLabel4.setText("Tileset preview:");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jLabel1)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jpanelIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jLabel3)
                                .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jLabel4)
                                .addComponent(jScrollPane4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jbFinish, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbCancel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jScrollPane4)
                                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                                .addComponent(jScrollPane1))
                            .addGap(11, 11, 11))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jpanelIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jbCancel)
                        .addComponent(jbFinish))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jpanelIcon;
    private JLabel jlGameIcon;
    private JButton jbFinish;
    private JButton jbCancel;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JScrollPane jScrollPane1;
    private JList<String> jlGame;
    private JScrollPane jScrollPane2;
    private JList<String> jlTsetFolder;
    private JScrollPane jScrollPane3;
    private JList<String> jlTsetName;
    private JScrollPane jScrollPane4;
    private TilesetThumbnailDisplay tilesetThumbnailDisplay;
    private JLabel jLabel4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
