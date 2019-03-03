/*
 * Attributes.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Scanner;

public class Attributes {

    private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    private boolean hasNumericAttributes = false;
    private int classIndex;

    public Attributes() {
        this.attributes = new ArrayList<Attribute>();
        this.classIndex = 0;
    }

    public void add( Attribute attribute ) {
        attributes.add(attribute);
        if (attribute instanceof NumericAttribute) {
            hasNumericAttributes = true;
        }
    }

    public int getClassIndex() {
        return classIndex;
    }

    public boolean getHasNumericAttributes() {
        return hasNumericAttributes;
    }

    public Attribute get( int i ) {
        return attributes.get(i);
    }

    public Attribute getClassAttribute() {
        return attributes.get(classIndex);
    }

    public int getIndex( String name ) throws Exception {
        for (int count = 0; count < attributes.size(); count++) {
            if (name.equals(attributes.get(count).getName())) {
                return count;
            }
        }
        throw new Exception("Name value is not valid");
    }

    public int size() {
        return attributes.size();
    }

    public void parse( Scanner scanner ) throws Exception {
        String line =  scanner.nextLine();
        AttributeFactory af = new AttributeFactory();
        while(testLine(line)) {
            Scanner lineScanner = new Scanner(line);
            add(af.make(lineScanner));
            line = scanner.nextLine();
        }
        classIndex = attributes.size() - 1;
    }

    public boolean testLine(String line) {
        Scanner scanner = new Scanner(line);
        ArrayList<String> attr_line = new ArrayList<String>();
        while (scanner.hasNext()) {
            attr_line.add(attr_line.size(), scanner.next());
        }
        if (attr_line.size() == 0)
            return false;
        if (attr_line.get(0).equals("@attribute")) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setClassIndex( int classIndex ) throws Exception {
        if (classIndex < 0 || classIndex > size()) {
            throw new Exception("class indes is out of range");
        }
        this.classIndex = classIndex;
    }

    public String toString() {
        StringBuilder stb = new StringBuilder("");
        for (int count = 0; count < attributes.size(); count++) {
            stb.append(attributes.get(count).toString());
            stb.append("\n");
        }
        return stb.toString();
    }
}
