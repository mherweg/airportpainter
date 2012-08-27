/*
 * Earth.java
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

import airportpainter.Airport;
import airportpainter.Atis;
import airportpainter.util.NavAid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import au.com.kelpie.fgfp.util.DataLoader;

/**
 * @
 */
public class Earth {

	private static List<Airport> _airports = new ArrayList<Airport>(5000);
	private static List<NavAid> _navAids = new ArrayList<NavAid>(2000);
        private static List<Atis> _atises = new ArrayList<Atis>(2000);
	//private static List<Fix> _fixes = null;

	/**
	 * Get the Airports.
	 * @return Airport list
	 */
	public static List<Airport> getAirports() {
		return _airports;
	}

	/**
	 * Set the Airports.
	 * @param _airports List of Airports
	 */
	public static void setAirports(final List<Airport> airports) {
		Earth._airports = airports;
		Collections.sort(_airports);
	}

	/**
	 * Get the Navaids.
	 * @return List of Nav Aids
	 */
	public static List<NavAid> getNavAids() {
		return _navAids;
	}

	/**
	 * Set the Navaids.
	 * @param List of Nav Aids
	 */
	public static void setNavAids(final List<NavAid> aids) {
		_navAids = aids;
	}

    /**
     * @return the _atises
     */
    public static List<Atis> getAtises() {
        return _atises;
    }

    /**
     * @param aAtises the _atises to set
     */
    public static void setAtises(List<Atis> aAtises) {
        _atises = aAtises;
    }

	/**
	 * Private constructor to prevent instantsiation
	 */
	protected Earth() {
		super();
	}

	/**
	 * Get Fixes
	 * @return List of Fixes
	 */
	/*public static synchronized List<Fix> getFixes() {
		if (_fixes == null) {
		    _fixes = new ArrayList<Fix>(50000);		    
        	final DataLoader loader = new DataLoader();
        	loader.loadFixes(null);
		}
		return _fixes;
	}*/

	/**
	 * Get Fixes
	 * @param fixes The fixes to set.
	 */
	/*public static void setFixes(final List<Fix> fixes) {
		_fixes = fixes;
	}*/


        public static Airport getAirportById(String name) {
            for (Airport a:_airports) {
                if (name.equals(a.getId())) {
                    return a;
                }
            }

            return null;
        }
}
