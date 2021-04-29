package Commands.UserCommands;

import Interface.ConcreteCommand;
import Models.Message;
import com.arangodb.entity.DocumentEntity;
import com.google.gson.JsonObject;
import org.json.JSONObject;

public class UpdateUserData extends ConcreteCommand {

    @Override
    protected void doCommand() {
        DocumentEntity userData = ArangoInstance.updateUserData(message.getUserID(), message.getUserData());
        JsonObject response = new JsonObject();
        JSONObject userDataJSON=new JSONObject(userData);
        String newDocumentString=userDataJSON.get("new").toString();
        response.add("userData", jsonParser.parse(newDocumentString));
        responseJson = jsonParser.parse(response.toString());
    }

    @Override
    public void setMessage(Message message) {

    }
}
