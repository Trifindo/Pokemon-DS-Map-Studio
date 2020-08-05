/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bdhc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class BdhcLoaderDP {

    private final int offsetSizeCoord = 0x08;
    private final int offsetNumCoords = 0x0A;
    private final int offsetSizeSlope = 0x0C;
    private final int offsetNumSlopes = 0x0E;
    private final int offsetSizePlate = 0x10;
    private final int offsetNumPlates = 0x12;

    private final int offsetCoordsData = 0x44;
    private int offsetSlopeData;
    private int offsetPlateData;
    private int offsetPlateIndices;

    private int sizeCoords;
    private int numCoords;
    private int sizeSlope;
    private int numSlopes;
    private int sizePlate;
    private int numPlates;
    private int numPlateIndices;

    private int[] xCoords;
    private float[] zCoords;
    private int[] yCoords;

    private int[] xSlopes;
    private int[] ySlopes;
    private int[] zSlopes;

    private int[][] plateIndices;
    private int[] slopeIndices;
    private float[] distances;
    private int[] plateIndList;

    public Bdhc loadBdhcFromFile(String path) throws IOException {
        
        return new Bdhc(loadPlatesFromBdhcDP(path));
    }

    private ArrayList<Plate> loadPlatesFromBdhcDP(String path) throws IOException {
        ArrayList plates = new ArrayList<>();

        File file = new File(path);
        byte[] data = Files.readAllBytes(file.toPath());

        sizeCoords = dataToUnsignedInt(data, offsetSizeCoord);
        numCoords = dataToUnsignedInt(data, offsetNumCoords);
        sizeSlope = dataToUnsignedInt(data, offsetSizeSlope);
        numSlopes = dataToUnsignedInt(data, offsetNumSlopes);
        sizePlate = dataToUnsignedInt(data, offsetSizePlate);
        numPlates = dataToUnsignedInt(data, offsetNumPlates);

        offsetSlopeData = offsetCoordsData + sizeCoords * numCoords;
        offsetPlateData = offsetSlopeData + sizeSlope * numSlopes;

        xCoords = new int[numCoords];
        zCoords = new float[numCoords];
        yCoords = new int[numCoords];

        for (int i = 0; i < numCoords; i++) {
            int offset = offsetCoordsData + i * sizeCoords;
            xCoords[i] = dataToSignedShort(data, offset + 2);
            zCoords[i] = dataToCoordZ(data, offset + 4);
            yCoords[i] = dataToSignedShort(data, offset + 10);
        }

        xSlopes = new int[numSlopes];
        ySlopes = new int[numSlopes];
        zSlopes = new int[numSlopes];

        for (int i = 0; i < numSlopes; i++) {
            int offset = offsetSlopeData + i * sizeSlope;
            xSlopes[i] = dataToSignedInt(data, offset);
            zSlopes[i] = dataToSignedInt(data, offset + 4);
            ySlopes[i] = dataToSignedInt(data, offset + 8);

            System.out.println(xSlopes[i] + " " + zSlopes[i] + " " + ySlopes[i]);
        }

        plateIndices = new int[numPlates][3];
        slopeIndices = new int[numPlates];
        distances = new float[numPlates];
        for (int i = 0; i < numPlates; i++) {
            int offset = offsetPlateData + i * sizePlate;
            for (int j = 0; j < 3; j++) {
                plateIndices[i][j] = dataToUnsignedInt(data, offset + j * 2);
            }
            slopeIndices[i] = dataToUnsignedInt(data, offset + 6);
            distances[i] = dataToDistance(data, offset + 8);
        }

        plateIndList = new int[numPlateIndices];
        for (int i = 0; i < numPlateIndices; i++) {
            int offset = offsetPlateIndices + i * 2;
            plateIndList[i] = dataToUnsignedInt(data, offset);
        }

        for (int i = 0; i < numPlates; i += 2) {
            int x = minCoord(xCoords, plateIndices[i]);
            int y = minCoord(yCoords, plateIndices[i]);
            float z = minCoord(zCoords, plateIndices[i]);
            int width = maxCoord(xCoords, plateIndices[i]) - x;
            int height = maxCoord(yCoords, plateIndices[i]) - y;
            int slopeIndex = slopeIndices[i];
            int type = getType(xSlopes[slopeIndex], zSlopes[slopeIndex], ySlopes[slopeIndex]);

            if (type == Plate.OTHER) {
                plates.add(new Plate(x, y, z, width, height, type, new int[]{
                    xSlopes[slopeIndex], zSlopes[slopeIndex], ySlopes[slopeIndex]}));
            } else {
                plates.add(new Plate(x, y, z, width, height, type));
            }
            /*
            plates.add(new Plate(
                    x,
                    y,
                    z,
                    width,
                    height,
                    type));*/

        }

        return plates;
    }
    
    public float calculateZ(float d, int plateIndex) {
        int slopeIndex = plateIndices[plateIndex][2];
        //final float den = 4095.56247663f;
        final float xd = xSlopes[slopeIndex] / Plate.SLOPE_UNIT;
        final float zd = zSlopes[slopeIndex] / Plate.SLOPE_UNIT;
        final float yd = ySlopes[slopeIndex] / Plate.SLOPE_UNIT;

        final float x0 = d * xd;
        final float z0 = d * zd;
        final float y0 = d * yd;

        final float mx = -xd / zd;
        final float my = -yd / zd;

        float zero = 0.001f;
        float z;
        if (Math.abs(xd) > zero) {
            final float zx1 = getLineY(mx, x0, z0, xCoords[plateIndices[plateIndex][0]]);
            final float zx2 = getLineY(mx, x0, z0, xCoords[plateIndices[plateIndex][1]]);
            z = Math.min(zx1, zx2);
        } else if (Math.abs(yd) > zero) {
            final float zy1 = getLineY(my, y0, z0, yCoords[plateIndices[plateIndex][0]]);
            final float zy2 = getLineY(my, y0, z0, yCoords[plateIndices[plateIndex][1]]);
            z = Math.min(zy1, zy2);
        } else {
            z = d;
        }
        return round(z, 3);
    }
    
    private static float getLineY(float m, float x0, float y0, float x) {
        return m * (x - x0) + y0;
    }
    
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    
    public static float dataToDistance(byte[] data, int offset) {
        short fractionalPart = (short) dataToUnsignedShort(data, offset);
        short decimalPart = dataToSignedShort(data, offset + 2);
        float value = (float) (-decimalPart - (fractionalPart & 0xFFFF) / 65536f); //TODO: Do not use minus sign?
        System.out.println("dec: " + decimalPart + " frac: " + (fractionalPart & 0xFFFF) + " value: " + value);
        return value;
    }
    
    public static int dataToUnsignedShort(byte[] data, int offset) {
        return ((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff);
    }
    
    public static float minCoord(float[] coords, int[] indices) {
        float min = Float.MAX_VALUE;
        for (int i = 0; i < indices.length; i++) {
            if (coords[indices[i]] < min) {
                min = coords[indices[i]];
            }
        }
        return min;
    }

    public static int minCoord(int[] coords, int[] indices) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < indices.length; i++) {
            if (coords[indices[i]] < min) {
                min = coords[indices[i]];
            }
        }
        return min;
    }

    public static int getType(int xSlope, int zSlope, int ySlope) {
        //TODO check if bridge
        
        for(int i = 0; i < Plate.slopes.length; i++){
            int[] slopes = Plate.slopes[i];
            if(xSlope == slopes[0] && zSlope == slopes[1] && ySlope == slopes[2]){
                return i;
            }
        }
        return Plate.OTHER;
    }

    public static int maxCoord(int[] coords, int[] indices) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < indices.length; i++) {
            if (coords[indices[i]] > max) {
                max = coords[indices[i]];
            }
        }
        return max;
    }

    public static int min(int[] array) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static int max(int[] array) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static int dataToUnsignedInt(byte[] data, int offset) {
        return ((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff);
    }

    public static int dataToSignedInt(byte[] data, int offset) {
        byte[] bytes = new byte[]{
            data[offset],
            data[offset + 1],
            data[offset + 2],
            data[offset + 3]
        };
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static short dataToSignedShort(byte[] data, int offset) {
        return (short) (((data[offset + 1]) << 8) | (data[offset] /*& 0xff*/));
    }

    
    
    public static float dataToCoordZ(byte[] data, int offset) {
        short fractionalPart = (short) dataToUnsignedShort(data, offset);
        short decimalPart = dataToSignedShort(data, offset + 2);
        float value = (float) (decimalPart + (fractionalPart & 0xFFFF) / 65536f); //TODO: Do not use minus sign?
        System.out.println("dec: " + decimalPart + " frac: " + (fractionalPart & 0xFFFF) + " value: " + value);
        return value;
    }
    
    /*
    public static float dataToCoordZ(byte[] data, int offset) {
        short fractionalPart = dataToSignedShort(data, offset);
        short decimalPart = dataToSignedShort(data, offset + 2);
        //System.out.println(decimalPart + " " + fractionalPart);
        return (float) (decimalPart - fractionalPart / 65536f); //TODO: Do not use minus sign?
    }*/

}
