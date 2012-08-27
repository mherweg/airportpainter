/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package airportpainter;

import airportpainter.logging.Logger;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGUniverse;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author francesco
 */
public class SharedObjectsSvg {

   
    private String model="";
    private String svgFile="";
    private int size=10;

    private boolean drawInAirport=false;
    private boolean drawInMap=false;

    private Pattern  pattern=null;

    SVGUniverse universe=null;
    URI uri=null;

    private static ArrayList<SharedObjectsSvg> allSharedObjectsDefinistions=null;
    private static String defaultModelString="";
    private static SharedObjectsSvg defaultModel=null;


    public SharedObjectsSvg(String model,String svgFile,int size) {
        this.model=model;
        this.svgFile=svgFile;
        this.size=size;
        
        pattern=Pattern.compile(model);
    }

    public URI getURI() {
        if (universe==null || uri==null) {
            universe = SVGCache.getSVGUniverse();
            //try {
                try {
                    uri = universe.loadSVG(Main.getFileFromResource(Main.imgFolder+svgFile));
                } catch (URISyntaxException ex) {
                    Logger.logException(ex, Logger.Realm.AIRPORT);
                }
            //} catch (IOException ex) {
                //Logger.logException(ex, Logger.Realm.AIRPORT);
                //return null;
            //}
        }

       return uri;
    }


    /**
     * @return the allSharedObjectsDefinistions
     */
    public static List<SharedObjectsSvg> getAllSharedObjectsDefinistions() {
        if (allSharedObjectsDefinistions==null) {
            allSharedObjectsDefinistions=new ArrayList<SharedObjectsSvg>();

            
            try {
            URL fx =Main.getFileFromResource("/config/shared_objects.xml");
            
            
            //java.io.File fx=new java.io.File("config/shared_objects.xml");
                {
                    // CARICA I DATI
                    SAXBuilder builder = new SAXBuilder();
                    try {

                        Document doc = builder.build(fx);

                        org.jdom.Element root = doc.getRootElement();

                        if (root != null) {
                            Iterator d= root.getChildren("default").iterator();

                            while (d.hasNext()) {
                                org.jdom.Element tempElement=(org.jdom.Element) d.next();

                                defaultModelString=tempElement.getAttributeValue("model", "");
                            }



                            Iterator i= root.getChildren("object").iterator();

                            while (i.hasNext()) {
                                org.jdom.Element tempElement=(org.jdom.Element) i.next();



                                SharedObjectsSvg tempSOS=new SharedObjectsSvg(
                                    tempElement.getAttributeValue("model", ""),
                                    tempElement.getAttributeValue("svg", ""),
                                    new Integer(tempElement.getAttributeValue("size", "")).intValue()
                                );

                                tempSOS.setDrawInAirport(
                                    tempElement.getAttributeValue("drawInAirport", "true").equals("true")
                                );
                                tempSOS.setDrawInMap(
                                    tempElement.getAttributeValue("drawInMap", "true").equals("true")
                                );


                                if (! defaultModelString.equals("")) {
                                    if (tempSOS.getModel().equals(defaultModelString)) {
                                        defaultModel=tempSOS;
                                    }
                                }


                                allSharedObjectsDefinistions.add(tempSOS);
                            }





                        }











                    } catch (Exception E) {
                        // Parser with specified options can't be built
                        Logger.logException(E, Logger.Realm.AIRPORT);
                    }
                }



            } catch (URISyntaxException ex) {
                Logger.logException(ex, Logger.Realm.AIRPORT);
            }


        }
        return allSharedObjectsDefinistions;
    }

    public static SharedObjectsSvg getFromModelOrDefault(String model) {
        SharedObjectsSvg temp=getFromModel(model);
        if (temp==null) {
            return defaultModel;
        }

        return temp;
    }


    public static  SharedObjectsSvg getFromModel(String model) {
        try {
            for (SharedObjectsSvg t: getAllSharedObjectsDefinistions()) {
                if (t.getPattern().matcher(model).matches()) {
                //if (t.getModel().equals(model)) {
                    return t;
                }
            }
        } catch (PatternSyntaxException e) {
            Logger.logException(e, Logger.Realm.MAP);
        }
        
        //Logger.log("Model not in xml:"+model,Logger.Level.INFO);
        
        return null;
    }

    /**
     * @param aAllSharedObjectsDefinistions the allSharedObjectsDefinistions to set
     */
    public static void setAllSharedObjectsDefinistions(ArrayList<SharedObjectsSvg> aAllSharedObjectsDefinistions) {
        allSharedObjectsDefinistions = aAllSharedObjectsDefinistions;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the drawInAirport
     */
    public boolean isDrawInAirport() {
        return drawInAirport;
    }

    /**
     * @param drawInAirport the drawInAirport to set
     */
    public void setDrawInAirport(boolean drawInAirport) {
        this.drawInAirport = drawInAirport;
    }

    /**
     * @return the drawInMap
     */
    public boolean isDrawInMap() {
        return drawInMap;
    }

    /**
     * @param drawInMap the drawInMap to set
     */
    public void setDrawInMap(boolean drawInMap) {
        this.drawInMap = drawInMap;
    }

    /**
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
