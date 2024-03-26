package formats.nsbtx2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.converter.ConverterErrorDialog;
import editor.handler.MapEditorHandler;
import formats.nsbtx.*;
import formats.nsbtx2.exceptions.NsbtxTextureSizeException;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import net.miginfocom.swing.*;

import utils.swing.ThumbnailFileChooser;
import utils.Utils;
import utils.Utils.MutableBoolean;

/**
 * @author Trifindo, JackHack96
 */
public class NsbtxEditorDialog2 extends JDialog {

    private MapEditorHandler handler;
    private NsbtxHandler2 nsbtxHandler;

    private boolean textureListEnabled = true;
    private boolean paletteListEnabled = true;
    private MutableBoolean jtfTextureActive = new MutableBoolean(true);
    private MutableBoolean jtfPaletteActive = new MutableBoolean(true);

    private static final Color editingColor = new Color(255, 185, 185);
    private static final Color rightColor = new Color(200, 255, 200);

    public NsbtxEditorDialog2(Window owner) {
        super(owner);
        initComponents();

        Utils.addListenerToJTextFieldColor(jtfTextureName, jtfTextureActive, editingColor, Color.black);
        Utils.addListenerToJTextFieldColor(jtfPaletteName, jtfPaletteActive, editingColor, Color.black);
    }

    private void jmiNewNsbtxActionPerformed(ActionEvent e) {
        nsbtxHandler.newNsbtx();

        updateViewTextureNameList(0);
        updateViewPaletteNameList(0);
        updateViewTextureName();
        updateViewPaletteName();
        updateView();
    }

    private void jmiOpenNsbtxActionPerformed(ActionEvent e) {
        openNsbtxWithDialog();
    }

