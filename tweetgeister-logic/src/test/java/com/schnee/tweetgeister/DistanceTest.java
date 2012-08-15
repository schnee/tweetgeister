package com.schnee.tweetgeister;

import java.util.HashSet;
import java.util.Set;

import com.aliasi.cluster.CompleteLinkClusterer;
import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.HierarchicalClusterer;
import com.aliasi.cluster.SingleLinkClusterer;
import com.aliasi.spell.TfIdfDistance;
import com.schnee.tweetgeister.analysis.TokenizedCharSequence;

import junit.framework.TestCase;

public class DistanceTest extends TestCase {

    public void testDistances() {

        Set<String> input = new HashSet<String>();

        input.add("In the Interactive Infographics talk #interinfo with @ellielovell and @rasga #sxswi #wxwm");
        input.add("Hard to talk about infographics without mentioning the worksof Jonathan Harris such as we feel fine http://bit.ly/NLGsM  #interinfo #sxsw");
        input.add("the panelists of #interinfo are excided about HTML5 & talk about the limitations of flash");
        input.add("the panelists of #interinfo are excided about HTML5 & talk about the limitations of flash (via @missmoss)");
        input.add("( abt visualisation information ) RT @missmoss: the panelists of #interinfo are excided about HTML5 & talk about the limitations of flash");
        input.add("Mapumental work being shown in infographics talk by @stamen  #interinfo #sxswi #wxwm");
        input.add("RT @john383 Mapumental work being shown in infographics talk by @stamen #interinfo #sxswi #wxwm");

        TfIdfDistance distance = new TfIdfDistance(TokenizedCharSequence.TOKENIZER_FACTORY);

        for (CharSequence charSequence : input) {
            distance.handle(charSequence);
        }

        // dump off-diagonal upper triangular distance matrix
        for (String s1 : input)
            for (String s2 : input)
                if (s1.compareTo(s2) < 0)
                    System.out.println(s1 + "\n" + s2 + "\n)=" + distance.distance(s1, s2));
        
        double maxDistance = 1;
        
        // Single-Link Clusterer
        HierarchicalClusterer<String> slClusterer 
            = new SingleLinkClusterer<String>(maxDistance,
                                              distance);

        // Complete-Link Clusterer
        HierarchicalClusterer<String> clClusterer
            = new CompleteLinkClusterer<String>(maxDistance,
                                                distance);

        // Hierarchical Clustering
        Dendrogram<String> slDendrogram
            = slClusterer.hierarchicalCluster(input);
        System.out.println("\nSingle Link Dendrogram");
        System.out.println(slDendrogram.prettyPrint());

        Dendrogram<String> clDendrogram
            = clClusterer.hierarchicalCluster(input);
        System.out.println("\nComplete Link Dendrogram");
        System.out.println(clDendrogram.prettyPrint());

        // Dendrograms to Clusterings
        System.out.println("\nComplete Link Clusterings");
        for (int k = 1; k <= clDendrogram.size(); ++k) {
            Set<Set<String>> clKClustering = clDendrogram.partitionK(k);
            System.out.println(k + "  " + clKClustering);
        }

        System.out.println("\nSingle Link Clusterings");
        for (int k = 1; k <= slDendrogram.size(); ++k) {
            Set<Set<String>> slKClustering = slDendrogram.partitionK(k);
            System.out.println(k + "  " + slKClustering);
        }



        Set<Set<String>> clClustering
            = clClusterer.cluster(input);
        System.out.println("\n\nComplete Link Clustering");
        System.out.println(clClustering);

        Set<Set<String>> slClustering
            = slClusterer.cluster(input);
        System.out.println("\nSingle Link Clustering");
        System.out.println(slClustering);


    }

}
