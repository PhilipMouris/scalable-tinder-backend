package tests;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class HelpersTest {
    TestServer server;
    EmbeddedChannel channel;

    public HelpersTest(){
        server = new TestServer();
        channel = server.getChannel();
    }

    public TestServer getTestServer(){
        return server;
    }

    public EmbeddedChannel getEmbbeddedChannel(){
        return channel;
    }

    @Test
    @DisplayName("Request Sent")
    public void requestSent(FullHttpRequest request){
        Boolean isSent = channel.writeInbound(request);
        assertTrue(isSent);
    }

    @Test
    @DisplayName("Response Received")
    public void responseReceived(){
        int messageSize = channel.outboundMessages().size();
        assertNotEquals(messageSize,0);
    }

    public void sendRequest(String commandName, String application, JSONObject body){
        body.put("command",commandName);
        body.put("application",application);
        System.out.println(body.toString() + "OKK??");
        ByteBuf bbuf = Unpooled.copiedBuffer( body.toString(), StandardCharsets.UTF_8);
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, "http://127.0.0.1:8020",bbuf);
        request.headers().add("Content-Type","application/json");
        requestSent(request);
        responseReceived();

    }

    private JSONObject readResponse(){
        FullHttpResponse httpResponse = channel.readOutbound();
        String httpResponseContent = httpResponse.content().toString(Charset.defaultCharset());
        return new JSONObject(httpResponseContent);
    }

    @Test
    @DisplayName("Compare Responses")
    public void compareResponses(JSONObject response,JSONObject expected, boolean strictMatch){
         Iterator<String> expectedKeys = expected.keys();
         //
         //if(strictMatch)
         while(expectedKeys.hasNext()){
             String expectedKey = expectedKeys.next();
             assertTrue(response.has(expectedKey));
             Object expectedValue = expected.get(expectedKey);
             Object receivedValue = response.get(expectedKey);
             assertTrue(expectedValue.equals((receivedValue)));

         }
    }


    public void testCommand(String commandName,String application,JSONObject body,JSONObject expected, boolean strictMatch ){
           sendRequest(commandName,application,body);
           JSONObject response = readResponse();
           compareResponses(response,expected, strictMatch);


    }

    public void testCommand(String commandName,String application,JSONObject body,JSONObject expected ){
        sendRequest(commandName,application,body);
        JSONObject response = readResponse();
        compareResponses(response,expected, false);


    }
}
