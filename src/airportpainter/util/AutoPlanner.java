/*
 * AutoPlanner.java
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

import airportpainter.Airport;
import airportpainter.earth.Coordinate;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatic plan creator.
 * Creates a plan between a pair of waypoints.
 * Package: au.com.kelpie.fgfp
 * Class    : AutoPlanner
 * 
 * Created : 20/05/2003
 * @author trecam01
 *
 */
public class AutoPlanner {

    protected boolean _addWayPoints;
	protected double _maxDeviation; 		
	protected double _minLegDistance;
	protected List<NavAid> _navAids = null;
        private List<Airport> _airports = null;
	protected double _normalLegDistance;
	protected boolean _useRadioBeacons;
	protected boolean _vorOnly;
	protected boolean _vorPrefered;
	protected int _addWayPointInterval;
	protected boolean _addWayPointBias;
	
	/**
	 * Default constructor
	 *
	 */
	public AutoPlanner() {
		super();
	}

	/**
	 * Create a planner that will use the given list of 
	 * navaids to build its plans.
	 * @param navAids
	 */
	public AutoPlanner(final List<NavAid> navAids) {
		super();
		_navAids = navAids;
	}
	
	/**
	 * Adds the navaids to a plan between to waypoints.
	 * This method finds the navaid nearest the centre of the
	 * great circle between the two waypoints and invokes itself
	 * recursively on each half of the plan, i.e. from waypoint --> mid point
	 * and mid point --> to waypoint. 
	 * @param from
	 * @param to
	 * @param plan
	 */
	/*private void addNavAidsToPlan(final Waypoint from, final Waypoint to, final List<Waypoint> plan) {
		
		final double distance = from.getLoc().distanceTo(to.getLoc());
		
		if (distance < _normalLegDistance) {
			return;
		}
		final double heading = Math.toDegrees(from.getLoc().bearingTo(to.getLoc()));
		final Coordinate midpoint = from.getLoc().coordinateAt(distance/2, heading);
		
		final NavAid midNavAid = getNavaidNearestMidpoint(from.getLoc(), to.getLoc(), midpoint);
		
		if (midNavAid != null) {
			final Waypoint wp = new Waypoint(midNavAid);
			// Get navaids to the midpoint
			addNavAidsToPlan(from, wp, plan);
			plan.add(wp);
			// Get navaids from the midpoint
			addNavAidsToPlan(wp, to, plan);
		}
		
	}*/
	
	/**
	 * Add additional waypoints to pad out the plan
	 */
	/*private void addWaypoints (final Waypoint from, final Waypoint to, final List<Waypoint> plan) {
		
	    // Clone the plan as an original reference
	    final List<Waypoint> oldPlan = new ArrayList<Waypoint>(plan);
	    
		// walk the legs and find those that are over the wished for interval 
		double maxLegInterval;
		if (_addWayPointBias) {
		    maxLegInterval = _addWayPointInterval * .75;
		} else {
		    maxLegInterval = _addWayPointInterval * 1.25;
		}
		Waypoint prevWp = from;
		for (int i = 0; i < oldPlan.size(); i++) {
			final Waypoint wp = oldPlan.get(i);
			final double legLength = prevWp.getLoc().distanceTo(wp.getLoc());
			if (legLength >= maxLegInterval) {
				addWaypointsToLeg(prevWp, wp, plan);	
			} 
			prevWp= wp;
		}
		// Try for the final leg
		final double legLength = prevWp.getLoc().distanceTo(to.getLoc());
		if (legLength >= maxLegInterval) {
			addWaypointsToLeg(prevWp, to, plan);	
		} 
				
	}*/

	/**
	 * Add additional waypoints to pad out the plan for this leg
     * @param prevWp
     * @param wp
     * @param plan
     */
    /*private void addWaypointsToLeg(final Waypoint from, final Waypoint to, final List<Waypoint> plan) {
        double additionalPoints;
        final double legLength = from.getLoc().distanceTo(to.getLoc());
        
        additionalPoints = legLength / _addWayPointInterval;
        if (_addWayPointBias && (additionalPoints - Math.floor(additionalPoints) > .2) ) {
            additionalPoints = Math.ceil(additionalPoints);
        } else {
            additionalPoints = Math.floor(additionalPoints);
        }
        final double interval = legLength / additionalPoints;
        
        Waypoint prevWp = from;
		final int index = plan.indexOf(prevWp) + 1;  
        
		for (int i = 0; i < ((int)additionalPoints) - 1; i++) {
            
            final double heading = prevWp.getLoc().bearingToDeg(to.getLoc());
    		final Coordinate xLoc = prevWp.getLoc().coordinateAt(interval, heading);
    		final Waypoint x = new Waypoint();
    		x.setType(Waypoint.TYPE_GPS);
    		x.setName(Messages.getString("AutoPlanner.5")); //$NON-NLS-1$
    		x.setLoc(xLoc);
   		    plan.add(index + i, x);
   		    prevWp = x;
        }
    }*/

