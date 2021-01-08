package rlbotexample.input.dynamic_data.car;

import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class Orientation {
    public final Vector3 nose;
    public final Vector3 roof;

    public Orientation() {
        this.nose = new Vector3(1, 0, 0);
        this.roof = new Vector3(0, 0, 1);
    }

    public Orientation(Vector3 nose, Vector3 roof) {
        this.nose = nose;
        this.roof = roof;
    }

    // this function finds the rotation vector
    // that rotates an oriented object onto another oriented object.
    // this is useful to find the optimal rotation path
    // to orient the car in the air, for example.

    // let's say you are facing a certain way in the air, and you want to face another way,
    // then what is the orientation and magnitude of the rotation that you need to take in order to get to
    // the new desired orientation? this function gives you such a vector.

    // it can seem kind of tricky at first, because you also need to find the orientation
    // of the rotator, such that it rotates the roof of the car at the exact spot we want,
    // while still rotating the nose at the correct spot, all of this in a single rotation.

    // the "naive" approach would be to rotate directly the nose of the car into place, and THEN rotate the roof,
    // but it's quicker to do both the nose and the roof rotation at the same time.
    public Vector3 findRotatorToRotateTo(Orientation orientation) {
        final Vector3 nosesOrientationAverage = this.nose.plus(orientation.nose).normalized();
        Vector3 rotatorMiddlePosition = this.nose.crossProduct(orientation.nose).normalized();
        // avoid the edge-case where our vectors have the same orientation
        if(rotatorMiddlePosition.magnitudeSquared() == 0) {
            rotatorMiddlePosition = this.nose.crossProduct(orientation.nose.plusAngle(new Vector3(0, 1, 0))).normalized();
        }
        // this rotator is the "naive" rotation that rotates the nose, but not the roof at the correct spot
        final double shortestAngleBetweenNoses = this.nose.angle(orientation.nose);
        final Vector3 shortestRotator = rotatorMiddlePosition.scaledToMagnitude(shortestAngleBetweenNoses);

        // first rotation of the desired roof vector.
        // we are going to rotate it again later to compare it with the current roof orientation so we can know
        // how much we need to rotate the final rotator.
        final Vector3 rotatedRoofVectorToFindAngleBetweenRoofs = orientation.roof.rotate(shortestRotator.scaled(-1));

        // this rotator is the one that would rotate the current nose vector at the upward position
        // if we were to apply its rotation to it
        final double angleBetweenCurrentNoseAndUpVector = this.nose.angle(Vector3.UP_VECTOR);
        final Vector3 rotatorToPointUpwardAndFindAngleBetweenRoofs = this.nose.crossProduct(Vector3.UP_VECTOR)
                .scaledToMagnitude(angleBetweenCurrentNoseAndUpVector);

        // rotating the current roof with a rotator that would align the current nose vector with the upward vector
        final Vector3 rotatedCurrentRoof = this.roof.rotate(rotatorToPointUpwardAndFindAngleBetweenRoofs);
        // second rotation of desired roof
        final Vector3 rotatedDesiredRoof = rotatedRoofVectorToFindAngleBetweenRoofs
                .rotate(rotatorToPointUpwardAndFindAngleBetweenRoofs);
        // together, these 2 rotations are going to let us flatten the vectors so we can find a signed angle between them

        // flattening and...
        final Vector2 flattenedCurrentRoof = rotatedCurrentRoof.flatten();
        final Vector2 flattenedDesiredRoof = rotatedDesiredRoof.flatten();

        // finding the angle phi and theta
        final double angleBetweenRoofs = flattenedCurrentRoof.correctionAngle(flattenedDesiredRoof);
        final double theta = angleBetweenRoofs/2;

        // rotator to rotate to the exact spot the un-rotated rotator. It's gonna give us an unscaled rotator, so
        // we need to also find the angle needed to perform the rotation afterwards.
        final Vector3 rotatorOfRotator = rotatorMiddlePosition.crossProduct(nosesOrientationAverage).scaledToMagnitude(theta);

        // find the unscaled rotator
        final Vector3 unscaledRotator = rotatorMiddlePosition.rotate(rotatorOfRotator).normalized();

        // the rest of this section is to rotate the nose vectors, to flatten them, and to find the angle
        // separating them in the direction of the rotation
        final double angleBetweenTheRotatorAndTheUpwardVector = unscaledRotator.angle(Vector3.UP_VECTOR);
        final Vector3 rotatorToRotateTheRotatorUpwards = unscaledRotator.crossProduct(Vector3.UP_VECTOR)
                .scaledToMagnitude(angleBetweenTheRotatorAndTheUpwardVector);

        final Vector3 rotatedCurrentNose = this.nose.rotate(rotatorToRotateTheRotatorUpwards);
        final Vector3 rotatedDesiredNose = orientation.nose.rotate(rotatorToRotateTheRotatorUpwards);

        final Vector2 flattenedCurrentNose = rotatedCurrentNose.flatten();
        final Vector2 flattenedDesiredNose = rotatedDesiredNose.flatten();

        final double angleBetweenNoses = Math.abs(flattenedCurrentNose.correctionAngle(flattenedDesiredNose));

        // return the scaled vector
        return unscaledRotator.scaledToMagnitude(angleBetweenNoses);
    }

    public Vector3 getNose() {
        return nose;
    }

    public Vector3 getRoof() {
        return roof;
    }
}
