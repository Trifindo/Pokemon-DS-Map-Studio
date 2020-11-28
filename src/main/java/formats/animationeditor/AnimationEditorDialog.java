package formats.animationeditor;

import editor.handler.MapEditorHandler;
import formats.nsbtx2.Nsbtx2;
import utils.Utils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author Trifindo, JackHack96
 */
public class AnimationEditorDialog extends JDialog {
    private MapEditorHandler handler;
    private AnimationHandler animHandler;

    private static final String playButtonIcon = "▶";
    private static final String stopButtonIcon = "◼";
    private static final Color playButtonColor = new Color(0, 153, 0);
    private static final Color stopButtonColor = new Color(255, 51, 51);

    private static final Color editingColor = new Color(255, 185, 185);
    private static final Color rightColor = new Color(200, 255, 200);

    private Utils.MutableBoolean jtfAnimNameEnabled = new Utils.MutableBoolean(true);
    private boolean animationListEnabled = true;
    private boolean textureListEnabled = true;

    public AnimationEditorDialog(Window owner) {
        super(owner);
        initComponents();
        jScrollPane1.getHorizontalScrollBar().setUnitIncrement(AnimationFramesDisplay.cellSize);
        Utils.addListenerToJTextFieldColor(jtfAnimationName, jtfAnimNameEnabled, editingColor, Color.black);
        System.out.println(jbPlay.getText());
    }

    private void formWindowClosed(WindowEvent e) {
        animHandler.pauseAnimation();
    }

    private void jbPlayActionPerformed(ActionEvent e) {
        togglePlay();
    }

    private void jbOpenNsbtxActionPerformed(ActionEvent e) {
        openNsbtxWithDialog();
    }

    private void jlTextureNamesValueChanged(ListSelectionEvent e) {
        if (animHandler != null) {
            if (animationListEnabled && !animHandler.isAnimationRunning()) {
                animHandler.setCurrentDelay((Integer) jsDelay.getValue());
                repaintFrames();
            }
        }
    }

    private void jbAddFrameActionPerformed(ActionEvent e) {
        addFrame();
    }

    private void jbRemoveFrameActionPerformed(ActionEvent e) {
        removeFrame();
    }

    private void jsDelayStateChanged(ChangeEvent e) {
        if (animHandler != null) {
            if (animationListEnabled && !animHandler.isAnimationRunning()) {
                animHandler.setCurrentDelay((Integer) jsDelay.getValue());
                repaintFrames();
            }
        }
    }

    private void jlAnimationNamesValueChanged(ListSelectionEvent e) {
        updateView();
    }

    private void jbOpenAnimationFileActionPerformed(ActionEvent e) {
        if (animHandler != null) {
            openAnimationFileWithDialog();
        }
    }

    private void jbSaveAnimationFileActionPerformed(ActionEvent e) {
        if (animHandler != null) {
            saveAnimationFileWithDialog();
        }
    }

    private void jbAddAnimationActionPerformed(ActionEvent e) {
        if (animHandler != null) {
            if (animationListEnabled && !animHandler.isAnimationRunning()) {
                if (animHandler.getAnimationFile() != null) {
                    animHandler.addAnimation("New animation");
                    updateViewAnimationListNames(jlAnimationNames.getModel().getSize());
                    repaintFrames();
                }
            }
        }
    }

    private void jbRemoveAnimationActionPerformed(ActionEvent e) {
        if (animHandler != null) {
            if (animationListEnabled && !animHandler.isAnimationRunning()) {
                if (animHandler.getAnimationSelected() != null) {
                    int index = getAnimationSelectedIndex();
                    animHandler.getAnimationFile().removeAnimation(getAnimationSelectedIndex());
                    updateViewAnimationListNames(index);
                    repaintFrames();
                }
            }
        }
    }

    private void jbApplyActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        this.animHandler = new AnimationHandler(this);

