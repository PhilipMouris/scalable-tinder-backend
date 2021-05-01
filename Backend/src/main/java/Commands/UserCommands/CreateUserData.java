<<<<<<< HEAD
package Commands.UserCommands;

import Interface.ConcreteCommand;

public class CreateUserData extends ConcreteCommand {
    @Override
     public void setParameters(){
          type="create";
          model = "UserData";
          inputParams= new String[]{"userData"};
          collection  = "users";
    }

}
=======
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
>>>>>>> e19d2841c1136831c0cabaaf2729f93cb75ced80
