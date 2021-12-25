/*
 * Created by JFormDesigner on Fri Nov 13 21:23:06 CET 2020
 */

package formats.collisions.bw;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.handler.MapEditorHandler;
import net.miginfocom.swing.*;

/**
 * @author Truck
 */
public class CollisionsEditorDialogBW extends JDialog {

    protected MapEditorHandler handler;
    private CollisionHandlerBW cHandler;

    public CollisionsEditorDialogBW(Window owner) {
        super(owner);
        initComponents();

        collisionsDisplayEditor.setMode(CollisionsDisplay3D.Mode.Edit);

        final int minFractionDigits = 1, maxFractionDigits = 5;
        setJSpinnerPrecission(jsZ0, minFractionDigits, maxFractionDigits);
        setJSpinnerPrecission(jsZ1, minFractionDigits, maxFractionDigits);
        setJSpinnerPrecission(jsZ2, minFractionDigits, maxFractionDigits);
        setJSpinnerPrecission(jsZ3, minFractionDigits, maxFractionDigits);
    }

    private void jpContainerComponentResized(ComponentEvent e) {
        int size = Math.min(jpContainer.getWidth(), jpContainer.getHeight());
        collisionsDisplayEditor.setPreferredSize(new Dimension(size, size));
        jpContainer.revalidate();
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        cHandler = new CollisionHandlerBW(handler, this);
        collisionsDisplay3D.initHandler(handler, cHandler);
        collisionsDisplayEditor.initHandler(handler, cHandler);
        ctileDisplay3D.init(cHandler);
    }

    public CTileDisplay3D getCtileDisplay3D() {
        return ctileDisplay3D;
    }

    public void updateView() {
        updateViewCollisionDisplays();
        updateViewCTile();
    }

    public void updateViewCTile() {
        jsZ0.setValue(cHandler.getCurrentTile()[0]);
        jsZ1.setValue(cHandler.getCurrentTile()[1]);
        jsZ2.setValue(cHandler.getCurrentTile()[2]);
        jsZ3.setValue(cHandler.getCurrentTile()[3]);
        ctileDisplay3D.repaint();
    }

    public void updateViewCollisionDisplays() {
        collisionsDisplayEditor.repaint();
        collisionsDisplay3D.repaint();
    }

    private void jbRotateActionPerformed(ActionEvent e) {
        cHandler.rotateCurrentTile();
        updateViewCTile();
    }

    private void jbFlipActionPerformed(ActionEvent e) {
        cHandler.flipCurrentTile();
        updateViewCTile();
    }

    private void jsZ0StateChanged(ChangeEvent e) {
        cHandler.getCurrentTile()[0] = (Float) jsZ0.getValue();
        updateViewCTile();
    }

    private void jsZ1StateChanged(ChangeEvent e) {
        cHandler.getCurrentTile()[1] = (Float) jsZ1.getValue();
        updateViewCTile();
    }

    private void jsZ2StateChanged(ChangeEvent e) {
        cHandler.getCurrentTile()[2] = (Float) jsZ2.getValue();
        updateViewCTile();
    }

    private void jsZ3StateChanged(ChangeEvent e) {
        cHandler.getCurrentTile()[3] = (Float) jsZ3.getValue();
        updateViewCTile();
    }

    private void jbMoveUpActionPerformed(ActionEvent e) {
        cHandler.moveCurrentTile(1.0f);
        updateViewCTile();
    }

    private void jbMoveDownActionPerformed(ActionEvent e) {
        cHandler.moveCurrentTile(-1.0f);
        updateViewCTile();
    }

    private void jcbViewMapActionPerformed(ActionEvent e) {
        collisionsDisplay3D.setMapEnabled(jcbViewMap.isSelected());
        collisionsDisplay3D.repaint();
    }

    private void jcbXRayActionPerformed(ActionEvent e) {
        collisionsDisplay3D.setxRayEnabled(jcbXRay.isSelected());
        collisionsDisplay3D.repaint();
    }

    private void jcbWireframeActionPerformed(ActionEvent e) {
        collisionsDisplay3D.setWireframeEnabled(jcbWireframe.isSelected());
        collisionsDisplay3D.repaint();
    }

    /*
    private void jcbTransparentActionPerformed(ActionEvent e) {
        collisionsDisplay3D.setTransparentEnabled(jcbTransparent.isSelected());
        collisionsDisplay3D.repaint();
    }*/

    private void setJSpinnerPrecission(JSpinner spinner, int minFractionDigits, int maxFractionDigits) {
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(minFractionDigits);
        format.setMaximumFractionDigits(maxFractionDigits);
    }

