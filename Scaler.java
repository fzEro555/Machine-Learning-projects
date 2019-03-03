/*
 * Scaler.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.util.ArrayList;

public class Scaler extends Object{

    private Attributes attributes;
    private ArrayList<Double> mins;
    private ArrayList<Double> maxs;

    public Scaler(){
        this.attributes = new Attributes();
    }

    public void configure( DataSet ds ) throws Exception{
        this.attributes = ds.getAttributes();
        this.maxs = new ArrayList<Double>(this.attributes.size());
        this.mins = new ArrayList<Double>(this.attributes.size());
        for (int i = 0; i < this.attributes.size(); i++) {
            this.maxs.add(Double.MIN_VALUE);
            this.mins.add(Double.MAX_VALUE);
        }

        for (Example ex : ds.getExamples()) {
            for (int i = 0; i < this.attributes.size(); i++) {
                if (ex.get(i) >= this.maxs.get(i)) {
                    this.maxs.set(i, ex.get(i));
                }
                if (ex.get(i) <= this.mins.get(i)) {
                    this.mins.set(i, ex.get(i));
                }
            }
        }
    }

    public DataSet scale( DataSet ds ) throws Exception{
        DataSet dataset = new DataSet(this.attributes);
        dataset.name = ds.name;
        for(Example example : ds.getExamples()){
            dataset.add(scale(example));
        }
        return dataset;
    }

    public Example scale( Example example ) throws Exception{
        Example ex = new Example(example.size());

        for (int i = 0; i < example.size(); i++) {
            ex.add(example.get(i));
            if (this.attributes.get(i) instanceof NumericAttribute) {
                Double scale;
                Double min = this.mins.get(i);
                Double max = this.maxs.get(i);

                if (example.get(i) > max) {
                    ex.set(i, new Double(1));
                }
                if (example.get(i) < min) {
                    ex.set(i, new Double(0));
                }
                else{
                    scale = (example.get(i) - min) / (max - min);
                    ex.set(i, scale);
                }
            }
        }
        return ex;
    }
}
