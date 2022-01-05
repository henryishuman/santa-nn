package neat;

public class Innovation {
	private int inId;
	private int outId;
	
	public Innovation(int inId, int outId) {
		this.inId = inId;
		this.outId = outId;
	}
	
	public int getInId() { return inId; }
	public int getOutId() { return outId; }
	
	@Override
    public boolean equals(Object o) {
        if (!(o instanceof Innovation)) {
            return false;
        }
        
        Innovation i = (Innovation) o;
        return inId == i.inId && outId == i.outId;
    }
}
