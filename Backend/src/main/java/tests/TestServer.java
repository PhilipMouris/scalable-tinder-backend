package tests;


import Config.Config;
import Controller.Controller;
import Database.PostgreSQL;
import Entities.ServicesType;
import Interface.ServiceControl;
import MessageQueue.ServicesMQ;
import NettyWebServer.HTTPHandler;
import NettyWebServer.NettyServer;
import NettyWebServer.NettyServerInitializer;
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
import java.util.logging.Level;

public class TestServer {
    public static boolean isInitialized = false;
    public static ServicesType[] services = new ServicesType[]{ServicesType.user,ServicesType.moderator, ServicesType.chat,ServicesType.user_to_user};
    private EmbeddedChannel channel;

    public EmbeddedChannel getChannel(){
        return channel;
    }

    private static int getInitialInstanceNum(ServicesType serviceName){
        Config config = Config.getInstance();
        switch (serviceName){
            case user:
                return config.getUserServiceNumInstances();
            case chat:
                return config.getChatServiceNumInstances();
            case moderator:
                return config.getModeratorServiceNumInstances();
            case user_to_user:
                return config.getUserToUserServiceNumInstances();
        }
        return 0;
    }

    public void initialize(){
        Controller c = new Controller();
        for(ServicesType type:services) {
            for(int i =0;i<getInitialInstanceNum(type);i++) {
                ServiceControl s = c.initService(type);
               Thread t =  new Thread(() -> {
                    s.start();
                });
               t.start();
               try {
                   t.join();
                   t.setDaemon(false);
                   Thread.sleep(200);
               }catch(Exception e){
                   e.printStackTrace();
               }
              
            }
        }
    }

    public void initializeDB(){
        PostgreSQL db = new PostgreSQL();
        db.populateDB();
    }

    public TestServer() {
        //new NettyServer(8020);
        new ServicesMQ();
        if(!isInitialized){
            initialize();
            initializeDB();
        isInitialized = true;
        }

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
        HashMap<String, ChannelHandlerContext> uuid = NettyServerInitializer.getUuid();
        this.channel = new EmbeddedChannel();
        channel.pipeline().addLast(new HttpRequestDecoder()).
                addLast(new HttpResponseDecoder()).
                addLast(new CorsHandler(corsConfig)).
                addLast(new HttpObjectAggregator(10000000)).
                addLast(new HTTPHandler()).
                addLast("MQ", new NettyWebServer.RequestHandler(null, uuid, RPC_QUEUE_REPLY_TO, RPC_QUEUE_SEND_TO,true));

    }



   
}
