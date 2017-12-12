package com.vkartik.research.Sudoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vkartik.research.Sudoku.algorithms.StronglyConnectedComponents;
import com.vkartik.research.Sudoku.models.Edge;
import com.vkartik.research.Sudoku.models.Graph;
import com.vkartik.research.Sudoku.models.Node;
import com.vkartik.research.Sudoku.utils.timer;

public class Sudoku implements Runnable, ActionListener {

    class variable {
        int idx;
        HashSet<Integer> domain;
    }
    // Where final values must be assigned, call the updateVals function in the board class to update it to GUI
    int[][] vals = new int[9][9];
    int[][] boxes = new int[9][9];
    int[][] rows = new int[9][9];
    int[][] cols = new int[9][9];
    Board board = null;

	HashMap<Integer,Graph> gboxes = new HashMap<Integer,Graph>();
    HashMap<Integer,Graph> grows = new HashMap<Integer,Graph>();
    HashMap<Integer,Graph> gcols = new HashMap<Integer,Graph>();
    
    HashSet<Integer>[] globalDomains= new HashSet[81];
    HashSet<Integer>[] neighbors =  new HashSet[81];
    TreeSet<Arc> globalQueue  = new TreeSet<Arc>();
    ArrayList<variable> globalVar = new ArrayList<variable>();
    
    Queue<variable> Q = new LinkedList<variable>();
    Queue<Graph> allDiffs = new LinkedList<Graph>();
    Queue<Graph> allDiffsMatch = new LinkedList<Graph>();
    ArrayList< HashSet<Node>> layers = new ArrayList<HashSet<Node>>();
    ArrayList<ArrayList<Edge>> matchNew = new ArrayList<ArrayList<Edge>>();
    int level1=0;
    
    private void init_csp(HashSet<Integer>[] Domains, HashSet<Integer>[] neighbors, int[][] vals, Queue<variable> Q) {

        //init Sudoku Domains, vals for non-editable, 1-9 for editable.
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                Domains[i * 9 + j] = new HashSet<Integer>();

                if (vals[i][j] == 0) {
                    for (int k = 1; k <= 9; k++) {
                        Domains[i * 9 + j].add(k);
                    }

                } else {
                    Domains[i * 9 + j].add(vals[i][j]);
                }

            }
        }


        //init Sudoku boxes
        boxes[0] = new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20};
        //Arrays.sort(boxes[0]);
        boxes[1] = new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23};
        //Arrays.sort(boxes[1]);
        boxes[2] = new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26};
        //Arrays.sort(boxes[2]);
        boxes[3] = new int[]{27, 28, 29, 36, 37, 38, 45, 46, 47};
        //Arrays.sort(boxes[3]);
        boxes[4] = new int[]{30, 31, 32, 39, 40, 41, 48, 49, 50};
        //Arrays.sort(boxes[4]);
        boxes[5] = new int[]{33, 34, 35, 42, 43, 44, 51, 52, 53};
        //Arrays.sort(boxes[5]);
        boxes[6] = new int[]{54, 55, 56, 63, 64, 65, 72, 73, 74};
        //Arrays.sort(boxes[6]);
        boxes[7] = new int[]{57, 58, 59, 66, 67, 68, 75, 76, 77};
        //Arrays.sort(boxes[7]);
        boxes[8] = new int[]{60, 61, 62, 69, 70, 71, 78, 79, 80};
        //Arrays.sort(boxes[8]);

        //init Sudoku Rows
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                rows[i][j] = i * 9 + j;
            }
        }

        //init Sudoku Columns
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cols[i][j] = j * 9 + i;
            }
        }



        //init Sudoku Neighbors
        int res = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            	
                neighbors[i * 9 + j] = new HashSet<Integer>();
                res = lookup(rows[i][j]);
                for (int k = 0; k < 9; k++) {

                    if (res >= 0) {
                        neighbors[i * 9 + j].add(boxes[res][k]);
                    }
                    neighbors[i * 9 + j].add(rows[i][k]);
                    neighbors[i * 9 + j].add(cols[j][k]);
                }
                
                //The subtraction of additional added cells boxes intersection rows and columns.
                neighbors[i * 9 + j].remove(rows[i][j]);

               	addneighbors(i,j);                	
               
            }
        }
    }

