/*
 * Created by JFormDesigner on Tue Dec 01 03:06:13 CET 2020
 */

package formats.obj;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import net.miginfocom.swing.*;

/**
 * @author AdAstra/LD3005
 */
public class ExportSingleMapObjDialog extends JDialog {
    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;
    private boolean includeVertexColors = true;
    private boolean useExportgroups = true;

    public ExportSingleMapObjDialog(Window owner, String title) {
        super(owner);
        initComponents();

        this.setTitle(title);

        getRootPane().setDefaultButton(jbAccept);
        jbAccept.requestFocus();
    }

    private void jcbExportgroupsActionPerformed(ActionEvent e) {

        useExportgroups = jcbExportgroups.isSelected();
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

    public boolean useExportgroups() {
        return useExportgroups;
    }

    public float getTileUpscaling(){
        try{
            return (Float)jsTileUpscaling.getValue();
        } catch(Exception ex){
            return 1.0f;
        }
    }

    private void jcbVertexColorsActionPerformed(ActionEvent e) {
        includeVertexColors = jcbVertexColors.isSelected();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jcbVertexColors = new JCheckBox();
        jcbExportgroups = new JCheckBox();
        label1 = new JLabel();
        jsTileUpscaling = new JSpinner();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import Tile Settings");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 5,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]",
            // rows
            "[grow,fill]" +
            "[]"));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Settingsa"));
            jPanel1.setLayout(new MigLayout(
                "insets 5,hidemode 3",
                // columns
                "[fill]" +
                "[165,grow,fill]",
                // rows
                "[fill]para" +
                "[]para" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[]" +
                "[]"));

            //---- jcbVertexColors ----
            jcbVertexColors.setSelected(true);
            jcbVertexColors.setText("Include Vertex Colors");
            jcbVertexColors.addActionListener(e -> jcbVertexColorsActionPerformed(e));
            jPanel1.add(jcbVertexColors, "cell 0 0 2 1");

            //---- jcbExportgroups ----
            jcbExportgroups.setSelected(true);
            jcbExportgroups.setText("Render whole group");
            jcbExportgroups.addActionListener(e -> jcbExportgroupsActionPerformed(e));
            jPanel1.add(jcbExportgroups, "cell 0 0 2 1");

            //---- label1 ----
            label1.setText("Tile Upscaling:");
            jPanel1.add(label1, "cell 0 1");

            //---- jsTileUpscaling ----
            jsTileUpscaling.setModel(new SpinnerNumberModel(1.0F, 1.0F, null, 0.001F));
            jPanel1.add(jsTileUpscaling, "cell 1 1");

            //---- jbAccept ----
            jbAccept.setText("OK");
            jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
            jPanel1.add(jbAccept, "cell 0 5,alignx right,growx 0");

            //---- jbCancel ----
            jbCancel.setText("Cancel");
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
            jPanel1.add(jbCancel, "cell 1 5");
        }
        contentPane.add(jPanel1, "cell 0 0");
        setSize(315, 225);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JCheckBox jcbVertexColors;
    private JCheckBox jcbExportgroups;
    private JLabel label1;
    private JSpinner jsTileUpscaling;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
