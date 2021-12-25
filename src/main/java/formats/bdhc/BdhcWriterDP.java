
package formats.bdhc;

import utils.BinaryBufferWriter;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Trifindo
 */
public class BdhcWriterDP {

    public static void writeBdhc(Bdhc bdhc, String path) throws IOException{
        byte[] byteData = bdhcToByteArray(bdhc);
        Files.write(new File(path).toPath(), byteData);
    }

    public static byte[] bdhcToByteArray(Bdhc bdhc) {
        BinaryBufferWriter writer = new BinaryBufferWriter();

        int[][] pointIndices = new int[bdhc.getPlates().size()][4];
        List<BdhcPoint> points = getPoints(bdhc, pointIndices);
        int[] slopeIndices = new int[bdhc.getPlates().size()];
        List<Slope> slopes = getSlopes(bdhc, slopeIndices);
        List<Integer> distances = getDistances(bdhc);
        List<IndexedTriangle> tris = getIndexedTris(bdhc, pointIndices, slopeIndices);
        List<List<Stripe>> stripes = new ArrayList<>();
        stripes.add(calculateStripeGroup(bdhc, new Rectangle(-16, -16, 16, 16)));
        stripes.add(calculateStripeGroup(bdhc, new Rectangle(0, -16, 16, 16)));
        stripes.add(calculateStripeGroup(bdhc, new Rectangle(-16, 0, 16, 16)));
        stripes.add(calculateStripeGroup(bdhc, new Rectangle(0, 0, 16, 16)));

        //FileOutputStream out = new FileOutputStream(path);

        writeHeader(writer, points, slopes, tris, stripes);
        writePoints(writer, points);
        writeSlopes(writer, slopes);
        writeTriangles(writer, tris, points, distances);
        writeStripeGroups(writer, stripes);
        writeStripes(writer, stripes);
        writeTriIndices(writer, stripes);

        //out.close();
        return writer.toByteArray();
    }

    private static void writeHeader(BinaryBufferWriter writer, List<BdhcPoint> points, List<Slope> slopes,
                                    List<IndexedTriangle> tris, List<List<Stripe>> stripes) {
        writeString(writer, "BDHC");
        writeShortValue(writer, 32);
        writeShortValue(writer, 0);

        writeShortValue(writer, Bdhc.sizePointDP);
        writeShortValue(writer, points.size());
        writeShortValue(writer, Bdhc.sizeSlopeDP);
        writeShortValue(writer, slopes.size());
        writeShortValue(writer, Bdhc.sizePlateDP);
        writeShortValue(writer, tris.size());

        writeShortValue(writer, 28);
        writeShortValue(writer, 2);
        writeShortValue(writer, 4);
        writeShortValue(writer, 10);

        writeShortValue(writer, getMaxNumStripes(stripes));
        writeShortValue(writer, getMaxNumTris(stripes));
        writeShortValue(writer, getNumStripes(stripes) * Bdhc.sizeStripeDP);
        writeShortValue(writer, 0);
        writeShortValue(writer, getNumTriIndices(stripes) * 2);
        writeShortValue(writer, 0);

        writeShortValue(writer, 2);
        writeShortValue(writer, 2);
        writeShortValue(writer, -4096);
        writeShortValue(writer, -17);
        writeShortValue(writer, -4096);
        writeShortValue(writer, -17);
        writeShortValue(writer, 4096);
        writeShortValue(writer, 16);
        writeShortValue(writer, 4096);
        writeShortValue(writer, 16);
        writeShortValue(writer, 4096);
        writeShortValue(writer, 16);
        writeShortValue(writer, 4096);
        writeShortValue(writer, 16);
    }

    private static void writeTriIndices(BinaryBufferWriter writer, List<List<Stripe>> stripes) {
        for (List<Stripe> stripeArrayList : stripes) {
            for (Stripe stripe : stripeArrayList) {
                for (int k = 0; k < stripe.plateIndices.size(); k++) {
                    writeShortValue(writer, stripe.plateIndices.get(k) * 2);
                    writeShortValue(writer, stripe.plateIndices.get(k) * 2 + 1);
                }
            }
        }
    }

