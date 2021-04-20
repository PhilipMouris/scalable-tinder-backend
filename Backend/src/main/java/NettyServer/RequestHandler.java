package NettyServer;

import com.rabbitmq.client.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class RequestHandler extends ChannelInboundHandlerAdapter {

    private HashMap<String, ChannelHandlerContext> uuid;
    private String RPC_QUEUE_REPLY_TO;
    private String RPC_QUEUE_SEND_TO;
    private Channel senderChannel;

    RequestHandler(Channel channel, HashMap<String, ChannelHandlerContext> uuid, String RPC_QUEUE_REPLY_TO, String RPC_QUEUE_SEND_TO) {
        this.uuid = uuid;
        this.RPC_QUEUE_REPLY_TO = RPC_QUEUE_REPLY_TO;
        this.RPC_QUEUE_SEND_TO = RPC_QUEUE_SEND_TO;
        this.senderChannel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) {
        ByteBuf buffer = (ByteBuf) o;

        //try and catch
        try {
            JSONObject body = new JSONObject(buffer.toString(CharsetUtil.UTF_8));

            final JSONObject jsonRequest = (JSONObject) channelHandlerContext.channel().attr(AttributeKey.valueOf("REQUEST")).get();
            final String corrId = (String) channelHandlerContext.channel().attr(AttributeKey.valueOf("CORRID")).get();
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

            transmitRequest(corrId,jsonRequest,channelHandlerContext);

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

    private void transmitRequest(String corrId, JSONObject jsonRequest, ChannelHandlerContext ctx){
        try {
            uuid.put(corrId,ctx);
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(RPC_QUEUE_REPLY_TO)
                    .build();
            System.out.println("Sent   : " + jsonRequest.toString());
            System.out.println();
            senderChannel.basicPublish("", RPC_QUEUE_SEND_TO, props, jsonRequest.toString().getBytes());
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
        cause.printStackTrace();
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes())
        ));
    }

}
