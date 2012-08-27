/*
 * IAirportParserFG.java
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
import java.io.IOException;
import java.util.List;

import airportpainter.Airport;

public interface IAirportParserFG {

	/**
	 * Parse the airport file and load into the Airport Collection.
	 * There are different implementations for each version of the data.
	 * 
	 * @param airport
	 *            List to contain the airport
	 * @param rdrAirport
	 *            For the airport file.
	 * @param monitor
	 *            Progress monitor.
	 */
	void loadAirports(final List<Airport> airports, final BufferedReader rdrAirport) throws IOException;

	/**
	 * @param airport
	 * @param rdrAirport
	 *            For the airport file.
	 * @param rdrRunwayIls
	 *            For the Runways ils information.
	 * @throws IOException
	 */
	public abstract void loadRunways(final Airport airport, final BufferedReader rdrAirport, final BufferedReader rdrRunwayIls)
			throws IOException;

}