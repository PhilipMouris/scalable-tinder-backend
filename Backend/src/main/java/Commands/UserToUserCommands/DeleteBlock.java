package Commands.UserToUserCommands;
import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class DeleteBlock extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspDeleteBlock\"";
        inputParams = new String[]{"blockData.in_source",
                "blockData.in_target"};
        outputName = "block";
    }
}
