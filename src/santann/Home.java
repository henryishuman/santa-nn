package santann;

import graphics.Spritesheet;
import util.RandUtil;

public class Home extends Entity {
	
	private String[] spritesheetIds = {
		"IDLE"
	};

	protected Home(int x, int y) {
		super(x, y, EntityType.HOME);
		size = 64;
		spritesheet = new Spritesheet("/images/home.gif", 32, spritesheetIds);
		spritesheet.setDelay(250);
		spritesheet.setCurrentFrame(RandUtil.getInt(0, 7));
	}

	@Override
	public void update(World w) {
		spritesheet.update();
	}
}
