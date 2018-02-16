package com.springsun.mdtclient.controller.client;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WaitForServerReply {
    private static Logger log = Logger.getLogger(WaitForServerReply.class.getName());
    private static final Object monitor = new Object();
    private static boolean serverHasReplied = false;

    public static void setServerHasReplied(boolean serverHasReplied) {
        WaitForServerReply.serverHasReplied = serverHasReplied;
    }

    public static Object getMonitor() {
        return monitor;
    }

    public static void waitForReply() {
        synchronized (monitor) {
            while (!serverHasReplied) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    log.log(Level.INFO, "InterruptedException in waitForReply(): ", e);
                    //e.printStackTrace();
                }
            }
        }
        serverHasReplied = false;
    }
}
