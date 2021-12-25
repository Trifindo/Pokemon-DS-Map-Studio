package math.vec;

import math.mat.Mat3f;

import java.nio.FloatBuffer;

public class Vec3f {

    public float x, y, z;

    /**
     * Constructs a Vec3f with all values set to 0.0f
     */
    public Vec3f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    /**
     * Constructs a Vec3f with the x, y, z values
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     */
    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a Vec3f as a copy of the input Vec3f
     *
     * @param other the vector to copy
     */
    public Vec3f(Vec3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
     * Constructs a Vec3f using a float array containing the x, y, z coordinates
     *
     * @param data the array containing the x, y, z coordinates
     */
    public Vec3f(float[] data) {
        this.x = data[0];
        this.y = data[1];
        this.z = data[2];
    }

    /**
     * Constructs a Vec3f using a float array containing the x, y, z coordinates
     * at a certain offset
     *
     * @param data the array containing the x, y, z coordinates
     * @param offset position of the x coordinates in the array
     */
    public Vec3f(float[] data, int offset) {
        this.x = data[offset];
        this.y = data[offset + 1];
        this.z = data[offset + 2];
    }

    /**
     * Constructs a Vec3f using a float buffer containing the x, y, z
     * coordinates at a certain offset
     *
     * @param data the array containing the x, y, z coordinates
     * @param offset position of the x coordinates in the array
     */
    public Vec3f(FloatBuffer data, int offset) {
        this.x = data.get(offset);
        this.y = data.get(offset + 1);
        this.z = data.get(offset + 2);
    }

    @Override
    public Vec3f clone() {
        return new Vec3f(this);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Float.floatToIntBits(this.x);
        hash = 41 * hash + Float.floatToIntBits(this.y);
        hash = 41 * hash + Float.floatToIntBits(this.z);
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
        final Vec3f other = (Vec3f) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    /**
     * Prints the coordinates of the Vec3f
     */
    public void print() {
        System.out.println(this);
    }

    /**
     * Prints the name and the coordinates of the Vec3f
     *
     * @param name the name of the vector
     */
    public void print(String name) {
        System.out.println(name + ": " + this);
    }

    /**
     * Copies the content of the vector
     *
     * @param other the vector to be copied
     * @return the copy
     */
    public Vec3f set(Vec3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    /**
     * Sets the X, Y, Z coordinates into the vector
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return the current vector
     */
    public Vec3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Creates a float array containing the X, Y, Z coordinates of the vector
     *
     * @return the new float array containing the X, Y,Z of the vector
     */
    public float[] toArray() {
        return new float[]{x, y, z};
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
    }

    /**
     * Writes the vector data into a <tt>FloatBuffer</tt>
     *
     * @param buffer the buffer to write in
     */
    public void writeInBuffer(FloatBuffer buffer) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
    }

    /**
     * Writes the vector data into a array at the offset location
     *
     * @param array array to write in
     * @param offset the position
     * @param src the vector to write
     */
    public static void writeInArray(float[] array, int offset, Vec3f src) {
        array[offset] = src.x;
        array[offset + 1] = src.y;
        array[offset + 2] = src.z;
    }

    /**
     * Writes the vector data into a array at the offset location
     *
     * @param array array to write in
     * @param offset the position
     */
    public void writeInArray(float[] array, int offset) {
        writeInArray(array, offset, this);
    }

    /**
     * Writes a vector array data into a float array at the offset location
     *
     * @param array array to write in
     * @param offset the position
     * @param vtxs vector array
     */
    public static void writeInArray(float[] array, int offset, Vec3f... vtxs) {
        for (Vec3f vtx : vtxs) {
            writeInArray(array, offset, vtx);
            offset += 3;
        }
    }

    /**
     * Writes a vector array data into a float array
     *
     * @param array array to write in
     * @param vtxs vector array
     */
    public static void writeInArray(float[] array, Vec3f... vtxs) {
        writeInArray(array, 0, vtxs);
    }

    /**
     * Creates a new float array containing all the vertex coordinates
     *
     * @param vtxs vector array to write
     * @return new array with all the vector data
     */
    public static float[] toArray(Vec3f... vtxs) {
        float[] array = new float[vtxs.length * 3];
        writeInArray(array, vtxs);
        return array;
    }

    /**
     * Calculates the norm of the vector
     *
     * @return the norm of the vector
     */
    public float norm() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Normalizes the vector.
     *
     * @param src input vector to be normalized
     * @param dst resulting destination vector
     */
    public static void normalize(Vec3f src, Vec3f dst) {
        float invNorm = 1.0f / src.norm();
        dst.x = src.x * invNorm;
        dst.y = src.y * invNorm;
        dst.z = src.z * invNorm;
    }

    /**
     * Normalizes the vector and modifies the input vector.
     *
     * @param src input vector to be normalized
     * @return same input vector normalized
     */
    public static Vec3f normalize(Vec3f src) {
        normalize(src, src);
        return src;
    }

    /**
     * Normalizes the vector.
     *
     * @param src input vector to be normalized
     * @return new normalized vector
     */
    public static Vec3f normalize_(Vec3f src) {
        Vec3f dst = new Vec3f();
        normalize(src, dst);
        return dst;
    }

    /**
     * Normalizes the vector.
     *
     * @return this normalized vector
     */
    public Vec3f normalize() {
        return normalize(this);
    }

    /**
     * Normalizes the vector.
     *
     * @return new normalized vector
     */
    public Vec3f normalize_() {
        return normalize_(this);
    }

    /**
     * Generates a random vector.
     *
     * @param dst destination random vector
     */
    public static void rand(Vec3f dst) {
        dst.x = (float) Math.random() * 2.0f - 1.0f;
        dst.y = (float) Math.random() * 2.0f - 1.0f;
        dst.z = (float) Math.random() * 2.0f - 1.0f;
    }

    /**
     * Generates a random vector.
     *
     * @return new random vector
     */
    public static Vec3f rand_() {
        Vec3f dst = new Vec3f();
        rand(dst);
        return dst;
    }

    /**
     * Converts this vector into a random vector
     *
     * @return this random vector
     */
    public Vec3f rand() {
        rand(this);
        return this;
    }

    /**
     * Adds the coordinates of two vectors.
     *
     * @param src1 first vector to be added
     * @param src2 second vector to be added
     * @param dst destination vector for storing the result
     */
    public static void add(Vec3f src1, Vec3f src2, Vec3f dst) {
        dst.x = src1.x + src2.x;
        dst.y = src1.y + src2.y;
        dst.z = src1.z + src2.z;
    }

    /**
     * Adds the coordinates of two vectors.
     *
     * @param src1 first vector to be added
     * @param src2 second vector to be added
     * @return new vector storing the result of the operation
     */
    public static Vec3f add_(Vec3f src1, Vec3f src2) {
        Vec3f dst = new Vec3f();
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
    public Vec3f add(Vec3f other) {
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
    public Vec3f add_(Vec3f other) {
        return add_(this, other);
    }

    public static void add(Vec3f src, float x, float y, float z, Vec3f dst) {
        dst.x = src.x + x;
        dst.y = src.y + y;
        dst.z = src.z + z;
    }

    public static Vec3f add_(Vec3f src, float x, float y, float z) {
        Vec3f dst = new Vec3f();
        add(src, x, y, z, dst);
        return dst;
    }

    public Vec3f add(float x, float y, float z) {
        add(this, x, y, z, this);
        return this;
    }

    public Vec3f add_(float x, float y, float z) {
        return add_(this, x, y, z);
    }

    /**
     * Subtracts the coordinates of this vector with another one and stores the
     * result in a new vector.
     *
     * @param src1 first vector to be subtracted
     * @param src2 second vector to subtract
     * @param dst destination vector for storing the result
     */
    public static void sub(Vec3f src1, Vec3f src2, Vec3f dst) {
        dst.x = src1.x - src2.x;
        dst.y = src1.y - src2.y;
        dst.z = src1.z - src2.z;
    }

    /**
     * Subtracts the coordinates of two vectors.
     *
     * @param src1 first vector to be subtracted
     * @param src2 second vector to subtract
     * @return new vector storing the result of the operation
     */
    public static Vec3f sub_(Vec3f src1, Vec3f src2) {
        Vec3f dst = new Vec3f();
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
    public Vec3f sub(Vec3f other) {
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
    public Vec3f sub_(Vec3f other) {
        return sub_(this, other);
    }

    /**
     * Performs the dot product operation of two vectors.
     *
     * @param src1 first vector
     * @param src2 second vector
     * @return dot product result
     */
    public static float dot(Vec3f src1, Vec3f src2) {
        return src1.x * src2.x + src1.y * src2.y + src1.z * src2.z;
    }

    /**
     * Performs the dot product operation of the current vector with another
     *
     * @param other vector to peform dot product with
     * @return dot product result
     */
    public float dot(Vec3f other) {
        return dot(this, other);
    }

    /**
     * Performs the cross product operation.
     *
     * @param src1 first input vector
     * @param src2 second input vector
     * @param dst output vector to store the operation result
     */
    public static void cross(Vec3f src1, Vec3f src2, Vec3f dst) {
        dst.x = src1.y * src2.z - src1.z * src2.y;
        dst.y = src1.z * src2.x - src1.x * src2.z;
        dst.z = src1.x * src2.y - src1.y * src2.x;
    }

    /**
     * Performs the cross product operation.
     *
     * @param src1 first input vector
     * @param src2 second input vector
     * @return output vector to store the operation result
     */
    public static Vec3f cross_(Vec3f src1, Vec3f src2) {
        Vec3f dst = new Vec3f();
        cross(src1, src2, dst);
        return dst;
    }

    /**
     * Performs the cross product operation and stores result in this vector.
     *
     * @param other second vector to perform cross product
     * @return this vector storing the cross product result
     */
    public Vec3f cross(Vec3f other) {
        cross(this.clone(), other, this);
        return this;
    }

    /**
     * Performs the cross product operation storing the result in a new vector
     *
     * @param other second vector to perform cross product
     * @return new vector storing the cross product result
     */
    public Vec3f cross_(Vec3f other) {
        return cross_(this, other);
    }

    /**
     * Performs matrix-vector multiplication
     *
     * @param src1 input matrix
     * @param src2 input vector
     * @param dst output vector
     */
    public static void mul(Mat3f src1, Vec3f src2, Vec3f dst) {
        dst.x = src1.m00 * src2.x + src1.m01 * src2.y + src1.m02 * src2.z;
        dst.y = src1.m10 * src2.x + src1.m11 * src2.y + src1.m12 * src2.z;
        dst.z = src1.m20 * src2.x + src1.m21 * src2.y + src1.m22 * src2.z;
    }

    /**
     * Performs matrix-vector multiplication
     *
     * @param src1 input matrix
     * @param src2 input vector
     * @return output vector storing the operation result
     */
    public static Vec3f mul_(Mat3f src1, Vec3f src2) {
        Vec3f dst = new Vec3f();
        mul(src1, src2, dst);
        return dst;
    }

    /**
     * Multiplies this vector with a matrix
     *
     * @param src input matrix
     * @return this output vector storing the operation result
     */
    public Vec3f mul(Mat3f src) {
        mul(src, this.clone(), this);
        return this;
    }

    /**
     * Multiplies this vector with a matrix
     *
     * @param src input matrix
     * @return new output vector storing the operation result
     */
    public Vec3f mul_(Mat3f src) {
        return mul_(src, this);
    }

    /**
     * Performs element-wise (Hadamard product) multiplication of two vectors
     *
     * @param src1 vector to be multiplied
     * @param src2 vector to be multiplied
     * @param dst output vector containing the result
     */
    public static void had(Vec3f src1, Vec3f src2, Vec3f dst) {
        dst.x = src1.x * src2.x;
        dst.y = src1.y * src2.y;
        dst.z = src1.z * src2.z;
    }

    /**
     * Performs element-wise multiplication (Hadamard product) of two vectors
     *
     * @param src1 vector to be multiplied
     * @param src2 vector to be multiplied
     * @return new vector containing the result
     */
    public static Vec3f had_(Vec3f src1, Vec3f src2) {
        Vec3f dst = new Vec3f();
        had(src1, src2, dst);
        return dst;
    }

    /**
     * Performs element-wise multiplication (Hadamard product) of this vector
     * with another and stores the result in this vector
     *
     * @param other vector to be multiplied
     * @return this vector containing the result
     */
    public Vec3f had(Vec3f other) {
        had(this, other, this);
        return this;
    }

    /**
     * Performs element-wise multiplication (Hadamard product) of this vector
     * with another and stores the result in a new vector
     *
     * @param other vector to be multiplied
     * @return new vector containing the result
     */
    public Vec3f had_(Vec3f other) {
        return had_(this, other);
    }

    /**
     * Scales a vector by a scalar value
     *
     * @param src input vector
     * @param scale scalar value
     * @param dst output scaled vector
     */
    public static void scale(Vec3f src, float scale, Vec3f dst) {
        dst.x = src.x * scale;
        dst.y = src.y * scale;
        dst.z = src.z * scale;
    }

    /**
     * Scales a vector by a scalar value
     *
     * @param src input vector
     * @param scale scalar value
     * @return new scaled vector
     */
    public static Vec3f scale_(Vec3f src, float scale) {
        Vec3f dst = new Vec3f();
        scale(src, scale, dst);
        return dst;
    }

    /**
     * Scales this vector by a scalar value
     *
     * @param scale scalar value
     * @return this scaled vector
     */
    public Vec3f scale(float scale) {
        scale(this, scale, this);
        return this;
    }

    /**
     * Scales this vector by a scalar value.
     *
     * @param scale scalar value
     * @return new scaled vector
     */
    public Vec3f scale_(float scale) {
        return scale_(this, scale);
    }

    /**
     * Negates a vector.
     *
     * @param src input vector to be negated
     * @param dst output negated vector
     */
    public static void negate(Vec3f src, Vec3f dst) {
        dst.x = -src.x;
        dst.y = -src.y;
        dst.z = -src.z;
    }

    /**
     * Negates a vector.
     *
     * @param src input vector to be negated
     * @return new negated vector
     */
    public static Vec3f negate_(Vec3f src) {
        Vec3f dst = new Vec3f();
        negate(src, dst);
        return dst;
    }

    /**
     * Negates this vector.
     *
     * @return this negated vector
     */
    public Vec3f negate() {
        negate(this, this);
        return this;
    }

    /**
     * Negates this vector.
     *
     * @return new negated vector
     */
    public Vec3f negate_() {
        return negate_(this);
    }

    /**
     * Inverts a vector.
     *
     * @param src input vector to be inverted
     * @param dst inverted vector
     */
    public static void invert(Vec3f src, Vec3f dst) {
        dst.x = 1.0f / src.x;
        dst.y = 1.0f / src.y;
        dst.z = 1.0f / src.z;
    }

    /**
     * Inverts a vector.
     *
     * @param src input vector to be inverted
     * @return new inverted vector
     */
    public static Vec3f invert_(Vec3f src) {
        Vec3f dst = new Vec3f();
        invert(src, dst);
        return dst;
    }

    /**
     * Inverts this vector.
     *
     * @return this vector inverted
     */
    public Vec3f invert() {
        invert(this, this);
        return this;
    }

    /**
     * Inverts this vector.
     *
     * @return new vector inverted
     */
    public Vec3f invert_() {
        return invert_(this);
    }

    /**
     * Converts a vector of angles from radians to degrees
     *
     * @param src input vector of angles in radians
     * @param dst output vector of angles in degrees
     */
    public static void toDegrees(Vec3f src, Vec3f dst) {
        dst.x = (float) Math.toDegrees(src.x);
        dst.y = (float) Math.toDegrees(src.y);
        dst.z = (float) Math.toDegrees(src.z);
    }

    /**
     * Converts a vector of angles from radians to degrees
     *
     * @param src input vector of angles in radians
     * @return vector of angles in degrees
     */
    public static Vec3f toDegrees_(Vec3f src) {
        Vec3f dst = new Vec3f();
        toDegrees(src, dst);
        return dst;
    }

    /**
     * Converts this vector of angles from radians to degrees
     *
     * @return this vector of angles in degrees
     */
    public Vec3f toDegrees() {
        toDegrees(this, this);
        return this;
    }

    /**
     * Converts this vector of angles from radians to degrees in a new vector
     *
     * @return new vector of angles in degrees
     */
    public Vec3f toDegrees_() {
        return toDegrees_(this);
    }

    public static void toRadians(Vec3f src, Vec3f dst) {
        dst.x = (float) Math.toRadians(src.x);
        dst.y = (float) Math.toRadians(src.y);
        dst.z = (float) Math.toRadians(src.z);
    }

    public static Vec3f toRadians_(Vec3f src) {
        Vec3f dst = new Vec3f();
        toRadians(src, dst);
        return dst;
    }

    public Vec3f toRadians() {
        toRadians(this, this);
        return this;
    }

    public Vec3f toRadians_() {
        return toRadians_(this);
    }

    public static void rotate(Vec3f src, Vec3f axis, float radians, Vec3f dst) {
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        dst.x = axis.x * (axis.x * src.x + axis.y * src.y + axis.z * src.z) * (1.0f - cos)
                + src.x * cos
                + (-axis.z * src.y + axis.y * src.z) * sin;
        dst.y = axis.y * (axis.x * src.x + axis.y * src.y + axis.z * src.z) * (1.0f - cos)
                + src.y * cos
                + (axis.z * src.x - axis.x * src.z) * sin;
        dst.z = axis.z * (axis.x * src.x + axis.y * src.y + axis.z * src.z) * (1.0f - cos)
                + src.z * cos
                + (-axis.y * src.x + axis.x * src.y) * sin;
    }

    public static Vec3f rotate_(Vec3f src, Vec3f axis, float radians) {
        Vec3f dst = new Vec3f();
        rotate(src, axis, radians, dst);
        return dst;
    }

    public Vec3f rotate(Vec3f axis, float radians) {
        rotate(this.clone(), axis, radians, this);
        return this;
    }

    public Vec3f rotate_(Vec3f axis, float radians) {
        return rotate_(this, axis, radians);
    }

    public static void rotateAround(Vec3f src, Vec3f center, Vec3f axis, float radians, Vec3f dst) {
        Vec3f srcMoved = src.sub_(center);
        rotate(srcMoved, axis, radians, dst);
        dst.add(center);
    }

    public static Vec3f rotateAround_(Vec3f src, Vec3f center, Vec3f axis, float radians) {
        Vec3f dst = new Vec3f();
        rotateAround(src, center, axis, radians, dst);
        return dst;
    }

    public Vec3f rotateAround(Vec3f center, Vec3f axis, float radians) {
        rotateAround(this.clone(), center, axis, radians, this);
        return this;
    }

    public Vec3f rotateAround_(Vec3f center, Vec3f axis, float radians) {
        return rotateAround_(this, center, axis, radians);
    }

    public static void anglesXZ(Vec3f src, Vec3f dst) {
        dst.x = (float) (Math.atan2(src.z, Math.sqrt(src.x * src.x + src.y * src.y)));
        dst.y = 0.0f;
        dst.z = (float) (Math.atan2(src.y, src.x));
    }

    public static Vec3f anglesXZ_(Vec3f src) {
        Vec3f dst = new Vec3f();
        anglesXZ(src, dst);
        return dst;
    }

    public Vec3f anglesXZ() {
        anglesXZ(this.clone(), this);
        return this;
    }

    public Vec3f anglesXZ_() {
        return anglesXZ_(this);
    }

    public static void anglesXZToVector(Vec3f src, Vec3f dst) {
        dst.x = (float) (Math.cos(src.x) * Math.cos(src.z));
        dst.y = (float) (Math.cos(src.x) * Math.sin(src.z));
        dst.z = (float) Math.sin(src.x);
    }

    public static Vec3f anglesXZToVector_(Vec3f src) {
        Vec3f dst = new Vec3f();
        anglesXZToVector(src, dst);
        return dst;
    }

    public Vec3f anglesXZToVector() {
        anglesXZToVector(this.clone(), this);
        return this;
    }

    public Vec3f anglesXZToVector_() {
        return anglesXZToVector_(this);
    }

    public static void rotateDeg(Vec3f src, Vec3f axis, float degrees, Vec3f dst) {
        rotate(src, axis, (float) Math.toRadians(degrees), dst);
    }

    public static Vec3f rotateDeg_(Vec3f src, Vec3f axis, float degrees) {
        Vec3f dst = new Vec3f();
        rotateDeg(src, axis, degrees, dst);
        return dst;
    }

    public Vec3f rotateDeg(Vec3f axis, float degrees) {
        rotateDeg(this.clone(), axis, degrees, this);
        return this;
    }

    public Vec3f rotateDeg_(Vec3f axis, float degrees) {
        return rotateDeg_(this, axis, degrees);
    }

    public static void rotateAroundDeg(Vec3f src, Vec3f center, Vec3f axis, float degrees, Vec3f dst) {
        Vec3f srcMoved = src.sub_(center);
        rotateDeg(srcMoved, axis, degrees, dst);
        dst.add(center);
    }

    public static Vec3f rotateAroundDeg_(Vec3f src, Vec3f center, Vec3f axis, float degrees) {
        Vec3f dst = new Vec3f();
        rotateAroundDeg(src, center, axis, degrees, dst);
        return dst;
    }

    public Vec3f rotateAroundDeg(Vec3f center, Vec3f axis, float degrees) {
        rotateAroundDeg(this.clone(), center, axis, degrees, this);
        return this;
    }

    public Vec3f rotateAroundDeg_(Vec3f center, Vec3f axis, float degrees) {
        return rotateAroundDeg_(this, center, axis, degrees);
    }

    public static void anglesXZDeg(Vec3f src, Vec3f dst) {
        anglesXZ(src, dst);
        dst.toDegrees();
    }

    public static Vec3f anglesXZDeg_(Vec3f src) {
        Vec3f dst = new Vec3f();
        anglesXZDeg(src, dst);
        return dst;
    }

    public Vec3f anglesXZDeg() {
        anglesXZDeg(this.clone(), this);
        return this;
    }

    public Vec3f anglesXZDeg_() {
        return anglesXZDeg_(this);
    }

    public static void anglesXZDegToVector(Vec3f src, Vec3f dst) {
        anglesXZToVector(src.toRadians_(), dst);
    }

    public static Vec3f anglesXZDegToVector_(Vec3f src) {
        Vec3f dst = new Vec3f();
        anglesXZDegToVector(src, dst);
        return dst;
    }

    public Vec3f anglesXZDegToVector() {
        anglesXZDegToVector(this.clone(), this);
        return this;
    }

    public Vec3f anglesXZDegToVector_() {
        return anglesXZDegToVector_(this);
    }

    public static float dist(Vec3f src1, Vec3f src2) {
        return src1.sub_(src2).norm();
    }

    public float dist(Vec3f other) {
        return dist(this, other);
    }

    /**
     * Projects one vector onto another
     *
     * @param src vector to be proyected
     * @param dir direction of the proyection
     * @param dst output proyected vector
     */
    public static void proj(Vec3f src, Vec3f dir, Vec3f dst) {
        dst.set(dir).normalize();
        dst.scale(src.dot(dst));
    }

    /**
     * Projects one vector onto another
     *
     * @param src vector to be proyected
     * @param dir direction of the proyection
     * @return new proyected vector
     */
    public static Vec3f proj_(Vec3f src, Vec3f dir) {
        Vec3f dst = new Vec3f();
        proj(src, dir, dst);
        return dst;
    }

    /**
     * Projects this vector onto another
     *
     * @param dir direction of the proyection
     * @return this proyected vector
     */
    public Vec3f proj(Vec3f dir) {
        proj(this.clone(), dir, this);
        return this;
    }

    /**
     * Projects this vector onto another
     *
     * @param dir direction of the proyection
     * @return new proyected vector
     */
    public Vec3f proj_(Vec3f dir) {
        return proj_(this, dir);
    }

    /**
     * Projects a point onto a line
     *
     * @param pointPos position of the point to be projected
     * @param linePos point belonging to the projection line
     * @param lineDir direction of the projection line
     * @param dst output projected point
     */
    public static void projPointOnLine(Vec3f pointPos, Vec3f linePos, Vec3f lineDir, Vec3f dst) {
        sub(pointPos, linePos, dst);
        dst.proj(lineDir).add(linePos);
    }

    /**
     * Projects a point onto a line
     *
     * @param pointPos position of the point to be projected
     * @param linePos point belonging to the projection line
     * @param lineDir direction of the projection line
     * @return new projected point
     */
    public static Vec3f projPointOnLine_(Vec3f pointPos, Vec3f linePos, Vec3f lineDir) {
        Vec3f dst = new Vec3f();
        projPointOnLine(pointPos, linePos, lineDir, dst);
        return dst;
    }

    /**
     * Projects this point onto a line and stores the result in this vector
     *
     * @param linePos point belonging to the projection line
     * @param lineDir direction of the projection line
     * @return this projected point
     */
    public Vec3f projPointOnLine(Vec3f linePos, Vec3f lineDir) {
        projPointOnLine(this.clone(), linePos, lineDir, this);
        return this;
    }

    /**
     * Projects this point onto a line storing the result in a new vector
     *
     * @param linePos point belonging to the projection line
     * @param lineDir direction of the projection line
     * @return new projected point
     */
    public Vec3f projPointOnLine_(Vec3f linePos, Vec3f lineDir) {
        return projPointOnLine_(this, linePos, lineDir);
    }

    public static void projLineOnPlane(Vec3f linePos, Vec3f lineDir, Vec3f planePos, Vec3f planeNorm, Vec3f dst) {
        normalize(lineDir, dst);
        float t = (planeNorm.dot(planePos) - planeNorm.dot(linePos)) / planeNorm.dot(dst);
        dst.scale(t).add(linePos);
    }

    /**
     * Checks if all the coordinates of the vector are finite.
     *
     * @param src vector to be evaluated
     * @return
     */
    public static boolean isFinite(Vec3f src){
        return Float.isFinite(src.x) && Float.isFinite(src.y) && Float.isFinite(src.z);
    }

    /**
     * Checks if all the coordinates of this vector are finite.
     *
     * @return
     */
    public boolean isFinite(){
        return isFinite(this);
    }

    /**
     * Calculates the mean of a list of vectors
     *
     * @param srcs list of vector
     * @return mean of the vector list
     */
    public static Vec3f mean(Vec3f... srcs){
        Vec3f dst = new Vec3f();
        for(Vec3f v : srcs){
            dst.add(v);
        }
        dst.scale(1.0f / srcs.length);
        return dst;
    }
}
