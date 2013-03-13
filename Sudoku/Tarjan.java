package Sudoku;
import java.util.ArrayList;
import java.util.List;

public class Tarjan {
	
	 private int index = 0;
	   private ArrayList<Node> stack = new ArrayList<Node>();
	   private ArrayList<ArrayList<Node>> SCC = new ArrayList<ArrayList<Node>>();
	   
	  /* The function tarjan has to be called for every unvisited node of the graph */
	   //adapted from: http://algowiki.net/wiki/index.php?title=Tarjan%27s_algorithm
	  public ArrayList<ArrayList<Node>> executeTarjan(AdjacencyList graph){
	       SCC.clear();
	       index = 0;
	       stack.clear();
	       if(graph != null){
	          List<Node> nodeList = new ArrayList<Node>(graph.getSourceNodeSet());
	          if(nodeList != null)
	          {
	                for (Node node : nodeList)
	                {
	                    if(node.index == -1)
	                    {
	                        tarjan(node, graph);
	                    }
	                }
	           }
	       }
	     return SCC;
	  }
	  
	   public ArrayList<ArrayList<Node>> tarjan(Node v, AdjacencyList list){
	       v.index = index;
	       v.lowlink = index;
	       index++;
	       stack.add(0, v);
	       for(Edge e : list.getAdjacent(v)){
	           Node n = e.to;
	           if(n.index == -1){
	               tarjan(n, list);
	               v.lowlink = Math.min(v.lowlink, n.lowlink);
	           }else if(stack.contains(n)){
	               v.lowlink = Math.min(v.lowlink, n.index);
	           }
	       }
	       if(v.lowlink == v.index){
	           Node n;
	           ArrayList<Node> component = new ArrayList<Node>();
	           do{
	               n = stack.remove(0);
	               component.add(n);
	           }while(n != v);
	           SCC.add(component);
	       }
	       return SCC;
	   }
	   
}
