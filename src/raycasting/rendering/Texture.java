package raycasting.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Texture {
	private String imagePath;
	
	public Texture(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public int sampleRGB(TexCoords c) {
		return sampleRGB(c.getU(), c.getV());
	}
	
	public int sampleRGB(double u, double v) {
		u = wrap(u);
		v = wrap(v);
		
		BufferedImage img = null;
		try {
			img = ImageLoader.loadImage(imagePath);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		
		double tx = u*img.getWidth(), ty = v*img.getHeight();
		return img.getRGB((int) tx, (int) ty);
	}
	
	private double wrap(double x) {
		x %= 1;
		if(x < 0) {
			return x+1;
		}
		return x;
	}
	
//	private int colorBilerp(int c00, int c01, int c10, int c11, double x, double y) {
//		return arrayToCol(colorBilerp(colToArray(c00), colToArray(c01), colToArray(c10), colToArray(c11), x, y));
//	}
//	
//	private int[] colorBilerp(int[] c00, int[] c01, int[] c10, int[] c11, double x, double y) {
//		return new int[] {bilerp(c00[0], c01[0], c10[0], c11[0], x, y),
//				bilerp(c00[1], c01[1], c10[1], c11[1], x, y),
//				bilerp(c00[2], c01[2], c10[2], c11[2], x, y)};
//	}
//	
//	private int lerp(int v0, int v1, double x) {
//		return (int) ((1-x) * v0 + x * v1);
//	}
//	
//	private int bilerp(int v00, int v01, int v10, int v11, double x, double y) {
//		int v0 = lerp(v00, v01, x);
//		int v1 = lerp(v10, v11, x);
//		return lerp(v0, v1, y);
//	}
//	
//	private int[] colToArray(int col) {
//		return new int[] {(col >> 16) & 0xff, (col >> 8) & 0xff, col & 0xff};
//	}
//	
//	private int arrayToCol(int[] colArr) {
//		return (colArr[0] << 16) | (colArr[1] << 8) | colArr[2];
//	}
}
