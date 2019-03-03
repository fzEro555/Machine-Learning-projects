import java.io.Serializable;
import java.util.ArrayList;

public class KernelPerceptron extends Classifier implements Serializable, OptionHandler{
    protected Attributes attributes;
    protected Examples examples;
    protected boolean converged = false;
    protected boolean fc = false;
    protected ArrayList<Double> alpha;
    public KernelPerceptron(){

    }

    public KernelPerceptron(String[] options ){
        this.setOptions(options);
    }

    public KernelPerceptron clone(){
        KernelPerceptron kernelPerceptron = new KernelPerceptron();
        return kernelPerceptron;
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
        if(this.sumK(example) > 0.0)
            distribution[1] = 1.0;
        else
            distribution[0] = 1.0;
        return distribution;
    }

    public void train(DataSet dataset) throws Exception{
        DataSet ds = this.Homogeneous(dataset);
        this.attributes = ds.getAttributes();
        this.examples = ds.getExamples();
        this.alpha = new ArrayList<>(this.examples.size());
        for(int i=0; i<this.examples.size(); i++){
            this.alpha.add(0.0);
        }
        this.converged = false;
        int iterations = 0;
        while(!this.converged){
            if (iterations >= 50000) {
                throw new FailedToConvergeException("Failed to converge!");
            }
            this.converged = true;
            for(int i=0; i<this.examples.size(); i++){
                iterations++;
                if(this.examples.get(i).get(ds.getAttributes().getClassIndex()) * this.sumK(this.examples.get(i)) <= 0){
                    this.alpha.set(i, this.alpha.get(i) + 1.0);
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

    public double sumK(Example example) throws Exception{
        double sumk = 0.0;
        for(int i=0; i<this.examples.size(); i++){
            Example ex = this.examples.get(i);
            sumk += this.alpha.get(i) * ex.get(this.attributes.getClassIndex()) * this.K(ex, example);
        }
        return sumk;
    }

    public double K(Example example1, Example example2) throws Exception{
        if(example1.size() != example2.size()){
            throw new Exception("Examples should have same size");
        }
        double sum = 0.0;
        for (int i=0; i<example1.size()-1; i++) {
            sum += example1.get(i)*example2.get(i);
        }
        return Math.pow(sum, 2);
    }
    public static void main(String[] args) {
        try {
            Evaluator evaluator = new Evaluator(new KernelPerceptron(), args);
            Performance performance = evaluator.evaluate();
            System.out.println(performance);
        } // try
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } // catch
    } // KernelPerceptron::main

}
