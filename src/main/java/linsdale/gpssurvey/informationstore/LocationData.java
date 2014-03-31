package linsdale.gpssurvey.informationstore;

import java.util.ArrayList;
import java.util.List;
import linsdale.gpssurvey.Altitude;
import linsdale.gpssurvey.Depth;
import linsdale.gpssurvey.HDOP;
import linsdale.gpssurvey.Position;
import linsdale.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;

/**
 *
 * @author richard
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

    protected LocationData() {
        location = null;
        refLocation = null;
    }

    protected LocationData(ConsolidatedGPSData gpsdata) {
        setLocation(gpsdata);
    }

    protected final synchronized LocationData setLocation(ConsolidatedGPSData gpsdata) {
        timedisplay = gpsdata.time.toString();
        isotimedisplay = gpsdata.time.toISOString();
        datedisplay = gpsdata.date.toString();
        isodatedisplay = gpsdata.date.toISOString();
        Position p = refLocation != null? new Position(refLocation.position, gpsdata.position): gpsdata.position;
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

    protected final synchronized LocationData setReferenceLocation(Location location) {
        refLocation = location;
        return this;
    }

    public final synchronized String getDatedisplay() {
        return datedisplay;
    }

    public final synchronized String getIsodatedisplay() {
        return isodatedisplay;
    }

    public final synchronized String getTimedisplay() {
        return timedisplay;
    }

    public final synchronized String getIsotimedisplay() {
        return isotimedisplay;
    }

    public final synchronized Location getLocation() {
        return location;
    }

    public final synchronized double getHeading() {
        return heading;
    }

    public final synchronized double getSpeed() {
        return speed;
    }

    public final synchronized double getKnots() {
        return knots;
    }

    public final synchronized Position getPosition() {
        return getLocation().getPosition();
    }

    public final synchronized HDOP getHDOP() {
        return getLocation().getHDOP();
    }

    public final synchronized Altitude getAltitude() {
        return getLocation().getAltitude();
    }

    public final synchronized Depth getDepth() {
        return getLocation().getDepth();
    }

    public final synchronized Location getReferenceLocation() {
        return refLocation;
    }

    public final synchronized Position getStartPosition() {
        return startPosition;
    }

    public final synchronized void setStartPosition() {
        startPosition = getPosition();
    }

    public final synchronized void clearStartPosition() {
        startPosition = null;
    }

    public final synchronized List<Position> getPoints() {
        return points;
    }

    public final synchronized Position getCurrentFromLastPoint() {
        return new Position(points.get(points.size()-1), getPosition());
    }
    
    public final synchronized Position getCurrentFromPoint(int i) {
        return new Position(points.get(i), getPosition());
    }
    public final synchronized Position getPointFromPreviousPoint(int i) {
        return new Position(points.get(i-1), points.get(i));
    }

    public final synchronized boolean hasNoPoints() {
        return points.isEmpty();
    }
    
    public final synchronized int getPointsSize() {
        return points.size();
    }

    public final synchronized void addPoint() {
        points.add(getPosition());
    }

    public final synchronized List<Position> getStartPoints() {
        return startPoints;
    }

    public final synchronized List<Position> getEndPoints() {
        return endPoints;
    }

    public final synchronized void addStartEndPoints() {
        startPoints.add(startPosition);
        endPoints.add(getPosition());
    }
    
    public final synchronized Position getFirstStartEnd() {
        return new Position(startPoints.get(0), endPoints.get(0));
    }
    
    public final synchronized Position getCurrentFromStart() {
        return new Position(startPosition, getPosition());
    }
    
    public final synchronized Position getCurrentFromEnd() {
        return new Position(endPoints.get(endPoints.size()-1), getPosition());
    }
    
    public final synchronized boolean hasNoStartEndPoints() {
        return startPoints.isEmpty();
    }
    
    public final synchronized int getStartEndPointsSize() {
        return startPoints.size();
    }

    public final synchronized boolean isRecording() {
        return recording;
    }

    public final synchronized void setRecording(boolean recording) {
        this.recording = recording;
    }

    public static class Location {

        private Position position;
        private HDOP hDOP;
        private Altitude altitude;
        private Depth depth;

        public Location(Position position, Altitude altitude, HDOP hDOP) {
            set(position, altitude, hDOP);
        }

        public Location(Position position, Altitude altitude, HDOP hDOP, Depth depth) {
            set(position, altitude, hDOP);
            setDepth(depth);
        }
        
        @Override
        @SuppressWarnings({"CloneDeclaresCloneNotSupported", "CloneDoesntCallSuperClone"})
        public Location clone() {
            return new Location(position.clone(), altitude.clone(), hDOP.clone(), depth == null? null:depth.clone());
        }

        public final synchronized Location set(Position position, Altitude altitude, HDOP hDOP) {
            this.position = position;
            this.altitude = altitude;
            this.hDOP = hDOP;
            return this;
        }

        protected final synchronized Location setDepth(Depth depth) {
            this.depth = depth;
            return this;
        }

        public final synchronized Position getPosition() {
            return position;
        }

        public final synchronized HDOP getHDOP() {
            return hDOP;
        }

        public final synchronized Altitude getAltitude() {
            return altitude;
        }

        public final synchronized Depth getDepth() {
            return depth;
        }
    }
}
