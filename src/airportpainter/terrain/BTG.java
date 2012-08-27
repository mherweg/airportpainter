/*
 * BTG.java
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

import airportpainter.APConfiguration;
import airportpainter.earth.Coordinate;
import airportpainter.logging.Logger;
import airportpainter.logging.Logger.Realm;
import airportpainter.terrain.types.BTGBoundingSphere;
import airportpainter.terrain.types.BTGVertex;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author francesco
 */
public class BTG {
    
    public BTGBoundingSphere boundingSphere=null;
    
    public ArrayList<BTGVertex> vertexes=new ArrayList<BTGVertex>();
    public ArrayList<Color> colors=new ArrayList<Color>();

    Coordinate upperLeft=null;
    Coordinate lowerRight=null;
    int width=520;
    int height=width;
    
    public Rectangle all=null;
    
   public static double bytesToDoubleLittleEndian( byte i[]) {
        // get the 8 unsigned raw bytes, accumulate to a long and then
        // convert the 64-bit pattern to a double.
        long accum = 0;
        for ( int shiftBy=0; shiftBy<64; shiftBy+=8 ) {
        // must cast to long or the shift would be done modulo 32
            accum |= ( (long)( i[shiftBy/8] & 0xff ) ) << shiftBy;
        }
        return Double.longBitsToDouble( accum );

        // there is no such method as Double.reverseBytes( d );
    }
   
    public static float bytesToFloatLittleEndian(byte i[]) {
        // get 4 unsigned raw byte components, accumulating to an int,
        // then convert the 32-bit pattern to a float.
        int accum = 0;
        for ( int shiftBy=0; shiftBy<32; shiftBy+=8 ) {
            accum |= ( i[shiftBy/8] & 0xff ) << shiftBy;
        }
        return Float.intBitsToFloat( accum );

        // there is no such method as Float.reverseBytes( f );
    }   
    
    public static int toLittle(int i) {
        return (int) (((i & 0xff) << 8)+ (i >>> 8));
    }
    public static long toLittleInt(int i) {        
        //System.out.println(Integer.toHexString( i ));
        //System.out.println(Integer.toHexString( (((i & 0xff) << 24) +((i & 0xff00) << 8)+((i & 0xff0000) >> 8)+((i & 0xff000000) >>24 )) ));        
        return (((i & 0xff) << 24) +((i & 0xff00) << 8)+((i & 0xff0000) >> 8)+(long) ((i & 0xff000000) >>>24 ));
    }
    
