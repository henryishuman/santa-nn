package graphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Spritesheet {
	private HashMap<String, BufferedImage[]> animations;
	private String currentAnimationId;
	private int currentFrame;
	private int numSprites;
	private int numFrames;
	
	private long lastChange;
	private long delay;
	
	private int timesPlayed;
	
	private boolean loaded;
	
	public Spritesheet(String imagePath, int imageWidth, String[] spriteIds) {
		timesPlayed = 0;
		lastChange = System.currentTimeMillis();
		
		try {
			BufferedImage spritesheet = ImageIO.read(
				getClass().getResource(imagePath)
			);
			
			numSprites = spritesheet.getHeight() / imageWidth;
			numFrames = spritesheet.getWidth() / imageWidth;
			animations = new HashMap<String, BufferedImage[]>();
			currentAnimationId = spriteIds[0];
			
			for (int i = 0; i < numSprites; i++) {
				BufferedImage[] frames = new BufferedImage[numFrames];
				for (int j = 0; j < numFrames; j++) {
					BufferedImage frame = spritesheet.getSubimage(
						j * imageWidth, i * imageWidth,
						imageWidth, imageWidth
					);
					frames[j] = frame;
				}
				animations.put(spriteIds[i], frames);
			}
			
			loaded = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasLoaded() {
		return loaded;
	}
	
	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}
	
	public void changeAnimation(String id) {
		currentAnimationId = id;
	}
	
	public void setDelay(int i) { delay = i; }
	
	public void update() {
		if(delay == -1) return;
		
		long elapsed = System.currentTimeMillis() - lastChange;
		if(elapsed > delay) {
			currentFrame++;
			lastChange = System.currentTimeMillis();
		}
		
		if(currentFrame == numFrames) {
			currentFrame = 0;
			timesPlayed++;
		}
	}

	public BufferedImage getImage() { return animations.get(currentAnimationId)[currentFrame]; }
	public boolean hasPlayedOnce() { return timesPlayed > 0; }
	public boolean hasPlayed(int i) { return timesPlayed == i; }
}
