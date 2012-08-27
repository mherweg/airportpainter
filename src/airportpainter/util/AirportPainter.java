/*
 * AirportPainter.java
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

import airportpainter.*;
import airportpainter.earth.Earth;
import airportpainter.magvar.CoreMag;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * This class draws an airport on a supplied GC.
 */
public class AirportPainter {
	private Airport _airport;
	private boolean _drawCompassRose = true;
	private boolean _drawRunwayList = true;
	private boolean _drawRunways = true;
	private boolean _drawTaxiways = true;

	private double[] _extents;

	public final static int FEET_PER_DEGREE = 6076 * 60;
        public final static double FEET_PER_METER = (1/0.3048);

        static String backgroundColor="FFFFFF";


        static String degrees="\u00b0";

        static float borderWidth=1.5f;
        static float borderWidthThin=0.6f;

        int fontMoltiplier=1;

        static Graphics2D g2d=null;

        static int width=0;
        static int height=0;

        static float height1=0;


	/**
	 * 
	 */
	public AirportPainter(final Airport airport) {
		_airport = airport;
		// Calculate all the parameters we need to paint the airport
		_extents = calcAirportExtent(_airport);
	}

	/**
	 * This method works out the actual extent of the airport 
	 * in Latitude and longitude.
	 * For these calculations and at this scale we assume the world is flat
	 */
	private double[] calcAirportExtent(final Airport airport) {
		// Start of by assuming just the airport with no runways or taxiways
		double _minLat = airport.getLat();
		double _maxLat = airport.getLat();
		double _minLong = airport.getLong();
		double _maxLong = airport.getLong();

		// for each runway get its extent
		for (final Runway runway : airport.getRunways()) {
			final double[] extent = calcRunwayExtent(runway);
			if (extent[0] < _minLat) {
				_minLat = extent[0];
			}
			if (extent[1] > _maxLat) {
				_maxLat = extent[1];
			}
			if (extent[2] < _minLong) {
				_minLong = extent[2];
			}
			if (extent[3] > _maxLong) {
				_maxLong = extent[3];
			}
		}

		// for each taxiway get its extent
		for (final Taxiway taxiway : airport.getTaxiways()) {
			List<LayoutNode> nodes = taxiway.getNodes();
			for (LayoutNode node : nodes) {
				
				if (node.getLat() < _minLat) {
					_minLat = node.getLat();
				}
				if (node.getLat() > _maxLat) {
					_maxLat = node.getLat();
				}
				if (node.getLong() < _minLong) {
					_minLong = node.getLong();
				}
				if (node.getLong() > _maxLong) {
					_maxLong = node.getLong();
				}
			}
		}

		return new double[] { _minLat, _minLong, _maxLat, _maxLong };
	}

	/**
	 * Calculate the extent of a runway or taxiway
	 * @param runway
	 * @return
	 */
	private double[] calcExtent(final double lat, final double lon, final double heading, final int length, final int width) {
		final double[] extent = new double[4];

		final double headingRadians = Math.toRadians(heading);

		// Corner offset component contributed by the runway length
		double feetEastL = length / 2 * Math.sin(headingRadians);
		double feetNorthL = length / 2 * Math.cos(headingRadians);

		// Corner offset component contributed by the runway width
		final double feetEastW = width / 2 * Math.cos(headingRadians);
		final double feetNorthW = width / 2 * Math.sin(headingRadians);

		// Get the corner offsets (Corners A,B,C,D)
		final double ALat = feetNorthL + feetNorthW;
		final double ALong = feetEastL - feetEastW;

		final double BLat = feetNorthL - feetNorthW;
		final double BLong = feetEastL + feetEastW;

		final double CLat = -feetNorthL - feetNorthW;
		final double CLong = -feetEastL + feetEastW;

		final double DLat = -feetNorthL + feetNorthW;
		final double DLong = -feetEastL - feetEastW;

		// Calculate the min and max lat and long
		// This is not obvious because any of the values could be negative
		final double maxLat = Math.max(Math.max(ALat, BLat), Math.max(CLat, DLat));
		final double minLat = Math.min(Math.min(ALat, BLat), Math.min(CLat, DLat));
		final double maxLong = Math.max(Math.max(ALong, BLong), Math.max(CLong, DLong));
		final double minLong = Math.min(Math.min(ALong, BLong), Math.min(CLong, DLong));

		// Convert these back to degrees lat and long and offset from centre

		extent[0] = lat + (minLat / FEET_PER_DEGREE);
		extent[1] = lat + (maxLat / FEET_PER_DEGREE);
		extent[2] = lon + (minLong / (FEET_PER_DEGREE * Math.cos(Math.toRadians(lat))));
		extent[3] = lon + (maxLong / (FEET_PER_DEGREE * Math.cos(Math.toRadians(lat))));

		return extent;
	}

	/**
	 * Calculate the extent of a runway
	 * @param runway
	 * @return
	 */
	private double[] calcRunwayExtent(final Runway r) {
		return calcExtent(r.getLat(), r.getLong(), r.getHeading(), r.getLength(), r.getWidth());
	}

	/**
	 * Draw an airport within the rectangle given on the gc
	 * @param device
	 * @param gc
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawAirport(final java.awt.Graphics2D gc, final int x, final int y, final int width, final int height,boolean drawObjectsInAirport) {

		// Work out a scale factor (pixels per foot)
		final double heightFeet = (_extents[2] - _extents[0]) * FEET_PER_DEGREE;
		final double widthFeet =
                    (_extents[3] - _extents[1]) * FEET_PER_DEGREE * Math.abs(Math.cos(Math.toRadians(_airport.getLat())))
                ;

		final double scaleX = widthFeet / (width-40);
		final double scaleY = heightFeet / (height-40);
		final double scale = Math.max(scaleX, scaleY);

		// Now calculate the actual width we will take at the scale to
		// calculate an offset to centre the drawing 
		final int trueWidth = (int) (widthFeet / scale);
		final int trueHeight = (int) (heightFeet / scale);
		// Rectangle to draw in is
		final int offsetX = (width - trueWidth) / 2 + x;
		final int offsetY = (height - trueHeight) / 2 + y;

		final Rectangle boundingBox = new Rectangle(offsetX, offsetY, trueWidth, trueHeight);


                gc.translate(35,150);

                //gc.drawRect(1,1,width-1, height-1);



                gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw the taxiways
		if (_drawTaxiways)  {
			for (final Taxiway taxiway : _airport.getTaxiways()) {
				drawTaxiway(gc, boundingBox, scale, _extents, taxiway);
			}
		}
		// Draw the runways
		if (_drawRunways)  {
			for (final Runway runway : _airport.getRunways()) {
				drawRunway(gc, boundingBox, scale, _extents, runway);
			}
		}
                gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		
		// Turn off advanced graphics, 
		// because it is broken when drawing text in GTK. 
		//gc.setAdvanced(false);
		// Draw heading stuff
		//drawAirportName(gc, x, y, width, height, _airport);
		//drawNearestBeacon(gc, x, y, width, height, _airport);
		
		// Draw the  runway list
		/*if (_drawRunwayList)  {
			drawRunwayList(gc, x, y, width, height, _airport.getRunways());
		}*/
		
		// Draw the  compass rose
		if (_drawCompassRose)  {
			drawCompassRose(gc, x, y, width, height);
		}

                //drawFrequencies(gc, x, y, width, height);


                drawTower(gc,boundingBox,scale,_extents);
                drawWindsocks(gc,boundingBox,scale,_extents);

