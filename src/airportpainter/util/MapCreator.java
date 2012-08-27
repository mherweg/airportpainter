/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package airportpainter.util;


import airportpainter.Airport;
import airportpainter.Main;
import airportpainter.ObjectShared;
import airportpainter.Runway;
import airportpainter.SharedObjectsSvg;
import airportpainter.earth.Coordinate;
import airportpainter.earth.Earth;
import airportpainter.logging.Logger;
import airportpainter.magvar.CoreMag;
import airportpainter.terrain.BTG;
import airportpainter.terrain.BTGObject;
import airportpainter.terrain.BTGObjectElement;
import airportpainter.terrain.types.MaterialColor;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author francesco
 */
public class MapCreator {

    private Airport airport;

    Coordinate upperLeft=null;
    Coordinate lowerRight=null;

    ArrayList<Rectangle> areasFilled = new ArrayList<Rectangle>();

    final double scale1=100; // meter per pixel
    int fontMoltiplier=1;

    int width=520; // *2 to increase DPI from 72 to 144 to 288
    int height=width;
    //int height=740;
    Rectangle all=new Rectangle(
        width,height
    );

    float height1=0;

    public PdfWriter writer=null;
    public Document document=null;

    private void addArea(Rectangle area) {

        if (area.width==0) {
            area.width=1;
        }
        if (area.height==0) {
            area.height=1;
        }

        areasFilled.add(area);

        
        
        
        /*
        g2d.setColor(Color.GREEN);
        g2d.drawRect(area.x,area.y,area.width,area.height);
        g2d.setColor(Color.black);
        */

    }


    private Point coordinateToPoint(Coordinate from) {
        Point res=new Point();


        res.x = (int ) (((double)width) *   (from.getLongitude()-upperLeft.getLongitude() )   / (   lowerRight.getLongitude()-upperLeft.getLongitude()  ) );
        res.y = (int ) (((double)height) *   (from.getLatitude()-upperLeft.getLatitude() )   / (   lowerRight.getLatitude()-upperLeft.getLatitude()  ) );

        return res;
    }


    private boolean isFreeArea(Rectangle area,ArrayList<Rectangle> areasFilled_) {
        for (Rectangle test:areasFilled_) {
            if ( 
                test.intersects(area) ||  // collision with an other object
                (! all.contains(area))    // outside area
            ) {
                return false;
            }
        }

        return true;
    }
    

    HashMap<String, ArrayList<Rectangle>> namedAreasFilled=new HashMap<String, ArrayList<Rectangle>>();
    private boolean isFreeArea(Rectangle area,String objType) {
        if (objType==null) {
            return isFreeArea(area, areasFilled);
        }
        
        if (! namedAreasFilled.containsKey(objType)) {
            namedAreasFilled.put(objType, new ArrayList<Rectangle>());
        }        
        
        return isFreeArea(area, namedAreasFilled.get(objType) );
    }
    
    private boolean isFreeAreaAndSetOccupied(Rectangle area,String objType,boolean setOccupied) {
        boolean res=isFreeArea(area,objType);
        
        if (res) {
            
            
            if (setOccupied) {
//                g2d.setColor(Color.RED);
//                g2d.drawRect(area.x,area.y,area.width,area.height);
//                g2d.setColor(Color.black);            
                
                namedAreasFilled.get(objType).add(area);
            }
        }
        
        return res;
        
    }
    
    private boolean isFreeArea(Rectangle area) {
        return isFreeArea(area, areasFilled);
    }
    

    private void colorPath(Point lastP,Point p,Color color) {
        
        /*g2d.setColor(color);
        g2d.drawLine(lastP.x,lastP.y, p.x,p.y);
        g2d.setColor(Color.black);*/
        
    }
    private void redColorPath(Point lastP,Point p) {
        colorPath(lastP,p,Color.red);
    }

