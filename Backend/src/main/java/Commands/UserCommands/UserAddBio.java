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
