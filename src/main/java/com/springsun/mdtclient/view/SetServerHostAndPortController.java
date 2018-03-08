package com.springsun.mdtclient.view;

import com.springsun.mdtclient.controller.CreateHostAndPortFile;
import com.springsun.mdtclient.model.DispetchingData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetServerHostAndPortController {
    private static Logger log = Logger.getLogger(SetServerHostAndPortController.class.getName());
    private String hostAsString = DispetchingData.getServerHost();
    private int portAsInt = DispetchingData.getServerPort();
    private List<String> lines;
    private String pathDirectory = DispetchingData.getPathDirectoryToFileServerHostAndPort();
    private String fileName = DispetchingData.getFileNameServerHostAndPort();

    @FXML
    private Label setHostPortLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private Label hostLabel;
    @FXML
    private Label portLabel;
    @FXML
    private Label warningLabel;
    @FXML
    private TextField tfHost;
    @FXML
    private TextField tfPort;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize(){
        infoLabel.setText("This settings will be done only once. " +
                "\nIf later you'll want to change the server, just delete file 'ServerHostAndPort.txt'" +
                "\nat " + pathDirectory +
                "\nIf you'll left textfields blank or press 'cancel' button, default values will be set.");
        tfHost.setPromptText(hostAsString);
        tfPort.setPromptText("" + portAsInt);
        warningLabel.textProperty().set("");
    }

    @FXML
    private void okHandler(ActionEvent actionEvent){
        if (!checkHost() || !checkPort()) return;
        lines = Arrays.asList(hostAsString, "" + portAsInt);
        CreateHostAndPortFile.createFile(pathDirectory, fileName, lines);
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelHandler(ActionEvent actionEvent){
        lines = Arrays.asList(hostAsString, "" + portAsInt);
        CreateHostAndPortFile.createFile(pathDirectory, fileName, lines);
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    private boolean checkHost() {
        if (tfHost.getText().equals("")){
            log.log(Level.INFO, "In GUI text field Host equals empty string. " +
                    "\nDefault host '" + hostAsString + "' will be used.");
            return true;
        }
        hostAsString = tfHost.getText();
        if (checkSymbolsInHost(hostAsString)) return true;
        warningLabel.setText("Wrong host format or wrong symbol(s) in host. \nIt must be [a-zA-Z0-9_.-]");
        return false;
    }

    private boolean checkPort(){
        if (tfPort.getText().equals("")){
            log.log(Level.INFO, "In GUI text field Port equals empty string. " +
                    "\nDefault port '" + portAsInt + "' will be used.");
            return true;
        }
        try {
            portAsInt = (int)Integer.parseInt(tfPort.getText());
        } catch (NumberFormatException e){
            warningLabel.setText("Can't parse port. It must be integer.");
            log.log(Level.WARNING, "Can't parse port from GUI: ", e);
            return false;
        }
        if (portAsInt < 1024 || portAsInt > 49151){
            warningLabel.setText("Port must be in range from 1024 to 49151");
            return false;
        }
        return true;
    }

    private boolean checkSymbolsInHost(String str){
        Pattern p = Pattern.compile("\\w+([\\.-]?\\w+)*");
        Matcher m = p.matcher(str);
        if (m.matches()) return true;
        return false;
    }
}
