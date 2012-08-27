/*
 * Main.java
 *
 * Copyright (C) 2009 Francesco Brisa
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

package airportpainter;

import airportpainter.earth.Earth;
import airportpainter.logging.Logger;
import airportpainter.util.AirportPainter;
import airportpainter.util.AutoPlanner;
import airportpainter.util.DataLoader;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jcmdline.*;

/**
 *
 * @author francesco
 */
public class Main {

    private static PropertyChangeSupport propertyChangeSupport;

    public static DataLoader loader = new DataLoader();

    public static String imgFolder="/imgs/";
    
   /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        String airportID="KSFO";
        double range=0;
        String backgroundColor="FFFFFF";
        








        StringParam fg_root_arg=new StringParam(
            "fg-root",
            "Flight gear data root folder",
            StringParam.OPTIONAL
        );

        StringParam airportID_arg=new StringParam(
            "airport",
            "The ICAO code of the airport we want to draw",
            StringParam.OPTIONAL
        );

        StringParam outputPng_arg=new StringParam(
            "output",
            "The filename or the directoryof the pdf to generate",
            StringParam.OPTIONAL
        );

        StringParam range_arg=new StringParam(
            "range",
            "don't do just the airport you selected, but all the airport within that range",
            StringParam.OPTIONAL
        );

        StringParam bgcolor_arg=new StringParam(
            "bg-color",
            "rgb (hex) background color, ex: for white: FFFFFF",
            StringParam.OPTIONAL
        );


        StringParam fg_scenery_arg=new StringParam(
            "fg-scenery",
            "Flight gear additional scenery folder",
            StringParam.OPTIONAL
        );

        /*BooleanParam keepPng_arg=new BooleanParam(
            "keepPng",
            "do not delete png files",
            BooleanParam.OPTIONAL
        );*/

        BooleanParam drawObjectsInAirport_arg=new BooleanParam(
            "drawObjectsInAirport",
            "draw Shared Objects in Airport page (Hangars etc...)",
            BooleanParam.OPTIONAL
        );
        BooleanParam drawObjectsInMap_arg=new BooleanParam(
            "drawObjectsInMap",
            "draw Shared Objects in Map page (pylons etc...)",
            BooleanParam.OPTIONAL
        );
        BooleanParam drawTerrainInMap_arg=new BooleanParam(
            "drawTerrainInMap",
            "draw terrain in Map page (grass, ocean, urban, etc...)",
            BooleanParam.OPTIONAL
        );
        BooleanParam longFileName_arg=new BooleanParam(
            "longFileName",
            "Adds airport name to output file",
            BooleanParam.OPTIONAL
        );


        // command line options
        BooleanParam ignorecaseOpt =new BooleanParam("ignoreCase", "ignore case while matching");


        // a help text because we will use a HelpCmdLineHandler so our
        // command will display verbose help with the -help option
        String helpText =
            "This program creates images of airports using the flightGear data only\n\n" +
            ""
        ;


        CmdLineHandler cl = new VersionCmdLineHandler(
            "V 0.1",
            new HelpCmdLineHandler(
                helpText,
                "airportPainter",
                "create pictures of airports.",
                new Parameter[] { fg_root_arg,airportID_arg,outputPng_arg,range_arg,bgcolor_arg,fg_scenery_arg,drawObjectsInAirport_arg,drawObjectsInMap_arg,drawTerrainInMap_arg,longFileName_arg},
                new Parameter[] { ignorecaseOpt}
            )
        );

        cl.parse(args);




        File pf=new File(System.getProperty("user.home")+"/.appPainter.xml");
        if (
            pf.exists()
        ) {
            // Load properties file.
            java.util.Properties properties = new java.util.Properties();
            try {
                properties.loadFromXML(new FileInputStream(pf));

                APConfiguration.setFgfsDataPath(properties.getProperty("fgroot"));
                APConfiguration.outputFolder= properties.getProperty("output_folder");

                if (APConfiguration.outputFolder==null) {
                    APConfiguration.outputFolder=".";
                }

                APConfiguration.drawObjectsInAirport= properties.getProperty("drawObjectsInAirport","false").equals("true");
                APConfiguration.drawObjectsInMap= properties.getProperty("drawObjectsInMap","false").equals("true");
                APConfiguration.drawTerrainInMap= properties.getProperty("drawTerrainInMap","false").equals("true");


                String paths[]=properties.getProperty("sceneries","").split(",");
                
                for (int t=0;t<paths.length;t++) {
                    APConfiguration.addFgfsSceneryPath(paths[t]);
                }





                airportID= properties.getProperty("airport_id","KSFO");


            } catch (IOException e) {
            }




        }




        //*****************************************
        // Here I have all args
        //*****************************************
        if (fg_root_arg.isSet()) {
            APConfiguration.setFgfsDataPath(fg_root_arg.getValue());
        }

        if (airportID_arg.isSet()) {
            airportID=airportID_arg.getValue();
        }

