package code;

import java.util.Collection;
import java.util.HashSet;

/**
 * Road represents ... a road ... in our graph, which is some metadata and a collection of
 * Segments. We have lots of information about Roads, but don't use much of it.
 * 
 * @author tony
 */
public class Road {
    public final int roadID;

    public final String name, city;

    public final Collection<Segment> components;

    // 0 : both directions allowed
    // 1 : one way road, direction from beginning to end
    private int oneway;

    private int notforpede;

    private int notforbicy;

    private int roadclass;

    private int notforcar;

    private int speed;

    private String label;

    public Road(int roadID, int type, String label, String city, int oneway,
            int speed, int roadclass, int notforcar, int notforpede,
            int notforbicy) {
        this.roadID = roadID;
        this.city = city;
        this.name = label;
        this.components = new HashSet<Segment>();
        //
        this.notforbicy = notforbicy;
        this.notforpede = notforpede;
        this.notforcar = notforcar;
        this.roadclass = roadclass;
        this.speed = speed;
        this.label = label;
        this.oneway = oneway;
    }

    public void addSegment(Segment seg) {
        components.add(seg);
    }

    @Override
    public String toString() {
        return "Road [roadID=" + roadID + ", name=" + name + "]";
    }

    /**
     * Get the oneway.
     *
     * @return the oneway
     */
    public int getOneway() {
        return oneway;
    }

    /**
     * Set the oneway.
     *
     * @param oneway
     *            the oneway to set
     */
    public void setOneway(int oneway) {
        this.oneway = oneway;
    }

    /**
     * Get the notforpede.
     *
     * @return the notforpede
     */
    public int getNotforpede() {
        return notforpede;
    }

    /**
     * Set the notforpede.
     *
     * @param notforpede
     *            the notforpede to set
     */
    public void setNotforpede(int notforpede) {
        this.notforpede = notforpede;
    }

    /**
     * Get the notforbicy.
     *
     * @return the notforbicy
     */
    public int getNotforbicy() {
        return notforbicy;
    }

    /**
     * Set the notforbicy.
     *
     * @param notforbicy
     *            the notforbicy to set
     */
    public void setNotforbicy(int notforbicy) {
        this.notforbicy = notforbicy;
    }

    /**
     * Get the roadclass.
     *
     * @return the roadclass
     */
    public int getRoadclass() {
        return roadclass;
    }

    /**
     * Set the roadclass.
     *
     * @param roadclass
     *            the roadclass to set
     */
    public void setRoadclass(int roadclass) {
        this.roadclass = roadclass;
    }

    /**
     * Get the notforcar.
     *
     * @return the notforcar
     */
    public int getNotforcar() {
        return notforcar;
    }

    /**
     * Set the notforcar.
     *
     * @param notforcar
     *            the notforcar to set
     */
    public void setNotforcar(int notforcar) {
        this.notforcar = notforcar;
    }

    /**
     * Get the speed.
     *
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set the speed.
     *
     * @param speed
     *            the speed to set
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Get the label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label.
     *
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the roadID.
     *
     * @return the roadID
     */
    public int getRoadID() {
        return roadID;
    }

    /**
     * Get the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Get the components.
     *
     * @return the components
     */
    public Collection<Segment> getComponents() {
        return components;
    }
}

// code for COMP261 assignments