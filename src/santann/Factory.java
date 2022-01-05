package santann;

import graphics.Spritesheet;

public class Factory extends Entity {

	private String[] spritesheetIds = {
		"IDLE"
	};
	
	protected Factory(int x, int y) {
		super(x, y, EntityType.FACTORY);
		size = 32;
		spritesheet = new Spritesheet("/images/factory.gif", size, spritesheetIds);
		spritesheet.setDelay(250);
	}

	@Override
	public void update(World w) {
		spritesheet.update();
	}
}
