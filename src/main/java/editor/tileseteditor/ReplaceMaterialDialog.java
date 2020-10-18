package editor.tileseteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

import editor.handler.MapEditorHandler;

import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * @author Trifindo, JackHack96
 */
public class ReplaceMaterialDialog extends JDialog {

    private MapEditorHandler handler;
    private TilesetEditorHandler tileHandler;
    private TilesetEditorDialog tsetDialog;

    private boolean jcbNewMaterialEnabled = true;

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;

    private int indexSelected;

    private ArrayList<ImageIcon> materialIcons;

    public ReplaceMaterialDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jcbNewMaterialActionPerformed(ActionEvent e) {
        indexSelected = jcbNewMaterial.getSelectedIndex();
        textureDisplayNew.setImageIndex(indexSelected);
        textureDisplayNew.repaint();
    }

    private void jbReplaceActionPerformed(ActionEvent e) {
        returnValue = APPROVE_OPTION;
        dispose();
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCEL_OPTION;
        dispose();
    }

    public void init(MapEditorHandler handler, TilesetEditorHandler tileHandler, TilesetEditorDialog tsetDialog) {
        this.handler = handler;
        this.tileHandler = tileHandler;
        this.tsetDialog = tsetDialog;

        textureDisplayToDelete.init(tileHandler);
        textureDisplayNew.init(tileHandler);
        initJComboBox();

        jtfMaterialName.setText(tileHandler.getMaterialSelected().getImageName());
        textureDisplayToDelete.setImageIndex(tileHandler.getMaterialIndexSelected());
        textureDisplayNew.setImageIndex(0);

    }

    private void initJComboBox() {
        Object[] names = new String[handler.getTileset().getMaterials().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = handler.getTileset().getMaterials().get(i).getImageName();
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(names);
        jcbNewMaterialEnabled = false;
        jcbNewMaterial.setModel(model);
        jcbNewMaterialEnabled = true;

        materialIcons = new ArrayList<>(handler.getTileset().getMaterials().size());
        for (int i = 0; i < handler.getTileset().getMaterials().size(); i++) {
            materialIcons.add(new ImageIcon(new ImageIcon(handler.getTileset().getTextureImg(i)).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
        }

        jcbNewMaterial.setRenderer(new DefaultListCellRenderer() {
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

    public int getReturnValue() {
        return returnValue;
    }

    public int getIndexSelected() {
        return indexSelected;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        jcbNewMaterial = new JComboBox<>();
        textureDisplayNew = new TextureDisplayConfigurable();
        jbReplace = new JButton();
        jbCancel = new JButton();
        jPanel3 = new JPanel();
        jtfMaterialName = new JTextField();
        textureDisplayToDelete = new TextureDisplayConfigurable();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Replace Material");
        setModal(true);
        var contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Replace Material"));

            //======== jPanel2 ========
            {
                jPanel2.setBorder(new TitledBorder("Select the Material to use instead"));
                jPanel2.setPreferredSize(new Dimension(280, 65));

                //---- jcbNewMaterial ----
                jcbNewMaterial.setModel(new DefaultComboBoxModel<>(new String[]{

                }));
                jcbNewMaterial.addActionListener(e -> jcbNewMaterialActionPerformed(e));

                //======== textureDisplayNew ========
                {
                    textureDisplayNew.setBorder(LineBorder.createBlackLineBorder());

                    GroupLayout textureDisplayNewLayout = new GroupLayout(textureDisplayNew);
                    textureDisplayNew.setLayout(textureDisplayNewLayout);
                    textureDisplayNewLayout.setHorizontalGroup(
                            textureDisplayNewLayout.createParallelGroup()
                                    .addGap(0, 126, Short.MAX_VALUE)
                    );
                    textureDisplayNewLayout.setVerticalGroup(
                            textureDisplayNewLayout.createParallelGroup()
                                    .addGap(0, 126, Short.MAX_VALUE)
                    );
                }

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                .addComponent(jcbNewMaterial)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(textureDisplayNew, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(0, 130, Short.MAX_VALUE)))
                                        .addContainerGap())
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jcbNewMaterial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(textureDisplayNew, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }

            //---- jbReplace ----
            jbReplace.setText("Replace");
            jbReplace.addActionListener(e -> jbReplaceActionPerformed(e));

            //---- jbCancel ----
            jbCancel.setText("Cancel");
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));

            //======== jPanel3 ========
            {
                jPanel3.setBorder(new TitledBorder(null, "Material that will be deleted", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, new Color(204, 0, 0)));
                jPanel3.setPreferredSize(new Dimension(280, 209));

                //---- jtfMaterialName ----
                jtfMaterialName.setEditable(false);
                jtfMaterialName.setText(" ");

                //======== textureDisplayToDelete ========
                {
                    textureDisplayToDelete.setBorder(LineBorder.createBlackLineBorder());

                    GroupLayout textureDisplayToDeleteLayout = new GroupLayout(textureDisplayToDelete);
                    textureDisplayToDelete.setLayout(textureDisplayToDeleteLayout);
                    textureDisplayToDeleteLayout.setHorizontalGroup(
                            textureDisplayToDeleteLayout.createParallelGroup()
                                    .addGap(0, 126, Short.MAX_VALUE)
                    );
                    textureDisplayToDeleteLayout.setVerticalGroup(
                            textureDisplayToDeleteLayout.createParallelGroup()
                                    .addGap(0, 126, Short.MAX_VALUE)
                    );
                }

                GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                .addComponent(jtfMaterialName)
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(textureDisplayToDelete, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(0, 130, Short.MAX_VALUE)))
                                        .addContainerGap())
                );
                jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jtfMaterialName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(textureDisplayToDelete, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jbReplace, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jbCancel, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)))
                                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                                            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jbReplace)
                                            .addComponent(jbCancel))
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
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JComboBox<String> jcbNewMaterial;
    private TextureDisplayConfigurable textureDisplayNew;
    private JButton jbReplace;
    private JButton jbCancel;
    private JPanel jPanel3;
    private JTextField jtfMaterialName;
    private TextureDisplayConfigurable textureDisplayToDelete;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