    /**
	 * Find all the navaids near a given point.
	 * @param fromCoord
	 * @param heading
	 * @param normalLegDistance
	 * @return
	 */
	private List<NavAid> getNavAidsNear(final Coordinate point, final double range) {
		final List<NavAid> nearAids = new ArrayList<NavAid>();
		// we do a little optimisation here rather than calculating
		// all distances accurately we make a quick rough calculation to exclude many coordinates
		final double roughLatSep = range / 60;
		final double roughLonSep = range / ( 60 * Math.cos(Math.toRadians(point.getLatitude())));
		
		for (final NavAid loc : _navAids) {
			if (_vorOnly && (loc.getType() != NavAid.VOR)) {
				continue;
			}
                        if (loc.getLoc()==null) {
                            int a=0;
                        }

			// See if near enough
			if (Math.abs(point.getLatitude() - loc.getLoc().getLatitude()) > roughLatSep) {
				continue;
			}
			if (Math.abs(point.getLongitude() - loc.getLoc().getLongitude()) > roughLonSep) {
				continue;
			}
			final double distance = point.distanceTo(loc.getLoc());
			if (distance <= range) {
				nearAids.add(loc);
			}
		}

		return nearAids;
	}

	/**
	 * Find the navaid nearest the midpoint of a plan leg.
	 * @param fromCoord
	 * @param _normalLegDistance
	 * @return
	 */
	private NavAid getNavaidNearestMidpoint(final Coordinate from, final Coordinate to, final Coordinate midpoint) {
		List<NavAid> nearAids;
		final double legDistance = from.distanceTo(to);
		final double headingFrom = from.bearingToDeg(midpoint);
		final double headingTo = midpoint.bearingToDeg(to);
		
		final double range = legDistance / 2;// - _minLegDistance;
		
		nearAids = getNavAidsNear(midpoint, range);
		// Scan through the navaids and find the one closest to the midpoint
		// Ensure it is within the max course deviation allowed
		NavAid bestLoc = null; 
		NavAid bestNDB = null; 
		double nearest = 100000;
		double nearestNDB = 100000;
		for (final NavAid navAid : nearAids) {
			final double deviationTo =  Math.abs(from.bearingToDeg(navAid.getLoc()) - headingFrom);
			final double deviationFrom =  Math.abs(navAid.getLoc().bearingToDeg(to) - headingTo);
			if ((deviationTo > _maxDeviation) || (deviationFrom > _maxDeviation)) {
				continue;
			}
			if (_vorPrefered && (navAid.getType() != NavAid.VOR)) {
				if ((midpoint.distanceTo(navAid.getLoc()) < nearestNDB) &&
				    (from.distanceTo(navAid.getLoc()) > _minLegDistance) &&
					(to.distanceTo(navAid.getLoc()) > _minLegDistance)) 
				{
					bestNDB = navAid;
					nearestNDB = midpoint.distanceTo(navAid.getLoc());
				}
			} else {
				if ((midpoint.distanceTo(navAid.getLoc()) < nearest) &&
				    (from.distanceTo(navAid.getLoc()) > _minLegDistance) &&
				    (to.distanceTo(navAid.getLoc()) > _minLegDistance)) 
				{
					bestLoc = navAid;
					nearest = midpoint.distanceTo(navAid.getLoc());
				}
			}
			
		}
		
		if ((bestLoc == null) && (bestNDB != null)) {
			bestLoc = bestNDB;
		}
		
		return bestLoc;
	}

	/**
	 * Find the navaid nearest a Coordinate.
	 * @param coord
	 * @param range
	 * @return NavAid
	 */
	public NavAid getNavaidNearest(final Coordinate coord, final double maxRange) {
		List<NavAid> nearAids;
		
		nearAids = getNavAidsNear(coord, maxRange);
		// Scan through the navaids and find the one closest to the midpoint
		// Ensure it is within the max course deviation allowed
		NavAid bestLoc = null; 
		double nearest = 100000;
		for (final NavAid navAid : nearAids) {
			if (coord.distanceTo(navAid.getLoc()) < nearest) {
				bestLoc = navAid;
				nearest = coord.distanceTo(navAid.getLoc());
			}
		}

		return bestLoc;
	}

