package main;


import javax.swing.JFrame;

import neat.Genome;
import neat.GenomeRenderer;
import neat.InnovationTable;

public class Program {
	public static void main(String[] args) {		
		//doTest();
		runSim();
	}
	
	public static void runSim() {
		JFrame window = new JFrame("SantaNEAT");
		window.setContentPane(new MainWindow());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
	
	public static void doTest() {
		InnovationTable.createInnovationTable();
		
		Genome g1 = new Genome(9, 6);
		Genome g2 = new Genome(9, 6);
		Genome child1 = null;
		Genome child2 = null;
		
		for (int i = 0; i < 600; i++) {
			if (i == 150) {
				System.out.println("");
			}
			child1 = g1.crossover(g2, true);
			child2 = g1.crossover(g2, true);
			
			g1 = child1;
			g2 = child2;
		}
		
		GenomeRenderer.renderGenome(g1, "parent1.png");
		GenomeRenderer.renderGenome(g2, "parent2.png");
	}
}
