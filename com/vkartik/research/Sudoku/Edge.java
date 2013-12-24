package com.vkartik.research.Sudoku;

public class Edge implements Comparable<Edge> {
   
   final Node from, to;
   final int weight;
   boolean used=false;					//needed for RESYN
   boolean vital=false;					//needed for RESYN
   boolean matched = false;				//Used with matching 
   
   public Edge(final Node argFrom, final Node argTo, final int argWeight){
       from = argFrom;
       to = argTo;
       weight = argWeight;
   }
   
   public int compareTo(final Edge argEdge){
       return weight - argEdge.weight;
   }
}