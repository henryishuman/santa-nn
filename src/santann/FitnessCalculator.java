package santann;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FitnessCalculator {
	public static ArrayList<Elf> cullUnfitElves(ArrayList<Elf> elves, double cullPercent) {
		Collections.sort(elves, createFitnessComparator());	
		for (int i = 0; i < elves.size() * cullPercent; i++) {
			Elf e = elves.get(0);
			elves.remove(e);
		}
		
		return elves;
	}
	
	public static ArrayList<Elf> removeAllElvesWithNoDeliveries(ArrayList<Elf> elves) {
		ArrayList<Elf> noDeliveries = new ArrayList<Elf>();
		for (Elf e : elves) {
			if (e.getDeliveries() == 0 && e.getPresentPickups() == 0)
				noDeliveries.add(e);
		}
		
		elves.removeAll(noDeliveries);
		return elves;
	}
	
	public static double getElfFitness(Elf elf) {
		double maxTime = (double) World.refreshTime;
		
		double onboardBonus = elf.getIsOnBoard() ? 5 : 0;
		double deliveryBonus = elf.getDeliveries() * 10;
		double stepBonus = elf.getStepsTaken() / maxTime;
		double presentBonus = elf.getTimeSpentHoldingPresent() / maxTime;
		return deliveryBonus + onboardBonus + stepBonus + presentBonus;
	}
	
	private static Comparator<Elf> createFitnessComparator() {
		return new Comparator<Elf>() {
			@Override
			public int compare(Elf e1, Elf e2) {
				double fit1 = getElfFitness(e1);
				double fit2 =  getElfFitness(e2);
				return Double.compare(fit1, fit2);
			}
		};
	}
}
