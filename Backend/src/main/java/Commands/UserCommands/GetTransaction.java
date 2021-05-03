package Commands.UserCommands;

import Interface.ConcreteCommand;

public class GetTransaction extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadTransaction\"";
        inputParams = new String[]{"transactionData.in_id"};
        outputName = "transaction";
        useCache = true;
    }
}
