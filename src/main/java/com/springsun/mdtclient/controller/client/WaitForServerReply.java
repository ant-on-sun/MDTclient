package com.springsun.mdtclient.controller.client;

public class WaitForServerReply {
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
                    e.printStackTrace();
                }
            }
        }
        serverHasReplied = false;
    }
}
