package editor.tileseteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;
import editor.smartdrawing.*;
import editor.tileselector.*;
import com.jogamp.opengl.GLContext;
import editor.TilesetRenderer;
import editor.handler.MapEditorHandler;
import editor.obj.ObjWriter;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import tileset.NormalsNotFoundException;
import tileset.TextureNotFoundException;
import tileset.Tile;
import tileset.Tileset;
import tileset.TilesetIO;
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

    private static final Color redColor = new Color(255, 200, 200);
    private static final Color greenColor = new Color(200, 255, 200);
    private static final Color whiteColor = Color.white;

    private ArrayList<ImageIcon> materialIcons = new ArrayList<>();

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
    }

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
            jtfGlobalTexScale.setBackground(selected ? Color.white : Color.lightGray);
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
                            handler.getGrid(), path, handler.getGameIndex(), true, includeVertexColors);
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
            final AddTileDialog addTileDialog = new AddTileDialog(handler.getMainFrame(),"Import Tile Settings");
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
                if (index < materialIcons.size() && index >= 0) {
                    label.setIcon(materialIcons.get(index));
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

            jtfNumTextures.setText(String.valueOf(tile.getTextureIDs().size()));

            updateViewXOffset(tile);
            updateViewYOffset(tile);
            updateViewGlobalTexScale(tile);

            jtfObjName.setText(tile.getObjFilename());

            updateViewTileIndex();

            SpinnerNumberModel model = new SpinnerNumberModel(0, 0, tile.getTextureIDs().size() - 1, 1);
            jSpinner1.setModel(model);

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
        jtfXOffset.setBackground(whiteColor);
        jtfXOffsetActive.value = true;
    }

    private void updateViewYOffset(Tile tile) {
        jtfYOffsetActive.value = false;
        jtfYOffset.setText(String.valueOf(tile.getYOffset()));
        jtfYOffset.setBackground(whiteColor);
        jtfYOffsetActive.value = true;
    }

    private void updateViewGlobalTexScale(Tile tile) {
        jtfGlobalTexScale.setText(String.valueOf(tile.getGlobalTextureScale()));
        boolean enabled = tile.useGlobalTextureMapping();
        jtfGlobalTexScale.setBackground(enabled ? Color.white : Color.lightGray);
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
            jtfMaterialName.setBackground(Color.white);
            jtfMaterialNameActive.value = true;
        }
    }

    private void updateViewPaletteNameImd() {
        if (jlistINames.getSelectedIndex() != -1) {
            String pName = handler.getTileset().getPaletteNameImd(jlistINames.getSelectedIndex());
            jtfPaletteNameActive.value = false;
            jtfPaletteName.setText(pName);
            jtfPaletteName.setBackground(Color.white);
            jtfPaletteNameActive.value = true;
        }
    }

    private void updateViewTextureNameImd() {
        if (jlistINames.getSelectedIndex() != -1) {
            String tName = handler.getTileset().getTextureNameImd(jlistINames.getSelectedIndex());
            jtfTextureNameActive.value = false;
            jtfTextureName.setText(tName);
            jtfTextureName.setBackground(Color.white);
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
        jtfYOffsetActive.value = true;
    }

    private void changeMaterialName() {
        String mName = jtfMaterialName.getText();
        int index = jlistINames.getSelectedIndex();
        handler.getTileset().setMaterialName(index, mName);

        jtfMaterialNameActive.value = false;
        jtfMaterialName.setBackground(greenColor);
        jtfMaterialNameActive.value = true;
    }

    private void changePaletteNameImd() {
        String pName = jtfPaletteName.getText();
        int index = jlistINames.getSelectedIndex();
        handler.getTileset().setPaletteNameImd(index, pName);

        jtfPaletteNameActive.value = false;
        jtfPaletteName.setBackground(greenColor);
        jtfPaletteNameActive.value = true;
    }

    private void changeTextureNameImd() {
        String tName = jtfTextureName.getText();
        int index = jlistINames.getSelectedIndex();
        handler.getTileset().setTextureNameImd(index, tName);

        jtfTextureNameActive.value = false;
        jtfTextureName.setBackground(greenColor);
        jtfTextureNameActive.value = true;
    }

    public void fixIndices() {
        int[] indices = tileHandler.getChangeIndices();
        tileHandler.fixMapGridIndices(indices);

        fixSmartGridIndices();
        //tileHandler.fixTilesetGridIndices(indices);
    }

    private void fixSmartGridIndices(){
        ArrayList<SmartGridEditable> smartGridEditableArray = smartGridEditableDisplay.getSmartGridArray();
        ArrayList<SmartGrid> smartGridArray = new ArrayList<>(smartGridEditableArray.size());
        for(SmartGridEditable smartGrid : smartGridEditableArray){
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
                }
            }
        });
    }

    private class MutableBoolean {

        public boolean value;

        public MutableBoolean(boolean value) {
            this.value = value;
        }
    };

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  
        tileDisplay = new TileDisplay();
        jPanel2 = new JPanel();
        jScrollPane2 = new JScrollPane();
        tileSelector = new TileSelector();
        jTabbedPane1 = new JTabbedPane();
        jPanel1 = new JPanel();
        jbRemoveTile = new JButton();
        jtfIndexTile = new JTextField();
        jbMoveDown = new JButton();
        jbMoveUp = new JButton();
        jLabel3 = new JLabel();
        jbDuplicateTile = new JButton();
        jPanel5 = new JPanel();
        jLabel10 = new JLabel();
        jcbTileableY = new JCheckBox();
        jtfGlobalTexScale = new JTextField();
        jbGlobalTexScale = new JButton();
        jbMoreSizeX = new JButton();
        jcbUtileable = new JCheckBox();
        jLabel2 = new JLabel();
        jcbVtileable = new JCheckBox();
        jLabel14 = new JLabel();
        jtfXOffset = new JTextField();
        jLabel15 = new JLabel();
        jtfYOffset = new JTextField();
        jtfSizeY = new JTextField();
        jbXOffset = new JButton();
        jbYOffset = new JButton();
        jLabel1 = new JLabel();
        jtfSizeX = new JTextField();
        jcbTileableX = new JCheckBox();
        jbLessSizeX = new JButton();
        jbMoreSizeY = new JButton();
        jbLessSizeY = new JButton();
        jcbGlobalTexMapping = new JCheckBox();
        jPanel6 = new JPanel();
        jLabel5 = new JLabel();
        jtfObjName = new JTextField();
        jbExportTileAsObj = new JButton();
        jbImportTileAsObj = new JButton();
        jbEditVertexColors = new JButton();
        jPanel7 = new JPanel();
        jLabel16 = new JLabel();
        jtfNumTextures = new JTextField();
        jLabel4 = new JLabel();
        jSpinner1 = new JSpinner();
        jcbMaterial = new JComboBox<>();
        textureDisplay = new TextureDisplay();
        jbAddTexture = new JButton();
        jLabel22 = new JLabel();
        jbAddTile = new JButton();
        jLabel23 = new JLabel();
        jbImportTiles = new JButton();
        jPanel3 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jlistINames = new JList<>();
        jtfPaletteName = new JTextField();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jtfTextureName = new JTextField();
        jbTextName = new JButton();
        jbPaletteName = new JButton();
        jcbEnableFog = new JCheckBox();
        jcbRenderFrontAndBack = new JCheckBox();
        jcbUniformNormal = new JCheckBox();
        jLabel8 = new JLabel();
        jtfMaterialName = new JTextField();
        jbMaterialName = new JButton();
        jLabel9 = new JLabel();
        jSpinner2 = new JSpinner();
        jLabel11 = new JLabel();
        jcbTexGenMode = new JComboBox<>();
        textureDisplayMaterial = new TextureDisplayMaterial();
        jcbAlwaysIncludedInImd = new JCheckBox();
        jbMoveMaterialUp = new JButton();
        jbMoveMaterialDown = new JButton();
        jbReplaceMaterial = new JButton();
        jLabel17 = new JLabel();
        jcbTexTilingU = new JComboBox<>();
        jLabel18 = new JLabel();
        jcbTexTilingV = new JComboBox<>();
        jLabel19 = new JLabel();
        jcbColorFormat = new JComboBox<>();
        jLabel20 = new JLabel();
        jcbL0 = new JCheckBox();
        jcbL1 = new JCheckBox();
        jcbL2 = new JCheckBox();
        jcbL3 = new JCheckBox();
        jbReplaceTexture = new JButton();
        jcbRenderBorder = new JCheckBox();
        jLabel21 = new JLabel();
        jcbUseVertexColors = new JCheckBox();
        jTabbedPane2 = new JTabbedPane();
        jPanel4 = new JPanel();
        jLabel12 = new JLabel();
        jbRotateModel = new JButton();
        jLabel13 = new JLabel();
        jbFlipModel = new JButton();
        jbMoveModelUp = new JButton();
        jbMoveModelDown = new JButton();
        jbMoveModelLeft = new JButton();
        jbMoveModelRight = new JButton();
        jbMoveModelUp1 = new JButton();
        jbMoveModelDown1 = new JButton();
        jPanel8 = new JPanel();
        jcbBackfaceCulling = new JCheckBox();
        jcbWireframe = new JCheckBox();
        jcbTexturesEnabled = new JCheckBox();
        jcbShadingEnabled = new JCheckBox();
        jPanel9 = new JPanel();
        jScrollPaneSmartGrid = new JScrollPane();
        smartGridEditableDisplay = new SmartGridEditableDisplay();
        jbMoveSPaintUp = new JButton();
        jbMoveSPaintDown = new JButton();
        jbAddSmartGrid = new JButton();
        jbRemoveSmartGrid = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tileset Editor");
        setIconImage(null);
        setMinimumSize(new Dimension(1094, 602));
        setModal(true);
        var contentPane = getContentPane();

        //======== tileDisplay ========
        {
            tileDisplay.setBorder(new BevelBorder(BevelBorder.LOWERED));
            tileDisplay.setPreferredSize(new Dimension(350, 300));

            GroupLayout tileDisplayLayout = new GroupLayout(tileDisplay);
            tileDisplay.setLayout(tileDisplayLayout);
            tileDisplayLayout.setHorizontalGroup(
                tileDisplayLayout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
            tileDisplayLayout.setVerticalGroup(
                tileDisplayLayout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
        }

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Tile Selector"));

            //======== jScrollPane2 ========
            {
                jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //======== tileSelector ========
                {
                    tileSelector.setPreferredSize(new Dimension(128, 0));
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
                            .addGap(0, 672, Short.MAX_VALUE)
                    );
                }
                jScrollPane2.setViewportView(tileSelector);
            }

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addGap(0, 0, 0))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addComponent(jScrollPane2)
            );
        }

        //======== jTabbedPane1 ========
        {

            //======== jPanel1 ========
            {

                //---- jbRemoveTile ----
                jbRemoveTile.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveTileIcon.png")));
                jbRemoveTile.setText("Remove Tiles");
                jbRemoveTile.addActionListener(e -> jbRemoveTileActionPerformed(e));

                //---- jtfIndexTile ----
                jtfIndexTile.setHorizontalAlignment(SwingConstants.CENTER);
                jtfIndexTile.setEnabled(false);

                //---- jbMoveDown ----
                jbMoveDown.setText("\u25bc");
                jbMoveDown.addActionListener(e -> jbMoveDownActionPerformed(e));

                //---- jbMoveUp ----
                jbMoveUp.setText("\u25b2");
                jbMoveUp.addActionListener(e -> jbMoveUpActionPerformed(e));

                //---- jLabel3 ----
                jLabel3.setText("Tile selected: ");

                //---- jbDuplicateTile ----
                jbDuplicateTile.setIcon(new ImageIcon(getClass().getResource("/icons/DuplicateTileIcon.png")));
                jbDuplicateTile.setText("Duplicate Tiles");
                jbDuplicateTile.addActionListener(e -> jbDuplicateTileActionPerformed(e));

                //======== jPanel5 ========
                {
                    jPanel5.setBorder(new TitledBorder("Tile Properties"));

                    //---- jLabel10 ----
                    jLabel10.setText("Texture Scale:");

                    //---- jcbTileableY ----
                    jcbTileableY.setForeground(new Color(0, 153, 0));
                    jcbTileableY.setText("Y Tileable");
                    jcbTileableY.addActionListener(e -> jcbTileableYActionPerformed(e));

                    //---- jtfGlobalTexScale ----
                    jtfGlobalTexScale.setText(" ");

                    //---- jbGlobalTexScale ----
                    jbGlobalTexScale.setText("Apply");
                    jbGlobalTexScale.addActionListener(e -> jbGlobalTexScaleActionPerformed(e));

                    //---- jbMoreSizeX ----
                    jbMoreSizeX.setText(">");
                    jbMoreSizeX.addActionListener(e -> jbMoreSizeXActionPerformed(e));

                    //---- jcbUtileable ----
                    jcbUtileable.setForeground(new Color(204, 0, 0));
                    jcbUtileable.setText("Texture U Tileable");
                    jcbUtileable.addActionListener(e -> jcbUtileableActionPerformed(e));

                    //---- jLabel2 ----
                    jLabel2.setForeground(new Color(0, 153, 0));
                    jLabel2.setText("Y Size:");

                    //---- jcbVtileable ----
                    jcbVtileable.setForeground(new Color(0, 153, 0));
                    jcbVtileable.setText("Texture V Tileable");
                    jcbVtileable.addActionListener(e -> jcbVtileableActionPerformed(e));

                    //---- jLabel14 ----
                    jLabel14.setForeground(new Color(204, 0, 0));
                    jLabel14.setText("X Offset: ");

                    //---- jtfXOffset ----
                    jtfXOffset.setText(" ");

                    //---- jLabel15 ----
                    jLabel15.setForeground(new Color(0, 153, 0));
                    jLabel15.setText("Y Offset: ");

                    //---- jtfYOffset ----
                    jtfYOffset.setText(" ");

                    //---- jtfSizeY ----
                    jtfSizeY.setEditable(false);
                    jtfSizeY.setHorizontalAlignment(SwingConstants.CENTER);

                    //---- jbXOffset ----
                    jbXOffset.setText("Apply");
                    jbXOffset.addActionListener(e -> jbXOffsetActionPerformed(e));

                    //---- jbYOffset ----
                    jbYOffset.setText("Apply");
                    jbYOffset.addActionListener(e -> jbYOffsetActionPerformed(e));

                    //---- jLabel1 ----
                    jLabel1.setForeground(new Color(204, 0, 0));
                    jLabel1.setText("X Size:");

                    //---- jtfSizeX ----
                    jtfSizeX.setEditable(false);
                    jtfSizeX.setHorizontalAlignment(SwingConstants.CENTER);

                    //---- jcbTileableX ----
                    jcbTileableX.setForeground(new Color(204, 0, 0));
                    jcbTileableX.setText("X Tileable");
                    jcbTileableX.addActionListener(e -> jcbTileableXActionPerformed(e));

                    //---- jbLessSizeX ----
                    jbLessSizeX.setText("<");
                    jbLessSizeX.addActionListener(e -> jbLessSizeXActionPerformed(e));

                    //---- jbMoreSizeY ----
                    jbMoreSizeY.setText(">");
                    jbMoreSizeY.addActionListener(e -> jbMoreSizeYActionPerformed(e));

                    //---- jbLessSizeY ----
                    jbLessSizeY.setText("<");
                    jbLessSizeY.addActionListener(e -> jbLessSizeYActionPerformed(e));

                    //---- jcbGlobalTexMapping ----
                    jcbGlobalTexMapping.setText("Global Texture Mapping");
                    jcbGlobalTexMapping.addActionListener(e -> jcbGlobalTexMappingActionPerformed(e));

                    GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
                    jPanel5.setLayout(jPanel5Layout);
                    jPanel5Layout.setHorizontalGroup(
                        jPanel5Layout.createParallelGroup()
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel5Layout.createParallelGroup()
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jtfGlobalTexScale))
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel14)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jtfXOffset, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel5Layout.createParallelGroup()
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jbXOffset, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel15)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jtfYOffset, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbYOffset, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jbGlobalTexScale)
                                                .addGap(18, 18, 18)
                                                .addComponent(jcbGlobalTexMapping))))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup()
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbLessSizeY))
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbLessSizeX)))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel5Layout.createParallelGroup()
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jtfSizeY, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbMoreSizeY)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jcbTileableY)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jcbVtileable))
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addComponent(jtfSizeX, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jbMoreSizeX)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jcbTileableX)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jcbUtileable)))))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                    jPanel5Layout.setVerticalGroup(
                        jPanel5Layout.createParallelGroup()
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jbLessSizeX, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbMoreSizeX, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jcbTileableX)
                                    .addComponent(jcbUtileable)
                                    .addComponent(jtfSizeX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jbLessSizeY, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbMoreSizeY, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jcbTileableY)
                                    .addComponent(jcbVtileable)
                                    .addComponent(jtfSizeY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(jtfXOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbXOffset)
                                    .addComponent(jLabel15)
                                    .addComponent(jtfYOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbYOffset))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jbGlobalTexScale)
                                    .addComponent(jcbGlobalTexMapping)
                                    .addComponent(jtfGlobalTexScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                }

                //======== jPanel6 ========
                {
                    jPanel6.setBorder(new TitledBorder("Tile 3D Model"));

                    //---- jLabel5 ----
                    jLabel5.setText("Model name:");

                    //---- jtfObjName ----
                    jtfObjName.setEditable(false);
                    jtfObjName.setColumns(17);
                    jtfObjName.setText(" ");
                    jtfObjName.setEnabled(false);
                    jtfObjName.setMaximumSize(new Dimension(9, 20));

                    //---- jbExportTileAsObj ----
                    jbExportTileAsObj.setIcon(new ImageIcon(getClass().getResource("/icons/ExportTileIcon.png")));
                    jbExportTileAsObj.setText("Export OBJ...");
                    jbExportTileAsObj.addActionListener(e -> jbExportTileAsObjActionPerformed(e));

                    //---- jbImportTileAsObj ----
                    jbImportTileAsObj.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                    jbImportTileAsObj.setText("Replace OBJ...");
                    jbImportTileAsObj.addActionListener(e -> jbImportTileAsObjActionPerformed(e));

                    //---- jbEditVertexColors ----
                    jbEditVertexColors.setIcon(new ImageIcon(getClass().getResource("/icons/VertexColorEditorIcon.png")));
                    jbEditVertexColors.setText("Edit Vertex Colors...");
                    jbEditVertexColors.addActionListener(e -> jbEditVertexColorsActionPerformed(e));

                    GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
                    jPanel6.setLayout(jPanel6Layout);
                    jPanel6Layout.setHorizontalGroup(
                        jPanel6Layout.createParallelGroup()
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel6Layout.createParallelGroup()
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jbExportTileAsObj)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbImportTileAsObj)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbEditVertexColors)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtfObjName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(10, 10, 10))
                    );
                    jPanel6Layout.setVerticalGroup(
                        jPanel6Layout.createParallelGroup()
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(jtfObjName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jbExportTileAsObj)
                                    .addComponent(jbImportTileAsObj)
                                    .addComponent(jbEditVertexColors))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                }

                //======== jPanel7 ========
                {
                    jPanel7.setBorder(new TitledBorder("Materials"));

                    //---- jLabel16 ----
                    jLabel16.setText("Number of materials:");

                    //---- jtfNumTextures ----
                    jtfNumTextures.setHorizontalAlignment(SwingConstants.CENTER);
                    jtfNumTextures.setEnabled(false);

                    //---- jLabel4 ----
                    jLabel4.setText("Material selected:");
                    jLabel4.setToolTipText("");

                    //---- jSpinner1 ----
                    jSpinner1.addChangeListener(e -> jSpinner1StateChanged(e));

                    //---- jcbMaterial ----
                    jcbMaterial.setModel(new DefaultComboBoxModel<>(new String[] {

                    }));
                    jcbMaterial.addActionListener(e -> jcbMaterialActionPerformed(e));

                    //======== textureDisplay ========
                    {
                        textureDisplay.setBorder(LineBorder.createBlackLineBorder());
                        textureDisplay.setPreferredSize(new Dimension(128, 128));

                        GroupLayout textureDisplayLayout = new GroupLayout(textureDisplay);
                        textureDisplay.setLayout(textureDisplayLayout);
                        textureDisplayLayout.setHorizontalGroup(
                            textureDisplayLayout.createParallelGroup()
                                .addGap(0, 126, Short.MAX_VALUE)
                        );
                        textureDisplayLayout.setVerticalGroup(
                            textureDisplayLayout.createParallelGroup()
                                .addGap(0, 126, Short.MAX_VALUE)
                        );
                    }

                    //---- jbAddTexture ----
                    jbAddTexture.setIcon(new ImageIcon(getClass().getResource("/icons/AddTileIcon.png")));
                    jbAddTexture.setText("Add texture...");
                    jbAddTexture.addActionListener(e -> jbAddTextureActionPerformed(e));

                    //---- jLabel22 ----
                    jLabel22.setText("Material: ");

                    GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
                    jPanel7.setLayout(jPanel7Layout);
                    jPanel7Layout.setHorizontalGroup(
                        jPanel7Layout.createParallelGroup()
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel7Layout.createParallelGroup()
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jcbMaterial)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jbAddTexture)
                                        .addGap(16, 16, 16))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtfNumTextures, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jSpinner1, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(textureDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                    );
                    jPanel7Layout.setVerticalGroup(
                        jPanel7Layout.createParallelGroup()
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel7Layout.createParallelGroup()
                                    .addComponent(textureDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel16)
                                            .addComponent(jtfNumTextures, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4)
                                            .addComponent(jSpinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel22)
                                            .addComponent(jcbMaterial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbAddTexture))))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                }

                //---- jbAddTile ----
                jbAddTile.setIcon(new ImageIcon(getClass().getResource("/icons/AddTileIcon.png")));
                jbAddTile.setText("Add Tiles...");
                jbAddTile.addActionListener(e -> jbAddTileActionPerformed(e));

                //---- jLabel23 ----
                jLabel23.setText("Move tile:");

                //---- jbImportTiles ----
                jbImportTiles.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                jbImportTiles.setText("Import Tiles...");
                jbImportTiles.addActionListener(e -> jbImportTilesActionPerformed(e));

                GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup()
                                .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jtfIndexTile, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(jLabel23)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jbMoveUp)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbMoveDown))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jbAddTile)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbRemoveTile)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbDuplicateTile)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbImportTiles)))
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addContainerGap())
                );
                jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jtfIndexTile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)
                                .addComponent(jbMoveUp)
                                .addComponent(jbMoveDown)
                                .addComponent(jLabel23))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jbDuplicateTile)
                                .addComponent(jbRemoveTile)
                                .addComponent(jbAddTile)
                                .addComponent(jbImportTiles))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }
            jTabbedPane1.addTab("Tile Editor", jPanel1);

            //======== jPanel3 ========
            {

                //======== jScrollPane1 ========
                {
                    jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                    //---- jlistINames ----
                    jlistINames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    jlistINames.addListSelectionListener(e -> jlistINamesValueChanged(e));
                    jScrollPane1.setViewportView(jlistINames);
                }

                //---- jtfPaletteName ----
                jtfPaletteName.setText(" ");
                jtfPaletteName.addActionListener(e -> jtfPaletteNameActionPerformed(e));

                //---- jLabel6 ----
                jLabel6.setText("Palette Name:");

                //---- jLabel7 ----
                jLabel7.setText("Texture Name:");

                //---- jtfTextureName ----
                jtfTextureName.setText(" ");
                jtfTextureName.addActionListener(e -> jtfTextureNameActionPerformed(e));

                //---- jbTextName ----
                jbTextName.setText("Apply");
                jbTextName.addActionListener(e -> jbTextNameActionPerformed(e));

                //---- jbPaletteName ----
                jbPaletteName.setText("Apply");
                jbPaletteName.addActionListener(e -> jbPaletteNameActionPerformed(e));

                //---- jcbEnableFog ----
                jcbEnableFog.setText("Enable Fog");
                jcbEnableFog.addActionListener(e -> jcbEnableFogActionPerformed(e));

                //---- jcbRenderFrontAndBack ----
                jcbRenderFrontAndBack.setText("Render Front and Back Face");
                jcbRenderFrontAndBack.addActionListener(e -> jcbRenderFrontAndBackActionPerformed(e));

                //---- jcbUniformNormal ----
                jcbUniformNormal.setText("Uniform Normal Orientation");
                jcbUniformNormal.addActionListener(e -> jcbUniformNormalActionPerformed(e));

                //---- jLabel8 ----
                jLabel8.setText("Material Name:");

                //---- jtfMaterialName ----
                jtfMaterialName.setText(" ");
                jtfMaterialName.addActionListener(e -> jtfMaterialNameActionPerformed(e));

                //---- jbMaterialName ----
                jbMaterialName.setText("Apply");
                jbMaterialName.addActionListener(e -> jbMaterialNameActionPerformed(e));

                //---- jLabel9 ----
                jLabel9.setText("Alpha: ");

                //---- jSpinner2 ----
                jSpinner2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
                jSpinner2.addChangeListener(e -> jSpinner2StateChanged(e));

                //---- jLabel11 ----
                jLabel11.setText("Tex Gen Mode: ");

                //---- jcbTexGenMode ----
                jcbTexGenMode.setMaximumRowCount(4);
                jcbTexGenMode.setModel(new DefaultComboBoxModel<>(new String[] {
                    "None",
                    "Texture",
                    "Normal",
                    "Vertex"
                }));
                jcbTexGenMode.addActionListener(e -> jcbTexGenModeActionPerformed(e));

                //======== textureDisplayMaterial ========
                {
                    textureDisplayMaterial.setBorder(LineBorder.createBlackLineBorder());
                    textureDisplayMaterial.setPreferredSize(new Dimension(128, 128));

                    GroupLayout textureDisplayMaterialLayout = new GroupLayout(textureDisplayMaterial);
                    textureDisplayMaterial.setLayout(textureDisplayMaterialLayout);
                    textureDisplayMaterialLayout.setHorizontalGroup(
                        textureDisplayMaterialLayout.createParallelGroup()
                            .addGap(0, 126, Short.MAX_VALUE)
                    );
                    textureDisplayMaterialLayout.setVerticalGroup(
                        textureDisplayMaterialLayout.createParallelGroup()
                            .addGap(0, 126, Short.MAX_VALUE)
                    );
                }

                //---- jcbAlwaysIncludedInImd ----
                jcbAlwaysIncludedInImd.setText("Always included in IMD");
                jcbAlwaysIncludedInImd.setToolTipText("Used in HGSS");
                jcbAlwaysIncludedInImd.addActionListener(e -> jcbAlwaysIncludedInImdActionPerformed(e));

                //---- jbMoveMaterialUp ----
                jbMoveMaterialUp.setText("\u25b2");
                jbMoveMaterialUp.addActionListener(e -> jbMoveMaterialUpActionPerformed(e));

                //---- jbMoveMaterialDown ----
                jbMoveMaterialDown.setText("\u25bc");
                jbMoveMaterialDown.addActionListener(e -> jbMoveMaterialDownActionPerformed(e));

                //---- jbReplaceMaterial ----
                jbReplaceMaterial.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveTileIcon.png")));
                jbReplaceMaterial.setText("Replace Material");
                jbReplaceMaterial.addActionListener(e -> jbReplaceMaterialActionPerformed(e));

                //---- jLabel17 ----
                jLabel17.setText("Tex Tiling U: ");

                //---- jcbTexTilingU ----
                jcbTexTilingU.setMaximumRowCount(4);
                jcbTexTilingU.setModel(new DefaultComboBoxModel<>(new String[] {
                    "Repeat",
                    "Clamp",
                    "Flip"
                }));
                jcbTexTilingU.addActionListener(e -> jcbTexTilingUActionPerformed(e));

                //---- jLabel18 ----
                jLabel18.setText("Tex Tiling V: ");

                //---- jcbTexTilingV ----
                jcbTexTilingV.setMaximumRowCount(4);
                jcbTexTilingV.setModel(new DefaultComboBoxModel<>(new String[] {
                    "Repeat",
                    "Clamp",
                    "Flip"
                }));
                jcbTexTilingV.addActionListener(e -> jcbTexTilingVActionPerformed(e));

                //---- jLabel19 ----
                jLabel19.setText("Color Format:");

                //---- jcbColorFormat ----
                jcbColorFormat.setModel(new DefaultComboBoxModel<>(new String[] {
                    "Palette 4",
                    "Palette 16",
                    "Palette 256",
                    "A3I5",
                    "A5I3"
                }));
                jcbColorFormat.addActionListener(e -> jcbColorFormatActionPerformed(e));

                //---- jLabel20 ----
                jLabel20.setText("Lights: ");

                //---- jcbL0 ----
                jcbL0.setText("L0");
                jcbL0.addActionListener(e -> jcbL0ActionPerformed(e));

                //---- jcbL1 ----
                jcbL1.setText("L1");
                jcbL1.addActionListener(e -> jcbL1ActionPerformed(e));

                //---- jcbL2 ----
                jcbL2.setText("L2");
                jcbL2.addActionListener(e -> jcbL2ActionPerformed(e));

                //---- jcbL3 ----
                jcbL3.setText("L3");
                jcbL3.addActionListener(e -> jcbL3ActionPerformed(e));

                //---- jbReplaceTexture ----
                jbReplaceTexture.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                jbReplaceTexture.setText("Change Texture");
                jbReplaceTexture.addActionListener(e -> jbReplaceTextureActionPerformed(e));

                //---- jcbRenderBorder ----
                jcbRenderBorder.setText("Draw Outline Border");
                jcbRenderBorder.addActionListener(e -> jcbRenderBorderActionPerformed(e));

                //---- jLabel21 ----
                jLabel21.setText("Material list:");

                //---- jcbUseVertexColors ----
                jcbUseVertexColors.setText("Use Vertex Colors");
                jcbUseVertexColors.addActionListener(e -> jcbUseVertexColorsActionPerformed(e));

                GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup()
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel3Layout.createParallelGroup()
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel21))
                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jLabel8, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel7, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel6, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jtfTextureName, GroupLayout.Alignment.LEADING)
                                                .addComponent(jtfMaterialName, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                                .addComponent(jtfPaletteName, GroupLayout.Alignment.LEADING)))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel9, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jSpinner2, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel3Layout.createParallelGroup()
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addComponent(jbMoveMaterialUp)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbMoveMaterialDown))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(jPanel3Layout.createParallelGroup()
                                                .addComponent(jbPaletteName, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbTextName, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbMaterialName, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addGroup(jPanel3Layout.createParallelGroup()
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGroup(jPanel3Layout.createParallelGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                            .addComponent(jLabel17, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jLabel11, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                            .addComponent(jcbTexTilingU, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jcbTexGenMode, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(jcbEnableFog)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                            .addComponent(jLabel18, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel19, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(jcbTexTilingV)
                                                            .addComponent(jcbColorFormat))))
                                                .addComponent(jcbUniformNormal)
                                                .addComponent(jcbRenderFrontAndBack)
                                                .addComponent(jcbAlwaysIncludedInImd)
                                                .addComponent(jcbRenderBorder))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(textureDisplayMaterial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jbReplaceTexture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jbReplaceMaterial, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel20)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbL0)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbL1)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbL2)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbL3)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbUseVertexColors)
                                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                );
                jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup()
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jtfMaterialName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel8)
                                        .addComponent(jbMaterialName))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jtfTextureName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel7)
                                        .addComponent(jbTextName))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jtfPaletteName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel6)
                                        .addComponent(jbPaletteName))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(jSpinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jbMoveMaterialUp)
                                        .addComponent(jbMoveMaterialDown))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup()
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(textureDisplayMaterial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbReplaceTexture)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbReplaceMaterial))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel11)
                                                .addComponent(jcbTexGenMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel17)
                                                .addComponent(jcbTexTilingU, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel18)
                                                .addComponent(jcbTexTilingV, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel19)
                                                .addComponent(jcbColorFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbEnableFog)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbUniformNormal)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbRenderFrontAndBack)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jcbAlwaysIncludedInImd)))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jcbRenderBorder)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel20)
                                        .addComponent(jcbL0)
                                        .addComponent(jcbL1)
                                        .addComponent(jcbL2)
                                        .addComponent(jcbL3)
                                        .addComponent(jcbUseVertexColors))
                                    .addGap(0, 163, Short.MAX_VALUE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel21)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane1)))
                            .addContainerGap())
                );
            }
            jTabbedPane1.addTab("Material Editor", jPanel3);
        }

        //======== jTabbedPane2 ========
        {

            //======== jPanel4 ========
            {

                //---- jLabel12 ----
                jLabel12.setText("Rotate: ");

                //---- jbRotateModel ----
                jbRotateModel.setText("\u21ba");
                jbRotateModel.setToolTipText("");
                jbRotateModel.addActionListener(e -> jbRotateModelActionPerformed(e));

                //---- jLabel13 ----
                jLabel13.setText("Flip : ");

                //---- jbFlipModel ----
                jbFlipModel.setText("\u21c6");
                jbFlipModel.setToolTipText("");
                jbFlipModel.setEnabled(false);
                jbFlipModel.addActionListener(e -> jbFlipModelActionPerformed(e));

                //---- jbMoveModelUp ----
                jbMoveModelUp.setForeground(new Color(0, 153, 0));
                jbMoveModelUp.setText("\u25b2");
                jbMoveModelUp.addActionListener(e -> jbMoveModelUpActionPerformed(e));

                //---- jbMoveModelDown ----
                jbMoveModelDown.setForeground(new Color(0, 153, 0));
                jbMoveModelDown.setText("\u25bc");
                jbMoveModelDown.addActionListener(e -> jbMoveModelDownActionPerformed(e));

                //---- jbMoveModelLeft ----
                jbMoveModelLeft.setForeground(new Color(204, 0, 0));
                jbMoveModelLeft.setText("\u25c4");
                jbMoveModelLeft.addActionListener(e -> jbMoveModelLeftActionPerformed(e));

                //---- jbMoveModelRight ----
                jbMoveModelRight.setForeground(new Color(204, 0, 0));
                jbMoveModelRight.setText("\u25ba");
                jbMoveModelRight.addActionListener(e -> jbMoveModelRightActionPerformed(e));

                //---- jbMoveModelUp1 ----
                jbMoveModelUp1.setForeground(Color.blue);
                jbMoveModelUp1.setText("\u25b2");
                jbMoveModelUp1.addActionListener(e -> jbMoveModelUp1ActionPerformed(e));

                //---- jbMoveModelDown1 ----
                jbMoveModelDown1.setForeground(Color.blue);
                jbMoveModelDown1.setText("\u25bc");
                jbMoveModelDown1.addActionListener(e -> jbMoveModelDown1ActionPerformed(e));

                GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
                jPanel4.setLayout(jPanel4Layout);
                jPanel4Layout.setHorizontalGroup(
                    jPanel4Layout.createParallelGroup()
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel4Layout.createParallelGroup()
                                .addComponent(jLabel12)
                                .addComponent(jLabel13))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel4Layout.createParallelGroup()
                                .addComponent(jbFlipModel)
                                .addComponent(jbRotateModel, GroupLayout.Alignment.TRAILING))
                            .addGap(18, 18, 18)
                            .addComponent(jbMoveModelLeft, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup()
                                .addComponent(jbMoveModelDown)
                                .addComponent(jbMoveModelUp, GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbMoveModelRight, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel4Layout.createParallelGroup()
                                .addComponent(jbMoveModelUp1)
                                .addComponent(jbMoveModelDown1))
                            .addGap(0, 0, Short.MAX_VALUE))
                );
                jPanel4Layout.setVerticalGroup(
                    jPanel4Layout.createParallelGroup()
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel12)
                                        .addComponent(jbRotateModel)
                                        .addComponent(jbMoveModelUp))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel13)
                                        .addComponent(jbFlipModel)
                                        .addComponent(jbMoveModelDown)))
                                .addComponent(jbMoveModelRight, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbMoveModelLeft, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jbMoveModelUp1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jbMoveModelDown1)))
                            .addContainerGap(15, Short.MAX_VALUE))
                );
            }
            jTabbedPane2.addTab("Model Editor", jPanel4);

            //======== jPanel8 ========
            {

                //---- jcbBackfaceCulling ----
                jcbBackfaceCulling.setSelected(true);
                jcbBackfaceCulling.setText("Backface Culling");
                jcbBackfaceCulling.addActionListener(e -> jcbBackfaceCullingActionPerformed(e));

                //---- jcbWireframe ----
                jcbWireframe.setSelected(true);
                jcbWireframe.setText("Wireframe");
                jcbWireframe.addActionListener(e -> jcbWireframeActionPerformed(e));

                //---- jcbTexturesEnabled ----
                jcbTexturesEnabled.setSelected(true);
                jcbTexturesEnabled.setText("Textures");
                jcbTexturesEnabled.addActionListener(e -> jcbTexturesEnabledActionPerformed(e));

                //---- jcbShadingEnabled ----
                jcbShadingEnabled.setText("Shading");
                jcbShadingEnabled.addActionListener(e -> jcbShadingEnabledActionPerformed(e));

                GroupLayout jPanel8Layout = new GroupLayout(jPanel8);
                jPanel8.setLayout(jPanel8Layout);
                jPanel8Layout.setHorizontalGroup(
                    jPanel8Layout.createParallelGroup()
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel8Layout.createParallelGroup()
                                .addComponent(jcbBackfaceCulling)
                                .addComponent(jcbWireframe))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel8Layout.createParallelGroup()
                                .addComponent(jcbShadingEnabled)
                                .addComponent(jcbTexturesEnabled))
                            .addContainerGap(484, Short.MAX_VALUE))
                );
                jPanel8Layout.setVerticalGroup(
                    jPanel8Layout.createParallelGroup()
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jcbBackfaceCulling)
                                .addComponent(jcbTexturesEnabled))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jcbWireframe)
                                .addComponent(jcbShadingEnabled))
                            .addContainerGap(29, Short.MAX_VALUE))
                );
            }
            jTabbedPane2.addTab("Display Settings", jPanel8);
        }

        //======== jPanel9 ========
        {
            jPanel9.setBorder(new TitledBorder("Smart Drawing"));

            //======== jScrollPaneSmartGrid ========
            {
                jScrollPaneSmartGrid.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPaneSmartGrid.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //======== smartGridEditableDisplay ========
                {

                    GroupLayout smartGridEditableDisplayLayout = new GroupLayout(smartGridEditableDisplay);
                    smartGridEditableDisplay.setLayout(smartGridEditableDisplayLayout);
                    smartGridEditableDisplayLayout.setHorizontalGroup(
                        smartGridEditableDisplayLayout.createParallelGroup()
                            .addGap(0, 156, Short.MAX_VALUE)
                    );
                    smartGridEditableDisplayLayout.setVerticalGroup(
                        smartGridEditableDisplayLayout.createParallelGroup()
                            .addGap(0, 597, Short.MAX_VALUE)
                    );
                }
                jScrollPaneSmartGrid.setViewportView(smartGridEditableDisplay);
            }

            //---- jbMoveSPaintUp ----
            jbMoveSPaintUp.setText("\u25b2");
            jbMoveSPaintUp.setFocusable(false);
            jbMoveSPaintUp.addActionListener(e -> jbMoveSPaintUpActionPerformed(e));

            //---- jbMoveSPaintDown ----
            jbMoveSPaintDown.setText("\u25bc");
            jbMoveSPaintDown.setFocusable(false);
            jbMoveSPaintDown.addActionListener(e -> jbMoveSPaintDownActionPerformed(e));

            //---- jbAddSmartGrid ----
            jbAddSmartGrid.setText("+");
            jbAddSmartGrid.setFocusable(false);
            jbAddSmartGrid.addActionListener(e -> jbAddSmartGridActionPerformed(e));

            //---- jbRemoveSmartGrid ----
            jbRemoveSmartGrid.setText("-");
            jbRemoveSmartGrid.setFocusable(false);
            jbRemoveSmartGrid.addActionListener(e -> jbRemoveSmartGridActionPerformed(e));

            GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
            jPanel9.setLayout(jPanel9Layout);
            jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbMoveSPaintUp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddSmartGrid, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbMoveSPaintDown, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveSmartGrid, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPaneSmartGrid)
            );
            jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup()
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPaneSmartGrid)
                        .addGap(9, 9, 9)
                        .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jbAddSmartGrid)
                            .addComponent(jbRemoveSmartGrid))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jbMoveSPaintUp)
                            .addComponent(jbMoveSPaintDown)))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jTabbedPane2)
                        .addComponent(tileDisplay, GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel9, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 541, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jPanel9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(tileDisplay, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTabbedPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jTabbedPane1))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  
    private TileDisplay tileDisplay;
    private JPanel jPanel2;
    private JScrollPane jScrollPane2;
    private TileSelector tileSelector;
    private JTabbedPane jTabbedPane1;
    private JPanel jPanel1;
    private JButton jbRemoveTile;
    private JTextField jtfIndexTile;
    private JButton jbMoveDown;
    private JButton jbMoveUp;
    private JLabel jLabel3;
    private JButton jbDuplicateTile;
    private JPanel jPanel5;
    private JLabel jLabel10;
    private JCheckBox jcbTileableY;
    private JTextField jtfGlobalTexScale;
    private JButton jbGlobalTexScale;
    private JButton jbMoreSizeX;
    private JCheckBox jcbUtileable;
    private JLabel jLabel2;
    private JCheckBox jcbVtileable;
    private JLabel jLabel14;
    private JTextField jtfXOffset;
    private JLabel jLabel15;
    private JTextField jtfYOffset;
    private JTextField jtfSizeY;
    private JButton jbXOffset;
    private JButton jbYOffset;
    private JLabel jLabel1;
    private JTextField jtfSizeX;
    private JCheckBox jcbTileableX;
    private JButton jbLessSizeX;
    private JButton jbMoreSizeY;
    private JButton jbLessSizeY;
    private JCheckBox jcbGlobalTexMapping;
    private JPanel jPanel6;
    private JLabel jLabel5;
    private JTextField jtfObjName;
    private JButton jbExportTileAsObj;
    private JButton jbImportTileAsObj;
    private JButton jbEditVertexColors;
    private JPanel jPanel7;
    private JLabel jLabel16;
    private JTextField jtfNumTextures;
    private JLabel jLabel4;
    private JSpinner jSpinner1;
    private JComboBox<String> jcbMaterial;
    private TextureDisplay textureDisplay;
    private JButton jbAddTexture;
    private JLabel jLabel22;
    private JButton jbAddTile;
    private JLabel jLabel23;
    private JButton jbImportTiles;
    private JPanel jPanel3;
    private JScrollPane jScrollPane1;
    private JList<String> jlistINames;
    private JTextField jtfPaletteName;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JTextField jtfTextureName;
    private JButton jbTextName;
    private JButton jbPaletteName;
    private JCheckBox jcbEnableFog;
    private JCheckBox jcbRenderFrontAndBack;
    private JCheckBox jcbUniformNormal;
    private JLabel jLabel8;
    private JTextField jtfMaterialName;
    private JButton jbMaterialName;
    private JLabel jLabel9;
    private JSpinner jSpinner2;
    private JLabel jLabel11;
    private JComboBox<String> jcbTexGenMode;
    private TextureDisplayMaterial textureDisplayMaterial;
    private JCheckBox jcbAlwaysIncludedInImd;
    private JButton jbMoveMaterialUp;
    private JButton jbMoveMaterialDown;
    private JButton jbReplaceMaterial;
    private JLabel jLabel17;
    private JComboBox<String> jcbTexTilingU;
    private JLabel jLabel18;
    private JComboBox<String> jcbTexTilingV;
    private JLabel jLabel19;
    private JComboBox<String> jcbColorFormat;
    private JLabel jLabel20;
    private JCheckBox jcbL0;
    private JCheckBox jcbL1;
    private JCheckBox jcbL2;
    private JCheckBox jcbL3;
    private JButton jbReplaceTexture;
    private JCheckBox jcbRenderBorder;
    private JLabel jLabel21;
    private JCheckBox jcbUseVertexColors;
    private JTabbedPane jTabbedPane2;
    private JPanel jPanel4;
    private JLabel jLabel12;
    private JButton jbRotateModel;
    private JLabel jLabel13;
    private JButton jbFlipModel;
    private JButton jbMoveModelUp;
    private JButton jbMoveModelDown;
    private JButton jbMoveModelLeft;
    private JButton jbMoveModelRight;
    private JButton jbMoveModelUp1;
    private JButton jbMoveModelDown1;
    private JPanel jPanel8;
    private JCheckBox jcbBackfaceCulling;
    private JCheckBox jcbWireframe;
    private JCheckBox jcbTexturesEnabled;
    private JCheckBox jcbShadingEnabled;
    private JPanel jPanel9;
    private JScrollPane jScrollPaneSmartGrid;
    private SmartGridEditableDisplay smartGridEditableDisplay;
    private JButton jbMoveSPaintUp;
    private JButton jbMoveSPaintDown;
    private JButton jbAddSmartGrid;
    private JButton jbRemoveSmartGrid;
    // JFormDesigner - End of variables declaration  
}
