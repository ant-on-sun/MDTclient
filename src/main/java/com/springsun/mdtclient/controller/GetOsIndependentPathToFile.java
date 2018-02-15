package com.springsun.mdtclient.controller;

import java.util.logging.Logger;

public class GetOsIndependentPathToFile {
    private static Logger log = Logger.getLogger(GetOsIndependentPathToFile.class.getName());

    public static String getPath (String s) throws StringIndexOutOfBoundsException{
        int i;
        if (s.startsWith("file:/")) s = s.substring(6);
        if (System.getProperty("os.name").contains("indow")){
            i = s.lastIndexOf(":");
            if (i < 1){
                log.severe("Wrong path to a file:  " + s);
                throw new StringIndexOutOfBoundsException("wrong path to a file");
            }
            s = s.substring(i-1);
            log.fine("GetOsIndependentPathToFile getPath() it's Windows, baby");
            return s;
        }
        log.fine("GetOsIndependentPathToFile getPath() it's not Windows, baby");
        return s;
    }
}
