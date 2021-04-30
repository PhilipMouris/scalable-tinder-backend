package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.entity.DocumentEntity;
import org.json.JSONObject;

public class UserAddBio extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        DocumentEntity userData = ArangoInstance.userAddBio(message.getUserID(),message.getBio());
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
