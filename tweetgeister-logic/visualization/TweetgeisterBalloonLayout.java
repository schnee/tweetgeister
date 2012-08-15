package com.schnee.tweetgeister.visualization;

/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.functors.ConstantTransformer;

import com.schnee.tweetgeister.Clusterer;
import com.schnee.tweetgeister.analysis.TokenizedCharSequence;
import com.schnee.tweetgeister.data.Node;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformerDecorator;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * Demonstrates the visualization of a Tree using TreeLayout
 * and BalloonLayout. An examiner lens performing a hyperbolic
 * transformation of the view is also included.
 * 
 * @author Tom Nelson
 * 
 */
@SuppressWarnings("serial")
public class TweetgeisterBalloonLayout extends JApplet {

    /**
     * the graph
     */
    Forest<Node, Integer> graph;

    Factory<DirectedGraph<Node, Integer>> graphFactory = new Factory<DirectedGraph<Node, Integer>>() {

        public DirectedGraph<Node, Integer> create() {
            return new DirectedSparseMultigraph<Node, Integer>();
        }
    };

    Factory<Tree<Node, Integer>> treeFactory = new Factory<Tree<Node, Integer>>() {

        public Tree<Node, Integer> create() {
            return new DelegateTree<Node, Integer>(graphFactory);
        }
    };

    Factory<Integer> edgeFactory = new Factory<Integer>() {
        int i = 0;

        public Integer create() {
            return i++;
        }
    };

    Factory<String> vertexFactory = new Factory<String>() {
        int i = 0;

        public String create() {
            return "V" + i++;
        }
    };

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<Node, Integer> vv;

    VisualizationServer.Paintable rings;

    String root;

    TreeLayout<Node, Integer> layout;

    BalloonLayout<Node, Integer> balloonLayout;
    
    RadialTreeLayout<Node, Integer> radialTreeLayout;

    /**
     * provides a Hyperbolic lens for the view
     */
    LensSupport hyperbolicViewSupport;

    public TweetgeisterBalloonLayout() {

        // create a simple graph for the demo
        graph = new DelegateForest<Node, Integer>();

        createTree();

        layout = new TreeLayout<Node, Integer>(graph);
        radialTreeLayout = new RadialTreeLayout<Node, Integer>(graph);
        balloonLayout = new BalloonLayout<Node, Integer>(graph);
        balloonLayout.setSize(new Dimension(5900, 5900));
  
        writeGraph();
        /*       
               vv = new VisualizationViewer<Node, Integer>(layout, new Dimension(6000, 6000));
               vv.setBackground(Color.white);
               vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
               vv.getRenderContext().setVertexLabelTransformer(new TopicNodeLabeller());
               // add a listener for ToolTips
               vv.setVertexToolTipTransformer(new ToStringLabeller());
               vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
             

               Container content = getContentPane();
               final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
               content.add(panel);

               final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

               vv.setGraphMouse(graphMouse);
               vv.addKeyListener(graphMouse.getModeKeyListener());
               vv.addPreRenderPaintable(rings);

               hyperbolicViewSupport = new ViewLensSupport<Node, Integer>(vv, new HyperbolicShapeTransformer(vv, vv
                       .getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)), new ModalLensGraphMouse());

               graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());

               JComboBox modeBox = graphMouse.getModeComboBox();
               modeBox.addItemListener(graphMouse.getModeListener());
               graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

               final ScalingControl scaler = new CrossoverScalingControl();

               vv.scaleToLayout(scaler);

               JButton plus = new JButton("+");
               plus.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                       scaler.scale(vv, 1.1f, vv.getCenter());
                   }
               });
               JButton minus = new JButton("-");
               minus.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                       scaler.scale(vv, 1 / 1.1f, vv.getCenter());
                   }
               });

               JToggleButton radial = new JToggleButton("Balloon");
               radial.addItemListener(new ItemListener() {

                   public void itemStateChanged(ItemEvent e) {
                       if (e.getStateChange() == ItemEvent.SELECTED) {

                           LayoutTransition<Node, Integer> lt = new LayoutTransition<Node, Integer>(vv, layout, radialLayout);
                           Animator animator = new Animator(lt);
                           animator.start();
                           vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setToIdentity();
                           vv.addPreRenderPaintable(rings);
                       } else {

                           LayoutTransition<Node, Integer> lt = new LayoutTransition<Node, Integer>(vv, radialLayout, layout);
                           Animator animator = new Animator(lt);
                           animator.start();
                           vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setToIdentity();
                           vv.removePreRenderPaintable(rings);
                       }
                       vv.repaint();
                   }
               });
               final JRadioButton hyperView = new JRadioButton("Hyperbolic View");
               hyperView.addItemListener(new ItemListener() {
                   public void itemStateChanged(ItemEvent e) {
                       hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                   }
               });

               JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
               scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

               JPanel controls = new JPanel();
               scaleGrid.add(plus);
               scaleGrid.add(minus);
               controls.add(radial);
               controls.add(scaleGrid);
               controls.add(modeBox);
               controls.add(hyperView);
               content.add(controls, BorderLayout.SOUTH);
               */
    }



