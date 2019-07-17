package raycasting.math;

import raycasting.rendering.RenderMath;

public abstract class Tuple2D {
	private double x, y;
	
	public Tuple2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Tuple2D(Tuple2D other) {
		this(other.getX(), other.getY());
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Tuple2D))
			return false;
		
		Tuple2D otherT = (Tuple2D) other;
		return Math.abs(x - otherT.getX() + y - otherT.getY()) < RenderMath.EPSILON;
	}
}