    boolean findFreePointFromOk=false;
    private Point findFreePointFrom(Dimension size,Point start) {
        int maxOpsRemaining=100;
        findFreePointFromOk=false;

        Point p=new Point(start.x,start.y);
        Point lastP=new Point(p.x,p.y);

        Rectangle test=new Rectangle(p,size);

        int rayInc=10;
        int ray=rayInc;


        boolean found=false;
        while ((! found ) && maxOpsRemaining>0) {
            found=true;
            
            if (isFreeArea(  test   )) {
                findFreePointFromOk=true;

                p=new Point(test.x,test.y);


                return p;
            }   else {

                lastP.x=p.x;
                lastP.y=p.y;

                p.y=start.y-ray;
                for (p.x=start.x-ray-size.width;p.x<=start.x+ray;p.x++) {
                    redColorPath(lastP, p);

                    test=new Rectangle(p,size);
                    if (isFreeArea(  test   )) {
                        p=new Point(test.x,test.y);
                        return p;
                    }

                    lastP.x=p.x;
                    lastP.y=p.y;
                }


                p.x=start.x+ray;
                for (p.y=start.y-ray-size.height;p.y<=start.y+ray;p.y++) {
                    redColorPath(lastP, p);

                    test=new Rectangle(p,size);
                    if (isFreeArea(  test   )) {
                        p=new Point(test.x,test.y);
                        return p;
                    }

                    lastP.x=p.x;
                    lastP.y=p.y;
                }

                p.y=start.y+ray;
                for (p.x=start.x+ray;p.x>=start.x-ray-size.width;p.x--) {
                    redColorPath(lastP, p);

                    test=new Rectangle(p,size);
                    if (isFreeArea(  test   )) {
                        p=new Point(test.x,test.y);
                        return p;
                    }

                    lastP.x=p.x;
                    lastP.y=p.y;
                }

                p.x=start.x-ray;
                for (p.y=start.y+ray;p.y>=start.y-ray-size.height;p.y--) {
                    redColorPath(lastP, p);

                    test=new Rectangle(p,size);
                    if (isFreeArea(  test   )) {
                        p=new Point(test.x,test.y);
                        return p;
                    }

                    lastP.x=p.x;
                    lastP.y=p.y;
                }


                ray=ray+rayInc;

                redColorPath(lastP, p);
                
                test=new Rectangle(p,size);

                found=false;

                maxOpsRemaining--;
            }
        }

        if (maxOpsRemaining==0) {
            // error
            airportpainter.logging.Logger.log("No more space on chart !",airportpainter.logging.Logger.Level.ERROR);
            return new Point(start.x,start.y);
        }

        // error
        return new Point(start.x,start.y);

    }






    /**
	 * Draws a little compass rose
	 * @param gc
	 * @param canvas_width
	 * @param canvas_height
	 */
	private void drawCompassRose(final int x, final int y, final int rayLenght) {
		// Generate the text using a Serif font

		try  {
			g2d.setColor(Color.black);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);


                        int rowLength=rayLenght;

                        //final int inc = (int) (device.getDPI().x * .05);
                        //final int inc = 1;
			//final int offsetX = x + width - inc * 3 ;
			//final int offsetY = y + inc * 4;
			final int offsetX = x + rayLenght;
			final int offsetY = y + rayLenght;
			// Draw a little arrow

                        g2d.translate(offsetX,offsetY);



                        // draw true north
                        g2d.drawLine(0, 0, 0, -rowLength);

                        Polygon p= new Polygon();
                        p.addPoint(0, -rowLength);
                        p.addPoint(-1, -(rowLength-4));
                        p.addPoint(-4, -(rowLength-12));
                        p.addPoint(0, -(rowLength-15));
                        p.addPoint(4, -(rowLength-12));
                        p.addPoint(1, -(rowLength-4));
                        p.addPoint(0, -rowLength);
                        g2d.fillPolygon(p);

                        // draw magnetic north
                        double variation=CoreMag.getMagneticVariation(airport);
                        g2d.rotate(variation*3.1416/180);

                        g2d.drawLine(0, 0, 0, -rowLength);

                        Polygon pm= new Polygon();
                        pm.addPoint(0, -rowLength);
                        pm.addPoint(0, -(rowLength-12));
                        pm.addPoint(4, -(rowLength-12));
                        pm.addPoint(1, -(rowLength-4));
                        pm.addPoint(0, -rowLength);
                        g2d.fillPolygon(pm);



                        {
                            String EoW="E";
                            double v2=variation;
                            if (v2<0) {
                                EoW="W";
                                v2=-v2;
                            }
                            String text=("VAR "+ ((double) ((int) variation*10)/10)+" "+EoW);

                            final Font font = new Font("Times", Font.PLAIN,8*fontMoltiplier); //$NON-NLS-1$
                            g2d.setFont(font);
                            //final Point textExtent = gc.getFontMetrics().get textExtent("N"); //$NON-NLS-1$

                           // get metrics from the graphics
                            FontMetrics metrics = g2d.getFontMetrics(font);
                            // get the height of a line of text in this font and render context
                            int hgt = metrics.getHeight();
                            // get the advance of my text in this font and render context
                            int adv = metrics.stringWidth(text);


                            g2d.rotate(-90*3.1416/180);

                            g2d.drawString(text  , rowLength/2-adv/2, hgt+4);

                            g2d.rotate(90*3.1416/180);
                        }



                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);




