package formats.collisions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.game.Game;
import editor.handler.MapEditorHandler;
import editor.state.CollisionLayerState;
import editor.state.StateHandler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.*;
import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class CollisionsEditorDialog extends JDialog {

    private MapEditorHandler handler;
    private CollisionHandler collisionHandler;

    public CollisionsEditorDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jSlider1StateChanged(ChangeEvent e) {
        collisionsDisplay.transparency = jSlider1.getValue() / 100f;
        collisionsDisplay.repaint();
    }

    private void jbImportCollisionsActionPerformed(ActionEvent e) {
        openCollisionWithDialog();
    }

    private void jbExportCollisionsActionPerformed(ActionEvent e) {
        saveCollisionWithDialog();
    }

    private void jbUndoActionPerformed(ActionEvent e) {
        undoState();
    }

    private void jbRedoActionPerformed(ActionEvent e) {
        redoState();
    }

    private void jcbFileSelectedActionPerformed(ActionEvent e) {
        collisionHandler.setIndexCollisionFileSelected(jcbFileSelected.getSelectedIndex());
        collisionHandler.resetMapStateHandler();
        jbUndo.setEnabled(false);
        jbRedo.setEnabled(false);
        updateView();
        repaintDisplay();
        repaintTypesDisplay();
        collisionLayerSelector.drawAllLayers();
        repaintLayerSelector();
    }

    private void jcbLockTerrainActionPerformed(ActionEvent e) {
        collisionHandler.setLockTerrainLayersEnabled(jcbLockTerrain.isSelected());
        repaintLayerSelector();
        repaintDisplay();
    }

    public void init(MapEditorHandler handler, BufferedImage mapImage) {
        //CollisionTypes colors = new CollisionTypes();
        this.handler = handler;
        collisionHandler = new CollisionHandler(handler, this);
        collisionsDisplay.init(handler, mapImage, collisionHandler);
        collisionsTypesDisplay.init(collisionHandler);
        collisionLayerSelector.init(collisionHandler);

        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, Game.isGenV(handler.getGameIndex()) ? "fileSelectorPanel" : "emptyPanel");

        updateView();
    }

    public void updateView() {
        updateViewCollisionTypeName();
    }

    public void updateViewCollisionTypeName() {
        jtfCollisionType.setText(collisionHandler.getCollisionNameSelected());
    }

    public void openCollisionWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastCollisionsDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastCollisionsDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Collision File (*.per)", Collisions.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open");

        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            String path = fc.getSelectedFile().getPath();
            handler.setLastCollisionsDirectoryUsed(fc.getSelectedFile().getParent());

            collisionHandler.setCollisions(new Collisions(path));

            collisionHandler.resetMapStateHandler();
            updateView();
            collisionsDisplay.repaint();
            collisionLayerSelector.drawAllLayers();
            collisionLayerSelector.repaint();
            collisionsTypesDisplay.repaint();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Can't open file", "Error collision file", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveCollisionWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastCollisionsDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastCollisionsDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Collision File (*.per)", Collisions.fileExtension));
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                String path = fc.getSelectedFile().getPath();
                path = Utils.addExtensionToPath(path, Collisions.fileExtension);

                collisionHandler.getCollisions().saveToFile(path);
                JOptionPane.showMessageDialog(this, "Collision succesfully exported.",
                        "Collisions saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't save file.",
                        "Error saving collision", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void repaintDisplay() {
        collisionsDisplay.repaint();
    }

    public void repaintTypesDisplay() {
        collisionsTypesDisplay.repaint();
    }

    public void repaintLayerSelector() {
        collisionLayerSelector.repaint();
    }

    public void redrawSelectedLayerInSelector() {
        collisionLayerSelector.drawLayer(collisionHandler.getIndexLayerSelected());
    }

    public void undoState() {
        StateHandler collStateHandler = collisionHandler.getCollisionStateHandler();
        if (collStateHandler.canGetPreviousState()) {
            CollisionLayerState state = (CollisionLayerState) collStateHandler.getPreviousState(new CollisionLayerState("Collision Edit", collisionHandler));
            state.revertState();
            jbRedo.setEnabled(true);
            if (!collStateHandler.canGetPreviousState()) {
                jbUndo.setEnabled(false);
            }
            collisionsDisplay.repaint();
            collisionLayerSelector.drawLayer(state.getLayerIndex());
            collisionLayerSelector.repaint();
        }
    }

    public void redoState() {
        StateHandler collStateHandler = collisionHandler.getCollisionStateHandler();
        if (collStateHandler.canGetNextState()) {
            CollisionLayerState state = (CollisionLayerState) collStateHandler.getNextState();
            state.revertState();
            jbUndo.setEnabled(true);
            collisionsDisplay.repaint();
            collisionLayerSelector.drawLayer(state.getLayerIndex());
            collisionLayerSelector.repaint();
            if (!collStateHandler.canGetNextState()) {
                jbRedo.setEnabled(false);
            }
        }
    }

    /*
    public void undoMapState() {
        StateHandler mapStateHandler = handler.getMapStateHandler();
        if (mapStateHandler.canGetPreviousState()) {
            MapLayerState state = (MapLayerState) mapStateHandler.getPreviousState(new MapLayerState("Map Edit", handler));
            state.revertState();
            jbRedo.setEnabled(true);
            if (!mapStateHandler.canGetPreviousState()) {
                jbUndo.setEnabled(false);
            }
            mapDisplay.repaint();
            thumbnailLayerSelector.drawLayerThumbnail(state.getLayerIndex());
            thumbnailLayerSelector.repaint();
        }
    }

    public void redoMapState() {
        StateHandler mapStateHandler = handler.getMapStateHandler();
        if (mapStateHandler.canGetNextState()) {
            MapLayerState state = (MapLayerState) mapStateHandler.getNextState();
            state.revertState();
            jbUndo.setEnabled(true);
            mapDisplay.repaint();
            thumbnailLayerSelector.drawLayerThumbnail(state.getLayerIndex());
            thumbnailLayerSelector.repaint();
            if (!mapStateHandler.canGetNextState()) {
                jbRedo.setEnabled(false);
            }
        }
    }
    */

    public JButton getUndoButton() {
        return jbUndo;
    }

    public JButton getRedoButton() {
        return jbRedo;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        jbImportCollisions = new JButton();
        jbExportCollisions = new JButton();
        label1 = new JLabel();
        jPanel1 = new JPanel();
        collisionLayerSelector = new CollisionLayerSelector();
        jPanel2 = new JPanel();
        collisionsDisplay = new CollisionsDisplay();
        jPanel3 = new JPanel();
        collisionsTypesDisplay = new CollisionsTypesSelector();
        jPanel7 = new JPanel();
        jbUndo = new JButton();
        jbRedo = new JButton();
        jPanel4 = new JPanel();
        jtfCollisionType = new JTextField();
        jPanel5 = new JPanel();
        jSlider1 = new JSlider();
        cardPanel = new JPanel();
        emptyPanel = new JPanel();
        fileSelectorPanel = new JPanel();
        label2 = new JLabel();
        jcbFileSelected = new JComboBox<>();
        jcbLockTerrain = new JCheckBox();
        panel2 = new JPanel();
        textArea2 = new JTextArea();
        panel3 = new JPanel();
        textArea1 = new JTextArea();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Collision Editor");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets dialog,hidemode 3,gap 5 5",
            // columns
            "[shrink 0,fill]" +
            "[shrink 0,fill]" +
            "[shrink 0,fill]" +
            "[grow,fill]",
            // rows
            "[]" +
            "[fill]" +
            "[]" +
            "[]" +
            "[]" +
            "[grow,fill]"));

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[]"));

            //---- jbImportCollisions ----
            jbImportCollisions.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
            jbImportCollisions.setText("Import PER");
            jbImportCollisions.addActionListener(e -> jbImportCollisionsActionPerformed(e));
            panel1.add(jbImportCollisions, "cell 0 0");

            //---- jbExportCollisions ----
            jbExportCollisions.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
            jbExportCollisions.setText("Export PER");
            jbExportCollisions.addActionListener(e -> jbExportCollisionsActionPerformed(e));
            panel1.add(jbExportCollisions, "cell 1 0");

            //---- label1 ----
            label1.setText("PER files are saved automatically when pressing the save map button from the main window");
            label1.setIcon(new ImageIcon(getClass().getResource("/icons/informationIcon.png")));
            panel1.add(label1, "cell 3 0");
        }
        contentPane.add(panel1, "cell 0 0 4 1");

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder(null, "Collision Layers", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS));

            //---- collisionLayerSelector ----
            collisionLayerSelector.setPreferredSize(new Dimension(128, 512));
            jPanel1.add(collisionLayerSelector);
        }
        contentPane.add(jPanel1, "cell 0 1 1 5");

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder(null, "Collision Data", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.X_AXIS));

            //---- collisionsDisplay ----
            collisionsDisplay.setBorder(LineBorder.createBlackLineBorder());
            jPanel2.add(collisionsDisplay);
        }
        contentPane.add(jPanel2, "cell 1 1 1 5");

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder(null, "Collision Type Selector", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.X_AXIS));

            //---- collisionsTypesDisplay ----
            collisionsTypesDisplay.setBorder(LineBorder.createBlackLineBorder());
            jPanel3.add(collisionsTypesDisplay);
        }
        contentPane.add(jPanel3, "cell 2 1 1 5");

        //======== jPanel7 ========
        {
            jPanel7.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEFT, TitledBorder.ABOVE_TOP));
            jPanel7.setLayout(new GridBagLayout());
            ((GridBagLayout)jPanel7.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout)jPanel7.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)jPanel7.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
            ((GridBagLayout)jPanel7.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //---- jbUndo ----
            jbUndo.setIcon(new ImageIcon(getClass().getResource("/icons/undoIcon.png")));
            jbUndo.setDisabledIcon(new ImageIcon(getClass().getResource("/icons/undoDisabledIcon.png")));
            jbUndo.setEnabled(false);
            jbUndo.setFocusPainted(false);
            jbUndo.addActionListener(e -> jbUndoActionPerformed(e));
            jPanel7.add(jbUndo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 5), 0, 0));

            //---- jbRedo ----
            jbRedo.setIcon(new ImageIcon(getClass().getResource("/icons/redoIcon.png")));
            jbRedo.setDisabledIcon(new ImageIcon(getClass().getResource("/icons/redoDisabledIcon.png")));
            jbRedo.setEnabled(false);
            jbRedo.setFocusPainted(false);
            jbRedo.addActionListener(e -> jbRedoActionPerformed(e));
            jPanel7.add(jbRedo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(jPanel7, "cell 3 1");

        //======== jPanel4 ========
        {
            jPanel4.setBorder(new TitledBorder(null, "Collision Type", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.X_AXIS));

            //---- jtfCollisionType ----
            jtfCollisionType.setEditable(false);
            jtfCollisionType.setBackground(Color.white);
            jPanel4.add(jtfCollisionType);
        }
        contentPane.add(jPanel4, "cell 3 2");

        //======== jPanel5 ========
        {
            jPanel5.setBorder(new TitledBorder(null, "Opacity", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.X_AXIS));

            //---- jSlider1 ----
            jSlider1.addChangeListener(e -> jSlider1StateChanged(e));
            jPanel5.add(jSlider1);
        }
        contentPane.add(jPanel5, "cell 3 3");

        //======== cardPanel ========
        {
            cardPanel.setLayout(new CardLayout());

            //======== emptyPanel ========
            {
                emptyPanel.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[fill]",
                    // rows
                    "[]"));
            }
            cardPanel.add(emptyPanel, "emptyPanel");

            //======== fileSelectorPanel ========
            {
                fileSelectorPanel.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[fill]" +
                    "[grow,fill]",
                    // rows
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]"));

                //---- label2 ----
                label2.setText("File selected:");
                fileSelectorPanel.add(label2, "cell 0 0");

                //---- jcbFileSelected ----
                jcbFileSelected.setModel(new DefaultComboBoxModel<>(new String[] {
                    "File 1",
                    "File 2"
                }));
                jcbFileSelected.addActionListener(e -> jcbFileSelectedActionPerformed(e));
                fileSelectorPanel.add(jcbFileSelected, "cell 1 0");

                //---- jcbLockTerrain ----
                jcbLockTerrain.setText("Lock terrain layers");
                jcbLockTerrain.setSelected(true);
                jcbLockTerrain.addActionListener(e -> jcbLockTerrainActionPerformed(e));
                fileSelectorPanel.add(jcbLockTerrain, "cell 0 1 2 1");

                //======== panel2 ========
                {
                    panel2.setBorder(new TitledBorder("Info"));
                    panel2.setLayout(new MigLayout(
                        "hidemode 3",
                        // columns
                        "[grow,fill]",
                        // rows
                        "[]"));

                    //---- textArea2 ----
                    textArea2.setEditable(false);
                    textArea2.setWrapStyleWord(true);
                    textArea2.setLineWrap(true);
                    textArea2.setText("The Terrain and Height layers should be edited with the Terrain Editor");
                    panel2.add(textArea2, "cell 0 0");
                }
                fileSelectorPanel.add(panel2, "cell 0 2 2 1");

                //======== panel3 ========
                {
                    panel3.setBorder(new TitledBorder("Warning"));
                    panel3.setLayout(new MigLayout(
                        "hidemode 3",
                        // columns
                        "[grow,fill]",
                        // rows
                        "[]"));

                    //---- textArea1 ----
                    textArea1.setWrapStyleWord(true);
                    textArea1.setLineWrap(true);
                    textArea1.setText("SDSME does not import/export PER files from BW/BW2 properly ");
                    textArea1.setEditable(false);
                    panel3.add(textArea1, "cell 0 0");
                }
                fileSelectorPanel.add(panel3, "cell 0 3 2 1");
            }
            cardPanel.add(fileSelectorPanel, "fileSelectorPanel");
        }
        contentPane.add(cardPanel, "cell 3 4");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton jbImportCollisions;
    private JButton jbExportCollisions;
    private JLabel label1;
    private JPanel jPanel1;
    private CollisionLayerSelector collisionLayerSelector;
    private JPanel jPanel2;
    private CollisionsDisplay collisionsDisplay;
    private JPanel jPanel3;
    private CollisionsTypesSelector collisionsTypesDisplay;
    private JPanel jPanel7;
    private JButton jbUndo;
    private JButton jbRedo;
    private JPanel jPanel4;
    private JTextField jtfCollisionType;
    private JPanel jPanel5;
    private JSlider jSlider1;
    private JPanel cardPanel;
    private JPanel emptyPanel;
    private JPanel fileSelectorPanel;
    private JLabel label2;
    private JComboBox<String> jcbFileSelected;
    private JCheckBox jcbLockTerrain;
    private JPanel panel2;
    private JTextArea textArea2;
    private JPanel panel3;
    private JTextArea textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
