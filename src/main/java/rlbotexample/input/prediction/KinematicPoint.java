package rlbotexample.input.prediction;

import util.vector.Vector3;

public class KinematicPoint {

    private Vector3 position;
    private Vector3 speed;
    private double gameTime;

    public KinematicPoint(Vector3 position, Vector3 speed, double gameTime) {
        this.position = position;
        this.speed = speed;
        this.gameTime = gameTime;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getSpeed() {
        return speed;
    }

    public double getGameTime() {
        return gameTime;
    }
}
