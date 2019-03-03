/*
 * Evaluator.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */
import java.util.Random;

public class Evaluator implements OptionHandler{
    private long seed = 2026875034;
    private Random random;
    private int folds = 10;
    private Classifier classifier;
    private TrainTestSets tts;

    public Evaluator(){

    }

    public Evaluator( Classifier classifier, String[] options ) throws Exception{
        this.classifier = classifier;
        this.tts = new TrainTestSets(options);
        this.setOptions(options);
    }

    public Performance evaluate() throws Exception{
        if(this.tts.getTestingSet().getExamples().size() != 0){
            this.tts.getTestingSet().cleanDataset();
            this.tts.getTrainingSet().cleanDataset();
            this.classifier.train(this.tts.getTrainingSet());
            return this.classifier.classify(this.tts.getTestingSet());
        }
        else{
            this.tts.getTrainingSet().partitions = new int[this.tts.getTrainingSet().getExamples().size()];
            for(int i=0; i<this.tts.getTrainingSet().getExamples().size(); i++){
                this.tts.getTrainingSet().partitions[i] = this.tts.getTrainingSet().random.nextInt(this.folds);
            }
            Performance performance = new Performance(this.tts.getTrainingSet().getAttributes());
            this.tts.getTrainingSet().setFolds(this.folds);
            this.tts.getTestingSet().setFolds(folds);
            for (int i = 0; i < this.folds; i++) {
                TrainTestSets ttset = this.tts.getTrainingSet().getCVSets(i);
                ttset.getTrainingSet().cleanDataset();
                ttset.getTestingSet().cleanDataset();
                this.classifier.train(ttset.getTrainingSet());
                performance.add(this.classifier.classify(ttset.getTestingSet()));
            }
            return performance;
        }
    }

    public long getSeed(){
        return this.seed;
    }

    public void setOptions( String args[] ) throws Exception{
        if (args.length == 0) {
            throw new Exception("Empty arguments");
        }
        this.tts = new TrainTestSets(args);
        this.classifier.setOptions(args);
        int i = 0;
        while(i < args.length) {
            switch(args[i]) {
                case "-x":
                    this.folds = Integer.parseInt(args[++i]);
                    break;
                case "-s":
                    this.setSeed(Long.parseLong(args[++i]));
                    break;
                case "-p":
                    DataSet train = this.tts.getTrainingSet();
                    double p = Double.parseDouble(args[++i]);
                    this.tts = train.splitTrainTest(p);
                    break;
            }
            i++;
        }
    }

    public void setSeed( long seed ){
        this.seed = seed;
    }
}
