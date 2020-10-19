package editor.converter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Trifindo, JackHack96
 */
public class ConverterDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;
    private boolean includeNsbtxInNsbmd = true;

    public ConverterDialog(Window owner) {
        super(owner);
        initComponents();

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

    private void jcbIncludeNsbtxInNsbmdActionPerformed(ActionEvent e) {
        includeNsbtxInNsbmd = jcbIncludeNsbtxInNsbmd.isSelected();
    }

    private void jbHelpActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this,
                "Maps can include the NSBTX in the NSBMD, but it's not necessary.\n" +
                        "Buildings should include the NSBTX in the NSBMD.",
                "Include NSBTX Help", JOptionPane.INFORMATION_MESSAGE);
    }

    public int getReturnValue() {
        return returnValue;
    }

    public boolean includeNsbtxInNsbmd() {
        return includeNsbtxInNsbmd;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jbAccept = new JButton();
        jbCancel = new JButton();
        jPanel1 = new JPanel();
        jcbIncludeNsbtxInNsbmd = new JCheckBox();
        jbHelp = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Converter Options");
        setResizable(false);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

        //---- jbAccept ----
        jbAccept.setText("OK");
        jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
        contentPane.add(jbAccept, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

        //---- jbCancel ----
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
        contentPane.add(jbCancel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Settings"));
            jPanel1.setLayout(new GridBagLayout());
            ((GridBagLayout)jPanel1.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout)jPanel1.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)jPanel1.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
            ((GridBagLayout)jPanel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- jcbIncludeNsbtxInNsbmd ----
            jcbIncludeNsbtxInNsbmd.setSelected(true);
            jcbIncludeNsbtxInNsbmd.setText("Include NSBTX in NSBMD");
            jcbIncludeNsbtxInNsbmd.addActionListener(e -> jcbIncludeNsbtxInNsbmdActionPerformed(e));
            jPanel1.add(jcbIncludeNsbtxInNsbmd, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

            //---- jbHelp ----
            jbHelp.setIcon(new ImageIcon(getClass().getResource("/icons/infoIcon.png")));
            jbHelp.setToolTipText("Help");
            jbHelp.setMargin(new Insets(2, 2, 2, 2));
            jbHelp.addActionListener(e -> jbHelpActionPerformed(e));
            jPanel1.add(jbHelp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(jPanel1, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton jbAccept;
    private JButton jbCancel;
    private JPanel jPanel1;
    private JCheckBox jcbIncludeNsbtxInNsbmd;
    private JButton jbHelp;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
