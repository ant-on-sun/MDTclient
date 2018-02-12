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

public final class Client implements IClient {
    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    private EventLoopGroup group;
    private ChannelFuture channelFuture;
    DispetchingData dispetchingData;

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
            channelFuture = b.connect(HOST, PORT).sync();
            Platform.runLater(() -> {
                dispetchingData.connectedProperty().set(true);
                dispetchingData.statusMessageModelProperty().set("Connected to server " + HOST + ":" + PORT);
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
            e.printStackTrace();
        }
    }

    public void writeToChannel(String message) throws InterruptedException {
        channelFuture.channel().writeAndFlush(message).sync();
    }

}
