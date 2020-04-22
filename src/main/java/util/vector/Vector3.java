package util.vector;

import com.google.flatbuffers.FlatBufferBuilder;

/**
 * A simple 3d vector class with the most essential operations.
 *
 * This class is here for your convenience, it is NOT part of the framework. You can add to it as much
 * as you want, or delete it.
 */
public class Vector3 extends rlbot.vector.Vector3 {

    public Vector3(double x, double y, double z) {
        super((float) x, (float) y, (float) z);
    }

    public Vector3(Vector2 xy, double z) { super((float) xy.x, (float) xy.y, (float) z); }

    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3(rlbot.flat.Vector3 vec) {
        // Invert the X value so that the axes make more sense.
        this(-vec.x(), vec.y(), vec.z());
    }

    public int toFlatbuffer(FlatBufferBuilder builder) {
        // Invert the X value again so that rlbot sees the format it expects.
        return rlbot.flat.Vector3.createVector3(builder, -x, y, z);
    }

    public Vector3 plus(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 minus(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 scaled(double scale) {
        return new Vector3(x * scale, y * scale, z * scale);
    }

    public Vector3 scaled(double scaleX, double scaleY, double scaleZ) {
        return new Vector3(x * scaleX, y * scaleY, z * scaleZ);
    }

    /**
     * If magnitude is negative, we will return a vector facing the opposite direction.
     */
    public Vector3 scaledToMagnitude(double magnitude) {
        if (isZero()) {
            return new Vector3();
        }
        double scaleRequired = magnitude / magnitude();
        return scaled(scaleRequired);
    }

    public double distance(Vector3 other) {
        double xDiff = x - other.x;
        double yDiff = y - other.y;
        double zDiff = z - other.z;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }

    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public double magnitudeSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3 normalized() {

        if (isZero()) {
            return new Vector3();
        }
        return this.scaled(1 / magnitude());
    }

    public double dotProduct(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Vector2 flatten() {
        return new Vector2(x, y);
    }

    public double angle(Vector3 v) {
        double mag2 = magnitudeSquared();
        double vmag2 = v.magnitudeSquared();
        double dot = dotProduct(v);
        return Math.acos(dot / Math.sqrt(mag2 * vmag2));
    }

    public Vector3 crossProduct(Vector3 v) {
        double tx = y * v.z - z * v.y;
        double ty = z * v.x - x * v.z;
        double tz = x * v.y - y * v.x;
        return new Vector3(tx, ty, tz);
    }

    public Vector3 toFrameOfReference(Vector3 frontDirection, Vector3 topDirection)
    {
        // Calculate the vector without any roll yet (the roll is calculated from the topDirection vector)
        Vector3 frameOfRefWithoutRoll = this.minusAngle(frontDirection);
        // Calculate the roll vector in the frame of ref so we can use it to do a planar projection followed by a rotation later on.
        // Basically, the vector is going to do nothing if it faces upward, and it's going to subtract its angle from
        // that top position if it has any
        Vector3 rollInFrameOfRef = topDirection.minusAngle(frontDirection);

        // Calculating the 2D equivalents in the planar projection
        Vector2 flattenedFrameOfRefWithoutRoll = new Vector2(frameOfRefWithoutRoll.z, frameOfRefWithoutRoll.y);
        Vector2 flattenedRollInFrameOfRef = new Vector2(rollInFrameOfRef.z, rollInFrameOfRef.y);

        // Applying the roll rotation
        Vector2 planarProjectionZyOfResult = flattenedFrameOfRefWithoutRoll.minusAngle(flattenedRollInFrameOfRef);

        // Put back into a coherent form the calculated coordinates and return the vector
        return new Vector3(frameOfRefWithoutRoll.x, planarProjectionZyOfResult.y, planarProjectionZyOfResult.x);
    }

    public Vector3 minusAngle(Vector3 rotationVector) {
        // Rotating the vector in xy beforehand
        Vector3 firstRotatedVector = new Vector3(this.flatten().minusAngle(rotationVector.flatten()), this.z);
        Vector3 firstRotationVector = new Vector3(rotationVector.flatten().magnitude(), 0, rotationVector.z);

        // Then rotating it in xz (the rotating vector in y is 0, thus we now only need to rotate it in xz)
        Vector2 projectedVectorXz = new Vector2(firstRotatedVector.x, firstRotatedVector.z)
                .minusAngle(new Vector2(firstRotationVector.x, firstRotationVector.z));

        // We can now add the x and the z coordinates separately from the firstly calculated y coordinate
        return new Vector3(projectedVectorXz.x, firstRotatedVector.y, projectedVectorXz.y);
    }

    public Vector3 plusAngle(Vector3 rotationVector) {
        // Rotating the vector in xy beforehand
        Vector3 firstRotatedVector = new Vector3(this.flatten().plusAngle(rotationVector.flatten()), this.z);
        Vector3 firstRotationVector = new Vector3(rotationVector.flatten().plusAngle(rotationVector.flatten()), rotationVector.z);

        // Then rotating it in the planar projection of the rotation angle (x, y, 0), (x, y, z)
        Vector2 projectedVectorXyXyz = new Vector2(firstRotatedVector.flatten().magnitude(), firstRotatedVector.z)
                .plusAngle(new Vector2(firstRotationVector.flatten().magnitude(), firstRotationVector.z));

        Vector2 resultXy = firstRotatedVector.flatten().scaledToMagnitude(projectedVectorXyXyz.x);
        // We can now add the x and the z coordinates separately from the firstly calculated y coordinate
        return new Vector3(resultXy.x, resultXy.y, projectedVectorXyXyz.y);
    }

    @Override
    public String toString() {
        return "[ x:" + this.x + ", y:" + this.y + ", z:" + this.z + " ]";
    }
}