    /**
     * Parse the airport file and load into the Airport Collection
     *
     * @param monitor
     * @param map
     * @param string
     */
    public void loadObjects(final ArrayList<BTGObject> airports, final DataInputStream in,final String path)
    throws IOException {

        // first of all read header of file
        int v=toLittle(in.readShort());
        //System.out.println(v);
        
        int magicNumber=toLittle(in.readUnsignedShort());
        //System.out.println(Integer.toHexString( magicNumber ));        

        long creationTime=toLittleInt(in.readInt());
        //Date d=new Date(creationTime*1000);        
        //System.out.println(d);        
        
        int numberOfTopLevelObjects=toLittle(in.readUnsignedShort());
        //System.out.println("numberOfTopLevelObjects="+numberOfTopLevelObjects);        
        
        for (int o=0;o<numberOfTopLevelObjects;o++) {
            BTGObject btgObject = new BTGObject(v,this);
            try {
                btgObject.readFromIn(in);
                airports.add(btgObject);                
                
                if (btgObject.objectType==BTGObject.TYPE_BOUNDING_SPHERE) {
                    boundingSphere=(BTGBoundingSphere) btgObject.getObjectElements().get(
                        btgObject.getObjectElements().size()-1
                    );// I keep only the last one
                }
                
            } catch (Exception ex) {
                Logger.logException(ex, Realm.MAP);
            }
            
        }
    }

    
    public String parsedFile="";
    public ArrayList<BTGObject> parseBTGFile(String path) {
        Logger.log("Parsing:"+path, Logger.Level.DEBUG,Realm.MAP);
        ArrayList<BTGObject>  objects=new ArrayList<BTGObject>();

        try {

            // Open the airports file
            DataInputStream rdrAirport;
            try {
                rdrAirport = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(path))));
            } catch (final IOException ioex) {
                // Try the file in non-zipped version
                // in some distributions = the Flightgear files have not
                // actually been zipped.
                rdrAirport = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
            }

            //try {
                //rdrAirport.mark(2000);
                //rdrAirport.reset();
            loadObjects(objects,rdrAirport,path);
            //} finally {
            rdrAirport.close();
            //}
        } catch (FileNotFoundException fnf) {
            Logger.logException(fnf, Realm.MAP);
        } catch (IOException ioe) {
            Logger.logException(ioe, Realm.MAP);
        }

        return objects;


    }
    
    public static ArrayList<BTGObject> loadAllTerrainIn(
            Coordinate upperLeft,
            Coordinate lowerRight,
            int width,
            int height
        ) {        
            double _minLongitude=upperLeft.getLongitude();
            double _minLatitude=upperLeft.getLatitude();
            double _maxLongitude=lowerRight.getLongitude();
            double _maxLatitude=lowerRight.getLatitude();
        
            HashMap<String,BTG> btgFilesLoaded=new HashMap<String,BTG>();
        
            // first I need to know the list of hangars
            ArrayList<BTGObject> objects=new ArrayList<BTGObject>();
            // for each scenery folder.....
            for (String path : APConfiguration.sceneryPath) {

                 // search in folder
                 String subpath=path+"/Terrain";

                 int minLongitudine=(int) (_minLongitude)-2;
                 int minLatitude=(int) (_minLatitude)-2;

                 int maxLongitudine=(int) (_maxLongitude)+2;
                 int maxLatitude=(int) (_maxLatitude)+2;

                 for (int x=minLongitudine;x<=maxLongitudine;x++) {
                     for (int y=minLatitude;y<=maxLatitude;y++) {


                        int xPos=Math.abs(x);
                        int yPos=Math.abs(y);


                        // decade
                        String woe="e";
                        if (x<0) {
                            woe="w";
                        }

                        int xD=(x/10)*10;
                        while (xD>x) {
                            xD=xD-10;
                        }
                        String xDS=new Integer(Math.abs(xD)).toString();

                        // format xDS like 000
                        while (xDS.length()<3) {
                            xDS="0"+xDS;
                        }


                        String son="n";
                        if (y<0) {
                            son="s";
                        }

                        int yD=(y/10)*10;
                        while (yD>y) {
                            yD=yD-10;
                        }
                        String yDS=new Integer(Math.abs(yD)).toString();


                        String xS=new Integer(xPos).toString();
                        // format xS like 000
                        while (xS.length()<3) {
                            xS="0"+xS;
                        }

                        
                        String yS=new Integer(yPos).toString();

                        String subpath2=woe+xDS+son+yDS+"/"+woe+xS+son+yS;
                        String subpath3=subpath+"/"+subpath2;

                        String[] fileNames;
                        File curDir=new File(subpath3);
                        fileNames=curDir.list();
                        if (fileNames!=null) {
                            File mioFile;
                            for(int t=0;t<fileNames.length;t++) {
                                mioFile=new File(curDir,fileNames[t]);
                                if ((! fileNames[t].equals(".."))) {
                                    if (
                                        fileNames[t].endsWith(".btg") ||
                                        fileNames[t].endsWith(".btg.gz")
                                    ) {

                                        //System.out.println(fileNames[t]);

                                        if (! btgFilesLoaded.containsKey( subpath2+"/"+fileNames[t] )) {
                                            BTG btg=new BTG();
                                            btg.upperLeft=upperLeft;
                                            btg.lowerRight=lowerRight;
                                            btg.width=width;
                                            btg.height=height;

                                            btg.all=new Rectangle(
                                                width,height
                                            );  
                                            
                                            objects.addAll(
                                                btg.parseBTGFile(
                                                    subpath3+"/"+fileNames[t]
                                                )
                                            );
                                            btgFilesLoaded.put(subpath2+"/"+fileNames[t], btg);
                                        }
                                        
                                        
                                    }
                                }
                            }

                        }


                     }
                 }

             }        
            return objects;
    }
}
