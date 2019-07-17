package raycasting.rendering;

import java.util.Map;

import raycasting.math.Angle;
import raycasting.math.Matrix3D;
import raycasting.math.Point3D;
import raycasting.math.Vector3D;

public class RenderMath {
	public static final double EPSILON = 0.0000001;
	public static final double NEAR = 0.01;
	
	public static Point3D intersectRayTriangle(Point3D origin, Vector3D ray, Triangle tri) {
		Vector3D edge1 = tri.getSideVect(0, 1);
		Vector3D edge2 = tri.getSideVect(0, 2);
		Vector3D h = ray.cross(edge2);
		
		double a = edge1.dot(h);
		if(a > -EPSILON && a < EPSILON) {
			return null;
		}
		
		double f = 1/a;
		
		Vector3D s = origin.from(tri.getVertex(0));
		double u = f * s.dot(h);
		if(u < 0 || u > 1) {
			return null;
		}
		
		Vector3D q = s.cross(edge1);
		double v = f * ray.dot(q);
		if(v < 0 || u+v > 1) {
			return null;
		}
		
		double t = f * edge2.dot(q);
		if(t > EPSILON) {
			return origin.add(ray.scalarMultiply(t));
		}
		return null;
	}
	
	public static Vector3D[][] getRays(Point3D origin, Vector3D facing, Vector3D up, int width, int height, Angle fov) {
		Vector3D[][] rays = new Vector3D[width][height];
		
		double pixDist = (width/2)/fov.mult(0.5).tan();
		double factor = NEAR/pixDist;
		Vector3D right = up.cross(facing);
		Point3D center = origin.add(facing.scalarMultiply(NEAR));
		
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				Point3D pixel = center.sub(up.scalarMultiply((y - height/2) * factor)).add(right.scalarMultiply((x - width/2) * factor));
				rays[x][y] = pixel.from(origin).normalize();
			}
		}
		
		return rays;
	}
	
	public static Triangle getNearestHit(Point3D origin, Map<Triangle, Point3D> hits) {
		Triangle nearest = null;
		
		for(Map.Entry<Triangle, Point3D> entry : hits.entrySet()) {
			if(entry.getValue() == null) {
				continue;
			}
			
			if(nearest == null || entry.getValue().distanceSquared(origin) < hits.get(nearest).distanceSquared(origin)) {
				nearest = entry.getKey();
			}
		}
		
		return nearest;
	}
	
	public static boolean abovePlane(Vector3D normal, double d, Point3D point) {
		return normal.dot(new Vector3D(point)) > d;
	}
	
	public static boolean abovePlane(Vector3D normal, double d, Triangle tri) {
		return abovePlane(normal, d, tri.getVertex(0)) || abovePlane(normal, d, tri.getVertex(1)) || abovePlane(normal, d, tri.getVertex(2));
	}
	
	public static Vector3D project(Vector3D vect, Vector3D planeNormal) {
		return vect.sub(planeNormal.scalarMultiply(vect.dot(planeNormal)));
	}
	
	public static Matrix3D getRotation(Vector3D axis, Angle rotation) {
		Matrix3D w = new Matrix3D(new double[][] {
			{           0, -axis.getZ(),  axis.getY()},
			{ axis.getZ(),            0, -axis.getX()},
			{-axis.getY(),  axis.getX(),            0}
		});
		
		double sin1 = rotation.sin(), sin2 = rotation.mult(0.5).sin();
		
		return Matrix3D.IDENTITY.add(w.scalarMultiply(sin1)).add(w.mult(w).scalarMultiply(2*sin2*sin2));
	}
	
	public static Matrix3D getRotation(Vector3D start, Vector3D end) {
		Vector3D v = start.cross(end);
		double c = start.dot(end);
		Matrix3D w = new Matrix3D(new double[][] {
			{        0, -v.getZ(),  v.getY()},
			{ v.getZ(),         0, -v.getX()},
			{-v.getY(),  v.getX(),         0}
		});
		
		return Matrix3D.IDENTITY.add(w).add(w.mult(w).scalarMultiply(1/(1+c)));
	}
}
