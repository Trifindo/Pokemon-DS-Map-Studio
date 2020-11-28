package editor.tileseteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.smartdrawing.*;
import editor.tileselector.*;
import com.jogamp.opengl.GLContext;
import tileset.TilesetRenderer;
import editor.handler.MapEditorHandler;
import formats.obj.ObjWriter;
import editor.smartdrawing.SmartGrid;
import editor.smartdrawing.SmartGridEditable;
import editor.vertexcolors.VColorEditorDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.*;

import tileset.*;
import utils.swing.ThumbnailFileChooser;
import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class TilesetEditorDialog extends JDialog {

    private MapEditorHandler handler;
    private TilesetEditorHandler tileHandler;

    private boolean jComboBoxListenerActive = true;
    private boolean jcbTexGenModeListenerActive = true;
    private boolean jcbTexTilingUListenerActive = true;
    private boolean jcbTexTilingVListenerActive = true;
    private boolean jcbColorFormatListenerActive = true;
    private MutableBoolean jtfMaterialNameActive = new MutableBoolean(true);
    private MutableBoolean jtfTextureNameActive = new MutableBoolean(true);
    private MutableBoolean jtfPaletteNameActive = new MutableBoolean(true);
    private boolean jsAlphaActive = true;
    private MutableBoolean jtfGlobalTexScaleActive = new MutableBoolean(true);
    private MutableBoolean jtfXOffsetActive = new MutableBoolean(true);
    private MutableBoolean jtfYOffsetActive = new MutableBoolean(true);
    private boolean jlTileMaterialsEnabled = true;

    private static final Color redColor = new Color(255, 200, 200);
    private static final Color greenColor = new Color(200, 255, 200);
    private static final Color defaultTextPaneBackground = UIManager.getColor("TextPane.background");
    private static final Color defaultTextPaneForeground = UIManager.getColor("TextPane.foreground");
    private static final Color defaultInactiveTextPaneColor = UIManager.getColor("TextPane.inactiveForeground");

    private ArrayList<ImageIcon> materialIcons = new ArrayList<>();
    private ArrayList<ImageIcon> tileMaterialIcons = new ArrayList<>();

    public TilesetEditorDialog(Window owner) {
        super(owner);
        initComponents();

        jTabbedPane1.setIconAt(0, new ImageIcon(getClass().getResource("/icons/TileIcon.png")));
        jTabbedPane1.setIconAt(1, new ImageIcon(getClass().getResource("/icons/MaterialIcon2.png")));

        jScrollPane2.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPaneSmartGrid.getVerticalScrollBar().setUnitIncrement(16);

        Color redColor = new Color(255, 200, 200);

        addListenerToJTextField(jtfMaterialName, jtfMaterialNameActive);
        addListenerToJTextField(jtfTextureName, jtfTextureNameActive);
        addListenerToJTextField(jtfPaletteName, jtfPaletteNameActive);
        addListenerToJTextField(jtfGlobalTexScale, jtfGlobalTexScaleActive);
        addListenerToJTextField(jtfXOffset, jtfXOffsetActive);
        addListenerToJTextField(jtfYOffset, jtfYOffsetActive);
    }

    private void tileSelectorMousePressed(MouseEvent e) {
        tileDisplay.repaint();
        updateView();
    }

    /*
    private void jSpinner1StateChanged(ChangeEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.setTextureIdIndexSelected((Integer) jSpinner1.getValue());

            System.out.println((Integer) jSpinner1.getValue());
            System.out.println(tileHandler.getTextureIdIndexSelected());

            jComboBoxListenerActive = false;
            jcbMaterial.setSelectedItem(tileHandler.getTextureSelectedName());
            jComboBoxListenerActive = true;

            updateViewTexture();
        }
    }*/

    private void jcbMaterialActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (jComboBoxListenerActive) {

                handler.getTileSelected().getTextureIDs().set(
                        tileHandler.getTextureIdIndexSelected(),
                        jcbMaterial.getSelectedIndex());

                updateSelectedTileThumbnail();
                /*
                TilesetRenderer tr = new TilesetRenderer(handler.getTileset());
                for (int i = 0; i < handler.getTileset().size(); i++) {
                tr.renderTileThumbnail(i);
                }*/

                //tileHandler.updateTileThumbnail(handler.getTileIndexSelected()); //PROBLEMATIC
                tileSelector.updateTile(handler.getTileIndexSelected());

                updateViewTexture();

                updateViewJLTileMaterials(jlTileMaterials.getSelectedIndex());
            }
        }
    }

    private void jbLessSizeXActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int value = Integer.parseInt(jtfSizeX.getText());
            value--;
            if (value >= 1) {
                jtfSizeX.setText(String.valueOf(value));
                handler.getTileSelected().setWidth(value);
                updateSelectedTileThumbnail();
                tileSelector.updateLayout();
                tileSelector.repaint();
            }
        }
    }

    private void jbMoreSizeXActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int value = Integer.parseInt(jtfSizeX.getText());
            value++;
            if (value <= Tile.maxTileSize) {
                jtfSizeX.setText(String.valueOf(value));
                handler.getTileSelected().setWidth(value);
                updateSelectedTileThumbnail();
                tileSelector.updateLayout();
                tileSelector.repaint();
            }
        }
    }

    private void jbLessSizeYActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int value = Integer.parseInt(jtfSizeY.getText());
            value--;
            if (value >= 1) {
                jtfSizeY.setText(String.valueOf(value));
                handler.getTileSelected().setHeight(value);
                updateSelectedTileThumbnail();
                tileSelector.updateLayout();
                tileSelector.repaint();
            }
        }
    }

    private void jbMoreSizeYActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int value = Integer.parseInt(jtfSizeY.getText());
            value++;
            if (value <= Tile.maxTileSize) {
                jtfSizeY.setText(String.valueOf(value));
                handler.getTileSelected().setHeight(value);
                updateSelectedTileThumbnail();
                tileSelector.updateLayout();
                tileSelector.repaint();
            }
        }
    }

    private void jbRemoveTileActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 1) {

            ArrayList<Integer> indices = tileSelector.getIndicesSelected();
            for (int i = 0; i < indices.size(); i++) {
                handler.getTileset().removeTile(indices.get(0));
            }

            int index = handler.getTileIndexSelected();
            if (index >= handler.getTileset().size()) {
                handler.setIndexTileSelected(handler.getTileset().size() - 1);
                tileHandler.setMaterialIndexSelected(0);
            }

            tileSelector.setIndexSecondTileSelected(-1);

            //tileHandler.updateTilesetRenderer();
            tileSelector.updateLayout();
            tileDisplay.requestUpdate();

            smartGridEditableDisplay.updateTiles();
            smartGridEditableDisplay.repaint();

            updateJComboBox();

            updateView();
            updateViewTexture();
        } else {
            JOptionPane.showMessageDialog(this, "The tileset needs at least 1 tile", "Can't delete tile", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void jbAddTileActionPerformed(ActionEvent evt) {
        final AddTileDialog addTileDialog = new AddTileDialog(handler.getMainFrame(),
                "Import Tile Settings");
        addTileDialog.setLocationRelativeTo(this);
        addTileDialog.setVisible(true);
        if (addTileDialog.getReturnValue() == AddTileDialog.APPROVE_OPTION) {
            float scale = addTileDialog.getScale();
            boolean flip = addTileDialog.flip();

            final JFileChooser fc = new JFileChooser();
            if (handler.getLastTilesetDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
            fc.setMultiSelectionEnabled(true);
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Open");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    handler.setLastTilesetDirectoryUsed(fc.getSelectedFile().getParent());
                    File[] files = fc.getSelectedFiles();
                    ArrayList<Tile> newTiles = new ArrayList<>();
                    boolean exceptionFound = false;
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        try {
                            Tile tile = new Tile(handler.getTileset(), file.getAbsolutePath());

                            if (scale != 1.0f) {
                                tile.scaleModel(scale);
                            }

                            if (flip) {
                                tile.flipModelYZ();
                            }

                            newTiles.add(tile);
                        } catch (TextureNotFoundException ex) {
                            exceptionFound = true;
                            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                    "Error reading texture",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (NormalsNotFoundException ex) {
                            exceptionFound = true;
                            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                    "Error reading normals",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if (exceptionFound) {
                        BufferedImage image = Utils.loadTexImageAsResource("/imgs/BlenderExportObjSettings.png");
                        JLabel picLabel = new JLabel(new ImageIcon(image));
                        JOptionPane.showMessageDialog(null, picLabel,
                                "Use the following Blender export settings",
                                JOptionPane.PLAIN_MESSAGE, null);
                    }

                    int start = handler.getTileset().getTiles().size();
                    handler.getTileset().getTiles().addAll(newTiles);

                    //New code
                    handler.getTileset().removeUnusedTextures();

                    updateTileThumbnails(start, handler.getTileset().size());

                    tileSelector.updateLayout();
                    tileDisplay.requestUpdate();

                    tileDisplay.repaint();

                    updateJComboBox();
                    updateView();
                    repaint();

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Can't open file", "Error opening some files", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private void jcbTileableXActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().setXtileable(jcbTileableX.isSelected());
        }
    }

    private void jcbTileableYActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().setYtileable(jcbTileableY.isSelected());
        }
    }

    private void jbMoveUpActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (handler.getTileIndexSelected() > 0) {
                int newIndex = handler.getTileIndexSelected() - 1;
                handler.getTileset().swapTiles(handler.getTileIndexSelected(), newIndex);
                handler.setIndexTileSelected(newIndex);
                tileSelector.updateLayout();
                tileSelector.repaint();
                updateViewTileIndex();
            }
        }
    }

    private void jbMoveDownActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (handler.getTileIndexSelected() < handler.getTileset().size() - 1) {
                int newIndex = handler.getTileIndexSelected() + 1;
                handler.getTileset().swapTiles(handler.getTileIndexSelected(), newIndex);
                handler.setIndexTileSelected(newIndex);
                tileSelector.updateLayout();
                tileSelector.repaint();
                updateViewTileIndex();
            }
        }
    }

    private void jbAddTextureActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            final ThumbnailFileChooser fc = new ThumbnailFileChooser(); //Check for a better alternative
            if (handler.getLastTilesetDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.PNG)", "png"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Open");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();
                    handler.setLastTilesetDirectoryUsed(fc.getSelectedFile().getParent());

                    String path = file.getAbsolutePath();
                    boolean textureAdded = handler.getTileset().addTexture(path);
                    if (textureAdded) {
                        handler.getTileSelected().getTextureIDs().set(
                                tileHandler.getTextureIdIndexSelected(),
                                handler.getTileset().getMaterials().size() - 1);
                        updateJComboBox();
                        updateViewTextNames();
                        jComboBoxListenerActive = false;
                        jcbMaterial.setSelectedItem(tileHandler.getTextureSelectedName());
                        jComboBoxListenerActive = true;
                        updateViewJLTileMaterials(jlTileMaterials.getSelectedIndex());
                        tileDisplay.requestUpdate();
                        tileDisplay.repaint();
                        updateSelectedTileThumbnail();
                        //tileHandler.updateTileThumbnail(handler.getTileIndexSelected());
                        tileSelector.updateTile(handler.getTileIndexSelected());
                        tileSelector.repaint();
                        textureDisplay.repaint();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "A texture with the same name is already in the tileset",
                                "Texture already loaded", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Can't open file", "Error opening the file", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        }
    }

    private void jlistINamesValueChanged(ListSelectionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int index = jlistINames.getSelectedIndex();
            if (index != -1) {
                tileHandler.setMaterialIndexSelected(jlistINames.getSelectedIndex());
            }

            updateViewMaterialProperties();
            textureDisplayMaterial.repaint();
        }
    }

    private void jbTextNameActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changeTextureNameImd();
        }
    }

    private void jbPaletteNameActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changePaletteNameImd();
        }
    }

    private void jcbGlobalTexMappingActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            boolean selected = jcbGlobalTexMapping.isSelected();
            handler.getTileSelected().setGlobalTextureMapping(selected);
            jtfGlobalTexScale.setEditable(selected);
            jbGlobalTexScale.setEnabled(selected);
            jtfGlobalTexScale.setBackground(selected ? defaultTextPaneBackground : defaultInactiveTextPaneColor);
            jtfGlobalTexScale.setForeground(defaultTextPaneForeground);
        }
    }

    private void jbRotateModelActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().rotateModelZ();

            updateView3DModel();
        }
    }

    private void jcbEnableFogActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setFogEnabled(jcbEnableFog.isSelected());
        }
    }

    private void jcbRenderFrontAndBackActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setRenderBothFaces(jcbRenderFrontAndBack.isSelected());
        }
    }

    private void jcbUniformNormalActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setUniformNormalOrientation(jcbUniformNormal.isSelected());
        }
    }

    private void jbMaterialNameActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changeMaterialName();
        }
    }

    private void jSpinner2StateChanged(ChangeEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (jsAlphaActive) {
                int alpha = (Integer) jSpinner2.getValue();
                if (alpha >= 0 && alpha < 32) {
                    tileHandler.getMaterialSelected().setAlpha(alpha);
                }
            }
        }
    }

    private void jbGlobalTexScaleActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changeGlobalTexScale();
        }
    }

    private void jcbTexGenModeActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (jcbTexGenModeListenerActive) {
                tileHandler.getMaterialSelected().setTexGenMode(jcbTexGenMode.getSelectedIndex());
            }
        }
    }

    private void jbDuplicateTileActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            ArrayList<Integer> indices = tileSelector.getIndicesSelected();

            handler.getTileset().duplicateTiles(indices);
            //int index = handler.getTileIndexSelected();
            //handler.getTileset().duplicateTile(index);
            tileDisplay.requestUpdate();
            //tileDisplay.swapVBOs(handler.getTileIndexSelected(), newIndex);
            //handler.setIndexTileSelected(indices.get(indices.get(0)));
            tileSelector.updateLayout();
            tileSelector.repaint();
            updateViewTileIndex();
        }

    }

    private void jbFlipModelActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().flipModelX();
            updateView3DModel();
        }
    }

    private void jbMoveModelUpActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().moveModel(0.0f, 1.0f, 0.0f);
            updateView3DModel();
        }
    }

    private void jbMoveModelDownActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().moveModel(0.0f, -1.0f, 0.0f);
            updateView3DModel();
        }
    }

    private void jbMoveModelLeftActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().moveModel(-1.0f, 0.0f, 0.0f);
            updateView3DModel();
        }
    }

    private void jbMoveModelRightActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().moveModel(1.0f, 0.0f, 0.0f);
            updateView3DModel();
        }
    }

    private void jbMoveMaterialUpActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int index = tileHandler.getMaterialIndexSelected();
            if (index > 0) {
                handler.getTileset().swapMaterials(index, index - 1);
                tileDisplay.swapTextures(index, index - 1);
                updateJComboBox();
                updateViewTextNames();
                jlistINames.setSelectedIndex(index - 1);
            }
        }
    }

    private void jbMoveMaterialDownActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int index = tileHandler.getMaterialIndexSelected();
            if (index < handler.getTileset().getMaterials().size() - 1) {
                handler.getTileset().swapMaterials(index, index + 1);
                tileDisplay.swapTextures(index, index + 1);
                updateJComboBox();
                updateViewTextNames();
                jlistINames.setSelectedIndex(index + 1);
            }
        }

    }

    private void jtfMaterialNameActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changeMaterialName();
        }
    }

    private void jtfTextureNameActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changeTextureNameImd();
        }
    }

    private void jtfPaletteNameActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changePaletteNameImd();
        }
    }

    private void jcbAlwaysIncludedInImdActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setAlwaysIncludeInImd(jcbAlwaysIncludedInImd.isSelected());
        }
    }

    private void jcbUtileableActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().setUtileable(jcbUtileable.isSelected());
        }
    }

    private void jcbVtileableActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().setVtileable(jcbVtileable.isSelected());
        }
    }

    private void jbXOffsetActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changeXOffset();
        }
    }

    private void jbYOffsetActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            changeYOffset();
        }
    }

    private void jbReplaceMaterialActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            replaceMaterial();
        }
    }

    private void jbMoveModelUp1ActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().moveModel(0.0f, 0.0f, 1.0f);
            updateView3DModel();
        }
    }

    private void jbMoveModelDown1ActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            handler.getTileSelected().moveModel(0.0f, 0.0f, -1.0f);
            updateView3DModel();
        }
    }

    private void jcbTexTilingUActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (jcbTexTilingUListenerActive) {
                tileHandler.getMaterialSelected().setTexTilingU(jcbTexTilingU.getSelectedIndex());
            }
        }
    }

    private void jcbTexTilingVActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (jcbTexTilingVListenerActive) {
                tileHandler.getMaterialSelected().setTexTilingV(jcbTexTilingV.getSelectedIndex());
            }
        }
    }

    private void jcbColorFormatActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            if (jcbColorFormatListenerActive) {
                tileHandler.getMaterialSelected().setColorFormat(jcbColorFormat.getSelectedIndex());
            }
        }
    }

    private void jcbL0ActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setLight0(jcbL0.isSelected());
        }
    }

    private void jcbL1ActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setLight1(jcbL1.isSelected());
        }
    }

    private void jcbL2ActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setLight2(jcbL2.isSelected());
        }
    }

    private void jcbL3ActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setLight3(jcbL3.isSelected());
        }
    }

    private void jbExportTileAsObjActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            final ExportTileDialog exportTileDialog = new ExportTileDialog(handler.getMainFrame(), "Export Tile Settings");
            exportTileDialog.setLocationRelativeTo(this);
            exportTileDialog.setVisible(true);
            if (exportTileDialog.getReturnValue() == AddTileDialog.APPROVE_OPTION) {
                float scale = exportTileDialog.getScale();
                boolean flip = exportTileDialog.flip();
                boolean includeVertexColors = exportTileDialog.includeVertexColors();

                final JFileChooser fc = new JFileChooser();
                if (handler.getLastTileObjDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastTileObjDirectoryUsed()));
                }
                fc.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
                fc.setApproveButtonText("Save");
                fc.setDialogTitle("Save tile as OBJ");
                fc.setSelectedFile(new File(handler.getTileSelected().getObjFilename()));
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String path = fc.getSelectedFile().getPath();
                    handler.setLastTileObjDirectoryUsed(fc.getSelectedFile().getParent());

                    ObjWriter objWriter = new ObjWriter(handler.getTileset(),
                            handler.getGrid(), path, handler.getGameIndex(), true, includeVertexColors, 1.0f);
                    try {
                        objWriter.writeTileObj(handler.getTileIndexSelected(), scale, flip);
                        JOptionPane.showMessageDialog(this, "Tile succesfully exported as OBJ",
                                "Tile saved", JOptionPane.INFORMATION_MESSAGE);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(this, "There was a problem saving the tile as OBJ",
                                "Can't save tile", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void jbReplaceTextureActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            replaceTextureWithDialog();
        }
    }

    private void jbImportTileAsObjActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            final AddTileDialog addTileDialog = new AddTileDialog(handler.getMainFrame(), "Import Tile Settings");
            addTileDialog.setLocationRelativeTo(this);
            addTileDialog.setVisible(true);
            if (addTileDialog.getReturnValue() == AddTileDialog.APPROVE_OPTION) {
                float scale = addTileDialog.getScale();
                boolean flip = addTileDialog.flip();

                final JFileChooser fc = new JFileChooser();
                if (handler.getLastTilesetDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
                }
                fc.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
                fc.setApproveButtonText("Open");
                fc.setDialogTitle("Open OBJ");
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        handler.setLastTilesetDirectoryUsed(fc.getSelectedFile().getParent());
                        File file = fc.getSelectedFile();
                        boolean exceptionFound = false;
                        try {
                            Tile tile = new Tile(handler.getTileset(),
                                    file.getAbsolutePath(),
                                    handler.getTileSelected());

                            if (scale != 1.0f) {
                                tile.scaleModel(scale);
                            }

                            if (flip) {
                                tile.flipModelYZ();
                            }

                            handler.getTileset().getTiles().set(handler.getTileIndexSelected(), tile);

                            //Remove unused textures
                            handler.getTileset().removeUnusedTextures();

                            updateSelectedTileThumbnail();

                            tileSelector.updateLayout();
                            tileDisplay.requestUpdate();

                            tileDisplay.repaint();

                            updateJComboBox();
                            updateView();
                            repaint();

                        } catch (TextureNotFoundException ex) {
                            exceptionFound = true;
                            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                    "Error reading texture",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (NormalsNotFoundException ex) {
                            exceptionFound = true;
                            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                    "Error reading normals",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        if (exceptionFound) {
                            BufferedImage image = Utils.loadTexImageAsResource("/imgs/BlenderExportObjSettings.png");
                            JLabel picLabel = new JLabel(new ImageIcon(image));
                            JOptionPane.showMessageDialog(null, picLabel,
                                    "Use the following Blender export settings",
                                    JOptionPane.PLAIN_MESSAGE, null);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Can't open file", "Error opening some files", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }

    private void jcbRenderBorderActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setRenderBorder(jcbRenderBorder.isSelected());
        }
    }

    private void jbImportTilesActionPerformed(ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastTilesetDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS Tileset (*.pdsts)", Tileset.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Select a Pokemon DS Map Studio Tileset");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                handler.setLastTilesetDirectoryUsed(fc.getSelectedFile().getParent());
                String path = fc.getSelectedFile().getPath();
                Tileset tileset = TilesetIO.readTilesetFromFile(path);
                int start = handler.getTileset().size();

                final ImportTilesDialog dialog = new ImportTilesDialog(handler.getMainFrame());
                dialog.init(tileset);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);

                if (dialog.getReturnValue() == ImportTilesDialog.APPROVE_OPTION) {
                    ArrayList<Tile> tiles = dialog.getTilesSelected();

                    handler.getTileset().importTiles(tiles);

                    handler.getTileset().removeUnusedTextures();

                    updateTileThumbnails(start, handler.getTileset().size());

                    tileSelector.updateLayout();
                    tileDisplay.requestUpdate();
                    tileDisplay.repaint();
                    updateJComboBox();
                    updateView();
                    repaint();
                }
            } catch (NullPointerException | TextureNotFoundException | IOException ex) {
                JOptionPane.showMessageDialog(this, "There was a problem opening the tileset",
                        "Error opening tileset", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void jcbBackfaceCullingActionPerformed(ActionEvent evt) {
        tileDisplay.setBackfaceCulling(jcbBackfaceCulling.isSelected());
        tileDisplay.repaint();
    }

    private void jcbWireframeActionPerformed(ActionEvent evt) {
        tileDisplay.setWireframe(jcbWireframe.isSelected());
        tileDisplay.repaint();
    }

    private void jcbNormalsActionPerformed(ActionEvent e) {
        tileDisplay.setNormalsEnabled(jcbNormals.isSelected());
        tileDisplay.repaint();
    }

    private void jbEditVertexColorsActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            final VColorEditorDialog dialog = new VColorEditorDialog(handler.getMainFrame());
            dialog.init(handler, tileHandler);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            handler.getTileSelected().updateObjData();

            updateView3DModel();
        }

    }

    private void jcbUseVertexColorsActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            tileHandler.getMaterialSelected().setVertexColorsEnabled(jcbUseVertexColors.isSelected());
        }
    }

    private void jcbTexturesEnabledActionPerformed(ActionEvent evt) {
        tileDisplay.setTexturesEnabled(jcbTexturesEnabled.isSelected());
        tileDisplay.repaint();
    }

    private void jcbShadingEnabledActionPerformed(ActionEvent evt) {
        tileDisplay.setLightingEnabled(jcbShadingEnabled.isSelected());
        tileDisplay.repaint();
    }

    private void jbMoveSPaintUpActionPerformed(ActionEvent evt) {
        smartGridEditableDisplay.moveSelectedSmartGridUp();
        smartGridEditableDisplay.repaint();
    }

    private void jbMoveSPaintDownActionPerformed(ActionEvent evt) {
        smartGridEditableDisplay.moveSelectedSmartGridDown();
        smartGridEditableDisplay.repaint();
    }

    private void jbAddSmartGridActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0) {
            int gridIndex = handler.getSmartGridIndexSelected();
            try {
                smartGridEditableDisplay.getSmartGridArray().add(gridIndex, new SmartGridEditable());
                //handler.getSmartGridArray().add(gridIndex, new SmartGrid());
            } catch (IndexOutOfBoundsException ex) {
                smartGridEditableDisplay.getSmartGridArray().add(new SmartGridEditable());
                //handler.getSmartGridArray().add(new SmartGrid());
            }
            smartGridEditableDisplay.updateSize();
            smartGridEditableDisplay.repaint();
        }
    }

    private void jbRemoveSmartGridActionPerformed(ActionEvent evt) {
        if (handler.getTileset().size() > 0 && smartGridEditableDisplay.getSmartGridArray().size() > 1) {
            int gridIndex = handler.getSmartGridIndexSelected();
            if (gridIndex >= 0 && gridIndex < smartGridEditableDisplay.getSmartGridArray().size()) {
                smartGridEditableDisplay.getSmartGridArray().remove(gridIndex);
                handler.setSmartGridIndexSelected(Math.max(0, gridIndex - 1));
                smartGridEditableDisplay.updateSize();
                smartGridEditableDisplay.repaint();
                //handler.getSmartGridArray().remove(gridIndex);
                //handler.setSmartGridIndexSelected(Math.max(0, gridIndex - 1));
            }
        }
    }

    private void jlTileMaterialsValueChanged(ListSelectionEvent e) {
        if (handler.getTileset().size() > 0){
            if(jlTileMaterialsEnabled){
                int index = jlTileMaterials.getSelectedIndex();
                if(index >= 0 && index < handler.getTileSelected().getTextureIDs().size()){
                    tileHandler.setTextureIdIndexSelected(index);

                    jComboBoxListenerActive = false;
                    jcbMaterial.setSelectedItem(tileHandler.getTextureSelectedName());
                    jComboBoxListenerActive = true;

                    updateViewTexture();
                }
            }
        }

    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        tileHandler = new TilesetEditorHandler(handler);

        tileDisplay.setHandler(handler);

        tileSelector.init(handler, this);
        tileSelector.updateLayout();

        smartGridEditableDisplay.init(handler);

        textureDisplay.init(tileHandler, this);
        textureDisplayMaterial.init(tileHandler, this);

        updateJComboBox();

        updateView();
    }

    private void updateJComboBox() {
        Object[] names = new String[handler.getTileset().getMaterials().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = handler.getTileset().getMaterials().get(i).getImageName();
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(names);
        jComboBoxListenerActive = false;
        jcbMaterial.setModel(model);

        jcbMaterial.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if(handler.getTileset().size() > 0){
                    if(index == -1){
                        index = tileHandler.getTextureIndexSelected();
                    }
                    if (index < materialIcons.size() && index >= 0) {
                        label.setIcon(materialIcons.get(index));
                    }
                }
                return label;
            }
        });

        if (handler.getTileset().size() > 0) {
            jcbMaterial.setSelectedIndex(handler.getTileSelected().getTextureIDs().get(0));
        }
        jComboBoxListenerActive = true;
    }

    private void updateView() {
        if (handler.getTileset().size() > 0) {
            Tile tile = handler.getTileSelected();

            jtfSizeX.setText(String.valueOf(tile.getWidth()));
            jtfSizeY.setText(String.valueOf(tile.getHeight()));

            //jtfNumTextures.setText(String.valueOf(tile.getTextureIDs().size()));

            updateViewJLTileMaterials(0);

            updateViewXOffset(tile);
            updateViewYOffset(tile);
            updateViewGlobalTexScale(tile);

            jtfObjName.setText(tile.getObjFilename());

            updateViewTileIndex();

            //SpinnerNumberModel model = new SpinnerNumberModel(0, 0, tile.getTextureIDs().size() - 1, 1);
            //jSpinner1.setModel(model);

            tileHandler.setTextureIdIndexSelected(0);
            textureDisplay.repaint();

            jComboBoxListenerActive = false;
            jcbMaterial.setSelectedItem(tileHandler.getTextureSelectedName());
            jComboBoxListenerActive = true;

            jcbTileableX.setSelected(tile.isXtileable());
            jcbTileableY.setSelected(tile.isYtileable());
            jcbUtileable.setSelected(tile.isUtileable());
            jcbVtileable.setSelected(tile.isVtileable());
            jcbGlobalTexMapping.setSelected(tile.useGlobalTextureMapping());

            updateViewTextNames();

            updateViewMaterialProperties();

            textureDisplayMaterial.repaint();

            //updateViewTextNames();
        }

    }

    private void updateViewJLTileMaterials(int indexSelected){
        jlTileMaterialsEnabled = false;
        DefaultListModel model = new DefaultListModel();
        for(int texID : handler.getTileSelected().getTextureIDs()){
            TilesetMaterial mat = handler.getTileset().getMaterial(texID);

            model.addElement(mat.getMaterialName());
        }
        jlTileMaterials.setModel(model);

        if(indexSelected < 0){
            indexSelected = 0;
        } else if(indexSelected >= model.size()){
            indexSelected = model.size() - 1;
        }
        jlTileMaterials.setSelectedIndex(indexSelected);
        jlTileMaterialsEnabled = true;

        tileMaterialIcons = new ArrayList<>(handler.getTileSelected().getTextureIDs().size());
        for (int id : handler.getTileSelected().getTextureIDs()) {
            tileMaterialIcons.add(new ImageIcon(new ImageIcon(handler.getTileset().getTextureImg(id)).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
        }

        jlTileMaterials.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index < tileMaterialIcons.size() && index >= 0) {
                    label.setIcon(tileMaterialIcons.get(index));
                }
                return label;
            }
        });
    }

    private void updateView3DModel() {
        updateSelectedTileThumbnail();

        tileDisplay.requestUpdate();
        tileDisplay.repaint();
        tileSelector.updateTile(handler.getTileIndexSelected());
        tileSelector.repaint();
    }

    private void updateViewXOffset(Tile tile) {
        jtfXOffsetActive.value = false;
        jtfXOffset.setText(String.valueOf(tile.getXOffset()));
        jtfXOffset.setBackground(defaultTextPaneBackground);
        jtfXOffset.setForeground(defaultTextPaneForeground);
        jtfXOffsetActive.value = true;
    }

    private void updateViewYOffset(Tile tile) {
        jtfYOffsetActive.value = false;
        jtfYOffset.setText(String.valueOf(tile.getYOffset()));
        jtfYOffset.setBackground(defaultTextPaneBackground);
        jtfYOffset.setForeground(defaultTextPaneForeground);
        jtfYOffsetActive.value = true;
    }

    private void updateViewGlobalTexScale(Tile tile) {
        jtfGlobalTexScale.setText(String.valueOf(tile.getGlobalTextureScale()));
        boolean enabled = tile.useGlobalTextureMapping();
        jtfGlobalTexScale.setBackground(enabled ? defaultTextPaneBackground : defaultInactiveTextPaneColor);
        jtfGlobalTexScale.setForeground(defaultTextPaneForeground);
        jtfGlobalTexScale.setEditable(enabled);
        jbGlobalTexScale.setEnabled(enabled);
    }

    private void updateViewTexGenMode() {
        jcbTexGenModeListenerActive = false;
        jcbTexGenMode.setSelectedIndex(tileHandler.getMaterialSelected().getTexGenMode());
        jcbTexGenModeListenerActive = true;
    }

    private void updateViewTexTilingU() {
        jcbTexTilingUListenerActive = false;
        jcbTexTilingU.setSelectedIndex(tileHandler.getMaterialSelected().getTexTilingU());
        jcbTexTilingUListenerActive = true;
    }

    private void updateViewTexTilingV() {
        jcbTexTilingVListenerActive = false;
        jcbTexTilingV.setSelectedIndex(tileHandler.getMaterialSelected().getTexTilingV());
        jcbTexTilingVListenerActive = true;
    }

    private void updateViewColorFormat() {
        jcbColorFormatListenerActive = false;
        jcbColorFormat.setSelectedIndex(tileHandler.getMaterialSelected().getColorFormat());
        jcbColorFormatListenerActive = true;
    }

    private void updateViewMaterialProperties() {
        updateViewMaterialName();
        updateViewPaletteNameImd();
        updateViewTextureNameImd();
        updateViewAlpha();
        updateViewTexGenMode();
        updateViewTexTilingU();
        updateViewTexTilingV();
        updateViewColorFormat();
        jcbEnableFog.setSelected(tileHandler.getMaterialSelected().isFogEnabled());
        jcbRenderFrontAndBack.setSelected(tileHandler.getMaterialSelected().renderBothFaces());
        jcbUniformNormal.setSelected(tileHandler.getMaterialSelected().uniformNormalOrientation());
        jcbAlwaysIncludedInImd.setSelected(tileHandler.getMaterialSelected().alwaysIncludeInImd());
        jcbUseVertexColors.setSelected(tileHandler.getMaterialSelected().vertexColorsEnabled());
        jcbL0.setSelected(tileHandler.getMaterialSelected().light0());
        jcbL1.setSelected(tileHandler.getMaterialSelected().light1());
        jcbL2.setSelected(tileHandler.getMaterialSelected().light2());
        jcbL3.setSelected(tileHandler.getMaterialSelected().light3());
        jcbRenderBorder.setSelected(tileHandler.getMaterialSelected().renderBorder());
    }

    public void updateViewTileIndex() {
        jtfIndexTile.setText(String.valueOf(handler.getTileIndexSelected()));
    }

    private void updateViewTextNames() {
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < handler.getTileset().getMaterials().size(); i++) {
            String textureName = handler.getTileset().getImageName(i);
            demoList.addElement(textureName);
        }
        jlistINames.setSelectedIndex(0);
        jlistINames.setModel(demoList);
        jlistINames.setSelectedIndex(0);

        materialIcons = new ArrayList<>(handler.getTileset().getMaterials().size());
        for (int i = 0; i < handler.getTileset().getMaterials().size(); i++) {
            materialIcons.add(new ImageIcon(new ImageIcon(handler.getTileset().getTextureImg(i)).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
        }

        jlistINames.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index < materialIcons.size() && index >= 0) {
                    label.setIcon(materialIcons.get(index));
                }
                return label;
            }
        });

    }

    public void updateViewMaterialName() {
        if (jlistINames.getSelectedIndex() != -1) {
            String mName = handler.getTileset().getMaterialName(jlistINames.getSelectedIndex());
            jtfMaterialNameActive.value = false;
            jtfMaterialName.setText(mName);
            jtfMaterialName.setBackground(defaultTextPaneBackground);
            jtfMaterialName.setForeground(defaultTextPaneForeground);
            jtfMaterialNameActive.value = true;
        }
    }

    private void updateViewPaletteNameImd() {
        if (jlistINames.getSelectedIndex() != -1) {
            String pName = handler.getTileset().getPaletteNameImd(jlistINames.getSelectedIndex());
            jtfPaletteNameActive.value = false;
            jtfPaletteName.setText(pName);
            jtfPaletteName.setBackground(defaultTextPaneBackground);
            jtfPaletteName.setForeground(defaultTextPaneForeground);
            jtfPaletteNameActive.value = true;
        }
    }

    private void updateViewTextureNameImd() {
        if (jlistINames.getSelectedIndex() != -1) {
            String tName = handler.getTileset().getTextureNameImd(jlistINames.getSelectedIndex());
            jtfTextureNameActive.value = false;
            jtfTextureName.setText(tName);
            jtfTextureName.setBackground(defaultTextPaneBackground);
            jtfTextureName.setForeground(defaultTextPaneForeground);
            jtfTextureNameActive.value = true;
        }
    }

    private void updateViewAlpha() {
        jsAlphaActive = false;
        if (jlistINames.getSelectedIndex() != -1) {
            jSpinner2.setValue(tileHandler.getMaterialSelected().getAlpha());
        }
        jsAlphaActive = true;
    }

    private void updateViewTexture() {
        textureDisplay.repaint();
        tileDisplay.repaint();
        tileSelector.repaint();
        //TODO: Update texture and repaint tileselector
    }

    private void updateSelectedTileThumbnail() {
        updateTileThumbnail(handler.getTileIndexSelected());

        tileDisplay.requestUpdate();
    }

    private void updateTileThumbnails(ArrayList<Integer> indicesTiles) {
        GLContext context = tileDisplay.getContext();

        TilesetRenderer tr = new TilesetRenderer(handler.getTileset());
        for (int i = 0; i < indicesTiles.size(); i++) {
            tr.renderTileThumbnail(indicesTiles.get(i));
        }
        tr.destroy();
        tileDisplay.setContext(context, false);
        tileDisplay.requestUpdate();
    }

    public void replaceMaterial() {
        final ReplaceMaterialDialog dialog = new ReplaceMaterialDialog(handler.getMainFrame());
        dialog.init(handler, tileHandler, this);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == ReplaceMaterialDialog.APPROVE_OPTION) {
            int newIndex = dialog.getIndexSelected();
            ArrayList<Integer> indicesTilesReplaced = handler.getTileset().replaceMaterial(tileHandler.getMaterialIndexSelected(), newIndex);

            updateTileThumbnails(indicesTilesReplaced);
            tileSelector.updateLayout();
            tileDisplay.requestUpdate();

            updateJComboBox();
            updateView();
            updateViewTexture();

            //tileHandler.setMaterialIndexSelected(newIndex);
        }

    }

    private void replaceTextureWithDialog() {
        final ThumbnailFileChooser fc = new ThumbnailFileChooser(); //Check for a better alternative
        if (handler.getLastTilesetDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.PNG)", "png"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open New Texture Image");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            handler.setLastTilesetDirectoryUsed(fc.getSelectedFile().getParent());

            String path = file.getAbsolutePath();
            replaceTexture(tileHandler.getMaterialIndexSelected(), path);
            /*
            try {
                boolean textureAdded = handler.getTileset().replaceTexture(tileHandler.getMaterialIndexSelected(), path);
                if (textureAdded) {
                    //Calculate tiles that have to be updated
                    ArrayList<Integer> tileIndsToUpdate = new ArrayList<>();
                    for (int i = 0; i < handler.getTileset().size(); i++) {
                        Tile t = handler.getTileset().get(i);
                        for (int j = 0; j < t.getTextureIDs().size(); j++) {
                            int id = t.getTextureIDs().get(j);
                            if (id == tileHandler.getMaterialIndexSelected()) {
                                tileIndsToUpdate.add(i);
                            }
                        }
                    }
                    updateJComboBox();
                    updateViewTextNames();
                    tileDisplay.requestUpdate();
                    tileDisplay.repaint();

                    tileHandler.updateTileThumnails(tileIndsToUpdate);
                    tileSelector.updateTiles(tileIndsToUpdate);
                    tileSelector.repaint();
                    textureDisplay.repaint();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "A texture with the same name is already in the tileset",
                            "Texture already loaded", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "There was a problem loading the image",
                        "Error opening image", JOptionPane.ERROR_MESSAGE);
            }
             */
        }
    }

    public void replaceTexture(int textureIndex, String path) {
        try {
            boolean textureAdded = handler.getTileset().replaceTexture(textureIndex, path);
            if (textureAdded) {
                //Calculate tiles that have to be updated
                ArrayList<Integer> tileIndsToUpdate = new ArrayList<>();
                for (int i = 0; i < handler.getTileset().size(); i++) {
                    Tile t = handler.getTileset().get(i);
                    for (int j = 0; j < t.getTextureIDs().size(); j++) {
                        int id = t.getTextureIDs().get(j);
                        if (id == textureIndex) {
                            tileIndsToUpdate.add(i);
                        }
                    }
                }
                updateJComboBox();
                updateViewTextNames();
                tileDisplay.requestUpdate();
                tileDisplay.repaint();

                updateTileThumbnails(0, handler.getTileset().size());
                //tileHandler.updateTileThumnails(tileIndsToUpdate);
                tileSelector.updateTiles(tileIndsToUpdate);
                tileSelector.repaint();
                textureDisplay.repaint();
            } else {
                JOptionPane.showMessageDialog(this,
                        "A texture with the same name is already in the tileset",
                        "Texture already loaded", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "There was a problem loading the image",
                    "Error opening image", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeGlobalTexScale() {
        float value;
        try {
            value = Float.valueOf(jtfGlobalTexScale.getText());
        } catch (NumberFormatException e) {
            value = handler.getTileSelected().getGlobalTextureScale();
        }
        handler.getTileSelected().setGlobalTextureScale(value);
        jtfGlobalTexScale.setText(String.valueOf(value));
        jtfGlobalTexScaleActive.value = false;
        jtfGlobalTexScale.setBackground(greenColor);
        jtfGlobalTexScale.setForeground(Color.black);
        jtfGlobalTexScaleActive.value = true;
    }

    private void changeXOffset() {
        float value;
        try {
            value = Float.valueOf(jtfXOffset.getText());
        } catch (NumberFormatException e) {
            value = handler.getTileSelected().getXOffset();
        }
        handler.getTileSelected().setXOffset(value);
        jtfXOffset.setText(String.valueOf(value));
        jtfXOffsetActive.value = false;
        jtfXOffset.setBackground(greenColor);
        jtfXOffset.setForeground(Color.black);
        jtfXOffsetActive.value = true;
    }

    private void changeYOffset() {
        float value;
        try {
            value = Float.valueOf(jtfYOffset.getText());
        } catch (NumberFormatException e) {
            value = handler.getTileSelected().getYOffset();
        }
        handler.getTileSelected().setYOffset(value);
        jtfYOffset.setText(String.valueOf(value));
        jtfYOffsetActive.value = false;
        jtfYOffset.setBackground(greenColor);
        jtfYOffset.setForeground(Color.black);
        jtfYOffsetActive.value = true;
    }

    private void changeMaterialName() {
        String mName = jtfMaterialName.getText();
        int index = jlistINames.getSelectedIndex();
        handler.getTileset().setMaterialName(index, mName);

        jtfMaterialNameActive.value = false;
        jtfMaterialName.setBackground(greenColor);
        jtfMaterialName.setForeground(Color.black);
        jtfMaterialNameActive.value = true;
    }

    private void changePaletteNameImd() {
        String pName = jtfPaletteName.getText();
        int index = jlistINames.getSelectedIndex();
        handler.getTileset().setPaletteNameImd(index, pName);

        jtfPaletteNameActive.value = false;
        jtfPaletteName.setBackground(greenColor);
        jtfPaletteName.setForeground(Color.black);
        jtfPaletteNameActive.value = true;
    }

    private void changeTextureNameImd() {
        String tName = jtfTextureName.getText();
        int index = jlistINames.getSelectedIndex();
        handler.getTileset().setTextureNameImd(index, tName);

        jtfTextureNameActive.value = false;
        jtfTextureName.setBackground(greenColor);
        jtfTextureName.setForeground(Color.black);
        jtfTextureNameActive.value = true;
    }

    public void fixIndices() {
        int[] indices = tileHandler.getChangeIndices();
        tileHandler.fixMapGridIndices(indices);

        fixSmartGridIndices();
        //tileHandler.fixTilesetGridIndices(indices);
    }

    private void fixSmartGridIndices() {
        ArrayList<SmartGridEditable> smartGridEditableArray = smartGridEditableDisplay.getSmartGridArray();
        ArrayList<SmartGrid> smartGridArray = new ArrayList<>(smartGridEditableArray.size());
        for (SmartGridEditable smartGrid : smartGridEditableArray) {
            smartGridArray.add(new SmartGrid(smartGrid.toTileIndices(handler.getTileset())));
        }
        handler.getTileset().setSgridArray(smartGridArray);
    }

    public TileDisplay getTileDisplay() {
        return tileDisplay;
    }

    private void updateTileThumbnail(int index) {
        GLContext context = tileDisplay.getContext();
        TilesetRenderer tr = new TilesetRenderer(handler.getTileset());
        tr.renderTileThumbnail(index);
        tr.destroy();
        tileDisplay.setContext(context, false);
    }

    private void updateTileThumbnails(int start, int end) {
        GLContext context = tileDisplay.getContext();
        TilesetRenderer tr = new TilesetRenderer(handler.getTileset());
        for (int i = start; i < end; i++) {
            tr.renderTileThumbnail(i);
        }
        tr.destroy();
        tileDisplay.setContext(context, false);
    }

    private void addListenerToJTextField(JTextField jtf, MutableBoolean enabled) {
        jtf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeBackground();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeBackground();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeBackground();
            }

            public void changeBackground() {
                if (enabled.value) {
                    jtf.setBackground(redColor);
                    jtf.setForeground(Color.black);
                }
            }
        });
    }




    private class MutableBoolean {

        public boolean value;

        public MutableBoolean(boolean value) {
            this.value = value;
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel18 = new JPanel();
        tileDisplay = new TileDisplay();
        jTabbedPane2 = new JTabbedPane();
        jPanel4 = new JPanel();
        jLabel12 = new JLabel();
        jbRotateModel = new JButton();
        jbMoveModelLeft = new JButton();
        jbMoveModelUp = new JButton();
        jbMoveModelRight = new JButton();
        jbMoveModelUp1 = new JButton();
        jLabel13 = new JLabel();
        jbFlipModel = new JButton();
        jbMoveModelDown = new JButton();
        jbMoveModelDown1 = new JButton();
        jPanel8 = new JPanel();
        jcbBackfaceCulling = new JCheckBox();
        jcbWireframe = new JCheckBox();
        jcbTexturesEnabled = new JCheckBox();
        jcbShadingEnabled = new JCheckBox();
        jcbNormals = new JCheckBox();
        jPanel2 = new JPanel();
        jScrollPane2 = new JScrollPane();
        tileSelector = new TileSelector();
        jPanel9 = new JPanel();
        jScrollPaneSmartGrid = new JScrollPane();
        smartGridEditableDisplay = new SmartGridEditableDisplay();
        panel9 = new JPanel();
        jbMoveSPaintUp = new JButton();
        jbAddSmartGrid = new JButton();
        jbMoveSPaintDown = new JButton();
        jbRemoveSmartGrid = new JButton();
        jTabbedPane1 = new JTabbedPane();
        jPanel1 = new JPanel();
        panel8 = new JPanel();
        jLabel3 = new JLabel();
        jtfIndexTile = new JTextField();
        jLabel23 = new JLabel();
        jbMoveUp = new JButton();
        jbMoveDown = new JButton();
        panel1 = new JPanel();
        jbAddTile = new JButton();
        jbRemoveTile = new JButton();
        jbDuplicateTile = new JButton();
        jbImportTiles = new JButton();
        jPanel5 = new JPanel();
        panel2 = new JPanel();
        jLabel1 = new JLabel();
        jbLessSizeX = new JButton();
        jtfSizeX = new JTextField();
        jbMoreSizeX = new JButton();
        jLabel2 = new JLabel();
        jbLessSizeY = new JButton();
        jtfSizeY = new JTextField();
        jbMoreSizeY = new JButton();
        panel4 = new JPanel();
        jcbTileableX = new JCheckBox();
        jcbUtileable = new JCheckBox();
        jcbTileableY = new JCheckBox();
        jcbVtileable = new JCheckBox();
        panel3 = new JPanel();
        jLabel14 = new JLabel();
        jtfXOffset = new JTextField();
        jbXOffset = new JButton();
        jLabel15 = new JLabel();
        jtfYOffset = new JTextField();
        jbYOffset = new JButton();
        panel5 = new JPanel();
        jcbGlobalTexMapping = new JCheckBox();
        jLabel10 = new JLabel();
        jtfGlobalTexScale = new JTextField();
        jbGlobalTexScale = new JButton();
        jPanel6 = new JPanel();
        jLabel5 = new JLabel();
        jtfObjName = new JTextField();
        panel10 = new JPanel();
        jbExportTileAsObj = new JButton();
        jbImportTileAsObj = new JButton();
        jbEditVertexColors = new JButton();
        jPanel7 = new JPanel();
        panel6 = new JPanel();
        jLabel22 = new JLabel();
        label2 = new JLabel();
        jbAddTexture = new JButton();
        jcbMaterial = new JComboBox<>();
        scrollPane1 = new JScrollPane();
        jlTileMaterials = new JList();
        textureDisplay = new TextureDisplay();
        jPanel3 = new JPanel();
        panel13 = new JPanel();
        jLabel21 = new JLabel();
        jScrollPane1 = new JScrollPane();
        jlistINames = new JList<>();
        panel11 = new JPanel();
        label1 = new JLabel();
        panel17 = new JPanel();
        jbMoveMaterialUp = new JButton();
        jbMoveMaterialDown = new JButton();
        jLabel8 = new JLabel();
        jLabel7 = new JLabel();
        jLabel6 = new JLabel();
        jtfPaletteName = new JTextField();
        jtfTextureName = new JTextField();
        jtfMaterialName = new JTextField();
        jbMaterialName = new JButton();
        jbTextName = new JButton();
        jbPaletteName = new JButton();
        jLabel9 = new JLabel();
        jLabel11 = new JLabel();
        jLabel17 = new JLabel();
        jLabel18 = new JLabel();
        jLabel19 = new JLabel();
        jSpinner2 = new JSpinner();
        jcbTexGenMode = new JComboBox<>();
        jcbTexTilingU = new JComboBox<>();
        jcbTexTilingV = new JComboBox<>();
        jcbColorFormat = new JComboBox<>();
        panel12 = new JPanel();
        jcbEnableFog = new JCheckBox();
        jcbRenderFrontAndBack = new JCheckBox();
        jcbRenderBorder = new JCheckBox();
        jcbAlwaysIncludedInImd = new JCheckBox();
        jcbUniformNormal = new JCheckBox();
        jcbUseVertexColors = new JCheckBox();
        panel14 = new JPanel();
        jLabel20 = new JLabel();
        jcbL0 = new JCheckBox();
        jcbL1 = new JCheckBox();
        jcbL2 = new JCheckBox();
        jcbL3 = new JCheckBox();
        panel7 = new JPanel();
        jbReplaceTexture = new JButton();
        jbReplaceMaterial = new JButton();
        textureDisplayMaterial = new TextureDisplayMaterial();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tileset Editor");
        setIconImage(null);
        setMinimumSize(null);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== panel18 ========
        {
            panel18.setMinimumSize(new Dimension(200, 500));
            panel18.setPreferredSize(new Dimension(400, 450));
            panel18.setLayout(new BoxLayout(panel18, BoxLayout.Y_AXIS));

            //======== tileDisplay ========
            {
                tileDisplay.setBorder(new BevelBorder(BevelBorder.LOWERED));
                tileDisplay.setPreferredSize(null);
                tileDisplay.setMaximumSize(null);
                tileDisplay.setMinimumSize(null);

                GroupLayout tileDisplayLayout = new GroupLayout(tileDisplay);
                tileDisplay.setLayout(tileDisplayLayout);
                tileDisplayLayout.setHorizontalGroup(
                    tileDisplayLayout.createParallelGroup()
                        .addGap(0, 322, Short.MAX_VALUE)
                );
                tileDisplayLayout.setVerticalGroup(
                    tileDisplayLayout.createParallelGroup()
                        .addGap(0, 542, Short.MAX_VALUE)
                );
            }
            panel18.add(tileDisplay);

            //======== jTabbedPane2 ========
            {
                jTabbedPane2.setPreferredSize(null);
                jTabbedPane2.setMinimumSize(null);
                jTabbedPane2.setMaximumSize(null);

                //======== jPanel4 ========
                {
                    jPanel4.setMaximumSize(null);
                    jPanel4.setMinimumSize(null);
                    jPanel4.setPreferredSize(null);
                    jPanel4.setLayout(new MigLayout(
                        "fill",
                        // columns
                        "[grow]" +
                        "[grow,shrink 0,fill]" +
                        "[grow,shrink 0,fill]" +
                        "[grow,shrink 0,fill]" +
                        "[grow,shrink 0,fill]" +
                        "[grow,shrink 0,fill]",
                        // rows
                        "[shrink 0,center]" +
                        "[shrink 0,center]"));

                    //---- jLabel12 ----
                    jLabel12.setText("Rotate: ");
                    jLabel12.setMaximumSize(null);
                    jLabel12.setMinimumSize(null);
                    jLabel12.setPreferredSize(null);
                    jPanel4.add(jLabel12, "cell 0 0");

                    //---- jbRotateModel ----
                    jbRotateModel.setText("\u21ba");
                    jbRotateModel.setToolTipText("");
                    jbRotateModel.setMaximumSize(null);
                    jbRotateModel.setMinimumSize(null);
                    jbRotateModel.setPreferredSize(null);
                    jbRotateModel.addActionListener(e -> jbRotateModelActionPerformed(e));
                    jPanel4.add(jbRotateModel, "cell 1 0,dock center");

                    //---- jbMoveModelLeft ----
                    jbMoveModelLeft.setForeground(new Color(204, 0, 0));
                    jbMoveModelLeft.setMaximumSize(null);
                    jbMoveModelLeft.setMinimumSize(null);
                    jbMoveModelLeft.setPreferredSize(null);
                    jbMoveModelLeft.setIcon(new ImageIcon(getClass().getResource("/icons/leftRedIcon.png")));
                    jbMoveModelLeft.addActionListener(e -> jbMoveModelLeftActionPerformed(e));
                    jPanel4.add(jbMoveModelLeft, "cell 2 0 1 2,dock center");

                    //---- jbMoveModelUp ----
                    jbMoveModelUp.setForeground(new Color(0, 153, 0));
                    jbMoveModelUp.setMaximumSize(null);
                    jbMoveModelUp.setMinimumSize(null);
                    jbMoveModelUp.setPreferredSize(null);
                    jbMoveModelUp.setIcon(new ImageIcon(getClass().getResource("/icons/upGreenIcon.png")));
                    jbMoveModelUp.addActionListener(e -> jbMoveModelUpActionPerformed(e));
                    jPanel4.add(jbMoveModelUp, "cell 3 0,dock center");

                    //---- jbMoveModelRight ----
                    jbMoveModelRight.setForeground(new Color(204, 0, 0));
                    jbMoveModelRight.setMaximumSize(null);
                    jbMoveModelRight.setMinimumSize(null);
                    jbMoveModelRight.setPreferredSize(null);
                    jbMoveModelRight.setIcon(new ImageIcon(getClass().getResource("/icons/rightRedIcon.png")));
                    jbMoveModelRight.addActionListener(e -> jbMoveModelRightActionPerformed(e));
                    jPanel4.add(jbMoveModelRight, "cell 4 0 1 2,dock center");

                    //---- jbMoveModelUp1 ----
                    jbMoveModelUp1.setForeground(Color.blue);
                    jbMoveModelUp1.setMaximumSize(null);
                    jbMoveModelUp1.setMinimumSize(null);
                    jbMoveModelUp1.setPreferredSize(null);
                    jbMoveModelUp1.setIcon(new ImageIcon(getClass().getResource("/icons/upBlueIcon.png")));
                    jbMoveModelUp1.addActionListener(e -> jbMoveModelUp1ActionPerformed(e));
                    jPanel4.add(jbMoveModelUp1, "cell 5 0,dock center");

                    //---- jLabel13 ----
                    jLabel13.setText("Flip: ");
                    jLabel13.setMaximumSize(null);
                    jLabel13.setMinimumSize(null);
                    jLabel13.setPreferredSize(null);
                    jPanel4.add(jLabel13, "cell 0 1");

                    //---- jbFlipModel ----
                    jbFlipModel.setText("\u21c6");
                    jbFlipModel.setToolTipText("");
                    jbFlipModel.setEnabled(false);
                    jbFlipModel.setMaximumSize(null);
                    jbFlipModel.setMinimumSize(null);
                    jbFlipModel.setPreferredSize(null);
                    jbFlipModel.addActionListener(e -> jbFlipModelActionPerformed(e));
                    jPanel4.add(jbFlipModel, "cell 1 1,dock center");

                    //---- jbMoveModelDown ----
                    jbMoveModelDown.setForeground(new Color(0, 153, 0));
                    jbMoveModelDown.setMaximumSize(null);
                    jbMoveModelDown.setMinimumSize(null);
                    jbMoveModelDown.setPreferredSize(null);
                    jbMoveModelDown.setIcon(new ImageIcon(getClass().getResource("/icons/downGreenIcon.png")));
                    jbMoveModelDown.addActionListener(e -> jbMoveModelDownActionPerformed(e));
                    jPanel4.add(jbMoveModelDown, "cell 3 1,dock center");

                    //---- jbMoveModelDown1 ----
                    jbMoveModelDown1.setForeground(Color.blue);
                    jbMoveModelDown1.setMaximumSize(null);
                    jbMoveModelDown1.setMinimumSize(null);
                    jbMoveModelDown1.setPreferredSize(null);
                    jbMoveModelDown1.setIcon(new ImageIcon(getClass().getResource("/icons/downBlueIcon.png")));
                    jbMoveModelDown1.addActionListener(e -> jbMoveModelDown1ActionPerformed(e));
                    jPanel4.add(jbMoveModelDown1, "cell 5 1,dock center");
                }
                jTabbedPane2.addTab("Model Editor", jPanel4);

                //======== jPanel8 ========
                {
                    jPanel8.setMaximumSize(null);
                    jPanel8.setMinimumSize(null);
                    jPanel8.setPreferredSize(null);
                    jPanel8.setLayout(new MigLayout(
                        "insets 5 5 5 05,hidemode 3,gap 5 5",
                        // columns
                        "[grow,fill]" +
                        "[grow,fill]",
                        // rows
                        "[fill]" +
                        "[fill]" +
                        "[]"));

                    //---- jcbBackfaceCulling ----
                    jcbBackfaceCulling.setSelected(true);
                    jcbBackfaceCulling.setText("Backface Culling");
                    jcbBackfaceCulling.setMaximumSize(null);
                    jcbBackfaceCulling.setMinimumSize(null);
                    jcbBackfaceCulling.setPreferredSize(null);
                    jcbBackfaceCulling.addActionListener(e -> jcbBackfaceCullingActionPerformed(e));
                    jPanel8.add(jcbBackfaceCulling, "cell 0 0");

                    //---- jcbWireframe ----
                    jcbWireframe.setSelected(true);
                    jcbWireframe.setText("Wireframe");
                    jcbWireframe.setMaximumSize(null);
                    jcbWireframe.setMinimumSize(null);
                    jcbWireframe.setPreferredSize(null);
                    jcbWireframe.addActionListener(e -> jcbWireframeActionPerformed(e));
                    jPanel8.add(jcbWireframe, "cell 0 1");

                    //---- jcbTexturesEnabled ----
                    jcbTexturesEnabled.setSelected(true);
                    jcbTexturesEnabled.setText("Textures");
                    jcbTexturesEnabled.setMaximumSize(null);
                    jcbTexturesEnabled.setMinimumSize(null);
                    jcbTexturesEnabled.setPreferredSize(null);
                    jcbTexturesEnabled.addActionListener(e -> jcbTexturesEnabledActionPerformed(e));
                    jPanel8.add(jcbTexturesEnabled, "cell 1 0");

                    //---- jcbShadingEnabled ----
                    jcbShadingEnabled.setText("Shading");
                    jcbShadingEnabled.setMaximumSize(null);
                    jcbShadingEnabled.setMinimumSize(null);
                    jcbShadingEnabled.setPreferredSize(null);
                    jcbShadingEnabled.addActionListener(e -> jcbShadingEnabledActionPerformed(e));
                    jPanel8.add(jcbShadingEnabled, "cell 1 1");

                    //---- jcbNormals ----
                    jcbNormals.setText("Normals");
                    jcbNormals.addActionListener(e -> jcbNormalsActionPerformed(e));
                    jPanel8.add(jcbNormals, "cell 0 2");
                }
                jTabbedPane2.addTab("Display Settings", jPanel8);
            }
            panel18.add(jTabbedPane2);
        }
        contentPane.add(panel18);

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Tile Selector"));
            jPanel2.setMinimumSize(new Dimension(150, 31));

            //======== jScrollPane2 ========
            {
                jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //======== tileSelector ========
                {
                    tileSelector.setPreferredSize(new Dimension(128, 0));
                    tileSelector.setMaximumSize(null);
                    tileSelector.setMinimumSize(null);
                    tileSelector.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            tileSelectorMousePressed(e);
                        }
                    });

                    GroupLayout tileSelectorLayout = new GroupLayout(tileSelector);
                    tileSelector.setLayout(tileSelectorLayout);
                    tileSelectorLayout.setHorizontalGroup(
                        tileSelectorLayout.createParallelGroup()
                            .addGap(0, 128, Short.MAX_VALUE)
                    );
                    tileSelectorLayout.setVerticalGroup(
                        tileSelectorLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                }
                jScrollPane2.setViewportView(tileSelector);
            }

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
            );
        }
        contentPane.add(jPanel2);

        //======== jPanel9 ========
        {
            jPanel9.setBorder(new TitledBorder("Smart Drawing"));
            jPanel9.setMinimumSize(null);
            jPanel9.setMaximumSize(null);
            jPanel9.setPreferredSize(null);

            //======== jScrollPaneSmartGrid ========
            {
                jScrollPaneSmartGrid.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPaneSmartGrid.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPaneSmartGrid.setPreferredSize(null);
                jScrollPaneSmartGrid.setMinimumSize(null);
                jScrollPaneSmartGrid.setMaximumSize(null);

                //======== smartGridEditableDisplay ========
                {
                    smartGridEditableDisplay.setMaximumSize(null);
                    smartGridEditableDisplay.setMinimumSize(null);
                    smartGridEditableDisplay.setPreferredSize(null);

                    GroupLayout smartGridEditableDisplayLayout = new GroupLayout(smartGridEditableDisplay);
                    smartGridEditableDisplay.setLayout(smartGridEditableDisplayLayout);
                    smartGridEditableDisplayLayout.setHorizontalGroup(
                        smartGridEditableDisplayLayout.createParallelGroup()
                            .addGap(0, 188, Short.MAX_VALUE)
                    );
                    smartGridEditableDisplayLayout.setVerticalGroup(
                        smartGridEditableDisplayLayout.createParallelGroup()
                            .addGap(0, 587, Short.MAX_VALUE)
                    );
                }
                jScrollPaneSmartGrid.setViewportView(smartGridEditableDisplay);
            }

            //======== panel9 ========
            {
                panel9.setMinimumSize(new Dimension(0, 15));
                panel9.setPreferredSize(new Dimension(0, 15));
                panel9.setMaximumSize(null);
                panel9.setLayout(new GridLayout(2, 2, 5, 5));

                //---- jbMoveSPaintUp ----
                jbMoveSPaintUp.setText("\u25b2");
                jbMoveSPaintUp.setFocusable(false);
                jbMoveSPaintUp.addActionListener(e -> jbMoveSPaintUpActionPerformed(e));
                panel9.add(jbMoveSPaintUp);

                //---- jbAddSmartGrid ----
                jbAddSmartGrid.setText("+");
                jbAddSmartGrid.setFocusable(false);
                jbAddSmartGrid.addActionListener(e -> jbAddSmartGridActionPerformed(e));
                panel9.add(jbAddSmartGrid);

                //---- jbMoveSPaintDown ----
                jbMoveSPaintDown.setText("\u25bc");
                jbMoveSPaintDown.setFocusable(false);
                jbMoveSPaintDown.addActionListener(e -> jbMoveSPaintDownActionPerformed(e));
                panel9.add(jbMoveSPaintDown);

                //---- jbRemoveSmartGrid ----
                jbRemoveSmartGrid.setText("-");
                jbRemoveSmartGrid.setFocusable(false);
                jbRemoveSmartGrid.addActionListener(e -> jbRemoveSmartGridActionPerformed(e));
                panel9.add(jbRemoveSmartGrid);
            }

            GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
            jPanel9.setLayout(jPanel9Layout);
            jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup()
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(panel9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPaneSmartGrid, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
                        .addGap(0, 0, 0))
            );
            jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup()
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPaneSmartGrid, GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel9, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
            );
        }
        contentPane.add(jPanel9);

        //======== jTabbedPane1 ========
        {
            jTabbedPane1.setMinimumSize(null);
            jTabbedPane1.setMaximumSize(null);
            jTabbedPane1.setPreferredSize(null);

            //======== jPanel1 ========
            {
                jPanel1.setMinimumSize(null);
                jPanel1.setPreferredSize(null);
                jPanel1.setMaximumSize(null);
                jPanel1.setLayout(new MigLayout(
                    "",
                    // columns
                    "[shrink 0,fill]",
                    // rows
                    "0[fill]0" +
                    "[fill]0" +
                    "[fill]0" +
                    "[fill]0" +
                    "[128:n,grow,shrink 0,fill]"));

                //======== panel8 ========
                {
                    panel8.setMaximumSize(null);
                    panel8.setMinimumSize(null);
                    panel8.setPreferredSize(null);
                    panel8.setLayout(new MigLayout(
                        "insets 0,hidemode 3,gap 10 0",
                        // columns
                        "[fill]" +
                        "[210:n,grow,fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]",
                        // rows
                        "[fill]"));

                    //---- jLabel3 ----
                    jLabel3.setText("Tile selected: ");
                    jLabel3.setMaximumSize(null);
                    jLabel3.setMinimumSize(null);
                    jLabel3.setPreferredSize(null);
                    panel8.add(jLabel3, "cell 0 0");

                    //---- jtfIndexTile ----
                    jtfIndexTile.setHorizontalAlignment(SwingConstants.CENTER);
                    jtfIndexTile.setEnabled(false);
                    jtfIndexTile.setMaximumSize(null);
                    jtfIndexTile.setMinimumSize(null);
                    jtfIndexTile.setPreferredSize(null);
                    panel8.add(jtfIndexTile, "cell 1 0");

                    //---- jLabel23 ----
                    jLabel23.setText("Move tile:");
                    jLabel23.setMaximumSize(null);
                    jLabel23.setMinimumSize(null);
                    jLabel23.setPreferredSize(null);
                    panel8.add(jLabel23, "cell 2 0");

                    //---- jbMoveUp ----
                    jbMoveUp.setText("\u25b2");
                    jbMoveUp.setMaximumSize(null);
                    jbMoveUp.setMinimumSize(null);
                    jbMoveUp.setPreferredSize(null);
                    jbMoveUp.addActionListener(e -> jbMoveUpActionPerformed(e));
                    panel8.add(jbMoveUp, "cell 3 0");

                    //---- jbMoveDown ----
                    jbMoveDown.setText("\u25bc");
                    jbMoveDown.setMaximumSize(null);
                    jbMoveDown.setMinimumSize(null);
                    jbMoveDown.setPreferredSize(null);
                    jbMoveDown.addActionListener(e -> jbMoveDownActionPerformed(e));
                    panel8.add(jbMoveDown, "cell 4 0");
                }
                jPanel1.add(panel8, "cell 0 0,gapx 5 5,gapy 10 5");

                //======== panel1 ========
                {
                    panel1.setMaximumSize(null);
                    panel1.setMinimumSize(null);
                    panel1.setPreferredSize(null);
                    panel1.setLayout(new MigLayout(
                        "insets 0,hidemode 3,gap 5 0",
                        // columns
                        "[grow,fill]" +
                        "[grow,fill]" +
                        "[129,grow,fill]" +
                        "[grow,fill]",
                        // rows
                        "[grow,fill]"));

                    //---- jbAddTile ----
                    jbAddTile.setIcon(new ImageIcon(getClass().getResource("/icons/AddTileIcon.png")));
                    jbAddTile.setText("Add Tiles...");
                    jbAddTile.setMaximumSize(null);
                    jbAddTile.setMinimumSize(null);
                    jbAddTile.setPreferredSize(null);
                    jbAddTile.addActionListener(e -> jbAddTileActionPerformed(e));
                    panel1.add(jbAddTile, "cell 0 0");

                    //---- jbRemoveTile ----
                    jbRemoveTile.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveTileIcon.png")));
                    jbRemoveTile.setText("Remove Tiles");
                    jbRemoveTile.setMaximumSize(null);
                    jbRemoveTile.setMinimumSize(null);
                    jbRemoveTile.setPreferredSize(null);
                    jbRemoveTile.addActionListener(e -> jbRemoveTileActionPerformed(e));
                    panel1.add(jbRemoveTile, "cell 1 0");

                    //---- jbDuplicateTile ----
                    jbDuplicateTile.setIcon(new ImageIcon(getClass().getResource("/icons/DuplicateTileIcon.png")));
                    jbDuplicateTile.setText("Duplicate Tiles");
                    jbDuplicateTile.setMaximumSize(null);
                    jbDuplicateTile.setMinimumSize(null);
                    jbDuplicateTile.setPreferredSize(null);
                    jbDuplicateTile.addActionListener(e -> jbDuplicateTileActionPerformed(e));
                    panel1.add(jbDuplicateTile, "cell 2 0");

                    //---- jbImportTiles ----
                    jbImportTiles.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                    jbImportTiles.setText("Import Tiles...");
                    jbImportTiles.setMaximumSize(null);
                    jbImportTiles.setMinimumSize(null);
                    jbImportTiles.setPreferredSize(null);
                    jbImportTiles.addActionListener(e -> jbImportTilesActionPerformed(e));
                    panel1.add(jbImportTiles, "cell 3 0");
                }
                jPanel1.add(panel1, "cell 0 1,gapx 5 5,gapy 5 5");

                //======== jPanel5 ========
                {
                    jPanel5.setBorder(new TitledBorder("Tile Properties"));
                    jPanel5.setMaximumSize(null);
                    jPanel5.setMinimumSize(null);
                    jPanel5.setPreferredSize(null);
                    jPanel5.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 10",
                        // columns
                        "[313,grow,fill]" +
                        "[fill]",
                        // rows
                        "[]" +
                        "[fill]" +
                        "[fill]"));

                    //======== panel2 ========
                    {
                        panel2.setMaximumSize(null);
                        panel2.setMinimumSize(null);
                        panel2.setPreferredSize(null);
                        panel2.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[grow,fill]" +
                            "[42:67,grow,fill]" +
                            "[grow,fill]" +
                            "[55:n,grow,fill]",
                            // rows
                            "[grow,fill]" +
                            "[grow,fill]"));

                        //---- jLabel1 ----
                        jLabel1.setForeground(new Color(204, 0, 0));
                        jLabel1.setText("X Size:");
                        jLabel1.setMaximumSize(null);
                        jLabel1.setMinimumSize(null);
                        jLabel1.setPreferredSize(null);
                        panel2.add(jLabel1, "cell 0 0");

                        //---- jbLessSizeX ----
                        jbLessSizeX.setText("<");
                        jbLessSizeX.setMaximumSize(null);
                        jbLessSizeX.setMinimumSize(null);
                        jbLessSizeX.setPreferredSize(null);
                        jbLessSizeX.addActionListener(e -> jbLessSizeXActionPerformed(e));
                        panel2.add(jbLessSizeX, "cell 1 0");

                        //---- jtfSizeX ----
                        jtfSizeX.setEditable(false);
                        jtfSizeX.setHorizontalAlignment(SwingConstants.CENTER);
                        jtfSizeX.setMaximumSize(null);
                        jtfSizeX.setMinimumSize(null);
                        jtfSizeX.setPreferredSize(null);
                        panel2.add(jtfSizeX, "cell 2 0");

                        //---- jbMoreSizeX ----
                        jbMoreSizeX.setText(">");
                        jbMoreSizeX.setMinimumSize(null);
                        jbMoreSizeX.setMaximumSize(null);
                        jbMoreSizeX.setPreferredSize(null);
                        jbMoreSizeX.addActionListener(e -> jbMoreSizeXActionPerformed(e));
                        panel2.add(jbMoreSizeX, "cell 3 0");

                        //---- jLabel2 ----
                        jLabel2.setForeground(new Color(0, 153, 0));
                        jLabel2.setText("Y Size:");
                        jLabel2.setMaximumSize(null);
                        jLabel2.setMinimumSize(null);
                        jLabel2.setPreferredSize(null);
                        panel2.add(jLabel2, "cell 0 1");

                        //---- jbLessSizeY ----
                        jbLessSizeY.setText("<");
                        jbLessSizeY.setMaximumSize(null);
                        jbLessSizeY.setMinimumSize(null);
                        jbLessSizeY.setPreferredSize(null);
                        jbLessSizeY.addActionListener(e -> jbLessSizeYActionPerformed(e));
                        panel2.add(jbLessSizeY, "cell 1 1");

                        //---- jtfSizeY ----
                        jtfSizeY.setEditable(false);
                        jtfSizeY.setHorizontalAlignment(SwingConstants.CENTER);
                        jtfSizeY.setMaximumSize(null);
                        jtfSizeY.setMinimumSize(null);
                        jtfSizeY.setPreferredSize(null);
                        panel2.add(jtfSizeY, "cell 2 1");

                        //---- jbMoreSizeY ----
                        jbMoreSizeY.setText(">");
                        jbMoreSizeY.setMinimumSize(null);
                        jbMoreSizeY.setMaximumSize(null);
                        jbMoreSizeY.setPreferredSize(null);
                        jbMoreSizeY.addActionListener(e -> jbMoreSizeYActionPerformed(e));
                        panel2.add(jbMoreSizeY, "cell 3 1");
                    }
                    jPanel5.add(panel2, "cell 0 0,wmin 4,hmin 2");

                    //======== panel4 ========
                    {
                        panel4.setMaximumSize(null);
                        panel4.setMinimumSize(null);
                        panel4.setPreferredSize(null);
                        panel4.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[grow,fill]" +
                            "[grow,fill]",
                            // rows
                            "[grow,fill]" +
                            "[grow,fill]"));

                        //---- jcbTileableX ----
                        jcbTileableX.setForeground(new Color(204, 0, 0));
                        jcbTileableX.setText("X Tileable");
                        jcbTileableX.setMaximumSize(null);
                        jcbTileableX.setMinimumSize(null);
                        jcbTileableX.setPreferredSize(null);
                        jcbTileableX.addActionListener(e -> jcbTileableXActionPerformed(e));
                        panel4.add(jcbTileableX, "cell 0 0");

                        //---- jcbUtileable ----
                        jcbUtileable.setForeground(new Color(204, 0, 0));
                        jcbUtileable.setText("Texture U Tileable");
                        jcbUtileable.setMaximumSize(null);
                        jcbUtileable.setMinimumSize(null);
                        jcbUtileable.setPreferredSize(null);
                        jcbUtileable.addActionListener(e -> jcbUtileableActionPerformed(e));
                        panel4.add(jcbUtileable, "cell 1 0");

                        //---- jcbTileableY ----
                        jcbTileableY.setForeground(new Color(0, 153, 0));
                        jcbTileableY.setText("Y Tileable");
                        jcbTileableY.setMaximumSize(null);
                        jcbTileableY.setMinimumSize(null);
                        jcbTileableY.setPreferredSize(null);
                        jcbTileableY.addActionListener(e -> jcbTileableYActionPerformed(e));
                        panel4.add(jcbTileableY, "cell 0 1");

                        //---- jcbVtileable ----
                        jcbVtileable.setForeground(new Color(0, 153, 0));
                        jcbVtileable.setText("Texture V Tileable");
                        jcbVtileable.setMaximumSize(null);
                        jcbVtileable.setMinimumSize(null);
                        jcbVtileable.setPreferredSize(null);
                        jcbVtileable.addActionListener(e -> jcbVtileableActionPerformed(e));
                        panel4.add(jcbVtileable, "cell 1 1");
                    }
                    jPanel5.add(panel4, "cell 1 0,gapx 5 5,gapy 5 5");

                    //======== panel3 ========
                    {
                        panel3.setMaximumSize(null);
                        panel3.setMinimumSize(null);
                        panel3.setPreferredSize(null);
                        panel3.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[fill]" +
                            "[133:n,grow,fill]" +
                            "[fill]" +
                            "[54:n,fill]" +
                            "[117:n,grow,fill]" +
                            "[fill]",
                            // rows
                            "[fill]"));

                        //---- jLabel14 ----
                        jLabel14.setForeground(new Color(204, 0, 0));
                        jLabel14.setText("X Offset: ");
                        jLabel14.setMaximumSize(null);
                        jLabel14.setMinimumSize(null);
                        jLabel14.setPreferredSize(null);
                        panel3.add(jLabel14, "cell 0 0");

                        //---- jtfXOffset ----
                        jtfXOffset.setText(" ");
                        jtfXOffset.setMaximumSize(null);
                        jtfXOffset.setMinimumSize(null);
                        jtfXOffset.setPreferredSize(null);
                        jtfXOffset.setForeground(UIManager.getColor("TextPane.foreground"));
                        panel3.add(jtfXOffset, "cell 1 0");

                        //---- jbXOffset ----
                        jbXOffset.setText("Apply");
                        jbXOffset.setMaximumSize(null);
                        jbXOffset.setMinimumSize(null);
                        jbXOffset.setPreferredSize(null);
                        jbXOffset.addActionListener(e -> jbXOffsetActionPerformed(e));
                        panel3.add(jbXOffset, "cell 2 0");

                        //---- jLabel15 ----
                        jLabel15.setForeground(new Color(0, 153, 0));
                        jLabel15.setText("Y Offset: ");
                        jLabel15.setMaximumSize(null);
                        jLabel15.setMinimumSize(null);
                        jLabel15.setPreferredSize(null);
                        panel3.add(jLabel15, "cell 3 0");

                        //---- jtfYOffset ----
                        jtfYOffset.setText(" ");
                        jtfYOffset.setMaximumSize(null);
                        jtfYOffset.setMinimumSize(null);
                        jtfYOffset.setPreferredSize(null);
                        panel3.add(jtfYOffset, "cell 4 0");

                        //---- jbYOffset ----
                        jbYOffset.setText("Apply");
                        jbYOffset.setMaximumSize(null);
                        jbYOffset.setMinimumSize(null);
                        jbYOffset.setPreferredSize(null);
                        jbYOffset.addActionListener(e -> jbYOffsetActionPerformed(e));
                        panel3.add(jbYOffset, "cell 5 0");
                    }
                    jPanel5.add(panel3, "cell 0 1 2 1");

                    //======== panel5 ========
                    {
                        panel5.setMaximumSize(null);
                        panel5.setMinimumSize(null);
                        panel5.setPreferredSize(null);
                        panel5.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[172,grow,fill]" +
                            "[grow,fill]" +
                            "[121,grow,fill]" +
                            "[grow,fill]",
                            // rows
                            "[grow,fill]"));

                        //---- jcbGlobalTexMapping ----
                        jcbGlobalTexMapping.setText("Global Texture Mapping");
                        jcbGlobalTexMapping.setMinimumSize(null);
                        jcbGlobalTexMapping.setMaximumSize(null);
                        jcbGlobalTexMapping.setPreferredSize(null);
                        jcbGlobalTexMapping.addActionListener(e -> jcbGlobalTexMappingActionPerformed(e));
                        panel5.add(jcbGlobalTexMapping, "cell 0 0");

                        //---- jLabel10 ----
                        jLabel10.setText("Texture Scale:");
                        jLabel10.setMaximumSize(null);
                        jLabel10.setMinimumSize(null);
                        jLabel10.setPreferredSize(null);
                        panel5.add(jLabel10, "cell 1 0");

                        //---- jtfGlobalTexScale ----
                        jtfGlobalTexScale.setText(" ");
                        jtfGlobalTexScale.setMaximumSize(null);
                        jtfGlobalTexScale.setMinimumSize(null);
                        jtfGlobalTexScale.setPreferredSize(null);
                        panel5.add(jtfGlobalTexScale, "cell 2 0");

                        //---- jbGlobalTexScale ----
                        jbGlobalTexScale.setText("Apply");
                        jbGlobalTexScale.setMaximumSize(null);
                        jbGlobalTexScale.setMinimumSize(null);
                        jbGlobalTexScale.setPreferredSize(null);
                        jbGlobalTexScale.addActionListener(e -> jbGlobalTexScaleActionPerformed(e));
                        panel5.add(jbGlobalTexScale, "cell 3 0");
                    }
                    jPanel5.add(panel5, "cell 0 2 2 1,alignx left,growx 0");
                }
                jPanel1.add(jPanel5, "cell 0 2,gapx 5 5,gapy 5 5");

                //======== jPanel6 ========
                {
                    jPanel6.setBorder(new TitledBorder("Tile 3D Model"));
                    jPanel6.setMaximumSize(null);
                    jPanel6.setMinimumSize(null);
                    jPanel6.setPreferredSize(null);
                    jPanel6.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[fill]" +
                        "[grow,fill]",
                        // rows
                        "[fill]" +
                        "[fill]"));

                    //---- jLabel5 ----
                    jLabel5.setText("Model name:");
                    jLabel5.setMaximumSize(null);
                    jLabel5.setMinimumSize(null);
                    jLabel5.setPreferredSize(null);
                    jPanel6.add(jLabel5, "cell 0 0");

                    //---- jtfObjName ----
                    jtfObjName.setEditable(false);
                    jtfObjName.setColumns(17);
                    jtfObjName.setText(" ");
                    jtfObjName.setEnabled(false);
                    jtfObjName.setMaximumSize(null);
                    jtfObjName.setMinimumSize(null);
                    jtfObjName.setPreferredSize(null);
                    jPanel6.add(jtfObjName, "cell 1 0");

                    //======== panel10 ========
                    {
                        panel10.setMaximumSize(null);
                        panel10.setMinimumSize(null);
                        panel10.setPreferredSize(null);
                        panel10.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[grow,fill]" +
                            "[grow,fill]" +
                            "[grow,fill]",
                            // rows
                            "[grow,fill]"));

                        //---- jbExportTileAsObj ----
                        jbExportTileAsObj.setIcon(new ImageIcon(getClass().getResource("/icons/ExportTileIcon.png")));
                        jbExportTileAsObj.setText("Export OBJ...");
                        jbExportTileAsObj.setMaximumSize(null);
                        jbExportTileAsObj.setMinimumSize(null);
                        jbExportTileAsObj.setPreferredSize(null);
                        jbExportTileAsObj.addActionListener(e -> jbExportTileAsObjActionPerformed(e));
                        panel10.add(jbExportTileAsObj, "cell 0 0");

                        //---- jbImportTileAsObj ----
                        jbImportTileAsObj.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                        jbImportTileAsObj.setText("Replace OBJ...");
                        jbImportTileAsObj.setMaximumSize(null);
                        jbImportTileAsObj.setMinimumSize(null);
                        jbImportTileAsObj.setPreferredSize(null);
                        jbImportTileAsObj.addActionListener(e -> jbImportTileAsObjActionPerformed(e));
                        panel10.add(jbImportTileAsObj, "cell 1 0");

                        //---- jbEditVertexColors ----
                        jbEditVertexColors.setIcon(new ImageIcon(getClass().getResource("/icons/VertexColorEditorIcon.png")));
                        jbEditVertexColors.setText("Edit Vertex Colors...");
                        jbEditVertexColors.setMaximumSize(null);
                        jbEditVertexColors.setMinimumSize(null);
                        jbEditVertexColors.setPreferredSize(null);
                        jbEditVertexColors.addActionListener(e -> jbEditVertexColorsActionPerformed(e));
                        panel10.add(jbEditVertexColors, "cell 2 0");
                    }
                    jPanel6.add(panel10, "cell 1 1");
                }
                jPanel1.add(jPanel6, "cell 0 3,gapx 5 5,gapy 5 5");

                //======== jPanel7 ========
                {
                    jPanel7.setBorder(new TitledBorder("Tile Materials"));
                    jPanel7.setMaximumSize(null);
                    jPanel7.setMinimumSize(null);
                    jPanel7.setPreferredSize(null);
                    jPanel7.setLayout(new MigLayout(
                        "insets 5,gap 5 5",
                        // columns
                        "[grow,fill]",
                        // rows
                        "[128:121,grow,shrink 0,fill]"));

                    //======== panel6 ========
                    {
                        panel6.setMaximumSize(null);
                        panel6.setMinimumSize(null);
                        panel6.setPreferredSize(null);
                        panel6.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[158,grow]" +
                            "[171,fill]" +
                            "[fill]",
                            // rows
                            "[fill]" +
                            "[]" +
                            "[fill]" +
                            "[]" +
                            "[128:128,grow,shrink 0,fill]"));

                        //---- jLabel22 ----
                        jLabel22.setText("Materials used by the Tile:");
                        jLabel22.setMaximumSize(null);
                        jLabel22.setMinimumSize(null);
                        jLabel22.setPreferredSize(null);
                        panel6.add(jLabel22, "cell 0 1");

                        //---- label2 ----
                        label2.setText("Change Material:");
                        panel6.add(label2, "cell 2 1");

                        //---- jbAddTexture ----
                        jbAddTexture.setIcon(new ImageIcon(getClass().getResource("/icons/AddTileIcon.png")));
                        jbAddTexture.setText("Add texture...");
                        jbAddTexture.setMaximumSize(null);
                        jbAddTexture.setMinimumSize(null);
                        jbAddTexture.setPreferredSize(null);
                        jbAddTexture.addActionListener(e -> jbAddTextureActionPerformed(e));
                        panel6.add(jbAddTexture, "cell 2 3");

                        //---- jcbMaterial ----
                        jcbMaterial.setModel(new DefaultComboBoxModel<>(new String[] {

                        }));
                        jcbMaterial.setMaximumSize(null);
                        jcbMaterial.setMinimumSize(null);
                        jcbMaterial.setPreferredSize(null);
                        jcbMaterial.addActionListener(e -> jcbMaterialActionPerformed(e));
                        panel6.add(jcbMaterial, "cell 2 2");

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                            scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                            //---- jlTileMaterials ----
                            jlTileMaterials.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            jlTileMaterials.addListSelectionListener(e -> jlTileMaterialsValueChanged(e));
                            scrollPane1.setViewportView(jlTileMaterials);
                        }
                        panel6.add(scrollPane1, "cell 0 2 1 3,grow");

                        //======== textureDisplay ========
                        {
                            textureDisplay.setBorder(LineBorder.createBlackLineBorder());
                            textureDisplay.setMaximumSize(null);
                            textureDisplay.setMinimumSize(null);
                            textureDisplay.setPreferredSize(new Dimension(127, 127));
                            textureDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                            GroupLayout textureDisplayLayout = new GroupLayout(textureDisplay);
                            textureDisplay.setLayout(textureDisplayLayout);
                            textureDisplayLayout.setHorizontalGroup(
                                textureDisplayLayout.createParallelGroup()
                                    .addGap(0, 125, Short.MAX_VALUE)
                            );
                            textureDisplayLayout.setVerticalGroup(
                                textureDisplayLayout.createParallelGroup()
                                    .addGap(0, 125, Short.MAX_VALUE)
                            );
                        }
                        panel6.add(textureDisplay, "cell 2 4,align center top,grow 0 0");
                    }
                    jPanel7.add(panel6, "cell 0 0");
                }
                jPanel1.add(jPanel7, "cell 0 4,gapx 5 5,gapy 5 5");
            }
            jTabbedPane1.addTab("Tile Editor", jPanel1);

            //======== jPanel3 ========
            {
                jPanel3.setMinimumSize(null);
                jPanel3.setPreferredSize(null);
                jPanel3.setMaximumSize(null);
                jPanel3.setLayout(new MigLayout(
                    null,
                    // columns
                    "[130:144,shrink 0,fill]" +
                    "[shrink 0,fill]",
                    // rows
                    "[grow,fill]"));

                //======== panel13 ========
                {
                    panel13.setMinimumSize(new Dimension(120, 0));
                    panel13.setMaximumSize(null);
                    panel13.setPreferredSize(null);
                    panel13.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel13.getLayout()).columnWidths = new int[] {151, 0};
                    ((GridBagLayout)panel13.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)panel13.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)panel13.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                    //---- jLabel21 ----
                    jLabel21.setText("Material list:");
                    jLabel21.setMaximumSize(null);
                    jLabel21.setMinimumSize(null);
                    jLabel21.setPreferredSize(null);
                    panel13.add(jLabel21, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== jScrollPane1 ========
                    {
                        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                        jScrollPane1.setMaximumSize(null);
                        jScrollPane1.setMinimumSize(null);
                        jScrollPane1.setPreferredSize(null);

                        //---- jlistINames ----
                        jlistINames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        jlistINames.setMaximumSize(null);
                        jlistINames.setMinimumSize(null);
                        jlistINames.setPreferredSize(null);
                        jlistINames.addListSelectionListener(e -> jlistINamesValueChanged(e));
                        jScrollPane1.setViewportView(jlistINames);
                    }
                    panel13.add(jScrollPane1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                jPanel3.add(panel13, "cell 0 0");

                //======== panel11 ========
                {
                    panel11.setMaximumSize(null);
                    panel11.setMinimumSize(null);
                    panel11.setPreferredSize(null);
                    panel11.setLayout(new MigLayout(
                        "insets 0,gap 5 5",
                        // columns
                        "[96:n,fill]" +
                        "[134:95,shrink 0,fill]" +
                        "[shrink 0,fill]",
                        // rows
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[181:163,grow,fill]"));

                    //---- label1 ----
                    label1.setText("Move Material:");
                    panel11.add(label1, "cell 0 0");

                    //======== panel17 ========
                    {
                        panel17.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 10 0",
                            // columns
                            "[shrink 0,fill]" +
                            "[shrink 0,fill]",
                            // rows
                            "[grow,fill]"));

                        //---- jbMoveMaterialUp ----
                        jbMoveMaterialUp.setText("\u25b2");
                        jbMoveMaterialUp.setMaximumSize(null);
                        jbMoveMaterialUp.setMinimumSize(null);
                        jbMoveMaterialUp.setPreferredSize(null);
                        jbMoveMaterialUp.addActionListener(e -> jbMoveMaterialUpActionPerformed(e));
                        panel17.add(jbMoveMaterialUp, "cell 0 0");

                        //---- jbMoveMaterialDown ----
                        jbMoveMaterialDown.setText("\u25bc");
                        jbMoveMaterialDown.setMaximumSize(null);
                        jbMoveMaterialDown.setMinimumSize(null);
                        jbMoveMaterialDown.setPreferredSize(null);
                        jbMoveMaterialDown.addActionListener(e -> jbMoveMaterialDownActionPerformed(e));
                        panel17.add(jbMoveMaterialDown, "cell 1 0");
                    }
                    panel11.add(panel17, "cell 1 0");

                    //---- jLabel8 ----
                    jLabel8.setText("Material Name:");
                    jLabel8.setMaximumSize(null);
                    jLabel8.setMinimumSize(null);
                    jLabel8.setPreferredSize(null);
                    panel11.add(jLabel8, "cell 0 1");

                    //---- jLabel7 ----
                    jLabel7.setText("Texture Name:");
                    jLabel7.setMaximumSize(null);
                    jLabel7.setMinimumSize(null);
                    jLabel7.setPreferredSize(null);
                    panel11.add(jLabel7, "cell 0 2");

                    //---- jLabel6 ----
                    jLabel6.setText("Palette Name:");
                    jLabel6.setMaximumSize(null);
                    jLabel6.setMinimumSize(null);
                    jLabel6.setPreferredSize(null);
                    panel11.add(jLabel6, "cell 0 3");

                    //---- jtfPaletteName ----
                    jtfPaletteName.setText(" ");
                    jtfPaletteName.setMaximumSize(null);
                    jtfPaletteName.setMinimumSize(null);
                    jtfPaletteName.setPreferredSize(null);
                    jtfPaletteName.addActionListener(e -> jtfPaletteNameActionPerformed(e));
                    panel11.add(jtfPaletteName, "cell 1 3");

                    //---- jtfTextureName ----
                    jtfTextureName.setText(" ");
                    jtfTextureName.setMaximumSize(null);
                    jtfTextureName.setMinimumSize(null);
                    jtfTextureName.setPreferredSize(null);
                    jtfTextureName.addActionListener(e -> jtfTextureNameActionPerformed(e));
                    panel11.add(jtfTextureName, "cell 1 2");

                    //---- jtfMaterialName ----
                    jtfMaterialName.setText(" ");
                    jtfMaterialName.setMaximumSize(null);
                    jtfMaterialName.setMinimumSize(null);
                    jtfMaterialName.setPreferredSize(null);
                    jtfMaterialName.addActionListener(e -> jtfMaterialNameActionPerformed(e));
                    panel11.add(jtfMaterialName, "cell 1 1");

                    //---- jbMaterialName ----
                    jbMaterialName.setText("Apply");
                    jbMaterialName.setMaximumSize(null);
                    jbMaterialName.setMinimumSize(null);
                    jbMaterialName.setPreferredSize(null);
                    jbMaterialName.addActionListener(e -> jbMaterialNameActionPerformed(e));
                    panel11.add(jbMaterialName, "cell 2 1");

                    //---- jbTextName ----
                    jbTextName.setText("Apply");
                    jbTextName.setMaximumSize(null);
                    jbTextName.setMinimumSize(null);
                    jbTextName.setPreferredSize(null);
                    jbTextName.addActionListener(e -> jbTextNameActionPerformed(e));
                    panel11.add(jbTextName, "cell 2 2");

                    //---- jbPaletteName ----
                    jbPaletteName.setText("Apply");
                    jbPaletteName.setMaximumSize(null);
                    jbPaletteName.setMinimumSize(null);
                    jbPaletteName.setPreferredSize(null);
                    jbPaletteName.addActionListener(e -> jbPaletteNameActionPerformed(e));
                    panel11.add(jbPaletteName, "cell 2 3");

                    //---- jLabel9 ----
                    jLabel9.setText("Alpha: ");
                    jLabel9.setMaximumSize(null);
                    jLabel9.setMinimumSize(null);
                    jLabel9.setPreferredSize(null);
                    panel11.add(jLabel9, "cell 0 4");

                    //---- jLabel11 ----
                    jLabel11.setText("Tex Gen Mode: ");
                    jLabel11.setMaximumSize(null);
                    jLabel11.setMinimumSize(null);
                    jLabel11.setPreferredSize(null);
                    panel11.add(jLabel11, "cell 0 5");

                    //---- jLabel17 ----
                    jLabel17.setText("Tex Tiling U: ");
                    jLabel17.setMaximumSize(null);
                    jLabel17.setMinimumSize(null);
                    jLabel17.setPreferredSize(null);
                    panel11.add(jLabel17, "cell 0 6");

                    //---- jLabel18 ----
                    jLabel18.setText("Tex Tiling V: ");
                    jLabel18.setMaximumSize(null);
                    jLabel18.setMinimumSize(null);
                    jLabel18.setPreferredSize(null);
                    panel11.add(jLabel18, "cell 0 7");

                    //---- jLabel19 ----
                    jLabel19.setText("Color Format:");
                    jLabel19.setMaximumSize(null);
                    jLabel19.setMinimumSize(null);
                    jLabel19.setPreferredSize(null);
                    panel11.add(jLabel19, "cell 0 8");

                    //---- jSpinner2 ----
                    jSpinner2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
                    jSpinner2.setMaximumSize(null);
                    jSpinner2.setMinimumSize(null);
                    jSpinner2.setPreferredSize(null);
                    jSpinner2.addChangeListener(e -> jSpinner2StateChanged(e));
                    panel11.add(jSpinner2, "cell 1 4 2 1");

                    //---- jcbTexGenMode ----
                    jcbTexGenMode.setMaximumRowCount(4);
                    jcbTexGenMode.setModel(new DefaultComboBoxModel<>(new String[] {
                        "None",
                        "Texture",
                        "Normal",
                        "Vertex"
                    }));
                    jcbTexGenMode.setMaximumSize(null);
                    jcbTexGenMode.setMinimumSize(null);
                    jcbTexGenMode.setPreferredSize(null);
                    jcbTexGenMode.addActionListener(e -> jcbTexGenModeActionPerformed(e));
                    panel11.add(jcbTexGenMode, "cell 1 5 2 1");

                    //---- jcbTexTilingU ----
                    jcbTexTilingU.setMaximumRowCount(4);
                    jcbTexTilingU.setModel(new DefaultComboBoxModel<>(new String[] {
                        "Repeat",
                        "Clamp",
                        "Flip"
                    }));
                    jcbTexTilingU.setMaximumSize(null);
                    jcbTexTilingU.setMinimumSize(null);
                    jcbTexTilingU.setPreferredSize(null);
                    jcbTexTilingU.addActionListener(e -> jcbTexTilingUActionPerformed(e));
                    panel11.add(jcbTexTilingU, "cell 1 6 2 1");

                    //---- jcbTexTilingV ----
                    jcbTexTilingV.setMaximumRowCount(4);
                    jcbTexTilingV.setModel(new DefaultComboBoxModel<>(new String[] {
                        "Repeat",
                        "Clamp",
                        "Flip"
                    }));
                    jcbTexTilingV.setMaximumSize(null);
                    jcbTexTilingV.setMinimumSize(null);
                    jcbTexTilingV.setPreferredSize(null);
                    jcbTexTilingV.addActionListener(e -> jcbTexTilingVActionPerformed(e));
                    panel11.add(jcbTexTilingV, "cell 1 7 2 1");

                    //---- jcbColorFormat ----
                    jcbColorFormat.setModel(new DefaultComboBoxModel<>(new String[] {
                        "Palette 4",
                        "Palette 16",
                        "Palette 256",
                        "A3I5",
                        "A5I3"
                    }));
                    jcbColorFormat.setMaximumSize(null);
                    jcbColorFormat.setMinimumSize(null);
                    jcbColorFormat.setPreferredSize(null);
                    jcbColorFormat.addActionListener(e -> jcbColorFormatActionPerformed(e));
                    panel11.add(jcbColorFormat, "cell 1 8 2 1");

                    //======== panel12 ========
                    {
                        panel12.setMaximumSize(null);
                        panel12.setMinimumSize(null);
                        panel12.setPreferredSize(null);
                        panel12.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[242:n,shrink 0,fill]",
                            // rows
                            "[center]" +
                            "[center]" +
                            "[center]" +
                            "[center]" +
                            "[center]" +
                            "[center]" +
                            "[grow,fill]"));

                        //---- jcbEnableFog ----
                        jcbEnableFog.setText("Enable Fog");
                        jcbEnableFog.setMaximumSize(null);
                        jcbEnableFog.setMinimumSize(null);
                        jcbEnableFog.setPreferredSize(null);
                        jcbEnableFog.addActionListener(e -> jcbEnableFogActionPerformed(e));
                        panel12.add(jcbEnableFog, "cell 0 0,dock center");

                        //---- jcbRenderFrontAndBack ----
                        jcbRenderFrontAndBack.setText("Render Front and Back Face");
                        jcbRenderFrontAndBack.setMaximumSize(null);
                        jcbRenderFrontAndBack.setMinimumSize(null);
                        jcbRenderFrontAndBack.setPreferredSize(null);
                        jcbRenderFrontAndBack.addActionListener(e -> jcbRenderFrontAndBackActionPerformed(e));
                        panel12.add(jcbRenderFrontAndBack, "cell 0 1,dock center");

                        //---- jcbRenderBorder ----
                        jcbRenderBorder.setText("Draw Outline Border");
                        jcbRenderBorder.setMaximumSize(null);
                        jcbRenderBorder.setMinimumSize(null);
                        jcbRenderBorder.setPreferredSize(null);
                        jcbRenderBorder.addActionListener(e -> jcbRenderBorderActionPerformed(e));
                        panel12.add(jcbRenderBorder, "cell 0 2,dock center");

                        //---- jcbAlwaysIncludedInImd ----
                        jcbAlwaysIncludedInImd.setText("Always included in IMD");
                        jcbAlwaysIncludedInImd.setToolTipText("Used in HGSS");
                        jcbAlwaysIncludedInImd.setMaximumSize(null);
                        jcbAlwaysIncludedInImd.setMinimumSize(null);
                        jcbAlwaysIncludedInImd.setPreferredSize(null);
                        jcbAlwaysIncludedInImd.addActionListener(e -> jcbAlwaysIncludedInImdActionPerformed(e));
                        panel12.add(jcbAlwaysIncludedInImd, "cell 0 3,dock center");

                        //---- jcbUniformNormal ----
                        jcbUniformNormal.setText("Uniform Normal Orientation");
                        jcbUniformNormal.setMaximumSize(null);
                        jcbUniformNormal.setMinimumSize(null);
                        jcbUniformNormal.setPreferredSize(null);
                        jcbUniformNormal.addActionListener(e -> jcbUniformNormalActionPerformed(e));
                        panel12.add(jcbUniformNormal, "cell 0 4,dock center");

                        //---- jcbUseVertexColors ----
                        jcbUseVertexColors.setText("Use Vertex Colors");
                        jcbUseVertexColors.setMaximumSize(null);
                        jcbUseVertexColors.setMinimumSize(null);
                        jcbUseVertexColors.setPreferredSize(null);
                        jcbUseVertexColors.addActionListener(e -> jcbUseVertexColorsActionPerformed(e));
                        panel12.add(jcbUseVertexColors, "cell 0 5,dock center");

                        //======== panel14 ========
                        {
                            panel14.setMaximumSize(null);
                            panel14.setMinimumSize(null);
                            panel14.setPreferredSize(null);
                            panel14.setLayout(new MigLayout(
                                "insets 0,hidemode 3,gap 5 5",
                                // columns
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]" +
                                "[fill]",
                                // rows
                                "[fill]"));

                            //---- jLabel20 ----
                            jLabel20.setText("Lights: ");
                            jLabel20.setMaximumSize(null);
                            jLabel20.setMinimumSize(null);
                            jLabel20.setPreferredSize(null);
                            panel14.add(jLabel20, "cell 0 0");

                            //---- jcbL0 ----
                            jcbL0.setText("L0");
                            jcbL0.setMaximumSize(null);
                            jcbL0.setMinimumSize(null);
                            jcbL0.setPreferredSize(null);
                            jcbL0.addActionListener(e -> jcbL0ActionPerformed(e));
                            panel14.add(jcbL0, "cell 1 0");

                            //---- jcbL1 ----
                            jcbL1.setText("L1");
                            jcbL1.setMaximumSize(null);
                            jcbL1.setMinimumSize(null);
                            jcbL1.setPreferredSize(null);
                            jcbL1.addActionListener(e -> jcbL1ActionPerformed(e));
                            panel14.add(jcbL1, "cell 2 0");

                            //---- jcbL2 ----
                            jcbL2.setText("L2");
                            jcbL2.setMaximumSize(null);
                            jcbL2.setMinimumSize(null);
                            jcbL2.setPreferredSize(null);
                            jcbL2.addActionListener(e -> jcbL2ActionPerformed(e));
                            panel14.add(jcbL2, "cell 3 0");

                            //---- jcbL3 ----
                            jcbL3.setText("L3");
                            jcbL3.setMaximumSize(null);
                            jcbL3.setMinimumSize(null);
                            jcbL3.setPreferredSize(null);
                            jcbL3.addActionListener(e -> jcbL3ActionPerformed(e));
                            panel14.add(jcbL3, "cell 4 0");
                        }
                        panel12.add(panel14, "cell 0 6");
                    }
                    panel11.add(panel12, "cell 0 9 2 1");

                    //======== panel7 ========
                    {
                        panel7.setLayout(new MigLayout(
                            "insets 0,hidemode 3,gap 5 5",
                            // columns
                            "[grow,fill]",
                            // rows
                            "[fill]" +
                            "[fill]" +
                            "[fill]"));

                        //---- jbReplaceTexture ----
                        jbReplaceTexture.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                        jbReplaceTexture.setText("Change Texture");
                        jbReplaceTexture.setMaximumSize(null);
                        jbReplaceTexture.setMinimumSize(null);
                        jbReplaceTexture.setPreferredSize(null);
                        jbReplaceTexture.addActionListener(e -> jbReplaceTextureActionPerformed(e));
                        panel7.add(jbReplaceTexture, "cell 0 0");

                        //---- jbReplaceMaterial ----
                        jbReplaceMaterial.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveTileIcon.png")));
                        jbReplaceMaterial.setText("Replace Material");
                        jbReplaceMaterial.setMaximumSize(null);
                        jbReplaceMaterial.setMinimumSize(null);
                        jbReplaceMaterial.setPreferredSize(null);
                        jbReplaceMaterial.addActionListener(e -> jbReplaceMaterialActionPerformed(e));
                        panel7.add(jbReplaceMaterial, "cell 0 1");

                        //======== textureDisplayMaterial ========
                        {
                            textureDisplayMaterial.setBorder(LineBorder.createBlackLineBorder());
                            textureDisplayMaterial.setPreferredSize(new Dimension(127, 127));
                            textureDisplayMaterial.setMaximumSize(new Dimension(127, 127));
                            textureDisplayMaterial.setMinimumSize(new Dimension(127, 127));
                            textureDisplayMaterial.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                            GroupLayout textureDisplayMaterialLayout = new GroupLayout(textureDisplayMaterial);
                            textureDisplayMaterial.setLayout(textureDisplayMaterialLayout);
                            textureDisplayMaterialLayout.setHorizontalGroup(
                                textureDisplayMaterialLayout.createParallelGroup()
                                    .addGap(0, 125, Short.MAX_VALUE)
                            );
                            textureDisplayMaterialLayout.setVerticalGroup(
                                textureDisplayMaterialLayout.createParallelGroup()
                                    .addGap(0, 125, Short.MAX_VALUE)
                            );
                        }
                        panel7.add(textureDisplayMaterial, "cell 0 2,align center center,grow 0 0");
                    }
                    panel11.add(panel7, "cell 2 9,alignx center,growx 0");
                }
                jPanel3.add(panel11, "cell 1 0");
            }
            jTabbedPane1.addTab("Material Editor", jPanel3);
        }
        contentPane.add(jTabbedPane1);
        setSize(1180, 700);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel18;
    private TileDisplay tileDisplay;
    private JTabbedPane jTabbedPane2;
    private JPanel jPanel4;
    private JLabel jLabel12;
    private JButton jbRotateModel;
    private JButton jbMoveModelLeft;
    private JButton jbMoveModelUp;
    private JButton jbMoveModelRight;
    private JButton jbMoveModelUp1;
    private JLabel jLabel13;
    private JButton jbFlipModel;
    private JButton jbMoveModelDown;
    private JButton jbMoveModelDown1;
    private JPanel jPanel8;
    private JCheckBox jcbBackfaceCulling;
    private JCheckBox jcbWireframe;
    private JCheckBox jcbTexturesEnabled;
    private JCheckBox jcbShadingEnabled;
    private JCheckBox jcbNormals;
    private JPanel jPanel2;
    private JScrollPane jScrollPane2;
    private TileSelector tileSelector;
    private JPanel jPanel9;
    private JScrollPane jScrollPaneSmartGrid;
    private SmartGridEditableDisplay smartGridEditableDisplay;
    private JPanel panel9;
    private JButton jbMoveSPaintUp;
    private JButton jbAddSmartGrid;
    private JButton jbMoveSPaintDown;
    private JButton jbRemoveSmartGrid;
    private JTabbedPane jTabbedPane1;
    private JPanel jPanel1;
    private JPanel panel8;
    private JLabel jLabel3;
    private JTextField jtfIndexTile;
    private JLabel jLabel23;
    private JButton jbMoveUp;
    private JButton jbMoveDown;
    private JPanel panel1;
    private JButton jbAddTile;
    private JButton jbRemoveTile;
    private JButton jbDuplicateTile;
    private JButton jbImportTiles;
    private JPanel jPanel5;
    private JPanel panel2;
    private JLabel jLabel1;
    private JButton jbLessSizeX;
    private JTextField jtfSizeX;
    private JButton jbMoreSizeX;
    private JLabel jLabel2;
    private JButton jbLessSizeY;
    private JTextField jtfSizeY;
    private JButton jbMoreSizeY;
    private JPanel panel4;
    private JCheckBox jcbTileableX;
    private JCheckBox jcbUtileable;
    private JCheckBox jcbTileableY;
    private JCheckBox jcbVtileable;
    private JPanel panel3;
    private JLabel jLabel14;
    private JTextField jtfXOffset;
    private JButton jbXOffset;
    private JLabel jLabel15;
    private JTextField jtfYOffset;
    private JButton jbYOffset;
    private JPanel panel5;
    private JCheckBox jcbGlobalTexMapping;
    private JLabel jLabel10;
    private JTextField jtfGlobalTexScale;
    private JButton jbGlobalTexScale;
    private JPanel jPanel6;
    private JLabel jLabel5;
    private JTextField jtfObjName;
    private JPanel panel10;
    private JButton jbExportTileAsObj;
    private JButton jbImportTileAsObj;
    private JButton jbEditVertexColors;
    private JPanel jPanel7;
    private JPanel panel6;
    private JLabel jLabel22;
    private JLabel label2;
    private JButton jbAddTexture;
    private JComboBox<String> jcbMaterial;
    private JScrollPane scrollPane1;
    private JList jlTileMaterials;
    private TextureDisplay textureDisplay;
    private JPanel jPanel3;
    private JPanel panel13;
    private JLabel jLabel21;
    private JScrollPane jScrollPane1;
    private JList<String> jlistINames;
    private JPanel panel11;
    private JLabel label1;
    private JPanel panel17;
    private JButton jbMoveMaterialUp;
    private JButton jbMoveMaterialDown;
    private JLabel jLabel8;
    private JLabel jLabel7;
    private JLabel jLabel6;
    private JTextField jtfPaletteName;
    private JTextField jtfTextureName;
    private JTextField jtfMaterialName;
    private JButton jbMaterialName;
    private JButton jbTextName;
    private JButton jbPaletteName;
    private JLabel jLabel9;
    private JLabel jLabel11;
    private JLabel jLabel17;
    private JLabel jLabel18;
    private JLabel jLabel19;
    private JSpinner jSpinner2;
    private JComboBox<String> jcbTexGenMode;
    private JComboBox<String> jcbTexTilingU;
    private JComboBox<String> jcbTexTilingV;
    private JComboBox<String> jcbColorFormat;
    private JPanel panel12;
    private JCheckBox jcbEnableFog;
    private JCheckBox jcbRenderFrontAndBack;
    private JCheckBox jcbRenderBorder;
    private JCheckBox jcbAlwaysIncludedInImd;
    private JCheckBox jcbUniformNormal;
    private JCheckBox jcbUseVertexColors;
    private JPanel panel14;
    private JLabel jLabel20;
    private JCheckBox jcbL0;
    private JCheckBox jcbL1;
    private JCheckBox jcbL2;
    private JCheckBox jcbL3;
    private JPanel panel7;
    private JButton jbReplaceTexture;
    private JButton jbReplaceMaterial;
    private TextureDisplayMaterial textureDisplayMaterial;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
