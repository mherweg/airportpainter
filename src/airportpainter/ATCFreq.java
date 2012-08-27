/*
 * ATCFreq.java
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
import java.util.HashMap;


/**
 * Atis object.
 * Represents a Atis as used in Flightgear.
 * Normally loaded from the <b>default.apt.gz</b> file.
 */
public class ATCFreq extends Location implements Serializable {
    
        //I  37.619  -122.375  11  113.7  50  KSFO  "San-Francisco-International"
	private double _freq;
        private String type;

        public static HashMap<String,String> typeNames=null;

	public ATCFreq() {
            if (typeNames==null) {
                typeNames=new HashMap<String,String>();
                typeNames.put("51", "UNICOM");
                typeNames.put("52", "CLEARANCE DELIVERY");
                typeNames.put("53", "GROUND");
                typeNames.put("54", "TOWER");
                typeNames.put("55", "APPROACH");
                typeNames.put("56", "DEPARTURE");
            }
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

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }


}