//Update globalQueue with neighbors of i,j cell
    void addneighbors(int i, int j) {
        
        Iterator<Integer> itr = neighbors[i * 9 + j].iterator();
        while (itr.hasNext()) {
            int x2 = itr.next();
            Arc temp = new Arc(rows[i][j], x2);
            globalQueue.add(temp);
            temp = new Arc(x2, rows[i][j]);
            globalQueue.add(temp);
        }
    }

    //Add Neighbor constraints for a variable for SAC
    void addConsSAC(int var) {

        Iterator<Integer> itr = neighbors[var].iterator();
        while (itr.hasNext()) {
            int x2 = itr.next();
            Q.add(globalVar.get(x2));

        }
    }

    int lookup(int key) {

        for (int i = 0; i < 9; i++) {
            if (Arrays.binarySearch(boxes[i], key) >= 0) {
                return i;
            }
        }

        return -1;
    }

    private int AC3() {

        int r, c;
        while (!globalQueue.isEmpty()) {

            Arc binaryConstraint = globalQueue.pollFirst();
            HashSet<Integer> revised = new HashSet<Integer>();

            for (int i : globalDomains[binaryConstraint.Xi]) {

                Iterator<Integer> itr = globalDomains[binaryConstraint.Xj].iterator();

                while (itr.hasNext()) {
                    int j = itr.next();
                    if (i != j) {
                        revised.add(i);
                        break;
                    }
                }

            }

            if (revised.isEmpty()) {
                return 0;
            } else if ((revised.size() != globalDomains[binaryConstraint.Xi].size())) {

                globalDomains[binaryConstraint.Xi] = revised;
                
                r = (int) Math.ceil(binaryConstraint.Xi / 9);
                c = (binaryConstraint.Xi) % 9;
                addneighbors(r, c);
            }
        }

        return 1;
    }

    HashSet<Integer> set_diff(HashSet<Integer> list1, HashSet<Integer> list2) {

        HashSet<Integer> setdiff = new HashSet<Integer>();

        if (list1 == null && list2 == null) {

            return null;
        } else if ((list1 == null) && list2 != null) {

            return null;
        } else if ((list2 == null) && list1 != null) {

            for (Integer j : list1) {
                if (setdiff.contains(j) == false) {
                    setdiff.add(j);
                }
            }
            return (setdiff);
        } else {

            for (Integer j : list1) {
                if (setdiff.contains(j) == false) {
                    setdiff.add(j);
                }
            }

            for (Integer i : list2) {
                if (setdiff.contains(i) == true) {
                    setdiff.remove(i);
                }
            }
            return (setdiff);
        }
    }

    private boolean SAC() {


        timer time= new timer();
        
        time.Start();
        
        init_csp(globalDomains, neighbors, vals, Q);
        AC3();

        HashSet<Integer>[] globalBackup = new HashSet[81];
        globalBackup = (HashSet<Integer>[]) globalDomains.clone();

        //Init Variables and Queue
        for (int i = 0; i < 81; i++) {
            variable tmp = new variable();
            tmp.domain = globalDomains[i];
            tmp.idx = i;
            globalVar.add(tmp);
            Q.add(tmp);
        }
        
        //Init Global queue
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            	addneighbors(i,j);
            }
        }
        
        TreeSet<Arc> Qbackup = new TreeSet<Arc>();
        Qbackup=(TreeSet<Arc>) globalQueue.clone();
        
        while (!Q.isEmpty()) {

            variable var;

            HashSet<Integer> domain = new HashSet<Integer>();
            HashSet<Integer> vi = new HashSet<Integer>();
            var = Q.poll();
            domain.addAll(var.domain);

            Iterator<Integer> p = domain.iterator();
            while (p.hasNext()) {
                int k = p.next();
                vi.clear();
                vi.add(k);

                globalDomains[var.idx] = vi;
                globalQueue=(TreeSet<Arc>) Qbackup.clone();
                
                if (AC3() == 0) {
                    addConsSAC(var.idx);
                    p.remove();
                    globalDomains[var.idx] = domain;
                    globalBackup[var.idx] = domain;
                    var.domain = domain;

                    if (domain.isEmpty()) {
                        return false;
                    }
                }

                globalDomains = globalBackup.clone();
            }

            globalDomains[var.idx] = domain;
            var.domain = domain;
            globalBackup[var.idx] = domain;
            
        }

        time.Stop();
        
        /*
        //Print domain contents of all (non-singleton) variables
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                //System.out.print(globalDomains[i*9+j].size()+"\t");
                System.out.println("a[" + (i * 9 + j) + "]" + "=" + globalDomains[i * 9 + j]);
            }
            System.out.println("\n");
        }
        System.out.println("\n");
        System.out.println("\n");
         */
        int count=0;
        for (int m = 0; m < 9; m++) {
            for (int n = 0; n < 9; n++) {

                Iterator<Integer> itr = globalDomains[m * 9 + n].iterator();
                
                if(globalDomains[m*9+n].size()==1)	{
                	vals[m][n] = itr.next().intValue();
                }
                else
                	count++;
                
            }
        }

        if(count>0){
        	board.showMessage("Error in consistency, Sudoku unsolved!");        	
        }
        
        board.writeVals();
            
        //Clear CSP initialization
        for(int p1=0;p1<81;p1++){
        	globalDomains[p1] = null;
        	neighbors[p1] = null;
        }
        globalQueue.clear();
        globalVar.clear();
        Q.clear();
        
        
       board.showMessage("Time taken: "+time.ElapsedTime());

        return true;
    }

    public int[] remove(int[] symbols, int c) {
        if (symbols == null) {
            return null;
        } else {

            int[] copy = new int[symbols.length - 1];
            for (int i = 0; i < symbols.length; i++) {
                if (symbols[i] == c) {

                    System.arraycopy(symbols, 0, copy, 0, i);
                    System.arraycopy(symbols, i + 1, copy, i, symbols.length - i - 1);
                    break;
                }
            }

            return (copy);
        }
    }

    public final boolean valid(int x, int y, int val) {	
        ops += 1;
        if (vals[x][y] == val) {
            return true;
        }
        if (rowContains(x, val)) {
            return false;
        }
        if (colContains(y, val)) {
            return false;
        }
        if (blockContains(x, y, val)) {
            return false;
        }
        return true;
    }

    // This defines a new data-type Arc which you can use for storing 
    // pairs of cells. We use these in the TreeSet Data-Structure above
    // you can opt to avoid this class or create your own helper class.
    class Arc implements Comparable<Object> { 	

        int Xi, Xj;

        public Arc(int cell_i, int cell_j) {
            if (cell_i == cell_j) {
                try {
                    throw new Exception(cell_i + "=" + cell_j);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            Xi = cell_i;
            Xj = cell_j;
        }

        public int compareTo(Object o) {
            return this.toString().compareTo(o.toString());
        }

        public String toString() {
            return "(" + Xi + "," + Xj + ")";
        }
    }

    // Returns true if move does not invalidate block
    public final boolean blockContains(int x, int y, int val) {	
        int block_x = x / 3;
        int block_y = y / 3;
        for (int r = (block_x) * 3; r < (block_x + 1) * 3; r++) {
            for (int c = (block_y) * 3; c < (block_y + 1) * 3; c++) {
                if (vals[r][c] == val) {
                    return true;
                }
            }
        }
        return false;
    }

    // Returns true if move does not invalidate column
    public final boolean colContains(int c, int val) {	
        for (int r = 0; r < 9; r++) {
            if (vals[r][c] == val) {
                return true;
            }
        }
        return false;
    }

    // Returns true if move does not invalidate row
    public final boolean rowContains(int r, int val) {	
        for (int c = 0; c < 9; c++) {
            if (vals[r][c] == val) {
                return true;
            }
        }
        return false;
    }

        // Returns success if int[][] vals contains a valid solution to Sudoku
    private void CheckSolution() { 	
        // If played by hand, need to grab vals
        board.updateVals(vals);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (vals[i][j] == 0) {
                    board.showMessage("Incomplete Sudoku, Complete the Sudoku and try again"); // Incomplete Sudoku
                    return;
                }
            }
        }

        for (int v = 1; v <= 9; v++) {
            // Every row is valid
            for (int r = 0; r < 9; r++) {
                if (!rowContains(r, v)) {
                    board.showMessage("Invalid Row: " + (r + 1));// + " val: " + v);
                    return;
                }
            }
            // Every row is valid
            for (int c = 0; c < 9; c++) {
                if (!colContains(c, v)) {
                    board.showMessage("Invalid Column: " + (c + 1));// + " val: " + v);
                    return;
                }
            }
            // Every block is valid
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (!blockContains(r, c, v)) {
                        board.showMessage("Invalid Block: " + (r + 1) + "," + (c + 1));// + " val: " + v);
                        return;
                    }
                }
            }
        }
        board.showMessage("Success! All Constraints Satisfied, Sudoku Solved");
    }

    /// ---------- GUI + APP Code --------- ////
    /// ----   DO NOT EDIT REST OF FILE --- ////
    enum algorithm { 	

        RESYN, SAC	
    }

    enum difficulty { 	

        SAC1,SAC2,AC1,AC2,GAC1,GAC2,Random
    }

    public static void main(String[] args) {  
        if (args.length == 0) {
            
        	level = difficulty.valueOf("AC1");
            Sudoku app = new Sudoku();
            gui=true;
            app.run();
        	
        	/*System.out.println();
            System.out.println("The code can be run with or without a GUI:");
            System.out.println();
            System.out.println("\tGUI\t$ java Sudoku <difficulty>");
            System.out.println("\tnoX\t$ java Sudoku <difficulty> <algorithm>");
            System.out.println();*/
            System.out.println("difficulty:\tAC1,AC2,SAC1,SAC2,GAC1,GAC2,Random");
            System.out.println("algorithm:\tRESYN, SAC");
            System.out.println();
            //System.exit(1);
        }
        //if (args.length >= 1) {
            
        //}
        /*if (args.length == 2) {
            alg = algorithm.valueOf(args[1]);
            gui = false;
        }*/

        System.out.println("Difficulty: " + level);

        
    }

    public Node get(Node v, HashSet<Node> right){
    	
 	   for(Node N:right){
 		   if(N.compareTo(v)>=0)
 			   return N;
 	   }
 	   return null;
    }  
    void buildAlldiffs()    {
    
    	/* Build bipartite graphs 
    	 * for each constraint of difference
    	 * in total 9(boxes) + 9(columns) + 9(rows)
    	 * 27 alldiff constraints
    	 */
    	//Populate right portion of bipartite with domain
    	ArrayList<Node> rightSub = new ArrayList<Node>();
    	for(int i=0;i<10;i++){
    	Node N = new Node(i);
    	rightSub.add(i, N);    	
    	}
    	
        for(int i=0;i<9;i++)    {
           
        	Graph buildBox= new Graph();;
        	Graph buildRow= new Graph();;
        	Graph buildCol= new Graph();
        	
            for(int j=0;j<9;j++)    {
            	
            	
            	Node N1 = new Node(boxes[i][j]+10);
            	//gboxes.get(i).left.add(N1);
            	Node N2 = new Node(rows[i][j]+10);
            	//grows.get(i).left.add(N2);
            	Node N3 = new Node(cols[i][j]+10);
            	//gcols.get(i).left.add(N3);
            	
                HashSet<Integer> domainBox =  globalDomains[boxes[i][j]];
                
                for(Integer I:domainBox){
                	Node N4 = rightSub.get(I);
                	
                	if (domainBox.size()==1)	{
                    	
                		buildBox.addEdge(N1,N4,0);
                    	buildBox.addEdge(N4,N1,0);
                    	
                    		for(Edge E: buildBox.getAdjacent(N4))	{
                    		
                    			if(E.to==N1){
                    			E.matched=true;
                        		buildBox.matched.add(E);
                        		//E.from.matched = true;
                        		//E.to.matched = true;
                    			}
                    		}
                    		
                    		
                	}
                	else	{
                	buildBox.addEdge(N1,N4,0);
                	buildBox.addEdge(N4,N1,0);
                	}
                	
                }                
                buildBox.left.add(N1);
                
                HashSet<Integer> domainRow = globalDomains[rows[i][j]];
               
                for(Integer I:domainRow){
                	Node N5 = rightSub.get(I);
                	
                	if (domainRow.size()==1)	{                    	
                		buildRow.addEdge(N2,N5,0);
                    	buildRow.addEdge(N5,N2,0);
                    	
                		 for(Edge E:buildRow.getAdjacent(N5))	{	
                		 
                			 if(E.to == N2)	{
                				 E.matched = true;
                    		buildRow.matched.add(E);
                    		//E.from.matched = true;
                    		//E.to.matched = true;
                			 }
                		 }
                    		                    	
                	}
                    	else	{	
                    		buildRow.addEdge(N2,N5,0);
                    		buildRow.addEdge(N5,N2,0);
                    	}
                	}
                	
                                
                buildRow.left.add(N2);
     
                
                HashSet<Integer> domainCol =  globalDomains[cols[i][j]];
               
                for(Integer I:domainCol){
                	Node N6 = rightSub.get(I);
                	
                	
                	if (domainBox.size()==1)	{
                    
                		buildCol.addEdge(N3,N6,0);
                    	buildCol.addEdge(N6,N3,0);
                    	
                    		for(Edge E: buildCol.getAdjacent(N6))	{
                    		
                    			if(E.to == N3){
                    			E.matched=true;
                        		buildCol.matched.add(E);
                        		//E.from.matched = true;
                        		//E.to.matched = true;
                    			}
                    		}
                    		                    		                    		
                        	                    	
                	}
                    	else{
                    	buildCol.addEdge(N3,N6,0);
                    	buildCol.addEdge(N6,N3,0);
                    	}
                	
                }                
                buildCol.left.add(N3);
                
                for(Edge E:buildBox.getAllEdges())	{
                	E.used=false;
                }
                for(Edge E:buildRow.getAllEdges())	{
                	E.used=false;
                }
                for(Edge E:buildCol.getAllEdges())	{
                	E.used=false;
                }
            }
            
            buildBox.right = (ArrayList<Node>) rightSub.clone();
            buildRow.right = (ArrayList<Node>) rightSub.clone();
            buildCol.right = (ArrayList<Node>) rightSub.clone();
            
            allDiffs.add(buildBox);
            allDiffs.add(buildRow);
            allDiffs.add(buildCol);
            
            gboxes.put(i, buildBox);
            grows.put(i, buildRow);
            gcols.put(i, buildCol);
            
            buildBox = null;
            buildRow = null;
            buildCol = null;
        }
        
    }
    
