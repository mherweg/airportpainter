/*
 * AirportPainter.java
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
import java.text.FieldPosition;


/**
 * Format a Latitude
 */
public class LatFormat extends LatLongFormat {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2021425344356567043L;

	/**
	 * 
	 */
	public LatFormat() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(
		final double number,
		final StringBuffer toAppendTo,
		final FieldPosition pos) {
		return super.formatAbsolute(number, toAppendTo, pos).append( number>0?"N":"S"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
