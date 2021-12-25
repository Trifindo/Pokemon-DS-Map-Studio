package editor.tileseteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import utils.swing.SwingUtils;
import utils.swing.SwingUtils.MutableBoolean;

/**
 * @author Trifindo, JackHack96
 */
public class ExportTileDialog extends JDialog {

    private final MutableBoolean jtfScaleEnabled = new MutableBoolean(true);
    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;
    private float scale = 1.0f;
    private float minScale = 0.001f;
    private boolean flip = false;
    private boolean includeVertexColors = true;

    private static final Color redColor = new Color(255, 200, 200);
    private static final Color greenColor = new Color(200, 255, 200);
    private static final Color whiteColor = Color.white;

    public ExportTileDialog(Window owner, String title) {
        super(owner);
        initComponents();

        this.setTitle(title);

        SwingUtils.addListenerToJTextField(jtfScale, jtfScaleEnabled, redColor);

        jtfScaleEnabled.value = false;
        jtfScale.setText(String.valueOf(scale));
        jtfScaleEnabled.value = true;
    }

    private void jcbFlipActionPerformed(ActionEvent e) {
        flip = jcbFlip.isSelected();
    }

    private void jbApplyScaleActionPerformed(ActionEvent e) {
        float value;
        try {
            value = Float.parseFloat(jtfScale.getText());
            if (value < minScale) {
                value = minScale;
            }
        } catch (NumberFormatException ex) {
            value = scale;
        }
        scale = value;
        jtfScale.setText(String.valueOf(value));
        jtfScaleEnabled.value = false;
        jtfScale.setBackground(greenColor);
        jtfScaleEnabled.value = true;
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

    public float getScale() {
        return scale;
    }

    public boolean flip() {
        return flip;
    }

    public boolean includeVertexColors() {
        return includeVertexColors;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jtfScale = new JTextField();
        jcbFlip = new JCheckBox();
        jbApplyScale = new JButton();
        jcbVertexColors = new JCheckBox();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import Tile Settings");
        setModal(true);
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Settings"));

            //---- jLabel1 ----
            jLabel1.setText("Scale: ");

            //---- jtfScale ----
            jtfScale.setText(" ");

            //---- jcbFlip ----
            jcbFlip.setText("Flip YZ");
            jcbFlip.addActionListener(e -> jcbFlipActionPerformed(e));

            //---- jbApplyScale ----
            jbApplyScale.setText("Apply");
            jbApplyScale.addActionListener(e -> jbApplyScaleActionPerformed(e));

            //---- jcbVertexColors ----
            jcbVertexColors.setSelected(true);
            jcbVertexColors.setText("Include Vertex Colors");
            jcbVertexColors.addActionListener(e -> jcbVertexColorsActionPerformed(e));

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addComponent(jcbVertexColors)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jtfScale, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jbApplyScale))
                                            .addComponent(jcbFlip))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel1)
                                            .addComponent(jtfScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jbApplyScale))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jcbFlip)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jcbVertexColors)
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
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JTextField jtfScale;
    private JCheckBox jcbFlip;
    private JButton jbApplyScale;
    private JCheckBox jcbVertexColors;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
