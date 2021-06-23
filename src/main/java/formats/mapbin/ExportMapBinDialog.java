package formats.mapbin;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Trifindo, JackHack96
 */
public class ExportMapBinDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;

    public ExportMapBinDialog(Window owner, String title) {
        super(owner);
        initComponents();

        this.setTitle(title);

        getRootPane().setDefaultButton(jbAccept);
        jbAccept.requestFocus();
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

    public boolean exportAllMapsBin() {
        return jrbExportAllMapsBin.isSelected();
    }
    
    public boolean exportCurrentMapBin() {
        return jrbExportCurrentMapBin.isSelected();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel2 = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        jPanel1 = new JPanel();
        jrbExportCurrentMapBin = new JRadioButton();
        jrbExportAllMapsBin = new JRadioButton();
        panel1 = new JPanel();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Title");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 5,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]",
            // rows
            "[]" +
            "[grow,fill]" +
            "[]"));

        //======== panel2 ========
        {
            panel2.setLayout(new MigLayout(
                "insets 5,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[fill]" +
                "[]"));

            //---- label1 ----
            label1.setText("Save the map and export it as NSBMD before exporting the BIN file");
            label1.setIcon(new ImageIcon(getClass().getResource("/icons/WarningIcon.png")));
            panel2.add(label1, "cell 0 0");

            //---- label2 ----
            label2.setText("Non-empty BDHCAM files will be included in the BIN");
            label2.setIcon(new ImageIcon(getClass().getResource("/icons/informationIcon.png")));
            panel2.add(label2, "cell 0 1");
        }
        contentPane.add(panel2, "cell 0 0");

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Settings"));
            jPanel1.setLayout(new MigLayout(
                "insets 5,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[165,grow,fill]",
                // rows
                "[fill]" +
                "[fill]"));

            //---- jrbExportCurrentMapBin ----
            jrbExportCurrentMapBin.setText("Export Only Current Map Binary File");
            jPanel1.add(jrbExportCurrentMapBin, "cell 0 1 2 1");

            //---- jrbExportAllMapsBin ----
            jrbExportAllMapsBin.setText("Export All Maps Binary Files");
            jrbExportAllMapsBin.setSelected(true);
            jPanel1.add(jrbExportAllMapsBin, "cell 0 0 2 1");
        }
        contentPane.add(jPanel1, "cell 0 1");

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
        contentPane.add(panel1, "cell 0 2,alignx right,growx 0");
        setSize(435, 230);
        setLocationRelativeTo(getOwner());

        //---- bgExportMap ----
        ButtonGroup bgExportMap = new ButtonGroup();
        bgExportMap.add(jrbExportCurrentMapBin);
        bgExportMap.add(jrbExportAllMapsBin);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel2;
    private JLabel label1;
    private JLabel label2;
    private JPanel jPanel1;
    private JRadioButton jrbExportCurrentMapBin;
    private JRadioButton jrbExportAllMapsBin;
    private JPanel panel1;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
