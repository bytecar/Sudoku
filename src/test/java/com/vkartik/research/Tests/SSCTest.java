package com.vkartik.research.Tests;

import java.util.ArrayList;
import org.junit.Test;
import com.vkartik.research.Sudoku.algorithms.StronglyConnectedComponents;
import com.vkartik.research.Sudoku.models.Graph;
import com.vkartik.research.Sudoku.models.Node;

public class SSCTest {

	@Test
	public void SSC() {
		Graph g1 = new Graph();
		
		Node n1 = new Node("1");
		Node n2 = new Node("2");
		Node n3 = new Node("3");
		Node n4 = new Node("4");
		Node n5 = new Node("0");
		
		g1.addEdge(n2, n1, 0);
		g1.addEdge(n1, n5, 0);
		g1.addEdge(n5, n2, 0);
		g1.addEdge(n5, n3, 0);
		g1.addEdge(n3, n4, 0);
		
		StronglyConnectedComponents ssc = new StronglyConnectedComponents(g1);
		ArrayList<ArrayList<Node>> components =  ssc.executeTarjan();
		
		int i=1;
		for(ArrayList<Node> subgraph : components) {
			System.out.println("Graph "+i);
			for(Node n:subgraph) {
				System.out.println(n.getName());
			}
			i++;
		}
		
		
	}
}

