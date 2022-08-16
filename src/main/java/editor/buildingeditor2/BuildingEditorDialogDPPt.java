package editor.buildingeditor2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import javax.swing.event.*;

import formats.nsbtx2.*;
import net.miginfocom.swing.*;
import editor.buildingeditor2.animations.AddBuildAnimationDialog;
import editor.buildingeditor2.animations.ModelAnimation;
import editor.buildingeditor2.animations.BuildAnimeListDPPt;
import editor.buildingeditor2.areabuild.AddBuildModelDialog;
import editor.buildingeditor2.areabuild.AreaBuild;
import editor.buildingeditor2.areadata.AreaDataDPPt;
import editor.buildingeditor2.buildfile.Build;
import editor.buildingeditor2.buildfile.BuildFile;
import editor.handler.MapEditorHandler;
import formats.nsbtx2.Nsbtx2;
import formats.nsbtx2.NsbtxLoader2;
import formats.nsbtx2.NsbtxPalette;
import formats.nsbtx2.NsbtxTexture;
import formats.nsbtx2.NsbtxWriter;

import java.awt.Color;

import nitroreader.nsbva.NSBVA;
import nitroreader.nsbva.NSBVAreader;
import utils.Utils.MutableBoolean;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
 * @author Trifindo, JackHack96
 */
public class BuildingEditorDialogDPPt extends JDialog {
    private MapEditorHandler handler;
    private BuildHandlerDPPt buildHandler;

    private ImageIcon nsbmdIcon;
    private ImageIcon nsbtxIcon;
    private ImageIcon areaDataIcon;
    private ArrayList<ImageIcon> animIcons;
    private ArrayList<Integer> selectedAnimIconIndices;
    private ArrayList<Integer> animIconIndices;

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
    private MutableBoolean jtfUnknown1Enabled = new MutableBoolean(true);
    private MutableBoolean jcbAreaTypeEnabled = new MutableBoolean(true);
    private MutableBoolean buildPropertiesEnabled = new MutableBoolean(true);

    private static final Color redColor = new Color(255, 200, 200);
    private static final Color greenColor = new Color(200, 255, 200);
    private static final Color defaultTextPaneBackground = UIManager.getColor("TextPane.background");

    public BuildingEditorDialogDPPt(Window owner) {
        super(owner);
        initComponents();

        final Class c = getClass();

        nsbmdIcon = new ImageIcon(c.getResource("/icons/NsbmdIcon.png"));
        nsbtxIcon = new ImageIcon(c.getResource("/icons/NsbtxIcon.png"));
        areaDataIcon = new ImageIcon(c.getResource("/icons/AreaDataIcon.png"));

        jTabbedPane1.setIconAt(0, new ImageIcon(c.getResource("/icons/BuildingIcon.png")));
        jTabbedPane1.setIconAt(1, new ImageIcon(c.getResource("/icons/AreaDataIcon.png")));
        jTabbedPane1.setIconAt(2, nsbtxIcon);
        jTabbedPane1.setIconAt(3, new ImageIcon(c.getResource("/icons/AnimationIcon.png")));
        jTabbedPane1.setIconAt(4, new ImageIcon(c.getResource("/icons/mapIcon.png")));

        animIcons = new ArrayList<>(5);
        animIcons.add(new ImageIcon(c.getResource("/icons/NsbcaIcon.png")));
        animIcons.add(new ImageIcon(c.getResource("/icons/NsbtaIcon.png")));
        animIcons.add(new ImageIcon(c.getResource("/icons/NsbtpIcon.png")));
        animIcons.add(new ImageIcon(c.getResource("/icons/NsbmaIcon.png")));
        animIcons.add(new ImageIcon(c.getResource("/icons/NsbvaIcon.png")));

        selectedAnimIconIndices = new ArrayList<>();
        animIconIndices = new ArrayList<>();

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

        Utils.addListenerToJTextFieldColor(jtfBuildTset, jtfBuildTsetEnabled,new Color(255, 185, 185), Color.black);
        Utils.addListenerToJTextFieldColor(jtfMapTset, jtfMapTsetEnabled, new Color(255, 185, 185), Color.black);
        Utils.addListenerToJTextFieldColor(jtfUnknown1, jtfUnknown1Enabled, new Color(255, 185, 185), Color.black);

        nitroDisplayGL.getObjectsGL().add(new ObjectGL());
        nitroDisplayAreaData.getObjectsGL().add(new ObjectGL());
        nitroDisplayMap.getObjectsGL().add(new ObjectGL());//Add space for Map's NSBMD
    }

    private void jlBuildModelValueChanged(ListSelectionEvent evt) {
        if (jlBuildModelEnabled.value) {
            updateViewMaterialOrderList(0);
            updateViewSelectedBuildAnimationsList(0);

            loadCurrentNsbmd();
            nitroDisplayGL.fitCameraToModel(0);
            nitroDisplayGL.requestUpdate();
        }
    }

    private void jlAreaDataListValueChanged(ListSelectionEvent evt) {
        if (jlAreaDataListEnabled.value) {
            updateViewAreaDataProperties();
        }

    }

