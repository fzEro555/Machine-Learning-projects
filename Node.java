/*
 * Node.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */
import java.util.ArrayList;

public class Node{

    public int attribute = -1;
    public int label = -1;
    public int[] classCounts = null;
    public ArrayList<Node> children = new ArrayList<Node>();
    private boolean isEmpty = false;
    Node(){
        this.classCounts = new int[]{};
    }

    Node( int[] classCounts ){
        this.classCounts = classCounts;
    }

    public boolean isLeaf(){
        if(this.children.size() == 0)
            return true;
        else
            return false;
    }

    public boolean isEmpty(){
        return this.isEmpty;
    }

    public void set_isEmpty(boolean isEmpty){
        this.isEmpty = isEmpty;
    }

    public double getError(){
        int n = 0;
        int x = 0;
        for (int i = 0; i < this.classCounts.length; i++){
            n += this.classCounts[i];
        }
        x = n - this.classCounts[this.label];
        return n * Utils.u25(n, x);
    }
}

