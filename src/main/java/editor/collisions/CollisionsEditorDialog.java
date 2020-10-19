package editor.collisions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.bdhc.Bdhc;
import editor.handler.MapEditorHandler;
import editor.state.CollisionLayerState;
import editor.state.StateHandler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
        float value = jSlider1.getValue() / 100f;
        collisionsDisplay.transparency = value;
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

    public void init(MapEditorHandler handler, BufferedImage mapImage) {
        //CollisionTypes colors = new CollisionTypes();
        this.handler = handler;
        collisionHandler = new CollisionHandler(handler, this);
        collisionsDisplay.init(handler, mapImage, collisionHandler);
        collisionsTypesDisplay.init(collisionHandler);
        collisionLayerSelector.init(collisionHandler);

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
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                handler.setLastCollisionsDirectoryUsed(fc.getSelectedFile().getParent());

                handler.setCollisions(new Collisions(path));

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

                handler.getCollisions().saveToFile(path);
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
        jPanel1 = new JPanel();
        collisionLayerSelector = new CollisionLayerSelector();
        jPanel2 = new JPanel();
        collisionsDisplay = new CollisionsDisplay();
        jPanel3 = new JPanel();
        collisionsTypesDisplay = new CollisionsTypesSelector();
        jPanel4 = new JPanel();
        jtfCollisionType = new JTextField();
        jPanel5 = new JPanel();
        jSlider1 = new JSlider();
        jPanel6 = new JPanel();
        jbImportCollisions = new JButton();
        jbExportCollisions = new JButton();
        jPanel7 = new JPanel();
        jbUndo = new JButton();
        jbRedo = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Collision Editor (BW and BW2 can be bugged)");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[grow,fill]" +
                        "[fill]" +
                        "[fill]",
                // rows
                "[fill]" +
                        "[grow,fill]" +
                        "[fill]" +
                        "[fill]"));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder(null, "Collision Layers", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS));

            //======== collisionLayerSelector ========
            {

                GroupLayout collisionLayerSelectorLayout = new GroupLayout(collisionLayerSelector);
                collisionLayerSelector.setLayout(collisionLayerSelectorLayout);
                collisionLayerSelectorLayout.setHorizontalGroup(
                        collisionLayerSelectorLayout.createParallelGroup()
                                .addGap(0, 512, Short.MAX_VALUE)
                );
                collisionLayerSelectorLayout.setVerticalGroup(
                        collisionLayerSelectorLayout.createParallelGroup()
                                .addGap(0, 84, Short.MAX_VALUE)
                );
            }
            jPanel1.add(collisionLayerSelector);
        }
        contentPane.add(jPanel1, "cell 0 2 1 2");

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder(null, "Collision Data", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.X_AXIS));

            //======== collisionsDisplay ========
            {
                collisionsDisplay.setBorder(LineBorder.createBlackLineBorder());

                GroupLayout collisionsDisplayLayout = new GroupLayout(collisionsDisplay);
                collisionsDisplay.setLayout(collisionsDisplayLayout);
                collisionsDisplayLayout.setHorizontalGroup(
                        collisionsDisplayLayout.createParallelGroup()
                                .addGap(0, 510, Short.MAX_VALUE)
                );
                collisionsDisplayLayout.setVerticalGroup(
                        collisionsDisplayLayout.createParallelGroup()
                                .addGap(0, 510, Short.MAX_VALUE)
                );
            }
            jPanel2.add(collisionsDisplay);
        }
        contentPane.add(jPanel2, "cell 0 0 1 2");

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder(null, "Collision Type Selector", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.X_AXIS));

            //======== collisionsTypesDisplay ========
            {
                collisionsTypesDisplay.setBorder(LineBorder.createBlackLineBorder());

                GroupLayout collisionsTypesDisplayLayout = new GroupLayout(collisionsTypesDisplay);
                collisionsTypesDisplay.setLayout(collisionsTypesDisplayLayout);
                collisionsTypesDisplayLayout.setHorizontalGroup(
                        collisionsTypesDisplayLayout.createParallelGroup()
                                .addGap(0, 126, Short.MAX_VALUE)
                );
                collisionsTypesDisplayLayout.setVerticalGroup(
                        collisionsTypesDisplayLayout.createParallelGroup()
                                .addGap(0, 510, Short.MAX_VALUE)
                );
            }
            jPanel3.add(collisionsTypesDisplay);
        }
        contentPane.add(jPanel3, "cell 1 0 1 2");

        //======== jPanel4 ========
        {
            jPanel4.setBorder(new TitledBorder(null, "Collision Type", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.X_AXIS));

            //---- jtfCollisionType ----
            jtfCollisionType.setEditable(false);
            jtfCollisionType.setBackground(Color.white);
            jPanel4.add(jtfCollisionType);
        }
        contentPane.add(jPanel4, "cell 1 2");

        //======== jPanel5 ========
        {
            jPanel5.setBorder(new TitledBorder(null, "Opacity", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.X_AXIS));

            //---- jSlider1 ----
            jSlider1.addChangeListener(e -> jSlider1StateChanged(e));
            jPanel5.add(jSlider1);
        }
        contentPane.add(jPanel5, "cell 1 3");

        //======== jPanel6 ========
        {
            jPanel6.setBorder(new TitledBorder(null, "Collision File", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
            jPanel6.setLayout(new GridBagLayout());
            ((GridBagLayout) jPanel6.getLayout()).columnWidths = new int[]{0, 0};
            ((GridBagLayout) jPanel6.getLayout()).rowHeights = new int[]{0, 0, 0};
            ((GridBagLayout) jPanel6.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
            ((GridBagLayout) jPanel6.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

            //---- jbImportCollisions ----
            jbImportCollisions.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
            jbImportCollisions.setText("Import");
            jbImportCollisions.addActionListener(e -> jbImportCollisionsActionPerformed(e));
            jPanel6.add(jbImportCollisions, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

            //---- jbExportCollisions ----
            jbExportCollisions.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
            jbExportCollisions.setText("Export");
            jbExportCollisions.addActionListener(e -> jbExportCollisionsActionPerformed(e));
            jPanel6.add(jbExportCollisions, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(jPanel6, "cell 2 1 1 3");

        //======== jPanel7 ========
        {
            jPanel7.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEFT, TitledBorder.ABOVE_TOP));
            jPanel7.setLayout(new BoxLayout(jPanel7, BoxLayout.X_AXIS));

            //---- jbUndo ----
            jbUndo.setIcon(new ImageIcon(getClass().getResource("/icons/undoIcon.png")));
            jbUndo.setDisabledIcon(new ImageIcon(getClass().getResource("/icons/undoDisabledIcon.png")));
            jbUndo.setEnabled(false);
            jbUndo.setFocusPainted(false);
            jbUndo.addActionListener(e -> jbUndoActionPerformed(e));
            jPanel7.add(jbUndo);

            //---- jbRedo ----
            jbRedo.setIcon(new ImageIcon(getClass().getResource("/icons/redoIcon.png")));
            jbRedo.setDisabledIcon(new ImageIcon(getClass().getResource("/icons/redoDisabledIcon.png")));
            jbRedo.setEnabled(false);
            jbRedo.setFocusPainted(false);
            jbRedo.addActionListener(e -> jbRedoActionPerformed(e));
            jPanel7.add(jbRedo);
        }
        contentPane.add(jPanel7, "cell 2 0");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private CollisionLayerSelector collisionLayerSelector;
    private JPanel jPanel2;
    private CollisionsDisplay collisionsDisplay;
    private JPanel jPanel3;
    private CollisionsTypesSelector collisionsTypesDisplay;
    private JPanel jPanel4;
    private JTextField jtfCollisionType;
    private JPanel jPanel5;
    private JSlider jSlider1;
    private JPanel jPanel6;
    private JButton jbImportCollisions;
    private JButton jbExportCollisions;
    private JPanel jPanel7;
    private JButton jbUndo;
    private JButton jbRedo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
