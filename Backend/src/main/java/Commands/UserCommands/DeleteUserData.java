<<<<<<< HEAD
package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.entity.DocumentDeleteEntity;
import com.google.gson.Gson;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class DeleteUserData extends ConcreteCommand {
    @Override
    public void setParameters() {
        type = "delete";
        model = "UserData";
        inputParams = new String[]{"userData.id"};
        collection = "users";
    }
}
=======
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
>>>>>>> e19d2841c1136831c0cabaaf2729f93cb75ced80
