
package editor.mapmatrix;

import com.jogamp.common.nio.Buffers;
import formats.backsound.Backsound;
import formats.bdhc.Bdhc;
import formats.bdhc.BdhcLoaderDP;
import formats.bdhc.BdhcLoaderHGSS;
import formats.bdhc.BdhcWriterDP;
import formats.bdhc.BdhcWriterHGSS;
import editor.buildingeditor2.buildfile.BuildFile;
import formats.bdhcam.Bdhcam;
import formats.bdhcam.BdhcamLoader;
import formats.bdhcam.BdhcamWriter;
import formats.collisions.Collisions;
import editor.game.Game;
import editor.grid.MapGrid;
import editor.handler.MapData;
import editor.handler.MapEditorHandler;
import formats.obj.ObjWriter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import tileset.Tile;
import tileset.Tileset;
import tileset.TilesetIO;
import utils.Utils;

/**
 * @author Trifindo
 */
public class MapMatrix {

    private static final int expectedMaxNumMaps = 25;

    private static final String mapTag = "mapstart";
    private static final String areaIndexTag = "areaindex";
    private static final String mapEndTag = "mapend";
    private static final String gameIndexTag = "gameindex";
    private static final String tileGridTag = "tilegrid";
    private static final String heightGridTag = "heightgrid";
    private static final String tilesetTag = "tileset";
    private static final String bdhcTag = "bdhc";

    private final MapEditorHandler handler;
    private Map<Point, MapData> matrix; //Key is map coords

    //Border maps
    private Set<Point> borderMaps;

    //Contours
    private Map<Integer, List<Point>> contourPoints;
    private Map<Integer, FloatBuffer> contourPointsBuffer;
    private Map<Integer, Color> areaColors;

    public String filePath = "";
    public String tilesetFilePath = "";
    public static final String fileExtension = "pdsmap";

    public MapMatrix(MapEditorHandler handler) {
        this.handler = handler;

        handler.clearCopyLayer();

        filePath = "";
        tilesetFilePath = "";

        matrix = new HashMap<>(expectedMaxNumMaps);
        matrix.put(new Point(0, 0), new MapData(handler));

        borderMaps = new HashSet<>();
        contourPoints = new HashMap<>();
        contourPointsBuffer = new HashMap<>();
        areaColors = new HashMap<>();

        updateBordersData();
    }

    public void updateBordersData() {
        updateBorderMaps();

        final Set<Integer> areaIndices = getAreaIndices();
        updateContourPoints(areaIndices);

        updateAreaColors(areaIndices);
    }

    public void saveGridsToFile(String path) throws FileNotFoundException {
        if (!path.endsWith("." + fileExtension)) {
            path = path.concat("." + fileExtension);
        }

        removeUnusedMaps();
        removeAllUnusedMapFiles();

        PrintWriter out = new PrintWriter(path);

        out.println(gameIndexTag);
        out.println(handler.getGameIndex());

        out.println(tilesetTag);
        String filename = Utils.removeExtensionFromPath(new File(path).getName());
        out.println(filename + "." + Tileset.fileExtension);

        Point minCoords = getMinCoords();
        for (Map.Entry<Point, MapData> map : matrix.entrySet()) {
            out.println(mapTag);
            out.println((map.getKey().x - minCoords.x) + " " + (map.getKey().y - minCoords.y));

            out.println(areaIndexTag);
            out.println(map.getValue().getAreaIndex());

            for (int[][] tLayer : map.getValue().getGrid().tileLayers) {
                out.println(tileGridTag);
                MapGrid.printMatrixInFile(out, tLayer); //Todo change this
            }

            for (int[][] hLayer : map.getValue().getGrid().heightLayers) {
                out.println(heightGridTag);
                MapGrid.printMatrixInFile(out, hLayer); //Todo change this
            }
            out.println(mapEndTag);
        }

        out.close();
    }

