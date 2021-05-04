package Commands.UserCommands;

import Interface.ConcreteCommand;
import org.json.JSONObject;


public class GetAllUserNotifications extends ConcreteCommand {

    @Override
    public void setParameters(){
        type = "findAll";
        model = "Notification";
        inputParams = new String[]{"limit","page"};
        collection  = "notifications";
        outputName= "notifications";
        // TODO Replace message.getParameter with user id from auth token, so a user can only view his notifications
        ((JSONObject)filterParams).put("userID",message.getParameter("notification.userID"));
    }

}
