package neat;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import santann.EntityComparator;

public class GenomeRenderer {
	public static BufferedImage renderGenome(Genome genome, String filePath) {
		int nodeSize = 15;
		int nodeOffset = 30;
		
		int inputX = 10;
		int outputX = 400;
		
		int xOffset = 10;
		int yOffset = 30;
		
		HashMap<NodeGene, Point> nodeMap = new HashMap<NodeGene, Point>();
	    
	    ArrayList<NodeGene> inputNodes = genome.getNodesByType(NodeGeneType.INPUT);
	    ArrayList<NodeGene> hiddenNodes = genome.getNodesByType(NodeGeneType.HIDDEN);
	    hiddenNodes = (ArrayList<NodeGene>) hiddenNodes.stream().filter(hid -> hid.getDepth() >= 0).collect(Collectors.toList());
	    ArrayList<NodeGene> outputNodes = genome.getNodesByType(NodeGeneType.OUTPUT);
	    	    	    
	    int maxNumHiddenNodesAtAnyDepth = 0;
		
	    int[] totalHiddenAtDepth = new int[0];
	    if (hiddenNodes.size() > 0) {
		    Collections.sort(hiddenNodes, EntityComparator.createNodeDepthComparator());
		    int maxDepth = hiddenNodes.get(hiddenNodes.size() - 1).getDepth() + 1;
		    totalHiddenAtDepth = new int[maxDepth];
	    
		    for (NodeGene hid : hiddenNodes) {
		    	totalHiddenAtDepth[hid.getDepth()]++;
		    }
		    
		    maxNumHiddenNodesAtAnyDepth = Arrays.stream(totalHiddenAtDepth).max().getAsInt();
	    }
	    
	    int maxNodesAtAnyDepth = Math.max(maxNumHiddenNodesAtAnyDepth, Math.max(inputNodes.size(), outputNodes.size()));
	    if (hiddenNodes.size() > 0) {
		    Collections.sort(hiddenNodes, EntityComparator.createNodeDepthComparator());
		    int maxDepth = hiddenNodes.get(hiddenNodes.size() - 1).getDepth() + 1;
		    int[] numHiddenAtDepth = new int[maxDepth];
	    
		    for (NodeGene hid : hiddenNodes) {
		    	int posX = inputX + (outputX - inputX) / (maxDepth) * hid.getDepth();
		    	int centralizeOffset = (maxNodesAtAnyDepth - totalHiddenAtDepth[hid.getDepth()])/2;
		    	int posY = (numHiddenAtDepth[hid.getDepth()] + centralizeOffset) * nodeOffset;
		    	nodeMap.put(hid, new Point(posX, posY));
		    	numHiddenAtDepth[hid.getDepth()]++;
		    	
		    }
	    }
	    	    
	    int numInputs = 0;
	    int inputCentralizeOffset = (maxNodesAtAnyDepth - inputNodes.size())/2;
	    for (NodeGene in : inputNodes) {
	    	int posX = inputX;
	    	int posY = (numInputs + inputCentralizeOffset) * nodeOffset;
	    	nodeMap.put(in, new Point(posX, posY));
	    	numInputs++;
	    }
	    
	    int numOutputs = 0;
	    int outputCentralizeOffset = (maxNodesAtAnyDepth - outputNodes.size())/2;
	    for (NodeGene out : outputNodes) {
	    	int posX = outputX;
	    	int posY = (numOutputs + outputCentralizeOffset) * nodeOffset ;
	    	nodeMap.put(out, new Point(posX, posY));
	    	numOutputs++;
	    }
	    
	    int height = Math.max(maxNodesAtAnyDepth, Math.max(numInputs, numOutputs)) * nodeOffset + yOffset * 2;
	    int width = outputX + xOffset * 2;
		
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = bi.createGraphics();
	    
	    g.translate(xOffset, yOffset);
	    	    
	    ArrayList<NodeGene> nodes = genome.getNodes();
	    Stroke defaultStroke = g.getStroke();
	    
	    ArrayList<ConnectionGene> cons = genome.getConnections();
    	for (ConnectionGene con : cons) {
    		if (con.getEnabled()) {
	    		NodeGene in = genome.getNodeWithId(con.getInId());
	    		NodeGene out = genome.getNodeWithId(con.getOutId());
	    		
	    		if (in.getDepth() >= 0 && out.getDepth() >= 0) {
		    		double weight = con.getWeight();
		    		
		    		if (in.getType() == NodeGeneType.HIDDEN) {
		    			if (weight > 0) g.setColor(Color.BLUE);
		    			else g.setColor(Color.CYAN);
		    		} else {
		    			if (weight > 0) g.setColor(Color.GREEN);
		    			else g.setColor(Color.RED);
		    		}
		    		
		    		weight = Math.abs(weight);
		    		
		    		Point inP = nodeMap.get(in);
		    		Point outP = nodeMap.get(out);
		    		g.setStroke(new BasicStroke((int)(10 * weight / 2)));
		    		g.drawLine(inP.x, inP.y, outP.x, outP.y);
	    		}
    		}
    	}
    	
    	for (NodeGene node : nodes) {
    		if (node.getDepth() >= 0) {
		    	g.setStroke(defaultStroke);
		    	g.setStroke(new BasicStroke(2));
		    	g.setColor(Color.BLACK);
		    	Point p = nodeMap.get(node);
		    	
		    	if (node.getType() == NodeGeneType.INPUT) g.setColor(Color.GREEN);
		    	else if (node.getType() == NodeGeneType.HIDDEN) g.setColor(Color.GRAY);
		    	else g.setColor(Color.ORANGE);
		    	
		    	g.fillOval(p.x - nodeSize/2, p.y - nodeSize/2, nodeSize, nodeSize);
		    	
		    	g.setColor(Color.BLACK);
		    	g.drawOval(p.x - nodeSize/2, p.y - nodeSize/2, nodeSize, nodeSize);
    		}
	    }
    	
    	g.translate(-xOffset, -yOffset);
    		    
	    try {
			ImageIO.write(bi, "PNG", new File(filePath));
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return null;
	}
}
