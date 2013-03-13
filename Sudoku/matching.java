package Sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author kartik
 * Working Maximum Bipartite Matching Algorithm
 * Courtesy: Hopcroft & Karp, fastest asymptotic algorithm O(n^2.5)
 */

public class matching {
	
	AdjacencyList bipartiteGraph = new AdjacencyList();
	ArrayList<ArrayList<Edge>> matching = new ArrayList<ArrayList<Edge>>();
	int level;
	int matching_size;
		
	
	int maxmatching()	{
				
		ArrayList<HashSet<Node>> layers = new ArrayList<HashSet<Node>>();
		ArrayList<ArrayList<Edge>> paths = new ArrayList<ArrayList<Edge>>();
		
		layers.clear();
		paths.clear();
		matching.clear();
		level=0;
		
		do {
			
		
		
		/*for(Edge E:bipartiteGraph.getAllEdges()){
			E.from.matched=false;
			E.to.matched = false;
		}*/
		//Construct a shortest paths DAG H from G and M
		layers = generateDAG(bipartiteGraph);
				
		//Construct a maximal set S of vertex-disjoint augmenting paths of length k.
		//Constructing maximal set of minimum length augmenting paths.
		paths = maximalPaths(layers,level);
						
		//Update matching
		matching = augment(paths, bipartiteGraph);
		
		if(layers.isEmpty() || paths.isEmpty() || matching.isEmpty()){
			return 0;
		}
		
		//Print Matching
			/*	for(ArrayList<Edge> N: matching){
					for(Edge n1: N){
						System.out.print(n1.to.name-10+" "+n1.from.name);
					}
					System.out.println();
				}*/
		
		}while(true);	
		//Assuming Every bipartite matching has a maximum matching size equal to one of its Left counterparts.		
	}
	
	ArrayList< HashSet<Node>> generateDAG(AdjacencyList graph)	{
		
		//prettyPrintBipartiteGraph(graph);
		ArrayList< HashSet<Node>> layers = new ArrayList<HashSet<Node>>();
		HashSet<Node> classified = new HashSet<Node>();
		HashSet<Node> allVertices = new HashSet<Node>();
		allVertices.addAll(graph.left);
		allVertices.addAll(graph.right);
		int i=0,k=0;
		while(true){
		
		for(int j=0;j<(graph.left.size() * graph.right.size());j++)	{
				layers.add(j, new HashSet<Node>());
		}
			
		layers.add(0, graph.left);
		HashSet<Node> tmp = (HashSet<Node>) layers.get(i).clone();
		
		if(graph.left.isEmpty()){
			return layers;
		}
		for(Node V : layers.get(i))	{		
			
			if(V.matched) 	{
				tmp.remove(V);
			}
		}
		layers.remove(i);
		layers.add(i, tmp);
		
		for(Node N:layers.get(i))	{
			
			List<Edge> E = graph.getAdjacent(N);
			for(Edge e1:E){
				if(!e1.matched)	{
					layers.get(i+1).add(e1.to);
					e1.to.indegree = e1.to.indegree + 1;
					e1.from.indegree = e1.from.indegree + 1;
				}
			}			
		}
		
		tmp = (HashSet<Node>) layers.get(i+1).clone();
		
		boolean status = checkFreeVertex(layers.get(i+1));
		
		if(status){ 
		//TODO remove repeated visiting of same set of nodes by using visited flag
		for(Node V : layers.get(i+1))	{		
				
			if(V.matched) 	{
				tmp.remove(V);
			}
		}
		
		layers.remove(i+1);
		layers.add(i+1, tmp);
		
		if(tmp.isEmpty())
			return layers;
		
		k=i+1;
		level = k;
		return layers;
		}		
		else	{
						
			
			ArrayList<Node> additions = new ArrayList<Node>();
			for(Node N1:layers.get(i+1))	{
				for(Edge E1 : graph.getAdjacent(N1))	{
					if(E1.matched){
						additions.add(E1.to);
					}
				}
			}
			
			
			layers.get(i+1).addAll(additions);
			
			if(layers.get(i+1).isEmpty())
				return layers;
			i = i+2;
			level=level+2;
		}
		
		
				
		for(HashSet<Node> V:layers){
			classified.addAll(V);
		}				
		}
		
	}
	
	
	private void prettyPrintBipartiteGraph(AdjacencyList graph) {
		
		for(Node N:graph.left){
			
			System.out.print("\n"+(N.name-10)+" {");
			for(Edge e:graph.getAdjacent(N))	{
				System.out.print(e.to.name+" ");								
			}
			System.out.println("}");
		}
		
	}

