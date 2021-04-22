package Interface;

//import Cache.UserCacheController;
//import ClientService.Client;
//import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.ErrorLog;
import Entities.RequestBody;
import com.google.gson.*;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import io.netty.handler.logging.LogLevel;
import org.redisson.api.RLiveObjectService;
import spark.Request;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TreeMap;

public abstract class ConcreteCommand extends Command {


//    protected RLiveObjectService RLiveObjectService;
//    protected ArangoInstance ArangoInstance;
//    protected ChatArangoInstance ChatArangoInstance;
//    protected UserCacheController UserCacheController;
    protected RequestBody message;
    protected JsonElement responseJson = new JsonObject();
    protected Gson gson;
    protected JsonParser jsonParser;

    @Override
    protected void execute() {

        try {
            TreeMap<String, Object> parameters = data;
//            RLiveObjectService = (RLiveObjectService)
//                    parameters.get("RLiveObjectService");
//            ArangoInstance = (ArangoInstance)
//                    parameters.get("ArangoInstance");
//            UserCacheController = (UserCacheController)
//                    parameters.get("UserCacheController");
//            ChatArangoInstance = (ChatArangoInstance)
//                    parameters.get("ChatArangoInstance");

            Channel channel = (Channel) parameters.get("channel");
            AMQP.BasicProperties properties = (AMQP.BasicProperties) parameters.get("properties");
            AMQP.BasicProperties replyProps = (AMQP.BasicProperties) parameters.get("replyProps");
            Envelope envelope = (Envelope) parameters.get("envelope");

            jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse((String) parameters.get("body"));
            gson = new GsonBuilder().create();
            message = gson.fromJson(jsonObject.get("body").toString(), RequestBody.class);

            doCommand();

            jsonObject.add("response", responseJson);
            channel.basicPublish("", properties.getReplyTo(), replyProps, jsonObject.toString().getBytes("UTF-8"));;
//            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
        }
    }

    public void setMessage(RequestBody message) {
        this.message = message;
    }

    public RequestBody getMessage() {
        return message;
    }

    protected abstract void doCommand();
}
