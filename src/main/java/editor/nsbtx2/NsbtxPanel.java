package editor.nsbtx2;

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

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Texture Names"));

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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
                jlTextureNames.addListSelectionListener(e -> jlTextureNamesValueChanged(e));
                jScrollPane1.setViewportView(jlTextureNames);
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)
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

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                        .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
            );
        }

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Texture"));

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

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(textureDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(textureDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel4 ========
        {
            jPanel4.setBorder(new TitledBorder("Palette"));

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

            GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(paletteDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(paletteDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
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
