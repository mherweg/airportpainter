/*
 * Taxiway.java
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
import java.util.ArrayList;
import java.util.List;


/**
 * Taxiway object.
 * Represents a taxiway as used in Flightgear.
 * Normally loaded from the <b>default.apt.gz</b> file.
 */
public class Taxiway implements Serializable {

	/**
	 * Serial version Id
	 */
	private static final long serialVersionUID = -4858865796185254698L;
	private final static int FEET_PER_DEGREE = 6076 * 60;
	protected String _id;
	protected List<LayoutNode> _nodes = new ArrayList<LayoutNode>();
	protected boolean _centreLineLights;
	protected String _surface;
	protected String _edgeLights;
	
	public Taxiway() {
		
	}

	/**
	 * Constructor Taxiway.
	 * @param id
	 * @param lat
	 * @param long
	 * @param length
	 * @param width
	 * @param heading
	 * @param centreLights
	 * @param surface
	 * @param edgeLights
	 */
	public Taxiway(final String id, final double latitude, final double longitude, final int length, final int width, final double heading, final boolean centreLineLights, final String surface, final String edgeLights) {

		_id = id;
		_nodes = makeNodesFromBox(latitude, longitude, length, heading, width);
		_centreLineLights = centreLineLights;
		_surface = surface;
		_edgeLights = edgeLights;
	}

	/**
	 * Given basic layout info construct a set of nodes that describe the border of area.
	 * @param latitude
	 * @param longitude
	 * @param length
	 * @param heading
	 * @param width
	 * @return
	 */
	private List<LayoutNode> makeNodesFromBox(double latitude,
			double longitude, int length, double heading, int width) {
		List<LayoutNode> nodes = new ArrayList<LayoutNode>();
		
		final double headingRadians = Math.toRadians(heading);

		// Corner offset component contributed by the runway length
		double feetEastL = length / 2 * Math.sin(headingRadians);
		double feetNorthL = length / 2 * Math.cos(headingRadians);

		// Corner offset component contributed by the runway width
		final double feetEastW = width / 2 * Math.cos(headingRadians);
		final double feetNorthW = width / 2 * Math.sin(headingRadians);

		// Get the corners (Corners A,B,C,D)
		final double ALat = latitude + (feetNorthL + feetNorthW) / FEET_PER_DEGREE;
		final double ALong = longitude + (feetEastL - feetEastW) / (FEET_PER_DEGREE * Math.cos(Math.toRadians(latitude)));

		final double BLat = latitude + (feetNorthL - feetNorthW) / FEET_PER_DEGREE;
		final double BLong = longitude + (feetEastL + feetEastW) / (FEET_PER_DEGREE * Math.cos(Math.toRadians(latitude)));

		final double CLat = latitude + (-feetNorthL - feetNorthW) / FEET_PER_DEGREE;
		final double CLong = longitude + (-feetEastL + feetEastW) / (FEET_PER_DEGREE * Math.cos(Math.toRadians(latitude)));

		final double DLat = latitude + (-feetNorthL + feetNorthW) / FEET_PER_DEGREE;
		final double DLong = longitude + (-feetEastL - feetEastW) / (FEET_PER_DEGREE * Math.cos(Math.toRadians(latitude)));

		nodes.add( new LayoutNode("111", ALat, ALong)); 
		nodes.add( new LayoutNode("111", BLat, BLong)); 
		nodes.add( new LayoutNode("111", CLat, CLong)); 
		nodes.add( new LayoutNode("113", DLat, DLong)); 
		
		return nodes;
	}

	/**
	 * Constructor Taxiway.
	 * @param id
	 * @param nodeList  List of Layout nodes that define the runway
	 * @param centreLights
	 * @param surface
	 * @param edgeLights
	 */
	public Taxiway(final String id, List<LayoutNode> nodes, final boolean centreLineLights, final String surface, final String edgeLights) {

		_id = id;
		_nodes = nodes;
		_centreLineLights = centreLineLights;
		_surface = surface;
		_edgeLights = edgeLights;
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
	 * Get the id.
	 * @return String
	 */
	public String getId() {
		return _id;
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
	 * Set the centre line lights flag.
	 * A=Asphalt, C=Concrete, T=Turf, D=Dirt, G=Gravel, W=Water, X=Other
	 * @param centreLineLights
	 */
	public void setCentreLineLights(final boolean centreLineLights) {
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
	 * Set the id.
	 * @param id The id to set
	 */
	public void setId(final String id) {
		_id = id;
	}

	/**
	 * Set the surface.
	 * A=Asphalt, C=Concrete, T=Turf, D=Dirt, G=Gravel, W=Water, X=Other
	 * @param surface The surface to set
	 */
	public void setSurface(final String surface) {
		_surface = surface;
	}

	public List<LayoutNode> getNodes() {
		return _nodes;
	}

}
