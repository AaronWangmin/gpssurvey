package linsdale.gpssurvey;

/**
 *
 * @author richard
 */
public class GPSTime {

    private final int hours;
    private final int minutes;
    private final int seconds;

    public GPSTime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String toISOString() {
        return String.format("%02d%02d%02d", hours, minutes, seconds);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + hours;
        hash = 53 * hash + minutes;
        hash = 53 * hash + seconds;
        return hash;
    }

    @Override
    public boolean equals(Object t) {
        if (!(t instanceof GPSTime)) {
            return false;
        }
        GPSTime time = (GPSTime) t;
        return time.hours == hours && time.minutes == minutes && time.seconds == seconds;
    }
    
    public boolean prior(GPSTime t) {
        // true if instance is prior to t; false if equal or after
        if (t == null ) { return false;}
        if (hours != t.hours) { return hours < t.hours ;}
        if (minutes != t.minutes) {return minutes < t.minutes;}
        return seconds < t.seconds;
    }
}
