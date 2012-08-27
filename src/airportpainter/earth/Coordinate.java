/*
 * Coordinate.java
 *
 * Copyright (C) 2009 Francesco Brisa
 *
 * This file is part of AirportPainter.
 *
 * AirportPainter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AirportPainter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License V.3 for more details.
 *
 * You should have received a copy of the GNU General Public License V.3
 * along with AirportPainter.  If not, see <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * email at:
 * fbrisa@gmail.com  or  fbrisa@yahoo.it
 *
 */
package airportpainter.earth;

import airportpainter.util.LatFormat;
import airportpainter.util.LongFormat;

/**
 * Location
 * 
 * This class represents a place on the surface of the earth ( or at some
 * arbitrary altitude )
 * 
 * The coordinates are held in geocentric form in decimal degrees.
 * 
 * @author trecam01
 */
public class Coordinate {

    // approximate radius of Earth in nautical miles. True radius varies from
    private static final double earth_radius = 3443.566;

    private double _latitude;
    private double _longitude;
    private static final LatFormat latitudeFormat = new LatFormat();

    private static final LongFormat longitudeFormat = new LongFormat();

    /**
	 * Default constructor.
	 *  
	 */
    public Coordinate() {
    }

    /**
	 * Construct a location from a lat, long and elevation
	 * 
	 * @param latitude
	 * @param longitude
	 * @param elevation
	 */
    public Coordinate(final double latitude, final double longitude) {
        _latitude = latitude;
        _longitude = longitude;
    }

    /**
	 * Calculate the initial bearing from this Location to another
	 * 
	 * @author trecam01
	 *  
	 */
    public double bearingTo(final Coordinate l) {

        final double lat1 = Math.toRadians(this._latitude);
        final double lat2 = Math.toRadians(l._latitude);

        final double lon1 = Math.toRadians(this._longitude);
        final double lon2 = Math.toRadians(l._longitude);

        final double dlon = lon1 - lon2;
        final double dlat = lat1 - lat2;

        final double a =
            Math.pow(Math.sin(dlat / 2), 2d)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2d);
        final double d = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double heading =
            Math.acos(
                (Math.sin(lat2) - Math.sin(lat1) * Math.cos(d)) / (Math.sin(d) * Math.cos(lat1)));

        if (Math.sin(lon2 - lon1) < 0) {
            heading = 2 * Math.PI - heading;
        }

        return heading;

    }

    /**
	 * Calculate the initial bearing from this Location to another
	 * 
	 * @author trecam01
	 *  
	 */
    public double bearingToDeg(final Coordinate l) {

        return Math.toDegrees(bearingTo(l));

    }

    /**
	 * Calculate a coordinate at a particular distance and heading from this coordinate 
	 * 
	 * @author trecam01
	 *  
	 */
    public Coordinate coordinateAt(final double distance, final double heading) {

        final double d = distance / earth_radius;

        double lat;
        double lon;
        final double lat1 = Math.toRadians(this._latitude);
        final double lon1 = Math.toRadians(this._longitude);

        final double tc = Math.toRadians(heading);
        double dlon;

        lat = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(tc));

        dlon =
            Math.atan2(
                Math.sin(tc) * Math.sin(d) * Math.cos(lat1),
                Math.cos(d) - (Math.sin(lat1) * Math.sin(lat)));

        // % is Mod
        lon = (lon1 + dlon + Math.PI) % (2 * Math.PI) - Math.PI;

        return new Coordinate(Math.toDegrees(lat), Math.toDegrees(lon));
    }

    /**
	 * Calculate the distance from this Location to another
	 * 
	 * @author trecam01
	 *  
	 */
    public double distanceTo(final Coordinate l) {

        final double lat1 = Math.toRadians(this._latitude);
        final double lat2 = Math.toRadians(l._latitude);

        final double lon1 = Math.toRadians(this._longitude);
        final double lon2 = Math.toRadians(l._longitude);

        final double dlon = lon1 - lon2;
        final double dlat = lat1 - lat2;

        final double a =
            Math.pow(Math.sin(dlat / 2), 2d)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2d);
        final double d = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.abs(earth_radius * d);

    }

    /**
	 * Get the latitude
	 * 
	 * @return
	 */
    public double getLatitude() {
        return _latitude;
    }

    /**
	 * Get the latitude as a string
	 * 
	 * @return
	 */
    public String getLatitudeAsString() {
        return latitudeFormat.format(_latitude);
    }

    /**
	 * Get the longitude.
	 * 
	 * @return
	 */
    public double getLongitude() {
        return _longitude;
    }

    /**
	 * Get the longitude as a string
	 * 
	 * @return
	 */
    public String getLongitudeAsString() {
        return longitudeFormat.format(_longitude);
    }

    /**
	 * Set the latitude
	 * 
	 * @param d
	 */
    public void setLatitude(final double d) {
        _latitude = d;
    }

    /**
	 * Set the longitude
	 * 
	 * @param d
	 */
    public void setLongitude(final double d) {
        _longitude = d;
    }

    /**
	 * Return the coordinate as a string
	 */
    @Override
	public String toString() {
        return getLatitudeAsString() + " " + getLongitudeAsString(); //$NON-NLS-1$
    }
    
    
    
    
    
    
    
    /**
     * Convert cartesian XYZ coordinate into geodetic coordinate with specified geodetic system.
     * @param xyz The xyz coordinate of the given pixel.
     * @param geoSystem The geodetic system.
     */
    public void xyz2geo(final double x,final double y, final double z) {

        double a = 0.0;
        double b = 0.0;
        double earthFlatCoef = 0.0;

        //if (geoSystem == EarthModel.WGS84) {
        if (true) {

            a = WGS84.a;
            b = WGS84.b;
            earthFlatCoef = WGS84.earthFlatCoef;

//        } else if (geoSystem == EarthModel.GRS80) {
//
//            a = GRS80.a;
//            b = GRS80.b;
//            earthFlatCoef = GRS80.earthFlatCoef;
//
//        } else {
//            throw new OperatorException("Incorrect geodetic system");
        }

        final double e2 = 2.0 / earthFlatCoef - 1.0 / (earthFlatCoef * earthFlatCoef);
        final double ep2 = e2 / (1 - e2);

        final double s = Math.sqrt(x*x + y*y);
        final double theta = Math.atan(z*a/(s*b));

        _longitude = (float)(Math.atan(y/x) * RTOD);
        
        if (_longitude < 0.0 && y >= 0.0) {
            _longitude += 180.0;
        } else if (_longitude > 0.0 && y < 0.0) {
            _longitude -= 180.0;
        }

        _latitude = (float)(Math.atan((z + ep2*b*Math.pow(Math.sin(theta), 3)) /
                                       (s - e2*a*Math.pow(Math.cos(theta), 3))) *
                                       RTOD);
        //org.esa.beam.util.math.MathUtils.RTOD
    }
    
    private static double RTOD=57.29577951308232;
    
    public static interface WGS84 {
        public static final double a = 6378137; // m
        public static final double b = 6356752.314245; // m
        public static final double earthFlatCoef = 298.257223563;
    }

    public static interface GRS80 {
        public static final double a = 6378137; // m
        public static final double b = 6356752.314140 ; // m
        public static final double earthFlatCoef = 298.257222101;
    }
}
