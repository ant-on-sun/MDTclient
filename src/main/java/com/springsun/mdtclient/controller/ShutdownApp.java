package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.DispetchingData;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShutdownApp {
    private static Logger log = Logger.getLogger(ShutdownApp.class.getName());

    public static void shutdown(){
        if (DispetchingData.getClient().getChannelFuture() != null) {
            DispetchingData.getClient().disconnectFromServer();
        }
        DispetchingData.getExecutorService().shutdown();
        try {
            DispetchingData.getExecutorService().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "InterruptedException in ShutdownApp : ", e);
            //e.printStackTrace();
        }
        DispetchingData.getExecutorService().shutdownNow();
        Platform.exit();
        System.exit(0);
    }
}
