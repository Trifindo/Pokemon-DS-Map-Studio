package editor.bdhc;

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
import javax.swing.LayoutStyle;
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

    private static final Color redColor = new Color(255, 200, 200);
    private static final Color greenColor = new Color(200, 255, 200);
    private static final Color whiteColor = Color.white;

    public BdhcEditorDialog(Window owner) {
        super(owner);
        initComponents();

        addListenerToJTextField(jtfCoordZ, jtfCoordZEnabled);
        addListenerToJTextField(jtfAngleX, jtfAngleXEnabled);
        addListenerToJTextField(jtfAngleY, jtfAngleYEnabled);

        setSize(new Dimension(1160,650));
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
        repaint();
    }

    private void jbRemovePlateActionPerformed(ActionEvent e) {
        if (bdhcHandler.getPlates().size() > 1) {
            bdhcHandler.removeSelectedPlate();

            updateViewPlateNames();
            repaint();
        }
    }

    private void jtfCoordZFocusGained(FocusEvent e) {
        jtfCoordZ.selectAll();
    }

    private void jbCoordinateZActionPerformed(ActionEvent e) {
        changeCoordZ();
    }

    private void jcbTypeActionPerformed(ActionEvent e) {
        if (jcbTypeEnabled) {
            bdhcHandler.getSelectedPlate().type = jcbType.getSelectedIndex();
            bdhcDisplay.repaint();
            updateViewSlopes();
            updateViewAngles();
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
        jtfAngleXEnabled.value = true;

        if (value != 0.0f) {
            jtfAngleYEnabled.value = false;
            jtfAngleY.setText(String.valueOf(0.0f));
            jtfAngleY.setBackground(greenColor);
            jtfAngleYEnabled.value = true;
        }

        bdhcHandler.getSelectedPlate().setAngleX((float) ((value / 180.0f) * Math.PI));

        updateViewAngles();
        updateViewSlopes();
        updateViewType();
        bdhcDisplay.repaint();
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
        jtfAngleYEnabled.value = true;

        if (value != 0.0f) {
            jtfAngleXEnabled.value = false;
            jtfAngleX.setText(String.valueOf(0.0f));
            jtfAngleX.setBackground(greenColor);
            jtfAngleXEnabled.value = true;
        }

        bdhcHandler.getSelectedPlate().setAngleY((float) ((value / 180.0f) * Math.PI));

        updateViewAngles();
        updateViewSlopes();
        updateViewType();
        bdhcDisplay.repaint();
    }

    private void jButton1ActionPerformed(ActionEvent e) {
        saveBdhcWithDialog();
    }

    private void jButton2ActionPerformed(ActionEvent e) {
        openBdhcWithDialog();
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
                JOptionPane.showMessageDialog(this, "Can't save file", "Error saving BDHC", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }

    public void updateView() {
        Plate p = bdhcHandler.getSelectedPlate();
        updateViewJTextField(jtfCoordZ, p.z, jtfCoordZEnabled);
        updateViewPlateNames();
        updateViewType();
        updateViewSlopes();
        updateViewAngles();
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

    public void changeCoordZ() {
        bdhcHandler.getSelectedPlate().z = getValueFromJTextField(
                jtfCoordZ, bdhcHandler.getSelectedPlate().z, jtfCoordZEnabled);
    }

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
        displayContainer = new JPanel();
        bdhcDisplay = new BdhcDisplay();
        panel1 = new JPanel();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        plateList = new JList<>();
        panel2 = new JPanel();
        jbAddPlate = new JButton();
        jbRemovePlate = new JButton();
        jPanel2 = new JPanel();
        jLabel2 = new JLabel();
        jtfCoordZ = new JTextField();
        jbCoordinateZ = new JButton();
        jcbType = new JComboBox<>();
        jLabel5 = new JLabel();
        angleDisplay1 = new AngleDisplay();
        angleDisplay2 = new AngleDisplay();
        jtfAngleX = new JTextField();
        jtfAngleY = new JTextField();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jbApplyAngleX = new JButton();
        jbApplyAngleY = new JButton();
        panel3 = new JPanel();
        jButton1 = new JButton();
        jButton2 = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("BDHC Editor");
        setMinimumSize(null);
        var contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]" +
            "[fill]",
            // rows
            "[grow,fill]"));

        //======== displayContainer ========
        {
            displayContainer.setMinimumSize(new Dimension(512, 512));
            displayContainer.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    displayContainerComponentResized(e);
                }
            });
            displayContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

            //======== bdhcDisplay ========
            {
                bdhcDisplay.setBorder(LineBorder.createBlackLineBorder());

                GroupLayout bdhcDisplayLayout = new GroupLayout(bdhcDisplay);
                bdhcDisplay.setLayout(bdhcDisplayLayout);
                bdhcDisplayLayout.setHorizontalGroup(
                    bdhcDisplayLayout.createParallelGroup()
                        .addGap(0, 510, Short.MAX_VALUE)
                );
                bdhcDisplayLayout.setVerticalGroup(
                    bdhcDisplayLayout.createParallelGroup()
                        .addGap(0, 510, Short.MAX_VALUE)
                );
            }
            displayContainer.add(bdhcDisplay);
        }
        contentPane.add(displayContainer, "cell 0 0");

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]"));

            //======== jPanel1 ========
            {
                jPanel1.setBorder(new TitledBorder("Plates"));

                //======== jScrollPane1 ========
                {
                    jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                    //---- plateList ----
                    plateList.setModel(new AbstractListModel<String>() {
                        String[] values = {

                        };
                        @Override
                        public int getSize() { return values.length; }
                        @Override
                        public String getElementAt(int i) { return values[i]; }
                    });
                    plateList.addListSelectionListener(e -> plateListValueChanged(e));
                    jScrollPane1.setViewportView(plateList);
                }

                GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addContainerGap())
                );
                jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }
            panel1.add(jPanel1, "cell 0 0");

            //======== panel2 ========
            {
                panel2.setLayout(new GridLayout());

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
            }
            panel1.add(panel2, "cell 0 1");

            //======== jPanel2 ========
            {
                jPanel2.setBorder(new TitledBorder("Plate Info"));

                //---- jLabel2 ----
                jLabel2.setForeground(new Color(0, 0, 204));
                jLabel2.setText("Z Coordinate: ");

                //---- jtfCoordZ ----
                jtfCoordZ.setText(" ");
                jtfCoordZ.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        jtfCoordZFocusGained(e);
                    }
                });

                //---- jbCoordinateZ ----
                jbCoordinateZ.setText("Apply");
                jbCoordinateZ.addActionListener(e -> jbCoordinateZActionPerformed(e));

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
                jcbType.addActionListener(e -> jcbTypeActionPerformed(e));

                //---- jLabel5 ----
                jLabel5.setText("Type: ");

                //======== angleDisplay1 ========
                {
                    angleDisplay1.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                    angleDisplay1.setToolTipText("");
                    angleDisplay1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            angleDisplay1MousePressed(e);
                        }
                    });

                    GroupLayout angleDisplay1Layout = new GroupLayout(angleDisplay1);
                    angleDisplay1.setLayout(angleDisplay1Layout);
                    angleDisplay1Layout.setHorizontalGroup(
                        angleDisplay1Layout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                    angleDisplay1Layout.setVerticalGroup(
                        angleDisplay1Layout.createParallelGroup()
                            .addGap(0, 58, Short.MAX_VALUE)
                    );
                }

                //======== angleDisplay2 ========
                {
                    angleDisplay2.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                    angleDisplay2.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            angleDisplay2MousePressed(e);
                        }
                    });

                    GroupLayout angleDisplay2Layout = new GroupLayout(angleDisplay2);
                    angleDisplay2.setLayout(angleDisplay2Layout);
                    angleDisplay2Layout.setHorizontalGroup(
                        angleDisplay2Layout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                    angleDisplay2Layout.setVerticalGroup(
                        angleDisplay2Layout.createParallelGroup()
                            .addGap(0, 58, Short.MAX_VALUE)
                    );
                }

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

                //---- jLabel6 ----
                jLabel6.setForeground(new Color(0, 153, 0));
                jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
                jLabel6.setText("Angle Y");

                //---- jLabel7 ----
                jLabel7.setForeground(new Color(204, 0, 0));
                jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
                jLabel7.setText("Angle X");

                //---- jbApplyAngleX ----
                jbApplyAngleX.setText("Apply");
                jbApplyAngleX.addActionListener(e -> jbApplyAngleXActionPerformed(e));

                //---- jbApplyAngleY ----
                jbApplyAngleY.setText("Apply");
                jbApplyAngleY.addActionListener(e -> jbApplyAngleYActionPerformed(e));

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup()
                                        .addComponent(jcbType, GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(jtfCoordZ, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jbCoordinateZ)
                                            .addGap(0, 0, Short.MAX_VALUE))))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jbApplyAngleX, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jtfAngleX, GroupLayout.Alignment.LEADING)
                                        .addComponent(angleDisplay1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel6, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(angleDisplay2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jtfAngleY)
                                        .addComponent(jbApplyAngleY, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addContainerGap())
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jtfCoordZ, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbCoordinateZ))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jcbType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup()
                                .addComponent(angleDisplay1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(angleDisplay2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup()
                                .addComponent(jtfAngleX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtfAngleY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup()
                                .addComponent(jbApplyAngleX)
                                .addComponent(jbApplyAngleY))
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }
            panel1.add(jPanel2, "cell 0 2");

            //======== panel3 ========
            {
                panel3.setLayout(new GridLayout());

                //---- jButton1 ----
                jButton1.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
                jButton1.setText("Export");
                jButton1.addActionListener(e -> jButton1ActionPerformed(e));
                panel3.add(jButton1);

                //---- jButton2 ----
                jButton2.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
                jButton2.setText("Import");
                jButton2.addActionListener(e -> jButton2ActionPerformed(e));
                panel3.add(jButton2);
            }
            panel1.add(panel3, "cell 0 3");
        }
        contentPane.add(panel1, "cell 1 0");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel displayContainer;
    private BdhcDisplay bdhcDisplay;
    private JPanel panel1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> plateList;
    private JPanel panel2;
    private JButton jbAddPlate;
    private JButton jbRemovePlate;
    private JPanel jPanel2;
    private JLabel jLabel2;
    private JTextField jtfCoordZ;
    private JButton jbCoordinateZ;
    private JComboBox<String> jcbType;
    private JLabel jLabel5;
    private AngleDisplay angleDisplay1;
    private AngleDisplay angleDisplay2;
    private JTextField jtfAngleX;
    private JTextField jtfAngleY;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JButton jbApplyAngleX;
    private JButton jbApplyAngleY;
    private JPanel panel3;
    private JButton jButton1;
    private JButton jButton2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
