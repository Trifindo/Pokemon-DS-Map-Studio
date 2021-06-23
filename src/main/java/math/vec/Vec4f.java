package math.vec;

import math.mat.Mat4f;

import java.nio.FloatBuffer;

public class Vec4f {

    public float x, y, z, w;

    /**
     * Constructs a Vec4f with all values set to 0.0f
     */
    public Vec4f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 1.0f;
    }

    /**
     * Constructs a Vec4f with the x, y, z, w values
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     * @param w The W coordinate
     */
    public Vec4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Constructs a Vec4f as a copy of the input Vec4f
     *
     * @param other the vector to copy
     */
    public Vec4f(Vec4f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    /**
     * Constructs a Vec4f using a float array containing the x, y, z, w coordinates
     *
     * @param data the array containing the x, y, z coordinates
     */
    public Vec4f(float[] data) {
        this.x = data[0];
        this.y = data[1];
        this.z = data[2];
        this.w = data[3];
    }

    /**
     * Constructs a Vec4f using the coordiantes of a Vec3f and the w coordinate
     *
     * @param other
     * @param w
     */
    public Vec4f(Vec3f other, float w){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = w;
    }

    /**
     * Constructs a Vec4f using the coordiantes of a Vec3f and w of 1.0f
     *
     * @param other
     */
    public Vec4f(Vec3f other){
        this(other, 1.0f);
    }

    @Override
    public Vec4f clone() {
        return new Vec4f(this);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Float.floatToIntBits(this.x);
        hash = 67 * hash + Float.floatToIntBits(this.y);
        hash = 67 * hash + Float.floatToIntBits(this.z);
        hash = 67 * hash + Float.floatToIntBits(this.w);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vec4f other = (Vec4f) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        if (Float.floatToIntBits(this.w) != Float.floatToIntBits(other.w)) {
            return false;
        }
        return true;
    }



    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + ", " + w + "]";
    }

    /**
     * Prints the coordinates of the Vec4f
     */
    public void print() {
        System.out.println(toString());
    }

    /**
     * Prints the name and the coordinates of the Vec4f
     *
     * @param name the name of the vector
     */
    public void print(String name) {
        System.out.println(name + ": " + toString());
    }

    /**
     * Copies the content of the vector
     *
     * @param other the vector to be copied
     * @return the copy
     */
    public Vec4f set(Vec4f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
        return this;
    }

    /**
     * Sets the X, Y, Z, W coordinates into the vector
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @param w the W coordinate
     * @return the current vector
     */
    public Vec4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Divides the xyz components of the vector by the w component and stores
     * the result in a Vec3f.
     *
     * @param src vector to be converted
     * @param dst output converted vector
     */
    public static void toVec3f(Vec4f src, Vec3f dst){
        dst.x = src.x / src.w;
        dst.y = src.y / src.w;
        dst.z = src.z / src.w;
    }

    /**
     * Divides the xyz components of the vector by the w component and stores
     * the result in a new Vec3f.
     *
     * @param src vector to be converted
     * @return converted vector
     */
    public static Vec3f toVec3f_(Vec4f src){
        Vec3f dst = new Vec3f();
        toVec3f(src, dst);
        return dst;
    }

    /**
     * Divides the xyz components of this vector by the w component and stores
     * the result in a new Vec3f.
     *
     * @return converted vector
     */
    public Vec3f toVec3f(){
        return toVec3f_(this);
    }

    /**
     * Creates a float array containing the X, Y, Z, W coordinates of the vector
     *
     * @return the new float array containing the X, Y, Z, W of the vector
     */
    public float[] toArray() {
        return new float[]{x, y, z, w};
    }

    /**
     * Writes the vector data into a <tt>FloatBuffer</tt> at the offset location
     *
     * @param buffer the buffer to write in
     * @param offset the position
     */
    public void writeInBuffer(FloatBuffer buffer, int offset) {
        buffer.put(offset, x);
        buffer.put(offset + 1, y);
        buffer.put(offset + 2, z);
        buffer.put(offset + 3, w);
    }

    /**
     * Calculates the norm of the vector
     *
     * @return the norm of the vector
     */
    public float norm() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    /**
     * Normalizes the vector.
     *
     * @param src input vector to be normalized
     * @param dst resulting destination vector
     */
    public static void normalize(Vec4f src, Vec4f dst) {
        float invNorm = 1.0f / src.norm();
        dst.x = src.x * invNorm;
        dst.y = src.y * invNorm;
        dst.z = src.z * invNorm;
        dst.w = src.w * invNorm;
    }

    /**
     * Normalizes the vector and modifies the input vector.
     *
     * @param src input vector to be normalized
     * @return same input vector normalized
     */
    public static Vec4f normalize(Vec4f src) {
        normalize(src, src);
        return src;
    }

    /**
     * Normalizes the vector.
     *
     * @param src input vector to be normalized
     * @return new normalized vector
     */
    public static Vec4f normalize_(Vec4f src) {
        Vec4f dst = new Vec4f();
        normalize(src, dst);
        return dst;
    }

    /**
     * Normalizes the vector.
     *
     * @return this normalized vector
     */
    public Vec4f normalize() {
        return normalize(this);
    }

    /**
     * Normalizes the vector.
     *
     * @return new normalized vector
     */
    public Vec4f normalize_() {
        return normalize_(this);
    }

    /**
     * Adds the coordinates of two vectors.
     *
     * @param src1 first vector to be added
     * @param src2 second vector to be added
     * @param dst destination vector for storing the result
     */
    public static void add(Vec4f src1, Vec4f src2, Vec4f dst) {
        dst.x = src1.x + src2.x;
        dst.y = src1.y + src2.y;
        dst.z = src1.z + src2.z;
        dst.w = src1.w + src2.w;
    }

    /**
     * Adds the coordinates of two vectors.
     *
     * @param src1 first vector to be added
     * @param src2 second vector to be added
     * @return new vector storing the result of the operation
     */
    public static Vec4f add_(Vec4f src1, Vec4f src2) {
        Vec4f dst = new Vec4f();
        add(src1, src2, dst);
        return dst;
    }

    /**
     * Adds the coordinates of this vector with another one and stores the
     * result in this vector.
     *
     * @param other vector to be added
     * @return this vector storing the operation result
     */
    public Vec4f add(Vec4f other) {
        add(this, other, this);
        return this;
    }

    /**
     * Adds the coordinates of this vector with another one and stores the
     * result in a new vector.
     *
     * @param other vector to be added
     * @return new vector storing the operation result
     */
    public Vec4f add_(Vec4f other) {
        return add_(this, other);
    }

    public static void add(Vec4f src, float x, float y, float z, float w, Vec4f dst) {
        dst.x = src.x + x;
        dst.y = src.y + y;
        dst.z = src.z + z;
        dst.w = src.w + w;
    }

    public static Vec4f add_(Vec4f src, float x, float y, float z, float w) {
        Vec4f dst = new Vec4f();
        add(src, x, y, z, w, dst);
        return dst;
    }

    public Vec4f add(float x, float y, float z, float w) {
        add(this, x, y, z, w, this);
        return this;
    }

    public Vec4f add_(float x, float y, float z, float w) {
        return add_(this, x, y, z, w);
    }

    /**
     * Subtracts the coordinates of this vector with another one and stores the
     * result in a new vector.
     *
     * @param src1 first vector to be subtracted
     * @param src2 second vector to subtract
     * @param dst destination vector for storing the result
     */
    public static void sub(Vec4f src1, Vec4f src2, Vec4f dst) {
        dst.x = src1.x - src2.x;
        dst.y = src1.y - src2.y;
        dst.z = src1.z - src2.z;
        dst.w = src1.w - src2.w;
    }

    /**
     * Subtracts the coordinates of two vectors.
     *
     * @param src1 first vector to be subtracted
     * @param src2 second vector to subtract
     * @return new vector storing the result of the operation
     */
    public static Vec4f sub_(Vec4f src1, Vec4f src2) {
        Vec4f dst = new Vec4f();
        sub(src1, src2, dst);
        return dst;
    }

    /**
     * Subctracts the coordinates of this vector with another one and stores the
     * result in this vector.
     *
     * @param other vector to subtract
     * @return this vector storing the operation result
     */
    public Vec4f sub(Vec4f other) {
        sub(this, other, this);
        return this;
    }

    /**
     * Subtracts the coordinates of this vector with another one and stores the
     * result in a new vector.
     *
     * @param other vector to subtract
     * @return new vector storing the operation result
     */
    public Vec4f sub_(Vec4f other) {
        return sub_(this, other);
    }

    /**
     * Performs the dot product operation of two vectors.
     *
     * @param src1 first vector
     * @param src2 second vector
     * @return dot product result
     */
    public static float dot(Vec4f src1, Vec4f src2) {
        return src1.x * src2.x + src1.y * src2.y + src1.z * src2.z + src1.w * src2.w;
    }

    /**
     * Performs the dot product operation of the current vector with another
     *
     * @param other vector to peform dot product with
     * @return dot product result
     */
    public float dot(Vec4f other) {
        return dot(this, other);
    }

    /**
     * Performs matrix-vector multiplication
     *
     * @param src1 input matrix
     * @param src2 input vector
     * @param dst output vector
     */
    public static void mul(Mat4f src1, Vec4f src2, Vec4f dst) {
        dst.x = src1.m00 * src2.x + src1.m01 * src2.y + src1.m02 * src2.z + src1.m03 * src2.w;
        dst.y = src1.m10 * src2.x + src1.m11 * src2.y + src1.m12 * src2.z + src1.m13 * src2.w;
        dst.z = src1.m20 * src2.x + src1.m21 * src2.y + src1.m22 * src2.z + src1.m23 * src2.w;
        dst.w = src1.m30 * src2.x + src1.m31 * src2.y + src1.m32 * src2.z + src1.m33 * src2.w;
    }

    /**
     * Performs matrix-vector multiplication
     *
     * @param src1 input matrix
     * @param src2 input vector
     * @return output vector storing the operation result
     */
    public static Vec4f mul_(Mat4f src1, Vec4f src2) {
        Vec4f dst = new Vec4f();
        mul(src1, src2, dst);
        return dst;
    }

    /**
     * Multiplies this vector with a matrix
     *
     * @param src input matrix
     * @return this output vector storing the operation result
     */
    public Vec4f mul(Mat4f src) {
        mul(src, this.clone(), this);
        return this;
    }

    /**
     * Multiplies this vector with a matrix
     *
     * @param src input matrix
     * @return new output vector storing the operation result
     */
    public Vec4f mul_(Mat4f src) {
        return mul_(src, this);
    }

    /**
     * Scales a vector by a scalar value
     *
     * @param src input vector
     * @param scale scalar value
     * @param dst output scaled vector
     */
    public static void scale(Vec4f src, float scale, Vec4f dst) {
        dst.x = src.x * scale;
        dst.y = src.y * scale;
        dst.z = src.z * scale;
        dst.w = src.w * scale;
    }

    /**
     * Scales a vector by a scalar value
     *
     * @param src input vector
     * @param scale scalar value
     * @return new scaled vector
     */
    public static Vec4f scale_(Vec4f src, float scale) {
        Vec4f dst = new Vec4f();
        scale(src, scale, dst);
        return dst;
    }

    /**
     * Scales this vector by a scalar value
     *
     * @param scale scalar value
     * @return this scaled vector
     */
    public Vec4f scale(float scale) {
        scale(this, scale, this);
        return this;
    }

    /**
     * Scales this vector by a scalar value
     *
     * @param scale scalar value
     * @return new scaled vector
     */
    public Vec4f scale_(float scale) {
        return scale_(this, scale);
    }

    /**
     * Negates a vector
     *
     * @param src input vector to be negated
     * @param dst output negated vector
     */
    public static void negate(Vec4f src, Vec4f dst) {
        dst.x = -src.x;
        dst.y = -src.y;
        dst.z = -src.z;
        dst.w = -src.w;
    }

    /**
     * Negates a vector
     *
     * @param src input vector to be negated
     * @return new negated vector
     */
    public static Vec4f negate_(Vec4f src) {
        Vec4f dst = new Vec4f();
        negate(src, dst);
        return dst;
    }

    /**
     * Negates this vector
     *
     * @return this negated vector
     */
    public Vec4f negate() {
        negate(this, this);
        return this;
    }

    /**
     * Negates this vector
     *
     * @return new negated vector
     */
    public Vec4f negate_() {
        return negate_(this);
    }

}