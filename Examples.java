/*
 * Examples.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Scanner;

public class Examples extends ArrayList<Example> {

    private Attributes attributes;
    private int[] classCounts;

    public Examples( Attributes attributes ) {
        this.attributes = attributes;
    }

    public void parse( Scanner scanner ) throws Exception {
        String line;
        while(scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.isEmpty()) {
                continue;
            }
            Scanner lineScanner = new Scanner(line);
            ArrayList<String> ex_line = new ArrayList<String>();
            while (lineScanner.hasNext()) {
                ex_line.add(ex_line.size(), lineScanner.next());
            }
            Example ex = new Example(ex_line.size());
            for (int count = 0; count < ex_line.size(); count++) {
                if (attributes.get(count) instanceof NumericAttribute) {
                    ex.add(Double.parseDouble(ex_line.get(count)));
                }
                else {
                    NominalAttribute inst = (NominalAttribute) attributes.get(count);
                    double val = (double) inst.getIndex(ex_line.get(count));
                    ex.add(val);
                }
            }
            add(ex);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("@examples");
        sb.append("\n\n");
        for (int count = 0; count < size(); count++) {
            for (int i = 0; i < attributes.size(); i++) {
                Example ex = get(count);
                double index = ex.get(i);
                if (attributes.get(i) instanceof NumericAttribute) {
                    sb.append(index);
                }
                else {
                    int nomIndex = (int) index;
                    NominalAttribute nomA = (NominalAttribute) attributes.get(i);
                    sb.append(nomA.getValue(nomIndex));
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int[] getClassCounts(){
        int[] classCounts = new int[this.attributes.getClassAttribute().size()];
        for (Example ex : this){
            classCounts[(ex.get(this.attributes.getClassIndex())).intValue()] += 1;
        }
        return classCounts;
    }
//    public boolean add( Example example ){
//        add(example);
//        return add(example);
//    }
}
