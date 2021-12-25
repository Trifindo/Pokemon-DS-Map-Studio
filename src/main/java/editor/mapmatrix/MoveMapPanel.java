package editor.mapmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import editor.handler.MapEditorHandler;

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
        hSpacer1 = new JPanel(null);
        jbUp = new JButton();
        hSpacer2 = new JPanel(null);
        jbLeft = new JButton();
        hSpacer3 = new JPanel(null);
        jbRight = new JButton();
        hSpacer4 = new JPanel(null);
        jbDown = new JButton();
        hSpacer5 = new JPanel(null);

        //======== this ========
        setMaximumSize(null);
        setMinimumSize(null);
        setPreferredSize(null);
        setLayout(new GridLayout(3, 3));
        add(hSpacer1);

        //---- jbUp ----
        jbUp.setMargin(new Insets(2, 2, 2, 2));
        jbUp.setMaximumSize(null);
        jbUp.setMinimumSize(null);
        jbUp.setPreferredSize(null);
        jbUp.setIcon(new ImageIcon(getClass().getResource("/icons/upIcon.png")));
        jbUp.addActionListener(e -> jbUpActionPerformed(e));
        add(jbUp);
        add(hSpacer2);

        //---- jbLeft ----
        jbLeft.setMargin(new Insets(2, 2, 2, 2));
        jbLeft.setMaximumSize(null);
        jbLeft.setMinimumSize(null);
        jbLeft.setPreferredSize(null);
        jbLeft.setIcon(new ImageIcon(getClass().getResource("/icons/leftIcon.png")));
        jbLeft.addActionListener(e -> jbLeftActionPerformed(e));
        add(jbLeft);
        add(hSpacer3);

        //---- jbRight ----
        jbRight.setMargin(new Insets(2, 2, 2, 2));
        jbRight.setMaximumSize(null);
        jbRight.setMinimumSize(null);
        jbRight.setPreferredSize(null);
        jbRight.setIcon(new ImageIcon(getClass().getResource("/icons/rightIcon.png")));
        jbRight.addActionListener(e -> jbRightActionPerformed(e));
        add(jbRight);
        add(hSpacer4);

        //---- jbDown ----
        jbDown.setMargin(new Insets(2, 2, 2, 2));
        jbDown.setMaximumSize(null);
        jbDown.setMinimumSize(null);
        jbDown.setPreferredSize(null);
        jbDown.setIcon(new ImageIcon(getClass().getResource("/icons/downIcon.png")));
        jbDown.addActionListener(e -> jbDownActionPerformed(e));
        add(jbDown);
        add(hSpacer5);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel hSpacer1;
    private JButton jbUp;
    private JPanel hSpacer2;
    private JButton jbLeft;
    private JPanel hSpacer3;
    private JButton jbRight;
    private JPanel hSpacer4;
    private JButton jbDown;
    private JPanel hSpacer5;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
