/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.buildingeditor2;

import editor.buildingeditor2.animations.AddBuildAnimationDialog;
import editor.buildingeditor2.animations.BuildAnimInfoHGSS;
import editor.buildingeditor2.animations.ModelAnimation;
import editor.buildingeditor2.areabuild.AddBuildModelDialog;
import editor.buildingeditor2.areabuild.AreaBuild;
import editor.buildingeditor2.areadata.AreaDataHGSS;
import editor.buildingeditor2.buildfile.Build;
import editor.buildingeditor2.buildfile.BuildFile;
import editor.handler.MapEditorHandler;
import editor.nsbtx2.Nsbtx2;
import editor.nsbtx2.NsbtxLoader2;
import editor.nsbtx2.NsbtxPalette;
import editor.nsbtx2.NsbtxTexture;
import editor.nsbtx2.NsbtxWriter;
import java.awt.Color;
import utils.Utils.MutableBoolean;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import nitroreader.nsbca.NSBCA;
import nitroreader.nsbca.NSBCAreader;
import nitroreader.nsbta.NSBTA;
import nitroreader.nsbta.NSBTAreader;
import nitroreader.nsbtp.NSBTP;
import nitroreader.nsbtp.NSBTPreader;
import nitroreader.shared.ByteReader;
import nitroreader.shared.G3Dheader;
import renderer.NitroDisplayGL;
import renderer.ObjectGL;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class BuildingEditorDialogHGSS extends javax.swing.JDialog {

    private MapEditorHandler handler;
    private BuildHandlerHGSS buildHandler;

    private ImageIcon nsbmdIcon;
    private ImageIcon nsbtxIcon;
    private ImageIcon areaDataIcon;
    private ArrayList<ImageIcon> animIcons;
    private ArrayList<Integer> selectedAnimIconIndices;
    private ArrayList<Integer> animIconIndices;
    private ArrayList<Integer> mapAnimIconIndices;

    private MutableBoolean jlBuildModelEnabled = new MutableBoolean(true);
    private MutableBoolean jlMaterialOrderEnabled = new MutableBoolean(true);
    private MutableBoolean jlBuildAnimeListEnabled = new MutableBoolean(true);
    private MutableBoolean jlAreaDataListEnabled = new MutableBoolean(true);
    private MutableBoolean jlBuildTsetListEnabled = new MutableBoolean(true);
    private MutableBoolean jlAreaBuildListEnabled = new MutableBoolean(true);
    private MutableBoolean jlAnimationsListEnabled = new MutableBoolean(true);
    private MutableBoolean jlBuildFileEnabled = new MutableBoolean(true);

    private MutableBoolean jcbAnimationTypeEnabled = new MutableBoolean(true);

    private MutableBoolean jtfBuildTsetEnabled = new MutableBoolean(true);
    private MutableBoolean jtfMapTsetEnabled = new MutableBoolean(true);
    private MutableBoolean jcbAreaTypeEnabled = new MutableBoolean(true);
    private MutableBoolean buildPropertiesEnabled = new MutableBoolean(true);
    private MutableBoolean jcbDynamicTexEnabled = new MutableBoolean(true);
    private MutableBoolean jcbAreaLightEnabled = new MutableBoolean(true);

    private MutableBoolean jcbAnimType1Enabled = new MutableBoolean(true);
    private MutableBoolean jcbAnimType2Enabled = new MutableBoolean(true);
    private MutableBoolean jcbLoopEnabled = new MutableBoolean(true);
    private MutableBoolean jcbUnknown1Enabled = new MutableBoolean(true);
    private MutableBoolean jcbNumAnimsEnabled = new MutableBoolean(true);

    private static final Color redColor = new Color(255, 200, 200);
    private static final Color greenColor = new Color(200, 255, 200);
    private static final Color whiteColor = Color.white;

    /**
     * Creates new form BuildingEditorDialog2
     */
    public BuildingEditorDialogHGSS(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        jTabbedPane1.setIconAt(0, new ImageIcon(getClass().getResource("/icons/BuildingIcon.png")));
        jTabbedPane1.setIconAt(1, new ImageIcon(getClass().getResource("/icons/AreaDataIcon.png")));
        jTabbedPane1.setIconAt(2, new ImageIcon(getClass().getResource("/icons/NsbtxIcon.png")));
        jTabbedPane1.setIconAt(3, new ImageIcon(getClass().getResource("/icons/AnimationIcon.png")));
        jTabbedPane1.setIconAt(4, new ImageIcon(getClass().getResource("/icons/mapIcon.png")));

        nsbmdIcon = new ImageIcon(getClass().getResource("/icons/NsbmdIcon.png"));
        nsbtxIcon = new ImageIcon(getClass().getResource("/icons/NsbtxIcon.png"));
        areaDataIcon = new ImageIcon(getClass().getResource("/icons/AreaDataIcon.png"));

        animIcons = new ArrayList<>(4);
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbcaIcon.png")));
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbtaIcon.png")));
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbtpIcon.png")));
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbmaIcon.png")));

        selectedAnimIconIndices = new ArrayList<>();
        animIconIndices = new ArrayList<>();
        mapAnimIconIndices = new ArrayList<>();

        addIconToJList(jlBuildModel, nsbmdIcon);
        addIconToJList(jlAreaBuildList, nsbmdIcon);
        addIconToJList(jlBuildFile, nsbmdIcon);
        addIconToJList(jlBuildTsetList, nsbtxIcon);
        addIconToJList(jlAreaDataList, areaDataIcon);
        //addAnimationIconsToJList(jlSelectedAnimationsList, selectedAnimIconIndices, animIcons);
        jlSelectedAnimationsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0 && index < selectedAnimIconIndices.size()) {
                    int animIndex = selectedAnimIconIndices.get(index);
                    if (animIndex >= 0 && animIndex < animIcons.size()) {
                        label.setIcon(animIcons.get(animIndex));
                    }
                }
                return label;
            }
        });

        jlAnimationsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0 && index < animIconIndices.size()) {
                    int animIndex = animIconIndices.get(index);
                    if (animIndex >= 0 && animIndex < animIcons.size()) {
                        label.setIcon(animIcons.get(animIndex));
                    }
                }
                return label;
            }
        });

        jlMapAnimationsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0 && index < mapAnimIconIndices.size()) {
                    int animIndex = mapAnimIconIndices.get(index);
                    if (animIndex >= 0 && animIndex < animIcons.size()) {
                        label.setIcon(animIcons.get(animIndex));
                    }
                }
                return label;
            }
        });

        Utils.addListenerToJTextFieldColor(jtfBuildTset, jtfBuildTsetEnabled, new Color(255, 200, 200));
        Utils.addListenerToJTextFieldColor(jtfMapTset, jtfMapTsetEnabled, new Color(255, 200, 200));

        nitroDisplayGL.getObjectsGL().add(new ObjectGL());
        nitroDisplayAreaData.getObjectsGL().add(new ObjectGL());
        nitroDisplayMap.getObjectsGL().add(new ObjectGL());//Add space for Map's NSBMD
        nitroDisplayMapAnims.getObjectsGL().add(new ObjectGL());//Add space for Map's NSBMD

        jcbAnimType1.setModel(new DefaultComboBoxModel(BuildAnimInfoHGSS.namesAnimType1.values().toArray()));
        jcbAnimType2.setModel(new DefaultComboBoxModel(BuildAnimInfoHGSS.namesAnimType2.values().toArray()));
        jcbLoopType.setModel(new DefaultComboBoxModel(BuildAnimInfoHGSS.namesLoopType.values().toArray()));
        jcbUnknown1.setModel(new DefaultComboBoxModel(BuildAnimInfoHGSS.namesDoorSound.values().toArray()));
        jcbNumAnims.setModel(new DefaultComboBoxModel(BuildAnimInfoHGSS.namesNumAnims.values().toArray()));

        //jcbDynamicTex.setModel(new DefaultComboBoxModel(AreaDataHGSS.namesDynamicTexType.values().toArray()));
        //updateModelJcbMapAnimations();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlBuildModel = new javax.swing.JList<>();
        jbAddBuilding = new javax.swing.JButton();
        jbExportBuilding = new javax.swing.JButton();
        jbReplaceBuilding = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jbRemoveBuilding = new javax.swing.JButton();
        jbFindBuilding = new javax.swing.JButton();
        nitroDisplayGL = new renderer.NitroDisplayGL();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlMaterialOrder = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jbRemoveMaterial = new javax.swing.JButton();
        jbAddMaterial = new javax.swing.JButton();
        jbMoveMaterialUp = new javax.swing.JButton();
        jbMoveMaterialDown = new javax.swing.JButton();
        jbImportMaterialsFromNsbmd = new javax.swing.JButton();
        jbSetAnimation = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jlSelectedAnimationsList = new javax.swing.JList<>();
        jbAddAnimToBuild = new javax.swing.JButton();
        jbReplaceAnimToBuild = new javax.swing.JButton();
        jbRemoveAnimToBuild = new javax.swing.JButton();
        jcbAnimType1 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jbPlay = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jcbLoopType = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jcbUnknown1 = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jcbNumAnims = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jcbAnimType2 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jlAreaDataList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jbAddAreaData = new javax.swing.JButton();
        jbRemoveAreaData = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jtfMapTset = new javax.swing.JTextField();
        jtfBuildTset = new javax.swing.JTextField();
        jbApplyBuildTset = new javax.swing.JButton();
        jbApplyMapTset = new javax.swing.JButton();
        jcbAreaType = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jcbDynamicTex = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jcbAreaLight = new javax.swing.JComboBox<>();
        jPanel20 = new javax.swing.JPanel();
        nitroDisplayMapAnims = new renderer.NitroDisplayGL();
        jbOpenMap1 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jlMapAnimationsList = new javax.swing.JList<>();
        jbAddMapAnim = new javax.swing.JButton();
        jbReplaceMapAnim = new javax.swing.JButton();
        jbExportMapAnim = new javax.swing.JButton();
        jbRemoveMapAnim = new javax.swing.JButton();
        jbPlayMapAnimation = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jlBuildTsetList = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();
        jbAddTset = new javax.swing.JButton();
        jbReplaceTset = new javax.swing.JButton();
        jbRemoveTset = new javax.swing.JButton();
        jbExportTileset = new javax.swing.JButton();
        nsbtxPanel = new editor.nsbtx2.NsbtxPanel();
        jbAddEmptyTileset = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jlAreaBuildList = new javax.swing.JList<>();
        jbRemoveBuildToTset = new javax.swing.JButton();
        jbReplaceBuildToTset = new javax.swing.JButton();
        jbAddBuildToTset = new javax.swing.JButton();
        nitroDisplayAreaData = new renderer.NitroDisplayGL();
        jbAddTexToNsbtx = new javax.swing.JButton();
        jbRemoveTextures = new javax.swing.JButton();
        jbRemoveAllUnusedTexPals = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jlAnimationsList = new javax.swing.JList<>();
        jbAddAnim = new javax.swing.JButton();
        jbReplaceAnim = new javax.swing.JButton();
        jbRemoveAnim = new javax.swing.JButton();
        jbExportAnimation = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        nitroDisplayMap = new renderer.NitroDisplayGL();
        jbOpenMap = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jlBuildFile = new javax.swing.JList<>();
        jPanel16 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jsBuildID = new javax.swing.JSpinner();
        jbChooseModelBld = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jsBuildX = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        jsBuildY = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        jsBuildZ = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        jsBuildScaleX = new javax.swing.JSpinner();
        jsBuildScaleY = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jsBuildScaleZ = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jbImportBld = new javax.swing.JButton();
        jbExportBld = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jbAddBuildBld = new javax.swing.JButton();
        jbRemoveBld = new javax.swing.JButton();
        jbCancel = new javax.swing.JButton();
        jbSaveAll = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jcbModelsSelected = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Building Editor (Experimental)");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Building Selector (build_model.narc)"));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlBuildModel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlBuildModel.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlBuildModelValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jlBuildModel);

        jbAddBuilding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddBuilding.setText("Add Building");
        jbAddBuilding.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddBuildingActionPerformed(evt);
            }
        });

        jbExportBuilding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jbExportBuilding.setText("Export Building");
        jbExportBuilding.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbExportBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportBuildingActionPerformed(evt);
            }
        });

        jbReplaceBuilding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ReplaceIcon.png"))); // NOI18N
        jbReplaceBuilding.setText("Replace Building");
        jbReplaceBuilding.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbReplaceBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReplaceBuildingActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/BuildingIcon.png"))); // NOI18N
        jLabel4.setText("Building List:");
        jLabel4.setToolTipText("");

        jbRemoveBuilding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveBuilding.setText("Remove Building");
        jbRemoveBuilding.setEnabled(false);
        jbRemoveBuilding.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveBuildingActionPerformed(evt);
            }
        });

        jbFindBuilding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/SearchIcon.png"))); // NOI18N
        jbFindBuilding.setText("Find Usages");
        jbFindBuilding.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbFindBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFindBuildingActionPerformed(evt);
            }
        });

        nitroDisplayGL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        javax.swing.GroupLayout nitroDisplayGLLayout = new javax.swing.GroupLayout(nitroDisplayGL);
        nitroDisplayGL.setLayout(nitroDisplayGLLayout);
        nitroDisplayGLLayout.setHorizontalGroup(
            nitroDisplayGLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );
        nitroDisplayGLLayout.setVerticalGroup(
            nitroDisplayGLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nitroDisplayGL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbReplaceBuilding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddBuilding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbExportBuilding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveBuilding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbFindBuilding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel4))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(nitroDisplayGL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jbAddBuilding)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbReplaceBuilding)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbExportBuilding)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbRemoveBuilding)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbFindBuilding)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addContainerGap())))))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Building Properties (build_model_matshp.dat)"));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlMaterialOrder.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jlMaterialOrder);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/MaterialIcon2.png"))); // NOI18N
        jLabel2.setText("Material order:");
        jLabel2.setToolTipText("");

        jbRemoveMaterial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveMaterial.setText("Remove Material");
        jbRemoveMaterial.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveMaterialActionPerformed(evt);
            }
        });

        jbAddMaterial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddMaterial.setText("Add Material");
        jbAddMaterial.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddMaterialActionPerformed(evt);
            }
        });

        jbMoveMaterialUp.setText("▲");
        jbMoveMaterialUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMaterialUpActionPerformed(evt);
            }
        });

        jbMoveMaterialDown.setText("▼");
        jbMoveMaterialDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMaterialDownActionPerformed(evt);
            }
        });

        jbImportMaterialsFromNsbmd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ImportTileIcon.png"))); // NOI18N
        jbImportMaterialsFromNsbmd.setText("Import from NSBMD");
        jbImportMaterialsFromNsbmd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbImportMaterialsFromNsbmdActionPerformed(evt);
            }
        });

        jbSetAnimation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AnimationIcon.png"))); // NOI18N
        jbSetAnimation.setText("Set Animation");
        jbSetAnimation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbSetAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSetAnimationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jbMoveMaterialUp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbMoveMaterialDown))
                            .addComponent(jbAddMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbRemoveMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jbSetAnimation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbImportMaterialsFromNsbmd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jLabel2))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jbAddMaterial)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveMaterial)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbImportMaterialsFromNsbmd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbSetAnimation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbMoveMaterialUp)
                            .addComponent(jbMoveMaterialDown))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Building Animations (bm_anime_list.narc)"));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AnimationIcon.png"))); // NOI18N
        jLabel3.setText("Animations:");
        jLabel3.setToolTipText("");

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlSelectedAnimationsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(jlSelectedAnimationsList);

        jbAddAnimToBuild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddAnimToBuild.setText("Add Animation");
        jbAddAnimToBuild.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddAnimToBuild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddAnimToBuildActionPerformed(evt);
            }
        });

        jbReplaceAnimToBuild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ReplaceIcon.png"))); // NOI18N
        jbReplaceAnimToBuild.setText("Replace Animation");
        jbReplaceAnimToBuild.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbReplaceAnimToBuild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReplaceAnimToBuildActionPerformed(evt);
            }
        });

        jbRemoveAnimToBuild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveAnimToBuild.setText("Remove Animation");
        jbRemoveAnimToBuild.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveAnimToBuild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveAnimToBuildActionPerformed(evt);
            }
        });

        jcbAnimType1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No animation", "Loop", "Trigger (?)", "Trigger", "Day/Night Cycle" }));
        jcbAnimType1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAnimType1ActionPerformed(evt);
            }
        });

        jLabel12.setText("Type 1:");

        jbPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AnimationIcon.png"))); // NOI18N
        jbPlay.setText("Play Animation");
        jbPlay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbPlayActionPerformed(evt);
            }
        });

        jLabel22.setText("Loop type:");

        jcbLoopType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Loop", "Trigger" }));
        jcbLoopType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbLoopTypeActionPerformed(evt);
            }
        });

        jLabel23.setText("Sound:");

        jcbUnknown1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3" }));
        jcbUnknown1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbUnknown1ActionPerformed(evt);
            }
        });

        jLabel24.setText("#Anims (?):");

        jcbNumAnims.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4" }));
        jcbNumAnims.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbNumAnimsActionPerformed(evt);
            }
        });

        jLabel25.setText("Type 2:");

        jcbAnimType2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No animation", "Loop", "Trigger" }));
        jcbAnimType2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAnimType2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jcbAnimType2, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jcbAnimType1, javax.swing.GroupLayout.Alignment.LEADING, 0, 124, Short.MAX_VALUE)
                                    .addComponent(jcbUnknown1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbRemoveAnimToBuild, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                            .addComponent(jbReplaceAnimToBuild, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddAnimToBuild, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbPlay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jcbLoopType, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jcbNumAnims, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(21, 21, 21))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jbAddAnimToBuild)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbReplaceAnimToBuild)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveAnimToBuild)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbPlay))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jcbAnimType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jcbLoopType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24)
                        .addComponent(jcbNumAnims, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25)
                        .addComponent(jcbAnimType2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jcbUnknown1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Building Editor", jPanel3);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Area Data Selector (area_data.narc)"));

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlAreaDataList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlAreaDataList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlAreaDataListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jlAreaDataList);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AreaDataIcon.png"))); // NOI18N
        jLabel1.setText("Area Data List:");

        jbAddAreaData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddAreaData.setText("Add Area Data");
        jbAddAreaData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddAreaData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddAreaDataActionPerformed(evt);
            }
        });

        jbRemoveAreaData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveAreaData.setText("Remove Area Data");
        jbRemoveAreaData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveAreaData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveAreaDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbAddAreaData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveAreaData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jbAddAreaData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveAreaData)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Area Data Properties"));

        jLabel5.setText("Building Tileset:");

        jLabel6.setText("Map Tileset:");

        jLabel8.setText("Area Type:");

        jbApplyBuildTset.setText("Apply");
        jbApplyBuildTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbApplyBuildTsetActionPerformed(evt);
            }
        });

        jbApplyMapTset.setText("Apply");
        jbApplyMapTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbApplyMapTsetActionPerformed(evt);
            }
        });

        jcbAreaType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Indoor Area", "Outdoor Area" }));
        jcbAreaType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAreaTypeActionPerformed(evt);
            }
        });

        jLabel27.setText("Map Animations:");

        jcbDynamicTex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbDynamicTexActionPerformed(evt);
            }
        });

        jLabel28.setText("Light Type:");

        jcbAreaLight.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Model's Light", "Day/Night Light", "Unknown Light" }));
        jcbAreaLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAreaLightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel28)
                    .addComponent(jLabel27))
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtfBuildTset, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(jtfMapTset))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbApplyBuildTset)
                            .addComponent(jbApplyMapTset)))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jcbAreaLight, javax.swing.GroupLayout.Alignment.LEADING, 0, 107, Short.MAX_VALUE)
                        .addComponent(jcbDynamicTex, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jcbAreaType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtfBuildTset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbApplyBuildTset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jtfMapTset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbApplyMapTset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jcbDynamicTex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jcbAreaType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jcbAreaLight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Map Animations Display"));

        nitroDisplayMapAnims.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        javax.swing.GroupLayout nitroDisplayMapAnimsLayout = new javax.swing.GroupLayout(nitroDisplayMapAnims);
        nitroDisplayMapAnims.setLayout(nitroDisplayMapAnimsLayout);
        nitroDisplayMapAnimsLayout.setHorizontalGroup(
            nitroDisplayMapAnimsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        nitroDisplayMapAnimsLayout.setVerticalGroup(
            nitroDisplayMapAnimsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jbOpenMap1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ImportTileIcon.png"))); // NOI18N
        jbOpenMap1.setText("Open Map");
        jbOpenMap1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOpenMap1ActionPerformed(evt);
            }
        });

        jLabel29.setText("*[Note: This map is used as a visual help for viewing the map animations]");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nitroDisplayMapAnims, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbOpenMap1)
                            .addComponent(jLabel29))
                        .addGap(0, 28, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbOpenMap1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nitroDisplayMapAnims, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Map Animations (Dynamic Textures)"));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AnimationIcon.png"))); // NOI18N
        jLabel7.setText("Map Animations List:");

        jScrollPane9.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane9.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlMapAnimationsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane9.setViewportView(jlMapAnimationsList);

        jbAddMapAnim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddMapAnim.setText("Add Animation");
        jbAddMapAnim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddMapAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddMapAnimActionPerformed(evt);
            }
        });

        jbReplaceMapAnim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ReplaceIcon.png"))); // NOI18N
        jbReplaceMapAnim.setText("Replace Animation");
        jbReplaceMapAnim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbReplaceMapAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReplaceMapAnimActionPerformed(evt);
            }
        });

        jbExportMapAnim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jbExportMapAnim.setText("Export Animation");
        jbExportMapAnim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbExportMapAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportMapAnimActionPerformed(evt);
            }
        });

        jbRemoveMapAnim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveMapAnim.setText("Remove Animation");
        jbRemoveMapAnim.setEnabled(false);
        jbRemoveMapAnim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveMapAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveMapAnimActionPerformed(evt);
            }
        });

        jbPlayMapAnimation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AnimationIcon.png"))); // NOI18N
        jbPlayMapAnimation.setText("Play Animation");
        jbPlayMapAnimation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbPlayMapAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbPlayMapAnimationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbAddMapAnim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveMapAnim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbReplaceMapAnim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbExportMapAnim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbPlayMapAnimation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jbAddMapAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbReplaceMapAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbExportMapAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveMapAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbPlayMapAnimation)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Area Data Editor", jPanel4);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Building Tileset Selector (areabm_texset.narc)"));

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlBuildTsetList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlBuildTsetList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlBuildTsetListValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(jlBuildTsetList);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/MaterialIcon.png"))); // NOI18N
        jLabel10.setText("Building Tileset List:");

        jbAddTset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddTset.setText("Add Tileset");
        jbAddTset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddTsetActionPerformed(evt);
            }
        });

        jbReplaceTset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ReplaceIcon.png"))); // NOI18N
        jbReplaceTset.setText("Replace Tileset");
        jbReplaceTset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbReplaceTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReplaceTsetActionPerformed(evt);
            }
        });

        jbRemoveTset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveTset.setText("Remove Tileset");
        jbRemoveTset.setEnabled(false);
        jbRemoveTset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveTsetActionPerformed(evt);
            }
        });

        jbExportTileset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jbExportTileset.setText("Export Tileset");
        jbExportTileset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbExportTileset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportTilesetActionPerformed(evt);
            }
        });

        jbAddEmptyTileset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddEmptyTileset.setText("Add Empty Tileset");
        jbAddEmptyTileset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddEmptyTileset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddEmptyTilesetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nsbtxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jbExportTileset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbReplaceTset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddTset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddEmptyTileset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveTset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(nsbtxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 27, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jbAddTset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbAddEmptyTileset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbReplaceTset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbExportTileset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbRemoveTset)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Building Tileset Properties (area_build.narc)"));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/BuildingIcon.png"))); // NOI18N
        jLabel9.setText("Buildings used by the Tileset:");

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlAreaBuildList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlAreaBuildList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlAreaBuildListValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(jlAreaBuildList);

        jbRemoveBuildToTset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveBuildToTset.setText("Remove Building");
        jbRemoveBuildToTset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveBuildToTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveBuildToTsetActionPerformed(evt);
            }
        });

        jbReplaceBuildToTset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ReplaceIcon.png"))); // NOI18N
        jbReplaceBuildToTset.setText("Replace Building");
        jbReplaceBuildToTset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbReplaceBuildToTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReplaceBuildToTsetActionPerformed(evt);
            }
        });

        jbAddBuildToTset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddBuildToTset.setText("Add Building");
        jbAddBuildToTset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddBuildToTset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddBuildToTsetActionPerformed(evt);
            }
        });

        nitroDisplayAreaData.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        javax.swing.GroupLayout nitroDisplayAreaDataLayout = new javax.swing.GroupLayout(nitroDisplayAreaData);
        nitroDisplayAreaData.setLayout(nitroDisplayAreaDataLayout);
        nitroDisplayAreaDataLayout.setHorizontalGroup(
            nitroDisplayAreaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        nitroDisplayAreaDataLayout.setVerticalGroup(
            nitroDisplayAreaDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        jbAddTexToNsbtx.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddTexToNsbtx.setText("Add Texs & Pals to NSBTX");
        jbAddTexToNsbtx.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddTexToNsbtx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddTexToNsbtxActionPerformed(evt);
            }
        });

        jbRemoveTextures.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveTextures.setText("Remove Tex & Pals from NSBTX");
        jbRemoveTextures.setToolTipText("");
        jbRemoveTextures.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveTextures.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveTexturesActionPerformed(evt);
            }
        });

        jbRemoveAllUnusedTexPals.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveAllUnusedTexPals.setText("Removed All Unused Tex & Pals");
        jbRemoveAllUnusedTexPals.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveAllUnusedTexPals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveAllUnusedTexPalsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jbAddBuildToTset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbReplaceBuildToTset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveBuildToTset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddTexToNsbtx, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveTextures, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nitroDisplayAreaData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveAllUnusedTexPals, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jbAddBuildToTset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbReplaceBuildToTset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveBuildToTset)
                        .addGap(18, 18, 18)
                        .addComponent(jbAddTexToNsbtx)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveTextures)
                        .addGap(18, 18, 18)
                        .addComponent(jbRemoveAllUnusedTexPals)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nitroDisplayAreaData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Building Tileset Editor", jPanel7);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Building Animations (bm_anime.narc)"));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AnimationIcon.png"))); // NOI18N
        jLabel11.setText("Animation List:");
        jLabel11.setToolTipText("");

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlAnimationsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(jlAnimationsList);

        jbAddAnim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddAnim.setText("Add Animation");
        jbAddAnim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbAddAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddAnimActionPerformed(evt);
            }
        });

        jbReplaceAnim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ReplaceIcon.png"))); // NOI18N
        jbReplaceAnim.setText("Replace Animation");
        jbReplaceAnim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbReplaceAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReplaceAnimActionPerformed(evt);
            }
        });

        jbRemoveAnim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveAnim.setText("Remove Animation");
        jbRemoveAnim.setEnabled(false);
        jbRemoveAnim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbRemoveAnim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveAnimActionPerformed(evt);
            }
        });

        jbExportAnimation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jbExportAnimation.setText("Export Animation");
        jbExportAnimation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbExportAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportAnimationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbAddAnim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveAnim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbReplaceAnim)
                            .addComponent(jbExportAnimation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jbAddAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbReplaceAnim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbExportAnimation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveAnim)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(731, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Building Animation Editor", jPanel9);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Map Display"));

        nitroDisplayMap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        javax.swing.GroupLayout nitroDisplayMapLayout = new javax.swing.GroupLayout(nitroDisplayMap);
        nitroDisplayMap.setLayout(nitroDisplayMapLayout);
        nitroDisplayMapLayout.setHorizontalGroup(
            nitroDisplayMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        nitroDisplayMapLayout.setVerticalGroup(
            nitroDisplayMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jbOpenMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ImportTileIcon.png"))); // NOI18N
        jbOpenMap.setText("Open Map");
        jbOpenMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOpenMapActionPerformed(evt);
            }
        });

        jLabel26.setText("*[Note: This map is used as a visual help for placing the buildings easily]");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nitroDisplayMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jbOpenMap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26)
                        .addGap(0, 90, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbOpenMap)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nitroDisplayMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Building Editor (*.bld)"));

        jScrollPane8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane8.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jlBuildFile.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlBuildFile.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlBuildFileValueChanged(evt);
            }
        });
        jScrollPane8.setViewportView(jlBuildFile);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Building"));

        jLabel13.setText("Building ID:");

        jsBuildID.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        jsBuildID.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBuildIDStateChanged(evt);
            }
        });

        jbChooseModelBld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ReplaceIcon.png"))); // NOI18N
        jbChooseModelBld.setText("Change Model");
        jbChooseModelBld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbChooseModelBldActionPerformed(evt);
            }
        });

        jLabel14.setForeground(new java.awt.Color(204, 0, 0));
        jLabel14.setText("X: ");

        jsBuildX.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-16.0f), Float.valueOf(15.0f), Float.valueOf(1.0f)));
        jsBuildX.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBuildXStateChanged(evt);
            }
        });

        jLabel15.setForeground(new java.awt.Color(51, 153, 0));
        jLabel15.setText("Y: ");

        jsBuildY.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-16.0f), Float.valueOf(15.0f), Float.valueOf(1.0f)));
        jsBuildY.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBuildYStateChanged(evt);
            }
        });

        jLabel16.setForeground(new java.awt.Color(0, 0, 204));
        jLabel16.setText("Z: ");

        jsBuildZ.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-16.0f), Float.valueOf(15.0f), Float.valueOf(1.0f)));
        jsBuildZ.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBuildZStateChanged(evt);
            }
        });

        jLabel17.setForeground(new java.awt.Color(204, 0, 0));
        jLabel17.setText("Scale X: ");

        jsBuildScaleX.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(15.0f), Float.valueOf(1.0f)));
        jsBuildScaleX.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBuildScaleXStateChanged(evt);
            }
        });

        jsBuildScaleY.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(15.0f), Float.valueOf(1.0f)));
        jsBuildScaleY.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBuildScaleYStateChanged(evt);
            }
        });

        jLabel18.setForeground(new java.awt.Color(0, 153, 0));
        jLabel18.setText("Scale Y: ");

        jLabel19.setForeground(new java.awt.Color(0, 0, 204));
        jLabel19.setText("Scale Z: ");

        jsBuildScaleZ.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(15.0f), Float.valueOf(1.0f)));
        jsBuildScaleZ.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBuildScaleZStateChanged(evt);
            }
        });

        jLabel20.setText("*[Note: axis are rotated compared to map editor's axis]");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jsBuildID, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbChooseModelBld))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jsBuildZ))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jsBuildY))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jsBuildX, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jsBuildScaleZ))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jsBuildScaleY))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jsBuildScaleX, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel20))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jsBuildID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbChooseModelBld))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jsBuildScaleX, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(jsBuildX, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(jsBuildScaleY, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(jsBuildY, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(jsBuildScaleZ, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jsBuildZ, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Building File"));

        jPanel18.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        jbImportBld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ImportTileIcon.png"))); // NOI18N
        jbImportBld.setText("Import BLD File");
        jbImportBld.setToolTipText("");
        jbImportBld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbImportBldActionPerformed(evt);
            }
        });
        jPanel18.add(jbImportBld);

        jbExportBld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jbExportBld.setText("Export BLD File");
        jbExportBld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportBldActionPerformed(evt);
            }
        });
        jPanel18.add(jbExportBld);

        jPanel19.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        jbAddBuildBld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddIcon.png"))); // NOI18N
        jbAddBuildBld.setText("Add Building");
        jbAddBuildBld.setToolTipText("");
        jbAddBuildBld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddBuildBldActionPerformed(evt);
            }
        });
        jPanel19.add(jbAddBuildBld);

        jbRemoveBld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jbRemoveBld.setText("Remove Building");
        jbRemoveBld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveBldActionPerformed(evt);
            }
        });
        jPanel19.add(jbRemoveBld);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Map Buildings Editor", jPanel13);

        jbCancel.setText("Close");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jbSaveAll.setText("Save All");
        jbSaveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSaveAllActionPerformed(evt);
            }
        });

        jLabel21.setText("Models Selected:");

        jcbModelsSelected.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Outdoor Models", "Indoor Models" }));
        jcbModelsSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbModelsSelectedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcbModelsSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbSaveAll, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21)
                        .addComponent(jcbModelsSelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbCancel)
                        .addComponent(jbSaveAll)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jlBuildModelValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlBuildModelValueChanged
        if (jlBuildModelEnabled.value) {
            updateViewMaterialOrderList(0);
            updateViewSelectedBuildAnimationsList(0);

            loadCurrentNsbmd();
            nitroDisplayGL.fitCameraToModel(0);
            nitroDisplayGL.requestUpdate();
        }
    }//GEN-LAST:event_jlBuildModelValueChanged

    private void jlAreaDataListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlAreaDataListValueChanged
        if (jlAreaDataListEnabled.value) {
            updateViewAreaDataProperties();
        }

    }//GEN-LAST:event_jlAreaDataListValueChanged

    private void jlBuildTsetListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlBuildTsetListValueChanged
        if (jlBuildTsetListEnabled.value) {
            updateViewAreaBuildList(0);

            try {
                byte[] data = buildHandler.getBuildTilesetList().getTilesets().get(jlBuildTsetList.getSelectedIndex()).getData();
                Nsbtx2 nsbtx = NsbtxLoader2.loadNsbtx(data);
                nsbtxPanel.setNsbtx(nsbtx);
                nsbtxPanel.updateViewTextureNameList(0);
                nsbtxPanel.updateViewPaletteNameList(0);
                nsbtxPanel.updateView();
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_jlBuildTsetListValueChanged

    private void jbAddBuildingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddBuildingActionPerformed
        if (buildHandler.getBuildModelList() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Add a new NSBMD Building Model");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    buildHandler.addBuilding(fc.getSelectedFile().getPath());

                    updateViewBuildModelList(buildHandler.getBuildModelList().getSize() - 1);
                    updateViewSelectedBuildAnimationsList(0);
                    updateViewMaterialOrderList(0);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem reading the NSBMD file",
                            "Error opening NSBMD file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbAddBuildingActionPerformed

    private void jbReplaceBuildingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReplaceBuildingActionPerformed
        if (buildHandler.getBuildModelList() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Select the new NSBMD Building Model (material order will be deleted)");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    buildHandler.replaceBuilding(jlBuildModel.getSelectedIndex(), fc.getSelectedFile().getPath());

                    updateViewBuildModelList(jlBuildModel.getSelectedIndex());
                    updateViewSelectedBuildAnimationsList(0);
                    updateViewMaterialOrderList(0);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem reading the NSBMD file",
                            "Error opening NSBMD file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbReplaceBuildingActionPerformed

    private void jbExportBuildingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportBuildingActionPerformed
        if (buildHandler.getBuildModelList() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save the NSBMD Building Model");
            try {//TODO: Replace this with some index bounds cheking?
                String fileName = buildHandler.getBuildModelList().getModelsName().get(jlBuildModel.getSelectedIndex());
                fc.setSelectedFile(new File(fileName + ".nsbmd"));
            } catch (Exception ex) {

            }
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    if (!fc.getSelectedFile().getPath().isEmpty()) {
                        buildHandler.saveBuilding(jlBuildModel.getSelectedIndex(), fc.getSelectedFile().getPath());
                    } else {
                        JOptionPane.showMessageDialog(this, "The entered name must be valid",
                                "Enter a valid name", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem writing the NSBMD file",
                            "Error writing NSBMD file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbExportBuildingActionPerformed

    private void jbRemoveBuildingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveBuildingActionPerformed
        if (buildHandler.getBuildModelList() != null) {
            ArrayList<Integer> occurrences = buildHandler.getAreaBuildList().getBuildingOccurrences(jlBuildModel.getSelectedIndex());
            if (!occurrences.isEmpty()) {
                String occurenceIDs = "";
                for (int i = 0; i < occurrences.size() - 1; i++) {
                    occurenceIDs += String.valueOf(occurrences.get(i)) + ", ";
                }
                occurenceIDs += String.valueOf(occurrences.get(occurrences.size() - 1));

                int returnVal2 = JOptionPane.showConfirmDialog(this.getContentPane(),
                        "This building is being used in the following tilesets: \n" + occurenceIDs + "\n"
                        + "Do you want to remove it and its occurences? \n"
                        + "(Removing buildings is NOT RECOMMENDED because the building IDs will be shifted)",
                        "Confirm remove building and occurrences", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (returnVal2 == JOptionPane.YES_OPTION) {
                    buildHandler.getAreaBuildList().removeBuildingOccurences(jlBuildModel.getSelectedIndex());

                    buildHandler.removeBuilding(jlBuildModel.getSelectedIndex());
                    updateViewBuildModelList(jlBuildModel.getSelectedIndex());
                    updateViewSelectedBuildAnimationsList(0);
                    updateViewMaterialOrderList(0);
                    updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
                }
            } else {
                int returnVal = JOptionPane.showConfirmDialog(this.getContentPane(),
                        "Are you sure you want to remove the selected building?\n"
                        + "(Removing buildings is NOT RECOMMENDED because the building IDs will be shifted)",
                        "Confirm remove building", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (returnVal == JOptionPane.YES_OPTION) {
                    buildHandler.removeBuilding(jlBuildModel.getSelectedIndex());
                    updateViewBuildModelList(jlBuildModel.getSelectedIndex());
                    updateViewSelectedBuildAnimationsList(0);
                    updateViewMaterialOrderList(0);
                    updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
                }
            }
        }
    }//GEN-LAST:event_jbRemoveBuildingActionPerformed

    private void jbAddMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddMaterialActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.addBuildingMaterial(jlBuildModel.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
        }
    }//GEN-LAST:event_jbAddMaterialActionPerformed

    private void jbRemoveMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveMaterialActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.removeBuildingMaterial(jlBuildModel.getSelectedIndex(), jlMaterialOrder.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
        }
    }//GEN-LAST:event_jbRemoveMaterialActionPerformed

    private void jbMoveMaterialUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMaterialUpActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.moveBuildingMaterialUp(jlBuildModel.getSelectedIndex(), jlMaterialOrder.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex() - 1);
        }
    }//GEN-LAST:event_jbMoveMaterialUpActionPerformed

    private void jbMoveMaterialDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMaterialDownActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.moveBuildingMaterialDown(jlBuildModel.getSelectedIndex(), jlMaterialOrder.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex() + 1);
        }
    }//GEN-LAST:event_jbMoveMaterialDownActionPerformed

    private void jbAddAnimToBuildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddAnimToBuildActionPerformed
        if (buildHandler.getBuildModelList() != null
                && buildHandler.getBuildModelAnimeList() != null
                && buildHandler.getBuildModelAnims() != null) {
            ArrayList<Integer> animations = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()).getAnimIDs();
            if (animations.isEmpty() || animations.size() < BuildAnimInfoHGSS.MAX_ANIMS_PER_BUILDING) {
                final AddBuildAnimationDialog dialog = new AddBuildAnimationDialog(handler.getMainFrame(), true);
                dialog.init(buildHandler.getBuildModelList().getModelsData().get(jlBuildModel.getSelectedIndex()),
                        buildHandler.getBuildModelAnims(),
                        buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()).getAnimIDs());
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                if (dialog.getReturnValue() == AddBuildAnimationDialog.ACEPTED) {
                    buildHandler.addBuildingAnimation(jlBuildModel.getSelectedIndex(), dialog.getIndexSelected());
                    updateViewSelectedBuildAnimationsList(jlSelectedAnimationsList.getSelectedIndex() + 1);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Buildings can't have more than "
                        + String.valueOf(BuildAnimInfoHGSS.MAX_ANIMS_PER_BUILDING) + " animations.",
                        "Can't add animation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbAddAnimToBuildActionPerformed

    private void jbReplaceAnimToBuildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReplaceAnimToBuildActionPerformed
        if (buildHandler.getBuildModelList() != null
                && buildHandler.getBuildModelAnimeList() != null
                && buildHandler.getBuildModelAnims() != null) {
            if (buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()).getAnimIDs().size() > 0) {
                final AddBuildAnimationDialog dialog = new AddBuildAnimationDialog(handler.getMainFrame(), true);
                dialog.init(buildHandler.getBuildModelList().getModelsData().get(jlBuildModel.getSelectedIndex()),
                        buildHandler.getBuildModelAnims(),
                        buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()).getAnimIDs());
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                if (dialog.getReturnValue() == AddBuildAnimationDialog.ACEPTED) {
                    buildHandler.replaceBuildingAnimation(jlBuildModel.getSelectedIndex(), dialog.getIndexSelected(), jlSelectedAnimationsList.getSelectedIndex());
                    updateViewSelectedBuildAnimationsList(jlSelectedAnimationsList.getSelectedIndex());
                }
            }
        }
    }//GEN-LAST:event_jbReplaceAnimToBuildActionPerformed

    private void jbRemoveAnimToBuildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveAnimToBuildActionPerformed
        if (buildHandler.getBuildModelList() != null
                && buildHandler.getBuildModelAnimeList() != null
                && buildHandler.getBuildModelAnims() != null) {
            buildHandler.removeBuildingAnimation(jlBuildModel.getSelectedIndex(), jlSelectedAnimationsList.getSelectedIndex());
            updateViewSelectedBuildAnimationsList(jlSelectedAnimationsList.getSelectedIndex() - 1);
        }
    }//GEN-LAST:event_jbRemoveAnimToBuildActionPerformed

    private void jbAddBuildToTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddBuildToTsetActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getAreaBuildList() != null && buildHandler.getBuildTilesetList() != null) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame(), true);
            dialog.init(buildHandler.getBuildModelList(), buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACEPTED) {
                buildHandler.addBuildToAreaBuild(jlBuildTsetList.getSelectedIndex(), dialog.getIndexSelected());
                updateViewAreaBuildList(jlAreaBuildList.getModel().getSize());
            }
        }
    }//GEN-LAST:event_jbAddBuildToTsetActionPerformed

    private void jbReplaceBuildToTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReplaceBuildToTsetActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getAreaBuildList() != null && buildHandler.getBuildTilesetList() != null) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame(), true);
            dialog.init(buildHandler.getBuildModelList(), buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACEPTED) {
                buildHandler.replaceBuildToAreaBuild(jlBuildTsetList.getSelectedIndex(), dialog.getIndexSelected(), jlAreaBuildList.getSelectedIndex());
                updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_jbReplaceBuildToTsetActionPerformed

    private void jbRemoveBuildToTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveBuildToTsetActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getAreaBuildList() != null && buildHandler.getBuildTilesetList() != null) {
            buildHandler.removeBuildToAreaBuild(jlBuildTsetList.getSelectedIndex(), jlAreaBuildList.getSelectedIndex());
            updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
        }
    }//GEN-LAST:event_jbRemoveBuildToTsetActionPerformed

    private void jbAddAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddAnimActionPerformed
        if (buildHandler.getBuildModelAnims() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Animation Files (*.nsbca, *.nsbta, *.nsbtp, *.nsbma)", "nsbca", "nsbta", "nsbtp", "nsbma"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Add a new Animation");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    buildHandler.addAnimationFile(fc.getSelectedFile().getPath());

                    updateViewAnimationsList(jlAnimationsList.getModel().getSize());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem reading the animation file",
                            "Error opening animation file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbAddAnimActionPerformed

    private void jbReplaceAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReplaceAnimActionPerformed
        if (buildHandler.getBuildModelAnims() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Animation Files (*.nsbca, *.nsbta, *.nsbtp, *.nsbma)", "nsbca", "nsbta", "nsbtp", "nsbma"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Select the new Animation");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    buildHandler.replaceAnimationFile(jlAnimationsList.getSelectedIndex(), fc.getSelectedFile().getPath());

                    updateViewAnimationsList(jlAnimationsList.getSelectedIndex());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem reading the animation file",
                            "Error opening animation file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbReplaceAnimActionPerformed

    private void jbExportAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportAnimationActionPerformed
        if (buildHandler.getBuildModelAnims() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            String type = buildHandler.getBuildModelAnims().getAnimations().get(jlAnimationsList.getSelectedIndex()).getExtensionName();

            fc.setFileFilter(new FileNameExtensionFilter(type.toUpperCase() + " (*." + type + ")", type));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save the Animation");
            try {//TODO: Replace this with some index bounds cheking?
                String fileName = buildHandler.getBuildModelAnims().getAnimations().get(jlAnimationsList.getSelectedIndex()).getName();
                fc.setSelectedFile(new File(fileName + "." + type));
            } catch (Exception ex) {

            }
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    if (!fc.getSelectedFile().getPath().isEmpty()) {
                        buildHandler.saveAnimationFile(jlAnimationsList.getSelectedIndex(), fc.getSelectedFile().getPath());
                    } else {
                        JOptionPane.showMessageDialog(this, "The entered name must be valid",
                                "Enter a valid name", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem writing the Animation file",
                            "Error writing Animation file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbExportAnimationActionPerformed

    private void jbRemoveAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveAnimActionPerformed

    }//GEN-LAST:event_jbRemoveAnimActionPerformed

    private void jbSaveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSaveAllActionPerformed
        if (buildHandler.areAllFilesLoaded()) {
            try {
                buildHandler.saveAllFiles();

                JOptionPane.showMessageDialog(this,
                        "All files were succesfully saved.\n"
                        + "Open the ROM with SDSME or similar and save it to apply the changes.",
                        "Files succesfully saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "There was a problem saving the files.",
                        "Problem saving files", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(BuildingEditorDialogHGSS.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }//GEN-LAST:event_jbSaveAllActionPerformed

    private void jbFindBuildingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFindBuildingActionPerformed
        if (buildHandler.getBuildModelList() != null) {
            ArrayList<Integer> occurrences = buildHandler.getAreaBuildList().getBuildingOccurrences(jlBuildModel.getSelectedIndex());
            if (!occurrences.isEmpty()) {
                String occurenceIDs = "";
                for (int i = 0; i < occurrences.size() - 1; i++) {
                    occurenceIDs += String.valueOf(occurrences.get(i)) + ", ";
                }
                occurenceIDs += String.valueOf(occurrences.get(occurrences.size() - 1));

                JOptionPane.showMessageDialog(this,
                        "This building is being used in the following building tilesets: \n" + occurenceIDs,
                        String.valueOf(occurrences.size()) + " occurrences found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "This building is not included in any building tileset.",
                        "No occurrences found", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbFindBuildingActionPerformed

    private void jcbAnimType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAnimType1ActionPerformed
        if (jcbAnimType1Enabled.value) {
            BuildAnimInfoHGSS info = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
            info.setAnimType1(BuildAnimInfoHGSS.namesAnimType1Swap.get(jcbAnimType1.getSelectedItem()));
        }
    }//GEN-LAST:event_jcbAnimType1ActionPerformed

    private void jbAddAreaDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddAreaDataActionPerformed
        if (buildHandler.getAreaDataList() != null) {
            buildHandler.getAreaDataList().getAreaDatas().add(new AreaDataHGSS());
            updateViewAreaDataList(buildHandler.getAreaDataList().getAreaDatas().size());
        }
    }//GEN-LAST:event_jbAddAreaDataActionPerformed

    private void jbRemoveAreaDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveAreaDataActionPerformed
        if (buildHandler.getAreaDataList() != null) {
            int returnVal = JOptionPane.showConfirmDialog(this.getContentPane(),
                    "Removing Area Datas is NOT recommended. \n"
                    + "Do you really want to remove this Area Data?",
                    "Confirm remove Area Data", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (returnVal == JOptionPane.YES_OPTION) {
                buildHandler.getAreaDataList().getAreaDatas().remove(jlAreaDataList.getSelectedIndex());
                updateViewAreaDataList(jlAreaDataList.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_jbRemoveAreaDataActionPerformed

    private void jbApplyBuildTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbApplyBuildTsetActionPerformed
        if (buildHandler.getAreaDataList() != null) {
            AreaDataHGSS areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            try {
                int value = Integer.parseInt(jtfBuildTset.getText());
                areaData.setBuildingTilesetID(value);
            } catch (NumberFormatException ex) {

            }
            jtfBuildTsetEnabled.value = false;
            jtfBuildTset.setText(String.valueOf(areaData.getBuildingTilesetID()));
            jtfBuildTset.setBackground(greenColor);
            jtfBuildTsetEnabled.value = true;
        }
    }//GEN-LAST:event_jbApplyBuildTsetActionPerformed

    private void jbApplyMapTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbApplyMapTsetActionPerformed
        if (buildHandler.getAreaDataList() != null) {
            AreaDataHGSS areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            try {
                int value = Integer.parseInt(jtfMapTset.getText());
                areaData.setMapTilesetID(value);
            } catch (NumberFormatException ex) {

            }
            jtfMapTsetEnabled.value = false;
            jtfMapTset.setText(String.valueOf(areaData.getMapTilesetID()));
            jtfMapTset.setBackground(greenColor);
            jtfMapTsetEnabled.value = true;
        }
    }//GEN-LAST:event_jbApplyMapTsetActionPerformed

    private void jcbAreaTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAreaTypeActionPerformed
        if (buildHandler.getAreaDataList() != null) {
            if (jcbAreaTypeEnabled.value) {
                AreaDataHGSS areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
                areaData.setAreaType(jcbAreaType.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_jcbAreaTypeActionPerformed

    private void jbPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPlayActionPerformed
        if (buildHandler.getBuildModelAnimeList() != null) {
            try {
                ArrayList<Integer> animIDs = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()).getAnimIDs();
                ModelAnimation anim = buildHandler.getBuildModelAnims().getAnimations().get(animIDs.get(jlSelectedAnimationsList.getSelectedIndex()));

                loadAnimationInNitroDisplay(nitroDisplayGL, 0, anim);

            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

    }//GEN-LAST:event_jbPlayActionPerformed

    private void jbImportMaterialsFromNsbmdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbImportMaterialsFromNsbmdActionPerformed
        if (buildHandler.getBuildModelList() != null) {
            try {
                int buildIndex = jlBuildModel.getSelectedIndex();
                byte[] data = buildHandler.getBuildModelList().getModelsData().get(buildIndex);

                ArrayList<Integer> materials = BuildHandlerHGSS.getMaterialOrder(data);

                buildHandler.getBuildModelMatshp().setMaterials(buildIndex, materials);
                updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "There was a problem importing the material order from the NSBMD.",
                        "Can't import material order", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbImportMaterialsFromNsbmdActionPerformed

    private void jlAreaBuildListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlAreaBuildListValueChanged
        if (buildHandler.getAreaBuildList() != null) {
            if (jlAreaBuildListEnabled.value) {
                try {
                    AreaBuild areaBuild = buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex());
                    int buildIndex = areaBuild.getBuildingIDs().get(jlAreaBuildList.getSelectedIndex());
                    byte[] data = buildHandler.getBuildModelList().getModelsData().get(buildIndex);
                    nitroDisplayAreaData.getObjectGL(0).setNsbmdData(data);
                } catch (Exception ex) {
                    nitroDisplayAreaData.getObjectGL(0).setNsbmd(null);
                }
                try {
                    nitroDisplayAreaData.getObjectGL(0).setNsbca(null);
                    nitroDisplayAreaData.getObjectGL(0).setNsbtp(null);
                    nitroDisplayAreaData.getObjectGL(0).setNsbta(null);
                    nitroDisplayAreaData.getObjectGL(0).setNsbva(null);
                    nitroDisplayAreaData.fitCameraToModel(0);
                    nitroDisplayAreaData.requestUpdate();
                } catch (Exception ex) {

                }
            }
        }
    }//GEN-LAST:event_jlAreaBuildListValueChanged

    private void jbAddTexToNsbtxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddTexToNsbtxActionPerformed
        try {
            byte[] nsbmdData = buildHandler.getBuildModelList().getModelsData().get(buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs().get(jlAreaBuildList.getSelectedIndex()));
            G3Dheader header = new G3Dheader(new ByteReader(nsbmdData));
            long[] offsets = header.getOffsets();

            if (offsets.length == 2) {
                byte[] nsbtxData = new byte[(int) (nsbmdData.length - offsets[1] + 20)];
                System.arraycopy(nsbmdData, (int) offsets[1] - 20, nsbtxData, 0, nsbtxData.length);

                Nsbtx2 nsbtxBuild = NsbtxLoader2.loadNsbtx(nsbtxData);
                Nsbtx2 nsbtx = nsbtxPanel.getNsbtx();

                int repeatedTextures = 0;
                int repeatedPalettes = 0;
                int addedTextures = 0;
                int addedPalettes = 0;
                //boolean newDataAdded = false;
                for (NsbtxTexture tex : nsbtxBuild.getTextures()) {
                    if (!nsbtx.getTextureNames().contains(tex.getName())) {
                        nsbtx.addTexture(tex);
                        addedTextures++;
                        //newDataAdded = true;
                    } else {
                        repeatedTextures++;
                    }
                }
                for (NsbtxPalette pal : nsbtxBuild.getPalettes()) {
                    if (!nsbtx.getPaletteNames().contains(pal.getName())) {
                        nsbtx.addPalette(pal);
                        addedPalettes++;
                        //newDataAdded = true;
                    } else {
                        repeatedPalettes++;
                    }
                }

                if (addedTextures > 0 || addedPalettes > 0) {
                    byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "temp2");

                    buildHandler.getBuildTilesetList().getTilesets().get(jlBuildTsetList.getSelectedIndex()).setData(newNsbtxData);

                    Nsbtx2 nsbtxReloaded = NsbtxLoader2.loadNsbtx(newNsbtxData);
                    nsbtxPanel.setNsbtx(nsbtxReloaded);
                    nsbtxPanel.updateViewTextureNameList(nsbtxReloaded.getTextures().size() - 1);
                    nsbtxPanel.updateViewPaletteNameList(nsbtxReloaded.getPalettes().size() - 1);
                    nsbtxPanel.updateView();

                    if (repeatedTextures > 0 || repeatedPalettes > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Textures and palettes succesfully imported.\n"
                                + "(Some textures or palettes were already in the tileset)\n"
                                + "- " + String.valueOf(addedTextures) + " textures added\n"
                                + "- " + String.valueOf(addedPalettes) + " palettes added",
                                "Data imported", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Textures and palettes succesfully imported.\n"
                                + "- " + String.valueOf(addedTextures) + " textures added\n"
                                + "- " + String.valueOf(addedPalettes) + " palettes added",
                                "Data imported", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The textures from the building's NSBMD are already in the NSBTX.",
                            "No data imported", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "The selected building's NSBMD doesn't contain any NSBTX",
                        "No NSBTX in the NSMBD", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "There was a problem importing the textures and palettes.\n"
                    + "Make sure that you have the converter program in the converter folder.",
                    "Can't import textures and palettes", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jbAddTexToNsbtxActionPerformed

    private void jbAddTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddTsetActionPerformed
        if (buildHandler.getBuildTilesetList() != null && buildHandler.getAreaBuildList() != null) {

            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Add a new Building NSBTX");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    byte[] data = Files.readAllBytes(fc.getSelectedFile().toPath());

                    buildHandler.addBuildingTileset(data);

                    updateViewBuildTsetList(buildHandler.getBuildTilesetList().getTilesets().size() - 1);
                    updateViewAreaBuildList(buildHandler.getAreaBuildList().getAreaBuilds().size() - 1);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "There was a problem adding the NSBTX",
                            "Can't add NSBTX", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbAddTsetActionPerformed

    private void jbReplaceTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReplaceTsetActionPerformed
        if (buildHandler.getBuildTilesetList() != null && buildHandler.getAreaBuildList() != null) {

            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Replace the Building NSBTX");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    byte[] data = Files.readAllBytes(fc.getSelectedFile().toPath());

                    buildHandler.replaceBuildingTileset(jlBuildTsetList.getSelectedIndex(), data);

                    updateViewBuildTsetList(jlBuildTsetList.getSelectedIndex());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "There was a problem adding the NSBTX",
                            "Can't add NSBTX", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


    }//GEN-LAST:event_jbReplaceTsetActionPerformed

    private void jbExportTilesetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportTilesetActionPerformed
        if (buildHandler.getBuildModelList() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save Building's NSBTX");
            try {//TODO: Replace this with some index bounds cheking?
                String fileName = "Building Tileset " + String.valueOf(jlBuildTsetList.getSelectedIndex());
                fc.setSelectedFile(new File(fileName + ".nsbtx"));
            } catch (Exception ex) {

            }
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    if (!fc.getSelectedFile().getPath().isEmpty()) {
                        buildHandler.saveBuildingTileset(jlBuildTsetList.getSelectedIndex(), fc.getSelectedFile().getPath());
                    } else {
                        JOptionPane.showMessageDialog(this, "The entered name must be valid",
                                "Enter a valid name", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem writing the NSBTX file",
                            "Error writing NSBTX file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbExportTilesetActionPerformed

    private void jbRemoveTsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveTsetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbRemoveTsetActionPerformed

    private void jbRemoveTexturesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveTexturesActionPerformed
        try {
            byte[] nsbmdData = buildHandler.getBuildModelList().getModelsData().get(buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs().get(jlAreaBuildList.getSelectedIndex()));
            G3Dheader header = new G3Dheader(new ByteReader(nsbmdData));
            long[] offsets = header.getOffsets();

            if (offsets.length == 2) {
                byte[] nsbtxData = new byte[(int) (nsbmdData.length - offsets[1] + 20)];
                System.arraycopy(nsbmdData, (int) offsets[1] - 20, nsbtxData, 0, nsbtxData.length);

                Nsbtx2 nsbtxBuild = NsbtxLoader2.loadNsbtx(nsbtxData);
                Nsbtx2 nsbtx = nsbtxPanel.getNsbtx();

                int removedTexs = 0;
                int removedPals = 0;
                for (NsbtxTexture tex : nsbtxBuild.getTextures()) {
                    if (nsbtx.getTextures().size() > 1) {
                        removedTexs += nsbtx.removeTexture(tex.getName());
                    }
                }
                for (NsbtxPalette pal : nsbtxBuild.getPalettes()) {
                    if (nsbtx.getPalettes().size() > 1) {
                        removedPals += nsbtx.removePalette(pal.getName());
                    }
                }

                byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "");

                buildHandler.getBuildTilesetList().getTilesets().get(jlBuildTsetList.getSelectedIndex()).setData(newNsbtxData);

                Nsbtx2 nsbtxReloaded = NsbtxLoader2.loadNsbtx(newNsbtxData);
                nsbtxPanel.setNsbtx(nsbtxReloaded);
                nsbtxPanel.updateViewTextureNameList(nsbtxReloaded.getTextures().size() - 1);
                nsbtxPanel.updateViewPaletteNameList(nsbtxReloaded.getPalettes().size() - 1);
                nsbtxPanel.updateView();

                if (removedTexs > 0 || removedPals > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Textures and palettes succesfully removed.\n"
                            + "- " + String.valueOf(removedTexs) + " textures removed\n"
                            + "- " + String.valueOf(removedPals) + " palettes removed",
                            "Data removed", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The textures and palettes from the building's NSBMD are not found in the NSBTX.",
                            "No data removed", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "The selected building's NSBMD doesn't contain any NSBTX",
                        "No NSBTX in the NSMBD", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            try {
                byte[] data = buildHandler.getBuildTilesetList().getTilesets().get(jlBuildTsetList.getSelectedIndex()).getData();
                Nsbtx2 nsbtx = NsbtxLoader2.loadNsbtx(data);
                nsbtxPanel.setNsbtx(nsbtx);
                nsbtxPanel.updateViewTextureNameList(0);
                nsbtxPanel.updateViewPaletteNameList(0);
                nsbtxPanel.updateView();
            } catch (Exception ex2) {

            }
            JOptionPane.showMessageDialog(this,
                    "There was a problem removing the textures and palettes.",
                    "Can't remove textures and palettes", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jbRemoveTexturesActionPerformed

    private void jbAddEmptyTilesetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddEmptyTilesetActionPerformed
        try {
            Nsbtx2 nsbtx = new Nsbtx2();
            BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            nsbtx.addTextureAndPalette(0, 0, img, Nsbtx2.FORMAT_COLOR_4, false, "placeholder", "placeholder_pl");
            byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "temp");

            buildHandler.addBuildingTileset(newNsbtxData);

            updateViewBuildTsetList(buildHandler.getBuildTilesetList().getTilesets().size() - 1);
            updateViewAreaBuildList(buildHandler.getAreaBuildList().getAreaBuilds().size() - 1);

            nitroDisplayAreaData.getObjectGL(0).setNsbmd(null);
            nitroDisplayAreaData.requestUpdate();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "There was an error adding the tileset.\n"
                    + "Make sure that the converter is found in the converter folder",
                    "Can't add tileset", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jbAddEmptyTilesetActionPerformed

    private void jbSetAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSetAnimationActionPerformed
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.removeAllBuildingMaterials(jlBuildModel.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
        }
    }//GEN-LAST:event_jbSetAnimationActionPerformed

    private void jbImportBldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbImportBldActionPerformed
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBuildDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("BLD (*.bld)", "bld"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Import Buildings File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                BuildFile buildFile = new BuildFile(fc.getSelectedFile().getAbsolutePath());

                handler.setBuildings(buildFile);

                updateViewBuildFileList(0);
                updateViewNitroDisplayMap();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "There was a problem importing the BLD file.",
                        "Can't import BLD file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jbImportBldActionPerformed

    private void jbExportBldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportBldActionPerformed
        if (handler.getBuildings() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastMapDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("BLD (*.bld)", "bld"));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save Building File");
            try {//TODO: Replace this with some index bounds cheking?
                File file = new File(handler.getMapMatrix().filePath);
                String fileName = Utils.removeExtensionFromPath(file.getName()) + "." + BuildFile.fileExtension;
                fc.setSelectedFile(new File(fileName));
            } catch (Exception ex) {

            }
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());

                    if (!fc.getSelectedFile().getPath().isEmpty()) {
                        handler.getBuildings().saveToFile(fc.getSelectedFile().getPath());
                    } else {
                        JOptionPane.showMessageDialog(this, "The entered name must be valid",
                                "Enter a valid name", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem writing the BLD file",
                            "Error writing BLD file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbExportBldActionPerformed

    private void jbAddBuildBldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddBuildBldActionPerformed
        if (handler.getBuildings() != null) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame(), true);
            dialog.init(buildHandler.getBuildModelList(), null);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACEPTED) {
                Build build = new Build();
                build.setModelID(dialog.getIndexSelected());
                handler.getBuildings().getBuilds().add(build);

                updateViewBuildFileList(handler.getBuildings().getBuilds().size() - 1);
                updateViewNitroDisplayMap();
            }
        }
    }//GEN-LAST:event_jbAddBuildBldActionPerformed

    private void jbRemoveBldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveBldActionPerformed
        if (handler.getBuildings() != null) {
            if (handler.getBuildings().getBuilds().size() > 0) {
                int index = jlBuildFile.getSelectedIndex();
                handler.getBuildings().getBuilds().remove(index);

                updateViewBuildFileList(index);
                updateViewNitroDisplayMap();
            }
        }
    }//GEN-LAST:event_jbRemoveBldActionPerformed

    private void jlBuildFileValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlBuildFileValueChanged
        if (jlBuildFileEnabled.value) {
            updateViewBuildProperties();
            setBoundingBoxes();
        }
    }//GEN-LAST:event_jlBuildFileValueChanged

    private void jsBuildIDStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBuildIDStateChanged
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setModelID((Integer) jsBuildID.getValue());

                updateViewBuildFileList(jlBuildFile.getSelectedIndex());
                updateViewNitroDisplayMap();
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_jsBuildIDStateChanged

    private void jsBuildXStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBuildXStateChanged
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setX((Float) jsBuildX.getValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_jsBuildXStateChanged

    private void jsBuildYStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBuildYStateChanged
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setY((Float) jsBuildY.getValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {

            }
        }

    }//GEN-LAST:event_jsBuildYStateChanged

    private void jsBuildZStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBuildZStateChanged
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());

                build.setZ((Float) jsBuildZ.getValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_jsBuildZStateChanged

    private void jsBuildScaleXStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBuildScaleXStateChanged
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setScaleX((Float) jsBuildScaleX.getValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_jsBuildScaleXStateChanged

    private void jsBuildScaleYStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBuildScaleYStateChanged
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setScaleY((Float) jsBuildScaleY.getValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_jsBuildScaleYStateChanged

    private void jsBuildScaleZStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBuildScaleZStateChanged
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setScaleZ((Float) jsBuildScaleZ.getValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {

            }
        }
    }//GEN-LAST:event_jsBuildScaleZStateChanged

    private void jbChooseModelBldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbChooseModelBldActionPerformed
        if (handler.getBuildings().getBuilds().size() > 0) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame(), true);
            dialog.init(buildHandler.getBuildModelList(), null);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACEPTED) {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setModelID(dialog.getIndexSelected());

                updateViewBuildFileList(jlBuildFile.getSelectedIndex());
                updateViewNitroDisplayMap();
            }
        }
    }//GEN-LAST:event_jbChooseModelBldActionPerformed

    private void jbOpenMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOpenMapActionPerformed
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Map's NSBMD");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());

                byte[] mapData = Files.readAllBytes(fc.getSelectedFile().toPath());
                nitroDisplayMap.getObjectGL(0).setNsbmdData(mapData);

                nitroDisplayMap.requestUpdate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "There was problem importing the map's NSBMD",
                        "Can't import map", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jbOpenMapActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        dispose();
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jcbModelsSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbModelsSelectedActionPerformed
        buildHandler.setBuildBlockIndexSelected(jcbModelsSelected.getSelectedIndex());

        updateView();
        updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
    }//GEN-LAST:event_jcbModelsSelectedActionPerformed

    private void jcbAnimType2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAnimType2ActionPerformed
        if (jcbAnimType2Enabled.value) {
            BuildAnimInfoHGSS info = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
            info.setAnimType2(BuildAnimInfoHGSS.namesAnimType2Swap.get(jcbAnimType2.getSelectedItem()));
        }
    }//GEN-LAST:event_jcbAnimType2ActionPerformed

    private void jcbLoopTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbLoopTypeActionPerformed
        if (jcbLoopEnabled.value) {
            BuildAnimInfoHGSS info = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
            info.setLoopType(BuildAnimInfoHGSS.namesLoopTypeSwap.get(jcbLoopType.getSelectedItem()));
        }
    }//GEN-LAST:event_jcbLoopTypeActionPerformed

    private void jcbNumAnimsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbNumAnimsActionPerformed
        if (jcbNumAnimsEnabled.value) {
            BuildAnimInfoHGSS info = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
            info.setNumAnims(BuildAnimInfoHGSS.namesNumAnimsSwap.get(jcbNumAnims.getSelectedItem()));
        }
    }//GEN-LAST:event_jcbNumAnimsActionPerformed

    private void jcbUnknown1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbUnknown1ActionPerformed
        if (jcbUnknown1Enabled.value) {
            BuildAnimInfoHGSS info = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
            info.setDoorSound(BuildAnimInfoHGSS.namesDoorSoundSwap.get(jcbUnknown1.getSelectedItem()));
        }
    }//GEN-LAST:event_jcbUnknown1ActionPerformed

    private void jcbDynamicTexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbDynamicTexActionPerformed
        if (jcbDynamicTexEnabled.value) {
            AreaDataHGSS areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            if(jcbDynamicTex.getSelectedIndex() == 0){
                areaData.setDynamicTexType(65535);
            }else{
                areaData.setDynamicTexType(jcbDynamicTex.getSelectedIndex() - 1);
            }
        }
    }//GEN-LAST:event_jcbDynamicTexActionPerformed

    private void jcbAreaLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAreaLightActionPerformed
        if (buildHandler.getAreaDataList() != null) {
            if (jcbAreaLightEnabled.value) {
                AreaDataHGSS areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
                areaData.setLightType(jcbAreaLight.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_jcbAreaLightActionPerformed

    private void jbRemoveAllUnusedTexPalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveAllUnusedTexPalsActionPerformed
        try {
            Set<String> textureNames = new TreeSet<>();
            Set<String> paletteNames = new TreeSet<>();

            final int numModels = buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs().size();
            for (int i = 0; i < numModels; i++) {
                try {
                    byte[] nsbmdData = buildHandler.getBuildModelList().getModelsData().get(buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs().get(i));

                    G3Dheader header = new G3Dheader(new ByteReader(nsbmdData));
                    long[] offsets = header.getOffsets();

                    if (offsets.length == 2) {//Has NSBTX
                        byte[] nsbtxData = new byte[(int) (nsbmdData.length - offsets[1] + 20)];
                        System.arraycopy(nsbmdData, (int) offsets[1] - 20, nsbtxData, 0, nsbtxData.length);

                        Nsbtx2 nsbtxBuild = NsbtxLoader2.loadNsbtx(nsbtxData);

                        for (NsbtxTexture tex : nsbtxBuild.getTextures()) {
                            textureNames.add(tex.getName());
                        }
                        for (NsbtxPalette pal : nsbtxBuild.getPalettes()) {
                            paletteNames.add(pal.getName());
                        }
                    }
                } catch (Exception ex) {

                }
            }

            Nsbtx2 nsbtx = nsbtxPanel.getNsbtx();

            int removedTexs = 0;
            int removedPals = 0;
            for (int i = 0; i < nsbtx.getTextures().size(); i++) {
                NsbtxTexture tex = nsbtx.getTexture(i);

                if (!textureNames.contains(tex.getName())) {
                    nsbtx.getTextures().remove(i);
                    removedTexs++;
                    i--;
                }
            }
            for (int i = 0; i < nsbtx.getPalettes().size(); i++) {
                NsbtxPalette pal = nsbtx.getPalette(i);

                if (!paletteNames.contains(pal.getName())) {
                    nsbtx.getPalettes().remove(i);
                    removedPals++;
                    i--;
                }
            }

            byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "");

            buildHandler.getBuildTilesetList().getTilesets().get(jlBuildTsetList.getSelectedIndex()).setData(newNsbtxData);

            Nsbtx2 nsbtxReloaded = NsbtxLoader2.loadNsbtx(newNsbtxData);
            nsbtxPanel.setNsbtx(nsbtxReloaded);
            nsbtxPanel.updateViewTextureNameList(nsbtxReloaded.getTextures().size() - 1);
            nsbtxPanel.updateViewPaletteNameList(nsbtxReloaded.getPalettes().size() - 1);
            nsbtxPanel.updateView();

            if (removedTexs > 0 || removedPals > 0) {
                JOptionPane.showMessageDialog(this,
                        "Textures and palettes succesfully removed.\n"
                        + "- " + String.valueOf(removedTexs) + " textures removed\n"
                        + "- " + String.valueOf(removedPals) + " palettes removed",
                        "Data removed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Can't remove textures or palettes from the NSBTX. \n"
                        + "All the textures and palettes are used by the buildings.",
                        "No data removed", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            try {
                byte[] data = buildHandler.getBuildTilesetList().getTilesets().get(jlBuildTsetList.getSelectedIndex()).getData();
                Nsbtx2 nsbtx = NsbtxLoader2.loadNsbtx(data);
                nsbtxPanel.setNsbtx(nsbtx);
                nsbtxPanel.updateViewTextureNameList(0);
                nsbtxPanel.updateViewPaletteNameList(0);
                nsbtxPanel.updateView();
            } catch (Exception ex2) {

            }
            JOptionPane.showMessageDialog(this,
                    "There was a problem removing the textures and palettes.",
                    "Can't remove textures and palettes", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jbRemoveAllUnusedTexPalsActionPerformed

    private void jbAddMapAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddMapAnimActionPerformed
        if (buildHandler.getMapAnimations() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Animation Files (*.nsbca, *.nsbta, *.nsbtp, *.nsbma)", "nsbca", "nsbta", "nsbtp", "nsbma"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Add a new Animation");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    buildHandler.addMapAnimationFile(fc.getSelectedFile().getPath());

                    updateViewMapAnimationsList(jlMapAnimationsList.getModel().getSize());
                    updateModelJcbMapAnimations();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem reading the animation file",
                            "Error opening animation file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbAddMapAnimActionPerformed

    private void jbReplaceMapAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReplaceMapAnimActionPerformed
        if (buildHandler.getMapAnimations() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Animation Files (*.nsbca, *.nsbta, *.nsbtp, *.nsbma)", "nsbca", "nsbta", "nsbtp", "nsbma"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Select the new Animation");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    buildHandler.replaceMapAnimationFile(jlMapAnimationsList.getSelectedIndex(), fc.getSelectedFile().getPath());

                    updateViewMapAnimationsList(jlMapAnimationsList.getSelectedIndex());
                    updateModelJcbMapAnimations();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem reading the animation file",
                            "Error opening animation file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbReplaceMapAnimActionPerformed

    private void jbExportMapAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportMapAnimActionPerformed
        if (buildHandler.getMapAnimations() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            String type = buildHandler.getMapAnimations().getAnimations().get(jlMapAnimationsList.getSelectedIndex()).getExtensionName();

            fc.setFileFilter(new FileNameExtensionFilter(type.toUpperCase() + " (*." + type + ")", type));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save the Animation");
            try {//TODO: Replace this with some index bounds cheking?
                String fileName = buildHandler.getMapAnimations().getAnimations().get(jlMapAnimationsList.getSelectedIndex()).getName();
                fc.setSelectedFile(new File(fileName + "." + type));
            } catch (Exception ex) {

            }
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastBuildDirectoryUsed(fc.getSelectedFile().getParent());

                    if (!fc.getSelectedFile().getPath().isEmpty()) {
                        buildHandler.saveMapAnimationFile(jlAnimationsList.getSelectedIndex(), fc.getSelectedFile().getPath());
                    } else {
                        JOptionPane.showMessageDialog(this, "The entered name must be valid",
                                "Enter a valid name", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem writing the Animation file",
                            "Error writing Animation file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jbExportMapAnimActionPerformed

    private void jbRemoveMapAnimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveMapAnimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbRemoveMapAnimActionPerformed

    private void jbOpenMap1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOpenMap1ActionPerformed
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Map's NSBMD");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());

                byte[] mapData = Files.readAllBytes(fc.getSelectedFile().toPath());
                nitroDisplayMapAnims.getObjectGL(0).setNsbmdData(mapData);

                nitroDisplayMapAnims.requestUpdate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "There was problem importing the map's NSBMD",
                        "Can't import map", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jbOpenMap1ActionPerformed

    private void jbPlayMapAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPlayMapAnimationActionPerformed
        try {
            ModelAnimation anim = buildHandler.getMapAnimations().getAnimations().get(jlMapAnimationsList.getSelectedIndex());

            loadAnimationInNitroDisplay(nitroDisplayMapAnims, 0, anim);
            if(anim.getAnimationType() == ModelAnimation.TYPE_NSBTA){
                
                nitroDisplayMapAnims.getObjectGL().enableNsbtaUseMaterialOrder();
                nitroDisplayMapAnims.requestUpdate();
                //nitroDisplayMapAnims.requestUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jbPlayMapAnimationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton jbAddAnim;
    private javax.swing.JButton jbAddAnimToBuild;
    private javax.swing.JButton jbAddAreaData;
    private javax.swing.JButton jbAddBuildBld;
    private javax.swing.JButton jbAddBuildToTset;
    private javax.swing.JButton jbAddBuilding;
    private javax.swing.JButton jbAddEmptyTileset;
    private javax.swing.JButton jbAddMapAnim;
    private javax.swing.JButton jbAddMaterial;
    private javax.swing.JButton jbAddTexToNsbtx;
    private javax.swing.JButton jbAddTset;
    private javax.swing.JButton jbApplyBuildTset;
    private javax.swing.JButton jbApplyMapTset;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbChooseModelBld;
    private javax.swing.JButton jbExportAnimation;
    private javax.swing.JButton jbExportBld;
    private javax.swing.JButton jbExportBuilding;
    private javax.swing.JButton jbExportMapAnim;
    private javax.swing.JButton jbExportTileset;
    private javax.swing.JButton jbFindBuilding;
    private javax.swing.JButton jbImportBld;
    private javax.swing.JButton jbImportMaterialsFromNsbmd;
    private javax.swing.JButton jbMoveMaterialDown;
    private javax.swing.JButton jbMoveMaterialUp;
    private javax.swing.JButton jbOpenMap;
    private javax.swing.JButton jbOpenMap1;
    private javax.swing.JButton jbPlay;
    private javax.swing.JButton jbPlayMapAnimation;
    private javax.swing.JButton jbRemoveAllUnusedTexPals;
    private javax.swing.JButton jbRemoveAnim;
    private javax.swing.JButton jbRemoveAnimToBuild;
    private javax.swing.JButton jbRemoveAreaData;
    private javax.swing.JButton jbRemoveBld;
    private javax.swing.JButton jbRemoveBuildToTset;
    private javax.swing.JButton jbRemoveBuilding;
    private javax.swing.JButton jbRemoveMapAnim;
    private javax.swing.JButton jbRemoveMaterial;
    private javax.swing.JButton jbRemoveTextures;
    private javax.swing.JButton jbRemoveTset;
    private javax.swing.JButton jbReplaceAnim;
    private javax.swing.JButton jbReplaceAnimToBuild;
    private javax.swing.JButton jbReplaceBuildToTset;
    private javax.swing.JButton jbReplaceBuilding;
    private javax.swing.JButton jbReplaceMapAnim;
    private javax.swing.JButton jbReplaceTset;
    private javax.swing.JButton jbSaveAll;
    private javax.swing.JButton jbSetAnimation;
    private javax.swing.JComboBox<String> jcbAnimType1;
    private javax.swing.JComboBox<String> jcbAnimType2;
    private javax.swing.JComboBox<String> jcbAreaLight;
    private javax.swing.JComboBox<String> jcbAreaType;
    private javax.swing.JComboBox<String> jcbDynamicTex;
    private javax.swing.JComboBox<String> jcbLoopType;
    private javax.swing.JComboBox<String> jcbModelsSelected;
    private javax.swing.JComboBox<String> jcbNumAnims;
    private javax.swing.JComboBox<String> jcbUnknown1;
    private javax.swing.JList<String> jlAnimationsList;
    private javax.swing.JList<String> jlAreaBuildList;
    private javax.swing.JList<String> jlAreaDataList;
    private javax.swing.JList<String> jlBuildFile;
    private javax.swing.JList<String> jlBuildModel;
    private javax.swing.JList<String> jlBuildTsetList;
    private javax.swing.JList<String> jlMapAnimationsList;
    private javax.swing.JList<String> jlMaterialOrder;
    private javax.swing.JList<String> jlSelectedAnimationsList;
    private javax.swing.JSpinner jsBuildID;
    private javax.swing.JSpinner jsBuildScaleX;
    private javax.swing.JSpinner jsBuildScaleY;
    private javax.swing.JSpinner jsBuildScaleZ;
    private javax.swing.JSpinner jsBuildX;
    private javax.swing.JSpinner jsBuildY;
    private javax.swing.JSpinner jsBuildZ;
    private javax.swing.JTextField jtfBuildTset;
    private javax.swing.JTextField jtfMapTset;
    private renderer.NitroDisplayGL nitroDisplayAreaData;
    private renderer.NitroDisplayGL nitroDisplayGL;
    private renderer.NitroDisplayGL nitroDisplayMap;
    private renderer.NitroDisplayGL nitroDisplayMapAnims;
    private editor.nsbtx2.NsbtxPanel nsbtxPanel;
    // End of variables declaration//GEN-END:variables

    public void init(MapEditorHandler handler, BuildHandlerHGSS buildHandler) {
        this.handler = handler;
        this.buildHandler = buildHandler;

    }

    public void updateView() {
        updateViewBuildModelList(jlBuildModel.getSelectedIndex());

        nitroDisplayGL.requestUpdate();

        updateViewBuildFileList(0);
        updateViewNitroDisplayMap();

        /*updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
        updateViewBuildAnimeList(jlSelectedAnimationsList.getSelectedIndex());
        updateViewAreaDataList(jlAreaDataList.getSelectedIndex());
        updateViewAreaDataProperties();
        updateViewBuildTsetList(jlBuildTsetList.getSelectedIndex());
        updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
        updateViewAnimationsList(jlAnimationsList.getSelectedIndex());*/
    }

    private void updateViewBuildModelList(int indexSelected) {
        if (buildHandler.getBuildModelList() != null) {

            addElementsToListWithIndices(jlBuildModel, jlBuildModelEnabled,
                    buildHandler.getBuildModelList().getModelsName(),
                    indexSelected);
            //jScrollPane1.getVerticalScrollBar().setValue(indexSelected);
        }
    }

    private void updateViewBuildFileList(int indexSelected) {
        if (handler.getBuildings() != null) {
            jlBuildFileEnabled.value = false;

            List<Build> builds = handler.getBuildings().getBuilds();

            DefaultListModel listModel = new DefaultListModel();
            for (int i = 0; i < builds.size(); i++) {
                Build build = builds.get(i);
                int buildID = build.getModeID();
                if (buildID >= 0 && buildID < buildHandler.getBuildModelList().getSize()) {
                    listModel.addElement(String.valueOf(buildID) + ": " + buildHandler.getBuildModelList().getModelsName().get(buildID));
                } else {
                    listModel.addElement(String.valueOf(buildID) + ": " + "NOT FOUND");
                }
            }
            jlBuildFile.setModel(listModel);
            jlBuildFileEnabled.value = true;

            indexSelected = Math.max(Math.min(jlBuildFile.getModel().getSize() - 1, indexSelected), 0);
            jlBuildFile.setSelectedIndex(indexSelected);
            jlBuildFile.ensureIndexIsVisible(indexSelected);
        }
    }

    private void updateViewBuildProperties() {
        if (handler.getBuildings() != null) {
            int buildIndex = jlBuildFile.getSelectedIndex();
            Build build = handler.getBuildings().getBuilds().get(buildIndex);

            buildPropertiesEnabled.value = false;
            jsBuildID.setValue(build.getModeID());
            jsBuildX.setValue(build.getX());
            jsBuildY.setValue(build.getY());
            jsBuildZ.setValue(build.getZ());
            jsBuildScaleX.setValue(build.getScaleX());
            jsBuildScaleY.setValue(build.getScaleY());
            jsBuildScaleZ.setValue(build.getScaleZ());
            buildPropertiesEnabled.value = true;
        }
    }

    private void updateViewMaterialOrderList(int indexSelected) {
        if (buildHandler.getBuildModelMatshp() != null) {
            ArrayList<Integer> materials = buildHandler.getBuildModelMatshp().getAllMaterials().get(jlBuildModel.getSelectedIndex());
            ArrayList<String> names;
            if (!materials.isEmpty()) {
                names = new ArrayList<>(materials.size());
                for (Integer material : materials) {
                    names.add("Material " + String.valueOf(material));
                }
            } else {
                names = new ArrayList<>();
                names.add("Undefined (Animation)");
            }
            addElementsToList(jlMaterialOrder, names, indexSelected);
        }
    }

    private void updateViewSelectedBuildAnimationsList(int indexSelected) {
        if (buildHandler.getBuildModelAnimeList() != null && buildHandler.getBuildModelAnims() != null) {
            jcbAnimationTypeEnabled.value = false;
            ArrayList<String> names = new ArrayList<>();
            //System.out.println("Build model index: " + jlBuildModel.getSelectedIndex());
            ArrayList<Integer> animations = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()).getAnimIDs();
            if (!animations.isEmpty()) {
                selectedAnimIconIndices = new ArrayList<>(animations.size());
                for (Integer animID : animations) {
                    names.add(String.valueOf(animID) + ": "
                            + buildHandler.getBuildModelAnims().getAnimations().get(animID).getName() + " ["
                            + buildHandler.getBuildModelAnims().getAnimationTypeName(animID) + "]");
                    selectedAnimIconIndices.add(buildHandler.getBuildModelAnims().getAnimationType(animID));
                }
            }
            addElementsToList(jlSelectedAnimationsList, names, indexSelected);

            //int animType = buildHandler.getBuildModelAnimeList().getSecondBytes().get(jlBuildModel.getSelectedIndex());
            updateViewAnimInfo();
            //jcbAnimationType.setSelectedIndex(animType + 1);
            jcbAnimationTypeEnabled.value = true;
        }
    }

    private void updateViewAnimInfo() {
        try {
            BuildAnimInfoHGSS info = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());

            updateViewJCB(jcbAnimType1, jcbAnimType1Enabled, BuildAnimInfoHGSS.namesAnimType1, info.getAnimType1());
            updateViewJCB(jcbAnimType2, jcbAnimType2Enabled, BuildAnimInfoHGSS.namesAnimType2, info.getAnimType2());
            updateViewJCB(jcbLoopType, jcbLoopEnabled, BuildAnimInfoHGSS.namesLoopType, info.getLoopType());
            updateViewJCB(jcbNumAnims, jcbNumAnimsEnabled, BuildAnimInfoHGSS.namesNumAnims, info.getNumAnims());
            updateViewJCB(jcbUnknown1, jcbUnknown1Enabled, BuildAnimInfoHGSS.namesDoorSound, info.getDoorSound());
        } catch (Exception ex) {

        }

    }

    private void updateViewJCB(JComboBox jcb, MutableBoolean jcbEnabled, Map<Integer, String> map, Integer value) {
        jcbEnabled.value = false;
        try {
            jcb.setSelectedItem(map.get(value));
        } catch (Exception ex) {
            jcb.setSelectedIndex(0);
        }
        jcbEnabled.value = true;
    }
    
    private void updateModelJcbMapAnimations(){
        jcbDynamicTexEnabled.value = false;
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("No Animations");
        for(int i = 0; i < buildHandler.getMapAnimations().getAnimations().size(); i++){
            model.addElement(buildHandler.getMapAnimations().getAnimations().get(i).getName());
        }
        jcbDynamicTex.setModel(model);
        jcbDynamicTexEnabled.value = true;
    }
    
    private void updateViewJcbMapAnimations(Integer value) {
        jcbDynamicTexEnabled.value = false;
        try {
            if(value == 65535){
                jcbDynamicTex.setSelectedIndex(0);
            }else{
                jcbDynamicTex.setSelectedIndex(value + 1);
            }
        } catch (Exception ex) {
            jcbDynamicTex.setSelectedIndex(0);
        }
        jcbDynamicTexEnabled.value = true;
    }

    private void updateViewAreaDataList(int indexSelected) {
        if (buildHandler.getAreaDataList() != null) {
            addElementsToList(jlAreaDataList, jlAreaDataListEnabled, "Area Data", buildHandler.getAreaDataList().getAreaDatas().size(), indexSelected);
        }
    }

    private void updateViewAreaDataProperties() {
        if (buildHandler.getAreaDataList() != null) {
            AreaDataHGSS areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            jtfBuildTsetEnabled.value = false;
            jtfMapTsetEnabled.value = false;
            jcbDynamicTexEnabled.value = false;
            jcbAreaTypeEnabled.value = false;
            jcbAreaLightEnabled.value = false;

            jtfBuildTset.setText(String.valueOf(areaData.getBuildingTilesetID()));
            jtfMapTset.setText(String.valueOf(areaData.getMapTilesetID()));
            jcbAreaType.setSelectedIndex(Math.max(Math.min(1, areaData.getAreaType()), 0));
            jcbAreaLight.setSelectedIndex(Math.max(Math.min(2, areaData.getLightType()), 0));
            updateViewJcbMapAnimations(areaData.getDynamicTexType());
            //updateViewJCB(jcbDynamicTex, jcbDynamicTexEnabled, AreaDataHGSS.namesDynamicTexType, areaData.getDynamicTexType());

            jtfBuildTset.setBackground(whiteColor);
            jtfMapTset.setBackground(whiteColor);
            jcbAreaType.setBackground(whiteColor);

            jtfBuildTsetEnabled.value = true;
            jtfMapTsetEnabled.value = true;
            jcbDynamicTexEnabled.value = true;
            jcbAreaTypeEnabled.value = true;
            jcbAreaLightEnabled.value = true;
        }
    }

    private void updateViewBuildTsetList(int indexSelected) {
        if (buildHandler.getBuildTilesetList() != null) {
            addElementsToList(jlBuildTsetList, jlBuildTsetListEnabled, "Tileset", buildHandler.getBuildTilesetList().getTilesets().size(), indexSelected);
        }
    }

    private void updateViewAreaBuildList(int indexSelected) {
        if (buildHandler.getAreaBuildList() != null && buildHandler.getBuildModelList() != null) {
            jlAreaBuildListEnabled.value = false;
            final int selectedTileset = jlBuildTsetList.getSelectedIndex();
            ArrayList<Integer> IDs = buildHandler.getAreaBuildList().getAreaBuilds().get(selectedTileset).getBuildingIDs();
            DefaultListModel listModel = new DefaultListModel();
            for (int i = 0; i < IDs.size(); i++) {
                try {
                    listModel.addElement(String.valueOf(IDs.get(i)) + ": " + buildHandler.getBuildModelList().getModelsName().get(IDs.get(i)));
                } catch (Exception ex) {
                    listModel.addElement(String.valueOf(IDs.get(i)) + ": " + "NOT FOUND");
                }
            }
            jlAreaBuildList.setModel(listModel);
            jlAreaBuildListEnabled.value = true;

            indexSelected = Math.max(Math.min(jlAreaBuildList.getModel().getSize() - 1, indexSelected), 0);
            jlAreaBuildList.setSelectedIndex(indexSelected);
            jlAreaBuildList.ensureIndexIsVisible(indexSelected);
        }
    }

    private void updateViewAnimationsList(int indexSelected) {
        if (buildHandler.getBuildModelAnims() != null) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<ModelAnimation> animations = buildHandler.getBuildModelAnims().getAnimations();
            animIconIndices = new ArrayList<>(animations.size());
            for (int i = 0; i < animations.size(); i++) {
                names.add(String.valueOf(i) + ": "
                        + animations.get(i).getName() + " ["
                        + animations.get(i).getAnimationTypeName() + "]");
                animIconIndices.add(animations.get(i).getAnimationType());
            }
            addElementsToList(jlAnimationsList, names, indexSelected);
            jlAnimationsList.ensureIndexIsVisible(indexSelected);
        }
    }

    private void updateViewMapAnimationsList(int indexSelected) {
        if (buildHandler.getMapAnimations() != null) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<ModelAnimation> animations = buildHandler.getMapAnimations().getAnimations();
            mapAnimIconIndices = new ArrayList<>(animations.size());
            for (int i = 0; i < animations.size(); i++) {
                names.add(String.valueOf(i) + ": "
                        + animations.get(i).getName() + " ["
                        + animations.get(i).getAnimationTypeName() + "]");
                mapAnimIconIndices.add(animations.get(i).getAnimationType());
            }
            addElementsToList(jlMapAnimationsList, names, indexSelected);
            jlMapAnimationsList.ensureIndexIsVisible(indexSelected);
        }
    }

    public void loadGame(String folderPath) {
        buildHandler.setGameFolderPath(folderPath);
        if (buildHandler.areAllFilesAvailable()) {
            try {
                buildHandler.loadAllFiles();

                updateModelJcbMapAnimations();
                updateViewBuildModelList(0);
                updateViewMaterialOrderList(0);
                updateViewSelectedBuildAnimationsList(0);
                updateViewAreaDataList(0);
                updateViewBuildTsetList(0);
                updateViewAreaBuildList(0);
                updateViewAnimationsList(0);
                updateViewMapAnimationsList(0);
                nitroDisplayGL.requestUpdate();
                
                
                tryLoadBuildFile();
                tryLoadMapAnimFile();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "There was a problem reading some of the files.",
                        "Error opening game files", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Can't find some game files. \n"
                    + "(Searching for Pokemon " + handler.getGame().getName() + " game files) \n"
                    + "Make sure that you select the main game folder.",
                    "Error finding game files", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void setBoundingBoxes() {
        for (ObjectGL object : nitroDisplayMap.getObjectsGL()) {
            object.setDrawBounds(false);
        }

        try {
            int index = jlBuildFile.getSelectedIndex() + 1;
            if (index > 0) {
                nitroDisplayMap.getObjectsGL().get(jlBuildFile.getSelectedIndex() + 1).setDrawBounds(true);
            }
        } catch (Exception ex) {

        }
    }

    public void updateViewNitroDisplayMap() {
        BuildFile buildFile = handler.getBuildings();

        for (int i = 1, size = nitroDisplayMap.getObjectsGL().size(); i < size; i++) {
            nitroDisplayMap.getObjectsGL().remove(nitroDisplayMap.getObjectsGL().size() - 1);
        }

        for (int i = 0; i < buildFile.getBuilds().size(); i++) {
            Build build = buildFile.getBuilds().get(i);

            ObjectGL object;
            try {
                object = nitroDisplayMap.getObjectGL(1 + i);
            } catch (Exception ex) {
                nitroDisplayMap.getObjectsGL().add(new ObjectGL());
                object = nitroDisplayMap.getObjectGL(1 + i);
            }
            try {
                byte[] data = buildHandler.getBuildModelList().getModelsData().get(build.getModeID());
                object.setNsbmdData(data);
            } catch (Exception ex) {

            }

            try {
                for (Integer animIndex : buildHandler.getBuildModelAnimeList().getAnimations().get(build.getModeID()).getAnimIDs()) {
                    ModelAnimation anim = buildHandler.getBuildModelAnims().getAnimations().get(animIndex);
                    loadAnimationInNitroDisplay(nitroDisplayMap, 1 + i, anim);
                }
            } catch (Exception ex) {

            }

            object.setX(build.getX() * 16.0f);
            object.setY(build.getY() * 16.0f);
            object.setZ(build.getZ() * 16.0f);
            object.setScaleX(build.getScaleX());
            object.setScaleY(build.getScaleY());
            object.setScaleZ(build.getScaleZ());

            nitroDisplayMap.requestUpdate();
        }

        setBoundingBoxes();
    }

    public void updateViewNitroDisplayMapBuildProperties(int buildingIndex) {
        try {
            BuildFile buildFile = handler.getBuildings();
            Build build = buildFile.getBuilds().get(buildingIndex);
            ObjectGL object = nitroDisplayMap.getObjectGL(1 + buildingIndex);
            object.setX(build.getX() * 16.0f);
            object.setY(build.getY() * 16.0f);
            object.setZ(build.getZ() * 16.0f);
            object.setScaleX(build.getScaleX());
            object.setScaleY(build.getScaleY());
            object.setScaleZ(build.getScaleZ());
        } catch (Exception ex) {

        }
    }

    public void loadCurrentNsbmd() {
        try {
            byte[] data = buildHandler.getBuildModelList().getModelsData().get(jlBuildModel.getSelectedIndex());
            nitroDisplayGL.getObjectGL(0).setNsbmdData(data);
            nitroDisplayGL.getObjectGL(0).setNsbca(null);
            nitroDisplayGL.getObjectGL(0).setNsbtp(null);
            nitroDisplayGL.getObjectGL(0).setNsbta(null);
            nitroDisplayGL.getObjectGL(0).setNsbva(null);
        } catch (Exception ex) {

        }
    }

    private void loadAnimationInNitroDisplay(NitroDisplayGL display, int objectIndex, ModelAnimation anim) {
        if (anim.getAnimationType() == ModelAnimation.TYPE_NSBCA) {
            NSBCAreader reader = new NSBCAreader(new ByteReader(anim.getData()));
            display.getObjectGL(objectIndex).setNsbca((NSBCA) reader.readFile());
            display.requestUpdate();
        } else if (anim.getAnimationType() == ModelAnimation.TYPE_NSBTA) {
            NSBTAreader reader = new NSBTAreader(new ByteReader(anim.getData()));
            display.getObjectGL(objectIndex).setNsbta((NSBTA) reader.readFile());
            display.requestUpdate();
        } else if (anim.getAnimationType() == ModelAnimation.TYPE_NSBTP) {
            NSBTPreader reader = new NSBTPreader(new ByteReader(anim.getData()));
            display.getObjectGL(objectIndex).setNsbtp((NSBTP) reader.readFile());
            display.requestUpdate();
        }
        /*else if (anim.getAnimationType() == BuildAnimation.TYPE_NSBMA) {
            NSBMAreader reader = new NSBMAreader(new ByteReader(anim.getData()));
            nitroDisplayGL1.getHandler().setNsbma((NSBMA) reader.readFile());
            nitroDisplayGL1.requestUpdate();
        }*/
    }

    public void tryLoadBuildFile() {
        try {
            File[] files = findFilesWithExtension(new File(handler.getMapMatrix().filePath).getParent(), "nsbmd");
            if (files.length > 0) {

                byte[] mapData = Files.readAllBytes(files[0].toPath());
                nitroDisplayMap.getObjectGL(0).setNsbmdData(mapData);

                nitroDisplayMap.requestUpdate();
            }
        } catch (Exception ex) {

        }
    }

    public void tryLoadMapAnimFile() {
        try {
            String filePath = handler.getMapMatrix().getFilePathWithCoords(handler.getMapMatrix().getMatrix(),
                    new File(handler.getMapMatrix().filePath).getParent(),
                    new File(handler.getMapMatrix().filePath).getName(),
                    handler.getMapSelected(), "nsbmd");

            File file = new File(filePath);
            if (file.exists()) {
                byte[] mapData = Files.readAllBytes(file.toPath());
                nitroDisplayMapAnims.getObjectGL(0).setNsbmdData(mapData);
                nitroDisplayMapAnims.requestUpdate();
            } else {
                File[] files = findFilesWithExtension(new File(handler.getMapMatrix().filePath).getParent(), "nsbmd");
                if (files.length > 0) {
                    byte[] mapData = Files.readAllBytes(files[0].toPath());
                    nitroDisplayMapAnims.getObjectGL(0).setNsbmdData(mapData);
                    nitroDisplayMapAnims.requestUpdate();
                }
            }
        } catch (Exception ex) {

        }
    }

    private void saveBuildings() throws IOException {
        File file = new File(handler.getMapMatrix().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + BuildFile.fileExtension;
        handler.getBuildings().saveToFile(path + File.separator + filename);
    }

    private static File[] findFilesWithExtension(String folderPath, String extension) {
        File dir = new File(folderPath);

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(extension);
            }
        });
    }

    private static void addElementsToList(JList list, MutableBoolean listEnabled, String name, int numElements, int indexSelected) {
        listEnabled.value = false;
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < numElements; i++) {
            listModel.addElement(name + " " + String.valueOf(i));
        }
        list.setModel(listModel);
        listEnabled.value = true;

        indexSelected = Math.max(Math.min(list.getModel().getSize() - 1, indexSelected), 0);
        list.setSelectedIndex(indexSelected);
        list.ensureIndexIsVisible(indexSelected);

    }

    private static void addElementsToListWithIndices(JList list, MutableBoolean listEnabled, ArrayList<String> elements, int indexSelected) {
        listEnabled.value = false;
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < elements.size(); i++) {
            listModel.addElement(String.valueOf(i) + ": " + elements.get(i));
        }
        list.setModel(listModel);
        listEnabled.value = true;

        indexSelected = Math.max(Math.min(list.getModel().getSize() - 1, indexSelected), 0);
        list.setSelectedIndex(indexSelected);
        list.ensureIndexIsVisible(indexSelected);

    }

    private static void addElementsToList(JList list, ArrayList<String> elements, int indexSelected) {
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < elements.size(); i++) {
            listModel.addElement(elements.get(i));
        }
        list.setModel(listModel);

        indexSelected = Math.max(Math.min(list.getModel().getSize() - 1, indexSelected), 0);
        list.setSelectedIndex(indexSelected);
    }

    private static void addIconToJList(JList list, ImageIcon icon) {
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setIcon(icon);
                return label;
            }
        });
    }

}
