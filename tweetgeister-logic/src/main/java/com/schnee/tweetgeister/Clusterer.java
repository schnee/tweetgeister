/**
 * 
 */
package com.schnee.tweetgeister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.aliasi.cluster.CompleteLinkClusterer;
import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.HierarchicalClusterer;
import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.ObjectToCounterMap;
import com.schnee.tweetgeister.data.Node;
import com.schnee.tweetgeister.data.TGTweet;
import com.schnee.tweetgeister.data.Tree;

/**
 * @author Brent Schneeman
 *
 */
public class Clusterer {

    private Set<CharSequence> inputSet;

    private TokenizerFactory tf;

    private TfIdfDistance distance;

    public Clusterer(Set<CharSequence> inputSet, TokenizerFactory tf) {
        super();
        this.inputSet = inputSet;
        this.tf = tf;

        distance = new TfIdfDistance(tf);

        for (CharSequence charSequence : inputSet) {
            distance.handle(charSequence);
        }

    }

    public TokenizerFactory getTf() {
        return tf;
    }

    public TfIdfDistance getDistance() {
        return distance;
    }

    public Tree<CharSequence> buildTree() {

        //get the top-level topic - howabout the most frequent term?
        Set<String> termSet = distance.termSet();

        ObjectToCounterMap<String> termCounts = new ObjectToCounterMap<String>();
        for (String string : termSet) {
            int count = distance.docFrequency(string);
            termCounts.set(string, count);
        }

        List<String> keysOrderedByCount = termCounts.keysOrderedByCountList();

        Tree<CharSequence> mindmap = new Tree<CharSequence>();

        Node<CharSequence> root = new Node<CharSequence>();
        mindmap.setRootElement(root);

        root.setData(keysOrderedByCount.get(0));

        fillTree(distance, root, inputSet, .9);

        return mindmap;
    }

    private void fillTree(final TfIdfDistance distance, Node<CharSequence> theNode, Set<CharSequence> is,
            final double partitionDistance) {
        Set<Set<CharSequence>> clusters = getClusters(distance, is, partitionDistance);

        double pd = partitionDistance;
        if (partitionDistance < 1) {
            double oneMinus = 1 - pd;
            oneMinus = oneMinus * .66;
            pd = 1 - oneMinus;
            //partitionDistance = mm1.getMax() * 0.75;
        } else {
            pd = 1;
        }
        //MinMax mm = new MinMax(is, distance);
        // System.out.println(mm);
        if (clusters.size() == 1) {
            for (Set<CharSequence> set : clusters) {
                if (set.size()   < 3) {
                    //ok, it is small'ish - don't go any farther
                    for (CharSequence tweet : set) {
                        Node<CharSequence> tinyTweet = new Node<CharSequence>(tweet);
                        theNode.addChild(tinyTweet);
                    }
                } else {

                    TfIdfDistance dist = new TfIdfDistance(tf);
                    for (CharSequence tweet : set) {
                        dist.handle(tweet);
                    }
                    //create a node that consists the earliest tweet in this group
                    TGTweet topic = findTopic(set);

                    Node<CharSequence> topicNode = new Node<CharSequence>(topic);
                    theNode.addChild(topicNode);
                    for (CharSequence tweet : set) {
                        Node<CharSequence> tinyTweet = new Node<CharSequence>(tweet);
                        topicNode.addChild(tinyTweet);
                    }
                }
            }
        } else {
            for (Set<CharSequence> set : clusters) {
                int size = set.size();
                if (size < 3) {
                    //create a node
                    //seriously, I have to do this?
                    for (CharSequence tweet : set) {
                        Node<CharSequence> tinyTweet = new Node<CharSequence>(tweet);
                        theNode.addChild(tinyTweet);
                    }
                } else {
                    //create a topic Node out of theGroup
                    //use a small corpus for clustering...
                    TfIdfDistance dist = new TfIdfDistance(tf);
                    for (CharSequence tweet : set) {
                        dist.handle(tweet);
                    }
                    //create a node that consists of the interesting phrases 
                    TGTweet topic = findTopic(set);

                    // MinMax mm1 = new MinMax(set, dist);

                    Node<CharSequence> topicNode = new Node<CharSequence>(topic);
                    theNode.addChild(topicNode);
                    fillTree(dist, topicNode, set, pd);

                }
            }
        }
    }

    /**
     * Finds the earliest version of the Tweets
     * @param is
     * @return
     */
    private TGTweet findTopic(Set<CharSequence> is) {

        List<TGTweet> temp = new ArrayList<TGTweet>();

        for (CharSequence cs : is) {
            if (cs instanceof TGTweet) {
                temp.add((TGTweet) cs);
            }
        }

        TGTweet min = Collections.min(temp);
        return min;
    }

    private Set<Set<CharSequence>> getClusters(final TfIdfDistance dist, final Set<CharSequence> inputSet,
            double partitionDistance) {
        Dendrogram<CharSequence> dendrogram = getDendrogram(dist, inputSet, partitionDistance);

        Set<Set<CharSequence>> clusters = dendrogram.partitionDistance(partitionDistance);

        return clusters;
    }

    protected Dendrogram<CharSequence> getDendrogram(final TfIdfDistance distance, final Set<CharSequence> inputSet,
            double maxDistance) {
        // Complete-Link Clusterer
        HierarchicalClusterer<CharSequence> clusterer = new CompleteLinkClusterer<CharSequence>(1.0, distance);

        Dendrogram<CharSequence> dendrogram = clusterer.hierarchicalCluster(inputSet);
        return dendrogram;
    }
}
