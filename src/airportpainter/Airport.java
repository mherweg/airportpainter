/*
 * Airport.java
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
import airportpainter.util.DataLoader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Airport object.
 * Represents an airport as used in Flightgear.
 * Normally loaded from the <b>default.apt.gz</b> file.
 */
public class Airport extends Location implements Serializable, Comparable<Airport> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2687956736719008297L;
	boolean _controlTower;
	List<Runway> _runways = null;
	boolean _showDefaultBuildings;
	List<Taxiway> _taxiways = null;
        private List<Atis> _atises = null;
        private List<ATCFreq> _atcfreqs = null;
	String _usage;
	long _maxRunwayLength = 0;

        private Tower _tower = null;
        private Beacon _beacon = null;

        private List<Location> _windsocks=null;

	public Airport() {
	
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
	public Airport(
		final String id,
		final double latitude,
		final double longitude,
		final double elevation,
		final String usage,
		final boolean tower,
		final boolean defaultBuildings,
		final String name,
		final long maxRunwayLength) {
			
		_id = id;
		_loc = new Coordinate(latitude, longitude);
		_elevation = elevation;
		_usage = usage;
		_controlTower = tower;
		_showDefaultBuildings = defaultBuildings;
		_name = name;
		_maxRunwayLength = maxRunwayLength;
	}
	
	
	/**
	 * Method addRunway.
	 * @param runway
	 */
	public void addRunway(final Runway runway) {
		getRunways().add(runway);
	}

	/**
	 * Method addAtis.
	 * @param atis
	 */
	public void addAtis(final Atis atis) {
		getAtises().add(atis);
	}

	/**
	 * Method addTaxiway.
	 * @param taxiway
	 */
	public void addTaxiway(final Taxiway taxiway) {
		getTaxiways().add(taxiway);
	}
	
	/**
	 * Returns the controlTower.
	 * @return boolean
	 */
	public boolean getControlTower() {
		return _controlTower;
	}

	/**
	 * Returns the runway at index i.
	 * @return Collection
	 */
	public Runway getRunway(final int i) {
		return getRunways().get(i);
	}

	/**
	 * Returns the runways.
	 * @return Collection
	 */
	public int getRunwayCount() {
		return getRunways().size();
	}

	/**
	 * Returns the runways.
	 * @return List of Runways
	 */
	public synchronized List<Runway> getRunways() {
	    if (_runways == null) {
	        loadRunwaysAndTaxiways();
	    }
		return _runways;
	}

	/**
	 * Returns the showDefaultBuildings.
	 * @return boolean
	 */
	public boolean getShowDefaultBuildings() {
		return _showDefaultBuildings;
	}

	/**
	 * Returns the Taxiway at index i.
	 * @return Collection
	 */
	public Taxiway getTaxiway(final int i) {
		return getTaxiways().get(i);
	}

	/**
	 * Returns the Taxiways.
	 * @return Collection
	 */
	public int getTaxiwayCount() {
		return getTaxiways().size();
	}

	/**
	 * Returns the Taxiways.
	 * @return List
	 */
	public List<Taxiway> getTaxiways() {
	    if (_taxiways == null) {
	        loadRunwaysAndTaxiways();
	    }
		return _taxiways;
	}

	/**
	 * Returns the usage.
	 * @return int
	 */
	public String getUsage() {
		return _usage;
	}

	/**
	 * Sets the controlTower.
	 * @param controlTower The controlTower to set
	 */
	public void setControlTower(final boolean controlTower) {
		_controlTower = controlTower;
	}

	/**
	 * Sets the runways.
	 * @param runways The runways to set
	 */
	public void setRunways(final List<Runway> runways) {
		_runways = runways;
	}

	/**
	 * Sets the showDefaultBuildings.
	 * @param showDefaultBuildings The showDefaultBuildings to set
	 */
	public void setShowDefaultBuildings(final boolean showDefaultBuildings) {
		_showDefaultBuildings = showDefaultBuildings;
	}

	/**
	 * Sets the taxiways.
	 * @param taxiways The taxiways to set
	 */
	public void setTaxiways(final List<Taxiway> taxiways) {
		_taxiways = taxiways;
	}

	/**
	 * Sets the usage.
	 * @param usage The usage to set
	 */
	public void setUsage(final String usage) {
		_usage = usage;
	}

	private void loadRunwaysAndTaxiways() {
	    //final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	    //final Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		//shell.setCursor(waitCursor);
	    final DataLoader loader = new DataLoader();
	    _runways = new ArrayList<Runway>();
	    _taxiways = new ArrayList<Taxiway>();
	    loader.loadRunways(this);
		//shell.setCursor(null);
		//waitCursor.dispose();
	}


	public long get_maxRunwayLength() {
		return _maxRunwayLength;
	}


	public void set_maxRunwayLength(final long runwayLength) {
		_maxRunwayLength = runwayLength;
	}

	/**
	 * This compare method sorts airports from biggest to smallest.
	 * This helps later when drawing and especially for hover on map.
	 */
	public int compareTo(final Airport o) {
		final Airport a = (Airport) o;
		if (a.get_maxRunwayLength() > get_maxRunwayLength()) {
			return 1;
		}
		if (a.get_maxRunwayLength() < get_maxRunwayLength()) {
			return -1;
		}
		return 0;
	}

    /**
     * @return the _atises
     */
    public List<Atis> getAtises() {
        if (_atises == null) {
            _atises=new ArrayList<Atis>();
        }
        return _atises;
    }

    /**
     * @param atises the _atises to set
     */
    public void setAtises(List<Atis> atises) {
        this._atises = atises;
    }

    /**
     * @return the _towers
     */
    public List<ATCFreq> getTowers() {
        if (_atcfreqs == null) {
            _atcfreqs=new ArrayList<ATCFreq>();
        }
        return _atcfreqs;
    }

    /**
     * @return the _freqs
     */
    public List<ATCFreq> getATCFreqs(final String type) {
        List<ATCFreq> toReturn=new ArrayList<ATCFreq>();

        for (ATCFreq atcFreq:getTowers()) {
            if (atcFreq.getType().equals(type)) {
                toReturn.add(atcFreq);
            }
        }

        return toReturn;
    }

    /**
     * @param towers the _towers to set
     */
    public void setATCFreqs(List<ATCFreq> towers) {
        this._atcfreqs = towers;
    }

	/**
	 * Method addTaxiway.
	 * @param taxiway
	 */
	public void addATCFreq(final ATCFreq tower) {
		getTowers().add(tower);
	}

    @Override
        public String toString() {
            return getId()+" "+getName()+" Runways:"+getRunwayCount();
        }

    /**
     * @return the _tower
     */
    public Tower getTower() {
        return _tower;
    }

    /**
     * @param tower the _tower to set
     */
    public void setTower(Tower tower) {
        this._tower = tower;
    }

    public boolean hasTower() {
        return _tower!=null;
    }



    /**
     * @return the _windsocks
     */
    public List<Location> getWindsocks() {
        if (_windsocks==null) {
            _windsocks=new ArrayList<Location>(1);
        }
        return _windsocks;
    }

    /**
     * @param windsocks the _windsocks to set
     */
    public void setWindsocks(List<Location> windsocks) {
        this._windsocks = windsocks;
    }


    /**
     * @param windsocks the _windsocks to add
     */
    public void addWindsock(Location windsock) {
        getWindsocks().add(windsock);
    }

    /**
     * @return the _beacon
     */
    public Beacon getBeacon() {
        return _beacon;
    }

    /**
     * @param beacon the _beacon to set
     */
    public void setBeacon(Beacon beacon) {
        this._beacon = beacon;
    }


    public boolean hasBeacon() {
        return _beacon!=null;
    }

    public String getLatitudeString1(double value,String pos,String neg,int format,boolean latOLong) {
        String temp="";

        double absLat=value;

        if (latOLong) {
            if ( getLong() <0 ) {
                absLat=-absLat;
            }
        } else {
            if (getLat() <0 ) {
                absLat=-absLat;
            }

        }


        int lat=(int) absLat;

        String latString=(new Integer(lat).toString());
        while (latString.length()<format) {
            latString="0"+latString;
        }

        int dec_=(int) ((int) ((absLat-lat)*100));
        double dec__=((double) dec_)/100;
        int dec=(int) dec_*60/100;


        int sec_=(int) ((absLat-lat-dec__)*10000);
        double sec__=((double) sec_)/100;
        int sec=(int) sec_*60/100;

        int prsec=(int) (sec__*10);

        temp= ( getLat() >0?pos:neg)+latString+" "+(new Integer(dec).toString())+"."+(new Integer(prsec).toString());

        return temp;

    }

}
