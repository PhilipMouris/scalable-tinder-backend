package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.ArangoDB;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import Database.ArangoInstance;
import Models.UserData;
public class GetUserData extends ConcreteCommand {

    @Override
    protected void doCommand() {
        ArangoDB arangoDB=ArangoInstance.getArangoDB();
        UserData userData = arangoDB.db(ArangoInstance.getDbName()).collection("users").getDocument(message.getUserID(), UserData.class);
        JsonObject response = new JsonObject();
        response.add("userData", jsonParser.parse(gson.toJson(userData)));
        responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
    }
    @Override
    public void setMessage(Message message) {

    }
}
