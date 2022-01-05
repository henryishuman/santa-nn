package santann;

import java.util.ArrayList;
import java.util.Collections;

import graphics.Spritesheet;
import neat.Genome;
import neat.Species;
import util.NameGenerator;

public class Elf extends Entity {
	
	private Genome brain;
	private int deliveries;
	private boolean isHoldingPresent;
	private int stepsTaken;
	private int presentPickups;
	private int timeSpentHoldingPresent;
	
	private int homeContact;
	
	private boolean isOnBoard;
	
	public double fitness;
	
	private String name;
	private Species species;
	
	private int speed;
		
	private String[] spritesheetIds = {
		"IDLE", "WALKING", 
		"IDLE_HOLDING_PRESENT",
		"WALKING_HOLDING_PRESENT"
	};
	
	protected Elf(int x, int y) {
		super(x, y, EntityType.ELF);
		// inputs:
		//  distance + angle to nearest home
		//  distance + angle to nearest present
		//  isHoldingPresent
		//  distance to top, right, bottom, and left wall
		// outputs:
		//  rotate cw, rotate acw, move forward
		//  deliver present, pick up present
		brain = new Genome(7, 5);
		initialise();
	}
	
	protected Elf(int x, int y, Genome g) {
		super(x, y, EntityType.ELF);
		brain = g;
		initialise();
	}
	
	private void initialise() {
		size = 32;
		spritesheet = new Spritesheet("/images/elf.gif", 16, spritesheetIds);
		name = NameGenerator.generateForename();
		speed = 2;
	}
	
	public int getDeliveries() { return deliveries; }
	public boolean getIsHoldingPresent() { return isHoldingPresent; }
	public Genome getGenome() { return brain; }
	public int getStepsTaken() { return stepsTaken; }
	public void setPosition(int x, int y) { this.x = x; this.y = y; }
	public int getHomeContact() { return homeContact; }
	public boolean getIsOnBoard() { return isOnBoard; }
	public int getPresentPickups() { return presentPickups; }
	public int getTimeSpentHoldingPresent() { return timeSpentHoldingPresent; }
	public String getName() { return name; }
	public Species getSpecies() { return species; }
	public void setSpecies(Species s) { this.species = s; }
	
	public void resetFitnessStats() {
		deliveries = 0;
		isHoldingPresent = false;
		stepsTaken = 0;
		homeContact = 0;
		isOnBoard = true;
		presentPickups = 0;
		timeSpentHoldingPresent = 0;
				
		fitness = 0;
	}

	@Override
	public void update(World w) {
		Home nearestHome = null;
		Present nearestPresent = null;
		
		ArrayList<Entity> entities = getEntitiesInSight(w.getEntities(), 1200);		
		Collections.sort(entities, EntityComparator.createEntityDistanceComparator(this));
		for (Entity e : entities) {
			if (nearestHome == null && e.getType() == EntityType.HOME) {
				nearestHome = (Home) e;
			}
			if (nearestPresent == null && e.getType() == EntityType.PRESENT) {
				nearestPresent = (Present) e;
			}
			
			if (nearestHome != null && nearestPresent != null) {
				break;
			}
		}
		
		double[] inputs = new double[7];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = -1.0;
		}
		
		if (nearestHome != null) {
			inputs[0] = this.getDistance(nearestHome);
		}
		if (nearestPresent != null) {
			inputs[1] = this.getDistance(nearestPresent);
		}
		
		inputs[2] = isHoldingPresent ? 1 : -1;
		
		inputs[3] = y;
		inputs[4] = x;
		inputs[5] = w.getWidth() - x;
		inputs[6] = w.getHeight() - y;
		
		double[] outputs = brain.calculateOutput(inputs);
		double decisionCutoff = 0.8;
		double xStart = x;
		double yStart = y;
		
		if (outputs[0] > decisionCutoff) {
			this.direction += speed * 2;
		}
		if (outputs[1] > decisionCutoff) {
			this.direction -= speed * 2;
		}
		
		if (this.direction > 359) direction = 0;
		if (this.direction < 0) direction = 359;
		
		if (outputs[2] > decisionCutoff) {
			moveAlongAngle(direction, speed);
		}
		
		if (xStart != x) stepsTaken++;
		if (yStart != y) stepsTaken++;
		
		if (direction > 270 || direction < 90) facingLeft = false;
		else facingLeft = true;
		
		if (xStart != x || yStart != y) { 
			spritesheet.setDelay(150);
			if (isHoldingPresent) {
				spritesheet.changeAnimation("WALKING_HOLDING_PRESENT");
			} else {
				spritesheet.changeAnimation("WALKING");
			}
		} else {
			spritesheet.setDelay(500);
			if (isHoldingPresent) {
				spritesheet.changeAnimation("IDLE_HOLDING_PRESENT");
			} else {
				spritesheet.changeAnimation("IDLE");
			}
		}
		
		spritesheet.update();
		
		if (outputs[3] > decisionCutoff) {
			// Deliver present
			if (nearestHome != null && intersects(nearestHome) && isHoldingPresent) {
				isHoldingPresent = false;
				deliveries++;
				w.numDeliveries++;
			}
		}
		if (outputs[4] > decisionCutoff) {
			// Pick up present
			if (nearestPresent != null && intersects(nearestPresent)) {
				if (!isHoldingPresent && !nearestPresent.getIsHeld()) {
					w.queueEntityForRemoval(nearestPresent);
					nearestPresent.setIsHeld(true);
					isHoldingPresent = true;
					presentPickups++;
					w.numPickups++;
				}
			}
		}
		
		if (nearestHome != null) {
			if (intersects(nearestHome)) {
				homeContact ++;
			}
		}
		
		if (isHoldingPresent) {
			timeSpentHoldingPresent++;
		}
		
		isOnBoard = isOnBoard(w);
	}
}
