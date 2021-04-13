package code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Node represents an intersection in the road graph. It stores its ID and its location, as
 * well as all the segments that it connects to. It knows how to draw itself, and has an
 * informative toString method.
 * 
 * @author tony
 */
public class Node {

    /** whether this node is visited or not */
    private boolean isVisited = false;

    /** the depth of the Node, for Articulation Points Algorithm use */
    private int depth = Integer.MAX_VALUE;

    /**
     * there are lots of node neighbours, so the previous node is the node before it reaches to
     * the current Node
     */
    private Node previousNode = null;

    public final int nodeID;

    private final Location location;

    public final Collection<Segment> segments;

    // /** the outgoing and incoming segments */
    // private Set<Segment> outGoSegments, incomingSegments;

    public Node(int nodeID, double lat, double lon) {
        this.nodeID = nodeID;
        this.location = Location.newFromLatLon(lat, lon);
        this.segments = new HashSet<Segment>();
        // set up the outgoing and incoming segments
        // this.outGoSegments = new HashSet<Segment>();
        // this.incomingSegments = new HashSet<Segment>();
    }

    public void addSegment(Segment seg) {
        segments.add(seg);
    }

    public List<Node> getOutGoingNodes() {
        List<Node> outgoing_neighbourNodes = new ArrayList<Node>();

        for (Segment seg : getOutGoingSegments()) {
            outgoing_neighbourNodes.add(seg.end);
            // check
            assert !seg.end.equals(this);
        }
        return outgoing_neighbourNodes;

    }

    public List<Node> getIncomingNodes() {
        List<Node> incomingNOdes = new ArrayList<Node>();
        for (Segment seg : getIncomingSegments()) {
            incomingNOdes.add(seg.start);
            // check
            assert !seg.start.equals(this);
        }
        return incomingNOdes;
    }

    /**
     * Description: <br/>
     * Return the collection of the outgoing segments of the node object.
     * 
     * @author Yun Zhou
     * @return the collection of the outgoing segments of the node object
     */
    public Set<Segment> getOutGoingSegments() {

        Set<Segment> outGoSegments = new LinkedHashSet<Segment>();

        Node currentNode = this;
        for (Segment segment : segments) {
            if (currentNode.equals(segment.start)) {
                outGoSegments.add(segment);
            }

        }
        return outGoSegments;

    }

    /**
     * Description: <br/>
     * Return the collection of the incoming segments of the node object
     * 
     * @author Yun Zhou
     * @return the collection of the incoming segments of the node object
     */
    public Set<Segment> getIncomingSegments() {

        Set<Segment> incomingSegments = new HashSet<Segment>();
        Node currentNode = this;
        for (Segment segment : segments) {
            if (currentNode.equals(segment.end)) {
                incomingSegments.add(segment);
            }
        }
        return incomingSegments;

    }

    public void draw(Graphics g, Dimension area, Location origin, double scale) {
        Point p = location.asPoint(origin, scale);

        // for efficiency, don't render nodes that are off-screen.
        if (p.x < 0 || p.x > area.width || p.y < 0 || p.y > area.height)
            return;

        int size = (int) (Mapper.NODE_GRADIENT * Math.log(scale) + Mapper.NODE_INTERCEPT);

        if (Mapper.targetNode == this) {
            System.out.println("FUCK YEAAA");
            g.setColor(Color.red);
        }
        g.fillRect(p.x - size / 2, p.y - size / 2, size, size);
    }

    public String toString() {
        Set<String> edges = new HashSet<String>();
        for (Segment s : segments) {
            if (!edges.contains(s.road.name))
                edges.add(s.road.name);
        }

        String str = "ID: " + nodeID + "  loc: " + location + "\nroads: ";
        for (String e : edges) {
            str += e + ", ";
        }
        return str.substring(0, str.length() - 2);
    }

    /**
     * Get the isVisited.
     *
     * @return the isVisited
     */
    public boolean isVisited() {
        return isVisited;
    }

    /**
     * Get the nodeID.
     *
     * @return the nodeID
     */
    public int getNodeID() {
        return nodeID;
    }

    /**
     * Get the location.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the segments.
     *
     * @return the segments
     */
    public Collection<Segment> getSegments() {
        return segments;
    }

    /**
     * Set the isVisited.
     *
     * @param isVisited
     *            the isVisited to set
     */
    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    /**
     * Get the previousNode.
     *
     * @return the previousNode
     */
    public Node getPreviousNode() {
        return previousNode;
    }

    /**
     * Set the previousNode.
     *
     * @param previousNode
     *            the previousNode to set
     */
    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nodeID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Node other = (Node) obj;
        if (nodeID != other.nodeID) {
            return false;
        }
        return true;
    }

    public Set<Node> getNeighbourNode() {
        Set<Node> nodes_nei = new HashSet<Node>();
        for (Segment segment : segments) {
            nodes_nei.add(segment.end);
            nodes_nei.add(segment.start);

        }
        // TODO Auto-generated method stub
        return nodes_nei;
    }

    /**
     * Get the depth.
     *
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Set the depth.
     *
     * @param depth
     *            the depth to set
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }
}

// code for COMP261 assignments