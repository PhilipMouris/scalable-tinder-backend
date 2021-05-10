package NettyWebServer;

import Config.Config;
import Entities.MediaServerRequest;
import com.google.gson.JsonObject;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName()) ;

    private Channel senderChannel;

    RequestHandler(Channel channel, HashMap<String, ChannelHandlerContext> uuid, String RPC_QUEUE_REPLY_TO, String RPC_QUEUE_SEND_TO) {
        this.uuid = uuid;
        this.RPC_QUEUE_REPLY_TO = RPC_QUEUE_REPLY_TO;
        this.RPC_QUEUE_SEND_TO = RPC_QUEUE_SEND_TO;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) {
        if(!(o instanceof CompositeByteBuf) && !(o instanceof TextWebSocketFrame)&&!(o instanceof MediaServerRequest) ){
            channelHandlerContext.fireChannelRead(o);
            return;
        }

        ByteBuf buffer;
        JSONObject body;
        if(o instanceof TextWebSocketFrame) {
            buffer = (ByteBuf) (((TextWebSocketFrame)o).content());
            body = new JSONObject(buffer.toString(CharsetUtil.UTF_8));

        }
        if(o instanceof MediaServerRequest) {
            body = ((MediaServerRequest)o).getRequest();
        }
        else {
            buffer = (ByteBuf) o;
            body = new JSONObject(buffer.toString(CharsetUtil.UTF_8));

        }

        //try and catch
        try {
            final JSONObject jsonRequest;
            final String corrId;
            if(o instanceof TextWebSocketFrame ){
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
            jsonRequest.put("body", body);
            authenticate(channelHandlerContext, jsonRequest);

            if(o instanceof  MediaServerRequest){
                 MediaServerRequest msr= ((MediaServerRequest)o);
                 msr.setJsonRequest(jsonRequest.toString());
                 transmitMediaRequest(corrId,msr.getByteArray(),channelHandlerContext,service);
             }
             else{
                 transmitRequest(corrId,jsonRequest,channelHandlerContext,service);
                }
             if (o instanceof TextWebSocketFrame) {
                channelHandlerContext.fireChannelRead(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);
            String responseMessage = "NO JSON PROVIDED";
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST,
                    copiedBuffer(responseMessage.getBytes()));
            channelHandlerContext.writeAndFlush(response);
        }

    }
    private void transmitMediaRequest(String corrId, byte[] byteArray, ChannelHandlerContext ctx, String appName) {
        try {
            uuid.put(corrId,ctx);
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(RPC_QUEUE_REPLY_TO)
                    .build();
//            System.out.println("Sent   : " + jsonRequest.toString() + "to: " +appName+"-Request");
            LOGGER.log(Level.INFO,"Sent File to "+appName+"-Request");
//            System.out.println();

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

                channel.basicPublish("", appName + "-Request", props, byteArray);
            }catch(IOException | TimeoutException e) {
                e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);

                LOGGER.log(Level.SEVERE,e.getMessage(),e);
            }

        } catch (Exception e) {
            e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);
            LOGGER.log(Level.SEVERE,e.getMessage(),e);

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
//            System.out.println("Sent   : " + jsonRequest.toString() + "to: " +appName+"-Request");
            LOGGER.log(Level.INFO,"Sent   : " + jsonRequest.toString() + "to: " +appName+"-Request");
//            System.out.println();

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
                e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);
                
                LOGGER.log(Level.SEVERE,e.getMessage(),e);
            }

        } catch (Exception e) {
            e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);
            LOGGER.log(Level.SEVERE,e.getMessage(),e);

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
        LOGGER.log(Level.SEVERE,cause.getMessage(),cause);
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(cause.getMessage().getBytes())
        ));
    }

    public void authenticate(ChannelHandlerContext channelHandlerContext, JSONObject jsonRequest) {
        if (jsonRequest.getString("command").equals("SignIn")
            || jsonRequest.getString("command").equals("SignUp")
            || jsonRequest.getString("command").equals("UpdateChat")
            || jsonRequest.getString("command").equals("UploadMedia")
            )
            return;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(jsonRequest.getJSONObject("Headers").getString("authorization"));
        } catch(JWTVerificationException | JSONException exception) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("error", "Permission denied, you need to sign in");
            FullHttpResponse unauthorizedResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.UNAUTHORIZED,
                    copiedBuffer(jsonResponse.toString().getBytes()));
            unauthorizedResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            unauthorizedResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, unauthorizedResponse.content().readableBytes());
            channelHandlerContext.writeAndFlush(unauthorizedResponse);
            channelHandlerContext.close();
        }
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            // As of https://en.wikipedia.org/wiki/X-Forwarded-For
            // The general format of the field is: X-Forwarded-For: client, proxy1, proxy2 ...
            // we only want the client
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }
}
