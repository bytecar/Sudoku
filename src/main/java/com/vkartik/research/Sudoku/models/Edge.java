package com.vkartik.research.Sudoku.models;

public class Edge implements Comparable<Edge> {

	Node from; 
	Node to;
	int weight;
	boolean used = false; // needed for RESYN
	boolean vital = false; // needed for RESYN
	boolean matched = false; // Used with matching

	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
		this.to = to;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isVital() {
		return vital;
	}

	public void setVital(boolean vital) {
		this.vital = vital;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public Edge(final Node argFrom, final Node argTo, final int argWeight) {
		from = argFrom;
		to = argTo;
		weight = argWeight;
	}

	public int compareTo(final Edge argEdge) {
		return weight - argEdge.weight;
	}
}