    private void jbImportPerActionPerformed(ActionEvent e) {
        openCollisionsWithDialog();
    }

    private void jbExportPerActionPerformed(ActionEvent e) {
        saveCollisionsWithDialog();
    }

    private void jcbCollisionFileActionPerformed(ActionEvent e) {
        cHandler.setSelectedCollisionFile(jcbCollisionFile.getSelectedIndex());
        updateView();
    }

    private void jsPlatesOpacityStateChanged(ChangeEvent e) {
        float value = jsPlatesOpacity.getValue() / 100.0f;
        collisionsDisplay3D.setPlatesAlpha(value);
        collisionsDisplayEditor.setPlatesAlpha(value);
        collisionsDisplay3D.repaint();
        collisionsDisplayEditor.repaint();
    }

    private void thisWindowClosed(WindowEvent e) {
        try {
            cHandler.saveToCollision();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "There was a problem generating the PER files",
                    "Collisions not changed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCollisionsWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastCollisionsDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastCollisionsDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Terrain File (*.per)", CollisionsBW3D.fileExtension));
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save Permissions File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                handler.setLastCollisionsDirectoryUsed(fc.getSelectedFile().getParent());

                cHandler.getCollisionsBW3D().saveToFile(path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "There was a problem saving the PER file",
                        "Error saving PER", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openCollisionsWithDialog(){
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastCollisionsDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastCollisionsDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Terrain File (*.per)", CollisionsBW3D.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Permissions File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                handler.setLastCollisionsDirectoryUsed(fc.getSelectedFile().getParent());

                cHandler.setCollisionsBW3D(new CollisionsBW3D(path, handler.getGameIndex()));

                updateView();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "There was a problem opening the file",
                        "Error opening PER file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JLabel getJlInfo() {
        return jlInfo;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel10 = new JPanel();
        jbImportPer = new JButton();
        jbExportPer = new JButton();
        label4 = new JLabel();
        panel12 = new JPanel();
        label5 = new JLabel();
        jcbCollisionFile = new JComboBox<>();
        splitPane1 = new JSplitPane();
        panel6 = new JPanel();
        collisionsDisplay3D = new CollisionsDisplay3D();
        panel7 = new JPanel();
        jcbViewMap = new JCheckBox();
        jcbXRay = new JCheckBox();
        jcbWireframe = new JCheckBox();
        label7 = new JLabel();
        jsPlatesOpacity = new JSlider();
        jpContainer = new JPanel();
        collisionsDisplayEditor = new CollisionsDisplay3D();
        panel2 = new JPanel();
        panel5 = new JPanel();
        panel4 = new JPanel();
        ctileDisplay3D = new CTileDisplay3D();
        panel8 = new JPanel();
        panel3 = new JPanel();
        jsZ0 = new JSpinner();
        jsZ1 = new JSpinner();
        jsZ3 = new JSpinner();
        jsZ2 = new JSpinner();
        panel9 = new JPanel();
        panel1 = new JPanel();
        label2 = new JLabel();
        jbMoveUp = new JButton();
        label1 = new JLabel();
        jbRotate = new JButton();
        jbMoveDown = new JButton();
        label3 = new JLabel();
        jbFlip = new JButton();
        panel11 = new JPanel();
        textArea1 = new JTextArea();
        jlInfo = new JLabel();
        panel13 = new JPanel();
        label6 = new JLabel();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Terrain Editor BW/BW2");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                thisWindowClosed(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[fill]0" +
            "[fill]0" +
            "[grow,fill]0" +
            "[240:261,grow,fill]",
            // rows
            "[]" +
            "[grow,fill]0" +
            "[]0" +
            "[]" +
            "[]"));

        //======== panel10 ========
        {
            panel10.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[23]"));

            //---- jbImportPer ----
            jbImportPer.setText("Import PER");
            jbImportPer.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
            jbImportPer.addActionListener(e -> jbImportPerActionPerformed(e));
            panel10.add(jbImportPer, "cell 0 0");

            //---- jbExportPer ----
            jbExportPer.setText("Export PER");
            jbExportPer.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
            jbExportPer.addActionListener(e -> jbExportPerActionPerformed(e));
            panel10.add(jbExportPer, "cell 1 0");

            //---- label4 ----
            label4.setText("PER files are saved automatically when pressing the save map button from the main window");
            label4.setIcon(new ImageIcon(getClass().getResource("/icons/informationIcon.png")));
            panel10.add(label4, "cell 3 0");
        }
        contentPane.add(panel10, "cell 1 0");

