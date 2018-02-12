package com.springsun.mdtclient.model;

import com.springsun.mdtclient.controller.IClient;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.ExecutorService;

public class DispetchingData {
    public DispetchingData() {
    }

    public boolean isConnected() {
        return connected.get();
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public boolean isUserCreated() {
        return userCreated.get();
    }

    public BooleanProperty userCreatedProperty() {
        return userCreated;
    }

    public String getStatusMessageModel() {
        return statusMessageModel.get();
    }

    public StringProperty statusMessageModelProperty() {
        return statusMessageModel;
    }

    public String getMessageModel() {
        return messageModel.get();
    }

    public StringProperty messageModelProperty() {
        return messageModel;
    }

    public void setConnected(BooleanProperty connected) {
        this.connected = connected;
    }

    public void setChecked(BooleanProperty checked) {
        this.checked = checked;
    }

    public void setUserCreated(BooleanProperty userCreated) {
        this.userCreated = userCreated;
    }

    public void setStatusMessageModel(StringProperty statusMessageModel) {
        this.statusMessageModel = statusMessageModel;
    }

    public void setMessageModel(StringProperty messageModel) {
        this.messageModel = messageModel;
    }

    public boolean isLoginAlredyExist() {
        return loginAlredyExist.get();
    }

    public BooleanProperty loginAlredyExistProperty() {
        return loginAlredyExist;
    }

    public void setLoginAlredyExist(boolean loginAlredyExist) {
        this.loginAlredyExist.set(loginAlredyExist);
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static IClient getClient() {
        return client;
    }

    public static IUser getUser() {
        return user;
    }

    public static void setExecutorService(ExecutorService executorService) {
        DispetchingData.executorService = executorService;
    }

    public static void setClient(IClient client) {
        DispetchingData.client = client;
    }

    public static void setUser(IUser user) {
        DispetchingData.user = user;
    }

    public String getResult() {
        return result.get();
    }

    public StringProperty resultProperty() {
        return result;
    }

    public void setResult(StringProperty result) {
        this.result = result;
    }

    static private ExecutorService executorService;
    static private IClient client;
    static private IUser user;

    private BooleanProperty connected = new SimpleBooleanProperty(false);
    private BooleanProperty checked = new SimpleBooleanProperty(false);
    private BooleanProperty userCreated = new SimpleBooleanProperty(false);
    private BooleanProperty loginAlredyExist = new SimpleBooleanProperty(true);
    private StringProperty statusMessageModel = new SimpleStringProperty("Trying to connect...");
    private StringProperty messageModel = new SimpleStringProperty("");
    private StringProperty result = new SimpleStringProperty("");



    public DispetchingData(BooleanProperty connected,
                           BooleanProperty checked,
                           BooleanProperty userCreated,
                           BooleanProperty loginAlredyExist,
                           StringProperty statusMessageModel,
                           StringProperty messageModel,
                           ExecutorService executorService,
                           IClient client,
                           IUser user) {
        this.connected = connected;
        this.checked = checked;
        this.userCreated = userCreated;
        this.loginAlredyExist = loginAlredyExist;
        this.statusMessageModel = statusMessageModel;
        this.messageModel = messageModel;
        this.executorService = executorService;
        this.client = client;
        this.user = user;
    }
}
