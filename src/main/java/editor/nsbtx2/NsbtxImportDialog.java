package editor.nsbtx2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * @author Trifindo, JackHack96
 */
public class NsbtxImportDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;

    private Nsbtx2 nsbtx;
    private Nsbtx2 nxbtxToImport;

    public NsbtxImportDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jbAddTextureActionPerformed(ActionEvent e) {
        if (jlTexturesInNsbtx.getModel().getSize() > 0) {
            int index = jlTexturesInNsbtx.getSelectedIndex();
            nxbtxToImport.addTexture(nsbtx.getTexture(index));
            nsbtx.getTextures().remove(index);
            updateViewTextures(index);
        }
    }

    private void jbRemoveTextureActionPerformed(ActionEvent e) {
        if (jlTexturesToImport.getModel().getSize() > 0) {
            int index = jlTexturesToImport.getSelectedIndex();
            nsbtx.addTexture(nxbtxToImport.getTexture(index));
            nxbtxToImport.getTextures().remove(index);
            updateViewTextures(index);
        }
    }

    private void jbAddAllTexturesActionPerformed(ActionEvent e) {
        if (jlTexturesInNsbtx.getModel().getSize() > 0) {
            nxbtxToImport.addTextures(nsbtx.getTextures());
            nsbtx.removeAllTextures();
            updateViewTextures(0);
        }
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCEL_OPTION;
        dispose();
    }

    private void jbAddPaletteActionPerformed(ActionEvent e) {
        if (jlPalettesInNsbtx.getModel().getSize() > 0) {
            int index = jlPalettesInNsbtx.getSelectedIndex();
            nxbtxToImport.addPalette(nsbtx.getPalette(index));
            nsbtx.getPalettes().remove(index);
            updateViewPalettes(index);
        }
    }

    private void jbAddAllPalettesActionPerformed(ActionEvent e) {
        if (jlPalettesInNsbtx.getModel().getSize() > 0) {
            nxbtxToImport.addPalettes(nsbtx.getPalettes());
            nsbtx.removeAllPalettes();
            updateViewPalettes(0);
        }
    }

    private void RemovePaletteActionPerformed(ActionEvent e) {
        if (jlPalettesToImport.getModel().getSize() > 0) {
            int index = jlPalettesToImport.getSelectedIndex();
            nsbtx.addPalette(nxbtxToImport.getPalette(index));
            nxbtxToImport.getPalettes().remove(index);
            updateViewPalettes(index);
        }
    }

    private void jbFinishActionPerformed(ActionEvent e) {
        returnValue = APPROVE_OPTION;
        dispose();
    }

    public void init(Nsbtx2 nsbtx) {
        this.nsbtx = nsbtx;

        nxbtxToImport = new Nsbtx2();

        updateViewTextures(0);
        updateViewPalettes(0);

    }

    private void updateViewTextures(int indexSelected) {
        updateViewTextureNames(nsbtx, jlTexturesInNsbtx, indexSelected);
        updateViewTextureNames(nxbtxToImport, jlTexturesToImport, indexSelected);
    }

    private void updateViewPalettes(int indexSelected) {
        updateViewPaletteNames(nxbtxToImport, jlPalettesToImport, indexSelected);
        updateViewPaletteNames(nsbtx, jlPalettesInNsbtx, indexSelected);
    }

    private void updateViewTextureNames(Nsbtx2 nsbtx, JList list, int indexSelected) {
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < nsbtx.getTextures().size(); i++) {
            String name = nsbtx.getTexture(i).getName();
            demoList.addElement(name);
        }
        list.setModel(demoList);
        if (indexSelected > demoList.size() - 1) {
            indexSelected = demoList.size() - 1;
        } else if (indexSelected < 0) {
            indexSelected = 0;
        }
        list.setSelectedIndex(indexSelected);
    }

    private void updateViewPaletteNames(Nsbtx2 nsbtx, JList list, int indexSelected) {
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < nsbtx.getPalettes().size(); i++) {
            String name = nsbtx.getPalette(i).getName();
            demoList.addElement(name);
        }
        list.setModel(demoList);
        if (indexSelected > demoList.size() - 1) {
            indexSelected = demoList.size() - 1;
        } else if (indexSelected < 0) {
            indexSelected = 0;
        }
        list.setSelectedIndex(indexSelected);
    }

    public Nsbtx2 getNsbtx() {
        return nxbtxToImport;
    }

    public int getReturnValue() {
        return returnValue;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jlTexturesInNsbtx = new JList<>();
        jLabel1 = new JLabel();
        jbAddTexture = new JButton();
        jbRemoveTexture = new JButton();
        jScrollPane2 = new JScrollPane();
        jlTexturesToImport = new JList<>();
        jLabel2 = new JLabel();
        jbAddAllTextures = new JButton();
        jbCancel = new JButton();
        jPanel2 = new JPanel();
        jScrollPane3 = new JScrollPane();
        jlPalettesInNsbtx = new JList<>();
        jLabel3 = new JLabel();
        jbAddPalette = new JButton();
        jScrollPane4 = new JScrollPane();
        jlPalettesToImport = new JList<>();
        jLabel4 = new JLabel();
        jbAddAllPalettes = new JButton();
        RemovePalette = new JButton();
        jbFinish = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select the Textures and Palettes to import from the NSBTX");
        setResizable(false);
        setModal(true);
        var contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Texture List"));

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane1.setPreferredSize(new Dimension(130, 130));

                //---- jlTexturesInNsbtx ----
                jlTexturesInNsbtx.setModel(new AbstractListModel<String>() {
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
                jlTexturesInNsbtx.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jScrollPane1.setViewportView(jlTexturesInNsbtx);
            }

            //---- jLabel1 ----
            jLabel1.setText("Textures in NSBTX:");

            //---- jbAddTexture ----
            jbAddTexture.setText("Add >>");
            jbAddTexture.addActionListener(e -> jbAddTextureActionPerformed(e));

            //---- jbRemoveTexture ----
            jbRemoveTexture.setText("Remove <<");
            jbRemoveTexture.addActionListener(e -> jbRemoveTextureActionPerformed(e));

            //======== jScrollPane2 ========
            {
                jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane2.setPreferredSize(new Dimension(130, 130));

                //---- jlTexturesToImport ----
                jlTexturesToImport.setModel(new AbstractListModel<String>() {
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
                jScrollPane2.setViewportView(jlTexturesToImport);
            }

            //---- jLabel2 ----
            jLabel2.setText("Textures to import:");

            //---- jbAddAllTextures ----
            jbAddAllTextures.setText("Add All >>");
            jbAddAllTextures.addActionListener(e -> jbAddAllTexturesActionPerformed(e));

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(jbRemoveTexture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jbAddTexture, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jbAddAllTextures, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                            .addComponent(jLabel1))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addComponent(jLabel2)
                                            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jbAddTexture)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbAddAllTextures)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(jbRemoveTexture)
                                                    .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
        }

        //---- jbCancel ----
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(e -> jbCancelActionPerformed(e));

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Palette List"));

            //======== jScrollPane3 ========
            {
                jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane3.setPreferredSize(new Dimension(130, 130));

                //---- jlPalettesInNsbtx ----
                jlPalettesInNsbtx.setModel(new AbstractListModel<String>() {
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
                jlPalettesInNsbtx.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jScrollPane3.setViewportView(jlPalettesInNsbtx);
            }

            //---- jLabel3 ----
            jLabel3.setText("Palettes in NSBTX:");

            //---- jbAddPalette ----
            jbAddPalette.setText("Add >>");
            jbAddPalette.addActionListener(e -> jbAddPaletteActionPerformed(e));

            //======== jScrollPane4 ========
            {
                jScrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane4.setPreferredSize(new Dimension(130, 130));

                //---- jlPalettesToImport ----
                jlPalettesToImport.setModel(new AbstractListModel<String>() {
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
                jScrollPane4.setViewportView(jlPalettesToImport);
            }

            //---- jLabel4 ----
            jLabel4.setText("Palettes to import:");

            //---- jbAddAllPalettes ----
            jbAddAllPalettes.setText("Add All >>");
            jbAddAllPalettes.addActionListener(e -> jbAddAllPalettesActionPerformed(e));

            //---- RemovePalette ----
            RemovePalette.setText("Remove <<");
            RemovePalette.addActionListener(e -> RemovePaletteActionPerformed(e));

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel2Layout.createParallelGroup()
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jbAddPalette, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                                            .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                    .addComponent(RemovePalette, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                    .addComponent(jbAddAllPalettes, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE))))
                                            .addComponent(jLabel3))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup()
                                            .addComponent(jLabel4)
                                            .addComponent(jScrollPane4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup()
                                            .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addComponent(jbAddPalette)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbAddAllPalettes)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(RemovePalette)
                                                    .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
        }

        //---- jbFinish ----
        jbFinish.setText("Finish");
        jbFinish.addActionListener(e -> jbFinishActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbFinish, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbCancel)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jbCancel)
                                        .addComponent(jbFinish))
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> jlTexturesInNsbtx;
    private JLabel jLabel1;
    private JButton jbAddTexture;
    private JButton jbRemoveTexture;
    private JScrollPane jScrollPane2;
    private JList<String> jlTexturesToImport;
    private JLabel jLabel2;
    private JButton jbAddAllTextures;
    private JButton jbCancel;
    private JPanel jPanel2;
    private JScrollPane jScrollPane3;
    private JList<String> jlPalettesInNsbtx;
    private JLabel jLabel3;
    private JButton jbAddPalette;
    private JScrollPane jScrollPane4;
    private JList<String> jlPalettesToImport;
    private JLabel jLabel4;
    private JButton jbAddAllPalettes;
    private JButton RemovePalette;
    private JButton jbFinish;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
