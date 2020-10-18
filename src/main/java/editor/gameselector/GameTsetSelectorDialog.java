package editor.gameselector;

import editor.MainFrame;
import editor.TilesetRenderer;
import editor.game.Game;
import editor.handler.MapEditorHandler;
import editor.smartdrawing.SmartGrid;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;

import tileset.TextureNotFoundException;
import tileset.Tileset;
import tileset.TilesetIO;
import utils.Utils;


/**
 * @author Trifindo, JackHack96
 */
public class GameTsetSelectorDialog extends JDialog {

    private MapEditorHandler handler;
    public static final int ACEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private int newGame = Game.DIAMOND;

    //Separator
    private static final String s = "/";

    private static final String rootFolderPath = "/" + "tilesets";
    private static final String none = "None";
    private ArrayList<String> folderPaths;
    private ArrayList<ArrayList<String>> subfolderPaths;
    private ArrayList<ArrayList<String>> tsetNames;

    public GameTsetSelectorDialog(Window owner) {
        super(owner);
        initComponents();
        //Load folder paths
        folderPaths = getSubfolderNamesAsResource(rootFolderPath);
        folderPaths.add(none);

        //Load subfolder paths
        subfolderPaths = new ArrayList<>(folderPaths.size());
        for (int i = 0; i < folderPaths.size() - 1; i++) {
            ArrayList<String> subfolders = new ArrayList<>();
            subfolders = getSubfolderNamesAsResource(rootFolderPath + s + folderPaths.get(i));
            subfolderPaths.add(subfolders);
        }

        //Load tileset filenames
        tsetNames = new ArrayList<>(subfolderPaths.size());
        for (int i = 0; i < subfolderPaths.size(); i++) {
            ArrayList<String> subfiles = new ArrayList<>();
            for (int j = 0; j < subfolderPaths.get(i).size(); j++) {
                ArrayList<String> names;
                names = getSubfileNamesAsResource(rootFolderPath + s
                        + folderPaths.get(i) + s + subfolderPaths.get(i).get(j));
                if (names != null) {
                    subfiles.add(names.get(0));
                }
            }
            tsetNames.add(subfiles);
        }

        //Remove null folders and files
        for (int i = 0; i < subfolderPaths.size() - 1; i++) {
            if (subfolderPaths.get(i) == null) {
                subfolderPaths.remove(i);
                tsetNames.remove(i);
                i--;
            } else if (subfolderPaths.get(i).size() < 1) {
                subfolderPaths.remove(i);
                tsetNames.remove(i);
                i--;
            } else {
                for (int j = 0; j < subfolderPaths.get(i).size(); j++) {
                    if (subfolderPaths.get(i).get(j) == null) {
                        subfolderPaths.get(i).remove(j);
                        tsetNames.get(i).remove(j);
                        j--;
                    }
                }
                if (subfolderPaths.get(i).size() < 1) {
                    subfolderPaths.remove(i);
                    tsetNames.remove(i);
                    i--;
                }
            }
        }


        addItemsToJList(jlTsetFolder, folderPaths);
        jlTsetFolder.setSelectedIndex(0);
        addItemsToJList(jlTsetName, subfolderPaths.get(0));
        jlTsetName.setSelectedIndex(0);

        jScrollPane4.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void jbFinishActionPerformed(ActionEvent e) {
        handler.setGameIndex(newGame);

        Tileset tileset;
        int folderIndex = jlTsetFolder.getSelectedIndex();
        int tilesetIndex = jlTsetName.getSelectedIndex();
        if (tilesetIndex < jlTsetFolder.getModel().getSize() - 1) {
            try {
                String path = rootFolderPath + s + folderPaths.get(folderIndex)
                        + s + subfolderPaths.get(folderIndex).get(tilesetIndex)
                        + s + tsetNames.get(folderIndex).get(tilesetIndex);
                System.out.println("Resulting path: " + path);
                tileset = TilesetIO.readTilesetFromFileAsResource(path);
                TilesetRenderer tr = new TilesetRenderer(tileset);
                try {
                    tr.renderTiles();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                tr.destroy();
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

        returnValue = ACEPTED;
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
                addItemsToJList(jlTsetName, subfolderPaths.get(jlTsetFolder.getSelectedIndex()));
                jlTsetName.setSelectedIndex(0);
            } else {
                addItemsToJList(jlTsetName, new ArrayList<>());
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
            System.out.println("Path name: " + rootFolderPath + s + folderPaths.get(folderIndex) + s + subfolderPaths.get(folderIndex).get(tilesetIndex) + s + thumbnailName);
            img = Utils.loadImageAsResource(rootFolderPath + s + folderPaths.get(folderIndex) + s + subfolderPaths.get(folderIndex).get(tilesetIndex) + s + thumbnailName);
            tilesetThumbnailDisplay.setImage(img);
            tilesetThumbnailDisplay.repaint();
        } catch (IOException | IndexOutOfBoundsException | IllegalArgumentException ex) {
            tilesetThumbnailDisplay.setImage(null);
            tilesetThumbnailDisplay.repaint();
        }

    }

    private void addItemsToJList(JList list, ArrayList<String> items) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : items) {
            model.addElement(item.replace("_", " "));
        }
        list.setModel(model);
    }

    private ArrayList<String> getSubfolderNamesAsResource(String root) {
        System.out.println("Folder root: " + root);
        File mainFolder;
        /*
        try {
            mainFloder = new File(this.getClass().getResource(root).toURI());
        } catch (URISyntaxException ex) {*/
        try {
            //mainFolder = new File(MainFrame.class.getClass().getResource(root).getFile());
            mainFolder = new File(GameTsetSelectorDialog.class.getResource(root).getFile());
            //mainFolder = getFileFromURL(root);
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
        //}
        File[] subdirs = mainFolder.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });

        if (subdirs != null) {
            if (subdirs.length > 0) {
                ArrayList<String> paths = new ArrayList<>(subdirs.length);
                for (int i = 0; i < subdirs.length; i++) {
                    System.out.println("    " + subdirs[i].getName());
                    paths.add(subdirs[i].getName());
                }
                return paths;
            }
        }

        return new ArrayList<>();
    }

