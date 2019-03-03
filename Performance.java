/*
 * Performance.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.util.ArrayList;

public class Performance extends Object{
    private Attributes attributes;
    private int[][] confusionMatrix;
    private int corrects = 0;
    private double sum = 0.0;
    private double sumSqr = 0.0;
    private int c;                // number of classes
    private int n = 0;            // number of predictions
    private int m = 0;            // number of additions
    private double f1 = 0.0;
    public ArrayList<Double> positives;
    public ArrayList<Double> negatives;
    private double AUCsum = 0.0;
    private double AUCsumSqr = 0.0;

    public Performance(Attributes attributes) throws Exception{
        this.attributes = attributes;
        if(this.attributes.get(this.attributes.getClassIndex()) instanceof NominalAttribute)
            this.c = this.attributes.get(this.attributes.getClassIndex()).size();
        this.confusionMatrix = new int[c][c];
        for(int i = 0; i < this.c; i++){
            for(int j = 0; j < this.c; j++){
                this.confusionMatrix[i][j] = 0;
            }
        }
        this.positives = new ArrayList<>();
        this.negatives = new ArrayList<>();
    }

    public void add(int actual, double [] predictions){
        int pre = Utils.maxIndex(predictions);
        this.n += 1;
        this.confusionMatrix[actual][pre] += 1;
        if (actual == pre) {
            this.corrects += 1;
        }
        if(actual == 0)
            this.positives.add(predictions[0]);
        if(actual == 1)
            this.negatives.add(predictions[0]);
    }

    public void add(Performance p) throws Exception{
        if(this.c != p.c)
            throw new Exception("The class number of two performances should be same");
        this.m += 1;
        this.n = this.n + p.n;
        for(int i=0; i<p.confusionMatrix.length; i++){
            for(int j=0; j<p.confusionMatrix[i].length; j++){
                this.confusionMatrix[i][j] = this.confusionMatrix[i][j] + p.confusionMatrix[i][j];
            }
        }
        this.f1 = this.f1 + p.getF1();
        this.corrects = this.corrects + p.corrects;
        this.sum = this.sum + p.getAccuracy();
        this.sumSqr = this.sumSqr + Math.pow(p.getAccuracy(), 2);
        this.AUCsum = this.AUCsum + p.getAUC();
        this.AUCsumSqr = this.AUCsumSqr + Math.pow(p.getAUC(),2);
        for(Double negative : p.negatives)
            this.negatives.add(negative);
        for(Double positive : p.positives)
            this.positives.add(positive);
    }

    public double getAccuracy(){
        if(this.n == 0)
            return 0.0;
        else
            return (1.0 * this.corrects) / (this.n);
    }

    public double getSDAcc(){
        if(this.m == 0)
            return 0.0;
        else
            return Math.sqrt((this.sumSqr - (Math.pow(sum, 2) / this.m)) / (this.m - 1));
    }

    public double getAUC(){
        if(this.m <= 1){
            double temp = 0.0;
            for(Double negative : this.negatives) {
                for (Double positive : this.positives) {
                    if (negative < positive)
                        temp = temp + 1.0;
                    else if (negative == positive)
                        temp = temp + 0.5;
                }
            }
            if(this.positives.size()> 0 && this.negatives.size() > 0)
                return temp / (double) this.positives.size() * this.negatives.size();
            else
                return 0.0;
        }
        else
            return (this.AUCsum / this.m * 1.0);
    }

    public double getSDAUC(){
        if(this.m == 0)
            return 0.0;
        else
            return Math.sqrt((this.AUCsumSqr - (Math.pow(this.AUCsum, 2) / this.m)) / (this.m - 1));
    }

    public double getF1(){
        double tp = this.confusionMatrix[1][1];
        double fp = this.confusionMatrix[0][1];
        double fn = this.confusionMatrix[1][0];
        double precision;
        double recall;
        if(tp == 0.0){
            precision = 0.0;
            recall = 0.0;
        }
        else{
            precision = tp / (tp + fp);
            recall = tp / (tp + fn);
        }
        double f1;
        if(precision + recall == 0.0){
            f1 = 0.0;
        }
        else{
            f1 = 2.0 * precision * recall / (recall + precision);
        }
        return f1;
    }
    public double getAvgF1(){
        if(this.m == 0){
            return this.getF1();
        }
        else{
            return this.f1 / this.m;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (this.m == 0) {
            sb.append("Accuracy: " + this.getAccuracy() + "\n");
            sb.append("AUC: " + this.getAUC() + "\n");
            sb.append("F1: " + this.getAvgF1() * 100.00 + "%\n");
        } else {
            sb.append("Accuracy: " + this.getAccuracy() + "\n");
            sb.append("SDAcc: " + this.getSDAcc() + "\n");
            sb.append("AUC: " + this.getAUC() + "\n");
            sb.append("SDAUC: " + this.getSDAUC() + "\n");
            sb.append("F1: " + this.getAvgF1() * 100.00 + "%\n");
        }
        return sb.toString();
    }
}
