package Sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;


/**
 *Old Implementation
 * @author kartik2vv
 */
public class HopcroftKarp {
    
public HashMap<Integer, ArrayList<Integer>> graph;
public ArrayList<Integer> distance = new ArrayList<Integer>();
public ArrayList<Integer> matching = new ArrayList<Integer>();        
Stack<Integer> stack = new Stack<Integer>();
public HashSet<Integer> B = new HashSet<Integer>();
public HashSet<Integer> maximal = new HashSet<Integer>();
Queue<Integer> Q = new LinkedList<Integer>();    


int n=9, m=9, first, S=-99, T=99, NIL=0, INF=999;


//from hopcroft's paper
public void MaximalPaths()  {
    stack.push(S);
    
    while(!stack.isEmpty()) {
        while(!graph.get(stack.peek()).isEmpty())   {
            first = graph.get(stack.peek()).get(0);
            if(!B.contains(first))  {
                stack.push(first);
                if(stack.peek() != T)   {
                    B.add(stack.peek());
                }
                else    {
                    while(stack.isEmpty())  {
                        maximal.add(stack.peek());
                        System.out.println("\t "+stack.pop());
                    }
                    stack.push(S);
                }
                }
            }
        stack.pop();
        }
    }

public boolean freeVertices_bfs()   {
    
    int u,v,len;
    
    for(int i=0;i<9;i++)    {
        if(matching.get(i)==NIL)    {
            distance.add(i, NIL);
            Q.add(i);
        }
        else    {
            distance.add(i, NIL);
        }
    }
    
    distance.add(NIL,INF);
    
    while(!Q.isEmpty()) {
        
        u=Q.poll();
        if(u!=NIL)  {
            len=graph.get(u).size();
            for(int i=0;i<len;i++)  {
                v=graph.get(u).get(i);
                if(distance.get(matching.get(v)).equals(INF))   {
                    distance.add(matching.get(v), distance.get(u)+1);
                    Q.add(matching.get(v));
                }
            }
        }
    }
    
    return(distance.get(NIL)!=INF);
}


    
}



