package com.springsun.mdtclient.view;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.springsun.mdtclient.controller.IClient;
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
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MainSceneController implements Initializable, MapComponentInitializedListener {
    public void setDispetchingData(DispetchingData dispetchingData) {
        this.dispetchingData = dispetchingData;
        resultLabel.textProperty().bind(this.dispetchingData.resultProperty());
        loginLabel.setText(DispetchingData.getUser().getLogin());
    }

    DispetchingData dispetchingData;
    IUser user;
    IClient client;
    private GoogleMap map;
    private MarkerOptions markerOptions;// = new MarkerOptions();
    private Marker marker;// = new Marker(markerOptions);
    private DecimalFormat formatter = new DecimalFormat("###.00000");

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
        //googleMapView.addMapInializedListener(() -> configureMap());
        googleMapView.addMapInializedListener(this);
    }

    @Override
    public void mapInitialized() {
        configureMap();
    }

    protected void configureMap() {
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(48.62939, 44.40183))
                .mapType(MapTypeIdEnum.ROADMAP)
                .zoom(7);
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
                    .visible(true);
            marker = new Marker(markerOptions);
//            marker = new Marker(new MarkerOptions()
//                    .position(latLong)
//                    .title("Current marker")
//                    .icon("/media/img/MapMarker.png"));
            map.addMarker(marker);
        });

    }

    @FXML
    private void calculateHandler(){
        sendDataToServer(user.getCurrentLatitude(), user.getCurrentLongitude());
        WaitForServerReply.waitForReply();
    }

    @FXML
    private void resetHandler(){}

    @FXML
    private void exitHandler(){
        DispetchingData.getClient().disconnectFromServer();
        DispetchingData.getExecutorService().shutdown();
        try {
            DispetchingData.getExecutorService().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DispetchingData.getExecutorService().shutdownNow();
        Platform.exit();
        System.exit(0);
    }

    private void sendDataToServer(float latitude, float longitude){
        String message = "3:" + latitude + ":" + longitude;
        try {
            client.writeToChannel(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
