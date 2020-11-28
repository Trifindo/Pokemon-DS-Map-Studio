/*
 * Created by JFormDesigner on Sat Nov 21 14:18:06 CET 2020
 */

package formats.bdhcam;

import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Truck
 */
public class CameraSettingsDialog extends JDialog {
    public CameraSettingsDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        label1 = new JLabel();
        spinner1 = new JSpinner();
        label2 = new JLabel();
        spinner2 = new JSpinner();
        label3 = new JLabel();
        spinner3 = new JSpinner();
        label4 = new JLabel();
        spinner4 = new JSpinner();
        label5 = new JLabel();
        spinner5 = new JSpinner();
        label6 = new JLabel();
        spinner6 = new JSpinner();
        label7 = new JLabel();
        spinner7 = new JSpinner();
        label8 = new JLabel();
        spinner8 = new JSpinner();
        label9 = new JLabel();
        spinner9 = new JSpinner();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[grow,fill]",
            // rows
            "[grow]"));

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[]" +
                "[]" +
                "[]"));

            //---- label1 ----
            label1.setText("Camera X:");
            panel1.add(label1, "cell 0 0");
            panel1.add(spinner1, "cell 1 0");

            //---- label2 ----
            label2.setText("Camera Y:");
            panel1.add(label2, "cell 2 0");
            panel1.add(spinner2, "cell 3 0");

            //---- label3 ----
            label3.setText("Camera Z:");
            panel1.add(label3, "cell 4 0");
            panel1.add(spinner3, "cell 5 0");

            //---- label4 ----
            label4.setText("Target X:");
            panel1.add(label4, "cell 0 1");
            panel1.add(spinner4, "cell 1 1");

            //---- label5 ----
            label5.setText("Target Y:");
            panel1.add(label5, "cell 2 1");
            panel1.add(spinner5, "cell 3 1");

            //---- label6 ----
            label6.setText("Target Z:");
            panel1.add(label6, "cell 4 1");
            panel1.add(spinner6, "cell 5 1");

            //---- label7 ----
            label7.setText("Up X:");
            panel1.add(label7, "cell 0 2");
            panel1.add(spinner7, "cell 1 2");

            //---- label8 ----
            label8.setText("Up Y:");
            panel1.add(label8, "cell 2 2");
            panel1.add(spinner8, "cell 3 2");

            //---- label9 ----
            label9.setText("Up Z:");
            panel1.add(label9, "cell 4 2");
            panel1.add(spinner9, "cell 5 2");
        }
        contentPane.add(panel1, "cell 0 0");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private JSpinner spinner1;
    private JLabel label2;
    private JSpinner spinner2;
    private JLabel label3;
    private JSpinner spinner3;
    private JLabel label4;
    private JSpinner spinner4;
    private JLabel label5;
    private JSpinner spinner5;
    private JLabel label6;
    private JSpinner spinner6;
    private JLabel label7;
    private JSpinner spinner7;
    private JLabel label8;
    private JSpinner spinner8;
    private JLabel label9;
    private JSpinner spinner9;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
