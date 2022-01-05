package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import santann.World;

public class MainWindow extends JPanel implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;
	
	private static final int FACTOR = 80;
	public static final int WIDTH = 16 * FACTOR;
	public static final int HEIGHT = 9 * FACTOR;
	public static final int SCALE = 1;
	
	private Thread thread;
	private boolean running;
	private static final int FPS = 60;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private World w;
		
	public MainWindow() {
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
		
		int worldWidth = HEIGHT;
		int worldHeight = HEIGHT;
		w = new World(
			(WIDTH - worldWidth)/2, 
			(HEIGHT - worldHeight)/2,
			worldWidth,
			worldHeight);
	}
	
	public void run() {
		init();
		
		long start, elapsed, wait;
		
		while(running) {
			start = System.nanoTime();
			
			update();
			draw();
			drawToScreen();
			
			elapsed  = System.nanoTime() - start;
			wait = (1000 / FPS) - elapsed / 1000000;
			if(wait < 0) wait = 5;
			
			try {
				Thread.sleep(wait);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void update() {
		w.update();
	}
	
	private void draw() {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		w.draw(g);
	}
	
	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_ESCAPE) {
			System.exit(99);
		}
		if(k == KeyEvent.VK_R) {
			int worldWidth = HEIGHT;
			int worldHeight = HEIGHT;
			w = new World(
				(WIDTH - worldWidth)/2, 
				(HEIGHT - worldHeight)/2,
				worldWidth,
				worldHeight);
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
	}
}
