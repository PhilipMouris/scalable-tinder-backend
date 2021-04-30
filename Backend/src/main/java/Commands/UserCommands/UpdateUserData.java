package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.entity.DocumentEntity;
import org.json.JSONObject;

public class UpdateUserData extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        DocumentEntity userData = ArangoInstance.updateUserData(message.getUserID(),message.getUserData());
        System.out.println(userData);
        JSONObject response  = new JSONObject();
        JSONObject userDataJSON= new JSONObject(gson.toJson(userData));
        System.out.println(userDataJSON);
        response.put("userData", userDataJSON);
        responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
        return HttpResponseTypes._200;
    }

    @Override
    public void setMessage(Message message) {

    }
}
