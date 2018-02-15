package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

public class AddDataToFile {
    private static Logger log = Logger.getLogger(AddDataToFile.class.getName());
    public static boolean addData(String data, IUser user){
        String pathAsString = null;
        Path path = null;
        //File file = null;
        try {
            if (CreateDataFile.createFile(user)){
                pathAsString = CreateDataFile.getPathAsString();
            } else throw new Exception("File is not exist and couldn't be made for some reason");

//            file = new File(pathAsString);
//            if (!file.exists()) {
//                CreateDataFile.createFile(user);
//            }
            path = Paths.get(pathAsString);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception caught in AddDataToFile while trying to get path from url: ", e);
            return false;
        }

        List<String> list = Collections.singletonList(data);
        try {
            Files.write(path, list, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception caught in AddDataToFile while trying to add line to file: ", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
