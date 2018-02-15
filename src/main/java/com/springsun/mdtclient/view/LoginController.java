package com.springsun.mdtclient.view;

import com.springsun.mdtclient.controller.IClient;
import com.springsun.mdtclient.controller.client.Client;
import com.springsun.mdtclient.controller.client.WaitForServerReply;
import com.springsun.mdtclient.model.DispetchingData;
import com.springsun.mdtclient.model.IUser;
import com.springsun.mdtclient.model.user.User;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController {

    DispetchingData dispetchingData = new DispetchingData();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    IUser user = new User();
    IClient client = new Client(dispetchingData);

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private Label text;
    @FXML
    private Label login;
    @FXML
    private Label pass;
    @FXML
    private Label wrong = new Label();
    @FXML
    private Label offlineLabel;
    @FXML
    private Label status = new Label();
    @FXML
    private TextField tfLogin = new TextField();
    @FXML
    private PasswordField pfPassword = new PasswordField();
    @FXML
    private Button submit;
    @FXML
    private Button createNewUser = new Button();

    @FXML
    public void initialize(){
        text.setText("Please enter your Login and Password, than press 'Submit'. \nOr press 'Create new user' to " +
                "create new account");
        login.setText("Login");
        pass.setText("Password");
        offlineLabel.setText("The server is not available at the moment." +
                "\n But you still can make checkpoints in stand-alone mode. \n They will be sent to server next time " +
                "\n and will be taken into account while counting the distance traveled.");
        wrong.textProperty().bind(dispetchingData.messageModelProperty());
        status.textProperty().bind(dispetchingData.statusMessageModelProperty());
        //tfLogin.disableProperty().bind(dispetchingData.connectedProperty().not());
        //pfPassword.disableProperty().bind(dispetchingData.connectedProperty().not());
        offlineLabel.visibleProperty().bind(dispetchingData.connectedProperty().not());
        createNewUser.disableProperty().bind(dispetchingData.connectedProperty().not());

        submit.disableProperty().bind(
                Bindings.isEmpty(tfLogin.textProperty())
                .or(Bindings.isEmpty(pfPassword.textProperty()))
        );
        connect();
        DispetchingData.setExecutorService(executorService);
        DispetchingData.setUser(user);
        DispetchingData.setClient(client);
    }

    public void connect(){
        if (dispetchingData.connectedProperty().get()) return; //connection with server already established
        Task<Void> task = new Task<Void>(){

            @Override
            protected Void call() throws Exception {
                client.connectToServer();
                return null;
            }

            @Override
            protected void failed() {
                dispetchingData.connectedProperty().set(false);
                dispetchingData.messageModelProperty().set("The server is not responding. " +
                        "Application will run in stand-alone mode.");
                Throwable exc = getException();
                //logger.error( "client connect error", exc );
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Client");
                alert.setHeaderText( exc.getClass().getName() );
                alert.setContentText( exc.getMessage() );
                alert.showAndWait();

            }
        };
        executorService.submit(task);
        executorService.execute(task);
    }

    @FXML
    private void submitHandler(ActionEvent actionEvent){
        user.setLogin(tfLogin.getText());
        user.setPassword(pfPassword.getText());
        if (dispetchingData.connectedProperty().get()){
            checkLoginAndPassword();
            WaitForServerReply.waitForReply();
        }

        if (dispetchingData.checkedProperty().get() || !dispetchingData.connectedProperty().get()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainScene.fxml"));
                AnchorPane pane = fxmlLoader.load();
                MainSceneController mainSceneController = fxmlLoader.getController();
                mainSceneController.setDispetchingData(dispetchingData);
                rootAnchorPane.getChildren().setAll(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void createUserHandler(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NewUserCreation.fxml"));
            Parent root = fxmlLoader.load();

            //Passing parameters to another controller
            NewUserCreationController newUserCreationController = fxmlLoader.getController();
            dispetchingData.loginAlredyExistProperty().set(true);
            newUserCreationController.setClient(client);
            newUserCreationController.setUser(user);
            newUserCreationController.setDispetchingData(dispetchingData);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootAnchorPane.getScene().getWindow());
            stage.setTitle("Create new user");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkLoginAndPassword(){
        String message = "2:" + user.getLogin() + ":" + user.getPassword();
        try {
            client.writeToChannel(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
