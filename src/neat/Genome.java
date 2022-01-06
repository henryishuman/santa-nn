package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import santann.EntityComparator;
import util.RandUtil;

public class Genome {
	private ArrayList<NodeGene> nodeGenes;
	private ArrayList<ConnectionGene> connectionGenes;
	private int numInputs;
	private int numOutputs;
	private boolean infiniteLoop;
	
	public Genome(int numInputs, int numOutputs) {
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		
		nodeGenes = new ArrayList<NodeGene>();
		connectionGenes = new ArrayList<ConnectionGene>();
		
		for (int i = 0; i < numInputs; i++) {
			NodeGene ng = new NodeGene(nodeGenes.size(), NodeGeneType.INPUT);
			ng.setDepth(0);
			nodeGenes.add(ng);
		}
		
		for (int i = 0; i < numOutputs; i++) {
			NodeGene ng = new NodeGene(nodeGenes.size(), NodeGeneType.OUTPUT);
			ng.setDepth(10000);
			nodeGenes.add(ng);
		}
		
		ArrayList<NodeGene> inputs = getNodesByType(NodeGeneType.INPUT);
		ArrayList<NodeGene> outputs = getNodesByType(NodeGeneType.OUTPUT);
		for (int i = 0; i < inputs.size(); i++) {
			for (int o = 0; o < outputs.size(); o++) {			
				ConnectionGene cg = new ConnectionGene(
					inputs.get(i).getId(),
					outputs.get(o).getId(),
					RandUtil.getDouble(-1, 1)
				);
				connectionGenes.add(cg);
			}
		}
	}
	
	public boolean hasInfiniteLoop() {
		return infiniteLoop;
	}
	
	public ArrayList<NodeGene> getNodes() {
		return this.nodeGenes;
	}
	
	public ArrayList<NodeGene> getNodesByType(NodeGeneType type) {
		return (ArrayList<NodeGene>) nodeGenes.stream().filter(node -> node.getType() == type)
                .collect(Collectors.toList());
	}
	
	public ArrayList<NodeGene> getNodesByTypes(NodeGeneType... types) {
		return (ArrayList<NodeGene>) nodeGenes.stream().filter(node -> isNodeGeneTypePresent(node.getType(), types))
                .collect(Collectors.toList());
	}
	
	public ArrayList<NodeGene> getNodesWithDepthGreaterThan(int depth) {
		return (ArrayList<NodeGene>) nodeGenes.stream().filter(node -> node.getDepth() > depth).collect(Collectors.toList());
	}
	
