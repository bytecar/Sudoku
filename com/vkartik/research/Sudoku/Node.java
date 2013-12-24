package com.vkartik.research.Sudoku;

import java.util.HashSet;

public class Node implements Comparable<Node> {
   
   final int name;
   boolean matched = false;
   int indegree = 0;			//Used for matching algorithm.
   boolean visited = false;   
   int lowlink = -1;          // used for Tarjan's algorithm
   int index = -1;            // used for Tarjan's algorithm
   
   public Node(final int argName) {
       name = argName;
   }
   public boolean existsIn(Node V,HashSet<Node> level)	{
	   for(Node N:level){
		   if(N==V)	{
			   return true;
		   }
	   }
	   
	   return false;
   }
	   
  
   public int compareTo(final Node argNode) {
       return argNode == this ? 0 : -1;
   }
}