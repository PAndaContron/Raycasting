package raycasting;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import raycasting.math.Angle;
import raycasting.math.Matrix3D;
import raycasting.math.Point2D;
import raycasting.math.Point3D;
import raycasting.math.Triangle2DPair;
import raycasting.math.Vector3D;
import raycasting.rendering.Object3D;
import raycasting.rendering.RenderMath;
import raycasting.rendering.Screen;
import raycasting.rendering.TexCoords;
import raycasting.rendering.Texture;
import raycasting.rendering.TextureInfo;
import raycasting.rendering.TexturedTriangle;
import raycasting.rendering.Triangle;

public class Main {
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	
	private static Point3D origin = new Point3D(1, 0, 0);
	private static Vector3D facing = new Vector3D(0, 0, 1);
	private static Vector3D up = new Vector3D(0, 1, 0);
	private static Angle fov = Angle.fromDeg(90);
	
	private static Point3D light = new Point3D(1, 1, 2);
	
	private static Cursor blankCursor;
	
	public static void main(String[] args) {
		Screen s = new Screen(WIDTH, HEIGHT, "Raycasting");
		BufferedImage img = s.getImage();
		
		JFrame f = s.getFrame();
		ControlListener listener = new ControlListener(f);
		f.addKeyListener(listener);
		f.addWindowFocusListener(listener);
		Timer timer = new Timer(100, listener);
		timer.start();
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		f.getContentPane().setCursor(blankCursor);
		
		s.setVisible(true);
		
		Point3D ftl = new Point3D(-0.5, +0.5, 3);
		Point3D ftr = new Point3D(+0.5, +0.5, 3);
		Point3D fbl = new Point3D(-0.5, -0.5, 3);
		Point3D fbr = new Point3D(+0.5, -0.5, 3);
		Point3D btl = new Point3D(-0.5, +0.5, 4);
		Point3D btr = new Point3D(+0.5, +0.5, 4);
		Point3D bbl = new Point3D(-0.5, -0.5, 4);
		Point3D bbr = new Point3D(+0.5, -0.5, 4);
		
		Point3D ptl = new Point3D(-20, -2, +20);
		Point3D ptr = new Point3D(+20, -2, +20);
		Point3D pbl = new Point3D(-20, -2, -20);
		Point3D pbr = new Point3D(+20, -2, -20);
		
		TexCoords tl = new TexCoords(0, 0);
		TexCoords tr = new TexCoords(1, 0);
		TexCoords bl = new TexCoords(0, 1);
		TexCoords br = new TexCoords(1, 1);
		
		TextureInfo top = new TextureInfo(tl, tr, bl);
		TextureInfo bottom = new TextureInfo(tr, br, bl);
		
		TexCoords tlp = new TexCoords(0, 0);
		TexCoords trp = new TexCoords(8, 0);
		TexCoords blp = new TexCoords(0, 8);
		TexCoords brp = new TexCoords(8, 8);
		
		TextureInfo pTop = new TextureInfo(tlp, trp, blp);
		TextureInfo pBottom = new TextureInfo(trp, brp, blp);
		
		Texture cube = new Texture("companionCube.png");
		Texture ground = new Texture("concrete.png");
		
		Object3D obj = new Object3D(
				new TexturedTriangle(ptl, ptr, pbl, pTop, ground),
				new TexturedTriangle(ptr, pbr, pbl, pBottom, ground),
				
				new TexturedTriangle(ftl, ftr, fbl, top, cube),
				new TexturedTriangle(ftr, fbr, fbl, bottom, cube),
				
				new TexturedTriangle(ftr, btr, fbr, top, cube),
				new TexturedTriangle(btr, bbr, fbr, bottom, cube),
				
				new TexturedTriangle(btr, btl, bbr, top, cube),
				new TexturedTriangle(btl, bbl, bbr, bottom, cube),
				
				new TexturedTriangle(btl, ftl, bbl, top, cube),
				new TexturedTriangle(ftl, fbl, bbl, bottom, cube),
				
				new TexturedTriangle(bbr, bbl, fbr, top, cube),
				new TexturedTriangle(bbl, fbl, fbr, bottom, cube),
				
				new TexturedTriangle(btl, btr, ftl, top, cube),
				new TexturedTriangle(btr, ftr, ftl, bottom, cube));
		
		Point3D tOrigin = null;
		Vector3D tFacing = null;
		Vector3D tUp = null;
		Vector3D tRight = null;
		Angle tFov = null;
		
		Vector3D[][] rays = null;
		double pixDist;
		Point3D screenCenter = null;
		
		Matrix3D projectionMatrix = null;
		Point2D projectedOrigin = null;
		
		Object3D tObj = null;
		Map<Triangle, Triangle2DPair> projection = null;
		Map<Triangle, Integer> quads = null;
		Set<Triangle> visibleTris = null;
		
		boolean tPaused = false;
		Point3D tLight = null;
		
		while(true) {
			long startTime = System.currentTimeMillis();
			
			for(int i=0; i<10; i++) {
				boolean lookChanged = !origin.equals(tOrigin) || !facing.equals(tFacing) || !up.equals(tUp) || !fov.equals(tFov);
				boolean geoChanged = !obj.equals(tObj);
				boolean trisChanged = lookChanged || geoChanged;
				boolean lightChanged = !light.equals(tLight);
				boolean screenChanged = trisChanged || tPaused != listener.isPaused() || lightChanged;
				
				if(lookChanged) {
					tOrigin = origin;
					tFacing = facing;
					tUp = up;
					tRight = tUp.cross(tFacing).normalize();
					tFov = fov;
					
					rays = RenderMath.getRays(tOrigin, tFacing, tUp, WIDTH, HEIGHT, tFov);
					pixDist = (WIDTH/2)/tFov.mult(0.5).tan();
					screenCenter = tOrigin.add(tFacing.scalarMultiply(RenderMath.NEAR));
					
					projectionMatrix = RenderMath.getRotation(tFacing, new Vector3D(0, 0, 1));
					projectionMatrix = RenderMath.getRotation(projectionMatrix.mult(tRight), new Vector3D(1, 0, 0)).mult(projectionMatrix);
					projectionMatrix = projectionMatrix.scalarMultiply(pixDist/RenderMath.NEAR);
					
					Vector3D vProjCenter = projectionMatrix.mult(new Vector3D(screenCenter));
					projectedOrigin = new Point2D(vProjCenter.getX(), vProjCenter.getY());
					projectedOrigin = new Point2D(vProjCenter.getX() - WIDTH/2, vProjCenter.getY() - HEIGHT/2);
				}
				
				if(trisChanged) {
					tObj = obj;
					projection = new HashMap<>();
					quads = new HashMap<>();
					visibleTris = new HashSet<>();
					
					Vector3D lnorm = RenderMath.project(tUp.cross(rays[0][HEIGHT/2]), tUp).normalize();
					Vector3D rnorm = RenderMath.project(rays[WIDTH-1][HEIGHT/2].cross(tUp), tUp).normalize();
					Vector3D unorm = RenderMath.project(tRight.cross(rays[WIDTH/2][0]), tRight).normalize();
					Vector3D dnorm = RenderMath.project(rays[WIDTH/2][HEIGHT-1].cross(tRight), tRight).normalize();
					
					Vector3D flnorm = RenderMath.project(rays[WIDTH/4][HEIGHT/2].cross(tUp), tUp).normalize();
					Vector3D frnorm = RenderMath.project(tUp.cross(rays[WIDTH*3/4][HEIGHT/2]), tUp).normalize();
					Vector3D funorm = RenderMath.project(rays[WIDTH/2][HEIGHT/4].cross(tRight), tRight).normalize();
					Vector3D fdnorm = RenderMath.project(tRight.cross(rays[WIDTH/2][HEIGHT*3/4]), tRight).normalize();
					
					Vector3D vOrigin = new Vector3D(tOrigin);
					double vd = tRight.dot(vOrigin);
					double hd = tUp.dot(vOrigin);
					
					double ld = lnorm.dot(vOrigin);
					double rd = rnorm.dot(vOrigin);
					double ud = unorm.dot(vOrigin);
					double dd = dnorm.dot(vOrigin);
					
					double fld = flnorm.dot(vOrigin);
					double frd = frnorm.dot(vOrigin);
					double fud = funorm.dot(vOrigin);
					double fdd = fdnorm.dot(vOrigin);
					
					for(Triangle t : obj) {
						Vector3D toOrigin = t.getVertex(0).to(origin).normalize();
						if(RenderMath.abovePlane(lnorm, ld, t) && RenderMath.abovePlane(rnorm, rd, t)
								&& RenderMath.abovePlane(unorm, ud, t) && RenderMath.abovePlane(dnorm, dd, t)
								&& toOrigin.dot(t.normal()) > 0) {
							visibleTris.add(t);
						}
					}
					
//					System.out.println(screenCenter);
//					System.out.println(projectedOrigin);
					
					for(Triangle t : visibleTris) {
						Triangle2DPair projected = RenderMath.projectClip(
								t, screenCenter, tFacing, tOrigin, projectionMatrix, projectedOrigin, HEIGHT);
						projection.put(t, projected);
						
						int quad = 0;
						
						boolean left = RenderMath.abovePlane(tRight.negate(), -vd, t);
						boolean right = RenderMath.abovePlane(tRight, vd, t);
						boolean up = RenderMath.abovePlane(tUp, hd, t);
						boolean down = RenderMath.abovePlane(tUp.negate(), -hd, t);
						
						boolean farLeft = RenderMath.abovePlane(flnorm, fld, t);
						boolean farRight = RenderMath.abovePlane(frnorm, frd, t);
						boolean farUp = RenderMath.abovePlane(funorm, fud, t);
						boolean farDown = RenderMath.abovePlane(fdnorm, fdd, t);
						
						boolean nearLeft = RenderMath.abovePlane(flnorm.negate(), -fld, t);
						boolean nearRight = RenderMath.abovePlane(frnorm.negate(), -frd, t);
						boolean nearUp = RenderMath.abovePlane(funorm.negate(), -fud, t);
						boolean nearDown = RenderMath.abovePlane(fdnorm.negate(), -fdd, t);
						
						if(farDown && farLeft) {
							quad = quad | (1 << 0);
						}
						if(farDown && left && nearLeft) {
							quad = quad | (1 << 1);
						}
						if(farDown && right && nearRight) {
							quad = quad | (1 << 2);
						}
						if(farDown && farRight) {
							quad = quad | (1 << 3);
						}
						if(down && nearDown && farLeft) {
							quad = quad | (1 << 4);
						}
						if(down && nearDown && left && nearLeft) {
							quad = quad | (1 << 5);
						}
						if(down && nearDown && right && nearRight) {
							quad = quad | (1 << 6);
						}
						if(down && nearDown && farRight) {
							quad = quad | (1 << 7);
						}
						if(up && nearUp && farLeft) {
							quad = quad | (1 << 8);
						}
						if(up && nearUp && left && nearLeft) {
							quad = quad | (1 << 9);
						}
						if(up && nearUp && right && nearRight) {
							quad = quad | (1 << 10);
						}
						if(up && nearUp && farRight) {
							quad = quad | (1 << 11);
						}
						if(farUp && farLeft) {
							quad = quad | (1 << 12);
						}
						if(farUp && left && nearLeft) {
							quad = quad | (1 << 13);
						}
						if(farUp && right && nearRight) {
							quad = quad | (1 << 14);
						}
						if(farUp && farRight) {
							quad = quad | (1 << 15);
						}
						
						quads.put(t, quad);
					}
				}
				
				if(screenChanged) {
					tPaused = listener.isPaused();
					tLight = light;
					
					for(int x=0; x<WIDTH; x++) {
						for(int y=0; y<HEIGHT; y++) {
							int quad = 1 << (((HEIGHT-y)/(HEIGHT/4) << 2) | (x/(WIDTH/4)));
							Point2D location = new Point2D(x, y);
							
							Map<Triangle, Point3D> hits = new HashMap<>();
							
							for(Triangle t : visibleTris) {
								if((quads.get(t) & quad) != 0 && projection.get(t).pointInside(location)) {
									hits.put(t, RenderMath.intersectRayTriangle(tOrigin, rays[x][y], t));
								}
							}
							
							Triangle nearest = RenderMath.getNearestHit(tOrigin, hits);
							if(nearest == null) {
								img.setRGB(x, y, tPaused ? 0x007f7f : 0x00ffff);
							} else {
								img.setRGB(x, y, nearest.getColor(hits.get(nearest), tOrigin, rays[x][y]));
								boolean hit = false;
								Vector3D lightray = hits.get(nearest).to(light).normalize();
								double lightDistSqr = hits.get(nearest).distanceSquared(light);
								for(Triangle t : obj) {
									Point3D p = RenderMath.intersectRayTriangle(hits.get(nearest), lightray, t);
									if(p != null && hits.get(nearest).distanceSquared(p) < lightDistSqr) {
										hit = true;
										img.setRGB(x, y, 0);
										break;
									}
								}
								
								if(!hit) {
									int rgb = nearest.getColor(hits.get(nearest), tOrigin, rays[x][y]);
									double cFactor = (nearest.normal().dot(lightray))/(1+lightDistSqr*0.01);
									if(tPaused) {
										cFactor *= 0.5;
									}
									int[] rgbArr = {(int)(((rgb >> 16) & 0xff)*cFactor),
											(int)(((rgb >> 8) & 0xff)*cFactor), (int)((rgb & 0xff)*cFactor)};
									rgb = (rgbArr[0] << 16) | (rgbArr[1] << 8) | rgbArr[2];
									img.setRGB(x, y, rgb);
								}
							}
						}
					}
				}
				
				s.draw();
			}
			
			long currTime = System.currentTimeMillis();
			if(currTime-startTime > 10) {
				System.out.println("FPS: "+(10000.0/(currTime-startTime)));
			}
		}
	}
	
