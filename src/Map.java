import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Map {
	private int[][] map;
	Drone drone;
	Point drone_start_point;

	public Map(String path, Point drone_start_point) {
		try {
			this.drone_start_point = drone_start_point;
			BufferedImage img_map = ImageIO.read(new File(path));
			this.map = render_map_from_image_to_height(img_map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int[][] render_map_from_image_to_height(BufferedImage map_img) {
		int w = map_img.getWidth();
		int h = map_img.getHeight();
		int[][] map = new int[w][h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int clr = map_img.getRGB(x, y);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;

				if (isWhite(red, green, blue)) {
					map[x][y] = 0; // White pixel represents ground level
				} else if (isGray(red, green, blue)) {
					map[x][y] = 3; // Gray pixel represents building, 3 meters high
				} else if (isBlack(red, green, blue)) {
					map[x][y] = -1; // Black pixel represents a wall
				}
			}
		}
		return map;
	}

	private boolean isWhite(int red, int green, int blue) {
		return red > 200 && green > 200 && blue > 200;
	}

	private boolean isGray(int red, int green, int blue) {
		return red > 100 && red < 200 &&
				green > 100 && green < 200 &&
				blue > 100 && blue < 200;
	}

	private boolean isBlack(int red, int green, int blue) {
		return red < 50 && green < 50 && blue < 50;
	}

	boolean isCollide(int x, int y) {
		return map[x][y] == -1;
	}

	public int getHeightAt(int x, int y) {
		if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) return -1; // Out of bounds
		return map[x][y];
	}

	public void paint(Graphics g) {
		Color c = g.getColor();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == 0) {
					g.setColor(Color.WHITE);
				} else if (map[i][j] == 3) {
					g.setColor(Color.GRAY);
				} else if (map[i][j] == -1) {
					g.setColor(Color.BLACK);
				}
				g.drawLine(i, j, i, j);
			}
		}
		g.setColor(c);
	}
}
