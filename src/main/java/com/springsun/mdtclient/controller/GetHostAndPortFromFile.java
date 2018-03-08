package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.DispetchingData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GetHostAndPortFromFile {
    private static Logger log = Logger.getLogger(GetHostAndPortFromFile.class.getName());

    public static List<String> getAsArrayList(){
        List<String> stringList = new ArrayList<>();
        String pathAsString = DispetchingData.getPathAsStringToFileServerHostAndPort();
        Path path = Paths.get(pathAsString);

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
