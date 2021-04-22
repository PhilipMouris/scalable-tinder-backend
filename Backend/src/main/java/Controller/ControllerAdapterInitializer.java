package Controller;

import Interface.ServiceControl;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class ControllerAdapterInitializer extends ChannelInitializer<SocketChannel> {

    private final ServiceControl service;

    public ControllerAdapterInitializer(ServiceControl service) {
        this.service = service;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("encode", new ObjectEncoder());
        pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers
                .cacheDisabled(getClass().getClassLoader())));
        pipeline.addLast("handler", new ControllerAdapterHandler(service));
    }

}