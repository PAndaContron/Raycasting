package raycasting.rendering;

import raycasting.math.Matrix3D;
import raycasting.math.Point3D;
import raycasting.math.Vector3D;

public class TexturedTriangle extends Triangle {
	private TextureInfo texInfo;
	private Texture texture;
	
	private Matrix3D alignMatrix;
	private Matrix3D transformMatrix;

	public TexturedTriangle(Point3D v0, Point3D v1, Point3D v2, TextureInfo texInfo, Texture texture) {
		super(v0, v1, v2);
		this.texInfo = texInfo;
		this.texture = texture;
		setAligned();
	}

	public TexturedTriangle(Triangle tri, TextureInfo texInfo, Texture texture) {
		this(tri.getVertex(0), tri.getVertex(1), tri.getVertex(2), texInfo, texture);
	}
	
	@Override
	public TexturedTriangle translate(Vector3D vect) {
		return new TexturedTriangle(super.translate(vect), texInfo, texture);
	}
	
	@Override
	public int getColor(Point3D point, Point3D lookOrigin, Vector3D lookRay) {
		Vector3D vPoint = alignMatrix.mult(new Vector3D(point));
		vPoint = new Vector3D(vPoint.getX(), vPoint.getZ(), 1);
		vPoint = transformMatrix.mult(vPoint);
		return texture.sampleRGB(vPoint.getX(), vPoint.getY());
	}
	
	private void setAligned() {
		alignMatrix = RenderMath.getRotation(normal(), new Vector3D(0, 1, 0));
		
		Vector3D vv0 = alignMatrix.mult(new Vector3D(getVertex(0)));
		Vector3D vv1 = alignMatrix.mult(new Vector3D(getVertex(1)));
		Vector3D vv2 = alignMatrix.mult(new Vector3D(getVertex(2)));
		
		Matrix3D a = new Matrix3D(new double[][] {
			{vv0.getX(), vv1.getX(), vv2.getX()},
			{vv0.getZ(), vv1.getZ(), vv2.getZ()},
			{         1,          1,          1}
		});
		
		Matrix3D b = new Matrix3D(new double[][] {
			{texInfo.getVertex(0).getU(), texInfo.getVertex(1).getU(), texInfo.getVertex(2).getU()},
			{texInfo.getVertex(0).getV(), texInfo.getVertex(1).getV(), texInfo.getVertex(2).getV()},
			{                          1,                           1,                           1}
		});
		
		transformMatrix = b.mult(a.inverse());
	}

}
