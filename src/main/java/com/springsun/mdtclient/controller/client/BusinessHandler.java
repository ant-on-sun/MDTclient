package com.springsun.mdtclient.controller.client;

import com.springsun.mdtclient.model.DispetchingData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BusinessHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = Logger.getLogger(BusinessHandler.class.getName());
    private ChannelHandlerContext context;
    private final String appPassword = "password";
    private final String approved = "approved";
    private Boolean appPasswordChecked;
    private boolean hashChecked;
    private String in;
    private Integer key;
    private String firstValue;
    private String secondValue;
    private String inWithHash;
    private int hash;
    private Object monitor = WaitForServerReply.getMonitor();
    private DispetchingData dispetchingData;
    private String message;
    private final int maxValueOfKeyOnServer = 8;
    private int errorCounter = 0;
    private final int maxQuantityOfErrors = 100;

    public BusinessHandler(DispetchingData dispetchingData) {
        this.dispetchingData = dispetchingData;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message to server to verify oneself.
        message = "1:" + appPassword;
        try {
            DispetchingData.getClient().writeToChannel(message);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "InterruptedException in BusinessHandler " +
                    "while trying to send first outgoing message (1:appPassword): ", e);
            //e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        context = ctx;
        inWithHash = (String) msg;
        hash = GetHashOfMessage.parseHash(inWithHash);
        in = GetMessageWithoutHash.getIncomingMessage(inWithHash);
        hashChecked = CheckHash.checkHash(in, hash);

        if (!hashChecked) {
            errorCounter++;
            Platform.runLater(() -> {
                dispetchingData.messageModelProperty().set("Data were changed while transmitting from server");
            });
            if (errorCounter > maxQuantityOfErrors) {
                log.log(Level.WARNING, "Too many errors in session. Channel will be closed.");
                closingChannelBecauseOfErrors();
            }
            log.log(Level.WARNING, "Hash sum of incoming message is not valid. Client will do nothing. " +
                    "Ask server to repeat transmitting");
            String message = "8:Data were changed while transmitting from server. Wrong hashCode.";
            try {
                DispetchingData.getClient().writeToChannel(message);
            } catch (InterruptedException e) {
                log.log(Level.WARNING, "InterruptedException in BusinessHandler " +
                        "while asking server to repeat transmitting: ", e);
                //e.printStackTrace();
            }
            return;
        }

        key = GetKeyFromMessage.parseKey(in);
        firstValue = GetFirstValue.parseFirstValue(in);
        secondValue = GetSecondValue.parseSecondValue(in);
        Platform.runLater(() -> {
            dispetchingData.messageModelProperty().set(firstValue);
        });
        switch (key){
            case 1: //Check if the server is a valid server of our application (expected reply: approved)
                if (approved.equals(firstValue)){
                    appPasswordChecked = true;
                    Platform.runLater(() -> {
                        dispetchingData.messageModelProperty().set("");
                    });
                    log.log(Level.FINE, "Application password approved.");
                } else {
                    log.log(Level.WARNING, "Application password is not correct. Channel will be closed.");
                    ctx.close();
                    Platform.runLater(() -> {
                        dispetchingData.connectedProperty().set(false);
                    });
                }
                break;
            case 2: //Incorrect password
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.checkedProperty().set(false);
                        });
                        monitor.notify();
                        log.log(Level.INFO, "Provided user password is not correct.");
                    }
                }
                break;
            case 3: //Login and password are correct
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.checkedProperty().set(true);
                        });
                        monitor.notify();
                        log.log(Level.FINE, "Provided user login and password are correct.");
                    }
                }
                break;
            case 4: //distance (result)
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.resultProperty().set(firstValue);
                        });
                        monitor.notify();
                        log.log(Level.FINE, "Receiving calculated result.");
                    }
                }
                break;
            case 5: //Login exist
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.checkedProperty().set(true);
                            dispetchingData.loginAlredyExistProperty().set(true);
                        });
                        monitor.notify();
                        log.log(Level.FINE, "Provided user login exist.");
                    }
                }
                break;
            case 6: //No such login
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.checkedProperty().set(false);
                            dispetchingData.loginAlredyExistProperty().set(false);
                        });
                        monitor.notify();
                        log.log(Level.FINE, "Provided user login doesn't exist.");
                    }
                }
                break;
            case 7: //New user successfully created
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.userCreatedProperty().set(true);
                        });
                        monitor.notify();
                        log.log(Level.FINE, "New user created.");
                    }
                }
                break;
            case 8: //Couldn't create new user
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.userCreatedProperty().set(false);
                        });
                        monitor.notify();
                        log.log(Level.WARNING, "New user was not created.");
                    }
                }
                break;
            case 9: //Couldn't reset result to zero
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.resultProperty().set(firstValue);
                        });
                        monitor.notify();
                        log.log(Level.WARNING, "Couldn't reset result to zero.");
                    }
                }
                break;
            case 10:
                //Data were changed while transmitting to server. Server will do nothing. Try to send data later.
                //Or: NumberFormatException. Couldn't parse number(s). Ask client to re-send data.
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.messageModelProperty().set(firstValue);
                        });
                        monitor.notify();
                        if (errorCounter > maxQuantityOfErrors) {
                            log.log(Level.WARNING, "Too many errors in session. Channel will be closed.");
                            closingChannelBecauseOfErrors();
                            break;
                        }
                        log.log(Level.INFO, in + "\nClient will try to re-send data.");
                    }
                    try {
                        DispetchingData.getClient().writeToChannel();
                    } catch (InterruptedException e) {
                        log.log(Level.WARNING, "InterruptedException in BusinessHandler " +
                                "while trying to re-send outgoing message (key 10): ", e);
                        //e.printStackTrace();
                    }
                    errorCounter++;
                }
                break;
            case 11: //Invalid key protocol was received on server
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.messageModelProperty().set(firstValue);
                        });
                        monitor.notify();
                        log.log(Level.WARNING, "Server has received invalid key protocol from client.");
                    }
                    String outgoingMessage = DispetchingData.getClient().getMessage();
                    key = GetKeyFromMessage.parseKey(outgoingMessage);
                    if (key < 1 || key > maxValueOfKeyOnServer) {
                        log.log(Level.WARNING, "Invalid key = " + key + " was created on client. " +
                                "\nOutgoingMessage = " + outgoingMessage + " \nClosing connection.");
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Client");
                            alert.setHeaderText("Error key in outgoing message");
                            alert.setContentText("Invalid key = "  + key + " was created on client. " +
                                    "\nOutgoingMessage = " + outgoingMessage +
                                    " \nTry to restart application.");
                            alert.showAndWait();
                        });
                        ctx.close();
                    } else {
                        log.log(Level.WARNING, "The key on client is fine, trying to re-send data.");
                        try {
                            DispetchingData.getClient().writeToChannel();
                        } catch (InterruptedException e) {
                            log.log(Level.WARNING, "InterruptedException in BusinessHandler " +
                                    "while trying to re-send outgoing message (key 11): ", e);
                            //e.printStackTrace();
                        }
                    }
                }
                break;
            default: //Invalid key protocol was received on client
                if (appPasswordChecked){
                    log.log(Level.WARNING, "Invalid key protocol was received from server on client.");
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.messageModelProperty().set(firstValue);
                        });
                        monitor.notify();
                    }
                    if (errorCounter > maxQuantityOfErrors) {
                        log.log(Level.WARNING, "Too many errors in session. Channel will be closed.");
                        closingChannelBecauseOfErrors();
                        break;
                    }
                    message = "7:Invalid key protocol was received from server on client";
                    try {
                        DispetchingData.getClient().writeToChannel(message);
                    } catch (InterruptedException e) {
                        log.log(Level.WARNING, "InterruptedException in BusinessHandler " +
                                "while trying to send outgoing message (key default): ", e);
                        //e.printStackTrace();
                    }
                    errorCounter++;
                }
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.log(Level.SEVERE, "Exception caught in method exceptionCaught() in Client's BuisinessHandler: ",
                cause.getMessage());
        if (errorCounter > maxQuantityOfErrors){
            //Close the connection when an exception is raised
            //cause.printStackTrace();
            log.log(Level.INFO, "Too many errors in session. Channel will be closed.");
            ctx.close();
            return;
        }
        message = "8:Exception caught on client. Ask server to resend data.";
        try {
            DispetchingData.getClient().writeToChannel(message);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "InterruptedException in BusinessHandler " +
                    "while asking server to repeat transmitting: ", e);
            //e.printStackTrace();
        }
        errorCounter++;
    }

    private void closingChannelBecauseOfErrors(){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Client");
            alert.setHeaderText("Errors in session");
            alert.setContentText("Too many errors in session. \nTry to restart application.");
            alert.showAndWait();
        });
        context.close();
    }

    //For tests
    public Boolean getAppPasswordChecked() {
        return appPasswordChecked;
    }

    //For tests
    public void setErrorCounter(int errorCounter) {
        this.errorCounter = errorCounter;
    }

    //For tests
    public int getErrorCounter() {
        return errorCounter;
    }

}
