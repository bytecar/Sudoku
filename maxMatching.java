
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author kartik
 */
public class maxMatching {
    
    //public int[][] bipartiteGraph = new int[9][9];
    public HashMap<Integer, HashSet<Integer>> bipartiteGraph = new HashMap<Integer, HashSet<Integer>>();
    public int[] matching = new int[20];
    public int[] freeVertex = new int[11];
    public int[] label = new int[11];
    public HashMap<Integer, HashSet<Integer>> auxGraph = new HashMap<Integer, HashSet<Integer>>();
    public Queue<Integer> Q = new LinkedList<Integer>();
    
    public int[] maximumMatching()   {
        
        for(int i=0;i<20;i++)    {
            matching[i]=999;
        }
        
        for(int v=0;v<9;v++)    {
            if(matching[v]==999)   {
                Q.add(v);
                label[v]=999;
            }
        }
        
        for(int i=0;i<9;i++)    {
            HashSet<Integer> tmp = new HashSet<Integer>();
            auxGraph.put(i, tmp);
        }
        
        while(!Q.isEmpty()) {
            
        for(int i=0;i<9;i++)
            freeVertex[i]=999;
        
        for(int v=0;v<9;v++)    {
                HashSet<Integer> tmp= auxGraph.get(v);
                        tmp.clear();
                        auxGraph.put(v,tmp);
        }
        
        for(int v=0;v<9;v++)    {
            for(int u:bipartiteGraph.get(v))   {
                if(matching[u]==999)  {
                    freeVertex[v]=u;
                }
                else    {
                    if(matching[u]!=v)   {
                        HashSet<Integer> tmp= auxGraph.get(v);
                        tmp.add(matching[u]);
                        auxGraph.put(v,tmp);
                    }
                }
            }
        }
        
        for(int v=0;v<9;v++)    {
            if(matching[v]==999)   {
                Q.add(v);
                label[v]=999;
            }
        }
        
        
            int vQ=Q.poll();
            
            if(freeVertex[vQ]!=999)   {
                augment(vQ);    
                continue;
            }
            else    {
                HashSet<Integer> tmp = auxGraph.get(vQ);
                for(int v1:tmp) {
                    if(label[v1]==999)    {
                    label[v1]=vQ;
                    Q.add(v1);
                    }
                }
                       
            }
        
        }  
        return matching;
    }
    

    public void augment(int v)  {
        
        if(label[v]==999) {
            matching[v]=freeVertex[v];
            matching[freeVertex[v]]=v;
        }
        else    {
            freeVertex[label[v]]=matching[v];
            matching[v]=freeVertex[v];
            matching[freeVertex[v]]=v;
           augment(label[v]);
        }
    }

}
 /*   public static void main(String[] args)  {
        //init Value Graph
        maxMatching vg = new maxMatching();
        
        
        
      
        
       
      for(int i=0;i<9;i++)    {
        
        HashSet<Integer> temp = new HashSet<Integer>();
        for(int j=1;j<=9;j++)    {
            temp.add(j+10);
        }
        vg.bipartiteGraph.put(i, temp);
        
                
       }
     
      /*ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(11);
        temp.add(12);
        temp.add(14);
        vg.bipartiteGraph.put(1, temp);
        temp = new ArrayList<Integer>();
        temp.add(12);
        temp.add(16);
        vg.bipartiteGraph.put(2, temp);
        temp = new ArrayList<Integer>();
        temp.add(12);
        temp.add(13);
        vg.bipartiteGraph.put(3, temp);
        temp = new ArrayList<Integer>();
        temp.add(13);
        temp.add(15);
        temp.add(16);
        vg.bipartiteGraph.put(4, temp);
        temp = new ArrayList<Integer>();
        temp.add(15);
        temp.add(13);
        temp.add(14);
        temp.add(16);
        vg.bipartiteGraph.put(5, temp);
        temp = new ArrayList<Integer>();
        temp.add(12);
        temp.add(15);
        vg.bipartiteGraph.put(6, temp);
        
        
        vg.maximumMatching();
    
            for(int i=0;i<9;i++)
            System.out.println("Matching: "+i+"\t"+vg.matching[i]);
    
    }
}
*/