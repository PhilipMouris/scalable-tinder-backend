package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class CreateBlock extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspblockuser\"";
        inputParams = new String[]{"blockData.source_user_id",
                "blockData.target_user_id",
                "blockData.created_at"};
        outputName = "block";
    }

}
