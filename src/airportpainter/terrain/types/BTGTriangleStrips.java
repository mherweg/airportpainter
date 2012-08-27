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
public class BTGTriangleStrips extends BTGObjectElement {
    //public ArrayList<BTGTriangleFace> triangles=new ArrayList<BTGTriangleFace>();    
    
    public BTGTriangleStrips() {
        super.type=BTGObject.TYPE_TRIANGLE_STRIPS;
    }
    
    
    @Override
    public void afterRead() {
        //System.out.println("nbytes:"+numberOfBytes);

        boolean do_vertices=true;
        boolean do_normals=true;
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
            int a=(int) v.get(0);
            int b=(int) v.get(1);
            
            ArrayList<Point> stack=new ArrayList<Point>();
            
            Polygon p=new Polygon();
            
            Point pa=btgObject.fileBtg.vertexes.get(a).getMapPoint();
            //Point pb=btgObject.fileBtg.vertexes.get(b).getMapPoint();
            p.addPoint(pa.x, pa.y);
            //p.addPoint(pb.x, pb.y);
            
            for (t=1;t<v.size();t++) {

                
                int c=(int) v.get(t);
                
                //Point pc=fromVertexIndexToPoint(c); 
                Point pc=btgObject.fileBtg.vertexes.get(c).getMapPoint();
                
                if (t%2 == 0 ) {
                    p.addPoint(pc.x, pc.y);
                } else {
                    stack.add(pc);
                }
                
                
                
                
//                BTGTriangleFace tf=new BTGTriangleFace();
//                tf.vertices[0]=a;
//                tf.vertices[1]=b;
//                tf.vertices[2]=c;
//
//                triangles.add(tf);                                    
                
                //a=b;
                //b=c;
            }        
            

            while (! stack.isEmpty()) {
                Point point=stack.remove(stack.size()-1);
                p.addPoint(point.x,point.y);
                
            }
            
            if (p.getBounds().intersects(btgObject.fileBtg.all)) {
                poligons.add(p);                    
           }
        }
        
        
        
        
        //guarda qui: /opt/fgfs/simgear/simgear/simgear/io/sg_binobj.cxx
        
        
        
    }

}
