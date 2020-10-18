package editor.converter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

import renderer.*;

/**
 * @author Trifindo, JackHack96
 */
public class ExportNsbmdResultDialog extends JDialog {
    public ExportNsbmdResultDialog(Window owner) {
        super(owner);
        initComponents();

        getRootPane().setDefaultButton(jButton1);
        jButton1.requestFocus();

        nitroDisplayGL1.getObjectsGL().add(new ObjectGL());
    }

    private void jButton1ActionPerformed(ActionEvent e) {
        dispose();
    }

    public void init(byte[] nsbmdData) {
        nitroDisplayGL1.getObjectGL(0).setNsbmdData(nsbmdData);
        nitroDisplayGL1.requestUpdate();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel2 = new JPanel();
        jButton1 = new JButton();
        jPanel1 = new JPanel();
        nitroDisplayGL1 = new NitroDisplayGL();
        jLabel1 = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("NSBMD Successfully Saved");
        setModal(true);
        var contentPane = getContentPane();

        //======== jPanel2 ========
        {
            jPanel2.setLayout(new GridBagLayout());

            //---- jButton1 ----
            jButton1.setText("OK");
            jButton1.addActionListener(e -> jButton1ActionPerformed(e));
            jPanel2.add(jButton1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
        }

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Resulting NSBMD Display"));

            //======== nitroDisplayGL1 ========
            {
                nitroDisplayGL1.setBorder(new LineBorder(new Color(102, 102, 102)));

                GroupLayout nitroDisplayGL1Layout = new GroupLayout(nitroDisplayGL1);
                nitroDisplayGL1.setLayout(nitroDisplayGL1Layout);
                nitroDisplayGL1Layout.setHorizontalGroup(
                        nitroDisplayGL1Layout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                );
                nitroDisplayGL1Layout.setVerticalGroup(
                        nitroDisplayGL1Layout.createParallelGroup()
                                .addGap(0, 222, Short.MAX_VALUE)
                );
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(nitroDisplayGL1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(nitroDisplayGL1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addContainerGap())
            );
        }

        //---- jLabel1 ----
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("NSBMD succesfully exported.");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel2;
    private JButton jButton1;
    private JPanel jPanel1;
    private NitroDisplayGL nitroDisplayGL1;
    private JLabel jLabel1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
