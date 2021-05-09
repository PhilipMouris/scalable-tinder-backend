package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
public class GetSourceBlocks extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadSourceBlocks\"";
        inputParams = new String[]{"blockData.source_user_id"};
        outputName = "block";
        useCache=true;
    }
}
