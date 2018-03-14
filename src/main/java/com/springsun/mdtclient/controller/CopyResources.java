package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.DispetchingData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CopyResources {
    private static Logger log = Logger.getLogger(CopyResources.class.getName());

    public static void copyFile(String sourceAsString, String destDirectory) throws Exception {
        File file = new File(sourceAsString);
        String fileName = file.getName();
        String destAsString = destDirectory + fileName;
        sourceAsString = GetOsIndependentPathToFile.getPath(sourceAsString);
        destAsString = GetOsIndependentPathToFile.getPath(destAsString);
        try {
            Files.createDirectories(Paths.get(destDirectory));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception while creating directories in path: " + destDirectory, e);
        }
        if (new File(destAsString).exists()) return;
        InputStream sourceAsStream = CopyResources.class.getResourceAsStream(DispetchingData.getAbsolutePathToFileInJar());
        if(sourceAsStream == null) {
            throw new Exception("Cannot get resource " + DispetchingData.getAbsolutePathToFileInJar() + " from .jar file");
        }
        try {
            Files.copy(sourceAsStream, Paths.get(destAsString));
        } catch (FileAlreadyExistsException e) {
            log.log(Level.INFO, "FileAlreadyExistsException in copyFile(): ", e);
            //e.printStackTrace();
        } catch (IOException e){
            log.log(Level.WARNING, "IOException in copyFile(): ", e);
            //e.printStackTrace();
        }
        log.log(Level.FINE, "File has been copied from " + sourceAsString + "\n to " + destAsString);
    }
}
