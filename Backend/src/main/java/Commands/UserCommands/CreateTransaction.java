package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import org.json.JSONObject;

public class CreateTransaction extends ConcreteCommand {

    @Override
    public void setParameters() {
        storedProcedure = "uspCreateTransaction";
        inputParams = new String[]{
                "transactionData.user_id",
                "transactionData.amount"
        };
        outputName = "transaction";
    }

    @Override
    public void doCustomCommand() {
        if(status!= HttpResponseTypes._200) return;
        storedProcedure = "uspSetUserPremium";
        inputParams = new String[]{
                "transactionData.in_user_id"
        };
       String responseJsonTemp = responseJson.toString();
       handleSQLCommand();
       responseJson = new JSONObject(responseJsonTemp);
    }
}
