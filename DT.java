/*
 * DT.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */
import java.io.Serializable;
import java.util.ArrayList;

public class DT extends Classifier implements Serializable, OptionHandler{
    protected Attributes attributes;
    protected Node root;
    protected boolean notprune = false;

    public DT(){
        this.root = new Node();
        this.attributes = new Attributes();
    }
    public DT( String[] options ) throws Exception{
        this.root = new Node();
        this.attributes = new Attributes();
        this.setOptions(options);
    }
    public Performance classify( DataSet ds ) throws Exception{
        Performance performance = new Performance(ds.getAttributes());
        for (Example example : ds.getExamples()) {
            double [] predictions = this.getDistribution(example);
            int actual = example.get(ds.getAttributes().getClassIndex()).intValue();
            performance.add(actual, predictions);
        }
        return performance;
    }
    public int classify( Example example ) throws Exception{
        return Utils.maxIndex(this.getDistribution(example));
    }
    public double[] getDistribution( Example example ) throws Exception{
        return this.getDistribution(this.root, example);
    }
    public void prune() throws Exception{
        this.prune(this.root);
    }
    public void setOptions( String[] options ){
        if (options.length == 0) {
            try{
                throw new Exception("Empty arguments");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        int i = 0;
        while(i < options.length) {
            switch(options[i]) {
                case "-u":
                    this.notprune = true;
                    break;
            }
            i++;
        }
    }
    public void train( DataSet ds ) throws Exception{
        this.attributes = ds.getAttributes();
        this.root = this.train_aux(ds);
        if(this.notprune == false)
            this.prune();
    }
    public Classifier clone(){
        DT dt = new DT();
        dt.attributes = this.attributes;
        dt.notprune = this.notprune;
        return dt;
    }

// private recursive methods

    private double[] getDistribution(Node node, Example example) throws Exception{
        int[] classCounts = node.classCounts;
        double s = 0.0;
        for(int i=0; i<classCounts.length; i++)
            s = s + classCounts[i];
        double[] distribution = new double[classCounts.length];
        for(int i=0; i<distribution.length; i++)
            distribution[i] = classCounts[i] / s;
        if(node.isLeaf()){
            return distribution;
        }
        else{
            Node childnode = node.children.get(example.get(node.attribute).intValue());
            if(childnode.isEmpty())
                return distribution;
            else
                return getDistribution(childnode, example);
        }
    }
    private void prune( Node node ) throws Exception {
        if (!node.isLeaf()) {
            double error = node.getError();
            double subtree_error = 0.0;
            for (Node childnode : node.children) {
                if (childnode.isEmpty() == false)
                    subtree_error = subtree_error + childnode.getError();
            }
            if (error < subtree_error) {
                node.children = new ArrayList<>();
            } else {
                for (Node childnode : node.children)
                    this.prune(childnode);
            }
        }
    }

    private Node train_aux( DataSet ds ) throws Exception{
        Node node = new Node();
        if (ds.isEmpty()) {
            node.set_isEmpty(true);
            node.label = -1;
            return node;
        }
        node.classCounts = ds.getClassCounts();
        node.label = ds.getMajorityClassLabel();
        if(ds.homogeneous() || ds.getExamples().size() <= 3){
            return node;
        }
        else{
            ArrayList<DataSet> datasets = ds.splitOnAttribute(ds.getBestSplittingAttribute());
            node.attribute = ds.getBestSplittingAttribute();
            for (DataSet dataset : datasets) {
                Node temp = this.train_aux(dataset);
                node.children.add(temp);
            }
            return node;
        }
    }

    public static void main( String[] args ) {
        try {
            Evaluator evaluator = new Evaluator( new DT(), args );
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // DT::main
}

