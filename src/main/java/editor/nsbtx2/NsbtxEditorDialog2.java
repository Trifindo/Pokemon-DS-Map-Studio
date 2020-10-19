package editor.nsbtx2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.converter.ConverterErrorDialog;
import editor.handler.MapEditorHandler;
import editor.nsbtx2.exceptions.NsbtxTextureSizeException;

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

    private static final Color editingColor = new Color(255, 200, 200);
    private static final Color rightColor = new Color(200, 255, 200);

    public NsbtxEditorDialog2(Window owner) {
        super(owner);
        initComponents();

        Utils.addListenerToJTextFieldColor(jtfTextureName, jtfTextureActive, editingColor);
        Utils.addListenerToJTextFieldColor(jtfPaletteName, jtfPaletteActive, editingColor);
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
            replaceTextureAndPalette();
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
            jtfTextureName.setBackground(Color.white);
            jtfTextureActive.value = true;
        } else {
            jtfTextureActive.value = false;
            jtfTextureName.setText("");
            jtfTextureName.setBackground(Color.white);
            jtfTextureActive.value = true;
        }
    }

    private void updateViewPaletteName() {
        if (nsbtxHandler.getSelectedPalette() != null) {
            String name = nsbtxHandler.getSelectedPalette().getName();
            jtfPaletteActive.value = false;
            jtfPaletteName.setText(name);
            jtfPaletteName.setBackground(Color.white);
            jtfPaletteActive.value = true;
        } else {
            jtfPaletteActive.value = false;
            jtfPaletteName.setText("");
            jtfPaletteName.setBackground(Color.white);
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
        int returnVal = fc.showOpenDialog(this);
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
            int returnVal = fc.showOpenDialog(this);
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
                int returnVal = fc.showOpenDialog(this);
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
                int returnVal = fc.showOpenDialog(this);
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
            int returnVal = fc.showOpenDialog(this);
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
            int returnVal = fc.showOpenDialog(this);
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
                int returnVal = fc.showOpenDialog(this);
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
            int returnVal = fc.showOpenDialog(this);
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
        int returnVal = fc.showOpenDialog(this);
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
                int returnVal = fc.showOpenDialog(this);
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
        jbMoveTextureUp = new JButton();
        jLabel7 = new JLabel();
        jbMoveTextureDown = new JButton();
        jPanel2 = new JPanel();
        jScrollPane2 = new JScrollPane();
        jlPaletteNames = new JList<>();
        jLabel9 = new JLabel();
        jbMovePaletteUp = new JButton();
        jbMovePaletteDown = new JButton();
        jPanel3 = new JPanel();
        nsbtxDisplay = new NsbtxDisplay2();
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
        jPanel5 = new JPanel();
        paletteDisplay = new PaletteDisplay2();
        jPanel6 = new JPanel();
        jLabel6 = new JLabel();
        jtfPaletteName = new JTextField();
        jbApplyPaletteName = new JButton();
        jPanel7 = new JPanel();
        jbReplaceTexture = new JButton();
        jbAddTextures = new JButton();
        jbRemoveTexture = new JButton();
        jPanel8 = new JPanel();
        jbRemovePalette = new JButton();
        jbReplacePalette = new JButton();
        jbAddPalettes = new JButton();
        jPanel9 = new JPanel();
        jbAddTextureAndPalette = new JButton();
        jbReplaceTextureAndPalette = new JButton();
        RemoveTextureAndPalette = new JButton();
        jPanel10 = new JPanel();
        jbImportTexAndPalsNsbtx = new JButton();
        jbImportTexNsbtx = new JButton();
        jbImportPalNsbtx = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("NSBTX Editor");
        setModal(true);
        Container contentPane = getContentPane();

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

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane1.setPreferredSize(new Dimension(130, 130));

                //---- jlTextureNames ----
                jlTextureNames.setModel(new AbstractListModel<String>() {
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
                jlTextureNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlTextureNames.addListSelectionListener(e -> jlTextureNamesValueChanged(e));
                jScrollPane1.setViewportView(jlTextureNames);
            }

            //---- jbMoveTextureUp ----
            jbMoveTextureUp.setText("\u25b2");
            jbMoveTextureUp.addActionListener(e -> jbMoveTextureUpActionPerformed(e));

            //---- jLabel7 ----
            jLabel7.setText("Move:");

            //---- jbMoveTextureDown ----
            jbMoveTextureDown.setText("\u25bc");
            jbMoveTextureDown.addActionListener(e -> jbMoveTextureDownActionPerformed(e));

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                                    .addGap(0, 0, Short.MAX_VALUE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel7)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jbMoveTextureUp)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbMoveTextureDown)))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel7)
                                            .addComponent(jbMoveTextureUp)
                                            .addComponent(jbMoveTextureDown))
                                    .addContainerGap())
            );
        }

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Palette Names"));

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
                    public int getSize() {
                        return values.length;
                    }

                    @Override
                    public String getElementAt(int i) {
                        return values[i];
                    }
                });
                jlPaletteNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlPaletteNames.addListSelectionListener(e -> jlPaletteNamesValueChanged(e));
                jScrollPane2.setViewportView(jlPaletteNames);
            }

            //---- jLabel9 ----
            jLabel9.setText("Move:");

            //---- jbMovePaletteUp ----
            jbMovePaletteUp.setText("\u25b2");
            jbMovePaletteUp.addActionListener(e -> jbMovePaletteUpActionPerformed(e));

            //---- jbMovePaletteDown ----
            jbMovePaletteDown.setText("\u25bc");
            jbMovePaletteDown.addActionListener(e -> jbMovePaletteDownActionPerformed(e));

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel2Layout.createParallelGroup()
                                            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addComponent(jLabel9)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jbMovePaletteUp)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbMovePaletteDown)))
                                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel9)
                                            .addComponent(jbMovePaletteUp)
                                            .addComponent(jbMovePaletteDown))
                                    .addContainerGap())
            );
        }

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Texture Display"));

            //======== nsbtxDisplay ========
            {
                nsbtxDisplay.setBorder(new LineBorder(new Color(102, 102, 102)));

                GroupLayout nsbtxDisplayLayout = new GroupLayout(nsbtxDisplay);
                nsbtxDisplay.setLayout(nsbtxDisplayLayout);
                nsbtxDisplayLayout.setHorizontalGroup(
                        nsbtxDisplayLayout.createParallelGroup()
                                .addGap(0, 158, Short.MAX_VALUE)
                );
                nsbtxDisplayLayout.setVerticalGroup(
                        nsbtxDisplayLayout.createParallelGroup()
                                .addGap(0, 158, Short.MAX_VALUE)
                );
            }

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

        //======== jPanel4 ========
        {
            jPanel4.setBorder(new TitledBorder("Texture Properties"));

            //---- jLabel1 ----
            jLabel1.setText("Color format:");

            //---- jcbColorTexture ----
            jcbColorTexture.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Palette 4",
                    "Palette 16",
                    "Palette 256",
                    "A3I5",
                    "A5I3"
            }));
            jcbColorTexture.setEnabled(false);
            jcbColorTexture.setMinimumSize(new Dimension(120, 20));
            jcbColorTexture.setPreferredSize(new Dimension(120, 20));

            //---- jLabel2 ----
            jLabel2.setText("Texture Name: ");

            //---- jtfTextureName ----
            jtfTextureName.setText(" ");

            //---- jLabel3 ----
            jLabel3.setText("Texture Width: ");

            //---- jLabel4 ----
            jLabel4.setText("Texture Height: ");

            //---- jtfTextureWidth ----
            jtfTextureWidth.setText(" ");
            jtfTextureWidth.setEnabled(false);

            //---- jtfTextureHeight ----
            jtfTextureHeight.setText(" ");
            jtfTextureHeight.setEnabled(false);

            //---- jbExportTextureImg ----
            jbExportTextureImg.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
            jbExportTextureImg.setText("Export Texture Image...");
            jbExportTextureImg.addActionListener(e -> jbExportTextureImgActionPerformed(e));

            //---- jcbTransparentColor ----
            jcbTransparentColor.setText("Transparent color");
            jcbTransparentColor.setEnabled(false);

            //---- jbApplyTextureName ----
            jbApplyTextureName.setText("Apply");
            jbApplyTextureName.addActionListener(e -> jbApplyTextureNameActionPerformed(e));

            GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                    jPanel4Layout.createParallelGroup()
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel4Layout.createParallelGroup()
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                    .addComponent(jcbTransparentColor)
                                                    .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(jbExportTextureImg, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                    .addGroup(jPanel4Layout.createParallelGroup()
                                                            .addComponent(jLabel1)
                                                            .addComponent(jLabel2))
                                                    .addGap(18, 18, 18)
                                                    .addGroup(jPanel4Layout.createParallelGroup()
                                                            .addComponent(jcbColorTexture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                                    .addComponent(jtfTextureName, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                    .addComponent(jbApplyTextureName))))
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                    .addGroup(jPanel4Layout.createParallelGroup()
                                                            .addComponent(jLabel3)
                                                            .addComponent(jLabel4))
                                                    .addGap(14, 14, 14)
                                                    .addGroup(jPanel4Layout.createParallelGroup()
                                                            .addComponent(jtfTextureHeight)
                                                            .addComponent(jtfTextureWidth))))
                                    .addContainerGap())
            );
            jPanel4Layout.setVerticalGroup(
                    jPanel4Layout.createParallelGroup()
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel1)
                                            .addComponent(jcbColorTexture, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel2)
                                            .addComponent(jtfTextureName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbApplyTextureName))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel3)
                                            .addComponent(jtfTextureWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel4)
                                            .addComponent(jtfTextureHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jcbTransparentColor)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jbExportTextureImg)
                                    .addContainerGap())
            );
        }

        //======== jPanel5 ========
        {
            jPanel5.setBorder(new TitledBorder("Palette Display"));

            //======== paletteDisplay ========
            {

                GroupLayout paletteDisplayLayout = new GroupLayout(paletteDisplay);
                paletteDisplay.setLayout(paletteDisplayLayout);
                paletteDisplayLayout.setHorizontalGroup(
                        paletteDisplayLayout.createParallelGroup()
                                .addGap(0, 160, Short.MAX_VALUE)
                );
                paletteDisplayLayout.setVerticalGroup(
                        paletteDisplayLayout.createParallelGroup()
                                .addGap(0, 160, Short.MAX_VALUE)
                );
            }

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

        //======== jPanel6 ========
        {
            jPanel6.setBorder(new TitledBorder("Palette Properties"));

            //---- jLabel6 ----
            jLabel6.setText("Palette Name:");

            //---- jtfPaletteName ----
            jtfPaletteName.setText(" ");

            //---- jbApplyPaletteName ----
            jbApplyPaletteName.setText("Apply");
            jbApplyPaletteName.addActionListener(e -> jbApplyPaletteNameActionPerformed(e));

            GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
            jPanel6.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(
                    jPanel6Layout.createParallelGroup()
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel6)
                                    .addGap(18, 18, 18)
                                    .addComponent(jtfPaletteName)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbApplyPaletteName)
                                    .addContainerGap())
            );
            jPanel6Layout.setVerticalGroup(
                    jPanel6Layout.createParallelGroup()
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel6)
                                            .addComponent(jtfPaletteName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbApplyPaletteName))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel7 ========
        {
            jPanel7.setBorder(new TitledBorder("Texture Editor"));

            //---- jbReplaceTexture ----
            jbReplaceTexture.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
            jbReplaceTexture.setText("Replace Only Texture...");
            jbReplaceTexture.addActionListener(e -> jbReplaceTextureActionPerformed(e));

            //---- jbAddTextures ----
            jbAddTextures.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
            jbAddTextures.setText("Add Only Textures...");
            jbAddTextures.addActionListener(e -> jbAddTexturesActionPerformed(e));

            //---- jbRemoveTexture ----
            jbRemoveTexture.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
            jbRemoveTexture.setText("Remove Only Texture");
            jbRemoveTexture.addActionListener(e -> jbRemoveTextureActionPerformed(e));

            GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
            jPanel7.setLayout(jPanel7Layout);
            jPanel7Layout.setHorizontalGroup(
                    jPanel7Layout.createParallelGroup()
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel7Layout.createParallelGroup()
                                            .addComponent(jbReplaceTexture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbAddTextures, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbRemoveTexture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            jPanel7Layout.setVerticalGroup(
                    jPanel7Layout.createParallelGroup()
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jbReplaceTexture)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbAddTextures)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbRemoveTexture)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel8 ========
        {
            jPanel8.setBorder(new TitledBorder("Palette Editor"));

            //---- jbRemovePalette ----
            jbRemovePalette.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
            jbRemovePalette.setText("Remove Only Palette");
            jbRemovePalette.addActionListener(e -> jbRemovePaletteActionPerformed(e));

            //---- jbReplacePalette ----
            jbReplacePalette.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
            jbReplacePalette.setText("Replace Only Palette...");
            jbReplacePalette.addActionListener(e -> jbReplacePaletteActionPerformed(e));

            //---- jbAddPalettes ----
            jbAddPalettes.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
            jbAddPalettes.setText("Add Only Palettes...");
            jbAddPalettes.addActionListener(e -> jbAddPalettesActionPerformed(e));

            GroupLayout jPanel8Layout = new GroupLayout(jPanel8);
            jPanel8.setLayout(jPanel8Layout);
            jPanel8Layout.setHorizontalGroup(
                    jPanel8Layout.createParallelGroup()
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel8Layout.createParallelGroup()
                                            .addComponent(jbReplacePalette, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbRemovePalette, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbAddPalettes, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            jPanel8Layout.setVerticalGroup(
                    jPanel8Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jbReplacePalette)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbAddPalettes)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbRemovePalette)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel9 ========
        {
            jPanel9.setBorder(new TitledBorder("Texture and Palette Editor"));

            //---- jbAddTextureAndPalette ----
            jbAddTextureAndPalette.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
            jbAddTextureAndPalette.setText("Add Textures and Palettes...");
            jbAddTextureAndPalette.addActionListener(e -> jbAddTextureAndPaletteActionPerformed(e));

            //---- jbReplaceTextureAndPalette ----
            jbReplaceTextureAndPalette.setIcon(new ImageIcon(getClass().getResource("/icons/ReplaceIcon.png")));
            jbReplaceTextureAndPalette.setText("Replace Texture and Palette...");
            jbReplaceTextureAndPalette.addActionListener(e -> jbReplaceTextureAndPaletteActionPerformed(e));

            //---- RemoveTextureAndPalette ----
            RemoveTextureAndPalette.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
            RemoveTextureAndPalette.setText("Remove Texture and Palette");
            RemoveTextureAndPalette.addActionListener(e -> RemoveTextureAndPaletteActionPerformed(e));

            GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
            jPanel9.setLayout(jPanel9Layout);
            jPanel9Layout.setHorizontalGroup(
                    jPanel9Layout.createParallelGroup()
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel9Layout.createParallelGroup()
                                            .addComponent(jbReplaceTextureAndPalette, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbAddTextureAndPalette, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(RemoveTextureAndPalette, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            jPanel9Layout.setVerticalGroup(
                    jPanel9Layout.createParallelGroup()
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jbReplaceTextureAndPalette)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbAddTextureAndPalette)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(RemoveTextureAndPalette)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel10 ========
        {
            jPanel10.setBorder(new TitledBorder("NSBTX Tools"));

            //---- jbImportTexAndPalsNsbtx ----
            jbImportTexAndPalsNsbtx.setText("Import Textures and Palettes from NSBTX...");
            jbImportTexAndPalsNsbtx.addActionListener(e -> jbImportTexAndPalsNsbtxActionPerformed(e));

            //---- jbImportTexNsbtx ----
            jbImportTexNsbtx.setText("Import Only Textures from NSBTX...");
            jbImportTexNsbtx.setEnabled(false);
            jbImportTexNsbtx.addActionListener(e -> jbImportTexNsbtxActionPerformed(e));

            //---- jbImportPalNsbtx ----
            jbImportPalNsbtx.setText("Import Only Palettes from NSBTX...");
            jbImportPalNsbtx.setEnabled(false);
            jbImportPalNsbtx.addActionListener(e -> jbImportPalNsbtxActionPerformed(e));

            GroupLayout jPanel10Layout = new GroupLayout(jPanel10);
            jPanel10.setLayout(jPanel10Layout);
            jPanel10Layout.setHorizontalGroup(
                    jPanel10Layout.createParallelGroup()
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel10Layout.createParallelGroup()
                                            .addComponent(jbImportTexAndPalsNsbtx, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbImportTexNsbtx, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jbImportPalNsbtx, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            jPanel10Layout.setVerticalGroup(
                    jPanel10Layout.createParallelGroup()
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jbImportTexAndPalsNsbtx)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbImportTexNsbtx)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jbImportPalNsbtx)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(jPanel9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel8, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(jPanel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                                                .addComponent(jPanel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(9, 9, 9)
                                                                                .addComponent(jPanel10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                                .addComponent(jPanel9, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jPanel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jPanel8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        pack();
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
    private JButton jbMoveTextureUp;
    private JLabel jLabel7;
    private JButton jbMoveTextureDown;
    private JPanel jPanel2;
    private JScrollPane jScrollPane2;
    private JList<String> jlPaletteNames;
    private JLabel jLabel9;
    private JButton jbMovePaletteUp;
    private JButton jbMovePaletteDown;
    private JPanel jPanel3;
    private NsbtxDisplay2 nsbtxDisplay;
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
    private JPanel jPanel5;
    private PaletteDisplay2 paletteDisplay;
    private JPanel jPanel6;
    private JLabel jLabel6;
    private JTextField jtfPaletteName;
    private JButton jbApplyPaletteName;
    private JPanel jPanel7;
    private JButton jbReplaceTexture;
    private JButton jbAddTextures;
    private JButton jbRemoveTexture;
    private JPanel jPanel8;
    private JButton jbRemovePalette;
    private JButton jbReplacePalette;
    private JButton jbAddPalettes;
    private JPanel jPanel9;
    private JButton jbAddTextureAndPalette;
    private JButton jbReplaceTextureAndPalette;
    private JButton RemoveTextureAndPalette;
    private JPanel jPanel10;
    private JButton jbImportTexAndPalsNsbtx;
    private JButton jbImportTexNsbtx;
    private JButton jbImportPalNsbtx;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
