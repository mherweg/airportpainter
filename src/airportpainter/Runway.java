/*
 * Runway.java
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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Runway object.
 * Represents a runway as used in Flightgear.
 * Normally loaded from the <b>default.apt.gz</b> file.
 */
public class Runway implements Serializable {

        

	/**
	 * 
	 */
	private static final long serialVersionUID = 2996327271997564470L;
	private double _ilsOppositeFreq;
	private double _ilsFreq;
	protected String _number;
	protected double _lat;
	protected double _long;
	protected double _heading;
	protected int _length;
	protected int _width;
	protected boolean _centreLineLights;
	protected String _surface;
	protected String _markings;
	protected String _edgeLights;
        private int _displacement;
        private int _displacementOpposite;

/*
01  	Asphalt.
02 	Concrete.
03 	Turf/grass.
04 	Dirt.
05 	Gravel.
06 	Asphalt helipad (big “H” in the middle).
07 	Concrete helipad (big “H” in the middle).
08 	Turf helipad (big “H” in the middle).
09 	Dirt helipad (big “H” in the middle).
10 	Asphalt taxiway with yellow hold line across long axis (not available from WorldMaker).
11 	Concrete taxiway with yellow hold line across long axis (not available from WorldMaker).
12 	Dry lakebed runway (eg. at KEDW Edwards AFB).
13 	Water runways (marked with bobbing buoys) for seaplane/floatplane bases (available in X-Plane 7.0 and later).
*/
        public static String SURFACE_ASPHALT="1";
        public static String SURFACE_CONCRETE="2";
        public static String SURFACE_TURF="3";
        public static String SURFACE_DIRT="4";
        public static String SURFACE_GRAVEL="5";
        public static String SURFACE_WATER="13";
        public static String SURFACE_OTHER="0";



	/**
	 * @return
	 */
	public double getIlsFreq() {
		return _ilsFreq;
	}

	/**
	 * @return
	 */
	public double getIlsOppositeFreq() {
		return _ilsOppositeFreq;
	}

	public Runway() {

	}

	/**
	 * Constructor Runway.
	 * @param number
	 * @param lat
	 * @param long
	 * @param length
	 * @param width
	 * @param heading
	 * @param centreLights
	 * @param surface
	 * @param markings
	 * @param edgeLights
	 */
	public Runway(
		final String number,
		final double latitude,
		final double longitude,
		final int length,
		final int width,
		final double heading,
		final boolean centreLights,
		final String surface,
		final String edgeLights,
		final String markings,
                final String displacement
        ) {

		_number = number;
		_lat = latitude;
		_long = longitude;
		_length = length;
		_heading = heading;
		_width = width;
		_centreLineLights = centreLights;
		_surface = surface;
                if (_surface.startsWith("0") && _surface.length()>1) {
                    _surface=_surface.substring(1);
                }
		_markings = markings;
		_edgeLights = edgeLights;
                
                int idd=displacement.indexOf(".");
                if (idd>0) {
                    _displacement=new Integer(displacement.substring(0,idd)).intValue();
                    _displacementOpposite=new Integer(displacement.substring(idd+1)).intValue();
                }
	}

	/**
	 * Get the centre lights flag.
	 * A=Asphalt, C=Concrete, T=Turf, D=Dirt, G=Gravel, W=Water, X=Other
	 * @return boolean
	 */
	public boolean getCentreLineLights() {
		return _centreLineLights;
	}

	/**
	 * Get the edge lights flag.
	 * N=None, H=High intensity, M=Medium, L=Low, B=Blue taxiway
	 * @return
	 */
	public String getEdgeLights() {
		return _edgeLights;
	}

	/**
	 * Get the heading.
	 * @return
	 */
	public double getHeading() {
		return _heading;
	}

	/**
	 * Get the lat.
	 * @return double
	 */
	public double getLat() {
		return _lat;
	}

	/**
	 * Get the length.
	 * @return int
	 */
	public int getLength() {
		return _length;
	}

	/**
	 * Get the longitude.
	 * @return double
	 */
	public double getLong() {
		return _long;
	}

