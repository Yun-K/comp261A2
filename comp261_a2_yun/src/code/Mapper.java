package code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import myCode.Fringe;
import myCode.FringeArtPoint;

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

    /** for the Articulation Points Algorithm use */
    LinkedHashSet<Node> articulationPoints = new LinkedHashSet<Node>();

    /** for the Articulation Points Algorithm use */
    public static final int INFINITY = Integer.MAX_VALUE;

    /** for challenge */
    static String distanceOrTimeString = "";

    static String travelType_category = "";

    /**
     * Set the distanceOrTimeString.
     *
     * @param distanceOrTimeString
     *            the distanceOrTimeString to set
     */
    public static void setDistanceOrTimeString(String distanceOrTimeString) {
        Mapper.distanceOrTimeString = distanceOrTimeString;
    }

    @Override
    protected void setTravelType(String travelType_category) {
        Mapper.travelType_category = travelType_category;
    }

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

                // should be outgoing,not all neighboiurs, but it's hard to see on the graph,so
                // let's use all neighbours first
                for (Node outgoing_node : currentNode.getAllNeighbourNodes()) {

                    // for (Node outgoing_node : currentNode.getOutGoingNodes()) {

                    if (!outgoing_node.isVisited()) {
                        //
                        // // for checking whether ths heuristic function h() is
                        // // consistent/monotonic
                        // double h_current = currentNode.getLocation()
                        // .distance(targetNode.getLocation());
                        // double h_neigh = outgoing_node.getLocation()
                        // .distance(targetNode.getLocation());
                        // double weight_cost = findSegmentWeight(currentNode, outgoing_node);
                        // if (h_current <= h_neigh + weight_cost) {
                        // if (h_current - h_neigh <= weight_cost) {
                        // System.err.println("It is consistent/monotonic!!!!!");
                        // assert true;
                        // }
                        //
                        // } else {
                        // System.err.println("It is not consistent/monotonic");
                        // assert false;
                        // }

                        // this part for checking one way road
                        // and if it's one way whether it's legal
                        if (!isLegalRoad(currentNode, outgoing_node)) {
                            // it's not legal, do not offer this into the fringeQueue, jump to
                            // search the next
                            System.out.println("!FIND ILLEGAL ROAD SEGMENT!");
                            continue;

                        }

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

        if (weight == Double.POSITIVE_INFINITY) {
            System.err.println(
                    "No segment between startNode to targetNode.\nbut args should be neighbour!"
                               + "\nSomething is incorrect inside the a* algorthim");
            assert false;
        }
        return weight;

    }

    /**
     * Description: <br/>
     * For checking the segment road from currentNode to outgoingNode is one-way road or not.
     * If it's one way, then check whether the route segment from currentNode to outgoingNode
     * is legal or not.
     * 
     * HINT: 1. the currentNode and outgoing node should be adjacent neighbour!
     * <p>
     * 2.If it's one way(i.e. oneway==1), then the allowed direction is from beginning to end!
     * 
     * 
     * @author Yun Zhou
     * @param currentNode
     *            currentNode
     * @param outgoing_node
     *            the outgoing node to check
     * @return true if it's legal, false otherwise
     */
    private boolean isLegalRoad(Node currentNode, Node outgoing_node) {
        boolean isLegal = true;

        for (Segment segment : currentNode.segments) {
            // if this segment is correct segment from startNode to outgoing Node
            if (segment.start.equals(currentNode) && segment.end.equals(outgoing_node)) {
                int onewayValue = segment.road.getOneway();
                // it's one way, direction from beginning to end
                // set isLegal to false first
                if (onewayValue == 1) {
                    // System.out.println("\nThis segment is one way!");
                    isLegal = false;

                    // do 2nd check whether it's legal or not
                    // loop through the components to check
                    for (Segment orderSegment : segment.road.getComponents()) {
                        // if it match the direction then it's legal and return true
                        if (orderSegment.start.equals(currentNode)
                                && orderSegment.end.equals(outgoing_node)) {
                            System.out
                                    .println("Although it's one-way,but it is legal way to move!!");
                            isLegal = true;
                            return isLegal;
                        }
                    }
                }
            }
        }

        //
        if (!isLegal) {
            System.out.println("Successfully avoid one way road");
        }
        return isLegal;
    }

    /**
     * Description: <br/>
     * Highligh all nodes, segments and setup the output string.
     * 
     * @author Yun Zhou
     */
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
        // String outputString = "The route: \n";
        // int index = 0;
        // for (Segment segment : path_segments) {
        // index++;
        // outputString += index + ". " + segment.road.toString() + " " // +
        // // segment.toString()
        // + " : " + String.format("%.2f km", segment.length) + "\n";
        //
        // }

        String outputString = "The route: \n";
        // the map for removing duplicate roads on output
        // the linkedHashMAp can make sure the order
        LinkedHashMap<String, Double> roadName_to_length_map = new LinkedHashMap<String, Double>();
        for (Segment segment : path_segments) {
            String key = segment.road.getName();
            // if it's first time to access
            if (!roadName_to_length_map.containsKey(key)) {
                roadName_to_length_map.put(key, segment.length);
            } else {
                // it's not first time, update the value length
                double newLength_value = roadName_to_length_map.get(key) + segment.length;
                roadName_to_length_map.put(key, newLength_value);
            }

        }

        // loop through the map to set up the output string
        int index = 0;
        for (String key : roadName_to_length_map.keySet()) {
            index++;
            double leng = roadName_to_length_map.get(key);
            outputString += index + ". " + key.toString() + "  :  "
                            + String.format("%.2f km", leng) + "\n";
        }

        outputString += "\nTotal distance = " + String.format("%.2f km", total_distance);
        getTextOutputArea().setText(outputString);

    }

    @Override
    protected void onAPs() {
        long start = System.currentTimeMillis();

        /*
         * do the initialization of each Node and assign random node as the root node
         */
        String outputString = "";
        this.articulationPoints = new LinkedHashSet<Node>();
        // int index = -1;
        // int randomIndex = new Random().nextInt(graph.nodes.values().size());
        // Node rootNode = null;// setup the root node
        for (Node node : graph.nodes.values()) {
            // index++;

            // reset the previous node, the visited to false ,the depth and the reachBack value
            node.setVisited(false);
            node.setPreviousNode(null);
            node.setDepth(INFINITY);
            node.setReachBackValue(INFINITY);
            // // assign the root node, it's random selected
            // if (randomIndex == index) {
            // rootNode = node;
            // }
            // if (node.getNodeID() == 20839) {
            // rootNode = node;
            // }

        }

        // treat every node as the root node, check if it's visited before
        // if it's visited before, then jump to check next
        // otherwise, implement the articulation Points algorthim
        for (Node rootNode : graph.nodes.values()) {
            if (rootNode.getDepth() != INFINITY || rootNode.isVisited()) {
                continue; // has visited, continue to check the next
            }

            // for debug, ids are for the large graph since these groups where ids belongs
            // are isolated so that easy to see the result, also these root node with these
            // ids are also the APoint
            // if (rootNode.getNodeID() != 20839 || rootNode.getNodeID() != 20384 ||
            // rootNode.getNodeID() != 20206) {
            // continue;
            // }

            /*
             * implement the articulation Points algorthim
             */
            int subTreeNumber = 0;
            // loop through the neighbours
            for (Node neighNode : rootNode.getAllNeighbourNodes()) {
                // if this neighbour is the first visit, then do iterative DFS
                if (neighNode.getDepth() == INFINITY
                        || !neighNode.isVisited()) {
                    iterativeAPts(neighNode, 1, rootNode);
                    subTreeNumber++;
                }
            }
            // need to move this out since the subTree may be a lot,just need to add once
            if (subTreeNumber > 1) {
                // System.out.println("The root node is the artic point");//debug
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

    /**
     * Description: <br/>
     * The iterative version of the articulation point algorthim.
     * <p>
     * Goal: set/update reachBack of each node in the sub-tree
     * <p>
     * • For each node, reachBack is set/updated in the following cases:
     * <p>
     * – When first visited: reachBack = depth
     * <p>
     * – When one of its children’s reachBack is updated: reachBack = min(childReach,
     * reachBack)
     * <p>
     * – When all the children have been calculated, update the reachBack of its parent
     * <p>
     * • Need to update the node information in different stages of DFS
     * 
     * 
     * @author Yun Zhou
     * @param currentNode
     *            current node
     * @param depth
     *            the depth
     * @param rootNode
     *            the parent node of the currentNode arg
     */
    private void iterativeAPts(Node currentNode, int depth, Node rootNode) {
        Stack<FringeArtPoint> fringeArtPStack = new Stack<FringeArtPoint>();
        fringeArtPStack.push(new FringeArtPoint(currentNode, depth, rootNode));

        // repeat until the stack is empty
        while (!fringeArtPStack.isEmpty()) {

            // assign variables
            FringeArtPoint peekFringe = fringeArtPStack.peek();// just peek, not pop out
            Node neighNode_fromPeekFringe = peekFringe.getFirst_neighbourNode();

            // the depth is still the INFINITY, it's the first visit,
            // set the reachBack to the depth
            if (neighNode_fromPeekFringe.getDepth() == INFINITY
                    || !neighNode_fromPeekFringe.isVisited()) {

                // do the initializion to set up the depth and the reach back and visit=true
                neighNode_fromPeekFringe.setVisited(true);
                neighNode_fromPeekFringe.setDepth(peekFringe.getDepth());
                neighNode_fromPeekFringe.setReachBackValue(peekFringe.getDepth());
                // initialize the children of the neighboyrNode
                // add all neighbour NOdes from the segments except parent
                // which is the root node
                this.remove_node_from_children(neighNode_fromPeekFringe, peekFringe.getRootNode());

                // can not do this operation directly inside the Fringe or Node,
                // since the artic points number is incorrect
                // maybe it's the pointer problem?
                // Because as the field, the childNode's depth and reachBack is different?
                // neighNode_fromPeekFringe.removeFromChildrenNodes(peekFringe.getRootNode());

            }

            /*
             * do the DFS of the child of child
             */
            // else if (!peekFringe.getChildNodes_of_firstNeighbourNode().isEmpty()) {
            else if (!neighNode_fromPeekFringe.getChildrenNodes().isEmpty()) {

                // get and remove a child from CHildrenNodes_neighbour
                // update the pointer
                Node childNode = neighNode_fromPeekFringe.get_and_remove_a_node_from_children();
                /*
                 * check whether this childNode has been visted before
                 */

                // it's been visited before since the depth is still the infinity,
                // update the reachBack value of the neighNode from peekFringe
                if (childNode.getDepth() < INFINITY
                        ||
                        childNode.isVisited()) {

                    // compare and update the reachBack value
                    int minReachBackValue_neigh = Math.min(
                            childNode.getDepth(),
                            neighNode_fromPeekFringe.getReachBackValue());

                    neighNode_fromPeekFringe.setReachBackValue(minReachBackValue_neigh);

                }
                // it's the first time to visit this neighNode.ChildNode,
                // push a new Fringe object into the fringeStack, and do the DFS
                else {
                    // increase the depth of this new Fringe instance
                    fringeArtPStack.push(new FringeArtPoint(childNode,
                            peekFringe.getDepth() + 1, neighNode_fromPeekFringe));
                }

            }

            //
            else {
                // if (neighNode_fromPeekFringe.getNodeID() != currentNode.getNodeID()) {
                // update the reachBack of peekFringe.rootNode
                if (!neighNode_fromPeekFringe.equals(currentNode)) {
                    int minReachBack_rootNOde = Math.min(
                            neighNode_fromPeekFringe.getReachBackValue(),
                            peekFringe.getRootNode().getReachBackValue());

                    peekFringe.getRootNode().setReachBackValue(minReachBack_rootNOde);

                    //
                    if (neighNode_fromPeekFringe
                            .getReachBackValue() >= peekFringe.getRootNode().getDepth()) {
                        this.articulationPoints.add(peekFringe.getRootNode());
                    }
                }
                fringeArtPStack.pop();// pop out the fringe
            }

        }

    }

    /**
     * Description: <br/>
     * COnstruct and set the childNodes of the currentNode. And remove the specificied
     * nodeToRemove which is the parent node.
     * 
     * @author Yun Zhou
     * @param currentNode
     *            the current node that need to set up the children
     * @param nodeToRemove
     *            the parent which need to be removed from current.childrenNode
     */
    private void remove_node_from_children(Node currentNode, Node nodeToRemove) {

        Set<Node> tempchildNodes = new LinkedHashSet<Node>();
        for (Segment segment : currentNode.segments) {
            tempchildNodes.add(segment.end);
            tempchildNodes.add(segment.start);
        }
        tempchildNodes.remove(nodeToRemove);// remove the specificed node
        // remove itself since the neighbour cannot be itself, much more easier, dont
        // need to do the if check
        tempchildNodes.remove(currentNode);

        currentNode.setChildrenNodes(tempchildNodes);

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