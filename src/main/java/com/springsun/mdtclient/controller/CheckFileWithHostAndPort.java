package com.springsun.mdtclient.controller;

import java.io.File;

public class CheckFileWithHostAndPort {

    public static boolean fileExist(String pathAsString){
        if (new File(pathAsString).exists()) return true;
        return false;
    }
}
