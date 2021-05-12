package tests;

import Config.Config;
import MediaServer.MediaHandler;
import NettyWebServer.HTTPHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.util.CharsetUtil;
import jdk.jfr.ContentType;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class ModeratorCommandsTest {
    HelpersTest testHelpers = new HelpersTest();

    @Test
    @DisplayName("TESTING")
    public void testOK(){
            JSONObject body = new JSONObject();
            JSONObject expected = new JSONObject();
             expected.put("error","Permission denied, you need to sign in");
              testHelpers.testCommand("CreateUserInteraction","UserToUser",body,expected);

//            ByteBuf bbuf = Unpooled.copiedBuffer( body.toJSONString(), StandardCharsets.UTF_8);
//            FullHttpRequest request = new DefaultFullHttpRequest(
//                    HttpVersion.HTTP_1_1, HttpMethod.POST, "http://127.0.0.1:8020",bbuf);
//            request.headers().add("Content-Type","application/json");
//
//
//
//            Boolean test = channel.writeInbound(request);
//            System.out.println(test + "TEST???");
//            System.out.println(channel.outboundMessages().size() + "SIZE");
//            FullHttpResponse httpResponse = channel.readOutbound();
//            channel.checkException();
//            System.out.println(httpResponse + "RESPONSE");
//            String httpResponseContent = httpResponse.content()
//                    .toString(Charset.defaultCharset());
//            System.out.println(httpResponseContent + "RESPONSE");


        //assertEquals(20,20,"OKKK");
    }
}
