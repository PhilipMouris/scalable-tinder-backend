package Chat;

import Database.ArangoInstance;
import NettyWebServer.HTTPHandler;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
    
    private static ConcurrentHashMap<String, Channel> webSocketMap = new ConcurrentHashMap<>();
    private ArangoInstance arangoInstance = new ArangoInstance(20);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HTTPHandler.class);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        JSONObject messageBody = new JSONObject(msg.text());
        Object chat = ArangoInstance.dbInstance.db("tinderDB").collection("chats").getDocument(messageBody.getJSONObject("chatData").getString("id"), Class.forName(String.format("Models.%s", "Chat")).newInstance().getClass());
        if (chat == null) {
            ctx.writeAndFlush(new TextWebSocketFrame("Chat does not exist"));
            return;
        }
        JSONObject jsonChat = new JSONObject((new Gson()).toJson(chat));
        String fromUserId = messageBody.getJSONObject("chatData").getJSONObject("message").getString("sourceUserId");
        String toUserId = fromUserId.equals(jsonChat.getString("userAId")) ?
                jsonChat.getString("userBId"): jsonChat.getString("userAId");
        Channel channel = webSocketMap.get(toUserId);

        if (channel == null || !channel.isActive()) {
            String message =  messageBody.getJSONObject("chatData").getJSONObject("message").toString();
            this.arangoInstance.createNotificaiton(Integer.parseInt(toUserId),
                    "messageReceived",
                    "Message received",
                    message
            );
            ctx.writeAndFlush(new TextWebSocketFrame("Not Online"));
        } else {
            channel.writeAndFlush(new TextWebSocketFrame(messageBody.getJSONObject("chatData").getJSONObject("message").toString()));
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null != msg && !(msg instanceof FullHttpRequest)) {
            ByteBuf buffer = (ByteBuf) (((TextWebSocketFrame)msg).content());
            JSONObject messageBody = new JSONObject(buffer.toString(CharsetUtil.UTF_8));
            String userId = messageBody.getJSONObject("chatData").getJSONObject("message").getString("sourceUserId");
            webSocketMap.put(userId, ctx.channel());
        }
        super.channelRead(ctx, msg);
    }
}

