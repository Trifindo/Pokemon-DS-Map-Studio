package editor.nsbtx2;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.buildingeditor2.tileset.*;
import editor.nsbtx2.Nsbtx2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.DefaultListModel;

import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class NsbtxPanel extends JPanel {

    private Nsbtx2 nsbtx;

    private boolean textureListEnabled = true;
    private boolean paletteListEnabled = true;
    private Utils.MutableBoolean jtfTextureActive = new Utils.MutableBoolean(true);
    private Utils.MutableBoolean jtfPaletteActive = new Utils.MutableBoolean(true);

    private static final Color editingColor = new Color(255, 200, 200);
    private static final Color rightColor = new Color(200, 255, 200);

    public NsbtxPanel() {
        initComponents();
    }

    private void jlTextureNamesValueChanged(ListSelectionEvent e) {
        if (nsbtx != null && textureListEnabled) {
            updateViewPaletteNamesUsingTexNames();
            updateView();
        }
    }

    private void jlPaletteNamesValueChanged(ListSelectionEvent e) {
        if (nsbtx != null && paletteListEnabled) {
            updateView();
        }
    }

    public void updateView() {
        if (nsbtx != null) {
            int texIndex = jlTextureNames.getSelectedIndex();
            int palIndex = jlPaletteNames.getSelectedIndex();

            try {
                BufferedImage img = nsbtx.getImage(texIndex, palIndex);
                textureDisplay.updateImage(img);
                textureDisplay.repaint();

                paletteDisplay.updatePalette(nsbtx.getTexture(texIndex), nsbtx.getPalette(palIndex));
                paletteDisplay.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    public void updateViewTextureNameList(int indexSelected) {
        textureListEnabled = false;
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < nsbtx.getTextures().size(); i++) {
            String name = nsbtx.getTexture(i).getName();
            demoList.addElement(name);
        }
        jlTextureNames.setModel(demoList);
        if (indexSelected > demoList.size() - 1) {
            indexSelected = demoList.size() - 1;
        } else if (indexSelected < 0) {
            indexSelected = 0;
        }
        jlTextureNames.setSelectedIndex(indexSelected);
        jlTextureNames.ensureIndexIsVisible(indexSelected);
        textureListEnabled = true;
    }

    public void updateViewPaletteNameList(int indexSelected) {
        paletteListEnabled = false;
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < nsbtx.getPalettes().size(); i++) {
            String name = nsbtx.getPalette(i).getName();
            demoList.addElement(name);
        }
        jlPaletteNames.setModel(demoList);
        if (indexSelected > demoList.size() - 1) {
            indexSelected = demoList.size() - 1;
        } else if (indexSelected < 0) {
            indexSelected = 0;
        }
        jlPaletteNames.setSelectedIndex(indexSelected);
        jlPaletteNames.ensureIndexIsVisible(indexSelected);
        paletteListEnabled = true;
    }

    private void updateViewPaletteNamesUsingTexNames() {
        int index = jlTextureNames.getSelectedIndex();
        int paletteIndex;
        String paletteName = nsbtx.getTexture(index).getName();
        if ((paletteIndex = nsbtx.indexOfPaletteName(paletteName)) != -1
                || (paletteIndex = nsbtx.indexOfPaletteName(paletteName + "_pl")) != -1) {
            jlPaletteNames.setSelectedIndex(paletteIndex);
            jlPaletteNames.ensureIndexIsVisible(jlPaletteNames.getSelectedIndex());
        }
    }

    public void setNsbtx(Nsbtx2 nsbtx) {
        this.nsbtx = nsbtx;
    }

    public Nsbtx2 getNsbtx() {
        return nsbtx;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jlTextureNames = new JList<>();
        jPanel2 = new JPanel();
        jScrollPane2 = new JScrollPane();
        jlPaletteNames = new JList<>();
        jPanel3 = new JPanel();
        textureDisplay = new TextureDisplay();
        jPanel4 = new JPanel();
        paletteDisplay = new PaletteDisplay();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{1.0, 1.0, 1.0E-4};

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Texture Names"));
            jPanel1.setLayout(new GridBagLayout());
            ((GridBagLayout) jPanel1.getLayout()).columnWidths = new int[]{0, 0};
            ((GridBagLayout) jPanel1.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) jPanel1.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
            ((GridBagLayout) jPanel1.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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
            jPanel1.add(jScrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Palette Names"));
            jPanel2.setLayout(new GridBagLayout());
            ((GridBagLayout) jPanel2.getLayout()).columnWidths = new int[]{0, 0};
            ((GridBagLayout) jPanel2.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) jPanel2.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
            ((GridBagLayout) jPanel2.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

            //======== jScrollPane2 ========
            {
                jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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
            jPanel2.add(jScrollPane2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(jPanel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Texture"));
            jPanel3.setLayout(new GridBagLayout());
            ((GridBagLayout) jPanel3.getLayout()).columnWidths = new int[]{0, 0};
            ((GridBagLayout) jPanel3.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) jPanel3.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
            ((GridBagLayout) jPanel3.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

            //======== textureDisplay ========
            {
                textureDisplay.setBorder(LineBorder.createBlackLineBorder());

                GroupLayout textureDisplayLayout = new GroupLayout(textureDisplay);
                textureDisplay.setLayout(textureDisplayLayout);
                textureDisplayLayout.setHorizontalGroup(
                        textureDisplayLayout.createParallelGroup()
                                .addGap(0, 158, Short.MAX_VALUE)
                );
                textureDisplayLayout.setVerticalGroup(
                        textureDisplayLayout.createParallelGroup()
                                .addGap(0, 158, Short.MAX_VALUE)
                );
            }
            jPanel3.add(textureDisplay, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(jPanel3, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== jPanel4 ========
        {
            jPanel4.setBorder(new TitledBorder("Palette"));
            jPanel4.setLayout(new GridBagLayout());
            ((GridBagLayout) jPanel4.getLayout()).columnWidths = new int[]{0, 0};
            ((GridBagLayout) jPanel4.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) jPanel4.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
            ((GridBagLayout) jPanel4.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

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
            jPanel4.add(paletteDisplay, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(jPanel4, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> jlTextureNames;
    private JPanel jPanel2;
    private JScrollPane jScrollPane2;
    private JList<String> jlPaletteNames;
    private JPanel jPanel3;
    private TextureDisplay textureDisplay;
    private JPanel jPanel4;
    private PaletteDisplay paletteDisplay;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
