
package utils.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Trifindo
 */
public class Clusterer {

    public static BufferedImage clusterColors(BufferedImage img, int numColors, int maxIte, float tol) {
        //return applyPaletteToImage(img, clusterColors(getPalette(img), numColors, maxIte, tol));
        return floydSteinbergDithering(img, clusterColors(getPalette(img), numColors, maxIte, tol));
    }

    public static List<Color> getPalette(BufferedImage img) {
        Set<FastColor> set = new TreeSet<>();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                set.add(new FastColor(img.getRGB(i, j), true));
            }
        }
        List<Color> colors = new ArrayList<>(set);
        System.out.println("Number of colors original image: " + colors.size());
        return colors;
    }

    public static BufferedImage floydSteinbergDithering(BufferedImage oldImg, List<Color> palette) {
        BufferedImage img = new BufferedImage(oldImg.getWidth(), oldImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(oldImg, 0, 0, null);
        img.getGraphics().dispose();

        int w = img.getWidth();
        int h = img.getHeight();

        FastColor[][] d = new FastColor[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                d[y][x] = new FastColor(img.getRGB(x, y), true);
            }
        }

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                FastColor oldColor = d[y][x];
                Color newColor = palette.get(getCloserColorIndex(oldColor, palette));
                img.setRGB(x, y, newColor.getRGB());

                //FastColor err = oldColor.sub(newColor);
                int[] err = new int[4];
                err[0] = oldColor.getRed() - newColor.getRed();
                err[1] = oldColor.getGreen() - newColor.getGreen();
                err[2] = oldColor.getBlue() - newColor.getBlue();
                err[3] = oldColor.getAlpha() - newColor.getAlpha();

                if (x + 1 < w) {
                    applyQuantError(d, y, x + 1, 7, err);
                }
                if (x - 1 >= 0 && y + 1 < h) {
                    applyQuantError(d, y + 1, x - 1, 3, err);
                }
                if (y + 1 < h) {
                    applyQuantError(d, y + 1, x, 5, err);
                }
                if (x + 1 < w && y + 1 < h) {
                    applyQuantError(d, y + 1, x + 1, 1, err);
                }
            }
        }

        return img;
    }

    public static void applyQuantError(FastColor[][] d, int y, int x, float mul, int[] err) {
        int r = Math.max(0, Math.min(d[y][x].getRed() + (int) ((err[0] * mul) / 16), 255));
        int g = Math.max(0, Math.min(d[y][x].getGreen() + (int) ((err[1] * mul) / 16), 255));
        int b = Math.max(0, Math.min(d[y][x].getBlue() + (int) ((err[2] * mul) / 16), 255));
        int a = Math.max(0, Math.min(d[y][x].getAlpha() + (int) ((err[3] * mul) / 16), 255));
        d[y][x] = new FastColor(r, g, b, a);
    }

    public static List<Color> clusterColors(List<Color> colors, int numColors, int maxIte, float tol) {
        List<Color> centroids = new ArrayList<>(numColors);
        List<Color> lastCentroids = new ArrayList<>(numColors);
        for (int i = 0; i < numColors; i++) {
            centroids.add(colors.get(new Random().nextInt(colors.size())));
            lastCentroids.add(colors.get(new Random().nextInt(colors.size())));
        }

        List<List<Integer>> colorsIndicesInClusters = new ArrayList<>(centroids.size());
        for (int i = 0; i < centroids.size(); i++) {
            colorsIndicesInClusters.add(new ArrayList<>());
        }
        int ite = 0;
        float difference = 1f;
        while (ite < maxIte && difference > tol) {
            for (int i = 0; i < centroids.size(); i++) {
                colorsIndicesInClusters.set(i, new ArrayList<>());
            }

            for (int i = 0; i < colors.size(); i++) {
                int index = getCloserColorIndex(colors.get(i), centroids);
                colorsIndicesInClusters.get(index).add(i);
            }

            for (int i = 0; i < centroids.size(); i++) {
                if (colorsIndicesInClusters.get(i).size() > 0) {
                    centroids.set(i, getMeanColor(colors, colorsIndicesInClusters.get(i)));
                } else {
                    centroids.set(i, colors.get(new Random().nextInt(colors.size())));
                }
            }

            difference = 0;
            for (int i = 0; i < centroids.size(); i++) {
                difference += getDifference(centroids.get(i), lastCentroids.get(i));
            }
            difference /= (centroids.size() * 4 * 255);

            lastCentroids = copyColors(centroids);

            ite++;
            System.out.println(ite);
        }

        System.out.println("Number of centroids: " + centroids.size());
        for (Color centroid : centroids) {
            System.out.println(centroid.getRGB());
        }

        return centroids;
    }

    private static List<Color> copyColors(List<Color> colors) {
        return colors.stream()
                .map(color -> new Color(color.getRGB(), true))
                .collect(Collectors.toList());
    }

    private static int getDifference(Color c1, Color c2) {
        int rDiff = Math.abs(c1.getRed() - c2.getRed());
        int gDiff = Math.abs(c1.getGreen() - c2.getGreen());
        int bDiff = Math.abs(c1.getBlue() - c2.getBlue());
        int aDiff = Math.abs(c1.getAlpha() - c2.getAlpha());
        return rDiff + gDiff + bDiff + aDiff;
    }

    private static FastColor getMeanColor(List<Color> colors, List<Integer> indices) {
        int rSum = 0;
        int gSum = 0;
        int bSum = 0;
        int aSum = 0;

        for (Integer index : indices) {
            Color c = colors.get(index);
            rSum += c.getRed();
            gSum += c.getGreen();
            bSum += c.getBlue();
            aSum += c.getAlpha();
        }
        return new FastColor(rSum / indices.size(), gSum / indices.size(), bSum / indices.size(), aSum / indices.size());
    }

    private static int getCloserColorIndex(Color c, List<Color> colors) {
        int index = 0;
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < colors.size(); i++) {
            int dist = getDistanceToColor(c, colors.get(i));
            if (dist < minDist) {
                index = i;
                minDist = dist;
            }
        }
        return index;
    }

    private static int getDistanceToColor(Color c1, Color c2) {
        int rd = c1.getRed() - c2.getRed();
        int gd = c1.getGreen() - c2.getGreen();
        int bd = c1.getBlue() - c2.getBlue();
        int ad = c1.getAlpha() - c2.getAlpha();

        return rd * rd + gd * gd + bd * bd + ad * ad;
    }
}
