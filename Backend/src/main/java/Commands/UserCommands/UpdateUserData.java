package Commands.UserCommands;

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
    protected void doCommand() {

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
//                throw error 404
        }
        JsonObject response = new JsonObject();
        JSONObject userDataJSON=new JSONObject(res);
        String newDocumentString=userDataJSON.get("new").toString();
        response.add("userData", jsonParser.parse(newDocumentString));
        responseJson = jsonParser.parse(response.toString());
    }

    @Override
    public void setMessage(Message message) {

    }
}
