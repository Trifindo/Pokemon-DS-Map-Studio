package math.transf;

import math.mat.Mat3f;
import math.mat.Mat4f;
import math.vec.Vec3f;

public class TransfMat {

    public static void perspective(float fovy, float aspect, float zNear, float zFar, Mat4f dst) {
        float tanHalfFovy = (float) Math.tan(Math.toRadians(fovy) * 0.5f);
        dst.m00 = 1.0f / (aspect * tanHalfFovy);
        dst.m01 = 0.0f;
        dst.m02 = 0.0f;
        dst.m03 = 0.0f;
        dst.m10 = 0.0f;
        dst.m11 = 1.0f / tanHalfFovy;
        dst.m12 = 0.0f;
        dst.m13 = 0.0f;
        dst.m20 = 0.0f;
        dst.m21 = 0.0f;
        dst.m22 = -(zFar + zNear) / (zFar - zNear);
        dst.m23 = -2.0f * zFar * zNear / (zFar - zNear);
        dst.m30 = 0.0f;
        dst.m31 = 0.0f;
        dst.m32 = -1.0f;
        dst.m33 = 0.0f;
    }

    public static Mat4f perspective_(float fovy, float aspect, float zNear, float zFar) {
        Mat4f dst = new Mat4f();
        perspective(fovy, aspect, zNear, zFar, dst);
        return dst;
    }

    public static void ortho(float left, float right, float bottom, float top, float zNear, float zFar, Mat4f dst) {
        dst.m00 = 2.0f / (right - left);
        dst.m10 = 0.0f;
        dst.m20 = 0.0f;
        dst.m30 = 0.0f;
        dst.m01 = 0.0f;
        dst.m11 = 2.0f / (top - bottom);
        dst.m21 = 0.0f;
        dst.m31 = 0.0f;
        dst.m02 = 0.0f;
        dst.m12 = 0.0f;
        dst.m22 = -2.0f / (zFar - zNear);
        dst.m32 = 0.0f;
        dst.m03 = -(right + left) / (right - left);
        dst.m13 = -(top + bottom) / (top - bottom);
        dst.m23 = -(zFar + zNear) / (zFar - zNear);
        dst.m33 = 1.0f;
    }

    public static Mat4f ortho_(float left, float right, float bottom, float top, float zNear, float zFar) {
        Mat4f dst = new Mat4f();
        ortho(left, right, bottom, top, zNear, zFar, dst);
        return dst;
    }

    public static void ortho_(float scale, float aspect, float zNear, float zFar, Mat4f dst){
        ortho(-scale * aspect, scale*aspect, -scale, scale, zNear, zFar, dst);
    }

    public static Mat4f ortho_(float scale, float aspect, float zNear, float zFar){
        Mat4f dst = new Mat4f();
        ortho_(scale, aspect, zNear, zFar, dst);
        return dst;
    }

    public static void rotation3f(float radians, Vec3f axis, Mat3f dst) {
        final float cos = (float) Math.cos(radians);
        final float sin = (float) Math.sin(radians);
        dst.m00 = cos + axis.x * axis.x * (1.0f - cos);
        dst.m01 = axis.x * axis.y * (1.0f - cos) - axis.z * sin;
        dst.m02 = axis.x * axis.z * (1.0f - cos) + axis.y * sin;

        dst.m10 = axis.y * axis.x * (1.0f - cos) + axis.z * sin;
        dst.m11 = cos + axis.y * axis.y * (1.0f - cos);
        dst.m12 = axis.y * axis.z * (1.0f - cos) - axis.x * sin;

        dst.m20 = axis.z * axis.x * (1.0f - cos) - axis.y * sin;
        dst.m21 = axis.z * axis.y * (1.0f - cos) + axis.x * sin;
        dst.m22 = cos + axis.z * axis.z * (1.0f - cos);
    }

    public static Mat3f rotation3f_(float radians, Vec3f axis){
        Mat3f dst = new Mat3f();
        rotation3f(radians, axis, dst);
        return dst;
    }

    public static void rotationDeg3f(float degrees, Vec3f axis, Mat3f dst) {
        rotation3f((float)Math.toRadians(degrees), axis, dst);
    }

    public static Mat3f rotationDeg3f_(float degrees, Vec3f axis) {
        Mat3f dst = new Mat3f();
        rotationDeg3f(degrees, axis, dst);
        return dst;
    }

    public static void rotationDeg(float degrees, Vec3f axis, Mat4f dst) {
        final float cos = (float) Math.cos(Math.toRadians(degrees));
        final float sin = (float) Math.sin(Math.toRadians(degrees));
        dst.m00 = cos + axis.x * axis.x * (1.0f - cos);
        dst.m01 = axis.x * axis.y * (1.0f - cos) - axis.z * sin;
        dst.m02 = axis.x * axis.z * (1.0f - cos) + axis.y * sin;
        dst.m03 = 0.0f;

        dst.m10 = axis.y * axis.x * (1.0f - cos) + axis.z * sin;
        dst.m11 = cos + axis.y * axis.y * (1.0f - cos);
        dst.m12 = axis.y * axis.z * (1.0f - cos) - axis.x * sin;
        dst.m13 = 0.0f;

        dst.m20 = axis.z * axis.x * (1.0f - cos) - axis.y * sin;
        dst.m21 = axis.z * axis.y * (1.0f - cos) + axis.x * sin;
        dst.m22 = cos + axis.z * axis.z * (1.0f - cos);
        dst.m23 = 0.0f;

        dst.m30 = 0.0f;
        dst.m31 = 0.0f;
        dst.m32 = 0.0f;
        dst.m33 = 1.0f;
    }

