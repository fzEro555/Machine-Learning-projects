/*
 * IBk.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.io.Serializable;

public class IBk extends Classifier implements Serializable, OptionHandler{
    protected DataSet dataset;
    protected Scaler scaler;
    protected int k = 3;

    public IBk(){
        this.dataset = new DataSet();
        this.scaler = new Scaler();
    }
    public IBk( String[] options ) throws Exception{
        this.dataset = new DataSet();
        this.scaler = new Scaler();
        this.setOptions(options);
    }
    public Performance classify( DataSet dataset ) throws Exception{
        Performance performance = new Performance(dataset.getAttributes());
        for (Example example : dataset.getExamples()) {
            double [] predictions = this.getDistribution(example);
            int classtype = Double.valueOf(example.get(dataset.getAttributes().getClassIndex())).intValue();
            performance.add(classtype, predictions);
        }
        return performance;
    }

    public int classify( Example query ) throws Exception{
        return Utils.maxIndex(this.getDistribution(query));
    }

    public Classifier clone(){
        IBk ibk = new IBk();
        ibk.setK(this.k);
        return ibk;
    }

    public double[] getDistribution( Example query ) throws Exception{
        if (this.dataset.getExamples().size() == 0) {
            throw new Exception("DataSet has no example");
        }
        DataSet ds = this.scaler.scale(this.dataset);
        Example scaled_ex = this.scaler.scale(query);
        double[] distribution = new double[this.dataset.attributes.getClassAttribute().size()];
        for (int i = 0; i < this.dataset.attributes.getClassAttribute().size(); i++) {
            distribution[i] = 0.0;
        }
        double[] distance = new double[k];
        int[] ex_index = new int[k];
        double[] KNN = new double[k];

        for (int i = 0; i < k; i++) {
            distance[i] = Double.MAX_VALUE;
            ex_index[i] = 0;
            KNN[i] = 0.0;
        }

        int index = 0;
        for (Example ex : ds.getExamples()) {
            double distance_temp = 0.0;
            for (int i = 0; i < this.dataset.getAttributes().size() - 1; i++) {
                if (this.dataset.getAttributes().get(i) instanceof NumericAttribute) {
                    distance_temp += Math.pow(ex.get(i) - scaled_ex.get(i), 2);
                }
                else {
                    if (!scaled_ex.get(i).equals(ex.get(i))) {
                        distance_temp += 1;
                    }
                }
            }
            distance_temp = Math.sqrt(distance_temp);
            int maxIndex = Utils.maxIndex(distance);
            if (distance_temp <= distance[maxIndex]) {
                distance[maxIndex] = distance_temp;
                ex_index[maxIndex] = index;
            }
            index += 1;
        }

        for (int i = 0; i < k; i++) {
            KNN[i] = this.dataset.getExamples().get(ex_index[i])
                    .get(this.dataset.getAttributes().getClassIndex());
            distribution[(int) KNN[i]] += 1;
        }

        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = distribution[i] / (double) k;
        }

        return distribution;
    }

    public void setK( int k ){
        this.k = k;
    }

    public void setOptions( String args[] ){
        if (args.length == 0) {
            try{
                throw new Exception("Empty arguments");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        int i = 0;
        while(i < args.length) {
            switch(args[i]) {
                case "-k":
                    this.k = Integer.valueOf(args[++i]);
                    break;
            }
            i++;
        }
    }

    public void train( DataSet dataset ) throws Exception{
        this.dataset = dataset;
        this.scaler.configure(dataset);
    }

    public static void main( String[] args ) {
        try {
            Evaluator evaluator = new Evaluator( new IBk(), args );
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // IBk::main
}

