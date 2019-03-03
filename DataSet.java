/*
 * DataSet.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import javax.xml.crypto.Data;
import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
public class DataSet {

    protected String name;
    protected Attributes attributes = null;
    protected Examples examples = null;
    protected Random random;
    protected int folds = 10;
    protected int[] partitions = null;

    public DataSet() {
        this.attributes = new Attributes();
        this.examples = new Examples(attributes);
        this.random = new Random();
    }

    public DataSet( Attributes attributes ) {
        this.attributes = attributes;
        this.examples = new Examples(attributes);
        this.random = new Random();
    }

    public void add( Example example ) {
        examples.add(example);
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public Examples getExamples() {
        return this.examples;
    }

    public boolean getHasNumericAttributes() {
        return attributes.getHasNumericAttributes();
    }

    public void load( String filename ) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        parse(sc);
    }

    private void parse( Scanner scanner ) throws Exception {
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        String ds_title = lineScanner.next();
        String ds_name = lineScanner.next();
        this.name = ds_name;
        line = scanner.nextLine();
        attributes.parse(scanner);
        line = scanner.nextLine();
        line = scanner.nextLine();
        examples.parse(scanner);
    }

    public void setRandom( Random random ) {
        random = new Random();
    }

    public TrainTestSets getCVSets( int p ) throws Exception{
        if(this.partitions == null){
            throw new Exception("partition is empty");
        }
        DataSet train = new DataSet(this.getAttributes());
        DataSet test = new DataSet(this.getAttributes());
        train.name = this.name;
        test.name = this.name;
        train.setFolds(this.getFolds());
        test.setFolds(this.getFolds());

        for (int i = 0; i < this.examples.size(); i++) {
            if (this.partitions[i] == p) {
                test.add(this.examples.get(i));
            }
            else {
                train.add(this.examples.get(i));
            }
        }

        return new TrainTestSets(train, test);
    }


    public int getFolds(){
        return this.folds;
    }
    public void setFolds( int folds ) throws Exception{
        this.folds = folds;
    }

    public boolean isEmpty(){
        if(this.getExamples().size() == 0)
            return true;
        else
            return false;
    }

    public double entropy(){
        double entropy = 0.0;
        if(this.isEmpty())
            return entropy;
        double[] temp = new double[this.getAttributes().getClassAttribute().size()];
        for(Example ex : this.getExamples()){
            for(int i=0; i<this.getAttributes().getClassAttribute().size(); i++){
                if(ex.get(this.getAttributes().getClassIndex()).intValue() == i){
                    temp[i] = temp[i] + 1.0;
                }
            }
        }
        for(int i=0; i<this.getAttributes().getClassAttribute().size(); i++){
            double p = temp[i] / this.getExamples().size();
            if(p > 0)
                entropy = entropy + ((-p) * Math.log(p)) / Math.log(2);
        }
        return entropy;
    }


    public double gainRatio( int attribute ) throws Exception{
        double gain = 0.0;
        double splitinformation = 0.0;
        ArrayList<DataSet> datasets = this.splitOnAttribute(attribute);
        for(DataSet ds : datasets){
            double p = (double) ds.getExamples().size() / this.getExamples().size();
            gain = gain + p * ds.entropy();
            if(p > 0)
                splitinformation = splitinformation + ((-p) * Math.log(p)) / Math.log(2);
        }
        gain = this.entropy() - gain;
        if(splitinformation != 0)
            return gain / splitinformation;
        else
            return 0.0;
    }

    public int getBestSplittingAttribute() throws Exception{
        int bestSplittingAttribute = 0;
        double maxGainRatio = 0.0;
        for(int i=0; i<this.getAttributes().size()-1; i++){
            if(this.gainRatio(i) > maxGainRatio){
                maxGainRatio = this.gainRatio(i);
                bestSplittingAttribute = i;
            }
        }
        return bestSplittingAttribute;
    }

    public ArrayList<DataSet> splitOnAttribute( int attribute ) throws Exception{
        ArrayList<DataSet> ds = new ArrayList<DataSet>();
        for(int i=0; i<this.getAttributes().get(attribute).size(); i++)
            ds.add(new DataSet(this.getAttributes()));
        for(Example ex : this.getExamples())
            ds.get(ex.get(attribute).intValue()).add(ex);
        return ds;
    }

    public boolean homogeneous() throws Exception{
        if(this.isEmpty())
            return true;
        else{
            double temp = this.getExamples().get(0).get(this.getAttributes().getClassIndex());
            for(Example ex : this.getExamples()){
                if(temp != ex.get(this.getAttributes().getClassIndex()))
                    return false;
            }
            return true;
        }
    }

    public int[] getClassCounts() throws Exception{
        return this.getExamples().getClassCounts();
    }

    public int getMajorityClassLabel() throws Exception{
        int[] classCounts = this.getClassCounts();
        int majorityClassLabel = 0;
        for(int i=0; i<classCounts.length; i++){
            if(classCounts[i] > classCounts[majorityClassLabel])
                majorityClassLabel = i;
        }
        return majorityClassLabel;
    }

    public TrainTestSets splitTrainTest(double p){
        DataSet train = new DataSet(this.getAttributes());
        DataSet test = new DataSet(this.getAttributes());
        if(p == 1.0){
            return new TrainTestSets(train,test);
        }
        else{
            Collections.shuffle(this.getExamples());
            int size = ((int) Math.ceil(p * this.getExamples().size()));
            int count = 0;
            for (Example e: this.getExamples()) {
                if (count < size){
                    train.getExamples().add(e);
                }
                else {
                    test.getExamples().add(e);
                }
                count += 1;
            }
            return new TrainTestSets(train,test);
        }
    }

    public DataSet Binary() throws Exception{
        DataSet dataset = new DataSet();
        Attributes attributes = new Attributes();

        for(int i=0; i<this.getAttributes().size()-1; i++){
            if(this.getAttributes().get(i) instanceof NumericAttribute){
                attributes.add(this.getAttributes().get(i));
            }
            else{
                int length = Integer.toBinaryString(this.getAttributes().get(i).size()-1).length();
                for(int j=0; j<length; j++){
                    NumericAttribute attribute = new NumericAttribute(this.getAttributes().get(i).getName() + j);
                    attributes.add(attribute);
                }
            }
        }
        attributes.add(this.getAttributes().getClassAttribute());
        attributes.setClassIndex(attributes.size()-1);
        dataset.attributes = attributes;
        for(Example ex : this.getExamples()){
            Example example = new Example(dataset.getAttributes().size());
            for(int i=0; i<this.getAttributes().size()-1; i++){
                if(this.getAttributes().get(i) instanceof NumericAttribute){
                    example.add(ex.get(i));
                }
                else{
                    String binary = Integer.toBinaryString(ex.get(i).intValue());
                    int length = Integer.toBinaryString(this.getAttributes().get(i).size()-1).length();
                    int temp_length = binary.length();
                    for(int j=0; j<length-temp_length; j++){
                        binary = "0" + binary;
                    }
                    for(int k=length-1; k>=0; k--){
                        example.add((double)
                                Integer.parseInt(String.valueOf(binary.charAt(k))));
                    }
                }
            }
            example.add(ex.get(this.getAttributes().getClassIndex()));
            dataset.getExamples().add(example);
        }
        return dataset;
    }

    public DataSet Bipolar() throws Exception{
        DataSet dataset = new DataSet();
        Attributes attributes = new Attributes();

        for(int i=0; i<this.getAttributes().size()-1; i++){
            if(this.getAttributes().get(i) instanceof NumericAttribute){
                attributes.add(this.getAttributes().get(i));
            }
            else{
                int length = Integer.toBinaryString(this.getAttributes().get(i).size()-1).length();
                for(int j=0; j<length; j++){
                    NumericAttribute attribute = new NumericAttribute(this.getAttributes().get(i).getName() + j);
                    attributes.add(attribute);
                }
            }
        }
        attributes.add(this.getAttributes().getClassAttribute());
        attributes.setClassIndex(attributes.size()-1);
        dataset.attributes = attributes;

        for(Example ex : this.getExamples()){
            Example example = new Example(dataset.getAttributes().size());
            for(int i=0; i<this.getAttributes().size()-1; i++){
                if(this.getAttributes().get(i) instanceof NumericAttribute){
                    example.add(ex.get(i));
                }
                else{
                    String binary = Integer.toBinaryString(ex.get(i).intValue());
                    int length = Integer.toBinaryString(this.getAttributes().get(i).size()-1).length();
                    int temp_length = binary.length();
                    for(int j=0; j<length-temp_length; j++){
                        binary = "0" + binary;
                    }
                    for(int k=length-1; k>=0; k--){
                        if(String.valueOf(binary.charAt(k)).equals("1")){
                            example.add(1.0);
                        }
                        else{
                            example.add(-1.0);
                        }
                    }
                }
            }
            example.add(ex.get(this.getAttributes().getClassIndex()));
            dataset.getExamples().add(example);
        }
        return dataset;
    }

    public void cleanDataset(){
        if(this.name.trim().equals("nursery")){
            for(Example ex : this.getExamples()){
                if(ex.get(this.getAttributes().getClassIndex()) == 3.0){
                    ex.set(this.getAttributes().getClassIndex(), 0.0);
                }
                else{
                    ex.set(this.getAttributes().getClassIndex(), 1.0);
                }
            }
        }
        if(this.name.trim().equals("house-votes-84")){
            Examples examples = new Examples(this.getAttributes());
            for(Example ex : this.getExamples()){
                if(ex.contains(2.0)){
                    continue;
                }
                examples.add(ex);
            }
            this.examples = examples;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("@dataset ");
        sb.append(name + "\n\n");
        sb.append(attributes.toString());
        sb.append("\n");
        sb.append(examples.toString());
        return sb.toString();
    }

}

