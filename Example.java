/*
 * Example.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Example extends java.util.ArrayList<Double> {

    public Example() {
        super();
    }

    public Example( int n ) {
        super(n);
        ensureCapacity(n);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (int count = 0; count < size(); count++) {
            sb.append(get(count));
            sb.append(" ");
        }
        return sb.toString();
    }
}
