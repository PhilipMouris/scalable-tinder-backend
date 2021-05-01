package Interface;

//import Cache.UserCacheController;
//import ClientService.Client;
import Cache.RedisConnection;
import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.ErrorLog;
import Database.PostgreSQL;
import Entities.HttpResponseTypes;
import Models.Message;
import com.arangodb.entity.DocumentEntity;
import com.google.gson.*;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    protected JSONObject jsonBodyObject;
    protected JSONObject responseJson = new JSONObject();
    protected Gson gson;
    protected JsonParser jsonParser;
    protected String customQuery;
    public  String  storedProcedure;
    protected String[] inputParams;
    protected String[] outputParams;
    protected String outputName = "record";
    protected String type;
    protected String model;
    protected String collection;
    protected Boolean useCache=false;
    protected RedisConnection redis = RedisConnection.getInstance();
    
    private final Logger LOGGER = Logger.getLogger(ConcreteCommand.class.getName()) ;

    @Override
    protected void execute() {

        try {
            TreeMap<String, Object> parameters = data;
            ArangoInstance = (ArangoInstance)
                    parameters.get("ArangoInstance");
            PostgresInstance = (PostgreSQL) parameters.get("PostgresInstance");
            LOGGER.log(Level.INFO,"ARANGO is "+ArangoInstance);
//            UserCacheController = (UserCacheController)
//                    parameters.get("UserCacheController");
//            ChatArangoInstance = (ChatArangoInstance)
//                    parameters.get("ChatArangoInstance");

            Channel channel = (Channel) parameters.get("channel");
            AMQP.BasicProperties replyProps = (AMQP.BasicProperties) parameters.get("replyProps");
            jsonParser = new JsonParser();
            String jsonString = (String) parameters.get("body");
            message = new Message();
            jsonBodyObject = new JSONObject(jsonString);
            message.setParameters(new JSONObject(jsonBodyObject.get("body").toString()));
            HttpResponseTypes status = doCommand();
            doCustomCommand();
            jsonBodyObject.put("response", responseJson);
            jsonBodyObject.put("status",jsonParser.parse(status.toString()));
            channel.basicPublish("", replyProps.getReplyTo(), replyProps, jsonBodyObject.toString().getBytes("UTF-8"));;
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

    private String generateSQLQuery(){
        if(customQuery!= null) return customQuery;
        String query = String.format("SELECT * FROM %s(", storedProcedure);
        for(int i =0;i<inputParams.length;i++){
            // TODO: Required Fields & validations
            Object parameter = message.getParameter(inputParams[i]);
            String newParameterString = parameter != null ? "'%s',":"%s,";
            query+= String.format(newParameterString, parameter);
        }
        query = query.substring(0,query.length()-1) + ")";
        return query;
    }
    private JSONArray convertToJSONArray(ResultSet resultSet)
            throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            JSONObject obj = new JSONObject();
            int totalRows = resultSet.getMetaData().getColumnCount();
            for (int i = 0; i < totalRows; i++) {
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));

            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }
    private String getSQLCommandId() {
        return String.format("%s%s",storedProcedure,message.getParameterValues(inputParams));
    }
    private HttpResponseTypes void handleSQLCommand() {
        if(storedProcedure==null && customQuery==null) return;
        try{
            String id = getSQLCommandId();
            if(useCache) {
                String value = redis.getKey(id);
                if(value != null){
                    responseJson.put(outputName,new JSONArray(value));
                    return;
                }
            }
        dbConn = PostgresInstance.getDataSource().getConnection();
        dbConn.setAutoCommit(true);
        Statement query = dbConn.createStatement();
        query.setPoolable(true);
        String SQLQuery = customQuery !=null ? customQuery : generateSQLQuery();
        set = query.executeQuery(SQLQuery);
        JSONObject response = new JSONObject();
        JSONArray data = convertToJSONArray(set);
        response.put(outputName,data );
        redis.setKey(id, data.toString());
        responseJson = response;
        LOGGER.log(Level.INFO,"Command: "+ this.getClass().getName()+" Executed Successfully");
        return HttpResponseTypes._200;
        }
        catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return HttpResponseTypes._500;
        } catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return HttpResponseTypes._500;
        }
        finally {
            PostgresInstance.disconnect(null, proc, dbConn);
        }

    }
    protected  HttpResponseTypes  handleNoSQLCommand(){
        try {
            if (collection ==  null) return;
            ArrayList<Object> parameters = new ArrayList<>();
            for(int i =0;i<inputParams.length;i++) {
                // TODO: Required Fields & validations
               parameters.add(message.getParameter(inputParams[i]));
            }

            responseJson = new JSONObject();
            JSONObject dbResponse = null;
            switch(type){
                case "create":
                    String key = ArangoInstance.insert(collection, parameters.get(0));
                    responseJson.put("id",key);
                    return;
                case "delete":
                     dbResponse = ArangoInstance.delete(collection, parameters.get(0));
                     responseJson.put(outputName,dbResponse);
                     break;
                //case "update":
                case "find":
                    dbResponse = ArangoInstance.find(collection, parameters.get(0), model);
                    break;
                case "update":
                    dbResponse  = ArangoInstance.update(collection,parameters.get(0),parameters.get(1));
            }
            responseJson.put(outputName, dbResponse);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void doCommand() {
        //Logger LOGGER = Logger.getLogger(UpdateBan.class.getName())
        setParameters();
        handleSQLCommand();
        handleNoSQLCommand();


    };

    public void setParameters(){
        
    };

    public void doCustomCommand(){
        
    }
}
