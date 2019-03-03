import java.io.Serializable;
import java.util.ArrayList;

public class Perceptron extends Classifier implements Serializable, OptionHandler{
    protected Attributes attributes;
    protected double rate = 0.9;
    protected boolean converged = false;
    protected boolean fc = false;
    protected ArrayList<Double> w;

    public Perceptron(){

    }

    public Perceptron(String[] options){
        this.setOptions(options);
    }

    public Perceptron clone(){
        Perceptron perceptron = new Perceptron();
        return perceptron;
    }

    public int classify( Example example ) throws Exception{
        return Utils.maxIndex(this.getDistribution(example));
    }

    public Performance classify( DataSet dataset ) throws Exception{
        DataSet ds = this.Homogeneous(dataset);
        Performance performance = new Performance(ds.getAttributes());
        for (Example example : ds.getExamples()) {
            double [] predictions = this.getDistribution(example);
            int actual = example.get(ds.getAttributes().getClassIndex()).intValue();
            if(actual == -1){
                actual = 0;
            }
            performance.add(actual, predictions);
        }
        return performance;
    }

    public double[] getDistribution( Example example ) throws Exception{
        double[] distribution = new double[this.attributes.getClassAttribute().size()];
        if(this.wMutiplyX(example) > 0.0)
            distribution[1] = 1.0;
        else
            distribution[0] = 1.0;
        return distribution;
    }

    public void train(DataSet dataset) throws Exception{
        DataSet ds = this.Homogeneous(dataset);
        this.attributes = ds.getAttributes();
        this.w = new ArrayList<>(this.attributes.size()-1);
        for(int i=0; i<this.attributes.size()-1; i++){
            this.w.add(0.0);
        }
        this.converged = false;
        int iterations = 0;
        while(!this.converged){
            if (iterations >= 50000) {
                throw new FailedToConvergeException("Failed to converge!");
            }
            this.converged = true;
            for(Example ex : ds.getExamples()){
                iterations++;
                if(ex.get(ds.getAttributes().getClassIndex()) * wMutiplyX(ex) <= 0){
                    this.changeW(ex);
                    this.converged = false;
                }
            }
        }
    }

    public void setOptions(String[] options){
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
                case "-fc":
                    this.fc = true;
                    break;
            }
            i++;
        }
    }

    public DataSet Homogeneous(DataSet dataset) throws Exception{
        DataSet ds = new DataSet();
        DataSet temp = dataset.Bipolar();
        for(int i=0; i<temp.getAttributes().getClassIndex(); i++){
            ds.getAttributes().add(temp.getAttributes().get(i));
        }
        ds.getAttributes().add(new NumericAttribute("bias"));
        ds.getAttributes().add(temp.getAttributes().getClassAttribute());
        ds.getAttributes().setClassIndex(ds.getAttributes().size()-1);
        for(Example ex : temp.getExamples()){
            ex.add(ex.get(temp.getAttributes().getClassIndex()));
            ex.set(temp.getAttributes().getClassIndex(),-1.0);
            if(ex.get(temp.getAttributes().getClassIndex()+1) == 0.0){
                ex.set(temp.getAttributes().getClassIndex()+1, -1.0);
            }
            ds.getExamples().add(ex);
        }
        return ds;
    }

    public double wMutiplyX(Example example){
        double sum = 0.0;
        for (int i = 0; i < example.size() - 1; i++){
            sum += example.get(i)*this.w.get(i);
        }
        return sum;
    }

    public void changeW(Example example){
        for (int i = 0; i < this.w.size(); i++)
            this.w.set(i, this.w.get(i) + this.rate * example.get(this.attributes.getClassIndex()) * example.get(i));
    }

    public static void main(String[] args) {
        try {
            Evaluator evaluator = new Evaluator(new Perceptron(), args);
            Performance performance = evaluator.evaluate();
            System.out.println(performance);
        } // try
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } // catch
    } // Perceptron::main
}
