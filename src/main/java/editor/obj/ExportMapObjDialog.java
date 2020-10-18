package editor.obj;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

import editor.tileseteditor.*;

import java.awt.Color;

import utils.swing.SwingUtils;
import utils.swing.SwingUtils.MutableBoolean;

/**
 * @author Trifindo, JackHack96
 */
public class ExportMapObjDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;
    private boolean includeVertexColors = true;

    public ExportMapObjDialog(Window owner, String title) {
        super(owner);
        initComponents();

        this.setTitle(title);

        getRootPane().setDefaultButton(jbAccept);
        jbAccept.requestFocus();
    }

    private void jcbVertexColorsActionPerformed(ActionEvent e) {
        includeVertexColors = jcbVertexColors.isSelected();
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        returnValue = APPROVE_OPTION;
        dispose();
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCEL_OPTION;
        dispose();
    }

    public int getReturnValue() {
        return returnValue;
    }

    public boolean includeVertexColors() {
        return includeVertexColors;
    }

    public boolean exportAllMapsSeparately() {
        return jrbExportAllMapsSeparately.isSelected();
    }

    public boolean exportAllMapsJoined() {
        return jrbExportAllMapsJoined.isSelected();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jcbVertexColors = new JCheckBox();
        jrbExportAllMapsSeparately = new JRadioButton();
        jrbExportCurrentMap = new JRadioButton();
        jrbExportAllMapsJoined = new JRadioButton();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import Tile Settings");
        setResizable(false);
        setModal(true);
        var contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Settings"));

            //---- jcbVertexColors ----
            jcbVertexColors.setSelected(true);
            jcbVertexColors.setText("Include Vertex Colors");
            jcbVertexColors.addActionListener(e -> jcbVertexColorsActionPerformed(e));

            //---- jrbExportAllMapsSeparately ----
            jrbExportAllMapsSeparately.setSelected(true);
            jrbExportAllMapsSeparately.setText("Export All Maps Separately");

            //---- jrbExportCurrentMap ----
            jrbExportCurrentMap.setText("Export Only Current Map");

            //---- jrbExportAllMapsJoined ----
            jrbExportAllMapsJoined.setText("Export All Maps Joined");

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(jcbVertexColors)
                            .addComponent(jrbExportAllMapsSeparately)
                            .addComponent(jrbExportCurrentMap)
                            .addComponent(jrbExportAllMapsJoined))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jcbVertexColors)
                        .addGap(18, 18, 18)
                        .addComponent(jrbExportAllMapsSeparately, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jrbExportAllMapsJoined)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jrbExportCurrentMap)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //---- jbAccept ----
        jbAccept.setText("OK");
        jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));

        //---- jbCancel ----
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(e -> jbCancelActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jbAccept, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
                            .addComponent(jbCancel, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jbAccept)
                        .addComponent(jbCancel))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());

        //---- bgExportMap ----
        var bgExportMap = new ButtonGroup();
        bgExportMap.add(jrbExportAllMapsSeparately);
        bgExportMap.add(jrbExportCurrentMap);
        bgExportMap.add(jrbExportAllMapsJoined);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JCheckBox jcbVertexColors;
    private JRadioButton jrbExportAllMapsSeparately;
    private JRadioButton jrbExportCurrentMap;
    private JRadioButton jrbExportAllMapsJoined;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
