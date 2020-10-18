package editor.buildingeditor2.animations;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;

import net.miginfocom.swing.*;
import renderer.*;

/**
 * @author Trifindo, JackHack96
 */
public class AddBuildAnimationDialog extends JDialog {

    public static final int ACEPTED = 0, CANCELED = 1;
    private int returnValue = CANCELED;
    private int indexSelected = 0;

    private byte[] buildModelData;
    private ArrayList<Integer> buildingAnimationIDs;
    private BuildAnimations buildAnimations;

    private ArrayList<Integer> animIconIndices;
    private ArrayList<ImageIcon> animIcons;

    public AddBuildAnimationDialog(Window owner) {
        super(owner);
        initComponents();

        animIcons = new ArrayList<>(4);
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbcaIcon.png")));
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbtaIcon.png")));
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbtpIcon.png")));
        animIcons.add(new ImageIcon(getClass().getResource("/icons/NsbmaIcon.png")));

        animIconIndices = new ArrayList<>();

        jlAnimationsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0 && index < animIconIndices.size()) {
                    int animIndex = animIconIndices.get(index);
                    if (animIndex >= 0 && animIndex < animIcons.size()) {
                        label.setIcon(animIcons.get(animIndex));
                    }
                }
                return label;
            }
        });

        nitroDisplayGL.getObjectsGL().add(new ObjectGL());
    }

    private void jlAnimationsListValueChanged(ListSelectionEvent e) {
        indexSelected = jlAnimationsList.getSelectedIndex();

        try {
            byte[] animData = buildAnimations.getAnimations().get(indexSelected).getData();
            ObjectGL object = nitroDisplayGL.getObjectGL(0);
            object.setNsbmdData(buildModelData);
            object.setNsbca(null);
            object.setNsbta(null);
            object.setNsbtp(null);
            object.setNsbva(null);
            switch (buildAnimations.getAnimationType(indexSelected)) {
                case ModelAnimation.TYPE_NSBCA:
                    object.setNsbcaData(animData);
                    break;
                case ModelAnimation.TYPE_NSBTA:
                    object.setNsbtaData(animData);
                    break;
                case ModelAnimation.TYPE_NSBTP:
                    object.setNsbtpData(animData);
                    break;
                case ModelAnimation.TYPE_NSBMA:
                    //object.setNsbData(animData);
                    break;
                case ModelAnimation.TYPE_NSBVA:
                    object.setNsbvaData(animData);
                    break;
            }

            nitroDisplayGL.requestUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        if (!buildingAnimationIDs.contains(indexSelected)) {
            returnValue = ACEPTED;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "The animation selected is already used by the building.",
                    "Can't add animation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCELED;
        dispose();
    }

    public void init(byte[] buildModelData, BuildAnimations animations, ArrayList<Integer> buildingAnimationIDs) {
        this.buildModelData = buildModelData;
        this.buildAnimations = animations;
        this.buildingAnimationIDs = buildingAnimationIDs;

        updateViewAnimationsList(0);
    }

    private void updateViewAnimationsList(int indexSelected) {
        if (buildAnimations != null) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<ModelAnimation> animations = buildAnimations.getAnimations();
            animIconIndices = new ArrayList<>(animations.size());
            for (int i = 0; i < animations.size(); i++) {
                names.add(String.valueOf(i) + ": "
                        + animations.get(i).getName() + " ["
                        + animations.get(i).getAnimationTypeName() + "]");
                animIconIndices.add(animations.get(i).getAnimationType());
            }
            addElementsToList(jlAnimationsList, names, indexSelected);
        }
    }

    private static void addElementsToList(JList list, ArrayList<String> elements, int indexSelected) {
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < elements.size(); i++) {
            listModel.addElement(elements.get(i));
        }
        list.setModel(listModel);

        indexSelected = Math.max(Math.min(list.getModel().getSize() - 1, indexSelected), 0);
        list.setSelectedIndex(indexSelected);
    }

    public int getIndexSelected() {
        return indexSelected;
    }

    public int getReturnValue() {
        return returnValue;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel12 = new JPanel();
        jLabel11 = new JLabel();
        jScrollPane5 = new JScrollPane();
        jlAnimationsList = new JList<>();
        nitroDisplayGL = new NitroDisplayGL();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Animation");
        setModal(true);
        var contentPane = getContentPane();
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
            jPanel12.setBorder(new TitledBorder("Building Animations (bm_anime.narc)"));
            jPanel12.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 5 5",
                    // columns
                    "[fill]" +
                            "[grow,fill]",
                    // rows
                    "[fill]" +
                            "[grow,fill]"));

            //---- jLabel11 ----
            jLabel11.setIcon(new ImageIcon(getClass().getResource("/icons/AnimationIcon.png")));
            jLabel11.setText("Animations:");
            jLabel11.setToolTipText("");
            jPanel12.add(jLabel11, "cell 0 0");

            //======== jScrollPane5 ========
            {
                jScrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- jlAnimationsList ----
                jlAnimationsList.setModel(new AbstractListModel<String>() {
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
                jlAnimationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlAnimationsList.addListSelectionListener(e -> jlAnimationsListValueChanged(e));
                jScrollPane5.setViewportView(jlAnimationsList);
            }
            jPanel12.add(jScrollPane5, "cell 0 1");

            //======== nitroDisplayGL ========
            {
                nitroDisplayGL.setBorder(new LineBorder(new Color(102, 102, 102)));

                GroupLayout nitroDisplayGLLayout = new GroupLayout(nitroDisplayGL);
                nitroDisplayGL.setLayout(nitroDisplayGLLayout);
                nitroDisplayGLLayout.setHorizontalGroup(
                        nitroDisplayGLLayout.createParallelGroup()
                                .addGap(0, 144, Short.MAX_VALUE)
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
    private JList<String> jlAnimationsList;
    private NitroDisplayGL nitroDisplayGL;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
