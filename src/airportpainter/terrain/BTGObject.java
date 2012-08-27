/*
 * BTGObject.java
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

import airportpainter.terrain.types.BTGBoundingSphere;
import airportpainter.terrain.types.BTGTriangleFaces;
import airportpainter.terrain.types.BTGTriangleFan;
import airportpainter.terrain.types.BTGTriangleStrips;
import airportpainter.terrain.types.BTGVertexList;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author francesco
 */
public class BTGObject {
    
    public static final byte TYPE_BOUNDING_SPHERE=0;
    public static final byte TYPE_VERTEX_LIST=1;
    public static final byte TYPE_COLOR_LIST=4;
    public static final byte TYPE_TRIANGLE_FACES=10;
    public static final byte TYPE_TRIANGLE_STRIPS=11;
    public static final byte TYPE_TRIANGLE_FANS=12;

    public int vertexIndex=0;
    
    
    // header
    byte objectType;
    int numberOfObjectProperties;
    int numberOfObjectElements;
    
    private ArrayList<BTGObjectProperty> objectProperties=new ArrayList<BTGObjectProperty>();
    private ArrayList<BTGObjectElement> objectElements=new ArrayList<BTGObjectElement>();
    
    public BTG fileBtg=null;

    public String materialName=null;
    public int indexType=-1;

    
    public void readFromIn(final DataInputStream in) throws IOException, Exception {
        byte objectType_=in.readByte();
 
        
        // check valid objectType
        if (objectType_>=0 && objectType_<=12) {
            objectType=objectType_;
            if (version>=7) {
                numberOfObjectProperties=BTG.toLittle(in.readUnsignedShort());
                numberOfObjectElements=BTG.toLittle(in.readUnsignedShort());                
            } else {
                numberOfObjectProperties=BTG.toLittle(in.readUnsignedShort());
                numberOfObjectElements=BTG.toLittle(in.readUnsignedShort());                
            }
            
            //System.out.println("OBJ TYPE="+objectType_+" props="+numberOfObjectProperties+" elements="+numberOfObjectElements);
            
            for (int p=0;p<numberOfObjectProperties;p++) {
                BTGObjectProperty bTGObjectProperty=new BTGObjectProperty();
                bTGObjectProperty.readFromIn(in);
                
                if (bTGObjectProperty.materialName!=null) {
                    materialName=bTGObjectProperty.materialName;
                }
                if (bTGObjectProperty.indexType!=-1) {
                    indexType=bTGObjectProperty.indexType;
                }
                
                
                getObjectProperties().add(bTGObjectProperty);                
            }
            
            for (int p=0;p<numberOfObjectElements;p++) {
                BTGObjectElement bTGObjectElement=null;
                switch (objectType) {
                    case TYPE_BOUNDING_SPHERE:
                        bTGObjectElement=new BTGBoundingSphere();                                                
                        break;
                    case TYPE_VERTEX_LIST:
                        bTGObjectElement=new BTGVertexList();                                                
                        break;
                    case TYPE_TRIANGLE_FACES:
                        bTGObjectElement=new BTGTriangleFaces();                        
                        break;
                    case TYPE_TRIANGLE_STRIPS:
                        bTGObjectElement=new BTGTriangleStrips();                        
                        break;
                    case TYPE_TRIANGLE_FANS:
                       bTGObjectElement=new BTGTriangleFan();                        
                        break;
                    default:
                        //System.out.println("unknown:"+objectType);
                        break;
                }
                
                if (bTGObjectElement!=null) {
                    bTGObjectElement.btgObject=this;
                    bTGObjectElement.readFromIn(in);                
                    bTGObjectElement.afterRead();
                    getObjectElements().add(bTGObjectElement);
                } else {
                    // not yet known..
                    bTGObjectElement=new BTGObjectElement();
                    bTGObjectElement.btgObject=this;
                    bTGObjectElement.readFromIn(in);                                    
                }
            }
            
           
           

        } else {
            throw  new Exception("Object type :"+objectType_+" not found !");
        }
        
        
        
    }

    protected int version=0;
    public BTGObject(int version,BTG fileBtg)  {
        this.version=version;
        this.fileBtg=fileBtg;
    }

    /**
     * @return the objectProperties
     */
    public ArrayList<BTGObjectProperty> getObjectProperties() {
        return objectProperties;
    }

    /**
     * @param objectProperties the objectProperties to set
     */
    public void setObjectProperties(ArrayList<BTGObjectProperty> objectProperties) {
        this.objectProperties = objectProperties;
    }

    /**
     * @return the objectElements
     */
    public ArrayList<BTGObjectElement> getObjectElements() {
        return objectElements;
    }

    /**
     * @param objectElements the objectElements to set
     */
    public void setObjectElements(ArrayList<BTGObjectElement> objectElements) {
        this.objectElements = objectElements;
    }
}


class BTGObjectProperty {
    byte propertyType;
    long numberOfBytesInPropertyData;
    byte propertyData[];
    
    String materialName=null;
    int indexType=-1;
    
    public void readFromIn(final DataInputStream in) throws IOException, Exception {
        byte propertyType_=in.readByte();
 
        // check valid objectType
        if (propertyType_>=0 && propertyType_<=1) {
            propertyType=propertyType_;
            
            numberOfBytesInPropertyData=BTG.toLittleInt(in.readInt());
            propertyData=new byte[(int) numberOfBytesInPropertyData];
            for (int p=0;p<numberOfBytesInPropertyData;p++) {
                propertyData[p]=in.readByte();
            }
            if (propertyType==0) {
                // material
                materialName=new String(propertyData);
            }
            if (propertyType==1) {
                // material
                indexType=propertyData[0];
            }
        } else {
            throw  new Exception("Property type :"+propertyType_+" not found !");
        }
        
        
        
    }
    
}

