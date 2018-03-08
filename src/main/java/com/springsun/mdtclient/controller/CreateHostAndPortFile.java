package com.springsun.mdtclient.controller;

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

public class CreateHostAndPortFile {
    private static Logger log = Logger.getLogger(CreateHostAndPortFile.class.getName());
    private static String pathAsString;

    public static boolean createFile(String pathDirectory, String fileName, List<String> lines){
        pathAsString = pathDirectory + fileName;
        try {
            Files.createDirectories(Paths.get(pathDirectory));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception while creating directories in path: " + pathDirectory, e);
        }
        if (new File(pathAsString).exists()) return true;

        try {
            Files.write(Paths.get(pathAsString), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception while creating file in path: " + pathAsString, e);
        }
        if (new File(pathAsString).exists()) return true;
        return false;
    }
}