        //======== panel12 ========
        {
            panel12.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[fill]",
                // rows
                "[]"));

            //---- label5 ----
            label5.setText("Collision File:");
            panel12.add(label5, "cell 0 0");

            //---- jcbCollisionFile ----
            jcbCollisionFile.setModel(new DefaultComboBoxModel<>(new String[] {
                "File 1",
                "File 2"
            }));
            jcbCollisionFile.addActionListener(e -> jcbCollisionFileActionPerformed(e));
            panel12.add(jcbCollisionFile, "cell 1 0");
        }
        contentPane.add(panel12, "cell 3 0,alignx right,growx 0");

        //======== splitPane1 ========
        {
            splitPane1.setResizeWeight(0.5);
            splitPane1.setDividerLocation(400);

            //======== panel6 ========
            {
                panel6.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "0[grow,fill]0",
                    // rows
                    "0[grow,fill]" +
                    "[]0" +
                    "[]"));
                panel6.add(collisionsDisplay3D, "cell 0 0");

                //======== panel7 ========
                {
                    panel7.setBorder(new TitledBorder("View Settings"));
                    panel7.setLayout(new MigLayout(
                        "insets 5,hidemode 3,gap 5 5",
                        // columns
                        "[171,grow,fill]" +
                        "[grow,fill]",
                        // rows
                        "[grow,fill]" +
                        "[grow,fill]" +
                        "[]"));

                    //---- jcbViewMap ----
                    jcbViewMap.setText("View Map");
                    jcbViewMap.setSelected(true);
                    jcbViewMap.addActionListener(e -> jcbViewMapActionPerformed(e));
                    panel7.add(jcbViewMap, "cell 0 0");

                    //---- jcbXRay ----
                    jcbXRay.setText("X Ray Plates");
                    jcbXRay.setSelected(true);
                    jcbXRay.addActionListener(e -> jcbXRayActionPerformed(e));
                    panel7.add(jcbXRay, "cell 1 0");

                    //---- jcbWireframe ----
                    jcbWireframe.setText("Wireframe Plates");
                    jcbWireframe.setSelected(true);
                    jcbWireframe.addActionListener(e -> jcbWireframeActionPerformed(e));
                    panel7.add(jcbWireframe, "cell 0 1");

                    //---- label7 ----
                    label7.setText("Plates Opacity:");
                    panel7.add(label7, "cell 1 1");

                    //---- jsPlatesOpacity ----
                    jsPlatesOpacity.setValue(85);
                    jsPlatesOpacity.setPreferredSize(new Dimension(100, 24));
                    jsPlatesOpacity.addChangeListener(e -> jsPlatesOpacityStateChanged(e));
                    panel7.add(jsPlatesOpacity, "cell 1 1");
                }
                panel6.add(panel7, "cell 0 1,gapx 5 5,gapy 5 5");
            }
            splitPane1.setLeftComponent(panel6);

            //======== jpContainer ========
            {
                jpContainer.setBorder(new LineBorder(Color.lightGray));
                jpContainer.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jpContainerComponentResized(e);
                    }
                });
                jpContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                jpContainer.add(collisionsDisplayEditor);
            }
            splitPane1.setRightComponent(jpContainer);
        }
        contentPane.add(splitPane1, "cell 1 1");

        //======== panel2 ========
        {
            panel2.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //======== panel5 ========
            {
                panel5.setLayout(new MigLayout(
                    "insets 0,hidemode 3,gap 0 0",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[224:n,fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[grow,fill]"));

                //======== panel4 ========
                {
                    panel4.setBorder(new TitledBorder("Selected Tile"));
                    panel4.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {141, 0};
                    ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {177, 0};
                    ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //---- ctileDisplay3D ----
                    ctileDisplay3D.setBorder(new LineBorder(Color.lightGray));
                    panel4.add(ctileDisplay3D, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
                }
                panel5.add(panel4, "cell 0 0");

                //======== panel8 ========
                {
                    panel8.setBorder(new TitledBorder("Z Coordinates"));
                    panel8.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel8.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)panel8.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel8.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)panel8.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== panel3 ========
                    {
                        panel3.setLayout(new GridLayout(2, 2, 5, 5));

                        //---- jsZ0 ----
                        jsZ0.setModel(new SpinnerNumberModel(0.0F, null, null, 1.0F));
                        jsZ0.addChangeListener(e -> jsZ0StateChanged(e));
                        panel3.add(jsZ0);

                        //---- jsZ1 ----
                        jsZ1.setModel(new SpinnerNumberModel(0.0F, null, null, 1.0F));
                        jsZ1.addChangeListener(e -> jsZ1StateChanged(e));
                        panel3.add(jsZ1);

                        //---- jsZ3 ----
                        jsZ3.setModel(new SpinnerNumberModel(0.0F, null, null, 1.0F));
                        jsZ3.addChangeListener(e -> jsZ3StateChanged(e));
                        panel3.add(jsZ3);

                        //---- jsZ2 ----
                        jsZ2.setModel(new SpinnerNumberModel(0.0F, null, null, 1.0F));
                        jsZ2.addChangeListener(e -> jsZ2StateChanged(e));
                        panel3.add(jsZ2);
                    }
                    panel8.add(panel3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
                }
                panel5.add(panel8, "cell 0 1");

                //======== panel9 ========
                {
                    panel9.setBorder(new TitledBorder("Tools"));
                    panel9.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel9.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)panel9.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel9.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== panel1 ========
                    {
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                        //---- label2 ----
                        label2.setText("Move:");
                        panel1.add(label2, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- jbMoveUp ----
                        jbMoveUp.setText("\u25b2");
                        jbMoveUp.addActionListener(e -> jbMoveUpActionPerformed(e));
                        panel1.add(jbMoveUp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- label1 ----
                        label1.setText("Rotate:");
                        panel1.add(label1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- jbRotate ----
                        jbRotate.setText("\u21ba");
                        jbRotate.addActionListener(e -> jbRotateActionPerformed(e));
                        panel1.add(jbRotate, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- jbMoveDown ----
                        jbMoveDown.setText("\u25bc");
                        jbMoveDown.addActionListener(e -> jbMoveDownActionPerformed(e));
                        panel1.add(jbMoveDown, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- label3 ----
                        label3.setText("Flip:");
                        panel1.add(label3, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- jbFlip ----
                        jbFlip.setText("\u21c4");
                        jbFlip.addActionListener(e -> jbFlipActionPerformed(e));
                        panel1.add(jbFlip, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel9.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
                }
                panel5.add(panel9, "cell 0 2");

                //======== panel11 ========
                {
                    panel11.setBorder(new TitledBorder("Information"));
                    panel11.setLayout(new MigLayout(
                        "hidemode 3",
                        // columns
                        "[grow,fill]",
                        // rows
                        "[]"));

                    //---- textArea1 ----
                    textArea1.setText("Some slopes and Z coordinates might not be valid. Those will be approximated with the closest plate available.");
                    textArea1.setLineWrap(true);
                    textArea1.setEditable(false);
                    textArea1.setWrapStyleWord(true);
                    panel11.add(textArea1, "cell 0 0");
                }
                panel5.add(panel11, "cell 0 3");
                panel5.add(jlInfo, "cell 0 4");
            }
            panel2.add(panel5, "cell 0 0");
        }
        contentPane.add(panel2, "cell 3 1");

        //======== panel13 ========
        {
            panel13.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]",
                // rows
                "[]"));

            //---- label6 ----
            label6.setText("Warning: SDSME doesn't import/export PER files from BW/BW2 correctly. These files have to be replaced manually or by using a different tool");
            label6.setIcon(new ImageIcon(getClass().getResource("/icons/WarningIcon.png")));
            panel13.add(label6, "cell 0 0");
        }
        contentPane.add(panel13, "cell 1 4");
        setSize(1230, 685);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel10;
    private JButton jbImportPer;
    private JButton jbExportPer;
    private JLabel label4;
    private JPanel panel12;
    private JLabel label5;
    private JComboBox<String> jcbCollisionFile;
    private JSplitPane splitPane1;
    private JPanel panel6;
    private CollisionsDisplay3D collisionsDisplay3D;
    private JPanel panel7;
    private JCheckBox jcbViewMap;
    private JCheckBox jcbXRay;
    private JCheckBox jcbWireframe;
    private JLabel label7;
    private JSlider jsPlatesOpacity;
    private JPanel jpContainer;
    private CollisionsDisplay3D collisionsDisplayEditor;
    private JPanel panel2;
    private JPanel panel5;
    private JPanel panel4;
    private CTileDisplay3D ctileDisplay3D;
    private JPanel panel8;
    private JPanel panel3;
    private JSpinner jsZ0;
    private JSpinner jsZ1;
    private JSpinner jsZ3;
    private JSpinner jsZ2;
    private JPanel panel9;
    private JPanel panel1;
    private JLabel label2;
    private JButton jbMoveUp;
    private JLabel label1;
    private JButton jbRotate;
    private JButton jbMoveDown;
    private JLabel label3;
    private JButton jbFlip;
    private JPanel panel11;
    private JTextArea textArea1;
    private JLabel jlInfo;
    private JPanel panel13;
    private JLabel label6;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
