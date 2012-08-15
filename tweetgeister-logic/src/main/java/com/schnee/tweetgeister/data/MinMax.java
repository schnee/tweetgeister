package com.schnee.tweetgeister.data;

import java.util.Set;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.util.Distance;

public class MinMax {

    private double min = 1.0;

    private double max = 0.0;

    private Set<CharSequence> set;

    private TfIdfDistance distance;

    public MinMax() {
        super();
        // TODO Auto-generated constructor stub
    }

    public MinMax(Set<CharSequence> set, TfIdfDistance distance) {
        this.set = set;
        this.distance = distance;

        for (CharSequence s1 : set) {
            for (CharSequence s2 : set) {
                if (s1.toString().compareTo(s2.toString()) < 0) {
                    double d = distance.distance(s1, s2);
                    updateMax(d);
                    updateMin(d);
                }
            }
        }
    }

    private double updateMin(double d) {
        if (d < min) {
            min = d;
        }

        return min;
    }

    private double updateMax(double d) {
        if (d > max) {
            max = d;
        }
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getSpread() {
        return max - min;
    }
    
    public int getSize() {
        return set.size();
    }
    
    public String toString() {
        return String.format("size  = %1d min = %2f max = %3f spread = %4f", set.size(), min, max, getSpread());
    }

}