        this.animationDisplay.init(animHandler);
        this.animationFramesDisplay.init(animHandler);
    }

    public void updateView() {
        if (animHandler.getAnimationFile() != null) {
            if (animHandler.getAnimationSelected() != null) {
                jtfAnimNameEnabled.value = false;
                jtfAnimationName.setText(animHandler.getAnimationSelected().getName());
                jtfAnimationName.setBackground(UIManager.getColor("TextPane.background"));
                jtfAnimationName.setForeground(UIManager.getColor("TextPane.foreground"));
                jtfAnimNameEnabled.value = true;
            }

            animationDisplay.repaint();

            animationFramesDisplay.updateSize();
            animationFramesDisplay.repaint();

            updateViewDelayDisplay();
        }

    }

    public void repaintFrames() {
        animationDisplay.repaint();

        animationFramesDisplay.repaint();

        updateViewTexturesNsbxt();

        updateViewDelayDisplay();
    }

    public void updateViewTexturesNsbxt() {
        jlTextureNames.setSelectedIndex(animHandler.getCurrentNsbtxTextureIndex());
    }

    public void updateViewDelayDisplay() {
        jsDelay.setValue(animHandler.getCurrentDelay());
    }

    public void updateViewAnimationListNames(int indexSelected) {
        if (animHandler.getAnimationFile() != null) {
            animationListEnabled = false;
            DefaultListModel demoList = new DefaultListModel();
            for (int i = 0; i < animHandler.getAnimationFile().size(); i++) {
                String name = animHandler.getAnimationFile().getAnimation(i).getName();
                demoList.addElement(name);
            }
            jlAnimationNames.setModel(demoList);
            if (indexSelected > demoList.size() - 1) {
                indexSelected = demoList.size() - 1;
            } else if (indexSelected < 0) {
                indexSelected = 0;
            }
            jlAnimationNames.setSelectedIndex(indexSelected);
            jlAnimationNames.ensureIndexIsVisible(indexSelected);
            animationListEnabled = true;
        }
    }

    public void updateViewTextureNames(int indexSelected) {
        if (animHandler.getNsbtx() != null) {
            textureListEnabled = false;
            DefaultListModel demoList = new DefaultListModel();
            for (int i = 0; i < animHandler.getNsbtx().getTextures().size(); i++) {
                String name = animHandler.getNsbtx().getTexture(i).getName();
                demoList.addElement(name);
            }
            jlTextureNames.setModel(demoList);
            if (indexSelected > demoList.size() - 1) {
                indexSelected = demoList.size() - 1;
            } else if (indexSelected < 0) {
                indexSelected = 0;
            }
            jlTextureNames.setSelectedIndex(indexSelected);
            textureListEnabled = true;
        }
    }

    private void togglePlay() {
        if (animHandler.getAnimationFile() != null) {
            if (animHandler.isAnimationRunning()) {
                animHandler.pauseAnimation();
                jbPlay.setText(playButtonIcon);
                jbPlay.setForeground(playButtonColor);
                setComponentsEnabled(true);
            } else {
                animHandler.playAnimation();
                jbPlay.setText(stopButtonIcon);
                jbPlay.setForeground(stopButtonColor);
                setComponentsEnabled(false);
            }
        }
    }

    private void setComponentsEnabled(boolean enabled) {
        jbAddAnimation.setEnabled(enabled);
        jbAddFrame.setEnabled(enabled);
        jbApply.setEnabled(enabled);
        jbOpenAnimationFile.setEnabled(enabled);
        jbOpenNsbtx.setEnabled(enabled);
        jbRemoveAnimation.setEnabled(enabled);
        jbRemoveFrame.setEnabled(enabled);
        jbSaveAnimationFile.setEnabled(enabled);
        jsDelay.setEnabled(enabled);
        jtfAnimationName.setEnabled(enabled);
        jlTextureNames.setEnabled(enabled);
    }

    public void changeAnimationName() {
        String name = jtfAnimationName.getText();
        if (name.length() <= Nsbtx2.maxNameSize) {
            jtfAnimNameEnabled.value = false;
            animHandler.getAnimationSelected().setName(name);
            jtfAnimationName.setBackground(rightColor);
            jtfAnimationName.setForeground(Color.black);
            jtfAnimNameEnabled.value = true;

            updateViewAnimationListNames(jlAnimationNames.getSelectedIndex());
        } else {
            JOptionPane.showMessageDialog(this,
                    "The animation name has more than 16 characters",
                    "The name is too long",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void openAnimationFileWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastNsbtxDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
        }
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Animation File File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                animHandler.readAnimationFile(fc.getSelectedFile().getPath());

                updateView();

                updateViewAnimationListNames(0);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening animation file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openNsbtxWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastNsbtxDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open NSBTX File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                animHandler.readNsbtx(fc.getSelectedFile().getPath());

                updateView();
                updateViewTextureNames(0);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening NSBTX", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAnimationFileWithDialog() {
        if (animHandler.getAnimationFile() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastNsbtxDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
            }
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save Animation File");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    String path = fc.getSelectedFile().getPath();

                    animHandler.saveAnimationFile(path);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was an error saving the IMD",
                            "Error saving IMD", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void addFrame() {
        if (animHandler != null) {
            if (animHandler.getAnimationSelected() != null) {
                animHandler.getAnimationSelected().addFrame(
                        jlTextureNames.getSelectedIndex(),
                        (Integer) jsDelay.getValue());
                repaintFrames();
            }
        }
    }

    public void removeFrame() {
        if (animHandler != null) {
            if (animHandler.getAnimationSelected() != null) {
                if (animHandler.getAnimationSelected().removeFrame(animHandler.getCurrentFrameIndex())) {
                    int index = animHandler.getCurrentFrameIndex() - 1;
                    if (index < 0) {
                        index = 0;
                    }
                    animHandler.setCurrentFrameIndex(index);
                }
                repaintFrames();
            }
        }
    }

    public int getAnimationSelectedIndex() {
        if (jlAnimationNames != null) {
            return jlAnimationNames.getSelectedIndex();
        } else {
            return -1;
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        animationDisplay = new AnimationDisplay();
        jbPlay = new JButton();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        animationFramesDisplay = new AnimationFramesDisplay();
        jPanel4 = new JPanel();
        jbOpenNsbtx = new JButton();
        jScrollPane3 = new JScrollPane();
        jlTextureNames = new JList<>();
        jbAddFrame = new JButton();
        jbRemoveFrame = new JButton();
        jsDelay = new JSpinner();
        jLabel2 = new JLabel();
        jPanel3 = new JPanel();
        jScrollPane2 = new JScrollPane();
        jlAnimationNames = new JList<>();
        jbOpenAnimationFile = new JButton();
        jbSaveAnimationFile = new JButton();
        jbAddAnimation = new JButton();
        jbRemoveAnimation = new JButton();
        jtfAnimationName = new JTextField();
        jbApply = new JButton();
        jLabel1 = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Animation Editor");
        setResizable(false);
        setModal(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                formWindowClosed(e);
            }
        });
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Animation Display"));

            //---- animationDisplay ----
            animationDisplay.setBorder(new LineBorder(new Color(102, 102, 102)));

            //---- jbPlay ----
            jbPlay.setForeground(new Color(0, 153, 0));
            jbPlay.setText("\u25b6");
            jbPlay.addActionListener(e -> jbPlayActionPerformed(e));

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(animationDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addComponent(jbPlay)))
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(animationDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jbPlay, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(26, Short.MAX_VALUE))
            );
        }

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Frames"));

            //======== jScrollPane1 ========
            {
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

                //---- animationFramesDisplay ----
                animationFramesDisplay.setPreferredSize(new Dimension(510, 96));
                jScrollPane1.setViewportView(animationFramesDisplay);
            }

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(10, Short.MAX_VALUE))
            );
        }

        //======== jPanel4 ========
        {
            jPanel4.setBorder(new TitledBorder("NSBTX File"));

            //---- jbOpenNsbtx ----
            jbOpenNsbtx.setText("Open NSBTX");
            jbOpenNsbtx.addActionListener(e -> jbOpenNsbtxActionPerformed(e));

            //======== jScrollPane3 ========
            {
                jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane3.setPreferredSize(new Dimension(130, 130));

                //---- jlTextureNames ----
                jlTextureNames.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };
                    @Override
                    public int getSize() { return values.length; }
                    @Override
                    public String getElementAt(int i) { return values[i]; }
                });
                jlTextureNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlTextureNames.addListSelectionListener(e -> jlTextureNamesValueChanged(e));
                jScrollPane3.setViewportView(jlTextureNames);
            }

            //---- jbAddFrame ----
            jbAddFrame.setText("Add Frame");
            jbAddFrame.addActionListener(e -> jbAddFrameActionPerformed(e));

            //---- jbRemoveFrame ----
            jbRemoveFrame.setText("Remove Frame");
            jbRemoveFrame.addActionListener(e -> jbRemoveFrameActionPerformed(e));

            //---- jsDelay ----
            jsDelay.setModel(new SpinnerNumberModel(0, 0, 254, 1));
            jsDelay.addChangeListener(e -> jsDelayStateChanged(e));

            //---- jLabel2 ----
            jLabel2.setText("Delay:");

            GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbRemoveFrame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddFrame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbOpenNsbtx, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jsDelay)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup()
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup()
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jbOpenNsbtx)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbAddFrame)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbRemoveFrame)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jsDelay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
            );
        }

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Animation List"));

            //======== jScrollPane2 ========
            {
                jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jScrollPane2.setPreferredSize(new Dimension(130, 130));

                //---- jlAnimationNames ----
                jlAnimationNames.setModel(new AbstractListModel<String>() {
                    String[] values = {

                    };
                    @Override
                    public int getSize() { return values.length; }
                    @Override
                    public String getElementAt(int i) { return values[i]; }
                });
                jlAnimationNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jlAnimationNames.addListSelectionListener(e -> jlAnimationNamesValueChanged(e));
                jScrollPane2.setViewportView(jlAnimationNames);
            }

            //---- jbOpenAnimationFile ----
            jbOpenAnimationFile.setText("Open Animation File");
            jbOpenAnimationFile.addActionListener(e -> jbOpenAnimationFileActionPerformed(e));

            //---- jbSaveAnimationFile ----
            jbSaveAnimationFile.setText("Save Animation File");
            jbSaveAnimationFile.addActionListener(e -> jbSaveAnimationFileActionPerformed(e));

            //---- jbAddAnimation ----
            jbAddAnimation.setText("Add Animation");
            jbAddAnimation.addActionListener(e -> jbAddAnimationActionPerformed(e));

            //---- jbRemoveAnimation ----
            jbRemoveAnimation.setText("Remove Animation");
            jbRemoveAnimation.addActionListener(e -> jbRemoveAnimationActionPerformed(e));

            //---- jtfAnimationName ----
            jtfAnimationName.setText(" ");

            //---- jbApply ----
            jbApply.setText("Apply");
            jbApply.addActionListener(e -> jbApplyActionPerformed(e));

            //---- jLabel1 ----
            jLabel1.setText("Animation name:");

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup()
                            .addComponent(jbOpenAnimationFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbSaveAnimationFile, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbAddAnimation, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbRemoveAnimation, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup()
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jtfAnimationName, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbApply))
                                    .addComponent(jLabel1))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup()
                            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jbOpenAnimationFile)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbSaveAnimationFile)
                                .addGap(18, 18, 18)
                                .addComponent(jbAddAnimation)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbRemoveAnimation)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jtfAnimationName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbApply))
                                .addGap(0, 20, Short.MAX_VALUE)))
                        .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addGap(5, 5, 5))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(18, 18, 18)
                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        setSize(845, 485);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private AnimationDisplay animationDisplay;
    private JButton jbPlay;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private AnimationFramesDisplay animationFramesDisplay;
    private JPanel jPanel4;
    private JButton jbOpenNsbtx;
    private JScrollPane jScrollPane3;
    private JList<String> jlTextureNames;
    private JButton jbAddFrame;
    private JButton jbRemoveFrame;
    private JSpinner jsDelay;
    private JLabel jLabel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane2;
    private JList<String> jlAnimationNames;
    private JButton jbOpenAnimationFile;
    private JButton jbSaveAnimationFile;
    private JButton jbAddAnimation;
    private JButton jbRemoveAnimation;
    private JTextField jtfAnimationName;
    private JButton jbApply;
    private JLabel jLabel1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
