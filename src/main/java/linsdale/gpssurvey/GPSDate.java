package linsdale.gpssurvey;

/**
 *
 * @author richard
 */
public class GPSDate {
    
    private final int day;
    private final int month;
    private final int year;
    
    public GPSDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
    
    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", day, month, year);
    }
    
    public String toISOString() {
        return String.format("%04d%02d%02d", year, month, day);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.day;
        hash = 53 * hash + this.month;
        hash = 53 * hash + this.year;
        return hash;
    }
    
    @Override
    public boolean equals(Object d) {
        if (!(d instanceof GPSDate)){
            return false;
        }
        GPSDate date = (GPSDate) d;
        return date.day == day && date.month == month && date.year == year;
    }
}
