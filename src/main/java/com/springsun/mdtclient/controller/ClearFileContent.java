package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClearFileContent {
    private static Logger log = Logger.getLogger(ClearFileContent.class.getName());

    public static boolean clear(IUser user){
        String pathAsString = CreateDataFile.getPathAsString();
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(pathAsString);
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, "FileNotFoundException in clear() : ", e);
            //e.printStackTrace();
            return false;
        }
        printWriter.close();
        return true;
    }

}
