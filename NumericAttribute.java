/*
 * NumericAttribute.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class NumericAttribute extends Attribute {

    public NumericAttribute() { }

    public NumericAttribute( String name ) {
        super(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( super.toString() );
        sb.append(" numeric");
        return sb.toString();
    }

    public boolean validValue( Double value ) {
        return true;
    }

}
