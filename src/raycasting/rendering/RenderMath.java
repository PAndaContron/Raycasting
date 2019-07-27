package raycasting.rendering;

import java.util.Map;

import raycasting.math.Angle;
import raycasting.math.Matrix3D;
import raycasting.math.Point2D;
import raycasting.math.Point3D;
import raycasting.math.Triangle2D;
import raycasting.math.Triangle2DPair;
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
	
	public static Point3D intersectRayPlane(Point3D origin, Vector3D ray, Vector3D normal, Point3D center) {
		ray = ray.normalize();
		double denom = ray.dot(normal);
		
		if(denom == 0) {
			return null;
		}
		
		double t = center.from(origin).dot(normal)/denom;
		if(t < 0) {
			return null;
		}
		
		return origin.add(ray.scalarMultiply(t));
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
	
	public static Triangle[] clip(Triangle t, Vector3D facing, Point3D origin) {
		double d = new Vector3D(origin).dot(facing);
		
		boolean above0 = abovePlane(facing, d, t.getVertex(0));
		boolean above1 = abovePlane(facing, d, t.getVertex(1));
		boolean above2 = abovePlane(facing, d, t.getVertex(2));
		
		int num = 0;
		if(above0) ++num;
		if(above1) ++num;
		if(above2) ++num;
		
		int v, v1, v2;
		Point3D p1, p2;
		
		switch(num) {
			case 0:
				return new Triangle[0];
			case 1:
				v = above0 ? 0 : above1 ? 1 : 2;
				v1 = (v+1)%3;
				v2 = (v+2)%3;
				p1 = intersectRayPlane(t.getVertex(v), t.getSideVect(v, v1), facing, origin);
				p2 = intersectRayPlane(t.getVertex(v), t.getSideVect(v, v2), facing, origin);
//				System.out.println(1);
				return new Triangle[] {new Triangle(t.getVertex(v), p1, p2)};
			case 2:
				v = above0 ? above1 ? 2 : 1 : 0;
				v1 = (v+1)%3;
				v2 = (v+2)%3;
				p1 = intersectRayPlane(t.getVertex(v), t.getSideVect(v, v1), facing, origin);
				p2 = intersectRayPlane(t.getVertex(v), t.getSideVect(v, v2), facing, origin);
//				System.out.println(2);
				return new Triangle[] {new Triangle(t.getVertex(v1), p2, p1), new Triangle(t.getVertex(v2), p2, t.getVertex(v1))};
			case 3:
				return new Triangle[] {t};
		}
		
		throw new IllegalStateException();
	}
	
	public static Vector3D project(Vector3D vect, Vector3D planeNormal) {
		return vect.sub(planeNormal.scalarMultiply(vect.dot(planeNormal)));
	}
	
	public static Triangle2D project(Triangle t, Point3D screenCenter, Vector3D facing, Point3D origin, Matrix3D projectionMatrix, Point2D projectedOrigin, int height) {
		Vector3D sv0 = project(screenCenter.to(t.getVertex(0)), facing);
		Vector3D sv1 = project(screenCenter.to(t.getVertex(1)), facing);
		Vector3D sv2 = project(screenCenter.to(t.getVertex(2)), facing);
		double d0 = t.getVertex(0).sub(sv0).distance(origin);
		double d1 = t.getVertex(1).sub(sv1).distance(origin);
		double d2 = t.getVertex(2).sub(sv2).distance(origin);
		sv0 = new Vector3D(screenCenter.add(sv0.scalarMultiply(1/((d0==0?EPSILON:d0)/NEAR))));
		sv1 = new Vector3D(screenCenter.add(sv1.scalarMultiply(1/((d1==0?EPSILON:d1)/NEAR))));
		sv2 = new Vector3D(screenCenter.add(sv2.scalarMultiply(1/((d2==0?EPSILON:d2)/NEAR))));
		sv0 = projectionMatrix.mult(sv0);
		sv1 = projectionMatrix.mult(sv1);
		sv2 = projectionMatrix.mult(sv2);
		Point2D pv0 = new Point2D(new Point2D(sv0.getX(), sv0.getY()).from(projectedOrigin));
		Point2D pv1 = new Point2D(new Point2D(sv1.getX(), sv1.getY()).from(projectedOrigin));
		Point2D pv2 = new Point2D(new Point2D(sv2.getX(), sv2.getY()).from(projectedOrigin));
		pv0 = new Point2D(pv0.getX(), height-pv0.getY());
		pv1 = new Point2D(pv1.getX(), height-pv1.getY());
		pv2 = new Point2D(pv2.getX(), height-pv2.getY());
		return new Triangle2D(pv0, pv1, pv2);
	}
	
	public static Triangle2DPair projectClip(Triangle t, Point3D screenCenter, Vector3D facing, Point3D origin, Matrix3D projectionMatrix, Point2D projectedOrigin, int height) {
		Triangle[] clipped = clip(t, facing, origin);
		Triangle2D t1 = null, t2 = null;
		
		switch(clipped.length) {
			case 2:
				t2 = project(clipped[1], screenCenter, facing, origin, projectionMatrix, projectedOrigin, height);
			case 1:
				t1 = project(clipped[0], screenCenter, facing, origin, projectionMatrix, projectedOrigin, height);
				break;
		}
		
		return new Triangle2DPair(t1, t2);
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
