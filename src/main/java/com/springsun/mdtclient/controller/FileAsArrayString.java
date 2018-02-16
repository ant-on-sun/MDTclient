package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.IUser;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class FileAsArrayString {
    private static Logger log = Logger.getLogger(FileAsArrayString.class.getName());

    public static List<String> getContent(IUser user){
        List<String> stringList = new ArrayList<>();
        String pathAsString = null;
        Path path = null;
        try {
            if (CreateDataFile.createFile(user)){
                pathAsString = CreateDataFile.getPathAsString();
            } else throw new Exception("File is not exist and couldn't be made for some reason");
            path = Paths.get(pathAsString);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception caught in FileAsArrayString getContent()" +
                    " while trying to get path from url: ", e);
        }

        /*Building ArrayList<String> from file*/
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringList.add(s));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception caught in FileAsArrayString getContent() " +
                    "at try-with-resources (building the ArrayList<String>): ", e);
        }
        return stringList;
    }
}
