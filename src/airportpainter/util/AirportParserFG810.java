/*
 * AirportParserFG810.java
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

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


import airportpainter.Airport;
import airportpainter.Runway;
import airportpainter.Taxiway;
import airportpainter.ATCFreq;
import airportpainter.Beacon;
import airportpainter.Location;
import airportpainter.Tower;
import airportpainter.logging.Logger;
import java.util.ArrayList;
/**
 * Parser for the flight gear airports file. Version 0.9.4 and above) Reads and parses the airports
 * file into an Collection of java objects
 */
public class AirportParserFG810 implements IAirportParserFG {

    private final HashMap<String, Airport> _airportMap = new HashMap<String, Airport>(25000);
    private final HashMap<String, Runway> _runwayMap = new HashMap<String, Runway>(25000);
    private final HashMap<String, Runway> _runwayOppositeMap = new HashMap<String, Runway>(25000);


    private static final HashMap<String, Long> _runwayOffsets = new HashMap<String, Long>(25000);

    /**
     * Parse the airport file and load into the Airport Collection
     * 
     * @param monitor
     * @param map
     * @param string
     */
    public void loadAirports(final List<Airport> airports, final BufferedReader in)
    throws IOException {

        StringTokenizer tokenizer;

        Airport airport = null;
        int airportCount = 0;
        long offset = 0;

        // Skip 2 header rows
        String buf = in.readLine();
        offset += buf.length() + 1;
        buf = in.readLine();
        offset += buf.length() + 1;
        buf = in.readLine();
        try {
            while (true) {
                if (buf == null) {
                    break;
                }
                if (buf.length() > 0) {
                    tokenizer = new StringTokenizer(buf);
                    String rType = tokenizer.nextToken();
                    if (rType.equals("1")) {// airport //$NON-NLS-1$
                        List<ATCFreq> _towers = new ArrayList<ATCFreq>(1);

                        final double elevation = new Double(tokenizer.nextToken()).doubleValue();
                        final boolean tower = tokenizer.nextToken().equals("1");  //$NON-NLS-1$
                        final boolean defaultBuildings = tokenizer.nextToken().equals("1"); //$NON-NLS-1$
                        final String id = tokenizer.nextToken();
                        // Store the offset so we can load the runways later
                        _runwayOffsets.put(id, new Long(offset));
                        final StringBuffer name = new StringBuffer();
                        while (tokenizer.hasMoreTokens()) {
                            name.append(tokenizer.nextToken());
                            name.append(" "); //$NON-NLS-1$
                        }
                        // Now read runways to get a latitude and longitude
                        // and find the longest
                        long maxLength = 0;
                        
                        
                        double latitude = 0;
                        double longitude = 0;
                        buf = in.readLine();
                        while (true) {
	                        if (buf == null) {
	                            break;
	                        }
	                        if (buf.length() > 0) {
	                        	tokenizer = new StringTokenizer(buf);
	                        	rType = tokenizer.nextToken();
	                        	if (rType.equals("1")) {// airport //$NON-NLS-1$
	                        		break;
	                        	}
	                        	if (rType.equals("10")) {// runway or taxiway //$NON-NLS-1$
	                        		final double r_lat = new Double(tokenizer.nextToken()).doubleValue();
	                        		final double r_long = new Double(tokenizer.nextToken()).doubleValue();
	                        		final String r_number = tokenizer.nextToken();
	                        		if (!r_number.equals("xxx")) {  // not a taxiway //$NON-NLS-1$
	                        			/* r_hdg */ tokenizer.nextToken();
	                        			final int r_length = new Integer(tokenizer.nextToken()).intValue();
	                        			if (r_length > maxLength) {
	                        				maxLength = r_length;
	                        				latitude = r_lat;
	                        				longitude = r_long;
	                        			}
	                        		}
                                        } else if (
                                            rType.equals("51") ||
                                            rType.equals("52") ||
                                            rType.equals("53") ||
                                            rType.equals("54") ||
                                            rType.equals("55") ||
                                            rType.equals("56")
                                        ) {

                                            final double freq = new Double(tokenizer.nextToken()).doubleValue()/100;

                                            final StringBuffer nameF = new StringBuffer();
                                            while (tokenizer.hasMoreTokens()) {
                                                nameF.append(tokenizer.nextToken());
                                                nameF.append(" "); //$NON-NLS-1$
                                            }


                                            ATCFreq mytower=new ATCFreq();
                                            mytower.setType(rType);
                                            mytower.setFreq(freq);
                                            mytower.setName(nameF.toString());

                                            _towers.add(mytower);

                                            //airport.addTower(mytower);
	                        	}
	                        }
	                        offset += buf.length() + 1;
	                        buf = in.readLine();
	                    }
                        airport = new Airport(id, latitude, longitude, elevation, "", tower, //$NON-NLS-1$
                                defaultBuildings, name.toString().trim(), maxLength);
                        airport.setATCFreqs(_towers);
                        airports.add(airport);

                        //System.out.println("ADD:"+airport.getName());

                        airportCount++;
                        
/*                        if (airportCount % 1000 == 0) {
                            monitor
                                    .subTask(Messages.getString("AirportParser.3") + airportCount + Messages.getString("AirportParser.4")); //$NON-NLS-1$ //$NON-NLS-2$
                            monitor.worked(1000);
                        }*/
                        // put it into our temporary map so we can add runways later
                        _airportMap.put(id, airport);
                    } else {
                        offset += buf.length() + 1;
                        buf = in.readLine();
                    }
                } else {
                    offset += buf.length() + 1;
                    buf = in.readLine();
                }
            }
        } catch (final EOFException e) {
        	Logger.logException(e, Logger.Realm.AIRPORT);
        } catch (final Exception e) {
        	Logger.logException(e, Logger.Realm.AIRPORT);
        }
        
    }


