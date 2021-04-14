package code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import myCode.Fringe;

/**
 * This is the main class for the mapping program. It extends the GUI abstract class and
 * implements all the methods necessary, as well as having a main function.
 * 
 * @author tony
 */
public class Mapper extends GUI {
    public static final Color NODE_COLOUR = new Color(77, 113, 255);

    public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);

    public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);

    // these two constants define the size of the node squares at different zoom
    // levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
    // log(scale)
    public static final int NODE_INTERCEPT = 1;

    public static final double NODE_GRADIENT = 0.8;

    // defines how much you move per button press, and is dependent on scale.
    public static final double MOVE_AMOUNT = 100;

    // defines how much you zoom in/out per button press, and the maximum and
    // minimum zoom levels.
    public static final double ZOOM_FACTOR = 1.3;

    public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

    // how far away from a node you can click before it isn't counted.
    public static final double MAX_CLICKED_DISTANCE = 0.15 + 999;

    // these two define the 'view' of the program, ie. where you're looking and
    // how zoomed in you are.
    private Location origin;

    private double scale;

    // our data structures.
    private Graph graph;

    private Trie trie;

    /** for the AStartSearch */
    public static Node startNode = null, targetNode = null;

    /** whether the path from startNode and targetNode is Found or not */
    boolean isFound_path = false;

    /** for highlighting segments use */
    private Segment lowestWeightSegment = null;

    @Override
    protected void redraw(Graphics g) {
        if (graph != null) {
            graph.draw(g, getDrawingAreaDimension(), origin, scale);
        }
    }

    @Override
    protected void onClick(MouseEvent e) {
        Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
        // find the closest node.
        double bestDist = Double.MAX_VALUE;
        Node closest = null;

        for (Node node : graph.nodes.values()) {
            double distance = clicked.distance(node.getLocation());
            if (distance < bestDist) {
                bestDist = distance;
                closest = node;
            }
        }

        // if it's close enough, highlight it and show some information.
        if (clicked.distance(closest.getLocation()) < MAX_CLICKED_DISTANCE) {
            graph.setHighlight(closest);
            getTextOutputArea().setText(closest.toString());
        }
        if (startNode == null) {
            startNode = closest;

        } else {
            // we've already got the startNode, so it's the tragetNOde
            targetNode = closest;

            onAStarSearch();
            // finish the A star search, so reset the starNode and the target Node
            startNode = null;
            targetNode = null;
            isFound_path = false;
        }

    }

    @Override
    protected void onSearch() {
        if (trie == null)
            return;

        // get the search query and run it through the trie.
        String query = getSearchBox().getText();
        Collection<Road> selected = trie.get(query);

        // figure out if any of our selected roads exactly matches the search
        // query. if so, as per the specification, we should only highlight
        // exact matches. there may be (and are) many exact matches, however, so
        // we have to do this carefully.
        boolean exactMatch = false;
        for (Road road : selected)
            if (road.name.equals(query))
                exactMatch = true;

        // make a set of all the roads that match exactly, and make this our new
        // selected set.
        if (exactMatch) {
            Collection<Road> exactMatches = new HashSet<>();
            for (Road road : selected)
                if (road.name.equals(query))
                    exactMatches.add(road);
            selected = exactMatches;
        }

        // set the highlighted roads.
        graph.setHighlight(selected);

        // now build the string for display. we filter out duplicates by putting
        // it through a set first, and then combine it.
        Collection<String> names = new HashSet<>();
        for (Road road : selected) {
            names.add(road.name);
        }
        String str = "";
        for (String name : names) {
            str += name + "; ";
        }

        if (str.length() != 0) {
            str = str.substring(0, str.length() - 2);
        }
        getTextOutputArea().setText(str);
    }

    @Override
    protected void onMove(Move m) {
        if (m == GUI.Move.NORTH) {
            origin = origin.moveBy(0, MOVE_AMOUNT / scale);
        } else if (m == GUI.Move.SOUTH) {
            origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
        } else if (m == GUI.Move.EAST) {
            origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
        } else if (m == GUI.Move.WEST) {
            origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
        } else if (m == GUI.Move.ZOOM_IN) {
            if (scale < MAX_ZOOM) {
                // yes, this does allow you to go slightly over/under the
                // max/min scale, but it means that we always zoom exactly to
                // the centre.
                scaleOrigin(true);
                scale *= ZOOM_FACTOR;
            }
        } else if (m == GUI.Move.ZOOM_OUT) {
            if (scale > MIN_ZOOM) {
                scaleOrigin(false);
                scale /= ZOOM_FACTOR;
            }
        }
    }

    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        graph = new Graph(nodes, roads, segments, polygons);
        trie = new Trie(graph.roads.values());
        origin = new Location(-250, 250); // close enough
        scale = 1;
    }

    @Override
    protected void onAStarSearch() {
        // do the precondition check first
        if (startNode == null || targetNode == null) {
            // not ready for the AStar search,return
            String errorMessString = "Not ready for the Astar search, please specifiy "
                                     + "the start Node and the target Node.";
            // System.err.println(errorMessString);
            getTextOutputArea().setText(errorMessString);
            return;
        }
        if (startNode.equals(targetNode)) {
            getTextOutputArea().setText("The targetNode and the startNode is the same!");
            return;
        }

        /*
         * Initially all the nodes are unvisited, and the fringe has a single element
         */
        for (Node node : graph.nodes.values()) {
            // reset the previous node and the status of the visited
            node.setVisited(false);
            node.setPreviousNode(null);
        }
        Queue<Fringe> fringesQueue = new PriorityQueue<Fringe>();
        // assign the eculiden distance as the heuristic.(from the assignment handout)
        double firstElement_total_estimated_cost = startNode.getLocation()
                .distance(targetNode.getLocation());
        fringesQueue.offer(new Fringe(startNode, null, 0, firstElement_total_estimated_cost));

        /**
         * loop through the fringe queue until find the path to the targetNode
         */
        while (!fringesQueue.isEmpty()) {
            // extract the fringe object with lowest cost from the PriorityQueue
            Fringe fringe_lowest = fringesQueue.poll();
            Node currentNode = fringe_lowest.getCurrent_node();

            if (!fringe_lowest.getCurrent_node().isVisited()) {
                // Set node* as visited, and set node*.prev = prev*;
                currentNode.setVisited(true);
                currentNode.setPreviousNode(fringe_lowest.getPrev_node());
                // it matches the targetNode, break
                if (currentNode.equals(targetNode)) {
                    this.isFound_path = true;
                    break;
                }

                // loop through the outgoing node neighbours

                for (Node outgoing_node : currentNode.getAllNeighbourNodes()) {

                    // for (Node outgoing_node : currentNode.getOutGoingNodes()) {

                    if (!outgoing_node.isVisited()) {
                        // assign the g(node)
                        double startNodeToNeighbourCost_sofar = fringe_lowest.getCurrent_cost()
                                + findSegmentWeight(currentNode, outgoing_node);
                        // assign the f(node).
                        // (hint: f(node)=g(node)+h(Node))
                        double total_estimated_cost = startNodeToNeighbourCost_sofar
                                + outgoing_node.getLocation().distance(targetNode.getLocation());

                        // every argument is ready, offer the new fringe object into the queue
                        fringesQueue.offer(new Fringe(outgoing_node, currentNode,
                                startNodeToNeighbourCost_sofar, total_estimated_cost));
                    }
                }
            }

        }

        // isFound_path = true;
        if (!isFound_path) {
            String text = "Sorry, did NOT find the path from the startNode:\n\t "
                          + startNode.toString()
                          + "\n to the targetNode: \n\t" + targetNode.toString();
            text += "\nSorry, did NOT find the path!!\nIt means either the group of startNode and targetNode is not connected\n\t"
                    + "OR NO PATHS since the some roadSegment is one-way";
            // System.err.println(text);
            getTextOutputArea().setText(text);
        }
        // find the path,highlight the segment,node etc
        else {
            highLightAllNodes_Segments();

        }

        // finish the A star search, so reset the starNode and the target Node
        startNode = null;
        targetNode = null;
        isFound_path = false;

    }

    /**
     * Description: <br/>
     * Return the lowest segment length between startNode and targetNode. Also, this method
     * will assign the field segment.
     * 
     * @author Yun Zhou
     * @param startNode
     * @param targetNode
     * @return the lowest segment length between startNode and targetNode.
     */
    private double findSegmentWeight(Node startNode, Node targetNode) {
        double weight = Double.POSITIVE_INFINITY;

        // // loop through all the outgoing edges
        // for (Segment seg : startNode.getOutGoingSegments()) {
        // // if find the matched segemnts, compare it and assight the weight
        // if (seg.end.equals(targetNode)) {
        // if (seg.length < weight) {
        // weight = seg.length;
        // this.lowestWeightSegment = seg;
        // }
        // }
        // }

        for (Segment seg : startNode.segments) {
            if (targetNode.segments.contains(seg)) {
                weight = seg.length;
                this.lowestWeightSegment = seg;
            }
        }

        return weight;

    }

    private void highLightAllNodes_Segments() {
        LinkedList<Node> path_nodes = new LinkedList<Node>();
        LinkedList<Segment> path_segments = new LinkedList<Segment>();
        LinkedList<Road> path_roads = new LinkedList<Road>();

        double total_distance = 0.0;
        Node prev_node = targetNode;// set the node
        while (prev_node != null) {
            /*
             * for nodes:
             */
            // add it at the begining of the linkedlist
            // since it's from targetNode to the startNOde
            path_nodes.addFirst(prev_node);

            // not work since the restriction of the provided algorthim
            // this.graph.setHighlight(prev_node);

            /*
             * for segments and roads:
             */
            Node currentNode = prev_node;
            prev_node = prev_node.getPreviousNode(); // set up the previous node,it's also set
                                                     // up the while loop condition
            if (prev_node == null) {
                // reaches the startNOde, break the loop
                break;
            }
            // find the weigh which is the length of the segments
            double lowestWeight = findSegmentWeight(prev_node, currentNode);
            total_distance += lowestWeight;
            // add corresponding element into the linkedList
            path_segments.addFirst(this.lowestWeightSegment);
            path_roads.addFirst(this.lowestWeightSegment.road);
        }
        // this.graph.setHighlight(path_roads);// highlight the roads
        this.graph.setHighlight(path_segments);// highlight the segment

        this.graph.setHighlight_nodes(path_nodes);// highlight nodes

        /*
         * Set up the output string, which includes roadNames, segment length etc. For testing
         * purpose, can also includes the nodeID and RoadID
         */
        String outputString = "The route: \n";
        int index = 0;
        for (Segment segment : path_segments) {
            index++;
            outputString += index + ". " + segment.road.toString() + " " // +
                                                                         // segment.toString()
                            + " : " + String.format("%.2f km", segment.length) + "\n";

        }

        outputString += "\nTotal distance = " + String.format("%.2f km", total_distance);
        getTextOutputArea().setText(outputString);

    }

    /** for the Articulation Points Algorithm use */
    LinkedHashSet<Node> articulationPoints = new LinkedHashSet<Node>();

    @Override
    protected void onAPs() {
        long start = System.currentTimeMillis();

        /*
         * do the initialization of each Node and assign random node as the root node
         */
        String outputString = "";
        this.articulationPoints = new LinkedHashSet<Node>();
        int index = -1;
        int randomIndex = new Random().nextInt(graph.nodes.values().size());

        // Node rootNode = null;// setup the root node
        for (Node node : graph.nodes.values()) {
            index++;
            // reset the previous node, the visited to false ,the depth and the reachBack value
            node.setVisited(false);
            node.setPreviousNode(null);
            node.setDepth(Integer.MAX_VALUE);
            node.setReachBackValue(Integer.MAX_VALUE);
            // // assign the root node, it's random selected
            // if (randomIndex == index) {
            // rootNode = node;
            // }

            // for debug, it's used for the big graph since this group is isolated
            // and easy to see the result, also this root node is the APoint
            // if (node.getNodeID() == 20839) {
            // rootNode = node;
            // rootNode.setDepth(0);
            // }
        }

        for (Node rootNode : graph.nodes.values()) {
            if (rootNode.getDepth() != Integer.MAX_VALUE) {
                continue; // has visited, continue to check the next
            }
            // // for debug, it's used for the big graph since this group is isolated
            // // and easy to see the result, also this root node is the APoint
            // if (rootNode.getNodeID() != 20839) {
            // continue;
            // }

            /*
             * implement the articulation Points algorthim
             */
            int subTreeNumber = 0;
            rootNode.setDepth(0);

            for (Node neighNode : rootNode.getAllNeighbourNodes()) {
                if (neighNode.getDepth() == Integer.MAX_VALUE) {
                    iterativeAPts(neighNode, 1, rootNode);
                    subTreeNumber++;
                }
            }
            // need to move this out since the subTree may be a lot
            if (subTreeNumber > 1) {
                outputString += "The root node is the articulation Point";
                this.articulationPoints.add(rootNode);
            }
        }
        /*
         * highlight nodes and set up the output string
         */
        this.graph.setHighlight_nodes(this.articulationPoints);
        outputString = "The true value of the small/big Graph:\t 240 and 10853" +
                       "\nMy algorthim can find " + this.articulationPoints.size()
                       + " articulation points for this graph: ";
        long end = System.currentTimeMillis();
        long runtime = end - start;
        outputString += "\nMy algorthim runs " + runtime + "  milliseconds!(1000millis=1sec)";

        System.out.println(outputString);
        getTextOutputArea().setText(outputString);
    }

    private void iterativeAPts(Node neighbourNode, int depth, Node rootNode) {
        Stack<Fringe> fringeStack = new Stack<Fringe>();
        fringeStack.push(new Fringe(neighbourNode, depth, rootNode));

        // repeat until the stack is empty
        while (!fringeStack.isEmpty()) {

            // assign variables
            Fringe peekFringe = fringeStack.peek();// just peek, not pop the Fringe out
            Node neighNode_fromPeekFringe = peekFringe.getFirst_neighbourNode();

            // get the childrenNodes, the root node has already been removed
            // via the Fringe constructor
            // ArrayList<Node> temp_childNodes_of_firstNeighbourNode = new
            // ArrayList<Node>(peekFringe
            // .getChildNodes_of_firstNeighbourNode());

            // the depth is still the MAX, it's the first visit,
            // set the reachBack to the depth
            if (neighNode_fromPeekFringe.getDepth() == Integer.MAX_VALUE
            // || !currentNeighbourNode_fromPeekFringe.isVisited()

            ) {

                neighNode_fromPeekFringe.setVisited(true);
                // do the initializion to set up the depth and the reach back
                neighNode_fromPeekFringe.setDepth(peekFringe.getDepth());
                neighNode_fromPeekFringe.setReachBackValue(peekFringe.getDepth());
                // remove the root node from the ChildNodes
                peekFringe.removeFromChildrenNodes(peekFringe.getRootNode());
                // neighNode_fromPeekFringe.removeFromChildrenNodes(peekFringe.getRootNode());
            }

            else if (!peekFringe.getChildNodes_of_firstNeighbourNode().isEmpty()) {
                // get and remove a child from CHildrenNodes_neighbour
                // update the pointer
                Node childNode = peekFringe.get_and_remove_a_node_from_children();
                /*
                 * check whether this childNode has been visted before
                 */

                // it's been visited before since the depth is still the infinity,
                // update the reachBack value of the
                if (childNode.getDepth() < Integer.MAX_VALUE
                // ||
                // childNode.isVisited()
                ) {

                    // compare and update the reachBack value
                    int minReachBackValue_neigh = Math.min(
                            childNode.getDepth(),
                            neighNode_fromPeekFringe.getReachBackValue());

                    neighNode_fromPeekFringe.setReachBackValue(minReachBackValue_neigh);

                }
                // it's the first time to visit this ChildNode,
                // push a new Fringe object into the fringeStack
                else {
                    // increase the depth of this new Fringe instance
                    fringeStack.push(new Fringe(childNode,
                            peekFringe.getDepth() + 1, neighNode_fromPeekFringe));
                }

            }

            else {
                // if (!neighNode_fromPeekFringe.equals(neighbourNode)) {
                if (neighNode_fromPeekFringe.getNodeID() != neighbourNode.getNodeID()) {
                    int minReachBack_rootNOde = Math.min(
                            neighNode_fromPeekFringe.getReachBackValue(),
                            peekFringe.getRootNode().getReachBackValue());

                    peekFringe.getRootNode().setReachBackValue(minReachBack_rootNOde);

                    if (neighNode_fromPeekFringe
                            .getReachBackValue() >= peekFringe.getRootNode()
                                    .getDepth()) {
                        this.articulationPoints.add(peekFringe.getRootNode());
                    }
                }
                fringeStack.pop();
            }

        }
    }

    /**
     * This method does the nasty logic of making sure we always zoom into/out of the centre of
     * the screen. It assumes that scale has just been updated to be either scale * ZOOM_FACTOR
     * (zooming in) or scale / ZOOM_FACTOR (zooming out). The passed boolean should correspond
     * to this, ie. be true if the scale was just increased.
     */
    private void scaleOrigin(boolean zoomIn) {
        Dimension area = getDrawingAreaDimension();
        double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

        int dx = (int) ((area.width - (area.width * zoom)) / 2);
        int dy = (int) ((area.height - (area.height * zoom)) / 2);

        origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
    }

    public static void main(String[] args) {
        new Mapper();
    }
}

// code for COMP261 assignments