                if (drawObjectsInAirport) {
                    drawHangars(gc,boundingBox,scale,_extents);
                }
                g2d.dispose();


	}

	private Point drawRunway(
		final java.awt.Graphics2D gc,
		final Rectangle rectangle,
		final double scale,
		final double offsetLat,
		final double offsetLong,
		final double length,
		final double width,
		final double headingRadians,
                final String surface,
                final int displacement,
                final int displacementOpposite
        ) {
		// Calculate the runway rectangle offset from airport origin
		// Corner offset component contributed by the runway length
		double feetEastL = length / 2 * Math.sin(headingRadians);
		double feetNorthL = length / 2 * Math.cos(headingRadians);

		// Corner offset component contributed by the runway width
		final double feetEastW = width / 2 * Math.cos(headingRadians);
		final double feetNorthW = width / 2 * Math.sin(headingRadians);

		// Get the corner offsets (Corners A,B,C,D)
		final double ALat = feetNorthL + feetNorthW + offsetLat;
		final double ALong = feetEastL - feetEastW + offsetLong;

		final double BLat = feetNorthL - feetNorthW + offsetLat;
		final double BLong = feetEastL + feetEastW + offsetLong;

		final double CLat = -feetNorthL - feetNorthW + offsetLat;
		final double CLong = -feetEastL + feetEastW + offsetLong;

		final double DLat = -feetNorthL + feetNorthW + offsetLat;
		final double DLong = -feetEastL - feetEastW + offsetLong;

		final int points[] = new int[8];
		points[0] = (int) (ALong / scale + rectangle.x);
		points[1] = rectangle.height - (int) (ALat / scale) + rectangle.y;
		points[2] = (int) (BLong / scale + rectangle.x);
		points[3] = rectangle.height - (int) (BLat / scale) + rectangle.y;
		points[4] = (int) (CLong / scale + rectangle.x);
		points[5] = rectangle.height - (int) (CLat / scale) + rectangle.y;
		points[6] = (int) (DLong / scale + rectangle.x);
		points[7] = rectangle.height - (int) (DLat / scale) + rectangle.y;

                Polygon p = new Polygon();
                p.addPoint(points[0], points[1]);
                p.addPoint(points[2], points[3]);
                p.addPoint(points[4], points[5]);
                p.addPoint(points[6], points[7]);





                // center
                Point pp=new Point(
                    (points[0]+points[2]+points[4]+points[6])/4,
                    (points[1]+points[3]+points[5]+points[7])/4
                );


                String dispColor="FFFFFF";
                if (
                    surface.equals(Runway.SURFACE_ASPHALT) ||
                    surface.equals(Runway.SURFACE_CONCRETE)
                ) {
                    gc.setColor(Color.decode("0x000000") );
                    gc.fillPolygon(p);
                } else {
                    gc.setColor(Color.decode("0x"+backgroundColor) );
                    gc.fillPolygon(p);
                    gc.setColor(Color.decode("0x000000") );
                    gc.drawPolygon(p);

                    dispColor="000000";

                }

                drawDisplacement(gc,scale,pp,headingRadians,displacement,length,width,dispColor);
                drawDisplacement(gc,scale,pp,headingRadians+Math.PI,displacementOpposite,length,width,dispColor);
                

                return pp;
	}


        private void drawDisplacement(final java.awt.Graphics2D gc,final double scale,Point pp,final double headingRadians,final int displacement,final double length,final double width,final String color) {
            if (displacement>0) {
                // draw line
                gc.translate(pp.x,pp.y);
                gc.rotate(headingRadians);
                int t1=(int) ((length/2-displacement)/scale);
                {


                    gc.translate(0, t1);

                    Polygon d1 = new Polygon();
                    d1.addPoint(
                        (int) (-width/2/scale),
                        0
                    );
                    d1.addPoint(
                        (int) (width/2/scale),
                        0
                    );

                    gc.setColor(Color.decode("0x"+color) );
                    gc.drawPolygon(d1);

                    gc.translate(0, -t1);
                }

                {
                    // draw arrows
                    int arrowHeight=(int) (width);
                    int tTemp=(int) (((length/2)-arrowHeight)/scale)-20;// starting from edge of runway
                    while (tTemp>t1) {

                        gc.translate(0, tTemp);

                        gc.setColor(Color.decode("0x"+color) );

                        // arrow
                        gc.drawLine(
                            (int) (-width/2/scale*8/10),
                            (int) (arrowHeight/scale),
                            0,
                            0
                        );
                        gc.drawLine(
                            (int) (width/2/scale*8/10),
                            (int) (arrowHeight/scale),
                            0,
                            0
                        );

                        gc.translate(0, -tTemp);

                        tTemp-=arrowHeight*10/9/scale;// so that using 270 will have 3 arrows
                    }

                }
                
                
                
                gc.rotate(-headingRadians);
                gc.translate(-pp.x,-pp.y);
            }
        }

	/**
	 * Draws a little compass rose
	 * @param gc
	 * @param canvas_width
	 * @param canvas_height
	 */
	private void drawCompassRose(final java.awt.Graphics2D gc, final int x, final int y, final int width, final int height) {
		// Generate the text using a Serif font
		
		try  {
			gc.setColor(Color.black);
			gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);


                        int rowLength=60;

                        //final int inc = (int) (device.getDPI().x * .05);
                        //final int inc = 1;
			//final int offsetX = x + width - inc * 3 ;
			//final int offsetY = y + inc * 4;
			final int offsetX = x + width -rowLength-20;
			final int offsetY = y + rowLength+40;
			// Draw a little arrow

                        gc.translate(offsetX,offsetY);



                        // draw true north
                        gc.drawLine(0, 0, 0, -rowLength);

                        Polygon p= new Polygon();
                        p.addPoint(0, -rowLength);
                        p.addPoint(-1, -(rowLength-4));
                        p.addPoint(-4, -(rowLength-12));
                        p.addPoint(0, -(rowLength-15));
                        p.addPoint(4, -(rowLength-12));
                        p.addPoint(1, -(rowLength-4));
                        p.addPoint(0, -rowLength);
                        gc.fillPolygon(p);

                        // draw magnetic north
                        double variation=CoreMag.getMagneticVariation(_airport);
                        gc.rotate(variation*3.1416/180);
                        
                        gc.drawLine(0, 0, 0, -rowLength);

                        Polygon pm= new Polygon();
                        pm.addPoint(0, -rowLength);
                        pm.addPoint(0, -(rowLength-12));
                        pm.addPoint(4, -(rowLength-12));
                        pm.addPoint(1, -(rowLength-4));
                        pm.addPoint(0, -rowLength);
                        gc.fillPolygon(pm);
                        
                        

                        {
                            String EoW="E";
                            double v2=variation;
                            if (v2<0) {
                                EoW="W";
                                v2=-v2;
                            }
                            String text=("VAR "+ ((double) ((int) variation*10)/10)+" "+EoW);

                            final Font font = new Font("Times", Font.PLAIN,10*fontMoltiplier); //$NON-NLS-1$
                            gc.setFont(font);
                            //final Point textExtent = gc.getFontMetrics().get textExtent("N"); //$NON-NLS-1$

                           // get metrics from the graphics
                            FontMetrics metrics = gc.getFontMetrics(font);
                            // get the height of a line of text in this font and render context
                            int hgt = metrics.getHeight();
                            // get the advance of my text in this font and render context
                            int adv = metrics.stringWidth(text);


                            gc.rotate(-90*3.1416/180);

                            gc.drawString(text  , rowLength*2/10, hgt+4);

                            gc.rotate(90*3.1416/180);
                        }



                        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);

			


                        gc.rotate(-variation*3.1416/180);
                        gc.translate(-offsetX,-offsetY);

		} finally  {
			//font.dispose();
		}
	}

	/**
	 * Draw a runway for the given airport
	 * @param gc
	 * @param scale
	 * @param airport
	 * @param runway
	 */
	private void drawRunway(final java.awt.Graphics2D gc, final Rectangle rectangle, final double scale, final double airportExtent[], final Runway runway) {

            final double offsetLat = (runway.getLat() - airportExtent[0]) * FEET_PER_DEGREE;
            final double offsetLong = (runway.getLong() - airportExtent[1]) * FEET_PER_DEGREE * Math.abs(Math.cos(Math.toRadians(_airport.getLat())));

            final double headingRadians = Math.toRadians(runway.getHeading());
            Point p=drawRunway(
                gc,
                rectangle,
                scale,
                offsetLat,
                offsetLong,
                runway.getLength(),
                runway.getWidth(),
                headingRadians,
                runway.getSurface(),
                runway.getDisplacement(),
                runway.getDisplacementOpposite()
            );




            // draw length information
            drawRunwayLength(gc, scale, p, runway);

            // draw Heading information
            drawRunwayNumber(gc, scale, p, runway,false);
            drawRunwayNumber(gc, scale, p, runway,true);


            // draw lights
            drawRunwayLights(gc, scale, p, runway,false);
            drawRunwayLights(gc, scale, p, runway,true);

	}

    private void  drawRunwayLength(final java.awt.Graphics2D gc, double scale,Point p,Runway runway) {

            final Font font = new Font("Times", Font.PLAIN,10*fontMoltiplier);
            gc.setFont(font);

            final double headingRadians = Math.toRadians(runway.getHeading());

            gc.setColor(Color.black);

            int tx=p.x;
            int ty=p.y;

            // to the runway center
            gc.translate(tx, ty);

            double rot=headingRadians+Math.PI/2;

            if (rot*180/Math.PI>90 && rot*180/Math.PI<270) {
                rot=rot+Math.PI;
            }

            gc.rotate(rot);

            double lp=runway.getLength()*8/10 /scale/2 ;

            gc.translate(lp, 0);

            String runwayLength=new Integer(runway.getLength()).toString()+"' X "+ new Integer(runway.getWidth()).toString()+"'";

            // get metrics from the graphics
            FontMetrics metrics = gc.getFontMetrics(gc.getFont());
            // get the height of a line of text in this font and render context
            int hgt = metrics.getHeight();
            // get the advance of my text in this font and render context
            int adv = metrics.stringWidth(runwayLength);

            gc.drawString(
                runwayLength,
                -adv ,
                (int) (-runway.getWidth()/2/scale-2)
            );

            gc.translate(-lp, 0);

            gc.rotate(-rot);
            gc.translate(-tx, -ty);
        }

    private void drawRunwayNumber(final java.awt.Graphics2D gc, double scale,Point p,Runway runway,boolean side) {

            final double headingRadians = Math.toRadians(runway.getHeading());

            int tx=p.x;
            int ty=p.y;

            // to the runway center
            gc.translate(tx, ty);

            double rot=headingRadians+Math.PI/2-Math.toRadians(180);
            if (! side) {
                rot+=Math.toRadians(180);
            }

            gc.rotate(rot);

            double lp=-(runway.getLength()/scale/2-4) ;

            gc.translate(lp, 0);

            gc.rotate( Math.toRadians(90) );

            String textRunwayNumber=runway.getNumber();
            if (! side) {
                textRunwayNumber=runway.getOppositeNumber();
            }

            if (textRunwayNumber.startsWith("00")) {
                textRunwayNumber="36"+textRunwayNumber.substring(2);
            }



            // get metrics from the graphics
            FontMetrics metrics = gc.getFontMetrics(gc.getFont());
            // get the height of a line of text in this font and render context
            int hgt = metrics.getHeight();
            // get the advance of my text in this font and render context
            int adv = metrics.stringWidth(textRunwayNumber);

            gc.drawString(
                textRunwayNumber,
                -adv/2 ,
                hgt+1
            );


            gc.rotate( -Math.toRadians(90) );
            gc.translate(-lp, 0);
            gc.rotate(-rot);
            gc.translate(-tx, -ty);












            // to the runway center
            gc.translate(tx, ty);



            if (rot*180/3.1416>90 && rot*180/3.1416<270) {
                rot=rot+3.14159;
            }

            gc.rotate(rot);

            lp=-runway.getLength()*95/100 /scale/2 ;
            if (side) {
                lp=-lp;
            }

            gc.translate(lp, 0);

            double myHead=runway.getHeading();


            double variation=CoreMag.getMagneticVariation(_airport);

            myHead=myHead-variation;


            if (side) {
                myHead=(myHead+180)%360;
            }


            myHead=(double) ((int) (myHead*10))/10;


            String text=new Double(myHead).toString()+degrees+" -> ";
            if (side) {
                text=" <- "+new Double(myHead).toString()+degrees;
            }



            // get the advance of my text in this font and render context
            adv = metrics.stringWidth(text);
            if (! side) {
                adv=0;
            }

            int yoffsetTest=(int) (-runway.getWidth()/2/scale-4);
            if (side) {
                yoffsetTest=(int) (runway.getWidth()/2/scale+hgt-2);
            }

            gc.drawString(
                text,
                -adv ,
                yoffsetTest
            );

            gc.translate(-lp, 0);

            gc.rotate(-rot);
            gc.translate(-tx, -ty);

        }

	/**
	 * Draws a list of runways in the top left of the canvas
	 * @param gc
	 * @param boundingBox
	 * @param collection
	 */
	private void drawRunwayList(final java.awt.Graphics2D gc, final int x, final int y, final int width, final int height, final Collection<Runway> runways) {
		// Generate the text using a fixed font
		//final StringBuffer text = new StringBuffer(Messages.getString("AirportPainter.3")); //$NON-NLS-1$

                //final StringBuffer text = new StringBuffer("Runways\n"); //$NON-NLS-1$


                final Font font = new Font("Times", Font.PLAIN,10*fontMoltiplier); //$NON-NLS-1$
                gc.setFont(font);

                ArrayList<String> texts=new ArrayList<String>();
                texts.add("Runways");

                // Here we use a smaller size of the font, just to minimise the impact of the list on the UI
		//final FontData fd[] = JFaceResources.getDialogFont().getFontData();
		//fd[0].setHeight(8);
		//final Font font = new Font(gc.getDevice(), fd[0]);
		try  {
			for (final Runway runway : runways) {
				texts.add((runway.getNumberPair()+"        ").substring(0,9)+(runway.getLength()+"        ").substring(0,9)); //$NON-NLS-1$
				if (runway.getIlsFreq() !=0 )  {
					texts.add("ILS "+runway.getIlsFreq()+"/"+runway.getIlsOppositeFreq()); //$NON-NLS-1$
				}
			}
			//gc.setFont(font);
			//final Point textExtent = gc.textExtent(text.toString());

                       // get metrics from the graphics
                        FontMetrics metrics = gc.getFontMetrics(font);
                        // get the height of a line of text in this font and render context
                        int hgt = metrics.getHeight();
                        // get the advance of my text in this font and render context
                        int adv = metrics.stringWidth("N");

                        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                        int i=0;
                        for (int t=texts.size()-1;t>=0;t--) {
                            gc.drawString(texts.get(t), x + 4 , y + height - 2 -hgt*i);                            
                            i++;
                        }

                        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);

			//gc.drawText(text.toString(), x + 4 , y + height - (textExtent.y + 2), true);
		} finally  {
			//font.dispose();
		}
	}

	/**
	 * Draw the airport id & name
	 * @param gc
	 * @param boundingBox
	 * @param collection
	 */
	private void drawAirportName(final java.awt.Graphics2D gc, final int x, final int y, final int width, final int height, final Airport airport) {
		// Generate the text using a fixed font
		final String text = airport.getId() + " - " + airport.getName();  //$NON-NLS-1$
//		final Font font = JFaceResources.getTextFont();
                Font font = new Font("Courier", Font.BOLD, 12*fontMoltiplier);
                gc.setColor(Color.black);
		try  {
			gc.setFont(font);

                        // get metrics from the graphics
                        FontMetrics metrics = gc.getFontMetrics(font);
                        // get the height of a line of text in this font and render context
                        int hgt = metrics.getHeight();
                        // get the advance of my text in this font and render context
                        int adv = metrics.stringWidth(text);

                        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			gc.drawString(text, x + 4 , y + hgt + 4);
                        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		} finally  {
//			font.dispose();
		}
	}

	/**
	 * Draw the nearest navaid info
	 * @param gc
	 * @param boundingBox
	 * @param collection
	 */
	private void drawNearestBeacon(final java.awt.Graphics2D gc,  final int x, final int y, final int width, final int height, final Airport airport) {

		final AutoPlanner planner = new AutoPlanner(Earth.getNavAids());
		final NavAid nearest = planner.getNavaidNearest(airport.getLoc(), 10d);

		if (nearest != null)  {
			// Generate the text using a fixed font
			// Allow for the airport name above	
			String text = airport.getId() + " - " + airport.getName();  //$NON-NLS-1$


                        final int headingToInt=(int) (Math.toDegrees(_airport.getLoc().bearingTo(nearest.getLoc())));
                        final int headingFromInt=(int) ((headingToInt+180)%360);

                        final double headingTo = (double) headingToInt;
                        final double headingFrom = (double) headingFromInt;


                        /*gc.translate(width/2, height/2);

                        //gc.rotate(Math.toRadians(30));
                        //gc.drawLine(0,0,0,-100);
                        //gc.rotate(-Math.toRadians(30));

                        gc.setColor(Color.green.darker().darker());

                        gc.rotate(Math.toRadians(headingTo));
                        gc.translate(0,-width/2*6/10 );


                        gc.drawLine(0,0,0,-120);

                        {




                            Font font = new Font("Courier", Font.PLAIN, 12);
                            // get metrics from the graphics
                            FontMetrics metrics = gc.getFontMetrics(font);
                            // get the height of a line of text in this font and render context
                            int hgt = metrics.getHeight();
                            // get the advance of my text in this font and render context
                            int adv = metrics.stringWidth(text);

                            text = nearest.getTypeName() + " "+ nearest.getId() + " - "+ nearest.getName();
                            try  {

                                    gc.rotate(-Math.PI/2d);

                                    gc.setFont(font);
                                    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                                    gc.drawString(
                                        text,
                                        0 ,
                                        -2
                                    );
                                    gc.drawString(
                                        "<- "+new Integer(headingFromInt).toString()+" - "+new Integer(headingToInt).toString()+" ->",
                                        0,
                                        12
                                    );
                                    gc.drawString(
                                        "Freq: "+new Double(nearest.getFreq()).toString(),
                                        0 ,
                                        24
                                    );
                                    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);

                                    gc.rotate(Math.PI/2d);
                            } finally  {
    //				font.dispose();
                            }

                        }

                        
                        gc.translate(0,width/2*6/10 );
                        gc.rotate(-Math.toRadians(headingTo));
                        gc.translate(-width/2, -height/2);
                        */


                        {
                            gc.setColor(Color.black);
                            Font font = new Font("Courier", Font.PLAIN, 12*fontMoltiplier);

                            // get metrics from the graphics
                            FontMetrics metrics = gc.getFontMetrics(font);
                            // get the height of a line of text in this font and render context
                            int hgt = metrics.getHeight();
                            // get the advance of my text in this font and render context
                            int adv = metrics.stringWidth(text);

                            text = "Nearest Navaid:" + nearest.getTypeName() + " "	 
                                + nearest.getId() + " - "
                                + nearest.getName() + " freq:" + nearest.getFreq()+
                                " <- "+new Integer(headingFromInt).toString()+" - "+new Integer(headingToInt).toString()+" ->"
                            ;
                            try  {

                                    gc.setFont(font);
                                    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                                    gc.drawString(text, x + 4 , y + hgt*2 + 4);
                                    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
                            } finally  {
    //				font.dispose();
                            }
                        }

                       
		}
	}

	/**
	 * Draw a taxiway for the given airport
	 * @param gc
	 * @param scale
	 * @param airport
	 * @param runway
	 */
	private void drawTaxiway(final java.awt.Graphics2D gc,  final Rectangle rectangle, final double scale, final double airportExtent[], final Taxiway taxiway) {


                gc.setColor(Color.gray);
                
                // Taxiways are described by paths. so we need to build a path
		GeneralPath path = new GeneralPath();
		
		try {
			List<LayoutNode> nodes = taxiway.getNodes();
			boolean first = true;
			for (LayoutNode node : nodes) {
				final double offsetLat = (node.getLat() - airportExtent[0]) * FEET_PER_DEGREE;
				final double offsetLong = (node.getLong() - airportExtent[1]) * FEET_PER_DEGREE * Math.abs(Math.cos(Math.toRadians(_airport.getLat())));

				int x = (int) (offsetLong / scale) + rectangle.x;
				int y = rectangle.height - (int) (offsetLat / scale) + rectangle.y;
				if (first)
				{
					path.moveTo(x, y);
					first = false;
				}
				else
				{
					if (node.getType().equals("112") || node.getType().equals("114"))
					{
						final double offsetLatB = (node.getBezierLat() - airportExtent[0]) * FEET_PER_DEGREE;
						final double offsetLongB = (node.getBezierLong() - airportExtent[1]) * FEET_PER_DEGREE * Math.abs(Math.cos(Math.toRadians(_airport.getLat())));
						int xB = (int) (offsetLongB / scale) + rectangle.x;
						int yB = rectangle.height - (int) (offsetLatB / scale) + rectangle.y;
						path.quadTo(xB, yB, x, y);
					}
					else
					{
						path.lineTo(x, y);
					}
				}
			}
			path.closePath();
			
			gc.fill( path);
		} finally {
			//path.dispose();
		}
	}

	/**
	 * @return
	 */
	public boolean isDrawCompassRose() {
		return _drawCompassRose;
	}

	/**
	 * @return
	 */
	public boolean isDrawRunwayList() {
		return _drawRunwayList;
	}

	/**
	 * @return
	 */
	public boolean isDrawRunways() {
		return _drawRunways;
	}

	/**
	 * @return
	 */
	public boolean isDrawTaxiways() {
		return _drawTaxiways;
	}

	/**
	 * @param drawCompassRose
	 */
	public void setDrawCompassRose(final boolean drawCompassRose) {
		_drawCompassRose = drawCompassRose;
	}

	/**
	 * @param drawRunwayList
	 */
	public void setDrawRunwayList(final boolean drawRunwayList) {
		_drawRunwayList = drawRunwayList;
	}

	/**
	 * @param drawRunways
	 */
	public void setDrawRunways(final boolean drawRunways) {
		_drawRunways = drawRunways;
	}

	/**
	 * @param drawTaxiways
	 */
	public void setDrawTaxiways(final boolean drawTaxiways) {
		_drawTaxiways = drawTaxiways;
	}


    /**
     * Draws a list of runways in the top left of the canvas
     * @param gc
     * @param boundingBox
     * @param collection
     */
    private void drawTower(final java.awt.Graphics2D gc, final Rectangle rectangle,final double scale, final double airportExtent[]) {
        if (_airport.hasTower()) {
            int niw=0;
            int nih=0;

            final double offsetLat = (_airport.getTower().getLat() - airportExtent[0]) * FEET_PER_DEGREE;
            final double offsetLong = (_airport.getTower().getLong() - airportExtent[1]) * FEET_PER_DEGREE * Math.abs(Math.cos(Math.toRadians(_airport.getLat())));

            int x = (int) (offsetLong / scale) + rectangle.x;
            int y = rectangle.height - (int) (offsetLat / scale) + rectangle.y;

            try {
                gc.translate(x-niw/2,y-nih/2);

                SVGUniverse universe = SVGCache.getSVGUniverse();
                URI uri = universe.loadSVG(
                    Main.getFileFromResource(Main.imgFolder+"tower.svg")
                );
                SVGDiagram diagram = universe.getDiagram(uri);
                //diagram.setIgnoringClipHeuristic(true);

                niw=16;
                nih=niw;

                float fs=1;

                float fsw=(float) (niw/((double) diagram.getRoot().getShape().getBounds2D().getWidth()));
                float fsh=(float) (nih/((double) diagram.getRoot().getShape().getBounds2D().getHeight()));

                fs=fsw;
                if (fsh<fs) {
                    fs=fsh;
                }

                g2d.scale( fs,fs);

                try {
                    diagram.render(g2d);
                } catch (SVGException ex) {
                    Logger.getLogger(MapCreator.class.getName()).log(Level.SEVERE, null, ex);
                }

                g2d.scale(1/fs,1/fs);


                gc.translate(-(x-niw/2),-(y-nih/2));


            //} catch (FileNotFoundException ex) {
                //Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            //} catch (IOException ex) {
                //Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                airportpainter.logging.Logger.logException(ex, airportpainter.logging.Logger.Realm.MAP);                            
            }


        }
    }


    /**
     * Draws a list of runways in the top left of the canvas
     * @param gc
     * @param boundingBox
     * @param collection
     */
    private void drawWindsocks(final java.awt.Graphics2D gc, final Rectangle rectangle,final double scale, final double airportExtent[]) {

        //BufferedImage image=null;

        int iw=0;
        int ih=0;

        int niw=0;
        int nih=0;


        SVGUniverse universe = SVGCache.getSVGUniverse();
        URI uri=null;
        try {
            uri = universe.loadSVG(Main.getFileFromResource(Main.imgFolder+"windsock.svg"));
        //} catch (IOException ex) {
            //Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            airportpainter.logging.Logger.logException(ex, airportpainter.logging.Logger.Realm.AIRPORT);                            
        }

        if (uri!=null) {
            for (Location windsock:_airport.getWindsocks()) {
                final double offsetLat = (windsock.getLat() - airportExtent[0]) * FEET_PER_DEGREE;
                final double offsetLong = (windsock.getLong() - airportExtent[1]) * FEET_PER_DEGREE * Math.abs(Math.cos(Math.toRadians(_airport.getLat())));

                int x = (int) (offsetLong / scale) + rectangle.x;
                int y = rectangle.height - (int) (offsetLat / scale) + rectangle.y;
                //try {
                    /*// Load the img
                    if (image==null) {
                        image = javax.imageio.ImageIO.read(new FileInputStream(new File("imgs/windsock.png")));
                    }
                    iw=image.getWidth();
                    ih=image.getHeight();

                    niw=iw/4;
                    nih=ih/4;

                    gc.drawImage(image, x-niw/2,y-nih/2,niw,nih, null);
                    */

                    SVGDiagram diagram = universe.getDiagram(uri);

                    niw=8;
                    nih=niw;

                    float fs=1;

                    float fsw=(float) (niw/((double) diagram.getRoot().getShape().getBounds2D().getWidth()));
                    float fsh=(float) (nih/((double) diagram.getRoot().getShape().getBounds2D().getHeight()));

                    fs=fsw;
                    if (fsh<fs) {
                        fs=fsh;
                    }

                    gc.translate(x-niw/2,y-nih/2);







                    //diagram.setIgnoringClipHeuristic(true);

                    g2d.scale( fs,fs);

                    try {
                        diagram.render(g2d);
                    } catch (SVGException ex) {
                        Logger.getLogger(MapCreator.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    g2d.scale(1/fs,1/fs);


                    gc.translate(-(x-niw/2),-(y-nih/2));


                    //gc.drawImage(image, x-image.getWidth()/2,y-image.getHeight()/2, null);
                /*} catch (FileNotFoundException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            }
        }
        
    }


    /**
     * Draws a list of runways in the top left of the canvas
     * @param gc
     * @param boundingBox
     * @param collection
     */
    /*private void drawFrequencies(final java.awt.Graphics2D gc, final int x, final int y, final int width, final int height) {
            // Generate the text using a fixed font
            //final StringBuffer text = new StringBuffer(Messages.getString("AirportPainter.3")); //$NON-NLS-1$

            //final StringBuffer text = new StringBuffer("Runways\n"); //$NON-NLS-1$


            final Font font = new Font("Times", Font.PLAIN,10*fontMoltiplier); //$NON-NLS-1$
            gc.setFont(font);
            ArrayList<String> texts=new ArrayList<String>();


            if (_airport.getAtises().size()>0) {
                texts.add("ATIS");
                int i=0;
                String temp="";
                for (final Atis atis : _airport.getAtises()) {
                    if (i==1) {
                        texts.add(
                            temp+(atis.getFreq() +"        ").substring(0,9)
                        );
                        temp="";
                        i=0;
                    } else {
                        temp=(atis.getFreq() +"        ").substring(0,9)+" ";
                        i=1;
                    }
                    
                }
                if (! temp.equals("")) {
                    texts.add(
                        temp
                    );
                }
            }


            String[] reqs={"50","51","52","53","54","55","56"};
            for (String req: reqs) {
                List <ATCFreq> tempFreqs=_airport.getATCFreqs(req);
                if (tempFreqs.size()>0) {
                    texts.add(ATCFreq.typeNames.get(req));
                    int i=0;
                    String temp="";
                    for (final ATCFreq tower : tempFreqs) {
                        if (i==1) {
                            texts.add(
                                temp+(tower.getFreq() +"        ").substring(0,9)
                            );
                            temp="";
                            i=0;
                        } else {
                            temp=(tower.getFreq() +"        ").substring(0,9)+" ";
                            i=1;
                        }

                    }
                    if (! temp.equals("")) {
                        texts.add(
                            temp
                        );
                    }
                }
            }

            try  {
                    // get metrics from the graphics
                    FontMetrics metrics = gc.getFontMetrics(font);
                    // get the height of a line of text in this font and render context
                    int hgt = metrics.getHeight();
                    // get the advance of my text in this font and render context
                    int adv = metrics.stringWidth("N");

                    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    //int i=0;
                    for (int t=0;t<texts.size();t++) {
                        gc.drawString(texts.get(t), x + 4 , 35+ y + hgt*(t+2)*8/10);
                    }

                    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);

                    //gc.drawText(text.toString(), x + 4 , y + height - (textExtent.y + 2), true);
            } finally  {
                    //font.dispose();
            }
    }

    */





    private void drawRunwayLights(final java.awt.Graphics2D gc, double scale,Point p,Runway runway,boolean side) {
            //BufferedImage image=null;

            SVGUniverse universe = SVGCache.getSVGUniverse();
            URI uri=null;
            try {
                uri = universe.loadSVG(Main.getFileFromResource(Main.imgFolder+"als.svg"));
            //} catch (IOException ex) {
                //Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                airportpainter.logging.Logger.logException(ex, airportpainter.logging.Logger.Realm.AIRPORT);                            
            }


            int iw=0;
            int ih=0;

            int niw=0;
            int nih=0;

            final double headingRadians = Math.toRadians(runway.getHeading());

            int tx=p.x;
            int ty=p.y;

            // to the runway center
            gc.translate(tx, ty);



            double rot=headingRadians+Math.PI/2;
            if (! side) {
                rot+=Math.PI;
            }

            gc.rotate(rot);

            double lp=-(runway.getLength()/scale/2) ;

            gc.translate(lp, 0);




            if (
                runway.hasPapi(side) ||
                runway.hasVasi(side)
            ) {
                //File vasiFile=new File("imgs/vasi.png");

                //if (vasiFile.exists()) {
                    //try {
                        // Load the img
                        gc.translate(20*fontMoltiplier, runway.getWidth()/scale/2+2);
                        gc.setColor(Color.DARK_GRAY);
                        gc.drawRect(0,0,2*fontMoltiplier,10*fontMoltiplier);
                        gc.setColor(Color.BLACK);
                        gc.translate(-20*fontMoltiplier, -(runway.getWidth()/scale/2+2));
                    /*} catch (FileNotFoundException ex) {
                        Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                //}
            }

             if (
                runway.hasSSLP(side) ||
                runway.hasSSALS(side) ||
                runway.hasSALSF(side) ||
                runway.hasALSF_I(side) ||
                runway.hasALSF_II(side)
            ) {
                //File vasiFile=new File("imgs/als.png");

                //if (vasiFile.exists()) {
                    //try {
                        gc.rotate(Math.PI/2 );

                        // Load the img

                        /*if (image==null) {
                            image = javax.imageio.ImageIO.read(new FileInputStream(new File("imgs/als.png")));
                        }
                        iw=image.getWidth();
                        ih=image.getHeight();

                        niw=iw/4;
                        nih=ih/4;

                        gc.drawImage(image, -niw/2,-nih/2+14,niw,nih, null);
                        */
                        //gc.translate(x-niw/2,y-nih/2);

                        /*SVGUniverse universe = SVGCache.getSVGUniverse();
                        URI uri = universe.loadSVG(
                            new FileInputStream(new File("imgs/als.svg")),"als"
                        );*/
                        SVGDiagram diagram = universe.getDiagram(uri);
                        //diagram.setIgnoringClipHeuristic(true);

                        niw=32;
                        nih=niw;

                        float fs=1;

                        float fsw=(float) (niw/((double) diagram.getRoot().getShape().getBounds2D().getWidth()));
                        float fsh=(float) (nih/((double) diagram.getRoot().getShape().getBounds2D().getHeight()));

                        fs=fsw;
                        if (fsh<fs) {
                            fs=fsh;
                        }


                        g2d.scale( fs,fs);

                        try {
                            diagram.render(g2d);
                        } catch (SVGException ex) {
                            Logger.getLogger(MapCreator.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        g2d.scale(1/fs,1/fs);


                        //gc.translate(-(x-niw/2),-(y-nih/2));
                        //image = javax.imageio.ImageIO.read(vasiFile);
                        //gc.drawImage(image, -image.getWidth()/2,14, null);


                        gc.rotate(-Math.PI/2);

                    /*} catch (FileNotFoundException ex) {
                        Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                //}
            }



            gc.translate(-lp, 0);
            gc.rotate(-rot);
            gc.translate(-tx, -ty);
        }




    private void drawHangars(final java.awt.Graphics2D gc, final Rectangle rectangle,final double scale, final double airportExtent[]) {
         {
            //try {
            {
                SVGUniverse universe = SVGCache.getSVGUniverse();
                
                /*
                URI uri = universe.loadSVG(
                    new FileInputStream(new File("imgs/tower.svg")),"tower"
                );*/

                
                List<ObjectShared> objects=ObjectShared.loadAllObjectsIn(_extents[1],_extents[0],_extents[3],_extents[2]);

                for (ObjectShared hangar: objects) {
                     //BufferedImage image=null;

                    /*if (! hangar.getName().equals("Models/Airport/apt-light.xml")) {
                        continue;
                    }*/

                    if (hangar.getSos()!=null) {
                        if (hangar.getSos().isDrawInAirport()) {

                            URI svgURI=hangar.getSos().getURI();
                            if (svgURI!=null) {
                                SVGDiagram diagram = universe.getDiagram(svgURI);
                                //diagram.setIgnoringClipHeuristic(true);
                                int niw=0;
                                int nih=0;

                                final double offsetLat = (hangar.getLat() - airportExtent[0]) * FEET_PER_DEGREE;
                                final double offsetLong = (hangar.getLong() - airportExtent[1]) * FEET_PER_DEGREE * Math.abs(Math.cos(Math.toRadians(_airport.getLat())));

                                int x = (int) (offsetLong / scale) + rectangle.x;
                                int y = rectangle.height - (int) (offsetLat / scale) + rectangle.y;

                                if ( x<=rectangle.width && y<=rectangle.height && x>=0 && y>=0 ) {
                                    // Load the img



                                    niw=hangar.getSos().getSize();
                                    nih=niw;

                                    g2d.translate(x-niw/2,y-nih/2);


                                    float fs=1;

                                    float fsw=(float) (niw/((double) diagram.getRoot().getShape().getBounds2D().getWidth()));
                                    float fsh=(float) (nih/((double) diagram.getRoot().getShape().getBounds2D().getHeight()));

                                    fs=fsw;
                                    if (fsh<fs) {
                                        fs=fsh;
                                    }

                                    g2d.rotate(-(Math.toRadians(90+hangar.getRotation())));

                                    g2d.scale( fs,fs);


                                    //g2d.drawRect(-2,-2,4,4);
                                    try {
                                        diagram.render(g2d);
                                    } catch (SVGException ex) {
                                        Logger.getLogger(MapCreator.class.getName()).log(Level.SEVERE, null, ex);
                                    }


                                    g2d.scale(1/fs,1/fs);

                                    g2d.rotate(+Math.toRadians((90+hangar.getRotation())));

                                    g2d.translate(-(x-niw/2),-(y-nih/2));
                                }
                            }
                        }
                    }
                }



            //} catch (IOException ex) {
                //Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            }


            
        }
    }


    private void notifyAction(String text) {
        Main.getPropertyChangeSupport().firePropertyChange("action", null, text);
    }

    private void createSheetsName(Airport airport, Document document) {
        // FIRST ROW
        // AIRPORT NAME     DATE        FLIGHTGEAR
        
        notifyAction("createSheetsName");
        
        PdfPTable table = new PdfPTable(3);
        {

            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
            Paragraph p=new Paragraph(airport.getName().toUpperCase(),font);

            PdfPCell cell = new PdfPCell(p);
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            //cell.setColspan(3);
            table.addCell(cell);
        }

        {

            DateFormat dateFormat = new SimpleDateFormat("dd MMM yy", Locale.US);
            Date date = new Date();
            String dateString=dateFormat.format(date).toUpperCase();


            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 8 , com.lowagie.text.Font.NORMAL);
            PdfPCell cell = new PdfPCell(new Paragraph( dateString,font ));
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
        }

        {
            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.COURIER, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
            PdfPCell cell = new PdfPCell(new Paragraph( "FLIGHTGEAR" ,font));
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
        }


        float[] widths2 = { 2f, 1f, 1f };
        try {
            table.setWidths(widths2);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }

        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private void createSheetsAircraftTypes(Airport airport, Document document) {
        // AIRPORT NAME     Aircrafts pictures
        notifyAction("createSheetsAircraftTypes");
        PdfPTable table = new PdfPTable(2);
         {

            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.NORMAL);
            Paragraph p=new Paragraph(airport.getId(),font);

            PdfPCell cell = new PdfPCell(p);
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);

            table.addCell(cell);
        }

         {
            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.COURIER, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
            PdfPCell cell = new PdfPCell(new Paragraph( "FLIGHTGEAR" ,font));
            cell.setBorderWidth(borderWidth);
            //cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
        }


        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private Paragraph createTinyParagraph(String title,String data) {
        Paragraph p=new Paragraph();
        
        p.setLeading((p.leading()/2)+2);
        p.add(new Chunk(title, new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 8 , com.lowagie.text.Font.NORMAL)));
        
        p.add(new Chunk(data, new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 10 , com.lowagie.text.Font.BOLD)));
        
        return p;
        
    }

    private String getNEarestBeaconString(Airport airport) {
        final AutoPlanner planner = new AutoPlanner(Earth.getNavAids());
        final NavAid nearest = planner.getNavaidNearest(airport.getLoc(), 10d);

        if (nearest != null)  {
                // Generate the text using a fixed font
                // Allow for the airport name above
                //String text = airport.getId() + " - " + airport.getName();  //$NON-NLS-1$
                String text;


                final int headingToInt=(int) (Math.toDegrees(_airport.getLoc().bearingTo(nearest.getLoc())));
                final int headingFromInt=(int) ((headingToInt+180)%360);

                final double headingTo = (double) headingToInt;
                final double headingFrom = (double) headingFromInt;

                text = nearest.getTypeName() + " "
                    + nearest.getId() + " - "
                    + nearest.getName() + "\r\nfreq:" + nearest.getFreq()+
                    " <- "+new Integer(headingFromInt).toString()+degrees+" - "+new Integer(headingToInt).toString()+degrees+" ->"
                ;

                return text;


        }

        return "";

    }

    private void createSheetsFreqs(Airport airport, Document document) {
        // FREQs Nearest Navaid
        notifyAction("createSheetsFreqs");
        PdfPTable table = new PdfPTable(2);
        {
            PdfPCell cell = new PdfPCell();

            cell.setVerticalAlignment(Element.ALIGN_TOP);
            

            {
                if (_airport.getAtises().size()>0) {
                    String freq="";
                    for (final Atis atis : _airport.getAtises()) {
                        freq=freq+new Double(atis.getFreq()).toString()+" ";
                    }

                    Paragraph p=createTinyParagraph("ATIS ",freq);
                    cell.addElement(p);
                }
            }

            {

                String[] reqs={"50","51","52","53","54","55","56"};
                for (String req: reqs) {

                    List <ATCFreq> tempFreqs=_airport.getATCFreqs(req);
                    if (tempFreqs.size()>0) {
                        String freq="";
                        for (final ATCFreq tower : tempFreqs) {
                            freq=freq+tower.getFreq()+" ";
                        }

                        Paragraph p=createTinyParagraph(ATCFreq.typeNames.get(req)+" ",freq);
                        cell.addElement(p);

                    }
                }


            }


            //cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setBorderWidth(borderWidth);
            cell.setVerticalAlignment(Element.ALIGN_TOP);

            table.addCell(cell);
        }

         {
            PdfPCell cell = new PdfPCell();
            {
                Paragraph p=new Paragraph(
                    "Nearest Navaid:" ,
                    new com.lowagie.text.Font(
                        com.lowagie.text.Font.TIMES_ROMAN,
                        8,
                        com.lowagie.text.Font.NORMAL
                    )
                );
                cell.addElement(p);
            }

            {
                Paragraph p=new Paragraph(
                    getNEarestBeaconString(airport) ,
                    new com.lowagie.text.Font(
                        com.lowagie.text.Font.TIMES_ROMAN,
                        10,
                        com.lowagie.text.Font.NORMAL
                    )
                );
                cell.addElement(p);
            }
            
            cell.setBorderWidth(borderWidth);
            //cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
        }


        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }






















/*


    private void createSheetsAddImage(Airport airport, Document document,String imageFileName) {

        Image im;
        try {
            im = Image.getInstance(imageFileName);
            im.scalePercent(25);
            try {
                document.add(im);
            } catch (DocumentException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (BadElementException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    private void createSheetsAddSvg(Airport airport,PdfWriter writer, Document document,String imageFileName) {
        // FREQs Nearest Navaid
        notifyAction("createSheetsAddSvg");
        PdfPTable table = new PdfPTable(1);
        {
            PdfPCell cell = new PdfPCell();
            cell.setMinimumHeight(height);

            {
                Paragraph p=new Paragraph();
                com.lowagie.text.Font fontWarning = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
                p.add(new Chunk(" ",fontWarning));
                cell.addElement(p);
            }


            cell.setBorderWidth(borderWidth);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            table.addCell(cell);
        }

                table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private void createSheetsFakeShops(Airport airport, Document document) {
        // FREQs Nearest Navaid
        notifyAction("createSheetsFakeShops");
        PdfPTable table = new PdfPTable(1);
        {
            PdfPCell cell = new PdfPCell();

            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setMinimumHeight(20);

            boolean addWorker=false;
            for (final Runway runway : airport.getRunways()) {
                if (runway.getLength()>1200/FEET_PER_METER) {
                    addWorker=true;
                }
            }

            boolean addFork=(airport.hasTower()) ;



            Paragraph p=new Paragraph();
            p.setLeading((p.leading()/2)+4);
            if (addWorker) {
                try {
                    p.add(new Chunk("   "));
                    p.add(new Chunk(Image.getInstance(getClass().getResource(Main.imgFolder+"avail_work.png") ), 0, -3));
                } catch (BadElementException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (addFork) {
                try {
                    p.add(new Chunk("   "));
                    p.add(new Chunk(Image.getInstance(getClass().getResource(Main.imgFolder+"avail_fork.png")), 0, -3));
                } catch (BadElementException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            cell.addElement(p);


            //cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setBorderWidth(borderWidth);
            cell.setVerticalAlignment(Element.ALIGN_TOP);

            table.addCell(cell);
        }

        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private boolean oneLightDone=false;
    private Chunk addOneLight(String text,com.lowagie.text.Font font) {
        String k="";

        if (oneLightDone) {
            k=" - ";
        }
        oneLightDone=true;
        return new Chunk(k+text,font);
    }
    private void createSheetsLights(Airport airport, Document document) {
        // FREQs Nearest Navaid
        notifyAction("createSheetsLights");
        PdfPTable table = new PdfPTable(1);
        {
            PdfPCell cell = new PdfPCell();

            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setMinimumHeight(20);



            
            Paragraph p=new Paragraph();
            p.setLeading((p.leading()/2)+4);
            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 10 , com.lowagie.text.Font.NORMAL);

            oneLightDone=false;


            try {
                p.add(new Chunk("   ",font));
                p.add(new Chunk(Image.getInstance(getClass().getResource(Main.imgFolder+"light.png")), 0, 0));
                p.add(new Chunk("   ",font));
            } catch (BadElementException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            }


            boolean addABN=airport.hasBeacon();
            if (addABN) {
                p.add(addOneLight("ABN",font));
            }

            boolean addALS=false;
            boolean addPAPI=false;
            for (final Runway runway : airport.getRunways()) {
                if (
                    (runway.hasALSF_I(false) || runway.hasALSF_I(true)) ||
                    (runway.hasALSF_II(false) || runway.hasALSF_II(true))
                ) {
                    addALS=true;
                }

                if (
                    (runway.hasPapi(false) || runway.hasPapi(true))
                ) {
                    addPAPI=true;
                }
            }
            if (addALS) {
                p.add(addOneLight("ALS",font));
            }



            if (addPAPI) {
                p.add(addOneLight("PAPI ",font));
                String separ="";
                for (final Runway runway : airport.getRunways()) {
                    if (runway.hasPapi(false)) {
                        p.add(new Chunk(separ+runway.getNumber()+" (3.5)"+degrees+" ",font));
                        separ=", ";
                    }
                    if (runway.hasPapi(true)) {
                        p.add(new Chunk(separ+runway.getOppositeNumber()+" (3.5)"+degrees+" ",font));
                        separ=", ";
                    }
                }
            }


            cell.addElement(p);


            //cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setBorderWidth(borderWidth);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            table.addCell(cell);
        }

        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private void createSheetsRunways(Airport airport, Document document) {
        notifyAction("createSheetsRunways");
        
        PdfPTable table = new PdfPTable(6);

        // titles
        com.lowagie.text.Font fontTitle = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 8 , com.lowagie.text.Font.NORMAL);
        {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(borderWidth);
            cell.setBorderWidthBottom(borderWidthThin);
            cell.setBorderWidthRight(borderWidthThin);
            cell.setBorderWidthLeft(borderWidth);
            Paragraph p=new Paragraph("RWY N"+degrees,fontTitle);
            p.setLeading((p.leading()/2)+2);
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);
        }
        {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(borderWidth);
            cell.setBorderWidthBottom(borderWidthThin);
            cell.setBorderWidthRight(borderWidthThin);
            cell.setBorderWidthLeft(borderWidthThin);
            Paragraph p=new Paragraph("Dimension (ft) - Surface",fontTitle);
            p.setLeading((p.leading()/2)+2);
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);
        }
        {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(borderWidth);
            cell.setBorderWidthBottom(borderWidthThin);
            cell.setBorderWidthRight(borderWidthThin);
            cell.setBorderWidthLeft(borderWidthThin);
            Paragraph p=new Paragraph("TORA (ft)",fontTitle);
            p.setLeading((p.leading()/2)+2);
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);
        }
        {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(borderWidth);
            cell.setBorderWidthBottom(borderWidthThin);
            cell.setBorderWidthRight(borderWidthThin);
            cell.setBorderWidthLeft(borderWidthThin);
            Paragraph p=new Paragraph("LDA (ft)",fontTitle);
            p.setLeading((p.leading()/2)+2);
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);
        }
        {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(borderWidth);
            cell.setBorderWidthBottom(borderWidthThin);
            cell.setBorderWidthRight(borderWidthThin);
            cell.setBorderWidthLeft(borderWidthThin);
            Paragraph p=new Paragraph("Strength",fontTitle);
            p.setLeading((p.leading()/2)+2);
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);

        }
        {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(borderWidth);
            cell.setBorderWidthBottom(borderWidthThin);
            cell.setBorderWidthRight(borderWidth);
            cell.setBorderWidthLeft(borderWidthThin);
            Paragraph p=new Paragraph("Lights",fontTitle);
            p.setLeading((p.leading()/2)+2);
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);
        }

        // data
        com.lowagie.text.Font fontData = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 10 , com.lowagie.text.Font.NORMAL);
        int rc=0;
        for (final Runway runway : airport.getRunways()) {
            {
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidthLeft(borderWidth);
                if (rc<airport.getRunways().size()-1) {
                    // thin bottom if not last runway
                    cell.setBorderWidthBottom(borderWidthThin);
                } else {
                    cell.setBorderWidthBottom(borderWidth);
                }
                cell.setBorderWidthRight(borderWidthThin);
                Paragraph p=new Paragraph(runway.getNumber()+"\r\n"+runway.getOppositeNumber(),fontData);
                p.setLeading((p.leading()/2)+2);
                p.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p);
                table.addCell(cell);
            }
            {
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidthLeft(borderWidthThin);
                if (rc<airport.getRunways().size()-1) {
                    // thin bottom if not last runway
                    cell.setBorderWidthBottom(borderWidthThin);
                } else {
                    cell.setBorderWidthBottom(borderWidth);
                }
                cell.setBorderWidthRight(borderWidthThin);
                String runwayLength=new Integer(runway.getLength()).toString()+" x "+ new Integer(runway.getWidth()).toString()+" "+runway.getSurfaceName();
                Paragraph p=new Paragraph(runwayLength,fontData);
                p.setLeading((p.leading()/2)+2);
                p.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p);
                table.addCell(cell);

            }
            {
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidthLeft(borderWidthThin);
                if (rc<airport.getRunways().size()-1) {
                    // thin bottom if not last runway
                    cell.setBorderWidthBottom(borderWidthThin);
                } else {
                    cell.setBorderWidthBottom(borderWidth);
                }
                cell.setBorderWidthRight(borderWidthThin);
                Paragraph p=new Paragraph(runway.getLength()+"\r\n"+runway.getLength(),fontData);
                p.setLeading((p.leading()/2)+2);
                p.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p);
                table.addCell(cell);
            }
            {
                PdfPCell cell = new PdfPCell();                
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidthLeft(borderWidthThin);
                if (rc<airport.getRunways().size()-1) {
                    // thin bottom if not last runway
                    cell.setBorderWidthBottom(borderWidthThin);
                } else {
                    cell.setBorderWidthBottom(borderWidth);
                }
                cell.setBorderWidthRight(borderWidthThin);
                Paragraph p=new Paragraph((runway.getLength()-runway.getDisplacement()) +"\r\n"+(runway.getLength()-runway.getDisplacementOpposite()),fontData);
                p.setLeading((p.leading()/2)+2);
                p.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p);
                table.addCell(cell);
            }
            {
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidthLeft(borderWidthThin);
                if (rc<airport.getRunways().size()-1) {
                    // thin bottom if not last runway
                    cell.setBorderWidthBottom(borderWidthThin);
                } else {
                    cell.setBorderWidthBottom(borderWidth);
                }
                cell.setBorderWidthRight(borderWidthThin);
                Paragraph p=new Paragraph("to do...",fontData);
                p.setLeading((p.leading()/2)+2);
                p.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(p);
                table.addCell(cell);
            }
            {
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidthLeft(borderWidthThin);
                cell.setBorderWidthRight(borderWidth);
                if (rc<airport.getRunways().size()-1) {
                    // thin bottom if not last runway
                    cell.setBorderWidthBottom(borderWidthThin);
                } else {
                    cell.setBorderWidthBottom(borderWidth);
                }
                Paragraph p=new Paragraph();
                p.setLeading((p.leading()/2)+2);
                p.setAlignment(Element.ALIGN_CENTER);
                try {
                    if (
                        runway.hasALSF_I(true) ||
                        runway.hasALSF_I(false) ||
                        runway.hasALSF_II(true) ||
                        runway.hasALSF_II(false) ||
                        runway.hasPapi(true) ||
                        runway.hasPapi(false) ||
                        runway.hasSALSF(true) ||
                        runway.hasSALSF(false) ||
                        runway.hasSSLP(true) ||
                        runway.hasSSLP(false) ||
                        runway.hasVasi(true) ||
                        runway.hasVasi(false)
                    ) {
                        p.add(new Chunk(Image.getInstance(getClass().getResource(Main.imgFolder+"light.png")), 0, 0));
                    } else {
                        p.add(new Chunk("--",fontData));
                    }
                } catch (BadElementException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                }
                cell.addElement(p);
                table.addCell(cell);
            }

            rc++;

        }


        float[] widths2 = { 1f,2f, 1f, 1f,2f,1f };
        try {
            table.setWidths(widths2);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }

        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }


    }








    private void createSheetsNotes(Airport airport, Document document) {
        // FREQs Nearest Navaid
        notifyAction("createSheetsNotes");
        PdfPTable table = new PdfPTable(1);
        {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            {
                Paragraph p=new Paragraph();
                com.lowagie.text.Font fontWarning = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
                p.add(new Chunk("Warning !",fontWarning));
                cell.addElement(p);
            }

            {
                Paragraph p=new Paragraph();
                com.lowagie.text.Font fontText = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.NORMAL);
                p.add(
                    new Chunk(
                        "Do not use this chart in real life, this is a chart suitable to be used only for FlightGear !!!",
                        fontText
                    )
                );
                cell.addElement(p);
            }




            cell.setBorderWidth(borderWidth);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);

            table.addCell(cell);
        }

                table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }








    private void createSheets2Name(Airport airport, Document document) {
        // FIRST ROW
        // AIRPORT NAME     DATE        FLIGHTGEAR
        
        notifyAction("createSheets2Name");
        
        PdfPTable table = new PdfPTable(3);


        {
            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.COURIER, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
            PdfPCell cell = new PdfPCell(new Paragraph( "FLIGHTGEAR" ,font));
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            //cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
        }


        {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yy", Locale.US);
            Date date = new Date();
            String dateString=dateFormat.format(date).toUpperCase();


            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 8 , com.lowagie.text.Font.NORMAL);
            PdfPCell cell = new PdfPCell(new Paragraph( dateString,font ));
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
        }


        {

            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
            Paragraph p=new Paragraph(airport.getName().toUpperCase(),font);

            PdfPCell cell = new PdfPCell(p);
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            //cell.setColspan(3);
            table.addCell(cell);
        }



        float[] widths2 = { 1f, 1f, 2f };
        try {
            table.setWidths(widths2);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }

        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSheets2Freqs(Airport airport, Document document) {
        // FREQs Nearest Navaid
        notifyAction("createSheets2Freqs");
        PdfPTable table = new PdfPTable(2);
        {
            PdfPCell cell = new PdfPCell();
            {
                Paragraph p=new Paragraph(
                    "Within Airspace:" ,
                    new com.lowagie.text.Font(
                        com.lowagie.text.Font.TIMES_ROMAN,
                        8,
                        com.lowagie.text.Font.NORMAL
                    )
                );
                cell.addElement(p);
            }

            cell.setBorderWidth(borderWidth);
            
            cell.setBorderWidthBottom(borderWidthThin);

            //cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
        }

        {
            PdfPCell cell = new PdfPCell();

            com.lowagie.text.Font fontBold = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.NORMAL);
            
            {
                Paragraph p=new Paragraph(airport.getId(),fontBold);
                cell.addElement(p);
            }
            {
                Paragraph p=new Paragraph();
                p.add(
                    new Chunk(
                        "Elev ",
                        font
                    )
                );

                int feets=(int) (airport.getElevation());
                p.add(
                    new Chunk(
                        new Integer(feets).toString()+"'",
                        fontBold
                    )
                );
                int meters=(int) (feets / FEET_PER_METER);

                p.add(
                    new Chunk(
                        "/"+new Integer(meters).toString()+"m",
                        font
                    )
                );

                cell.addElement(p);
            }

            {
                Paragraph p=new Paragraph();
                p.add(
                    new Chunk(
                        airport.getLatitudeString1(airport.getLat(),"N","S",2,false)+
                        "\r\n"+
                        airport.getLatitudeString1(airport.getLong(),"W","E",3,true)
                        ,
                        font
                    )
                );

                cell.addElement(p);
            }



            
            cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);

            table.addCell(cell);
        }



        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSheets2ILS(Airport airport, Document document) {
        // FREQs Nearest Navaid
        notifyAction("createSheets2ILS");
        PdfPTable table = new PdfPTable(2);
        {
            PdfPCell cell = new PdfPCell();
            {
                Paragraph p=new Paragraph(
                    "(TWR)" ,
                    new com.lowagie.text.Font(
                        com.lowagie.text.Font.TIMES_ROMAN,
                        8,
                        com.lowagie.text.Font.NORMAL
                    )
                );
                p.setLeading((p.leading()/2)+2);
                cell.addElement(p);
            }



            List <ATCFreq> tempFreqsTwr=_airport.getATCFreqs("54");
            List <ATCFreq> tempFreqsGnd=_airport.getATCFreqs("53");
            if (tempFreqsTwr.size()>0 || tempFreqsGnd.size()>0) {

                if (tempFreqsTwr.size()>0 ) {
                    Paragraph p=new Paragraph();
                    p.setLeading((p.leading()/2)+6);
                    Chunk c=new Chunk(
                        airport.getName().toUpperCase()+" APPROACH " ,
                        new com.lowagie.text.Font(
                            com.lowagie.text.Font.TIMES_ROMAN,
                            8,
                            com.lowagie.text.Font.NORMAL
                        )
                    );
                    p.add(c);
                    for (final ATCFreq tower : tempFreqsTwr) {
                        Chunk c2=new Chunk(
                            (tower.getFreq() +"        ").substring(0,9)+" " ,
                            new com.lowagie.text.Font(
                                com.lowagie.text.Font.TIMES_ROMAN,
                                8,
                                com.lowagie.text.Font.BOLD
                            )
                        );
                        p.add(c2);

                    }
                    cell.addElement(p);

                }



                if (tempFreqsGnd.size()>0 ) {
                    Paragraph p=new Paragraph();
                    p.setLeading((p.leading()/2)+6);
                    Chunk c=new Chunk(
                        "GROUND " ,
                        new com.lowagie.text.Font(
                            com.lowagie.text.Font.TIMES_ROMAN,
                            8,
                            com.lowagie.text.Font.NORMAL
                        )
                    );
                    p.add(c);
                    for (final ATCFreq tower : tempFreqsGnd) {
                        Chunk c2=new Chunk(
                            (tower.getFreq() +"        ").substring(0,9)+" " ,
                            new com.lowagie.text.Font(
                                com.lowagie.text.Font.TIMES_ROMAN,
                                8,
                                com.lowagie.text.Font.BOLD
                            )
                        );
                        p.add(c2);

                    }
                    cell.addElement(p);

                }


            }




            cell.setBorderWidth(borderWidth);
            cell.setBorderWidthTop(borderWidthThin);
            cell.setBorderWidthRight(borderWidthThin);

            //cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
        }

        {
            com.lowagie.text.Font font = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, com.lowagie.text.Font.DEFAULTSIZE , com.lowagie.text.Font.NORMAL);
            PdfPCell cell = new PdfPCell();

            Paragraph p=new Paragraph();
            p.setLeading((p.leading())+6);
            Chunk c=new Chunk(
                "ATIS " ,
                new com.lowagie.text.Font(
                    com.lowagie.text.Font.TIMES_ROMAN,
                    8,
                    com.lowagie.text.Font.NORMAL
                )
            );
            p.add(c);

            for (final Atis atis : _airport.getAtises()) {
                Chunk c2=new Chunk(
                    (atis.getFreq() +"        ").substring(0,9)+" ",
                    new com.lowagie.text.Font(
                        com.lowagie.text.Font.TIMES_ROMAN,
                        8,
                        com.lowagie.text.Font.BOLD
                    )
                );
                p.add(c2);
            }

            cell.addElement(p);


            /// ILS table
            {
                height1=0;
                PdfPTable tableILS = new PdfPTable(4);
                tableILS.setWidthPercentage(100);
                
                // titles
                com.lowagie.text.Font fontTitle = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 8 , com.lowagie.text.Font.NORMAL);
                {
                    PdfPCell cellTitle = new PdfPCell();
                    cellTitle.setVerticalAlignment(Element.ALIGN_CENTER);
                    cellTitle.setBorderWidth(borderWidthThin);
                    Paragraph pt=new Paragraph("RWY",fontTitle);
                    pt.setLeading((pt.leading()/2)+2);
                    pt.setAlignment(Element.ALIGN_CENTER);
                    cellTitle.addElement(pt);
                    tableILS.addCell(cellTitle);
                }
                {
                    PdfPCell cellTitle = new PdfPCell();
                    cellTitle.setVerticalAlignment(Element.ALIGN_CENTER);
                    cellTitle.setBorderWidth(borderWidthThin);
                    Paragraph pt=new Paragraph("ILS",fontTitle);
                    pt.setLeading((pt.leading()/2)+2);
                    pt.setAlignment(Element.ALIGN_CENTER);
                    cellTitle.addElement(pt);
                    tableILS.addCell(cellTitle);
                }
                {
                    PdfPCell cellTitle = new PdfPCell();
                    cellTitle.setVerticalAlignment(Element.ALIGN_CENTER);
                    cellTitle.setBorderWidth(borderWidthThin);
                    Paragraph pt=new Paragraph("RWY",fontTitle);
                    pt.setLeading((pt.leading()/2)+2);
                    pt.setAlignment(Element.ALIGN_CENTER);
                    cellTitle.addElement(pt);
                    tableILS.addCell(cellTitle);
                }
                {
                    PdfPCell cellTitle = new PdfPCell();
                    cellTitle.setVerticalAlignment(Element.ALIGN_CENTER);
                    cellTitle.setBorderWidth(borderWidthThin);
                    Paragraph pt=new Paragraph("ILS",fontTitle);
                    pt.setLeading((pt.leading()/2)+2);
                    pt.setAlignment(Element.ALIGN_CENTER);
                    cellTitle.addElement(pt);
                    tableILS.addCell(cellTitle);
                }



                int pos=0;
                int rs=airport.getRunways().size();
                for (final Runway runway : airport.getRunways()) {
                    if (runway.getIlsFreq() !=0 )  {
                        height1=height1+10;
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( runway.getNumber() ,fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( runway.getIlsFreq()+" "+"... "+ new Integer((int) runway.getHeading()).toString()+degrees ,fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }
                    } /*else {
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( "",fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( "",fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }

                    }*/



                    if (runway.getIlsOppositeFreq() !=0 )  {
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( runway.getOppositeNumber() ,fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( runway.getIlsOppositeFreq()+" "+"... "+ new Integer((int)   ( runway.getHeading() + 180 ) %  180    ).toString()+degrees ,fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }
                    } else {
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( "",fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }
                        {
                            PdfPCell cellFreq = new PdfPCell();
                            cellFreq.setVerticalAlignment(Element.ALIGN_CENTER);
                            cellFreq.setBorderWidth(borderWidthThin);
                            if (pos>0) {
                                cellFreq.setBorderWidthTop(0);
                            }
                            if (pos<rs-1) {
                                cellFreq.setBorderWidthBottom(0);
                            }

                            Paragraph pt=new Paragraph( "",fontTitle);
                            pt.setLeading((pt.leading()/2)+2);
                            pt.setAlignment(Element.ALIGN_CENTER);
                            cellFreq.addElement(pt);
                            tableILS.addCell(cellFreq);
                        }

                    }

                    pos++;


                }







                float[] widths2 = { 1f, 3f, 1f ,3f};
                try {
                    tableILS.setWidths(widths2);                    
                } catch (DocumentException ex) {
                    Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
                }

                cell.addElement(tableILS);
            }








            cell.setBorderWidth(borderWidth);
            cell.setVerticalAlignment(Element.ALIGN_TOP);

            table.addCell(cell);            
        }



        table.setWidthPercentage(100);
        try {
            document.add(table);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /*private void createSheets2AddImage(Airport airport, Document document,String pngName2,boolean keepPng) {

        MapCreator mp=new MapCreator(airport);
        BufferedImage bimage=mp.createImage();


        File outputfile = new File( pngName2   );
        try {
            ImageIO.write(bimage, "png", outputfile);
        } catch(Exception e) {
            System.out.println(e);
        }

        Image im;
        try {
            im = Image.getInstance( pngName2 );

            im.scalePercent(25);

            try {
                document.add(im);
            } catch (DocumentException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (BadElementException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (! keepPng) {
            outputfile.delete();
        }

    }*/

    private void createSheets2AddSvg(Airport airport, PdfWriter writer, Document document,boolean drawObjectsInMap,boolean drawTerrainInMap) {
        notifyAction("createSheets2AddSvg");
        
        MapCreator mp=new MapCreator(airport,height1);
        mp.writer=writer;
        mp.document=document;


        mp.createImage(drawObjectsInMap,drawTerrainInMap);
        /*BufferedImage bimage=mp.createImage();


        File outputfile = new File( pngName2   );
        try {
            ImageIO.write(bimage, "png", outputfile);
        } catch(Exception e) {
            System.out.println(e);
        }

        Image im;
        try {
            im = Image.getInstance( pngName2 );

            im.scalePercent(25);

            try {
                document.add(im);
            } catch (DocumentException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (BadElementException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (! keepPng) {
            outputfile.delete();
        }
        */
    }




    private void createSheets1(Airport airport, PdfWriter writer,Document document,String pdfName,boolean drawObjectsInMap,boolean drawTerrainInMap) {
        // doing PDF now
        try {

            //PdfWriter pdfwriter = PdfWriter.getInstance(document, new FileOutputStream(pdfName));

            // step 3: we open the document
            //document.open();

            // step 4: we add some paragraphs to the document
            //document.add(
                //new Paragraph(airport.getId()+" - "+airport.getName())
            //);

            
            createSheetsName(airport,document);
            createSheetsAircraftTypes(airport,document);
            createSheetsFreqs(airport,document);
            //createSheetsAddImage(airport,document,pngName1);
            createSheetsAddSvg(airport,writer,document,pdfName);
            createSheetsFakeShops(airport,document);
            createSheetsLights(airport,document);
            createSheetsRunways(airport,document);
            createSheetsNotes(airport,document);            



            // part 2
            document.newPage();

            
            createSheets2Name(airport,document);
            createSheets2Freqs(airport,document);
            createSheets2ILS(airport,document);
            //createSheets2AddImage(airport,document,pngName2,keepPng);

            
            createSheets2AddSvg(airport,writer,document,drawObjectsInMap,drawTerrainInMap);





            /*table.setWidthPercentage(50);
            table.setHorizontalAlignment(Element.ALIGN_RIGHT);
            document.add(table);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
            */



             // step 5: we close the document
            document.close();
            
            notifyAction("");

        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }





    public static void writeAirportImage(Airport airport, String filename,String backgroundColor_,boolean drawObjectsInAirport,boolean drawObjectsInMap,boolean drawTerrainInMap) {
        //Image image = new Image( Display.getCurrent(),400, 400);


        String pdfName=filename+".pdf";
        /*String pngName1=filename+"_airport.png";
        String pngName2=filename+"_map.png";*/


        backgroundColor=backgroundColor_;

        final AirportPainter ap = new AirportPainter(airport);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();


        

        width=520;
        height= ((int) PageSize.A4.height())- 150-30*airport.getRunwayCount()-100;
        //int height=740;
        

        //BufferedImage bimage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);

        //Graphics2D g2d = bimage.createGraphics();
        Document document = new Document();
        
        
        PdfWriter writer;
        try {
            try {

                writer = PdfWriter.getInstance(document, new FileOutputStream(pdfName));
                document.open();
                g2d = writer.getDirectContent().createGraphicsShapes(PageSize.A4.width(), PageSize.A4.height());
                ap.drawAirport(g2d, 0, 0, width, height-30,drawObjectsInAirport);
                ap.createSheets1(airport, writer,document,pdfName,drawObjectsInMap,drawTerrainInMap);
            } catch (DocumentException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }


        /*
        if (! backgroundColor.equals("")) {

            g2d.setColor(Color.decode("0x"+backgroundColor));
            Polygon p = new Polygon();
            p.addPoint(0, 0);
            p.addPoint(width, 0);
            p.addPoint(width, height);
            p.addPoint(0, height);
            p.addPoint(0, 0);
            g2d.fillPolygon(p);

        }

        System.out.print("Creating:"+pngName1+" ... ");
        */
        /*
        File outputfile = new File(pngName1);
        try {
            ImageIO.write(bimage, "png", outputfile);
        } catch(Exception e) {
            System.out.println(e);
        }

        */

        


        /*// doing PDF now
        Document document = new Document();
        try {

            

            System.out.println(pdfName+" OK");
            PdfWriter.getInstance(document, new FileOutputStream(pdfName));

            // step 3: we open the document
            document.open();

            // step 4: we add some paragraphs to the document
            document.add(
                new Paragraph(airport.getId()+" - "+airport.getName())
            );

            Image im;
            try {
                im = Image.getInstance(filename);

                document.add(im);

                 // step 5: we close the document
                document.close();

            } catch (BadElementException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
            }




        } catch (FileNotFoundException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(AirportPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        /*if (! keepPng) {
            outputfile.delete();
        }*/

    }





}
