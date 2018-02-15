package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ClearFileContent {

    public static boolean clear(IUser user){
        String pathAsString = CreateDataFile.getPathAsString();
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(pathAsString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        printWriter.close();
        return true;
    }

}