        if (range_arg.isSet()) {
            range=new Double(range_arg.getValue()).doubleValue();
        }
        if (bgcolor_arg.isSet()) {
            backgroundColor=bgcolor_arg.getValue();
        }

        Logger.log("FG_ROOT:"+APConfiguration.getFgfsDataPath());
        Logger.log("AIRPORT:"+airportID);        
        if (range>0) {
            Logger.log("RANGE:  "+new Double(range).toString()+" nm.");
        }

        if (outputPng_arg.isSet()) {
            APConfiguration.outputFolder=outputPng_arg.getValue();
        }
        /*if (keepPng_arg.isSet()) {
            keepPng=keepPng_arg.isTrue();
        }*/

        
        Iterator i=fg_scenery_arg.getValues().iterator();
        while (i.hasNext()) {
            String  path=(String) i.next();
            APConfiguration.addFgfsSceneryPath(path);
        }




        if (drawObjectsInAirport_arg.isSet()) {
            APConfiguration.drawObjectsInAirport=drawObjectsInAirport_arg.isTrue();
        }
        if (drawObjectsInMap_arg.isSet()) {
            APConfiguration.drawObjectsInMap=drawObjectsInMap_arg.isTrue();
        }
        if (drawTerrainInMap_arg.isSet()) {
            APConfiguration.drawObjectsInMap=drawTerrainInMap_arg.isTrue();
        }

        if (longFileName_arg.isSet()) {
            APConfiguration.longFileName=longFileName_arg.isTrue();
        }

        final String aid=airportID;
        if (!airportID_arg.isSet()) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Appainter app=new Appainter();
                    app.setVisible(true);
                    if (! aid.equals("")) {
                        app.setAirportID(aid);
                    }
                    app.setSceneryPaths(APConfiguration.sceneryPath);
                }
            });
        } else {

            loader.loadAll();
            generate(airportID,range,APConfiguration.outputFolder,backgroundColor,APConfiguration.drawObjectsInAirport,APConfiguration.drawObjectsInMap,APConfiguration.drawTerrainInMap,APConfiguration.longFileName);
            
        }

    }

    public static boolean generate(
        String airportID,
        double range,
        String outfolder,
        String backgroundColor,
        boolean drawObjectsInAirport,
        boolean drawObjectsInMap,
        boolean drawTerrainInMap,
        boolean longFileName
    ) {
        Airport airport=Earth.getAirportById(airportID);
        final List<Airport> airportListToDo = new ArrayList<Airport>();

        if (airport!=null) {
            airportListToDo.add(airport);
            if (range>0) {

                getPropertyChangeSupport().firePropertyChange("text", "","Searching airports ...");

                final AutoPlanner planner = new AutoPlanner(Earth.getNavAids());
                planner.setAirports(Earth.getAirports());
                //final NavAid nearest = planner.getNavaidNearest(airport.getLoc(), 10d);
                airportListToDo.addAll(planner.getAirportNear(airport.getLoc(), range));

            } else {

            }

            String outFile;
            getPropertyChangeSupport().firePropertyChange("max", 0,airportListToDo.size() );

            int n=0;
            for (Airport myAirport: airportListToDo) {

                outFile=outfolder+"/"+myAirport.getId();
                
                if (longFileName) {
                    outFile=outFile+" - "+myAirport.getName();
                }

                getPropertyChangeSupport().firePropertyChange("text", "","Rendering: "+myAirport.getId()+" "+myAirport.getName() );

                AirportPainter.writeAirportImage(myAirport,outFile,backgroundColor,drawObjectsInAirport,drawObjectsInMap,drawTerrainInMap);

                getPropertyChangeSupport().firePropertyChange("value", -1,n );
                n++;                
            }

            getPropertyChangeSupport().firePropertyChange("finished", null,"");
            return true;

        } else {
            getPropertyChangeSupport().firePropertyChange("text", "-","ERROR: Airport not found" );
            Logger.log("ERROR: Airport not found", Logger.Level.ERROR);
            
            getPropertyChangeSupport().firePropertyChange("finished", null,"");
            return false;
        }
        
        
    }


    public static URL getFileFromResource(String resourceLocation) throws URISyntaxException {
        try {
            return Main.class.getResource(resourceLocation).toURI().toURL();            
        } catch(NullPointerException e) {
            Logger.logException(e,Logger.Realm.GENERIC);
            return null;
        } catch (MalformedURLException ex) {
            Logger.logException(ex,Logger.Realm.GENERIC);
            return null;
        }


    }

    /**
     * @return the propertyChangeSupport
     */
    public static PropertyChangeSupport getPropertyChangeSupport() {
        if (propertyChangeSupport==null) {
            propertyChangeSupport=new PropertyChangeSupport(Main.class);
        }
        return propertyChangeSupport;
    }

    /**
     * @param aPropertyChangeSupport the propertyChangeSupport to set
     */
    public static void setPropertyChangeSupport(PropertyChangeSupport aPropertyChangeSupport) {
        propertyChangeSupport = aPropertyChangeSupport;
    }
    
}
