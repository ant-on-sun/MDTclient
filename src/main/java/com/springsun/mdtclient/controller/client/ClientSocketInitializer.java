package com.springsun.mdtclient.controller.client;

import com.springsun.mdtclient.model.DispetchingData;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;

import static com.springsun.mdtclient.controller.client.Client.host;
import static com.springsun.mdtclient.controller.client.Client.port;

public class ClientSocketInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslContext;
    DispetchingData dispetchingData;
    public ClientSocketInitializer(SslContext sslCtx, DispetchingData dispetchingData) {
        sslContext = sslCtx;
        this.dispetchingData = dispetchingData;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslContext != null){
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc(), host, port));
        }
        //pipeline.addLast(new LoggingHandler(LogLevel.WARN));
        pipeline.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new BusinessHandler(dispetchingData));
    }
}
