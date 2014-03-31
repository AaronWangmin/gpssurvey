package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
 */
public class Course {

    private final double value;

    public Course(double value) {
        this.value = value;
    }

    public double get() {
        return value;
    }

    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Course clone() {
        return new Course(value);
    }
    
    @Override
    public String toString() {
        return String.format("%6.0f", value);
    }
    
    public String toStringWithUnits() {
        return String.format("%6.0f°", value);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Course) {
            Course otherC = (Course) other;
            return this.toString().equals(otherC.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
