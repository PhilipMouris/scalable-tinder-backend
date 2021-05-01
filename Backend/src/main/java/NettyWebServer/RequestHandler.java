package NettyWebServer;

import Config.Config;
import com.rabbitmq.client.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class RequestHandler extends ChannelInboundHandlerAdapter {

    private HashMap<String, ChannelHandlerContext> uuid;
    private String RPC_QUEUE_REPLY_TO;
    private String RPC_QUEUE_SEND_TO;

    private Config config = Config.getInstance();
    private String serverHost = config.getServerQueueHost();
    private int serverPort = config.getServerQueuePort();
    private String serverUser = config.getServerQueueUserName();
    private String serverPass = config.getServerQueuePass();
    private Channel senderChannel;

    RequestHandler(Channel channel, HashMap<String, ChannelHandlerContext> uuid, String RPC_QUEUE_REPLY_TO, String RPC_QUEUE_SEND_TO) {
        this.uuid = uuid;
        this.RPC_QUEUE_REPLY_TO = RPC_QUEUE_REPLY_TO;
        this.RPC_QUEUE_SEND_TO = RPC_QUEUE_SEND_TO;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) {
        if(!(o instanceof CompositeByteBuf) && !(o instanceof TextWebSocketFrame)){
            channelHandlerContext.fireChannelRead(o);
            return;
        }
        ByteBuf buffer;
        if(o instanceof TextWebSocketFrame) {
            buffer = (ByteBuf) (((TextWebSocketFrame)o).content());
        }
        else {
            buffer = (ByteBuf) o;
        }

        //try and catch
        try {
            JSONObject body = new JSONObject(buffer.toString(CharsetUtil.UTF_8));
            final JSONObject jsonRequest;
            final String corrId;
            if(o instanceof TextWebSocketFrame){
                jsonRequest = new JSONObject();
                jsonRequest.put("Headers", new JSONObject());
                corrId = UUID.randomUUID().toString();
            }
            else {
                jsonRequest = (JSONObject) channelHandlerContext.channel().attr(AttributeKey.valueOf("REQUEST")).get();
                corrId = (String) channelHandlerContext.channel().attr(AttributeKey.valueOf("CORRID")).get();
            }
            jsonRequest.put("command", body.get("command"));
            String service = (String) body.get("application");
            jsonRequest.put("application", service);

            if (body.has("image")){
//                String imageName = ImageWriter.write((String) body.get("image"));
                body.remove("image");
                body.put("imageUrl", "ALO");
//                System.out.println("Image Name : " + imageName);
            }

            jsonRequest.put("body", body);

            transmitRequest(corrId,jsonRequest,channelHandlerContext,service);
            if (o instanceof TextWebSocketFrame) {
                channelHandlerContext.fireChannelRead(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String responseMessage = "NO JSON PROVIDED";
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST,
                    copiedBuffer(responseMessage.getBytes()));
            channelHandlerContext.writeAndFlush(response);
        }

    }

    private void transmitRequest(String corrId, JSONObject jsonRequest, ChannelHandlerContext ctx,String appName){
        try {
            uuid.put(corrId,ctx);
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(RPC_QUEUE_REPLY_TO)
                    .build();
            System.out.println("Sent   : " + jsonRequest.toString() + "to: " +appName+"-Request");
            System.out.println();

            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(serverHost);
            connectionFactory.setPort(serverPort);
            connectionFactory.setUsername(serverUser);
            connectionFactory.setPassword(serverPass);
            Connection connection = null;
            Channel channel ;
            try {
                connection = connectionFactory.newConnection();
                channel = connection.createChannel();
                
                channel.basicPublish("", appName + "-Request", props, jsonRequest.toString().getBytes());
            }catch(IOException | TimeoutException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        System.out.println("ALO ERORR");
        cause.printStackTrace();
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes())
        ));
    }

}
