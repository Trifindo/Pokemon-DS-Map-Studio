package math.mat;

public class Mat4f {

    public float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;

    public Mat4f() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;

        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;

        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 0.0f;
    }

    public Mat4f(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public Mat4f(Mat4f other) {
        set(other);
    }

    public Mat4f(Mat3f other) {
        set(other);
    }

    public Mat4f(float[] data) {
        set(data);
    }

    @Override
    public Mat4f clone() {
        return new Mat4f(this);
    }

    @Override
    public String toString() {
        final String columnFormat = "|%-14s%-14s%-14s%-14s|\n";
        return String.format(columnFormat, m00, m01, m02, m03)
                + String.format(columnFormat, m10, m11, m12, m13)
                + String.format(columnFormat, m20, m21, m22, m23)
                + String.format(columnFormat, m30, m31, m32, m33);
        /*
        return "|" + m00 + ", " + m01 + ", " + m02 + ", " + m03 + "|\n"
                + "|" + m10 + ", " + m11 + ", " + m12 + ", " + m13 + "|\n"
                + "|" + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "|\n"
                + "|" + m30 + ", " + m31 + ", " + m32 + ", " + m33 + "|";*/
    }

    public void print() {
        System.out.println(toString());
    }

    public final void set(Mat4f other) {
        this.m00 = other.m00;
        this.m01 = other.m01;
        this.m02 = other.m02;
        this.m03 = other.m03;

        this.m10 = other.m10;
        this.m11 = other.m11;
        this.m12 = other.m12;
        this.m13 = other.m13;

        this.m20 = other.m20;
        this.m21 = other.m21;
        this.m22 = other.m22;
        this.m23 = other.m23;

        this.m30 = other.m30;
        this.m31 = other.m31;
        this.m32 = other.m32;
        this.m33 = other.m33;
    }

    public final void set(Mat3f other) {
        this.m00 = other.m00;
        this.m01 = other.m01;
        this.m02 = other.m02;
        this.m03 = 0.0f;

        this.m10 = other.m10;
        this.m11 = other.m11;
        this.m12 = other.m12;
        this.m13 = 0.0f;

        this.m20 = other.m20;
        this.m21 = other.m21;
        this.m22 = other.m22;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }

    public final void set(float[] data) {
        this.m00 = data[0];
        this.m10 = data[1];
        this.m20 = data[2];
        this.m30 = data[3];

        this.m01 = data[4];
        this.m11 = data[5];
        this.m21 = data[6];
        this.m31 = data[7];

        this.m02 = data[8];
        this.m12 = data[9];
        this.m22 = data[10];
        this.m32 = data[11];

        this.m03 = data[12];
        this.m13 = data[13];
        this.m23 = data[14];
        this.m33 = data[15];
    }

    public void set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;

        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;

        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;

        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public float[] toArray() {
        return new float[]{
                m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33
        };
    }

    public static Mat4f identity() {
        return new Mat4f(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public void setIdentity(){
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;

        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;

        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }

    public static void add(Mat4f src1, Mat4f src2, Mat4f dst) {
        dst.m00 = src1.m00 + src2.m00;
        dst.m01 = src1.m01 + src2.m01;
        dst.m02 = src1.m02 + src2.m02;
        dst.m03 = src1.m03 + src2.m03;

        dst.m10 = src1.m10 + src2.m10;
        dst.m11 = src1.m11 + src2.m11;
        dst.m12 = src1.m12 + src2.m12;
        dst.m13 = src1.m13 + src2.m13;

        dst.m20 = src1.m20 + src2.m20;
        dst.m21 = src1.m21 + src2.m21;
        dst.m22 = src1.m22 + src2.m22;
        dst.m23 = src1.m23 + src2.m23;

        dst.m30 = src1.m30 + src2.m30;
        dst.m31 = src1.m31 + src2.m31;
        dst.m32 = src1.m32 + src2.m32;
        dst.m33 = src1.m33 + src2.m33;
    }

    public static Mat4f add_(Mat4f src1, Mat4f src2) {
        Mat4f dst = new Mat4f();
        add(src1, src2, dst);
        return dst;
    }

    public Mat4f add(Mat4f other) {
        add(this, other, this);
        return this;
    }

    public Mat4f add_(Mat4f other) {
        return add_(this, other);
    }

    public static void sub(Mat4f src1, Mat4f src2, Mat4f dst) {
        dst.m00 = src1.m00 - src2.m00;
        dst.m01 = src1.m01 - src2.m01;
        dst.m02 = src1.m02 - src2.m02;
        dst.m03 = src1.m03 - src2.m03;

        dst.m10 = src1.m10 - src2.m10;
        dst.m11 = src1.m11 - src2.m11;
        dst.m12 = src1.m12 - src2.m12;
        dst.m13 = src1.m13 - src2.m13;

        dst.m20 = src1.m20 - src2.m20;
        dst.m21 = src1.m21 - src2.m21;
        dst.m22 = src1.m22 - src2.m22;
        dst.m23 = src1.m23 - src2.m23;

        dst.m30 = src1.m30 - src2.m30;
        dst.m31 = src1.m31 - src2.m31;
        dst.m32 = src1.m32 - src2.m32;
        dst.m33 = src1.m33 - src2.m33;
    }

    public static Mat4f sub_(Mat4f src1, Mat4f src2) {
        Mat4f dst = new Mat4f();
        sub(src1, src2, dst);
        return dst;
    }

    public Mat4f sub(Mat4f other) {
        sub(this, other, this);
        return this;
    }

    public Mat4f sub_(Mat4f other) {
        return sub_(this, other);
    }

    public static void mul(Mat4f src1, Mat4f src2, Mat4f dst) {
        dst.m00 = src1.m00 * src2.m00 + src1.m01 * src2.m10 + src1.m02 * src2.m20 + src1.m03 * src2.m30;
        dst.m01 = src1.m00 * src2.m01 + src1.m01 * src2.m11 + src1.m02 * src2.m21 + src1.m03 * src2.m31;
        dst.m02 = src1.m00 * src2.m02 + src1.m01 * src2.m12 + src1.m02 * src2.m22 + src1.m03 * src2.m32;
        dst.m03 = src1.m00 * src2.m03 + src1.m01 * src2.m13 + src1.m02 * src2.m23 + src1.m03 * src2.m33;

        dst.m10 = src1.m10 * src2.m00 + src1.m11 * src2.m10 + src1.m12 * src2.m20 + src1.m13 * src2.m30;
        dst.m11 = src1.m10 * src2.m01 + src1.m11 * src2.m11 + src1.m12 * src2.m21 + src1.m13 * src2.m31;
        dst.m12 = src1.m10 * src2.m02 + src1.m11 * src2.m12 + src1.m12 * src2.m22 + src1.m13 * src2.m32;
        dst.m13 = src1.m10 * src2.m03 + src1.m11 * src2.m13 + src1.m12 * src2.m23 + src1.m13 * src2.m33;

        dst.m20 = src1.m20 * src2.m00 + src1.m21 * src2.m10 + src1.m22 * src2.m20 + src1.m23 * src2.m30;
        dst.m21 = src1.m20 * src2.m01 + src1.m21 * src2.m11 + src1.m22 * src2.m21 + src1.m23 * src2.m31;
        dst.m22 = src1.m20 * src2.m02 + src1.m21 * src2.m12 + src1.m22 * src2.m22 + src1.m23 * src2.m32;
        dst.m23 = src1.m20 * src2.m03 + src1.m21 * src2.m13 + src1.m22 * src2.m23 + src1.m23 * src2.m33;

        dst.m30 = src1.m30 * src2.m00 + src1.m31 * src2.m10 + src1.m32 * src2.m20 + src1.m33 * src2.m30;
        dst.m31 = src1.m30 * src2.m01 + src1.m31 * src2.m11 + src1.m32 * src2.m21 + src1.m33 * src2.m31;
        dst.m32 = src1.m30 * src2.m02 + src1.m31 * src2.m12 + src1.m32 * src2.m22 + src1.m33 * src2.m32;
        dst.m33 = src1.m30 * src2.m03 + src1.m31 * src2.m13 + src1.m32 * src2.m23 + src1.m33 * src2.m33;
    }

    public static Mat4f mul_(Mat4f src1, Mat4f src2) {
        Mat4f dst = new Mat4f();
        mul(src1, src2, dst);
        return dst;
    }

    public Mat4f mul(Mat4f other) {
        mul(this.clone(), other, this);
        return this;
    }

    public Mat4f mul_(Mat4f other) {
        return mul_(this, other);
    }

    /*
    public void setRow(int index, Vec3f row) {
        float[] data = toArray();
        index *= 4;
        data[index] = row.x;
        data[index + 1] = row.y;
        data[index + 2] = row.z;
        set(data);
    }
    public void setCol(int index, Vec3f col) {
        float[] data = toArray();
        data[index] = col.x;
        data[index + 4] = col.y;
        data[index + 8] = col.z;
        set(data);
    }*/
    public static void inverse(Mat4f src, Mat4f dst) {
        float a = src.m00 * src.m11 - src.m01 * src.m10;
        float b = src.m00 * src.m12 - src.m02 * src.m10;
        float c = src.m00 * src.m13 - src.m03 * src.m10;
        float d = src.m01 * src.m12 - src.m02 * src.m11;
        float e = src.m01 * src.m13 - src.m03 * src.m11;
        float f = src.m02 * src.m13 - src.m03 * src.m12;
        float g = src.m20 * src.m31 - src.m21 * src.m30;
        float h = src.m20 * src.m32 - src.m22 * src.m30;
        float i = src.m20 * src.m33 - src.m23 * src.m30;
        float j = src.m21 * src.m32 - src.m22 * src.m31;
        float k = src.m21 * src.m33 - src.m23 * src.m31;
        float l = src.m22 * src.m33 - src.m23 * src.m32;

        float det = a * l - b * k + c * j + d * i - e * h + f * g;

        /*
        if(Math.abs(det) < 0.0001f){
            return false;
        }*/
        det = 1.0f / det;
        dst.set(
                (+src.m11 * l - src.m12 * k + src.m13 * j) * det,
                (-src.m01 * l + src.m02 * k - src.m03 * j) * det,
                (+src.m31 * f - src.m32 * e + src.m33 * d) * det,
                (-src.m21 * f + src.m22 * e - src.m23 * d) * det,
                (-src.m10 * l + src.m12 * i - src.m13 * h) * det,
                (+src.m00 * l - src.m02 * i + src.m03 * h) * det,
                (-src.m30 * f + src.m32 * c - src.m33 * b) * det,
                (+src.m20 * f - src.m22 * c + src.m23 * b) * det,
                (+src.m10 * k - src.m11 * i + src.m13 * g) * det,
                (-src.m00 * k + src.m01 * i - src.m03 * g) * det,
                (+src.m30 * e - src.m31 * c + src.m33 * a) * det,
                (-src.m20 * e + src.m21 * c - src.m23 * a) * det,
                (-src.m10 * j + src.m11 * h - src.m12 * g) * det,
                (+src.m00 * j - src.m01 * h + src.m02 * g) * det,
                (-src.m30 * d + src.m31 * b - src.m32 * a) * det,
                (+src.m20 * d - src.m21 * b + src.m22 * a) * det);
        //return true;
    }

    public static Mat4f inverse_(Mat4f src) {
        Mat4f dst = new Mat4f();
        inverse(src, dst);
        return dst;
    }

    public Mat4f inverse() {
        inverse(this.clone(), this);
        return this;
    }

    public Mat4f inverse_() {
        return inverse_(this);
    }

    public static void transp(Mat4f src, Mat4f dst) {
        dst.m00 = src.m00;
        dst.m01 = src.m10;
        dst.m02 = src.m20;
        dst.m03 = src.m30;

        dst.m10 = src.m01;
        dst.m11 = src.m11;
        dst.m12 = src.m21;
        dst.m13 = src.m31;

        dst.m20 = src.m02;
        dst.m21 = src.m12;
        dst.m22 = src.m22;
        dst.m23 = src.m32;

        dst.m30 = src.m03;
        dst.m31 = src.m13;
        dst.m32 = src.m23;
        dst.m33 = src.m33;
    }

    public static Mat4f transp_(Mat4f src) {
        Mat4f dst = new Mat4f();
        transp(src, dst);
        return dst;
    }

    public Mat4f transp() {
        transp(this.clone(), this);
        return this;
    }

    public Mat4f transp_() {
        return transp_(this);
    }

}
