package com.springsun.mdtclient.controller.client;

import com.springsun.mdtclient.model.DispetchingData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BusinessHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = Logger.getLogger(BusinessHandler.class.getName());
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

    public BusinessHandler(DispetchingData dispetchingData) {
        this.dispetchingData = dispetchingData;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message to server to verify oneself.
        message = "1:" + appPassword;
        int h = message.hashCode();
        message = message + ":" + h;
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        inWithHash = (String) msg;
        hash = GetHashOfMessage.parseHash(inWithHash);
        in = GetMessageWithoutHash.getIncomingMessage(inWithHash);
        hashChecked = CheckHash.checkHash(in, hash);
        if (!hashChecked) {
            Platform.runLater(() -> {
                dispetchingData.messageModelProperty().set("Data were changed while transmitting from server");
            });
            log.log(Level.WARNING, "Hash sum of incoming message is not valid. Client will do nothing.");
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
                        dispetchingData.checkedProperty().set(false);
                        monitor.notify();
                        log.log(Level.INFO, "Provided user password is not correct.");
                    }
                }
                break;
            case 3: //Login and password are correct
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.checkedProperty().set(true);
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
                        dispetchingData.checkedProperty().set(true);
                        dispetchingData.loginAlredyExistProperty().set(true);
                        monitor.notify();
                        log.log(Level.FINE, "Provided user login exist.");
                    }
                }
                break;
            case 6: //No such login
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.checkedProperty().set(false);
                        dispetchingData.loginAlredyExistProperty().set(false);
                        monitor.notify();
                        log.log(Level.FINE, "Provided user login doesn't exist.");
                    }
                }
                break;
            case 7: //New user successfully created
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.userCreatedProperty().set(true);
                        monitor.notify();
                        log.log(Level.FINE, "New user created.");
                    }
                }
                break;
            case 8: //Couldn't create new user
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.userCreatedProperty().set(false);
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
            case 10: //Data were changed while transmitting to server. Server will do nothing. Try to send data later.
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.messageModelProperty().set(firstValue);
                        });
                        monitor.notify();
                        log.log(Level.INFO, "Data were changed while transmitting to server. " +
                                "Server will do nothing.");
                    }
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
                }
                break;
            default: //Invalid key protocol was received on client
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        Platform.runLater(() -> {
                            dispetchingData.messageModelProperty().set(firstValue);
                        });
                        monitor.notify();
                        message = "7:Invalid key protocol was received from server on client";
                        int h = message.hashCode();
                        message = message + ":" + h;
                        ctx.writeAndFlush(message);
                        log.log(Level.WARNING, "Invalid key protocol was received from server on client.");
                    }
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
        //Close the connection when an exception is raised
        cause.printStackTrace();
        ctx.close();
    }
}
