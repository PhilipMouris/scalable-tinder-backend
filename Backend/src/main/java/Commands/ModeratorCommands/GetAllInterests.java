package Commands.ModeratorCommands;
import Interface.ConcreteCommand;

public class GetAllInterests extends ConcreteCommand {
    @Override
    public void setParameters() {
        // Name of the stored procedure
        storedProcedure = "uspViewInterests";
        // parameters MUST BE IN ORDER of usp definition
        inputParams = new String[]{"page", "limit"};
        // Defaults to "record" Not required
        outputName = "interests";
        useCache=true;
    }
}
