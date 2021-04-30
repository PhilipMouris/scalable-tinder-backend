package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.Message;
import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentEntity;
import org.json.JSONObject;

public class CreateUserData extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        ArangoDB arangoDB=ArangoInstance.getArangoDB();
        DocumentEntity dbRes = arangoDB.db(ArangoInstance.getDbName()).collection("users").insertDocument(message.getUserData());
        String id = dbRes.getKey();
        JSONObject response = new JSONObject();
        response.put("id", id);
        responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
        return HttpResponseTypes._200;

    }

    @Override
    public void setMessage(Message message) {

    }
}
