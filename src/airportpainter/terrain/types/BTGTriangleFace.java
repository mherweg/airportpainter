/*
 * BTGTriangleFace.java
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

/**
 *
 * @author francesco
 */
public class BTGTriangleFace extends BTGObjectElement {
    //guarda qui: /opt/fgfs/simgear/simgear/simgear/io/sg_binobj.cxx


    public int vertices[]={0,0,0};
    
    public BTGTriangleFace() {
        super.type=BTGObject.TYPE_TRIANGLE_FACES;
    }
        
    @Override
    public String toString() {
        return "TriangleFace {v: "+vertices[0]+","+vertices[1]+","+vertices[2]+"}";
    }
}
