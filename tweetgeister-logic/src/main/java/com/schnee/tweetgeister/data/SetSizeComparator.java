package com.schnee.tweetgeister.data;

import java.util.Comparator;
import java.util.Set;

public class SetSizeComparator implements Comparator<Set<?>> {

    public int compare(Set<?> o1, Set<?> o2) {
        return new Integer(o1.size()).compareTo(o2.size());
    }
 
}
