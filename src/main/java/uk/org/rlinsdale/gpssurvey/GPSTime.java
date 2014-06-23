/*
 * Copyright (C) 2014 Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.org.rlinsdale.gpssurvey;

/**
 * The GPS Time class.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSTime {

    private final int hours;
    private final int minutes;
    private final int seconds;

    /**
     * Constructor.
     * 
     * @param hours hours of the day
     * @param minutes minutes of the hour
     * @param seconds seconds of the minute
     */
    public GPSTime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Format the date in hhmmss format.
     * 
     * @return the formated string
     */
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
    
    /**
     * Test is this time is prior to another time.
     * 
     * @param t the time to be used for comparison
     * @return true if this time is prior to time t
     */
    public boolean prior(GPSTime t) {
        // true if instance is prior to t; false if equal or after
        if (t == null ) { return false;}
        if (hours != t.hours) { return hours < t.hours ;}
        if (minutes != t.minutes) {return minutes < t.minutes;}
        return seconds < t.seconds;
    }
}
