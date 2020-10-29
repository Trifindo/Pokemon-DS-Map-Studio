package editor.bdhc;

import net.miginfocom.swing.MigLayout;
import utils.Utils;
import utils.Utils.MutableBoolean;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Trifindo, JackHack96
 */
public class AngleCalculatorDialog extends JDialog {
    public static final int ACCEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private final MutableBoolean jtfTilesForwardEnabled = new MutableBoolean(true);
    private final MutableBoolean jtfTilesUpEnabled = new MutableBoolean(true);
    private static final Color editingColor = new Color(255, 185, 185);
    private static final Color rightColor = new Color(200, 255, 200);

    private float tilesForward = 1.0f;
    private float tilesUp = 1.0f;
    private float angle = 45.0f;

    public AngleCalculatorDialog(Window owner, float angle, float tilesForward) {
        super(owner);
        initComponents();

        this.angle = angle;
        this.tilesForward = tilesForward;
        this.tilesUp = (float) (tilesForward * Math.tan(angle * (Math.PI / 180f)));

        Utils.addListenerToJTextFieldColor(jtfTilesForward, jtfTilesForwardEnabled, editingColor, Color.black);
        Utils.addListenerToJTextFieldColor(jtfTilesUp, jtfTilesUpEnabled, editingColor, Color.black);

        updateTilesForward();
        updateTilesUp();
        updateAngle();
    }

    private void jbApplyTilesForwardActionPerformed(ActionEvent e) {
        try {
            tilesForward = Float.parseFloat(jtfTilesForward.getText());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        updateTilesForward();
        updateAngle();
    }

    private void jbApplyTilesUpActionPerformed(ActionEvent e) {
        try {
            tilesUp = Float.parseFloat(jtfTilesUp.getText());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        updateTilesUp();
        updateAngle();
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCELED;
        dispose();
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        returnValue = ACCEPTED;
        dispose();
    }

    public void updateTilesForward() {
        jtfTilesForwardEnabled.value = false;
        jtfTilesForward.setText(String.valueOf(tilesForward));
        jtfTilesForward.setBackground(UIManager.getColor("TextPane.background"));
        jtfTilesForward.setForeground(UIManager.getColor("TextPane.foreground"));
        jtfTilesForwardEnabled.value = true;
    }

    public void updateTilesUp() {
        jtfTilesUpEnabled.value = false;
        jtfTilesUp.setText(String.valueOf(tilesUp));
        jtfTilesUp.setBackground(UIManager.getColor("TextPane.background"));
        jtfTilesUp.setForeground(UIManager.getColor("TextPane.foreground"));
        jtfTilesUpEnabled.value = true;
    }

    public void updateAngle() {
        angle = (float) (Math.atan2(tilesUp, tilesForward) * (180f / Math.PI));
        jtfAngle.setText(String.valueOf(angle));
    }

    public float getAngle() {
        return angle;
    }

    public int getReturnValue() {
        return returnValue;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jtfTilesForward = new JTextField();
        jtfTilesUp = new JTextField();
        jbApplyTilesForward = new JButton();
        jbApplyTilesUp = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        jPanel3 = new JPanel();
        jLabel4 = new JLabel();
        jtfAngle = new JTextField();
        panel1 = new JPanel();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Angle Calculator");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]" +
            "[fill]",
            // rows
            "[fill]" +
            "[fill]" +
            "[fill]"));

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Write the plate orientation"));
            jPanel1.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[grow,fill]" +
                "[fill]",
                // rows
                "[fill]" +
                "[fill]"));

            //---- jLabel2 ----
            jLabel2.setForeground(Color.red);
            jLabel2.setText("Tiles forward:");
            jPanel1.add(jLabel2, "cell 0 1");

            //---- jLabel3 ----
            jLabel3.setForeground(new Color(51, 204, 255));
            jLabel3.setText("Tiles up:");
            jPanel1.add(jLabel3, "cell 0 0");

            //---- jtfTilesForward ----
            jtfTilesForward.setText(" ");
            jPanel1.add(jtfTilesForward, "cell 1 1");

            //---- jtfTilesUp ----
            jtfTilesUp.setText(" ");
            jPanel1.add(jtfTilesUp, "cell 1 0");

            //---- jbApplyTilesForward ----
            jbApplyTilesForward.setText("Apply");
            jbApplyTilesForward.addActionListener(e -> jbApplyTilesForwardActionPerformed(e));
            jPanel1.add(jbApplyTilesForward, "cell 2 1");

            //---- jbApplyTilesUp ----
            jbApplyTilesUp.setText("Apply");
            jbApplyTilesUp.addActionListener(e -> jbApplyTilesUpActionPerformed(e));
            jPanel1.add(jbApplyTilesUp, "cell 2 0");
        }
        contentPane.add(jPanel1, "cell 0 0");

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new BevelBorder(BevelBorder.LOWERED));
            jPanel2.setLayout(new BorderLayout());

            //---- jLabel1 ----
            jLabel1.setIcon(new ImageIcon(getClass().getResource("/imgs/AngleCalculator.png")));
            jPanel2.add(jLabel1, BorderLayout.CENTER);
        }
        contentPane.add(jPanel2, "cell 1 0 1 2");

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Resulting angle"));
            jPanel3.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[grow,fill]",
                // rows
                "[fill]"));

            //---- jLabel4 ----
            jLabel4.setForeground(new Color(204, 0, 204));
            jLabel4.setText("Angle:");
            jPanel3.add(jLabel4, "cell 0 0");

            //---- jtfAngle ----
            jtfAngle.setEditable(false);
            jtfAngle.setText(" ");
            jPanel3.add(jtfAngle, "cell 1 0");
        }
        contentPane.add(jPanel3, "cell 0 1");

        //======== panel1 ========
        {
            panel1.setLayout(new GridLayout(1, 2));

            //---- jbAccept ----
            jbAccept.setText("OK");
            jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
            panel1.add(jbAccept);

            //---- jbCancel ----
            jbCancel.setText("Cancel");
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
            panel1.add(jbCancel);
        }
        contentPane.add(panel1, "cell 0 2 2 1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JTextField jtfTilesForward;
    private JTextField jtfTilesUp;
    private JButton jbApplyTilesForward;
    private JButton jbApplyTilesUp;
    private JPanel jPanel2;
    private JLabel jLabel1;
    private JPanel jPanel3;
    private JLabel jLabel4;
    private JTextField jtfAngle;
    private JPanel panel1;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
