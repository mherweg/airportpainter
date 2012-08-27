/*
 * NavAidParserFG.java
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
import java.util.zip.GZIPInputStream;



/**
 * Parser for the flight gear navaids file.
 * Reads and parses the airports file into an Collection of
 * java objects
 */
public class NavAidParserFG {

    /**
     * Parse the navaid file and load into the navaid Collection.
     * @param navaids List to contain the navaids
     * @param path For the navaid file.
     * @param monitor Progress monitor.
     */
    public void parse(final List<NavAid> navaids, final String path) throws IOException {

        BufferedReader in;
        try  {
                in = new BufferedReader(
                        new InputStreamReader(
                                new GZIPInputStream(new FileInputStream(path))));
        } catch (final IOException ioex)  {
                // Try the file in non-zipped version
                // in some distributions = the Flightgear files have not
                // actually been zipped.
                in = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path)));
        }
        StringTokenizer tokenizer;

        NavAid navAid = null;
        int navAidCount = 0;

        try {
            char c0='0';
            while (true) {
                Thread.yield();
                final String buf = in.readLine();
                if (buf == null) {
                        break;
                }
                if (buf.length() > 0) {

                    c0=buf.charAt(0);
                    if (c0=='1' || c0=='2' || c0=='3') {
                        tokenizer = new StringTokenizer(buf);
                        tokenizer.nextToken();
                        final double latitude = new Double(tokenizer.nextToken()).doubleValue();
                        final double longitude = new Double(tokenizer.nextToken()).doubleValue();
                        final double elevation = new Double(tokenizer.nextToken()).doubleValue();
                        final double freq = new Double(tokenizer.nextToken()).doubleValue();
                        final double range = new Double(tokenizer.nextToken()).doubleValue();

                        final String magVar = tokenizer.nextToken();
                        final String id = tokenizer.nextToken();
                        final StringBuffer name = new StringBuffer();
                        while (tokenizer.hasMoreTokens()) {
                                name.append(tokenizer.nextToken());
                                name.append(" "); //$NON-NLS-1$
                        }

                        if (c0=='1') {
                            if (buf.charAt(1)=='3') {
                                // TACAN
                                navAid =
                                        new NavAid(
                                                id,
                                                latitude,
                                                longitude,
                                                elevation,
                                                freq/100,
                                                range,
                                                magVar,
                                                name.toString().trim());
                                navAid.setType(NavAid.TACAN);
                            }
                        }

                        if (c0=='2') {
                            navAid =
                                    new NavAid(
                                            id,
                                            latitude,
                                            longitude,
                                            elevation,
                                            freq,
                                            range,
                                            magVar,
                                            name.toString().trim());
                            navAid.setType(NavAid.NDB);
                        }

                        if (c0=='3') {
                            navAid =
                                    new NavAid(
                                            id,
                                            latitude,
                                            longitude,
                                            elevation,
                                            freq / 100,
                                            range,
                                            magVar,
                                            name.toString().trim());
                            navAid.setType(NavAid.VOR);
                        }
                    }
                    if (navAid!=null) {
                        navaids.add(navAid);
                        navAidCount++;

                    }
                }
            }
        } catch (final EOFException e) {
        } finally {
                in.close();
        }

    }

}
