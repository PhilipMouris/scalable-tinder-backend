package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import org.json.JSONObject;

public class CreateUserData extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        String id = ArangoInstance.insertNewUser(message.getUserData());
        JSONObject response  = new JSONObject();
        response.put("id",id);
        responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
        return HttpResponseTypes._200;

    }

    @Override
    public void setMessage(Message message) {

    }
}
