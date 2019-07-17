package raycasting.math;

public class Point2D extends Tuple2D {

	public Point2D(double x, double y) {
		super(x, y);
	}

	public Point2D(Tuple2D other) {
		super(other);
	}
	
	public double distanceSquared(Point2D other) {
		double dx = getX()-other.getX(), dy = getY()-other.getY();
		return dx*dx + dy*dy;
	}
	
	public double distance(Point2D other) {
		return Math.sqrt(distanceSquared(other));
	}
	
	public Point2D add(Vector2D vector) {
		return new Point2D(getX()+vector.getX(), getY()+vector.getY());
	}
	
	public Point2D sub(Vector2D vector) {
		return new Point2D(getX()-vector.getX(), getY()-vector.getY());
	}
	
	public Vector2D from(Point2D other) {
		return new Vector2D(getX()-other.getX(), getY()-other.getY());
	}
	
	public Vector2D to(Point2D other) {
		return other.from(this);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Point2D && super.equals(other);
	}

}
