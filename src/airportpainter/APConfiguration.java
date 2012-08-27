/*
 * APConfiguration.java
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

import airportpainter.logging.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author francesco
 */
public class APConfiguration {

    private static String fgfsDataPath="";

    public static String outputFolder="./";

    public static String aptPath="apt.dat.gz";
    public static String navPath="nav.dat.gz";
    public static String atisPath="default.atis";

    public static List<String> sceneryPath=new ArrayList<String>();

    public static boolean drawObjectsInAirport=true;
    public static boolean drawObjectsInMap=false;
    public static boolean drawTerrainInMap=false;
    public static boolean longFileName=false;


    public APConfiguration() {

    }

    /**
     * @return the fgfsDataPath
     */
    public static String getFgfsDataPath() {
        return fgfsDataPath;
    }

    /**
     * @param fgfsDataPath the fgfsDataPath to set
     */
    public static void setFgfsDataPath(String fgfsDataPath_) {
        fgfsDataPath = fgfsDataPath_;
        aptPath=fgfsDataPath+"/Airports/apt.dat.gz";
        navPath=fgfsDataPath+"/Navaids/nav.dat.gz";
        atisPath=fgfsDataPath+"/ATC/default.atis";

        addFgfsSceneryPath(fgfsDataPath+"/Scenery");
    }

    public static void addFgfsSceneryPath(String sceneryPAth) {
        if (! sceneryPAth.equals("")) {
            if (! sceneryPath.contains(sceneryPAth)) {
                Logger.log("Adding scenery path:"+sceneryPAth, Logger.Level.DEBUG,Logger.Realm.GENERIC);
                sceneryPath.add(sceneryPAth);
            }    
        }
    }
}
