package com.schnee.tweetgeister.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import com.schnee.tweetgeister.data.Node;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformerDecorator;

class Rings implements VisualizationServer.Paintable {

    BalloonLayout<Node, Integer> layout;
    VisualizationViewer<Node, Integer> vv;

    public Rings(BalloonLayout<Node, Integer> layout, VisualizationViewer<Node, Integer> vv) {
        this.layout = layout;
        this.vv = vv;
    }

    public void paint(Graphics g) {
        g.setColor(Color.gray);

        Graphics2D g2d = (Graphics2D) g;

        Ellipse2D ellipse = new Ellipse2D.Double();
        for (Node<?> v : layout.getGraph().getVertices()) {
            Double radius = layout.getRadii().get(v);
            if (radius == null)
                continue;
            Point2D p = layout.transform(v);
            ellipse.setFrame(-radius, -radius, 2 * radius, 2 * radius);
            AffineTransform at = AffineTransform.getTranslateInstance(p.getX(), p.getY());
            Shape shape = at.createTransformedShape(ellipse);

            MutableTransformer viewTransformer = vv.getRenderContext().getMultiLayerTransformer()
                    .getTransformer(Layer.VIEW);

            if (viewTransformer instanceof MutableTransformerDecorator) {
                shape = vv.getRenderContext().getMultiLayerTransformer().transform(shape);
            } else {
                shape = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, shape);
            }

            g2d.draw(shape);
        }
    }

    public boolean useTransform() {
        return true;
    }
}
