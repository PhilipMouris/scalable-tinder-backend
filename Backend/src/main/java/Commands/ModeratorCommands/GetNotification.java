package Commands.ModeratorCommands;

import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.google.gson.Gson;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class GetNotification extends ConcreteCommand {
    @Override
    public void setParameters(){
        type="find";
        model = "Notification";
        inputParams= new String[]{"notification._key"};
        collection  = "notifications";
    }

}
