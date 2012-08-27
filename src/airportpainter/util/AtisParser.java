/*
 * AtisParser.java
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import airportpainter.Atis;
import airportpainter.Airport;
import airportpainter.earth.Earth;
import java.io.EOFException;
import java.util.StringTokenizer;
/**
 * Parser for the flight gear airports file. 
 * This class just reads the header and delegates the work to the correct parser for that version.
 */
public class AtisParser {

    /* (non-Javadoc)
	 * @see au.com.kelpie.fgfp.parsers.IAirportParserFG#parse(java.util.List, java.lang.String, java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
    public void parse(final List<Atis> airports, final String atisPath) throws IOException {

        // Open the airports file
    	BufferedReader rdrAtis;
        //try {
        rdrAtis = new BufferedReader(new InputStreamReader(new FileInputStream(atisPath)));
        //} catch (final IOException ioex) {
            // Try the file in non-zipped version
            // in some distributions = the Flightgear files have not
            // actually been zipped.
            //rdrAtis = new BufferedReader(new InputStreamReader(new FileInputStream(atisPath)));
        //}
        
    	try {
                rdrAtis.mark(2000);
                rdrAtis.reset();
                loadAtises(Earth.getAtises(), rdrAtis);
            } finally {
                rdrAtis.close();
            }

    }
    public void loadAtises(final List<Atis> atises, final BufferedReader in)
    throws IOException {

        StringTokenizer tokenizer;

        Atis atis = null;
        int atisCount = 0;
        long offset = 0;

        // Skip 2 header rows
        String buf = in.readLine();
        offset += buf.length() + 1;
        buf = in.readLine();
        offset += buf.length() + 1;
        buf = in.readLine();
        try {
            while (true) {
                Thread.yield();
                if (buf == null) {
                    break;
                }
                if (buf.length() > 0) {
                    tokenizer = new StringTokenizer(buf);
                    String rType = tokenizer.nextToken();
                    if (rType.equals("I")) {// atis
                        final double latitude = new Double(tokenizer.nextToken()).doubleValue();
                        final double longitude = new Double(tokenizer.nextToken()).doubleValue();
                        final double elevation = new Double(tokenizer.nextToken()).doubleValue();
                        final double freq = new Double(tokenizer.nextToken()).doubleValue();
                        final String unknown = tokenizer.nextToken();
                        final String id = tokenizer.nextToken();

                        final StringBuffer name = new StringBuffer();
                        while (tokenizer.hasMoreTokens()) {
                            name.append(tokenizer.nextToken());
                            name.append(" "); //$NON-NLS-1$
                        }
                        
                        String atisName=name.toString().trim();
                        if (atisName.startsWith("\"")) {
                            atisName=atisName.substring(1);
                        }
                        if (atisName.endsWith("\"") ) {
                            atisName=atisName.substring(0,atisName.length()-1);
                        }



                        atis = new Atis();
                        atis.setLat(latitude);
                        atis.setLong(longitude);
                        atis.setElevation(elevation);
                        atis.setId(id);
                        atis.setName(atisName);
                        atis.setFreq(freq);

                        atises.add(atis);

                        Airport airport=Earth.getAirportById(id);
                        if (airport!=null) {
                            airport.addAtis(atis);
                        } else {
                            //System.out.println("No airport for atis:"+id);
                        }



                        //System.out.println("ADD:"+airport.getName());

                        atisCount++;
                        
                        offset += buf.length() + 1;
                        buf = in.readLine();


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
        	e.printStackTrace();
        } catch (final Exception e) {
        	e.printStackTrace();
        }

    }


}