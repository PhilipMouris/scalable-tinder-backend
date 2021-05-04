package Commands.UserCommands;

import Interface.ConcreteCommand;

public class CreateTransaction extends ConcreteCommand {

    @Override
    public void setParameters() {
        storedProcedure = "\"uspCreateTransaction\"";
        inputParams = new String[]{
                "transactionData.in_user_id",
                "transactionData.in_amout"
        };
        outputName = "transaction";
    }
}
