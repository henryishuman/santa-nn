package santann;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import neat.ConnectionGene;
import neat.Genome;
import neat.InnovationTable;
import neat.Species;

public class FitnessCalculator {
	public static ArrayList<Species> currentSpecies;

	public static ArrayList<Elf> cullUnfitElves(ArrayList<Elf> elves, double cullPercent) {
		currentSpecies = organiseElvesIntoSpecies(elves);
		for (Elf e : elves) {
			e.fitness = getElfFitness(e);
		}
				
		Collections.sort(elves, createFitnessComparator());
		for (int i = 0; i < elves.size() * cullPercent; i++) {
			Elf e = elves.get(0);
			Species s = e.getSpecies();
			s.removeMember(e);
			elves.remove(e);
		}
		
		return elves;
	}
	
	private static double getElfFitness(Elf elf) {
		double individualFitness = getIndividualFitness(elf);
		return individualFitness / elf.getSpecies().numMembers();
	}
	
	private static double getIndividualFitness(Elf elf) {
		double maxTime = (double) World.refreshTime;
		
		double onboardBonus = elf.getIsOnBoard() ? 5 : 0;
		double deliveryBonus = elf.getDeliveries() * 10;
		double stepBonus = elf.getStepsTaken() / maxTime;
		double presentBonus = elf.getTimeSpentHoldingPresent() / maxTime;
		return deliveryBonus + onboardBonus + stepBonus + presentBonus;
	}
	
	@SuppressWarnings( "unchecked" )
	public static ArrayList<Species> organiseElvesIntoSpecies(ArrayList<Elf> elves) {
		ArrayList<Species> species = new ArrayList<Species>();
		ArrayList<Elf> remainingElves = (ArrayList<Elf>) elves.clone();
		
		while (remainingElves.size() > 0) {
			Elf rep = remainingElves.get(0);
			remainingElves.remove(rep);
			
			Genome repG = rep.getGenome();
			Species s = new Species();
			s.addMember(rep);
			rep.setSpecies(s);
			
			for (int i = remainingElves.size() - 1; i >= 0; i--) {
				Elf other = remainingElves.get(i);
				Genome otherG = other.getGenome();
				double compatDistance = getCompatabilityDistance(repG, otherG);
				if (compatDistance <= 0.5) {
					s.addMember(other);
					other.setSpecies(s);
					remainingElves.remove(other);
				}
			}
			
			species.add(s);
		}
		
		return species;
	}
	
	private static double getCompatabilityDistance(Genome g1, Genome g2) {
		int maxInnovations = InnovationTable.getCurrentInnovationId();
		double numExcess = 0;
		double numDisjoint = 0;
		double sumWeightDifference = 0;
		double totalMatchingPairs = 0;
		double totalGenes = (int)Math.max(g1.getConnections().size(), g2.getConnections().size());
		double minGenes = (int)Math.min(g1.getConnections().size(), g2.getConnections().size());
		numExcess = totalGenes - minGenes;
		
		for (int i = 0; i < maxInnovations; i++) {
			ConnectionGene cg1 = g1.getGeneAtInnovation(i);
			ConnectionGene cg2 = g2.getGeneAtInnovation(i);
			
			if ((cg1 == null && cg2 != null) || (cg1 != null && cg2 == null)) {
				numDisjoint += 1;
			} else if (cg1 != null && cg2 != null) {
				sumWeightDifference += Math.abs(cg1.getWeight() - cg2.getWeight());
				totalMatchingPairs++;
			}
		}
		
		return (numExcess + numDisjoint) / totalGenes + (sumWeightDifference / totalMatchingPairs) * 0.4;
	}
	
	private static Comparator<Elf> createFitnessComparator() {
		return new Comparator<Elf>() {
			@Override
			public int compare(Elf e1, Elf e2) {
				double fit1 = e1.fitness;
				double fit2 =  e2.fitness;
				return Double.compare(fit1, fit2);
			}
		};
	}
}