    private void writeGraph() {
        Container c = new Container();

        VisualizationViewer<Node, Integer> vv = new VisualizationViewer<Node, Integer>(balloonLayout, new Dimension(3000,
                3000));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexLabelTransformer(new TopicNodeLabeller(graph));
        vv.getRenderContext().setVertexIncludePredicate(new VertexDisplayPredicate(false));
        // add a listener for ToolTips
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        //vv.addPreRenderPaintable(rings);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);
        rings = new Rings(balloonLayout, vv);
        vv.addPreRenderPaintable(rings);
        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(3000, 3000,
                java.awt.image.BufferedImage.TYPE_INT_RGB);

        Graphics2D gr = bi.createGraphics();

        // MOST-IMPORTANT

        vv.setSize(3000, 3000);

        final ScalingControl scaler = new CrossoverScalingControl();

        vv.scaleToLayout(scaler);

        // MOST-IMPORTANT

        c.addNotify();

        c.add(vv);

        c.setVisible(true);

        c.paintComponents(gr);

        try {
            ImageIO.write(bi, "png", new File("vis.png"));
            System.out.println("done");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void createTree() {

        try {
            Twitter twitter = new TwitterFactory().getInstance();

            List<Tweet> allTweets = new ArrayList<Tweet>();
            String qString = "bp";
            Query query = new Query(qString);
            query.setRpp(100);
            query.setLang("en");

            int hits = 100;
            int page = 1;
            while (allTweets.size() < 1400 && page < 16 && hits == 100) {

                query.setPage(page);

                // System.out.println(query.toString());
                QueryResult result = twitter.search(query);
                List<Tweet> tweets = result.getTweets();
                allTweets.addAll(tweets);
                hits = tweets.size();
                System.out.println("page: " + page + " hits: " + hits + " all tweets:" + allTweets.size());
                page++;
            }
            System.out.println("hits: " + allTweets.size());

            Set<CharSequence> inputSet = new HashSet<CharSequence>();

            for (Tweet tweet : allTweets) {
                inputSet.add(tweet.getText());
            }

            Clusterer cl = new Clusterer(inputSet, TokenizedCharSequence.TOKENIZER_FACTORY);

            com.schnee.tweetgeister.data.Tree<CharSequence> mindmap = cl.buildTree();

            fillTree(mindmap);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void fillTree(com.schnee.tweetgeister.data.Tree<CharSequence> mindmap) {

        Node<CharSequence> theRoot = mindmap.getRootElement();

        int tabLevel = 0;

        graph.addVertex(theRoot);

        walk(theRoot, tabLevel);

    }

    private void walk(Node<CharSequence> element, int tabLevel) {

        CharSequence text = element.getData();
        String theData = text.toString();

        theData = cleanNewLines(theData);

        tabLevel++;
        for (Node<CharSequence> data : element.getChildren()) {

            graph.addEdge(edgeFactory.create(), element, data);
            walk(data, tabLevel);

        }

    }

    private String cleanNewLines(String theData) {

        //theData = theData.replaceAll("[\\n\\r]+", " ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < theData.length(); i++) {
            char toAppend = 'a';
            switch (theData.charAt(i)) {
            case '\r':
                toAppend = ' ';
                break;
            case '\n':
                toAppend = ' ';
                break;
            default:
                toAppend = theData.charAt(i);
            }
            sb.append(toAppend);
        }
        theData = sb.toString();

        return theData;

    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new TweetgeisterBalloonLayout());
        frame.pack();
        frame.setVisible(true);
    }

    private final static class VertexDisplayPredicate<V, E> implements Predicate<Context<Graph<V, E>, V>>
    //  extends  AbstractGraphPredicate<V,E>
    {
        protected boolean filter_small;

        protected final static int MIN_DEGREE = 4;

        public VertexDisplayPredicate(boolean filter) {
            this.filter_small = filter;
        }

        public void filterSmall(boolean b) {
            filter_small = b;
        }

        public boolean evaluate(Context<Graph<V, E>, V> context) {
            Graph<V, E> graph = context.graph;
            V v = context.element;
            //        Vertex v = (Vertex)arg0;
            if (filter_small)
                return (graph.degree(v) >= MIN_DEGREE);
            else
                return true;
        }
    }
}
