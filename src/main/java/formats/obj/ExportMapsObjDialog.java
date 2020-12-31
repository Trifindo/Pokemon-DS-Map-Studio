package formats.obj;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import net.miginfocom.swing.*;

/**
 * @author Trifindo, JackHack96
 */
public class ExportMapsObjDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;
    private boolean includeVertexColors = true;
    private boolean useExportgroups = true;

    public ExportMapsObjDialog(Window owner, String title) {
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
    
    public boolean exportAllMapsBothModes() {
        return jrbExportAllMapsBothModes.isSelected();
    }

    public float getTileUpscaling(){
        try{
            return (Float)jsTileUpscaling.getValue();
        } catch(Exception ex){
            return 1.0f;
        }
    }
    
    public boolean useExportgroups() {
        return jcbUseExportgroups.isSelected();
    }

    private void jcbUseExportgroupsActionPerformed(ActionEvent e) {
        useExportgroups = jcbUseExportgroups.isSelected();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jcbVertexColors = new JCheckBox();
        label1 = new JLabel();
        jsTileUpscaling = new JSpinner();
        jrbExportAllMapsBothModes = new JRadioButton();
        jrbExportAllMapsSeparately = new JRadioButton();
        jrbExportCurrentMap = new JRadioButton();
        jrbExportAllMapsJoined = new JRadioButton();
        jcbUseExportgroups = new JCheckBox();
        panel1 = new JPanel();
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
                "insets 5,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[165,grow,fill]",
                // rows
                "[fill]para" +
                "[]para" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[]" +
                "[]" +
                "[]" +
                "[]"));

            //---- jcbVertexColors ----
            jcbVertexColors.setSelected(true);
            jcbVertexColors.setText("Include Vertex Colors");
            jcbVertexColors.addActionListener(e -> jcbVertexColorsActionPerformed(e));
            jPanel1.add(jcbVertexColors, "cell 0 0 2 1");

            //---- label1 ----
            label1.setText("Tile Upscaling:");
            jPanel1.add(label1, "cell 0 1");

            //---- jsTileUpscaling ----
            jsTileUpscaling.setModel(new SpinnerNumberModel(1.0F, 1.0F, null, 0.001F));
            jPanel1.add(jsTileUpscaling, "cell 1 1");

            //---- jrbExportAllMapsBothModes ----
            jrbExportAllMapsBothModes.setSelected(true);
            jrbExportAllMapsBothModes.setText("Export All Both Separately and Joined");
            jPanel1.add(jrbExportAllMapsBothModes, "cell 0 2 2 1");

            //---- jrbExportAllMapsSeparately ----
            jrbExportAllMapsSeparately.setText("Export All Maps Separately");
            jPanel1.add(jrbExportAllMapsSeparately, "cell 0 3 2 1");

            //---- jrbExportCurrentMap ----
            jrbExportCurrentMap.setText("Export Only Current Map");
            jPanel1.add(jrbExportCurrentMap, "cell 0 5 2 1");

            //---- jrbExportAllMapsJoined ----
            jrbExportAllMapsJoined.setText("Export All Maps Joined");
            jPanel1.add(jrbExportAllMapsJoined, "cell 0 4 2 1");

            //---- jcbUseExportgroups ----
            jcbUseExportgroups.setText("Use Export Groups");
            jcbUseExportgroups.setActionCommand("Use Export Groups");
            jcbUseExportgroups.addActionListener(e -> jcbUseExportgroupsActionPerformed(e));
            jPanel1.add(jcbUseExportgroups, "cell 0 8");
        }
        contentPane.add(jPanel1, "cell 0 0");

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[grow,right]" +
                "[grow,right]",
                // rows
                "[]"));

            //---- jbAccept ----
            jbAccept.setText("OK");
            jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
            panel1.add(jbAccept, "cell 0 0,alignx right,growx 0");

            //---- jbCancel ----
            jbCancel.setText("Cancel");
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
            panel1.add(jbCancel, "cell 1 0");
        }
        contentPane.add(panel1, "cell 0 1,alignx right,growx 0");
        setSize(310, 355);
        setLocationRelativeTo(getOwner());

        //---- bgExportMap ----
        ButtonGroup bgExportMap = new ButtonGroup();
        bgExportMap.add(jrbExportAllMapsBothModes);
        bgExportMap.add(jrbExportAllMapsSeparately);
        bgExportMap.add(jrbExportCurrentMap);
        bgExportMap.add(jrbExportAllMapsJoined);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JCheckBox jcbVertexColors;
    private JLabel label1;
    private JSpinner jsTileUpscaling;
    private JRadioButton jrbExportAllMapsBothModes;
    private JRadioButton jrbExportAllMapsSeparately;
    private JRadioButton jrbExportCurrentMap;
    private JRadioButton jrbExportAllMapsJoined;
    private JCheckBox jcbUseExportgroups;
    private JPanel panel1;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
