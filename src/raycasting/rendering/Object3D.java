package raycasting.rendering;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Object3D implements Iterable<Triangle> {
	private Set<Triangle> tris;
	
	public Object3D(Triangle... triangles) {
		tris = new HashSet<>();
		for(Triangle t : triangles) {
			tris.add(t);
		}
	}

	@Override
	public Iterator<Triangle> iterator() {
		return tris.iterator();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Object3D && ((Object3D) other).tris.equals(tris);
	}
}
