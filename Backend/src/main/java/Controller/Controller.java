package Controller;

import Config.Config;
import Interface.ServiceControl;
import Entities.ControlCommand;
import Entities.ControlMessage;
import Entities.ErrorLog;
import Entities.ServicesType;
import NettyWebServer.NettyServerInitializer;
import Services.ChatService;
import Services.UserToUserService;
import Services.UserService;
import Services.ModeratorService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class Controller {

    public static Channel channel;
    protected ServiceControl service;
    Config conf = Config.getInstance();
    private String server = conf.getControllerHost();
    private int port = conf.getControllerPort();
    private String serviceName;
    private HashMap<String,ServiceControl>  availableServices = new HashMap<>();
//    public static void main(String[] args) {
//        Client c = new Client();
//        c.initService(ServicesType.post);
//        new Thread(() -> {
//            c.start();
//        }).start();
//        c.startService();
//    }

    public void initDBs(){
        for(String service : availableServices.keySet()){
            availableServices.get(service).initDB();
        }
    }
    public static void sendResponse(ChannelHandlerContext ctx,String responseMsg){
        JsonParser jsonParser = new JsonParser();
        JSONObject responseJ= new JSONObject();
        responseJ.put("Message",responseMsg);
        JsonObject responseJson =(JsonObject) jsonParser.parse(responseJ.toString());
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                copiedBuffer(responseJson.get("Message").toString().getBytes()));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response);
    }
    public void initService(ServicesType serviceName) {
        switch (serviceName) {
            case user:
                service = new UserService();
//                this.serviceName = conf.getServicesMqUserQueue();
                availableServices.putIfAbsent(conf.getServicesMqUserQueue(),service);
                break;
            case user_to_user:
                service = new UserToUserService();
//                this.serviceName = conf.getServicesMqUserToUserQueue();
                availableServices.putIfAbsent(conf.getServicesMqUserToUserQueue(),service);

                break;
            case moderator:
                service = new ModeratorService();
//                this.serviceName = conf.getServicesMqModeratorQueue();
                availableServices.putIfAbsent(conf.getServicesMqModeratorQueue(),service);
                break;
            case chat:
                service = new ChatService();
//                this.serviceName = conf.getServicesMqChatQueue();
                availableServices.putIfAbsent(conf.getServicesMqChatQueue(),service);
                break;
            // TODO ADD SERVICE
        }
    }

    public void startServices() {
        for(String service : availableServices.keySet()){
            availableServices.get(service).start();
        }
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ControllerAdapterInitializer(availableServices));
//            b.option(ChannelOption.SO_KEEPALIVE, true);
            Channel ch = b.bind(port).sync().channel();
            Controller.channel = ch;
            System.err.println("Controller is listening on http://127.0.0.1:" + port + '/');
//            ch.writeAndFlush(new ControlMessage(ControlCommand.initialize, serviceName));
            ch.closeFuture().sync();

//            Thread t = new Thread(() -> {
//                Scanner sc = new Scanner(System.in);
//                while (true){
//                    String line = sc.nextLine();
//                    ErrorLog l = new ErrorLog(LogLevel.ERROR, line);
//                    Client.channel.writeAndFlush(l);
//                }
//            });
//            t.start();

//            Controller.channel.writeAndFlush(new ControlMessage(ControlCommand.initialize, serviceName));
//            Controller.channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,errors.toString()));
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
//            System.exit(0);
        }
    }
}