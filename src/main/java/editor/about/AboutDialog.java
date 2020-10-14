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

/**
 * @author Trifindo, JackHack96
 */
public class AboutDialog extends JDialog {
    public AboutDialog(Window owner) {
        super(owner);
        initComponents();
        jlVersionName.setText(MapEditorHandler.versionName);
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
        jbOk = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About");
        setResizable(false);
        setModal(true);
        var contentPane = getContentPane();

        //---- jlVersionName ----
        jlVersionName.setFont(new Font("Tahoma", Font.BOLD, 18));
        jlVersionName.setHorizontalAlignment(SwingConstants.CENTER);
        jlVersionName.setText("Pokemon DS Map Studio 2.1");

        //---- jlAuthor ----
        jlAuthor.setText("-- by Trifindo --");
        jlAuthor.setHorizontalAlignment(SwingConstants.CENTER);

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

        //---- klTrifindo ----
        klTrifindo.setHorizontalAlignment(SwingConstants.CENTER);
        klTrifindo.setIcon(new ImageIcon(getClass().getResource("/icons/trifindo.gif")));

        //======== jScrollPane1 ========
        {

            //---- jtDescription ----
            jtDescription.setEditable(false);
            jtDescription.setColumns(20);
            jtDescription.setRows(5);
            jtDescription.setText("Pokemon DS Map Studio is a tool for creating gen 4 and gen 5 Pok\u00e9mon games' maps,\ndesigned to be used alongside SDSME.\n\nIt doesn't require 3D modeling, instead it provides a tilemap-like interface that is automatically \nconverted in a 3D model.\nNote that this tool DOES NOT import maps from the original games, neither it can modify them.\n\nSupported games:\n- Pokemon Diamond/Pearl\n- Pokemon Platinum\n- Pokemon Heart Gold/Soul Silver\n\nNot completely working:\n- Pokemon Black/White\n- Pokemon Black 2/ White 2");
            jtDescription.setLineWrap(true);
            jtDescription.setFont(UIManager.getFont("TextArea.font"));
            jScrollPane1.setViewportView(jtDescription);
        }

        //======== panel2 ========
        {
            panel2.setLayout(new BorderLayout());

            //---- jlCredits ----
            jlCredits.setForeground(UIManager.getColor("Label.foreground"));
            jlCredits.setText("<html>\n\n<body>\n    JackHack96<br/>\n    Mikelan98<br/>\n    Driox<br/>\n    Jiboule<br/>\n    Nextworld<br/>\n    Jay<br/>\n    Brom<br/>\n    AdAstra<br/>\n    Ren\u00e9<br/>\n    Monkeyboy0<br/>\n</body>\n\n</html>");
            jlCredits.setBorder(new TitledBorder("Credits"));
            panel2.add(jlCredits, BorderLayout.CENTER);
        }

        //---- jbOk ----
        jbOk.setText("OK");
        jbOk.addActionListener(e -> jbOkClick(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jlVersionName, GroupLayout.PREFERRED_SIZE, 788, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlAuthor, GroupLayout.PREFERRED_SIZE, 788, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlWebsite, GroupLayout.PREFERRED_SIZE, 788, GroupLayout.PREFERRED_SIZE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(klTrifindo)
                            .addGap(5, 5, 5)
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 719, GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(352, 352, 352)
                            .addComponent(jbOk))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(69, 69, 69)
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 719, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(jlVersionName, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                    .addGap(6, 6, 6)
                    .addComponent(jlAuthor)
                    .addGap(7, 7, 7)
                    .addComponent(jlWebsite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(5, 5, 5)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(klTrifindo, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
                    .addGap(5, 5, 5)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)
                    .addComponent(jbOk)
                    .addContainerGap())
        );
        pack();
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
    private JButton jbOk;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