    private static void writeStripes(BinaryBufferWriter writer, List<List<Stripe>> stripes) {
        int offset = 0;
        for (List<Stripe> stripeArrayList : stripes) {
            for (Stripe stripe : stripeArrayList) {
                writeShortValue(writer, stripe.plateIndices.size() * 2);
                writeShortValue(writer, 0);
                writeShortValue(writer, stripe.y);
                writeShortValue(writer, offset);
                writeShortValue(writer, 0);
                offset += stripe.plateIndices.size() * 2; //*2;
            }
        }
    }

    private static void writeStripeGroups(BinaryBufferWriter writer, List<List<Stripe>> stripes) {
        int offset = 0;
        for (List<Stripe> stripe : stripes) {
            writeShortValue(writer, stripe.size());
            writeShortValue(writer, offset);
            offset += stripe.size();
        }
    }

    private static void writeTriangles(BinaryBufferWriter writer, List<IndexedTriangle> tris, List<BdhcPoint> points, List<Integer> distances) {
        for (int i = 0; i < tris.size(); i++) {
            IndexedTriangle tri = tris.get(i);

            writeShortValue(writer, tri.pointInd1);
            writeShortValue(writer, tri.pointInd2);
            writeShortValue(writer, tri.pointInd3);
            writeShortValue(writer, tri.slopeInd);

            writeIntValue(writer, distances.get(i / 2));
            
            /*
            if (tri.type == Plate.PLANE || tri.type == Plate.BRIDGE) {
                if(tri.coordZ % 1 == 0){
                    writeZValue(out, -tri.coordZ);
                }else{
                    writeZValue(out, -tri.coordZ - 1);
                }
                
            } else {
                switch (tri.type) {
                    case Plate.LEFT_STAIRS:
                        writeSlopeZValue(out, 46341 * (-tri.getMaxX(points) - (int) tri.coordZ));
                        break;
                    case Plate.RIGHT_STAIRS:
                        writeSlopeZValue(out, 46341 * (tri.getMinX(points) - (int) tri.coordZ));
                        break;
                    case Plate.UP_STAIRS:
                        writeSlopeZValue(out, 46341 * (-tri.getMaxY(points) - (int) tri.coordZ));
                        break;
                    case Plate.DOWN_STAIRS:
                        writeSlopeZValue(out, 46341 * (tri.getMinY(points) - (int) tri.coordZ));
                        break;
                }
            }*/
        }
    }

    private static List<Integer> getDistances(Bdhc bdhc) {
        List<Integer> distances = new ArrayList<>();

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

            distances.add(zValue);
        }
        return distances;
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

    private static void writeSlopes(BinaryBufferWriter writer, List<Slope> slopes) {
        for (Slope slope : slopes) {
            writeIntValue(writer, slope.x);
            writeIntValue(writer, slope.y);
            writeIntValue(writer, slope.z);
        }
    }

    private static void writePoints(BinaryBufferWriter writer, List<BdhcPoint> points) {
        for (BdhcPoint point : points) {
            writeShortValue(writer, 0);
            writeShortValue(writer, point.x);
            writeZValue(writer, point.z);
            writeShortValue(writer, 0);
            writeShortValue(writer, point.y);
        }
    }

    private static List<Stripe> calculateStripeGroup(Bdhc bdhc, Rectangle groupBounds) {
        List<PlateInfo> platesInfo = new ArrayList<>();
        //Get plate info of plates inside group
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);
            if (overlap(groupBounds, p.getBounds())) {
                platesInfo.add(new PlateInfo(i, p.y + p.height));
            }
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

        //Sort stripes elements on X axis
        for (Stripe stripe : stripes) {
            stripe.sortPlateIndices(bdhc.getPlates());
        }

