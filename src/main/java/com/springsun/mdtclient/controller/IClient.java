package com.springsun.mdtclient.controller;

import io.netty.channel.ChannelFuture;

import javax.net.ssl.SSLException;

public interface IClient {

    void connectToServer() throws SSLException, InterruptedException;
    void writeToChannel(String message) throws InterruptedException;
    void writeToChannel() throws InterruptedException;
    void disconnectFromServer();
    ChannelFuture getChannelFuture();
    String getMessage();
    void setMessage(String message);
}
