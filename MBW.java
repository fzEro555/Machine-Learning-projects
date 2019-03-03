import java.io.Serializable;
import java.util.ArrayList;

public class MBW extends Classifier implements Serializable, OptionHandler {
    protected Attributes attributes;
    protected double alpha = 1.5;
    protected double beta = 0.5;
    protected double threshold = 1.0;
    protected double u0 = 2.0;
    protected double v0 = 1.0;
    protected double M = 1.0;
    protected ArrayList<Double> v;
    protected ArrayList<Double> u;
    protected ArrayList<Double> V;
    protected ArrayList<Double> U;
    protected boolean vote = false;
    protected double Z = 0.0;
    public MBW(){

    }

    public MBW(String[] options){
        this.setOptions(options);
    }

    public MBW clone() {
        MBW mbw = new MBW();
        return mbw;
    }

    public int classify( Example example ) throws Exception{
        return Utils.maxIndex(this.getDistribution(example));
    }

    public Performance classify( DataSet dataset ) throws Exception{
        DataSet ds = this.augmentation(dataset);
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
        Example ex = this.normalization(example);
        double[] distribution = new double[this.attributes.getClassAttribute().size()];
        double y;
        if(this.vote){
            ArrayList<Double> U_temp = new ArrayList<Double>(this.U.size());
            ArrayList<Double> V_temp = new ArrayList<Double>(this.V.size());
            for(int i=0; i<this.U.size(); i++){
                U_temp.add(this.U.get(i) / this.Z);
                V_temp.add(this.V.get(i) / this.Z);
            }
            y = this.sign(U_temp, V_temp, ex);
        }
        else{
            y = this.sign(this.u, this.v, ex);
        }
        if(y > 0.0)
            distribution[1] = 1.0;
        else
            distribution[0] = 1.0;
        return distribution;
    }

    public void train(DataSet dataset) throws Exception{
        DataSet ds = this.augmentation(dataset);
        this.attributes = ds.getAttributes();
        this.u = this.initu(this.attributes.size()-1);
        this.v = this.initv(this.attributes.size()-1);
        this.U = this.initU(this.attributes.size()-1);
        this.V = this.initV(this.attributes.size()-1);
        int c = 0;
        int i = 0;
        for(Example ex : ds.getExamples()){
            ex = this.normalization(ex);
            double y_sign = this.sign(this.u, this.v, ex);
            double y = ex.get(this.attributes.getClassIndex());
            double label = (y == 0.0) ? -1.0 : 1.0;
            if(y_sign * label <= this.M){
                for(int j=0; j<this.u.size(); j++){
                    this.U.set(j, this.U.get(j) + this.u.get(j) * c);
                    this.V.set(j, this.V.get(j) + this.v.get(j) * c);
                }
                this.updateU(ex, label);
                this.updateV(ex, label);
                c = 0;
                i++;
            }
            else{
                c++;
                this.Z++;
            }
        }
    }

    public double sign(ArrayList<Double> u, ArrayList<Double> v, Example example){
        double result = multiplyX(u,example) - multiplyX(v, example) - this.threshold;
        return result;
    }

    public double multiplyX(ArrayList<Double> list, Example example){
        double sum = 0.0;
        for (int i = 0; i < example.size() - 1; i++){
            sum += example.get(i) * list.get(i);
        }
        return sum;
    }


    public DataSet augmentation(DataSet dataset) throws Exception{
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
            ex.set(temp.getAttributes().getClassIndex(), 1.0);
            ds.getExamples().add(ex);
        }
        return ds;
    }

    public Example normalization(Example example){
        double sum = 0.0;
        for(int i=0; i<example.size(); i++){
            sum += example.get(i);
        }
        for(int i=0; i<example.size(); i++){
            example.set(i, example.get(i)/sum);
        }
        return example;
    }

    public ArrayList<Double> initu(int length){
        ArrayList<Double> U = new ArrayList<Double>(length);
        for(int i=0; i<length; i++){
            U.add(this.u0);
        }
        return U;
    }

    public ArrayList<Double> initv(int length){
        ArrayList<Double> V = new ArrayList<Double>(length);
        for(int i=0; i<length; i++){
            V.add(this.v0);
        }
        return V;
    }

    public ArrayList<Double> initU(int length){
        ArrayList<Double> U = new ArrayList<Double>(length);
        for(int i=0; i<length; i++){
            U.add(0.0);
        }
        return U;
    }

    public ArrayList<Double> initV(int length){
        ArrayList<Double> V = new ArrayList<Double>(length);
        for(int i=0; i<length; i++){
            V.add(0.0);
        }
        return V;
    }

    public void updateU(Example example, double label){
        for(int i=0; i<this.u.size(); i++){
            if(example.get(i) > 0){
                if(label > 0){
                    this.u.set(i, this.u.get(i) * alpha * (1 + example.get(i)));
                }
                else{
                    this.u.set(i, this.u.get(i) * beta * (1 - example.get(i)));
                }
            }
        }
    }

    public void updateV(Example example, double label){
        for(int i=0; i<this.v.size(); i++){
            if(example.get(i) > 0){
                if(label > 0){
                    this.v.set(i, this.v.get(i) * beta * (1 - example.get(i)));
                }
                else{
                    this.v.set(i, this.v.get(i) * alpha * (1 + example.get(i)));
                }
            }
        }
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
                case "-v":
                    this.vote = true;
                    break;
            }
            i++;
        }
    }

    public static void main(String[] args) {
        try {
            Evaluator evaluator = new Evaluator(new MBW(), args);
            Performance performance = evaluator.evaluate();
            System.out.println(performance);
        } // try
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } // catch
    } // MBW::main
}
