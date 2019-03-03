/*
 * NominalAttribute.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

import java.util.ArrayList;

public class NominalAttribute extends Attribute {

    private ArrayList<String> domain;

    public NominalAttribute() {
        this.domain = new ArrayList<String>();
    }

    public NominalAttribute(String name) {
        super(name);
        this.domain = new ArrayList<String>();
    }

    public void addValue( String value ) {
        domain.add(value);
    }

    public ArrayList<String> getdomain(){
        return this.domain;
    }

    @Override
    public int size() {
        return domain.size();
    }

    public String getValue( int index ) {
        return domain.get(index);
    }

    public int getIndex( String value ) throws Exception {
        for (int count = 0; count < domain.size(); count++) {
            if (value.equals(getValue(count))) {
                return count;
            }
        }
        throw new Exception("NominalAttribute: Value is not in Domain.");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( super.toString() );
        for (int count = 0; count < domain.size(); count++) {
            sb.append(" " + getValue(count));
        }
        return sb.toString();
    }

    public boolean validValue( String value ) {
        boolean valid = false;
        for (int count = 0; count < domain.size(); count++) {
            if (value.equals(getValue(count))) {
                valid = true;
                return valid;
            }
        }
        return valid;
    }
}
