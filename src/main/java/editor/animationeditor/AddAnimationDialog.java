package editor.animationeditor;

import java.awt.event.*;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Trifindo, JackHack96
 */
public class AddAnimationDialog extends JDialog {
    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;

    public AddAnimationDialog(Window owner) {
        super(owner);
        initComponents();
    }

    public int getReturnValue() {
        return CANCEL_OPTION;
    }

    public String getNewName() {
        return jTextField1.getText();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        jLabel2 = new JLabel();
        panel1 = new JPanel();
        jButton1 = new JButton();
        jButton2 = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Enter the new animation name");
        setResizable(false);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3",
            // columns
            "[grow,fill]",
            // rows
            "[grow,fill]"));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Animation Name"));
            jPanel1.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[grow,fill]",
                // rows
                "[fill]" +
                "[fill]" +
                "[]"));

            //---- jLabel1 ----
            jLabel1.setText("Name of the animation: ");
            jPanel1.add(jLabel1, "cell 0 0");

            //---- jTextField1 ----
            jTextField1.setText(" ");
            jPanel1.add(jTextField1, "cell 1 0");

            //---- jLabel2 ----
            jLabel2.setText("The animation must have the same name as the NSBTX texure");
            jPanel1.add(jLabel2, "cell 0 1 2 1");

            //======== panel1 ========
            {
                panel1.setLayout(new GridLayout(1, 2));

                //---- jButton1 ----
                jButton1.setText("OK");
                panel1.add(jButton1);

                //---- jButton2 ----
                jButton2.setText("Cancel");
                panel1.add(jButton2);
            }
            jPanel1.add(panel1, "cell 0 2 2 1");
        }
        contentPane.add(jPanel1, "cell 0 0");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JTextField jTextField1;
    private JLabel jLabel2;
    private JPanel panel1;
    private JButton jButton1;
    private JButton jButton2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
