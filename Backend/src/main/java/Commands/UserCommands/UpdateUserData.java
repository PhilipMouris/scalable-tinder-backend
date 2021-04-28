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
        DocumentEntity userData = ArangoInstance.updateUserData(message.getUserID(),message.getUserData());
        System.out.println(userData);
        JSONObject response  = new JSONObject();
        JSONObject userDataJSON= new JSONObject(gson.toJson(userData));
        System.out.println(userDataJSON);
        response.put("userData", userDataJSON);
        responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
    }

    @Override
    public void setMessage(Message message) {

    }
}
