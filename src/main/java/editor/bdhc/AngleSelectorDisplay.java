package editor.bdhc;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.*;

/**
 * @author Trifindo, JackHack96
 */
public class AngleSelectorDisplay extends JPanel {
    private static final Color gridColor = Color.LIGHT_GRAY;
    private static final Color plateColor = Color.ORANGE;

    private static final int tileSize = 16;

    public AngleSelectorDisplay() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGap(0, 300, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