    private ArrayList<String> getSubfileNamesAsResource(String root) {
        System.out.println("File root folder: " + root);
        File mainFolder;
        /*try {
            mainFloder = new File(this.getClass().getResource(root).toURI());
        } catch (URISyntaxException ex) {*/
        try {
            //mainFloder = new File(MainFrame.class.getClass().getResource(root).getFile());
            mainFolder = new File(GameTsetSelectorDialog.class.getResource(root).getFile());
            //mainFolder = getFileFromURL(root);
        } catch (NullPointerException ex) {
            return null;
        }
        //}
        File[] subdirs = mainFolder.listFiles(new FilenameFilter() {
            public boolean accept(File mainFloder, String filename) {
                return filename.endsWith(".pdsts");
            }
        });

        if (subdirs != null) {
            if (subdirs.length > 0) {
                System.out.println("    " + subdirs[0].getName());
                ArrayList<String> paths = new ArrayList<>(subdirs.length);
                for (int i = 0; i < subdirs.length; i++) {
                    //System.out.println(subdirs[i].getName());
                    paths.add(subdirs[i].getName());
                }
                return paths;
            }
        }
        System.out.println("Null :/");
        return null;
    }

    private File getFileFromURL(String path) {
        URL url = this.getClass().getClassLoader().getResource(path);
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        } finally {
            return file;
        }
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
                        .addGap(0, 139, Short.MAX_VALUE)
                );
                tilesetThumbnailDisplayLayout.setVerticalGroup(
                    tilesetThumbnailDisplayLayout.createParallelGroup()
                        .addGap(0, 342, Short.MAX_VALUE)
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
                                .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(jLabel4)
                                .addComponent(jScrollPane4, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE))
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
                                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
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
