
package utils.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * @author Trifindo
 */
public class JCheckboxList extends JList<JCheckBox> {

    protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public JCheckboxList() {
        setCellRenderer(new CellRenderer());
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    int index = locationToIndex(e.getPoint());
                    if (index != -1) {
                        JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                        checkbox.setSelected(!checkbox.isSelected());
                        repaint();
                    }
                }
            }
        });
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JCheckboxList(ListModel<JCheckBox> model) {
        this();
        setModel(model);
    }

    protected class CellRenderer implements ListCellRenderer<JCheckBox> {

        public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index,
                boolean isSelected, boolean cellHasFocus) {

            //Drawing checkbox, change the appearance here
            value.setBackground(isSelected ? getSelectionBackground() : getBackground());
            //checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
            value.setEnabled(isEnabled());
            value.setFont(getFont());
            value.setFocusPainted(false);
            value.setBorderPainted(true);
            value.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
            return value;
        }
    }
}
