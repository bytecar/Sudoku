package Sudoku;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

public class AdjacencyList {

  public  Map<Node, List<Edge>> adjacencies = new HashMap<Node, List<Edge>>();
   
   HashSet<Node> left = new HashSet<Node>(); //matching purpose
   ArrayList<Node> right = new ArrayList<Node>(); //matching purpose
   ArrayList<Edge> matched = new ArrayList<Edge>();
   
   //Unmatched edges from left to right, matched edges from left to right.
   
   public void addEdge(Node source, Node target, int weight){
       List<Edge> list;
       if(!adjacencies.containsKey(source)){
           list = new ArrayList<Edge>();
           adjacencies.put(source, list);
       }else{
           list = adjacencies.get(source);
       }
       list.add(new Edge(source, target, weight));
       
       }

   public List<Edge> getAdjacent(Node source){
       return adjacencies.get(source);
   }

   public void removeEdge(Edge e){
	   adjacencies.get(e.from).remove(e); 
   }
   
   public void removeVertex(Node N){
	   adjacencies.remove(N);
   }
   public void reverseEdge(Edge e){
       adjacencies.get(e.from).remove(e);
       addEdge(e.to, e.from, e.weight);
   }

   public void removeReverseEdge(Edge e){
	  
	   adjacencies.get(e.to).remove(e);
   }
   
   public Edge returnReverseEdge(Node n1, Node n2){
	   for(Edge E:adjacencies.get(n1))	{
		   if(E.to == n2){
			   return E;
		   }
	   }
	   return null;
   }
   public Edge findEdge(Node a, Node b)	{
	   for(Edge E:this.getAllEdges())	{
		   if(E.from==a && E.to==b){
			   return E;
		   }
	   }
	return null;
   }
	  
   public void reverseGraph(){
       adjacencies = getReversedList().adjacencies;
   }

   public AdjacencyList getReversedList(){
       AdjacencyList newlist = new AdjacencyList();
       for(List<Edge> edges : adjacencies.values()){
           for(Edge e : edges){
               newlist.addEdge(e.to, e.from, e.weight);
           }
       }
       return newlist;
   }

   public Set<Node> getSourceNodeSet(){
       return adjacencies.keySet();
   }

   public Collection<Edge> getAllEdges(){
       List<Edge> edges = new ArrayList<Edge>();
       for(List<Edge> e : adjacencies.values()){
           edges.addAll(e);
       }
       return edges;
   }
}