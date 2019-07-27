package raycasting.math;

public class Triangle2DPair {
	private Triangle2D tri1, tri2;
	
	public Triangle2DPair(Triangle2D tri1, Triangle2D tri2) {
		this.tri1 = tri1;
		this.tri2 = tri2;
//		if(tri1 != null && tri2 != null)
//			System.out.println("2 triangles in pair");
	}
	
	public boolean pointInside(Point2D p) {
		return (tri1 != null && tri1.pointInside(p)) || (tri2 != null && tri2.pointInside(p));
	}
}