	/**
	 * Generate a plan between two waypoints.
	 * Adds the navaids to a plan between to waypoints.
	 * This method finds the navaid nearest the centre of the
	 * great circle between the two waypoints and invokes itself
	 * recursively on each half of the plan, i.e. from waypoint --> mid point
	 * and mid point --> to waypoint. 
	 * Optionally it will add Top Of Climb and Beginning of Descent Waypoints and
	 * Intermediate (GPS) waypoints where no navaids are available.
	 * 
	 * @param from
	 * @param to
	 * @return List of new waypoints.
	 */	
	/*public List<Waypoint> makePlan(final Waypoint from, final Waypoint to) {
		_maxDeviation = _prefs.getInt(IPreferenceConstants.MAX_DEVIATION);
		_normalLegDistance = _prefs.getInt(IPreferenceConstants.MAX_LEG_LENGTH);
		_minLegDistance = _prefs.getInt(IPreferenceConstants.MIN_LEG_LENGTH);
		_useRadioBeacons = _prefs.getBoolean(IPreferenceConstants.USE_RADIO_BEACONS);
		_vorOnly = _prefs.getBoolean(IPreferenceConstants.VOR_ONLY);
		_vorPrefered = _prefs.getBoolean(IPreferenceConstants.VOR_PREFERED);
		_addWayPoints = _prefs.getBoolean(IPreferenceConstants.ADD_WAYPOINTS);
		_addWayPointInterval = _prefs.getInt(IPreferenceConstants.MAX_LEG_LENGTH);
		_addWayPointBias = _prefs.getBoolean(IPreferenceConstants.ADD_WAYPOINT_BIAS);
		
		final List<Waypoint> plan = new Vector<Waypoint>();
		if (_useRadioBeacons) {
			if (_navAids == null) {
				throw new IllegalStateException(Messages.getString("AutoPlanner.4"));  //$NON-NLS-1$
			}
			// We use an iterative process of finding the navaid 
			// nearest to the mid point and then do the same recursively
			// while the leg length is greater than MAX_LEG_LENGTH
			addNavAidsToPlan(from, to, plan);
		}
				
		if (_addWayPoints) {
			addWaypoints(from, to, plan);
		}
		
		return plan;
	}*/

	/**
	 * Sets the available navaids to use when building a plan.
	 * @param list
	 */
	public void setNavAids(final List<NavAid> list) {
		_navAids = list;
	}




        /**
	 * Find all the navaids near a given point.
	 * @param fromCoord
	 * @param heading
	 * @param normalLegDistance
	 * @return
	 */
	public List<Airport> getAirportNear(final Coordinate point, final double range) {
		final List<Airport> nearAirports = new ArrayList<Airport>();
		// we do a little optimisation here rather than calculating
		// all distances accurately we make a quick rough calculation to exclude many coordinates
		final double roughLatSep = range / 60;
		final double roughLonSep = range / ( 60 * Math.cos(Math.toRadians(point.getLatitude())));

		for (final Airport loc : _airports) {
			// See if near enough
			if (Math.abs(point.getLatitude() - loc.getLoc().getLatitude()) > roughLatSep) {
				continue;
			}
			if (Math.abs(point.getLongitude() - loc.getLoc().getLongitude()) > roughLonSep) {
				continue;
			}
			final double distance = point.distanceTo(loc.getLoc());
			if (distance <= range) {
				nearAirports.add(loc);
			}
		}

		return nearAirports;
	}

    /**
     * @return the _airports
     */
    public List<Airport> getAirports() {
        return _airports;
    }

    /**
     * @param airports the _airports to set
     */
    public void setAirports(List<Airport> airports) {
        this._airports = airports;
    }



     /**
	 * Find all the navaids near a given point.
	 * @param fromCoord
	 * @param heading
	 * @param normalLegDistance
	 * @return
	 */
	public List<NavAid> getNavAidsBeetween(final Coordinate from, final Coordinate to) {
		final List<NavAid> nearAids = new ArrayList<NavAid>();
		// we do a little optimisation here rather than calculating
		// all distances accurately we make a quick rough calculation to exclude many coordinates

		for (final NavAid loc : _navAids) {
                        // See if near enough
                        if (
                            (from.getLongitude()<  loc.getLoc().getLongitude() ) &&
                            (loc.getLoc().getLongitude() < to.getLongitude()) &&

                            (to.getLatitude()<  loc.getLoc().getLatitude() ) &&
                            (loc.getLoc().getLatitude() < from.getLatitude())
                        ) {
                            nearAids.add(loc);
                        }
		}

		return nearAids;
	}




 /**
	 * Find all the navaids near a given point.
	 * @param fromCoord
	 * @param heading
	 * @param normalLegDistance
	 * @return
	 */
	public List<Airport> getAirportBeetween(final Coordinate from, final Coordinate to) {
		final List<Airport> nearAids = new ArrayList<Airport>();
		// we do a little optimisation here rather than calculating
		// all distances accurately we make a quick rough calculation to exclude many coordinates

		for (final Airport loc : _airports) {
                        // See if near enough
                        if (
                            (from.getLongitude()<  loc.getLoc().getLongitude() ) &&
                            (loc.getLoc().getLongitude() < to.getLongitude()) &&

                            (to.getLatitude()<  loc.getLoc().getLatitude() ) &&
                            (loc.getLoc().getLatitude() < from.getLatitude())
                        ) {
                            nearAids.add(loc);
                        }
		}

		return nearAids;
	}

}
