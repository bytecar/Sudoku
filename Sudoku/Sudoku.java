package Sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;

public class Sudoku implements Runnable, ActionListener {

    class variable {

        int idx;
        HashSet<Integer> domain;
    }
    // Where final values must be assigned, call the updateVals funtion in the board class to upadte it to GUI
    int[][] vals = new int[9][9];
    int[][] boxes = new int[9][9];
    int[][] rows = new int[9][9];
    int[][] cols = new int[9][9];
    Board board = null;

    /// --- DEPTH FIRST SEARCH ---//
    
    private void DFS() {
        // Zero out values prior to Running
        board.Clear();
        ops = 0;
        recursions = 0;

        // Recurisively call your code.  Init: Cell 0 (Top Left)
        boolean success = RecursiveDFS(0);

        // Print evaluation of run
        Finished(success);
    }

    // YOU MAY NOT CHANGE INTERFACE DEFINITION
    private boolean RecursiveDFS(int cell) {
        recursions += 1;
        // YOUR CODE GOES HERE


        return false;
    }
    /// --- AC-3 Constraint Satisfication --- ///
    /**
     * Discussion and Comments about AC3:
     * 
     */
    // Useful but not required Data-Structures;
    HashSet<Integer>[] globalDomains = new HashSet[81];
    HashSet<Integer>[] neighbors = new HashSet[81];
    TreeSet<Arc> globalQueue = new TreeSet<Arc>();
    AdjacencyList adj = new AdjacencyList();
    
    ArrayList<variable> globalVar = new ArrayList<variable>();
    Queue<variable> Q = new LinkedList<variable>();
    Queue<HashMap<Integer,HashSet<Integer>>> allDiffs = new LinkedList<HashMap<Integer,HashSet<Integer>>>();

    private void init_csp(HashSet<Integer>[] Domains, HashSet<Integer>[] neighbors, int[][] vals, Queue<variable> Q) {

        //init Sudoku Domains
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
                res = (lookup(rows[i][j]));
                for (int k = 0; k < 9; k++) {

                    if (res >= 0) {
                        neighbors[i * 9 + j].add(boxes[res][k]);
                    }
                    neighbors[i * 9 + j].add(rows[i][k]);
                    neighbors[i * 9 + j].add(cols[j][k]);
                }

                neighbors[i * 9 + j].remove(rows[i][j]);

                Iterator<Integer> itr = neighbors[i * 9 + j].iterator();
                while (itr.hasNext()) {
                    int x2 = itr.next();
                    Arc temp = new Arc(rows[i][j], x2);
                    globalQueue.add(temp);
                    temp = new Arc(x2, rows[i][j]);;
                    globalQueue.add(temp);
                }
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

    void init_globalQ() {

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                Iterator<Integer> itr = neighbors[i * 9 + j].iterator();
                while (itr.hasNext()) {
                    int x2 = itr.next();
                    Arc temp = new Arc(rows[i][j], x2);
                    globalQueue.add(temp);
                    temp = new Arc(x2, rows[i][j]);;
                    globalQueue.add(temp);
                }
            }
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

    int init_variable() {

        for (int i = 0; i < 81; i++) {

            variable tmp = new variable();
            tmp.domain = globalDomains[i];
            tmp.idx = i;

            globalVar.add(tmp);
        }

        return 0;
    }

    private int AC3() {

        int r, c;
        //board.Clear();
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
                //board.showMessage("No Solution");
                return 0;
            } else if ((revised.size() != globalDomains[binaryConstraint.Xi].size())) {

                globalDomains[binaryConstraint.Xi] = revised;
                
                
                

                /*if (revised.size() == 1) {
                    Iterator<Integer> itr = globalDomains[binaryConstraint.Xi].iterator();
                    vals[r][c] = itr.next().intValue();
                    board.writeVals();
                }*/
                
                r = (int) Math.ceil(binaryConstraint.Xi / 9);
                c = (binaryConstraint.Xi) % 9;
                addneighbors(r, c);

            }


        }



