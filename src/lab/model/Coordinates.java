package lab.model;


public class Coordinates {
    private Double x; //Значение поля должно быть больше -922, Поле не может быть null
    private long y;

    /**
     * Задает координаты объекта класса Coordinates из двух элементов типа String,
     * соответствующих координатам "х" и "y".*/
    public Coordinates(Double inputX, long inputY) {
        this.x = inputX;
        this.y = inputY;
    }

    public Coordinates() {
    }

    public void setX(Double inputX) {
        this.x = inputX;
    }

    public void setY(long inputY) {
        this.y = inputY;
    }

    public Double getX() {
        return this.x;
    }

    public long getY() {
        return this.y;
    }


    public String fileToString() {
        return x + ";" + y;
    }

    @Override
    public String toString() {
        return " {" +
                "\n  x = " + x +
                "\n  y = " + y + "\n}";
    }
}