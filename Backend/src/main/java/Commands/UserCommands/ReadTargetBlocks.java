package Commands.UserCommands;
import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
public class ReadTargetBlocks extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadTargetBlocks\"";
        inputParams = new String[]{"blockData.target_user_id"};
        outputName = "block";
        useCache=true;
    }
}
