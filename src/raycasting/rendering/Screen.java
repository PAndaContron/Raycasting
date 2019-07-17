package raycasting.rendering;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Screen {
	private JFrame frame;
	private BufferedImage img;
	private int width;
	private int height;
	
	public Screen(int screenWidth, int screenHeight, String title) {
		width = screenWidth;
		height = screenHeight;
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		frame = new JFrame(title);
		frame.add(new JLabel(new ImageIcon(img)));

		frame.pack();
		frame.setResizable(false);
		// frame.setSize(WIDTH, HEIGHT);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
	
	public void draw() {
		frame.repaint();
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public JFrame getFrame() {
		return frame;
	}

}
