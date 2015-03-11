package com.vkartik.research.Sudoku.models;

import java.util.HashSet;

public class Node implements Comparable<Node> {

	String name;
	boolean visited = false;
	int lowlink = -1; // used for Tarjan's algorithm
	int index = -1; // used for Tarjan's algorithm

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public int getLowlink() {
		return lowlink;
	}

	public void setLowlink(int lowlink) {
		this.lowlink = lowlink;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Node(final String argName) {
		name = argName;
	}

	public boolean existsIn(Node V, HashSet<Node> level) {
		for (Node N : level) {
			if (N == V) {
				return true;
			}
		}

		return false;
	}

	public int compareTo(final Node argNode) {
		return argNode == this ? 0 : -1;
	}
}