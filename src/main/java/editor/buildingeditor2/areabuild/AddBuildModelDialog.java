package editor.buildingeditor2.areabuild;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;

import editor.buildingeditor2.buildmodel.BuildModelList;
import net.miginfocom.swing.*;
import renderer.*;

/**
 * @author Trifindo, JackHack96
 */
public class AddBuildModelDialog extends JDialog {
    public static final int ACEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private int indexSelected = 0;

    private BuildModelList buildings;
    //private AreaBuild areaBuild;
    private ArrayList<Integer> addedBuildings;

    private ImageIcon nsbmdIcon;

    public AddBuildModelDialog(Window owner) {
        super(owner);
        initComponents();
        nsbmdIcon = new ImageIcon(getClass().getResource("/icons/NsbmdIcon.png"));

        addIconToJList(jlBuildModelList, nsbmdIcon);

        nitroDisplayGL.getObjectsGL().add(new ObjectGL());
    }

    private void jlBuildModelListValueChanged(ListSelectionEvent e) {
        indexSelected = jlBuildModelList.getSelectedIndex();
        try {
            byte[] data = buildings.getModelsData().get(indexSelected);
            nitroDisplayGL.getObjectGL().setNsbmdData(data);
            nitroDisplayGL.getObjectGL().setNsbca(null);
            nitroDisplayGL.getObjectGL().setNsbtp(null);
            nitroDisplayGL.getObjectGL().setNsbta(null);
            nitroDisplayGL.getObjectGL().setNsbva(null);
            nitroDisplayGL.requestUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        if (addedBuildings != null) {
            if (!addedBuildings.contains(indexSelected)) {
                returnValue = ACEPTED;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "The building selected is already in the list.",
                        "Can't add building", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            returnValue = ACEPTED;
            dispose();
        }
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCELED;
        dispose();
    }

    public void init(BuildModelList buildings, ArrayList<Integer> addedBuildings) {
        this.buildings = buildings;
        this.addedBuildings = addedBuildings;

        updateViewBuildModelList(0);
    }

    public int getIndexSelected() {
        return indexSelected;
    }

    public int getReturnValue() {
        return returnValue;
    }

    private void updateViewBuildModelList(int indexSelected) {
        addElementsToListWithIndices(jlBuildModelList,
                buildings.getModelsName(),
                indexSelected);

    }

    private static void addElementsToListWithIndices(JList list, ArrayList<String> elements, int indexSelected) {
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < elements.size(); i++) {
            listModel.addElement(String.valueOf(i) + ": " + elements.get(i));
        }
        list.setModel(listModel);

        indexSelected = Math.max(Math.min(list.getModel().getSize() - 1, indexSelected), 0);
        list.setSelectedIndex(indexSelected);
        list.ensureIndexIsVisible(indexSelected);
    }

    private static void addIconToJList(JList list, ImageIcon icon) {
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setIcon(icon);
                return label;
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel12 = new JPanel();
        jLabel11 = new JLabel();
        jScrollPane5 = new JScrollPane();
        jlBuildModelList = new JList<>();
        nitroDisplayGL = new NitroDisplayGL();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Building Model");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[grow,fill]" +
                        "[grow,fill]",
                // rows
                "[grow,fill]" +
                        "[fill]"));

        //======== jPanel12 ========
        {
            jPanel12.setBorder(new TitledBorder("Building Models"));
            jPanel12.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 5 5",
                    // columns
                    "[fill]" +
                            "[grow,fill]",
                    // rows
                    "[fill]" +
                            "[grow,fill]"));

            //---- jLabel11 ----
            jLabel11.setIcon(new ImageIcon(getClass().getResource("/icons/BuildingIcon.png")));
            jLabel11.setText("Building List:");
            jLabel11.setToolTipText("");
            jPanel12.add(jLabel11, "cell 0 0");

            //======== jScrollPane5 ========
            {
                jScrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- jlBuildModelList ----
                jlBuildModelList.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };

                    @Override
                    public int getSize() {
                        return values.length;
                    }

                    @Override
                    public String getElementAt(int i) {
                        return values[i];
                    }
                });
                jlBuildModelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlBuildModelList.addListSelectionListener(e -> jlBuildModelListValueChanged(e));
                jScrollPane5.setViewportView(jlBuildModelList);
            }
            jPanel12.add(jScrollPane5, "cell 0 1");

            //======== nitroDisplayGL ========
            {
                nitroDisplayGL.setBorder(new LineBorder(new Color(102, 102, 102)));

                GroupLayout nitroDisplayGLLayout = new GroupLayout(nitroDisplayGL);
                nitroDisplayGL.setLayout(nitroDisplayGLLayout);
                nitroDisplayGLLayout.setHorizontalGroup(
                        nitroDisplayGLLayout.createParallelGroup()
                                .addGap(0, 343, Short.MAX_VALUE)
                );
                nitroDisplayGLLayout.setVerticalGroup(
                        nitroDisplayGLLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                );
            }
            jPanel12.add(nitroDisplayGL, "cell 1 1");
        }
        contentPane.add(jPanel12, "cell 0 0 2 1");

        //---- jbAccept ----
        jbAccept.setText("OK");
        jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
        contentPane.add(jbAccept, "cell 0 1");

        //---- jbCancel ----
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
        contentPane.add(jbCancel, "cell 1 1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel12;
    private JLabel jLabel11;
    private JScrollPane jScrollPane5;
    private JList<String> jlBuildModelList;
    private NitroDisplayGL nitroDisplayGL;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
