
package formats.bdhc;

import utils.BinaryBufferWriter;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Trifindo
 */
public class BdhcWriterHGSS {

    public static void writeBdhc(Bdhc bdhc, String path) throws IOException{
        byte[] byteData = bdhcToByteArray(bdhc);
        Files.write(new File(path).toPath(), byteData);
    }

    public static byte[] bdhcToByteArray(Bdhc bdhc) throws IOException {

        BinaryBufferWriter writer = new BinaryBufferWriter();

        int[][] pointIndices = new int[bdhc.getPlates().size()][2];
        ArrayList<Point> points = getPoints(bdhc, pointIndices);
        int[] slopeIndices = new int[bdhc.getPlates().size()];
        ArrayList<Slope> slopes = getSlopes(bdhc, slopeIndices);
        int[] zIndices = new int[bdhc.getPlates().size()];
        ArrayList<Integer> zCoords = getCoordsZ(bdhc, zIndices);
        ArrayList<Stripe> stripes = calculateStripes(bdhc);

        //FileOutputStream out = new FileOutputStream(path);

        writeHeader(writer, bdhc, points, slopes, zCoords, stripes);

        writePoints(writer, points);

        writeSlopes(writer, slopes);

        writeCoordsZ(writer, zCoords);

        writePlates(writer, pointIndices, slopeIndices, zIndices);

        writeStripes(writer, stripes);

        writeIndices(writer, stripes);

        //out.close();
        return writer.toByteArray();
    }

    private static void writeHeader(BinaryBufferWriter writer, Bdhc bdhc,
                                    ArrayList<Point> points, ArrayList<Slope> slopes, ArrayList<Integer> zCoords,
                                    ArrayList<Stripe> stripes) throws IOException {

        writeString(writer, "BDHC");

        writeShortValue(writer, points.size());
        writeShortValue(writer, slopes.size());
        writeShortValue(writer, zCoords.size());
        writeShortValue(writer, bdhc.getPlates().size());
        writeShortValue(writer, stripes.size());
        writeShortValue(writer, getNumPlateIndices(stripes));
    }

    private static void writeIndices(BinaryBufferWriter writer,
                                     ArrayList<Stripe> stripes) throws IOException {
        for (int i = 0; i < stripes.size(); i++) {
            Stripe stripe = stripes.get(i);
            for (int j = 0; j < stripe.plateIndices.size(); j++) {
                writeShortValue(writer, stripe.plateIndices.get(j));
            }
        }
    }

    private static void writeStripes(BinaryBufferWriter writer, ArrayList<Stripe> stripes) throws IOException {
        int offset = 0;
        for (int i = 0; i < stripes.size(); i++) {
            Stripe stripe = stripes.get(i);
            writeShortValue(writer, 0);
            writeShortValue(writer, stripe.y);
            writeShortValue(writer, stripe.plateIndices.size());
            writeShortValue(writer, offset);
            offset += stripe.plateIndices.size();
        }
    }

    private static void writePlates(BinaryBufferWriter writer,
                                    int[][] pointIndices, int[] slopeIndices, int[] zIndices) throws IOException {

        for (int i = 0; i < pointIndices.length; i++) {
            writeShortValue(writer, pointIndices[i][0]);
            writeShortValue(writer, pointIndices[i][1]);
            writeShortValue(writer, slopeIndices[i]);
            writeShortValue(writer, zIndices[i]);
        }
    }

    private static void writeCoordsZ(BinaryBufferWriter writer, ArrayList<Integer> coordsZ)
            throws IOException {
        for (int i = 0; i < coordsZ.size(); i++) {
            writeIntValue(writer, coordsZ.get(i));
        }
    }

    private static void writeSlopes(BinaryBufferWriter writer, ArrayList<Slope> slopes)
            throws IOException {
        for (int i = 0; i < slopes.size(); i++) {
            Slope slope = slopes.get(i);
            writeIntValue(writer, slope.x);
            writeIntValue(writer, slope.y);
            writeIntValue(writer, slope.z);
        }

    }

