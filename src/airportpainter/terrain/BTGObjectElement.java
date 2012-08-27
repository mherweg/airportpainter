/*
 * BTGObjectElement.java
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
package airportpainter.terrain;

import airportpainter.earth.Coordinate;
import airportpainter.logging.Logger;
import java.awt.Point;
import java.awt.Polygon;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BTGObjectElement {
    
    public byte type=0;
    
    public long numberOfBytes;
    public byte propertyData[];
    

    
    static int SG_IDX_VERTICES=0x01;
    static int SG_IDX_NORMALS=0x02;
    static int SG_IDX_COLORS=0x04;
    static int SG_IDX_TEXCOORDS=0x08;
    
    public airportpainter.terrain.BTGObject btgObject=null;
    
    public ArrayList<Polygon> poligons=new ArrayList<Polygon>();    
        
    public void readFromIn(final DataInputStream in) throws IOException, Exception {
//        byte b[]={0,0,0,0};
//        b[0]=in.readByte();
//        b[1]=in.readByte();
//        b[2]=in.readByte();
//        b[3]=in.readByte();

        
        //numberOfBytes=Integer.reverseBytes(in.readInt());
        
        numberOfBytes=BTG.toLittleInt(in.readInt());
        try {
            propertyData=new byte[(int) numberOfBytes];        
        } catch (OutOfMemoryError e) {
            Logger.logException(e, Logger.Realm.MAP);
            throw e;
        }
        // first 4 are ignored now
        
        for (int p=0;p<numberOfBytes;p++) {
            propertyData[p]=in.readByte();
        }        
    }
    
    public void afterRead() {
        Logger.log("TO DO !!!", Logger.Level.DEBUG ,Logger.Realm.MAP);
    }

public static int unsignedByteToInt(byte b) {
    return (int) b & 0xFF;
    }    

    protected Point coordinateToPoint(Coordinate from) {
        Point res=new Point();


        res.x = (int ) (((double)btgObject.fileBtg.width) *   ( from.getLongitude()- btgObject.fileBtg.upperLeft.getLongitude() )   / (   btgObject.fileBtg.lowerRight.getLongitude()-btgObject.fileBtg.upperLeft.getLongitude()  ) );
        res.y = (int ) (((double)btgObject.fileBtg.height) *   (from.getLatitude()-btgObject.fileBtg.upperLeft.getLatitude() )   / (   btgObject.fileBtg.lowerRight.getLatitude()-btgObject.fileBtg.upperLeft.getLatitude()  ) );

        return res;
    }
    protected Point fromVertexIndexToPoint(int idx) {
        double x=btgObject.fileBtg.boundingSphere.xPart+btgObject.fileBtg.vertexes.get(idx).xPart;
        double y=btgObject.fileBtg.boundingSphere.yPart+btgObject.fileBtg.vertexes.get(idx).yPart;
        double z=btgObject.fileBtg.boundingSphere.zPart+btgObject.fileBtg.vertexes.get(idx).zPart;

        Coordinate c=new Coordinate();        
        c.xyz2geo(x, y, z);

        Point p=new Point();
        p.setLocation(                
            coordinateToPoint( c).x,
            coordinateToPoint( c).y                                    
        );
        
        return p;
        
    }
}
