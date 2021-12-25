package editor.keyboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Trifindo, JackHack96
 */
public class KeyboardInfoDialog extends JDialog {
    public KeyboardInfoDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jButton1ActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jList1 = new JList<>();
        jButton1 = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Keyboard and Mouse Shortcuts (Not finished)");
        setModal(true);
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Map Display Key Shortcuts"));

            //======== jScrollPane1 ========
            {

                //---- jList1 ----
                jList1.setModel(new AbstractListModel<String>() {
                    String[] values = {
                        "SPACE - Change perspective",
                        "C - Clear tiles",
                        "S - Use Smart Drawing",
                        "SHIFT - Invert Smart Drawing",
                        "H - View Height Map",
                        "G - View Grid",
                        "LEFT CLICK - Set Tile/Height selected",
                        "RIGHT CLICK - Get Tile selected",
                        "MOUSE WHEEL CLICK - Flood fill",
                        "Ctrl+Z - Undo",
                        "Crtl+Y - Redo"
                    };
                    @Override
                    public int getSize() { return values.length; }
                    @Override
                    public String getElementAt(int i) { return values[i]; }
                });
                jScrollPane1.setViewportView(jList1);
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //---- jButton1 ----
        jButton1.setText("OK");
        jButton1.addActionListener(e -> jButton1ActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(129, 129, 129)
                    .addComponent(jButton1, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .addGap(139, 139, 139))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jButton1)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> jList1;
    private JButton jButton1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
