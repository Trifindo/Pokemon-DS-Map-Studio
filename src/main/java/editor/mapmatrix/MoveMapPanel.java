package editor.mapmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import editor.handler.MapEditorHandler;
import editor.state.MapLayerState;

import java.awt.Point;

/**
 * @author Trifindo, JackHack96
 */
public class MoveMapPanel extends JPanel {

    private MapEditorHandler handler;

    public MoveMapPanel() {
        initComponents();
    }

    private void jbUpActionPerformed(ActionEvent e) {
        moveMap(handler.getMapSelected(), new Point(0, -1));
    }

    private void jbLeftActionPerformed(ActionEvent e) {
        moveMap(handler.getMapSelected(), new Point(-1, 0));
    }

    private void jbRightActionPerformed(ActionEvent e) {
        moveMap(handler.getMapSelected(), new Point(1, 0));
    }

    private void jbDownActionPerformed(ActionEvent e) {
        moveMap(handler.getMapSelected(), new Point(0, 1));
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
    }

    public void moveMap(Point p, Point dp) {
        //handler.setLayerChanged(false);
        //handler.addMapState(new MapLayerState("Map moved", handler));

        Point newCoords = new Point(p.x + dp.x, p.y + dp.y);
        handler.getMapMatrix().moveMap(p, newCoords);

        handler.setMapSelected(newCoords);
        //handler.getMainFrame().getMapDisplay().setCameraAtSelectedMap();
        handler.getMainFrame().getMapDisplay().repaint();

        handler.getMapMatrix().updateBordersData();
        handler.getMainFrame().updateMapMatrixDisplay();


    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jbUp = new JButton();
        jPanel2 = new JPanel();
        jbLeft = new JButton();
        jPanel3 = new JPanel();
        jbRight = new JButton();
        jPanel5 = new JPanel();
        jbDown = new JButton();
        jPanel6 = new JPanel();

        //======== this ========
        setLayout(new GridLayout(3, 3, 2, 2));

        //======== jPanel1 ========
        {

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGap(0, 24, Short.MAX_VALUE)
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGap(0, 23, Short.MAX_VALUE)
            );
        }
        add(jPanel1);

        //---- jbUp ----
        jbUp.setText("\u25b2");
        jbUp.setMargin(new Insets(2, 2, 2, 2));
        jbUp.addActionListener(e -> jbUpActionPerformed(e));
        add(jbUp);

        //======== jPanel2 ========
        {

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGap(0, 24, Short.MAX_VALUE)
            );
            jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                            .addGap(0, 23, Short.MAX_VALUE)
            );
        }
        add(jPanel2);

        //---- jbLeft ----
        jbLeft.setText("\u25c4");
        jbLeft.setMargin(new Insets(2, 2, 2, 2));
        jbLeft.addActionListener(e -> jbLeftActionPerformed(e));
        add(jbLeft);

        //======== jPanel3 ========
        {

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup()
                            .addGap(0, 24, Short.MAX_VALUE)
            );
            jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup()
                            .addGap(0, 23, Short.MAX_VALUE)
            );
        }
        add(jPanel3);

        //---- jbRight ----
        jbRight.setText("\u25ba");
        jbRight.setMargin(new Insets(2, 2, 2, 2));
        jbRight.addActionListener(e -> jbRightActionPerformed(e));
        add(jbRight);

        //======== jPanel5 ========
        {

            GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
            jPanel5.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(
                    jPanel5Layout.createParallelGroup()
                            .addGap(0, 24, Short.MAX_VALUE)
            );
            jPanel5Layout.setVerticalGroup(
                    jPanel5Layout.createParallelGroup()
                            .addGap(0, 23, Short.MAX_VALUE)
            );
        }
        add(jPanel5);

        //---- jbDown ----
        jbDown.setText("\u25bc");
        jbDown.setMargin(new Insets(2, 2, 2, 2));
        jbDown.addActionListener(e -> jbDownActionPerformed(e));
        add(jbDown);

        //======== jPanel6 ========
        {

            GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
            jPanel6.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(
                    jPanel6Layout.createParallelGroup()
                            .addGap(0, 24, Short.MAX_VALUE)
            );
            jPanel6Layout.setVerticalGroup(
                    jPanel6Layout.createParallelGroup()
                            .addGap(0, 23, Short.MAX_VALUE)
            );
        }
        add(jPanel6);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JButton jbUp;
    private JPanel jPanel2;
    private JButton jbLeft;
    private JPanel jPanel3;
    private JButton jbRight;
    private JPanel jPanel5;
    private JButton jbDown;
    private JPanel jPanel6;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
