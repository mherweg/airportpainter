/*
 * Location.java
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
package airportpainter;

import airportpainter.earth.Coordinate;

/**
 * A Location is the parent of all real locations, such as Airports and navaids.
 */
public class Location implements Cloneable{

	protected double _elevation;
	protected String _id;
	protected Coordinate _loc;
	protected String _name;

	/**
	 * Returns the elevation.
	 * @return double
	 */
	public double getElevation() {
		return _elevation;
	}

	/**
	 * Returns the id.
	 * @return String
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Returns the latitude.
	 * @return double
	 */
	public double getLat() {
		return _loc.getLatitude();
	}

	/**
	 * Returns the latitude as a String.
	 * @return
	 */
	public String getLatAsString() {
		return _loc.getLatitudeAsString();
	}

	/**
	 * Returns the longitude.
	 * @return double
	 */
	public double getLong() {
		return _loc.getLongitude();
	}

	/**
	 * Returns the longitude as a String.
	 * @return
	 */
	public String getLongAsString() {
		return _loc.getLongitudeAsString();
	}

	/**
	 * Get the location
	 * @return The location
	 */
	public Coordinate getLoc() {
		return _loc;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Sets the elevation.
	 * @param elevation The elevation to set
	 */
	public void setElevation(final double elevation) {
		_elevation = elevation;
	}

	/**
	 * Sets the id.
	 * @param id The id to set
	 */
	public void setId(final String id) {
		_id = id;
	}

	/**
	 * Sets the latitude.
	 * @param lat The lat to set
	 */
	public void setLat(final double lat) {
		if (_loc == null) {
                    _loc=new Coordinate();
		}
                _loc.setLatitude(lat);
	}

	/**
	 * Sets the longitude.
	 * @param l The l to set
	 */
	public void setLong(final double l) {
		if (_loc == null) {
                    _loc=new Coordinate();
		}
                _loc.setLongitude(l);
	}

	/**
	 * @param location
	 */
	public void setLoc(final Coordinate location) {
		_loc = location;
	}


	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(final String name) {
		_name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone(){
		try {
			return super.clone();
		} catch (final CloneNotSupportedException e) {
			// Can't happen, we are cloneable
			return null;
		}
	}

	@Override
        public String toString() {
            return _id+" "+_name+" "+_loc.toString();
        }

}
