/*
 * Copyright 2014-2017 Richard Linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.gpssurvey;

/**
 * The GPS Time class.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
