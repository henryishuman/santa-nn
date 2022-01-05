package neat;

import java.util.ArrayList;

public class InnovationTable {
	private static InnovationTable instance;
	private static int currentInnovationId;
	private ArrayList<Innovation> innovations;
	
	public static void createInnovationTable() {
		instance = new InnovationTable();
	}
	
	private InnovationTable() {
		currentInnovationId = 0;
		innovations = new ArrayList<Innovation>();
	}
	
	public static int getCurrentInnovationId() {
		return currentInnovationId;
	}
	
	public static Integer getInnovationId(int inId, int outId) {
		Innovation n = findInnovation(inId, outId);
		return instance.innovations.indexOf(n);
	}
	
	public static boolean doesInnovationExist(int inId, int outId) {
		return findInnovation(inId, outId) != null;
	}
	
	public static boolean doesInnovationExist(ConnectionGene gene) {
		return findInnovation(gene.getInId(), gene.getOutId()) != null;
	}
	
	public static void addInnovation(int inId, int outId) {
		Innovation n = new Innovation(inId, outId);
		instance.innovations.add(n);
		currentInnovationId++;
	}
	
	public static Innovation findInnovation(int inId, int outId) {
		Innovation n = new Innovation(inId, outId);
		return instance.innovations.stream().filter(inno -> inno.equals(n)).findFirst().orElse(null);
	}
	
	public static Innovation findInnovation(int id) {
		return instance.innovations.get(id);
	}
}
