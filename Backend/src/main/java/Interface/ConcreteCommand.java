package Interface;

//import Cache.UserCacheController;
//import ClientService.Client;
import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.ErrorLog;
import Database.PostgreSQL;
import Entities.HttpResponseTypes;
import Models.Message;
import com.google.gson.*;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ConcreteCommand extends Command {


//    protected RLiveObjectService RLiveObjectService;
    protected ArangoInstance ArangoInstance;
    protected PostgreSQL PostgresInstance;
//    protected ChatArangoInstance ChatArangoInstance;
//    protected UserCacheController UserCacheController;
    protected Message message;
    protected JsonObject jsonBodyObject;
    protected JsonElement responseJson = new JsonObject();
    protected Gson gson;
    protected JsonParser jsonParser;
    private final Logger LOGGER = Logger.getLogger(ConcreteCommand.class.getName()) ;

    @Override
    protected void execute() {

        try {
            TreeMap<String, Object> parameters = data;
//            RLiveObjectService = (RLiveObjectService)
//                    parameters.get("RLiveObjectService");
            ArangoInstance = (ArangoInstance)
                    parameters.get("ArangoInstance");
            PostgresInstance = (PostgreSQL) parameters.get("PostgresInstance");
            LOGGER.log(Level.INFO,"ARANGO is "+ArangoInstance);
//            UserCacheController = (UserCacheController)
//                    parameters.get("UserCacheController");
//            ChatArangoInstance = (ChatArangoInstance)
//                    parameters.get("ChatArangoInstance");

            Channel channel = (Channel) parameters.get("channel");
            AMQP.BasicProperties properties = (AMQP.BasicProperties) parameters.get("properties");
            AMQP.BasicProperties replyProps = (AMQP.BasicProperties) parameters.get("replyProps");
            Envelope envelope = (Envelope) parameters.get("envelope");

            jsonParser = new JsonParser();
            jsonBodyObject = (JsonObject) jsonParser.parse((String) parameters.get("body"));
            gson = new GsonBuilder().setDateFormat("YYYY-MM-dd HH:mm:SS").create();
//            System.out.println(jsonBodyObject.get("body").toString());
            message = gson.fromJson(jsonBodyObject.get("body").toString(), Message.class);

            HttpResponseTypes status = doCommand();
            jsonBodyObject.add("status",jsonParser.parse(status.toString()));
            jsonBodyObject.add("response", responseJson);
            channel.basicPublish("", replyProps.getReplyTo(), replyProps, jsonBodyObject.toString().getBytes("UTF-8"));;
//            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
//            StringWriter errors = new StringWriter();
//            e.printStackTrace(new PrintWriter(errors));
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
        }
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    protected abstract HttpResponseTypes doCommand();
}
