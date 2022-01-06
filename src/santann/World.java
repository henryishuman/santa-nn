package santann;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import graphics.RepeatingPattern;
import neat.Genome;
import neat.GenomeRenderer;
import neat.InnovationTable;
import neat.Species;
import util.RandUtil;

public class World {
	private int x, y;
	private int width;
	private int height;
	
	private final int elfCount = 40;
	private final int homeCount = 5;
	
	private ArrayList<Entity> entities;
	private ArrayList<Entity> entityQueue;
	private ArrayList<Entity> entityRemovalQueue;
	
	private int generation;
	public static final long refreshTime = 1000;
	private long currentTick = 0;
	
	private String worldSaveDir;
	
	private RepeatingPattern floor;
	
	public int numPickups = 0;
	public int numDeliveries = 0;
	
	private BufferedImage fittestElfGenomeImg = null;
	
	public World(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		floor = new RepeatingPattern("/images/snow_floor.gif", 1, width, height);
		
		worldSaveDir = "santa-nn_" + getCurrentTimeStamp();
		File genomeDir = new File(worldSaveDir);
		if (!genomeDir.exists()) genomeDir.mkdir();
		
		InnovationTable.createInnovationTable();
		this.entities = new ArrayList<Entity>();
		this.entityQueue = new ArrayList<Entity>();
		this.entityRemovalQueue = new ArrayList<Entity>();
		addInitialElves();
		addOtherEntities();
	}
	
