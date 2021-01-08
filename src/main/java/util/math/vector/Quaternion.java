package util.math.vector;

public class Quaternion {
    public double r;
    public double i;
    public double j;
    public double k;

    public Quaternion(double r, double i, double j, double k) {
        this.r = r;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    public Quaternion(double r, Vector3 v) {
        this.r = r;
        this.i = v.x;
        this.j = v.y;
        this.k = v.z;
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                r*other.r - i*other.i - j*other.j - k*other.k,
                r*other.i + i*other.r + j*other.k - k*other.j,
                r*other.j + j*other.r + k*other.i - i*other.k,
                r*other.k + k*other.r + i*other.j - j*other.i);
    }

    public Vector3 toVector3() {
        return new Vector3(i, j, k);
    }
}