	ArrayList<ArrayList<Edge>> maximalPaths(ArrayList<HashSet<Node>> layers, int level)	{
		
		ArrayList<ArrayList<Edge>> paths = new ArrayList<ArrayList<Edge>>();
		int i=0;

		Iterator<Node> it = layers.get(level).iterator();
		
		while(it.hasNext())	{
			Node V=it.next();
			it.remove();
			Edge Ebck = null;
			for(Edge E:bipartiteGraph.getAdjacent(V))	{
				
				Ebck=E;
				//TODO delete Node V only if it is added in paths else not!
				if(E.to.existsIn(E.to, layers.get(level-1)))	{
					if(!E.matched)	{
						ArrayList<Edge> tmp = new ArrayList<Edge>();
						tmp.add(E);
						paths.add(tmp);
						V.indegree--;
						E.to.indegree--;
						layers.get(level-1).remove(E.to);
						if(level>1)
							maximalPaths(layers,level-1);
						break;
					}
				}
				
			}
			if(Ebck.matched)	{
				layers.get(level).add(Ebck.from);
			}
		}
		
	return paths;	
	}
	
	
	ArrayList<ArrayList<Edge>> augment(ArrayList<ArrayList<Edge>> paths, AdjacencyList graph)	{
		
		ArrayList<Edge> commonEdges = collectCommonEdges(paths, matching);
		ArrayList<ArrayList<Edge>> match = new ArrayList<ArrayList<Edge>>();
		
		
		for(ArrayList<Edge> E1:paths)	{
			ArrayList<Edge> tmp = new ArrayList<Edge>();
			
			for(Edge E:E1)	{
				if(commonEdges.contains(E))	{
					paths.remove(E);
					E.matched = false;
					E.to.matched = false;
					E.from.matched = false;
					markReverseEdges(graph,false);
				}
				else	{					
					tmp.add(E);
					E.to.matched=true;
					E.matched=true;
					E.from.matched=true;
					markReverseEdges(graph,true);
				}
			}
			match.add(tmp);
			
			
			
			
		}
		
		for(Edge Ek:graph.matched){
			ArrayList<Edge> EM = new ArrayList<Edge>();	
			EM.add(Ek);
			match.add(EM);
			
		}
		
		return match;
	}
	
	private void markReverseEdges(AdjacencyList graph, boolean flag) {
		
	for(Edge e:graph.getAllEdges())	{
		if(e.to.matched && e.from.matched)
			e.matched=flag;
	}	
	}

	int min(int a, int b)	{
		return(a>b?b:a);
	}
	
	
	ArrayList<Edge> collectCommonEdges(ArrayList<ArrayList<Edge>> paths, ArrayList<ArrayList<Edge>> matching)	{
		
		ArrayList<Edge> commonEdges = new ArrayList<Edge>();
		
		for(ArrayList<Edge> E1: paths){
			for(Edge E: E1){
				if(matching.contains(E))	{
					commonEdges.add(E);
				}
			}
		}
		
		return commonEdges;
	}
	
	boolean checkFreeVertex(HashSet<Node> layer)	{
		
		for(Node N:layer){
			if(!N.matched)
				return true;
		}
		
		return false;
	}
/*	public static void main(String[] args){
		
		
		//Example, Constraint of difference from Regin's paper
		
		AdjacencyList graph = new AdjacencyList();
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Node n6 = new Node(6);
		Node n7 = new Node(7);
		Node n8 = new Node(8);
		Node n9 = new Node(9);
		Node n10 = new Node(10);
		Node n11 = new Node(11);
		Node n12 = new Node(12);
		Node n13 = new Node(13);
		
		graph.addEdge(n1, n7, 0);
		graph.addEdge(n7, n1, 0);
		graph.addEdge(n1, n8, 0);
		graph.addEdge(n8, n1, 0);
		graph.addEdge(n2, n8, 0);
		graph.addEdge(n8, n2, 0);
		graph.addEdge(n2, n9, 0);
		graph.addEdge(n9, n2, 0);		
		graph.addEdge(n3, n7, 0);
		graph.addEdge(n7, n3, 0);
		graph.addEdge(n3, n9, 0);
		graph.addEdge(n9, n3, 0);
		graph.addEdge(n4, n8, 0);
		graph.addEdge(n8, n4, 0);
		graph.addEdge(n4, n10, 0);
		graph.addEdge(n10, n4, 0);
		graph.addEdge(n5, n9, 0);
		graph.addEdge(n9, n5, 0);
		graph.addEdge(n5, n10, 0);
		graph.addEdge(n10, n5, 0);
		graph.addEdge(n5, n11, 0);
		graph.addEdge(n11, n5, 0);
		graph.addEdge(n5, n12, 0);
		graph.addEdge(n12, n5, 0);
		graph.addEdge(n6, n12, 0);
		graph.addEdge(n12, n6, 0);
		graph.addEdge(n6, n13, 0);
		graph.addEdge(n13, n6, 0);
		
		
		
		matching m1 = new matching();
		m1.bipartiteGraph = graph;
		m1.bipartiteGraph.left.add(n1);
		m1.bipartiteGraph.left.add(n2);
		m1.bipartiteGraph.left.add(n3);
		m1.bipartiteGraph.left.add(n4);
		m1.bipartiteGraph.left.add(n5);
		m1.bipartiteGraph.left.add(n6);
		m1.matching_size = m1.bipartiteGraph.left.size();
		
		m1.bipartiteGraph.right.add(n9);		
		m1.bipartiteGraph.right.add(n7);
		m1.bipartiteGraph.right.add(n8);
		m1.bipartiteGraph.right.add(n10);
		m1.bipartiteGraph.right.add(n11);
		m1.bipartiteGraph.right.add(n12);
		m1.bipartiteGraph.right.add(n13);
		
		m1.maxmatching();
		
		System.out.println("Done!");
		
	}*/
}