
package geometry;

import graphicslib3D.Matrix3D;

/**
 * @author Trifindo
 */
public class Generator {

    public static float[] generateCenteredGrid(int cols, int rows, float size, float z) {
        float[] grid = new float[(cols + rows + 2) * 2 * 3];
        for (int i = 0; i < rows + 1; i++) {
            grid[i * 6 + 0] = -(size * cols) / 2;
            grid[i * 6 + 1] = size * i - (size * rows) / 2;
            grid[i * 6 + 2] = z;

            grid[i * 6 + 3] = (size * cols) / 2;
            grid[i * 6 + 4] = size * i - (size * rows) / 2;
            grid[i * 6 + 5] = z;
        }

        for (int i = 0; i < cols + 1; i++) {
            grid[(i + rows + 1) * 6 + 0] = size * i - (size * cols) / 2;
            grid[(i + rows + 1) * 6 + 1] = -(size * rows) / 2;
            grid[(i + rows + 1) * 6 + 2] = z;

            grid[(i + rows + 1) * 6 + 3] = size * i - (size * cols) / 2;
            grid[(i + rows + 1) * 6 + 4] = (size * rows) / 2;
            grid[(i + rows + 1) * 6 + 5] = z;
        }

        return grid;
    }


    public static float[] generateGridColors(int cols, int rows,
                                             float[] rgbaTop, float[] rgbaBot, float[] rgbaLeft, float[] rgbaRight) {
        final int colorsPerVertex = 4;
        final int vertexPerLine = 2;
        final int numVertices = (cols + rows + 2) * colorsPerVertex * vertexPerLine;
        float[] colors = new float[numVertices];
        int c = 0;
        for (int i = 0; i < rows + 1; i++) {
            for (int j = 0; j < rgbaLeft.length; j++, c++) {
                colors[c] = rgbaLeft[j];
            }
            for (int j = 0; j < rgbaRight.length; j++, c++) {
                colors[c] = rgbaRight[j];
            }
        }

        for (int i = 0; i < cols + 1; i++) {
            for (int j = 0; j < rgbaTop.length; j++, c++) {
                colors[c] = rgbaTop[j];
            }
            for (int j = 0; j < rgbaBot.length; j++, c++) {
                colors[c] = rgbaTop[j];
            }
        }
        return colors;
    }

    public static float[] generateGrid(int cols, int rows, float size, float z) {
        float[] grid = new float[(cols + rows + 2) * 2 * 3];
        for (int i = 0; i < rows + 1; i++) {
            grid[i * 6 + 0] = 0;
            grid[i * 6 + 1] = size * i;
            grid[i * 6 + 2] = z;

            grid[i * 6 + 3] = (size * cols);
            grid[i * 6 + 4] = size * i;
            grid[i * 6 + 5] = z;
        }

        for (int i = 0; i < cols + 1; i++) {
            grid[(i + rows + 1) * 6 + 0] = size * i;
            grid[(i + rows + 1) * 6 + 1] = 0;
            grid[(i + rows + 1) * 6 + 2] = z;

            grid[(i + rows + 1) * 6 + 3] = size * i;
            grid[(i + rows + 1) * 6 + 4] = (size * rows);
            grid[(i + rows + 1) * 6 + 5] = z;
        }

        return grid;
    }

    public static float[] generateAxis(float size) {
        float[] axis = new float[3 * 3 * 2];

        set3f(axis, 0, 0.0f, 0.0f, 0.0f);
        set3f(axis, 3, size, 0.0f, 0.0f);
        set3f(axis, 6, 0.0f, 0.0f, 0.0f);
        set3f(axis, 9, 0.0f, size, 0.0f);
        set3f(axis, 12, 0.0f, 0.0f, 0.0f);
        set3f(axis, 15, 0.0f, 0.0f, size);

        return axis;
    }

    public static Matrix3D perspective(float fovy, float aspect, float n, float f) {
        float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);

        Matrix3D r = new Matrix3D();
        r.setElementAt(0, 0, A);
        r.setElementAt(1, 1, q);
        r.setElementAt(2, 2, B);
        r.setElementAt(3, 2, -1.0f);
        r.setElementAt(2, 3, C);
        r.setElementAt(3, 3, 0.0f);

        //Matrix3D r = new Matrix3D();


        return r;
    }

    public static Matrix3D ortographic(float l, float r, float b, float t, float n, float f) {
        Matrix3D m = new Matrix3D();
        float A = 2 / (r - l);
        float B = 2 / (t - b);
        float C = -2 / (f - n);

        m.setElementAt(0, 0, 2 / (r - l));
        m.setElementAt(1, 1, 2 / (t - b));
        m.setElementAt(2, 2, -2 / (f - n));
        m.setElementAt(0, 3, -(r + l) / (r - l));
        m.setElementAt(1, 3, -(t + b) / (t - b));
        m.setElementAt(2, 3, -(f + n) / (f - n));
        m.setElementAt(3, 3, 1.0);

        return m;
    }

    private static void set3f(float[] array, int index, float e1, float e2, float e3) {
        array[index] = e1;
        array[index + 1] = e2;
        array[index + 2] = e3;
    }

}
