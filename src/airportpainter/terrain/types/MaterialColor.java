/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package airportpainter.terrain.types;

import airportpainter.Main;
import airportpainter.logging.Logger;
import java.awt.Color;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author francesco
 */
public class MaterialColor {

    /**
     * @return the backGroundMaterialColor
     */
    public static MaterialColor getBackGroundMaterialColor() {
        if (backGroundMaterialColor==null) {
            getAllSharedObjectsDefinistions();
        }
        return backGroundMaterialColor;
    }

    /**
     * @param aBackGroundMaterialColor the backGroundMaterialColor to set
     */
    public static void setBackGroundMaterialColor(MaterialColor aBackGroundMaterialColor) {
        backGroundMaterialColor = aBackGroundMaterialColor;
    }

   
    private String name="";
    private Color color=null;

    private boolean drawInAirport=false;
    private boolean drawInMap=true;


   
    private static ArrayList<MaterialColor> allMaterialColorDefinitions=null;
    public static MaterialColor defaultMaterialColor=null;
    private static MaterialColor backGroundMaterialColor=null;

    private static HashMap<String, String> missedHit=new HashMap<String, String>();
    

    public MaterialColor(String name,Color color) {
        this.name=name;
        this.color=color;
    }


    /**
     * @return the allSharedObjectsDefinistions
     */
    public static List<MaterialColor> getAllSharedObjectsDefinistions() {
        if (allMaterialColorDefinitions==null) {
            allMaterialColorDefinitions=new ArrayList<MaterialColor>();


            try {                
                URL fx =Main.getFileFromResource("/config/material_colors.xml");
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

                                
                                defaultMaterialColor=new MaterialColor(
                                    "default",
                                    Color.decode(
                                        tempElement.getAttributeValue("color", "#FFFFFF")
                                    )
                                );
                            }

                            Iterator d2= root.getChildren("background").iterator();

                            while (d2.hasNext()) {
                                org.jdom.Element tempElement=(org.jdom.Element) d2.next();

                                
                                setBackGroundMaterialColor(new MaterialColor(
                                       "background",
                                       Color.decode(
                                           tempElement.getAttributeValue("color", "#FFFFFF")
                                       )
                                   ));
                            }


                            Iterator i= root.getChildren("material").iterator();

                            while (i.hasNext()) {
                                org.jdom.Element tempElement=(org.jdom.Element) i.next();

                                MaterialColor tempSOS=new MaterialColor(
                                    tempElement.getAttributeValue("name", ""),
                                    Color.decode(tempElement.getAttributeValue("color", "#FFFFFF"))
                                );

                                tempSOS.setDrawInAirport(
                                    tempElement.getAttributeValue("drawInAirport", "true").equals("true")
                                );
                                tempSOS.setDrawInMap(
                                    tempElement.getAttributeValue("drawInMap", "true").equals("true")
                                );


                                allMaterialColorDefinitions.add(tempSOS);
                            }





                        }











                    } catch (Exception E) {
                        // Parser with specified options can't be built
                        Logger.logException(E, Logger.Realm.MAP);
                    }
                }
            } catch (URISyntaxException ex) {
                Logger.logException(ex, Logger.Realm.MAP);
            }





        }
        return allMaterialColorDefinitions;
    }

    public static MaterialColor getFromModelOrDefault(String model) {
        MaterialColor temp=getFromModel(model);
        if (temp==null) {
            if (defaultMaterialColor==null) {
                defaultMaterialColor=new MaterialColor("default", Color.white);
            }
            
            if (! missedHit.containsKey(model)) {
                missedHit.put(model, model);
                Logger.log("Unknown material:"+model, Logger.Level.DEBUG);
            }
            
            return defaultMaterialColor;
        }

        return temp;
    }


    public static  MaterialColor getFromModel(String model) {
        for (MaterialColor t: getAllSharedObjectsDefinistions()) {
            if (t.getName().toLowerCase().equals(model.toLowerCase())) {
                return t;
            }
        }

        return null;
    }

    /**
     * @param aAllSharedObjectsDefinistions the allSharedObjectsDefinistions to set
     */
    public static void setAllSharedObjectsDefinistions(ArrayList<MaterialColor> aAllSharedObjectsDefinistions) {
        allMaterialColorDefinitions = aAllSharedObjectsDefinistions;
    }

    /**
     * @return the model
     */
    public String getName() {
        return name;
    }

    /**
     * @param model the model to set
     */
    public void setName(String model) {
        this.name = model;
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
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
