
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
import java.util.List;

/**
 * @author Trifindo
 */
public class BdhcWriterHGSS {

    public static void writeBdhc(Bdhc bdhc, String path) throws IOException {
        byte[] byteData = bdhcToByteArray(bdhc);
        Files.write(new File(path).toPath(), byteData);
    }

    public static byte[] bdhcToByteArray(Bdhc bdhc) {
        BinaryBufferWriter writer = new BinaryBufferWriter();

        int[][] pointIndices = new int[bdhc.getPlates().size()][2];
        List<Point> points = getPoints(bdhc, pointIndices);
        int[] slopeIndices = new int[bdhc.getPlates().size()];
        List<Slope> slopes = getSlopes(bdhc, slopeIndices);
        int[] zIndices = new int[bdhc.getPlates().size()];
        List<Integer> zCoords = getCoordsZ(bdhc, zIndices);
        List<Stripe> stripes = calculateStripes(bdhc);

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

    private static void writeHeader(BinaryBufferWriter writer, Bdhc bdhc, List<Point> points, List<Slope> slopes, List<Integer> zCoords, List<Stripe> stripes) {
        writeString(writer, "BDHC");

        writeShortValue(writer, points.size());
        writeShortValue(writer, slopes.size());
        writeShortValue(writer, zCoords.size());
        writeShortValue(writer, bdhc.getPlates().size());
        writeShortValue(writer, stripes.size());
        writeShortValue(writer, getNumPlateIndices(stripes));
    }

    private static void writeIndices(BinaryBufferWriter writer, List<Stripe> stripes) {
        for (Stripe stripe : stripes) {
            for (int j = 0; j < stripe.plateIndices.size(); j++) {
                writeShortValue(writer, stripe.plateIndices.get(j));
            }
        }
    }

    private static void writeStripes(BinaryBufferWriter writer, List<Stripe> stripes) {
        int offset = 0;
        for (Stripe stripe : stripes) {
            writeShortValue(writer, 0);
            writeShortValue(writer, stripe.y);
            writeShortValue(writer, stripe.plateIndices.size());
            writeShortValue(writer, offset);
            offset += stripe.plateIndices.size();
        }
    }

    private static void writePlates(BinaryBufferWriter writer, int[][] pointIndices, int[] slopeIndices, int[] zIndices) {
        for (int i = 0; i < pointIndices.length; i++) {
            writeShortValue(writer, pointIndices[i][0]);
            writeShortValue(writer, pointIndices[i][1]);
            writeShortValue(writer, slopeIndices[i]);
            writeShortValue(writer, zIndices[i]);
        }
    }

    private static void writeCoordsZ(BinaryBufferWriter writer, List<Integer> coordsZ) {
        for (Integer integer : coordsZ) {
            writeIntValue(writer, integer);
        }
    }

    private static void writeSlopes(BinaryBufferWriter writer, List<Slope> slopes) {
        for (Slope slope : slopes) {
            writeIntValue(writer, slope.x);
            writeIntValue(writer, slope.y);
            writeIntValue(writer, slope.z);
        }
    }

    private static void writePoints(BinaryBufferWriter writer, List<Point> points) {
        for (Point point : points) {
            writeShortValue(writer, 0);
            writeShortValue(writer, point.x);
            writeShortValue(writer, 0);
            writeShortValue(writer, point.y);
        }
    }

    private static List<Stripe> calculateStripes(Bdhc bdhc) {
        List<PlateInfo> platesInfo = new ArrayList<>();

        //Get plate info of plates inside group
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);
            platesInfo.add(new PlateInfo(i, p.y + p.height));
        }

        //Sort by height
        Collections.sort(platesInfo);

        //Group by height and form stripes
        List<Stripe> stripes = new ArrayList<>();
        int previousY = -16;
        for (int i = 0; i < platesInfo.size(); i++) {
            int y = platesInfo.get(i).y;
            if (y != previousY) {
                int yMinBounds = previousY;

                Stripe stripe = new Stripe(y);
                for (PlateInfo plateInfo : platesInfo) {
                    Plate p = bdhc.getPlate(plateInfo.plateIndex);
                    if (yMinBounds < p.y + p.height && y > p.y) {
                        stripe.plateIndices.add(plateInfo.plateIndex);
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
        for (Stripe stripe : stripes) {
            stripe.sortPlateIndices(bdhc.getPlates());
        }

        return stripes;
    }

    private static List<Integer> getCoordsZ(Bdhc bdhc, int[] zIndices) {
        List<Integer> zCoords = new ArrayList<>();

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
                float x = xd > zero ? p.x + p.width : p.x;
                float n = z - mx * x;
                d = (float) -(n / Math.sqrt(mx * mx + 1.0f));
            } else if (Math.abs(yd) > zero) {
                float y = yd > zero ? p.y + p.height : p.y;
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

    private static List<Slope> getSlopes(Bdhc bdhc, int[] indices) {
        List<Slope> slopes = new ArrayList<>();

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

    private static List<Point> getPoints(Bdhc bdhc, int[][] coordIndices) {
        List<Point> coords = new ArrayList<>();
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

    public static int getNumStripes(List<List<Stripe>> stripes) {
        return stripes.stream().mapToInt(List::size).sum();
    }

    public static int getNumPlateIndices(List<Stripe> stripes) {
        return stripes.stream().mapToInt(stripe -> stripe.plateIndices.size()).sum();
    }

    private static void writeString(BinaryBufferWriter writer, String s) {
        writer.writeString(s);
    }

    private static void writeShortValue(BinaryBufferWriter writer, int value) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        writer.write(buffer.putShort((short) value).array());
    }

    private static void writeIntValue(BinaryBufferWriter writer, int value) {
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
