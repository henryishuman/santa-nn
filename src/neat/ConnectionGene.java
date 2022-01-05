package neat;

public class ConnectionGene {
	private int inId;
	private int outId;
	private double weight;
	private boolean enabled;
	
	public ConnectionGene(int inId, int outId, double weight) {
		if (!InnovationTable.doesInnovationExist(inId, outId))
			InnovationTable.addInnovation(inId, outId);
		this.inId = inId;
		this.outId = outId;
		this.weight = weight;
		this.enabled = true;
	}
	
	public int getInId() {
		return inId;
	}
	
	public int getOutId() {
		return outId;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void updateWeight(double change) {
		weight += change;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void toggle() {
		this.enabled = !this.enabled;
	}
	
	public ConnectionGene copy() {
		ConnectionGene g = new ConnectionGene(inId, outId, weight);
		g.enabled = this.enabled;
		return g;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof ConnectionGene)) return false;
		ConnectionGene g = (ConnectionGene) o;
		
		return inId == g.inId && outId == g.outId;
	}
}
