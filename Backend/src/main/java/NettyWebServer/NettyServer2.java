package NettyWebServer;

import Config.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer2 {

    Config c = Config.getInstance();
    private int port = c.getWebServerPort();
    public NettyServer2(int port){
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
                    .childHandler(new NettyServerInitializer(port));
            Channel ch = b.bind(port).sync().channel();

            System.err.println("Web Server is listening on http://127.0.0.1:" + port + '/');

            ch.closeFuture().sync();

        }  
        catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyServer2 s = new NettyServer2(8020);
        s.start();
        
    }
}
