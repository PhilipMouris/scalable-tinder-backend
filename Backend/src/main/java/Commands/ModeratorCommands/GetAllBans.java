package Commands.ModeratorCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class GetAllBans extends ConcreteCommand {
    //private final Logger LOGGER = Logger.getLogger(GetAllBans.class.getName()) ;

    @Override
    public void setParameters() {
        // Name of the stored procedure
        storedProcedure = "\"uspReadAllBans\"";
        // parameters MUST BE IN ORDER of usp definition
        inputParams = new String[]{"page", "limit"};
        // Defaults to "record" Not required
        outputName = "bans";
        useCache=true;
    }
}
