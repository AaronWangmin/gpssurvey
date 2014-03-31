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
package linsdale.gpssurvey;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class Position {

    private final static double metresPerDegreeLat = 6371000 * Math.PI / 180;

    public final Latitude latitude;
    public final Longitude longitude;
    public final Coordinate x;
    public final Coordinate y;
    public final Distance distance;
    public final Course course;

    public Position(Latitude latitude, Longitude longitude) {
        this(latitude, longitude, null);
    }

    public Position(Position from, Position to) {
        this(to.latitude, to.longitude, from);
    }

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
