<<<<<<< HEAD
package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.entity.DocumentEntity;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class UpdateUserData extends ConcreteCommand {

    @Override
    protected void doCommand() {
        Object userID = (String) message.getParameter("userData.id");
        Object userData = message.getParameter("userData");
        DocumentEntity data = ArangoInstance.updateUserData(userID, userData);
        if(data==null){
            // throw error 404
        }
        responseJson  = new JSONObject();
        JSONObject userDataJSON= new JSONObject(new Gson().toJson(data));
        responseJson.put("userData", new JSONObject(userDataJSON.get("newDocument").toString()));
    }

}
=======
package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Database.ArangoInstance;
import Interface.ConcreteCommand;
import Models.Message;
import Models.UserData;
import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.model.DocumentUpdateOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

public class UpdateUserData extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {

        ArangoDB arangoDB= ArangoInstance.getArangoDB();
        String userID= message.getUserID();
        UserData userData= message.getUserData();
        UserData userDataToFind = ArangoInstance.getUserData(userID);
        DocumentEntity res = null;
        System.out.println("UserData Is" + new Gson().toJson(userData));
        if (userDataToFind != null) {
            System.out.println(userData);
            res = arangoDB.db(ArangoInstance.getDbName()).collection("users").updateDocument(userID, gson.toJson(userData), new DocumentUpdateOptions().returnNew(true));
        } else {
               return HttpResponseTypes._404;
        }
        JsonObject response = new JsonObject();
        JSONObject userDataJSON=new JSONObject(res);
        String newDocumentString=userDataJSON.get("new").toString();
        response.add("userData", jsonParser.parse(newDocumentString));
        responseJson = jsonParser.parse(response.toString());
        return HttpResponseTypes._200;
    }

    @Override
    public void setMessage(Message message) {

    }
}
>>>>>>> e19d2841c1136831c0cabaaf2729f93cb75ced80
