/*
 * GaussianEstimator.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class GaussianEstimator extends Estimator{
    protected Double sum = 0.0;
    protected Double sumsqr = 0.0;
    protected final static Double oneOverSqrt2PI = 1.0/Math.sqrt(2.0*Math.PI);

    public GaussianEstimator(){
        super();
    }

    public void add( Number x ) throws Exception{
        this.n += 1;
        this.sum = this.sum+ x.doubleValue();
        this.sumsqr = this.sumsqr + Math.pow(x.doubleValue(), 2);
    }

    public Double getMean(){
        double mean = this.sum / this.getN();
        return mean;
    }

    public Double getVariance(){
        double variance = (this.sumsqr - (Math.pow(sum, 2) / this.getN())) / (this.getN() - 1);
        return variance;
    }

    public Double getProbability( Number x ){

        Double mean = this.getMean();
        Double variance = this.getVariance();
        Double probability;
        probability = oneOverSqrt2PI / Math.sqrt(variance)
                * Math.exp((-1 * Math.pow(x.doubleValue() - mean, 2)) / (2 * variance));
        return probability;
    }
}