	/**
	 * Get the markings flag
	 * V=Visual, P=Precision, R=Non-Precision, B=Buoys - water
	 * @return String
	 */
	public String getMarkings() {
		return _markings;
	}

	/**
	 * Get the number.
	 * @return String
	 */
	public String getNumber() {
		return _number;
	}

	/**
	 * Get the the pair of opposite end numbers for this runway.
	 * @return String
	 */
	public String getNumberPair() {
		return _number + "/" + getOppositeNumber(); //$NON-NLS-1$
	}

	/**
	 * Get the the pair of opposite end numbers for this runway.
	 * @return String
	 */
	public String getOppositeNumber() {
		if (_number.equals("N")) { //$NON-NLS-1$
			return "S"; //$NON-NLS-1$
		}
		if (_number.equals("S")) { //$NON-NLS-1$
			return "N"; //$NON-NLS-1$
		}
		if (_number.equals("E")) { //$NON-NLS-1$
			return "W"; //$NON-NLS-1$
		}
		if (_number.equals("W")) { //$NON-NLS-1$
			return "E"; //$NON-NLS-1$
		}

		// See if an R, C or L
		String headingPart = ""; //$NON-NLS-1$
		String extraPart = ""; //$NON-NLS-1$
		if (_number.endsWith("R")) { //$NON-NLS-1$
			headingPart = _number.substring(0, _number.indexOf("R")); //$NON-NLS-1$
			extraPart = "L"; //$NON-NLS-1$
		} else if (_number.endsWith("L")) { //$NON-NLS-1$
			headingPart = _number.substring(0, _number.indexOf("L")); //$NON-NLS-1$
			extraPart = "R"; //$NON-NLS-1$
		} else if (_number.endsWith("C")) { //$NON-NLS-1$
			headingPart = _number.substring(0, _number.indexOf("C")); //$NON-NLS-1$
			extraPart = "C"; //$NON-NLS-1$
		} else {
			headingPart = _number;
			extraPart = ""; //$NON-NLS-1$
		}
		int x;
		try {
			x = Integer.parseInt(headingPart);
		} catch (final NumberFormatException e) {
			x = 0;
		}
		final NumberFormat nf = new DecimalFormat("00"); //$NON-NLS-1$
		if (x < 18) {
			return nf.format(x + 18) + extraPart; 
		} else {
			return nf.format(x - 18) + extraPart; 
		}
	}

	/**
	 * Get the surface.
	 * A=Asphalt, C=Concrete, T=Turf, D=Dirt, G=Gravel, W=Water, X=Other
	 * @return String
	 */
	public String getSurface() {
		return _surface;
	}

	/**
	 * Get the width.
	 * @return int
	 */
	public int getWidth() {
		return _width;
	}

	/**
	 * Set the centre lights flag.
	 * A=Asphalt, C=Concrete, T=Turf, D=Dirt, G=Gravel, W=Water, X=Other
	 * @param centreLights
	 */
	public void setCentreLights(final boolean centreLineLights) {
		_centreLineLights = centreLineLights;
	}

	/**
	 * Set the edge lights flag.
	 * N=None, H=High intensity, M=Medium, L=Low, B=Blue taxiway
	 * @param edgeLights
	 */
	public void setEdgeLights(final String edgeLights) {
		_edgeLights = edgeLights;
	}

	/**
	 * @param heading
	 */
	public void setHeading(final double heading) {
		_heading = heading;
	}

	/**
	 * Set the lat.
	 * @param lat The lat to set
	 */
	public void setLat(final double lat) {
		_lat = lat;
	}

	/**
	 * Set the length.
	 * @param length The length to set
	 */
	public void setLength(final int length) {
		_length = length;
	}

	/**
	 * Set the longitude.
	 * @param l The l to set
	 */
	public void setLong(final double l) {
		_long = l;
	}

	/**
	 * Set the markings flag
	 * V=Visual, P=Precision, R=Non-Precision, B=Buoys - water
	 * @param markings
	 */
	public void setMarkings(final String markings) {
		_markings = markings;
	}