        return stripes;
    }

    private static List<IndexedTriangle> getIndexedTris(Bdhc bdhc, int[][] pointIndices, int[] slopeIndices) {
        List<IndexedTriangle> plates = new ArrayList<>();
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);

            plates.add(new IndexedTriangle(
                    pointIndices[i][0],
                    pointIndices[i][1],
                    pointIndices[i][2],
                    slopeIndices[i],
                    p.z,
                    p.type)); //TODO: Change this
            plates.add(new IndexedTriangle(
                    pointIndices[i][0],
                    pointIndices[i][2],
                    pointIndices[i][3],
                    slopeIndices[i],
                    p.z,
                    p.type)); //TODO: Change this
        }
        return plates;
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

    private static List<BdhcPoint> getPoints(Bdhc bdhc, int[][] coordIndices) {
        List<BdhcPoint> coords = new ArrayList<>();
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            Plate p = bdhc.getPlate(i);
            int[] zOffsets = new int[4];
            if (p.type == Plate.PLANE || p.type == Plate.BRIDGE) {
                zOffsets[0] = 0;
                zOffsets[1] = 0;
                zOffsets[2] = 0;
                zOffsets[3] = 0;
            } else { //TODO: Improve this using lookup table
                switch (p.type) {
                    case Plate.LEFT_STAIRS:
                        zOffsets[0] = p.width;
                        zOffsets[3] = p.width;
                        break;
                    case Plate.RIGHT_STAIRS:
                        zOffsets[1] = p.width;
                        zOffsets[2] = p.width;
                        break;
                    case Plate.UP_STAIRS:
                        zOffsets[2] = p.height;
                        zOffsets[3] = p.height;
                        break;
                    case Plate.DOWN_STAIRS:
                        zOffsets[0] = p.height;
                        zOffsets[1] = p.height;
                        break;
                }
            }
            addCoordinate(coords, new BdhcPoint(p.x, p.y + p.height, p.z + zOffsets[0]), i, coordIndices, 0);
            addCoordinate(coords, new BdhcPoint(p.x + p.width, p.y + p.height, p.z + zOffsets[1]), i, coordIndices, 1);
            addCoordinate(coords, new BdhcPoint(p.x + p.width, p.y, p.z + zOffsets[2]), i, coordIndices, 2);
            addCoordinate(coords, new BdhcPoint(p.x, p.y, p.z + zOffsets[3]), i, coordIndices, 3);
        }
        return coords;
    }

    private static void addCoordinate(List<BdhcPoint> coords, BdhcPoint coord, int pointIndex, int[][] indices, int indOffset) {
        /*
        coords.add(coord);
        indices[pointIndex][indOffset] = coords.size() - 1;
         */

        int index = coords.indexOf(coord);
        if (index == -1) {
            index = coords.size();
            coords.add(coord);
        }
        indices[pointIndex][indOffset] = index;
    }

    public static int getNumStripes(List<List<Stripe>> stripes) {
        return stripes.stream().mapToInt(List::size).sum();
    }

    public static int getMaxNumStripes(List<List<Stripe>> stripes) {
        return stripes.stream().mapToInt(List::size).filter(stripe -> stripe >= 0).max().orElse(0);
    }

    public static int getMaxNumTris(List<List<Stripe>> stripes) {
        int maxNumPlates = 0;
        for (List<Stripe> stripe : stripes) {
            for (Stripe value : stripe) {
                int numPlates = value.plateIndices.size();
                if (numPlates > maxNumPlates) {
                    maxNumPlates = numPlates;
                }
            }
        }
        return maxNumPlates * 2;
    }

    public static int getNumTriIndices(List<List<Stripe>> stripes) {
        int numIndices = stripes.stream().flatMap(Collection::stream).mapToInt(value -> value.plateIndices.size()).sum();
        return numIndices * 2;
    }

    public static boolean overlap(Rectangle r1, Rectangle r2) {
        return r2.x < r1.x + r1.width && r2.x + r2.width > r1.x && r2.y < r1.y + r1.height && r2.y + r2.height > r1.y;
    }

    private static void writeString(BinaryBufferWriter writer, String s) {
        writer.writeString(s);
    }

    private static void writeIntArray(BinaryBufferWriter writer, int[] data) {
        for (int i : data) {
            writeShortValue(writer, i);
        }
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

    private static void writeZValue(BinaryBufferWriter writer, float value) {
        short decimalPart = (short) value;
        short fractionalPart = (short) ((value - decimalPart) * (65536));
        writeShortValue(writer, fractionalPart);
        writeShortValue(writer, decimalPart);
    }

    private static void writeSlopeZValue(BinaryBufferWriter writer, int value) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        writer.write(buffer.putInt(value).array());
    }
}
