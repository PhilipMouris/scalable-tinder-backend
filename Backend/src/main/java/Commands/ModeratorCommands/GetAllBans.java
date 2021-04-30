package Commands.ModeratorCommands;

import Interface.ConcreteCommand;

public class GetAllBans extends ConcreteCommand {

    @Override
    public void setParameters() {
        // Name of the stored procedure
        storedProcedure = "\"uspReadAllBans\"";
        // parameters MUST BE IN ORDER of usp definition
        inputParams = new String[]{"page", "limit"};
        // Defaults to "record" Not required
        outputName = "bans";
    }
}
