/*
 * NavAidParserDafift.java
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Parser for the flight gear navaids file.
 * Reads and parses the airports file into an Collection of
 * java objects
 */
public class NavAidParserDafift {

	/**
	 * Parse the navaid file and load into the navaid Collection.
	 * @param navaids List to contain the navaids
	 * @param path For the navaid file.
	 * @param monitor Progress monitor.
	 */
	public void parse(final List<NavAid> navaids, final String path) throws IOException {

		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

		StringTokenizer tokenizer;

		String name = ""; //$NON-NLS-1$
		String id = ""; //$NON-NLS-1$
		int type = 0;
		double frequency = 0;
		double range = 0;
		double latitude = 0;
		double longitude = 0;
		double elevation = 0;
		String magVar = ""; //$NON-NLS-1$
		boolean coLocatedDME = false;

		NavAid navAid = null;
		int navAidCount = 0;

		try {
			// Bypass header		
			in.readLine();
			while (true) {
				Thread.yield();
				final String buf = in.readLine();
				if (buf == null) {
					break;
				}
				if (buf.length() > 0) {
					tokenizer = new StringTokenizer(buf, "\t", true); //$NON-NLS-1$
					// Need to walk over the tabs.  Empty tokens will not be returned
					String token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						id = token;
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						final int rawType = new Integer(token).intValue();
						if (rawType < 5) {
							type = NavAid.VOR;
							coLocatedDME = (rawType == 4);
						} else if (rawType < 8) {
							type = NavAid.NDB;
						} else if (rawType == 9) {
							type = NavAid.DME;
						} else {
							// We don't handle anything else
							continue;
						}
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						name = token;
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						if (!token.equals("U")) { //$NON-NLS-1$
							frequency = new Double(token.substring(0,token.length()-1)).doubleValue();
							frequency /= 1000;
						}
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						if (!token.equals("U")) { //$NON-NLS-1$
							range = new Double(token).doubleValue();
						}
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						latitude = new Double(token).doubleValue();
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						longitude = new Double(token).doubleValue();
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						magVar = token.substring(8);
						tokenizer.nextToken();
					}
					token = tokenizer.nextToken();
					if (!token.equals("\t")) { //$NON-NLS-1$
						if (!token.equals("U")) { //$NON-NLS-1$
							elevation = new Double(token).doubleValue();
						}
						tokenizer.nextToken();
					}

					navAid =
						new NavAid(
							id,
							latitude,
							longitude,
							elevation,
							frequency,
							range,
							magVar,
							name.toString().trim());
					navAid.setCoLocatedDME(coLocatedDME);
					navAid.setType(type);
					navaids.add(navAid);
					navAidCount++;					
				}
			}
		} catch (final EOFException e) {
		} finally {
			in.close();
		}

	}

}
