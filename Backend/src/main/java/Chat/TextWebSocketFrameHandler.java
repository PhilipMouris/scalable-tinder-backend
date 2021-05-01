package Chat;

import NettyWebServer.HTTPHandler;
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
        String toUserId = messageBody.getJSONObject("body").getString("toUserId");
        Channel channel = webSocketMap.get(toUserId);
        if (channel == null || !channel.isActive()) {
            ctx.writeAndFlush(new TextWebSocketFrame("Not Online"));
        } else {
            channel.writeAndFlush(new TextWebSocketFrame(messageBody.getJSONObject("body").toString()));
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null != msg && !(msg instanceof FullHttpRequest)) {
            ByteBuf buffer = (ByteBuf) (((TextWebSocketFrame)msg).content());
            JSONObject messageBody = new JSONObject(buffer.toString(CharsetUtil.UTF_8));
            String userId = messageBody.getJSONObject("body").getString("fromUserId");
            webSocketMap.put(userId, ctx.channel());
        }
        super.channelRead(ctx, msg);
    }
}