boolean checkFreeVertex(HashSet<Node> layer)	{
		
		for(Node N:layer){
			if(!N.matched)
				return true;
		}
		
		return false;
	}
    
public void findEdges(Graph graph,ArrayList<Node> vertices)	{
	
	for(Edge E:graph.getAllEdges())	{
		if(vertices.contains(E.to) && vertices.contains(E.from))	{
			E.used = true;
			Edge rev=graph.returnReverseEdge(E.to, E.from);
			rev.used=true;
		}
	}
	
}

public boolean matchingExists(Edge E,ArrayList<ArrayList<Edge>> matching)	{
	for(ArrayList<Edge> E1:matching){
		for(Edge E2:E1){
			if((E2.from==E.from && E2.to==E.to) || (E2.to==E.from && E2.from==E.to)){
				return true;
			}
		}
	}
	return false;
}
    public HashSet<Edge> RemoveEdgesFromG(Graph valueGraph, ArrayList<ArrayList<Edge>> matching,ArrayList<HashSet<Edge>> layers,ArrayList<ArrayList<Node>> components)  {
        

    	HashSet<Edge> deletionList = new HashSet<Edge>();
    	HashSet<Edge> revDeletionList = new HashSet<Edge>();
    	
        for(HashSet<Edge> T1: layers ){
        	for(Edge E:T1)	{
        		E.used=true;
        		/*Edge rev=valueGraph.returnReverseEdge(E.to, E.from);
        		if(rev!=null)
        			rev.used=true;*/
        	
        	}
        }
      
        for(ArrayList<Node> n1 : components)
        	findEdges(valueGraph,n1);
        	
        
        for(Edge E:valueGraph.getAllEdges())	{
        	if(!E.used){
        		if(matchingExists(E,matching))	{
        			E.vital=true;
        			Edge rev=valueGraph.returnReverseEdge(E.to, E.from);
        			
        			if(rev!=null)
            		rev.vital=true;
        		}
        		else 	{
        			
        			deletionList.add(E);
        			
        			if(E!=null)
        			valueGraph.removeEdge(E);
        			Edge rev=valueGraph.returnReverseEdge(E.to, E.from);
        			
        			if(rev!=null)
            		valueGraph.removeEdge(rev);
        		}
        	}
        }       
                     
       
           /* for(Edge Ex:valueGraph.getAllEdges()){
            for(Edge E:deletionList){
            	if((Ex.to == E.to && Ex.from==E.from)||(Ex.from == E.to && Ex.to==E.from)){
            		if(!E.matched || !E.vital)
            			//System.out.println(E.to.name-10+"-"+E.from.name+"  Deleted!" );
            			valueGraph.removeEdge(E);
            			//valueGraph.removeReverseEdge(E);
            	}
            }
            
            }*/
            
            
        
            
            for(Edge E:deletionList){
            	//   System.out.println(E.to.name-10 +" "+E.from.name);
            	   Edge rev=valueGraph.returnReverseEdge(E.to, E.from);
            	   //System.out.println(rev.from.name-10 +" "+rev.to.name);
            	   revDeletionList.add(rev);
               }
        
        return revDeletionList;
       
    }
        
  
