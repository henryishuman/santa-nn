package santann;

import java.util.Comparator;

import neat.NodeGene;

public class EntityComparator {
	public static Comparator<Entity> createEntityDistanceComparator(Entity e) {
		return new Comparator<Entity>() {
			@Override
			public int compare(Entity e1, Entity e2) {
				double ds1 = e.getDistance(e1);
				double ds2 = e.getDistance(e2);
				return Double.compare(ds1, ds2);
			}
		};
	}
	
	public static Comparator<Entity> createRenderDistanceComparator() {
		return new Comparator<Entity>() {
			@Override
			public int compare(Entity e1, Entity e2) {
				double ds1 = e1.getY() + e1.getSize() / 2;
				double ds2 = e2.getY() + e2.getSize() / 2;
				return Double.compare(ds1, ds2);
			}
		};
	}
	
	public static Comparator<NodeGene> createNodeDepthComparator() {
		return new Comparator<NodeGene>() {
			@Override
			public int compare(NodeGene n1, NodeGene n2) {
				return Integer.compare(n1.getDepth(), n2.getDepth());
			}
		};
	}	
}