	private boolean isNodeGeneTypePresent(NodeGeneType type, NodeGeneType[] types) {
		for (int i = 0; i < types.length; i++) {
			if (types[i] == type) { 
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<ConnectionGene> getConnections() {
		return this.connectionGenes;
	}

	public void mutate() {
		for (NodeGene ng : getNodesByType(NodeGeneType.HIDDEN)) {
			ng.setDepth(-1);
		}	
		for (NodeGene ng : getNodesByType(NodeGeneType.INPUT)) {
			recalculateDepth(ng, new ArrayList<Integer>());
		}
		
		if (randChance(0.2)) {
			addNewNode();
		}
		if (randChance(0.2)) {
			addNewConnection();
		}
		if (randChance(0.2)) {
			updateWeightShift();
		}
		if (randChance(0.2)) {
			updateWeightRandom();
		}
		if (randChance(0.2)) {
			toggleConnection();
		}
		
		for (NodeGene ng : getNodesByType(NodeGeneType.INPUT)) {
			recalculateDepth(ng, new ArrayList<Integer>());
		}
	}
	
	private boolean randChance(double chance) {
		return RandUtil.getDouble(0, 1) < chance;
	}
	
	private void addNewNode() {
		if (connectionGenes.size() > 0) {
			ConnectionGene c = connectionGenes.get(RandUtil.getInt(0, connectionGenes.size()-1));
			c.disable();
			
			NodeGene newNode = new NodeGene(nodeGenes.size(), NodeGeneType.HIDDEN);
			
			ConnectionGene c1 = new ConnectionGene(c.getInId(), newNode.getId(), RandUtil.getDouble(-1, 1));
			ConnectionGene c2 = new ConnectionGene(newNode.getId(), c.getOutId(), RandUtil.getDouble(-1, 1));
			
			nodeGenes.add(newNode);
			connectionGenes.add(c1);
			connectionGenes.add(c2);
			
			recalculateDepth(newNode, new ArrayList<Integer>());
		}
	}
	
	private void recalculateDepth(NodeGene ng, ArrayList<Integer> visitedNodeIds) {
		if (!visitedNodeIds.contains(ng.getId())) {
			visitedNodeIds.add(ng.getId());
			
			if (ng.getType() == NodeGeneType.HIDDEN) {
				ArrayList<NodeGene> subNodes = getNodesLeadingTo(ng);
				int largestDepth = 0;
				for (NodeGene subNode : subNodes) {
					if (subNode.getDepth() > largestDepth) {
						largestDepth = subNode.getDepth();
					}
				}
				
				ng.setDepth(largestDepth + 1);
			}
			
			for (ConnectionGene cg : getConnectionsFromNode(ng)) {
				if (cg.getEnabled()) {
					NodeGene toNode = getNodeWithId(cg.getOutId());
					recalculateDepth(toNode, visitedNodeIds);
				} 
			}
		}
	}
	
	private ArrayList<NodeGene> getNodesLeadingTo(NodeGene ng) {
		ArrayList<NodeGene> nodeGenes = new ArrayList<NodeGene>();
		connectionGenes.forEach(conn -> {
			if (conn.getOutId() == ng.getId() && conn.getEnabled()) {
				nodeGenes.add(getNodeWithId(conn.getInId()));
			}
		});
		return nodeGenes;
	}
	
	private void addNewConnection() {
		ArrayList<NodeGene> inputs = getNodesByTypes(NodeGeneType.INPUT, NodeGeneType.HIDDEN);
		
		int iInput = RandUtil.getInt(0, inputs.size() - 1);
		NodeGene rInput = inputs.get(iInput);
		
		ArrayList<NodeGene> outputs = getNodesWithDepthGreaterThan(rInput.getDepth());
		outputs.remove(rInput);
		
		int iOutput = RandUtil.getInt(0, outputs.size() - 1);
		NodeGene rOutput = outputs.get(iOutput);
		
		ConnectionGene c = new ConnectionGene(rInput.getId(), rOutput.getId(), RandUtil.getDouble(-1, 1));		
		if (!connectionGenes.contains(c)) {
			connectionGenes.add(c);
			recalculateDepth(rInput, new ArrayList<Integer>());
		} 
		
	}
	
	private void updateWeightShift() {
		if (connectionGenes.size() > 0) {
			ConnectionGene gene = connectionGenes.get(RandUtil.getInt(0, connectionGenes.size()-1));
			double currentWeight = gene.getWeight();
			gene.setWeight(currentWeight * RandUtil.getDouble(0, 1));
		}
	}
	
	private void updateWeightRandom() {
		if (connectionGenes.size() > 0) {
			ConnectionGene gene = connectionGenes.get(RandUtil.getInt(0, connectionGenes.size()-1));
			gene.setWeight(RandUtil.getDouble(-1, 1));
		}
	}
	
	private void toggleConnection() {
		if (connectionGenes.size() > 0) {
			ConnectionGene gene = connectionGenes.get(RandUtil.getInt(0, connectionGenes.size()-1));
			gene.toggle();
			NodeGene node = getNodeWithId(gene.getOutId());
			recalculateDepth(node, new ArrayList<Integer>());
		}
	}
	
	public double[] calculateOutput(double[] inputs) {
		for (int i = 0; i < nodeGenes.size(); i++) {
			nodeGenes.get(i).setValue(0);
		}
		
		ArrayList<NodeGene> sensors = getNodesByTypes(NodeGeneType.INPUT);
		for (int i = 0; i < sensors.size(); i++) {
			NodeGene ng = sensors.get(i);
			ng.setValue(inputs[i]);
			ArrayList<ConnectionGene> conns = getConnectionsFromNode(ng);
			for (ConnectionGene cg : conns) {
				NodeGene toNode = getNodeWithId(cg.getOutId());
				toNode.incValue(OutputFunctions.sigmoid(ng.getValue()) * cg.getWeight());
			}
		}
		
		for (int i = 0; i <= getMaxDepth(); i++) {
			ArrayList<NodeGene> hiddenAtDepth = getNodesAtDepth(i);
			for (NodeGene ng : hiddenAtDepth) {
				ArrayList<ConnectionGene> conns = getConnectionsFromNode(ng);
				for (ConnectionGene cg : conns) {
					NodeGene toNode = getNodeWithId(cg.getOutId());
					toNode.incValue(OutputFunctions.sigmoid(ng.getValue()) * cg.getWeight());
				}
			}
		}
		
		ArrayList<NodeGene> outputNodes = getNodesByTypes(NodeGeneType.OUTPUT);
		double[] outputs = new double[outputNodes.size()];
		for (int i = 0; i < outputNodes.size(); i++) {
			outputs[i] = outputNodes.get(i).getValue();
		}
		
		return outputs;
	}
	
	private int getMaxDepth() {
		ArrayList<NodeGene> hidden = getNodesByTypes(NodeGeneType.HIDDEN);
		if (hidden.size() > 0) {
			Collections.sort(hidden, EntityComparator.createNodeDepthComparator());
			return hidden.get(hidden.size()-1).getDepth();
		}
		return 0;
	}
	
	public Genome crossover(Genome g, boolean equalFitness) {
		Genome offspring = new Genome(numInputs, numOutputs);
		int maxInnovations = InnovationTable.getCurrentInnovationId();
		ArrayList<NodeGene> offspringNodes = new ArrayList<NodeGene>();
		ArrayList<ConnectionGene> offspringConnections = new ArrayList<ConnectionGene>();
		
		for (int i = 0; i < maxInnovations; i++) {
			ConnectionGene p1 = this.getGeneAtInnovation(i);
			ConnectionGene p2 = g.getGeneAtInnovation(i);
			ConnectionGene oc = null;
			
			if (equalFitness) {
				oc = RandUtil.pickRandomOrNotNull(p1, p2);
			} else {
				oc = p1;
			}
			
			if (oc != null) {
				if (!offspring.connectionGenes.contains(oc))
					offspringConnections.add(oc.copy());
				
				NodeGene p1InNode = this.getNodeWithId(oc.getInId());
				NodeGene p2InNode = g.getNodeWithId(oc.getInId());
				NodeGene oInNode = RandUtil.pickRandomOrNotNull(p1InNode, p2InNode);
				if (!doesNodeExistInList(offspringNodes, oc.getInId()) && oInNode.getType() == NodeGeneType.HIDDEN) {
					NodeGene oInNodeCopy = oInNode.copy();
					oInNodeCopy.setDepth(oInNode.getDepth());
					offspringNodes.add(oInNodeCopy);
				}
				
				NodeGene p1OutNode = this.getNodeWithId(oc.getOutId());
				NodeGene p2OutNode = g.getNodeWithId(oc.getOutId());
				NodeGene oOutNode = RandUtil.pickRandomOrNotNull(p1OutNode, p2OutNode);
				if (!doesNodeExistInList(offspringNodes, oc.getOutId()) && oOutNode.getType() == NodeGeneType.HIDDEN) {
					NodeGene oOutNodeCopy = oOutNode.copy();
					oOutNodeCopy.setDepth(oOutNode.getDepth());
					offspringNodes.add(oOutNodeCopy);
				}
			}
		}
		
		offspring.nodeGenes.addAll(offspringNodes);
		offspring.connectionGenes.addAll(offspringConnections);
		offspring.mutate();
		
		return offspring;
	}
	
	public ConnectionGene getGeneAtInnovation(int innovationId) {
		Innovation n = InnovationTable.findInnovation(innovationId);
		ArrayList<ConnectionGene> conns = (ArrayList<ConnectionGene>) connectionGenes.stream().filter(conn -> n.getInId() == conn.getInId() && n.getOutId() == conn.getOutId())
                .collect(Collectors.toList());
		if (conns.size() > 0) {
			return conns.get(0);
		} else {
			return null;
		}
	}
	
	public NodeGene getNodeWithId(int nodeId) {
		ArrayList<NodeGene> nodes = (ArrayList<NodeGene>) nodeGenes.stream().filter(node -> node.getId() == nodeId)
                .collect(Collectors.toList());
		if (nodes.size() > 0) {
			return nodes.get(0);
		} else {
			return null;
		}
	}
	
	public ArrayList<NodeGene> getNodesAtDepth(int depth) {
		return (ArrayList<NodeGene>) nodeGenes.stream().filter(node -> node.getDepth() == depth)
                .collect(Collectors.toList());
	}
	
	private boolean doesNodeExistInList(ArrayList<NodeGene> nodes, int nodeId) {
		ArrayList<NodeGene> foundNodes = (ArrayList<NodeGene>) nodes.stream().filter(node -> node.getId() == nodeId)
                .collect(Collectors.toList());
		return foundNodes.size() > 0;
	}
	
	private ArrayList<ConnectionGene> getConnectionsFromNode(NodeGene ng) {
		return (ArrayList<ConnectionGene>) connectionGenes.stream().filter(conn -> conn.getInId() == ng.getId())
				.collect(Collectors.toList());
	}
	
	private ArrayList<ConnectionGene> getEnabledConnections() {
		return (ArrayList<ConnectionGene>) connectionGenes.stream().filter(conn -> conn.getEnabled()).collect(Collectors.toList());
	}
	
	private int largestHiddenLayerSize() {
		int largestSize = 0;
		for(int i = 0; i < getMaxDepth(); i++) {
			int layerSize = getNodesAtDepth(i).size();
			if (layerSize > largestSize) {
				largestSize = layerSize;
			}
		}
		
		return largestSize;
	}
	
	public double calculateComplexity() {
		int activeConnections = getEnabledConnections().size();
		int numHiddenNodes = getNodesByType(NodeGeneType.HIDDEN).size();
		int largestLayerSize = largestHiddenLayerSize();
		return ((activeConnections / (numHiddenNodes + 1)) + largestLayerSize) / 10.0;
	}
}
