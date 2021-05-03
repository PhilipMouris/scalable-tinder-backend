package Commands.UserCommands;

import Interface.ConcreteCommand;

public class GetAllTransactions extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadAllTransactions\"";
        inputParams = new String[]{"page","limit"};
        outputName = "transactions";
        useCache = true;
    }
}
