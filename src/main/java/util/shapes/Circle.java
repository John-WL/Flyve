package util.shapes;

import util.math.vector.Ray2;
import util.math.vector.Vector2;

public class Circle {

    public Vector2 center;
    public double radii;

    public Circle(Vector2 center, double radius) {
        this.center = center;
        this.radii = radius;
    }

    public Vector2 findPointOnCircle(double rads) {
        Vector2 d = new Vector2(Math.cos(rads), Math.sin(rads));

        return center.plus(d.scaled(radii));
    }

    public Vector2 findClosestPointFrom(Vector2 v) {
        return center.plus(v.minus(center).scaledToMagnitude(radii));
    }

    public double findRadsFromClosestPoint(Vector2 v) {
        Vector2 closestPoint = findClosestPointFrom(v);
        return Math.atan2(closestPoint.minus(center).y, closestPoint.minus(center).x);
    }

    // don't look this is ugly! (but it works)
    public Vector2[] findIntersectionPoints(Circle circle) {
        // stoopid edge-case in which we can't compute it, but one or two solutions can still exist...
        // so we basically rotate the whole thing before calculating it and rotating it back to handle that.
        // it takes a little more time but at least it computes correctly.
        if(this.center.y == circle.center.y
                && this.center.x != circle.center.x) {
            Circle rotatedThis = new Circle(this.center.plusAngle(new Vector2(0, 1)), this.radii);
            Circle rotatedCircle = new Circle(circle.center.plusAngle(new Vector2(0, 1)), circle.radii);

            Vector2[] preliminaryIntersectionPoints = rotatedThis.findIntersectionPoints(rotatedCircle);

            return new Vector2[]{
                    preliminaryIntersectionPoints[0].minusAngle(new Vector2(0, 1)),
                    preliminaryIntersectionPoints[1].minusAngle(new Vector2(0, 1))
            };
        }

        double a = (-(this.center.x*this.center.x) - (this.center.y*this.center.y)
                + (circle.center.x*circle.center.x) + (circle.center.y*circle.center.y)
                + (this.radii *this.radii)
                - (circle.radii *circle.radii))
                / (2*(circle.center.y - this.center.y));
        double d = (circle.center.x - this.center.x)
                / (circle.center.y - this.center.y);

        double A = d*d + 1;
        double B = -2*this.center.x + 2*this.center.y*d - 2*a*d;
        double C = this.center.x*this.center.x + this.center.y*this.center.y - 2*this.center.y*a + a*a - this.radii *this.radii;
        double D = B*B - 4*A*C;
        double x1 = (-B + Math.sqrt(D))
                / (2*A);
        double x2 = (-B - Math.sqrt(D))
                / (2*A);
        double y1 = a - x1*d;
        double y2 = a - x2*d;

        return new Vector2[]{
                new Vector2(x1, y1),
                new Vector2(x2, y2)
        };
    }

    public Ray2[] findTangentsFrom(Vector2 pointOnTangent) {
        Vector2 centerOfCircleForFindingTangentPoints = this.center.plus(pointOnTangent).scaled(0.5);
        double radiiOfCircleForFindingTangentPoints = this.center.minus(pointOnTangent).magnitude()*0.5;
        Circle circleForFindingTangentPoints = new Circle(centerOfCircleForFindingTangentPoints, radiiOfCircleForFindingTangentPoints);

        Vector2[] tangentPoints = findIntersectionPoints(circleForFindingTangentPoints);

        return new Ray2[] {
                new Ray2(tangentPoints[0], pointOnTangent.minus(tangentPoints[0])),
                new Ray2(tangentPoints[1], pointOnTangent.minus(tangentPoints[1]))
        };
    }

    public Ray2[] findTangentsFrom(Circle circle) {
        Ray2[] externalTangents = findOuterTangents(circle);
        Ray2[] internalTangents = findInnerTangents(circle);

        if(this.center.y >= circle.center.y) {
            return new Ray2[]{
                    externalTangents[0],
                    externalTangents[1],
                    internalTangents[0],
                    internalTangents[1]
            };
        }
        else {
            return new Ray2[]{
                    externalTangents[1],
                    externalTangents[0],
                    internalTangents[1],
                    internalTangents[0]
            };
        }
    }