                        g2d.rotate(-variation*3.1416/180);
                        g2d.translate(-offsetX,-offsetY);

		} finally  {
			//font.dispose();
		}
	}



    Graphics2D g2d = null;
    /**
     *
     */
    public MapCreator(final Airport airport_,float height1_) {
            airport = airport_;
            height1=height1_;
            // Calculate all the parameters we need to paint the airport
            //_extents = calcAirportExtent(_airport);
    }


    private void drawAnAirport(Airport tempAirport) {
        notifyAction("drawAnAirport");
        // draw runways
        for (Runway runway:tempAirport.getRunways()) {

            g2d.translate(
                coordinateToPoint( new Coordinate(runway.getLat(),runway.getLong())).x,
                coordinateToPoint( new Coordinate(runway.getLat(),runway.getLong())).y
            );





            g2d.rotate( Math.toRadians(runway.getHeading() ) );


            g2d.setColor(Color.decode("0x000000") );
            int hl=(runway.getLength() / ((int) AirportPainter.FEET_PER_METER) /((int) scale1))/2; // half runway length in pixels

            g2d.fillRect( -2 ,-hl,2,hl*2);

            g2d.rotate( Math.toRadians(-runway.getHeading() ) );




            g2d.translate(
                -coordinateToPoint( new Coordinate(runway.getLat(),runway.getLong())).x,
                -coordinateToPoint( new Coordinate(runway.getLat(),runway.getLong())).y
            );

            addArea(
                new Rectangle(
                    coordinateToPoint( new Coordinate(runway.getLat(),runway.getLong())).x- ((int) (hl* Math.abs(Math.sin(Math.toRadians(runway.getHeading()))))) ,
                    coordinateToPoint( new Coordinate(runway.getLat(),runway.getLong())).y- ((int) (hl* Math.abs(Math.cos(Math.toRadians(runway.getHeading()))))),
                    ((int) (hl* Math.abs(Math.sin(Math.toRadians(runway.getHeading())))))*2,
                    ((int) (hl* Math.abs(Math.cos(Math.toRadians(runway.getHeading())))))*2
                )
            );

        }

        g2d.dispose();

    }

    public void createImage(boolean drawObjectsInMap,boolean drawTerrainInMap) {

        //GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //GraphicsDevice gs = ge.getDefaultScreenDevice();
        //GraphicsConfiguration gc = gs.getDefaultConfiguration();





        //BufferedImage bimage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);

        //g2d = bimage.createGraphics();

        g2d = writer.getDirectContentUnder().createGraphicsShapes(PageSize.A4.width(), PageSize.A4.height());


        
        g2d.translate( 38,190 +  height1);

        //g2d.fillRect((int) document.left() ,(int) document.bottom(), (int) (document.right ()-document.left ()),
          //(int) (document.top ()-document.bottom ()));

        g2d.drawRect(0,0,width,height);


        final double scaleY=
                scale1                                                          // meter per pixel
                /                                                               // ---------------
                (AirportPainter.FEET_PER_DEGREE / AirportPainter.FEET_PER_METER)  // meter per degree
        ;       // degree per pixel


        final double scaleX=
                scale1                                                          // meter per pixel
                /                                                               // ---------------
                ((AirportPainter.FEET_PER_DEGREE*Math.abs(Math.cos(Math.toRadians(airport.getLat())))) / AirportPainter.FEET_PER_METER)  // meter per degree
        ;       // degree per pixel

        int wm=width/2;
        int hm=height/2;

        //final double offsetLatUpLeft = (airport.getLat()) * AirportPainter.FEET_PER_DEGREE;
        //final double offsetLong = (airport.getTower().getLong()) * AirportPainter.FEET_PER_DEGREE *Math.abs(Math.cos(Math.toRadians(airport.getLat())));


        // find upper left corner latitude:
        double ulLat=airport.getLat()+hm*scaleY;
        // find upper left corner longitude:
        double ulLon=airport.getLong()-wm*scaleX;

        upperLeft=new Coordinate(ulLat,ulLon);

        
   
        // find lower right corner latitude:
        double lrLat=airport.getLat()-hm*scaleY;
        // find lower right corner longitude:
        double lrLon=airport.getLong()+wm*scaleX;

        lowerRight=new Coordinate(lrLat,lrLon);

        Logger.log("from:"+upperLeft.toString());
        Logger.log("to:"+lowerRight.toString());


        boolean drawTheAirports=true;
        boolean drawTheNavaids=true;
        boolean drawTheCompassRose=true;
        boolean drawTheScale=true;


        if (drawTerrainInMap) {            
            drawTerrain();        
        }
        

        if (drawObjectsInMap) {
            drawObjects();
        }
        //drawBorderSvg();


        // cerco i navaid nell'area

        final AutoPlanner planner = new AutoPlanner(Earth.getNavAids());
        planner.setAirports(Earth.getAirports());


        List<Airport> airports=planner.getAirportBeetween(upperLeft,lowerRight);
        if (drawTheAirports) {
            for (Airport tempAirport:airports) {
                drawAnAirport(tempAirport);
            }            
        }

        List<NavAid> insertedNavaids=new ArrayList<NavAid>();

        List<NavAid> nl=planner.getNavAidsBeetween(upperLeft,lowerRight);
        if (drawTheNavaids) {

            
            notifyAction("drawTheNavaids");
            for (NavAid navAid:nl) {
                int niw=0;
                int nih=0;

                g2d.translate( coordinateToPoint(navAid.getLoc()).x,coordinateToPoint(navAid.getLoc()).y );

                boolean inserted=false;

                //BufferedImage image=null;
                if (navAid.getType()==NavAid.VOR) {
                    try {

                        SVGUniverse universe = SVGCache.getSVGUniverse();
                        URI uri = universe.loadSVG(                                
                            Main.getFileFromResource(Main.imgFolder+"vor.svg")
                        );
                        SVGDiagram diagram = universe.getDiagram(uri);
                        //diagram.setIgnoringClipHeuristic(true);

                        niw=48;
                        nih=niw;

                        float fs=1;

                        float fsw=(float) (niw/((double) diagram.getRoot().getShape().getBounds2D().getWidth()));
                        float fsh=(float) (nih/((double) diagram.getRoot().getShape().getBounds2D().getHeight()));

                        fs=fsw;
                        if (fsh<fs) {
                            fs=fsh;
                        }

                        g2d.scale( fs,fs);

                        double variation=CoreMag.getMagneticVariation(airport);
                        g2d.rotate(Math.toRadians(variation));

                        
                        //g2d.clipRect(all.x,all.y,all.width,all.height);
                        try {

                            diagram.render(g2d);
                        } catch (SVGException ex) {
                            Logger.logException(ex, Logger.Realm.MAP);
                        }

                        g2d.rotate(-Math.toRadians(variation));

                        g2d.scale(1/fs,1/fs);

                        inserted=true;

                    //} catch (FileNotFoundException ex) {
                            //Logger.logException(ex, Logger.Realm.MAP);
                    //} catch (IOException ex) {
                            //Logger.logException(ex, Logger.Realm.MAP);
                    } catch (URISyntaxException ex) {
                            Logger.logException(ex, Logger.Realm.MAP);
                    }

                    //g2d.drawString(navAid.getName(), 0, 0);
                }


                if (navAid.getType()==NavAid.NDB) {

                    try {

                        SVGUniverse universe = SVGCache.getSVGUniverse();
                        URI uri = universe.loadSVG(
                            Main.getFileFromResource(Main.imgFolder+"ndb.svg")
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
                            Logger.logException(ex, Logger.Realm.MAP);
                        }

                        g2d.scale(1/fs,1/fs);

                        inserted=true;

                    //} catch (FileNotFoundException ex) {
                            //Logger.logException(ex, Logger.Realm.MAP);
                    //} catch (IOException ex) {
                            //Logger.logException(ex, Logger.Realm.MAP);
                    } catch (URISyntaxException ex) {
                            Logger.logException(ex, Logger.Realm.MAP);                            
                    }
                }



                g2d.translate( -coordinateToPoint(navAid.getLoc()).x,-coordinateToPoint(navAid.getLoc()).y );

                //if (image!=null) {
                    //if (navAid.getName().equals("CAMERI NDB")) {
                if (inserted) {
                    addArea(
                        new Rectangle(
                            coordinateToPoint(navAid.getLoc()).x-niw/2,
                            coordinateToPoint(navAid.getLoc()).y-nih/2,
                            niw,
                            nih
                        )
                    );

                    insertedNavaids.add(navAid);
                }
            }


            for (NavAid navAid:nl) {
                int niw=0;
                int nih=0;

                g2d.translate( coordinateToPoint(navAid.getLoc()).x,coordinateToPoint(navAid.getLoc()).y );

                boolean inserted=false;
                if (navAid.getType()==NavAid.TACAN) {

                    boolean skip=false;

                    // check if not alread inserted as vor or ndb
                    for (NavAid ina:insertedNavaids) {
                        if (ina.getFreq()==navAid.getFreq()) {
                            // skip
                            skip=true;
                        }
                    }

                    if (! skip) {
                        try {
                            SVGUniverse universe = SVGCache.getSVGUniverse();
                            URI uri = universe.loadSVG(
                                Main.getFileFromResource(Main.imgFolder+"tacan.svg")
                            );
                            SVGDiagram diagram = universe.getDiagram(uri);
                            //diagram.setIgnoringClipHeuristic(true);

                            niw=48;
                            nih=niw;

                            float fs=1;

                            float fsw=(float) (niw/((double) diagram.getRoot().getShape().getBounds2D().getWidth()));
                            float fsh=(float) (nih/((double) diagram.getRoot().getShape().getBounds2D().getHeight()));

                            fs=fsw;
                            if (fsh<fs) {
                                fs=fsh;
                            }

                            g2d.scale( fs,fs);

                            double variation=CoreMag.getMagneticVariation(airport);
                            g2d.rotate(Math.toRadians(variation));

                            try {
                                diagram.render(g2d);
                            } catch (SVGException ex) {
                                Logger.logException(ex, Logger.Realm.MAP);
                            }

                            g2d.rotate(-Math.toRadians(variation));

                            g2d.scale(1/fs,1/fs);

                            inserted=true;


                        //} catch (FileNotFoundException ex) {
                            //Logger.logException(ex, Logger.Realm.MAP);
                        //} catch (IOException ex) {
                            //Logger.logException(ex, Logger.Realm.MAP);
                        } catch (URISyntaxException ex) {
                            Logger.logException(ex, Logger.Realm.MAP);                            
                        }
                    }



                    //g2d.drawString(navAid.getName(), 0, 0);
                }

                g2d.translate( -coordinateToPoint(navAid.getLoc()).x,-coordinateToPoint(navAid.getLoc()).y );

                if (inserted) {
                    addArea(
                        new Rectangle(
                            coordinateToPoint(navAid.getLoc()).x-niw/2,
                            coordinateToPoint(navAid.getLoc()).y-nih/2,
                            niw,
                            nih
                        )
                    );

                    insertedNavaids.add(navAid);
                }


            }

        }




        final Font fontTitle = new Font("Times", Font.PLAIN,10*fontMoltiplier); //$NON-NLS-1$
        final Font fontID = new Font("Times", Font.BOLD,12*fontMoltiplier); //$NON-NLS-1$
        
        // draw texts
        notifyAction("drawTexts");
        if (drawTheAirports) {

            for (Airport tempAirport:airports) {
                String testoTitle=tempAirport.getName();

                if (
                    true
                ) {


                    // get metrics from the graphics
                    FontMetrics metrics = g2d.getFontMetrics(fontTitle);
                    // get the height of a line of text in this font and render context
                    int hgtTitle = metrics.getHeight();
                    // get the advance of my text in this font and render context
                    int advTitle = metrics.stringWidth(testoTitle);



                    Logger.log(tempAirport.getName());
                    //if (navAid.getName().equals("NOVARA NDB")) {
                        //int a=0;
                    //}


                    int dimX=advTitle;
                    dimX=dimX+4;

                    int dimY=hgtTitle+4;

                    Point p=findFreePointFrom(
                        new Dimension(dimX,dimY),
                        coordinateToPoint(
                            tempAirport.getLoc()
                        )
                    );


                    g2d.translate( p.x,p.y);



                    g2d.setFont(fontTitle);
                    g2d.drawString(testoTitle, dimX/2-advTitle/2, hgtTitle);


                    g2d.translate( -p.x,-p.y);


                    addArea(
                        new Rectangle(
                            p.x,
                            p.y,
                            dimX,
                            dimY
                        )
                    );


                }
            }
        }


        if (drawTheNavaids) {

            for (NavAid navAid:insertedNavaids) {

                String testoTitle=navAid.getTypeName();
                String testo=new Double(navAid.getFreq()).toString()+" "+ navAid.getId();

                if (
                    true
                ) {


                    // get metrics from the graphics
                    FontMetrics metrics = g2d.getFontMetrics(fontTitle);
                    // get the height of a line of text in this font and render context
                    int hgtTitle = metrics.getHeight();
                    // get the advance of my text in this font and render context
                    int advTitle = metrics.stringWidth(testoTitle);

                    // get metrics from the graphics
                    FontMetrics metricsId = g2d.getFontMetrics(fontID);
                    // get the height of a line of text in this font and render context
                    int hgtID = metricsId.getHeight();
                    // get the advance of my text in this font and render context
                    int advID = metricsId.stringWidth(testo);







                    //System.out.println(navAid.getName());
                    //if (navAid.getName().equals("NOVARA NDB")) {
                        //int a=0;
                    //}

//                    if (navAid.getType()==NavAid.TACAN) {
//                        int a=0;
//                    }

                    int dimX=advTitle;
                    if (advID>advTitle) {
                        dimX=advID;
                    }
                    dimX=dimX+4;

                    int dimY=hgtTitle+hgtID+4;

                    Point p=findFreePointFrom(
                        new Dimension(dimX,dimY),
                        coordinateToPoint(
                            navAid.getLoc()
                        )
                    );


                    g2d.translate( p.x,p.y);


                    g2d.drawLine(0,hgtTitle/2, dimX/2-advTitle/2-2,hgtTitle/2); // up left to right
                    g2d.drawLine(0,hgtTitle/2,0,hgtTitle+hgtID+3); // up left to bottom
                    g2d.drawLine(0,hgtTitle+hgtID+3,dimX,hgtTitle+hgtID+3); // down left to right
                    g2d.drawLine(dimX,hgtTitle/2,dimX,hgtTitle+hgtID+3); // up right to bottom
                    g2d.drawLine(dimX/2+advTitle/2+2 ,hgtTitle/2,dimX,hgtTitle/2); // up right to left



                    g2d.setFont(fontTitle);
                    g2d.drawString(testoTitle, dimX/2-advTitle/2, hgtTitle);

                    g2d.setFont(fontID);
                    g2d.drawString(testo, 2, hgtTitle+hgtID);

                    g2d.translate( -p.x,-p.y);


                    addArea(
                        new Rectangle(
                            p.x,
                            p.y,
                            dimX,
                            dimY
                        )
                    );

                }
            }
        }

        if (drawTheCompassRose) {

            // compass rose
            Point p=findFreePointFrom(
                new Dimension(100,100),
                new Point(10,10)
            );
            drawCompassRose(p.x,p.y,100/2);
            addArea(
                new Rectangle(
                    p.x,
                    p.y,
                    100,
                    100
                )
            );

        }



        if (drawTheScale) {


            // drawing scale
            final int lineLength=10;
            final int oneEveryKm=10;
            int x=0;
            while (x<width)  {
                //one line every oneEveryKm km.
                if (x>0){
                    g2d.drawLine(x,0, x,lineLength);
                    g2d.drawLine(x,height, x,height-lineLength);
                }

                x=x+(int) (oneEveryKm*1000/scale1);
            }

            int y=0;
            while (y<height)  {
                //one line every oneEveryKm km.
                if (y>0){
                    g2d.drawLine(0,y, lineLength,y);
                    g2d.drawLine(width,y, width-lineLength,y);
                }

                y=y+(int) (oneEveryKm*1000/scale1);
            }


        }

        //g2d.clipRect(all.x,all.y,all.width,all.height);

//        Polygon p = new Polygon();
//        p.addPoint(0,0);
//        p.addPoint(100,100);
//        p.addPoint(0,100);
//        g2d.setColor(Color.red);
//        g2d.fillPolygon(p);
        
        g2d.dispose();


        //return bimage;

    }

    private void drawTerrain() {        
        notifyAction("drawTerrain");
        
        //SVGUniverse universe = SVGCache.getSVGUniverse();
        List<BTGObject> objects=BTG.loadAllTerrainIn(upperLeft,lowerRight,width,height);        
        // now I have all objects
        
        // drawing background...
        g2d.setColor(MaterialColor.getBackGroundMaterialColor().getColor());
        g2d.fillRect(0,0,all.width-0,all.height-0);          
        
        
        
        //int colDiff=0;

        
        HashMap<String,Integer> materialFound=new HashMap<String,Integer>();
        HashMap<String,Integer> materialNotFound=new HashMap<String,Integer>();
        
        
        //for (MaterialColor mcnow:MaterialColor.getAllSharedObjectsDefinistions()) {
            //if (mcnow.isDrawInMap()) {
                for (BTGObject o:objects) {
                    if (
                        o.materialName!=null //&&
                        //o.materialName.toLowerCase().equals(mc.getName().toLowerCase())
                    ) {
                        
                        materialNotFound.put(o.materialName, 1);

                        MaterialColor mc=MaterialColor.getFromModelOrDefault(o.materialName);

                        
                        if (mc.isDrawInMap()) {
                            Color colore=mc.getColor();
                            
                            if (mc!=MaterialColor.defaultMaterialColor) {
                                materialFound.put(o.materialName, 1);
                            }
                            
                            if (
                                colore.getRGB()!=MaterialColor.getBackGroundMaterialColor().getColor().getRGB()
                            ) {
                                
                            
                                for (BTGObjectElement e:o.getObjectElements()) {
                                    if (
                                        e.type==BTGObject.TYPE_TRIANGLE_FACES ||
                                        e.type==BTGObject.TYPE_TRIANGLE_STRIPS ||
                                        e.type==BTGObject.TYPE_TRIANGLE_FANS ||
                                        true
                                    ) {
                                        if (colore!=null) {
                                            for (Polygon p:e.poligons) {

                                                g2d.setColor(colore);
//                                                g2d.setColor(e.mcdebug);
//                                                //g2d.setColor(new Color((colDiff & 1)*255, (colDiff & 2)/2*255, (colDiff & 4)/4*255));
                                                g2d.fillPolygon(p);
                                                //g2d.setColor(new Color((colDiff & 1)*255, (colDiff & 2)*255, (colDiff & 4)*255));
                                                //g2d.setColor(Color.RED);
                                                g2d.drawPolygon(p);
                                                g2d.clipRect(0,0,all.width-0,all.height-0);                                    
                                                
                                                
                                                
                                                //colDiff++;
                                                //colDiff=colDiff%8;
                                                
                                            }

                                        }                    
                                    }
                                }

                            } else {
                                Logger.log("Skipping material since its color is equal to background",Logger.Level.DEBUG);
                            }
                        }
                    }
                }

            //}     
        //}        
                
        materialNotFound.keySet().removeAll(materialFound.keySet());
                
                
        for(String mat:materialNotFound.keySet()) {
            Logger.log("Unknown material:"+mat,Logger.Level.INFO);
        }
        
    }

    HashMap<String, SharedObjectsSvg> notDrawn=new HashMap<String, SharedObjectsSvg>();

    private void drawObjects() {        
        notifyAction("drawObjects");

        SVGUniverse universe = SVGCache.getSVGUniverse();
        List<ObjectShared> objects=ObjectShared.loadAllObjectsIn(upperLeft.getLongitude(),upperLeft.getLatitude(), lowerRight.getLongitude(),lowerRight.getLatitude());

        notDrawn.clear();

        for (ObjectShared hangar: objects) {
            if (hangar.getSos()!=null) {
                if (hangar.getSos().isDrawInMap()) {

                    URI svgURI=hangar.getSos().getURI();
                    if (svgURI!=null) {
                        SVGDiagram diagram = universe.getDiagram(svgURI);
                        //diagram.setIgnoringClipHeuristic(true);
                        int niw=0;
                        int nih=0;

                        {
                            // Load the img

                            niw=hangar.getSos().getSize();
                            nih=niw;

                            if (all.contains(coordinateToPoint( hangar.getLoc()).x,coordinateToPoint( hangar.getLoc()).y)) {
                                
                                
                                
                                float fs=1;

                                float fsw=(float) (niw/((double) diagram.getRoot().getShape().getBounds2D().getWidth()));
                                float fsh=(float) (nih/((double) diagram.getRoot().getShape().getBounds2D().getHeight()));

                                fs=fsw;
                                if (fsh<fs) {
                                    fs=fsh;
                                }


                                Rectangle rec=new Rectangle(
                                    coordinateToPoint(hangar.getLoc()).x-niw/2,
                                    coordinateToPoint( hangar.getLoc()).y-nih/2,
                                    niw, nih
                                );
                                
                                if (
                                    isFreeAreaAndSetOccupied(
                                        rec,
                                        hangar.getSos().getModel(),
                                        true
                                    )
                                ) {
                                    //g2d.drawRect(-2,-2,4,4);

                                    g2d.translate(coordinateToPoint( hangar.getLoc()).x,coordinateToPoint( hangar.getLoc()).y);
                                    //g2d.rotate(-(Math.toRadians(90+hangar.getRotation())));

                                    g2d.scale( fs,fs);
                                    
                                    try {
                                        diagram.render(g2d);
                                    } catch (SVGException ex) {
                                        Logger.logException(ex, Logger.Realm.MAP);
                                    }                                    
                                    
                                    g2d.scale(1/fs,1/fs);

                                    //g2d.rotate(+Math.toRadians((90+hangar.getRotation())));

                                    g2d.translate(-coordinateToPoint( hangar.getLoc()).x,-coordinateToPoint( hangar.getLoc()).y);                                    
                                } else {
                                    // area is already occupied
                                    Logger.log("a.c.",Logger.Level.DEBUG, Logger.Realm.MAP);
                                }
                                        
                                




                            }


                        }
                    }
                } else {
//                    if (! notDrawn.containsKey(hangar.getName())) {
//                        //System.out.println("GO:skypping "+hangar.getName()+" because drawInMap=false in xml config");                            
//                        notDrawn.put(hangar.getName(), hangar.getSos());                                
//                    }
                }
            } else {
                if (! notDrawn.containsKey(hangar.getName())) {
                    //System.out.println("GO:skypping "+hangar.getName()+" because not in xml config");
                    notDrawn.put(hangar.getName(), hangar.getSos());                                
                }
            }
        }
        
        
        for (String name:notDrawn.keySet()) {
            Logger.log("Not drawn:"+name,Logger.Level.DEBUG,Logger.Realm.MAP);
        }
        
    }





    private void drawBorderSvg() {
        SVGUniverse universe = SVGCache.getSVGUniverse();
    }
    
    private void notifyAction(String text) {
        Main.getPropertyChangeSupport().firePropertyChange("mapCreator", null, text);
    }
}
