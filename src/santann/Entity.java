package santann;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;

import graphics.Spritesheet;

public abstract class Entity {
	protected Spritesheet spritesheet;
	protected boolean facingLeft;
	
	protected double x, y;
	protected int size;
	protected int direction;
	protected EntityType type;
		
	protected Entity(int x, int y, EntityType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public int getSize() { return size; }
	public EntityType getType() { return type; }
	
	public boolean intersects(Entity e) {
		return getRectangle().intersects(e.getRectangle());
	}
	
	public boolean intersects(Rectangle r) {
		return getRectangle().intersects(r);
	}
	
	private Rectangle getRectangle() {
		return new Rectangle(
			(int)(x - size/2), (int)(y - size/2), size, size
		);
	}
	
	public double getDistance(Entity e) {
		double dx = this.x - e.x;
		double dy = this.y - e.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public double getAngleBetween(Entity e) {
		double angle = Math.toDegrees(Math.atan2(e.y - y, e.x - x));
	    if(angle < 0) {
	        angle += 360;
	    }

	    return angle;
	}
	
	public boolean isOnBoard(World w) {
		return (x < w.getWidth() && x >= 0 &&
				y >= 0 && y < w.getHeight());
	}
	
	public ArrayList<Entity> getEntitiesInSight(ArrayList<Entity> entities, int sightLimit) {
		Line2D lineOfSight = getLineOfSight(sightLimit);
		return (ArrayList<Entity>) entities.stream()
				.filter(e -> (lineOfSight.intersects(e.getRectangle()) || this.intersects(e)) && e != this)
				.collect(Collectors.toList());
	}
	
	protected Line2D getLineOfSight(int sightLimit) {
		double xpos2 = (int)(x + (sightLimit * Math.cos(Math.toRadians(direction))));
		double ypos2 = (int)(y + (sightLimit * Math.sin(Math.toRadians(direction))));
		return new Line2D.Double(x, y, xpos2, ypos2);
	}
	
	public void moveAlongAngle(int angle, int speed) {
		x = x + Math.cos(Math.toRadians(angle)) * speed;
	    y = y + Math.sin(Math.toRadians(angle)) * speed;
	}
	
	public abstract void update(World w);
	
	public void draw(Graphics g) {
		if (spritesheet.hasLoaded()) {
			if (!facingLeft) {
				BufferedImage image = spritesheet.getImage();
				g.drawImage(image, (int)(x - size/2), (int)(y - size/2), size, size, null);
			} else {
				BufferedImage image = spritesheet.getImage();
				g.drawImage(image, (int)(x + size/2), (int)(y - size/2), -size, size, null);
			}
		}
	}
}
