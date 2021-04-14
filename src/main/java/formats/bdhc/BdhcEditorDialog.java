package formats.bdhc;

import editor.game.Game;
import editor.handler.MapEditorHandler;
import net.miginfocom.swing.*;
import utils.Utils;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Trifindo, JackHack96
 */
public class BdhcEditorDialog extends JDialog {
    private MapEditorHandler handler;
    private BdhcHandler bdhcHandler;

    private boolean plateListEnabled = true;
    private MutableBoolean jtfCoordZEnabled = new MutableBoolean(true);
    private MutableBoolean jtfAngleXEnabled = new MutableBoolean(true);
    private MutableBoolean jtfAngleYEnabled = new MutableBoolean(true);
    private boolean jcbTypeEnabled = true;
    private boolean jsCoordZEnabled = true;

    private static final Color redColor = new Color(255, 185, 185);
    private static final Color greenColor = new Color(200, 250, 200);
    private static final Color whiteColor = UIManager.getColor("TextPane.background");

    public BdhcEditorDialog(Window owner) {
        super(owner);
        initComponents();

        //addListenerToJTextField(jtfCoordZ, jtfCoordZEnabled);
        addListenerToJTextField(jtfAngleX, jtfAngleXEnabled);
        addListenerToJTextField(jtfAngleY, jtfAngleYEnabled);

        //jsCoordZ.setEditor(new JSpinner.NumberEditor(jsCoordZ, "#########.######"));
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)jsCoordZ.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(5);
        //setSize(new Dimension(1160, 650));
    }

    private void plateListValueChanged(ListSelectionEvent e) {
        if (plateListEnabled) {
            bdhcHandler.setSelectedPlate(plateList.getSelectedIndex());
            updateView();
            bdhcDisplay.repaint();
        }
    }

    private void jbAddPlateActionPerformed(ActionEvent e) {
        bdhcHandler.addPlate();
        bdhcHandler.setSelectedPlate(bdhcHandler.getPlates().size() - 1);

        updateViewPlateNames();
        updateViewBdhcDisplay3D();
        repaint();
    }

    private void jbRemovePlateActionPerformed(ActionEvent e) {
        if (bdhcHandler.getPlates().size() > 1) {
            bdhcHandler.removeSelectedPlate();

            updateViewPlateNames();
            updateViewBdhcDisplay3D();
            repaint();
        }
    }

    private void jbDuplicatePlateActionPerformed(ActionEvent e) {
        if (bdhcHandler.getPlates().size() > 0) {
            bdhcHandler.duplicateSelectedPlate();

            updateViewPlateNames();
            updateViewBdhcDisplay3D();
            repaint();
        }
    }

    /*
    private void jtfCoordZFocusGained(FocusEvent e) {
        jtfCoordZ.selectAll();
    }*/

    /*
    private void jbCoordinateZActionPerformed(ActionEvent e) {
        changeCoordZ();
    }*/

    private void jcbTypeActionPerformed(ActionEvent e) {
        if (jcbTypeEnabled) {
            bdhcHandler.getSelectedPlate().type = jcbType.getSelectedIndex();
            bdhcDisplay.repaint();
            updateViewSlopes();
            updateViewAngles();
            updateViewBdhcDisplay3D();
        }
    }

    private void angleDisplay1MousePressed(MouseEvent e) {
        Plate plate = bdhcHandler.getSelectedPlate();

        final AngleCalculatorDialog dialog = new AngleCalculatorDialog(handler.getMainFrame(), plate.getAngleDegreesX(), plate.width);
        dialog.setLocationRelativeTo(handler.getMainFrame());
        dialog.setVisible(true);

        if (dialog.getReturnValue() == AngleCalculatorDialog.ACCEPTED) {
            float angle = dialog.getAngle();
            float max = 80.0f;
            float min = -80.0f;
            if (angle > max) {
                angle = max;
            } else if (angle < min) {
                angle = min;
            }

            jtfAngleXEnabled.value = false;
            jtfAngleX.setText(String.valueOf(angle));
            jtfAngleXEnabled.value = true;

            if (angle != 0.0f) {
                jtfAngleYEnabled.value = false;
                jtfAngleY.setText(String.valueOf(0.0f));
                jtfAngleYEnabled.value = true;
            }

            bdhcHandler.getSelectedPlate().setAngleX((float) ((angle / 180.0f) * Math.PI));

            updateViewAngles();
            updateViewSlopes();
            updateViewType();
            bdhcDisplay.repaint();
            updateViewBdhcDisplay3D();
        }
    }

    private void angleDisplay2MousePressed(MouseEvent e) {
        Plate plate = bdhcHandler.getSelectedPlate();

        final AngleCalculatorDialog dialog = new AngleCalculatorDialog(handler.getMainFrame(),
                plate.getAngleDegreesY(), plate.height);
        dialog.setLocationRelativeTo(handler.getMainFrame());
        dialog.setVisible(true);

        if (dialog.getReturnValue() == AngleCalculatorDialog.ACCEPTED) {
            float angle = dialog.getAngle();
            float max = 80.0f;
            float min = -80.0f;
            if (angle > max) {
                angle = max;
            } else if (angle < min) {
                angle = min;
            }

            jtfAngleYEnabled.value = false;
            jtfAngleY.setText(String.valueOf(angle));
            jtfAngleYEnabled.value = true;

            if (angle != 0.0f) {
                jtfAngleXEnabled.value = false;
                jtfAngleX.setText(String.valueOf(0.0f));
                jtfAngleXEnabled.value = true;
            }

            bdhcHandler.getSelectedPlate().setAngleY((float) ((angle / 180.0f) * Math.PI));

            updateViewAngles();
            updateViewSlopes();
            updateViewType();
            bdhcDisplay.repaint();
            updateViewBdhcDisplay3D();
        }
    }

    private void jtfAngleXFocusGained(FocusEvent e) {
        jtfAngleXEnabled.value = false;
        jtfAngleX.setText(removeCharAtEnd(jtfAngleX.getText(), "º"));
        jtfAngleX.selectAll();
        jtfAngleXEnabled.value = true;
    }

    private void jtfAngleXFocusLost(FocusEvent e) {
        jtfAngleXEnabled.value = false;
        jtfAngleX.setText(addCharAtEnd(jtfAngleX.getText(), "º"));
        jtfAngleXEnabled.value = true;
    }

    private void jtfAngleYFocusGained(FocusEvent e) {
        jtfAngleYEnabled.value = false;
        jtfAngleY.setText(removeCharAtEnd(jtfAngleY.getText(), "º"));
        jtfAngleY.selectAll();
        jtfAngleYEnabled.value = true;
    }

    private void jtfAngleYFocusLost(FocusEvent e) {
        jtfAngleYEnabled.value = false;
        jtfAngleY.setText(addCharAtEnd(jtfAngleY.getText(), "º"));
        jtfAngleYEnabled.value = true;
    }

    private void jbApplyAngleXActionPerformed(ActionEvent e) {
        float value;
        try {
            value = Float.parseFloat(removeCharAtEnd(jtfAngleX.getText().replace(',', '.'), "º"));
            float max = 80.0f;
            float min = -80.0f;
            if (value > max) {
                value = max;
            } else if (value < min) {
                value = min;
            }
        } catch (NumberFormatException ex) {
            value = 0.0f;
        }
        jtfAngleXEnabled.value = false;
        jtfAngleX.setText(String.valueOf(value));
        jtfAngleX.setBackground(greenColor);
        jtfAngleX.setForeground(Color.black);
        jtfAngleXEnabled.value = true;

        if (value != 0.0f) {
            jtfAngleYEnabled.value = false;
            jtfAngleY.setText(String.valueOf(0.0f));
            jtfAngleY.setBackground(greenColor);
            jtfAngleY.setForeground(Color.black);
            jtfAngleYEnabled.value = true;
        }

        bdhcHandler.getSelectedPlate().setAngleX((float) ((value / 180.0f) * Math.PI));

        updateViewAngles();
        updateViewSlopes();
        updateViewType();
        bdhcDisplay.repaint();
        updateViewBdhcDisplay3D();
    }

    private void jbApplyAngleYActionPerformed(ActionEvent e) {
        float value;
        try {
            value = Float.parseFloat(removeCharAtEnd(jtfAngleY.getText().replace(',', '.'), "º"));
            float max = 80.0f;
            float min = -80.0f;
            if (value > max) {
                value = max;
            } else if (value < min) {
                value = min;
            }
        } catch (NumberFormatException ex) {
            value = 0.0f;
        }
        jtfAngleYEnabled.value = false;
        jtfAngleY.setText(String.valueOf(value));
        jtfAngleY.setBackground(greenColor);
        jtfAngleY.setForeground(Color.black);
        jtfAngleYEnabled.value = true;

        if (value != 0.0f) {
            jtfAngleXEnabled.value = false;
            jtfAngleX.setText(String.valueOf(0.0f));
            jtfAngleX.setBackground(greenColor);
            jtfAngleX.setForeground(Color.black);
            jtfAngleXEnabled.value = true;
        }

        bdhcHandler.getSelectedPlate().setAngleY((float) ((value / 180.0f) * Math.PI));

        updateViewAngles();
        updateViewSlopes();
        updateViewType();
        bdhcDisplay.repaint();
        updateViewBdhcDisplay3D();
    }

    private void jButton1ActionPerformed(ActionEvent e) {
        saveBdhcWithDialog();
    }

    private void jButton2ActionPerformed(ActionEvent e) {
        openBdhcWithDialog();
    }

    private void jcbViewMapActionPerformed(ActionEvent e) {
        bdhcDisplay3D.setMapEnabled(jcbViewMap.isSelected());
        bdhcDisplay3D.repaint();
    }

    private void jcbXRayPlatesActionPerformed(ActionEvent e) {
        bdhcDisplay3D.setxRayEnabled(jcbXRayPlates.isSelected());
        bdhcDisplay3D.repaint();
    }

    private void jcbWireframePlatesActionPerformed(ActionEvent e) {
        bdhcDisplay3D.setWireframeEnabled(jcbWireframePlates.isSelected());
        bdhcDisplay3D.repaint();
    }

    private void jcbTransparentPlatesActionPerformed(ActionEvent e) {
        bdhcDisplay3D.setTransparentEnabled(jcbTransparentPlates.isSelected());
        bdhcDisplay3D.repaint();
    }

    private void jsCoordZStateChanged(ChangeEvent e) {
        if(jsCoordZEnabled){
            bdhcHandler.getSelectedPlate().z = (Float)jsCoordZ.getValue();
            updateViewBdhcDisplay3D();
            bdhcDisplay.repaint();
        }
    }

    private void displayContainerComponentResized(ComponentEvent e) {
        int size = Math.min(displayContainer.getWidth(), displayContainer.getHeight());
        bdhcDisplay.setPreferredSize(new Dimension(size, size));
        displayContainer.revalidate();
    }

    public void init(MapEditorHandler handler, BufferedImage mapImage) {
        this.handler = handler;
        this.bdhcHandler = new BdhcHandler(this);
        bdhcHandler.init(handler);
        bdhcDisplay.init(handler, mapImage, bdhcHandler);
        angleDisplay1.init(bdhcHandler, 0, 1, true);
        angleDisplay2.init(bdhcHandler, 2, 1, false);
        bdhcDisplay3D.setHandler(handler, bdhcHandler);

        updateView();
    }

    public void openBdhcWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBdhcDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBdhcDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Terrain File (*.bdhc)", Bdhc.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                handler.setLastBdhcDirectoryUsed(fc.getSelectedFile().getParent());

                int game = handler.getGameIndex();
                if (game == Game.DIAMOND || game == Game.PEARL) {
                    handler.setBdhc(new BdhcLoaderDP().loadBdhcFromFile(path));
                } else {
                    handler.setBdhc(new BdhcLoaderHGSS().loadBdhcFromFile(path));
                }

                bdhcHandler.setSelectedPlate(0);
                updateView();
                bdhcDisplay.repaint();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file", "Error opening BDHC file", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void saveBdhcWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBdhcDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBdhcDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Terrain File (*.bdhc)", Bdhc.fileExtension));
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                handler.setLastBdhcDirectoryUsed(fc.getSelectedFile().getParent());
                path = Utils.addExtensionToPath(path, Bdhc.fileExtension);

                int game = handler.getGameIndex();
                if (game == Game.DIAMOND || game == Game.PEARL) {
                    BdhcWriterDP.writeBdhc(bdhcHandler.getBdhc(), path);
                } else {
                    BdhcWriterHGSS.writeBdhc(bdhcHandler.getBdhc(), path);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't save file", "Error saving BDHC", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void updateView() {
        Plate p = bdhcHandler.getSelectedPlate();
        //updateViewJTextField(jtfCoordZ, p.z, jtfCoordZEnabled);
        updateViewJSCoordZ();
        updateViewPlateNames();
        updateViewType();
        updateViewSlopes();
        updateViewAngles();

        updateViewBdhcDisplay3D();
    }

    public void updateViewJSCoordZ(){
        jsCoordZEnabled = false;
        jsCoordZ.setValue(bdhcHandler.getSelectedPlate().z);
        jsCoordZEnabled = true;
    }

    public void updateViewBdhcDisplay3D(){
        bdhcDisplay3D.updatePlateCoords();
        bdhcDisplay3D.repaint();
    }

    public void updateViewAngles() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(5);
        jtfAngleXEnabled.value = false;
        jtfAngleX.setText(df.format(bdhcHandler.getSelectedPlate().getAngleDegreesX()) + "º");
        jtfAngleXEnabled.value = true;
        jtfAngleYEnabled.value = false;
        jtfAngleY.setText(df.format(bdhcHandler.getSelectedPlate().getAngleDegreesY()) + "º");
        jtfAngleYEnabled.value = true;
    }

    public String removeCharAtEnd(String s, String c) {
        if (s.endsWith(c)) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public String addCharAtEnd(String s, String c) {
        if (!s.endsWith(c)) {
            return s + c;
        }
        return s;
    }

    public void updateViewSlopes() {
        //jtfSlopeX.setText(String.valueOf(bdhcHandler.getSelectedPlate().getSlope()[0]));
        //jtfSlopeY.setText(String.valueOf(bdhcHandler.getSelectedPlate().getSlope()[2]));
        //jtfSlopeZ.setText(String.valueOf(bdhcHandler.getSelectedPlate().getSlope()[1]));

        angleDisplay1.repaint();
        angleDisplay2.repaint();
    }

    public void updateViewType() {
        jcbTypeEnabled = false;
        jcbType.setSelectedIndex(bdhcHandler.getSelectedPlate().type);
        jcbTypeEnabled = true;
    }

    private void updateViewJTextField(JTextField jtf, float value, MutableBoolean enabled) {
        enabled.value = false;
        jtf.setText(String.valueOf(value));
        jtf.setBackground(whiteColor);
        jtf.setForeground(UIManager.getColor("TextPane.foreground"));
        enabled.value = true;
    }

    private void updateViewPlateNames() {
        plateListEnabled = false;
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < bdhcHandler.getPlates().size(); i++) {
            String name = "Plate " + i;
            demoList.addElement(name);
        }
        plateList.setModel(demoList);
        plateList.setSelectedIndex(bdhcHandler.getSelectedPlateIndex());
        plateListEnabled = true;
    }

    /*
    public void changeCoordZ() {
        bdhcHandler.getSelectedPlate().z = getValueFromJTextField(
                jtfCoordZ, bdhcHandler.getSelectedPlate().z, jtfCoordZEnabled);
    }*/

    public int getValueFromJTextField(JTextField jtf, int defaultValue, MutableBoolean enabled) {
        int value;
        try {
            value = Integer.valueOf(jtf.getText());
            int max = 4096;
            int min = -4096;
            if (value > max) {
                value = max;
            } else if (value < min) {
                value = min;
            }
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        jtf.setText(String.valueOf(value));
        enabled.value = false;
        jtf.setBackground(greenColor);
        jtf.setForeground(Color.black);
        enabled.value = true;

        return value;
    }

    public float getValueFromJTextField(JTextField jtf, float defaultValue, MutableBoolean enabled) {
        float value;
        try {
            value = Float.valueOf(jtf.getText());
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        jtf.setText(String.valueOf(value));
        enabled.value = false;
        jtf.setBackground(greenColor);
        jtf.setForeground(Color.black);
        enabled.value = true;

        return value;
    }

    private void addListenerToJTextField(JTextField jtf, MutableBoolean enabled) {
        jtf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeBackground();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeBackground();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeBackground();
            }

            public void changeBackground() {
                if (enabled.value) {
                    jtf.setBackground(redColor);
                    jtf.setForeground(Color.black);
                }
            }
        });
    }



    private class MutableBoolean {

        public boolean value;

        public MutableBoolean(boolean value) {
            this.value = value;
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel4 = new JPanel();
        jButton2 = new JButton();
        jButton1 = new JButton();
        label1 = new JLabel();
        splitPane1 = new JSplitPane();
        panel3 = new JPanel();
        bdhcDisplay3D = new BdhcDisplay3D();
        panel5 = new JPanel();
        jcbViewMap = new JCheckBox();
        jcbXRayPlates = new JCheckBox();
        jcbWireframePlates = new JCheckBox();
        jcbTransparentPlates = new JCheckBox();
        displayContainer = new JPanel();
        bdhcDisplay = new BdhcDisplay();
        panel1 = new JPanel();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        plateList = new JList<>();
        panel2 = new JPanel();
        jbAddPlate = new JButton();
        jbRemovePlate = new JButton();
        jbDuplicatePlate = new JButton();
        jPanel2 = new JPanel();
        jLabel2 = new JLabel();
        jsCoordZ = new JSpinner();
        jcbType = new JComboBox<>();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        angleDisplay1 = new AngleDisplay();
        angleDisplay2 = new AngleDisplay();
        jtfAngleX = new JTextField();
        jtfAngleY = new JTextField();
        jbApplyAngleX = new JButton();
        jbApplyAngleY = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("BDHC Editor");
        setMinimumSize(new Dimension(1300, 625));
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "fill,insets 5,hidemode 3,gapx 5",
            // columns
            "[1022,fill]" +
            "[grow,fill]",
            // rows
            "[]" +
            "[622,grow,fill]"));

        //======== panel4 ========
        {
            panel4.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[]"));

            //---- jButton2 ----
            jButton2.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
            jButton2.setText("Import BDHC");
            jButton2.addActionListener(e -> jButton2ActionPerformed(e));
            panel4.add(jButton2, "cell 0 0");

            //---- jButton1 ----
            jButton1.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
            jButton1.setText("Export BDHC");
            jButton1.addActionListener(e -> jButton1ActionPerformed(e));
            panel4.add(jButton1, "cell 1 0");

            //---- label1 ----
            label1.setText("BDHC files are saved automatically when pressing the save map button from the main window");
            label1.setIcon(new ImageIcon(getClass().getResource("/icons/informationIcon.png")));
            panel4.add(label1, "cell 3 0");
        }
        contentPane.add(panel4, "cell 0 0 2 1");

        //======== splitPane1 ========
        {
            splitPane1.setResizeWeight(0.5);
            splitPane1.setDividerLocation(290);

            //======== panel3 ========
            {
                panel3.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "0[grow,fill]0",
                    // rows
                    "0[grow,fill]0" +
                    "[grow,fill]0" +
                    "[]"));

                //---- bdhcDisplay3D ----
                bdhcDisplay3D.setPreferredSize(new Dimension(200, 512));
                bdhcDisplay3D.setBorder(new LineBorder(Color.lightGray));
                panel3.add(bdhcDisplay3D, "cell 0 0");

                //======== panel5 ========
                {
                    panel5.setBorder(new TitledBorder("View Settings"));
                    panel5.setLayout(new MigLayout(
                        "hidemode 3",
                        // columns
                        "[grow,fill]" +
                        "[grow,fill]",
                        // rows
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]0" +
                        "[grow,fill]0" +
                        "[]0"));

                    //---- jcbViewMap ----
                    jcbViewMap.setText("View Map");
                    jcbViewMap.setSelected(true);
                    jcbViewMap.addActionListener(e -> jcbViewMapActionPerformed(e));
                    panel5.add(jcbViewMap, "cell 0 1");

                    //---- jcbXRayPlates ----
                    jcbXRayPlates.setText("X Ray Plates");
                    jcbXRayPlates.setSelected(true);
                    jcbXRayPlates.addActionListener(e -> jcbXRayPlatesActionPerformed(e));
                    panel5.add(jcbXRayPlates, "cell 1 1");

                    //---- jcbWireframePlates ----
                    jcbWireframePlates.setText("Wireframe Plates");
                    jcbWireframePlates.setSelected(true);
                    jcbWireframePlates.addActionListener(e -> jcbWireframePlatesActionPerformed(e));
                    panel5.add(jcbWireframePlates, "cell 0 2");

                    //---- jcbTransparentPlates ----
                    jcbTransparentPlates.setText("Transparent Plates");
                    jcbTransparentPlates.setSelected(true);
                    jcbTransparentPlates.addActionListener(e -> jcbTransparentPlatesActionPerformed(e));
                    panel5.add(jcbTransparentPlates, "cell 1 2");
                }
                panel3.add(panel5, "cell 0 1");
            }
            splitPane1.setLeftComponent(panel3);

            //======== displayContainer ========
            {
                displayContainer.setMinimumSize(new Dimension(480, 480));
                displayContainer.setBorder(new LineBorder(Color.lightGray));
                displayContainer.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        displayContainerComponentResized(e);
                    }
                });
                displayContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

                //---- bdhcDisplay ----
                bdhcDisplay.setBorder(new LineBorder(Color.lightGray));
                displayContainer.add(bdhcDisplay);
            }
            splitPane1.setRightComponent(displayContainer);
        }
        contentPane.add(splitPane1, "cell 0 1");

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[grow]" +
                "[]"));

            //======== jPanel1 ========
            {
                jPanel1.setBorder(new TitledBorder("Plates"));
                jPanel1.setPreferredSize(null);
                jPanel1.setMinimumSize(null);
                jPanel1.setLayout(new MigLayout(
                    "insets 5,hidemode 3",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[grow,fill]" +
                    "[]"));

                //======== jScrollPane1 ========
                {
                    jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    jScrollPane1.setPreferredSize(null);

                    //---- plateList ----
                    plateList.setModel(new AbstractListModel<String>() {
                        String[] values = {

                        };
                        @Override
                        public int getSize() { return values.length; }
                        @Override
                        public String getElementAt(int i) { return values[i]; }
                    });
                    plateList.setPreferredSize(null);
                    plateList.addListSelectionListener(e -> plateListValueChanged(e));
                    jScrollPane1.setViewportView(plateList);
                }
                jPanel1.add(jScrollPane1, "cell 0 0");

                //======== panel2 ========
                {
                    panel2.setLayout(new GridLayout(1, 0, 5, 0));

                    //---- jbAddPlate ----
                    jbAddPlate.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                    jbAddPlate.setText("Add Plate");
                    jbAddPlate.addActionListener(e -> jbAddPlateActionPerformed(e));
                    panel2.add(jbAddPlate);

                    //---- jbRemovePlate ----
                    jbRemovePlate.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                    jbRemovePlate.setText("Remove Plate");
                    jbRemovePlate.addActionListener(e -> jbRemovePlateActionPerformed(e));
                    panel2.add(jbRemovePlate);

                    //---- jbDuplicatePlate ----
                    jbDuplicatePlate.setIcon(new ImageIcon(getClass().getResource("/icons/copyIcon.png")));
                    jbDuplicatePlate.setText("Duplicate Plate");
                    jbDuplicatePlate.addActionListener(e -> jbDuplicatePlateActionPerformed(e));
                    panel2.add(jbDuplicatePlate);
                }
                jPanel1.add(panel2, "cell 0 1");
            }
            panel1.add(jPanel1, "cell 0 0,growy");

            //======== jPanel2 ========
            {
                jPanel2.setBorder(new TitledBorder("Plate Info"));
                jPanel2.setPreferredSize(new Dimension(200, 219));
                jPanel2.setLayout(new MigLayout(
                    "insets 5,hidemode 3,gap 5 5",
                    // columns
                    "[97,fill]" +
                    "[140,grow,fill]" +
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[70:70,grow,fill]" +
                    "[]" +
                    "[]"));

                //---- jLabel2 ----
                jLabel2.setForeground(new Color(0, 153, 255));
                jLabel2.setText("Z Coord: ");
                jPanel2.add(jLabel2, "cell 0 0 2 1");

                //---- jsCoordZ ----
                jsCoordZ.setModel(new SpinnerNumberModel(0.0F, null, null, 1.0F));
                jsCoordZ.addChangeListener(e -> jsCoordZStateChanged(e));
                jPanel2.add(jsCoordZ, "cell 1 0 3 1");

                //---- jcbType ----
                jcbType.setModel(new DefaultComboBoxModel<>(new String[] {
                    "PLANE",
                    "BRIDGE (DP ONLY)",
                    "STAIRS LEFT (-X)",
                    "STAIRS RIGHT (+X)",
                    "STAIRS UP (-Y)",
                    "STAIRS DOWN (+Y)",
                    "OTHER"
                }));
                jcbType.setPreferredSize(new Dimension(100, 30));
                jcbType.addActionListener(e -> jcbTypeActionPerformed(e));
                jPanel2.add(jcbType, "cell 1 1 2 1,growx");

                //---- jLabel5 ----
                jLabel5.setText("Type: ");
                jPanel2.add(jLabel5, "cell 0 1");

                //---- jLabel6 ----
                jLabel6.setForeground(new Color(0, 153, 0));
                jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
                jLabel6.setText("Angle Y");
                jPanel2.add(jLabel6, "cell 0 2");

                //---- jLabel7 ----
                jLabel7.setForeground(new Color(204, 0, 0));
                jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
                jLabel7.setText("Angle X");
                jPanel2.add(jLabel7, "cell 2 2");

                //---- angleDisplay1 ----
                angleDisplay1.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                angleDisplay1.setToolTipText("");
                angleDisplay1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                angleDisplay1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        angleDisplay1MousePressed(e);
                    }
                });
                jPanel2.add(angleDisplay1, "cell 0 3");

                //---- angleDisplay2 ----
                angleDisplay2.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                angleDisplay2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                angleDisplay2.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        angleDisplay2MousePressed(e);
                    }
                });
                jPanel2.add(angleDisplay2, "cell 2 3");

                //---- jtfAngleX ----
                jtfAngleX.setText(" ");
                jtfAngleX.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        jtfAngleXFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        jtfAngleXFocusLost(e);
                    }
                });
                jPanel2.add(jtfAngleX, "cell 0 4");

                //---- jtfAngleY ----
                jtfAngleY.setText(" ");
                jtfAngleY.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        jtfAngleYFocusGained(e);
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        jtfAngleYFocusLost(e);
                    }
                });
                jPanel2.add(jtfAngleY, "cell 2 4");

                //---- jbApplyAngleX ----
                jbApplyAngleX.setText("Apply");
                jbApplyAngleX.addActionListener(e -> jbApplyAngleXActionPerformed(e));
                jPanel2.add(jbApplyAngleX, "cell 0 5");

                //---- jbApplyAngleY ----
                jbApplyAngleY.setText("Apply");
                jbApplyAngleY.addActionListener(e -> jbApplyAngleYActionPerformed(e));
                jPanel2.add(jbApplyAngleY, "cell 2 5");
            }
            panel1.add(jPanel2, "cell 0 1");
        }
        contentPane.add(panel1, "cell 1 1");
        setSize(1250, 615);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel4;
    private JButton jButton2;
    private JButton jButton1;
    private JLabel label1;
    private JSplitPane splitPane1;
    private JPanel panel3;
    private BdhcDisplay3D bdhcDisplay3D;
    private JPanel panel5;
    private JCheckBox jcbViewMap;
    private JCheckBox jcbXRayPlates;
    private JCheckBox jcbWireframePlates;
    private JCheckBox jcbTransparentPlates;
    private JPanel displayContainer;
    private BdhcDisplay bdhcDisplay;
    private JPanel panel1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> plateList;
    private JPanel panel2;
    private JButton jbAddPlate;
    private JButton jbRemovePlate;
    private JButton jbDuplicatePlate;
    private JPanel jPanel2;
    private JLabel jLabel2;
    private JSpinner jsCoordZ;
    private JComboBox<String> jcbType;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private AngleDisplay angleDisplay1;
    private AngleDisplay angleDisplay2;
    private JTextField jtfAngleX;
    private JTextField jtfAngleY;
    private JButton jbApplyAngleX;
    private JButton jbApplyAngleY;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
