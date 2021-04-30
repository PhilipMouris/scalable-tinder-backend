package Commands.UserCommands;

import Interface.ConcreteCommand;
import Models.Message;
import Models.UserData;
import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.model.DocumentUpdateOptions;
import org.json.JSONObject;

public class UserAddBio extends ConcreteCommand {

    @Override
    protected void doCommand() {
        ArangoDB arangoDB=ArangoInstance.getArangoDB();
        UserData userData = ArangoInstance.getUserData(message.getUserID());
        DocumentEntity res = null;
        if (userData != null) {
            userData.setBio(message.getBio());
            res = arangoDB.db(ArangoInstance.getDbName()).collection("users").updateDocument(message.getUserID(), userData, new DocumentUpdateOptions().returnNew(true));
        } else {
//                throw error 404
        }
        JSONObject response = new JSONObject();
        JSONObject userDataJSON = new JSONObject(gson.toJson(res));
        System.out.println(userDataJSON);
        response.put("userData", userDataJSON);
        responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
    }

    @Override
    public void setMessage(Message message) {

    }
}
