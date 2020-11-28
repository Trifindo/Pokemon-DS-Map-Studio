
package editor.handler;

import formats.backsound.Backsound;
import formats.bdhc.Bdhc;
import editor.buildingeditor2.buildfile.BuildFile;
import formats.bdhcam.Bdhcam;
import formats.collisions.Collisions;
import editor.grid.MapGrid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @author Trifindo
 */
public class MapData {

    //Map Editor Handler
    private MapEditorHandler handler;

    //Map grid
    private MapGrid grid;

    //Bdhc
    private Bdhc bdhc;

    //Bdhcam
    private Bdhcam bdhcam;

    //Backsound
    private Backsound backsound;

    //Collisions
    private Collisions collisions;
    private Collisions collisions2;

    //Building file
    private BuildFile buildings;

    //Map thumbnail
    public static final int mapThumbnailSize = 64;
    private static final int smallTileSize = 2;
    private BufferedImage mapThumbnail;

    //Area index
    private int areaIndex;

    public MapData(MapEditorHandler handler) {
        this.handler = handler;
        grid = new MapGrid(handler);

        bdhc = new Bdhc();
        backsound = new Backsound();
        collisions = new Collisions(handler.getGameIndex());
        collisions2 = new Collisions(handler.getGameIndex());;
        buildings = new BuildFile();
        bdhcam = new Bdhcam();

        areaIndex = 0;
        //System.out.println("Map data created");
    }

    public void updateMapThumbnail() {
        int[][][] tiles = grid.tileLayers;
        int[][][] heights = grid.heightLayers;

        mapThumbnail = new BufferedImage(mapThumbnailSize, mapThumbnailSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = mapThumbnail.getGraphics();

        g.setColor(new Color(0.0f, 0.5f, 0.5f, 1.0f));
        g.fillRect(0, 0, mapThumbnail.getWidth(), mapThumbnail.getHeight());

        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                try {
                    int maxHeight = -Integer.MIN_VALUE;
                    int maxHeightIndex = 0;
                    for (int k = 0; k < MapGrid.numLayers; k++) {
                        if (heights[k][i][j] > maxHeight && tiles[k][i][j] > -1) {
                            maxHeight = heights[k][i][j];
                            maxHeightIndex = k;
                        }
                    }

                    int tileIndex = tiles[maxHeightIndex][i][j];
                    if (tileIndex != -1) {
                        BufferedImage tileThumbnail = handler.getTileset().get(tileIndex).getSmallThumbnail();

                        g.drawImage(tileThumbnail,
                                i * smallTileSize,
                                (MapGrid.cols - j - 1) * smallTileSize - (tileThumbnail.getHeight() - smallTileSize), //+ tileThumbnail.getHeight(),
                                null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public MapGrid getGrid() {
        return grid;
    }

    public void setGrid(MapGrid grid) {
        this.grid = grid;
    }

    public Bdhc getBdhc() {
        return bdhc;
    }

    public void setBdhc(Bdhc bdhc) {
        this.bdhc = bdhc;
    }

    public Backsound getBacksound() {
        return backsound;
    }

    public void setBacksound(Backsound backsound) {
        this.backsound = backsound;
    }

    public Collisions getCollisions() {
        return collisions;
    }

    public void setCollisions(Collisions collisions) {
        this.collisions = collisions;
    }

    public Collisions getCollisions2() {
        return collisions2;
    }

    public void setCollisions2(Collisions collisions2) {
        this.collisions2 = collisions2;
    }

    public BuildFile getBuildings() {
        return buildings;
    }

    public void setBuildings(BuildFile buildings) {
        this.buildings = buildings;
    }

    public BufferedImage getMapThumbnail() {
        return mapThumbnail;
    }

    public boolean isUnused() {
        return grid.isEmpty();
    }

    public void setAreaIndex(int areaIndex) {
        this.areaIndex = areaIndex;
    }

    public int getAreaIndex() {
        return areaIndex;
    }

    public Bdhcam getBdhcam() {
        return bdhcam;
    }

    public void setBdhcam(Bdhcam bdhcam) {
        this.bdhcam = bdhcam;
    }
}
