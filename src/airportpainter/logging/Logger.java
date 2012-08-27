/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airportpainter.logging;

import java.util.EnumSet;

/**
 *
 * @author francesco
 */
public class Logger {
    
    public enum Level { DEBUG,INFO,ERROR  };
    public enum Realm { GENERIC,AIRPORT,MAP  };
    
    public static Level minimumLevel=Level.INFO;
    public static EnumSet<Realm> activeRealms=EnumSet.of(Realm.GENERIC,Realm.MAP);
    
            
    public static void log(String text) {
        log(text, Level.INFO, Realm.GENERIC);
    }
    public static void log(String text,Level myLevel) {
        log(text,myLevel, Realm.GENERIC);
    }
    public static void logException(Exception e,Realm myRealm) {
        log(e.toString()+"\r\n"+e.getStackTrace().toString(),Level.ERROR, myRealm);
    }
    public static void logException(VirtualMachineError e,Realm myRealm) {
        log(e.toString()+"\r\n"+e.getStackTrace().toString(),Level.ERROR, myRealm);
    }
    public static void log(String text,Level myLevel,Realm myRealm) {
        if (
            activeRealms.contains(myRealm) &&
            myLevel.compareTo(minimumLevel)>=0
        )  {
            System.out.println(text);            
        }
    }
}
