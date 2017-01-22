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
package uk.theretiredprogrammer.gpssurvey.informationstore;

import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.gpssurvey.Altitude;
import uk.theretiredprogrammer.gpssurvey.Depth;
import uk.theretiredprogrammer.gpssurvey.HDOP;
import uk.theretiredprogrammer.gpssurvey.Position;
import uk.theretiredprogrammer.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;

/**
 * The Location Data Class. Central register of current location data.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class LocationData {

    private final static double MPS2KNOTS = 1.943844; // metres/sec to knots
    //
    private String datedisplay;
    private String isodatedisplay;
    private String timedisplay;
    private String isotimedisplay;
    private Location location;
    private double heading = 0;
    private double speed = 0; // m/s
    private double knots = 0;
    //
    private Location refLocation;
    // recorder data
    private Position startPosition;
    private List<Position> points = new ArrayList<>();
    private List<Position> startPoints = new ArrayList<>();
    private List<Position> endPoints = new ArrayList<>();
    private boolean recording = false;

    /**
     * Constructor
     */
    protected LocationData() {
        location = null;
        refLocation = null;
    }

    /**
     * Constructor
     *
     * @param gpsdata the gps data to used to define the location
     */
    protected LocationData(ConsolidatedGPSData gpsdata) {
        setLocation(gpsdata);
    }

    /**
     * Update the Location data with new gps data.
     *
     * @param gpsdata the gps data to used to define the location
     * @return this location data object
     */
    protected final synchronized LocationData setLocation(ConsolidatedGPSData gpsdata) {
        timedisplay = gpsdata.time.toString();
        isotimedisplay = gpsdata.time.toISOString();
        datedisplay = gpsdata.date.toString();
        isodatedisplay = gpsdata.date.toISOString();
        Position p = refLocation != null ? new Position(refLocation.position, gpsdata.position) : gpsdata.position;
        if (location == null) {
            location = new Location(p, gpsdata.altitude, gpsdata.hDOP);
        } else {
            location.set(p, gpsdata.altitude, gpsdata.hDOP);
        }
        heading = gpsdata.course;
        speed = gpsdata.speed;
        knots = getSpeed() * MPS2KNOTS;
        return this;
    }

    /**
     * Set the reference location
     *
     * @param location the reference location.
     * @return this location data object
     */
    protected final synchronized LocationData setReferenceLocation(Location location) {
        refLocation = location;
        return this;
    }

    /**
     * Get the gps date
     *
     * @return the GPS date
     */
    public final synchronized String getDatedisplay() {
        return datedisplay;
    }

    /**
     * Get the gps date
     *
     * @return the GPS data in yyymmdd format
     */
    public final synchronized String getIsodatedisplay() {
        return isodatedisplay;
    }

    /**
     * Get the gps time
     *
     * @return the gps time
     */
    public final synchronized String getTimedisplay() {
        return timedisplay;
    }

    /**
     * Get the gps time
     *
     * @return the GPS time in hhmmss format
     */
    public final synchronized String getIsotimedisplay() {
        return isotimedisplay;
    }

    /**
     * Get the current location.
     *
     * @return the current location
     */
    public final synchronized Location getLocation() {
        return location;
    }

    /**
     * Get the current course.
     *
     * @return the current course
     */
    public final synchronized double getHeading() {
        return heading;
    }

    /**
     * Get the current speed.
     *
     * @return the current speed.
     */
    public final synchronized double getSpeed() {
        return speed;
    }

    /**
     * Get the current speed (in knots)
     *
     * @return the current speed (in knots)
     */
    public final synchronized double getKnots() {
        return knots;
    }

    /**
     * Get the current position
     *
     * @return the current position
     */
    public final synchronized Position getPosition() {
        return getLocation().getPosition();
    }

    /**
     * Get the current HDOP
     *
     * @return the current HDOP
     */
    public final synchronized HDOP getHDOP() {
        return getLocation().getHDOP();
    }

    /**
     * Get the current altitude
     *
     * @return the current altitude
     */
    public final synchronized Altitude getAltitude() {
        return getLocation().getAltitude();
    }

    /**
     * Get the current depth
     *
     * @return the current depth
     */
    public final synchronized Depth getDepth() {
        return getLocation().getDepth();
    }

    /**
     * Get the Reference location
     *
     * @return the reference location
     */
    public final synchronized Location getReferenceLocation() {
        return refLocation;
    }

    /**
     * Get the start position (of a track)
     *
     * @return the start position
     */
    public final synchronized Position getStartPosition() {
        return startPosition;
    }

    /**
     * Set the start position (of a track)
     */
    public final synchronized void setStartPosition() {
        startPosition = getPosition();
    }

    /**
     * Clear the start position (of a track)
     */
    public final synchronized void clearStartPosition() {
        startPosition = null;
    }

    /**
     * Get the set of position which are recorded as the current track
     *
     * @return list of positions
     */
    public final synchronized List<Position> getPoints() {
        return points;
    }

    /**
     * Get the current position relation to the last position recorded on the
     * track.
     *
     * @return the current position
     */
    public final synchronized Position getCurrentFromLastPoint() {
        return new Position(points.get(points.size() - 1), getPosition());
    }

    /**
     * Get the current position relation to a selected position recorded on the
     * track.
     *
     * @param i the index of the position on the track to be used as origin
     * position
     * @return the current position
     */
    public final synchronized Position getCurrentFromPoint(int i) {
        return new Position(points.get(i), getPosition());
    }

    /**
     * Get the position of a point on the track relative to the previous point
     * on the track.
     *
     * @param i the index of the position on the track to be calculated
     * @return the indexed items relative position
     */
    public final synchronized Position getPointFromPreviousPoint(int i) {
        return new Position(points.get(i - 1), points.get(i));
    }

    /**
     * test if track has no points defined.
     *
     * @return true if track is empty
     */
    public final synchronized boolean hasNoPoints() {
        return points.isEmpty();
    }

    /**
     * Get the size of the track
     *
     * @return the number of points in the track
     */
    public final synchronized int getPointsSize() {
        return points.size();
    }

    /**
     * Add the current location to the track
     */
    public final synchronized void addPoint() {
        points.add(getPosition());
    }

    /**
     * Get the set of start points for recorded tracks.
     *
     * @return list of start points
     */
    public final synchronized List<Position> getStartPoints() {
        return startPoints;
    }

    /**
     * Get a set of end points for recorded tracks.
     *
     * @return list of end points
     */
    public final synchronized List<Position> getEndPoints() {
        return endPoints;
    }

    /**
     * Add the current track to the start and end point lists.
     */
    public final synchronized void addStartEndPoints() {
        startPoints.add(startPosition);
        endPoints.add(getPosition());
    }

    /**
     * Get the position of the first end point relative to first start point.
     *
     * @return the relative position of the first end point
     */
    public final synchronized Position getFirstStartEnd() {
        return new Position(startPoints.get(0), endPoints.get(0));
    }

    /**
     * Get the relative position of the current position relative to the start
     * position of the current track.
     *
     * @return the current position
     */
    public final synchronized Position getCurrentFromStart() {
        return new Position(startPosition, getPosition());
    }

    /**
     * Get the relative position of the current position relation to the end
     * point of the last recorded track.
     *
     * @return the current position
     */
    public final synchronized Position getCurrentFromEnd() {
        return new Position(endPoints.get(endPoints.size() - 1), getPosition());
    }

    /**
     * Test if any tracks have been recorded.
     *
     * @return true if no tracks have yet been recorded
     */
    public final synchronized boolean hasNoStartEndPoints() {
        return startPoints.isEmpty();
    }

    /**
     * Get the number of tracks recorded.
     *
     * @return the number of tracks recorded.
     */
    public final synchronized int getStartEndPointsSize() {
        return startPoints.size();
    }

    /**
     * Test if recording is in progress.
     *
     * @return true if recording is in progress
     */
    public final synchronized boolean isRecording() {
        return recording;
    }

    /**
     * Set recording state.
     *
     * @param recording true if recording to start
     */
    public final synchronized void setRecording(boolean recording) {
        this.recording = recording;
    }

    /**
     * The data object holding current location information
     */
    public static class Location {

        private Position position;
        private HDOP hDOP;
        private Altitude altitude;
        private Depth depth;

        /**
         * Constructor.
         *
         * @param position the position
         * @param altitude the altitude
         * @param hDOP the HDOP
         */
        public Location(Position position, Altitude altitude, HDOP hDOP) {
            set(position, altitude, hDOP);
        }

        /**
         * Constructor
         *
         * @param position the position
         * @param altitude the altitude
         * @param hDOP the HDOP
         * @param depth the depth
         */
        public Location(Position position, Altitude altitude, HDOP hDOP, Depth depth) {
            set(position, altitude, hDOP);
            setDepth(depth);
        }

        @Override
        @SuppressWarnings({"CloneDeclaresCloneNotSupported", "CloneDoesntCallSuperClone"})
        public Location clone() {
            return new Location(position.clone(), altitude.clone(), hDOP.clone(), depth == null ? null : depth.clone());
        }

        /**
         * Update gps Location data.
         *
         * @param position the position
         * @param altitude the altitude
         * @param hDOP the HDOP
         * @return this location object
         */
        public final synchronized Location set(Position position, Altitude altitude, HDOP hDOP) {
            this.position = position;
            this.altitude = altitude;
            this.hDOP = hDOP;
            return this;
        }

        /**
         * Update depth Location data
         *
         * @param depth the depth
         * @return this location object
         */
        protected final synchronized Location setDepth(Depth depth) {
            this.depth = depth;
            return this;
        }

        /**
         * Get the current position
         *
         * @return the position
         */
        public final synchronized Position getPosition() {
            return position;
        }

        /**
         * Get the current HDOP
         * @return the HDOP 
         */
        public final synchronized HDOP getHDOP() {
            return hDOP;
        }

        /**
         * Get the current altitude
         * 
         * @return the altitude
         */
        public final synchronized Altitude getAltitude() {
            return altitude;
        }

        /**
         * Get the current depth
         * 
         * @return the depth
         */
        public final synchronized Depth getDepth() {
            return depth;
        }
    }
}
