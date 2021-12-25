package editor.buildingeditor;

import editor.handler.MapEditorHandler;
import net.miginfocom.swing.*;
import utils.Utils;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * @author Trifindo, JackHack96
 */
public class BuildingEditorDialog extends JDialog {
    private MapEditorHandler handler;
    private BuildHandler buildHandler;

    private boolean jlBuildingIDsEnabled = true;

    private boolean firstRemovedBuilding = true;

    public BuildingEditorDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jlBuildingIDsValueChanged(ListSelectionEvent e) {
        if (jlBuildingIDsEnabled) {
            updateViewMaterialOrder();
        }
    }

    private void jbMoveUpActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void jbMoveDownActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void jbOpenMatshpActionPerformed(ActionEvent e) {
        openMatshpWithDialog();
    }

    private void jbSaveMatshpActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildModelMatshp() != null) {
            saveMatshpWithDialog();
        }
    }

    private void jbBmmAddBuildingActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildModelMatshp() != null) {
            addBuildingBmmWithDialog();
        }
    }

    private void jbBmmReplaceBuildingActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildModelMatshp() != null) {
            replaceBuildingBmmWithDialog();
        }
    }

    private void jbBmmRemoveBuildingActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildModelMatshp() != null) {
            removeBuildingBmm();
        }
    }

    private void jbOpenBuilTilesetListActionPerformed(ActionEvent e) {
        openTilesetListWithDialog();
    }

    private void jbSaveBuildTilesetListActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildTilesetList() != null) {
            saveBtlWithDialog();
        }
    }

    private void jbAddBuildingTblActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildTilesetList() != null) {
            addBuildingTblWithDialog();
        }
    }

    private void jbReplaceBuildingTblActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildTilesetList() != null) {
            replaceBuildingTblWithDialog();
        }
    }

    private void jbRemoveBuildingTblActionPerformed(ActionEvent e) {
        if (buildHandler.getBuildTilesetList() != null) {
            removeBuildingTbl();
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        this.buildHandler = new BuildHandler();
    }

    public void openMatshpWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBuildDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
        }
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Build Model Matshp File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                buildHandler.loadBuildModelMashup(fc.getSelectedFile().getPath());

                updateViewBmm();
                updateViewMaterialOrder();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openTilesetListWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBuildDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
        }
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Build Tileset List");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                buildHandler.loadBuildTilesetList(fc.getSelectedFile().getPath());
                updateViewBtl();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveMatshpWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBuildDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
        }
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save Build Model Matshp File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                String path = fc.getSelectedFile().getPath();
                path = Utils.addExtensionToPath(path, "dat");

                buildHandler.getBuildModelMatshp().saveToFile(path);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't save file.",
                        "Error saving Build Model Matshp File", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveBtlWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBuildDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
        }
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save Build Tile List");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                String path = fc.getSelectedFile().getPath();

                buildHandler.getBuildTilesetList().saveToFile(path);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't save file.",
                        "Error saving Building Tileset List", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void updateViewBmm() {
        updateViewBmm(0);
    }

    public void updateViewBmm(int indexSelected) {
        System.out.println("Index selected: " + indexSelected);
        jlBuildingIDsEnabled = false;
        DefaultListModel<String> demoList = new DefaultListModel<>();
        for (int i = 0; i < buildHandler.getBuildModelMatshp().getAllMaterials().size(); i++) {
            demoList.addElement("Building " + String.format("%03d", i));
        }
        jlBuildingIDs.setModel(demoList);
        jlBuildingIDs.setSelectedIndex(indexSelected);
        jlBuildingIDs.ensureIndexIsVisible(indexSelected);
        jlBuildingIDsEnabled = true;
        //jlBuildingIDs.ensureIndexIsVisible(indexSelected);
    }

    public void updateViewMaterialOrder() {
        DefaultListModel<String> demoList = new DefaultListModel<>();
        int buildingSelected = jlBuildingIDs.getSelectedIndex();
        List<Integer> materials = buildHandler.getBuildModelMatshp().getMaterials(buildingSelected);
        if (materials != null) {
            for (Integer material : materials) {
                demoList.addElement("Material " + material);
            }
        } else {
            demoList.addElement("Undefined");
        }

        System.out.println("ID list index selected: " + jlBuildingIDs.getSelectedIndex());

        jlMaterialOrder.setModel(demoList);
        jlMaterialOrder.setSelectedIndex(0);
    }

    public void updateViewBtl() {
        updateViewBtl(0);
    }

    public void updateViewBtl(int indexSelected) {
        DefaultListModel<String> demoList = new DefaultListModel<>();
        List<Integer> buildingIDs = buildHandler.getBuildTilesetList().getBuildingIDs();
        for (int i = 0; i < buildHandler.getBuildTilesetList().getBuildingIDs().size(); i++) {
            demoList.addElement("Building " + String.format("%03d", buildingIDs.get(i)));
        }
        jlBuildTilesetList.setModel(demoList);
        jlBuildTilesetList.setSelectedIndex(indexSelected);
        jlBuildTilesetList.ensureIndexIsVisible(indexSelected);
    }

    public void addBuildingBmmWithDialog() {
        final BuildingMaterialRequestDialog dialog = new BuildingMaterialRequestDialog(handler.getMainFrame());
        dialog.init("Number of materials for the Building: ", 1, 20);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == IntegerRequestDialog.ACCEPTED) {
            List<Integer> newMaterials;
            if (dialog.useUndefined()) {
                newMaterials = null;
            } else {
                int nMaterials = dialog.getIntegerRequested();
                newMaterials = new ArrayList<>(nMaterials);
                for (int i = 0; i < nMaterials; i++) {
                    newMaterials.add(i);
                }
            }
            buildHandler.getBuildModelMatshp().getAllMaterials().add(newMaterials);
            updateViewBmm(buildHandler.getBuildModelMatshp().getAllMaterials().size() - 1);
            updateViewMaterialOrder();
        }
    }

    public void addBuildingTblWithDialog() {
        final IntegerRequestDialog dialog = new IntegerRequestDialog(handler.getMainFrame());
        dialog.init("Enter the building ID: ", 0, 10000);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == IntegerRequestDialog.ACCEPTED) {
            int buildingID = dialog.getIntegerRequested();

            if (!buildHandler.getBuildTilesetList().getBuildingIDs().contains(buildingID)) {
                buildHandler.getBuildTilesetList().getBuildingIDs().add(buildingID);
                updateViewBtl(buildHandler.getBuildTilesetList().getBuildingIDs().size() - 1);
            } else {
                JOptionPane.showMessageDialog(this, "The building already exists in the list",
                        "Repeated building", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void replaceBuildingBmmWithDialog() {
        final BuildingMaterialRequestDialog dialog = new BuildingMaterialRequestDialog(handler.getMainFrame());
        dialog.init("Number of materials for the Building: ", 1, 20);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == IntegerRequestDialog.ACCEPTED) {
            ArrayList<Integer> newMaterials;
            if (dialog.useUndefined()) {
                newMaterials = null;
            } else {
                int nMaterials = dialog.getIntegerRequested();
                newMaterials = new ArrayList<>(nMaterials);
                for (int i = 0; i < nMaterials; i++) {
                    newMaterials.add(i);
                }
            }
            buildHandler.getBuildModelMatshp().getAllMaterials().set(jlBuildingIDs.getSelectedIndex(), newMaterials);
            updateViewBmm(buildHandler.getBuildModelMatshp().getAllMaterials().size() - 1);
            updateViewMaterialOrder();
        }
    }

    public void replaceBuildingTblWithDialog() {
        final IntegerRequestDialog dialog = new IntegerRequestDialog(handler.getMainFrame());
        dialog.init("Enter the building ID: ", 0, 10000);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == IntegerRequestDialog.ACCEPTED) {
            int buildingID = dialog.getIntegerRequested();

            if (!buildHandler.getBuildTilesetList().getBuildingIDs().contains(buildingID)) {
                buildHandler.getBuildTilesetList().getBuildingIDs().set(jlBuildTilesetList.getSelectedIndex(), buildingID);
                updateViewBtl(jlBuildTilesetList.getSelectedIndex());
            } else {
                JOptionPane.showMessageDialog(this, "The building already exists in the list",
                        "Repeated building", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void removeBuildingBmm() {
        if (buildHandler.getBuildModelMatshp().getAllMaterials().size() > 1) {
            if (firstRemovedBuilding) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to remove the selected building?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    firstRemovedBuilding = false;
                } else {
                    return;
                }
            }
            int index = jlBuildingIDs.getSelectedIndex();
            buildHandler.getBuildModelMatshp().getAllMaterials().remove(index);
            if (index > 0) {
                index--;
            }
            updateViewBmm(index);
            updateViewMaterialOrder();
        } else {
            JOptionPane.showMessageDialog(this, "There must be at least 1 building",
                    "Can't remove building", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeBuildingTbl() {
        if (buildHandler.getBuildTilesetList().getBuildingIDs().size() > 1) {
            int index = jlBuildTilesetList.getSelectedIndex();
            buildHandler.getBuildTilesetList().getBuildingIDs().remove(index);
            if (index > 0) {
                index--;
            }
            updateViewBtl(index);
        } else {
            JOptionPane.showMessageDialog(this, "There must be at least 1 building",
                    "Can't remove building", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jScrollPane1 = new JScrollPane();
        jlBuildingIDs = new JList<>();
        jScrollPane2 = new JScrollPane();
        jlMaterialOrder = new JList<>();
        panel1 = new JPanel();
        jbOpenMatshp = new JButton();
        jbSaveMatshp = new JButton();
        jbBmmAddBuilding = new JButton();
        jbBmmReplaceBuilding = new JButton();
        jbBmmRemoveBuilding = new JButton();
        jLabel3 = new JLabel();
        panel2 = new JPanel();
        jbMoveUp = new JButton();
        jbMoveDown = new JButton();
        jPanel3 = new JPanel();
        jLabel4 = new JLabel();
        jScrollPane3 = new JScrollPane();
        jlBuildTilesetList = new JList<>();
        panel3 = new JPanel();
        jbOpenBuilTilesetList = new JButton();
        jbSaveBuildTilesetList = new JButton();
        jbAddBuildingTbl = new JButton();
        jbReplaceBuildingTbl = new JButton();
        jbRemoveBuildingTbl = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Building Editor");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Building Materials (build_model_matshp.dat)"));
            jPanel1.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]" +
                            "[grow,fill]" +
                            "[fill]",
                    // rows
                    "[fill]" +
                            "[grow,fill]"));

            //---- jLabel1 ----
            jLabel1.setText("Building List:");
            jPanel1.add(jLabel1, "cell 0 0");

            //---- jLabel2 ----
            jLabel2.setText("Material order:");
            jPanel1.add(jLabel2, "cell 1 0");

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setToolTipText("");
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- jlBuildingIDs ----
                jlBuildingIDs.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };

                    @Override
                    public int getSize() {
                        return values.length;
                    }

                    @Override
                    public String getElementAt(int i) {
                        return values[i];
                    }
                });
                jlBuildingIDs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlBuildingIDs.addListSelectionListener(e -> jlBuildingIDsValueChanged(e));
                jScrollPane1.setViewportView(jlBuildingIDs);
            }
            jPanel1.add(jScrollPane1, "cell 0 1");

            //======== jScrollPane2 ========
            {
                jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- jlMaterialOrder ----
                jlMaterialOrder.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };

                    @Override
                    public int getSize() {
                        return values.length;
                    }

                    @Override
                    public String getElementAt(int i) {
                        return values[i];
                    }
                });
                jlMaterialOrder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jScrollPane2.setViewportView(jlMaterialOrder);
            }
            jPanel1.add(jScrollPane2, "cell 1 1");

            //======== panel1 ========
            {
                panel1.setLayout(new GridBagLayout());
                ((GridBagLayout) panel1.getLayout()).columnWidths = new int[]{0, 0};
                ((GridBagLayout) panel1.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
                ((GridBagLayout) panel1.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
                ((GridBagLayout) panel1.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- jbOpenMatshp ----
                jbOpenMatshp.setText("Open Matshp...");
                jbOpenMatshp.addActionListener(e -> jbOpenMatshpActionPerformed(e));
                panel1.add(jbOpenMatshp, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                //---- jbSaveMatshp ----
                jbSaveMatshp.setText("Save Matshp...");
                jbSaveMatshp.addActionListener(e -> jbSaveMatshpActionPerformed(e));
                panel1.add(jbSaveMatshp, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                //---- jbBmmAddBuilding ----
                jbBmmAddBuilding.setText("Add Building...");
                jbBmmAddBuilding.addActionListener(e -> jbBmmAddBuildingActionPerformed(e));
                panel1.add(jbBmmAddBuilding, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                //---- jbBmmReplaceBuilding ----
                jbBmmReplaceBuilding.setText("Replace Building...");
                jbBmmReplaceBuilding.addActionListener(e -> jbBmmReplaceBuildingActionPerformed(e));
                panel1.add(jbBmmReplaceBuilding, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                //---- jbBmmRemoveBuilding ----
                jbBmmRemoveBuilding.setText("Remove Building...");
                jbBmmRemoveBuilding.addActionListener(e -> jbBmmRemoveBuildingActionPerformed(e));
                panel1.add(jbBmmRemoveBuilding, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                //---- jLabel3 ----
                jLabel3.setText("Move Material:");
                panel1.add(jLabel3, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                //======== panel2 ========
                {
                    panel2.setLayout(new GridLayout());

                    //---- jbMoveUp ----
                    jbMoveUp.setText("\u25b2");
                    jbMoveUp.setEnabled(false);
                    jbMoveUp.addActionListener(e -> jbMoveUpActionPerformed(e));
                    panel2.add(jbMoveUp);

                    //---- jbMoveDown ----
                    jbMoveDown.setText("\u25bc");
                    jbMoveDown.setEnabled(false);
                    jbMoveDown.addActionListener(e -> jbMoveDownActionPerformed(e));
                    panel2.add(jbMoveDown);
                }
                panel1.add(panel2, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            jPanel1.add(panel1, "cell 2 1");
        }
        contentPane.add(jPanel1);

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Tileset Building List (area_build.narc files)"));
            jPanel3.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]" +
                            "[fill]",
                    // rows
                    "[fill]" +
                            "[grow,fill]"));

            //---- jLabel4 ----
            jLabel4.setText("Buildings used in tileset:");
            jPanel3.add(jLabel4, "cell 0 0 2 1");

            //======== jScrollPane3 ========
            {
                jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane3.setToolTipText("");
                jScrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- jlBuildTilesetList ----
                jlBuildTilesetList.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };

                    @Override
                    public int getSize() {
                        return values.length;
                    }

                    @Override
                    public String getElementAt(int i) {
                        return values[i];
                    }
                });
                jlBuildTilesetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jScrollPane3.setViewportView(jlBuildTilesetList);
            }
            jPanel3.add(jScrollPane3, "cell 0 1");

            //======== panel3 ========
            {
                panel3.setLayout(new GridBagLayout());
                ((GridBagLayout) panel3.getLayout()).columnWidths = new int[]{0, 0};
                ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0};
                ((GridBagLayout) panel3.getLayout()).columnWeights = new double[]{0.0, 1.0E-4};
                ((GridBagLayout) panel3.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- jbOpenBuilTilesetList ----
                jbOpenBuilTilesetList.setText("Open List...");
                jbOpenBuilTilesetList.addActionListener(e -> jbOpenBuilTilesetListActionPerformed(e));
                panel3.add(jbOpenBuilTilesetList, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- jbSaveBuildTilesetList ----
                jbSaveBuildTilesetList.setText("Save List...");
                jbSaveBuildTilesetList.addActionListener(e -> jbSaveBuildTilesetListActionPerformed(e));
                panel3.add(jbSaveBuildTilesetList, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- jbAddBuildingTbl ----
                jbAddBuildingTbl.setText("Add Building...");
                jbAddBuildingTbl.addActionListener(e -> jbAddBuildingTblActionPerformed(e));
                panel3.add(jbAddBuildingTbl, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- jbReplaceBuildingTbl ----
                jbReplaceBuildingTbl.setText("Replace Building...");
                jbReplaceBuildingTbl.addActionListener(e -> jbReplaceBuildingTblActionPerformed(e));
                panel3.add(jbReplaceBuildingTbl, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- jbRemoveBuildingTbl ----
                jbRemoveBuildingTbl.setText("Remove Building...");
                jbRemoveBuildingTbl.addActionListener(e -> jbRemoveBuildingTblActionPerformed(e));
                panel3.add(jbRemoveBuildingTbl, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            jPanel3.add(panel3, "cell 1 1");
        }
        contentPane.add(jPanel3);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private JList<String> jlBuildingIDs;
    private JScrollPane jScrollPane2;
    private JList<String> jlMaterialOrder;
    private JPanel panel1;
    private JButton jbOpenMatshp;
    private JButton jbSaveMatshp;
    private JButton jbBmmAddBuilding;
    private JButton jbBmmReplaceBuilding;
    private JButton jbBmmRemoveBuilding;
    private JLabel jLabel3;
    private JPanel panel2;
    private JButton jbMoveUp;
    private JButton jbMoveDown;
    private JPanel jPanel3;
    private JLabel jLabel4;
    private JScrollPane jScrollPane3;
    private JList<String> jlBuildTilesetList;
    private JPanel panel3;
    private JButton jbOpenBuilTilesetList;
    private JButton jbSaveBuildTilesetList;
    private JButton jbAddBuildingTbl;
    private JButton jbReplaceBuildingTbl;
    private JButton jbRemoveBuildingTbl;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
