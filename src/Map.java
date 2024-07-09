import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Map {
	private boolean[][] map;
	Drone drone;
	Point drone_start_point;

	public Map(String path) {
		try {
			BufferedImage img_origin = ImageIO.read(new File(path));
			// Create a new BufferedImage with the new dimensions
			BufferedImage resizedImage = new BufferedImage(850, 480, BufferedImage.TYPE_INT_ARGB);


			resizedImageToSmaller(img_origin , resizedImage);
			this.map = render_map_from_image_to_boolean(resizedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void set_drone_start_point(Point drone_start_point){
		this.drone_start_point = drone_start_point;
	}

	private boolean[][] render_map_from_image_to_boolean(BufferedImage map_img) {
		int w = map_img.getWidth();
		int h = map_img.getHeight();
		boolean[][] map = new boolean[w][h];
		int runStart = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int clr = map_img.getRGB(x, y);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;

				if((red == 255 && green == 255 && blue == 255) && runStart == 0)
				{
					runStart = 1;
					this.drone_start_point = new Point(x, y);
				}

				map[x][y] = (red != 0 && green != 0 && blue != 0);  // Non-black considered passable
			}
		}
		return map;
	}

	public int[] startPos(){
		for (int y = 0; y < this.map.length; y++) {
			for (int x = 0; x < this.map[0].length; x++) {
				if(this.map[y][x]){return new int[]{x,y};}
			}
		}

		return null;
	}

	private void resizedImageToSmaller (BufferedImage img_map , BufferedImage resizedImage){
		// Get the Graphics2D object
		Graphics2D g2d = resizedImage.createGraphics();

		// Draw the original image to the new image with the new dimensions
		g2d.drawImage(img_map, 0,0 , 850,480, null);
		g2d.dispose();

		try {
			// Save the resized image to a new file
			ImageIO.write(resizedImage, "png", new File("resized_image_map.png"));
		}catch (Exception e){}
	}

	boolean isCollide(int x, int y) {
		if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) {
			return true;  // Out of bounds considered as a collision
		}
		boolean collision = !map[x][y];
		System.out.println("Checking collision at (" + x + ", " + y + "): " + collision);
		return collision;
	}

	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.GRAY);
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (!map[i][j]) {
					g.drawLine(i, j, i, j);
				}
			}
		}
		g.setColor(c);
	}

	public int startY (){
		return map[0].length;
	}

	public int startX (){
		return map.length;
	}

	public boolean getMapIJ(int i , int j) {
		return map[i][j];
	}

}
