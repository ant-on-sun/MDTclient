package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClearFileContent {
    private static Logger log = Logger.getLogger(ClearFileContent.class.getName());

    public static boolean clear(IUser user){
        String pathAsString = "";
        try {
            if (CreateDataFile.createFile(user)){
                pathAsString = CreateDataFile.getPathAsString();
            } else throw new Exception("File is not exist and couldn't be made for some reason");
        } catch (Exception e){
            log.log(Level.SEVERE, "Exception caught in ClearFileContent while trying to get path from url: ", e);
            return false;
        }

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
