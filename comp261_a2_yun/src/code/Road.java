package code;

import java.util.Collection;
import java.util.LinkedHashSet;

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
    public int oneway;

    public boolean notforPeopleWalk;

    public boolean notforbicy;

    public boolean notforcar;

    public int roadclass;

    public int speed;

    public String label;

    // public String[] speedLimitCate = { "5 km/h", "20 km/h", "40 km/h", "60 km/h", "80 km/h",
    // "100 km/h", "110 km/h", "no limit" };

    public int[] speedLimitCate = { 5, 20, 40, 60, 80, 100, 110, Integer.MAX_VALUE };

    // 0 = Residential
    // 1 = Collector
    // 2 = Arterial
    // 3 = Principal HW
    // 4 = Major HW
    public String[] roadClassArray = { "Residential", "Collector", "Arterial", "Principal HW",
            "Major HW" };

    public String roadClassString;

    public Road(int roadID, int type, String label, String city, int oneway,
            int speed, int roadclass, int notforcar, int notforpede,
            int notforbicy) {
        this.roadID = roadID;
        this.city = city;
        this.name = label;
        this.components = new LinkedHashSet<Segment>();// change it to LinkedHashSet so that
                                                       // it's ordered and got index
        //
        this.oneway = oneway;
        //
        if (notforbicy == 1) {
            this.notforbicy = true;
        } else if (notforbicy == 0) {
            this.notforbicy = false;
        }
        if (notforpede == 1) {
            this.notforPeopleWalk = true;
        } else if (notforpede == 0) {
            this.notforPeopleWalk = false;
        }
        if (notforcar == 1) {
            this.notforcar = true;
        } else if (notforcar == 0) {
            this.notforcar = false;
        }
        //
        this.label = label;
        // assign by the index
        this.speed = speedLimitCate[speed];
        this.roadclass = roadclass;
        this.roadClassString = roadClassArray[roadclass];
    }

    /**
     * Get the notforPeopleWalk.
     *
     * @return the notforPeopleWalk
     */
    public boolean isNotforPeopleWalk() {
        return notforPeopleWalk;
    }

    /**
     * Set the notforPeopleWalk.
     *
     * @param notforPeopleWalk
     *            the notforPeopleWalk to set
     */
    public void setNotforPeopleWalk(boolean notforPeopleWalk) {
        this.notforPeopleWalk = notforPeopleWalk;
    }

    /**
     * Get the notforbicy.
     *
     * @return the notforbicy
     */
    public boolean isNotforbicy() {
        return notforbicy;
    }

    /**
     * Set the notforbicy.
     *
     * @param notforbicy
     *            the notforbicy to set
     */
    public void setNotforbicy(boolean notforbicy) {
        this.notforbicy = notforbicy;
    }

    /**
     * Get the notforcar.
     *
     * @return the notforcar
     */
    public boolean isNotforcar() {
        return notforcar;
    }

    /**
     * Set the notforcar.
     *
     * @param notforcar
     *            the notforcar to set
     */
    public void setNotforcar(boolean notforcar) {
        this.notforcar = notforcar;
    }

    /**
     * Get the speedLimitCate.
     *
     * @return the speedLimitCate
     */
    public int[] getSpeedLimitCate() {
        return speedLimitCate;
    }

    /**
     * Set the speedLimitCate.
     *
     * @param speedLimitCate
     *            the speedLimitCate to set
     */
    public void setSpeedLimitCate(int[] speedLimitCate) {
        this.speedLimitCate = speedLimitCate;
    }

    /**
     * Get the roadClassArray.
     *
     * @return the roadClassArray
     */
    public String[] getRoadClassArray() {
        return roadClassArray;
    }

    /**
     * Set the roadClassArray.
     *
     * @param roadClassArray
     *            the roadClassArray to set
     */
    public void setRoadClassArray(String[] roadClassArray) {
        this.roadClassArray = roadClassArray;
    }

    /**
     * Get the roadClassString.
     *
     * @return the roadClassString
     */
    public String getRoadClassString() {
        return roadClassString;
    }

    /**
     * Set the roadClassString.
     *
     * @param roadClassString
     *            the roadClassString to set
     */
    public void setRoadClassString(String roadClassString) {
        this.roadClassString = roadClassString;
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