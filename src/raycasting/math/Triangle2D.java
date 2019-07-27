package raycasting.math;

import raycasting.rendering.RenderMath;

public class Triangle2D {
	private Point2D vertex0, vertex1, vertex2;
	private double a, m0, m1, m2;
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
		m0 = (v1.getY() - v0.getY())/(v1.getX() - v0.getX());
		m1 = (v2.getY() - v1.getY())/(v2.getX() - v1.getX());
		m2 = (v0.getY() - v2.getY())/(v0.getX() - v2.getX());
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
		if(Math.abs((p.getY() - vertex0.getY())/(p.getX() - vertex0.getX()) - m0) < RenderMath.EPSILON
				|| Math.abs((p.getY() - vertex1.getY())/(p.getX() - vertex1.getX()) - m1) < RenderMath.EPSILON
				|| Math.abs((p.getY() - vertex2.getY())/(p.getX() - vertex2.getX()) - m2) < RenderMath.EPSILON) {
			return true;
		}
		
		double s = sc + sx * p.getX() + sy * p.getY();
	    double t = tc + tx * p.getX() + ty * p.getY();

	    if ((s < 0) != (t < 0))
	        return false;

	    return a < 0 ? (s <= RenderMath.EPSILON && s + t >= a - RenderMath.EPSILON) :
	    	(s >= -RenderMath.EPSILON && s + t <= a + RenderMath.EPSILON);
	}
}
