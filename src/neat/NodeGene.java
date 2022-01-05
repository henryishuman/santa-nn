package neat;

public class NodeGene {
	private int id;
	private NodeGeneType type;
	private double value;
	private int depth;
	
	public NodeGene(int id, NodeGeneType type) {
		this.id = id;
		this.type = type;
	}
	
	public int getId() {
		return id;		
	}
	
	public NodeGeneType getType() {
		return type;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public void incValue(double value) {
		this.value += value;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void incDepth() {
		this.depth ++;
	}
	
	public NodeGene copy() {
		NodeGene g = new NodeGene(id, type);
		return g;
	}
}
