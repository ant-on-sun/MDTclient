package com.springsun.mdtclient.controller;

import com.springsun.mdtclient.model.DispetchingData;
import com.springsun.mdtclient.view.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main extends Application {

    static {
        InputStream stream = Main.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.err.println("Could not set up logger configuration: " + e.toString());
        }
    }

    private static Logger log = Logger.getLogger(Main.class.getName());

    @Override
    public void init(){
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("MDT client");
        primaryStage.setMinHeight(490);
        primaryStage.setMinWidth(700);
        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            if (DispetchingData.getClient().getChannelFuture() != null) {
                DispetchingData.getClient().disconnectFromServer();
            }
            DispetchingData.getExecutorService().shutdown();
            try {
                DispetchingData.getExecutorService().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, "Exception caught in Main while closing application: ", e);
                //e.printStackTrace();
            }
            DispetchingData.getExecutorService().shutdownNow();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception caught in Main: ", e);
            //e.printStackTrace();
        }
    }

}
