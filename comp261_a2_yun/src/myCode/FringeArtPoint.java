package myCode;

import java.util.Collection;

import code.Node;

public class FringeArtPoint {
    /** fields for the Articulation Points Algorithm */
    private Node first_neighbourNode;

    /** fields for the Articulation Points Algorithm */
    private Node rootNode;

    private int depth;

    private Collection<Node> childNodes_of_firstNeighbourNode;

    public FringeArtPoint() {
    }

    public FringeArtPoint(Node currentNode, int depth, Node rootNode) {
        this.first_neighbourNode = currentNode;
        this.depth = depth;
        this.rootNode = rootNode;
    }

    // getter and setters
    public Node getFirst_neighbourNode() {
        return first_neighbourNode;
    }

    public void setFirst_neighbourNode(Node first_neighbourNode) {
        this.first_neighbourNode = first_neighbourNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Collection<Node> getChildNodes_of_firstNeighbourNode() {
        return childNodes_of_firstNeighbourNode;
    }

    public void setChildNodes_of_firstNeighbourNode(
            Collection<Node> childNodes_of_firstNeighbourNode) {
        this.childNodes_of_firstNeighbourNode = childNodes_of_firstNeighbourNode;
    }
}