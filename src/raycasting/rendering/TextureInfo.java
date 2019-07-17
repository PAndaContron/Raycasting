package raycasting.rendering;

public class TextureInfo {
	private TexCoords vertex0, vertex1, vertex2;
	
	public TextureInfo(TexCoords v0, TexCoords v1, TexCoords v2) {
		vertex0 = v0;
		vertex1 = v1;
		vertex2 = v2;
	}
	
	public TexCoords getVertex(int v) {
		switch(v) {
			case 0: return vertex0;
			case 1: return vertex1;
			case 2: return vertex2;
		}
		
		throw new IllegalArgumentException("Cannot get vertex "+v+" of triangle");
	}
}
