import java.awt.*;

public class Vector {
    public double x, y;
    public Vector() { this(0, 0); }
    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    public Vector(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public Vector times(int time) { return new Vector(x * time, y * time); }
    public Vector copy() { return new Vector(x, y);}
    public Vector add(Vector other) { return new Vector(x + other.x, y + other.y); }
    public Vector subtract(Vector other) { return new Vector(x - other.x, y - other.y); }
    public Vector invert() { return new Vector(y, x); }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector && ((Vector) obj).x == x && ((Vector) obj).y == y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
    public static Vector convert(Point point) { return new Vector(point.x, point.y); }
}

