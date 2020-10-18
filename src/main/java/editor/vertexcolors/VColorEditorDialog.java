package editor.vertexcolors;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;
import editor.handler.MapEditorHandler;
import editor.tileseteditor.TilesetEditorHandler;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import tileset.TileGeometryCompresser;
import utils.Utils;
import utils.Utils.MutableBoolean;

/**
 * @author Trifindo, JackHack96
 */
public class VColorEditorDialog extends JDialog {

    private MapEditorHandler handler;
    private TilesetEditorHandler tsetHandler;

    private MutableBoolean jtfRedEnabled = new MutableBoolean(true);
    private MutableBoolean jtfGreenEnabled = new MutableBoolean(true);
    private MutableBoolean jtfBlueEnabled = new MutableBoolean(true);

    private String allVertexModeDescription = "Use left click for painting vertices. "
            + "Use mouse wheel for rotating the model. ";
    private String perFaceModeDescription = "Use right click for selecting a face. "
            + "Use left click for painting the vertices of the selected face. "
            + "Use the mouse wheel for rotating the model. ";
    private String grabColorModeDescription = "Use left click for selecting the vertex and taking its color. "
            + "Use the mouse wheel for rotating the model. ";

    public VColorEditorDialog(Window owner) {
        super(owner);
        initComponents();

        addListenersToJTextField(jtfRed, jtfRedEnabled);
        addListenersToJTextField(jtfGreen, jtfGreenEnabled);
        addListenersToJTextField(jtfBlue, jtfBlueEnabled);

        jtaModeDescription.setText(allVertexModeDescription);
    }

    private void colorWheelPanel1MousePressed(MouseEvent evt) {
        Color selectedColor = colorWheelPanel1.getSelectedColor();
        tsetHandler.setLastColorUsed(selectedColor);
        jPanel1.setBackground(selectedColor);
        updateViewTextFieldsRGB(selectedColor);
        jPanel1.repaint();
    }

    private void colorWheelPanel1MouseDragged(MouseEvent evt) {
        Color selectedColor = colorWheelPanel1.getSelectedColor();
        tsetHandler.setLastColorUsed(selectedColor);
        jPanel1.setBackground(selectedColor);
        updateViewTextFieldsRGB(selectedColor);
        jPanel1.repaint();
    }

    private void colorBrightnessPanel1MousePressed(MouseEvent evt) {
        colorWheelPanel1.setBrightness(colorBrightnessPanel1.getBrightness());
        colorWheelPanel1.updateImage();
        colorWheelPanel1.repaint();

        Color selectedColor = colorWheelPanel1.getSelectedColor();
        tsetHandler.setLastColorUsed(selectedColor);
        jPanel1.setBackground(selectedColor);
        updateViewTextFieldsRGB(selectedColor);
        jPanel1.repaint();
    }

    private void colorBrightnessPanel1MouseDragged(MouseEvent evt) {
        colorWheelPanel1.setBrightness(colorBrightnessPanel1.getBrightness());
        colorWheelPanel1.updateImage();
        colorWheelPanel1.repaint();

        Color selectedColor = colorWheelPanel1.getSelectedColor();
        tsetHandler.setLastColorUsed(selectedColor);
        jPanel1.setBackground(selectedColor);
        updateViewTextFieldsRGB(selectedColor);
        jPanel1.repaint();
    }

    private void jbVertexModeActionPerformed(ActionEvent evt) {
        vColorEditorDisplay1.setSelectionMode(VColorEditorDisplay.BRUSH_MODE);
        vColorEditorDisplay1.repaint();

        jtaModeDescription.setText(allVertexModeDescription);
    }

    private void jbFaceModeActionPerformed(ActionEvent evt) {
        vColorEditorDisplay1.setSelectionMode(VColorEditorDisplay.POLYGON_SELECTION_MODE);
        vColorEditorDisplay1.repaint();

        jtaModeDescription.setText(perFaceModeDescription);
    }

    private void jbSolidViewActionPerformed(ActionEvent evt) {
        vColorEditorDisplay1.setDrawTextures(false);
        vColorEditorDisplay1.repaint();
    }

    private void jbTextureViewActionPerformed(ActionEvent evt) {
        vColorEditorDisplay1.setDrawTextures(true);
        vColorEditorDisplay1.repaint();
    }

