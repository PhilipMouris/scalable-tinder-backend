package MediaServer;

import Config.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MediaServer {

    Config c = Config.getInstance();
    private int port ;
    public final Logger LOGGER = Logger.getLogger(MediaServer.class.getName()) ;
    public MediaServer(int port){
        this.port = port;
    }
    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(4);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MediaServerInitializer(port));
//            b.option(ChannelOption.SO_KEEPALIVE, true);
            Channel ch = b.bind(port).sync().channel();


//            System.err.println("Web Server is listening on http://127.0.0.1:" + port + '/');
            LOGGER.log(Level.INFO,"Web Server is listening on http://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();

        }  
        catch (InterruptedException e) {
            System.out.println("ALO");
            e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int[] ports = new int[]{8050};
        for(int port :ports){
            MediaServer s = new MediaServer(port);
            new Thread(() -> {
                s.start();
            }).start();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();s.LOGGER.log(Level.SEVERE,e.getMessage(),e);
            }
        }
    }
}
