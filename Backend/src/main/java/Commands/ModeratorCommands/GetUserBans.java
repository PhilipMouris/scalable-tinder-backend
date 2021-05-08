package Commands.ModeratorCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class GetUserBans extends ConcreteCommand{
    @Override
    public void setParameters() {
        // Name of the stored procedure
        storedProcedure = "\"uspReadUserBans\"";
        // parameters MUST BE IN ORDER of usp definition
        inputParams = new String[]{"banData.user_id", "page", "limit"};
        // Defaults to "record" Not required
        outputName = "userBans";
    }
}
