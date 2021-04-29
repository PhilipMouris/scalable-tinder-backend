package Commands.UserCommands;

import Interface.ConcreteCommand;
import Models.Message;
import com.arangodb.entity.DocumentDeleteEntity;
import org.json.JSONObject;

public class DeleteUserData extends ConcreteCommand {

    @Override
    protected void doCommand() {
        DocumentDeleteEntity dbRes = ArangoInstance.deleteUserData(message.getUserID());
        JSONObject dbResJSON = new JSONObject(gson.toJson(dbRes));
        JSONObject response = new JSONObject();
        response.put("response", dbResJSON);
        responseJson = jsonParser.parse(response.toString());
    }

    @Override
    public void setMessage(Message message) {

    }
}
