package com.schnee.tweetgeister.visualization;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.util.LabelWrapper;

public class TopicNodeLabeller<V, E> implements Transformer<V, String> {

    LabelWrapper lw = new LabelWrapper(20);
    protected Forest<V, E> forest;
    
    public TopicNodeLabeller(Forest<V, E> f)
    {
        this.forest = f;
    }
    
    public String transform(V v) {
        String retval = null;
       
        if(forest.getChildCount(v) > 0){
            retval = lw.transform(v.toString());
        }

        return retval;
    }

}
