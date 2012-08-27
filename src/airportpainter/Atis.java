/*
 * Atis.java
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

/**
 * Atis object.
 * Represents a Atis as used in Flightgear.
 * Normally loaded from the <b>default.apt.gz</b> file.
 */
public class Atis extends Location implements Serializable {
    
        //I  37.619  -122.375  11  113.7  50  KSFO  "San-Francisco-International"
	private String _unknow1;
	private double _freq;
	private String _unknow2;
	
	public Atis() {

	}

    /**
     * @return the _freq
     */
    public double getFreq() {
        return _freq;
    }

    /**
     * @param freq the _freq to set
     */
    public void setFreq(double freq) {
        this._freq = freq;
    }


}
