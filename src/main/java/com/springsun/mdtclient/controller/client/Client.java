package com.springsun.mdtclient.controller.client;

import com.springsun.mdtclient.controller.IClient;
import com.springsun.mdtclient.model.DispetchingData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javafx.application.Platform;

import javax.net.ssl.SSLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements IClient {
    private static Logger log = Logger.getLogger(Client.class.getName());
    static final boolean SSL = System.getProperty("ssl") != null;
    static String host = DispetchingData.getServerHost();//System.getProperty("host", "127.0.0.1");
    static int port = DispetchingData.getServerPort();//Integer.parseInt(System.getProperty("port", "8007"));
    private EventLoopGroup group;
    private ChannelFuture channelFuture;
    private DispetchingData dispetchingData;
    private String message;

    public Client() {
    }

    public Client(DispetchingData dispetchingData) {
        this.dispetchingData = dispetchingData;
    }

    public void connectToServer() throws SSLException, InterruptedException {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
                sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                sslCtx = null;
            }
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ClientSocketInitializer(sslCtx, dispetchingData));

            // Start the connection attempt.
            channelFuture = b.connect(host, port).sync();
            Platform.runLater(() -> {
                dispetchingData.connectedProperty().set(true);
                dispetchingData.statusMessageModelProperty().set("Connected to server " + host + ":" + port);
            });

            // Wait until the client socket is closed
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void disconnectFromServer(){
        try {
            //Shutdown EventLoopGroup
            group.shutdownGracefully().sync();
            channelFuture.channel().closeFuture().sync(); //close port
        } catch (InterruptedException e){
            log.log(Level.WARNING, "Exception in disconnectFromServer(): ", e);
            //e.printStackTrace();
        }
    }

    //Add hash to message, write to channel and flush
    public void writeToChannel(String msg) throws InterruptedException {
        message = msg;
        int h = message.hashCode();
        message = message + ":" + h;
        channelFuture.channel().writeAndFlush(message).sync();
    }

    //Write to channel one more time last outgoing message
    public void writeToChannel() throws InterruptedException {
        if (message != null) channelFuture.channel().writeAndFlush(message).sync();
    }

    public ChannelFuture getChannelFuture(){
        return channelFuture;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static void setHost(String host) {
        Client.host = host;
    }

    public static void setPort(int port) {
        Client.port = port;
    }

}
