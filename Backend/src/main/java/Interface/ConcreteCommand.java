package Interface;

//import Cache.UserCacheController;
//import ClientService.Client;
import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.ErrorLog;
import Models.Message;
import Models.Message;
import com.google.gson.*;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TreeMap;

public abstract class ConcreteCommand extends Command {


//    protected RLiveObjectService RLiveObjectService;
    protected ArangoInstance ArangoInstance;
//    protected ChatArangoInstance ChatArangoInstance;
//    protected UserCacheController UserCacheController;
    protected Message message;
    protected JsonElement responseJson = new JsonObject();
    protected Gson gson;
    protected JsonParser jsonParser;

    @Override
    protected void execute() {

        try {
            TreeMap<String, Object> parameters = data;
//            RLiveObjectService = (RLiveObjectService)
//                    parameters.get("RLiveObjectService");
            ArangoInstance = (ArangoInstance)
                    parameters.get("ArangoInstance");
            System.out.println("ARANGO is "+ArangoInstance);
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
            System.out.println(jsonObject.get("body").toString());
            message = gson.fromJson(jsonObject.get("body").toString(), Message.class);

            doCommand();

            jsonObject.add("response", responseJson);
            channel.basicPublish("", replyProps.getReplyTo(), replyProps, jsonObject.toString().getBytes("UTF-8"));;
//            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
        }
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    protected abstract void doCommand();
}