    private Ray2[] findOuterTangents(Circle circle) {
        if(this.radii > circle.radii) {
            return findOuterTangentsWithThisRadiiBiggerThanTheParameterRadii(circle);
        }
        else if(this.radii < circle.radii) {
            return findOuterTangentsWithThisRadiiSmallerThanTheParameterRadii(circle);
        }
        else {
            return findOuterTangentsWithThisRadiiEqualToTheParameterRadii(circle);
        }
    }

    private Ray2[] findOuterTangentsWithThisRadiiBiggerThanTheParameterRadii(Circle circle) {
        Circle circleForTangents = new Circle(this.center, this.radii - circle.radii);

        Ray2[] tangents = circleForTangents.findTangentsFrom(circle.center);
        Vector2[] tangentOffsets = new Vector2[] {
                tangents[0].offset.minus(this.center).scaledToMagnitude(circle.radii),
                tangents[1].offset.minus(this.center).scaledToMagnitude(circle.radii)
        };

        return new Ray2[] {
                new Ray2(tangents[0].offset.plus(tangentOffsets[0]), tangents[0].direction),
                new Ray2(tangents[1].offset.plus(tangentOffsets[1]), tangents[1].direction)
        };
    }

    private Ray2[] findOuterTangentsWithThisRadiiSmallerThanTheParameterRadii(Circle circle) {
        Circle circleForTangents = new Circle(circle.center, circle.radii - this.radii);

        Ray2[] tangents = circleForTangents.findTangentsFrom(this.center);
        Vector2[] tangentOffsets = new Vector2[] {
                tangents[0].offset.minus(circle.center).scaledToMagnitude(this.radii),
                tangents[1].offset.minus(circle.center).scaledToMagnitude(this.radii)
        };

        return new Ray2[] {
                new Ray2(tangents[0].offset.plus(tangents[0].direction).plus(tangentOffsets[0]), tangents[0].direction.scaled(-1)),
                new Ray2(tangents[1].offset.plus(tangents[1].direction).plus(tangentOffsets[1]), tangents[1].direction.scaled(-1))
        };
    }

    private Ray2[] findOuterTangentsWithThisRadiiEqualToTheParameterRadii(Circle circle) {
        Circle circleForTangents = new Circle(this.center, this.radii - circle.radii);

        Ray2[] tangents = circleForTangents.findTangentsFrom(circle.center);
        Vector2[] tangentOffsets = new Vector2[] {
                this.center.minus(circle.center).plusAngle(new Vector2(0, 1)).scaledToMagnitude(circle.radii),
                this.center.minus(circle.center).minusAngle(new Vector2(0, 1)).scaledToMagnitude(circle.radii)
        };

        return new Ray2[] {
                new Ray2(tangents[0].offset.plus(tangentOffsets[0]), tangents[0].direction),
                new Ray2(tangents[1].offset.plus(tangentOffsets[1]), tangents[1].direction)
        };
    }

    private Ray2[] findInnerTangents(Circle circle) {
        Circle circleForTangents = new Circle(this.center, this.radii + circle.radii);

        Ray2[] tangents = circleForTangents.findTangentsFrom(circle.center);
        Vector2[] tangentOffsets = new Vector2[] {
                tangents[0].offset.minus(this.center).scaledToMagnitude(-circle.radii),
                tangents[1].offset.minus(this.center).scaledToMagnitude(-circle.radii)
        };

        return new Ray2[] {
                new Ray2(tangents[0].offset.plus(tangentOffsets[0]), tangents[0].direction),
                new Ray2(tangents[1].offset.plus(tangentOffsets[1]), tangents[1].direction)
        };
    }

    public Vector2 getCenter() {
        return center;
    }

    public double getRadii() {
        return radii;
    }

    public boolean contains(Vector2 point) {
        return point.minus(center).magnitudeSquared() < radii * radii;
    }
}
