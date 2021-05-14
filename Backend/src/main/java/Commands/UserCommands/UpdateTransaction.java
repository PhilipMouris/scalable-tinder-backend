package Commands.UserCommands;

import Interface.ConcreteCommand;

public class UpdateTransaction extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspUpdateTransaction\"";
        inputParams = new String[]{
                "transactionData.in_id",
                "transactionData.in_user_id",
                "transactionData.in_amout"
        };
        outputName = "transaction";
    }
}
