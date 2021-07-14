
package editor.mapgroups;

import editor.handler.MapEditorHandler;
import utils.swing.JScrollCheckboxList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author AdAstra
 */
public class VisualizeExportGroupsDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;

    private ArrayList<Integer> selectedAreaIndices = new ArrayList<>();

    private MapEditorHandler handler;

    /**
     * Creates new form ExportImdDialog
     */
    public VisualizeExportGroupsDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        getRootPane().setDefaultButton(jbExit);
        jbExit.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new JPanel();
        jScrollCheckboxList = new JScrollCheckboxList();
        jbExit = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export Groups list (WIP)");
        setModal(true);
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Export Groups"));

            //---- jScrollCheckboxList ----
            jScrollCheckboxList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            jScrollCheckboxList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addComponent(jScrollCheckboxList, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap())
            )));
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollCheckboxList, GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addContainerGap())
            );
        }

        //---- jbCancel ----
        jbExit.setText("Close");
        jbExit.addActionListener(e -> jbCancelActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jPanel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jbExit)))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jbExit))
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents


    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        returnValue = CANCEL_OPTION;
        dispose();
    }//GEN-LAST:event_jbCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JScrollCheckboxList jScrollCheckboxList;
    private JButton jbExit;
    // End of variables declaration//GEN-END:variables

    public void init(MapEditorHandler handler) {
        this.handler = handler;

        loadExportGroupsIndices();
    }

    private void loadExportGroupsIndices() {

        TreeMap<Integer, MapGroup> exportGroupsMap = handler.getMapMatrix().getExportGroups();

        try {
            DefaultListModel<JCheckBox> model = new DefaultListModel();
            this.jScrollCheckboxList.getCheckboxList().setModel(model);
            for (MapGroup expg : exportGroupsMap.values()) {
                if (expg.getIndex() != 0)
                    model.addElement(new JCheckBox(String.valueOf(expg)));
            }

            this.jScrollCheckboxList.getCheckboxList().setModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getReturnValue() {
        return returnValue;
    }
}