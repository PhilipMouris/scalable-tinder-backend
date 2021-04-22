package Controller;

import Config.Config;
import Interface.ServiceControl;
import Entities.ControlCommand;
import Entities.ControlMessage;
import Entities.ErrorLog;
import Entities.ServicesType;
import Services.ChatService;
import Services.UserToUserService;
import Services.UserService;
import Services.ModeratorService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;

public class Controller {

    public static Channel channel;
    protected ServiceControl service;
    Config conf = Config.getInstance();
    private String server = conf.getControllerHost();
    private int port = conf.getControllerPort();
    private String serviceName;

//    public static void main(String[] args) {
//        Client c = new Client();
//        c.initService(ServicesType.post);
//        new Thread(() -> {
//            c.start();
//        }).start();
//        c.startService();
//    }

    public void initDB(){
        service.initDB();
    }

    public void initService(ServicesType serviceName) {
        switch (serviceName) {
            case user:
                service = new UserService();
                this.serviceName = conf.getServicesMqUserQueue();
                break;
            case user_to_user:
                service = new UserToUserService();
                this.serviceName = conf.getServicesMqUserToUserQueue();
                break;
            case moderator:
                service = new ModeratorService();
                this.serviceName = conf.getServicesMqModeratorQueue();
                break;
            case chat:
                service = new ChatService();
                this.serviceName = conf.getServicesMqChatQueue();
                break;
            // TODO ADD SERVICE
        }
    }

    public void startService() {
        this.service.start();
    }

    public void start() {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
//            clientBootstrap.remoteAddress(new InetSocketAddress(server, port));
            clientBootstrap.handler(new ControllerAdapterInitializer(service));

            System.err.println("Controller is listening on http://127.0.0.1:" + port + '/');

//            takeConsoleInput();

            ChannelFuture channelFuture = clientBootstrap.bind(port).sync();
//            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            Controller.channel = channelFuture.channel();

//            Thread t = new Thread(() -> {
//                Scanner sc = new Scanner(System.in);
//                while (true){
//                    String line = sc.nextLine();
//                    ErrorLog l = new ErrorLog(LogLevel.ERROR, line);
//                    Client.channel.writeAndFlush(l);
//                }
//            });
//            t.start();

            Controller.channel.writeAndFlush(new ControlMessage(ControlCommand.initialize, serviceName));
            Controller.channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,errors.toString()));
        } finally {
            group.shutdownGracefully();
//            System.exit(0);
        }
    }
}