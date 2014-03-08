package com.vkartik.research.Sudoku.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.vkartik.research.Sudoku.models.Graph;
import com.vkartik.research.Sudoku.models.Edge;
import com.vkartik.research.Sudoku.models.Node;
//TODO:Fix the return output of this class.
public class StronglyConnectedComponents {

	private int index = 0;
	Graph graph;
	private ArrayList<Node> stack = new ArrayList<Node>();
	private ArrayList<ArrayList<Node>> SCC = new ArrayList<ArrayList<Node>>();

	//TODO: The return output needs to be list of sub-graphs.
	public StronglyConnectedComponents(Graph g) {
		this.graph = g;
	}

	/*
	 * The function tarjan has to be called for every unvisited node of the
	 * graph
	 */
	// adapted from:
	// http://algowiki.net/wiki/index.php?title=Tarjan%27s_algorithm
	public ArrayList<ArrayList<Node>> executeTarjan() {
		SCC.clear();
		index = 0;
		stack.clear();
		if (graph != null) {
			List<Node> nodeList = new ArrayList<Node>(graph.getSourceNodeSet());
			if (nodeList != null) {
				for (Node node : nodeList) {
					if (node.getIndex() == -1) {
						tarjan(node, graph);
					}
				}
			}
		}
		return SCC;
	}

	public ArrayList<ArrayList<Node>> tarjan(Node v, Graph list) {
		v.setIndex(index);
		v.setLowlink(index);
		index++;
		stack.add(0, v);
		for (Edge e : list.getAdjacent(v)) {
			Node n = e.getTo();
			if (n.getIndex() == -1) {
				tarjan(n, list);
				v.setLowlink(Math.min(v.getLowlink(), n.getLowlink()));
			} else if (stack.contains(n)) {
				v.setLowlink(Math.min(v.getLowlink(), n.getIndex()));
				;
			}
		}
		if (v.getLowlink() == v.getIndex()) {
			Node n;
			ArrayList<Node> component = new ArrayList<Node>();
			do {
				n = stack.remove(0);
				component.add(n);
			} while (n != v);
			SCC.add(component);
		}
		return SCC;
	}

}
