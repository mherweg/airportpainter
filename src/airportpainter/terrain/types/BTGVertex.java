/*
 * BTGVertex.java
 *
 * Copyright (C) 2011 Francesco Brisa
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
package airportpainter.terrain.types;

import airportpainter.terrain.BTG;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 *
 * @author francesco
 */
public class BTGVertex {
    //int id;
    public float xPart;
    public float yPart;
    public float zPart;
    
    private Point mapPoint=null;
    
    public ArrayList<Polygon> iBelongTo=new ArrayList<Polygon>(3);
    
    public void afterRead(byte data[]) {
        byte i[]={
            data[0],
            data[1],
            data[2],
            data[3]
        };
        xPart=BTG.bytesToFloatLittleEndian( i );

        byte i2[]={
            data[4],
            data[5],
            data[6],
            data[7]
        };
        yPart=BTG.bytesToFloatLittleEndian( i2 );
        
        byte i3[]={
            data[8],
            data[9],
            data[10],
            data[11]
        };
        zPart=BTG.bytesToFloatLittleEndian( i3 );

    }

    /**
     * @return the mapPoint
     */
    public Point getMapPoint() {
        return mapPoint;
    }

    /**
     * @param mapPoint the mapPoint to set
     */
    public void setMapPoint(Point mapPoint) {
        this.mapPoint = mapPoint;
    }
    
}
