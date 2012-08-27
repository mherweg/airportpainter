/*
 * BTGVertexList.java
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

import airportpainter.terrain.BTGObjectElement;
import java.util.Arrays;

/**
 *
 * @author francesco
 */
public class BTGVertexList extends BTGObjectElement {
    @Override
    public void afterRead() {
        //vertexes=new ArrayList<BTGVertex>((int) (numberOfBytes/12));

        for (int t=0;t<(numberOfBytes/12);t++) {
            BTGVertex v=new BTGVertex();
        
            //v.id=btgObject.vertexIndex;
            //btgObject.vertexIndex++;
            
            byte i[]=Arrays.copyOfRange(propertyData,t*12,t*12+12);
            v.afterRead(i);
            
            
            //vertexes.add(v);
            
            btgObject.fileBtg.vertexes.add(v);
            v.setMapPoint( fromVertexIndexToPoint(btgObject.fileBtg.vertexes.size()-1)  );
        }        
    }
}
