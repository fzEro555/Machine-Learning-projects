/*
 * AttributeFactory.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Scanner;

public class AttributeFactory extends Object {

    public static Attribute make( Scanner scanner ) throws Exception {
        ArrayList<String> attr_line = new ArrayList<String>();
        while (scanner.hasNext()) {
            attr_line.add(attr_line.size(), scanner.next());
        }
        if (attr_line.get(2).equals("numeric")) {
            NumericAttribute newAttr = new NumericAttribute(attr_line.get(1));
            return newAttr;
        }
        else {
            NominalAttribute newAttr = new NominalAttribute(attr_line.get(1));
            int count = 2;
            while (count < attr_line.size()) {
                newAttr.addValue(attr_line.get(count));
                count++;
            }
            return newAttr;
        }
    }
}