	private static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
	    return sdfDate.format(new Date());
	}
	
	public BufferedImage getFittestElfGenomeImg() {
		return fittestElfGenomeImg;
	}
	
	private void addInitialElves() {
		for (int e = 0; e < elfCount; e++) {
			int elfX = RandUtil.getInt(0, width);
			int elfY = RandUtil.getInt(0, height);
			this.entities.add(new Elf(elfX, elfY));
		}
	}
	
	private void addOtherEntities() {	
		for (int h = 0; h < homeCount; h++) {
			int xpos = RandUtil.getInt(0 + width/5,  width - width/5);
			int ypos = RandUtil.getInt(0 + height/5, height - height/5);
			this.entities.add(new Home(xpos, ypos));
		}
		
		for (int e = 0; e < elfCount; e++) {
			int xpos = RandUtil.getInt(0, width);
			int ypos = RandUtil.getInt(0, height);
			this.entities.add(new Present(xpos, ypos));
		}
		
		currentTick = 0;
		numPickups = 0;
		numDeliveries = 0;
	}
	
	public void queueEntity(Entity e) {
		this.entityQueue.add(e);
	}
	
	public void queueEntityForRemoval(Entity e) {
		this.entityRemovalQueue.add(e);
	}
	
	private void unqueueEntities() {
		this.entities.addAll(this.entityQueue);
		this.entities.removeAll(this.entityRemovalQueue);
		this.entityQueue.clear();
		this.entityRemovalQueue.clear();
	}
	
	public void reinitialiseWorld() {
		generation += 1;
		ArrayList<Elf> elves = getElves();
		ArrayList<Elf> fitElves = FitnessCalculator.cullUnfitElves(elves, 0.5);
		fittestElfGenomeImg = GenomeRenderer.renderGenome(fitElves.get(fitElves.size() - 1).getGenome(), worldSaveDir + "/gen_" + generation + ".png");
		ArrayList<Elf> offspring = new ArrayList<Elf>();
		while (offspring.size() + fitElves.size() < elfCount) {
			Elf p1 = fitElves.get(RandUtil.getInt(0, fitElves.size() - 1));
			ArrayList<Object> speciesMembers = p1.getSpecies().getMembers();
			Elf p2 = (Elf) speciesMembers.get(RandUtil.getInt(0, speciesMembers.size() - 1));
			if (p1 != null && p2 != null) {
				Genome g = null;
				if (p1.fitness > p2.fitness) {
					g = p1.getGenome().crossover(p2.getGenome(), false);
				} else if (p1.fitness < p2.fitness) {
					g = p2.getGenome().crossover(p1.getGenome(), false);
				} else {
					g = p1.getGenome().crossover(p2.getGenome(), true);
				}
				
				int elfX = RandUtil.getInt(0, width);
				int elfY = RandUtil.getInt(0, height);
				Elf baby = new Elf(elfX, elfY, g);
				baby.setSpecies(p1.getSpecies());
				p1.getSpecies().addMember(baby);
				offspring.add(baby);
			}
		}
		
		for (Elf e : fitElves) {
			if (!e.isOnBoard(this)) {
				int elfX = RandUtil.getInt(0, width);
				int elfY = RandUtil.getInt(0, height);
				e.setPosition(elfX, elfY);
			}
			e.resetFitnessStats();
		}
		
		this.entities = new ArrayList<Entity>();
		this.entityQueue = new ArrayList<Entity>();
		this.entityRemovalQueue = new ArrayList<Entity>();
		this.entities.addAll(fitElves);
		this.entities.addAll(offspring);
		
		addOtherEntities();
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public ArrayList<Entity> getEntities() { return entities; }
	public ArrayList<Elf> getElves() {
		ArrayList<Elf> elves = new ArrayList<Elf>();
		for (Entity e : entities) {
			if (e.getType() == EntityType.ELF) {
				elves.add((Elf)e);
			}
		}
		return elves;
	}
	
	public void update() {
		for (Entity e : entities) {
			e.update(this);
		}
		
		unqueueEntities();
		
		if (currentTick > refreshTime) {
			reinitialiseWorld();
		}
		currentTick++;
	}
	
	public void draw(Graphics g) {
		Collections.sort(entities, EntityComparator.createRenderDistanceComparator());
		
		g.translate(x, y);
		
		g.setColor(Color.MAGENTA);
		g.fillRect(0, 0, width, height);
		g.drawImage(floor.getRepeatingImage(), 0, 0, width, height, null);
		
		for (Entity e : entities) {
			e.draw(g);
			if (e.getType() == EntityType.ELF)
				drawElfName(g, (Elf)e);
		}

		g.translate(-x, -y);
		
		drawFittestGenomeImage(g, 10, 10, 260, 260);
		drawDebug(g, 10, 290);
	}
	
	private void drawElfName(Graphics g, Elf e) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Consolas", 1, 12));
		int textWidth = g.getFontMetrics().stringWidth(e.getName());
		g.drawString(e.getName(), (int)(e.getX() - textWidth/2), (int)(e.getY() - e.getSize()/1.5));
	}
	
	private void drawFittestGenomeImage(Graphics g, int x, int y, int width, int height) {	
		if (fittestElfGenomeImg != null) {
			g.drawImage(fittestElfGenomeImg, x, y, width, height, null);
		} else {
			g.setFont(new Font("Courier", 1, 10));
			g.setColor(Color.YELLOW);
			g.drawString("Genome not yet available.", x + 5, y + 10);
		}
		
		g.setColor(Color.WHITE);
		g.drawRect(x, y, width, height);
	}
	
	private void drawDebug(Graphics g, int x, int y) {
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Courier", 1, 20));
		g.drawString("Current gen: " + generation, x, y);
		g.drawString("Gen completion: " + ((currentTick*100)/refreshTime) + "%", x, y + 20);
		
		if (FitnessCalculator.currentSpecies != null) {
			List<Species> speciesWithMembers = FitnessCalculator.currentSpecies.stream().filter(spec -> spec.numMembers() > 0).collect(Collectors.toList());
			g.drawString("Num species: " + speciesWithMembers.size(), x, y + 40);	
		} else {
			g.drawString("Num species: n/a", x, y + 40);
		}
		
		g.drawString("Presents left: " + (elfCount - numDeliveries), x, y + 80);
		g.drawString("Pickups: " + numPickups, x, y + 100);
		g.drawString("Deliveries: " + numDeliveries, x, y + 120);
	}
}