void rebuildEdgesWithDomains(Graph graph)	{
	
	int r=0,c=0;
	
	//Remove all edges and rebuild them based on updated domain values
    /*for(Edge E:graph.getAllEdges())	{
 	   graph.removeEdge(E);        	   
    }*/
    
	ArrayList<Edge> removalList = new ArrayList<Edge>();
    HashSet<Node> rightSet = new HashSet<Node>();           
    for(int i=0;i<10;i++){
 	   Node n1 = new Node(i);
 	   rightSet.add(n1);
    }
    
    for(Node N:graph.left){
 	
 	   r = (int) Math.ceil((N.name-10) / 9);
        c = (N.name-10) % 9;
        
     for(Edge E:graph.getAdjacent(N)){
    	 
    	   if (!globalDomains[r*9+c].contains(E.to.name))	{
               removalList.add(E);
    		   
 	   } 
     }
    }
    
    for(Edge E1:removalList){
    	
    	if(E1!=null)
    	graph.removeEdge(E1);

    }
    
    for(Edge E:graph.getAllEdges()){
 	   E.used=false;
    }
    
}


void rebuildDomainsWithEdges(Graph graph)	{
	int r,c;
	
	for(Node N:graph.left){
		
		r = (int) Math.ceil((N.name-10) / 9);
        c = (N.name-10) % 9;
        
        globalDomains[r*9+c].clear();
		for(Edge E:graph.getAdjacent(N))	{
			globalDomains[r*9+c].add(E.to.name);
		}
		
	}
	
	
}


    public boolean RESYN()	{
    	init_csp(globalDomains, neighbors, vals, Q);
        buildAlldiffs();
       
        ArrayList<ArrayList<Edge>> match=null;
        HashSet<Node> left=null;
        Graph graph = null;
        HashSet<Edge> deletionList = new HashSet<Edge>();        
        int r,c,count1=0;
        
        while(!allDiffs.isEmpty())  {
                   
           graph = allDiffs.poll();                   
           StronglyConnectedComponents SCC = new StronglyConnectedComponents();
           
           rebuildEdgesWithDomains(graph);
           
           ArrayList<ArrayList<Node>> components = SCC.executeTarjan(graph);
           //System.out.println(components.size());
           matching m1 = new matching();
           
           
           m1.matching_size = graph.left.size();
           m1.bipartiteGraph = graph;
           m1.matching.add(graph.matched);
           left=(HashSet<Node>) m1.bipartiteGraph.left;
           
           for(Edge E:m1.bipartiteGraph.getAllEdges())	{
        	   E.from.matched=false;
        	   E.to.matched = false;
        	   E.matched = false;
           }
           for(Edge E:graph.matched){
        	   E.from.matched=true;
        	   E.to.matched = true;
        	   E.matched=true;
           }
           
           m1.maxmatching();
           match = m1.matching;
         //  if(match.size()<m1.matching_size)	{
        //	   return false;
        //   }          
           

     /*  	for(Node N:left){
       	
       		System.out.println(N.name-10+" ");
       		  
       		for(Edge E:graph.getAdjacent(N))	{
       			System.out.print(E.to.name+" ");
       	
       		}
       		
       	
       		System.out.println();
       	}*/
           int before=graph.getAllEdges().size(), after;
           System.out.println("Before "+before);
          /* for(Edge E:graph.getAllEdges()){
        	 System.out.println(E.to.name+" "+E.from.name);  
           }*/
           deletionList = RemoveEdgesFromG(graph,match,m1.layerEdges,components);
           after=graph.getAllEdges().size();
           System.out.println("After "+after);
           
           
           /*for(Edge E:graph.getAllEdges()){
          	 System.out.println(E.to.name+" "+E.from.name);  
             }*/
           
           rebuildDomainsWithEdges(graph);
           
         //  Print Deletion List
        /*   for(Edge E:deletionList){
        	//   System.out.println(E.to.name-10 +" "+E.from.name);
        	   Edge rev=graph.returnReverseEdge(E.to, E.from);
        	   //System.out.println(rev.from.name-10 +" "+rev.to.name);
        	   revDeletionList.add(rev);
           }*/
           
           
        	for(Node N1:left){
        		HashSet<Integer> domain = new HashSet<Integer>();
        		System.out.println(N1.name-10+" ");
        		  
        		r = (int) Math.ceil((N1.name-10) / 9);
                c = (N1.name-10) % 9;
                  
        		for(Edge E:graph.getAdjacent(N1))	{
        				
        			if(!deletionList.isEmpty() ){
        				if( !deletionList.contains(E) )
        					//System.out.print(E.to.name+" ");
        					domain.add(E.to.name);
        			
             		}
        			else	{
        				domain.add(E.to.name);
        			}
        		}
        		globalDomains[r*9+c] = domain;
        		System.out.println("         "+domain.size());
        	}

    /*    	boolean computeMatching=false;
        
        	for(Edge E:deletionList){
        		if(match.contains(E))	{
        			match.remove(E);
        			if(E.vital)
        				return false;
        			else	{
        				computeMatching=true;
        			}
        		}
        		if(!deletionList.isEmpty())
        			
        			if(graph.getAllEdges().contains(E))
        				graph.removeEdge(E);
        		}
        	
        	if(computeMatching){
        		if(matchingCoveringX(graph,match))	{
        			return false;
        		}
        		else	{
        			match = matchNew;
        		}
        	}
        	
        	deletionList = RemoveEdgesFromG(graph,match,m1.layerEdges,components);*/
           
        	
        }
         
        
        int count=0;
        
        for (int m = 0; m < 9; m++) {
            for (int n = 0; n < 9; n++) {
                Iterator<Integer> itr = globalDomains[m * 9 + n].iterator();
                if(globalDomains[m*9+n].size()==1)
                	vals[m][n] = itr.next().intValue();
                else
                	count++;
            }	
        }
        
   
        if(count>0){
        	board.showMessage("Error in consistency, Sudoku Unsolved!");        	
        }
        
        board.writeVals();
        
        //Clear CSP initialization
        for(int p1=0;p1<81;p1++){
        	globalDomains[p1] = null;
        	neighbors[p1] = null;
        }
        globalQueue.clear();
        globalVar.clear();
        Q.clear();
        
        
        return true;
    }
    
    private boolean matchingCoveringX(Graph graph,ArrayList<ArrayList<Edge>> match) {
		//HashSet<Node> left=new HashSet<Node>();
    	matching m1 = new matching();
    	  m1.matching_size = graph.left.size();
          m1.bipartiteGraph = graph;
          m1.matching.add(graph.matched);
          
          for(ArrayList<Edge> matches:match)	{
        	  m1.matching.add(matches);
          }
          //left=(HashSet<Node>) m1.bipartiteGraph.left;
          
          for(Edge E:m1.bipartiteGraph.getAllEdges())	{
       	   E.from.matched=false;
       	   E.to.matched = false;
       	   E.matched = false;
          }
          for(Edge E:graph.matched){
       	   E.from.matched=true;
       	   E.to.matched = true;
       	   E.matched=true;
          }
          
          m1.maxmatching();
          matchNew = m1.matching;
          
          if(matchNew.size()==m1.matching_size)	{
        	  return true;
          }
    	
		return false;
	}

	public void run() { 	
        board = new Board(gui, this);
        while (!initialize());
        if (gui) {
            board.initVals(vals);
        } else {
            //System.out.println("Algorithm: " + alg);
            switch (alg) {
                default:
                case RESYN:
                    board.initVals(vals);
                    RESYN();
                    break;
                case SAC:
                    board.initVals(vals);
                    SAC();
                    break;
            }
            CheckSolution();
        }
    }

    public final boolean initialize() { 
        switch (level) {

            //04-27-2011-123749 , Level 9 Sudoku instance from sudoku.unl.edu (Willem's) database
            case SAC1:
                vals[0] = new int[]{0, 0, 1, 0, 8, 0, 6, 0, 4};
                vals[1] = new int[]{0, 3, 7, 6, 0, 0, 0, 0, 0};
                vals[2] = new int[]{5, 0, 0, 0, 0, 0, 0, 0, 0};
                vals[3] = new int[]{0, 0, 0, 0, 0, 5, 0, 0, 0};
                vals[4] = new int[]{0, 0, 6, 0, 1, 0, 8, 0, 0};
                vals[5] = new int[]{0, 0, 0, 4, 0, 0, 0, 0, 0};
                vals[6] = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 3};
                vals[7] = new int[]{0, 0, 0, 0, 0, 7, 5, 2, 0};
                vals[8] = new int[]{8, 0, 2, 0, 9, 0, 7, 0, 0};
                break;

            //12-08-2009-072537 , Level 6 Sudoku instance from sudoku.unl.edu (Omaha World Herald) database
            case SAC2:
                vals[0] = new int[]{0, 5, 7, 4, 8, 0, 0, 0, 6};
                vals[1] = new int[]{0, 2, 0, 0, 0, 7, 0, 1, 0};
                vals[2] = new int[]{0, 0, 8, 0, 6, 0, 0, 0, 0};
                vals[3] = new int[]{0, 6, 0, 0, 0, 0, 0, 0, 0};
                vals[4] = new int[]{0, 1, 0, 2, 0, 3, 0, 4, 0};
                vals[5] = new int[]{0, 0, 0, 0, 0, 0, 0, 8, 0};
                vals[6] = new int[]{0, 0, 0, 0, 7, 0, 1, 0, 0};
                vals[7] = new int[]{0, 8, 0, 9, 0, 0, 0, 5, 0};
                vals[8] = new int[]{6, 0, 0, 0, 3, 1, 9, 2, 0};
                break;

            //04-25-2010-014227 , Level 1 Sudoku instance from sudoku.unl.edu database
            case AC1:
                vals[0] = new int[]{0, 0, 0, 0, 7, 1, 0, 0, 0};
                vals[1] = new int[]{0, 0, 0, 0, 0, 2, 5, 0, 0};
                vals[2] = new int[]{9, 1, 0, 0, 0, 0, 8, 0, 0};
                vals[3] = new int[]{0, 0, 0, 0, 0, 7, 0, 8, 9};
                vals[4] = new int[]{0, 0, 7, 0, 0, 8, 0, 4, 0};
                vals[5] = new int[]{0, 6, 5, 9, 0, 0, 7, 0, 0};
                vals[6] = new int[]{1, 3, 4, 0, 0, 0, 0, 6, 0};
                vals[7] = new int[]{2, 0, 0, 3, 0, 0, 0, 0, 0};
                vals[8] = new int[]{0, 0, 0, 7, 6, 0, 0, 0, 0};
                break;

            //Rhubarb Pie, Level 3  Sudoku instance from sudoku.unl.edu database
            case AC2:
                vals[0] = new int[]{0, 0, 0, 0, 2, 0, 3, 0, 4};
                vals[1] = new int[]{0, 8, 0, 3, 5, 0, 0, 0, 0};
                vals[2] = new int[]{9, 0, 0, 0, 0, 4, 0, 2, 6};
                vals[3] = new int[]{0, 0, 0, 5, 0, 0, 0, 6, 0};
                vals[4] = new int[]{0, 4, 0, 0, 0, 0, 1, 0, 0};
                vals[5] = new int[]{7, 0, 0, 0, 0, 2, 0, 0, 9};
                vals[6] = new int[]{5, 0, 0, 4, 0, 7, 0, 1, 0};
                vals[7] = new int[]{0, 0, 9, 0, 0, 0, 0, 8, 0};
                vals[8] = new int[]{0, 0, 0, 1, 0, 3, 6, 7, 0};
                break;
                
                //NY Times May 15th 2009, from sudoku.unl.edu
            case GAC1:
            	vals[0] = new int[]{0, 0, 0, 0, 0, 0, 0, 8, 7};
                vals[1] = new int[]{0, 2, 0, 0, 0, 7, 0, 0, 5};
                vals[2] = new int[]{0, 6, 1, 0, 0, 0, 0, 0, 0};
                vals[3] = new int[]{0, 0, 0, 0, 0, 9, 0, 0, 4};
                vals[4] = new int[]{0, 5, 6, 3, 0, 0, 0, 0, 1};
                vals[5] = new int[]{0, 0, 0, 2, 7, 0, 3, 0, 0};
                vals[6] = new int[]{0, 3, 0, 0, 0, 5, 0, 0, 0};
                vals[7] = new int[]{0, 0, 8, 7, 4, 0, 0, 0, 0};
                vals[8] = new int[]{6, 0, 0, 0, 9, 8, 0, 0, 0};            	            	
            	break;
            
            	//November 6th 2009, Omaha World Sudoku from sudoku.unl.edu
            case GAC2:
            	vals[0] = new int[]{0, 0, 0, 0, 0, 9, 8, 2, 0};
                vals[1] = new int[]{1, 0, 0, 0, 0, 2, 4, 3, 9};
                vals[2] = new int[]{0, 0, 0, 8, 3, 0, 0, 0, 1};
                vals[3] = new int[]{0, 4, 0, 0, 0, 0, 3, 0, 0};
                vals[4] = new int[]{5, 0, 1, 0, 9, 0, 2, 0, 7};
                vals[5] = new int[]{0, 0, 3, 0, 0, 0, 0, 8, 0};
                vals[6] = new int[]{2, 0, 0, 0, 6, 7, 0, 0, 0};
                vals[7] = new int[]{3, 7, 6, 9, 0, 0, 0, 0, 2};
                vals[8] = new int[]{0, 1, 9, 2, 0, 0, 0, 0, 0};
            	break;

            case Random:
            default:
                ArrayList<Integer> preset = new ArrayList<Integer>();
                while (preset.size() < numCells) {
                    int r = rand.nextInt(81);
                    if (!preset.contains(r)) {
                        preset.add(r);
                        int x = r / 9;
                        int y = r % 9;
                        if (!assignRandomValue(x, y)) {
                            return false;
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void actionPerformed(ActionEvent e) {		
        String label = ((JButton) e.getSource()).getText();
        if (label.equals("RESYN")) {
        	board.initVals(vals);
            RESYN();
        } else if (label.equals("SAC")) {
        	board.initVals(vals);
            SAC();
        } else if (label.equals("Clear")) {
            board.Clear();
        }else if (label.equals("Check")) {
        	CheckSolution();
        }
        else {
                        
        }     
    }

    public final boolean assignRandomValue(int x, int y) { 
        ArrayList<Integer> pval = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        while (!pval.isEmpty()) {
            int ind = rand.nextInt(pval.size());
            int i = pval.get(ind);
            if (valid(x, y, i)) {
                vals[x][y] = i;
                return true;
            } else {
                pval.remove(ind);
            }
        }

        System.err.println("No valid moves exist.  Recreating board.");
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                vals[r][c] = 0;
            }
        }
        return false;
    }

    private void Finished(boolean success) {  

        if (success) {
            board.writeVals();
            // board.showMessage("Solved in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recusive ops)");
        } else {
            //board.showMessage("No valid configuration found in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recursive ops)");
        }
    }

    class Board {  

        GUI G = null;
        boolean gui = true;

        public Board(boolean X, Sudoku s) {
            gui = X;
            if (gui) {
                G = new GUI(s);
            }
        }

        public void initVals(int[][] vals) {
            G.initVals(vals);
        }

        public void writeVals() {
            if (gui) {
                G.writeVals();
            } else {
                for (int r = 0; r < 9; r++) {
                    if (r % 3 == 0) {
                        System.out.println(" ----------------------------");
                    }
                    for (int c = 0; c < 9; c++) {
                        if (c % 3 == 0) {
                            System.out.print(" | ");
                        }
                        if (vals[r][c] != 0) {
                            System.out.print(vals[r][c] + " ");
                        } else {
                            System.out.print("_ ");
                        }
                    }
                    System.out.println(" | ");
                }
                System.out.println(" ----------------------------");
            }
        }

        public void Clear() {
            if (gui) {
                G.clear();
            }
        }

        public void showMessage(String msg) {
            if (gui) {
                G.showMessage(msg);
            }
            System.out.println(msg);
        }

        public void updateVals(int[][] vals) {
            if (gui) {
                G.updateVals(vals);
            }
        }
    }
    
    
    class GUI {  
        // ---- Graphics ---- //

    	
    	
        int size = 40;
        JFrame mainFrame = null;
        JTextField[][] cells;
        JPanel[][] blocks;

        public void initVals(int[][] vals) {
            // Set up the initial vals value(Sudoku in question), Mark in gray as fixed
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (vals[r][c] != 0) {
                        cells[r][c].setText(vals[r][c] + "");
                        cells[r][c].setEditable(false);
                        cells[r][c].setBackground(Color.lightGray);
                    }
                }
            }
        }
        
        public void showMessage(String msg) {
            JOptionPane.showMessageDialog(null,
                    msg, "Message", JOptionPane.INFORMATION_MESSAGE);
        }

        //Get values from cells array to solution(vals) array
        public void updateVals(int[][] vals) {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    try {
                        vals[r][c] = Integer.parseInt(cells[r][c].getText());
                    } catch (java.lang.NumberFormatException e) {
                        //showMessage("Invalid Board");
                        return;
                    }
                }
            }
        }

        //Clear up all the solutions and set solution array Vals to 0, and clear the values in GUI, not clearing the values from the problem.
        public void clear() {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (cells[r][c].isEditable()) {
                        cells[r][c].setText("");
                        vals[r][c] = 0;
                    } else {
                    	cells[r][c].setEditable(true);
                    	cells[r][c].setBackground(Color.white);
                        cells[r][c].setText("");
                        vals[r][c] = 0;
                    }
                }
            }                        
            
        }

        //Write the updated solution in vals to the GUI
        public void writeVals() {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (vals[r][c] == 0) {
                        cells[r][c].setText("");
                    } else {
                        cells[r][c].setText(vals[r][c] + "");
                    }
                }
            }
        }
                
        public GUI(final Sudoku s) {

            mainFrame = new javax.swing.JFrame();
            mainFrame.getContentPane().setLayout(new BorderLayout());
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel gamePanel = new javax.swing.JPanel();
            gamePanel.setBackground(Color.black);
            mainFrame.getContentPane().add(gamePanel, BorderLayout.NORTH);
            gamePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            gamePanel.setLayout(new GridLayout(3, 3, 3, 3));

            blocks = new JPanel[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 2; j >= 0; j--) {
                    blocks[i][j] = new JPanel();
                    blocks[i][j].setLayout(new GridLayout(3, 3));
                    gamePanel.add(blocks[i][j]);
                }
            }

            cells = new JTextField[9][9];
            for (int cell = 0; cell < 81; cell++) {
                int i = cell / 9;
                int j = cell % 9;
                cells[i][j] = new JTextField();
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setSize(new java.awt.Dimension(size, size));
                cells[i][j].setPreferredSize(new java.awt.Dimension(size, size));
                cells[i][j].setMinimumSize(new java.awt.Dimension(size, size));
                blocks[i / 3][j / 3].add(cells[i][j]);
            }

            String[] consistency = {"AC1","AC2", "SAC1","SAC2","GAC1","GAC2", "Random"};
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            mainFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            JButton DFS_Button = new JButton("RESYN");
            DFS_Button.addActionListener(s);
            JButton SAC_Button = new JButton("SAC");
            SAC_Button.addActionListener(s);
            JButton Clear_Button = new JButton("Clear");
            Clear_Button.addActionListener(s);
            JButton Check_Button = new JButton("Check");
            Check_Button.addActionListener(s);            
            final JComboBox<Object> list = new JComboBox<Object>(consistency);
            list.addActionListener(new ActionListener()	{
            	 public void actionPerformed(ActionEvent e) {
            		 JComboBox cb = (JComboBox)e.getSource();
                     String input = (String)cb.getSelectedItem();
                     level = difficulty.valueOf(input);
                     
                   
                     board.Clear();
                     while (!initialize());
                     board.initVals(vals);
                     
            	        //System.out.println("Selected index=" + list.getSelectedIndex()+ " Selected item=" + list.getSelectedItem());
            	      }
            });
            
            
       
            buttonPanel.add(DFS_Button);
            buttonPanel.add(SAC_Button);
            buttonPanel.add(Clear_Button);
            buttonPanel.add(Check_Button);
            buttonPanel.add(list);	
            

            mainFrame.pack();
            mainFrame.setVisible(true);

        }
    }
    Random rand = new Random();
    // ----- Helper ---- //
    static algorithm alg = algorithm.RESYN;
    static difficulty level = difficulty.AC1;
    static boolean gui = true;
    static int ops;
    static int recursions;
    static int numCells = 15;
    static DecimalFormat myformat = new DecimalFormat("###,###");
}
