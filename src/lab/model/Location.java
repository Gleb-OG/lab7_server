package lab.model;


public class Location {
    private float x;
    private double y;
    private Long z; //Поле не может быть null

    public Location(float inputX, double inputY, Long inputZ) {
        this.x = inputX;
        this.y = inputY;
        this.z = inputZ;
    }

    public Location() {
    }

    public float getX() { return x; }

    public double getY() { return y; }

    public Long getZ() { return z; }

    public void setX(float input) {
        this.x = input;
    }

    public void setY(double input) {
        this.y = input;
    }

    public void setZ(Long input) {
        this.z = input;
    }

    @Override
    public String toString() {
        return " {" +
                "\n      x = " + x +
                "\n      y = " + y +
                "\n      z = " + z + "\n}";
    }
}