package raycasting.math;

public class Triangle2D {
	private static final double EPSILON = 0.0001;
	
	private Point2D vertex0, vertex1, vertex2;
	private double a;
	private double sc, sx, sy, tc, tx, ty;
	
	public Triangle2D(Point2D v0, Point2D v1, Point2D v2) {
		vertex0 = v0;
		vertex1 = v1;
		vertex2 = v2;
		
		sc = v0.getY() * v2.getX() - v0.getX() * v2.getY();
		sx = v2.getY() - v0.getY();
		sy = v0.getX() - v2.getX();
		tc = v0.getX() * v1.getY() - v0.getY() * v1.getX();
		tx = v0.getY() - v1.getY();
		ty = v1.getX() - v0.getX();
		
		a = -v1.getY() * v2.getX() + v0.getY() * (v2.getX() - v1.getX()) + v0.getX() * (v1.getY() - v2.getY()) + v1.getX() * v2.getY();
	}
	
	public Point2D getVertex(int v) {
		switch(v) {
			case 0: return vertex0;
			case 1: return vertex1;
			case 2: return vertex2;
		}
		
		throw new IllegalArgumentException("Cannot get vertex "+v+" of triangle");
	}
	
	public boolean pointInside(Point2D p) {
		double s = sc + sx * p.getX() + sy * p.getY();
	    double t = tc + tx * p.getX() + ty * p.getY();

	    if ((s < 0) != (t < 0))
	        return false;

	    return a < 0 ? (s <= EPSILON && s + t >= a - EPSILON) : (s >= -EPSILON && s + t <= a + EPSILON);
	}
}
