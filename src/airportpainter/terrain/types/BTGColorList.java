/*
 * BTGColorList.java
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
import airportpainter.terrain.BTGObject;
import airportpainter.terrain.BTGObjectElement;
import java.awt.Color;
import java.util.Arrays;

/**
 *
 * @author francesco
 */
public class BTGColorList extends BTGObjectElement {
    
   public BTGColorList() {
        super.type=BTGObject.TYPE_COLOR_LIST;
    }
     
   

   
   
    public void afterRead() {
        
        
        //vertexes=new ArrayList<BTGVertex>((int) (numberOfBytes/12));

        for (int t=0;t<(numberOfBytes/12);t++) {
            //Color c=new Color(); 
            
            
            byte i[]={
                propertyData[t*12+0],
                propertyData[t*12+1],
                propertyData[t*12+2],
                propertyData[t*12+3]
            };
            float green=BTG.bytesToFloatLittleEndian( i );

            byte i2[]={
                propertyData[t*12+4],
                propertyData[t*12+5],
                propertyData[t*12+6],
                propertyData[t*12+7]
            };
            float blue=BTG.bytesToFloatLittleEndian( i2 );

            byte i3[]={
                propertyData[t*12+8],
                propertyData[t*12+9],
                propertyData[t*12+10],
                propertyData[t*12+11]
            };
            float redPart=BTG.bytesToFloatLittleEndian( i3 );

            byte i4[]={
                propertyData[12],
                propertyData[13],
                propertyData[14],
                propertyData[15]
            };
            float alpha=BTG.bytesToFloatLittleEndian( i4 );

            //Color c=new Color(red,green,blue,alpha);            
            
            Color c=new Color(redPart,green,blue,alpha);
            
            btgObject.fileBtg.colors.add(c);
        }
        
    }
}