    public static Mat4f rotationDeg_(float degrees, Vec3f axis) {
        Mat4f dst = new Mat4f();
        rotationDeg(degrees, axis, dst);
        return dst;
    }

    public static void translation(Vec3f trans, Mat4f dst) {
        dst.m00 = 1.0f;
        dst.m01 = 0.0f;
        dst.m02 = 0.0f;
        dst.m03 = trans.x;

        dst.m10 = 0.0f;
        dst.m11 = 1.0f;
        dst.m12 = 0.0f;
        dst.m13 = trans.y;

        dst.m20 = 0.0f;
        dst.m21 = 0.0f;
        dst.m22 = 1.0f;
        dst.m23 = trans.z;

        dst.m30 = 0.0f;
        dst.m31 = 0.0f;
        dst.m32 = 0.0f;
        dst.m33 = 1.0f;
    }

    public static Mat4f translation_(Vec3f trans) {
        Mat4f dst = new Mat4f();
        translation(trans, dst);
        return dst;
    }

    public static void scale(Vec3f scale, Mat4f dst){
        dst.m00 = scale.x;
        dst.m01 = 0.0f;
        dst.m02 = 0.0f;
        dst.m03 = 0.0f;

        dst.m10 = 0.0f;
        dst.m11 = scale.y;
        dst.m12 = 0.0f;
        dst.m13 = 0.0f;

        dst.m20 = 0.0f;
        dst.m21 = 0.0f;
        dst.m22 = scale.z;
        dst.m23 = 0.0f;

        dst.m30 = 0.0f;
        dst.m31 = 0.0f;
        dst.m32 = 0.0f;
        dst.m33 = 1.0f;
    }

    public static Mat4f scale_(Vec3f scale){
        Mat4f dst = new Mat4f();
        scale(scale, dst);
        return dst;
    }

    public static void lookAt(Vec3f pos, Vec3f tar, Vec3f up, Mat4f dst) {
        Vec3f camDirection = pos.sub_(tar).normalize();
        Vec3f camRight = up.cross_(camDirection).normalize();
        Vec3f camUp = camDirection.cross_(camRight);

        dst.m00 = camRight.x;
        dst.m01 = camRight.y;
        dst.m02 = camRight.z;
        dst.m03 = 0.0f;

        dst.m10 = camUp.x;
        dst.m11 = camUp.y;
        dst.m12 = camUp.z;
        dst.m13 = 0.0f;

        dst.m20 = camDirection.x;
        dst.m21 = camDirection.y;
        dst.m22 = camDirection.z;
        dst.m23 = 0.0f;

        dst.m30 = 0.0f;
        dst.m31 = 0.0f;
        dst.m32 = 0.0f;
        dst.m33 = 1.0f;

        Mat4f trans = Mat4f.identity();
        trans.m03 = -pos.x;
        trans.m13 = -pos.y;
        trans.m23 = -pos.z;

        dst.mul(trans);

    }

    public static Mat4f lookAt_(Vec3f pos, Vec3f tar, Vec3f up) {
        Mat4f dst = new Mat4f();
        lookAt(pos, tar, up, dst);
        return dst;
    }

    public static void eulerDegToMat(Vec3f degrees, Mat3f rot) {
        Mat3f rx = TransfMat.rotationDeg3f_(degrees.x, new Vec3f(1.0f, 0.0f, 0.0f));
        Mat3f ry = TransfMat.rotationDeg3f_(degrees.y, new Vec3f(0.0f, 1.0f, 0.0f));
        Mat3f rz = TransfMat.rotationDeg3f_(degrees.z, new Vec3f(0.0f, 0.0f, 1.0f));

        rot.set(rz.mul(ry.mul(rx)));
    }

    public static Mat3f eulerDegToMat_(Vec3f degrees) {
        Mat3f dst = new Mat3f();
        eulerDegToMat(degrees, dst);
        return dst;
    }

    public static void eulerToMat(Vec3f radians, Mat3f rot) {
        Mat3f rx = TransfMat.rotation3f_(radians.x, new Vec3f(1.0f, 0.0f, 0.0f));
        Mat3f ry = TransfMat.rotation3f_(radians.y, new Vec3f(0.0f, 1.0f, 0.0f));
        Mat3f rz = TransfMat.rotation3f_(radians.z, new Vec3f(0.0f, 0.0f, 1.0f));

        rot.set(rz.mul(ry.mul(rx)));
    }

    public static Mat3f eulerToMat_(Vec3f radians) {
        Mat3f dst = new Mat3f();
        eulerToMat(radians, dst);
        return dst;
    }

    public static void matToEuler(Mat3f mat, Vec3f dst) {
        float sy = (float) Math.sqrt(mat.m00 * mat.m00 + mat.m10 * mat.m10);

        if (sy > 1e-6) { //not singular
            dst.x = (float) Math.atan2(mat.m21, mat.m22);
            dst.y = (float) Math.atan2(-mat.m20, sy);
            dst.z = (float) Math.atan2(mat.m10, mat.m00);
        } else {
            dst.x = (float) Math.atan2(-mat.m12, mat.m11);
            dst.y = (float) Math.atan2(-mat.m20, sy);
            dst.z = 0.0f;
        }
    }

    public static Vec3f matToEuler_(Mat3f mat){
        Vec3f dst = new Vec3f();
        matToEuler(mat, dst);
        return dst;
    }

    public static void matToEulerDeg(Mat3f mat, Vec3f dst) {
        matToEuler(mat, dst);
        dst.toDegrees();
    }

    public static Vec3f matToEulerDeg_(Mat3f mat){
        Vec3f dst = new Vec3f();
        matToEulerDeg(mat, dst);
        return dst;
    }
}