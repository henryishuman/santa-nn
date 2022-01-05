package santann;

import graphics.Spritesheet;

public class Present extends Entity {
	
	private boolean isHeld;
	
	private String[] spritesheetIds = {
		"IDLE"
	};

	protected Present(int x, int y) {
		super(x, y, EntityType.PRESENT);
		size = 16;
		spritesheet = new Spritesheet("/images/present.gif", 8, spritesheetIds);
		spritesheet.setDelay(-1);
	}
	
	public boolean getIsHeld() {
		return isHeld;
	}
	
	public void setIsHeld(boolean isHeld) {
		this.isHeld = isHeld;
	}

	@Override
	public void update(World w) {
		spritesheet.update();
	}
}
