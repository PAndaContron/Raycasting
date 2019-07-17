package raycasting.math;

public class Point3D extends Tuple3D {

	public Point3D(double x, double y, double z) {
		super(x, y, z);
	}

	public Point3D(Tuple3D other) {
		super(other);
	}
	
	public double distanceSquared(Point3D other) {
		double dx = getX()-other.getX(), dy = getY()-other.getY(), dz = getZ()-other.getZ();
		return dx*dx + dy*dy + dz*dz;
	}
	
	public double distance(Point3D other) {
		return Math.sqrt(distanceSquared(other));
	}
	
	public Point3D add(Vector3D vector) {
		return new Point3D(getX()+vector.getX(), getY()+vector.getY(), getZ()+vector.getZ());
	}
	
	public Point3D sub(Vector3D vector) {
		return new Point3D(getX()-vector.getX(), getY()-vector.getY(), getZ()-vector.getZ());
	}
	
	public Vector3D from(Point3D other) {
		return new Vector3D(getX()-other.getX(), getY()-other.getY(), getZ()-other.getZ());
	}
	
	public Vector3D to(Point3D other) {
		return other.from(this);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Point3D && super.equals(other);
	}

}
