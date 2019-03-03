/*
 * CategoricalEstimator.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */
import java.util.ArrayList;

public class CategoricalEstimator extends Estimator{
    protected ArrayList<Integer> dist;

    public CategoricalEstimator(){
        this.dist = new ArrayList<Integer>();
    }

    public CategoricalEstimator( Integer k ){
        this.dist = new ArrayList<Integer>();
        for (int i = 0; i < k; i++) {
            this.dist.add(0);
        }
    } // number of categories

    public void add( Number x ) throws Exception{
        this.n += 1;
        this.dist.set(x.intValue(), this.dist.get(x.intValue()) + 1);
    }

    public Double getProbability( Number x ){
        double probability = (this.dist.get(x.intValue()) + 1) / (this.getN().doubleValue() + this.dist.size());
        return probability;
    }
}
