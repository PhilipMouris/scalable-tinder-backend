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
import tests.HelpersTest;

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
    public void testCreateCommand(){
            JSONObject body = new JSONObject();
            JSONObject expected = new JSONObject();
             String reason = "Ban reason";
             int userID = 6;
             String expiryDate="9/9/2022";
             JSONObject banData = new JSONObject();
             banData.put("reason",reason);
             banData.put("user_id",userID);
             banData.put("expiry_date", expiryDate);
             banData.put("moderator_id",1);
             body.put("banData",banData);
             testHelpers.testCommand("CreateBan","Moderator","ban",body,expected);

    }
}
