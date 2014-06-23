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
 * The GPSDate Class - represents a date.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
