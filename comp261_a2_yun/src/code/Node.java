package code;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Node represents an intersection in the road graph. It stores its ID and its location, as
 * well as all the segments that it connects to. It knows how to draw itself, and has an
 * informative toString method.
 * 
 * @author tony
 */
public class Node {

    public final int nodeID;

    public final Location location;

    public final Collection<Segment> segments;

    private Set<Segment> outGoSegments, incomingSegments;

    public Node(int nodeID, double lat, double lon) {
        this.nodeID = nodeID;
        this.location = Location.newFromLatLon(lat, lon);
        this.segments = new HashSet<Segment>();
        // set up the outgoing and incoming segments
        this.outGoSegments = new HashSet<Segment>();
        this.incomingSegments = new HashSet<Segment>();
    }

    public void addSegment(Segment seg) {
        segments.add(seg);
    }

    /**
     * Description: <br/>
     * Return the collection of the outgoing segments of the node object.
     * 
     * @author Yun Zhou
     * @return the collection of the outgoing segments of the node object
     */
    public Collection<Segment> getOutGoingSegments() {
        // if it's already initialized, then just return it
        if (!this.outGoSegments.isEmpty()) {
            return this.outGoSegments;
        }

        Node currentNode = this;
        this.outGoSegments = new HashSet<Segment>();
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
    public Collection<Segment> getIncomingSegments() {
        // if it's already initialized, then just return it
        if (!this.incomingSegments.isEmpty()) {
            return this.incomingSegments;
        }
        Node currentNode = this;
        this.incomingSegments = new HashSet<Segment>();
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
     * Briefly describe the feature of the function:
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + nodeID;
        return result;
    }

    /**
     * Briefly describe the feature of the function:
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (nodeID != other.nodeID) {
            return false;
        }
        return true;
    }
}

// code for COMP261 assignments