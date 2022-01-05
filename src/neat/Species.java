package neat;

import java.util.ArrayList;

public class Species {
	private ArrayList<Object> members;
	
	public Species() {
		members = new ArrayList<Object>();
	}
	
	public void addMember(Object member) {
		members.add(member);
	}
	
	public void removeMember(Object member) {
		members.remove(member);
	}
	
	public Object getMember(int i) {
		return members.get(i);
	}
	
	public ArrayList<Object> getMembers() {
		return members;
	}
	
	public int numMembers() {
		return members.size();
	}
	
	public boolean isMember(Object o) {
		return members.contains(o);
	}
}