	private static class ControlListener implements KeyListener, ActionListener, WindowFocusListener {
		private static final double SPEED = 0.1, MOUSE_SPEED = 0.1;
		private boolean w, a, s, d, q, z, n, m, paused;
		private JFrame frame;
		private Robot bot;
		
		public ControlListener(JFrame frame) {
			this.frame = frame;
			
			try {
				bot = new Robot();
			} catch(AWTException e) {}

			Point center = new Point(WIDTH/2, HEIGHT/2);
			SwingUtilities.convertPointToScreen(center, frame);
			bot.mouseMove(center.x, center.y);
		}
		
		public boolean isPaused() {
			return paused;
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					w = true;
					break;
				case KeyEvent.VK_A:
					a = true;
					break;
				case KeyEvent.VK_S:
					s = true;
					break;
				case KeyEvent.VK_D:
					d = true;
					break;
				case KeyEvent.VK_Q:
					q = true;
					break;
				case KeyEvent.VK_Z:
					z = true;
					break;
				case KeyEvent.VK_N:
					n = true;
					break;
				case KeyEvent.VK_M:
					m = true;
					break;
				case KeyEvent.VK_ESCAPE:
					paused = !paused;
					if(paused) {
						frame.getContentPane().setCursor(Cursor.getDefaultCursor());
					} else {
						frame.getContentPane().setCursor(blankCursor);
					}
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					w = false;
					break;
				case KeyEvent.VK_A:
					a = false;
					break;
				case KeyEvent.VK_S:
					s = false;
					break;
				case KeyEvent.VK_D:
					d = false;
					break;
				case KeyEvent.VK_Q:
					q = false;
					break;
				case KeyEvent.VK_Z:
					z = false;
					break;
				case KeyEvent.VK_N:
					n = false;
					break;
				case KeyEvent.VK_M:
					m = false;
					break;
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(paused) {
				return;
			}
			
			int x=0, y=0, z=0, f=0;
			if(this.w) z += 1;
			if(this.s) z -= 1;
			if(this.a) x -= 1;
			if(this.d) x += 1;
			if(this.q) y += 1;
			if(this.z) y -= 1;
			if(this.n) f -= 1;
			if(this.m) f += 1;
			
			double p=0, l=0;
			Point cursor = MouseInfo.getPointerInfo().getLocation();
			Point center = new Point(WIDTH/2, HEIGHT/2);
			SwingUtilities.convertPointToScreen(center, frame);
			int mx = cursor.x - center.x, my = cursor.y - center.y;
			l = mx*MOUSE_SPEED;
			p = my*MOUSE_SPEED;
			bot.mouseMove(center.x, center.y);
			
			fov = fov.add(Angle.fromDeg(f));
			
			Vector3D cUp = new Vector3D(0, 1, 0);
			Vector3D cFacing = RenderMath.project(facing, cUp).normalize();
			Vector3D cRight = cUp.cross(cFacing);
			
			origin = origin.add(cFacing.scalarMultiply(z*SPEED)).add(cUp.scalarMultiply(y*SPEED)).add(cRight.scalarMultiply(x*SPEED));
			
			Vector3D right = up.cross(facing);
			Matrix3D rotation = RenderMath.getRotation(right, Angle.fromDeg(p)).mult(RenderMath.getRotation(cUp, Angle.fromDeg(l)));
			up = rotation.mult(up);
			facing = rotation.mult(facing);
			
			if(up.getY() < 0) {
				Vector3D nUp = RenderMath.project(up, cUp).normalize();
				Matrix3D correction = RenderMath.getRotation(up, nUp);
				up = correction.mult(up);
				facing = correction.mult(facing);
			}
			
			right = up.cross(facing);
			if(Math.abs(right.getY() - 0) > RenderMath.EPSILON) {
				Vector3D nRight = RenderMath.project(right, cUp).normalize();
				Matrix3D correction = RenderMath.getRotation(right, nRight);
				up = correction.mult(up);
				facing = correction.mult(facing);
			}
		}

		@Override
		public void windowGainedFocus(WindowEvent e) {}

		@Override
		public void windowLostFocus(WindowEvent e) {
			paused = true;
			frame.getContentPane().setCursor(Cursor.getDefaultCursor());
		}
		
	}

}
