package Interface;

//import Cache.UserCacheController;
//import ClientService.Client;
import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.ErrorLog;
import Database.PostgreSQL;
import Models.Message;
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
        JSONObject messageParams = message.getParameters();
        if(customQuery!= null) return customQuery;
        String query = String.format("SELECT * FROM %s(", storedProcedure);
        for(int i =0;i<inputParams.length;i++){
            // TODO: Required Fields & validations
            query+= messageParams.has(inputParams[i])?
                    messageParams.get(inputParams[i]) : null;
            query+= ",";
        }
        query = query.substring(0,query.length()-1) + ")";
        return query;
    }
    public JSONArray convertToJSONArray(ResultSet resultSet)
            throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            JSONObject obj = new JSONObject();
            int total_rows = resultSet.getMetaData().getColumnCount();
            for (int i = 0; i < total_rows; i++) {
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));

            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }
    protected void doCommand() {
        try {
            setParameters();
            // In case only a custom command is needed
            if(storedProcedure==null)return;
            dbConn = PostgresInstance.getDataSource().getConnection();
            dbConn.setAutoCommit(true);
            Statement query = dbConn.createStatement();
            query.setPoolable(true);
            String SQLQuery = customQuery !=null ? customQuery : generateSQLQuery();
            System.out.println(SQLQuery + SQLQuery);
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
    };

    public void setParameters(){
        
    };

    public void doCustomCommand(){
        
    }
}
