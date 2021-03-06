package com.springsun.mdtclient.view;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import com.springsun.mdtclient.controller.*;
import com.springsun.mdtclient.controller.client.WaitForServerReply;
import com.springsun.mdtclient.model.DispetchingData;
import com.springsun.mdtclient.model.IUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainSceneController implements Initializable, MapComponentInitializedListener {
    private static Logger log = Logger.getLogger(MainSceneController.class.getName());
    public void setDispetchingData(DispetchingData dispetchingData) {
        this.dispetchingData = dispetchingData;
        resultLabel.textProperty().bind(this.dispetchingData.resultProperty());
        loginLabel.setText(DispetchingData.getUser().getLogin());
        if (dispetchingData.connectedProperty().get()) {
            sendDataToServer(1000, 1000);
            sendDataFromFileToServer();
        }
    }

    DispetchingData dispetchingData;
    IUser user;
    IClient client;
    private GoogleMap map;
    private MarkerOptions markerOptions;
    private Marker marker;
    private DecimalFormat formatter = new DecimalFormat("###.0000000");
    private MapOptions mapOptions;
    //Method MarkerImageFactory.createMarkerImage() can work only with image file that is not in the .jar
    private String pathToMarkerImage = DispetchingData.getPathAsStringToDestinationDirectory()
            + DispetchingData.getMarkerImgFileName();

    @FXML
    private Label loginLabel;
    @FXML
    private Label distanceTraveledLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private Label instructionLabel;
    @FXML
    private Label currentLatLabel;
    @FXML
    private Label currentLongLabel;
    @FXML
    private Label latValueLabel;
    @FXML
    private Label longValueLabel;
    @FXML
    private Button calculateButton;
    @FXML
    private Button resetResultButton;
    @FXML
    private Button exitButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private GoogleMapView googleMapView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        user = DispetchingData.getUser();
        client = DispetchingData.getClient();
        instructionLabel.setText("Right-click on map to set a marker \n of new position" +
                "\n (current latitude and longitude)\n Than press 'Calculate' button \n to calculate " +
                "\n distance traveled");
        distanceTraveledLabel.setText("Distance traveled, meters :");
        latValueLabel.setText("");
        longValueLabel.setText("");
        googleMapView.addMapInializedListener(this);
        configureMarker();
        log.log(Level.FINE, "MainSceneController has been initialized.");
    }

    @Override
    public void mapInitialized() {
        configureMap();
        log.log(Level.FINE, "Google map has been initialized.");
    }

    protected void configureMap() {
        mapOptions = new MapOptions();

        mapOptions.center(new LatLong(48.62939, 44.40183))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(11);
        map = googleMapView.createMap(mapOptions, false);

        map.addMouseEventHandler(UIEventType.rightclick, (GMapMouseEvent event) -> {
            if (marker != null) map.removeMarker(marker);
            LatLong latLong = event.getLatLong();
            float latitude = (float) latLong.getLatitude();
            float longitude = (float) latLong.getLongitude();
            user.setCurrentLatitude(latitude);
            user.setCurrentLongitude(longitude);
            latValueLabel.setText(formatter.format(latLong.getLatitude()));
            longValueLabel.setText(formatter.format(latLong.getLongitude()));
            markerOptions = new MarkerOptions();
            markerOptions.position(latLong)
                    .animation(Animation.NULL)
                    .title("Current position")
                    .icon(pathToMarkerImage)
                    .visible(true);
            marker = new Marker(markerOptions);
            map.addMarker(marker);
        });

    }

    @FXML
    private void calculateHandler(){
        if (user.getCurrentLatitude() > 999 || user.getCurrentLongitude() > 999) return;
        if (dispetchingData.connectedProperty().get()){
            sendDataToServer(user.getCurrentLatitude(), user.getCurrentLongitude());
            WaitForServerReply.waitForReply();
            log.log(Level.FINE, "New result has been calculated.");
            return;
        }
        String data = "3:" + user.getCurrentLatitude() + ":" + user.getCurrentLongitude();
        AddDataToFile.addData(data, user);
        dispetchingData.resultProperty().set("New checkpoint remembered");
        log.log(Level.FINE, "New checkpoint remembered in stand-alone mode.");
    }

    @FXML
    private void resetHandler(){
        String message = "6:" + 0;
        try {
            client.writeToChannel(message);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "InterruptedException in MainSceneController in resetHandler() : ", e);
            //e.printStackTrace();
        }
    }

    @FXML
    private void exitHandler(){
        ShutdownApp.shutdown();
    }

    private void sendDataToServer(float latitude, float longitude){
        String message = "3:" + latitude + ":" + longitude;
        try {
            client.writeToChannel(message);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "InterruptedException in MainSceneController in sendDataToServer() : ", e);
            //e.printStackTrace();
        }
    }

    //Send data from the file to the server and clear all data in the file
    private void sendDataFromFileToServer(){
        List<String> list = FileAsArrayString.getContent(DispetchingData.getUser());
        list.forEach(s -> {
            try {
                DispetchingData.getClient().writeToChannel(s);
            } catch (InterruptedException e) {
                log.log(Level.WARNING, "InterruptedException in MainSceneController " +
                        "in sendDataFromFileToServer() : ", e);
                //e.printStackTrace();
            }
            WaitForServerReply.waitForReply();
        });
        ClearFileContent.clear(DispetchingData.getUser());
    }

    private void configureMarker(){
        pathToMarkerImage = "file:///" + GetOsIndependentPathToFile.getPath(pathToMarkerImage);
        pathToMarkerImage = pathToMarkerImage.replace(" ", "%20");
        pathToMarkerImage = pathToMarkerImage.replace("\\", "/");
        log.log(Level.FINE, pathToMarkerImage);
        pathToMarkerImage = MarkerImageFactory.createMarkerImage(pathToMarkerImage, "png");
        pathToMarkerImage = pathToMarkerImage.replace("(", "");
        pathToMarkerImage = pathToMarkerImage.replace(")", "");
        log.log(Level.FINE, pathToMarkerImage);
    }

}
