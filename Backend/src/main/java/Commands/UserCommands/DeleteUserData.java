package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.Message;
import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentDeleteEntity;
import org.json.JSONObject;

public class DeleteUserData extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        ArangoDB arangoDB=ArangoInstance.getArangoDB();
        DocumentDeleteEntity dbRes = arangoDB.db(ArangoInstance.getDbName()).collection("users").deleteDocument(message.getUserID());
        JSONObject dbResJSON = new JSONObject(gson.toJson(dbRes));
        JSONObject response = new JSONObject();
        response.put("response", dbResJSON);
        responseJson = jsonParser.parse(response.toString());
        return HttpResponseTypes._200;
    }

    @Override
    public void setMessage(Message message) {

    }
}
