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
 * The Position Class.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Position {

    private final static double metresPerDegreeLat = 6371000 * Math.PI / 180;

    /**
     * The Latitude
     */
    public final Latitude latitude;

    /**
     * The Longitude
     */
    public final Longitude longitude;

    /**
     * The X coordinate (east) from origin
     */
    public final Coordinate x;

    /**
     * The y coordinate (north) from origin
     */
    public final Coordinate y;

    /**
     * The distance from origin
     */
    public final Distance distance;

    /**
     * The course from origin
     */
    public final Course course;

    /**
     * Constructor. Create a position with latitude/longitude values only.
     * 
     * @param latitude the latitude
     * @param longitude the longitude
     */
    public Position(Latitude latitude, Longitude longitude) {
        this(latitude, longitude, null);
    }

    /**
     * Constructor.  Create a position with both latitude/longitude and relative values.
     * 
     * @param from the origin for relative calculation
     * @param to the position
     */
    public Position(Position from, Position to) {
        this(to.latitude, to.longitude, from);
    }

    /**
     * Constructor.  Create a position with both latitude/longitude and relative values.
     * 
     * @param latitude the latitude
     * @param longitude the longitude
     * @param origin the origin for relative calculation
     */
    public Position(Latitude latitude, Longitude longitude, Position origin) {
        this.latitude = latitude;
        this.longitude = longitude;
        if (origin != null) {
            double avLat = (origin.latitude.get() + latitude.get()) / 2;
            double metresPerDegreeLong = metresPerDegreeLat * Math.cos(Math.toRadians(avLat));
            double yval = (origin.longitude.get() - longitude.get()) * metresPerDegreeLong;
            double xval = (origin.latitude.get() - latitude.get()) * metresPerDegreeLat;
            y = new Coordinate(yval);
            x = new Coordinate(xval);
            // simple course and distance calculation - uses flat triangle approxamation rather than great circle
            distance = new Distance(Math.sqrt(xval * xval + yval * yval));
            double angle = Math.toDegrees(Math.atan2(xval, yval));
            while (angle < 0.0) {
                angle += 360.0; // normalise angle (0 to 360)
            }
            course = new Course(angle);
        } else {
            y = new Coordinate(0);
            x = new Coordinate(0);
            distance = new Distance(0);
            course = new Course(0);
        }
    }
    
    private Position(Latitude latitude, Longitude longitude, Coordinate x, Coordinate y,
            Distance distance, Course course) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.course = course;
    }
    
   @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Position clone() {
        return new Position(latitude.clone(), longitude.clone(), x.clone(), y.clone(), distance.clone(), course.clone());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Position) {
            Position otherp = (Position) other;
            return this.x.equals(otherp.x) && this.y.equals(otherp.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return latitude.hashCode() + longitude.hashCode();
    }
}
