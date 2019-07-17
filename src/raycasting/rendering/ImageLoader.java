package raycasting.rendering;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLoader {
	private static Map<String, BufferedImage> cache = new HashMap<>();
	
	public static BufferedImage loadImage(String name) throws IOException {
		if(cache.containsKey(name)) {
			return cache.get(name);
		}
		
		BufferedImage img = ImageIO.read(ImageLoader.class.getResourceAsStream("/textures/"+name));
		cache.put(name, img);
		return img;
	}
}
