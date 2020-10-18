package editor.tileseteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import editor.tileselector.*;
import editor.TilesetRenderer;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import tileset.Tile;
import tileset.Tileset;

/**
 * @author Trifindo, JackHack96
 */
public class ImportTilesDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;

    private Tileset tileset;

    public ImportTilesDialog(Window owner) {
        super(owner);
        initComponents();

        this.jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);

        tileMultiSelector.requestFocus();
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCEL_OPTION;
        dispose();
    }

    private void jbFinishActionPerformed(ActionEvent e) {
        if (tileMultiSelector.getNumTilesSelected() > 0) {
            returnValue = APPROVE_OPTION;
            dispose();
        }else{
            JOptionPane.showMessageDialog(this, "You need to select at least 1 tile",
                    "No tiles selected", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jbSelectAllActionPerformed(ActionEvent e) {
        tileMultiSelector.selectAll();
        tileMultiSelector.repaint();
    }

    private void jbDeselectAllActionPerformed(ActionEvent e) {
        tileMultiSelector.deselectAll();
        tileMultiSelector.repaint();
    }

    public void init(Tileset tileset) {
        this.tileset = tileset;

        TilesetRenderer tr = new TilesetRenderer(tileset);
        try {
            tr.renderTiles();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        tr.destroy();

        tileMultiSelector.init(tileset);
    }

    public int getReturnValue() {
        return returnValue;
    }

    public ArrayList<Tile> getTilesSelected() {
        return tileMultiSelector.getTilesSelected();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        tileMultiSelector = new TileMultiSelector();
        jbCancel = new JButton();
        jbFinish = new JButton();
        jPanel2 = new JPanel();
        jbSelectAll = new JButton();
        jbDeselectAll = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select the tiles to import");
        setModal(true);
        var contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Tile Selector"));

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane1.setFocusable(false);

                //======== tileMultiSelector ========
                {
                    tileMultiSelector.setPreferredSize(new Dimension(128, 400));

                    GroupLayout tileMultiSelectorLayout = new GroupLayout(tileMultiSelector);
                    tileMultiSelector.setLayout(tileMultiSelectorLayout);
                    tileMultiSelectorLayout.setHorizontalGroup(
                        tileMultiSelectorLayout.createParallelGroup()
                            .addGap(0, 128, Short.MAX_VALUE)
                    );
                    tileMultiSelectorLayout.setVerticalGroup(
                        tileMultiSelectorLayout.createParallelGroup()
                            .addGap(0, 400, Short.MAX_VALUE)
                    );
                }
                jScrollPane1.setViewportView(tileMultiSelector);
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addComponent(jScrollPane1)
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
            );
        }

        //---- jbCancel ----
        jbCancel.setText("Cancel");
        jbCancel.setFocusable(false);
        jbCancel.addActionListener(e -> jbCancelActionPerformed(e));

        //---- jbFinish ----
        jbFinish.setText("Finish");
        jbFinish.setFocusable(false);
        jbFinish.addActionListener(e -> jbFinishActionPerformed(e));

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Select Options"));

            //---- jbSelectAll ----
            jbSelectAll.setText("Select All");
            jbSelectAll.setFocusable(false);
            jbSelectAll.addActionListener(e -> jbSelectAllActionPerformed(e));

            //---- jbDeselectAll ----
            jbDeselectAll.setText("Deselect All");
            jbDeselectAll.setFocusable(false);
            jbDeselectAll.addActionListener(e -> jbDeselectAllActionPerformed(e));

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jbSelectAll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbDeselectAll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jbSelectAll)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbDeselectAll)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jbFinish, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbCancel))
                        .addComponent(jPanel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jbCancel)
                                .addComponent(jbFinish)))
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private TileMultiSelector tileMultiSelector;
    private JButton jbCancel;
    private JButton jbFinish;
    private JPanel jPanel2;
    private JButton jbSelectAll;
    private JButton jbDeselectAll;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
