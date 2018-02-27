package com.springsun.mdtclient.controller;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InetCheck {
    private static Logger log = Logger.getLogger(InetCheck.class.getName());
    private static String host = "google.com";
    private static int port = 80;
    private static int timeout = 2000;

    public static boolean internetConnectionIsAvailable(){
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e){
            log.log(Level.WARNING, "Can't get to google.com. Internet is not available");
            return false;
        }
    }

}
