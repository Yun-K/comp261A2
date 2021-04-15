package myCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import code.Node;
import code.Road;

/***
 * Description: <br/>
 * My restriction class for parsing the restrictions.tab file to the java object.
 * 
 * @author Yun Zhou 300442776
 * @version
 */
public class Restriction {

    int nodeID_1, roadID_1, nodeID, roadID_2, nodeID_2;

    static List<Restriction> allRestrictions = new ArrayList<Restriction>();

    /**
     * A constructor. It construct a new instance of Restriction.
     *
     * @param nodeID_1
     * @param roadID_1
     * @param nodeID
     * @param roadID_2
     * @param nodeID_2
     */
    public Restriction(int nodeID_1, int roadID_1, int nodeID, int roadID_2, int nodeID_2) {
        this.nodeID_1 = nodeID_1;
        this.roadID_1 = roadID_1;
        this.nodeID = nodeID;
        this.roadID_2 = roadID_2;
        this.nodeID_2 = nodeID_2;
    }

    public static List<Restriction> parseRestrictions(File restrictionFile) {
        if (restrictionFile == null) {
            return null;

        }

        allRestrictions = new ArrayList<Restriction>();
        try {
            // make a reader
            BufferedReader br = new BufferedReader(new FileReader(restrictionFile));
            String line = br.readLine();// jump the first line

            // read in each line of the file
            while ((line = br.readLine()) != null) {
                // tokenise the line by splitting it at the tabs.
                String[] tokens = line.split("[\t]+");

                // process the tokens
                int tnodeID_1 = asInt(tokens[0]);
                int troadID_1 = asInt(tokens[1]);
                int tnodeID = asInt(tokens[2]);
                int troadID_2 = asInt(tokens[3]);
                int tnodeID_2 = asInt(tokens[4]);

                allRestrictions
                        .add(new Restriction(tnodeID_1, troadID_1, tnodeID, troadID_2, tnodeID_2));
            }

            br.close();
        } catch (IOException e) {
            throw new RuntimeException("file reading failed.");
        }

        return allRestrictions;
    }

    private static int asInt(String str) {
        return Integer.parseInt(str);
    }

    private static double asDouble(String str) {
        return Double.parseDouble(str);
    }

    // Node nodeID_1() {
    //
    //
    //
    //
    // }
    //
    // Road roadID_1() {
    // }
    //
    // Node nodeID() {
    // }
    //
    // Road roadID_2() {
    // }
    //
    // Node nodeID_2() {
    // }

}
