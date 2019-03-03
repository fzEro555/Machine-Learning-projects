/*
 * NaiveBayes.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.util.ArrayList;
import java.io.Serializable;


public class NaiveBayes extends Classifier implements Serializable, OptionHandler{
    protected Attributes attributes;
    protected CategoricalEstimator classDistribution;
    protected ArrayList< ArrayList<Estimator> > classConditionalDistributions;

    public NaiveBayes(){
        this.attributes = new Attributes();
        this.classDistribution = new CategoricalEstimator();
        this.classConditionalDistributions = new ArrayList<ArrayList<Estimator>>();
    }

    public NaiveBayes( String[] options ) throws Exception{
        this.attributes = new Attributes();
        this.classDistribution = new CategoricalEstimator();
        this.classConditionalDistributions = new ArrayList<ArrayList<Estimator>>();
        this.setOptions(options);
    }

    public Performance classify( DataSet dataset ) throws Exception{
        Performance performance = new Performance(dataset.getAttributes());
        for (Example example : dataset.getExamples()) {
            double [] pre = this.getDistribution(example);
            int actual = Double.valueOf(example.get(dataset.getAttributes().getClassIndex())).intValue();
            performance.add(actual, pre);
        }
        return performance;
    }

    public int classify( Example example ) throws Exception{
        return Utils.maxIndex(this.getDistribution(example));
    }

    public Classifier clone(){
        NaiveBayes bayes = new NaiveBayes();
        bayes.attributes = this.attributes;
        return bayes;
    }

    public double[] getDistribution( Example example ) throws Exception{
        double[] distribution = new double[this.attributes.getClassAttribute().size()];
        double sum = 0.0;

        for(int i=0; i<this.attributes.getClassAttribute().size(); i++){
            double temp = 1.0;
            for (int j = 0; j < this.attributes.size() - 1; j++) {
                temp = temp * this.classConditionalDistributions.get(i).get(j).getProbability(example.get(j));
            }
            distribution[i] = this.classDistribution.getProbability(i) * temp;
        }
        return distribution;
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
    }

    public void train( DataSet dataset ) throws Exception{
        this.attributes = dataset.getAttributes();
        this.classDistribution = new CategoricalEstimator(this.attributes.getClassAttribute().size());
        for (int i = 0; i < this.attributes.getClassAttribute().size(); i++) {
            ArrayList<Estimator> estimators = new ArrayList<Estimator>();
            for (int j = 0; j < dataset.attributes.size() - 1; j++) {
                if (this.attributes.get(j) instanceof NumericAttribute) {
                    estimators.add(new GaussianEstimator());
                }
                else {
                    estimators.add(new CategoricalEstimator(this.attributes.get(j).size()));
                }
            }
            this.classConditionalDistributions.add(estimators);
        }

        for(Example ex : dataset.getExamples()){
            this.classDistribution.add(ex.get(this.attributes.getClassIndex()));
            for(int i = 0; i < this.attributes.size() - 1; i++){
                this.classConditionalDistributions.get(ex.get(this.attributes.getClassIndex()).intValue()).get(i).add(ex.get(i));
            }
        }
    }

    public static void main( String[] args ) {
        try {
            Evaluator evaluator = new Evaluator( new NaiveBayes(), args );
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // NaiveBayes::main
}

