package com.springsun.mdtclient.controller.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AppThreadForTests extends Application {
    private static Stage primaryStage;
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Tests");
        AnchorPane root = new AnchorPane();
        this.primaryStage.setScene(new Scene(root, 300, 200));
        this.primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
