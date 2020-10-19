package editor.mapmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import editor.handler.MapEditorHandler;
import editor.state.MapLayerState;

import java.awt.Point;
import net.miginfocom.swing.*;

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
        jbUp = new JButton();
        jbLeft = new JButton();
        jbRight = new JButton();
        jbDown = new JButton();

        //======== this ========
        setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 2 2",
            // columns
            "[grow,fill]" +
            "[grow,fill]" +
            "[grow,fill]",
            // rows
            "[grow,fill]" +
            "[grow,fill]" +
            "[grow,fill]"));

        //---- jbUp ----
        jbUp.setText("\u25b2");
        jbUp.setMargin(new Insets(2, 2, 2, 2));
        jbUp.addActionListener(e -> jbUpActionPerformed(e));
        add(jbUp, "cell 1 0");

        //---- jbLeft ----
        jbLeft.setText("\u25c4");
        jbLeft.setMargin(new Insets(2, 2, 2, 2));
        jbLeft.addActionListener(e -> jbLeftActionPerformed(e));
        add(jbLeft, "cell 0 1");

        //---- jbRight ----
        jbRight.setText("\u25ba");
        jbRight.setMargin(new Insets(2, 2, 2, 2));
        jbRight.addActionListener(e -> jbRightActionPerformed(e));
        add(jbRight, "cell 2 1");

        //---- jbDown ----
        jbDown.setText("\u25bc");
        jbDown.setMargin(new Insets(2, 2, 2, 2));
        jbDown.addActionListener(e -> jbDownActionPerformed(e));
        add(jbDown, "cell 1 2");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton jbUp;
    private JButton jbLeft;
    private JButton jbRight;
    private JButton jbDown;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
