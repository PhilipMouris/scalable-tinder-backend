package Interface;

//import Cache.UserCacheController;
//import ClientService.Client;
import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.ErrorLog;
import Database.PostgreSQL;
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
    

    @Override
    protected void execute() {

        try {
            TreeMap<String, Object> parameters = data;
            ArangoInstance = (ArangoInstance)
                    parameters.get("ArangoInstance");
            PostgresInstance = (PostgreSQL) parameters.get("PostgresInstance");
            Channel channel = (Channel) parameters.get("channel");
            AMQP.BasicProperties replyProps = (AMQP.BasicProperties) parameters.get("replyProps");
            jsonParser = new JsonParser();
            String jsonString = (String) parameters.get("body");
            message = new Message();
            jsonBodyObject = new JSONObject(jsonString);
            message.setParameters(new JSONObject(jsonBodyObject.get("body").toString()));
            doCommand();
            doCustomCommand();
            jsonBodyObject.put("response", responseJson);
            channel.basicPublish("", replyProps.getReplyTo(), replyProps, jsonBodyObject.toString().getBytes("UTF-8"));;
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
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
    private void handleSQLCommand() {
        if(storedProcedure==null) return;
        try{
        dbConn = PostgresInstance.getDataSource().getConnection();
        dbConn.setAutoCommit(true);
        Statement query = dbConn.createStatement();
        query.setPoolable(true);
        String SQLQuery = customQuery !=null ? customQuery : generateSQLQuery();
        set = query.executeQuery(SQLQuery);
        JSONObject response = new JSONObject();
        response.put(outputName, convertToJSONArray(set));
        responseJson = response;
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            PostgresInstance.disconnect(null, proc, dbConn);
        }

    }
    protected  void handleNoSQLCommand(){
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
        setParameters();
        handleSQLCommand();
        handleNoSQLCommand();


    };

    public void setParameters(){
        
    };

    public void doCustomCommand(){
        
    }
}
