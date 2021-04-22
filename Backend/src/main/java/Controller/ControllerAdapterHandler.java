package Controller;

import Interface.ServiceControl;
import Entities.ControlMessage;
import Entities.ErrorLog;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class ControllerAdapterHandler extends ChannelInboundHandlerAdapter {

    private HashMap<String,ServiceControl> availableServices ;

    public ControllerAdapterHandler(HashMap<String,ServiceControl> availableServices) {
        this.availableServices= availableServices;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object arg1) {
        ByteBuf buffer = (ByteBuf) arg1;

        //try and catch
        try {
            JSONObject body = new JSONObject(buffer.toString(CharsetUtil.UTF_8));

            final JSONObject jsonRequest = (JSONObject) channelHandlerContext.channel().attr(AttributeKey.valueOf("REQUEST")).get();
            final String corrId = (String) channelHandlerContext.channel().attr(AttributeKey.valueOf("CORRID")).get();
            jsonRequest.put("command", body.get("command"));
            String service_s = (String) body.get("application");
            String param = (String) body.get("param");
            String path = (String) body.get("path");
            jsonRequest.put("application", service_s);
            jsonRequest.put("body", body);
            ServiceControl service = availableServices.get(service_s);
            String responseMessage = controlService(channelHandlerContext,service,(String)(body.get("command")),param,path);

            Controller.sendResponse(channelHandlerContext,responseMessage);

        } catch (JSONException e) {
            e.printStackTrace();
            String responseMessage = "NO CORRECT JSON PROVIDED";
            Controller.sendResponse(channelHandlerContext,responseMessage);
        }
        
    }

    private String controlService(ChannelHandlerContext ctx,ServiceControl service,String command,String param,String path){
        String responseMessage = "";
        try {
            switch (command) {
                case "set_max_db_connections_count":
                    service.setMaxDBConnections(Integer.parseInt(param));
                    responseMessage = "MAX DB CONNECTIONS SET";
                    break;
                case "set_max_thread_count":
                    service.setMaxThreadsSize(Integer.parseInt(param));
                    responseMessage = "MAX THREAD COUNT SET";
                    break;
                case "continue":
                    service.resume();
                    responseMessage = "SERVICE RESUMED";
                    break;
                case "freeze":
                    service.freeze();

                    responseMessage = "SERVICE FROZE";
                    break;
                case "add_command":
                    service.add_command(param, path);
                    responseMessage = "COMMAND ADDED";
                    break;
                case "delete_command":
                    service.delete_command(param);
                    responseMessage = "COMMAND DELETED";
                    break;
                case "update_command":
                    service.update_command(param, path);
                    responseMessage = "COMMAND UPDATED";
                    break;
//            case dropPostDB: service.dropPostDB();
//                break;
//            case createPostDB: service.createPostDB();
//                break;
//            case seedPostDB: service.seedPostDB();
//                break;
                default: {
                    responseMessage = "Unknown Command";

                    break;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.ACCEPTED,
                    copiedBuffer(errors.toString().getBytes()));
            ctx.writeAndFlush(response);
        }
        return responseMessage;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext arg0) {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext arg0) {

    }

}