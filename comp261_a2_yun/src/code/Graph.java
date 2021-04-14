package code;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This represents the data structure storing all the roads, nodes, and segments, as well as
 * some information on which nodes and segments should be highlighted.
 * 
 * @author tony
 */
public class Graph {
    // map node IDs to Nodes.
    Map<Integer, Node> nodes = new HashMap<>();

    // map road IDs to Roads.
    Map<Integer, Road> roads;

    // just some collection of Segments.
    Collection<Segment> segments;

    Node highlightedNode;

    Collection<Road> highlightedRoads = new HashSet<>();

    public Graph(File nodes, File roads, File segments, File polygons) {
        this.nodes = Parser_Stream.parseNodes(nodes, this);
        this.roads = Parser_Stream.parseRoads(roads, this);
        this.segments = Parser_Stream.parseSegments(segments, this);
    }

    public void draw(Graphics g, Dimension screen, Location origin, double scale) {
        // a compatibility wart on swing is that it has to give out Graphics
        // objects, but Graphics2D objects are nicer to work with. Luckily
        // they're a subclass, and swing always gives them out anyway, so we can
        // just do this.
        Graphics2D g2 = (Graphics2D) g;

        // draw all the segments.
        g2.setColor(Mapper.SEGMENT_COLOUR);
        for (Segment s : segments)
            s.draw(g2, origin, scale);

        // draw the segments of all highlighted roads, if the target node is exist
        g2.setColor(Mapper.HIGHLIGHT_COLOUR);
        g2.setStroke(new BasicStroke(3));
        for (Road road : highlightedRoads) {
            for (Segment seg : road.components) {
                seg.draw(g2, origin, scale);
            }
        }

        // draw the segments of all highlighted segments, if the target node is exist
        if (!highlightedSegments.isEmpty()) {
            g2.setColor(Color.PINK);
            for (Segment segment : highlightedSegments) {
                segment.draw(g2, origin, scale);
            }

        }
        // draw all the nodes.
        g2.setColor(Mapper.NODE_COLOUR);
        for (Node n : nodes.values()) {
            n.draw(g2, screen, origin, scale);
        }
        // draw the highlighted node, if it exists.
        if (highlightedNode != null) {
            g2.setColor(Mapper.HIGHLIGHT_COLOUR);
            highlightedNode.draw(g2, screen, origin, scale);
        }

        if (!highlightedNodes.isEmpty()) {
            int index = -1;
            g2.setColor(Color.red);
            for (Node node : highlightedNodes) {
                index++;
                // g2.setColor(Color.green);
                // if (index == 0 || index == highlightedNodes.size() - 1) {
                // g2.setColor(Color.red);
                // }
                node.draw(g2, screen, origin, scale);

            }

        }

    }

    public void setHighlight(Node node) {
        this.highlightedNode = node;
    }

    public void setHighlight(Collection<Road> roads) {
        this.highlightedRoads = roads;
    }

    Collection<Segment> highlightedSegments = new HashSet<>();

    Collection<Node> highlightedNodes = new LinkedList<Node>();

    public void setHighlight(List<Segment> path_segments) {
        this.highlightedSegments = path_segments;

    }

    public void setHighlight_nodes(Collection<Node> path_nodes) {
        this.highlightedNodes = path_nodes;
    }

}

// code for COMP261 assignments