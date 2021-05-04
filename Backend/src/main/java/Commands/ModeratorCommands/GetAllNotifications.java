package Commands.ModeratorCommands;
import Interface.ConcreteCommand;


public class GetAllNotifications extends ConcreteCommand {

    @Override
    public void setParameters(){
        type = "findAll";
        model = "Notification";
        inputParams = new String[]{"limit","page"};
        collection  = "notifications";
        outputName= "notifications";
    }

}
