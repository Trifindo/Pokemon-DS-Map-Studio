package editor.mapmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

import editor.handler.MapData;
import editor.handler.MapEditorHandler;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 * @author Trifindo, JackHack96
 */
public class MapMatrixImportDialog extends JDialog {

    private MapEditorHandler handler;
    private String mapPath;
    private HashMap<Point, MapData> maps;

    public MapMatrixImportDialog(Window owner) {
        super(owner);
        initComponents();

        jScrollPane1.getHorizontalScrollBar().setUnitIncrement(16);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void jbUpActionPerformed(ActionEvent e) {
        mapImportDisplay1.moveMap(new Point(0, -1));
        mapImportDisplay1.repaint();
    }

    private void jbLeftActionPerformed(ActionEvent e) {
        mapImportDisplay1.moveMap(new Point(-1, 0));
        mapImportDisplay1.repaint();
    }

    private void jbRightActionPerformed(ActionEvent e) {
        mapImportDisplay1.moveMap(new Point(1, 0));
        mapImportDisplay1.repaint();
    }

    private void jbDownActionPerformed(ActionEvent e) {
        mapImportDisplay1.moveMap(new Point(0, 1));
        mapImportDisplay1.repaint();
    }

    private void jbImportActionPerformed(ActionEvent e) {
        if (isNewMapOverlapingWithMap()) {
            int returnValue = JOptionPane.showConfirmDialog(this,
                    "The new map is overlapping with the current map.\n"
                            + "The maps that are overlapping will be removed.\n"
                            + "Do you want to continue?",
                    "Maps overlapping",
                    JOptionPane.YES_NO_OPTION);
            if (returnValue == JOptionPane.YES_OPTION) {
                importMaps();
                dispose();
            }
        } else {
            importMaps();
            dispose();
        }
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        dispose();
    }

    public void init(MapEditorHandler handler, String mapPath, HashMap<Point, MapData> maps) {
        this.handler = handler;
        this.mapPath = mapPath;
        this.maps = maps;

        mapImportDisplay1.init(handler, maps);
        mapImportDisplay1.updateMapsImage();
        mapImportDisplay1.updateSize();
    }

    private boolean isNewMapOverlapingWithMap() {
        Point pos = mapImportDisplay1.getNewMapPos();
        for (Point p : maps.keySet()) {
            Point mapCoords = new Point(p.x + pos.x, p.y + pos.y);
            if (handler.getMapMatrix().getMatrix().containsKey(mapCoords)) {
                return true;
            }
        }
        return false;
    }

    public void importMaps() {
        try {
            handler.getMapMatrix().addMapsFromFile(maps, mapImportDisplay1.getNewMapPos(),
                    new File(mapPath).getParent(),
                    new File(mapPath).getName()
            );

            try {
                handler.getMainFrame().updateViewAllMapData();
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "There was a problem importing the maps",
                    "Can't import maps",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        mapImportDisplay1 = new MapImportDisplay();
        jPanel2 = new JPanel();
        jPanel3 = new JPanel();
        jbUp = new JButton();
        jPanel4 = new JPanel();
        jbLeft = new JButton();
        jPanel5 = new JPanel();
        jbRight = new JButton();
        jPanel6 = new JPanel();
        jbDown = new JButton();
        jPanel7 = new JPanel();
        jbImport = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose where to import the maps");
        setModal(true);
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Choose where to import the map"));

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //======== mapImportDisplay1 ========
                {

                    GroupLayout mapImportDisplay1Layout = new GroupLayout(mapImportDisplay1);
                    mapImportDisplay1.setLayout(mapImportDisplay1Layout);
                    mapImportDisplay1Layout.setHorizontalGroup(
                        mapImportDisplay1Layout.createParallelGroup()
                            .addGap(0, 480, Short.MAX_VALUE)
                    );
                    mapImportDisplay1Layout.setVerticalGroup(
                        mapImportDisplay1Layout.createParallelGroup()
                            .addGap(0, 235, Short.MAX_VALUE)
                    );
                }
                jScrollPane1.setViewportView(mapImportDisplay1);
            }

            //======== jPanel2 ========
            {
                jPanel2.setBorder(new TitledBorder(null, "Move Map", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
                jPanel2.setPreferredSize(new Dimension(100, 110));
                jPanel2.setLayout(new GridLayout(3, 3));

                //======== jPanel3 ========
                {

                    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                    jPanel3.setLayout(jPanel3Layout);
                    jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup()
                            .addGap(0, 30, Short.MAX_VALUE)
                    );
                    jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup()
                            .addGap(0, 27, Short.MAX_VALUE)
                    );
                }
                jPanel2.add(jPanel3);

                //---- jbUp ----
                jbUp.setText("\u25b2");
                jbUp.setMargin(new Insets(2, 2, 2, 2));
                jbUp.addActionListener(e -> jbUpActionPerformed(e));
                jPanel2.add(jbUp);

                //======== jPanel4 ========
                {

                    GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
                    jPanel4.setLayout(jPanel4Layout);
                    jPanel4Layout.setHorizontalGroup(
                        jPanel4Layout.createParallelGroup()
                            .addGap(0, 30, Short.MAX_VALUE)
                    );
                    jPanel4Layout.setVerticalGroup(
                        jPanel4Layout.createParallelGroup()
                            .addGap(0, 27, Short.MAX_VALUE)
                    );
                }
                jPanel2.add(jPanel4);

                //---- jbLeft ----
                jbLeft.setText("\u25c4");
                jbLeft.setMargin(new Insets(2, 2, 2, 2));
                jbLeft.addActionListener(e -> jbLeftActionPerformed(e));
                jPanel2.add(jbLeft);

                //======== jPanel5 ========
                {

                    GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
                    jPanel5.setLayout(jPanel5Layout);
                    jPanel5Layout.setHorizontalGroup(
                        jPanel5Layout.createParallelGroup()
                            .addGap(0, 30, Short.MAX_VALUE)
                    );
                    jPanel5Layout.setVerticalGroup(
                        jPanel5Layout.createParallelGroup()
                            .addGap(0, 27, Short.MAX_VALUE)
                    );
                }
                jPanel2.add(jPanel5);

                //---- jbRight ----
                jbRight.setText("\u25ba");
                jbRight.setMargin(new Insets(2, 2, 2, 2));
                jbRight.addActionListener(e -> jbRightActionPerformed(e));
                jPanel2.add(jbRight);

                //======== jPanel6 ========
                {

                    GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
                    jPanel6.setLayout(jPanel6Layout);
                    jPanel6Layout.setHorizontalGroup(
                        jPanel6Layout.createParallelGroup()
                            .addGap(0, 30, Short.MAX_VALUE)
                    );
                    jPanel6Layout.setVerticalGroup(
                        jPanel6Layout.createParallelGroup()
                            .addGap(0, 27, Short.MAX_VALUE)
                    );
                }
                jPanel2.add(jPanel6);

                //---- jbDown ----
                jbDown.setText("\u25bc");
                jbDown.setMargin(new Insets(2, 2, 2, 2));
                jbDown.addActionListener(e -> jbDownActionPerformed(e));
                jPanel2.add(jbDown);

                //======== jPanel7 ========
                {

                    GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
                    jPanel7.setLayout(jPanel7Layout);
                    jPanel7Layout.setHorizontalGroup(
                        jPanel7Layout.createParallelGroup()
                            .addGap(0, 30, Short.MAX_VALUE)
                    );
                    jPanel7Layout.setVerticalGroup(
                        jPanel7Layout.createParallelGroup()
                            .addGap(0, 27, Short.MAX_VALUE)
                    );
                }
                jPanel2.add(jPanel7);
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
                        .addContainerGap())
            );
        }

        //---- jbImport ----
        jbImport.setText("Import");
        jbImport.addActionListener(e -> jbImportActionPerformed(e));

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
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jbImport)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jbCancel)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jbImport)
                        .addComponent(jbCancel))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private MapImportDisplay mapImportDisplay1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JButton jbUp;
    private JPanel jPanel4;
    private JButton jbLeft;
    private JPanel jPanel5;
    private JButton jbRight;
    private JPanel jPanel6;
    private JButton jbDown;
    private JPanel jPanel7;
    private JButton jbImport;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