    private void jlBuildTsetListValueChanged(ListSelectionEvent evt) {
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
                ex.printStackTrace();
            }
        }
    }

    private void jbAddBuildingActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Add a new NSBMD Building Model");
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbReplaceBuildingActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Select the new NSBMD Building Model (material order will be deleted)");
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbExportBuildingActionPerformed(ActionEvent evt) {
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
                ex.printStackTrace();
            }
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbRemoveBuildingActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null) {
            ArrayList<Integer> occurrences = buildHandler.getAreaBuildList().getBuildingOccurrences(jlBuildModel.getSelectedIndex());
            if (!occurrences.isEmpty()) {
                String occurenceIDs = "";
                for (int i = 0; i < occurrences.size() - 1; i++) {
                    occurenceIDs += String.valueOf(occurrences.get(i)) + ", ";
                }
                occurenceIDs += String.valueOf(occurrences.get(occurrences.size() - 1));

                final int returnVal2 = JOptionPane.showConfirmDialog(this.getContentPane(),
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
                final int returnVal = JOptionPane.showConfirmDialog(this.getContentPane(),
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
    }

    private void jbAddMaterialActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.addBuildingMaterial(jlBuildModel.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
        }
    }

    private void jbRemoveMaterialActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.removeBuildingMaterial(jlBuildModel.getSelectedIndex(), jlMaterialOrder.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
        }
    }

    private void jbMoveMaterialUpActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.moveBuildingMaterialUp(jlBuildModel.getSelectedIndex(), jlMaterialOrder.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex() - 1);
        }
    }

    private void jbMoveMaterialDownActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.moveBuildingMaterialDown(jlBuildModel.getSelectedIndex(), jlMaterialOrder.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex() + 1);
        }
    }

    private void jbAddAnimToBuildActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null
                && buildHandler.getBuildModelAnimeList() != null
                && buildHandler.getBuildModelAnims() != null) {
            ArrayList<Integer> animations = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
            if (animations.isEmpty() || animations.size() < BuildAnimeListDPPt.MAX_ANIMS_PER_BUILDING) {
                final AddBuildAnimationDialog dialog = new AddBuildAnimationDialog(handler.getMainFrame());
                dialog.init(buildHandler.getBuildModelList().getModelsData().get(jlBuildModel.getSelectedIndex()),
                        buildHandler.getBuildModelAnims(),
                        buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()));
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                if (dialog.getReturnValue() == AddBuildAnimationDialog.ACCEPTED) {
                    buildHandler.addBuildingAnimation(jlBuildModel.getSelectedIndex(), dialog.getIndexSelected());
                    updateViewSelectedBuildAnimationsList(jlSelectedAnimationsList.getSelectedIndex() + 1);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Buildings can't have more than "
                                + String.valueOf(BuildAnimeListDPPt.MAX_ANIMS_PER_BUILDING) + " animations.",
                        "Can't add animation", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jbReplaceAnimToBuildActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null
                && buildHandler.getBuildModelAnimeList() != null
                && buildHandler.getBuildModelAnims() != null) {
            if (buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()).size() > 0) {
                final AddBuildAnimationDialog dialog = new AddBuildAnimationDialog(handler.getMainFrame());
                dialog.init(buildHandler.getBuildModelList().getModelsData().get(jlBuildModel.getSelectedIndex()),
                        buildHandler.getBuildModelAnims(),
                        buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex()));
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                if (dialog.getReturnValue() == AddBuildAnimationDialog.ACCEPTED) {
                    buildHandler.replaceBuildingAnimation(jlBuildModel.getSelectedIndex(), dialog.getIndexSelected(), jlSelectedAnimationsList.getSelectedIndex());
                    updateViewSelectedBuildAnimationsList(jlSelectedAnimationsList.getSelectedIndex());
                }
            }
        }
    }

    private void jbRemoveAnimToBuildActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null
                && buildHandler.getBuildModelAnimeList() != null
                && buildHandler.getBuildModelAnims() != null) {
            buildHandler.removeBuildingAnimation(jlBuildModel.getSelectedIndex(), jlSelectedAnimationsList.getSelectedIndex());
            updateViewSelectedBuildAnimationsList(jlSelectedAnimationsList.getSelectedIndex() - 1);
        }
    }

    private void jbAddBuildToTsetActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getAreaBuildList() != null && buildHandler.getBuildTilesetList() != null) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame());
            dialog.init(buildHandler.getBuildModelList(), buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACCEPTED) {
                buildHandler.addBuildToAreaBuild(jlBuildTsetList.getSelectedIndex(), dialog.getIndexSelected());
                updateViewAreaBuildList(jlAreaBuildList.getModel().getSize());
            }
        }
    }

    private void jbReplaceBuildToTsetActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getAreaBuildList() != null && buildHandler.getBuildTilesetList() != null) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame());
            dialog.init(buildHandler.getBuildModelList(), buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex()).getBuildingIDs());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACCEPTED) {
                buildHandler.replaceBuildToAreaBuild(jlBuildTsetList.getSelectedIndex(), dialog.getIndexSelected(), jlAreaBuildList.getSelectedIndex());
                updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
            }
        }
    }

    private void jbRemoveBuildToTsetActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getAreaBuildList() != null && buildHandler.getBuildTilesetList() != null) {
            buildHandler.removeBuildToAreaBuild(jlBuildTsetList.getSelectedIndex(), jlAreaBuildList.getSelectedIndex());
            updateViewAreaBuildList(jlAreaBuildList.getSelectedIndex());
        }
    }

    private void jbAddAnimActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelAnims() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Animation Files (*.nsbca, *.nsbta, *.nsbtp, *.nsbma, *.nsbva)", "nsbca", "nsbta", "nsbtp", "nsbma", "nsbva"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Add a new Animation");
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbReplaceAnimActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelAnims() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Animation Files (*.nsbca, *.nsbta, *.nsbtp, *.nsbma, *.nsbva)", "nsbca", "nsbta", "nsbtp", "nsbma", "nsbva"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Select the new Animation");
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbExportAnimationActionPerformed(ActionEvent evt) {
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
                ex.printStackTrace();
            }
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbRemoveAnimActionPerformed(ActionEvent evt) {

    }

    private void jbSaveAllActionPerformed(ActionEvent evt) {
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
                Logger.getLogger(BuildingEditorDialogDPPt.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    private void jbFindBuildingActionPerformed(ActionEvent evt) {
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
    }

    private void jcbAnimationTypeActionPerformed(ActionEvent evt) {
        if (jcbAnimationTypeEnabled.value) {
            if (buildHandler.getBuildModelAnimeList() != null) {
                buildHandler.getBuildModelAnimeList().getSecondBytes().set(jlBuildModel.getSelectedIndex(), (byte) (jcbAnimationType.getSelectedIndex() - 1));
            }
        }
    }

    private void jbAddAreaDataActionPerformed(ActionEvent evt) {
        if (buildHandler.getAreaDataList() != null) {
            buildHandler.getAreaDataList().getAreaDatas().add(new AreaDataDPPt());
            updateViewAreaDataList(buildHandler.getAreaDataList().getAreaDatas().size());
        }
    }

    private void jbRemoveAreaDataActionPerformed(ActionEvent evt) {
        if (buildHandler.getAreaDataList() != null) {
            final int returnVal = JOptionPane.showConfirmDialog(this.getContentPane(),
                    "Removing Area Datas is NOT recommended. \n"
                            + "Do you really want to remove this Area Data?",
                    "Confirm remove Area Data", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (returnVal == JOptionPane.YES_OPTION) {
                buildHandler.getAreaDataList().getAreaDatas().remove(jlAreaDataList.getSelectedIndex());
                updateViewAreaDataList(jlAreaDataList.getSelectedIndex());
            }
        }
    }

    private void jbApplyBuildTsetActionPerformed(ActionEvent evt) {
        if (buildHandler.getAreaDataList() != null) {
            AreaDataDPPt areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            try {
                int value = Integer.parseInt(jtfBuildTset.getText());
                areaData.setBuildingTilesetID(value);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            jtfBuildTsetEnabled.value = false;
            jtfBuildTset.setText(String.valueOf(areaData.getBuildingTilesetID()));
            jtfBuildTset.setBackground(greenColor);
            jtfBuildTset.setForeground(Color.black);
            jtfBuildTsetEnabled.value = true;
        }
    }

    private void jbApplyMapTsetActionPerformed(ActionEvent evt) {
        if (buildHandler.getAreaDataList() != null) {
            AreaDataDPPt areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            try {
                int value = Integer.parseInt(jtfMapTset.getText());
                areaData.setMapTilesetID(value);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            jtfMapTsetEnabled.value = false;
            jtfMapTset.setText(String.valueOf(areaData.getMapTilesetID()));
            jtfMapTset.setBackground(greenColor);
            jtfMapTset.setForeground(Color.black);
            jtfMapTsetEnabled.value = true;
        }
    }

    private void jbApplyUnknown1ActionPerformed(ActionEvent evt) {
        if (buildHandler.getAreaDataList() != null) {
            AreaDataDPPt areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            try {
                int value = Integer.parseInt(jtfUnknown1.getText());
                areaData.setUnknown1(value);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            jtfUnknown1Enabled.value = false;
            jtfUnknown1.setText(String.valueOf(areaData.getUnknown1()));
            jtfUnknown1.setBackground(greenColor);
            jtfUnknown1.setForeground(Color.black);
            jtfUnknown1Enabled.value = true;
        }
    }

    private void jcbAreaTypeActionPerformed(ActionEvent evt) {
        if (buildHandler.getAreaDataList() != null) {
            if (jcbAreaTypeEnabled.value) {
                AreaDataDPPt areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
                areaData.setAreaType(jcbAreaType.getSelectedIndex());
            }
        }
    }

    private void jbPlayActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelAnimeList() != null) {
            try {
                ArrayList<Integer> animIDs = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
                ModelAnimation anim = buildHandler.getBuildModelAnims().getAnimations().get(animIDs.get(jlSelectedAnimationsList.getSelectedIndex()));

                loadAnimationInNitroDisplay(nitroDisplayGL, 0, anim);


                /*
                if (anim.getAnimationType() == BuildAnimation.TYPE_NSBCA) {
                    NSBCAreader reader = new NSBCAreader(new ByteReader(anim.getData()));
                    nitroDisplayGL.getObjectGL(0).setNsbca((NSBCA) reader.readFile());
                    nitroDisplayGL.requestUpdate();
                } else if (anim.getAnimationType() == BuildAnimation.TYPE_NSBTA) {
                    NSBTAreader reader = new NSBTAreader(new ByteReader(anim.getData()));
                    nitroDisplayGL.getObjectGL(0).setNsbta((NSBTA) reader.readFile());
                    nitroDisplayGL.requestUpdate();
                } else if (anim.getAnimationType() == BuildAnimation.TYPE_NSBTP) {
                    NSBTPreader reader = new NSBTPreader(new ByteReader(anim.getData()));
                    nitroDisplayGL.getObjectGL(0).setNsbtp((NSBTP) reader.readFile());
                    nitroDisplayGL.requestUpdate();
                //} else if (anim.getAnimationType() == BuildAnimation.TYPE_NSBMA) {
                //    NSBMAreader reader = new NSBMAreader(new ByteReader(anim.getData()));
                //    nitroDisplayGL1.getHandler().setNsbma((NSBMA) reader.readFile());
                //    nitroDisplayGL1.requestUpdate();
                } else if (anim.getAnimationType() == BuildAnimation.TYPE_NSBVA) {
                    NSBVAreader reader = new NSBVAreader(new ByteReader(anim.getData()));
                    nitroDisplayGL1.getHandler().setNsbva((NSBVA) reader.readFile());
                    nitroDisplayGL1.requestUpdate();
                }
                */
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

    }

    private void jbImportMaterialsFromNsbmdActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null) {
            try {
                int buildIndex = jlBuildModel.getSelectedIndex();
                byte[] data = buildHandler.getBuildModelList().getModelsData().get(buildIndex);

                ArrayList<Integer> materials = BuildHandlerDPPt.getMaterialOrder(data);

                buildHandler.getBuildModelMatshp().setMaterials(buildIndex, materials);
                updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "There was a problem importing the material order from the NSBMD.",
                        "Can't import material order", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jlAreaBuildListValueChanged(ListSelectionEvent evt) {
        if (buildHandler.getAreaBuildList() != null) {
            if (jlAreaBuildListEnabled.value) {
                try {
                    AreaBuild areaBuild = buildHandler.getAreaBuildList().getAreaBuilds().get(jlBuildTsetList.getSelectedIndex());
                    int buildIndex = areaBuild.getBuildingIDs().get(jlAreaBuildList.getSelectedIndex());
                    byte[] data = buildHandler.getBuildModelList().getModelsData().get(buildIndex);
                    nitroDisplayAreaData.getObjectGL(0).setNsbmdData(data);
                } catch (Exception ex) {
                    nitroDisplayAreaData.getObjectGL(0).setNsbmdData(null);
                }
                try {
                    nitroDisplayAreaData.getObjectGL(0).setNsbca(null);
                    nitroDisplayAreaData.getObjectGL(0).setNsbtp(null);
                    nitroDisplayAreaData.getObjectGL(0).setNsbta(null);
                    nitroDisplayAreaData.getObjectGL(0).setNsbva(null);
                    nitroDisplayAreaData.fitCameraToModel(0);
                    nitroDisplayAreaData.requestUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void jbAddTexToNsbtxActionPerformed(ActionEvent evt) {
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
                    final byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "temp2");

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

    }

    private void jbAddTsetActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildTilesetList() != null && buildHandler.getAreaBuildList() != null) {

            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Add a new Building NSBTX");
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbReplaceTsetActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildTilesetList() != null && buildHandler.getAreaBuildList() != null) {

            final JFileChooser fc = new JFileChooser();
            if (handler.getLastBuildDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Replace the Building NSBTX");
            final int returnVal = fc.showOpenDialog(this);
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


    }

    private void jbExportTilesetActionPerformed(ActionEvent evt) {
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
                ex.printStackTrace();
            }
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbRemoveTsetActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jbRemoveTexturesActionPerformed(ActionEvent evt) {
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

                final byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "");

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
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this,
                    "There was a problem removing the textures and palettes.",
                    "Can't remove textures and palettes", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void jbAddEmptyTilesetActionPerformed(ActionEvent evt) {
        try {
            Nsbtx2 nsbtx = new Nsbtx2();
            BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            nsbtx.addTextureAndPalette(0, 0, img, Nsbtx2.FORMAT_COLOR_4, false, "placeholder", "placeholder_pl");
            final byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "temp");

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
    }

    private void jbSetAnimationActionPerformed(ActionEvent evt) {
        if (buildHandler.getBuildModelList() != null && buildHandler.getBuildModelMatshp() != null) {
            buildHandler.removeAllBuildingMaterials(jlBuildModel.getSelectedIndex());
            updateViewMaterialOrderList(jlMaterialOrder.getSelectedIndex());
        }
    }

    private void jbImportBldActionPerformed(ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBuildDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBuildDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("BLD (*.bld)", "bld"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Import Buildings File");
        final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbExportBldActionPerformed(ActionEvent evt) {
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
                ex.printStackTrace();
            }
            final int returnVal = fc.showOpenDialog(this);
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
    }

    private void jbAddBuildBldActionPerformed(ActionEvent evt) {
        if (handler.getBuildings() != null) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame());
            dialog.init(buildHandler.getBuildModelList(), null);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACCEPTED) {
                Build build = new Build();
                build.setModelID(dialog.getIndexSelected());
                handler.getBuildings().getBuilds().add(build);

                updateViewBuildFileList(handler.getBuildings().getBuilds().size() - 1);
                updateViewNitroDisplayMap();
            }
        }
    }

    private void jbRemoveBldActionPerformed(ActionEvent evt) {
        if (handler.getBuildings() != null) {
            if (handler.getBuildings().getBuilds().size() > 0) {
                int index = jlBuildFile.getSelectedIndex();
                handler.getBuildings().getBuilds().remove(index);

                updateViewBuildFileList(index);
                updateViewNitroDisplayMap();
            }
        }
    }

    private void jlBuildFileValueChanged(ListSelectionEvent evt) {
        if (jlBuildFileEnabled.value) {
            updateViewBuildProperties();
            setBoundingBoxes();
        }
    }

    private void jsBuildIDStateChanged(ChangeEvent evt) {
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setModelID((Integer) jsBuildID.getValue());

                updateViewBuildFileList(jlBuildFile.getSelectedIndex());
                updateViewNitroDisplayMap();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void jsBuildXStateChanged(ChangeEvent evt) {
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setX(((Double) jsBuildX.getValue()).floatValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void jsBuildYStateChanged(ChangeEvent evt) {
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setY(((Double) jsBuildY.getValue()).floatValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void jsBuildZStateChanged(ChangeEvent evt) {
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());

                build.setZ(((Double) jsBuildZ.getValue()).floatValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void jsBuildScaleXStateChanged(ChangeEvent evt) {
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setScaleX(((Double) jsBuildScaleX.getValue()).floatValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void jsBuildScaleYStateChanged(ChangeEvent evt) {
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setScaleY(((Double) jsBuildScaleY.getValue()).floatValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void jsBuildScaleZStateChanged(ChangeEvent evt) {
        if (buildPropertiesEnabled.value) {
            try {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setScaleZ(((Double) jsBuildScaleZ.getValue()).floatValue());

                updateViewNitroDisplayMapBuildProperties(jlBuildFile.getSelectedIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void jbChooseModelBldActionPerformed(ActionEvent evt) {
        if (handler.getBuildings().getBuilds().size() > 0) {
            final AddBuildModelDialog dialog = new AddBuildModelDialog(handler.getMainFrame());
            dialog.init(buildHandler.getBuildModelList(), null);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getReturnValue() == AddBuildModelDialog.ACCEPTED) {
                Build build = handler.getBuildings().getBuilds().get(jlBuildFile.getSelectedIndex());
                build.setModelID(dialog.getIndexSelected());

                updateViewBuildFileList(jlBuildFile.getSelectedIndex());
                updateViewNitroDisplayMap();
            }
        }
    }

    private void jbOpenMapActionPerformed(ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Map's NSBMD");
        final int returnVal = fc.showOpenDialog(this);
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

    }

    private void jbCancelActionPerformed(ActionEvent evt) {
        dispose();
    }

    private void jbRemoveAllUnusedTexPalsActionPerformed(ActionEvent evt) {
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
                    ex.printStackTrace();
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

            final byte[] newNsbtxData = NsbtxWriter.writeNsbtx(nsbtx, "");

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
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this,
                    "There was a problem removing the textures and palettes.",
                    "Can't remove textures and palettes", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void init(MapEditorHandler handler, BuildHandlerDPPt buildHandler) {
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
            ArrayList<Integer> animations = buildHandler.getBuildModelAnimeList().getAnimations().get(jlBuildModel.getSelectedIndex());
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

            int animType = buildHandler.getBuildModelAnimeList().getSecondBytes().get(jlBuildModel.getSelectedIndex());

            jcbAnimationType.setSelectedIndex(animType + 1);
            jcbAnimationTypeEnabled.value = true;

            boolean slopeAnim = buildHandler.getBuildModelAnimeList().getSlopeAnimations().get(jlBuildModel.getSelectedIndex()).booleanValue();
            jcbSlope.setSelected(slopeAnim);
        }
    }

    private void updateViewAreaDataList(int indexSelected) {
        if (buildHandler.getAreaDataList() != null) {
            addElementsToList(jlAreaDataList, jlAreaDataListEnabled, "Area Data", buildHandler.getAreaDataList().getAreaDatas().size(), indexSelected);
        }
    }

    private void updateViewAreaDataProperties() {
        if (buildHandler.getAreaDataList() != null) {
            AreaDataDPPt areaData = buildHandler.getAreaDataList().getAreaDatas().get(jlAreaDataList.getSelectedIndex());
            jtfBuildTsetEnabled.value = false;
            jtfMapTsetEnabled.value = false;
            jtfUnknown1Enabled.value = false;
            jcbAreaTypeEnabled.value = false;

            jtfBuildTset.setText(String.valueOf(areaData.getBuildingTilesetID()));
            jtfMapTset.setText(String.valueOf(areaData.getMapTilesetID()));
            jtfUnknown1.setText(String.valueOf(areaData.getUnknown1()));
            jcbAreaType.setSelectedIndex(Math.max(Math.min(1, areaData.getUnknown2()), 0));

            jtfBuildTset.setBackground(defaultTextPaneBackground);
            jtfMapTset.setBackground(defaultTextPaneBackground);
            jtfUnknown1.setBackground(defaultTextPaneBackground);
            jcbAreaType.setBackground(defaultTextPaneBackground);

            jtfBuildTsetEnabled.value = true;
            jtfMapTsetEnabled.value = true;
            jtfUnknown1Enabled.value = true;
            jcbAreaTypeEnabled.value = true;
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

    public void loadGame(String folderPath) {
        buildHandler.setGameFolderPath(folderPath);
        if (buildHandler.areAllFilesAvailable()) {
            try {
                buildHandler.loadAllFiles();

                updateViewBuildModelList(0);
                updateViewMaterialOrderList(0);
                updateViewSelectedBuildAnimationsList(0);
                updateViewAreaDataList(0);
                updateViewBuildTsetList(0);
                updateViewAreaBuildList(0);
                updateViewAnimationsList(0);
                nitroDisplayGL.requestUpdate();

                tryLoadBuildFile();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "There was a problem reading some of the files.",
                        "Error opening game files", JOptionPane.ERROR_MESSAGE);
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
            final int downLimitZero = Math.max(0, jlBuildFile.getSelectedIndex() + 1);
            final int index = Math.min(downLimitZero, jlBuildFile.getModel().getSize());
            nitroDisplayMap.getObjectsGL().get(index).setDrawBounds(true);
        } catch (Exception ex) {
            ex.printStackTrace();
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
                ex.printStackTrace();
            }

            try {
                for (Integer animIndex : buildHandler.getBuildModelAnimeList().getAnimations().get(build.getModeID())) {
                    ModelAnimation anim = buildHandler.getBuildModelAnims().getAnimations().get(animIndex);
                    loadAnimationInNitroDisplay(nitroDisplayMap, 1 + i, anim);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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

    /*
    public void updateViewNitroDisplayMap() {
        BuildFile buildFile = handler.getBuildings();

        for (int i = 1, size = nitroDisplayMap.getObjectsGL().size(); i < size; i++) {
            nitroDisplayMap.getObjectsGL().remove(nitroDisplayMap.getObjectsGL().size() - 1);
        }

        for (int i = 0; i < buildFile.getBuilds().size(); i++) {
            Build build = buildFile.getBuilds().get(i);

            byte[] data = buildHandler.getBuildModelList().getModelsData().get(build.getModeID());
            ObjectGL object;
            try {
                object = nitroDisplayMap.getObjectGL(1 + i);
            } catch (Exception ex) {
                nitroDisplayMap.getObjectsGL().add(new ObjectGL());
                object = nitroDisplayMap.getObjectGL(1 + i);
            }
            object.setNsbmdData(data);

            for (Integer animIndex : buildHandler.getBuildModelAnimeList().getAnimations().get(build.getModeID())) {
                BuildAnimation anim = buildHandler.getBuildModelAnims().getAnimations().get(animIndex);
                loadAnimationInNitroDisplay(nitroDisplayMap, 1 + i, anim);
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
    }*/

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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
        else if (anim.getAnimationType() == ModelAnimation.TYPE_NSBVA) {
            NSBVAreader reader = new NSBVAreader(new ByteReader(anim.getData()));
            display.getObjectGL(objectIndex).setNsbva((NSBVA) reader.readFile());
            display.requestUpdate();
        }
    }

    public void tryLoadBuildFile() {
        try {
            String filePath = handler.getMapMatrix().getFilePathWithCoords(handler.getMapMatrix().getMatrix(),
                    new File(handler.getMapMatrix().filePath).getParent(),
                    new File(handler.getMapMatrix().filePath).getName(),
                    handler.getMapSelected(), "nsbmd");

            File file = new File(filePath);
            if (file.exists()) {
                byte[] mapData = Files.readAllBytes(file.toPath());
                nitroDisplayMap.getObjectGL(0).setNsbmdData(mapData);
                nitroDisplayMap.requestUpdate();
            } else {
                File[] files = findFilesWithExtension(new File(handler.getMapMatrix().filePath).getParent(), "nsbmd");
                if (files != null && files.length > 0) {
                    byte[] mapData = Files.readAllBytes(files[0].toPath());
                    nitroDisplayMap.getObjectGL(0).setNsbmdData(mapData);
                    nitroDisplayMap.requestUpdate();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*
        try {
            File[] files = findFilesWithExtension(handler.getLastMapDirectoryUsed(), "nsbmd");
            if (files != null && files.length > 0) {

                byte[] mapData = Files.readAllBytes(files[0].toPath());
                nitroDisplayMap.getObjectGL(0).setNsbmdData(mapData);

                nitroDisplayMap.requestUpdate();
            }
        } catch (Exception ex) {
ex.printStackTrace();
}
        */
    }

    private void saveBuildings() throws IOException {
        File file = new File(handler.getMapMatrix().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + BuildFile.fileExtension;
        handler.getBuildings().saveToFile(path + File.separator + filename);
    }

    private static File[] findFilesWithExtension(String folderPath, String extension) {
        if (folderPath == null) {
            return null;
        }
        File dir = new File(folderPath);

        return dir.listFiles((dir1, filename) -> filename.endsWith(extension));
    }

    private static void addElementsToList(JList list, MutableBoolean listEnabled, String name, int numElements, int indexSelected) {
        listEnabled.value = false;
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < numElements; i++) {
            listModel.addElement(name + " " + i);
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
            listModel.addElement(i + ": " + elements.get(i));
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

    private void jcbSlopeActionPerformed(ActionEvent e) {
        buildHandler.getBuildModelAnimeList().getSlopeAnimations().set(jlBuildModel.getSelectedIndex(), jcbSlope.isSelected());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jTabbedPane1 = new JTabbedPane();
        jPanel3 = new JPanel();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jlBuildModel = new JList<>();
        jLabel4 = new JLabel();
        nitroDisplayGL = new NitroDisplayGL();
        panel2 = new JPanel();
        jbAddBuilding = new JButton();
        jbReplaceBuilding = new JButton();
        jbExportBuilding = new JButton();
        jbRemoveBuilding = new JButton();
        jbFindBuilding = new JButton();
        jPanel2 = new JPanel();
        jLabel2 = new JLabel();
        jScrollPane2 = new JScrollPane();
        jlMaterialOrder = new JList<>();
        panel3 = new JPanel();
        jbAddMaterial = new JButton();
        jbRemoveMaterial = new JButton();
        jbImportMaterialsFromNsbmd = new JButton();
        jbSetAnimation = new JButton();
        jbMoveMaterialUp = new JButton();
        jbMoveMaterialDown = new JButton();
        jPanel8 = new JPanel();
        jLabel3 = new JLabel();
        jScrollPane3 = new JScrollPane();
        jlSelectedAnimationsList = new JList<>();
        panel4 = new JPanel();
        jbAddAnimToBuild = new JButton();
        jbReplaceAnimToBuild = new JButton();
        jbRemoveAnimToBuild = new JButton();
        jLabel12 = new JLabel();
        jcbAnimationType = new JComboBox<>();
        jcbSlope = new JCheckBox();
        jbPlay = new JButton();
        jPanel4 = new JPanel();
        jPanel5 = new JPanel();
        jScrollPane4 = new JScrollPane();
        jlAreaDataList = new JList<>();
        jLabel1 = new JLabel();
        panel5 = new JPanel();
        jbAddAreaData = new JButton();
        jbRemoveAreaData = new JButton();
        jPanel6 = new JPanel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jtfUnknown1 = new JTextField();
        jtfMapTset = new JTextField();
        jtfBuildTset = new JTextField();
        jbApplyBuildTset = new JButton();
        jbApplyMapTset = new JButton();
        jbApplyUnknown1 = new JButton();
        jcbAreaType = new JComboBox<>();
        jPanel7 = new JPanel();
        jPanel10 = new JPanel();
        jLabel10 = new JLabel();
        nsbtxPanel = new NsbtxPanel();
        jScrollPane6 = new JScrollPane();
        jlBuildTsetList = new JList<>();
        panel6 = new JPanel();
        jbAddTset = new JButton();
        jbAddEmptyTileset = new JButton();
        jbReplaceTset = new JButton();
        jbExportTileset = new JButton();
        jbRemoveTset = new JButton();
        jPanel11 = new JPanel();
        jLabel9 = new JLabel();
        jScrollPane7 = new JScrollPane();
        jlAreaBuildList = new JList<>();
        panel7 = new JPanel();
        jbAddBuildToTset = new JButton();
        jbReplaceBuildToTset = new JButton();
        jbRemoveBuildToTset = new JButton();
        jbAddTexToNsbtx = new JButton();
        jbRemoveTextures = new JButton();
        jbRemoveAllUnusedTexPals = new JButton();
        nitroDisplayAreaData = new NitroDisplayGL();
        jPanel9 = new JPanel();
        jPanel12 = new JPanel();
        jLabel11 = new JLabel();
        jScrollPane5 = new JScrollPane();
        jlAnimationsList = new JList<>();
        panel8 = new JPanel();
        jbAddAnim = new JButton();
        jbReplaceAnim = new JButton();
        jbExportAnimation = new JButton();
        jbRemoveAnim = new JButton();
        jPanel13 = new JPanel();
        jPanel14 = new JPanel();
        nitroDisplayMap = new NitroDisplayGL();
        jbOpenMap = new JButton();
        jLabel21 = new JLabel();
        jPanel15 = new JPanel();
        jScrollPane8 = new JScrollPane();
        jlBuildFile = new JList<>();
        jPanel16 = new JPanel();
        jLabel13 = new JLabel();
        jsBuildID = new JSpinner();
        jbChooseModelBld = new JButton();
        jLabel14 = new JLabel();
        jsBuildX = new JSpinner();
        jLabel15 = new JLabel();
        jsBuildY = new JSpinner();
        jLabel16 = new JLabel();
        jsBuildZ = new JSpinner();
        jLabel17 = new JLabel();
        jsBuildScaleX = new JSpinner();
        jsBuildScaleY = new JSpinner();
        jLabel18 = new JLabel();
        jLabel19 = new JLabel();
        jsBuildScaleZ = new JSpinner();
        jLabel20 = new JLabel();
        jPanel17 = new JPanel();
        jPanel18 = new JPanel();
        jbImportBld = new JButton();
        jbExportBld = new JButton();
        jPanel19 = new JPanel();
        jbAddBuildBld = new JButton();
        jbRemoveBld = new JButton();
        panel1 = new JPanel();
        jbSaveAll = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Building Editor (Experimental)");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]",
            // rows
            "[grow,fill]" +
            "[]"));

        //======== jTabbedPane1 ========
        {

            //======== jPanel3 ========
            {
                jPanel3.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]" +
                    "[fill]",
                    // rows
                    "[grow,fill]" +
                    "[grow,fill]"));

                //======== jPanel1 ========
                {
                    jPanel1.setBorder(new TitledBorder("Building Selector (build_model.narc)"));
                    jPanel1.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[462,grow,fill]" +
                        "[175,fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[grow,fill]"));

                    //======== jScrollPane1 ========
                    {
                        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlBuildModel ----
                        jlBuildModel.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlBuildModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jlBuildModel.addListSelectionListener(e -> jlBuildModelValueChanged(e));
                        jScrollPane1.setViewportView(jlBuildModel);
                    }
                    jPanel1.add(jScrollPane1, "cell 1 1");

                    //---- jLabel4 ----
                    jLabel4.setIcon(new ImageIcon(getClass().getResource("/icons/BuildingIcon.png")));
                    jLabel4.setText("Building List:");
                    jLabel4.setToolTipText("");
                    jPanel1.add(jLabel4, "cell 1 0");

                    //======== nitroDisplayGL ========
                    {
                        nitroDisplayGL.setBorder(new LineBorder(new Color(102, 102, 102)));

                        GroupLayout nitroDisplayGLLayout = new GroupLayout(nitroDisplayGL);
                        nitroDisplayGL.setLayout(nitroDisplayGLLayout);
                        nitroDisplayGLLayout.setHorizontalGroup(
                            nitroDisplayGLLayout.createParallelGroup()
                                .addGap(0, 425, Short.MAX_VALUE)
                        );
                        nitroDisplayGLLayout.setVerticalGroup(
                            nitroDisplayGLLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                        );
                    }
                    jPanel1.add(nitroDisplayGL, "cell 0 0 1 2,gapx 5 5,gapy 5 5");

                    //======== panel2 ========
                    {
                        panel2.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- jbAddBuilding ----
                        jbAddBuilding.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddBuilding.setText("Add Building");
                        jbAddBuilding.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddBuilding.addActionListener(e -> jbAddBuildingActionPerformed(e));
                        panel2.add(jbAddBuilding, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbReplaceBuilding ----
                        jbReplaceBuilding.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                        jbReplaceBuilding.setText("Replace Building");
                        jbReplaceBuilding.setHorizontalAlignment(SwingConstants.LEFT);
                        jbReplaceBuilding.addActionListener(e -> jbReplaceBuildingActionPerformed(e));
                        panel2.add(jbReplaceBuilding, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbExportBuilding ----
                        jbExportBuilding.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
                        jbExportBuilding.setText("Export Building");
                        jbExportBuilding.setHorizontalAlignment(SwingConstants.LEFT);
                        jbExportBuilding.addActionListener(e -> jbExportBuildingActionPerformed(e));
                        panel2.add(jbExportBuilding, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveBuilding ----
                        jbRemoveBuilding.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveBuilding.setText("Remove Building");
                        jbRemoveBuilding.setEnabled(false);
                        jbRemoveBuilding.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveBuilding.addActionListener(e -> jbRemoveBuildingActionPerformed(e));
                        panel2.add(jbRemoveBuilding, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbFindBuilding ----
                        jbFindBuilding.setIcon(new ImageIcon(getClass().getResource("/icons/SearchIcon.png")));
                        jbFindBuilding.setText("Find Usages");
                        jbFindBuilding.setHorizontalAlignment(SwingConstants.LEFT);
                        jbFindBuilding.addActionListener(e -> jbFindBuildingActionPerformed(e));
                        panel2.add(jbFindBuilding, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    jPanel1.add(panel2, "cell 2 1");
                }
                jPanel3.add(jPanel1, "cell 0 0 1 2,gapx 5 5,gapy 5 5");

                //======== jPanel2 ========
                {
                    jPanel2.setBorder(new TitledBorder("Selected Building Properties (build_model_matshp.dat)"));
                    jPanel2.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[132,grow,fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[grow,fill]"));

                    //---- jLabel2 ----
                    jLabel2.setIcon(new ImageIcon(getClass().getResource("/icons/MaterialIcon2.png")));
                    jLabel2.setText("Material order:");
                    jLabel2.setToolTipText("");
                    jPanel2.add(jLabel2, "cell 0 0");

                    //======== jScrollPane2 ========
                    {
                        jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlMaterialOrder ----
                        jlMaterialOrder.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlMaterialOrder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jScrollPane2.setViewportView(jlMaterialOrder);
                    }
                    jPanel2.add(jScrollPane2, "cell 0 1");

                    //======== panel3 ========
                    {
                        panel3.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- jbAddMaterial ----
                        jbAddMaterial.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddMaterial.setText("Add Material");
                        jbAddMaterial.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddMaterial.addActionListener(e -> jbAddMaterialActionPerformed(e));
                        panel3.add(jbAddMaterial, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveMaterial ----
                        jbRemoveMaterial.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveMaterial.setText("Remove Material");
                        jbRemoveMaterial.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveMaterial.addActionListener(e -> jbRemoveMaterialActionPerformed(e));
                        panel3.add(jbRemoveMaterial, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbImportMaterialsFromNsbmd ----
                        jbImportMaterialsFromNsbmd.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                        jbImportMaterialsFromNsbmd.setText("Import from NSBMD");
                        jbImportMaterialsFromNsbmd.setHorizontalAlignment(SwingConstants.LEFT);
                        jbImportMaterialsFromNsbmd.addActionListener(e -> jbImportMaterialsFromNsbmdActionPerformed(e));
                        panel3.add(jbImportMaterialsFromNsbmd, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbSetAnimation ----
                        jbSetAnimation.setIcon(new ImageIcon(getClass().getResource("/icons/AnimationIcon.png")));
                        jbSetAnimation.setText("Set Animation");
                        jbSetAnimation.setHorizontalAlignment(SwingConstants.LEFT);
                        jbSetAnimation.addActionListener(e -> jbSetAnimationActionPerformed(e));
                        panel3.add(jbSetAnimation, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbMoveMaterialUp ----
                        jbMoveMaterialUp.setText("\u25b2");
                        jbMoveMaterialUp.addActionListener(e -> jbMoveMaterialUpActionPerformed(e));
                        panel3.add(jbMoveMaterialUp, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- jbMoveMaterialDown ----
                        jbMoveMaterialDown.setText("\u25bc");
                        jbMoveMaterialDown.addActionListener(e -> jbMoveMaterialDownActionPerformed(e));
                        panel3.add(jbMoveMaterialDown, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    jPanel2.add(panel3, "cell 1 1");
                }
                jPanel3.add(jPanel2, "cell 1 0,gapx 5 5,gapy 5 5");

                //======== jPanel8 ========
                {
                    jPanel8.setBorder(new TitledBorder("Selected Building Animations (bm_anime_list.narc)"));
                    jPanel8.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[168,fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[grow,fill]"));

                    //---- jLabel3 ----
                    jLabel3.setIcon(new ImageIcon(getClass().getResource("/icons/AnimationIcon.png")));
                    jLabel3.setText("Animations:");
                    jLabel3.setToolTipText("");
                    jPanel8.add(jLabel3, "cell 0 0");

                    //======== jScrollPane3 ========
                    {
                        jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlSelectedAnimationsList ----
                        jlSelectedAnimationsList.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlSelectedAnimationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jScrollPane3.setViewportView(jlSelectedAnimationsList);
                    }
                    jPanel8.add(jScrollPane3, "cell 0 1");

                    //======== panel4 ========
                    {
                        panel4.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- jbAddAnimToBuild ----
                        jbAddAnimToBuild.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddAnimToBuild.setText("Add Animation");
                        jbAddAnimToBuild.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddAnimToBuild.addActionListener(e -> jbAddAnimToBuildActionPerformed(e));
                        panel4.add(jbAddAnimToBuild, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbReplaceAnimToBuild ----
                        jbReplaceAnimToBuild.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                        jbReplaceAnimToBuild.setText("Replace Animation");
                        jbReplaceAnimToBuild.setHorizontalAlignment(SwingConstants.LEFT);
                        jbReplaceAnimToBuild.addActionListener(e -> jbReplaceAnimToBuildActionPerformed(e));
                        panel4.add(jbReplaceAnimToBuild, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveAnimToBuild ----
                        jbRemoveAnimToBuild.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveAnimToBuild.setText("Remove Animation");
                        jbRemoveAnimToBuild.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveAnimToBuild.addActionListener(e -> jbRemoveAnimToBuildActionPerformed(e));
                        panel4.add(jbRemoveAnimToBuild, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jLabel12 ----
                        jLabel12.setText("Type:");
                        panel4.add(jLabel12, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- jcbAnimationType ----
                        jcbAnimationType.setModel(new DefaultComboBoxModel<>(new String[] {
                            "Disabled",
                            "Loop",
                            "Unknown",
                            "Trigger (?)",
                            "Trigger"
                        }));
                        jcbAnimationType.addActionListener(e -> jcbAnimationTypeActionPerformed(e));
                        panel4.add(jcbAnimationType, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jcbSlope ----
                        jcbSlope.setText("Slope Animation (?)");
                        jcbSlope.addActionListener(e -> jcbSlopeActionPerformed(e));
                        panel4.add(jcbSlope, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbPlay ----
                        jbPlay.setIcon(new ImageIcon(getClass().getResource("/icons/AnimationIcon.png")));
                        jbPlay.setText("Play Animation");
                        jbPlay.setHorizontalAlignment(SwingConstants.LEFT);
                        jbPlay.addActionListener(e -> jbPlayActionPerformed(e));
                        panel4.add(jbPlay, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    jPanel8.add(panel4, "cell 1 1");
                }
                jPanel3.add(jPanel8, "cell 1 1,gapx 5 5,gapy 5 5");
            }
            jTabbedPane1.addTab("Building Editor", jPanel3);

            //======== jPanel4 ========
            {
                jPanel4.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]" +
                    "[grow,fill]" +
                    "[450,fill]",
                    // rows
                    "[grow,fill]"));

                //======== jPanel5 ========
                {
                    jPanel5.setBorder(new TitledBorder("Area Data Selector (area_data.narc)"));
                    jPanel5.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[154,grow,fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[grow,fill]"));

                    //======== jScrollPane4 ========
                    {
                        jScrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlAreaDataList ----
                        jlAreaDataList.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlAreaDataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jlAreaDataList.addListSelectionListener(e -> jlAreaDataListValueChanged(e));
                        jScrollPane4.setViewportView(jlAreaDataList);
                    }
                    jPanel5.add(jScrollPane4, "cell 0 1");

                    //---- jLabel1 ----
                    jLabel1.setIcon(new ImageIcon(getClass().getResource("/icons/AreaDataIcon.png")));
                    jLabel1.setText("Area Data List:");
                    jPanel5.add(jLabel1, "cell 0 0");

                    //======== panel5 ========
                    {
                        panel5.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                        ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                        //---- jbAddAreaData ----
                        jbAddAreaData.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddAreaData.setText("Add Area Data");
                        jbAddAreaData.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddAreaData.addActionListener(e -> jbAddAreaDataActionPerformed(e));
                        panel5.add(jbAddAreaData, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveAreaData ----
                        jbRemoveAreaData.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveAreaData.setText("Remove Area Data");
                        jbRemoveAreaData.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveAreaData.addActionListener(e -> jbRemoveAreaDataActionPerformed(e));
                        panel5.add(jbRemoveAreaData, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    jPanel5.add(panel5, "cell 1 1");
                }
                jPanel4.add(jPanel5, "cell 0 0,gapx 5 5,gapy 5 5");

                //======== jPanel6 ========
                {
                    jPanel6.setBorder(new TitledBorder("Area Data Properties"));
                    jPanel6.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[fill]" +
                        "[grow,fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]"));

                    //---- jLabel5 ----
                    jLabel5.setText("Building Tileset:");
                    jPanel6.add(jLabel5, "cell 0 0");

                    //---- jLabel6 ----
                    jLabel6.setText("Map Tileset:");
                    jPanel6.add(jLabel6, "cell 0 1");

                    //---- jLabel7 ----
                    jLabel7.setText("Unknown 1:");
                    jPanel6.add(jLabel7, "cell 0 2");

                    //---- jLabel8 ----
                    jLabel8.setText("Area Type:");
                    jPanel6.add(jLabel8, "cell 0 3");
                    jPanel6.add(jtfUnknown1, "cell 1 2");
                    jPanel6.add(jtfMapTset, "cell 1 1");
                    jPanel6.add(jtfBuildTset, "cell 1 0");

                    //---- jbApplyBuildTset ----
                    jbApplyBuildTset.setText("Apply");
                    jbApplyBuildTset.addActionListener(e -> jbApplyBuildTsetActionPerformed(e));
                    jPanel6.add(jbApplyBuildTset, "cell 2 0");

                    //---- jbApplyMapTset ----
                    jbApplyMapTset.setText("Apply");
                    jbApplyMapTset.addActionListener(e -> jbApplyMapTsetActionPerformed(e));
                    jPanel6.add(jbApplyMapTset, "cell 2 1");

                    //---- jbApplyUnknown1 ----
                    jbApplyUnknown1.setText("Apply");
                    jbApplyUnknown1.addActionListener(e -> jbApplyUnknown1ActionPerformed(e));
                    jPanel6.add(jbApplyUnknown1, "cell 2 2");

                    //---- jcbAreaType ----
                    jcbAreaType.setModel(new DefaultComboBoxModel<>(new String[] {
                        "Outdoor Area",
                        "Indoor Area"
                    }));
                    jcbAreaType.addActionListener(e -> jcbAreaTypeActionPerformed(e));
                    jPanel6.add(jcbAreaType, "cell 1 3 2 1");
                }
                jPanel4.add(jPanel6, "cell 1 0,gapx 5 5,gapy 5 5");
            }
            jTabbedPane1.addTab("Area Data Editor", jPanel4);

            //======== jPanel7 ========
            {
                jPanel7.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[fill]" +
                    "[grow,fill]",
                    // rows
                    "[grow,fill]"));

                //======== jPanel10 ========
                {
                    jPanel10.setBorder(new TitledBorder("Building Tileset Selector (areabm_texset.narc)"));
                    jPanel10.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[328,shrink 0,fill]" +
                        "[150,fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[fill]" +
                        "[grow,fill]"));

                    //---- jLabel10 ----
                    jLabel10.setIcon(new ImageIcon(getClass().getResource("/icons/MaterialIcon.png")));
                    jLabel10.setText("Building Tileset List:");
                    jPanel10.add(jLabel10, "cell 1 0 2 1");
                    jPanel10.add(nsbtxPanel, "cell 0 0 1 3");

                    //======== jScrollPane6 ========
                    {
                        jScrollPane6.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlBuildTsetList ----
                        jlBuildTsetList.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlBuildTsetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jlBuildTsetList.addListSelectionListener(e -> jlBuildTsetListValueChanged(e));
                        jScrollPane6.setViewportView(jlBuildTsetList);
                    }
                    jPanel10.add(jScrollPane6, "cell 1 1 1 2");

                    //======== panel6 ========
                    {
                        panel6.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                        ((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- jbAddTset ----
                        jbAddTset.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddTset.setText("Add Tileset");
                        jbAddTset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddTset.addActionListener(e -> jbAddTsetActionPerformed(e));
                        panel6.add(jbAddTset, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbAddEmptyTileset ----
                        jbAddEmptyTileset.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddEmptyTileset.setText("Add Empty Tileset");
                        jbAddEmptyTileset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddEmptyTileset.addActionListener(e -> jbAddEmptyTilesetActionPerformed(e));
                        panel6.add(jbAddEmptyTileset, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbReplaceTset ----
                        jbReplaceTset.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                        jbReplaceTset.setText("Replace Tileset");
                        jbReplaceTset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbReplaceTset.addActionListener(e -> jbReplaceTsetActionPerformed(e));
                        panel6.add(jbReplaceTset, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbExportTileset ----
                        jbExportTileset.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
                        jbExportTileset.setText("Export Tileset");
                        jbExportTileset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbExportTileset.addActionListener(e -> jbExportTilesetActionPerformed(e));
                        panel6.add(jbExportTileset, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveTset ----
                        jbRemoveTset.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveTset.setText("Remove Tileset");
                        jbRemoveTset.setEnabled(false);
                        jbRemoveTset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveTset.addActionListener(e -> jbRemoveTsetActionPerformed(e));
                        panel6.add(jbRemoveTset, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    jPanel10.add(panel6, "cell 2 1 1 2");
                }
                jPanel7.add(jPanel10, "cell 0 0");

                //======== jPanel11 ========
                {
                    jPanel11.setBorder(new TitledBorder("Building Tileset Properties (area_build.narc)"));
                    jPanel11.setLayout(new MigLayout(
                        "insets 0,hidemode 3,gap 5 5",
                        // columns
                        "[150,fill]" +
                        "[grow,fill]",
                        // rows
                        "[fill]" +
                        "[fill]" +
                        "[grow,fill]"));

                    //---- jLabel9 ----
                    jLabel9.setIcon(new ImageIcon(getClass().getResource("/icons/BuildingIcon.png")));
                    jLabel9.setText("Buildings used by the Tileset:");
                    jPanel11.add(jLabel9, "cell 0 0 2 1");

                    //======== jScrollPane7 ========
                    {
                        jScrollPane7.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlAreaBuildList ----
                        jlAreaBuildList.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlAreaBuildList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jlAreaBuildList.addListSelectionListener(e -> jlAreaBuildListValueChanged(e));
                        jScrollPane7.setViewportView(jlAreaBuildList);
                    }
                    jPanel11.add(jScrollPane7, "cell 0 1 1 2");

                    //======== panel7 ========
                    {
                        panel7.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel7.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)panel7.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)panel7.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                        ((GridBagLayout)panel7.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- jbAddBuildToTset ----
                        jbAddBuildToTset.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddBuildToTset.setText("Add Building");
                        jbAddBuildToTset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddBuildToTset.addActionListener(e -> jbAddBuildToTsetActionPerformed(e));
                        panel7.add(jbAddBuildToTset, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbReplaceBuildToTset ----
                        jbReplaceBuildToTset.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                        jbReplaceBuildToTset.setText("Replace Building");
                        jbReplaceBuildToTset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbReplaceBuildToTset.addActionListener(e -> jbReplaceBuildToTsetActionPerformed(e));
                        panel7.add(jbReplaceBuildToTset, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveBuildToTset ----
                        jbRemoveBuildToTset.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveBuildToTset.setText("Remove Building");
                        jbRemoveBuildToTset.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveBuildToTset.addActionListener(e -> jbRemoveBuildToTsetActionPerformed(e));
                        panel7.add(jbRemoveBuildToTset, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbAddTexToNsbtx ----
                        jbAddTexToNsbtx.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddTexToNsbtx.setText("Add Texs & Pals to NSBTX");
                        jbAddTexToNsbtx.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddTexToNsbtx.addActionListener(e -> jbAddTexToNsbtxActionPerformed(e));
                        panel7.add(jbAddTexToNsbtx, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveTextures ----
                        jbRemoveTextures.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveTextures.setText("Remove Tex & Pals from NSBTX");
                        jbRemoveTextures.setToolTipText("");
                        jbRemoveTextures.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveTextures.addActionListener(e -> jbRemoveTexturesActionPerformed(e));
                        panel7.add(jbRemoveTextures, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveAllUnusedTexPals ----
                        jbRemoveAllUnusedTexPals.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveAllUnusedTexPals.setText("Removed All Unused Tex & Pals");
                        jbRemoveAllUnusedTexPals.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveAllUnusedTexPals.addActionListener(e -> jbRemoveAllUnusedTexPalsActionPerformed(e));
                        panel7.add(jbRemoveAllUnusedTexPals, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    jPanel11.add(panel7, "cell 1 1");

                    //======== nitroDisplayAreaData ========
                    {
                        nitroDisplayAreaData.setBorder(new LineBorder(new Color(102, 102, 102)));

                        GroupLayout nitroDisplayAreaDataLayout = new GroupLayout(nitroDisplayAreaData);
                        nitroDisplayAreaData.setLayout(nitroDisplayAreaDataLayout);
                        nitroDisplayAreaDataLayout.setHorizontalGroup(
                            nitroDisplayAreaDataLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                        );
                        nitroDisplayAreaDataLayout.setVerticalGroup(
                            nitroDisplayAreaDataLayout.createParallelGroup()
                                .addGap(0, 290, Short.MAX_VALUE)
                        );
                    }
                    jPanel11.add(nitroDisplayAreaData, "cell 1 2");
                }
                jPanel7.add(jPanel11, "cell 1 0");
            }
            jTabbedPane1.addTab("Building Tileset Editor", jPanel7);

            //======== jPanel9 ========
            {
                jPanel9.setLayout(new MigLayout(
                    "insets 0,hidemode 3",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[grow,fill]"));

                //======== jPanel12 ========
                {
                    jPanel12.setBorder(new TitledBorder("Selected Building Animations (bm_anime.narc)"));
                    jPanel12.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[grow,fill]"));

                    //---- jLabel11 ----
                    jLabel11.setIcon(new ImageIcon(getClass().getResource("/icons/AnimationIcon.png")));
                    jLabel11.setText("Animation List:");
                    jLabel11.setToolTipText("");
                    jPanel12.add(jLabel11, "cell 0 0");

                    //======== jScrollPane5 ========
                    {
                        jScrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlAnimationsList ----
                        jlAnimationsList.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlAnimationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jScrollPane5.setViewportView(jlAnimationsList);
                    }
                    jPanel12.add(jScrollPane5, "cell 0 1");

                    //======== panel8 ========
                    {
                        panel8.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel8.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)panel8.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)panel8.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                        ((GridBagLayout)panel8.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- jbAddAnim ----
                        jbAddAnim.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                        jbAddAnim.setText("Add Animation");
                        jbAddAnim.setHorizontalAlignment(SwingConstants.LEFT);
                        jbAddAnim.addActionListener(e -> jbAddAnimActionPerformed(e));
                        panel8.add(jbAddAnim, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbReplaceAnim ----
                        jbReplaceAnim.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                        jbReplaceAnim.setText("Replace Animation");
                        jbReplaceAnim.setHorizontalAlignment(SwingConstants.LEFT);
                        jbReplaceAnim.addActionListener(e -> jbReplaceAnimActionPerformed(e));
                        panel8.add(jbReplaceAnim, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbExportAnimation ----
                        jbExportAnimation.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
                        jbExportAnimation.setText("Export Animation");
                        jbExportAnimation.setHorizontalAlignment(SwingConstants.LEFT);
                        jbExportAnimation.addActionListener(e -> jbExportAnimationActionPerformed(e));
                        panel8.add(jbExportAnimation, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbRemoveAnim ----
                        jbRemoveAnim.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                        jbRemoveAnim.setText("Remove Animation");
                        jbRemoveAnim.setEnabled(false);
                        jbRemoveAnim.setHorizontalAlignment(SwingConstants.LEFT);
                        jbRemoveAnim.addActionListener(e -> jbRemoveAnimActionPerformed(e));
                        panel8.add(jbRemoveAnim, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    jPanel12.add(panel8, "cell 1 1");
                }
                jPanel9.add(jPanel12, "cell 0 0,gapx 5 5,gapy 5 5");
            }
            jTabbedPane1.addTab("Building Animation Editor", jPanel9);

            //======== jPanel13 ========
            {
                jPanel13.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]" +
                    "[grow,fill]",
                    // rows
                    "[grow,fill]"));

                //======== jPanel14 ========
                {
                    jPanel14.setBorder(new TitledBorder("Map Display"));
                    jPanel14.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[fill]" +
                        "[grow,fill]",
                        // rows
                        "[fill]" +
                        "[grow,fill]"));

                    //======== nitroDisplayMap ========
                    {
                        nitroDisplayMap.setBorder(new LineBorder(new Color(102, 102, 102)));

                        GroupLayout nitroDisplayMapLayout = new GroupLayout(nitroDisplayMap);
                        nitroDisplayMap.setLayout(nitroDisplayMapLayout);
                        nitroDisplayMapLayout.setHorizontalGroup(
                            nitroDisplayMapLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                        );
                        nitroDisplayMapLayout.setVerticalGroup(
                            nitroDisplayMapLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                        );
                    }
                    jPanel14.add(nitroDisplayMap, "cell 0 1 2 1,growy");

                    //---- jbOpenMap ----
                    jbOpenMap.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                    jbOpenMap.setText("Open Map");
                    jbOpenMap.setHorizontalAlignment(SwingConstants.LEFT);
                    jbOpenMap.addActionListener(e -> jbOpenMapActionPerformed(e));
                    jPanel14.add(jbOpenMap, "cell 0 0");

                    //---- jLabel21 ----
                    jLabel21.setText("*[Note: This map is used as a visual help for placing the buildings easily]");
                    jPanel14.add(jLabel21, "cell 1 0");
                }
                jPanel13.add(jPanel14, "cell 0 0,gapx 5 5,gapy 5 5");

                //======== jPanel15 ========
                {
                    jPanel15.setBorder(new TitledBorder("Building Editor (*.bld)"));
                    jPanel15.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[185,grow,fill]" +
                        "[grow,fill]",
                        // rows
                        "[fill]" +
                        "[grow,fill]"));

                    //======== jScrollPane8 ========
                    {
                        jScrollPane8.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- jlBuildFile ----
                        jlBuildFile.setModel(new AbstractListModel<String>() {
                            String[] values = {

                            };
                            @Override
                            public int getSize() { return values.length; }
                            @Override
                            public String getElementAt(int i) { return values[i]; }
                        });
                        jlBuildFile.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jlBuildFile.addListSelectionListener(e -> jlBuildFileValueChanged(e));
                        jScrollPane8.setViewportView(jlBuildFile);
                    }
                    jPanel15.add(jScrollPane8, "cell 0 0 1 2");

                    //======== jPanel16 ========
                    {
                        jPanel16.setBorder(new TitledBorder("Selected Building"));
                        jPanel16.setLayout(new MigLayout(
                            "insets 5,hidemode 3,gap 5 5",
                            // columns
                            "[fill]" +
                            "[103,grow,fill]" +
                            "[fill]" +
                            "[grow,fill]",
                            // rows
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]"));

                        //---- jLabel13 ----
                        jLabel13.setText("Building ID:");
                        jPanel16.add(jLabel13, "cell 0 0");

                        //---- jsBuildID ----
                        jsBuildID.setModel(new SpinnerNumberModel(0, 0, null, 1));
                        jsBuildID.addChangeListener(e -> jsBuildIDStateChanged(e));
                        jPanel16.add(jsBuildID, "cell 1 0");

                        //---- jbChooseModelBld ----
                        jbChooseModelBld.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                        jbChooseModelBld.setText("Change Model");
                        jbChooseModelBld.setHorizontalAlignment(SwingConstants.LEFT);
                        jbChooseModelBld.addActionListener(e -> jbChooseModelBldActionPerformed(e));
                        jPanel16.add(jbChooseModelBld, "cell 3 0");

                        //---- jLabel14 ----
                        jLabel14.setForeground(new Color(204, 0, 0));
                        jLabel14.setText("X: ");
                        jPanel16.add(jLabel14, "cell 0 1");

                        //---- jsBuildX ----
                        jsBuildX.setModel(new SpinnerNumberModel(0.0F, -16.0F, 15.0F, 1.0F));
                        jsBuildX.addChangeListener(e -> jsBuildXStateChanged(e));
                        jPanel16.add(jsBuildX, "cell 1 1");

                        //---- jLabel15 ----
                        jLabel15.setForeground(new Color(51, 153, 0));
                        jLabel15.setText("Y: ");
                        jPanel16.add(jLabel15, "cell 0 2");

                        //---- jsBuildY ----
                        jsBuildY.setModel(new SpinnerNumberModel(0.0F, -16.0F, 15.0F, 1.0F));
                        jsBuildY.addChangeListener(e -> jsBuildYStateChanged(e));
                        jPanel16.add(jsBuildY, "cell 1 2");

                        //---- jLabel16 ----
                        jLabel16.setForeground(new Color(0, 0, 204));
                        jLabel16.setText("Z: ");
                        jPanel16.add(jLabel16, "cell 0 3");

                        //---- jsBuildZ ----
                        jsBuildZ.setModel(new SpinnerNumberModel(0.0F, -16.0F, 15.0F, 1.0F));
                        jsBuildZ.addChangeListener(e -> jsBuildZStateChanged(e));
                        jPanel16.add(jsBuildZ, "cell 1 3");

                        //---- jLabel17 ----
                        jLabel17.setForeground(new Color(204, 0, 0));
                        jLabel17.setText("Scale X: ");
                        jPanel16.add(jLabel17, "cell 2 1");

                        //---- jsBuildScaleX ----
                        jsBuildScaleX.setModel(new SpinnerNumberModel(0.0F, 0.0F, 15.0F, 1.0F));
                        jsBuildScaleX.addChangeListener(e -> jsBuildScaleXStateChanged(e));
                        jPanel16.add(jsBuildScaleX, "cell 3 1");

                        //---- jsBuildScaleY ----
                        jsBuildScaleY.setModel(new SpinnerNumberModel(0.0F, 0.0F, 15.0F, 1.0F));
                        jsBuildScaleY.addChangeListener(e -> jsBuildScaleYStateChanged(e));
                        jPanel16.add(jsBuildScaleY, "cell 3 2");

                        //---- jLabel18 ----
                        jLabel18.setForeground(new Color(0, 153, 0));
                        jLabel18.setText("Scale Y: ");
                        jPanel16.add(jLabel18, "cell 2 2");

                        //---- jLabel19 ----
                        jLabel19.setForeground(new Color(0, 0, 204));
                        jLabel19.setText("Scale Z: ");
                        jPanel16.add(jLabel19, "cell 2 3");

                        //---- jsBuildScaleZ ----
                        jsBuildScaleZ.setModel(new SpinnerNumberModel(0.0F, 0.0F, 15.0F, 1.0F));
                        jsBuildScaleZ.addChangeListener(e -> jsBuildScaleZStateChanged(e));
                        jPanel16.add(jsBuildScaleZ, "cell 3 3");

                        //---- jLabel20 ----
                        jLabel20.setText("*[Note: axis are rotated compared to map editor's axis]");
                        jPanel16.add(jLabel20, "cell 0 4 4 1");
                    }
                    jPanel15.add(jPanel16, "cell 1 1,gapx 5 5,gapy 5 5");

                    //======== jPanel17 ========
                    {
                        jPanel17.setBorder(new TitledBorder("Building File"));
                        jPanel17.setLayout(new GridLayout(2, 2, 5, 5));

                        //======== jPanel18 ========
                        {
                            jPanel18.setLayout(new GridLayout(1, 0, 5, 5));

                            //---- jbImportBld ----
                            jbImportBld.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                            jbImportBld.setText("Import BLD File");
                            jbImportBld.setToolTipText("");
                            jbImportBld.addActionListener(e -> jbImportBldActionPerformed(e));
                            jPanel18.add(jbImportBld);

                            //---- jbExportBld ----
                            jbExportBld.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
                            jbExportBld.setText("Export BLD File");
                            jbExportBld.addActionListener(e -> jbExportBldActionPerformed(e));
                            jPanel18.add(jbExportBld);
                        }
                        jPanel17.add(jPanel18);

                        //======== jPanel19 ========
                        {
                            jPanel19.setLayout(new GridLayout(1, 0, 5, 5));

                            //---- jbAddBuildBld ----
                            jbAddBuildBld.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                            jbAddBuildBld.setText("Add Building");
                            jbAddBuildBld.setToolTipText("");
                            jbAddBuildBld.addActionListener(e -> jbAddBuildBldActionPerformed(e));
                            jPanel19.add(jbAddBuildBld);

                            //---- jbRemoveBld ----
                            jbRemoveBld.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                            jbRemoveBld.setText("Remove Building");
                            jbRemoveBld.addActionListener(e -> jbRemoveBldActionPerformed(e));
                            jPanel19.add(jbRemoveBld);
                        }
                        jPanel17.add(jPanel19);
                    }
                    jPanel15.add(jPanel17, "cell 1 0,gapx 5 5,gapy 5 5");
                }
                jPanel13.add(jPanel15, "cell 1 0,gapx 5 5,gapy 5 5");
            }
            jTabbedPane1.addTab("Map Buildings Editor", jPanel13);
        }
        contentPane.add(jTabbedPane1, "cell 0 0");

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "insets 5,hidemode 3,gap 5 5",
                // columns
                "[grow,fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[grow,fill]"));

            //---- jbSaveAll ----
            jbSaveAll.setText("Save All");
            jbSaveAll.setPreferredSize(new Dimension(100, 0));
            jbSaveAll.setMinimumSize(null);
            jbSaveAll.setMaximumSize(null);
            jbSaveAll.setIcon(new ImageIcon(getClass().getResource("/icons/saveMapIconSmall.png")));
            jbSaveAll.addActionListener(e -> jbSaveAllActionPerformed(e));
            panel1.add(jbSaveAll, "cell 1 0");

            //---- jbCancel ----
            jbCancel.setText("Close");
            jbCancel.setMaximumSize(null);
            jbCancel.setMinimumSize(null);
            jbCancel.setPreferredSize(new Dimension(100, 0));
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
            panel1.add(jbCancel, "cell 2 0");
        }
        contentPane.add(panel1, "cell 0 1,gapx 5 5,gapy 5 5");
        setSize(1200, 660);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane jTabbedPane1;
    private JPanel jPanel3;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> jlBuildModel;
    private JLabel jLabel4;
    private NitroDisplayGL nitroDisplayGL;
    private JPanel panel2;
    private JButton jbAddBuilding;
    private JButton jbReplaceBuilding;
    private JButton jbExportBuilding;
    private JButton jbRemoveBuilding;
    private JButton jbFindBuilding;
    private JPanel jPanel2;
    private JLabel jLabel2;
    private JScrollPane jScrollPane2;
    private JList<String> jlMaterialOrder;
    private JPanel panel3;
    private JButton jbAddMaterial;
    private JButton jbRemoveMaterial;
    private JButton jbImportMaterialsFromNsbmd;
    private JButton jbSetAnimation;
    private JButton jbMoveMaterialUp;
    private JButton jbMoveMaterialDown;
    private JPanel jPanel8;
    private JLabel jLabel3;
    private JScrollPane jScrollPane3;
    private JList<String> jlSelectedAnimationsList;
    private JPanel panel4;
    private JButton jbAddAnimToBuild;
    private JButton jbReplaceAnimToBuild;
    private JButton jbRemoveAnimToBuild;
    private JLabel jLabel12;
    private JComboBox<String> jcbAnimationType;
    private JCheckBox jcbSlope;
    private JButton jbPlay;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JScrollPane jScrollPane4;
    private JList<String> jlAreaDataList;
    private JLabel jLabel1;
    private JPanel panel5;
    private JButton jbAddAreaData;
    private JButton jbRemoveAreaData;
    private JPanel jPanel6;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JTextField jtfUnknown1;
    private JTextField jtfMapTset;
    private JTextField jtfBuildTset;
    private JButton jbApplyBuildTset;
    private JButton jbApplyMapTset;
    private JButton jbApplyUnknown1;
    private JComboBox<String> jcbAreaType;
    private JPanel jPanel7;
    private JPanel jPanel10;
    private JLabel jLabel10;
    private NsbtxPanel nsbtxPanel;
    private JScrollPane jScrollPane6;
    private JList<String> jlBuildTsetList;
    private JPanel panel6;
    private JButton jbAddTset;
    private JButton jbAddEmptyTileset;
    private JButton jbReplaceTset;
    private JButton jbExportTileset;
    private JButton jbRemoveTset;
    private JPanel jPanel11;
    private JLabel jLabel9;
    private JScrollPane jScrollPane7;
    private JList<String> jlAreaBuildList;
    private JPanel panel7;
    private JButton jbAddBuildToTset;
    private JButton jbReplaceBuildToTset;
    private JButton jbRemoveBuildToTset;
    private JButton jbAddTexToNsbtx;
    private JButton jbRemoveTextures;
    private JButton jbRemoveAllUnusedTexPals;
    private NitroDisplayGL nitroDisplayAreaData;
    private JPanel jPanel9;
    private JPanel jPanel12;
    private JLabel jLabel11;
    private JScrollPane jScrollPane5;
    private JList<String> jlAnimationsList;
    private JPanel panel8;
    private JButton jbAddAnim;
    private JButton jbReplaceAnim;
    private JButton jbExportAnimation;
    private JButton jbRemoveAnim;
    private JPanel jPanel13;
    private JPanel jPanel14;
    private NitroDisplayGL nitroDisplayMap;
    private JButton jbOpenMap;
    private JLabel jLabel21;
    private JPanel jPanel15;
    private JScrollPane jScrollPane8;
    private JList<String> jlBuildFile;
    private JPanel jPanel16;
    private JLabel jLabel13;
    private JSpinner jsBuildID;
    private JButton jbChooseModelBld;
    private JLabel jLabel14;
    private JSpinner jsBuildX;
    private JLabel jLabel15;
    private JSpinner jsBuildY;
    private JLabel jLabel16;
    private JSpinner jsBuildZ;
    private JLabel jLabel17;
    private JSpinner jsBuildScaleX;
    private JSpinner jsBuildScaleY;
    private JLabel jLabel18;
    private JLabel jLabel19;
    private JSpinner jsBuildScaleZ;
    private JLabel jLabel20;
    private JPanel jPanel17;
    private JPanel jPanel18;
    private JButton jbImportBld;
    private JButton jbExportBld;
    private JPanel jPanel19;
    private JButton jbAddBuildBld;
    private JButton jbRemoveBld;
    private JPanel panel1;
    private JButton jbSaveAll;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