    /* (non-Javadoc)
	 * @see au.com.kelpie.fgfp.parsers.IAirportParserFG#loadRunways(au.com.kelpie.fgfp.model.Airport, java.lang.String, java.lang.String)
	 */
    public synchronized void loadRunways(final Airport airport, final BufferedReader rdrAirport, final BufferedReader rdrRunwayIls)
    	throws IOException {

        StringTokenizer tokenizer;

        try {
            final long offset = (_runwayOffsets.get(airport.getId())).longValue();
            rdrAirport.skip(offset);
            while (true) {
            	final String buf = rdrAirport.readLine();
            	if (buf == null) {
            		break;
            	}

            	if (buf.length() > 0) {
            		tokenizer = new StringTokenizer(buf);
            		final String rType = tokenizer.nextToken();
            		if (rType.equals("1")) {// airport //$NON-NLS-1$
            			tokenizer.nextToken();  //elev
            			tokenizer.nextToken();  // tower 
            			tokenizer.nextToken();  // buildings
            			final String id = tokenizer.nextToken();
            			if (airport.getId().equals(id)) {
            				loadRunwaysForAirport(airport, rdrRunwayIls, rdrAirport);
            				return;
            			}
            		}
            	}
            }
        } catch (final EOFException e) {
        }
    }

