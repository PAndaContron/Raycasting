package raycasting.rendering;

import raycasting.math.Angle;
import raycasting.math.Point3D;
import raycasting.math.Vector3D;

public class Triangle {
	private Point3D vertex0, vertex1, vertex2;
	
	public Triangle(Point3D v0, Point3D v1, Point3D v2) {
		vertex0 = v0;
		vertex1 = v1;
		vertex2 = v2;
	}
	
	public Point3D getVertex(int v) {
		switch(v) {
			case 0: return vertex0;
			case 1: return vertex1;
			case 2: return vertex2;
		}
		
		throw new IllegalArgumentException("Cannot get vertex "+v+" of triangle");
	}
	
	public Vector3D getSideVect(int v1, int v2) {
		return getVertex(v1).to(getVertex(v2));
	}
	
	public Angle getAngle(int v) {
		return getSideVect(v, (v+1) % 3).angleBetween(getSideVect(v, (v+2) % 3));
	}
	
	public Vector3D normal() {
		return getSideVect(0, 1).cross(getSideVect(0, 2)).normalize();
	}
	
	public Triangle translate(Vector3D vect) {
		return new Triangle(vertex0.add(vect), vertex1.add(vect), vertex2.add(vect));
	}
	
	public int getColor(Point3D point, Point3D lookOrigin, Vector3D lookRay) {
		return 0x00ff00;
	}
}
