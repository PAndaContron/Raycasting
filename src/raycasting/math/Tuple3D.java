package raycasting.math;

import raycasting.rendering.RenderMath;

public abstract class Tuple3D {
	private double x, y, z;
	
	public Tuple3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Tuple3D(Tuple3D other) {
		this(other.getX(), other.getY(), other.getZ());
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	@Override
	public String toString() {
		return String.format("(%f, %f, %f)", x, y, z);
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Tuple3D))
			return false;
		
		Tuple3D otherT = (Tuple3D) other;
		return Math.abs(x - otherT.getX() + y - otherT.getY() + z - otherT.getZ()) < RenderMath.EPSILON;
	}
}