        /*
         * Print domain contents of all (non-singleton) variables
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

        // Print evaluation of run
        //success = true;
        //Finished(success);
        //board.updateVals(vals);
        //board.showMessage("AC3 Done!!");

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

        init_variable();
        init_queue(Q);

        init_globalQ();
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
                //var.domain = vi;

                globalQueue=(TreeSet<Arc>) Qbackup.clone();
                if (AC3() == 0) {
                    addConsSAC(var.idx);

                    p.remove();
                    //domain.remove(k);
                    globalDomains[var.idx] = domain;
                    globalBackup[var.idx] = domain;
                    var.domain = domain;

                   /* int count = 0;
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            //System.out.print(globalDomains[i*9+j].size()+"\t");
                            if (globalDomains[i * 9 + j].size() == 1) {
                                count++;
                            }
                        }

                    }
                    System.out.println("Resolved: " + count);
                    System.out.println("\n");
                     */
                    if (domain.isEmpty()) {
                        return false;
                    }
                }

                /*  for(int l=0;l<81;l++)   {
                globalDomains[l]=globalVar.get(l).domain;
                }*/
                globalDomains = globalBackup.clone();
            }

            globalDomains[var.idx] = domain;
            var.domain = domain;
            globalBackup[var.idx] = domain;

            // }                                    
        }

        time.Stop();
        
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


        for (int m = 0; m < 9; m++) {
            for (int n = 0; n < 9; n++) {

                Iterator<Integer> itr = globalDomains[m * 9 + n].iterator();
                vals[m][n] = itr.next().intValue();                 
                
            }
        }

        board.writeVals();
               
       board.showMessage("Time taken: "+time.ElapsedTime());

        //System.out.println("Time taken: "+time.ElapsedTime());
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

    // This is the actual AC-3 Algorithm ( You may change this function )
    private final boolean AC3(HashSet<Integer>[] Domains) {



        return true;
    }

    // This is the Depth First Search.  ( YOU MAY NOT CHANGE THIS INTERFACE )
    private final boolean AC3_DFS(int cell, HashSet<Integer>[] Domains) {
        recursions += 1;
        // YOUR CODE HERE
        return false;
    }

    // This is the Revise function defined in the book ( arc-reduce on wiki )
    // ( You may change this function definition )
    private final boolean Revise(Arc t, HashSet<Integer>[] Domains) {
        ops += 1;


        return false;
    }

    // This defines constraints between a set of variables
    // This is discussed in the book but you may change the interface.
    private final void allDiff(int[] all) {
        // YOUR CODE HERE
    }

    /// ---------- HELPER FUNCTIONS --------- ///
    /// ----   DO NOT EDIT REST OF FILE   --- ///
    // Returns true if that move does not invalidate board
    public final boolean valid(int x, int y, int val) {	// DO NOT EDIT
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
    class Arc implements Comparable<Object> { 	// DO NOT EDIT

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
    public final boolean blockContains(int x, int y, int val) {	// DO NOT EDIT
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
    public final boolean colContains(int c, int val) {	// DO NOT EDIT
        for (int r = 0; r < 9; r++) {
            if (vals[r][c] == val) {
                return true;
            }
        }
        return false;
    }

    // Returns true if move does not invalidate row
    public final boolean rowContains(int r, int val) {	// DO NOT EDIT
        for (int c = 0; c < 9; c++) {
            if (vals[r][c] == val) {
                return true;
            }
        }
        return false;
    }

    public int init_queue(Queue<variable> Q) {

        for (int i = 0; i < 81; i++) {
            Q.add(globalVar.get(i));
        }

        return 0;
    }

    // Returns success if int[][] vals contains a valid solution to Sudoku
    private void CheckSolution() { 	// DO NOT EDIT
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
    enum algorithm { 	// DO NOT EDIT

        RESYN, SAC
    }

    enum difficulty { 	// DO NOT EDIT

        SAC1, SAC2, AC1, AC2, random
    }

    public static void main(String[] args) {  // DO NOT EDIT
        if (args.length == 0) {
            System.out.println();
            System.out.println("The code can be run with or without a GUI:");
            System.out.println();
            System.out.println("\tGUI\t$ java Sudoku <difficulty>");
            System.out.println("\tnoX\t$ java Sudoku <difficulty> <algorithm>");
            System.out.println();
            System.out.println("difficulty:\teasy, medium, noSolution, hardNoSolution");
            System.out.println("algorithm:\tRESYN, SAC");
            System.out.println();
            System.exit(1);
        }
        if (args.length >= 1) {
            level = difficulty.valueOf(args[0]);
        }
        if (args.length == 2) {
            alg = algorithm.valueOf(args[1]);
            gui = false;
        }

        System.out.println("Difficulty: " + level);

        Sudoku app = new Sudoku();
        app.run();
    }

    void buildAlldiffs()    {
        
        for(int i=0;i<9;i++)    {
            HashMap<Integer,HashSet<Integer>> box = new HashMap<Integer,HashSet<Integer>>();
            HashMap<Integer,HashSet<Integer>> row = new HashMap<Integer,HashSet<Integer>>();
            HashMap<Integer,HashSet<Integer>> col = new HashMap<Integer,HashSet<Integer>>();
            
            for(int j=0;j<9;j++)    {
                
                HashSet<Integer> domainBox = new HashSet<Integer>();
                domainBox = (HashSet<Integer>) globalDomains[boxes[i][j]].clone();
                box.put(boxes[i][j],domainBox);
                
                HashSet<Integer> domainRow = new HashSet<Integer>();
                domainRow = (HashSet<Integer>) globalDomains[rows[i][j]].clone();
                row.put(rows[i][j],domainRow);
                
                HashSet<Integer> domainCol = new HashSet<Integer>();
                domainCol = (HashSet<Integer>) globalDomains[cols[i][j]].clone();
                col.put(cols[i][j],domainCol);
                
                
            }
            allDiffs.add(box);
            allDiffs.add(row);
            allDiffs.add(col);
        }
        
    }
    
    //for Tarjan SCC
    void buildAdjacencies(HashMap<Integer, HashSet<Integer>> valueGraph)	{
    	
    	
    	for(Iterator<Integer> it = valueGraph.keySet().iterator(); it.hasNext();) {
    	    Integer key = it.next();
    	    HashSet<Integer> value = valueGraph.get(key);
    	    Node source = new Node(key);
    	    
    	    for(Integer i : value) {
    	    	Node dest = new Node(i);
    	    	adj.addEdge(source, dest, 0);    	    	
    	    }
    	}
    	    	
    	
    }

    
    public void removeEdgesFromG(HashMap<Integer, HashSet<Integer>> valueGraph, int[] matching)  {
        
        Node zero = new Node(0);
        int[] vital=null;
        
        HashMap<Integer, HashSet<Integer>> edgeset = new HashMap<Integer, HashSet<Integer>>();
        buildAdjacencies(valueGraph);
        ArrayList<ArrayList<Node>> ssc;
                
         for(int v=0;v<9;v++)    {
            for(int u:valueGraph.get(v))   {
                if(matching[u]==999)  {
                }
                else    {
                    if(matching[u]!=v)   {
                        HashSet<Integer> tmp= edgeset.get(v);
                        tmp.add(matching[u]);
                        edgeset.put(v,tmp);
                    }
                }
            }
        }
                 
         Tarjan connectedComponents = new Tarjan();
         ssc=connectedComponents.tarjan(zero,adj);
         
         
        
    }
    
    public boolean RESYN() {
        
    	init_csp(globalDomains, neighbors, vals, Q);
        buildAlldiffs();
       
        HashMap<Integer,HashSet<Integer>> allDiff;
        HashMap<Node,HashSet<Edge>> valueg = new HashMap<Node,HashSet<Edge>>();
        HashMap<Node,ArrayList<Node>> alledges = new HashMap<Node,ArrayList<Node>>();
        
        //Build G' for Hopcroft
        Node source = new Node(-99999);
    	Node sink = new Node(99999);
	    
        HashSet<Edge> sourceEdges = new HashSet<Edge>();
        HashSet<Edge> sinkEdges = new HashSet<Edge>();
        ArrayList<Node> forallsource = new ArrayList<Node>();
        ArrayList<Node> forallsink = new ArrayList<Node>();
        ArrayList<Node> match;
        
        while(!allDiffs.isEmpty())  {
                   
            allDiff = allDiffs.poll();        
                             
            for(Iterator<Integer> it = allDiff.keySet().iterator(); it.hasNext();) {
            	
            	HashSet<Edge> edges = new HashSet<Edge>();
            	
            	Integer key = it.next();
        	    HashSet<Integer> value = allDiff.get(key);
        	    Node start = new Node(key);
        	    
        	    //Source-L
        	    Edge esource = new Edge(source,start,0);
        	    sourceEdges.add(esource);
        	    forallsource.add(start);
        	    
        	    ArrayList<Node> foralledges = new ArrayList<Node>(); 
        	    for(Integer i : value) {
        	    	        	    	
        	    	Node dest = new Node(i);
        	    	Edge edg = new Edge(start,dest,0);
        	    	edges.add(edg);
        	    	
        	    	//alledges
        	    	foralledges.add(dest);
        	    	
        	    	//R-Sink
        	    	Edge esink = new Edge(dest,sink,0);
            	    sinkEdges.add(esink);
            	    forallsink.add(dest);
            	    
            	    
        	    }
        	    
        	    //alledges
        	    alledges.put(start,foralledges);
        	    
        	    valueg.put(start, edges);
        	    edges.clear();
        	    foralledges.clear();
        	}
            valueg.put(source, sourceEdges);
            valueg.put(sink, sinkEdges);
            alledges.put(source, forallsource);
            alledges.put(sink, forallsink);
            
            //End building G'
            
            //Assignments and Call Hopcroft            
            Hopcroft matching = new Hopcroft();
            matching.List = alledges;
            matching.source = source;
            matching.sink=sink;
            match = matching.Matching();
            
                                    
            //removeEdgesFromG(match.bipartiteGraph,matched);
            
        }
    return true;
    }

    public void run() { 	// DO NOT EDIT
        board = new Board(gui, this);
        while (!initialize());
        if (gui) {
            board.initVals(vals);
            //this.init_csp(globalDomains, neighbors, vals);
        } else {
            //board.writeVals();
            this.init_csp(globalDomains, neighbors, vals, Q);
            System.out.println("Algorithm: " + alg);
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

    public final boolean initialize() { // DO NOT EDIT
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

            case random:
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

    public void actionPerformed(ActionEvent e) {		// DO NOT EDIT
        String label = ((JButton) e.getSource()).getText();
        if (label.equals("RESYN")) {
            RESYN();
        } else if (label.equals("SAC")) {
            SAC();
        } else if (label.equals("Clear")) {
            board.Clear();
        } else if (label.equals("Check")) {
            CheckSolution();
        }
    }

    public final boolean assignRandomValue(int x, int y) { // DO NOT EDIT
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

    private void Finished(boolean success) {  // DO NOT EDIT

        if (success) {
            board.writeVals();
            // board.showMessage("Solved in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recusive ops)");
        } else {
            //board.showMessage("No valid configuration found in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recursive ops)");
        }
    }

    class Board {  // DO NOT EDIT

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
    
    
    class GUI {  // DO NOT EDIT
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
                        showMessage("Invalid Board");
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
                        cells[r][c].setText("" + vals[r][c]);
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
                
        public GUI(Sudoku s) {

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
            buttonPanel.add(DFS_Button);
            buttonPanel.add(SAC_Button);
            buttonPanel.add(Clear_Button);
            buttonPanel.add(Check_Button);

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
