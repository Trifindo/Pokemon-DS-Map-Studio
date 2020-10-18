
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
public class BdhcLoaderHGSS {

    private static final int offsetNumCoords = 0x04;
    private static final int offsetNumSlopes = 0x06;
    private static final int offsetNumHeights = 0x08;
    private static final int offsetNumPlates = 0x0A;

    private static final int sizeCoords = 0x08;
    private static final int sizeSlope = 0x0C;
    private static final int sizeHeight = 0x04;
    private static final int sizePlate = 0x08;

    private static final int offsetCoordsData = 0x10;
    private int offsetSlopeData;
    private int offsetHeightData;
    private int offsetPlateData;

    private int numCoords;
    private int numSlopes;
    private int numHeights;
    private int numPlates;
    private static final int indicesPerPlate = 4;

    private int[] xCoords;
    private float[] zCoords;
    private int[] zCoordsInt;
    private int[] yCoords;

    private int[] xSlopes;
    private int[] ySlopes;
    private int[] zSlopes;

    private int[][] plateIndices;

    public Bdhc loadBdhcFromFile(String path) throws IOException {

        return new Bdhc(loadPlatesFromBdhcDP(path));
    }

    private ArrayList<Plate> loadPlatesFromBdhcDP(String path) throws IOException {
        ArrayList plates = new ArrayList<>();

        File file = new File(path);
        byte[] data = Files.readAllBytes(file.toPath());

        numCoords = dataToUnsignedShort(data, offsetNumCoords);
        numSlopes = dataToUnsignedShort(data, offsetNumSlopes);
        numHeights = dataToUnsignedShort(data, offsetNumHeights);
        numPlates = dataToUnsignedShort(data, offsetNumPlates);

        offsetSlopeData = offsetCoordsData + sizeCoords * numCoords;
        offsetHeightData = offsetSlopeData + sizeSlope * numSlopes;
        offsetPlateData = offsetHeightData + sizeHeight * numHeights;

        xCoords = new int[numCoords];
        yCoords = new int[numCoords];
        for (int i = 0; i < numCoords; i++) {
            int offset = offsetCoordsData + i * sizeCoords;
            xCoords[i] = dataToSignedShort(data, offset + 2);
            yCoords[i] = dataToSignedShort(data, offset + 6);
        }

        xSlopes = new int[numSlopes];
        ySlopes = new int[numSlopes];
        zSlopes = new int[numSlopes];
        for (int i = 0; i < numSlopes; i++) {
            int offset = offsetSlopeData + i * sizeSlope;
            xSlopes[i] = dataToSignedInt(data, offset);
            zSlopes[i] = dataToSignedInt(data, offset + 4);
            ySlopes[i] = dataToSignedInt(data, offset + 8);

            //System.out.println("Slopes: " + xSlopes[i] + " " + zSlopes[i] + " " + ySlopes[i]);
        }

        zCoords = new float[numHeights];
        zCoordsInt = new int[numHeights];
        for (int i = 0; i < numHeights; i++) {
            zCoords[i] = dataToCoordZ(data, offsetHeightData + i * sizeHeight);
            zCoordsInt[i] = dataToSignedInt(data, offsetHeightData + i * sizeHeight);
        }

        plateIndices = new int[numPlates][indicesPerPlate];
        for (int i = 0; i < numPlates; i++) {
            int offset = offsetPlateData + i * sizePlate;
            for (int j = 0; j < indicesPerPlate; j++) {
                plateIndices[i][j] = dataToUnsignedShort(data, offset + j * 2);
            }
        }

        for (int i = 0; i < numPlates; i++) {
            int x = xCoords[plateIndices[i][0]];
            int y = yCoords[plateIndices[i][0]];
            int width = xCoords[plateIndices[i][1]] - xCoords[plateIndices[i][0]];
            int height = yCoords[plateIndices[i][1]] - yCoords[plateIndices[i][0]];
            int slopeIndex = plateIndices[i][2];

            int type = getType(xSlopes[slopeIndex], zSlopes[slopeIndex], ySlopes[slopeIndex]);

            float d = zCoords[plateIndices[i][3]];
            float z = calculateZ(d, i);

            if (type == Plate.OTHER) {
                plates.add(new Plate(x, y, z, width, height, type, new int[]{
                    xSlopes[slopeIndex], zSlopes[slopeIndex], ySlopes[slopeIndex]}));
            } else {
                plates.add(new Plate(x, y, z, width, height, type));
            }

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

    public static int getType(int xSlope, int zSlope, int ySlope) {
        //TODO check if bridge

        for (int i = 0; i < Plate.slopes.length; i++) {
            int[] slopes = Plate.slopes[i];
            if (xSlope == slopes[0] && zSlope == slopes[1] && ySlope == slopes[2]) {
                return i;
            }
        }
        return Plate.OTHER;
    }

    public static int dataToUnsignedShort(byte[] data, int offset) {
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
        float value = (float) (-decimalPart - (fractionalPart & 0xFFFF) / 65536f); //TODO: Do not use minus sign?
        //System.out.println("dec: " + decimalPart + " frac: " + (fractionalPart & 0xFFFF) + " value: " + value);
        return value;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
