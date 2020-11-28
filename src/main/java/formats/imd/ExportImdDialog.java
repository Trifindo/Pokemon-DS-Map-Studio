package formats.imd;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import net.miginfocom.swing.*;

import utils.swing.*;
import editor.handler.MapEditorHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class ExportImdDialog extends JDialog {

    public static final int APPROVE_OPTION = 1, CANCEL_OPTION = 0;
    private int returnValue = CANCEL_OPTION;

    private String objFolderPath = "";
    private String imdFolderPath = "";
    private ArrayList<String> selectedObjNames = new ArrayList<>();

    private MapEditorHandler handler;

    public ExportImdDialog(Window owner) {
        super(owner);
        initComponents();

        getRootPane().setDefaultButton(jbAccept);
        jbAccept.requestFocus();
    }

    private void jbObjBrowseActionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();
        File folder = new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath)).getParentFile();
        fc.setCurrentDirectory(folder);
        //fc.setSelectedFile(folder);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                try {
                    return f.isDirectory() || f.getName().endsWith(".obj");
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Folder or OBJ file (*.obj)";
            }
        });
        fc.setApproveButtonText("Select folder");
        fc.setDialogTitle("Select the folder that contains the OBJ files");

        int returnValOpen = fc.showOpenDialog(this);
        if (returnValOpen == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (file.exists()) {
                String folderPath;
                if (file.isDirectory()) {
                    folderPath = file.getPath();
                } else {
                    folderPath = file.getParent();
                }
                loadObjFilesFromFolder(folderPath);
            }
        }
    }

    private void jbSelectAllActionPerformed(ActionEvent e) {
        DefaultListModel<JCheckBox> model = (DefaultListModel<JCheckBox>) jScrollCheckboxList.getCheckboxList().getModel();
        for (int i = 0; i < model.getSize(); i++) {
            model.get(i).setSelected(true);
        }
        jScrollCheckboxList.repaint();
    }

    private void jbDeselectAllActionPerformed(ActionEvent e) {
        DefaultListModel<JCheckBox> model = (DefaultListModel<JCheckBox>) jScrollCheckboxList.getCheckboxList().getModel();
        for (int i = 0; i < model.getSize(); i++) {
            model.get(i).setSelected(false);
        }
        jScrollCheckboxList.repaint();
    }

    private void jbCancelActionPerformed(ActionEvent e) {
        returnValue = CANCEL_OPTION;
        dispose();
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        if (!new File(objFolderPath).exists()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a valid folder that contains the OBJ files.",
                    "Invalid OBJ folder",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!new File(imdFolderPath).exists()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a valid output folder for exporting the IMD files.",
                    "Invalid IMD folder",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            DefaultListModel<JCheckBox> model = (DefaultListModel<JCheckBox>) jScrollCheckboxList.getCheckboxList().getModel();
            selectedObjNames = new ArrayList<>(model.getSize());
            for (int i = 0; i < model.getSize(); i++) {
                if (model.get(i).isSelected()) {
                    selectedObjNames.add(model.get(i).getText());
                }
            }
            if (selectedObjNames.size() > 0) {
                returnValue = APPROVE_OPTION;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "There are no OBJ selected for converting into IMD.\n"
                                + "Select at least one OBJ model from the list.", "No OBJ models selected",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void jbImdBrowseActionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();
        File folder = new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath)).getParentFile();
        fc.setCurrentDirectory(folder);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setApproveButtonText("Select folder");
        fc.setDialogTitle("Select the folder for exporting the IMD files");

        int returnValOpen = fc.showOpenDialog(this);
        if (returnValOpen == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (file.exists() && file.isDirectory()) {
                imdFolderPath = file.getPath();
                jtfImdFolderPath.setText(imdFolderPath);
            }
        }
    }

    public void init(MapEditorHandler handler) {
        this.handler = handler;

        String objFolderPath = new File(handler.getMapMatrix().filePath).getParent();
        loadObjFilesFromFolder(objFolderPath);

        String imdFolderPath = new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath)).getParent();
        if (isFolderPathValid(imdFolderPath)) {
            this.imdFolderPath = imdFolderPath;
            jtfImdFolderPath.setText(imdFolderPath);
        }

    }

    private boolean isFolderPathValid(String folderPath) {
        try {
            File file = new File(folderPath);
            return file.isDirectory();
        } catch (Exception ex) {
            return false;
        }
    }

    private void loadObjFilesFromFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".obj");
                }
            });

            DefaultListModel<JCheckBox> model = new DefaultListModel();
            this.jScrollCheckboxList.getCheckboxList().setModel(model);
            for (File file : files) {
                model.addElement(new JCheckBox(file.getName()));
            }

            for (int i = 0; i < model.getSize(); i++) {
                model.get(i).setSelected(hasMatrixCoordsInObjName(model.get(i).getText()));
            }
            jScrollCheckboxList.getCheckboxList().setModel(model);

            objFolderPath = folderPath;
            jtfObjFolderPath.setText(folderPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean hasMatrixCoordsInObjName(String imdName) {
        String name = Utils.removeExtensionFromPath(imdName);
        try {
            String[] splitName = name.split("_");
            return (hasCoordInName(splitName[splitName.length - 2])
                    && hasCoordInName(splitName[splitName.length - 1]));
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean hasCoordInName(String name) {
        try {
            Integer.parseInt(name);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
    private boolean hasMatrixCoordsInObjName(String objName) {
        String name = Utils.removeExtensionFromPath(objName);
        try {
            String[] splitName = name.split("_");
            return (hasCoordInName(splitName[splitName.length - 2], "X")
                    && hasCoordInName(splitName[splitName.length - 1], "Y"));
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean hasCoordInName(String name, String coordChar) {
        try {
            Integer.parseInt(name.substring(1));
            return name.startsWith(coordChar);
        } catch (Exception ex) {
            return false;
        }
    }*/

    public int getReturnValue() {
        return returnValue;
    }

    public String getObjFolderPath() {
        return objFolderPath;
    }

    public String getImdFolderPath() {
        return imdFolderPath;
    }

    public ArrayList<String> getSelectedObjNames() {
        return selectedObjNames;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jtfObjFolderPath = new JTextField();
        jbObjBrowse = new JButton();
        jbSelectAll = new JButton();
        jbDeselectAll = new JButton();
        jScrollCheckboxList = new JScrollCheckboxList();
        jPanel2 = new JPanel();
        jLabel3 = new JLabel();
        jtfImdFolderPath = new JTextField();
        jbImdBrowse = new JButton();
        panel1 = new JPanel();
        jbAccept = new JButton();
        jbCancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export maps as IMD settings");
        setModal(true);
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new TitledBorder("OBJ settings"));

            //---- jLabel1 ----
            jLabel1.setText("Select the OBJ maps that will be converted into IMD:");

            //---- jLabel2 ----
            jLabel2.setText("OBJ folder path:");

            //---- jtfObjFolderPath ----
            jtfObjFolderPath.setEditable(false);

            //---- jbObjBrowse ----
            jbObjBrowse.setText("Browse...");
            jbObjBrowse.addActionListener(e -> jbObjBrowseActionPerformed(e));

            //---- jbSelectAll ----
            jbSelectAll.setText("Select All");
            jbSelectAll.addActionListener(e -> jbSelectAllActionPerformed(e));

            //---- jbDeselectAll ----
            jbDeselectAll.setText("Deselect All");
            jbDeselectAll.addActionListener(e -> jbDeselectAllActionPerformed(e));

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup()
                                    .addComponent(jLabel1)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jbSelectAll)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbDeselectAll))
                                    .addComponent(jScrollCheckboxList, GroupLayout.PREFERRED_SIZE, 340, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 10, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtfObjFolderPath)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbObjBrowse)))
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jtfObjFolderPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbObjBrowse))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollCheckboxList, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jbSelectAll)
                            .addComponent(jbDeselectAll))
                        .addContainerGap())
            );
        }

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("IMD settings"));

            //---- jLabel3 ----
            jLabel3.setText("IMD destination folder path:");

            //---- jtfImdFolderPath ----
            jtfImdFolderPath.setEditable(false);

            //---- jbImdBrowse ----
            jbImdBrowse.setText("Browse...");
            jbImdBrowse.addActionListener(e -> jbImdBrowseActionPerformed(e));

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtfImdFolderPath, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbImdBrowse)
                        .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jtfImdFolderPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbImdBrowse))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3,alignx right",
                // columns
                "[fill]" +
                "[right]",
                // rows
                "[]"));

            //---- jbAccept ----
            jbAccept.setText("OK");
            jbAccept.setPreferredSize(new Dimension(65, 23));
            jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
            panel1.add(jbAccept, "cell 0 0");

            //---- jbCancel ----
            jbCancel.setText("Cancel");
            jbCancel.addActionListener(e -> jbCancelActionPerformed(e));
            panel1.add(jbCancel, "cell 1 0");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTextField jtfObjFolderPath;
    private JButton jbObjBrowse;
    private JButton jbSelectAll;
    private JButton jbDeselectAll;
    private JScrollCheckboxList jScrollCheckboxList;
    private JPanel jPanel2;
    private JLabel jLabel3;
    private JTextField jtfImdFolderPath;
    private JButton jbImdBrowse;
    private JPanel panel1;
    private JButton jbAccept;
    private JButton jbCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
