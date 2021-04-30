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
        // DocumentEntity userData = ArangoInstance.updateUserData(message.getUserID(),message.getUserData());
//        JSONObject response  = new JSONObject();
//        JSONObject userDataJSON= new JSONObject(gson.toJson(userData));
//        response.put("userData", userDataJSON);
        //responseJson = jsonParser.parse(response.toString());
    }

    @Override
    public void setMessage(Message message) {

    }
}
