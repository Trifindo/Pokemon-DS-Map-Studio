package editor.keyboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

/**
 * @author Trifindo, JackHack96
 */
public class KeyboardInfoDialog2 extends JDialog {
    public KeyboardInfoDialog2(Window owner) {
        super(owner);
        initComponents();

        getRootPane().setDefaultButton(jbAccept);
        jbAccept.requestFocus();
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jTabbedPane1 = new JTabbedPane();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jList1 = new JList<>();
        jPanel5 = new JPanel();
        jScrollPane4 = new JScrollPane();
        jList4 = new JList<>();
        jPanel3 = new JPanel();
        jScrollPane2 = new JScrollPane();
        jList2 = new JList<>();
        jPanel4 = new JPanel();
        jScrollPane3 = new JScrollPane();
        jList3 = new JList<>();
        jPanel1 = new JPanel();
        jbAccept = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Keyboard Shortcuts");
        setModal(true);
        Container contentPane = getContentPane();

        //======== jTabbedPane1 ========
        {

            //======== jPanel2 ========
            {

                //======== jScrollPane1 ========
                {

                    //---- jList1 ----
                    jList1.setModel(new AbstractListModel<String>() {
                        String[] values = {
                                "CTRL + Z - Undo",
                                "CTRL + Y - Redo",
                                "NUMPAD [1, 2, 3, 4, 5, 6, 7, 8]- Select layer",
                                "SHIFT + NUMPAD [1, 2, 3, 4, 5, 6, 7, 8] - Hide / Show layer",
                                "ESC - Enable edit mode",
                                "SHIFT + LEFT CLICK - Move camera",
                                "MOUSE WHEEL - Zoom",
                                "SPACE - Enable / Disable 3D view",
                                "E - Enable edit mode",
                                "G - Enable / disable grid",
                                "W - Enable / disable wireframe",
                                "A - Enable / disable area borders",
                                "F - Fit camera at current map",
                                "Q - Import map from image"
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
                    jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    jScrollPane1.setViewportView(jList1);
                }

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                                        .addContainerGap())
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                                        .addContainerGap())
                );
            }
            jTabbedPane1.addTab("Common Controls", jPanel2);

            //======== jPanel5 ========
            {

                //======== jScrollPane4 ========
                {

                    //---- jList4 ----
                    jList4.setModel(new AbstractListModel<String>() {
                        String[] values = {
                                "SPACE - Enable ortho view",
                                "H - Enable height view",
                                "ARROW RIGHT - Move to right map",
                                "ARROW LEFT - Move to left map",
                                "ARROW UP - Move to up map",
                                "ARROW DOWN - Move to down map"
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
                    jScrollPane4.setViewportView(jList4);
                }

                GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
                jPanel5.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                        jPanel5Layout.createParallelGroup()
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                                        .addContainerGap())
                );
                jPanel5Layout.setVerticalGroup(
                        jPanel5Layout.createParallelGroup()
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                                        .addContainerGap())
                );
            }
            jTabbedPane1.addTab("3D View", jPanel5);

            //======== jPanel3 ========
            {

                //======== jScrollPane2 ========
                {

                    //---- jList2 ----
                    jList2.setModel(new AbstractListModel<String>() {
                        String[] values = {
                                "SPACE - Enable 3D view",
                                "H - Enable height view",
                                "C - Enable clear mode",
                                "S - Enable smart drawing",
                                "ARROW RIGHT - Move to right map",
                                "ARROW LEFT - Move to left map",
                                "ARROW UP - Move to up map",
                                "ARROW DOWN - Move to down map"
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
                    jScrollPane2.setViewportView(jList2);
                }

                GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                                        .addContainerGap())
                );
                jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                                        .addContainerGap())
                );
            }
            jTabbedPane1.addTab("Ortho View", jPanel3);

            //======== jPanel4 ========
            {

                //======== jScrollPane3 ========
                {

                    //---- jList3 ----
                    jList3.setModel(new AbstractListModel<String>() {
                        String[] values = {
                                "SPACE - Enable 3D view",
                                "H - Disable height view",
                                "ARROW RIGHT - Move to right map",
                                "ARROW LEFT - Move to left map",
                                "ARROW UP - Move to up map",
                                "ARROW DOWN - Move to down map"
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
                    jScrollPane3.setViewportView(jList3);
                }

                GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
                jPanel4.setLayout(jPanel4Layout);
                jPanel4Layout.setHorizontalGroup(
                        jPanel4Layout.createParallelGroup()
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                                        .addContainerGap())
                );
                jPanel4Layout.setVerticalGroup(
                        jPanel4Layout.createParallelGroup()
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                                        .addContainerGap())
                );
            }
            jTabbedPane1.addTab("Height View", jPanel4);
        }

        //======== jPanel1 ========
        {
            jPanel1.setLayout(new FlowLayout());

            //---- jbAccept ----
            jbAccept.setText("OK");
            jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
            jPanel1.add(jbAccept);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane jTabbedPane1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JList<String> jList1;
    private JPanel jPanel5;
    private JScrollPane jScrollPane4;
    private JList<String> jList4;
    private JPanel jPanel3;
    private JScrollPane jScrollPane2;
    private JList<String> jList2;
    private JPanel jPanel4;
    private JScrollPane jScrollPane3;
    private JList<String> jList3;
    private JPanel jPanel1;
    private JButton jbAccept;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