	/**
	 * Set the number.
	 * @param number The number to set
	 */
	public void setNumber(final String number) {
		_number = number;
	}

	/**
	 * Set the surface.
	 * A=Asphalt, C=Concrete, T=Turf, D=Dirt, G=Gravel, W=Water, X=Other
	 * @param surface The surface to set
	 */
	public void setSurface(final String surface) {
		_surface = surface;
	}

	/**
	 * Set the width.
	 * @param width The width to set
	 */
	public void setWidth(final int width) {
		_width = width;
	}

	/**
	 * @param freq
	 */
	public void setIlsFreq(final double ilsFreq) {
		_ilsFreq = ilsFreq;
	}

	/**
	 * @param freq
	 */
	public void setOppositeIlsFreq(final double ilsFreq) {
		_ilsOppositeFreq = ilsFreq;
	}

    /**
     * @return the _displacement
     */
    public int getDisplacement() {
        return _displacement;
    }

    /**
     * @param displacement the _displacement to set
     */
    public void setDisplacement(int displacement) {
        this._displacement = displacement;
    }

    /**
     * @return the _displacementOpposite
     */
    public int getDisplacementOpposite() {
        return _displacementOpposite;
    }

    /**
     * @param displacementOpposite the _displacementOpposite to set
     */
    public void setDisplacementOpposite(int displacementOpposite) {
        this._displacementOpposite = displacementOpposite;
    }



    private boolean saveLightCheck(final int offset,final boolean side, final String checkValue) {
        if (offset<_edgeLights.length()) {
            if (! side) {
                return _edgeLights.substring(offset,offset+1).equals(checkValue);
            }

            return _edgeLights.substring(3+offset,3+offset+1).equals(checkValue);    
            
            
        }
        
        return false;
    }


    //Visual approach path indicator codes
    public boolean hasVasi(boolean otherSide) {
        return saveLightCheck(0,otherSide,"2");
    }

    //Precision Approach Path Indicator
    public boolean hasPapi(boolean otherSide) {
        return saveLightCheck(0,otherSide,"3");
    }

    //Space Shuttle Landing PAPI
    public boolean hasSSLP(boolean otherSide) {
        return saveLightCheck(0,otherSide,"4");
    }



    //Simplified short approach light system
    public boolean hasSSALS(boolean otherSide) {
        return saveLightCheck(2,otherSide,"2");
    }

    //Short approach light system with sequenced flashing lights
    public boolean hasSALSF(boolean otherSide) {
        return saveLightCheck(2,otherSide,"3");
    }

    //Approach light system with sequenced flashing lights
    public boolean hasALSF_I(boolean otherSide) {
        return saveLightCheck(2,otherSide,"4");
    }

    //Approach light system with sequenced flashing lights and red side bar lights the last 1000’
    public boolean hasALSF_II(boolean otherSide) {
        return saveLightCheck(2,otherSide,"5");
    }

    //Omni-directional approach light system
    public boolean hasODALS(boolean otherSide) {
        return saveLightCheck(2,otherSide,"6");
    }

    //Omni-directional approach light system
    public boolean hasCalvert1(boolean otherSide) {
        return saveLightCheck(2,otherSide,"7");
    }

    //Omni-directional approach light system
    public boolean hasCalvert2(boolean otherSide) {
        return saveLightCheck(2,otherSide,"8");
    }


    public String getSurfaceName() {

        if (getSurface().equals(SURFACE_ASPHALT)) {
            return "Asphalt";
        }
        if (getSurface().equals(SURFACE_CONCRETE)) {
            return "Concrete";
        }
        if (getSurface().equals(SURFACE_TURF)) {
            return "Turf";
        }
        if (getSurface().equals(SURFACE_DIRT)) {
            return "Dirt";
        }
        if (getSurface().equals(SURFACE_GRAVEL)) {
            return "Gravel";
        }
        if (getSurface().equals(SURFACE_WATER)) {
            return "Water";
        }
        if (getSurface().equals(SURFACE_OTHER)) {
            return "Other";
        }

        return "-";
    }
}
