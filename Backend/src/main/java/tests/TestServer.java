package tests;


import Config.Config;
import NettyWebServer.HTTPHandler;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class TestServer {

    private EmbeddedChannel channel;

    public EmbeddedChannel getChannel(){
        return channel;
    }

    public TestServer() {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
                .allowedRequestHeaders("X-Requested-With", "Content-Type", "Content-Length")
                .allowedRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS,HttpMethod.HEAD)
                .build();
        Config config = Config.getInstance();
        String loadBalancerHost = config.getLoadBalancerQueueHost();
        int loadBalancerPort = config.getLoadBalancerQueuePort();
        String loadBalancerUser = config.getLoadBalancerQueueUserName();
        String loadBalancerPass = config.getLoadBalancerQueuePass();
        String RPC_QUEUE_SEND_TO = config.getLoadBalancerQueueName();
        ConnectionFactory loadBalancerFactory = new ConnectionFactory();
        loadBalancerFactory.setHost(loadBalancerHost);
        loadBalancerFactory.setPort(loadBalancerPort);
        loadBalancerFactory.setUsername(loadBalancerUser);
        loadBalancerFactory.setPassword(loadBalancerPass);
        String RPC_QUEUE_REPLY_TO = config.getServerQueueName();
        HashMap<String, ChannelHandlerContext> uuid = new HashMap<String, ChannelHandlerContext>();
        this.channel = new EmbeddedChannel();
        channel.pipeline().addLast(new HttpRequestDecoder()).
                addLast(new HttpResponseDecoder()).
                addLast(new CorsHandler(corsConfig)).
                addLast(new HttpObjectAggregator(10000000)).
                addLast(new HTTPHandler()).
                addLast("MQ", new NettyWebServer.RequestHandler(null, uuid, RPC_QUEUE_REPLY_TO, RPC_QUEUE_SEND_TO,true));

    }


   
}