    private static void writePoints(BinaryBufferWriter writer, ArrayList<Point> points)
            throws IOException {
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            writeShortValue(writer, 0);
            writeShortValue(writer, point.x);
            writeShortValue(writer, 0);
            writeShortValue(writer, point.y);
        }
    }

    private static ArrayList<Stripe> calculateStripes(Bdhc bdhc) {
        ArrayList<PlateInfo> platesInfo = new ArrayList<>();

        //Get plate info of plates inside group
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);
            platesInfo.add(new PlateInfo(i, p.y + p.height));
        }

        //Sort by height
        Collections.sort(platesInfo);

        //Group by height and form stripes
        ArrayList<Stripe> stripes = new ArrayList<>();
        int previousY = -16;
        for (int i = 0; i < platesInfo.size(); i++) {
            int y = platesInfo.get(i).y;
            if (y != previousY) {
                int yMinBounds = previousY;
                int yMaxBounds = y;

                Stripe stripe = new Stripe(y);
                for (int j = 0; j < platesInfo.size(); j++) {
                    Plate p = bdhc.getPlate(platesInfo.get(j).plateIndex);
                    if (yMinBounds < p.y + p.height && yMaxBounds > p.y) {
                        stripe.plateIndices.add(platesInfo.get(j).plateIndex);
                    }
                }
                stripes.add(stripe);
                previousY = y;
            }
        }

        //For each stripe add colindant plates
        for (int i = 0; i < stripes.size() - 1; i++) {
            Stripe currentStripe = stripes.get(i);
            Stripe nextStripe = stripes.get(i + 1);
            for (int j = 0; j < nextStripe.plateIndices.size(); j++) {
                int index = nextStripe.plateIndices.get(j);
                if (!currentStripe.plateIndices.contains(index)) {
                    currentStripe.plateIndices.add(index);
                }
            }
        }

        //Sort stripes elements on X axis
        for (int i = 0; i < stripes.size(); i++) {
            stripes.get(i).sortPlateIndices(bdhc.getPlates());
        }

        return stripes;
    }

    private static ArrayList<Integer> getCoordsZ(Bdhc bdhc, int[] zIndices) {
        ArrayList<Integer> zCoords = new ArrayList<>();

        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);

            float z = p.z;
            float d;

            int[] slope = p.getSlope();
            final float xd = slope[0] / Plate.SLOPE_UNIT;
            final float zd = slope[1] / Plate.SLOPE_UNIT;
            final float yd = slope[2] / Plate.SLOPE_UNIT;

            final float mx = -xd / zd;
            final float my = -yd / zd;

            final float zero = 0.001f;
            if (Math.abs(xd) > zero) {
                float x;
                if (xd > zero) {
                    x = p.x + p.width;
                } else {
                    x = p.x;
                }
                float n = z - mx * x;
                d = (float) -(n / Math.sqrt(mx * mx + 1.0f));
            } else if (Math.abs(yd) > zero) {
                float y;
                if (yd > zero) {
                    y = p.y + p.height;
                } else {
                    y = p.y;
                }
                float n = z - my * y;
                d = (float) -(n / Math.sqrt(my * my + 1.0f));
            } else {
                d = -z;
            }

            int zValue = getIntegerValueZ(d);
            //System.out.println(Integer.toHexString(zValue));

            int index = zCoords.indexOf(zValue);
            if (index == -1) {
                zIndices[i] = zCoords.size();
                zCoords.add(zValue);
            } else {
                zIndices[i] = index;
            }
        }
        return zCoords;
    }

    private static ArrayList<Slope> getSlopes(Bdhc bdhc, int[] indices) {
        ArrayList<Slope> slopes = new ArrayList<>();

        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);
            Slope slope = new Slope(p.getSlope());
            int index = slopes.indexOf(slope);
            if (index == -1) {
                index = slopes.size();
                slopes.add(slope);
            }
            indices[i] = index;
        }

        return slopes;
    }

    private static ArrayList<Point> getPoints(Bdhc bdhc, int[][] coordIndices) {
        ArrayList<Point> coords = new ArrayList<>();
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);

            Point[] platePoints = new Point[]{
                    new Point(p.x, p.y),
                    new Point(p.x + p.width, p.y + p.height)
            };

            for (int j = 0; j < platePoints.length; j++) {
                int index = coords.indexOf(platePoints[j]);
                if (index == -1) {
                    coordIndices[i][j] = coords.size();
                    coords.add(platePoints[j]);
                } else {
                    coordIndices[i][j] = index;
                }
            }
        }
        return coords;
    }

    public static int getNumStripes(ArrayList<ArrayList<Stripe>> stripes) {
        int numStripes = 0;
        for (int i = 0; i < stripes.size(); i++) {
            numStripes += stripes.get(i).size();
        }
        return numStripes;
    }

    public static int getNumPlateIndices(ArrayList<Stripe> stripes) {
        int numIndices = 0;
        for (int i = 0; i < stripes.size(); i++) {
            numIndices += stripes.get(i).plateIndices.size();
        }
        return numIndices;
    }

    private static void writeString(BinaryBufferWriter writer, String s)
            throws IOException {
        writer.writeString(s);
    }

    private static void writeShortValue(BinaryBufferWriter writer, int value)
            throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        writer.write(buffer.putShort((short) value).array());
    }

    private static void writeIntValue(BinaryBufferWriter writer, int value)
            throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        writer.write(buffer.putInt(value).array());
    }

    private static int getIntegerValueZ(float value) {
        short decimalPart = (short) Math.floor(value);
        short fractionalPart = (short) ((value - decimalPart) * (65536));

        /*
        System.out.println(
                "decimal: " + Integer.toHexString(decimalPart & 0xffff)
                + " fractional: " + Integer.toHexString(fractionalPart & 0xffff));*/

        return ((decimalPart & 0xFFFF) << 16) | (fractionalPart & 0xFFFF);
    }
    /*
    private static int getIntegerValueZ(float value) {
        short decimalPart = (short) value;
        short fractionalPart = (short) ((value - decimalPart) * (65536));
        return (decimalPart << 16) + fractionalPart;
    }*/

}