    public void loadGridsFromFile(String path) throws Exception {
        filePath = "";
        tilesetFilePath = "";

        matrix = new HashMap<>(expectedMaxNumMaps);

        borderMaps = new HashSet<>();
        updateBordersData();

        InputStream input = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        int numMapsRead = 0;
        int numTileLayersRead = 0;
        int numHeightLayersRead = 0;

        Point currentMapCoords = new Point(0, 0);
        MapGrid currentGrid = new MapGrid(handler);
        int currentAreaIndex = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(gameIndexTag)) {
                handler.setGameIndex(Integer.parseInt(br.readLine()));
            } else if (line.startsWith(tilesetTag)) {
                String folderPath = new File(path).getParent();
                tilesetFilePath = folderPath + File.separator + br.readLine();
                System.out.println("Tileset path: " + tilesetFilePath);
            } else if (line.startsWith(mapTag)) {
                String[] splitLine = br.readLine().split(" ");
                int x = Integer.parseInt(splitLine[0]);
                int y = Integer.parseInt(splitLine[1]);
                currentMapCoords = new Point(x, y);
                currentGrid = new MapGrid(handler);
            } else if (line.startsWith(areaIndexTag)) {
                currentAreaIndex = Integer.parseInt(br.readLine());
            } else if (line.startsWith(tileGridTag)) {
                MapGrid.loadMatrixFromFile(br, currentGrid.tileLayers[numTileLayersRead]);
                numTileLayersRead++;
            } else if (line.startsWith(heightGridTag)) {
                MapGrid.loadMatrixFromFile(br, currentGrid.heightLayers[numHeightLayersRead]);
                numHeightLayersRead++;
            } else if (line.startsWith(mapEndTag)) {
                MapData mapData = new MapData(handler);
                mapData.setGrid(currentGrid);
                mapData.setAreaIndex(currentAreaIndex);
                matrix.put(currentMapCoords, mapData);
                numMapsRead++;
                numTileLayersRead = 0;
                numHeightLayersRead = 0;
            }
        }
        if (numMapsRead == 0) {
            MapData mapData = new MapData(handler);
            mapData.setGrid(currentGrid);
            matrix.put(new Point(0, 0), mapData);
        }

        br.close();
        input.close();
    }

    public static Map<Point, MapData> getGridsFromFile(String path, MapEditorHandler handler) throws Exception {
        Map<Point, MapData> matrix = new HashMap<>(expectedMaxNumMaps);

        InputStream input = new FileInputStream(new File(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        String tilesetFilePath;

        int numMapsRead = 0;
        int numTileLayersRead = 0;
        int numHeightLayersRead = 0;

        Point currentMapCoords = new Point(0, 0);
        MapGrid currentGrid = new MapGrid(handler);
        int currentAreaIndex = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(gameIndexTag)) {

            } else if (line.startsWith(tilesetTag)) {
                String folderPath = new File(path).getParent();
                tilesetFilePath = folderPath + File.separator + br.readLine();

            } else if (line.startsWith(mapTag)) {
                String[] splitLine = br.readLine().split(" ");
                int x = Integer.parseInt(splitLine[0]);
                int y = Integer.parseInt(splitLine[1]);
                currentMapCoords = new Point(x, y);
                currentGrid = new MapGrid(handler);
            } else if (line.startsWith(areaIndexTag)) {
                currentAreaIndex = Integer.parseInt(br.readLine());
            } else if (line.startsWith(tileGridTag)) {
                MapGrid.loadMatrixFromFile(br, currentGrid.tileLayers[numTileLayersRead]);
                numTileLayersRead++;
            } else if (line.startsWith(heightGridTag)) {
                MapGrid.loadMatrixFromFile(br, currentGrid.heightLayers[numHeightLayersRead]);
                numHeightLayersRead++;
            } else if (line.startsWith(mapEndTag)) {
                MapData mapData = new MapData(handler);
                mapData.setGrid(currentGrid);
                mapData.setAreaIndex(currentAreaIndex);
                matrix.put(currentMapCoords, mapData);
                numMapsRead++;
                numTileLayersRead = 0;
                numHeightLayersRead = 0;
            }
        }
        if (numMapsRead == 0) {
            MapData mapData = new MapData(handler);
            mapData.setGrid(currentGrid);
            matrix.put(new Point(0, 0), mapData);
        }

        br.close();
        input.close();

        return matrix;
    }

    public void addMapsFromFile(Map<Point, MapData> newMaps, Point offset, String folderPath, String fileName) throws IOException, NullPointerException {
        fileName = Utils.removeExtensionFromPath(fileName);
        Tileset tileset = TilesetIO.readTilesetFromFile(folderPath + File.separator + fileName + "." + Tileset.fileExtension);

        List<Tile> newTiles = new ArrayList<>();

        int[] tileLookupTable = new int[tileset.size()];
        for (int i = 0; i < tileset.size(); i++) {
            Tile tile = tileset.get(i);

            int index = handler.getTileset().indexOfTileVisualData(tile);
            if (index == -1) {
                //System.out.println("Tile not found: " + i);
                tileLookupTable[i] = handler.getTileset().size() + newTiles.size();
                newTiles.add(tile);
            } else {
                tileLookupTable[i] = index;
            }
        }

        for (MapData mapData : newMaps.values()) {
            mapData.getGrid().applyLookupTable(tileLookupTable);
        }

        handler.getTileset().importTiles(newTiles);
        //handler.getTileset().removeUnusedTextures();

        loadBDHCsFromFile(newMaps, folderPath, fileName, handler.getGameIndex());
        loadBdhcamsFromFile(newMaps, folderPath, fileName, handler.getGameIndex());
        loadCollisionsFromFile(newMaps, folderPath, fileName, handler.getGameIndex());
        loadBacksoundsFromFile(newMaps, folderPath, fileName, handler.getGameIndex());
        loadBuildingsFromFile(newMaps, folderPath, fileName);

        for (Map.Entry<Point, MapData> entry : newMaps.entrySet()) {
            Point coords = new Point(entry.getKey().x + offset.x, entry.getKey().y + offset.y);
            matrix.put(coords, entry.getValue());
        }
    }

    public void saveMapsAsObj(String path, boolean saveTextures, boolean includeVertexColors, float tileUpscale) throws FileNotFoundException {
        removeUnusedMaps();

        String folderPath = new File(path).getParent();
        String fileName = Utils.removeExtensionFromPath(new File(path).getName());

        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            String objFilePath = getFilePathWithCoords(matrix, folderPath, fileName, mapEntry.getKey(), "obj");
            mapEntry.getValue().getGrid().saveMapToOBJ(handler.getTileset(), objFilePath, saveTextures, includeVertexColors, tileUpscale);
        }
        //TODO: This method saves textures for each map. Make that textures are exported only once
    }

    public void saveMapsAsObjJoined(String path, boolean saveTextures, boolean includeVertexColors, float tileUpscale) throws FileNotFoundException {
        removeUnusedMaps();

        String folderPath = new File(path).getParent();
        String fileName = Utils.removeExtensionFromPath(new File(path).getName());

        String objFilePath = folderPath + File.separator + fileName + ".obj";

        ObjWriter writer = new ObjWriter(handler.getTileset(), generateGridHashMap(), objFilePath, handler.getGameIndex(),
                saveTextures, includeVertexColors, tileUpscale);
        writer.writeMapObj();
    }

    public void saveBDHCs() throws IOException {
        int game = handler.getGameIndex();
        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            String path = getFilePathWithCoords(matrix, new File(filePath).getParent(),
                    new File(filePath).getName(), mapEntry.getKey(), Bdhc.fileExtension);
            if (game == Game.DIAMOND || game == Game.PEARL) {
                BdhcWriterDP.writeBdhc(mapEntry.getValue().getBdhc(), path);
            } else {
                BdhcWriterHGSS.writeBdhc(mapEntry.getValue().getBdhc(), path);
            }
        }
    }

    public void saveBacksounds() throws IOException {
        int game = handler.getGameIndex();
        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            String path = getFilePathWithCoords(matrix, new File(filePath).getParent(),
                    new File(filePath).getName(), mapEntry.getKey(), Backsound.fileExtension);
            if (game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
                mapEntry.getValue().getBacksound().writeToFile(path);
            }
        }
    }

    public void saveBdhcams() throws IOException {
        int game = handler.getGameIndex();
        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            String path = getFilePathWithCoords(matrix, new File(filePath).getParent(),
                    new File(filePath).getName(), mapEntry.getKey(), Bdhcam.fileExtension);
            if (game == Game.PLATINUM || game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
                BdhcamWriter.writeBdhcamToFile(path, mapEntry.getValue().getBdhcam(), mapEntry.getValue().getBdhc(), game);
            }
        }
    }

    public void saveCollisions() throws IOException {
        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            String path = getFilePathWithCoords(matrix, new File(filePath).getParent(),
                    new File(filePath).getName(), mapEntry.getKey(), Collisions.fileExtension);
            mapEntry.getValue().getCollisions().saveToFile(path);

            if (Game.isGenV(handler.getGameIndex())) {
                if (mapEntry.getValue().getCollisions2() != null) {
                    String path2 = getFilePathWithCoords(matrix, new File(filePath).getParent(),
                            new File(filePath).getName(), "2", mapEntry.getKey(), Collisions.fileExtension);
                    mapEntry.getValue().getCollisions2().saveToFile(path2);
                }
            }
        }
    }

    public void saveBuildings() throws IOException {
        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            String path = getFilePathWithCoords(matrix, new File(filePath).getParent(),
                    new File(filePath).getName(), mapEntry.getKey(), BuildFile.fileExtension);
            mapEntry.getValue().getBuildings().saveToFile(path);
        }
    }

    /*
    public void saveBinaryMaps() {
        System.out.println("Saving Binary maps...");
        int game = handler.getGameIndex();
        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            Point p = mapEntry.getKey();
            try {
                if (game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
                    String path = getFilePathWithCoords(matrix, new File(filePath).getParent(),
                            new File(filePath).getName(), mapEntry.getKey(), "bin");

                    File file = new File(path);
                    MapBinHGSS mapBin = new MapBinHGSS(file.getParent(), file.getName());
                    mapBin.saveToFile(path);

                    System.out.println("Map BIN saved (" + p.x + ", " + p.y + ")");
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
                System.out.println("Map BIN NOT saved! (" + p.x + ", " + p.y + ")");

            }
        }
    }*/


    public void loadBDHCsFromFile(Map<Point, MapData> matrix, String folderPath, String mapFileName, int game) {
        if (matrix.size() == 1) {//OLD MAP TYPE
            Map.Entry<Point, MapData> mapEntry = matrix.entrySet().iterator().next();
            try {
                String bdhcPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), Bdhc.fileExtension);
                loadBDHC(bdhcPath, mapEntry.getValue(), game);
            } catch (Exception ex) {
                try {
                    String bdhcPath = getFilePath(folderPath, mapFileName, Bdhc.fileExtension);
                    loadBDHC(bdhcPath, mapEntry.getValue(), game);
                } catch (Exception ex2) {
                    mapEntry.getValue().setBdhc(new Bdhc());
                }
            }
        } else {
            for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
                try {
                    String bdhcPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), Bdhc.fileExtension);
                    loadBDHC(bdhcPath, mapEntry.getValue(), game);
                } catch (Exception ex) {
                    mapEntry.getValue().setBdhc(new Bdhc());
                }
            }
        }
    }

    public void loadBDHCsFromFile(String folderPath, String mapFileName) {
        loadBDHCsFromFile(matrix, folderPath, mapFileName, handler.getGameIndex());
    }

    public void loadBDHC(String path, MapData mapData, int game) throws IOException {
        if (game == Game.DIAMOND || game == Game.PEARL) {
            mapData.setBdhc(new BdhcLoaderDP().loadBdhcFromFile(path));
        } else {
            mapData.setBdhc(new BdhcLoaderHGSS().loadBdhcFromFile(path));
        }
    }

    public static void loadBdhcamsFromFile(Map<Point, MapData> matrix, String folderPath, String mapFileName, int game) {
        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            try {
                String bdhcamPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), Bdhcam.fileExtension);
                loadBdhcam(bdhcamPath, mapEntry.getValue(), game);
            } catch (Exception ex) {
                mapEntry.getValue().setBdhcam(new Bdhcam());
            }
        }
    }

    public static void loadBdhcam(String path, MapData mapData, int game) throws Exception {
        if (game == Game.PLATINUM || game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
            mapData.setBdhcam(BdhcamLoader.loadBdhcam(path));
        } else {
            mapData.setBdhcam(new Bdhcam());
        }
    }

    public void loadBdhcamsFromFile(String folderPath, String mapFileName) {
        loadBdhcamsFromFile(matrix, folderPath, mapFileName, handler.getGameIndex());
    }

    public static void loadBacksoundsFromFile(Map<Point, MapData> matrix, String folderPath, String mapFileName, int game) {
        if (matrix.size() == 1) {//OLD MAP TYPE
            Map.Entry<Point, MapData> mapEntry = matrix.entrySet().iterator().next();
            try {
                String backsoundPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), Backsound.fileExtension);
                loadBacksound(backsoundPath, mapEntry.getValue(), game);
            } catch (Exception ex) {
                try {
                    String backsoundPath = getFilePath(folderPath, mapFileName, Backsound.fileExtension);
                    loadBacksound(backsoundPath, mapEntry.getValue(), game);
                } catch (Exception ex2) {
                    mapEntry.getValue().setBacksound(new Backsound());
                }
            }
        } else {
            for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
                try {
                    String backsoundPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), Backsound.fileExtension);
                    loadBacksound(backsoundPath, mapEntry.getValue(), game);
                } catch (Exception ex) {
                    mapEntry.getValue().setBacksound(new Backsound());
                }
            }
        }
    }

    public void loadBacksoundsFromFile(String folderPath, String mapFileName) {
        loadBacksoundsFromFile(matrix, folderPath, mapFileName, handler.getGameIndex());
    }

    private static void loadBacksound(String path, MapData mapData, int game) throws Exception {
        if (game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
            mapData.setBacksound(new Backsound(path));
        } else {
            mapData.setBacksound(new Backsound());
        }
    }

    public static void loadCollisionsFromFile(Map<Point, MapData> matrix, String folderPath, String mapFileName, int gameIndex) {
        if (matrix.size() == 1) {//OLD MAP TYPE
            Map.Entry<Point, MapData> mapEntry = matrix.entrySet().iterator().next();
            try {
                String collisionsPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), Collisions.fileExtension);
                mapEntry.getValue().setCollisions(new Collisions(collisionsPath));
            } catch (Exception ex) {
                try {
                    String collisionsPath = getFilePath(folderPath, mapFileName, Collisions.fileExtension);
                    mapEntry.getValue().setCollisions(new Collisions(collisionsPath));
                } catch (Exception ex2) {
                    mapEntry.getValue().setCollisions(new Collisions(gameIndex));
                }
            }
            if (Game.isGenV(gameIndex)) {
                try {
                    String collisionsPath = getFilePathWithCoords(matrix, folderPath, mapFileName, "2",
                            mapEntry.getKey(), Collisions.fileExtension);
                    mapEntry.getValue().setCollisions2(new Collisions(collisionsPath));
                } catch (Exception ex) {
                    mapEntry.getValue().setCollisions2(new Collisions(gameIndex));
                }
            }
        } else {
            for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
                try {
                    String collisionsPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), Collisions.fileExtension);
                    mapEntry.getValue().setCollisions(new Collisions(collisionsPath));
                } catch (Exception ex) {
                    mapEntry.getValue().setCollisions(new Collisions(gameIndex));
                }
                if (Game.isGenV(gameIndex)) {
                    try {
                        String collisionsPath = getFilePathWithCoords(matrix, folderPath, mapFileName, "2",
                                mapEntry.getKey(), Collisions.fileExtension);
                        mapEntry.getValue().setCollisions2(new Collisions(collisionsPath));
                    } catch (Exception ex) {
                        mapEntry.getValue().setCollisions2(new Collisions(gameIndex));
                    }
                }
            }
        }
    }

    public void loadCollisionsFromFile(String folderPath, String mapFileName) {
        loadCollisionsFromFile(matrix, folderPath, mapFileName, handler.getGameIndex());
    }

    public static void loadBuildingsFromFile(Map<Point, MapData> matrix, String folderPath, String mapFileName) {
        if (matrix.size() == 1) {//OLD MAP TYPE
            Map.Entry<Point, MapData> mapEntry = matrix.entrySet().iterator().next();
            try {
                String buildingsPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), BuildFile.fileExtension);
                mapEntry.getValue().setBuildings(new BuildFile(buildingsPath));
                //handler.setBuildings(new BuildFile(buildingsPath));
            } catch (Exception ex) {
                try {
                    String buildingsPath = getFilePath(folderPath, mapFileName, BuildFile.fileExtension);
                    mapEntry.getValue().setBuildings(new BuildFile(buildingsPath));
                    //handler.setBuildings(new BuildFile(buildingsPath));
                } catch (Exception ex2) {
                    mapEntry.getValue().setBuildings(new BuildFile());
                    //handler.setBuildings(new BuildFile());
                }
            }
        } else {
            for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
                try {
                    String buildingsPath = getFilePathWithCoords(matrix, folderPath, mapFileName, mapEntry.getKey(), BuildFile.fileExtension);
                    mapEntry.getValue().setBuildings(new BuildFile(buildingsPath));
                    //handler.setBuildings(new BuildFile(buildingsPath));
                } catch (Exception ex) {
                    mapEntry.getValue().setBuildings(new BuildFile());
                    //handler.setBuildings(new BuildFile());
                }
            }
        }
    }

    public void loadBuildingsFromFile(String folderPath, String mapFileName) {
        loadBuildingsFromFile(matrix, folderPath, mapFileName);
    }

    public static String getFilePathWithCoords(Map<Point, MapData> matrix, String folderPath, String mapFileName, Point mapCoords, String extensionName) {
        return getFilePathWithCoords(matrix, folderPath, mapFileName, "", mapCoords, extensionName);
    }

    public static String getFilePathWithCoords(Map<Point, MapData> matrix, String folderPath, String mapFileName, String nameEnd, Point mapCoords, String extensionName) {
        //String filename = Utils.removeExtensionFromPath(mapFileName) + nameEnd;
        //Point minCoords = getMinCoords(matrix);
        //filename += "_" + String.format("%02d", mapCoords.x - minCoords.x) + "_" + String.format("%02d", mapCoords.y - minCoords.y);
        String filename = getMapName(matrix, mapFileName, nameEnd, mapCoords);
        return folderPath + File.separator + filename + "." + extensionName;
    }

    public static String getMapName(Map<Point, MapData> matrix, String mapFileName, String nameEnd, Point mapCoords){
        String mapName = Utils.removeExtensionFromPath(mapFileName) + nameEnd;
        Point minCoords = getMinCoords(matrix);
        mapName += "_" + String.format("%02d", mapCoords.x - minCoords.x) + "_" + String.format("%02d", mapCoords.y - minCoords.y);
        return mapName;
    }

    public String getMapName(Point mapCoords){
        return getMapName(this.matrix, new File(filePath).getName(),"",mapCoords);
    }

    private static String getFilePath(String folderPath, String mapFileName, String extensionName) {
        String filename = Utils.removeExtensionFromPath(mapFileName);
        return folderPath + File.separator + filename + "." + extensionName;
    }

    public MapData getMapAndCreate(Point mapCoords) {
        MapData mapData = matrix.get(mapCoords);
        if (mapData == null) {
            System.out.println("NEW MAP at " + mapCoords.x + " " + mapCoords.y);
            mapData = new MapData(handler);
            matrix.put(mapCoords, mapData);

            updateBordersData();
        }
        return mapData;
    }

    public MapData getMap(Point mapCoords) {
        return matrix.get(mapCoords);
    }

    public void updateBorderMaps() {
        Set<Point> mapCoords = matrix.keySet();
        this.borderMaps = new HashSet<>(mapCoords.size() * 8); //Approximation

        //Set borders
        for (Point map : mapCoords) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    borderMaps.add(new Point(map.x + i, map.y + j));
                }
            }
        }

        //Clear interior
        for (Point map : mapCoords) {
            borderMaps.remove(map);
        }

        //System.out.println("Border maps updated! " + borderMaps.size());
    }

    public void updateContourPoints(Set<Integer> areaIndices) {
        contourPoints = generateAllContourPoints(new Point(0, 0), areaIndices);
        contourPointsBuffer = generateAllContourPointsGL(contourPoints);
    }

    public Map<Integer, Color> generateAreaColors(Set<Integer> areaIndices) {
        Map<Integer, Color> colors = new HashMap<>(areaIndices.size());
        for (Integer areaIndex : areaIndices) {
            colors.put(areaIndex, new Color(Color.HSBtoRGB((areaIndex * 45.0f + 155f) / 255f, 0.8f, 1.0f)));
        }
        return colors;
    }

    public void updateAreaColors(Set<Integer> areaIndices) {
        areaColors = generateAreaColors(areaIndices);
    }

    public MapData getMapAndCreate(int x, int y) {
        return getMapAndCreate(new Point(x, y));
    }

    public Map<Point, MapData> getMatrix() {
        return matrix;
    }

    public Set<Point> getBorderMaps() {
        return borderMaps;
    }

    public Point getMinCoords() {
        return getMinCoords(matrix);
    }

    public static Point getMinCoords(Map<Point, MapData> matrix) {
        if (matrix.size() > 0) {
            Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
            Set<Point> coords = matrix.keySet();
            for (Point p : coords) {
                if (p.x < min.x) {
                    min.x = p.x;
                }
                if (p.y < min.y) {
                    min.y = p.y;
                }
            }
            return min;
        } else {
            return new Point(0, 0);
        }
    }

    public Point getMaxCoords() {
        return getMaxCoords(matrix);
    }

    public static Point getMaxCoords(Map<Point, MapData> matrix) {
        if (matrix.size() > 0) {
            Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
            Set<Point> coords = matrix.keySet();
            for (Point p : coords) {
                if (p.x > max.x) {
                    max.x = p.x;
                }
                if (p.y > max.y) {
                    max.y = p.y;
                }
            }
            return max;
        } else {
            return new Point(0, 0);
        }
    }

    public Dimension getMatrixSize() {
        return getMatrixSize(matrix);
    }

    public static Dimension getMatrixSize(Map<Point, MapData> matrix) {
        Point min = getMinCoords(matrix);
        Point max = getMaxCoords(matrix);
        return new Dimension(max.x - min.x + 1, max.y - min.y + 1);
    }

    public void removeUnusedMaps() {
        matrix.entrySet().removeIf(e -> e.getValue().isUnused() && matrix.size() > 1);
        updateBordersData();
    }

    public void updateAllLayersGL() {
        for (MapData mapData : matrix.values()) {
            mapData.getGrid().updateAllMapLayers(handler.useRealTimePostProcessing());
        }
    }

    public void removeAllUnusedMapFiles() {
        try {
            String folderPath = new File(filePath).getParent();

            removeUnusedMapFiles(folderPath, "obj");
            removeUnusedMapFiles(folderPath, "mtl");
            removeUnusedMapFiles(folderPath, "imd");
            removeUnusedMapFiles(folderPath, "nsbmd");
            removeUnusedMapFiles(folderPath, Bdhc.fileExtension);
            removeUnusedMapFiles(folderPath, Collisions.fileExtension);
            removeUnusedMapFiles(folderPath, BuildFile.fileExtension);
            removeUnusedMapFiles(folderPath, Backsound.fileExtension);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void removeUnusedMapFiles(String folderPath, String fileExtension) {
        Point minCoords = getMinCoords();

        if (folderPath != null) {
            File folder = new File(folderPath);
            File[] filesToRemove = folder.listFiles((dir, name) -> canRemoveMapFile(name, fileExtension, minCoords));

            for (File file : filesToRemove) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void clearAllCollisions() {
        for (MapData mapData : matrix.values()) {
            mapData.setCollisions(new Collisions(handler.getGameIndex()));
            mapData.setCollisions2(new Collisions(handler.getGameIndex()));
        }
    }

    public List<Point> generateContourPoints(Point min) {
        List<Point> contourPoints = new ArrayList<>(matrix.size() * 3);//Approximation
        for (Point p : matrix.keySet()) {
            if (!matrix.containsKey(new Point(p.x - 1, p.y))) {
                contourPoints.add(new Point(p.x - min.x, p.y - min.y));
                contourPoints.add(new Point(p.x - min.x, p.y + 1 - min.y));
            }
            if (!matrix.containsKey(new Point(p.x + 1, p.y))) {
                contourPoints.add(new Point(p.x + 1 - min.x, p.y - min.y));
                contourPoints.add(new Point(p.x + 1 - min.x, p.y + 1 - min.y));
            }
            if (!matrix.containsKey(new Point(p.x, p.y - 1))) {
                contourPoints.add(new Point(p.x - min.x, p.y - min.y));
                contourPoints.add(new Point(p.x + 1 - min.x, p.y - min.y));
            }
            if (!matrix.containsKey(new Point(p.x, p.y + 1))) {
                contourPoints.add(new Point(p.x - min.x, p.y + 1 - min.y));
                contourPoints.add(new Point(p.x + 1 - min.x, p.y + 1 - min.y));
            }
        }
        return contourPoints;
    }

    public Set<Integer> getAreaIndices() {
        return matrix.values().stream()
                .map(MapData::getAreaIndex)
                .collect(Collectors.toSet());
    }

    public int getNumAreas() {
        return getAreaIndices().size();
    }

    public Map<Integer, List<Point>> generateAllContourPoints(Point min, Set<Integer> areaIndices) {

        Map<Integer, List<Point>> allContours = new HashMap<>(areaIndices.size());
        for (Integer areaIndex : areaIndices) {
            allContours.put(areaIndex, new ArrayList<>());
        }

        for (Map.Entry<Point, MapData> mapEntry : matrix.entrySet()) {
            Point p = mapEntry.getKey();
            MapData map = mapEntry.getValue();

            List<Point> contourPoints = allContours.get(map.getAreaIndex());
            if (canAddContour(p, new Point(p.x - 1, p.y))) {
                contourPoints.add(new Point(p.x - min.x, p.y - min.y));
                contourPoints.add(new Point(p.x - min.x, p.y + 1 - min.y));
            }
            if (canAddContour(p, new Point(p.x + 1, p.y))) {
                contourPoints.add(new Point(p.x + 1 - min.x, p.y - min.y));
                contourPoints.add(new Point(p.x + 1 - min.x, p.y + 1 - min.y));
            }
            if (canAddContour(p, new Point(p.x, p.y - 1))) {
                contourPoints.add(new Point(p.x - min.x, p.y - min.y));
                contourPoints.add(new Point(p.x + 1 - min.x, p.y - min.y));
            }
            if (canAddContour(p, new Point(p.x, p.y + 1))) {
                contourPoints.add(new Point(p.x - min.x, p.y + 1 - min.y));
                contourPoints.add(new Point(p.x + 1 - min.x, p.y + 1 - min.y));
            }
        }
        return allContours;
    }

    private boolean canAddContour(Point p, Point nearP) {
        return !matrix.containsKey(nearP) || matrix.get(p).getAreaIndex() != matrix.get(nearP).getAreaIndex();
    }

    public float[] generateContourPointsGL(List<Point> points) {
        float[] contourPointsGL = new float[points.size() * 3];
        int i = 0;
        for (Point p : points) {
            contourPointsGL[i] = p.x;
            contourPointsGL[i + 1] = p.y;
            contourPointsGL[i + 2] = 0.0f;
            i += 3;
        }
        return contourPointsGL;
    }

    public Map<Integer, FloatBuffer> generateAllContourPointsGL(Map<Integer, List<Point>> contourPoints) {
        Map<Integer, FloatBuffer> allContourPoints = new HashMap<>(contourPoints.size());
        for (Map.Entry<Integer, List<Point>> entry : contourPoints.entrySet()) {
            List<Point> points = entry.getValue();
            float[] contourPointsGL = new float[points.size() * 3];
            int i = 0;
            for (Point p : points) {
                contourPointsGL[i] = p.x;
                contourPointsGL[i + 1] = p.y;
                contourPointsGL[i + 2] = 0.0f;
                i += 3;
            }
            allContourPoints.put(entry.getKey(), Buffers.newDirectFloatBuffer(contourPointsGL));
        }

        return allContourPoints;
    }

    private boolean canRemoveMapFile(String fileName, String fileExtension, Point minCoords) {
        try {
            //System.out.println("MAP FILE: " + fileName);
            return fileName.endsWith("." + fileExtension)
                    && nameHasMapCoords(fileName)
                    && !isMapFileUsed(fileName, minCoords);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isMapFileUsed(String fileName, Point minCoords) {
        Point mapCoords = geMapCoordsFromName(fileName);

        mapCoords.x += minCoords.x;
        mapCoords.y += minCoords.y;

        //System.out.println(mapCoords.x + " " + mapCoords.y + " " + fileName + " USED: " + matrix.keySet().contains(mapCoords));
        return matrix.containsKey(mapCoords);
    }

    private Point geMapCoordsFromName(String fileName) {
        String name = Utils.removeExtensionFromPath(fileName);
        String[] splitName = name.split("_");
        return new Point(Integer.parseInt(splitName[splitName.length - 2]), Integer.parseInt(splitName[splitName.length - 1]));
    }

    private boolean nameHasMapCoords(String fileName) {
        //System.out.println(fileName);
        String name = Utils.removeExtensionFromPath(fileName);
        try {
            String[] splitName = name.split("_");
            return canParseInteger(splitName[splitName.length - 1]) && canParseInteger(splitName[splitName.length - 2]);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean canParseInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Map<Integer, FloatBuffer> getContourPointsGL() {
        return contourPointsBuffer;
    }

    public Map<Integer, List<Point>> getContourPoints() {
        return contourPoints;
    }

    public Map<Integer, Color> getAreaColors() {
        return areaColors;
    }

    public void moveMap(Point src, Point dst) {
        if (matrix.containsKey(dst)) {
            MapData copy = matrix.get(dst);
            matrix.put(dst, matrix.get(src));
            matrix.put(src, copy);
        } else {
            matrix.put(dst, matrix.get(src));
            matrix.remove(src);
        }
    }

    public Map<Point, MapGrid> generateGridHashMap() {
        return matrix.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, mapEntry -> mapEntry.getValue().getGrid(),
                        (a, b) -> b, () -> new HashMap<>(matrix.size())));
    }

    public BufferedImage getMapMatrixImage() {
        try {
            Point min = handler.getMapMatrix().getMinCoords();
            Dimension size = handler.getMapMatrix().getMatrixSize();

            BufferedImage mapsImg = new BufferedImage(
                    size.width * MapData.mapThumbnailSize,
                    size.height * MapData.mapThumbnailSize,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics g = mapsImg.getGraphics();

            for (Map.Entry<Point, MapData> map : handler.getMapMatrix().getMatrix().entrySet()) {
                Point p = map.getKey();
                int x = (p.x - min.x) * MapData.mapThumbnailSize;
                int y = (p.y - min.y) * MapData.mapThumbnailSize;
                g.drawImage(map.getValue().getMapThumbnail(), x, y, null);

                try {
                    Color c = handler.getMapMatrix().getAreaColors().get(map.getValue().getAreaIndex());
                    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
                    g.fillRect(x, y, MapData.mapThumbnailSize - 1, MapData.mapThumbnailSize - 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                g.setColor(Color.white);
                g.drawRect(x, y, MapData.mapThumbnailSize - 1, MapData.mapThumbnailSize - 1);
            }

            //List<Point> contourPoints = handler.getMapMatrix().generateContourPoints(handler.getMapMatrix().getMinCoords());
            Map<Integer, List<Point>> allContourPoints = handler.getMapMatrix().getContourPoints();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(8));
            for (Map.Entry<Integer, List<Point>> entry : allContourPoints.entrySet()) {
                List<Point> contourPoints = entry.getValue();
                for (int i = 0; i < contourPoints.size(); i += 2) {
                    try {
                        g.setColor(handler.getMapMatrix().getAreaColors().get(entry.getKey()));
                    } catch (Exception ex) {
                        g.setColor(Color.blue);
                    }
                    Point p1 = contourPoints.get(i);
                    Point p2 = contourPoints.get(i + 1);
                    g.drawLine(
                            (p1.x - min.x) * MapData.mapThumbnailSize,
                            (p1.y - min.y) * MapData.mapThumbnailSize,
                            (p2.x - min.x) * MapData.mapThumbnailSize,
                            (p2.y - min.y) * MapData.mapThumbnailSize);
                }
            }
            return mapsImg;
        } catch (Exception ex) {
            return null;
        }
    }
}
