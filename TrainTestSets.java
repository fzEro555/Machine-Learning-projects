/*
 * TrainTestSets.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class TrainTestSets implements OptionHandler {

    protected DataSet train;
    protected DataSet test;

    public TrainTestSets() {
        train = new DataSet();
        test = new DataSet();
    }

    public TrainTestSets( String [] options ) throws Exception {
        train = new DataSet();
        test = new DataSet();
        this.setOptions(options);
    }

    public TrainTestSets( DataSet train, DataSet test ) {
        this.train = train;
        this.test = test;
    }

    public DataSet getTrainingSet() {
        return this.train;
    }

    public DataSet getTestingSet() {
        return this.test;
    }

    public void setTrainingSet( DataSet train ) {
        this.train = train;
    }

    public void setTestingSet( DataSet test ) {
        this.test = test;
    }

    public void setOptions( String[] options ) throws Exception {
        int a = 0;
        while(a < options.length) {
            switch(options[a]) {
                case "-t":
                    train.load(options[++a]);
                    //train.cleanDataset();
                    break;
                case "-T":
                    test.load(options[++a]);
                    //test.cleanDataset();
                    break;
            }
            a++;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(train.toString());
        if (test.getExamples().size() > 0) {
            sb.append("\n");
            sb.append("test");
        }
        return sb.toString();
    }
}
