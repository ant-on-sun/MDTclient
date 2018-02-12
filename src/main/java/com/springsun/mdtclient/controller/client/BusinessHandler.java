package com.springsun.mdtclient.controller.client;

import com.springsun.mdtclient.model.DispetchingData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;

public class BusinessHandler extends ChannelInboundHandlerAdapter {
    private final String appPassword = "password";
    private final String approved = "approved";
    private Boolean appPasswordChecked;
    private String in;
    private Integer key;
    private String firstValue;
    private String secondValue;
    private Object monitor = WaitForServerReply.getMonitor();
    private DispetchingData dispetchingData;

    public BusinessHandler(DispetchingData dispetchingData) {
        this.dispetchingData = dispetchingData;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message to server to verify oneself.
        ctx.writeAndFlush("1:" + appPassword);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        in = (String)msg;
        key = GetKeyFromMessage.parseKey(in);
        firstValue = GetFirstValue.parseFirstValue(in);
        secondValue = GetSecondValue.parseSecondValue(in);
        Platform.runLater(() -> {
            dispetchingData.messageModelProperty().set(firstValue);
        });
        switch (key){
            case 1: //Check if the server is a valid server of our application (expected reply: approved)
                if (approved.equalsIgnoreCase(firstValue)){
                    appPasswordChecked = true;
                    Platform.runLater(() -> {
                        dispetchingData.messageModelProperty().set("");
                    });
                } else {
                    ctx.close();
                }
                break;
            case 2: //Incorrect password
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.checkedProperty().set(false);
                        monitor.notify();
                    }
                }
                break;
            case 3: //Login and password are correct
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.checkedProperty().set(true);
                        monitor.notify();
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
                    }
                }
                break;
            case 7: //New user successfully created
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.userCreatedProperty().set(true);
                        monitor.notify();
                    }
                }
                break;
            case 8: //Couldn't create new user
                if (appPasswordChecked){
                    synchronized (monitor){
                        WaitForServerReply.setServerHasReplied(true);
                        dispetchingData.userCreatedProperty().set(false);
                        monitor.notify();
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
