/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import editor.mapdisplay.MapDisplay;
import com.jogamp.opengl.GLContext;
import editor.about.AboutDialog;
import editor.animationeditor.AnimationEditorDialog;
import editor.backsound.Backsound;
import editor.backsound.BacksoundEditorDialog;
import editor.exceptions.WrongFormatException;
import editor.bdhc.Bdhc;
import editor.bdhc.BdhcEditorDialog;
import editor.bdhc.BdhcLoaderDP;
import editor.bdhc.BdhcLoaderHGSS;
import editor.bdhc.BdhcWriterDP;
import editor.bdhc.BdhcWriterHGSS;
import editor.buildingeditor.BuildingEditorDialog;
import editor.buildingeditor2.BuildHandlerDPPt;
import editor.buildingeditor2.BuildingEditorChooser;
import editor.buildingeditor2.BuildingEditorDialogDPPt;
import editor.buildingeditor2.buildfile.BuildFile;
import editor.collisions.Collisions;
import editor.collisions.CollisionsEditorDialog;
import editor.converter.ConverterDialog;
import editor.converter.ConverterErrorDialog;
import editor.converter.ExportNsbmdDialog;
import editor.converter.ExportNsbmdResultDialog;
import editor.converter.ExportNsbtxDialog;
import editor.converter.ExportNsbtxResultDialog;
import editor.converter.NsbmdOutputInfoDialog;
import editor.converter.NsbtxOutputInfoDialog;
import editor.dae.DaeReader;
import editor.game.Game;
import editor.gameselector.GameChangerDialog;
import editor.gameselector.GameSelectorDialog;
import editor.gameselector.GameTsetSelectorDialog;
import editor.gameselector.GameTsetSelectorDialog2;
import editor.grid.GeometryGL;
import editor.handler.MapEditorHandler;
import editor.grid.MapGrid;
import editor.grid.MapLayerGL;
import editor.handler.MapData;
import editor.imd.ExportImdDialog;
import editor.imd.ImdModel;
import editor.imd.ImdOutputInfo;
import editor.imd.ImdOutputInfoDialog;
import editor.keyboard.KeyboardInfoDialog;
import editor.keyboard.KeyboardInfoDialog2;
import editor.layerselector.ThumbnailLayerSelector;
import editor.mapmatrix.MapMatrix;
import editor.mapmatrix.MapMatrixDisplay;
import editor.mapmatrix.MapMatrixImportDialog;
import editor.narc2.Narc;
import editor.narc2.NarcIO;
import editor.nsbtx.NsbtxEditorDialog;
import editor.nsbtx2.Nsbtx2;
import editor.nsbtx2.NsbtxEditorDialog2;
import editor.nsbtx2.NsbtxLoader2;
import editor.obj.ExportMapObjDialog;
import editor.obj.ObjWriter;
import editor.smartdrawing.SmartGrid;
import editor.state.MapLayerState;
import editor.state.StateHandler;
import editor.tileseteditor.AddTileDialog;
import editor.tileseteditor.ExportTileDialog;
import editor.tileseteditor.TileDisplay;
import editor.tileseteditor.TilesetEditorDialog;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import tileset.NormalsNotFoundException;
import tileset.TextureNotFoundException;
import tileset.Tileset;
import tileset.TilesetIO;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class MainFrame extends javax.swing.JFrame {

    MapEditorHandler handler;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPaneMapMatrix.getHorizontalScrollBar().setUnitIncrement(16);
        jScrollPaneMapMatrix.getVerticalScrollBar().setUnitIncrement(16);

        //jLabel1.setText(System.getProperty("java.version"));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/programIcon.png")));
        setLocationRelativeTo(null);

        //Tileset
        Tileset tileset = new Tileset();
        tileset.getSmartGridArray().add(new SmartGrid());

        TilesetRenderer tr = new TilesetRenderer(tileset);
        try {
            tr.renderTiles();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Border maps tileset
        Tileset borderMapsTileset = new Tileset();

        handler = new MapEditorHandler(this);
        handler.setTileset(tileset);
        handler.setBorderMapsTileset(borderMapsTileset);

        mapDisplay.setHandler(handler);
        tileSelector.init(handler);
        heightSelector.init(handler);
        smartGridDisplay.init(handler, false);
        thumbnailLayerSelector.init(handler);
        updateViewGame();
        tileDisplay.setHandler(handler);
        tileDisplay.setWireframe(true);
        mapMatrixDisplay.init(handler);
        moveMapPanel.init(handler);

        setTitle(handler.getVersionName());

        handler.updateAllMapThumbnails();
        mapMatrixDisplay.updateMapsImage();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupDrawMode = new javax.swing.ButtonGroup();
        buttonGroupViewMode = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jbNewMap = new javax.swing.JButton();
        jbOpenMap = new javax.swing.JButton();
        jbSaveMap = new javax.swing.JButton();
        jbAddMaps = new javax.swing.JButton();
        jbExportObj = new javax.swing.JButton();
        jbExportImd = new javax.swing.JButton();
        jbExportNsb = new javax.swing.JButton();
        jbExportNsb1 = new javax.swing.JButton();
        jbExportNsb2 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jbUndo = new javax.swing.JButton();
        jbRedo = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        jbTilelistEditor = new javax.swing.JButton();
        jbCollisionsEditor = new javax.swing.JButton();
        jbBdhcEditor = new javax.swing.JButton();
        jbBacksoundEditor = new javax.swing.JButton();
        jbNsbtxEditor1 = new javax.swing.JButton();
        jbBuildingEditor = new javax.swing.JButton();
        jbAnimationEditor = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jbKeboardInfo = new javax.swing.JButton();
        jbHelp = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tileSelector = new editor.tileselector.TileSelector();
        jPanel2 = new javax.swing.JPanel();
        heightSelector = new editor.heightselector.HeightSelector();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        smartGridDisplay = new editor.smartdrawing.SmartGridDisplay();
        jPanel5 = new javax.swing.JPanel();
        thumbnailLayerSelector = new editor.layerselector.ThumbnailLayerSelector();
        jlGameIcon = new javax.swing.JLabel();
        jlGame = new javax.swing.JLabel();
        jlGameName = new javax.swing.JLabel();
        mapDisplayContainer = new javax.swing.JPanel();
        mapDisplay = new editor.mapdisplay.MapDisplay();
        jPanel11 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jtbView3D = new javax.swing.JToggleButton();
        jtbViewOrtho = new javax.swing.JToggleButton();
        jtbViewHeight = new javax.swing.JToggleButton();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        jtbViewGrid = new javax.swing.JToggleButton();
        jtbViewWireframe = new javax.swing.JToggleButton();
        jPanel12 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jtbModeEdit = new javax.swing.JToggleButton();
        jtbModeClear = new javax.swing.JToggleButton();
        jtbModeSmartPaint = new javax.swing.JToggleButton();
        jtbModeInvSmartPaint = new javax.swing.JToggleButton();
        jSeparator19 = new javax.swing.JToolBar.Separator();
        jtbModeMove = new javax.swing.JToggleButton();
        jtbModeZoom = new javax.swing.JToggleButton();
        jbFitCameraToMap = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelMatrixInfo = new javax.swing.JPanel();
        jScrollPaneMapMatrix = new javax.swing.JScrollPane();
        mapMatrixDisplay = new editor.mapmatrix.MapMatrixDisplay();
        tileDisplay = new editor.tileseteditor.TileDisplay();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        moveMapPanel = new editor.mapmatrix.MoveMapPanel();
        jLabel1 = new javax.swing.JLabel();
        jsSelectedArea = new javax.swing.JSpinner();
        jPanelAreaColor = new javax.swing.JPanel();
        jPanelMapTools = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jsHeightMapAlpha = new javax.swing.JSlider();
        jPanel8 = new javax.swing.JPanel();
        jsBackImageAlpha = new javax.swing.JSlider();
        jcbRealTimePolyGrouping = new javax.swing.JCheckBox();
        jcbViewAreas = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jbMoveMapUp = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jbMoveMapLeft = new javax.swing.JButton();
        jbMoveMapRight = new javax.swing.JButton();
        jbMoveMapDown = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jbMoveMapUpZ = new javax.swing.JButton();
        jbMoveMapDownZ = new javax.swing.JButton();
        jcbViewGridsBorders = new javax.swing.JCheckBox();
        jPanel14 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlMapCoords = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jlNumPolygons = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlNumMaterials = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jmiNewMap = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jmiOpenMap = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jmiSaveMap = new javax.swing.JMenuItem();
        jmiSaveMapAs = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jmiAddMaps = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jmiExportObjWithText = new javax.swing.JMenuItem();
        jmiExportMapAsImd = new javax.swing.JMenuItem();
        jmiExportMapAsNsb = new javax.swing.JMenuItem();
        jmiExportMapBtx = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jmiImportTileset = new javax.swing.JMenuItem();
        jmiExportTileset = new javax.swing.JMenuItem();
        jmiExportAllTiles = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jmiUndo = new javax.swing.JMenuItem();
        jmiRedo = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jmiClearLayer = new javax.swing.JMenuItem();
        jmiClearAllLayers = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jmiCopyLayer = new javax.swing.JMenuItem();
        jmiPasteLayer = new javax.swing.JMenuItem();
        jmiPasteLayerTiles = new javax.swing.JMenuItem();
        jmiPasteLayerHeights = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jmi3dView = new javax.swing.JMenuItem();
        jmiTopView = new javax.swing.JMenuItem();
        jmiHeightView = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jmiToggleGrid = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jmiLoadBackImg = new javax.swing.JMenuItem();
        jcbUseBackImage = new javax.swing.JCheckBoxMenuItem();
        jmiBuildingEditor = new javax.swing.JMenu();
        jmiTilesetEditor = new javax.swing.JMenuItem();
        jmiCollisionEditor = new javax.swing.JMenuItem();
        jmiBdhcEditor = new javax.swing.JMenuItem();
        jmiNsbtxEditor = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jmiAnimationEditor = new javax.swing.JMenuItem();
        jmHelp = new javax.swing.JMenu();
        jmiKeyboardInfo = new javax.swing.JMenuItem();
        jmiAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Pokemon DS Map Studio 1.10");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMargin(new java.awt.Insets(5, 8, 5, 8));
        jToolBar1.setMaximumSize(new java.awt.Dimension(100, 34));
        jToolBar1.setMinimumSize(new java.awt.Dimension(100, 34));
        jToolBar1.setPreferredSize(new java.awt.Dimension(100, 34));

        jbNewMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/newMapIcon.png"))); // NOI18N
        jbNewMap.setToolTipText("New Map");
        jbNewMap.setBorderPainted(false);
        jbNewMap.setFocusable(false);
        jbNewMap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbNewMap.setIconTextGap(0);
        jbNewMap.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jbNewMap.setMaximumSize(new java.awt.Dimension(38, 38));
        jbNewMap.setMinimumSize(new java.awt.Dimension(38, 38));
        jbNewMap.setPreferredSize(new java.awt.Dimension(38, 38));
        jbNewMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbNewMapActionPerformed(evt);
            }
        });
        jToolBar1.add(jbNewMap);

        jbOpenMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/openMapIcon.png"))); // NOI18N
        jbOpenMap.setToolTipText("Open Map");
        jbOpenMap.setFocusable(false);
        jbOpenMap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbOpenMap.setMaximumSize(new java.awt.Dimension(38, 38));
        jbOpenMap.setMinimumSize(new java.awt.Dimension(38, 38));
        jbOpenMap.setName(""); // NOI18N
        jbOpenMap.setPreferredSize(new java.awt.Dimension(38, 38));
        jbOpenMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbOpenMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOpenMapActionPerformed(evt);
            }
        });
        jToolBar1.add(jbOpenMap);

        jbSaveMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/saveMapIcon.png"))); // NOI18N
        jbSaveMap.setToolTipText("Save Map");
        jbSaveMap.setFocusable(false);
        jbSaveMap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbSaveMap.setMaximumSize(new java.awt.Dimension(38, 38));
        jbSaveMap.setMinimumSize(new java.awt.Dimension(38, 38));
        jbSaveMap.setName(""); // NOI18N
        jbSaveMap.setPreferredSize(new java.awt.Dimension(38, 38));
        jbSaveMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbSaveMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSaveMapActionPerformed(evt);
            }
        });
        jToolBar1.add(jbSaveMap);

        jbAddMaps.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/importMapIcon.png"))); // NOI18N
        jbAddMaps.setToolTipText("Add Maps");
        jbAddMaps.setFocusable(false);
        jbAddMaps.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbAddMaps.setMaximumSize(new java.awt.Dimension(38, 38));
        jbAddMaps.setMinimumSize(new java.awt.Dimension(38, 38));
        jbAddMaps.setName(""); // NOI18N
        jbAddMaps.setPreferredSize(new java.awt.Dimension(38, 38));
        jbAddMaps.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbAddMaps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddMapsActionPerformed(evt);
            }
        });
        jToolBar1.add(jbAddMaps);

        jbExportObj.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exportObjIcon.png"))); // NOI18N
        jbExportObj.setToolTipText("Export Map as OBJ with Textures");
        jbExportObj.setFocusable(false);
        jbExportObj.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbExportObj.setMaximumSize(new java.awt.Dimension(38, 38));
        jbExportObj.setMinimumSize(new java.awt.Dimension(38, 38));
        jbExportObj.setName(""); // NOI18N
        jbExportObj.setPreferredSize(new java.awt.Dimension(38, 38));
        jbExportObj.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbExportObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportObjActionPerformed(evt);
            }
        });
        jToolBar1.add(jbExportObj);

        jbExportImd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exportImdIcon.png"))); // NOI18N
        jbExportImd.setToolTipText("Export Map as IMD");
        jbExportImd.setFocusable(false);
        jbExportImd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbExportImd.setMaximumSize(new java.awt.Dimension(38, 38));
        jbExportImd.setMinimumSize(new java.awt.Dimension(38, 38));
        jbExportImd.setName(""); // NOI18N
        jbExportImd.setPreferredSize(new java.awt.Dimension(38, 38));
        jbExportImd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbExportImd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportImdActionPerformed(evt);
            }
        });
        jToolBar1.add(jbExportImd);

        jbExportNsb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exportNsbIcon.png"))); // NOI18N
        jbExportNsb.setToolTipText("Export Map as NSBMD");
        jbExportNsb.setFocusable(false);
        jbExportNsb.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbExportNsb.setMaximumSize(new java.awt.Dimension(38, 38));
        jbExportNsb.setMinimumSize(new java.awt.Dimension(38, 38));
        jbExportNsb.setName(""); // NOI18N
        jbExportNsb.setPreferredSize(new java.awt.Dimension(38, 38));
        jbExportNsb.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbExportNsb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportNsbActionPerformed(evt);
            }
        });
        jToolBar1.add(jbExportNsb);

        jbExportNsb1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exportBtxIcon.png"))); // NOI18N
        jbExportNsb1.setToolTipText("Export Map NSBTX");
        jbExportNsb1.setFocusable(false);
        jbExportNsb1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbExportNsb1.setMaximumSize(new java.awt.Dimension(38, 38));
        jbExportNsb1.setMinimumSize(new java.awt.Dimension(38, 38));
        jbExportNsb1.setName(""); // NOI18N
        jbExportNsb1.setPreferredSize(new java.awt.Dimension(38, 38));
        jbExportNsb1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbExportNsb1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportNsb1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jbExportNsb1);

        jbExportNsb2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exportAreasIcon.png"))); // NOI18N
        jbExportNsb2.setToolTipText("Export Area NSBTX");
        jbExportNsb2.setFocusable(false);
        jbExportNsb2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbExportNsb2.setMaximumSize(new java.awt.Dimension(38, 38));
        jbExportNsb2.setMinimumSize(new java.awt.Dimension(38, 38));
        jbExportNsb2.setName(""); // NOI18N
        jbExportNsb2.setPreferredSize(new java.awt.Dimension(38, 38));
        jbExportNsb2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbExportNsb2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExportNsb2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jbExportNsb2);
        jToolBar1.add(jSeparator4);

        jbUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/undoIcon.png"))); // NOI18N
        jbUndo.setToolTipText("Undo (Ctrl+Z)");
        jbUndo.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/undoDisabledIcon.png"))); // NOI18N
        jbUndo.setEnabled(false);
        jbUndo.setFocusable(false);
        jbUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbUndo.setMaximumSize(new java.awt.Dimension(38, 38));
        jbUndo.setMinimumSize(new java.awt.Dimension(38, 38));
        jbUndo.setName(""); // NOI18N
        jbUndo.setPreferredSize(new java.awt.Dimension(38, 38));
        jbUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbUndoActionPerformed(evt);
            }
        });
        jToolBar1.add(jbUndo);

        jbRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/redoIcon.png"))); // NOI18N
        jbRedo.setToolTipText("Redo (Ctrl+Y)");
        jbRedo.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/redoDisabledIcon.png"))); // NOI18N
        jbRedo.setEnabled(false);
        jbRedo.setFocusable(false);
        jbRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbRedo.setMaximumSize(new java.awt.Dimension(38, 38));
        jbRedo.setMinimumSize(new java.awt.Dimension(38, 38));
        jbRedo.setName(""); // NOI18N
        jbRedo.setPreferredSize(new java.awt.Dimension(38, 38));
        jbRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRedoActionPerformed(evt);
            }
        });
        jToolBar1.add(jbRedo);
        jToolBar1.add(jSeparator11);

        jbTilelistEditor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/tilelistEditorIcon.png"))); // NOI18N
        jbTilelistEditor.setToolTipText("Tile List Editor");
        jbTilelistEditor.setFocusable(false);
        jbTilelistEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbTilelistEditor.setMaximumSize(new java.awt.Dimension(38, 38));
        jbTilelistEditor.setMinimumSize(new java.awt.Dimension(38, 38));
        jbTilelistEditor.setName(""); // NOI18N
        jbTilelistEditor.setPreferredSize(new java.awt.Dimension(38, 38));
        jbTilelistEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbTilelistEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbTilelistEditorActionPerformed(evt);
            }
        });
        jToolBar1.add(jbTilelistEditor);

        jbCollisionsEditor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/collisionEditorIcon.png"))); // NOI18N
        jbCollisionsEditor.setToolTipText("Collisions Editor");
        jbCollisionsEditor.setFocusable(false);
        jbCollisionsEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbCollisionsEditor.setMaximumSize(new java.awt.Dimension(38, 38));
        jbCollisionsEditor.setMinimumSize(new java.awt.Dimension(38, 38));
        jbCollisionsEditor.setName(""); // NOI18N
        jbCollisionsEditor.setPreferredSize(new java.awt.Dimension(38, 38));
        jbCollisionsEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbCollisionsEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCollisionsEditorActionPerformed(evt);
            }
        });
        jToolBar1.add(jbCollisionsEditor);

        jbBdhcEditor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bdhcEditorIcon.png"))); // NOI18N
        jbBdhcEditor.setToolTipText("BDHC Editor");
        jbBdhcEditor.setFocusable(false);
        jbBdhcEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbBdhcEditor.setMaximumSize(new java.awt.Dimension(38, 38));
        jbBdhcEditor.setMinimumSize(new java.awt.Dimension(38, 38));
        jbBdhcEditor.setName(""); // NOI18N
        jbBdhcEditor.setPreferredSize(new java.awt.Dimension(38, 38));
        jbBdhcEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbBdhcEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbBdhcEditorActionPerformed(evt);
            }
        });
        jToolBar1.add(jbBdhcEditor);

        jbBacksoundEditor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/backsoundEditorIcon.png"))); // NOI18N
        jbBacksoundEditor.setToolTipText("Backsound Editor");
        jbBacksoundEditor.setFocusable(false);
        jbBacksoundEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbBacksoundEditor.setMaximumSize(new java.awt.Dimension(38, 38));
        jbBacksoundEditor.setMinimumSize(new java.awt.Dimension(38, 38));
        jbBacksoundEditor.setName(""); // NOI18N
        jbBacksoundEditor.setPreferredSize(new java.awt.Dimension(38, 38));
        jbBacksoundEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbBacksoundEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbBacksoundEditorActionPerformed(evt);
            }
        });
        jToolBar1.add(jbBacksoundEditor);

        jbNsbtxEditor1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/nsbtxEditorIcon.png"))); // NOI18N
        jbNsbtxEditor1.setToolTipText("NSBTX Editor");
        jbNsbtxEditor1.setFocusable(false);
        jbNsbtxEditor1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbNsbtxEditor1.setMaximumSize(new java.awt.Dimension(38, 38));
        jbNsbtxEditor1.setMinimumSize(new java.awt.Dimension(38, 38));
        jbNsbtxEditor1.setName(""); // NOI18N
        jbNsbtxEditor1.setPreferredSize(new java.awt.Dimension(38, 38));
        jbNsbtxEditor1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbNsbtxEditor1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbNsbtxEditor1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jbNsbtxEditor1);

        jbBuildingEditor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buildingEditorIcon.png"))); // NOI18N
        jbBuildingEditor.setToolTipText("Building Editor");
        jbBuildingEditor.setFocusable(false);
        jbBuildingEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbBuildingEditor.setMaximumSize(new java.awt.Dimension(38, 38));
        jbBuildingEditor.setMinimumSize(new java.awt.Dimension(38, 38));
        jbBuildingEditor.setName(""); // NOI18N
        jbBuildingEditor.setPreferredSize(new java.awt.Dimension(38, 38));
        jbBuildingEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbBuildingEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbBuildingEditorActionPerformed(evt);
            }
        });
        jToolBar1.add(jbBuildingEditor);

        jbAnimationEditor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/animationEditorIcon.png"))); // NOI18N
        jbAnimationEditor.setToolTipText("Animation Editor");
        jbAnimationEditor.setFocusable(false);
        jbAnimationEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbAnimationEditor.setMaximumSize(new java.awt.Dimension(38, 38));
        jbAnimationEditor.setMinimumSize(new java.awt.Dimension(38, 38));
        jbAnimationEditor.setName(""); // NOI18N
        jbAnimationEditor.setPreferredSize(new java.awt.Dimension(38, 38));
        jbAnimationEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbAnimationEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAnimationEditorActionPerformed(evt);
            }
        });
        jToolBar1.add(jbAnimationEditor);
        jToolBar1.add(jSeparator7);

        jbKeboardInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/keyboardInfoIcon.png"))); // NOI18N
        jbKeboardInfo.setToolTipText("Keyboard Shortcuts");
        jbKeboardInfo.setFocusable(false);
        jbKeboardInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbKeboardInfo.setMaximumSize(new java.awt.Dimension(38, 38));
        jbKeboardInfo.setMinimumSize(new java.awt.Dimension(38, 38));
        jbKeboardInfo.setName(""); // NOI18N
        jbKeboardInfo.setPreferredSize(new java.awt.Dimension(38, 38));
        jbKeboardInfo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbKeboardInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbKeboardInfoActionPerformed(evt);
            }
        });
        jToolBar1.add(jbKeboardInfo);

        jbHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/helpIcon.png"))); // NOI18N
        jbHelp.setToolTipText("Help");
        jbHelp.setFocusable(false);
        jbHelp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbHelp.setMaximumSize(new java.awt.Dimension(38, 38));
        jbHelp.setMinimumSize(new java.awt.Dimension(38, 38));
        jbHelp.setName(""); // NOI18N
        jbHelp.setPreferredSize(new java.awt.Dimension(38, 38));
        jbHelp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbHelpActionPerformed(evt);
            }
        });
        jToolBar1.add(jbHelp);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tile List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tileSelector.setPreferredSize(new java.awt.Dimension(128, 0));
        tileSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tileSelectorMousePressed(evt);
            }
        });

        javax.swing.GroupLayout tileSelectorLayout = new javax.swing.GroupLayout(tileSelector);
        tileSelector.setLayout(tileSelectorLayout);
        tileSelectorLayout.setHorizontalGroup(
            tileSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );
        tileSelectorLayout.setVerticalGroup(
            tileSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(tileSelector);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addGap(0, 0, 0))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Z", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        heightSelector.setPreferredSize(new java.awt.Dimension(16, 496));

        javax.swing.GroupLayout heightSelectorLayout = new javax.swing.GroupLayout(heightSelector);
        heightSelector.setLayout(heightSelectorLayout);
        heightSelectorLayout.setHorizontalGroup(
            heightSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        heightSelectorLayout.setVerticalGroup(
            heightSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(heightSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(heightSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Smart Drawing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        javax.swing.GroupLayout smartGridDisplayLayout = new javax.swing.GroupLayout(smartGridDisplay);
        smartGridDisplay.setLayout(smartGridDisplayLayout);
        smartGridDisplayLayout.setHorizontalGroup(
            smartGridDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );
        smartGridDisplayLayout.setVerticalGroup(
            smartGridDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(smartGridDisplay);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Layer", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(204, 102, 0))); // NOI18N

        javax.swing.GroupLayout thumbnailLayerSelectorLayout = new javax.swing.GroupLayout(thumbnailLayerSelector);
        thumbnailLayerSelector.setLayout(thumbnailLayerSelectorLayout);
        thumbnailLayerSelectorLayout.setHorizontalGroup(
            thumbnailLayerSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        thumbnailLayerSelectorLayout.setVerticalGroup(
            thumbnailLayerSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 512, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(thumbnailLayerSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(thumbnailLayerSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jlGameIcon.setText(" ");
        jlGameIcon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlGameIcon.setMaximumSize(new java.awt.Dimension(32, 32));
        jlGameIcon.setMinimumSize(new java.awt.Dimension(32, 32));
        jlGameIcon.setPreferredSize(new java.awt.Dimension(32, 32));
        jlGameIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jlGameIconMousePressed(evt);
            }
        });

        jlGame.setText("Map for: ");

        jlGameName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jlGameName.setText("Game Name");

        mapDisplayContainer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                mapDisplayContainerComponentResized(evt);
            }
        });
        mapDisplayContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        mapDisplay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        mapDisplay.setMaximumSize(new java.awt.Dimension(544, 544));

        javax.swing.GroupLayout mapDisplayLayout = new javax.swing.GroupLayout(mapDisplay);
        mapDisplay.setLayout(mapDisplayLayout);
        mapDisplayLayout.setHorizontalGroup(
            mapDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 542, Short.MAX_VALUE)
        );
        mapDisplayLayout.setVerticalGroup(
            mapDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 542, Short.MAX_VALUE)
        );

        mapDisplayContainer.add(mapDisplay);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "View", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP));

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);

        buttonGroupViewMode.add(jtbView3D);
        jtbView3D.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/3DViewIcon.png"))); // NOI18N
        jtbView3D.setToolTipText("3D View");
        jtbView3D.setFocusable(false);
        jtbView3D.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbView3D.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbView3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbView3DActionPerformed(evt);
            }
        });
        jToolBar2.add(jtbView3D);

        buttonGroupViewMode.add(jtbViewOrtho);
        jtbViewOrtho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/topViewIcon.png"))); // NOI18N
        jtbViewOrtho.setSelected(true);
        jtbViewOrtho.setToolTipText("Top View");
        jtbViewOrtho.setFocusable(false);
        jtbViewOrtho.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbViewOrtho.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbViewOrtho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbViewOrthoActionPerformed(evt);
            }
        });
        jToolBar2.add(jtbViewOrtho);

        buttonGroupViewMode.add(jtbViewHeight);
        jtbViewHeight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/heightViewIcon.png"))); // NOI18N
        jtbViewHeight.setToolTipText("Height View");
        jtbViewHeight.setFocusable(false);
        jtbViewHeight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbViewHeight.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbViewHeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbViewHeightActionPerformed(evt);
            }
        });
        jToolBar2.add(jtbViewHeight);
        jToolBar2.add(jSeparator14);

        jtbViewGrid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/gridViewIcon.png"))); // NOI18N
        jtbViewGrid.setSelected(true);
        jtbViewGrid.setToolTipText("Grid");
        jtbViewGrid.setFocusable(false);
        jtbViewGrid.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbViewGrid.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbViewGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbViewGridActionPerformed(evt);
            }
        });
        jToolBar2.add(jtbViewGrid);

        jtbViewWireframe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/wireViewIcon.png"))); // NOI18N
        jtbViewWireframe.setToolTipText("Wireframe");
        jtbViewWireframe.setFocusable(false);
        jtbViewWireframe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbViewWireframe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbViewWireframe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbViewWireframeActionPerformed(evt);
            }
        });
        jToolBar2.add(jtbViewWireframe);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tools", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP));

        jToolBar3.setFloatable(false);
        jToolBar3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar3.setRollover(true);

        buttonGroupDrawMode.add(jtbModeEdit);
        jtbModeEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/CursorIcon.png"))); // NOI18N
        jtbModeEdit.setSelected(true);
        jtbModeEdit.setToolTipText("Select Mode");
        jtbModeEdit.setFocusable(false);
        jtbModeEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbModeEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbModeEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbModeEditActionPerformed(evt);
            }
        });
        jToolBar3.add(jtbModeEdit);

        buttonGroupDrawMode.add(jtbModeClear);
        jtbModeClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ClearTileIcon.png"))); // NOI18N
        jtbModeClear.setToolTipText("Clear Mode");
        jtbModeClear.setFocusable(false);
        jtbModeClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbModeClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbModeClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbModeClearActionPerformed(evt);
            }
        });
        jToolBar3.add(jtbModeClear);

        buttonGroupDrawMode.add(jtbModeSmartPaint);
        jtbModeSmartPaint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/SmartGridIcon.png"))); // NOI18N
        jtbModeSmartPaint.setToolTipText("Smart Drawing");
        jtbModeSmartPaint.setFocusable(false);
        jtbModeSmartPaint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbModeSmartPaint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbModeSmartPaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbModeSmartPaintActionPerformed(evt);
            }
        });
        jToolBar3.add(jtbModeSmartPaint);

        buttonGroupDrawMode.add(jtbModeInvSmartPaint);
        jtbModeInvSmartPaint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/SmartGridInvertedIcon.png"))); // NOI18N
        jtbModeInvSmartPaint.setToolTipText("Smart Drawing Inverted");
        jtbModeInvSmartPaint.setFocusable(false);
        jtbModeInvSmartPaint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbModeInvSmartPaint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbModeInvSmartPaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbModeInvSmartPaintActionPerformed(evt);
            }
        });
        jToolBar3.add(jtbModeInvSmartPaint);
        jToolBar3.add(jSeparator19);

        buttonGroupDrawMode.add(jtbModeMove);
        jtbModeMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/MoveIcon.png"))); // NOI18N
        jtbModeMove.setToolTipText("Move Camera");
        jtbModeMove.setFocusable(false);
        jtbModeMove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbModeMove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbModeMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbModeMoveActionPerformed(evt);
            }
        });
        jToolBar3.add(jtbModeMove);

        buttonGroupDrawMode.add(jtbModeZoom);
        jtbModeZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ZoomIcon.png"))); // NOI18N
        jtbModeZoom.setToolTipText("Zoom Camera");
        jtbModeZoom.setFocusable(false);
        jtbModeZoom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbModeZoom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbModeZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbModeZoomActionPerformed(evt);
            }
        });
        jToolBar3.add(jtbModeZoom);

        jbFitCameraToMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/fitMapIcon.png"))); // NOI18N
        jbFitCameraToMap.setToolTipText("Fit Camera in Selected Map");
        jbFitCameraToMap.setFocusable(false);
        jbFitCameraToMap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbFitCameraToMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbFitCameraToMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFitCameraToMapActionPerformed(evt);
            }
        });
        jToolBar3.add(jbFitCameraToMap);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jScrollPaneMapMatrix.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneMapMatrix.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPaneMapMatrix.setMaximumSize(new java.awt.Dimension(300, 300));

        mapMatrixDisplay.setPreferredSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout mapMatrixDisplayLayout = new javax.swing.GroupLayout(mapMatrixDisplay);
        mapMatrixDisplay.setLayout(mapMatrixDisplayLayout);
        mapMatrixDisplayLayout.setHorizontalGroup(
            mapMatrixDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 131, Short.MAX_VALUE)
        );
        mapMatrixDisplayLayout.setVerticalGroup(
            mapMatrixDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );

        jScrollPaneMapMatrix.setViewportView(mapMatrixDisplay);

        tileDisplay.setFocusable(false);
        tileDisplay.setPreferredSize(new java.awt.Dimension(140, 140));

        javax.swing.GroupLayout tileDisplayLayout = new javax.swing.GroupLayout(tileDisplay);
        tileDisplay.setLayout(tileDisplayLayout);
        tileDisplayLayout.setHorizontalGroup(
            tileDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        tileDisplayLayout.setVerticalGroup(
            tileDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );

        jLabel3.setText("Tile Selected:");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Move Map", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));
        jPanel7.add(moveMapPanel);

        jLabel1.setText("Area:");

        jsSelectedArea.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        jsSelectedArea.setFocusable(false);
        jsSelectedArea.setPreferredSize(new java.awt.Dimension(40, 20));
        jsSelectedArea.setRequestFocusEnabled(false);
        jsSelectedArea.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsSelectedAreaStateChanged(evt);
            }
        });

        jPanelAreaColor.setBackground(new java.awt.Color(51, 102, 255));
        jPanelAreaColor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanelAreaColorLayout = new javax.swing.GroupLayout(jPanelAreaColor);
        jPanelAreaColor.setLayout(jPanelAreaColorLayout);
        jPanelAreaColorLayout.setHorizontalGroup(
            jPanelAreaColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        jPanelAreaColorLayout.setVerticalGroup(
            jPanelAreaColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelMatrixInfoLayout = new javax.swing.GroupLayout(jPanelMatrixInfo);
        jPanelMatrixInfo.setLayout(jPanelMatrixInfoLayout);
        jPanelMatrixInfoLayout.setHorizontalGroup(
            jPanelMatrixInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMatrixInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMatrixInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneMapMatrix, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(tileDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addGroup(jPanelMatrixInfoLayout.createSequentialGroup()
                        .addGroup(jPanelMatrixInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanelMatrixInfoLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(10, 10, 10)
                                .addComponent(jsSelectedArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelAreaColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelMatrixInfoLayout.setVerticalGroup(
            jPanelMatrixInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMatrixInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneMapMatrix, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelMatrixInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMatrixInfoLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1))
                    .addComponent(jsSelectedArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelAreaColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tileDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Matrix", jPanelMatrixInfo);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Height Map Alpha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jsHeightMapAlpha.setValue(99);
        jsHeightMapAlpha.setFocusable(false);
        jsHeightMapAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsHeightMapAlphaStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsHeightMapAlpha, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsHeightMapAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Back Image Alpha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jsBackImageAlpha.setFocusable(false);
        jsBackImageAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsBackImageAlphaStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsBackImageAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsBackImageAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jcbRealTimePolyGrouping.setSelected(true);
        jcbRealTimePolyGrouping.setText("Real-Time Poly Grouping");
        jcbRealTimePolyGrouping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRealTimePolyGroupingActionPerformed(evt);
            }
        });

        jcbViewAreas.setSelected(true);
        jcbViewAreas.setText("View Area Contours");
        jcbViewAreas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbViewAreasActionPerformed(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Move Layer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jPanel10.setLayout(new java.awt.GridLayout(3, 1, 3, 3));

        jbMoveMapUp.setForeground(new java.awt.Color(0, 153, 0));
        jbMoveMapUp.setText("▲");
        jbMoveMapUp.setFocusable(false);
        jbMoveMapUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMapUpActionPerformed(evt);
            }
        });
        jPanel10.add(jbMoveMapUp);

        jPanel13.setLayout(new java.awt.GridLayout(0, 2, 3, 0));

        jbMoveMapLeft.setForeground(new java.awt.Color(204, 0, 0));
        jbMoveMapLeft.setText("◄");
        jbMoveMapLeft.setFocusable(false);
        jbMoveMapLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMapLeftActionPerformed(evt);
            }
        });
        jPanel13.add(jbMoveMapLeft);

        jbMoveMapRight.setForeground(new java.awt.Color(204, 0, 0));
        jbMoveMapRight.setText("►");
        jbMoveMapRight.setFocusable(false);
        jbMoveMapRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMapRightActionPerformed(evt);
            }
        });
        jPanel13.add(jbMoveMapRight);

        jPanel10.add(jPanel13);

        jbMoveMapDown.setForeground(new java.awt.Color(0, 153, 0));
        jbMoveMapDown.setText("▼");
        jbMoveMapDown.setToolTipText("");
        jbMoveMapDown.setFocusable(false);
        jbMoveMapDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMapDownActionPerformed(evt);
            }
        });
        jPanel10.add(jbMoveMapDown);

        jPanel16.setLayout(new java.awt.GridLayout(2, 0, 0, 3));

        jbMoveMapUpZ.setForeground(new java.awt.Color(0, 0, 255));
        jbMoveMapUpZ.setText("▲");
        jbMoveMapUpZ.setFocusable(false);
        jbMoveMapUpZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMapUpZActionPerformed(evt);
            }
        });
        jPanel16.add(jbMoveMapUpZ);

        jbMoveMapDownZ.setForeground(new java.awt.Color(0, 0, 255));
        jbMoveMapDownZ.setText("▼");
        jbMoveMapDownZ.setFocusable(false);
        jbMoveMapDownZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbMoveMapDownZActionPerformed(evt);
            }
        });
        jPanel16.add(jbMoveMapDownZ);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jcbViewGridsBorders.setSelected(true);
        jcbViewGridsBorders.setText("View Grids Borders");
        jcbViewGridsBorders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbViewGridsBordersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMapToolsLayout = new javax.swing.GroupLayout(jPanelMapTools);
        jPanelMapTools.setLayout(jPanelMapToolsLayout);
        jPanelMapToolsLayout.setHorizontalGroup(
            jPanelMapToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMapToolsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMapToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbViewGridsBorders)
                    .addGroup(jPanelMapToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jcbViewAreas)
                    .addComponent(jcbRealTimePolyGrouping)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelMapToolsLayout.setVerticalGroup(
            jPanelMapToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMapToolsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbRealTimePolyGrouping)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbViewAreas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbViewGridsBorders)
                .addContainerGap(210, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Map Tools", jPanelMapTools);

        jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Selected Map Info:");
        jPanel14.add(jLabel4);

        jLabel6.setText("Coordinates:");
        jPanel14.add(jLabel6);

        jlMapCoords.setText(" ");
        jlMapCoords.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel14.add(jlMapCoords);

        jLabel2.setText("# Polygons:");
        jPanel14.add(jLabel2);

        jlNumPolygons.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlNumPolygons.setText(" ");
        jlNumPolygons.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel14.add(jlNumPolygons);

        jLabel5.setText("# Materials:");
        jPanel14.add(jLabel5);

        jlNumMaterials.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlNumMaterials.setText(" ");
        jlNumMaterials.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel14.add(jlNumMaterials);

        jMenu1.setText("File");

        jmiNewMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/newMapIcon_s.png"))); // NOI18N
        jmiNewMap.setText("New Map...");
        jmiNewMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiNewMapActionPerformed(evt);
            }
        });
        jMenu1.add(jmiNewMap);
        jMenu1.add(jSeparator1);

        jmiOpenMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/openMapIcon_s.png"))); // NOI18N
        jmiOpenMap.setText("Open Map...");
        jmiOpenMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiOpenMapActionPerformed(evt);
            }
        });
        jMenu1.add(jmiOpenMap);
        jMenu1.add(jSeparator2);

        jmiSaveMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/saveMapIconSmall.png"))); // NOI18N
        jmiSaveMap.setText("Save Map...");
        jmiSaveMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiSaveMapActionPerformed(evt);
            }
        });
        jMenu1.add(jmiSaveMap);

        jmiSaveMapAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/saveMapIconSmall.png"))); // NOI18N
        jmiSaveMapAs.setText("Save Map as...");
        jmiSaveMapAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiSaveMapAsActionPerformed(evt);
            }
        });
        jMenu1.add(jmiSaveMapAs);
        jMenu1.add(jSeparator3);

        jmiAddMaps.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddMapIconSmall.png"))); // NOI18N
        jmiAddMaps.setText("Add Maps...");
        jmiAddMaps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiAddMapsActionPerformed(evt);
            }
        });
        jMenu1.add(jmiAddMaps);
        jMenu1.add(jSeparator8);

        jmiExportObjWithText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jmiExportObjWithText.setText("Export Map as OBJ with textures...");
        jmiExportObjWithText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiExportObjWithTextActionPerformed(evt);
            }
        });
        jMenu1.add(jmiExportObjWithText);

        jmiExportMapAsImd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jmiExportMapAsImd.setText("Export Map as IMD...");
        jmiExportMapAsImd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiExportMapAsImdActionPerformed(evt);
            }
        });
        jMenu1.add(jmiExportMapAsImd);

        jmiExportMapAsNsb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jmiExportMapAsNsb.setText("Export Map as NSBMD...");
        jmiExportMapAsNsb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiExportMapAsNsbActionPerformed(evt);
            }
        });
        jMenu1.add(jmiExportMapAsNsb);

        jmiExportMapBtx.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jmiExportMapBtx.setText("Export Map's NSBTX...");
        jmiExportMapBtx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiExportMapBtxActionPerformed(evt);
            }
        });
        jMenu1.add(jmiExportMapBtx);
        jMenu1.add(jSeparator6);

        jmiImportTileset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ImportTileIcon.png"))); // NOI18N
        jmiImportTileset.setText("Import Tileset...");
        jmiImportTileset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiImportTilesetActionPerformed(evt);
            }
        });
        jMenu1.add(jmiImportTileset);

        jmiExportTileset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jmiExportTileset.setText("Export Tileset...");
        jmiExportTileset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiExportTilesetActionPerformed(evt);
            }
        });
        jMenu1.add(jmiExportTileset);

        jmiExportAllTiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ExportIcon.png"))); // NOI18N
        jmiExportAllTiles.setText("Export All Tiles as OBJ...");
        jmiExportAllTiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiExportAllTilesActionPerformed(evt);
            }
        });
        jMenu1.add(jmiExportAllTiles);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jmiUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/undoIconSmall.png"))); // NOI18N
        jmiUndo.setText("Undo");
        jmiUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiUndoActionPerformed(evt);
            }
        });
        jMenu2.add(jmiUndo);

        jmiRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/redoIconSmall.png"))); // NOI18N
        jmiRedo.setText("Redo");
        jmiRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRedoActionPerformed(evt);
            }
        });
        jMenu2.add(jmiRedo);
        jMenu2.add(jSeparator10);

        jmiClearLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/RemoveIcon.png"))); // NOI18N
        jmiClearLayer.setText("Clear Layer");
        jmiClearLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiClearLayerActionPerformed(evt);
            }
        });
        jMenu2.add(jmiClearLayer);

        jmiClearAllLayers.setText("Clear All Layers");
        jmiClearAllLayers.setEnabled(false);
        jmiClearAllLayers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiClearAllLayersActionPerformed(evt);
            }
        });
        jMenu2.add(jmiClearAllLayers);
        jMenu2.add(jSeparator13);

        jmiCopyLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/copyIcon.png"))); // NOI18N
        jmiCopyLayer.setText("Copy Layer");
        jmiCopyLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiCopyLayerActionPerformed(evt);
            }
        });
        jMenu2.add(jmiCopyLayer);

        jmiPasteLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pasteIcon.png"))); // NOI18N
        jmiPasteLayer.setText("Paste Layer");
        jmiPasteLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiPasteLayerActionPerformed(evt);
            }
        });
        jMenu2.add(jmiPasteLayer);

        jmiPasteLayerTiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pasteTileIcon.png"))); // NOI18N
        jmiPasteLayerTiles.setText("Paste Layer Tiles");
        jmiPasteLayerTiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiPasteLayerTilesActionPerformed(evt);
            }
        });
        jMenu2.add(jmiPasteLayerTiles);

        jmiPasteLayerHeights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pasteHeightIcon.png"))); // NOI18N
        jmiPasteLayerHeights.setText("Paste Layer Heights");
        jmiPasteLayerHeights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiPasteLayerHeightsActionPerformed(evt);
            }
        });
        jMenu2.add(jmiPasteLayerHeights);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("View");

        jmi3dView.setText("3D View");
        jmi3dView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmi3dViewActionPerformed(evt);
            }
        });
        jMenu3.add(jmi3dView);

        jmiTopView.setText("Top View");
        jmiTopView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiTopViewActionPerformed(evt);
            }
        });
        jMenu3.add(jmiTopView);

        jmiHeightView.setText("Height View");
        jmiHeightView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiHeightViewActionPerformed(evt);
            }
        });
        jMenu3.add(jmiHeightView);
        jMenu3.add(jSeparator5);

        jmiToggleGrid.setText("Toggle Grid");
        jmiToggleGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiToggleGridActionPerformed(evt);
            }
        });
        jMenu3.add(jmiToggleGrid);
        jMenu3.add(jSeparator9);

        jmiLoadBackImg.setText("Open Background Image");
        jmiLoadBackImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiLoadBackImgActionPerformed(evt);
            }
        });
        jMenu3.add(jmiLoadBackImg);

        jcbUseBackImage.setText("Use Background Image");
        jcbUseBackImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbUseBackImageActionPerformed(evt);
            }
        });
        jMenu3.add(jcbUseBackImage);

        jMenuBar1.add(jMenu3);

        jmiBuildingEditor.setText("Tools");

        jmiTilesetEditor.setText("Tileset Editor");
        jmiTilesetEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiTilesetEditorActionPerformed(evt);
            }
        });
        jmiBuildingEditor.add(jmiTilesetEditor);

        jmiCollisionEditor.setText("Collision Editor");
        jmiCollisionEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiCollisionEditorActionPerformed(evt);
            }
        });
        jmiBuildingEditor.add(jmiCollisionEditor);

        jmiBdhcEditor.setText("BDHC Editor");
        jmiBdhcEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiBdhcEditorActionPerformed(evt);
            }
        });
        jmiBuildingEditor.add(jmiBdhcEditor);

        jmiNsbtxEditor.setText("NSBTX Editor");
        jmiNsbtxEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiNsbtxEditorActionPerformed(evt);
            }
        });
        jmiBuildingEditor.add(jmiNsbtxEditor);

        jMenuItem1.setText("Building Editor");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jmiBuildingEditor.add(jMenuItem1);

        jmiAnimationEditor.setText("Animation Editor");
        jmiAnimationEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiAnimationEditorActionPerformed(evt);
            }
        });
        jmiBuildingEditor.add(jmiAnimationEditor);

        jMenuBar1.add(jmiBuildingEditor);

        jmHelp.setText("Help");

        jmiKeyboardInfo.setText("Keyboard Shortcuts");
        jmiKeyboardInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiKeyboardInfoActionPerformed(evt);
            }
        });
        jmHelp.add(jmiKeyboardInfo);

        jmiAbout.setText("About");
        jmiAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiAboutActionPerformed(evt);
            }
        });
        jmHelp.add(jmiAbout);

        jMenuBar1.add(jmHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mapDisplayContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 937, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlGameName, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlGame))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlGameIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlGameIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlGame)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlGameName)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mapDisplayContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmiNewMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiNewMapActionPerformed
        newMap();
    }//GEN-LAST:event_jmiNewMapActionPerformed

    private void jmiOpenMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiOpenMapActionPerformed
        openMapWithDialog();
    }//GEN-LAST:event_jmiOpenMapActionPerformed

    private void jmiSaveMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSaveMapActionPerformed
        if (handler.getMapMatrix().filePath.isEmpty()) {
            saveMapWithDialog();
        } else {
            saveMap();
        }
    }//GEN-LAST:event_jmiSaveMapActionPerformed

    private void jmiSaveMapAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSaveMapAsActionPerformed
        saveMapWithDialog();
    }//GEN-LAST:event_jmiSaveMapAsActionPerformed

    private void jmiTilesetEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiTilesetEditorActionPerformed
        openTilesetEditor();
    }//GEN-LAST:event_jmiTilesetEditorActionPerformed

    private void jmiExportObjWithTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportObjWithTextActionPerformed
        saveMapAsObjWithDialog(true);
    }//GEN-LAST:event_jmiExportObjWithTextActionPerformed

    private void jbOpenMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOpenMapActionPerformed
        openMapWithDialog();
    }//GEN-LAST:event_jbOpenMapActionPerformed

    private void jbNewMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNewMapActionPerformed
        newMap();
    }//GEN-LAST:event_jbNewMapActionPerformed

    private void jbSaveMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSaveMapActionPerformed
        if (handler.getMapMatrix().filePath.isEmpty()) {
            saveMapWithDialog();
        } else {
            saveMap();
        }
    }//GEN-LAST:event_jbSaveMapActionPerformed

    private void jbExportObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportObjActionPerformed
        saveMapAsObjWithDialog(true);
    }//GEN-LAST:event_jbExportObjActionPerformed

    private void jbExportImdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportImdActionPerformed
        saveMapsAsImdWithDialog();
    }//GEN-LAST:event_jbExportImdActionPerformed

    private void jbHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbHelpActionPerformed
        openAboutDialog();//TODO move this to another button
    }//GEN-LAST:event_jbHelpActionPerformed

    private void jmiImportTilesetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiImportTilesetActionPerformed
        openTilesetWithDialog();
    }//GEN-LAST:event_jmiImportTilesetActionPerformed

    private void jbExportNsbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportNsbActionPerformed
        saveMapsAsNsbWithDialog();
    }//GEN-LAST:event_jbExportNsbActionPerformed

    private void jbTilelistEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbTilelistEditorActionPerformed
        openTilesetEditor();
    }//GEN-LAST:event_jbTilelistEditorActionPerformed

    private void jbBdhcEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBdhcEditorActionPerformed
        openBdhcEditor();
    }//GEN-LAST:event_jbBdhcEditorActionPerformed

    private void jbCollisionsEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCollisionsEditorActionPerformed
        openCollisionsEditor();
    }//GEN-LAST:event_jbCollisionsEditorActionPerformed

    private void jbKeboardInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbKeboardInfoActionPerformed
        openKeyboardInfoDialog();
    }//GEN-LAST:event_jbKeboardInfoActionPerformed

    private void jmiExportTilesetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportTilesetActionPerformed
        saveTilesetWithDialog();
    }//GEN-LAST:event_jmiExportTilesetActionPerformed

    private void jmi3dViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmi3dViewActionPerformed
        mapDisplay.set3DView();
        mapDisplay.repaint();
    }//GEN-LAST:event_jmi3dViewActionPerformed

    private void jmiTopViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiTopViewActionPerformed
        mapDisplay.setOrthoView();
        mapDisplay.repaint();
    }//GEN-LAST:event_jmiTopViewActionPerformed

    private void jmiHeightViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiHeightViewActionPerformed
        mapDisplay.setHeightView();
        mapDisplay.repaint();
    }//GEN-LAST:event_jmiHeightViewActionPerformed

    private void jmiToggleGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiToggleGridActionPerformed
        mapDisplay.toggleGridView();
        mapDisplay.repaint();
    }//GEN-LAST:event_jmiToggleGridActionPerformed

    private void jmiCollisionEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCollisionEditorActionPerformed
        openCollisionsEditor();
    }//GEN-LAST:event_jmiCollisionEditorActionPerformed

    private void jmiBdhcEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiBdhcEditorActionPerformed
        openBdhcEditor();
    }//GEN-LAST:event_jmiBdhcEditorActionPerformed

    private void jmiNsbtxEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiNsbtxEditorActionPerformed
        openNsbtxEditor();
    }//GEN-LAST:event_jmiNsbtxEditorActionPerformed

    private void jmiKeyboardInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiKeyboardInfoActionPerformed
        openKeyboardInfoDialog();
    }//GEN-LAST:event_jmiKeyboardInfoActionPerformed

    private void jmiAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiAboutActionPerformed
        openAboutDialog();
    }//GEN-LAST:event_jmiAboutActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int returnVal = JOptionPane.showConfirmDialog(this,
                "Do you want to exit Pokemon DS Map Studio?",
                "Closing Pokemon DS Map Studio", JOptionPane.YES_NO_OPTION);
        if (returnVal == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jcbUseBackImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbUseBackImageActionPerformed
        mapDisplay.setBackImageEnabled(jcbUseBackImage.isSelected());
        mapDisplay.repaint();
    }//GEN-LAST:event_jcbUseBackImageActionPerformed

    private void jmiLoadBackImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiLoadBackImgActionPerformed
        openBackImgWithDialog();
    }//GEN-LAST:event_jmiLoadBackImgActionPerformed

    private void jmiClearLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiClearLayerActionPerformed
        handler.clearLayer(handler.getActiveLayerIndex());
    }//GEN-LAST:event_jmiClearLayerActionPerformed

    private void jmiClearAllLayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiClearAllLayersActionPerformed
        handler.getGrid().clearAllLayers();
        thumbnailLayerSelector.drawAllLayerThumbnails();
        thumbnailLayerSelector.repaint();
        mapDisplay.updateMapLayersGL();
        mapDisplay.repaint();
    }//GEN-LAST:event_jmiClearAllLayersActionPerformed

    private void jbRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRedoActionPerformed
        redoMapState();
    }//GEN-LAST:event_jbRedoActionPerformed

    private void jbUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbUndoActionPerformed
        undoMapState();
    }//GEN-LAST:event_jbUndoActionPerformed

    private void jbExportNsb1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportNsb1ActionPerformed
        saveMapBtxWithDialog();
    }//GEN-LAST:event_jbExportNsb1ActionPerformed

    private void jmiExportMapAsImdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportMapAsImdActionPerformed
        saveMapAsImdWithDialog();
    }//GEN-LAST:event_jmiExportMapAsImdActionPerformed

    private void jmiExportMapAsNsbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportMapAsNsbActionPerformed
        saveMapAsNsbWithDialog();
    }//GEN-LAST:event_jmiExportMapAsNsbActionPerformed

    private void jmiExportMapBtxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportMapBtxActionPerformed
        saveMapBtxWithDialog();
    }//GEN-LAST:event_jmiExportMapBtxActionPerformed

    private void jmiUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiUndoActionPerformed
        undoMapState();
    }//GEN-LAST:event_jmiUndoActionPerformed

    private void jmiRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRedoActionPerformed
        redoMapState();
    }//GEN-LAST:event_jmiRedoActionPerformed

    private void jmiExportAllTilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportAllTilesActionPerformed
        saveAllTilesAsObjWithDialog();

    }//GEN-LAST:event_jmiExportAllTilesActionPerformed

    private void jlGameIconMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlGameIconMousePressed
        changeGame();
    }//GEN-LAST:event_jlGameIconMousePressed

    private void jsHeightMapAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsHeightMapAlphaStateChanged
        mapDisplay.setHeightMapAlpha(jsHeightMapAlpha.getValue() / 100f);
        mapDisplay.repaint();
    }//GEN-LAST:event_jsHeightMapAlphaStateChanged

    private void jbBuildingEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBuildingEditorActionPerformed
        openBuildingEditor2();
    }//GEN-LAST:event_jbBuildingEditorActionPerformed

    private void jbNsbtxEditor1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNsbtxEditor1ActionPerformed
        openNsbtxEditor2();
    }//GEN-LAST:event_jbNsbtxEditor1ActionPerformed

    private void jbAnimationEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAnimationEditorActionPerformed
        openAnimationEditor();
    }//GEN-LAST:event_jbAnimationEditorActionPerformed

    private void jbMoveMapUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMapUpActionPerformed
        moveTilesUp();
    }//GEN-LAST:event_jbMoveMapUpActionPerformed

    private void jbMoveMapDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMapDownActionPerformed
        moveTilesDown();
    }//GEN-LAST:event_jbMoveMapDownActionPerformed

    private void jbMoveMapLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMapLeftActionPerformed
        moveTilesLeft();
    }//GEN-LAST:event_jbMoveMapLeftActionPerformed

    private void jbMoveMapRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMapRightActionPerformed
        moveTilesRight();
    }//GEN-LAST:event_jbMoveMapRightActionPerformed

    private void jsBackImageAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsBackImageAlphaStateChanged
        mapDisplay.setBackImageAlpha(jsBackImageAlpha.getValue() / 100f);
        mapDisplay.repaint();
    }//GEN-LAST:event_jsBackImageAlphaStateChanged

    private void tileSelectorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tileSelectorMousePressed
        repaintTileDisplay();
    }//GEN-LAST:event_tileSelectorMousePressed

    private void jmiCopyLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCopyLayerActionPerformed
        if (handler.getTileset().size() > 0) {
            handler.copySelectedLayer();
        }
    }//GEN-LAST:event_jmiCopyLayerActionPerformed

    private void jmiPasteLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiPasteLayerActionPerformed
        handler.pasteLayer(handler.getActiveLayerIndex());
    }//GEN-LAST:event_jmiPasteLayerActionPerformed

    private void jmiPasteLayerTilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiPasteLayerTilesActionPerformed
        handler.pasteLayerTiles(handler.getActiveLayerIndex());
    }//GEN-LAST:event_jmiPasteLayerTilesActionPerformed

    private void jmiPasteLayerHeightsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiPasteLayerHeightsActionPerformed
        handler.pasteLayerHeights(handler.getActiveLayerIndex());
    }//GEN-LAST:event_jmiPasteLayerHeightsActionPerformed

    private void jbBacksoundEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBacksoundEditorActionPerformed
        openBacksoundEditor();
    }//GEN-LAST:event_jbBacksoundEditorActionPerformed

    private void mapDisplayContainerComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_mapDisplayContainerComponentResized
        int size = Math.min(mapDisplayContainer.getWidth(), mapDisplayContainer.getHeight());
        mapDisplay.setPreferredSize(new Dimension(size, size));
        mapDisplayContainer.revalidate();
        //mapDisplay.repaint();
    }//GEN-LAST:event_mapDisplayContainerComponentResized

    private void jtbView3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbView3DActionPerformed
        mapDisplay.set3DView();
        mapDisplay.repaint();
    }//GEN-LAST:event_jtbView3DActionPerformed

    private void jtbViewOrthoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbViewOrthoActionPerformed
        mapDisplay.setOrthoView();
        mapDisplay.repaint();
    }//GEN-LAST:event_jtbViewOrthoActionPerformed

    private void jtbViewHeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbViewHeightActionPerformed
        mapDisplay.setHeightView();
        mapDisplay.repaint();
    }//GEN-LAST:event_jtbViewHeightActionPerformed

    private void jtbViewGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbViewGridActionPerformed
        mapDisplay.setGridEnabled(jtbViewGrid.isSelected());
        mapDisplay.repaint();
    }//GEN-LAST:event_jtbViewGridActionPerformed

    private void jtbModeEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbModeEditActionPerformed
        mapDisplay.setEditMode(MapDisplay.EditMode.MODE_EDIT);
    }//GEN-LAST:event_jtbModeEditActionPerformed

    private void jtbModeMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbModeMoveActionPerformed
        mapDisplay.setEditMode(MapDisplay.EditMode.MODE_MOVE);
    }//GEN-LAST:event_jtbModeMoveActionPerformed

    private void jtbModeZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbModeZoomActionPerformed
        mapDisplay.setEditMode(MapDisplay.EditMode.MODE_ZOOM);
    }//GEN-LAST:event_jtbModeZoomActionPerformed

    private void jtbModeClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbModeClearActionPerformed
        mapDisplay.setEditMode(MapDisplay.EditMode.MODE_CLEAR);
    }//GEN-LAST:event_jtbModeClearActionPerformed

    private void jtbModeSmartPaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbModeSmartPaintActionPerformed
        mapDisplay.setEditMode(MapDisplay.EditMode.MODE_SMART_PAINT);
    }//GEN-LAST:event_jtbModeSmartPaintActionPerformed

    private void jtbModeInvSmartPaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbModeInvSmartPaintActionPerformed
        mapDisplay.setEditMode(MapDisplay.EditMode.MODE_INV_SMART_PAINT);
    }//GEN-LAST:event_jtbModeInvSmartPaintActionPerformed

    private void jbFitCameraToMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFitCameraToMapActionPerformed
        mapDisplay.setCameraAtSelectedMap();
        mapDisplay.repaint();
    }//GEN-LAST:event_jbFitCameraToMapActionPerformed

    private void jsSelectedAreaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsSelectedAreaStateChanged
        try {
            handler.getMapData().setAreaIndex((Integer) jsSelectedArea.getValue());
            handler.getMapMatrix().updateBordersData();
            mapMatrixDisplay.updateMapsImage();
            mapMatrixDisplay.repaint();
            mapDisplay.repaint();

            jPanelAreaColor.setBackground(handler.getMapMatrix().getAreaColors().get(handler.getMapData().getAreaIndex()));
            jPanelAreaColor.repaint();
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_jsSelectedAreaStateChanged

    private void jbExportNsb2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExportNsb2ActionPerformed
        saveAreasAsBtxWithDialog();
    }//GEN-LAST:event_jbExportNsb2ActionPerformed

    private void jcbRealTimePolyGroupingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRealTimePolyGroupingActionPerformed
        handler.setRealTimePostProcessing(jcbRealTimePolyGrouping.isSelected());
        handler.getMapMatrix().updateAllLayersGL();
        mapDisplay.repaint();
        updateViewGeometryCount();
    }//GEN-LAST:event_jcbRealTimePolyGroupingActionPerformed

    private void jtbViewWireframeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbViewWireframeActionPerformed
        mapDisplay.setDrawWireframeEnabled(jtbViewWireframe.isSelected());
        mapDisplay.repaint();
    }//GEN-LAST:event_jtbViewWireframeActionPerformed

    private void jcbViewAreasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbViewAreasActionPerformed
        mapDisplay.setDrawAreasEnabled(jcbViewAreas.isSelected());
        mapDisplay.repaint();
    }//GEN-LAST:event_jcbViewAreasActionPerformed

    private void jbMoveMapUpZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMapUpZActionPerformed
        moveTilesUpZ();
    }//GEN-LAST:event_jbMoveMapUpZActionPerformed

    private void jbMoveMapDownZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbMoveMapDownZActionPerformed
        moveTilesDownZ();
    }//GEN-LAST:event_jbMoveMapDownZActionPerformed

    private void jbAddMapsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddMapsActionPerformed
        addMapWithDialog();
    }//GEN-LAST:event_jbAddMapsActionPerformed

    private void jcbViewGridsBordersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbViewGridsBordersActionPerformed
        mapDisplay.setDrawGridBorderMaps(jcbViewGridsBorders.isSelected());
        mapDisplay.repaint();
    }//GEN-LAST:event_jcbViewGridsBordersActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        openBuildingEditor2();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jmiAnimationEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiAnimationEditorActionPerformed
        openAnimationEditor();
    }//GEN-LAST:event_jmiAnimationEditorActionPerformed

    private void jmiAddMapsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiAddMapsActionPerformed
        addMapWithDialog();
    }//GEN-LAST:event_jmiAddMapsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName()) || "GTK+".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);

                if (args.length > 0) {
                    try {
                        if (args[0].endsWith(MapMatrix.fileExtension)) {
                            mainFrame.openMap(args[0]);
                        } else if (args[0].endsWith(Tileset.fileExtension)) {
                            mainFrame.openTileset(args[0]);
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupDrawMode;
    private javax.swing.ButtonGroup buttonGroupViewMode;
    private editor.heightselector.HeightSelector heightSelector;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAreaColor;
    private javax.swing.JPanel jPanelMapTools;
    private javax.swing.JPanel jPanelMatrixInfo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneMapMatrix;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JToolBar.Separator jSeparator19;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JButton jbAddMaps;
    private javax.swing.JButton jbAnimationEditor;
    private javax.swing.JButton jbBacksoundEditor;
    private javax.swing.JButton jbBdhcEditor;
    private javax.swing.JButton jbBuildingEditor;
    private javax.swing.JButton jbCollisionsEditor;
    private javax.swing.JButton jbExportImd;
    private javax.swing.JButton jbExportNsb;
    private javax.swing.JButton jbExportNsb1;
    private javax.swing.JButton jbExportNsb2;
    private javax.swing.JButton jbExportObj;
    private javax.swing.JButton jbFitCameraToMap;
    private javax.swing.JButton jbHelp;
    private javax.swing.JButton jbKeboardInfo;
    private javax.swing.JButton jbMoveMapDown;
    private javax.swing.JButton jbMoveMapDownZ;
    private javax.swing.JButton jbMoveMapLeft;
    private javax.swing.JButton jbMoveMapRight;
    private javax.swing.JButton jbMoveMapUp;
    private javax.swing.JButton jbMoveMapUpZ;
    private javax.swing.JButton jbNewMap;
    private javax.swing.JButton jbNsbtxEditor1;
    private javax.swing.JButton jbOpenMap;
    private javax.swing.JButton jbRedo;
    private javax.swing.JButton jbSaveMap;
    private javax.swing.JButton jbTilelistEditor;
    private javax.swing.JButton jbUndo;
    private javax.swing.JCheckBox jcbRealTimePolyGrouping;
    private javax.swing.JCheckBoxMenuItem jcbUseBackImage;
    private javax.swing.JCheckBox jcbViewAreas;
    private javax.swing.JCheckBox jcbViewGridsBorders;
    private javax.swing.JLabel jlGame;
    private javax.swing.JLabel jlGameIcon;
    private javax.swing.JLabel jlGameName;
    private javax.swing.JLabel jlMapCoords;
    private javax.swing.JLabel jlNumMaterials;
    private javax.swing.JLabel jlNumPolygons;
    private javax.swing.JMenu jmHelp;
    private javax.swing.JMenuItem jmi3dView;
    private javax.swing.JMenuItem jmiAbout;
    private javax.swing.JMenuItem jmiAddMaps;
    private javax.swing.JMenuItem jmiAnimationEditor;
    private javax.swing.JMenuItem jmiBdhcEditor;
    private javax.swing.JMenu jmiBuildingEditor;
    private javax.swing.JMenuItem jmiClearAllLayers;
    private javax.swing.JMenuItem jmiClearLayer;
    private javax.swing.JMenuItem jmiCollisionEditor;
    private javax.swing.JMenuItem jmiCopyLayer;
    private javax.swing.JMenuItem jmiExportAllTiles;
    private javax.swing.JMenuItem jmiExportMapAsImd;
    private javax.swing.JMenuItem jmiExportMapAsNsb;
    private javax.swing.JMenuItem jmiExportMapBtx;
    private javax.swing.JMenuItem jmiExportObjWithText;
    private javax.swing.JMenuItem jmiExportTileset;
    private javax.swing.JMenuItem jmiHeightView;
    private javax.swing.JMenuItem jmiImportTileset;
    private javax.swing.JMenuItem jmiKeyboardInfo;
    private javax.swing.JMenuItem jmiLoadBackImg;
    private javax.swing.JMenuItem jmiNewMap;
    private javax.swing.JMenuItem jmiNsbtxEditor;
    private javax.swing.JMenuItem jmiOpenMap;
    private javax.swing.JMenuItem jmiPasteLayer;
    private javax.swing.JMenuItem jmiPasteLayerHeights;
    private javax.swing.JMenuItem jmiPasteLayerTiles;
    private javax.swing.JMenuItem jmiRedo;
    private javax.swing.JMenuItem jmiSaveMap;
    private javax.swing.JMenuItem jmiSaveMapAs;
    private javax.swing.JMenuItem jmiTilesetEditor;
    private javax.swing.JMenuItem jmiToggleGrid;
    private javax.swing.JMenuItem jmiTopView;
    private javax.swing.JMenuItem jmiUndo;
    private javax.swing.JSlider jsBackImageAlpha;
    private javax.swing.JSlider jsHeightMapAlpha;
    private javax.swing.JSpinner jsSelectedArea;
    private javax.swing.JToggleButton jtbModeClear;
    private javax.swing.JToggleButton jtbModeEdit;
    private javax.swing.JToggleButton jtbModeInvSmartPaint;
    private javax.swing.JToggleButton jtbModeMove;
    private javax.swing.JToggleButton jtbModeSmartPaint;
    private javax.swing.JToggleButton jtbModeZoom;
    private javax.swing.JToggleButton jtbView3D;
    private javax.swing.JToggleButton jtbViewGrid;
    private javax.swing.JToggleButton jtbViewHeight;
    private javax.swing.JToggleButton jtbViewOrtho;
    private javax.swing.JToggleButton jtbViewWireframe;
    private editor.mapdisplay.MapDisplay mapDisplay;
    private javax.swing.JPanel mapDisplayContainer;
    private editor.mapmatrix.MapMatrixDisplay mapMatrixDisplay;
    private editor.mapmatrix.MoveMapPanel moveMapPanel;
    private editor.smartdrawing.SmartGridDisplay smartGridDisplay;
    private editor.layerselector.ThumbnailLayerSelector thumbnailLayerSelector;
    private editor.tileseteditor.TileDisplay tileDisplay;
    private editor.tileselector.TileSelector tileSelector;
    // End of variables declaration//GEN-END:variables

    public void openMap(String path) {
        try {
            String folderPath = new File(path).getParent();
            String fileName = new File(path).getName();
            handler.setLastMapDirectoryUsed(folderPath);

            handler.getMapMatrix().loadGridsFromFile(path);
            handler.getMapMatrix().filePath = path;
            handler.setDefaultMapSelected();

            setTitle(handler.getMapName() + " - " + handler.getVersionName());

            handler.resetMapStateHandler();
            jbUndo.setEnabled(false);
            jbRedo.setEnabled(false);

            try {
                Tileset tileset = TilesetIO.readTilesetFromFile(handler.getMapMatrix().tilesetFilePath);
                handler.setTileset(tileset);
                System.out.println("Textures loaded from path: " + new File(path).getParent());

                renderTilesetThumbnails();

                handler.setIndexTileSelected(0);
                handler.setSmartGridIndexSelected(0);

                handler.getMapMatrix().updateAllLayersGL();
                handler.getMapMatrix().updateBordersData();
                handler.updateAllMapThumbnails();
                mapMatrixDisplay.updateSize();
                updateMapMatrixDisplay();
                updateViewMapInfo();

                tileSelector.updateLayout();
                tileSelector.repaint();
                mapDisplay.requestUpdate();
                mapDisplay.setCameraAtSelectedMap();
                mapDisplay.repaint();
                tileDisplay.requestUpdate();
                tileDisplay.repaint();

                smartGridDisplay.updateSize();
                smartGridDisplay.repaint();
                thumbnailLayerSelector.drawAllLayerThumbnails();
                thumbnailLayerSelector.repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error opening map", JOptionPane.ERROR_MESSAGE);
            } catch (TextureNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error opening map", JOptionPane.ERROR_MESSAGE);
            }

            handler.getMapMatrix().loadBDHCsFromFile(folderPath, fileName);
            handler.getMapMatrix().loadBacksoundsFromFile(folderPath, fileName);
            handler.getMapMatrix().loadCollisionsFromFile(folderPath, fileName);
            handler.getMapMatrix().loadBuildingsFromFile(folderPath, fileName);

            updateViewGame();

            repaintHeightSelector();
            repaintTileSelector();
            repaintMapDisplay();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Can't open file", "Error opening map", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Can't open file", "Error opening map", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openMapWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }

        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS map (*.pdsmap)", MapMatrix.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Map");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            openMap(fc.getSelectedFile().getPath());
        }
    }

    public void addMapWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }

        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS map (*.pdsmap)", MapMatrix.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Add Maps from PDSMAP file");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (fc.getSelectedFile().exists()) {
                handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    HashMap<Point, MapData> maps = MapMatrix.getGridsFromFile(fc.getSelectedFile().getPath(), handler);

                    final MapMatrixImportDialog dialog = new MapMatrixImportDialog(this, true);
                    dialog.init(handler, fc.getSelectedFile().getPath(), maps);
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "There was a problem importing the maps",
                            "Can't add maps", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    public void openTilesetEditor() {
        final TilesetEditorDialog dialog = new TilesetEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (handler.getTileset().size() > 0) {
            handler.getTileset().removeUnusedTextures();
            dialog.fixIndices();
            tileSelector.updateLayout();
            handler.getMapMatrix().updateAllLayersGL();
            handler.getMapMatrix().updateBordersData();
            handler.updateAllMapThumbnails();
            mapMatrixDisplay.updateSize();
            updateMapMatrixDisplay();
            mapDisplay.requestUpdate();
            mapDisplay.repaint();
            tileDisplay.requestUpdate();
            tileDisplay.repaint();
            smartGridDisplay.updateSize();
            smartGridDisplay.repaint();
            thumbnailLayerSelector.drawAllLayerThumbnails();
            thumbnailLayerSelector.repaint();
        }

        repaint();
    }

    public void openCollisionsEditor() {
        mapDisplay.requestScreenshot();
        mapDisplay.setOrthoView();
        mapDisplay.setCameraAtSelectedMap();
        boolean gridEnabled = mapDisplay.isGridEnabled();
        mapDisplay.disableGridView();
        mapDisplay.display();
        final CollisionsEditorDialog dialog = new CollisionsEditorDialog(this, true);
        dialog.init(handler, mapDisplay.getScreenshot());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        mapDisplay.setGridEnabled(gridEnabled);
        mapDisplay.display();
    }

    public void openBdhcEditor() {
        if (handler.getGame().gameSelected < Game.BLACK) {
            mapDisplay.requestScreenshot();
            mapDisplay.setOrthoView();
            mapDisplay.setCameraAtSelectedMap();
            boolean gridEnabled = mapDisplay.isGridEnabled();
            mapDisplay.disableGridView();
            mapDisplay.display();
            final BdhcEditorDialog dialog = new BdhcEditorDialog(this, true);
            dialog.init(handler, mapDisplay.getScreenshot());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            mapDisplay.setGridEnabled(gridEnabled);
            mapDisplay.display();
        } else {
            JOptionPane.showMessageDialog(this, "Gen V Games do not have BDHC files",
                    "BDHC editor is not available", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openBacksoundEditor() {
        if (handler.getGame().gameSelected == Game.HEART_GOLD || handler.getGame().gameSelected == Game.SOUL_SILVER) {
            mapDisplay.requestScreenshot();
            mapDisplay.setOrthoView();
            mapDisplay.setCameraAtSelectedMap();
            boolean gridEnabled = mapDisplay.isGridEnabled();
            mapDisplay.disableGridView();
            mapDisplay.display();
            final BacksoundEditorDialog dialog = new BacksoundEditorDialog(this, true);
            dialog.init(handler, mapDisplay.getScreenshot());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            mapDisplay.setGridEnabled(gridEnabled);
            mapDisplay.display();
        } else {
            JOptionPane.showMessageDialog(this, "Only HGSS have Backsound files",
                    "Backsound Editor not available", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openNsbtxEditor() {
        final NsbtxEditorDialog dialog = new NsbtxEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openNsbtxEditor2() {
        final NsbtxEditorDialog2 dialog = new NsbtxEditorDialog2(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openBuildingEditor() {
        final BuildingEditorDialog dialog = new BuildingEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openBuildingEditor2() {
        BuildingEditorChooser.loadGame(handler);
    }

    public void openAnimationEditor() {
        final AnimationEditorDialog dialog = new AnimationEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openKeyboardInfoDialog() {
        final KeyboardInfoDialog2 dialog = new KeyboardInfoDialog2(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openTileset(String path) {
        String folderPath = new File(path).getParent();

        handler.setLastTilesetDirectoryUsed(folderPath);
        try {
            Tileset tileset = TilesetIO.readTilesetFromFile(path);
            handler.getMapMatrix().tilesetFilePath = path;
            handler.setTileset(tileset);
            System.out.println("Textures loaded from path: " + new File(path).getParent());

            renderTilesetThumbnails();

            handler.setIndexTileSelected(0);
            handler.setSmartGridIndexSelected(0);

            tileSelector.updateLayout();
            tileSelector.repaint();
            smartGridDisplay.updateSize();
            smartGridDisplay.repaint();
            mapDisplay.requestUpdate();
            mapDisplay.repaint();
            tileDisplay.requestUpdate();
            tileDisplay.repaint();
            thumbnailLayerSelector.drawAllLayerThumbnails();
            thumbnailLayerSelector.repaint();
        } catch (/*ParserConfigurationException | SAXException*/IOException ex) {

        } catch (TextureNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error opening tilset", JOptionPane.ERROR_MESSAGE);
        }

        repaintHeightSelector();
        repaintTileSelector();
        repaintMapDisplay();
    }

    public void openTilesetWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastTilesetDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS Tileset (*.pdsts)", Tileset.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getPath();
            openTileset(path);
        }
    }

    private void openBackImgWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Background Image");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fc.getSelectedFile());

                mapDisplay.setBackImage(img);
                mapDisplay.setBackImageEnabled(true);
                jcbUseBackImage.setSelected(true); //Redundant

                mapDisplay.repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file", "Error opening image", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void newMap() {
        int returnVal = JOptionPane.showConfirmDialog(this, "Do you want to close current map?", "Create new map", JOptionPane.YES_NO_OPTION);
        if (returnVal == JOptionPane.YES_OPTION) {
            final GameTsetSelectorDialog2 dialog = new GameTsetSelectorDialog2(this, true);
            dialog.init(handler);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.getReturnValue() == GameTsetSelectorDialog2.ACEPTED) {
                handler.setIndexTileSelected(0);
                handler.setSmartGridIndexSelected(0);

                handler.setMapMatrix(new MapMatrix(handler));
                handler.setMapSelected(new Point(0, 0));

                /*
                handler.setCollisions(new Collisions(handler.getGameIndex()));
                handler.setBdhc(new Bdhc());
                handler.setBacksound(new Backsound());
                handler.setBuildings(new BuildFile());
                handler.setGrid(new MapGrid(handler));*/
                handler.resetMapStateHandler();
                jbUndo.setEnabled(false);
                jbRedo.setEnabled(false);

                //handler.setTileset(new Tileset());
                //handler.getSmartGridArray().add(new SmartGrid());
                tileSelector.updateLayout();
                tileSelector.repaint();

                smartGridDisplay.updateSize();
                smartGridDisplay.repaint();

                mapDisplay.requestUpdate();
                mapDisplay.setCameraAtSelectedMap();
                repaintMapDisplay();
                tileDisplay.requestUpdate();
                tileDisplay.repaint();
                thumbnailLayerSelector.drawAllLayerThumbnails();
                thumbnailLayerSelector.repaint();

                handler.updateAllMapThumbnails();
                mapMatrixDisplay.updateSize();
                updateMapMatrixDisplay();

                updateViewGame();

                setTitle(handler.getVersionName());
            }
        }
    }

    private void saveMap() {
        try {
            handler.getMapMatrix().saveGridsToFile(handler.getMapMatrix().filePath);

            setTitle(handler.getMapName() + " - " + handler.getVersionName());

            saveTileset();
            handler.getMapMatrix().saveCollisions();
            handler.getMapMatrix().saveBacksounds();
            handler.getMapMatrix().saveBDHCs();
            handler.getMapMatrix().saveBuildings();
            //saveBdhc();
            //saveBacksound();
            //saveCollisions();
            //saveBuildings();

            saveMapThumbnail();
        } catch (ParserConfigurationException | TransformerException | IOException ex) {
            JOptionPane.showMessageDialog(this, "There was a problem saving all the map files",
                    "Error saving map files", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMapWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS map (*.pdsmap)", MapMatrix.fileExtension));
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                String path = fc.getSelectedFile().getPath();
                handler.getMapMatrix().saveGridsToFile(path);
                handler.getMapMatrix().filePath = path;

                setTitle(handler.getMapName() + " - " + handler.getVersionName());

                saveTileset();
                handler.getMapMatrix().saveCollisions();
                handler.getMapMatrix().saveBacksounds();
                handler.getMapMatrix().saveBDHCs();
                handler.getMapMatrix().saveBuildings();
                //saveCollisions();
                //saveBacksound();
                //saveBdhc();
                //saveBuildings();

                saveMapThumbnail();
            } catch (ParserConfigurationException | TransformerException | IOException ex) {
                JOptionPane.showMessageDialog(this, "There was a problem saving all the map files",
                        "Error saving map files", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTilesetWithDialog() {
        if (handler.getTileset().size() > 0) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastTilesetDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS tileset (*.pdsts)", Tileset.fileExtension));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save Tileset");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    File file = fc.getSelectedFile();
                    String path = file.getParent();
                    String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Tileset.fileExtension;
                    TilesetIO.saveTilesetToFile(path + File.separator + filename, handler.getTileset());
                    handler.getTileset().saveImagesToFile(path);

                    saveTilesetThumbnail(path + File.separator + "TilesetThumbnail.png");

                    JOptionPane.showMessageDialog(this, "Tileset succesfully exported.", "Tileset saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Can't save file", "Error saving tileset", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "The tileset is empty", "Error saving tileset", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveAllTilesAsObjWithDialog() {
        if (handler.getTileset().size() > 0) {
            final ExportTileDialog exportTileDialog = new ExportTileDialog(handler.getMainFrame(), true, "Export Tile Settings");
            exportTileDialog.setLocationRelativeTo(this);
            exportTileDialog.setVisible(true);
            if (exportTileDialog.getReturnValue() == AddTileDialog.APPROVE_OPTION) {
                float scale = exportTileDialog.getScale();
                boolean flip = exportTileDialog.flip();
                boolean includeVertexColors = exportTileDialog.includeVertexColors();

                final JFileChooser fc = new JFileChooser();
                if (handler.getLastTileObjDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastTileObjDirectoryUsed()));
                }
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setApproveButtonText("Save");
                fc.setDialogTitle("Select folder for saving all tiles as OBJ");
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    handler.setLastTileObjDirectoryUsed(fc.getSelectedFile().getPath());
                    try {
                        ObjWriter objWriter = new ObjWriter(handler.getTileset(),
                                handler.getGrid(), fc.getSelectedFile().getPath(),
                                handler.getGameIndex(), true, includeVertexColors);
                        objWriter.writeAllTilesObj(scale, flip);
                        JOptionPane.showMessageDialog(this, "Tiles succesfully exported.", "Tiles saved", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Can't save tiles", "Error saving tiles", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "The tileset is empty", "Error saving tiles", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMapAsObjWithDialog(boolean saveTextures) {
        final ExportMapObjDialog exportMapDialog = new ExportMapObjDialog(this, true, "Export OBJ Map Settings");
        exportMapDialog.setLocationRelativeTo(null);
        exportMapDialog.setVisible(true);

        if (exportMapDialog.getReturnValue() == exportMapDialog.APPROVE_OPTION) {
            boolean includeVertexColors = exportMapDialog.includeVertexColors();
            boolean exportAllMapsSeparately = exportMapDialog.exportAllMapsSeparately();
            boolean exportAllMapsJoined = exportMapDialog.exportAllMapsJoined();

            final JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath)));
            if (handler.getLastMapDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Select a name for saving the maps as OBJ");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    String path = fc.getSelectedFile().getPath();
                    if (exportAllMapsSeparately) {
                        path = Utils.removeMapCoordsFromName(path);
                        handler.getMapMatrix().saveMapsAsObj(path, saveTextures, includeVertexColors);
                        JOptionPane.showMessageDialog(this, "OBJ maps succesfully exported.", "Maps saved", JOptionPane.INFORMATION_MESSAGE);
                    } else if (exportAllMapsJoined) {
                        path = Utils.removeMapCoordsFromName(path);
                        handler.getMapMatrix().saveMapsAsObjJoined(path, saveTextures, includeVertexColors);
                        JOptionPane.showMessageDialog(this, "OBJ map succesfully exported.", "Map saved", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        handler.getGrid().saveMapToOBJ(handler.getTileset(), path, saveTextures, includeVertexColors);
                        JOptionPane.showMessageDialog(this, "OBJ map succesfully exported.", "Map saved", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "Can't save file.", "Error saving map", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void saveTileset() throws FileNotFoundException, ParserConfigurationException, TransformerException, IOException {
        File file = new File(handler.getMapMatrix().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Tileset.fileExtension;
        TilesetIO.saveTilesetToFile(path + File.separator + filename, handler.getTileset());
        handler.getTileset().saveImagesToFile(path);

        saveTilesetThumbnail(path + File.separator + "TilesetThumbnail.png");
    }

    public void saveTilesetThumbnail(String path) throws IOException {
        BufferedImage img = tileSelector.getTilesetImage();
        if (img != null) {
            File file = new File(path);
            ImageIO.write(img, "png", file);
        }
    }

    public void saveMapThumbnail() throws IOException {
        mapDisplay.requestScreenshot();
        mapDisplay.display();

        String path = new File(handler.getMapMatrix().filePath).getParent();
        File file = new File(path + File.separator + "MapThumbnail.png");
        ImageIO.write(mapDisplay.getScreenshot(), "png", file);
    }

    /*
    public void saveBdhc() throws IOException {
        File file = new File(handler.getMapMatrix().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Bdhc.fileExtension;

        int game = handler.getGameIndex();
        if (game == Game.DIAMOND || game == Game.PEARL) {
            BdhcWriterDP.writeBdhc(handler.getBdhc(), path + File.separator + filename);
        } else {
            BdhcWriterHGSS.writeBdhc(handler.getBdhc(), path + File.separator + filename);
        }

    }

    public void saveBacksound() throws IOException {
        int game = handler.getGameIndex();
        if (game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
            File file = new File(handler.getMapMatrix().filePath);
            String path = file.getParent();
            String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Backsound.fileExtension;

            System.out.println("Backsound OUT: " + filename);

            handler.getBacksound().writeToFile(path + File.separator + filename);
        }
    }

    public void saveCollisions() throws IOException {
        File file = new File(handler.getMapMatrix().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Collisions.fileExtension;
        handler.getCollisions().saveToFile(path + File.separator + filename);
    }

    public void saveBuildings() throws IOException {
        File file = new File(handler.getMapMatrix().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + BuildFile.fileExtension;
        handler.getBuildings().saveToFile(path + File.separator + filename);
    }*/
    public void saveMapsAsImdWithDialog() {
        if (handler.getTileset().size() == 0) {
            JOptionPane.showMessageDialog(this,
                    "There is no tileset loaded.\n"
                    + "The IMD can be exported but the materials will be set to default.\n",
                    "No tileset loaded",
                    JOptionPane.WARNING_MESSAGE);
        }

        final ExportImdDialog configDialog = new ExportImdDialog(this, true);
        configDialog.init(handler);
        configDialog.setLocationRelativeTo(this);
        configDialog.setVisible(true);

        if (configDialog.getReturnValue() == ExportImdDialog.APPROVE_OPTION) {
            ArrayList<String> fileNames = configDialog.getSelectedObjNames();
            String objFolderPath = configDialog.getObjFolderPath();
            String imdFolderPath = configDialog.getImdFolderPath();

            final ImdOutputInfoDialog outputDialog = new ImdOutputInfoDialog(this, true);
            outputDialog.init(handler, fileNames, objFolderPath, imdFolderPath);
            outputDialog.setLocationRelativeTo(null);
            outputDialog.setVisible(true);

        }
    }

    public void saveMapAsImdWithDialog() {
        if (handler.getTileset().size() == 0) {
            JOptionPane.showMessageDialog(this,
                    "There is no tileset loaded.\n"
                    + "The IMD can be exported but the materials will be set to default.\n",
                    "No tileset loaded",
                    JOptionPane.WARNING_MESSAGE);
        }

        final JFileChooser fcOpen = new JFileChooser();
        fcOpen.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath) + ".obj"));
        if (handler.getLastMapDirectoryUsed() != null) {
            fcOpen.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fcOpen.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
        fcOpen.setApproveButtonText("Open");
        fcOpen.setDialogTitle("Open OBJ Map for converting into IMD");
        int returnValOpen = fcOpen.showOpenDialog(this);
        if (returnValOpen == JFileChooser.APPROVE_OPTION) {
            if (fcOpen.getSelectedFile().exists()) {
                String pathOpen = fcOpen.getSelectedFile().getPath();

                final JFileChooser fcSave = new JFileChooser();
                fcSave.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath)));
                fcSave.setCurrentDirectory(fcOpen.getSelectedFile().getParentFile());
                fcSave.setFileFilter(new FileNameExtensionFilter("IMD (*.imd)", "imd"));
                fcSave.setApproveButtonText("Save");
                fcSave.setDialogTitle("Save");
                int returnValSave = fcSave.showOpenDialog(this);
                if (returnValSave == JFileChooser.APPROVE_OPTION) {
                    String pathSave = fcSave.getSelectedFile().getPath();

                    try {
                        ImdModel model = new ImdModel(pathOpen, pathSave, handler.getTileset().getMaterials());
                        final int numVertices = model.getNumVertices();
                        final int numPolygons = model.getNumPolygons();
                        final int numTris = model.getNumTris();
                        final int numQuads = model.getNumQuads();
                        JOptionPane.showMessageDialog(this, "IMD map succesfully exported.\n\n"
                                + "Number of Materials: " + String.valueOf(model.getNumMaterials()) + "\n"
                                + "Number of Vertices: " + String.valueOf(numVertices) + "\n"
                                + "Number of Polygons: " + String.valueOf(numPolygons) + "\n"
                                + "Number of Triangles: " + String.valueOf(numTris) + "\n"
                                + "Number of Quads: " + String.valueOf(numQuads),
                                "Map saved", JOptionPane.INFORMATION_MESSAGE);
                        final int maxNumPolygons = 1800;
                        final int maxNumTris = 1200;
                        if (numTris > maxNumTris) {
                            JOptionPane.showMessageDialog(this, "The map might not work properly in game.\n\n"
                                    + "The map contains " + String.valueOf(numTris) + " triangles" + "\n"
                                    + "Try to use less than " + maxNumTris + " triangles" + "\n"
                                    + "Or try to use quads instead of triangles" + "\n",
                                    "Too many triangles", JOptionPane.INFORMATION_MESSAGE);
                        } else if (numPolygons > maxNumPolygons) {
                            JOptionPane.showMessageDialog(this, "The map may not work properly in game.\n\n"
                                    + "The map contains " + String.valueOf(numPolygons) + " polygons" + "\n"
                                    + "Try to use less than " + maxNumPolygons + " polygons",
                                    "Too many polygons", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (ParserConfigurationException | TransformerException ex) {
                        JOptionPane.showMessageDialog(this,
                                "There was a problem parsing the XML data of the IMD",
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this,
                                "There was a problem exporting the IMD",
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (TextureNotFoundException ex) {
                        JOptionPane.showMessageDialog(this,
                                ex.getMessage(),
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (NormalsNotFoundException ex) {
                        JOptionPane.showMessageDialog(this,
                                ex.getMessage(),
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "The selected OBJ file could not be opened",
                        "Can't open OBJ",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveMapsAsNsbWithDialog() {
        final ExportNsbmdDialog configDialog = new ExportNsbmdDialog(this, true);
        configDialog.init(handler);
        configDialog.setLocationRelativeTo(this);
        configDialog.setVisible(true);

        if (configDialog.getReturnValue() == ExportNsbmdDialog.APPROVE_OPTION) {
            ArrayList<String> fileNames = configDialog.getSelectedImdNames();
            String imdFolderPath = configDialog.getImdFolderPath();
            String nsbFolderPath = configDialog.getNsbFolderPath();

            final NsbmdOutputInfoDialog outputDialog = new NsbmdOutputInfoDialog(this, true);
            outputDialog.init(handler, fileNames, imdFolderPath, nsbFolderPath, configDialog.includeNsbtxInNsbmd());
            outputDialog.setLocationRelativeTo(null);
            outputDialog.setVisible(true);
        }
    }

    public void saveMapAsNsbWithDialog() {
        final ConverterDialog convDialog = new ConverterDialog(this, true);
        convDialog.setLocationRelativeTo(this);
        convDialog.setVisible(true);
        if (convDialog.getReturnValue() == ConverterDialog.APPROVE_OPTION) {
            boolean includeNsbtx = convDialog.includeNsbtxInNsbmd();
            try {
                final JFileChooser fcOpen = new JFileChooser();
                fcOpen.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath) + ".imd"));
                if (handler.getLastMapDirectoryUsed() != null) {
                    fcOpen.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
                }
                fcOpen.setFileFilter(new FileNameExtensionFilter("IMD (*.imd)", "imd"));
                fcOpen.setApproveButtonText("Open");
                fcOpen.setDialogTitle("Open IMD Map for converting into NSBMD");
                int returnVal = fcOpen.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String imdPath;
                    if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                        imdPath = fcOpen.getSelectedFile().getPath();
                    } else {
                        String cwd = System.getProperty("user.dir"); // get current user directory
                        imdPath = new File(cwd).toURI().relativize(fcOpen.getSelectedFile().toPath().toRealPath().toUri()).getPath(); //this is some serious java shit
                    }
                    final JFileChooser fcSave = new JFileChooser();
                    fcSave.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath)));
                    fcSave.setCurrentDirectory(fcOpen.getSelectedFile().getParentFile());
                    fcSave.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
                    fcSave.setApproveButtonText("Save");
                    fcSave.setDialogTitle("Save");
                    int returnValSave = fcSave.showOpenDialog(this);

                    if (returnValSave == JFileChooser.APPROVE_OPTION) {
                        String nsbPath = fcSave.getSelectedFile().getPath();
                        String filename = new File(nsbPath).getName();

                        try {
                            String converterPath = "converter/g3dcvtr.exe";
                            String[] cmd;
                            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                                if (includeNsbtx) {
                                    cmd = new String[]{converterPath, imdPath, "-eboth", "-o", filename};
                                } else {
                                    cmd = new String[]{converterPath, imdPath, "-emdl", "-o", filename};
                                }

                            } else {
                                if (includeNsbtx) {
                                    cmd = new String[]{"wine", converterPath, imdPath, "-eboth", "-o", filename};
                                } else {
                                    cmd = new String[]{"wine", converterPath, imdPath, "-emdl", "-o", filename};
                                }
                                // NOTE: wine call works only with relative path
                            }

                            if (!Files.exists(Paths.get(converterPath))) {
                                throw new IOException();
                            }

                            Process p = new ProcessBuilder(cmd).start();

                            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                            String outputString = "";
                            String line = null;
                            while ((line = stdError.readLine()) != null) {
                                outputString += line + "\n";
                            }

                            p.waitFor();
                            p.destroy();

                            if (!filename.endsWith("nsbmd")) {
                                filename += ".nsbmd";
                            }
                            if (!nsbPath.endsWith("nsbmd")) {
                                nsbPath += ".nsbmd";
                            }

                            System.out.println(System.getProperty("user.dir"));
                            File srcFile = new File(System.getProperty("user.dir") + File.separator + filename);
                            File dstFile = new File(nsbPath);
                            if (srcFile.exists()) {
                                try {
                                    Files.move(srcFile.toPath(), dstFile.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING);

                                    try {
                                        byte[] nsbmdData = Files.readAllBytes(dstFile.toPath());

                                        ExportNsbmdResultDialog resultDialog = new ExportNsbmdResultDialog(this, true);
                                        resultDialog.init(nsbmdData);
                                        resultDialog.setLocationRelativeTo(this);
                                        resultDialog.setVisible(true);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(this, "NSBMD succesfully exported.",
                                                "NSBMD saved", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(this,
                                            "File was not moved to the save directory. \n"
                                            + "Reopen Pokemon DS Map Studio and try again.",
                                            "Problem saving generated file",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                ConverterErrorDialog dialog = new ConverterErrorDialog(this, true);
                                dialog.init("There was a problem creating the NSBMD file. \n"
                                        + "The output from the converter is:",
                                        outputString);
                                dialog.setTitle("Problem generating file");
                                dialog.setLocationRelativeTo(this);
                                dialog.setVisible(true);
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "The program \"g3dcvtr.exe\" is not found in the \"converter\" folder.\n"
                                    + "Put the program and its *.dll files in the folder and try again.",
                                    "Converter not found",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (InterruptedException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "The model was not converted",
                                    "Problem converting the model",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "There was a problem reading the IMD file",
                        "Error loading the IMD file",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveMapBtxWithDialog() {
        final JFileChooser fcOpen = new JFileChooser();
        fcOpen.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath) + ".imd"));
        if (handler.getLastMapDirectoryUsed() != null) {
            fcOpen.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fcOpen.setFileFilter(new FileNameExtensionFilter("IMD (*.imd)", "imd"));
        fcOpen.setApproveButtonText("Open");
        fcOpen.setDialogTitle("Open IMD Map for converting into NSBTX");
        int returnVal = fcOpen.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String imdPath = fcOpen.getSelectedFile().getPath();

            final JFileChooser fcSave = new JFileChooser();
            fcSave.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getMapMatrix().filePath)));
            fcSave.setCurrentDirectory(fcOpen.getSelectedFile().getParentFile());
            fcSave.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
            fcSave.setApproveButtonText("Save");
            fcSave.setDialogTitle("Save");
            int returnValSave = fcSave.showOpenDialog(this);

            if (returnValSave == JFileChooser.APPROVE_OPTION) {
                String nsbPath = fcSave.getSelectedFile().getPath();
                String filename = new File(nsbPath).getName();

                System.out.println(filename);
                String converterPath = "converter/g3dcvtr.exe";
                String[] cmd = {converterPath, imdPath, "-etex", "-o", filename};
                Process p;
                try {
                    p = new ProcessBuilder(cmd).start();

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    String outputString = "";
                    String line = null;
                    while ((line = stdError.readLine()) != null) {
                        outputString += line + "\n";
                    }

                    p.waitFor();
                    p.destroy();

                    if (!filename.endsWith("nsbtx")) {
                        filename += ".nsbtx";
                    }
                    if (!nsbPath.endsWith("nsbtx")) {
                        nsbPath += ".nsbtx";
                    }

                    System.out.println(System.getProperty("user.dir"));
                    File srcFile = new File(System.getProperty("user.dir") + "/" + filename);
                    File dstFile = new File(nsbPath);
                    if (srcFile.exists()) {
                        try {
                            Files.move(srcFile.toPath(), dstFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING);
                            try {
                                byte[] nsbtxData = Files.readAllBytes(dstFile.toPath());
                                Nsbtx2 nsbtx = NsbtxLoader2.loadNsbtx(nsbtxData);

                                ExportNsbtxResultDialog resultDialog = new ExportNsbtxResultDialog(this, true);
                                resultDialog.init(nsbtx);
                                resultDialog.setLocationRelativeTo(this);
                                resultDialog.setVisible(true);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(this, "NSBTX succesfully exported.",
                                        "NSBTX saved", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "File was not moved to the save directory. \n"
                                    + "Reopen Pokemon DS Map Studio and try again.",
                                    "Problem saving generated file",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        ConverterErrorDialog dialog = new ConverterErrorDialog(this, true);
                        dialog.init("There was a problem creating the NSBTX file. \n"
                                + "The output from the converter is:",
                                outputString);
                        dialog.setTitle("Problem generating file");
                        dialog.setLocationRelativeTo(this);
                        dialog.setVisible(true);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "The program \"g3dcvtr.exe\" is not found in the \"converter\" folder.\n"
                            + "Put the program and its *.dll files in the folder and try again.",
                            "Converter not found",
                            JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(this,
                            "The model was not converted",
                            "Problem converting the model",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void saveAreasAsBtxWithDialog() {
        final ExportNsbtxDialog configDialog = new ExportNsbtxDialog(this, true);
        configDialog.init(handler);
        configDialog.setLocationRelativeTo(this);
        configDialog.setVisible(true);

        if (configDialog.getReturnValue() == ExportImdDialog.APPROVE_OPTION) {
            ArrayList<Integer> areaIndices = configDialog.getSelectedAreaIndices();
            String nsbtxFolderPath = configDialog.getNsbtxFolderPath();

            final NsbtxOutputInfoDialog outputDialog = new NsbtxOutputInfoDialog(this, true);
            outputDialog.init(handler, areaIndices, nsbtxFolderPath);
            outputDialog.setLocationRelativeTo(null);
            outputDialog.setVisible(true);
        }
    }

    public void changeGame() {
        final GameChangerDialog dialog = new GameChangerDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == GameSelectorDialog.ACEPTED) {
            updateViewGame();

            handler.getMapMatrix().updateAllLayersGL();
            mapDisplay.repaint();

            updateViewGeometryCount();
        }
    }

    public void openAboutDialog() {
        final AboutDialog dialog = new AboutDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void repaintHeightSelector() {
        heightSelector.repaint();
    }

    public void repaintTileSelector() {
        tileSelector.repaint();
    }

    public void repaintTileDisplay() {
        tileDisplay.repaint();
    }

    public void updateTileSelectorScrollBar() {
        int y = tileSelector.getTileSelectedY() - jScrollPane1.getHeight() / 2;
        jScrollPane1.getVerticalScrollBar().setValue(y);
    }

    public void updateMapMatrixDisplayScrollBars() {
        Point min = handler.getMapMatrix().getMinCoords();
        Point p = handler.getMapSelected();

        int x = (int) ((p.x - min.x) * MapData.mapThumbnailSize * mapMatrixDisplay.getScale()) - jScrollPaneMapMatrix.getWidth() / 2;
        int y = (int) ((p.y - min.y) * MapData.mapThumbnailSize * mapMatrixDisplay.getScale()) - jScrollPaneMapMatrix.getHeight() / 2;

        jScrollPaneMapMatrix.getHorizontalScrollBar().setValue(x);
        jScrollPaneMapMatrix.getVerticalScrollBar().setValue(y);
    }

    public void repaintThumbnailLayerSelector() {
        thumbnailLayerSelector.repaint();
    }

    public void repaintMapDisplay() {
        mapDisplay.repaint();
    }

    public ThumbnailLayerSelector getThumbnailLayerSelector() {
        return thumbnailLayerSelector;
    }

    private void updateViewGame() {
        jlGameName.setText(Game.gameNames[handler.getGameIndex()]);
        jlGameIcon.setIcon(new ImageIcon(handler.getGame().gameIcons[handler.getGameIndex()]));
    }

    public void undoMapState() {
        StateHandler mapStateHandler = handler.getMapStateHandler();
        if (mapStateHandler.canGetPreviousState()) {
            MapLayerState state = (MapLayerState) mapStateHandler.getPreviousState(new MapLayerState("Map Edit", handler, true));
            state.revertState();
            jbRedo.setEnabled(true);
            if (!mapStateHandler.canGetPreviousState()) {
                jbUndo.setEnabled(false);
            }
            for (Point mapCoord : state.getKeySet()) {
                MapData mapData = handler.getMapMatrix().getMap(mapCoord);
                mapData.getGrid().updateMapLayerGL(state.getLayerIndex(), handler.useRealTimePostProcessing());
                mapData.updateMapThumbnail();
            }
            //mapDisplay.updateMapLayerGL(state.getLayerIndex());

            handler.getMapMatrix().removeUnusedMaps();
            if (!handler.mapSelectedExists()) {
                handler.setDefaultMapSelected();

                handler.getMainFrame().getThumbnailLayerSelector().drawAllLayerThumbnails();
                handler.getMainFrame().getThumbnailLayerSelector().repaint();
            }

            mapDisplay.repaint();
            updateMapMatrixDisplay();
            thumbnailLayerSelector.drawLayerThumbnail(state.getLayerIndex());
            thumbnailLayerSelector.repaint();
        }
    }

    public void redoMapState() {
        StateHandler mapStateHandler = handler.getMapStateHandler();
        if (mapStateHandler.canGetNextState()) {
            MapLayerState state = (MapLayerState) mapStateHandler.getNextState();
            state.revertState();
            jbUndo.setEnabled(true);
            for (Point mapCoord : state.getKeySet()) {
                MapData mapData = handler.getMapMatrix().getMap(mapCoord);
                mapData.getGrid().updateMapLayerGL(state.getLayerIndex(), handler.useRealTimePostProcessing());
                mapData.updateMapThumbnail();
            }
            handler.getMapMatrix().removeUnusedMaps();

            //mapDisplay.updateMapLayerGL(state.getLayerIndex());
            mapDisplay.repaint();
            updateMapMatrixDisplay();
            thumbnailLayerSelector.drawLayerThumbnail(state.getLayerIndex());
            thumbnailLayerSelector.repaint();
            if (!mapStateHandler.canGetNextState()) {
                jbRedo.setEnabled(false);
            }
        }
    }

    public void moveTilesUp() {
        handler.addMapState(new MapLayerState("Move tiles up", handler));
        handler.getGrid().moveTilesUp(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.updateActiveMapLayerGL();
        mapDisplay.repaint();
    }

    public void moveTilesDown() {
        handler.addMapState(new MapLayerState("Move tiles down", handler));
        handler.getGrid().moveTilesDown(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.updateActiveMapLayerGL();
        mapDisplay.repaint();
    }

    public void moveTilesLeft() {
        handler.addMapState(new MapLayerState("Move tiles left", handler));
        handler.getGrid().moveTilesLeft(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.updateActiveMapLayerGL();
        mapDisplay.repaint();
    }

    public void moveTilesRight() {
        handler.addMapState(new MapLayerState("Move tiles right", handler));
        handler.getGrid().moveTilesRight(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.updateActiveMapLayerGL();
        mapDisplay.repaint();
    }

    public void moveTilesUpZ() {
        handler.addMapState(new MapLayerState("Move tiles up Z", handler));
        handler.getGrid().moveTilesUpZ(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.updateActiveMapLayerGL();
        mapDisplay.repaint();
    }

    public void moveTilesDownZ() {
        handler.addMapState(new MapLayerState("Move tiles down Z", handler));
        handler.getGrid().moveTilesDownZ(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.updateActiveMapLayerGL();
        mapDisplay.repaint();
    }

    public void updateViewMapInfo() {
        getjPanelAreaColor().setBackground(handler.getMapMatrix().getAreaColors().get(handler.getCurrentMap().getAreaIndex()));
        getjPanelAreaColor().repaint();

        getJsSelectedArea().setValue(handler.getCurrentMap().getAreaIndex());

        updateViewGeometryCount();

        Point coords = handler.getMapSelected();
        jlMapCoords.setText("(" + coords.x + ", " + coords.y + ")");
    }

    public void updateViewGeometryCount() {
        try {
            jlNumPolygons.setText(String.valueOf(handler.getGrid().getNumPolygons()));
            jlNumMaterials.setText(String.valueOf(handler.getGrid().getNumMaterials()));
        } catch (Exception ex) {
            jlNumPolygons.setText("");
            jlNumMaterials.setText("");
        }
    }

    public JButton getUndoButton() {
        return jbUndo;
    }

    public JButton getRedoButton() {
        return jbRedo;
    }

    public MapDisplay getMapDisplay() {
        return mapDisplay;
    }

    public TileDisplay getTileDisplay() {
        return tileDisplay;
    }

    public MapMatrixDisplay getMapMatrixDisplay() {
        return mapMatrixDisplay;
    }

    public JScrollPane getjScrollPaneMapMatrix() {
        return jScrollPaneMapMatrix;
    }

    public void updateMapMatrixDisplay() {
        Dimension size = jScrollPaneMapMatrix.getSize();
        mapMatrixDisplay.updateSize();
        mapMatrixDisplay.revalidate();
        mapMatrixDisplay.updateMapsImage();

        jScrollPaneMapMatrix.setPreferredSize(size);
        jScrollPaneMapMatrix.revalidate();
    }

    public void renderTilesetThumbnails() {
        GLContext context = mapDisplay.getContext();
        TilesetRenderer tr = new TilesetRenderer(handler.getTileset());
        try {
            tr.renderTiles();
        } catch (NullPointerException e) {

        }
        tr.destroy();
        mapDisplay.setContext(context, false);
    }

    public JToggleButton getJtbModeEdit() {
        return jtbModeEdit;
    }

    public JToggleButton getJtbModeClear() {
        return jtbModeClear;
    }

    public JToggleButton getJtbModeSmartPaint() {
        return jtbModeSmartPaint;
    }

    public JToggleButton getJtbModeInvSmartPaint() {
        return jtbModeInvSmartPaint;
    }

    public JToggleButton getJtbModeMove() {
        return jtbModeMove;
    }

    public JToggleButton getJtbModeZoom() {
        return jtbModeZoom;
    }

    public JToggleButton getJtbView3D() {
        return jtbView3D;
    }

    public JToggleButton getJtbViewOrtho() {
        return jtbViewOrtho;
    }

    public JToggleButton getJtbViewHeight() {
        return jtbViewHeight;
    }

    public JToggleButton getJtbViewGrid() {
        return jtbViewGrid;
    }

    public JPanel getjPanelAreaColor() {
        return jPanelAreaColor;
    }

    public JSpinner getJsSelectedArea() {
        return jsSelectedArea;
    }

    public JToggleButton getJtbViewWireframe() {
        return jtbViewWireframe;
    }

    public JCheckBox getJcbViewAreas() {
        return jcbViewAreas;
    }

    public void updateViewAllMapData() {
        renderTilesetThumbnails();

        handler.setIndexTileSelected(0);
        handler.setSmartGridIndexSelected(0);

        handler.getMapMatrix().updateAllLayersGL();
        handler.getMapMatrix().updateBordersData();
        handler.updateAllMapThumbnails();
        mapMatrixDisplay.updateSize();
        updateMapMatrixDisplay();
        updateViewMapInfo();

        tileSelector.updateLayout();
        tileSelector.repaint();
        mapDisplay.requestUpdate();
        mapDisplay.setCameraAtSelectedMap();
        mapDisplay.repaint();
        tileDisplay.requestUpdate();
        tileDisplay.repaint();

        smartGridDisplay.updateSize();
        smartGridDisplay.repaint();
        thumbnailLayerSelector.drawAllLayerThumbnails();
        thumbnailLayerSelector.repaint();
    }

}