    /**
     * @param airport
     * @param runwayIlsPath
     * @param in
     * @throws IOException
     */
    private void loadRunwaysForAirport(final Airport airport, final BufferedReader rdrRunwayIls, final BufferedReader rdrAirport)
    throws IOException {
        StringTokenizer tokenizer;
        String buf;
        boolean match = true;
        // Load the runways
        while (match) {
            buf = rdrAirport.readLine();
            if (buf == null) {
                break;
            }
            if (buf.length() > 0) {
                tokenizer = new StringTokenizer(buf);
                final String rType = tokenizer.nextToken();
                if (rType.equals("10")) {// runway or taxiway //$NON-NLS-1$
                    final double r_lat = new Double(tokenizer.nextToken()).doubleValue();
                    final double r_long = new Double(tokenizer.nextToken()).doubleValue();
                    String r_number = tokenizer.nextToken();
                    final double r_hdg = new Double(tokenizer.nextToken()).doubleValue();
                    final int r_length = new Integer(tokenizer.nextToken()).intValue();
                    final String r_displacement = tokenizer.nextToken();//displacement
                    tokenizer.nextToken();  //stopway
                    final int r_width = new Integer(tokenizer.nextToken()).intValue();
                    final String r_edgeLights = tokenizer.nextToken();
                    final String r_surface = tokenizer.nextToken();
                    if (!r_number.equals("xxx")) { //$NON-NLS-1$
                    	// Center runways now have an x appended.
                    	if (r_number.endsWith("x")) { //$NON-NLS-1$
                    		r_number = r_number.substring(0, 2);
                    	}
                        tokenizer.nextToken();  //shoulder
                        final String r_markings = tokenizer.nextToken();
                        final Runway runway = new Runway(r_number, r_lat, r_long, r_length, r_width, r_hdg,
                                false, r_surface, r_edgeLights, r_markings,r_displacement);
                        airport.addRunway(runway);
                        // put it into our temporary map so we can add runways
                        // later
                        _runwayMap.put(airport.getId() + runway.getNumber(), runway);
                        _runwayOppositeMap.put(airport.getId() + runway.getOppositeNumber(), runway);
                    } else {
                        final Taxiway taxiway = new Taxiway("", r_lat, r_long, r_length, r_width, r_hdg, //$NON-NLS-1$
                                false, r_surface, r_edgeLights);
                        airport.addTaxiway(taxiway);
                    }
                } else if (rType.equals("14")) {// tower
                    final double r_lat = new Double(tokenizer.nextToken()).doubleValue();
                    final double r_long = new Double(tokenizer.nextToken()).doubleValue();

                    final Tower tower = new Tower();
                    tower.setLat(r_lat);
                    tower.setLong(r_long);

                    airport.setTower(tower);
                } else if (rType.equals("15")) {// Startup position
                } else if (rType.equals("18")) {// beacon
                    final double r_lat = new Double(tokenizer.nextToken()).doubleValue();
                    final double r_long = new Double(tokenizer.nextToken()).doubleValue();
                    String r_number = tokenizer.nextToken();
                    String name = tokenizer.nextToken();

                    final Beacon beacon = new Beacon();
                    beacon.setLat(r_lat);
                    beacon.setLong(r_long);
                    beacon.setName(name);
                    airport.setBeacon(beacon);
                    
                } else if (rType.equals("19")) {// windsock
                    final double r_lat = new Double(tokenizer.nextToken()).doubleValue();
                    final double r_long = new Double(tokenizer.nextToken()).doubleValue();

                    final Location windosock = new Location();
                    windosock.setLat(r_lat);
                    windosock.setLong(r_long);

                    airport.addWindsock(windosock);
                } else {
                    loadIls(rdrRunwayIls, airport);
                    match = false;
                }
            }
        }
    }

    /**
     * Parse the ils file and add to runway data
     * 
     * @param monitor
     * @param airports
     * @param string
     */
    private void loadIls(final BufferedReader rdrRunwayIls, final Airport airport) throws IOException {

        StringTokenizer tokenizer;

        while (true) {
        	final String buf = rdrRunwayIls.readLine();
        	if (buf == null) {
        		break;
        	}
        	if (buf.length() > 0) {
        		switch (buf.charAt(0)) {
        		// Version 9.05 and later
        		case '4': // navAid
        		case '5': // navAid

        			tokenizer = new StringTokenizer(buf);
        			tokenizer.nextToken();
        			if (tokenizer.hasMoreTokens()) {
                                    /* latitude */ tokenizer.nextToken();
                                } else {
                                    Logger.log("Warning:"+buf+" has no more tokens (Latitude)",Logger.Level.INFO, Logger.Realm.AIRPORT);
                                }
        			if (tokenizer.hasMoreTokens()) {
                                    /* longitude */ tokenizer.nextToken();
                                } else {
                                    Logger.log("Warning:"+buf+" has no more tokens (Longitude)",Logger.Level.INFO, Logger.Realm.AIRPORT);
                                }
        			if (tokenizer.hasMoreTokens()) {
                                    /* elevation */ tokenizer.nextToken();
                                } else {
                                    Logger.log("Warning:"+buf+" has no more tokens (Elevation)",Logger.Level.INFO, Logger.Realm.AIRPORT);
                                }


                                
        			final double freq = new Double(tokenizer.nextToken()).doubleValue();
        			/* magVar */ tokenizer.nextToken();
        			tokenizer.nextToken();
        			tokenizer.nextToken();
        			final String airportId = tokenizer.nextToken();
        			final String runwayId = tokenizer.nextToken();
        			// Match the airport
        			if (airportId.equals(airport.getId())) {
        				for (final Runway runway : airport.getRunways()) {
        					if (runway.getNumber().equals(runwayId)) {
        						runway.setIlsFreq(freq / 100);
        					}
        					if (runway.getOppositeNumber().equals(runwayId)) {
        						runway.setOppositeIlsFreq(freq / 100);
        					}
        				}
        			}
        			break;
        		}
        	}
        }
        
    }

}