    private void jbWireframeViewActionPerformed(ActionEvent evt) {
        vColorEditorDisplay1.setWireframe(true);
        vColorEditorDisplay1.repaint();
    }

    private void jbSimpleViewActionPerformed(ActionEvent evt) {
        vColorEditorDisplay1.setWireframe(false);
        vColorEditorDisplay1.repaint();
    }

    private void jbGrabColorActionPerformed(ActionEvent evt) {
        vColorEditorDisplay1.setSelectionMode(VColorEditorDisplay.COLOR_GRAB_MODE);
        vColorEditorDisplay1.repaint();

        jtaModeDescription.setText(grabColorModeDescription);
    }

    private void jSlider1StateChanged(ChangeEvent evt) {
        vColorEditorDisplay1.setBrushSize(jSlider1.getValue());
        vColorEditorDisplay1.repaint();
    }

    public void init(MapEditorHandler handler, TilesetEditorHandler tsetHandler) {
        this.handler = handler;
        this.tsetHandler = tsetHandler;
        this.vColorEditorDisplay1.setHandler(handler, tsetHandler, this);

        setSelectedColor(tsetHandler.getLastColorUsed());
    }

    public Color getSelectedColor() {
        return colorWheelPanel1.getSelectedColor();
    }

    public void updateViewTextFieldsRGB(Color color) {
        jtfRedEnabled.value = false;
        jtfGreenEnabled.value = false;
        jtfBlueEnabled.value = false;
        jtfRed.setText(String.valueOf(color.getRed()));
        jtfGreen.setText(String.valueOf(color.getGreen()));
        jtfBlue.setText(String.valueOf(color.getBlue()));
        jtfRedEnabled.value = true;
        jtfGreenEnabled.value = true;
        jtfBlueEnabled.value = true;
    }

    public void setSelectedColor(Color color) {
        float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        colorWheelPanel1.setColor(color);
        colorWheelPanel1.updateImage();
        colorBrightnessPanel1.setBrightness(hsv[2]);
        jPanel1.setBackground(color);

        tsetHandler.setLastColorUsed(color);

        updateViewTextFieldsRGB(color);
        colorWheelPanel1.repaint();
        colorBrightnessPanel1.repaint();
        jPanel1.repaint();

    }

    private Color getColorFromJTextFields() {
        int r = getColorComponentFromJTextField(jtfRed);
        int g = getColorComponentFromJTextField(jtfGreen);
        int b = getColorComponentFromJTextField(jtfBlue);
        return new Color(r, g, b);
    }

    private int getColorComponentFromJTextField(JTextField jtf) {
        int value;
        try {
            value = Integer.parseInt(jtf.getText());
        } catch (NumberFormatException ex) {
            value = 0;
        }
        if (value < 0) {
            value = 0;
        } else if (value > 255) {
            value = 255;
        }
        return value;
    }

    private void fixJTextFieldValue(JTextField jtf, MutableBoolean enabled) {
        enabled.value = false;
        jtf.setText(String.valueOf(getColorComponentFromJTextField(jtf)));
        enabled.value = true;
    }

