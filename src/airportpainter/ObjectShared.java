/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package airportpainter;
import airportpainter.earth.Coordinate;
import airportpainter.logging.Logger;
import airportpainter.logging.Logger.Level;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author francesco
 */
public class ObjectShared extends Location implements Serializable {

        private String  _stgFile;
        private double  _rotation;

        private SharedObjectsSvg sos=null;

	public ObjectShared() {

        }




    /**
     * Parse the airport file and load into the Airport Collection
     *
     * @param monitor
     * @param map
     * @param string
     */
    public static void loadObjects(final List<ObjectShared> airports, final BufferedReader in,final String path)
    throws IOException {

        StringTokenizer tokenizer;

        
        int airportCount = 0;
        long offset = 0;

        String buf = in.readLine();
        try {
            while (true) {
                Thread.yield();
                if (buf == null) {
                    break;
                }
                if (buf.length() > 0) {
                    tokenizer = new StringTokenizer(buf);
                    String rShared = tokenizer.nextToken();
                    String rType = tokenizer.nextToken();
                    {
                        final double r_long = new Double(tokenizer.nextToken()).doubleValue();
                        final double r_lat = new Double(tokenizer.nextToken()).doubleValue();
                        final double r_elev = new Double(tokenizer.nextToken()).doubleValue();
                        final double r_rot = new Double(tokenizer.nextToken()).doubleValue();


                        ObjectShared airport = new ObjectShared();
                        airport._elevation=r_elev;
                        airport._id=rShared;
                        airport._loc=new Coordinate(r_lat,r_long);
                        airport._name=rType;
                        airport.setRotation(r_rot);
                        airport.setStgFile(path);

                        airports.add(airport);

                        offset += buf.length() + 1;
                        buf = in.readLine();
                    }
                } else {
                    offset += buf.length() + 1;
                    buf = in.readLine();
                }
            }
        } catch (final EOFException e) {
        } catch (final Exception e) {
        }

    }




        public static List<ObjectShared> parseSTGFile(String path) {
            ArrayList<ObjectShared>  objects=new ArrayList<ObjectShared>();

            try {

                // Open the airports file
                BufferedReader rdrAirport;
                try {
                    rdrAirport = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
                } catch (final IOException ioex) {
                    // Try the file in non-zipped version
                    // in some distributions = the Flightgear files have not
                    // actually been zipped.
                    rdrAirport = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                }

                try {
                    //rdrAirport.mark(2000);
                    //rdrAirport.reset();
                    loadObjects(objects,rdrAirport,path);
                } finally {
                    rdrAirport.close();
                }
            } catch (FileNotFoundException fnf) {

            } catch (IOException ioe) {

            }

            return objects;


        }



        public static List<ObjectShared> loadAllObjectsIn(
            double _minLongitude,
            double _minLatitude,
            double _maxLongitude,
            double _maxLatitude
        ) {
            // first I need to know the list of hangars
             ArrayList<ObjectShared> objects=new ArrayList<ObjectShared>();

             // for each scenery folder.....
             for (String path : APConfiguration.sceneryPath) {

                 // search in folder
                 String subpath=path+"/Objects";

                 int minLongitudine=(int) (_minLongitude)-1;
                 int minLatitude=(int) (_minLatitude)-1;

                 int maxLongitudine=(int) (_maxLongitude)+1;
                 int maxLatitude=(int) (_maxLatitude)+1;

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

                        String subpath2=subpath+"/"+woe+xDS+son+yDS;

                        String subpath3=subpath2+"/"+woe+xS+son+yS;

                        String[] fileNames;
                        File curDir=new File(subpath3);
                        fileNames=curDir.list();
                        if (fileNames!=null) {
                            File mioFile;
                            for(int t=0;t<fileNames.length;t++) {
                                mioFile=new File(curDir,fileNames[t]);
                                if ((! fileNames[t].equals(".."))) {
                                    if (fileNames[t].endsWith(".stg")) {

                                        Logger.log(fileNames[t],Level.DEBUG);

                                        objects.addAll(ObjectShared.parseSTGFile(subpath3+"/"+fileNames[t]));
                                    }
                                }
                            }

                        }


                     }
                 }

             }

             return objects;
        }

    /**
     * @return the _stgFile
     */
    public String  getStgFile() {
        return _stgFile;
    }

    /**
     * @param stgFile the _stgFile to set
     */
    public void setStgFile(String  stgFile) {
        this._stgFile = stgFile;
    }

    @Override
    public String toString() {
        return super.toString()+" "+getStgFile();
    }

    /**
     * @return the sos
     */
    public SharedObjectsSvg getSos() {
        if (sos==null) {
            sos=SharedObjectsSvg.getFromModelOrDefault(_name);
        }

        return sos;
    }

    /**
     * @param sos the sos to set
     */
    public void setSos(SharedObjectsSvg sos) {
        this.sos = sos;
    }

    /**
     * @return the _rotation
     */
    public double getRotation() {
        return _rotation;
    }

    /**
     * @param rotation the _rotation to set
     */
    public void setRotation(double rotation) {
        this._rotation = rotation;
    }

}
