/*
 * Attribute.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Attribute extends Object {

    protected String name;

    public Attribute() { }

    public Attribute( String name ) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int size() {
        return 0;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String toString() {
        return "@attribute " + name;
    }

}
