package editor.backsound;

import editor.exceptions.WrongFormatException;
import editor.handler.MapEditorHandler;
import editor.sound.SoundPlayer;
import net.miginfocom.swing.MigLayout;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Trifindo, JackHack96
 */
public class BacksoundEditorDialog extends JDialog {
    private MapEditorHandler handler;
    private BacksoundHandler backsoundHandler;

    private SoundPlayer soundPlayer = new SoundPlayer();

    private boolean plateListEnabled = true;
    private boolean jcbSoundTypeEnabled = true;
    private boolean jsVolumeEnabled = true;

    private static final String[] soundFilenames = new String[]{
            "/sounds/backsounds/sound00.wav",
            "/sounds/backsounds/sound01.wav",
            "/sounds/backsounds/sound02.wav",
            "/sounds/backsounds/sound03.wav",
            "/sounds/backsounds/sound04.wav",
            "/sounds/backsounds/sound05.wav",
            "/sounds/backsounds/sound06.wav",
            "/sounds/backsounds/sound07.wav",
            "/sounds/backsounds/sound08.wav",
            "/sounds/backsounds/sound09.wav",
            "/sounds/backsounds/sound0A.wav",
            "/sounds/backsounds/sound0B.wav",
            "/sounds/backsounds/sound0C.wav",
            "/sounds/backsounds/sound0D.wav",
            "/sounds/backsounds/sound0E.wav",
            "/sounds/backsounds/sound0F.wav"};

    public BacksoundEditorDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void formWindowClosed(WindowEvent e) {
        if (soundPlayer.isAlive()) {
            soundPlayer.stopPlayer();
        }
    }

    private void plateListValueChanged(ListSelectionEvent e) {
        if (plateListEnabled) {
            backsoundHandler.setIndexSelected(plateList.getSelectedIndex());
            updateView();
            backsoundDisplay.repaint();
        }
    }

    private void jbAddPlateActionPerformed(ActionEvent e) {
        backsoundHandler.addSoundplate();
        backsoundHandler.setIndexSelected(backsoundHandler.getSoundplates().size() - 1);

        updateView();
        repaint();
    }

