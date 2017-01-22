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
 * The GPSDate Class - represents a date.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class GPSDate {
    
    private final int day;
    private final int month;
    private final int year;
    
    /**
     * Constructor.
     * 
     * @param day the day of the month
     * @param month the month of the year
     * @param year the year
     */
    public GPSDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
    
    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", day, month, year);
    }
    
    /**
     * Format the date in yyyymmdd format.
     * 
     * @return the formated string
     */
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
