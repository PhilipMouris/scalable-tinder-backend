package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import Database.ArangoInstance;
import Models.UserData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


public class GetUserDataPaginated extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        ArangoDB arangoDB=ArangoInstance.getArangoDB();
        List<UserData> userDataList=new ArrayList<UserData>();
        String dbName=ArangoInstance.getDbName();
        try {
            String query = "FOR u IN users LIMIT @offset,@count RETURN u";
            Map<String, Object> bindVars = new HashMap<String,Object> ();
            bindVars.put("count",message.getParameter("limit"));
            bindVars.put("offset", (int) message.getParameter("page") * (int) message.getParameter("limit"));
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                System.out.println("Key: " + aDocument.getProperties());
                UserData userData=gson.fromJson(gson.toJson(aDocument.getProperties()),UserData.class);
                if(userData.getPreferences() !=null) {
                    System.out.println(userData.getPreferences().age + "AGE");
                }
                userData.set_key(aDocument.getKey());
                userDataList.add(userData);
            });

        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
        JSONObject response = new JSONObject();
        response.put("userDataArray", new JSONArray(gson.toJson(userDataList)));
        responseJson = response;
        System.out.println(response);
        return  HttpResponseTypes._200;
    }
    @Override
    public void setMessage(Message message) {

    }
}
