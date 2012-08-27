/*
 * BTGBoundingSphere.java
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
import airportpainter.terrain.BTGObjectElement;

/**
 *
 * @author francesco
 */
public class BTGBoundingSphere extends BTGObjectElement {
    public double xPart;
    public double yPart;
    public double zPart;
    public float radius;
    
    @Override
    public void afterRead() {
        
        
        byte i[]={
            propertyData[0],
            propertyData[1],
            propertyData[2],
            propertyData[3],
            propertyData[4],
            propertyData[5],
            propertyData[6],
            propertyData[7]
        };
        xPart=BTG.bytesToDoubleLittleEndian( i );

        byte i2[]={
            propertyData[8],
            propertyData[9],
            propertyData[10],
            propertyData[11],
            propertyData[12],
            propertyData[13],
            propertyData[14],
            propertyData[15]
        };
        yPart=BTG.bytesToDoubleLittleEndian( i2 );

        byte i3[]={
            propertyData[16],
            propertyData[17],
            propertyData[18],
            propertyData[19],
            propertyData[20],
            propertyData[21],
            propertyData[22],
            propertyData[23],
        };
        zPart=BTG.bytesToDoubleLittleEndian( i3 );
        
        byte i4[]={
            propertyData[24],
            propertyData[25],
            propertyData[26],
            propertyData[27]
        };
        radius=BTG.bytesToFloatLittleEndian( i4 );
        
        
    }
}
