package Commands.ModeratorCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class GetModeratorBans extends ConcreteCommand{
    @Override
    public void setParameters() {
        // Name of the stored procedure
        storedProcedure = "\"uspReadModeratorBans\"";
        // parameters MUST BE IN ORDER of usp definition
        inputParams = new String[]{"banData.id", "page", "limit"};
        // Defaults to "record" Not required
        outputName = "moderatorBans";
    }
}
