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
public class UpdateUserData extends ConcreteCommand {

    @Override
    protected void doCommand() {
        Object userID = (String) message.getParameter("userData.id");
        Object userData = message.getParameter("userData");
        DocumentEntity data = ArangoInstance.updateUserData(userID, userData);
        if(data==null){
            // throw error 404
        }
        responseJson  = new JSONObject();
        JSONObject userDataJSON= new JSONObject(new Gson().toJson(data));
        responseJson.put("userData", new JSONObject(userDataJSON.get("newDocument").toString()));
    }

}
