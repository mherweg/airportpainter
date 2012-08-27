/*
 * AirportParserFG.java
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
import java.util.zip.GZIPInputStream;

import airportpainter.Airport;
import airportpainter.logging.Logger;
import airportpainter.logging.Logger.Realm;
/**
 * Parser for the flight gear airports file. 
 * This class just reads the header and delegates the work to the correct parser for that version.
 */
public class AirportParserFG {

    /* (non-Javadoc)
	 * @see au.com.kelpie.fgfp.parsers.IAirportParserFG#parse(java.util.List, java.lang.String, java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
    public void parse(final List<Airport> airports, final String airportPath, final String runwayPath) throws IOException {

        // Open the airports file
    	BufferedReader rdrAirport;
        try {
        	rdrAirport = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
            		airportPath))));
        } catch (final IOException ioex) {
            // Try the file in non-zipped version
            // in some distributions = the Flightgear files have not
            // actually been zipped.
        	rdrAirport = new BufferedReader(new InputStreamReader(new FileInputStream(airportPath)));
        }
        
    	try {
        	rdrAirport.mark(2000);
			IAirportParserFG parser = getParserForVersion(rdrAirport);
			rdrAirport.reset();
			parser.loadAirports(airports, rdrAirport);
		} finally {
			rdrAirport.close();
		}

    }

    /* (non-Javadoc)
     * @see au.com.kelpie.fgfp.parsers.IAirportParserFG#loadRunways(au.com.kelpie.fgfp.model.Airport, java.lang.String, java.lang.String)
     */
    public synchronized void loadRunways(final Airport airport, final String airportPath, final String runwayIlsPath)
            throws IOException {
    	
    	// Open the airports file
    	BufferedReader rdrAirport;
        try {
            rdrAirport = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
            		airportPath))));
        } catch (final IOException ioex) {
            // Try the file in non-zipped version
            // in some distributions = the Flightgear files have not
            // actually been zipped.
            rdrAirport = new BufferedReader(new InputStreamReader(new FileInputStream(airportPath)));
        }
        
        BufferedReader rdrIls;
        try {
        	rdrIls = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
        			runwayIlsPath))));
        } catch (final IOException ioex) {
            // Try the file in non-zipped version
            // in some distributions = the Flightgear files have not
            // actually been zipped.
        	rdrIls = new BufferedReader(new InputStreamReader(new FileInputStream(runwayIlsPath)));
        }
        
        try {
        	rdrAirport.mark(2000);
			IAirportParserFG parser = getParserForVersion(rdrAirport);
			rdrAirport.reset();
			parser.loadRunways(airport, rdrAirport, rdrIls);
        } catch (IOException e) {
            Logger.logException(e,Realm.AIRPORT);
        } finally {
        	rdrAirport.close();
        	rdrIls.close();
		}
    }       
    
    private IAirportParserFG getParserForVersion(BufferedReader in) throws IOException {
		// Read the first 2 lines. The version is the first token on the second line
    	in.readLine();
    	String buf = in.readLine();
    	/*if (buf.startsWith("715")) {
    		//return new AirportParserFG715();
    	}
    	if (buf.startsWith("850")) {
    		//return new AirportParserFG850();
    	}*/
    	if (buf.startsWith("810")) {
    		return new AirportParserFG810();
    	}
    	
		throw new IOException("Airport file has unknown format.");
	}

}