    private void addListenersToJTextField(JTextField jtf, MutableBoolean enabled) {
        ((AbstractDocument) jtf.getDocument()).setDocumentFilter(new DocumentFilter() {
            Pattern regEx = Pattern.compile("\\d*");

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (!matcher.matches()) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });

        jtf.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void update() {
                if (enabled.value) {
                    Color color = getColorFromJTextFields();
                    float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                    colorWheelPanel1.setColor(color);
                    colorWheelPanel1.updateImage();
                    colorBrightnessPanel1.setBrightness(hsv[2]);
                    jPanel1.setBackground(color);

                    tsetHandler.setLastColorUsed(color);

                    colorWheelPanel1.repaint();
                    colorBrightnessPanel1.repaint();
                    jPanel1.repaint();
                }
            }
        });

        jtf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fixJTextFieldValue(jtf, enabled);
            }
        });

        jtf.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                fixJTextFieldValue(jtf, enabled);
            }
        });
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  
        vColorEditorDisplay1 = new VColorEditorDisplay();
        jPanel2 = new JPanel();
        colorWheelPanel1 = new ColorWheelPanel();
        colorBrightnessPanel1 = new ColorBrightnessPanel();
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jtfRed = new JTextField();
        jtfGreen = new JTextField();
        jLabel3 = new JLabel();
        jtfBlue = new JTextField();
        jPanel3 = new JPanel();
        jbVertexMode = new JButton();
        jbFaceMode = new JButton();
        jbGrabColor = new JButton();
        jPanel4 = new JPanel();
        jbSolidView = new JButton();
        jbTextureView = new JButton();
        jbWireframeView = new JButton();
        jbSimpleView = new JButton();
        jPanel5 = new JPanel();
        jSlider1 = new JSlider();
        jPanel6 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jtaModeDescription = new JTextArea();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Vertex Color Editor");
        setModal(true);
        var contentPane = getContentPane();

        //======== vColorEditorDisplay1 ========
        {
            vColorEditorDisplay1.setBorder(new BevelBorder(BevelBorder.LOWERED));

            GroupLayout vColorEditorDisplay1Layout = new GroupLayout(vColorEditorDisplay1);
            vColorEditorDisplay1.setLayout(vColorEditorDisplay1Layout);
            vColorEditorDisplay1Layout.setHorizontalGroup(
                vColorEditorDisplay1Layout.createParallelGroup()
                    .addGap(0, 623, Short.MAX_VALUE)
            );
            vColorEditorDisplay1Layout.setVerticalGroup(
                vColorEditorDisplay1Layout.createParallelGroup()
                    .addGap(0, 585, Short.MAX_VALUE)
            );
        }

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Brush Color"));

            //======== colorWheelPanel1 ========
            {
                colorWheelPanel1.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        colorWheelPanel1MouseDragged(e);
                    }
                });
                colorWheelPanel1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        colorWheelPanel1MousePressed(e);
                    }
                });

                GroupLayout colorWheelPanel1Layout = new GroupLayout(colorWheelPanel1);
                colorWheelPanel1.setLayout(colorWheelPanel1Layout);
                colorWheelPanel1Layout.setHorizontalGroup(
                    colorWheelPanel1Layout.createParallelGroup()
                        .addGap(0, 113, Short.MAX_VALUE)
                );
                colorWheelPanel1Layout.setVerticalGroup(
                    colorWheelPanel1Layout.createParallelGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                );
            }

            //======== colorBrightnessPanel1 ========
            {
                colorBrightnessPanel1.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        colorBrightnessPanel1MouseDragged(e);
                    }
                });
                colorBrightnessPanel1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        colorBrightnessPanel1MousePressed(e);
                    }
                });

                GroupLayout colorBrightnessPanel1Layout = new GroupLayout(colorBrightnessPanel1);
                colorBrightnessPanel1.setLayout(colorBrightnessPanel1Layout);
                colorBrightnessPanel1Layout.setHorizontalGroup(
                    colorBrightnessPanel1Layout.createParallelGroup()
                        .addGap(0, 25, Short.MAX_VALUE)
                );
                colorBrightnessPanel1Layout.setVerticalGroup(
                    colorBrightnessPanel1Layout.createParallelGroup()
                        .addGap(0, 113, Short.MAX_VALUE)
                );
            }

            //======== jPanel1 ========
            {
                jPanel1.setBackground(Color.white);
                jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

                GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                );
                jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addGap(0, 22, Short.MAX_VALUE)
                );
            }

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 11));
            jLabel1.setForeground(Color.red);
            jLabel1.setText("R:");

            //---- jLabel2 ----
            jLabel2.setFont(new Font("Tahoma", Font.BOLD, 11));
            jLabel2.setForeground(new Color(0, 153, 0));
            jLabel2.setText("G:");

            //---- jtfRed ----
            jtfRed.setText("255");

            //---- jtfGreen ----
            jtfGreen.setText("255");

            //---- jLabel3 ----
            jLabel3.setFont(new Font("Tahoma", Font.BOLD, 11));
            jLabel3.setForeground(new Color(0, 0, 204));
            jLabel3.setText("B:");

            //---- jtfBlue ----
            jtfBlue.setText("255");

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(colorWheelPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(colorBrightnessPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup()
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jtfRed, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jtfBlue, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jtfGreen, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(colorBrightnessPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(colorWheelPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jtfRed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jtfGreen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jtfBlue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Edit Modes"));

            //---- jbVertexMode ----
            jbVertexMode.setIcon(new ImageIcon(getClass().getResource("/icons/VertexSelectionIcon.png")));
            jbVertexMode.setText("All Vertex Mode");
            jbVertexMode.addActionListener(e -> jbVertexModeActionPerformed(e));

            //---- jbFaceMode ----
            jbFaceMode.setIcon(new ImageIcon(getClass().getResource("/icons/FaceSelectionIcon.png")));
            jbFaceMode.setText("Per Face Mode");
            jbFaceMode.addActionListener(e -> jbFaceModeActionPerformed(e));

            //---- jbGrabColor ----
            jbGrabColor.setIcon(new ImageIcon(getClass().getResource("/icons/GrabColorIcon.png")));
            jbGrabColor.setText("Grab Vertex Color");
            jbGrabColor.addActionListener(e -> jbGrabColorActionPerformed(e));

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbVertexMode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbFaceMode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbGrabColor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jbVertexMode)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbFaceMode)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbGrabColor)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel4 ========
        {
            jPanel4.setBorder(new TitledBorder("View Modes"));

            //---- jbSolidView ----
            jbSolidView.setIcon(new ImageIcon(getClass().getResource("/icons/SolidViewIcon.png")));
            jbSolidView.setText("Solid View");
            jbSolidView.addActionListener(e -> jbSolidViewActionPerformed(e));

            //---- jbTextureView ----
            jbTextureView.setIcon(new ImageIcon(getClass().getResource("/icons/TextureViewIcon.png")));
            jbTextureView.setText("Texture View");
            jbTextureView.addActionListener(e -> jbTextureViewActionPerformed(e));

            //---- jbWireframeView ----
            jbWireframeView.setIcon(new ImageIcon(getClass().getResource("/icons/WireframeViewIcon.png")));
            jbWireframeView.setText("Wireframe View");
            jbWireframeView.addActionListener(e -> jbWireframeViewActionPerformed(e));

            //---- jbSimpleView ----
            jbSimpleView.setIcon(new ImageIcon(getClass().getResource("/icons/NoWireframeViewIcon.png")));
            jbSimpleView.setText("Simple View");
            jbSimpleView.addActionListener(e -> jbSimpleViewActionPerformed(e));

            GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup()
                            .addComponent(jbTextureView, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbSolidView, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbWireframeView, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbSimpleView, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
            );
            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jbSolidView)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbTextureView)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbWireframeView)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbSimpleView)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== jPanel5 ========
        {
            jPanel5.setBorder(new TitledBorder("Brush Size"));

            //---- jSlider1 ----
            jSlider1.setMinimum(5);
            jSlider1.setValue(20);
            jSlider1.addChangeListener(e -> jSlider1StateChanged(e));

            GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
            jPanel5.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup()
                    .addComponent(jSlider1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
            );
            jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup()
                    .addComponent(jSlider1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            );
        }

        //======== jPanel6 ========
        {
            jPanel6.setBorder(new TitledBorder("Mode Description"));

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

                //---- jtaModeDescription ----
                jtaModeDescription.setColumns(20);
                jtaModeDescription.setFont(new Font("Tahoma", Font.PLAIN, 11));
                jtaModeDescription.setLineWrap(true);
                jtaModeDescription.setRows(5);
                jtaModeDescription.setWrapStyleWord(true);
                jtaModeDescription.setDisabledTextColor(new Color(51, 51, 51));
                jtaModeDescription.setEnabled(false);
                jScrollPane1.setViewportView(jtaModeDescription);
            }

            GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
            jPanel6.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup()
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
            );
            jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup()
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(vColorEditorDisplay1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(vColorEditorDisplay1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  
    private VColorEditorDisplay vColorEditorDisplay1;
    private JPanel jPanel2;
    private ColorWheelPanel colorWheelPanel1;
    private ColorBrightnessPanel colorBrightnessPanel1;
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTextField jtfRed;
    private JTextField jtfGreen;
    private JLabel jLabel3;
    private JTextField jtfBlue;
    private JPanel jPanel3;
    private JButton jbVertexMode;
    private JButton jbFaceMode;
    private JButton jbGrabColor;
    private JPanel jPanel4;
    private JButton jbSolidView;
    private JButton jbTextureView;
    private JButton jbWireframeView;
    private JButton jbSimpleView;
    private JPanel jPanel5;
    private JSlider jSlider1;
    private JPanel jPanel6;
    private JScrollPane jScrollPane1;
    private JTextArea jtaModeDescription;
    // JFormDesigner - End of variables declaration  
}
