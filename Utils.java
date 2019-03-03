/*
 * Utils.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Utils{
    public static int maxIndex( double[] p ){
        int max_index = 0;
        double num = p[0];
        for (int i = 0; i < p.length; i++) {
            if (num < p[i]) {
                num = p[i];
                max_index = i;
            }
        }
        return max_index;
    }

    public static double u25( int n, int x ){
        double z = 0.6925;
        double p = (x + 0.5 + z*z*0.5 + Math.sqrt(z*z*(x+0.5)*(1-(x+0.5)/n) + z*z*0.25)) / (n + z*z);
        return p;
    }
}
