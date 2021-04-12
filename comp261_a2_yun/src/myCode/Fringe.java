package myCode;

import code.*;

/**
 * Description: <br/>
 * The Fringe has 4 fields, which is current node, previous node, current cost and the total
 * estimated cost.
 * 
 * 
 * A list of the node that we explored so far.
 * 
 * @author Yun Zhou 300442776
 * @version
 */
public class Fringe {

    /**
     * current_node:the current node.
     */
    private Node current_node;

    /**
     * prev_node: the node before the current node.(i.e. the incoming adjacentList of the
     * currentNode)
     */
    private Node prev_node;

    /**
     * the current cost is the weight from currentNode to the outgoing neighbour node
     */
    private double current_cost;

    /**
     * the total estimated cost is the estimated cost of the current node to the goal node
     */
    private double total_estimated_cost;

    /**
     * A constructor. It construct a new instance of Fringe.
     *
     * @param current_node
     *            the current node.
     * @param prev_node
     *            the node before the current node.(i.e. the incoming adjacentList of the
     *            currentNode)
     * @param current_cost
     *            the weight from currentNode to the outgoing neighbour node
     * @param total_estimated_cost
     *            the total estimated cost is the estimated cost of the current node to the
     *            goal node
     */

    public Fringe(Node current_node, Node prev_node, double current_cost,
            double total_estimated_cost) {
        this.current_node = current_node;
        this.prev_node = prev_node;
        this.current_cost = current_cost;
        this.total_estimated_cost = total_estimated_cost;
    }

    /**
     * Description: <br/>
     * For sorting, based on the total estimated cost to the goal.
     * 
     * @author Yun Zhou
     * @param otherFringe
     *            the Fringe to compare
     * @return
     */
    public int compareTo(Fringe otherFringe) {
        Fringe thisFringe = this;
        if (thisFringe.total_estimated_cost < otherFringe.total_estimated_cost) {
            // sort it baesd on the total estimated cost. sort it like: 10.3, 11.4, 20.2 etc
            return -1;
        }
        if (thisFringe.total_estimated_cost > otherFringe.total_estimated_cost) {
            return 1;
        }
        return 0;// the same,return 0
    }

    /**
     * Get the current_node.
     *
     * @return the current_node
     */
    public Node getCurrent_node() {
        return current_node;
    }

    /**
     * Get the prev_node.
     *
     * @return the prev_node
     */
    public Node getPrev_node() {
        return prev_node;
    }

    /**
     * Get the current_cost.
     *
     * @return the current_cost
     */
    public double getCurrent_cost() {
        return current_cost;
    }

    /**
     * Get the total_estimated_cost.
     *
     * @return the total_estimated_cost
     */
    public double getTotal_estimated_cost() {
        return total_estimated_cost;
    }

    /**
     * Set the current_node.
     *
     * @param current_node
     *            the current_node to set
     */
    public void setCurrent_node(Node current_node) {
        this.current_node = current_node;
    }

    /**
     * Set the prev_node.
     *
     * @param prev_node
     *            the prev_node to set
     */
    public void setPrev_node(Node prev_node) {
        this.prev_node = prev_node;
    }

    /**
     * Set the current_cost.
     *
     * @param current_cost
     *            the current_cost to set
     */
    public void setCurrent_cost(double current_cost) {
        this.current_cost = current_cost;
    }

    /**
     * Set the total_estimated_cost.
     *
     * @param total_estimated_cost
     *            the total_estimated_cost to set
     */
    public void setTotal_estimated_cost(double total_estimated_cost) {
        this.total_estimated_cost = total_estimated_cost;
    }
}