    private void jbRemovePlateActionPerformed(ActionEvent e) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            backsoundHandler.removeSelectedSoundplate();
            updateView();
            repaint();
        }
    }

    private void jcbSoundTypeActionPerformed(ActionEvent e) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            backsoundHandler.getSelectedSoundplate().setSoundCode(jcbSoundType.getSelectedIndex());
            updateViewSoundType();
            backsoundDisplay.repaint();
        }
    }

    private void jsVolumeStateChanged(ChangeEvent e) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            backsoundHandler.getSelectedSoundplate().setVolume(jsVolume.getValue());
            updateViewVolume();
            backsoundDisplay.repaint();
        }
    }

    private void jbPlayPauseActionPerformed(ActionEvent e) {
        if (backsoundHandler.getSoundplates().size() > 0) {
            if (soundPlayer.isAlive()) {
                soundPlayer.stopPlayer();
            } else {
                soundPlayer = new SoundPlayer();
                soundPlayer.init(soundFilenames[backsoundHandler.getSelectedSoundplate().getSoundCode()],
                        () -> {
                            jbPlayPause.setText("Play Sound");
                            jbPlayPause.setForeground(new Color(0, 153, 0));
                        });
                soundPlayer.start();
                jbPlayPause.setText("Stop");
                jbPlayPause.setForeground(Color.red);
            }
        }
    }

    private void jbImportActionPerformed(ActionEvent e) {
        openBacksoundWithDialog();
    }

    private void jbExportActionPerformed(ActionEvent e) {
        saveBacksoundWithDialog();
    }

    public void init(MapEditorHandler handler, BufferedImage mapImage) {
        this.handler = handler;
        this.backsoundHandler = new BacksoundHandler(handler, this);
        this.backsoundDisplay.init(handler, backsoundHandler, mapImage);

        backsoundHandler.setIndexSelected(0);
        updateView();
    }

    public void updateView() {
        updateViewPlateNames();

        updateViewSoundType();
        updateViewVolume();

        if (backsoundHandler.getSoundplates().size() > 0) {
            jsVolume.setEnabled(true);
            jcbSoundType.setEnabled(true);
            jbPlayPause.setEnabled(true);
        } else {
            jsVolume.setEnabled(false);
            jcbSoundType.setEnabled(false);
            jbPlayPause.setEnabled(false);
        }
    }

    private void updateViewSoundType() {
        if (backsoundHandler.getSoundplates().size() > 0) {
            jcbSoundTypeEnabled = false;
            int index = Math.max(0, Math.min(backsoundHandler.getSelectedSoundplate().soundCode, 15));
            jcbSoundType.setSelectedIndex(index);
            jcbSoundTypeEnabled = true;
        }
    }

    private void updateViewVolume() {
        if (backsoundHandler.getSoundplates().size() > 0) {
            jsVolumeEnabled = false;
            int volume = Math.max(0, Math.min(backsoundHandler.getSelectedSoundplate().volume, 2));
            jsVolume.setValue(volume);
            jsVolumeEnabled = true;
        }
    }

    private void updateViewPlateNames() {
        plateListEnabled = false;
        DefaultListModel demoList = new DefaultListModel();
        for (int i = 0; i < backsoundHandler.getSoundplates().size(); i++) {
            String name = "Soundplate " + i;
            demoList.addElement(name);
        }
        plateList.setModel(demoList);
        plateList.setSelectedIndex(backsoundHandler.getIndexSelected());
        plateListEnabled = true;
    }

    public void openBacksoundWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBdhcDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBdhcDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Backsound File (*.bgs)", Backsound.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Background Sound File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                handler.setLastBdhcDirectoryUsed(fc.getSelectedFile().getParent());

                handler.setBacksound(new Backsound(path));

                backsoundHandler.setIndexSelected(0);
                updateView();
                backsoundDisplay.repaint();

            } catch (IOException | WrongFormatException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file", "Error opening Backsound file", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void saveBacksoundWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastBdhcDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastBdhcDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Backsound File (*.bgs)", Backsound.fileExtension));
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save Background Sound File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getPath();
                handler.setLastBdhcDirectoryUsed(fc.getSelectedFile().getParent());
                path = Utils.addExtensionToPath(path, Backsound.fileExtension);

                backsoundHandler.getBacksound().writeToFile(path);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't save file", "Error saving Backsound file", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        backsoundDisplay = new BacksoundDisplay();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        plateList = new JList<>();
        panel1 = new JPanel();
        jbAddPlate = new JButton();
        jbRemovePlate = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        jcbSoundType = new JComboBox<>();
        jLabel2 = new JLabel();
        jsVolume = new JSlider();
        jbPlayPause = new JButton();
        jPanel3 = new JPanel();
        jbImport = new JButton();
        jbExport = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Backsound Editor");
        setModal(true);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                formWindowClosed(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets dialog,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]" +
            "[fill]",
            // rows
            "[grow,fill]" +
            "[fill]" +
            "[fill]"));

        //======== backsoundDisplay ========
        {
            backsoundDisplay.setBorder(new LineBorder(new Color(102, 102, 102)));

            GroupLayout backsoundDisplayLayout = new GroupLayout(backsoundDisplay);
            backsoundDisplay.setLayout(backsoundDisplayLayout);
            backsoundDisplayLayout.setHorizontalGroup(
                backsoundDisplayLayout.createParallelGroup()
                    .addGap(0, 558, Short.MAX_VALUE)
            );
            backsoundDisplayLayout.setVerticalGroup(
                backsoundDisplayLayout.createParallelGroup()
                    .addGap(0, 490, Short.MAX_VALUE)
            );
        }
        contentPane.add(backsoundDisplay, "cell 0 0 1 3");

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("Soundplates"));
            jPanel1.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[grow,fill]",
                // rows
                "[grow,fill]" +
                "[]"));

            //======== jScrollPane1 ========
            {

                //---- plateList ----
                plateList.setModel(new AbstractListModel<String>() {
                    String[] values = {
                        ""
                    };
                    @Override
                    public int getSize() { return values.length; }
                    @Override
                    public String getElementAt(int i) { return values[i]; }
                });
                plateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                plateList.addListSelectionListener(e -> plateListValueChanged(e));
                jScrollPane1.setViewportView(plateList);
            }
            jPanel1.add(jScrollPane1, "cell 0 0");

            //======== panel1 ========
            {
                panel1.setLayout(new GridLayout(0, 2));

                //---- jbAddPlate ----
                jbAddPlate.setIcon(new ImageIcon(getClass().getResource("/icons/AddIcon.png")));
                jbAddPlate.setText("Add Plate");
                jbAddPlate.setPreferredSize(new Dimension(119, 25));
                jbAddPlate.addActionListener(e -> jbAddPlateActionPerformed(e));
                panel1.add(jbAddPlate);

                //---- jbRemovePlate ----
                jbRemovePlate.setIcon(new ImageIcon(getClass().getResource("/icons/RemoveIcon.png")));
                jbRemovePlate.setText("Remove Plate");
                jbRemovePlate.addActionListener(e -> jbRemovePlateActionPerformed(e));
                panel1.add(jbRemovePlate);
            }
            jPanel1.add(panel1, "cell 0 1");
        }
        contentPane.add(jPanel1, "cell 1 0");

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Soundplate Properties"));
            jPanel2.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[grow,fill]",
                // rows
                "[fill]" +
                "[fill]" +
                "[fill]"));

            //---- jLabel1 ----
            jLabel1.setText("Sound type:");
            jPanel2.add(jLabel1, "cell 0 0");

            //---- jcbSoundType ----
            jcbSoundType.setModel(new DefaultComboBoxModel<>(new String[] {
                "Water flow",
                "Wind turbine",
                "Sea waves",
                "Silence 1",
                "Whirlpool",
                "Strong water current",
                "Silence 2",
                "Stadium chant",
                "Ship horn",
                "Silence 3",
                "Sea wave 2",
                "Bells ",
                "Wind",
                "Silence 4",
                "Unknown",
                "Synth horn"
            }));
            jcbSoundType.addActionListener(e -> jcbSoundTypeActionPerformed(e));
            jPanel2.add(jcbSoundType, "cell 1 0");

            //---- jLabel2 ----
            jLabel2.setText("Volume:");
            jPanel2.add(jLabel2, "cell 0 1");

            //---- jsVolume ----
            jsVolume.setMajorTickSpacing(1);
            jsVolume.setMaximum(2);
            jsVolume.setPaintLabels(true);
            jsVolume.setPaintTicks(true);
            jsVolume.setSnapToTicks(true);
            jsVolume.setValue(1);
            jsVolume.addChangeListener(e -> jsVolumeStateChanged(e));
            jPanel2.add(jsVolume, "cell 1 1");

            //---- jbPlayPause ----
            jbPlayPause.setFont(new Font("Tahoma", Font.BOLD, 11));
            jbPlayPause.setForeground(new Color(0, 153, 0));
            jbPlayPause.setText("Play Sound");
            jbPlayPause.addActionListener(e -> jbPlayPauseActionPerformed(e));
            jPanel2.add(jbPlayPause, "cell 0 2 2 1");
        }
        contentPane.add(jPanel2, "cell 1 1");

        //======== jPanel3 ========
        {
            jPanel3.setBorder(new TitledBorder("Import / Export Backsound File"));
            jPanel3.setLayout(new GridLayout());

            //---- jbImport ----
            jbImport.setIcon(new ImageIcon(getClass().getResource("/icons/ImportTileIcon.png")));
            jbImport.setText("Import");
            jbImport.addActionListener(e -> jbImportActionPerformed(e));
            jPanel3.add(jbImport);

            //---- jbExport ----
            jbExport.setIcon(new ImageIcon(getClass().getResource("/icons/ExportIcon.png")));
            jbExport.setText("Export");
            jbExport.addActionListener(e -> jbExportActionPerformed(e));
            jPanel3.add(jbExport);
        }
        contentPane.add(jPanel3, "cell 1 2");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private BacksoundDisplay backsoundDisplay;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JList<String> plateList;
    private JPanel panel1;
    private JButton jbAddPlate;
    private JButton jbRemovePlate;
    private JPanel jPanel2;
    private JLabel jLabel1;
    private JComboBox<String> jcbSoundType;
    private JLabel jLabel2;
    private JSlider jsVolume;
    private JButton jbPlayPause;
    private JPanel jPanel3;
    private JButton jbImport;
    private JButton jbExport;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
