package Commands.ModeratorCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class GetModeratorUserBans extends ConcreteCommand {
    @Override
    public void setParameters() {
        // Name of the stored procedure
        storedProcedure = "\"uspReadModeratorUserBans\"";
        // parameters MUST BE IN ORDER of usp definition
        inputParams = new String[]{"banData.id", "banData.user_id", "page", "limit"};
        // Defaults to "record" Not required
        outputName = "moderatorUserBans";
    }
}
