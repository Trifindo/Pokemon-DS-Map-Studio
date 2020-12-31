package formats.imd;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

import editor.handler.MapEditorHandler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import tileset.NormalsNotFoundException;
import tileset.TextureNotFoundException;
import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class ImdOutputInfoDialog extends JDialog {

    private MapEditorHandler handler;

    private ArrayList<String> fileNames;
    private String objFolderPath;
    private String imdFolderPath;

    private Thread convertingThread;

    private static final Color GREEN = new Color(6, 176, 37);
    private static final Color ORANGE = new Color(255, 106, 0);
    private static final Color RED = Color.red;

    private enum ConvertStatus {
        SUCCESS_STATUS("SUCCESSFULLY CONVERTED", GREEN),
        TOO_MANY_POLYGONS_STATUS("SUCCESSFULLY CONVERTED (TOO MANY POLYGONS)", ORANGE),
        TOO_MANY_TRIANGLES_STATUS("SUCCESSFULLY CONVERTED (TOO MANY TRIANGLES)", ORANGE),
        XML_ERROR_STATUS("NOT CONVERTED (XML ERROR)", RED),
        IO_ERROR_STATUS("NOT CONVERTED (IO ERROR)", RED),
        TEXTURE_ERROR_STATUS("NOT CONVERTED (TEXTURES NOT FOUND)", RED),
        NORMALS_ERROR_STATUS("NOT CONVERTED (NORMALS NOT FOUND)", RED),
        UNKNOWN_ERROR_STATUS("NOT CONVERTED (UNKNOWN ERROR)", RED);

        public final String msg;
        public final Color color;

        private ConvertStatus(String msg, Color color) {
            this.msg = msg;
            this.color = color;
        }
    }

    ;

    public ImdOutputInfoDialog(Window owner) {
        super(owner);
        initComponents();

        getRootPane().setDefaultButton(jbAccept);
        jbAccept.requestFocus();

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);

        jTable1.getColumnModel().getColumn(1).setCellRenderer(new StatusColumnCellRenderer());
    }

    private void formWindowActivated(WindowEvent e) {
        if (convertingThread == null) {
            convertingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    saveAllImds();
                }
            });
            convertingThread.start();
        }
    }

    private void formWindowClosed(WindowEvent e) {
        convertingThread.interrupt();
    }

    private void jbAcceptActionPerformed(ActionEvent e) {
        dispose();
    }

    public void init(MapEditorHandler handler, ArrayList<String> fileNames,
                     String objFolderPath, String imdFolderPath) {
        this.handler = handler;
        this.fileNames = fileNames;
        this.objFolderPath = objFolderPath;
        this.imdFolderPath = imdFolderPath;
    }

    public void saveAllImds() {
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();

        int nFilesProcessed = 0;
        int nFilesConverted = 0;
        int nFilesConvertedWithWarnings = 0;
        int nFilesNotConverted = 0;
        for (String fileName : fileNames) {
            if (!Thread.currentThread().isInterrupted()) {
                System.out.println(nFilesProcessed + " OBJ Processing...");
                String objFileName = Utils.removeExtensionFromPath(fileName) + ".obj";
                String imdFileName = Utils.removeExtensionFromPath(fileName) + ".imd";

                String pathOpen = objFolderPath + File.separator + objFileName;
                String pathSave = imdFolderPath + File.separator + imdFileName;

                ConvertStatus exportStatus;
                ImdModel model = null;
                try {
                    model = new ImdModel(pathOpen, pathSave, handler.getTileset().getMaterials());

                    final int maxNumPolygons = 1300;
                    final int maxNumTris = 1200;
                    if (model.getNumTris() > maxNumTris) {
                        exportStatus = ConvertStatus.TOO_MANY_TRIANGLES_STATUS;
                    } else if (model.getNumPolygons() > maxNumPolygons) {
                        exportStatus = ConvertStatus.TOO_MANY_POLYGONS_STATUS;
                    } else {
                        exportStatus = ConvertStatus.SUCCESS_STATUS;
                    }
                } catch (ParserConfigurationException | TransformerException ex) {
                    exportStatus = ConvertStatus.XML_ERROR_STATUS;
                } catch (IOException ex) {
                    exportStatus = ConvertStatus.IO_ERROR_STATUS;
                } catch (TextureNotFoundException ex) {
                    exportStatus = ConvertStatus.TEXTURE_ERROR_STATUS;
                } catch (NormalsNotFoundException ex) {
                    exportStatus = ConvertStatus.NORMALS_ERROR_STATUS;
                } catch (Exception ex) {
                    exportStatus = ConvertStatus.UNKNOWN_ERROR_STATUS;
                }

                if (exportStatus == ConvertStatus.SUCCESS_STATUS) {
                    tableModel.addRow(new Object[]{
                            fileName,
                            exportStatus,
                            model.getNumMaterials(),
                            model.getNumVertices(),
                            model.getNumPolygons(),
                            model.getNumTris(),
                            model.getNumQuads()
                    });

                    nFilesConverted++;
                } else if (exportStatus == ConvertStatus.TOO_MANY_TRIANGLES_STATUS
                        || exportStatus == ConvertStatus.TOO_MANY_POLYGONS_STATUS) {
                    tableModel.addRow(new Object[]{
                            fileName,
                            exportStatus,
                            model.getNumMaterials(),
                            model.getNumVertices(),
                            model.getNumPolygons(),
                            model.getNumTris(),
                            model.getNumQuads()
                    });

                    nFilesConvertedWithWarnings++;
                    nFilesConverted++;
                } else {
                    tableModel.addRow(new Object[]{
                            fileName,
                            exportStatus,
                            "---",
                            "---",
                            "---",
                            "---",
                            "---"
                    });

                    nFilesNotConverted++;
                }

                nFilesProcessed++;

                jlFilesProcessed.setText(String.valueOf(nFilesProcessed) + "/" + String.valueOf(fileNames.size()));
                jlFilesConverted.setText(String.valueOf(nFilesConverted));
                jlFilesWithWarnings.setText(String.valueOf(nFilesConvertedWithWarnings));
                jlFilesNotConverted.setText(String.valueOf(nFilesNotConverted));

                jProgressBar1.setValue((nFilesProcessed * 100) / fileNames.size());
            }
        }

        if (nFilesConverted > 0) {
            jlFilesConverted.setForeground(GREEN);
        }

        if (nFilesConvertedWithWarnings > 0) {
            jlFilesWithWarnings.setForeground(ORANGE);
        }

        if (nFilesNotConverted > 0) {
            jlFilesNotConverted.setForeground(RED);
        }

        if (nFilesNotConverted > 0) {
            jlStatus.setForeground(RED);
            jlStatus.setText("Finished with errors");

            jlResult.setForeground(RED);
            jlResult.setText(String.valueOf(nFilesNotConverted) + " OBJ file(s) could not be converted into IMD");
        } else if (nFilesConvertedWithWarnings > 0) {
            jlStatus.setForeground(ORANGE);
            jlStatus.setText("Finished with warnings");

            jlResult.setForeground(ORANGE);
            jlResult.setText("All the OBJ files have been converted into IMD (" + String.valueOf(nFilesConvertedWithWarnings) + " file(s) might have too many polygons)");
        } else {
            jlStatus.setForeground(GREEN);
            jlStatus.setText("Finished");

            jlResult.setForeground(GREEN);
            jlResult.setText("All the OBJ files have been converted into IMD");
        }

        jbAccept.setEnabled(true);

        getRootPane().setDefaultButton(jbAccept);
        jbAccept.requestFocus();
    }

    private class StatusColumnCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            ConvertStatus status = (ConvertStatus) value;
            l.setForeground(status.color);
            l.setText(status.msg);

            Font font = l.getFont();
            font = font.deriveFont(
                    Collections.singletonMap(
                            TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD));
            l.setFont(font);

            setHorizontalAlignment(JLabel.CENTER);

            //Return the JLabel which renders the cell.
            return l;

        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel1 = new JPanel();
        jbAccept = new JButton();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jLabel1 = new JLabel();
        jProgressBar1 = new JProgressBar();
        jLabel2 = new JLabel();
        jlFilesProcessed = new JLabel();
        jLabel4 = new JLabel();
        jlFilesConverted = new JLabel();
        jLabel6 = new JLabel();
        jlFilesWithWarnings = new JLabel();
        jLabel8 = new JLabel();
        jlFilesNotConverted = new JLabel();
        jLabel3 = new JLabel();
        jlStatus = new JLabel();
        jLabel5 = new JLabel();
        jlResult = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resulting IMD files info");
        setModal(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                formWindowActivated(e);
            }
            @Override
            public void windowClosed(WindowEvent e) {
                formWindowClosed(e);
            }
        });
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setLayout(new FlowLayout());

            //---- jbAccept ----
            jbAccept.setText("OK");
            jbAccept.setEnabled(false);
            jbAccept.addActionListener(e -> jbAcceptActionPerformed(e));
            jPanel1.add(jbAccept);
        }

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new TitledBorder("Export IMD info"));

            //======== jScrollPane1 ========
            {

                //---- jTable1 ----
                jTable1.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "Name", "Status", "# Materials", "# Vertices", "# Polygons", "# Triangles", "# Quads"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        Object.class, Object.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false, false, false, false, false
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                jScrollPane1.setViewportView(jTable1);
            }

            //---- jLabel1 ----
            jLabel1.setText("IMD exporting progress:");

            //---- jLabel2 ----
            jLabel2.setText("Files processed:");

            //---- jlFilesProcessed ----
            jlFilesProcessed.setFont(new Font("Tahoma", Font.BOLD, 11));
            jlFilesProcessed.setText("0");
            jlFilesProcessed.setToolTipText("");

            //---- jLabel4 ----
            jLabel4.setText("Files converted into IMD:");

            //---- jlFilesConverted ----
            jlFilesConverted.setFont(new Font("Tahoma", Font.BOLD, 11));
            jlFilesConverted.setText("0");

            //---- jLabel6 ----
            jLabel6.setText("Files converted but with warnings :");

            //---- jlFilesWithWarnings ----
            jlFilesWithWarnings.setFont(new Font("Tahoma", Font.BOLD, 11));
            jlFilesWithWarnings.setText("0");

            //---- jLabel8 ----
            jLabel8.setText("Files not converted:");

            //---- jlFilesNotConverted ----
            jlFilesNotConverted.setFont(new Font("Tahoma", Font.BOLD, 11));
            jlFilesNotConverted.setText("0");

            //---- jLabel3 ----
            jLabel3.setText("Status:");

            //---- jlStatus ----
            jlStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
            jlStatus.setText("Converting...");

            //---- jLabel5 ----
            jLabel5.setText("Result:");

            //---- jlResult ----
            jlResult.setFont(new Font("Tahoma", Font.BOLD, 12));
            jlResult.setText(" ");

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlFilesProcessed, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlFilesConverted, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlFilesWithWarnings, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlFilesNotConverted, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlResult, GroupLayout.PREFERRED_SIZE, 572, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jProgressBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlStatus, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                                .addGap(21, 21, 21)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jProgressBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jlStatus))))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jlFilesProcessed))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlFilesConverted))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlFilesWithWarnings))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlFilesNotConverted))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jlResult))
                        .addGap(9, 9, 9))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel1;
    private JButton jbAccept;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JTable jTable1;
    private JLabel jLabel1;
    private JProgressBar jProgressBar1;
    private JLabel jLabel2;
    private JLabel jlFilesProcessed;
    private JLabel jLabel4;
    private JLabel jlFilesConverted;
    private JLabel jLabel6;
    private JLabel jlFilesWithWarnings;
    private JLabel jLabel8;
    private JLabel jlFilesNotConverted;
    private JLabel jLabel3;
    private JLabel jlStatus;
    private JLabel jLabel5;
    private JLabel jlResult;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
