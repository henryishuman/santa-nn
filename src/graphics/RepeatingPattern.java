package graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class RepeatingPattern {
	private BufferedImage baseImage;
	private BufferedImage repeatingImage;
	
	private boolean loaded;
	
	public RepeatingPattern(String imagePath, double imageScale, int widthToCover, int heightToCover) {		
		try {
			baseImage = ImageIO.read(
				getClass().getResource(imagePath)
			);
			
			repeatingImage = new BufferedImage(widthToCover, heightToCover, BufferedImage.TYPE_INT_ARGB);
			Graphics g = repeatingImage.getGraphics();
			
			int tileWidth = (int)Math.ceil(baseImage.getWidth() * imageScale);
			int tilesAcross = widthToCover / tileWidth + 1;
			int tilesDown = heightToCover / tileWidth + 1;
			for (int i = 0; i < tilesDown; i++) {
				for (int j = 0; j < tilesAcross; j++) {
					g.drawImage(baseImage, j*tileWidth, i*tileWidth, tileWidth, tileWidth, null);
				}
			}
			
			g.dispose();
			
			loaded = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasLoaded() {
		return loaded;
	}
	
	public BufferedImage getImage() {
		return baseImage;
	}

	public BufferedImage getRepeatingImage() { 
		return repeatingImage;
	}
}
