import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;


public class Hopcroft {
	
	 
    //public int[][] bipartiteGraph = new int[9][9];
    public HashMap<Node,HashSet<Edge>> bipartiteGraph;
    
    Stack<Node> stack = new Stack<Node>();
    HashMap<Node,ArrayList<Node>> List;
    ArrayList<Node> B = new ArrayList<Node>();
    ArrayList<Node> buffer;
    
    Node source;
    Node sink;
    
    public ArrayList<Node> Matching()   {
    	
    	stack.push(source);
    	while(!stack.isEmpty())	{
    		
    		while(! List.get(stack.peek()).isEmpty() )	{
    			
    				Node first = List.get(stack.peek()).get(0);
    				
    				if(!B.contains(first))	{
    					stack.push(first);
    					buffer = List.get(stack.peek());
    					buffer.remove(first);
    					
    					if(!stack.peek().equals(sink)){
    						B.add(stack.peek());    						
    					}
    					else	{
    							stack.clear();
    							
    							for(Node i : B)	{
    							System.out.println(i.name);
    							}
    							
    							stack.push(source);
    					}
    					
    				}
    				
    				stack.pop();   				
    		}
    		
    	}
    	
      return B;
}
    
}