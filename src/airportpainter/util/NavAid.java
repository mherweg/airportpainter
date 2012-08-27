/*
 * NavAid.java
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
package airportpainter.util;

import airportpainter.Location;
import airportpainter.earth.Coordinate;
import java.io.Serializable;


/**
 * Represents a navaid as used in Flightgear.
 * Normally loaded from the <b>default.nav.gz</b> file.
 */
public class NavAid extends Location implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8440800470576343671L;
	public static final int DME = 0;
	public static final int NDB = 2;
	public static final int VOR = 3;
	public static final int TACAN = 13;

	protected boolean _coLocatedDME;

	protected double _freq;
	protected String _magVariation;
	protected double _range;
	protected int _type;

	public NavAid() {

	}

	/**
	 * Constructor Airport.
	 * @param id
	 * @param latitude
	 * @param longitude
	 * @param usage
	 * @param tower
	 * @param defaultBuildings
	 * @param string
	 */
	public NavAid(
		final String id,
		final double latitude,
		final double longitude,
		final double elevation,
		final double freq,
		final double range,
		final String magVariation,
		final String name) {

		_id = id;
		_loc = new Coordinate(latitude, longitude);
		_elevation = elevation;
		_freq = freq;
		_range = range;
		_magVariation = magVariation;
		_name = name;
	}

	/**
	 * Returns the controlTower.
	 * @return boolean
	 */
	public boolean getCoLocatedDME() {
		return _coLocatedDME;
	}

	/**
	 * Get the frequency.
	 * @return
	 */
	public double getFreq() {
		return _freq;
	}

	/**
	 * Get the magnetic variation at the navaids location.
	 * @return
	 */
	public String getMagVariation() {
		return _magVariation;
	}

	/**
	 * Get the range.
	 * This is the distance in Nautical Miles from which the navaid can be received
	 * @return double
	 */
	public double getRange() {
		return _range;
	}

	/**
	 * Get the type of NavAid.
	 * VOR or NDB
	 * @return
	 */
	public int getType() {
		return _type;
	}

	/**
	 * Get the common name of the type of NavAid. 
	 * @return
	 */
	public String getTypeName() {
		switch (_type) {
			case VOR :
				return "VOR"; //$NON-NLS-1$
			case NDB :
				return "NDB"; //$NON-NLS-1$
			case DME :
				return "DME"; //$NON-NLS-1$
			case TACAN :
				return "TACAN"; //$NON-NLS-1$
			default :
				return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * Set the Co Located DME flag
	 * @param b 
	 */
	public void setCoLocatedDME(final boolean coLocatedDME) {
		_coLocatedDME = coLocatedDME;
	}

	/**
	 * Set the frequency
	 * @param freq
	 */
	public void setFreq(final double freq) {
		_freq = freq;
	}

	/**
	 * Set the magnetic variation.
	 * @param magVariation
	 */
	public void setMagVariation(final String magVariation) {
		_magVariation = magVariation;
	}

	/**
	 * Set the range.
	 * This is the distance in Nautical Miles from which the navaid can be received
	 * @param range
	 */
	public void setRange(final double range) {
		_range = range;
	}

	/**
	 * Set the navaid type.
	 * @param _type
	 */
	public void setType(final int type) {
		switch (type) {
			case VOR :
				break;
			case NDB :
				break;
			case DME :
				break;
			case TACAN :
				break;
			default :
				throw new IllegalArgumentException();
		}
		_type = type;
	}

    @Override
        public String toString() {
            return getId()+" "+getTypeName()+" "+getName()+" "+getFreq()+" "+getLatAsString()+" "+getLongAsString();
        }

}
