package Commands.UserCommands;

import Interface.ConcreteCommand;

public class DeleteTransaction extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspDeleteTransaction\"";
        inputParams = new String[]{"transactionData.in_id"};
        outputName = "transaction";
    }
}
