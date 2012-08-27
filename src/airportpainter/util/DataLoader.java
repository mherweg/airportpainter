/*
 * DataLoader.java
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
package airportpainter.util;

import airportpainter.APConfiguration;
import airportpainter.Airport;
import airportpainter.earth.Earth;
import airportpainter.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A Helper class for controlling the loading of all the various data files.
 */
public class DataLoader {

	//protected IPreferenceStore _prefs = FGFPApplication.getInstance().getPreferenceStore();
	
	/**
	 * 
	 */
	public DataLoader() {
		super();
	}

        


	/**
	 * Loads the Airport database into memory. This method also passes the
	 * loaded list into the AirportView
	 * 
	 * @param monitor
	 * 
	 * @throws IOException
	 */
	public void loadAirports() {
		final List<Airport> airports = new ArrayList<Airport>(5000);
		try {

                    final AirportParserFG airportParser = new AirportParserFG();
                    airportParser.parse(airports, APConfiguration.aptPath, APConfiguration.navPath);



		} catch (final Exception e) {
		    Logger.logException(e, Logger.Realm.AIRPORT);
		}
		
		Earth.setAirports(airports);
		
		/*Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final AirportView view = (AirportView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
						AirportView.ID_VIEW);
				view.setContent(airports);
			    // Get the map view to redraw
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final MapView view2 = (MapView) page.findView(MapView.ID_VIEW);
				if (view2 != null) {
					view2.redraw();
				}

			}
		});*/
		
	}

	/**
	 * Loads the Airport database into memory. This method also passes the
	 * loaded list into the AirportView
	 * 
	 * @param monitor
	 * 
	 * @throws IOException
	 */
	public void loadRunways(final Airport airport) {
		try {
                        final AirportParserFG airportParser = new AirportParserFG();
                        airportParser.loadRunways(airport, APConfiguration.aptPath, APConfiguration.navPath);
			
		} catch (final Exception e) {
		    Logger.logException(e, Logger.Realm.AIRPORT);
		}
	}

	/**
	 * Loads the NavAid database into memory. This method also passes the loaded
	 * list into the NavAidView
	 * 
	 * @param monitor
	 * 
	 * @throws IOException
	 */
	public void loadNavAids() {
		Earth.getNavAids().clear();
		try {
                    String path;
                    final NavAidParserFG navAidParser = new NavAidParserFG();
                    navAidParser.parse(Earth.getNavAids(), APConfiguration.navPath);
		} catch (final Exception e) {
			Logger.logException(e, Logger.Realm.GENERIC);
		}
		
		
	}
        
	/**
	 * Loads the Fix database into memory. 
	 * 
	 * @param monitor
	 * 
	 * @throws IOException
	 */
	/*public void loadFixes(final IProgressMonitor monitor) {
		Earth.getFixes().clear();
		try {
			if (_prefs.getString(IPreferenceConstants.NAVIGATION_DATA).equals("fgfs")) { //$NON-NLS-1$
				String path;
				if (_prefs.getBoolean(IPreferenceConstants.FGFS_USE_DFT_PATH)) {
					path = _prefs.getString(IPreferenceConstants.FGFS_DIR) + "/Navaids/fix.dat.gz"; //$NON-NLS-1$
				} else {
					path = _prefs.getString(IPreferenceConstants.FIXES_PATH);
				}
				final FixParserFG fixParser = new FixParserFG();
				fixParser.parse(Earth.getFixes(), path, monitor);
			} 
		} catch (final Exception e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					final MessageBox dialog = new MessageBox(null, SWT.OK | SWT.ICON_ERROR);
					dialog.setMessage(Messages.getString("FlightPlanner.9") + //$NON-NLS-1$
							Messages.getString("FlightPlanner.10") + //$NON-NLS-1$
							Messages.getString("FlightPlanner.11")); //$NON-NLS-1$
					dialog.setText(Messages.getString("FlightPlanner.12")); //$NON-NLS-1$
					dialog.open();
				}
			});
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final FixView view = (FixView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
						FixView.ID_VIEW);
        		view.setContent(Earth.getFixes());
			}
		});
	}*/


	/**
	 * Loads the Atises database into memory. This method also passes the loaded
	 *
	 * @throws IOException
	 */
	public void loadAtises() {
		Earth.getAtises().clear();
		try {
                    final AtisParser atisParser = new AtisParser();
                    atisParser.parse(Earth.getAtises(), APConfiguration.atisPath);
		} catch (final Exception e) {
                    Logger.logException(e, Logger.Realm.GENERIC);
		}


	}


        public int loadAll() {
            if (new File(APConfiguration.aptPath).exists()) {
                Logger.log("Loading:"+APConfiguration.aptPath, Logger.Level.DEBUG, Logger.Realm.GENERIC);
                loadAirports();
            }

            if (new File(APConfiguration.navPath).exists()) {
                Logger.log("Loading:"+APConfiguration.navPath, Logger.Level.DEBUG, Logger.Realm.GENERIC);
                loadNavAids();
            }

            if (new File(APConfiguration.atisPath).exists()) {
                Logger.log("Loading:"+APConfiguration.atisPath, Logger.Level.DEBUG, Logger.Realm.GENERIC);
                loadAtises();
            }

            return 1;
        }
}
