package editor.converter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

import net.miginfocom.swing.*;

/**
 * @author Trifindo, JackHack96
 */
public class ConverterErrorDialog extends JDialog {
    public ConverterErrorDialog(Window owner) {
        super(owner);
        initComponents();

        jlError.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
    }

    private void jButton1ActionPerformed(ActionEvent e) {
        dispose();
    }

    public void init(String mainMsg, String textAreaMsg) {
        jLabel1.setText(mainMsg);
        jTextArea1.setText(textAreaMsg);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jLabel1 = new JLabel();
        jlError = new JLabel();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        panel1 = new JPanel();
        jButton1 = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets dialog,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]",
            // rows
            "[fill]" +
            "[grow,fill]" +
            "[fill]"));

        //---- jLabel1 ----
        jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel1.setText("Error message");
        contentPane.add(jLabel1, "cell 0 0");

        //---- jlError ----
        jlError.setHorizontalAlignment(SwingConstants.CENTER);
        jlError.setText(" ");
        jlError.setMaximumSize(new Dimension(50, 50));
        jlError.setMinimumSize(new Dimension(50, 50));
        jlError.setPreferredSize(new Dimension(50, 50));
        contentPane.add(jlError, "cell 1 0");

        //======== jScrollPane1 ========
        {

            //---- jTextArea1 ----
            jTextArea1.setColumns(20);
            jTextArea1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            jTextArea1.setRows(5);
            jTextArea1.setTabSize(3);
            jScrollPane1.setViewportView(jTextArea1);
        }
        contentPane.add(jScrollPane1, "cell 0 1 2 1");

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- jButton1 ----
            jButton1.setText("OK");
            jButton1.addActionListener(e -> jButton1ActionPerformed(e));
            panel1.add(jButton1);
        }
        contentPane.add(panel1, "cell 0 2 2 1,alignx center,growx 0");
        setSize(640, 285);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel jLabel1;
    private JLabel jlError;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    private JPanel panel1;
    private JButton jButton1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
