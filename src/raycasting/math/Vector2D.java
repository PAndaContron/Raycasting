package raycasting.math;

public class Vector2D extends Tuple2D {

	public Vector2D(double x, double y) {
		super(x, y);
	}

	public Vector2D(Tuple2D other) {
		super(other);
	}
	
	public double magnitudeSqaured() {
		return dot(this);
	}
	
	public double magnitude() {
		return Math.sqrt(magnitudeSqaured());
	}
	
	public Vector2D normalize() {
		return scalarMultiply(1/magnitude());
	}
	
	public Vector2D negate() {
		return new Vector2D(-getX(), -getY());
	}
	
	public Vector2D scalarMultiply(double scalar) {
		return new Vector2D(getX()*scalar, getY()*scalar);
	}
	
	public Vector2D add(Vector2D other) {
		return new Vector2D(getX()+other.getX(), getY()+other.getY());
	}
	
	public Vector2D sub(Vector2D other) {
		return new Vector2D(getX()-other.getX(), getY()-other.getY());
	}
	
	public double dot(Vector2D other) {
		return getX()*other.getX() + getY()*other.getY();
	}
	
	public Angle angleBetween(Vector2D other) {
		return Angle.acos(dot(other)/(magnitude()*other.magnitude()));
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Vector2D && super.equals(other);
	}

}
