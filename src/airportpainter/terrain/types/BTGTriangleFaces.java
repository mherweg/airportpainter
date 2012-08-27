/*
 * BTGTriangleFaces.java
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
 */package airportpainter.terrain.types;

import airportpainter.terrain.BTGObject;
import airportpainter.terrain.BTGObjectElement;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 *
 * @author francesco
 */
public class BTGTriangleFaces extends BTGObjectElement {
    //public ArrayList<BTGTriangleFace> triangles=new ArrayList<BTGTriangleFace>();    
    
    
    public BTGTriangleFaces() {
        super.type=BTGObject.TYPE_TRIANGLE_FACES;
    }
    
    
    @Override
    public void afterRead() {
        //System.out.println("nbytes:"+numberOfBytes);

        boolean do_vertices=true;
        boolean do_normals=true;
        boolean do_colors=false;
        boolean do_texcoords=true;

        ArrayList <Integer> v=new ArrayList<Integer>();    
        
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
        
        for (t=0;t<(v.size()/3);t++) {

//            BTGTriangleFace tf=new BTGTriangleFace();
//            tf.vertices[0]=(int) v.get(t*3);
//            tf.vertices[1]=(int) v.get(t*3+1);
//            tf.vertices[2]=(int) v.get(t*3+2);
//
//            triangles.add(tf);                                    

            //un triangolo !
            Polygon p=new Polygon();

            //System.out.println(o.fileBtg.parsedFile);
            //Point pa=fromVertexIndexToPoint(v.get(t*3)); 
            //Point pb=fromVertexIndexToPoint(v.get(t*3+1)); 
            //Point pc=fromVertexIndexToPoint(v.get(t*3+2)); 

            Point pa=btgObject.fileBtg.vertexes.get(v.get(t*3)).getMapPoint();
            Point pb=btgObject.fileBtg.vertexes.get(v.get(t*3+1)).getMapPoint();
            Point pc=btgObject.fileBtg.vertexes.get(v.get(t*3+1)).getMapPoint();
            
            p.addPoint(pa.x,pa.y);
            p.addPoint(pb.x,pb.y);
            p.addPoint(pc.x,pc.y);

            if (p.getBounds().intersects(btgObject.fileBtg.all)) {
                poligons.add(p);                    

           }
        }
        
        
        //guarda qui: /opt/fgfs/simgear/simgear/simgear/io/sg_binobj.cxx
        
        
        
    }
 
}