    private void jmiSaveNsbtxActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            if (nsbtxHandler.getNsbtx().getPath() != null) {
                saveNsbtx();
            } else {
                saveNsbtxWithDialog();
            }
        }
    }

    private void jmiSaveNsbtxAsActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            saveNsbtxWithDialog();
        }
    }

    private void jlTextureNamesValueChanged(ListSelectionEvent e) {
        if (nsbtxHandler.getNsbtx() != null && textureListEnabled) {
            //nsbtxHandler.setTextureIndexSelected(jlPaletteNames.getSelectedIndex());
            updateViewPaletteNamesUsingTexNames();
            updateView();
        }
    }

    private void jbMoveTextureUpActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            if (nsbtxHandler.getNsbtx().moveTextureUp(getTextureIndexSelected())) {
                updateViewTextureNameList(getTextureIndexSelected() - 1);
            }
        }
    }

    private void jbMoveTextureDownActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            if (nsbtxHandler.getNsbtx().moveTextureDown(getTextureIndexSelected())) {
                updateViewTextureNameList(getTextureIndexSelected() + 1);
            }
        }
    }

    private void jlPaletteNamesValueChanged(ListSelectionEvent e) {
        if (nsbtxHandler.getNsbtx() != null && paletteListEnabled) {
            updateView();
        }
    }

    private void jbMovePaletteUpActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            if (nsbtxHandler.getNsbtx().movePaletteUp(getPaletteIndexSelected())) {
                updateViewPaletteNameList(getPaletteIndexSelected() - 1);
            }
        }
    }

    private void jbMovePaletteDownActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            if (nsbtxHandler.getNsbtx().movePaletteDown(getPaletteIndexSelected())) {
                updateViewPaletteNameList(getPaletteIndexSelected() + 1);
            }
        }
    }

    private void jbExportTextureImgActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            saveTextureWithDialog();
        }
    }

    private void jbApplyTextureNameActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            changeTextureName();
        }
    }

    private void jbApplyPaletteNameActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            changePaletteName();
        }
    }

    private void jbReplaceTextureActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            replaceTexture();
        }
    }

    private void jbAddTexturesActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            addTextures();
        }
    }

    private void jbRemoveTextureActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            removeTexture();
        }
    }

    private void jbRemovePaletteActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            removePalette();
        }
    }

    private void jbReplacePaletteActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            replacePalette();
        }
    }

    private void jbAddPalettesActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            addPalettes();
        }
    }

    private void jbAddTextureAndPaletteActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            addTextureAndPalette();
        }
    }

    private void jbReplaceTextureAndPaletteActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            replaceTextureAndPalette();
        }
    }

    private void RemoveTextureAndPaletteActionPerformed(ActionEvent e) {
        if (nsbtxHandler.getNsbtx() != null) {
            removeTextureAndPalette();
        }
    }

    private void jbImportTexAndPalsNsbtxActionPerformed(ActionEvent e) {
        if (nsbtxHandler != null) {
            importTexturesAndPalettesWithDialog();
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        this.nsbtxHandler = new NsbtxHandler2(handler, this);

        this.nsbtxDisplay.init(nsbtxHandler);
        this.paletteDisplay.init(nsbtxHandler);

    }

    private void updateView() {
        nsbtxDisplay.updateImage();
        nsbtxDisplay.repaint();

        paletteDisplay.updatePalette();
        paletteDisplay.repaint();

        updateViewTextureProperties();

        updateViewTextureName();
        updateViewPaletteName();
    }

    private void updateViewTextureProperties() {
        if (nsbtxHandler.getSelectedTexture() != null) {
            int index = Nsbtx2.formatToJcbLookup[nsbtxHandler.getSelectedTexture().getColorFormat()];
            jcbColorTexture.setSelectedIndex(index);
            jtfTextureWidth.setText(String.valueOf(nsbtxHandler.getSelectedTexture().getWidth()));
            jtfTextureHeight.setText(String.valueOf(nsbtxHandler.getSelectedTexture().getHeight()));
            jcbTransparentColor.setSelected(nsbtxHandler.getSelectedTexture().isTransparent());
        }
    }

    private void updateViewTextureName() {
        if (nsbtxHandler.getSelectedTexture() != null) {
            String name = nsbtxHandler.getSelectedTexture().getName();
            jtfTextureActive.value = false;
            jtfTextureName.setText(name);
            jtfTextureName.setBackground(UIManager.getColor("TextPane.background"));
            jtfTextureName.setForeground(UIManager.getColor("TextPane.foreground"));
            jtfTextureActive.value = true;
        } else {
            jtfTextureActive.value = false;
            jtfTextureName.setText("");
            jtfTextureName.setBackground(UIManager.getColor("TextPane.background"));
            jtfTextureName.setForeground(UIManager.getColor("TextPane.foreground"));
            jtfTextureActive.value = true;
        }
    }

    private void updateViewPaletteName() {
        if (nsbtxHandler.getSelectedPalette() != null) {
            String name = nsbtxHandler.getSelectedPalette().getName();
            jtfPaletteActive.value = false;
            jtfPaletteName.setText(name);
            jtfPaletteName.setBackground(UIManager.getColor("TextPane.background"));
            jtfPaletteName.setForeground(UIManager.getColor("TextPane.foreground"));
            jtfPaletteActive.value = true;
        } else {
            jtfPaletteActive.value = false;
            jtfPaletteName.setText("");
            jtfPaletteName.setBackground(UIManager.getColor("TextPane.background"));
            jtfPaletteName.setForeground(UIManager.getColor("TextPane.foreground"));
            jtfPaletteActive.value = true;
        }
    }

    private void updateViewTextureNameList(int indexSelected) {
        textureListEnabled = false;
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < nsbtxHandler.getNsbtx().getTextures().size(); i++) {
            String name = nsbtxHandler.getNsbtx().getTexture(i).getName();
            demoList.addElement(name);
        }
        jlTextureNames.setModel(demoList);
        if (indexSelected > demoList.size() - 1) {
            indexSelected = demoList.size() - 1;
        } else if (indexSelected < 0) {
            indexSelected = 0;
        }
        jlTextureNames.setSelectedIndex(indexSelected);//nsbtxHandler.getTextureIndexSelected());
        textureListEnabled = true;
    }

    private void updateViewPaletteNameList(int indexSelected) {
        paletteListEnabled = false;
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < nsbtxHandler.getNsbtx().getPalettes().size(); i++) {
            String name = nsbtxHandler.getNsbtx().getPalette(i).getName();
            demoList.addElement(name);
        }
        jlPaletteNames.setModel(demoList);
        if (indexSelected > demoList.size() - 1) {
            indexSelected = demoList.size() - 1;
        } else if (indexSelected < 0) {
            indexSelected = 0;
        }
        jlPaletteNames.setSelectedIndex(indexSelected);//nsbtxHandler.getPaletteIndexSelected());
        paletteListEnabled = true;
    }

    private void updateViewPaletteNamesUsingTexNames() {
        int index = jlTextureNames.getSelectedIndex();
        int paletteIndex;
        String paletteName = nsbtxHandler.getNsbtx().getTexture(index).getName();
        if ((paletteIndex = nsbtxHandler.indexOfPaletteName(paletteName)) != -1
                || (paletteIndex = nsbtxHandler.indexOfPaletteName(setElementInString(paletteName, "_pl"))) != -1) {
            jlPaletteNames.setSelectedIndex(paletteIndex);
            jlPaletteNames.ensureIndexIsVisible(jlPaletteNames.getSelectedIndex());
        }
    }

    private void openNsbtxWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastNsbtxDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open NSBTX File");
        final int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                nsbtxHandler.loadNsbtx(fc.getSelectedFile().getPath());

                updateViewTextureNameList(0);
                updateViewPaletteNameList(0);

                textureListEnabled = false;
                jlTextureNames.setSelectedIndex(0);
                textureListEnabled = true;
                paletteListEnabled = false;
                jlPaletteNames.setSelectedIndex(0);
                paletteListEnabled = true;

                updateView();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening NSBTX", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTextureWithDialog() {
        if (nsbtxHandler.getNsbtx().hasTextures() && nsbtxHandler.getNsbtx().hasPalettes()) {
            final ThumbnailFileChooser fc = new ThumbnailFileChooser();
            if (handler.getLastNsbtxDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save Texture as PNG");
            final int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    String path = fc.getSelectedFile().getPath();
                    path = Utils.addExtensionToPath(path, "png");
                    ImageIO.write(nsbtxHandler.getSelectedImage(), "png", new File(path));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Can't save file.",
                            "Error saving image", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "The NSBTX needs at least 1 texture and 1 palette for exporting the texture",
                    "Can't export texture image", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void replaceTexture() {
        if (nsbtxHandler.getNsbtx().hasTextures() && nsbtxHandler.getNsbtx().hasPalettes()) {
            final NsbtxColorFormatReplaceSelector dialog = new NsbtxColorFormatReplaceSelector(handler.getMainFrame());
            dialog.init(
                    nsbtxHandler.getSelectedTexture().getColorFormat(),
                    nsbtxHandler.getSelectedTexture().isTransparent());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.getReturnValue() == NsbtxColorFormatReplaceSelector.APPROVE_OPTION) {
                int colorFormat = dialog.getFormat();
                boolean isTransparent = dialog.getIsTransparent();

                final ThumbnailFileChooser fc = new ThumbnailFileChooser();
                if (handler.getLastNsbtxDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
                }
                fc.setFileFilter(new FileNameExtensionFilter("png (*.png)", "png"));
                fc.setApproveButtonText("Open");
                fc.setDialogTitle("Open PNG Image");
                final int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
                    try {
                        BufferedImage img = ImageIO.read(fc.getSelectedFile());

                        nsbtxHandler.getNsbtx().replaceTexture(
                                jlTextureNames.getSelectedIndex(),
                                jlPaletteNames.getSelectedIndex(),
                                img, colorFormat, isTransparent
                        );

                        updateView();

                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Can't open file.",
                                "Error opening image", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "The NSBTX needs at least 1 texture and 1 palette for replacing the texture",
                    "Can't replace texture", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addTextures() {
        if (nsbtxHandler.getNsbtx().hasPalettes()) {
            final NsbtxColorFormatAddSelector dialog = new NsbtxColorFormatAddSelector(handler.getMainFrame());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.getReturnValue() == NsbtxColorFormatAddSelector.APPROVE_OPTION) {
                int colorFormat = dialog.getFormat();
                boolean isTransparent = dialog.getIsTransparent();

                final ThumbnailFileChooser fc = new ThumbnailFileChooser();
                if (handler.getLastNsbtxDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
                }
                fc.setFileFilter(new FileNameExtensionFilter("png (*.png)", "png"));
                fc.setApproveButtonText("Open");
                fc.setDialogTitle("Open PNG Image");
                fc.setMultiSelectionEnabled(true);
                final int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());

                    File[] files = fc.getSelectedFiles();

                    boolean errorLoadingFiles = false;
                    for (File file : files) {
                        try {
                            BufferedImage img = ImageIO.read(file);
                            String name = Utils.removeExtensionFromPath(file.getName());

                            nsbtxHandler.getNsbtx().addTexture(
                                    jlTextureNames.getSelectedIndex(),
                                    jlPaletteNames.getSelectedIndex(),
                                    img, colorFormat, isTransparent, name
                            );
                        } catch (IOException ex) {
                            errorLoadingFiles = true;
                        }
                    }

                    if (errorLoadingFiles) {
                        JOptionPane.showMessageDialog(this, "Some images could not be opened",
                                "Error opening images", JOptionPane.ERROR_MESSAGE);
                    }

                    updateViewTextureNameList(getTextureIndexSelected() + 1);
                    updateView();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "The NSBTX needs at least 1 palette for adding only the texture",
                    "Can't add texture", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeTexture() {
        if (nsbtxHandler.getNsbtx().hasTextures()) {
            int index = getTextureIndexSelected();
            if (index != -1) {
                nsbtxHandler.getNsbtx().getTextures().remove(index);
            }
        }
        updateViewTextureNameList(getTextureIndexSelected());
        updateView();
    }

    private void replacePalette() {
        if (nsbtxHandler.getNsbtx().hasTextures() && nsbtxHandler.getNsbtx().hasPalettes()) {
            final ThumbnailFileChooser fc = new ThumbnailFileChooser();
            if (handler.getLastNsbtxDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("png (*.png)", "png"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Open PNG Image");
            final int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    BufferedImage img = ImageIO.read(fc.getSelectedFile());
                    try {
                        nsbtxHandler.getNsbtx().replacePalette(
                                getTextureIndexSelected(),
                                getPaletteIndexSelected(), img);

                        updateView();
                    } catch (NsbtxTextureSizeException ex) {
                        JOptionPane.showMessageDialog(this,
                                "The source image and the new image must have the same size",
                                "Can't replace palette", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Can't open file.",
                            "Error opening image", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "The NSBTX needs at least 1 texture and 1 palette for replacing only the palette",
                    "Can't replace palette", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addPalettes() {
        if (nsbtxHandler.getNsbtx().hasTextures() && nsbtxHandler.getNsbtx().hasPalettes()) {
            final ThumbnailFileChooser fc = new ThumbnailFileChooser();
            if (handler.getLastNsbtxDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("png (*.png)", "png"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Open PNG Image");
            fc.setMultiSelectionEnabled(true);
            final int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());

                File[] files = fc.getSelectedFiles();

                boolean errorLoadingFiles = false;
                boolean errorTexturesSize = false;
                String wrongSizeTextureNames = "";
                int numWrongSizeTextures = 0;
                final int maxWrongSizeTextures = 6;
                for (File file : files) {
                    try {
                        BufferedImage img = ImageIO.read(file);
                        String name = Utils.removeExtensionFromPath(file.getName());
                        try {
                            nsbtxHandler.getNsbtx().addPalette(
                                    getTextureIndexSelected(),
                                    getPaletteIndexSelected(),
                                    name + "_pl", img);
                        } catch (NsbtxTextureSizeException ex) {
                            errorTexturesSize = true;
                            if (numWrongSizeTextures < maxWrongSizeTextures) {
                                wrongSizeTextureNames += "- " + name + "\n";
                            } else if (numWrongSizeTextures == maxWrongSizeTextures) {
                                wrongSizeTextureNames += "...(more)" + "\n";
                            }
                            numWrongSizeTextures++;
                        }
                    } catch (IOException ex) {
                        errorLoadingFiles = true;
                    }
                }

                if (errorTexturesSize) {
                    JOptionPane.showMessageDialog(this,
                            String.valueOf(numWrongSizeTextures)
                                    + " images do not have the same size as the source texture: \n"
                                    + wrongSizeTextureNames,
                            "Can't add some palettes", JOptionPane.ERROR_MESSAGE);
                } else if (errorLoadingFiles) {
                    JOptionPane.showMessageDialog(this, "There was a problem opening some images",
                            "Error opening some images", JOptionPane.ERROR_MESSAGE);
                }

                updateViewPaletteNameList(getPaletteIndexSelected() + 1);
                updateView();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "The NSBTX needs at least 1 texture and 1 palette for adding only the palette",
                    "Can't add palette", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removePalette() {
        if (nsbtxHandler.getNsbtx().hasPalettes()) {
            int index = getPaletteIndexSelected();
            if (index != -1) {
                nsbtxHandler.getNsbtx().getPalettes().remove(index);
            }
        }
        updateViewPaletteNameList(getPaletteIndexSelected());
        updateView();
    }

    private void replaceTextureAndPalette() {
        if (nsbtxHandler.getNsbtx().hasTextures() && nsbtxHandler.getNsbtx().hasPalettes()) {
            final NsbtxColorFormatReplaceSelector dialog = new NsbtxColorFormatReplaceSelector(handler.getMainFrame());
            dialog.init(
                    nsbtxHandler.getSelectedTexture().getColorFormat(),
                    nsbtxHandler.getSelectedTexture().isTransparent());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.getReturnValue() == NsbtxColorFormatReplaceSelector.APPROVE_OPTION) {
                int colorFormat = dialog.getFormat();
                boolean isTransparent = dialog.getIsTransparent();

                final ThumbnailFileChooser fc = new ThumbnailFileChooser();
                if (handler.getLastNsbtxDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
                }
                fc.setFileFilter(new FileNameExtensionFilter("png (*.png)", "png"));
                fc.setApproveButtonText("Open");
                fc.setDialogTitle("Open PNG Image");
                final int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
                    try {
                        BufferedImage img = ImageIO.read(fc.getSelectedFile());

                        nsbtxHandler.getNsbtx().replaceTextureAndPalette(
                                getTextureIndexSelected(),
                                getPaletteIndexSelected(), img,
                                colorFormat, isTransparent);

                        updateView();

                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Can't open file.",
                                "Error opening image", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "The NSBTX needs at least 1 texture and 1 palette for replacing the texture",
                    "Can't replace texture", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addTextureAndPalette() {
        final NsbtxColorFormatAddSelector dialog = new NsbtxColorFormatAddSelector(handler.getMainFrame());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == NsbtxColorFormatReplaceSelector.APPROVE_OPTION) {
            int colorFormat = dialog.getFormat();
            boolean isTransparent = dialog.getIsTransparent();

            final ThumbnailFileChooser fc = new ThumbnailFileChooser();
            if (handler.getLastNsbtxDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("png (*.png)", "png"));
            fc.setApproveButtonText("Open");
            fc.setDialogTitle("Open PNG Image");
            fc.setMultiSelectionEnabled(true);
            final int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());

                boolean errorLoadingFiles = false;
                File[] files = fc.getSelectedFiles();
                for (File file : files) {
                    try {
                        BufferedImage img = ImageIO.read(file);

                        String name = Utils.removeExtensionFromPath(file.getName());

                        nsbtxHandler.getNsbtx().addTextureAndPalette(
                                getTextureIndexSelected(),
                                getPaletteIndexSelected(), img,
                                colorFormat, isTransparent, name, name + "_pl");

                    } catch (IOException ex) {
                        errorLoadingFiles = true;
                    }
                }

                if (errorLoadingFiles) {
                    JOptionPane.showMessageDialog(this, "There was a problem opening some images",
                            "Error opening some images", JOptionPane.ERROR_MESSAGE);
                }

                updateViewTextureNameList(getTextureIndexSelected() + 1);
                updateViewPaletteNameList(getPaletteIndexSelected() + 1);
                updateView();
            }
        }
    }

    private void removeTextureAndPalette() {
        if (nsbtxHandler.getNsbtx().hasTextures()) {
            int index = getTextureIndexSelected();
            if (index != -1) {
                nsbtxHandler.getNsbtx().getTextures().remove(index);
            }
        }

        if (nsbtxHandler.getNsbtx().hasPalettes()) {
            int index = getPaletteIndexSelected();
            if (index != -1) {
                nsbtxHandler.getNsbtx().getPalettes().remove(index);
            }
        }
        updateViewTextureNameList(getTextureIndexSelected());
        updateViewPaletteNameList(getPaletteIndexSelected());
        updateView();
    }

    private void importTexturesAndPalettesWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastNsbtxDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Choose the NSBTX to import data from");
        final int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
            try {

                Nsbtx2 nsbtx = NsbtxLoader2.loadNsbtx(fc.getSelectedFile().getPath());

                final NsbtxImportDialog dialog = new NsbtxImportDialog(handler.getMainFrame());
                dialog.init(nsbtx);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);

                if (dialog.getReturnValue() == NsbtxImportDialog.APPROVE_OPTION) {

                    nsbtxHandler.getNsbtx().addNsbtx(dialog.getNsbtx());

                    updateViewTextureNameList(nsbtxHandler.getNsbtx().getTextures().size() - 1);
                    updateViewPaletteNameList(nsbtxHandler.getNsbtx().getPalettes().size() - 1);

                    updateView();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening NSBTX", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeTextureName() {
        if (nsbtxHandler.getSelectedTexture() != null) {
            String name = jtfTextureName.getText();
            if (name.length() <= Nsbtx2.maxNameSize) {
                jtfTextureActive.value = false;
                nsbtxHandler.getSelectedTexture().setName(name);
                jtfTextureName.setBackground(rightColor);
                jtfTextureName.setForeground(Color.black);
                jtfTextureActive.value = true;

                updateViewTextureNameList(getTextureIndexSelected());
            } else {
                JOptionPane.showMessageDialog(this,
                        "The texture name has more than 16 characters",
                        "The name is too long",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void changePaletteName() {
        if (nsbtxHandler.getSelectedPalette() != null) {
            String name = jtfPaletteName.getText();
            if (name.length() <= Nsbtx2.maxNameSize) {
                jtfPaletteActive.value = false;
                nsbtxHandler.getSelectedPalette().setName(name);
                jtfPaletteName.setBackground(rightColor);
                jtfPaletteName.setForeground(Color.black);
                jtfPaletteActive.value = true;

                updateViewPaletteNameList(getPaletteIndexSelected());
            } else {
                JOptionPane.showMessageDialog(this,
                        "The palette name has more than 16 characters",
                        "The name is too long",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean hasRepeatedTextures() {
        String repeatedTex = nsbtxHandler.getNsbtx().getRepeatedTextureName();
        String repeatedPal = nsbtxHandler.getNsbtx().getRepeatedPaletteName();
        if (repeatedTex != null) {
            JOptionPane.showMessageDialog(this, "The texture named \"" + repeatedTex + "\" is repeated",
                    "Repeated texture name", JOptionPane.ERROR_MESSAGE);
            return true;
        } else if (repeatedPal != null) {
            JOptionPane.showMessageDialog(this, "The palette named \"" + repeatedPal + "\" is repeated",
                    "Repeated palette name", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private void saveNsbtx() {
        if (!hasRepeatedTextures()) {
            String path = nsbtxHandler.getNsbtx().getPath();
            path = Utils.removeExtensionFromPath(path);
            path = Utils.addExtensionToPath(path, "imd");

            NsbtxImd imd = new NsbtxImd(nsbtxHandler.getNsbtx());

            try {
                imd.saveToFile(path);

                saveImdToNsbmd(path);
            } catch (IOException | ParserConfigurationException | TransformerException ex) {
                JOptionPane.showMessageDialog(this, "There was an error saving the IMD",
                        "Error saving IMD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveNsbtxWithDialog() {
        if (nsbtxHandler.getNsbtx().hasTextures() && nsbtxHandler.getNsbtx().hasPalettes()) {
            if (!hasRepeatedTextures()) {
                final JFileChooser fc = new JFileChooser();
                if (handler.getLastNsbtxDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
                }
                fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
                fc.setApproveButtonText("Save");
                fc.setDialogTitle("Save NSBTX");
                final int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
                    try {
                        String path = fc.getSelectedFile().getPath();
                        path = Utils.removeExtensionFromPath(path);
                        path = Utils.addExtensionToPath(path, "imd");

                        NsbtxImd imd = new NsbtxImd(nsbtxHandler.getNsbtx());

                        imd.saveToFile(path);

                        saveImdToNsbmd(path);

                    } catch (IOException | ParserConfigurationException | TransformerException ex) {
                        JOptionPane.showMessageDialog(this, "There was an error saving the IMD",
                                "Error saving IMD", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "The NSBTX needs at least 1 texture and 1 palette for exporting the texture",
                    "Can't export texture image", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveImdToNsbmd(String imdPath) {
        File file = new File(imdPath);
        if (file.exists()) {
            String filename = new File(imdPath).getName();
            filename = Utils.removeExtensionFromPath(filename);
            try {
                String converterPath = "converter/g3dcvtr.exe";
                String[] cmd;
                if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                    cmd = new String[]{converterPath, imdPath, "-etex", "-o", filename};
                } else {
                    cmd = new String[]{"wine", converterPath, imdPath, "-etex", "-o", filename};
                    // NOTE: wine call works only with relative path
                }

                if (!Files.exists(Paths.get(converterPath))) {
                    throw new IOException();
                }

                Process p = new ProcessBuilder(cmd).start();

                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                String outputString = "";
                String line = null;
                while ((line = stdError.readLine()) != null) {
                    outputString += line + "\n";
                }

                p.waitFor();
                p.destroy();

                String nsbPath = Utils.removeExtensionFromPath(imdPath);
                nsbPath = Utils.addExtensionToPath(nsbPath, "nsbtx");

                filename = Utils.removeExtensionFromPath(filename);
                filename = Utils.addExtensionToPath(filename, "nsbtx");

                System.out.println(System.getProperty("user.dir"));
                File srcFile = new File(System.getProperty("user.dir") + File.separator + filename);
                File dstFile = new File(nsbPath);
                if (srcFile.exists()) {
                    try {
                        Files.move(srcFile.toPath(), dstFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                        //srcFile.renameTo(new File(nsbPath));
                        JOptionPane.showMessageDialog(this, "NSBTX succesfully saved.",
                                "NSBTX saved", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this,
                                "File was not moved to the save directory. \n"
                                        + "Reopen Pokemon DS Map Studio and try again.",
                                "Problem saving generated file",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    if (file.exists()) {

                        try {
                            Files.delete(file.toPath());
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "The intermediate IMD file can't be deleted",
                                    "The intermediate IMD delete error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    ConverterErrorDialog dialog = new ConverterErrorDialog(handler.getMainFrame());
                    dialog.init("There was a problem saving the NSBTX file. \n"
                                    + "The output from the converter is:",
                            outputString);
                    dialog.setTitle("Problem generating file");
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "The program \"g3dcvtr.exe\" is not found in the \"converter\" folder.\n"
                                + "Put the program and its *.dll files in the folder and try again.",
                        "Converter not found",
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException ex) {
                JOptionPane.showMessageDialog(this,
                        "The IMD was not converted",
                        "Problem converting the IMD",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "There was a problem saving the NSBTX",
                    "Problem creating the NSBTX",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getTextureIndexSelected() {
        return jlTextureNames.getSelectedIndex();
    }

    public int getPaletteIndexSelected() {
        return jlPaletteNames.getSelectedIndex();
    }

    private String setElementInString(String src, String newPart) {
        return src + newPart;
        //return src.replaceFirst(" ", newPart).substring(0, Math.min(16, src.length()));
    }

    private void jbImportTexNsbtxActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void jbImportPalNsbtxActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jMenuBar1 = new JMenuBar();
        jmFile = new JMenu();
        jmiNewNsbtx = new JMenuItem();
        jmiOpenNsbtx = new JMenuItem();
        jmiSaveNsbtx = new JMenuItem();
        jmiSaveNsbtxAs = new JMenuItem();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jlTextureNames = new JList<>();
        panel4 = new JPanel();
        jbMoveTextureUp = new JButton();
        jLabel7 = new JLabel();
        jbMoveTextureDown = new JButton();
        jPanel2 = new JPanel();
        jScrollPane2 = new JScrollPane();
        jlPaletteNames = new JList<>();
        panel5 = new JPanel();
        jLabel9 = new JLabel();
        jbMovePaletteUp = new JButton();
        jbMovePaletteDown = new JButton();
        panel1 = new JPanel();
        jPanel3 = new JPanel();
        nsbtxDisplay = new NsbtxDisplay2();
        jPanel5 = new JPanel();
        paletteDisplay = new PaletteDisplay2();
        panel2 = new JPanel();
        jPanel4 = new JPanel();
        jLabel1 = new JLabel();
        jcbColorTexture = new JComboBox<>();
        jLabel2 = new JLabel();
        jtfTextureName = new JTextField();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jtfTextureWidth = new JTextField();
        jtfTextureHeight = new JTextField();
        jbExportTextureImg = new JButton();
        jcbTransparentColor = new JCheckBox();
        jbApplyTextureName = new JButton();
        jPanel6 = new JPanel();
        jLabel6 = new JLabel();
        jtfPaletteName = new JTextField();
        jbApplyPaletteName = new JButton();
        jPanel10 = new JPanel();
        jbImportTexAndPalsNsbtx = new JButton();
        jbImportTexNsbtx = new JButton();
        jbImportPalNsbtx = new JButton();
        panel3 = new JPanel();
        jPanel9 = new JPanel();
        jbAddTextureAndPalette = new JButton();
        jbReplaceTextureAndPalette = new JButton();
        RemoveTextureAndPalette = new JButton();
        jPanel7 = new JPanel();
        jbReplaceTexture = new JButton();
        jbAddTextures = new JButton();
        jbRemoveTexture = new JButton();
        jPanel8 = new JPanel();
        jbRemovePalette = new JButton();
        jbReplacePalette = new JButton();
        jbAddPalettes = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("NSBTX Editor");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 5,hidemode 3,gap 5 5",
            // columns
            "[160,shrink 0,fill]" +
            "[160,shrink 0,fill]" +
            "[fill]" +
            "[360,grow,fill]" +
            "[fill]",
            // rows
            "[grow,fill]"));

        //======== jMenuBar1 ========
        {

            //======== jmFile ========
            {
                jmFile.setText("File");

                //---- jmiNewNsbtx ----
                jmiNewNsbtx.setText("New NSBTX...");
                jmiNewNsbtx.addActionListener(e -> jmiNewNsbtxActionPerformed(e));
                jmFile.add(jmiNewNsbtx);
                jmFile.addSeparator();

                //---- jmiOpenNsbtx ----
                jmiOpenNsbtx.setText("Open NSBTX...");
                jmiOpenNsbtx.addActionListener(e -> jmiOpenNsbtxActionPerformed(e));
                jmFile.add(jmiOpenNsbtx);
                jmFile.addSeparator();

                //---- jmiSaveNsbtx ----
                jmiSaveNsbtx.setText("Save NSBTX...");
                jmiSaveNsbtx.addActionListener(e -> jmiSaveNsbtxActionPerformed(e));
                jmFile.add(jmiSaveNsbtx);

                //---- jmiSaveNsbtxAs ----
                jmiSaveNsbtxAs.setText("Save NSBTX as...");
                jmiSaveNsbtxAs.addActionListener(e -> jmiSaveNsbtxAsActionPerformed(e));
                jmFile.add(jmiSaveNsbtxAs);
            }
            jMenuBar1.add(jmFile);
        }
        setJMenuBar(jMenuBar1);

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Texture Names"));
            jPanel1.setMinimumSize(null);
            jPanel1.setPreferredSize(new Dimension(150, 198));
            jPanel1.setLayout(new MigLayout(
                "insets 5,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[fill]" +
                "[60,fill]",
                // rows
                "[grow,fill]" +
                "[]"));

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane1.setPreferredSize(new Dimension(150, 130));

                //---- jlTextureNames ----
                jlTextureNames.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };
                    @Override
                    public int getSize() { return values.length; }
                    @Override
                    public String getElementAt(int i) { return values[i]; }
                });
                jlTextureNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlTextureNames.setPreferredSize(null);
                jlTextureNames.addListSelectionListener(e -> jlTextureNamesValueChanged(e));
                jScrollPane1.setViewportView(jlTextureNames);
            }
            jPanel1.add(jScrollPane1, "cell 0 0 3 1");

            //======== panel4 ========
            {
                panel4.setLayout(new GridBagLayout());
                ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- jbMoveTextureUp ----
                jbMoveTextureUp.setText("\u25b2");
                jbMoveTextureUp.setIcon(null);
                jbMoveTextureUp.setActionCommand("\u25b2");
                jbMoveTextureUp.addActionListener(e -> jbMoveTextureUpActionPerformed(e));
                panel4.add(jbMoveTextureUp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- jLabel7 ----
                jLabel7.setText("Move:");
                panel4.add(jLabel7, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- jbMoveTextureDown ----
                jbMoveTextureDown.setText("\u25bc");
                jbMoveTextureDown.setIcon(null);
                jbMoveTextureDown.addActionListener(e -> jbMoveTextureDownActionPerformed(e));
                panel4.add(jbMoveTextureDown, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            jPanel1.add(panel4, "cell 1 1");
        }
        contentPane.add(jPanel1, "cell 0 0");

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Palette Names"));
            jPanel2.setLayout(new MigLayout(
                "insets 5,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[grow,fill]" +
                "[]"));

            //======== jScrollPane2 ========
            {
                jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane2.setPreferredSize(new Dimension(130, 130));

                //---- jlPaletteNames ----
                jlPaletteNames.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };
                    @Override
                    public int getSize() { return values.length; }
                    @Override
                    public String getElementAt(int i) { return values[i]; }
                });
                jlPaletteNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlPaletteNames.addListSelectionListener(e -> jlPaletteNamesValueChanged(e));
                jScrollPane2.setViewportView(jlPaletteNames);
            }
            jPanel2.add(jScrollPane2, "cell 0 0 2 1");

            //======== panel5 ========
            {
                panel5.setLayout(new GridBagLayout());
                ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- jLabel9 ----
                jLabel9.setText("Move:");
                panel5.add(jLabel9, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- jbMovePaletteUp ----
                jbMovePaletteUp.setText("\u25b2");
                jbMovePaletteUp.addActionListener(e -> jbMovePaletteUpActionPerformed(e));
                panel5.add(jbMovePaletteUp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- jbMovePaletteDown ----
                jbMovePaletteDown.setText("\u25bc");
                jbMovePaletteDown.addActionListener(e -> jbMovePaletteDownActionPerformed(e));
                panel5.add(jbMovePaletteDown, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            jPanel2.add(panel5, "cell 1 1");
        }
        contentPane.add(jPanel2, "cell 1 0");

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]" +
                "[]"));

            //======== jPanel3 ========
            {
                jPanel3.setBorder(new TitledBorder("Texture Display"));

                //---- nsbtxDisplay ----
                nsbtxDisplay.setBorder(new LineBorder(new Color(102, 102, 102)));

                GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup()
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(nsbtxDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup()
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(nsbtxDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }
            panel1.add(jPanel3, "cell 0 0");

            //======== jPanel5 ========
            {
                jPanel5.setBorder(new TitledBorder("Palette Display"));

                GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
                jPanel5.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                    jPanel5Layout.createParallelGroup()
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(paletteDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                jPanel5Layout.setVerticalGroup(
                    jPanel5Layout.createParallelGroup()
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(paletteDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }
            panel1.add(jPanel5, "cell 0 1");
        }
        contentPane.add(panel1, "cell 2 0");

        //======== panel2 ========
        {
            panel2.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[]" +
                "[]" +
                "[]"));

            //======== jPanel4 ========
            {
                jPanel4.setBorder(new TitledBorder("Texture Properties"));
                jPanel4.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[fill]" +
                    "[grow,fill]" +
                    "[fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]"));

                //---- jLabel1 ----
                jLabel1.setText("Color format:");
                jPanel4.add(jLabel1, "cell 0 0");

                //---- jcbColorTexture ----
                jcbColorTexture.setModel(new DefaultComboBoxModel<>(new String[] {
                    "Palette 4",
                    "Palette 16",
                    "Palette 256",
                    "A3I5",
                    "A5I3"
                }));
                jcbColorTexture.setEnabled(false);
                jcbColorTexture.setMinimumSize(new Dimension(120, 20));
                jcbColorTexture.setPreferredSize(new Dimension(120, 20));
                jPanel4.add(jcbColorTexture, "cell 1 0 2 1");

                //---- jLabel2 ----
                jLabel2.setText("Texture Name: ");
                jPanel4.add(jLabel2, "cell 0 1");

                //---- jtfTextureName ----
                jtfTextureName.setText(" ");
                jPanel4.add(jtfTextureName, "cell 1 1");

                //---- jLabel3 ----
                jLabel3.setText("Texture Width: ");
                jPanel4.add(jLabel3, "cell 0 2");

                //---- jLabel4 ----
                jLabel4.setText("Texture Height: ");
                jPanel4.add(jLabel4, "cell 0 3");

                //---- jtfTextureWidth ----
                jtfTextureWidth.setText(" ");
                jtfTextureWidth.setEnabled(false);
                jPanel4.add(jtfTextureWidth, "cell 1 2 2 1");

                //---- jtfTextureHeight ----
                jtfTextureHeight.setText(" ");
                jtfTextureHeight.setEnabled(false);
                jPanel4.add(jtfTextureHeight, "cell 1 3 2 1");

                //---- jbExportTextureImg ----
                jbExportTextureImg.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
                jbExportTextureImg.setText("Export Texture Image...");
                jbExportTextureImg.addActionListener(e -> jbExportTextureImgActionPerformed(e));
                jPanel4.add(jbExportTextureImg, "cell 0 5 3 1");

                //---- jcbTransparentColor ----
                jcbTransparentColor.setText("Transparent color");
                jcbTransparentColor.setEnabled(false);
                jPanel4.add(jcbTransparentColor, "cell 0 4 2 1");

                //---- jbApplyTextureName ----
                jbApplyTextureName.setText("Apply");
                jbApplyTextureName.addActionListener(e -> jbApplyTextureNameActionPerformed(e));
                jPanel4.add(jbApplyTextureName, "cell 2 1");
            }
            panel2.add(jPanel4, "cell 0 0");

            //======== jPanel6 ========
            {
                jPanel6.setBorder(new TitledBorder("Palette Properties"));
                jPanel6.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[fill]" +
                    "[grow,fill]" +
                    "[fill]",
                    // rows
                    "[fill]"));

                //---- jLabel6 ----
                jLabel6.setText("Palette Name:");
                jPanel6.add(jLabel6, "cell 0 0");

                //---- jtfPaletteName ----
                jtfPaletteName.setText(" ");
                jPanel6.add(jtfPaletteName, "cell 1 0");

                //---- jbApplyPaletteName ----
                jbApplyPaletteName.setText("Apply");
                jbApplyPaletteName.addActionListener(e -> jbApplyPaletteNameActionPerformed(e));
                jPanel6.add(jbApplyPaletteName, "cell 2 0");
            }
            panel2.add(jPanel6, "cell 0 1");

            //======== jPanel10 ========
            {
                jPanel10.setBorder(new TitledBorder("NSBTX Tools"));
                jPanel10.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]"));

                //---- jbImportTexAndPalsNsbtx ----
                jbImportTexAndPalsNsbtx.setText("Import Textures and Palettes from NSBTX...");
                jbImportTexAndPalsNsbtx.addActionListener(e -> jbImportTexAndPalsNsbtxActionPerformed(e));
                jPanel10.add(jbImportTexAndPalsNsbtx, "cell 0 0");

                //---- jbImportTexNsbtx ----
                jbImportTexNsbtx.setText("Import Only Textures from NSBTX...");
                jbImportTexNsbtx.setEnabled(false);
                jbImportTexNsbtx.addActionListener(e -> jbImportTexNsbtxActionPerformed(e));
                jPanel10.add(jbImportTexNsbtx, "cell 0 1");

                //---- jbImportPalNsbtx ----
                jbImportPalNsbtx.setText("Import Only Palettes from NSBTX...");
                jbImportPalNsbtx.setEnabled(false);
                jbImportPalNsbtx.addActionListener(e -> jbImportPalNsbtxActionPerformed(e));
                jPanel10.add(jbImportPalNsbtx, "cell 0 2");
            }
            panel2.add(jPanel10, "cell 0 2");
        }
        contentPane.add(panel2, "cell 3 0");

        //======== panel3 ========
        {
            panel3.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]" +
                "[]" +
                "[]"));

            //======== jPanel9 ========
            {
                jPanel9.setBorder(new TitledBorder("Texture and Palette Editor"));
                jPanel9.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]"));

                //---- jbAddTextureAndPalette ----
                jbAddTextureAndPalette.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                jbAddTextureAndPalette.setText("Add Textures and Palettes...");
                jbAddTextureAndPalette.addActionListener(e -> jbAddTextureAndPaletteActionPerformed(e));
                jPanel9.add(jbAddTextureAndPalette, "cell 0 1");

                //---- jbReplaceTextureAndPalette ----
                jbReplaceTextureAndPalette.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                jbReplaceTextureAndPalette.setText("Replace Texture and Palette...");
                jbReplaceTextureAndPalette.addActionListener(e -> jbReplaceTextureAndPaletteActionPerformed(e));
                jPanel9.add(jbReplaceTextureAndPalette, "cell 0 0");

                //---- RemoveTextureAndPalette ----
                RemoveTextureAndPalette.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                RemoveTextureAndPalette.setText("Remove Texture and Palette");
                RemoveTextureAndPalette.addActionListener(e -> RemoveTextureAndPaletteActionPerformed(e));
                jPanel9.add(RemoveTextureAndPalette, "cell 0 2");
            }
            panel3.add(jPanel9, "cell 0 0");

            //======== jPanel7 ========
            {
                jPanel7.setBorder(new TitledBorder("Texture Editor"));
                jPanel7.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]"));

                //---- jbReplaceTexture ----
                jbReplaceTexture.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                jbReplaceTexture.setText("Replace Only Texture...");
                jbReplaceTexture.addActionListener(e -> jbReplaceTextureActionPerformed(e));
                jPanel7.add(jbReplaceTexture, "cell 0 0");

                //---- jbAddTextures ----
                jbAddTextures.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                jbAddTextures.setText("Add Only Textures...");
                jbAddTextures.addActionListener(e -> jbAddTexturesActionPerformed(e));
                jPanel7.add(jbAddTextures, "cell 0 1");

                //---- jbRemoveTexture ----
                jbRemoveTexture.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                jbRemoveTexture.setText("Remove Only Texture");
                jbRemoveTexture.addActionListener(e -> jbRemoveTextureActionPerformed(e));
                jPanel7.add(jbRemoveTexture, "cell 0 2");
            }
            panel3.add(jPanel7, "cell 0 1");

            //======== jPanel8 ========
            {
                jPanel8.setBorder(new TitledBorder("Palette Editor"));
                jPanel8.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]"));

                //---- jbRemovePalette ----
                jbRemovePalette.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                jbRemovePalette.setText("Remove Only Palette");
                jbRemovePalette.addActionListener(e -> jbRemovePaletteActionPerformed(e));
                jPanel8.add(jbRemovePalette, "cell 0 2");

                //---- jbReplacePalette ----
                jbReplacePalette.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
                jbReplacePalette.setText("Replace Only Palette...");
                jbReplacePalette.addActionListener(e -> jbReplacePaletteActionPerformed(e));
                jPanel8.add(jbReplacePalette, "cell 0 0");

                //---- jbAddPalettes ----
                jbAddPalettes.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                jbAddPalettes.setText("Add Only Palettes...");
                jbAddPalettes.addActionListener(e -> jbAddPalettesActionPerformed(e));
                jPanel8.add(jbAddPalettes, "cell 0 1");
            }
            panel3.add(jPanel8, "cell 0 2");
        }
        contentPane.add(panel3, "cell 4 0");
        setSize(1110, 510);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar jMenuBar1;
    private JMenu jmFile;
    private JMenuItem jmiNewNsbtx;
    private JMenuItem jmiOpenNsbtx;
    private JMenuItem jmiSaveNsbtx;
    private JMenuItem jmiSaveNsbtxAs;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> jlTextureNames;
    private JPanel panel4;
    private JButton jbMoveTextureUp;
    private JLabel jLabel7;
    private JButton jbMoveTextureDown;
    private JPanel jPanel2;
    private JScrollPane jScrollPane2;
    private JList<String> jlPaletteNames;
    private JPanel panel5;
    private JLabel jLabel9;
    private JButton jbMovePaletteUp;
    private JButton jbMovePaletteDown;
    private JPanel panel1;
    private JPanel jPanel3;
    private NsbtxDisplay2 nsbtxDisplay;
    private JPanel jPanel5;
    private PaletteDisplay2 paletteDisplay;
    private JPanel panel2;
    private JPanel jPanel4;
    private JLabel jLabel1;
    private JComboBox<String> jcbColorTexture;
    private JLabel jLabel2;
    private JTextField jtfTextureName;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JTextField jtfTextureWidth;
    private JTextField jtfTextureHeight;
    private JButton jbExportTextureImg;
    private JCheckBox jcbTransparentColor;
    private JButton jbApplyTextureName;
    private JPanel jPanel6;
    private JLabel jLabel6;
    private JTextField jtfPaletteName;
    private JButton jbApplyPaletteName;
    private JPanel jPanel10;
    private JButton jbImportTexAndPalsNsbtx;
    private JButton jbImportTexNsbtx;
    private JButton jbImportPalNsbtx;
    private JPanel panel3;
    private JPanel jPanel9;
    private JButton jbAddTextureAndPalette;
    private JButton jbReplaceTextureAndPalette;
    private JButton RemoveTextureAndPalette;
    private JPanel jPanel7;
    private JButton jbReplaceTexture;
    private JButton jbAddTextures;
    private JButton jbRemoveTexture;
    private JPanel jPanel8;
    private JButton jbRemovePalette;
    private JButton jbReplacePalette;
    private JButton jbAddPalettes;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
