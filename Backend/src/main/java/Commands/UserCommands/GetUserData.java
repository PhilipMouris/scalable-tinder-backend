package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.google.gson.Gson;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class GetUserData extends ConcreteCommand {
    @Override
    public void setParameters(){
        type="find";
        model = "UserData";
        inputParams= new String[]{"userData.id"};
        collection  = "users";
    }
}
