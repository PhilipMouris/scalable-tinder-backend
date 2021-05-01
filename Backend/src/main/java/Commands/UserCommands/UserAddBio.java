<<<<<<< HEAD
package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.entity.DocumentEntity;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class UserAddBio extends ConcreteCommand {
    public void setParameters(){
        type="update";
        model = "UserData";
        inputParams= new String[]{"userData.id","userData"};
        collection  = "users";
    }
}
=======
package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.Message;
import Models.UserData;
import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.model.DocumentUpdateOptions;
import org.json.JSONObject;

public class UserAddBio extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        ArangoDB arangoDB=ArangoInstance.getArangoDB();
        UserData userData = ArangoInstance.getUserData(message.getUserID());
        DocumentEntity res = null;
        if (userData != null) {
            userData.setBio(message.getBio());
            res = arangoDB.db(ArangoInstance.getDbName()).collection("users").updateDocument(message.getUserID(), userData, new DocumentUpdateOptions().returnNew(true));
        } else {
               return HttpResponseTypes._404;
        }
        JSONObject response = new JSONObject();
        JSONObject userDataJSON = new JSONObject(gson.toJson(res));
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
>>>>>>> e19d2841c1136831c0cabaaf2729f93cb75ced80
