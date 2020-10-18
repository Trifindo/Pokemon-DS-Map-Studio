package editor.gameselector;

import editor.collisions.Collisions;
import editor.game.Game;
import editor.handler.MapEditorHandler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author Trifindo, JackHack96
 */
public class GameChangerDialog extends JDialog {

    private MapEditorHandler handler;
    public static final int ACEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private int newGame = Game.DIAMOND;
    private int oldGame;

    public GameChangerDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jcbGameActionPerformed(ActionEvent e) {
        newGame = jcbGame.getSelectedIndex();
        //handler.setGameIndex(jComboBox1.getSelectedIndex());
        updateViewGameIcon();
    }

    private void jbFinishActionPerformed(ActionEvent e) {
        if ((Game.isGenV(oldGame) && !Game.isGenV(newGame)) || (!Game.isGenV(oldGame) && Game.isGenV(newGame))) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "The map collisions will be deleted. Do you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                handler.setGameIndex(newGame);
                handler.setCollisions(new Collisions(newGame));
                returnValue = ACEPTED;
                dispose();
            }
        } else {
            handler.setGameIndex(newGame);
            returnValue = ACEPTED;
            dispose();
        }
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCELED;
        dispose();
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        jcbGame.setSelectedIndex(handler.getGame().gameSelected);
        oldGame = handler.getGameIndex();
    }

    public void updateViewGameIcon() {
        jlGameIcon.setIcon(new ImageIcon(handler.getGame().gameIcons[newGame]));
    }

    public int getReturnValue() {
        return returnValue;
    }

    public int getGameIndex() {
        return newGame;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jpanelIcon = new JPanel();
        jlGameIcon = new JLabel();
        jcbGame = new JComboBox<>();
        jLabel1 = new JLabel();
        jbFinish = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Change Game");
        setModal(true);
        var contentPane = getContentPane();

        //======== jpanelIcon ========
        {
            jpanelIcon.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

            //---- jlGameIcon ----
            jlGameIcon.setMaximumSize(new Dimension(32, 32));
            jlGameIcon.setMinimumSize(new Dimension(32, 32));
            jlGameIcon.setPreferredSize(new Dimension(32, 32));

            GroupLayout jpanelIconLayout = new GroupLayout(jpanelIcon);
            jpanelIcon.setLayout(jpanelIconLayout);
            jpanelIconLayout.setHorizontalGroup(
                jpanelIconLayout.createParallelGroup()
                    .addGroup(jpanelIconLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jlGameIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jpanelIconLayout.setVerticalGroup(
                jpanelIconLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jpanelIconLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlGameIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
            );
        }

        //---- jcbGame ----
        jcbGame.setModel(new DefaultComboBoxModel<>(new String[] {
            "Pokemon Diamond",
            "Pokemon Pearl",
            "Pokemon Platinum",
            "Pokemon Heart Gold",
            "Pokemon Soul Silver",
            "Pokemon Black",
            "Pokemon White",
            "Pokemon Black 2",
            "Pokemon White 2"
        }));
        jcbGame.addActionListener(e -> jcbGameActionPerformed(e));

        //---- jLabel1 ----
        jLabel1.setText("Select Game for this Map: ");

        //---- jbFinish ----
        jbFinish.setText("Finish");
        jbFinish.setToolTipText("");
        jbFinish.addActionListener(e -> jbFinishActionPerformed(e));

        //---- jbCancel ----
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(e -> jbCancelActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jcbGame))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpanelIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jbFinish, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbCancel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(7, 7, 7)
                            .addComponent(jLabel1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jcbGame, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jpanelIcon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jbFinish)
                        .addComponent(jbCancel))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jpanelIcon;
    private JLabel jlGameIcon;
    private JComboBox<String> jcbGame;
    private JLabel jLabel1;
    private JButton jbFinish;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
