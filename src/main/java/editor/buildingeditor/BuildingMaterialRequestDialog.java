package editor.buildingeditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.miginfocom.swing.*;

/**
 * @author Trifindo, JackHack96
 */
public class BuildingMaterialRequestDialog extends JDialog {
    public static final int ACCEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private int value = 0;
    private int min = 1;
    private int max = 20;
    private boolean useUndefined = false;

    public BuildingMaterialRequestDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        if (useUndefined) {
            returnValue = ACCEPTED;
            dispose();
        } else {
            try {
                value = Integer.parseInt(jTextField1.getText());
                if (value >= min && value <= max) {
                    returnValue = ACCEPTED;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The number must be inside the bounds ["
                                    + min + ", "
                                    + max + "]",
                            "Number is out of bounds",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "The number must be a integer",
                        "Error converting number",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCELED;
        dispose();
    }

    private void jcbUndefinedActionPerformed(ActionEvent e) {
        jTextField1.setEnabled(!jcbUndefined.isSelected());
        useUndefined = jcbUndefined.isSelected();
    }

    public void init(String msg, int min, int max) {
        this.jLabel1.setText(msg);
        this.min = min;
        this.max = max;

        this.jTextField1.setText(String.valueOf(value));
    }

    public int getIntegerRequested() {
        return value;
    }

    public boolean useUndefined() {
        return useUndefined;
    }

    public int getReturnValue() {
        return returnValue;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        jcbUndefined = new JCheckBox();
        panel1 = new JPanel();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                        "[grow,fill]" +
                        "[fill]",
                // rows
                "[fill]" +
                        "[fill]"));

        //---- jLabel1 ----
        jLabel1.setText("Number of materials for the Building: ");
        contentPane.add(jLabel1, "cell 0 0");
        contentPane.add(jTextField1, "cell 1 0");

        //---- jcbUndefined ----
        jcbUndefined.setText("Undefined");
        jcbUndefined.addActionListener(e -> jcbUndefinedActionPerformed(e));
        contentPane.add(jcbUndefined, "cell 2 0");

        //======== panel1 ========
        {
            panel1.setLayout(new GridLayout(1, 2));

            //---- jbAccept ----
            jbAccept.setText("Accept");
            jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
            panel1.add(jbAccept);

            //---- jbCancel ----
            jbCancel.setText("Cancel");
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
            panel1.add(jbCancel);
        }
        contentPane.add(panel1, "cell 1 1 2 1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel jLabel1;
    private JTextField jTextField1;
    private JCheckBox jcbUndefined;
    private JPanel panel1;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
