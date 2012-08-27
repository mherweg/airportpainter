/*
 * BTGTriangleStrip.java
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

import airportpainter.terrain.BTGObject;
import airportpainter.terrain.BTGObjectElement;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 *
 * @author francesco
 */
public class BTGTriangleFan extends BTGObjectElement {
    //public ArrayList<BTGTriangleFace> triangles=new ArrayList<BTGTriangleFace>();    
    
    public BTGTriangleFan() {
        super.type=BTGObject.TYPE_TRIANGLE_FANS;
    }
    
    //public static int maxToDo=10;
    
    @Override
    public void afterRead() {
        //System.out.println("nbytes:"+numberOfBytes);

        //if (maxToDo>0)  {

            boolean do_vertices=true;
            boolean do_normals=false;
            boolean do_colors=false;
            boolean do_texcoords=true;



            ArrayList<Integer> v=new ArrayList<Integer>();    

            int t=0;
            while (t<(numberOfBytes)) {
                if (do_vertices) {
                    int a=(int) (unsignedByteToInt(propertyData[t])+ (propertyData[t+1] << 8));
                    v.add(a);
                    t=t+2;
                }
                if (do_normals) {
                    t=t+2;
                }
                if (do_colors) {
                    t=t+2;
                }
                if (do_texcoords) {
                    t=t+2;
                }
            }

            if (v.size()>2) {
                
                Polygon p=new Polygon();

                int a=(int) v.get(0);
                Point pa=btgObject.fileBtg.vertexes.get(a).getMapPoint();
                
                int b=(int) v.get(1);
                    
                if (b!=(int) v.get(v.size()-1)) {
                    p.addPoint(pa.x, pa.y);                
                    btgObject.fileBtg.vertexes.get(a).iBelongTo.add(p);
                }
                
                for (t=1;t<v.size()-1;t++) {
                    b=(int) v.get(t);
                    int c=(int) v.get(t+1);

                    Point pb=btgObject.fileBtg.vertexes.get(b).getMapPoint();
                    Point pc=btgObject.fileBtg.vertexes.get(c).getMapPoint();
                
                    p.addPoint(pb.x, pb.y);
                    btgObject.fileBtg.vertexes.get(b).iBelongTo.add(p);
                    p.addPoint(pc.x, pc.y);
                    btgObject.fileBtg.vertexes.get(c).iBelongTo.add(p);
                }        

                if (p.getBounds().intersects(btgObject.fileBtg.all)) {
                    poligons.add(p);                    

                }
                
                
                
                
            }
            
            //maxToDo--;
        //}
        
        
        //guarda qui: /opt/fgfs/simgear/simgear/simgear/io/sg_binobj.cxx
        
        
        
    }



    
}
