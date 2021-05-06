package Commands.UserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
public class ReadSourceBlocks extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadSourceBlocks\"";
        inputParams = new String[]{"blockData.source_user_id"};
        outputName = "block";
        useCache=true;
    }
}
