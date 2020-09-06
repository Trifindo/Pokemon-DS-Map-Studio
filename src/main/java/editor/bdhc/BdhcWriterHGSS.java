/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bdhc;

import java.awt.Point;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Trifindo
 */
public class BdhcWriterHGSS {

    public static void writeBdhc(Bdhc bdhc, String path) throws IOException {

        int[][] pointIndices = new int[bdhc.getPlates().size()][2];
        ArrayList<Point> points = getPoints(bdhc, pointIndices);
        int[] slopeIndices = new int[bdhc.getPlates().size()];
        ArrayList<Slope> slopes = getSlopes(bdhc, slopeIndices);
        int[] zIndices = new int[bdhc.getPlates().size()];
        ArrayList<Integer> zCoords = getCoordsZ(bdhc, zIndices);
        ArrayList<Stripe> stripes = calculateStripes(bdhc);

        FileOutputStream out = new FileOutputStream(path);

        writeHeader(out, bdhc, points, slopes, zCoords, stripes);

        writePoints(out, points);

        writeSlopes(out, slopes);

        writeCoordsZ(out, zCoords);

        writePlates(out, pointIndices, slopeIndices, zIndices);

        writeStripes(out, stripes);

        writeIndices(out, stripes);

        out.close();

    }

    private static void writeHeader(FileOutputStream out, Bdhc bdhc,
            ArrayList<Point> points, ArrayList<Slope> slopes, ArrayList<Integer> zCoords,
            ArrayList<Stripe> stripes) throws IOException {

        writeString(out, "BDHC");

        writeShortValue(out, points.size());
        writeShortValue(out, slopes.size());
        writeShortValue(out, zCoords.size());
        writeShortValue(out, bdhc.getPlates().size());
        writeShortValue(out, stripes.size());
        writeShortValue(out, getNumPlateIndices(stripes));
    }

    private static void writeIndices(FileOutputStream out,
            ArrayList<Stripe> stripes) throws IOException {
        for (int i = 0; i < stripes.size(); i++) {
            Stripe stripe = stripes.get(i);
            for (int j = 0; j < stripe.plateIndices.size(); j++) {
                writeShortValue(out, stripe.plateIndices.get(j));
            }
        }
    }

    private static void writeStripes(FileOutputStream out, ArrayList<Stripe> stripes) throws IOException {
        int offset = 0;
        for (int i = 0; i < stripes.size(); i++) {
            Stripe stripe = stripes.get(i);
            writeShortValue(out, 0);
            writeShortValue(out, stripe.y);
            writeShortValue(out, stripe.plateIndices.size());
            writeShortValue(out, offset);
            offset += stripe.plateIndices.size();
        }
    }

    private static void writePlates(FileOutputStream out,
            int[][] pointIndices, int[] slopeIndices, int[] zIndices) throws IOException {

        for (int i = 0; i < pointIndices.length; i++) {
            writeShortValue(out, pointIndices[i][0]);
            writeShortValue(out, pointIndices[i][1]);
            writeShortValue(out, slopeIndices[i]);
            writeShortValue(out, zIndices[i]);
        }
    }

    private static void writeCoordsZ(FileOutputStream out, ArrayList<Integer> coordsZ)
            throws IOException {
        for (int i = 0; i < coordsZ.size(); i++) {
            writeIntValue(out, coordsZ.get(i));
        }
    }

    private static void writeSlopes(FileOutputStream out, ArrayList<Slope> slopes)
            throws IOException {
        for (int i = 0; i < slopes.size(); i++) {
            Slope slope = slopes.get(i);
            writeIntValue(out, slope.x);
            writeIntValue(out, slope.y);
            writeIntValue(out, slope.z);
        }

    }

    private static void writePoints(FileOutputStream out, ArrayList<Point> points)
            throws IOException {
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            writeShortValue(out, 0);
            writeShortValue(out, point.x);
            writeShortValue(out, 0);
            writeShortValue(out, point.y);
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

    private static void writeString(FileOutputStream out, String s)
            throws IOException {
        out.write(s.getBytes());
    }

    private static void writeShortValue(FileOutputStream out, int value)
            throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        out.write(buffer.putShort((short) value).array());
    }

    private static void writeIntValue(FileOutputStream out, int value)
            throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        out.write(buffer.putInt(value).array());
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
