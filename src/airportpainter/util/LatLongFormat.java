/*
 * LatFormat.java
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

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.StringTokenizer;

/**
 * Formatter to format either a latitude or longitude.
 * Latitudes and Longitude share a base format of 2312"08'.
 */
public abstract class LatLongFormat extends NumberFormat {

	private static final DecimalFormat nf = new DecimalFormat("00");  //$NON-NLS-1$
		
	/**
	 * Default constructor
	 */
	public LatLongFormat() {
		super();
	}

	/**
	 * Convert a decimal degree to degree min sec format.
	 * 
	 * @param d
	 * @return
	 */
	private void decToDegree(final StringBuffer buff, final double d) {
		// The degree portion is just the integer portion of the value
		double deg = Math.floor(d);
		final double minsec = (d - deg) * 60;
		double min = Math.floor(minsec);
		double sec = (minsec - min) * 60;
		// Correct for rounding errors
		if (60-sec < .005) {
			sec = 0;
			min++;
		}
		if (60-min < .005) {
			min = 0;
			deg++;
		}
		buff.append(nf.format(deg));
		buff.append("\u00b0"); //$NON-NLS-1$
		buff.append(nf.format(min));
		buff.append("\""); //$NON-NLS-1$
		buff.append(nf.format(sec));
		buff.append("\'");		  //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public abstract StringBuffer format(
		double number,
		StringBuffer toAppendTo,
		FieldPosition pos) ;

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(
		final long number,
		final StringBuffer toAppendTo,
		final FieldPosition pos) {
		return this.format((double) number, toAppendTo, pos);
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public StringBuffer formatAbsolute(
		final double number,
		final StringBuffer toAppendTo,
		final FieldPosition pos) {
		
		decToDegree(toAppendTo, Math.abs(number));
		return toAppendTo;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Number parse(final String source, final ParsePosition parsePosition) {
		int sign = 1;
		double deg = 0;
		double min = 0;
		double sec = 0;
		String work = source.substring(parsePosition.getIndex()).trim();
		parsePosition.setIndex(parsePosition.getIndex() + work.length()-1);
		// Check if the string is a simple decimal number
		try {
			parsePosition.setIndex(work.length());
			return Double.valueOf(source);
		} catch (final NumberFormatException nfe){
			// See if the last char is N,S,E or W
			
			final char lastChar = work.charAt(work.length()-1);
			switch (lastChar) {
				case 'E' :
				case 'N' :
					sign = +1;
					work = work.substring(0, work.length()-1);
					break;
				case 'W' :
				case 'S' :
					sign = -1;
					work = work.substring(0, work.length()-1);
					break;
				default :
					break;
			}
			//  Tokenize the string using deg/sec/min and just blanks and . 
			final StringTokenizer tokenizer = new StringTokenizer(work, " .\u00b0\"\'");  //$NON-NLS-1$
			// We should get 1-3 tokens each of which is numeric
			// If any of the following throw Number format exceptions
			// then the exception is just re-thrown by this method
			if (tokenizer.hasMoreTokens()) {
				final String degTok = tokenizer.nextToken();
				deg = Double.parseDouble(degTok);
				if (deg > 180) {
					throw new NumberFormatException(source);
				}
			}
			if (tokenizer.hasMoreTokens()) {
				final String degTok = tokenizer.nextToken();
				min = Double.parseDouble(degTok);
				if (min > 60) {
					throw new NumberFormatException(source);
				}
			}
			if (tokenizer.hasMoreTokens()) {
				final String degTok = tokenizer.nextToken();
				sec = Double.parseDouble(degTok);
				if (sec > 60) {
					throw new NumberFormatException(source);
				}
			}
			
			return new Double((deg + min/60 + sec /3600) * sign);
			
		}
		
	}

}