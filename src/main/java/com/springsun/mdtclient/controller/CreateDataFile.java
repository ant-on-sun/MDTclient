package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateDataFile {
    private static Logger log = Logger.getLogger(CreateDataFile.class.getName());
    private static String pathAsString;
    private static String pathDirectory;

    public static Boolean createFile(IUser user){
        pathDirectory = System.getProperty("user.home") + File.separator + "MDTclient" + File.separator
                + user.getLogin() + File.separator;
        pathAsString = pathDirectory + "DataFile.txt";
        try {
            Files.createDirectories(Paths.get(pathDirectory));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception while creating directories in path: " + pathDirectory, e);
        }
        if (new File(pathAsString).exists()) return true;
        List<String> lines = Arrays.asList("3:" + user.getCurrentLatitude() + ":" + user.getCurrentLongitude());
        try {
            Files.write(Paths.get(pathAsString), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception while creating file TotalScore.txt in path: " + pathAsString, e);
        }
        if (new File(pathAsString).exists()) return true;
        return false;
    }

    public static String getPathAsString() {
        return pathAsString;
    }
}
