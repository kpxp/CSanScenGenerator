package CSanScenGenerator;

/**
 *
 * @author Peter
 */
public class Point {
    public int x, y;
    public Point(int x, int y){this.x = x; this.y = y;}
    public static int distance(Point a, Point b){
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
    @Override
    public String toString(){
        return "(" + x + "," + y + ")";
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Point)) return false;
        return x == ((Point) o).x && y == ((Point) o).y;
    }

    @Override
    public int hashCode() {
        return x + y << 5;
    }
}
