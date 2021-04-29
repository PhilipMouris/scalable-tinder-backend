package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.entity.DocumentDeleteEntity;
import com.google.gson.Gson;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class DeleteUserData extends ConcreteCommand {

    @Override
    protected void doCommand() {
        DocumentDeleteEntity dbRes = ArangoInstance.deleteUserData(message.getUserID());
        JSONObject dbResJSON= new JSONObject(gson.toJson(dbRes));
        JSONObject response  = new JSONObject();
        response.put("response",dbResJSON);
        responseJson = jsonParser.parse(response.toString());
    }

    @Override
    public void setMessage(Message message) {

    }
}
