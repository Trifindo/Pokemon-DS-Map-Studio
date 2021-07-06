package editor.about;

import javax.swing.border.*;

import editor.handler.MapEditorHandler;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.GroupLayout;
import net.miginfocom.swing.*;

/**
 * @author Trifindo, JackHack96
 */
public class AboutDialog extends JDialog {
    public AboutDialog(Window owner) {
        super(owner);
        initComponents();
        jlVersionName.setText(MapEditorHandler.versionName);

        getRootPane().setDefaultButton(jbOk);
        jbOk.requestFocus();
    }

    private void jlWebsiteClick(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/Trifindo/Pokemon-DS-Map-Studio"));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    private void jbOkClick(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jlVersionName = new JLabel();
        jlAuthor = new JLabel();
        jlWebsite = new JLabel();
        klTrifindo = new JLabel();
        jScrollPane1 = new JScrollPane();
        jtDescription = new JTextArea();
        panel2 = new JPanel();
        jlCredits = new JLabel();
        panel1 = new JPanel();
        jbOk = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 5,hidemode 3,gap 5 5",
            // columns
            "[fill]" +
            "[grow,fill]",
            // rows
            "[fill]" +
            "[fill]" +
            "[fill]" +
            "[grow,fill]" +
            "[80]" +
            "[49]"));

        //---- jlVersionName ----
        jlVersionName.setFont(new Font("Tahoma", Font.BOLD, 18));
        jlVersionName.setHorizontalAlignment(SwingConstants.CENTER);
        jlVersionName.setText("Pokemon DS Map Studio 2.2");
        contentPane.add(jlVersionName, "cell 0 0 2 1");

        //---- jlAuthor ----
        jlAuthor.setText("<html>-- by <b style=\"color:#06B006\";>Trifindo</b> --</html>");
        jlAuthor.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(jlAuthor, "cell 0 1 2 1");

        //---- jlWebsite ----
        jlWebsite.setText("<html><body><a href=\"https://github.com/Trifindo/Pokemon-DS-Map-Studio\">Official website</a></body></html>");
        jlWebsite.setHorizontalAlignment(SwingConstants.CENTER);
        jlWebsite.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jlWebsite.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jlWebsiteClick(e);
            }
        });
        contentPane.add(jlWebsite, "cell 0 2 2 1");

        //---- klTrifindo ----
        klTrifindo.setHorizontalAlignment(SwingConstants.CENTER);
        klTrifindo.setIcon(new ImageIcon(getClass().getResource("/icons/trifindo.gif")));
        contentPane.add(klTrifindo, "cell 0 3");

        //======== jScrollPane1 ========
        {

            //---- jtDescription ----
            jtDescription.setEditable(false);
            jtDescription.setColumns(20);
            jtDescription.setRows(5);
            jtDescription.setText("Pokemon DS Map Studio is a tool for creating NDS Pok\u00e9mon maps, designed to be used alongside SDSME, DSPRE or similar.\n\nIt doesn't require 3D modeling knowledge. Rather, it provides a tilemap-like interface that is automatically converted to a 3D model. Please note that this tool DOES NOT allow modification of maps from official games.");
            jtDescription.setLineWrap(true);
            jtDescription.setFont(UIManager.getFont("TextArea.font"));
            jtDescription.setWrapStyleWord(true);
            jScrollPane1.setViewportView(jtDescription);
        }
        contentPane.add(jScrollPane1, "cell 1 3");

        //======== panel2 ========
        {
            panel2.setLayout(new BorderLayout());

            //---- jlCredits ----
            jlCredits.setForeground(UIManager.getColor("Label.foreground"));
            jlCredits.setText("<html>\n<table>\t\n\t<tr><th><p style=\"color:#F07903\";>JackHack96</p></th><th><p style=\"color:#333FFF\";>Mikelan98</p></th><th><p style=\"color:#898989\";>Jay</p></th></tr>\n\t<tr><th><p style=\"color:#F00303\";>Driox</p></th><th><p style=\"color:#AD7F36\";>Jiboule</p></th><th><p style=\"color:#3B41A06\";>Brom</p></th></tr>\n\t<tr><th><p style=\"color:#2F9B0F\";>Nextworld</p></th><th><p style=\"color:#603294\";>AdAstra</p></th><th><p style=\"color:#328D94\";>Monkeyboy0</p></th></tr>\n\t<tr><th><p style=\"color:#7AB006\";>Ren\u00e9</p></th><th><p style=\"color:#7AB0F6\";>Silast</p></th></tr>\n</table>\n</html>");
            jlCredits.setBorder(new TitledBorder("Credits"));
            panel2.add(jlCredits, BorderLayout.CENTER);
        }
        contentPane.add(panel2, "cell 1 4");

        //======== panel1 ========
        {
            panel1.setLayout(new FlowLayout());

            //---- jbOk ----
            jbOk.setText("OK");
            jbOk.addActionListener(e -> jbOkClick(e));
            panel1.add(jbOk);
        }
        contentPane.add(panel1, "cell 0 5 2 1");
        setSize(635, 425);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel jlVersionName;
    private JLabel jlAuthor;
    private JLabel jlWebsite;
    private JLabel klTrifindo;
    private JScrollPane jScrollPane1;
    private JTextArea jtDescription;
    private JPanel panel2;
    private JLabel jlCredits;
    private JPanel panel1;
    private JButton jbOk;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
