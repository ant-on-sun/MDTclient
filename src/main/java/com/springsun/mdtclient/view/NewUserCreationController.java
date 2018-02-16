package com.springsun.mdtclient.view;

import com.springsun.mdtclient.controller.IClient;
import com.springsun.mdtclient.controller.client.WaitForServerReply;
import com.springsun.mdtclient.model.DispetchingData;
import com.springsun.mdtclient.model.IUser;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserCreationController {
    private static Logger log = Logger.getLogger(NewUserCreationController.class.getName());
    private DispetchingData dispetchingData;
    private IClient client;
    private IUser user;

    @FXML
    private AnchorPane pane;
    @FXML
    private VBox vBox;
    @FXML
    private Label enterLogin;
    @FXML
    private Label login;
    @FXML
    private Label isLoginExist;
    @FXML
    private Label enterPassword;
    @FXML
    private Label password;
    @FXML
    private Label wrongLettersInPassword;
    @FXML
    private Label confirmPassword;
    @FXML
    private Label userCr;
    @FXML
    private TextField tfLogin;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfConfirmPassword;
    @FXML
    private Button checkLogin;
    @FXML
    private Button createUser;

    @FXML
    public void initialize(){
        enterLogin.setText("Enter your login and press 'Check Login'");
        isLoginExist.setText("");
        userCr.setText("");
        checkLogin.disableProperty().bind(Bindings.isEmpty(tfLogin.textProperty()));
        wrongLettersInPassword.setText("");
        log.log(Level.FINE, "NewUserCreationController has been initialized.");
    }

    @FXML
    private void checkLoginHandler(ActionEvent actionEvent){
        user.setLogin(tfLogin.getText());
        checkLogin();
        WaitForServerReply.waitForReply();
        if (dispetchingData.loginAlredyExistProperty().get()){
            isLoginExist.setText("Such login already exist.\nPlease try to enter another one.");
        } else {
            createUser.disableProperty().set(false);
            isLoginExist.setText("");
        }
    }

    @FXML
    private void createUserHandler(ActionEvent actionEvent){
        String str = pfPassword.getText();
        boolean lettersOk = checkLettersInPassword(str);
        if (lettersOk) {
            boolean passwordConfirmed = checkEqualityInPasswordFields(pfPassword.getText(), pfConfirmPassword.getText());
            if (passwordConfirmed){
                user.setLogin(tfLogin.getText());
                user.setPassword(pfPassword.getText());
                tellServerToCreateAccount();
                WaitForServerReply.waitForReply();
                if (dispetchingData.userCreatedProperty().get()){
                    createUser.disableProperty().set(true);
                    userCr.setText("New user successfully created.\nClose this window and enter your login / password");
                } else {
                    log.log(Level.INFO, "Couldn't create new user for some reasons.");
                    userCr.setText("Couldn't create new user.\nSorry, try again later");
                }
            } else {
                wrongLettersInPassword.setText("Passwords in fields are not equals");
            }
        } else {
            wrongLettersInPassword.setText("Password must contain only alphabet english letters, numbers and _" +
                    "\n Please enter another password");
        }
    }

    private void checkLogin(){
        String message = "4:" + user.getLogin();
        try {
            client.writeToChannel(message);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "InterruptedException in NewUserCreationController in checkLogin() : ", e);
            //e.printStackTrace();
        }
    }

    private boolean checkLettersInPassword(String str){
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(str);
        if (m.matches()) return true;
        return false;
    }

    private boolean checkEqualityInPasswordFields(String pf1, String pf2){
        if (pf1.equals(pf2)) return true;
        return false;
    }

    private void tellServerToCreateAccount(){
        String message = "5:" + user.getLogin() + ":" + user.getPassword();
        try {
            client.writeToChannel(message);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "InterruptedException in NewUserCreationController " +
                    "in tellServerToCreateAccount() : ", e);
            //e.printStackTrace();
        }
    }

    public void setClient(IClient client) {
        this.client = client;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    public void setDispetchingData(DispetchingData dispetchingData) {
        this.dispetchingData = dispetchingData;
        vBox.visibleProperty().bind(dispetchingData.loginAlredyExistProperty().not());
    }

}
