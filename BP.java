import java.io.Serializable;
import java.util.Random;

public class BP extends Classifier implements Serializable, OptionHandler{
    protected Attributes attributes;
    protected int J = 3;
    protected double rate = 0.9;
    protected double minError = 0.1;
    protected double[][] V;
    protected double[][] W;
    protected double E;
    protected boolean converged = false;
    protected int q;
    public BP(){

    }

    public BP(String[] options){
        this.setOptions(options);
    }

    public BP clone() {
        BP bp = new BP();
        return bp;
    }

    public void setOptions(String[] options) {
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
                case "-J":
                    this.J = Integer.parseInt(options[++i]) + 1;
                    break;
            }
            i++;
        }
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
        double[] h = this.H(example);
        double[] o = this.O(h);
        return o;
    }

    public void train(DataSet dataset) throws Exception{
        DataSet ds = this.Homogeneous(dataset);
        this.attributes = ds.getAttributes();
        int K = this.attributes.getClassAttribute().size();
        this.V = this.initializeMatrix(this.J - 1, this.attributes.size()-1);
        this.W = this.initializeMatrix(K, this.J);
        this.q = 0;
        this.E = 0.0;
        int iterations = 0;
        while(!this.converged) {
            if (iterations >= 50000) {
                throw new FailedToConvergeException("Failed to converge!");
            }
            iterations++;
            for(Example ex : ds.getExamples()){
                double[] h = this.H(ex);
                double[] o = this.O(h);
                double[] y = new double[K];

                double index = ex.get(this.attributes.size()-1);
                if(index == -1){
                    index = 0.0;
                }
                y[(int) index]++;
                for(int k=0; k<K; k++){
                    this.E += 0.5 * Math.pow((y[k] - o[k]), 2);
                }

                double[] delta_o = new double[K];
                for(int k=0; k<K; k++){
                    delta_o[k] = (y[k] - o[k]) * (1 - o[k]) * o[k];
                }

                double[] delta_h = new double[this.J];
                for(int j=0; j<this.J; j++){
                    double temp = 0.0;
                    for(int k=0; k<delta_o.length; k++){
                        temp += delta_o[k] * this.W[k][j];
                    }
                    delta_h[j] = h[j] * (1 - h[j]) * temp;
                }

                for(int k=0; k<K; k++){
                    for(int j=0; j<this.J; j++){
                        this.W[k][j] += this.rate * delta_o[k] * h[j];
                    }
                }

                for(int j=0; j<this.J-1; j++){
                    for(int i=0; i<this.attributes.size()-1; i++){
                        this.V[j][i] += this.rate * delta_h[j] * ex.get(i);
                    }
                }
                this.q++;
            }
            if(this.E < this.minError){
                converged = true;
            }
            else{
                this.E = 0.0;
            }
        }
    }

    public DataSet Homogeneous(DataSet dataset) throws Exception{
        DataSet ds = new DataSet();
        DataSet temp = dataset.Binary();
        for(int i=0; i<temp.getAttributes().getClassIndex(); i++){
            ds.getAttributes().add(temp.getAttributes().get(i));
        }
        ds.getAttributes().add(new NumericAttribute("bias"));
        ds.getAttributes().add(temp.getAttributes().getClassAttribute());
        ds.getAttributes().setClassIndex(ds.getAttributes().size()-1);
        for(Example ex : temp.getExamples()){
            ex.add(ex.get(temp.getAttributes().getClassIndex()));
            ex.set(temp.getAttributes().getClassIndex(),-1.0);
            ds.getExamples().add(ex);
        }
        return ds;
    }

    public double[][] initializeMatrix(int row, int column){
        double[][] matrix = new double[row][column];
        Random random = new Random();
        for(int i=0; i<row; i++){
            for(int j=0; j<column; j++){
                matrix[i][j] = 2 * random.nextDouble() - 1.0;
            }
        }
        return matrix;
    }

    public double[] H(Example example){
        double[] h = new double[this.J];
        for(int j=0; j<this.J-1; j++){
            double temp = 0.0;
            for(int i=0; i<example.size()-1; i++){
                temp += this.V[j][i] * example.get(i);
            }
            h[j] = 1.0/(1.0 + Math.exp(-1.0 * temp));
        }
        h[this.J - 1] = -1.0;
        return h;
    }

    public double[] O(double[] h){
        double[] o = new double[this.attributes.getClassAttribute().size()];
        for(int k=0; k<this.attributes.getClassAttribute().size(); k++){
            double temp = 0.0;
            for(int i=0; i<h.length; i++){
                temp += this.W[k][i] * h[i];
            }
            o[k] = 1.0/(1.0 + Math.exp(-1.0 * temp));
        }
        return o;
    }
    public static void main(String[] args) {
        try {
            Evaluator evaluator = new Evaluator(new BP(), args);
            Performance performance = evaluator.evaluate();
            System.out.println(performance);
        } // try
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } // catch
    } // BP::main
}
