package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.google.gson.Gson;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class GetUserData extends ConcreteCommand {

    @Override
    protected void doCommand() {
//        UserData userData = ArangoInstance.insertNewUser(message.getUserData());
//        JSONObject response  = new JSONObject();
//        responseJson = jsonParser.parse(response.toString());
//        System.out.println(response);
    }

    @Override
    public void setMessage(Message message) {

    }
}
