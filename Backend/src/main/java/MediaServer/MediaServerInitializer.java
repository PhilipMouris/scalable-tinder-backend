package MediaServer;

import Chat.TextWebSocketFrameHandler;
import Config.Config;
import NettyWebServer.HTTPHandler;
import NettyWebServer.RequestHandler;
import com.rabbitmq.client.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class MediaServerInitializer extends ChannelInitializer<SocketChannel> {

    private Config config = Config.getInstance();



    private static HashMap<String, ChannelHandlerContext> uuid = new HashMap<String, ChannelHandlerContext>();
    private Channel receiverChannel;
    private Channel senderChannel;


    private final Logger LOGGER = Logger.getLogger(MediaServerInitializer.class.getName()) ;

    public MediaServerInitializer(int port) {

    }
    public static HashMap<String, ChannelHandlerContext> getUuid() {
        return uuid;
    }

    @Override
    protected void initChannel(SocketChannel arg0) {

        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
                .allowedRequestHeaders("X-Requested-With", "Content-Type", "Content-Length")
                .allowedRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS,HttpMethod.HEAD)
                .build();
        ChannelPipeline p = arg0.pipeline();
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast(new CorsHandler(corsConfig));
        p.addLast(new HttpObjectAggregator(10000000));
//        p.addLast(new HTTPHandler());
        p.addLast(new MediaHandler());

    }

   
}
