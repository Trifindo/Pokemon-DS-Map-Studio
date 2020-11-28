package formats.nsbtx2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author Trifindo, JackHack96
 */
public class NsbtxColorFormatAddSelector extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;

    private int format;
    private boolean isTransparent;

    public NsbtxColorFormatAddSelector(Window owner) {
        super(owner);
        initComponents();
    }

    private void jbOkayActionPerformed(ActionEvent e) {
        format = Nsbtx2.jcbToFormatLookup[jcbColorTexture.getSelectedIndex()];
        isTransparent = jcbTransparentColor.isSelected();

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

    public int getFormat() {
        return format;
    }

    public boolean getIsTransparent() {
        return isTransparent;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jcbColorTexture = new JComboBox<>();
        jbOkay = new JButton();
        jbCancel = new JButton();
        jcbTransparentColor = new JCheckBox();
        jLabel1 = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose Color Format");
        setResizable(false);
        setModal(true);
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Texture Color Format"));

            //---- jcbColorTexture ----
            jcbColorTexture.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Palette 4",
                    "Palette 16",
                    "Palette 256",
                    "A3I5",
                    "A5I3"
            }));
            jcbColorTexture.setSelectedIndex(1);
            jcbColorTexture.setMinimumSize(new Dimension(120, 20));
            jcbColorTexture.setPreferredSize(new Dimension(120, 20));

            //---- jbOkay ----
            jbOkay.setText("OK");
            jbOkay.addActionListener(e -> jbOkayActionPerformed(e));

            //---- jbCancel ----
            jbCancel.setText("Cancel");
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));

            //---- jcbTransparentColor ----
            jcbTransparentColor.setSelected(true);
            jcbTransparentColor.setText("Transparent Color");

            //---- jLabel1 ----
            jLabel1.setText("Choose Format:");

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jbOkay, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jbCancel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel1)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jcbColorTexture, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jcbTransparentColor)
                                                    .addGap(0, 0, Short.MAX_VALUE)))
                                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jcbColorTexture, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jcbTransparentColor)
                                            .addComponent(jLabel1))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(jbOkay)
                                            .addComponent(jbCancel))
                                    .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
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
    private JComboBox<String> jcbColorTexture;
    private JButton jbOkay;
    private JButton jbCancel;
    private JCheckBox jcbTransparentColor;
    private JLabel jLabel1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
