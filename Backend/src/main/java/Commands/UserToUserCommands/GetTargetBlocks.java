package Commands.UserToUserCommands;
import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
public class GetTargetBlocks extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadTargetBlocks\"";
        inputParams = new String[]{"blockData.in_target_user","page","limit"};
        outputName = "blocks";
        useCache=true;
    }
}
