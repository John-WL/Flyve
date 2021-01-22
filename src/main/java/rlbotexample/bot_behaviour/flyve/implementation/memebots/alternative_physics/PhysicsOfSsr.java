package rlbotexample.bot_behaviour.flyve.implementation.memebots.alternative_physics;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.shapes.Sphere;

import java.util.ArrayList;
import java.util.List;

public class PhysicsOfSsr {

    public static final double PLAYERS_RADII = 100;
    public static final double FRONT_FLIP_RANGE = 800;
    public static final double ATTACK_IMPULSE_STRENGTH = 800;

    private static final List<AssignedVector3> assignedImpulses = new ArrayList<>();
    private static final List<AssignedVector3> assignedPenetrations = new ArrayList<>();

    public static void execute(DataPacket input) {
        BallStateSetter.handleBallState(input);

        findAdditionalImpulses(input);
        applyImpulses(input);
    }

    private static void findAdditionalImpulses(DataPacket input) {

        assignedImpulses.clear();
        assignedPenetrations.clear();

        for(ExtendedCarData car: input.allCars) {
            attack(car, input);
            computeCarCollisions(car, input);
        }
    }

    private static void computeCarCollisions(ExtendedCarData car, DataPacket input) {
        for(ExtendedCarData otherCar: input.allCars) {
            if(otherCar.equals(car)) {
                continue;
            }

            if(car.position.minus(otherCar.position).magnitudeSquared() < PLAYERS_RADII * PLAYERS_RADII *4) {
                Vector3 carPenetration = new Vector3(otherCar.position.minus(car.position))
                        .scaledToMagnitude(car.position.minus(otherCar.position).magnitude() - 2* PLAYERS_RADII);
                Vector3 carImpulse = otherCar.velocity.minus(car.velocity).projectOnto(otherCar.position.minus(car.position))
                        .scaled(1.5)
                        .plus(carPenetration);
                assignedImpulses.add(new AssignedVector3(car, carImpulse));
                System.out.println(car.isSupersonic);
                System.out.println(carImpulse);
                assignedPenetrations.add(new AssignedVector3(car, carPenetration));
            }
        }

        Ray3 mapCollisionRay = StandardMapSplitMesh.getCollisionRayOrElse(new Sphere(car.position, PLAYERS_RADII), null);
        if(mapCollisionRay != null && mapCollisionRay.direction.dotProduct(car.velocity) < 0) {
            Vector3 perpendicularComponent = car.velocity.projectOnto(mapCollisionRay.direction);
            assignedImpulses.add(new AssignedVector3(car, perpendicularComponent.scaled(-1.6)));
            assignedPenetrations.add(new AssignedVector3(car, mapCollisionRay.direction.scaledToMagnitude(0.5)));
        }
    }

    private static void attack(ExtendedCarData attackingCar, DataPacket input) {
        if(attackingCar.previousHasJustUsedSecondJump) {
            //addWeirdAttackImpulses(attackingCar, input);

            Vector3 flipDirection = attackingCar.spin.scaled(-1).toFrameOfReference(attackingCar.orientation);
            addFrontFlipAttackImpulses(attackingCar, flipDirection, input);
            addBackFlipAttackImpulses(attackingCar, flipDirection, input);
        }
    }

    private static void addFrontFlipAttackImpulses(ExtendedCarData attackingCar, Vector3 flipDirection, DataPacket input) {
        if(flipDirection.y > Math.abs(2*flipDirection.x)) {
            for(ExtendedCarData otherCar: input.allCars) {
                if(otherCar.equals(attackingCar)) {
                    continue;
                }

                if(attackingCar.position.minus(otherCar.position).magnitude() < FRONT_FLIP_RANGE) {
                    assignedImpulses.add(new AssignedVector3(otherCar, attackingCar.orientation.roofVector.scaled(-1).plus(attackingCar.orientation.noseVector).scaledToMagnitude(ATTACK_IMPULSE_STRENGTH)));
                }
            }
        }
    }

    private static void addBackFlipAttackImpulses(ExtendedCarData attackingCar, Vector3 flipDirection, DataPacket input) {
        if(flipDirection.y < -Math.abs(2*flipDirection.x)) {
            for(ExtendedCarData otherCar: input.allCars) {
                if(otherCar.equals(attackingCar)) {
                    continue;
                }

                if(attackingCar.position.minus(otherCar.position).magnitude() < FRONT_FLIP_RANGE) {
                    assignedImpulses.add(new AssignedVector3(otherCar, attackingCar.orientation.roofVector.plus(attackingCar.orientation.noseVector.scaled(-1)).scaledToMagnitude(ATTACK_IMPULSE_STRENGTH)));
                }
            }
        }
    }

    private static void addWeirdAttackImpulses(ExtendedCarData attackingCar, DataPacket input) {
        Vector3 forceDirection = attackingCar.spin.scaled(-1).crossProduct(attackingCar.orientation.roofVector);
        forceDirection = forceDirection.scaledToMagnitude(4600);

        for(ExtendedCarData otherCar: input.allCars) {
            if (otherCar.equals(attackingCar)) {
                continue;
            }

            if(attackingCar.position.minus(otherCar.position).magnitude() < 1200) {
                assignedImpulses.add(new AssignedVector3(otherCar, forceDirection));
            }
        }
    }

    public static void applyImpulses(DataPacket input) {
        for(ExtendedCarData car: input.allCars) {
            Vector3 impulse = new Vector3();
            Vector3 penetration = new Vector3();

            for(AssignedVector3 element: assignedImpulses) {
                if(element.carData == car) {
                    impulse = impulse.plus(element.vector);
                }
            }
            for(AssignedVector3 element: assignedPenetrations) {
                if(element.carData == car) {
                    penetration = penetration.plus(element.vector);
                }
            }

            /*
            if(car == input.car) {
                if(car.position.z < 3000) {
                    CarStateSetter.addImpulseAndMoveBy(car, new Vector3(), new Vector3(0, 0, 1000));
                }
            }
            else {
                CarStateSetter.addImpulseAndMoveBy(car, impulse, penetration);
            }*/
            CarStateSetter.addImpulseAndMoveBy(car, impulse, penetration);
        }
    }
}
