package raycasting.math;

public class Vector3D extends Tuple3D {

	public Vector3D(double x, double y, double z) {
		super(x, y, z);
	}

	public Vector3D(Tuple3D other) {
		super(other);
	}
	
	public double magnitudeSqaured() {
		return dot(this);
	}
	
	public double magnitude() {
		return Math.sqrt(magnitudeSqaured());
	}
	
	public Vector3D normalize() {
		return scalarMultiply(1/magnitude());
	}
	
	public Vector3D negate() {
		return new Vector3D(-getX(), -getY(), -getZ());
	}
	
	public Vector3D scalarMultiply(double scalar) {
		return new Vector3D(getX()*scalar, getY()*scalar, getZ()*scalar);
	}
	
	public Vector3D add(Vector3D other) {
		return new Vector3D(getX()+other.getX(), getY()+other.getY(), getZ()+other.getZ());
	}
	
	public Vector3D sub(Vector3D other) {
		return new Vector3D(getX()-other.getX(), getY()-other.getY(), getZ()-other.getZ());
	}
	
	public double dot(Vector3D other) {
		return getX()*other.getX() + getY()*other.getY() + getZ()*other.getZ();
	}
	
	public Angle angleBetween(Vector3D other) {
		return Angle.acos(dot(other)/(magnitude()*other.magnitude()));
	}
	
	public Vector3D cross(Vector3D other) {
		return new Vector3D(getY()*other.getZ() - getZ()*other.getY(),
							getZ()*other.getX() - getX()*other.getZ(),
							getX()*other.getY() - getY()*other.getX());
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Vector3D && super.equals(other);
	}

}
